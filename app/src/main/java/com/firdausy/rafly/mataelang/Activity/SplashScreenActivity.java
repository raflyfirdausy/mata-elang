package com.firdausy.rafly.mataelang.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.Internet;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    private Context context = SplashScreenActivity.this;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash_screen);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Timer timer = new Timer();
        boolean cekKoneksi = new Internet().CekKoneksi(context);
        if (cekKoneksi) {
            timer.schedule(new Splash(), 3000);
        } else {
            Snackbar.make(coordinatorLayout, "Tidak Ada Koneksi Internet !", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Coba Lagi", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(context, SplashScreenActivity.class));
                            finish();
                        }
                    })
                    .show();
        }
    }

    class Splash extends TimerTask {
        @Override
        public void run() {
            if (firebaseAuth.getCurrentUser() != null) {
                databaseReference.child("user")
                        .child("admin")
                        .child(firebaseAuth.getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    startActivity(new Intent(context, MainActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(context, MainActivityIbuActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                            }
                        });
            } else {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        }
    }
}
