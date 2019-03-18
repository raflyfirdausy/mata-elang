package com.firdausy.rafly.mataelang.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.ibu.MainActivityIbuActivity;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private Context context = LoginActivity.this;
    private Button btn_login;
    private FirebaseAuth firebaseAuth;
    private EditText et_emailLogin;
    private EditText et_passwordLogin;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    private ImageView iv_help;
    private LinearLayout ll_daftar;
    private TextView tv_klikDisini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }


        btn_login = findViewById(R.id.btn_login);
        et_emailLogin = findViewById(R.id.et_emailLogin);
        et_passwordLogin = findViewById(R.id.et_passwordLogin);
        iv_help = findViewById(R.id.iv_help);
        ll_daftar = findViewById(R.id.ll_daftar);
        tv_klikDisini = findViewById(R.id.tv_klikDisini);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Proses login
                prosesLogin();
            }
        });

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

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
        }
        AnimateBell();
        iv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilbantuan();
            }
        });
        tv_klikDisini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DaftarPosyanduActivity.class));
            }
        });


    }

    private void tampilbantuan() {
        new Bantuan(context).alertDialogInformasi(getString(R.string.bantuan));
    }

    private void AnimateBell() {
        Animation shaje = AnimationUtils.loadAnimation(context, R.anim.shakeanimation);
        iv_help.setImageResource(R.drawable.ic_help_outline_black_24dp);

        iv_help.setAnimation(shaje);
    }

    private void prosesLogin() {
        if ((TextUtils.isEmpty(et_emailLogin.getText().toString())) ||
                (TextUtils.isEmpty(et_passwordLogin.getText().toString()))) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.username_password_kosong));
        } else {

            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Login ...",
                    true);

            firebaseAuth.signInWithEmailAndPassword(et_emailLogin.getText().toString(),
                    et_passwordLogin.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                databaseReference.child("user")
                                        .child("admin")
                                        .child(firebaseAuth.getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    new Bantuan(context).toastShort("2222222222");
                                                    startActivity(new Intent(context, MainActivity.class));
                                                    finish();
                                                } else {
                                                    new Bantuan(context).toastShort("333333333333");
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
                                new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }
    }
}
