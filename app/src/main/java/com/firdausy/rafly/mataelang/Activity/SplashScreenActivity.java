package com.firdausy.rafly.mataelang.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.firdausy.rafly.mataelang.Activity.ibu.MainActivityIbuActivity;
import com.firdausy.rafly.mataelang.BuildConfig;
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

    private boolean terupdate;
    private String LINK_DOWNLOAD;

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
        boolean cekKoneksi = new Internet().CekKoneksi(context);
        if (cekKoneksi) {
            databaseReference.child("build_config").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        LINK_DOWNLOAD = dataSnapshot.child("LINK_DOWNLOAD").getValue(String.class);
                        if (dataSnapshot.child("VERSION_CODE").getValue(String.class)
                                .equalsIgnoreCase(String.valueOf(BuildConfig.VERSION_CODE)) &&
                                dataSnapshot.child("VERSION_NAME").getValue(String.class)
                                        .equalsIgnoreCase(BuildConfig.VERSION_NAME)) {
                            terupdate = true;
                        } else {
                            terupdate = false;
                        }
                    }

                    Timer timer = new Timer();
                    if (terupdate) {
                        timer.schedule(new Splash(), 3000);
                    } else {
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Peringatan")
                                .setMessage("Aplikasi sudah kadaluarsa, silahkan download aplikasi yang terbaru !")
                                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(LINK_DOWNLOAD)));
                                    }
                                })
                                .setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                }
            });
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
                                if (dataSnapshot.exists()) {
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
