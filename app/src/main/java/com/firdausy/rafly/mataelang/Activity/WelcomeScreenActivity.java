package com.firdausy.rafly.mataelang.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class WelcomeScreenActivity extends AppCompatActivity {

    private Context context = WelcomeScreenActivity.this;
    private Button btn_masuk;
    private Button btn_daftar;
    private ImageView iv_help;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        btn_masuk = findViewById(R.id.btn_masuk);
        btn_daftar = findViewById(R.id.btn_daftar);
        iv_help = findViewById(R.id.iv_help);

        animateHelp();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginIbuActivity.class));
            }
        });

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DaftarIbuActivity.class));
            }
        });

        iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilbantuan();
            }
        });
    }

    private void tampilbantuan() {
        databaseReference.child("build_config")
                .child("BANTUAN")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            new Bantuan(context).alertDialogInformasi(dataSnapshot.getValue(String.class));
                        } else {
                            new Bantuan(context).alertDialogInformasi(getString(R.string.bantuan));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
    }

    private void animateHelp() {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shakeanimation);
        iv_help.setImageResource(R.drawable.ic_help_outline_black_24dp);
        iv_help.setAnimation(shake);
    }
}
