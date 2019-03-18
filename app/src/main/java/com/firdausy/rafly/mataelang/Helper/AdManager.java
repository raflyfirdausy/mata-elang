package com.firdausy.rafly.mataelang.Helper;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdManager {
    private static InterstitialAd interstitialAd;
    private static boolean isInterAdsShowed = false;
    private Activity activity;
    private String AD_UNIT_ID;

    public AdManager(Activity activity, String AD_UNIT_ID) {
        this.activity = activity;
        this.AD_UNIT_ID = AD_UNIT_ID;
        createAd();
    }

    public void createAd(){
        //buat iklan
        interstitialAd  = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();

        //load iklan
        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
//                new Bantuan(activity).toastShort("onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
//                new Bantuan(activity).toastShort("onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
//                new Bantuan(activity).toastShort("onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
//                new Bantuan(activity).toastShort("onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
//                new Bantuan(activity).toastShort("onAdClosed");
                createAd();
            }
        });
    }

    public static InterstitialAd getAd(){
        if(interstitialAd != null && interstitialAd.isLoaded() && !isInterAdsShowed){
            isInterAdsShowed = true;
            return interstitialAd;
        }
        else return null;
    }
}
