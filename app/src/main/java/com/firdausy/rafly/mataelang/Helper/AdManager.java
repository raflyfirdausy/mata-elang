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
            public void onAdClosed() {
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
