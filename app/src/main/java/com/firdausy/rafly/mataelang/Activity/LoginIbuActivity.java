package com.firdausy.rafly.mataelang.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.ibu.MainActivityIbuActivity;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginIbuActivity extends AppCompatActivity {
    private Context context = LoginIbuActivity.this;
    private EditText et_noHpLogin;
    private Button btn_login;
    private TextView tv_loginDisini;
    private RelativeLayout rl_login;
    private LinearLayout ll_verifikasi;
    private LinearLayout ll_timer;
    private TextView tv_timer;
    private TextView tv_kirim_ulang;
    private EditText et_pin;
    private CountDownTimer countDownTimer;
    private long waktu;
    private String code;
    private String verifikasiId;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private PhoneAuthProvider.ForceResendingToken token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ibu);

        et_noHpLogin = findViewById(R.id.et_noHpLogin);
        btn_login = findViewById(R.id.btn_login);
        tv_loginDisini = findViewById(R.id.tv_loginDisini);
        rl_login = findViewById(R.id.rl_login);
        ll_verifikasi = findViewById(R.id.ll_verifikasi);
        ll_timer = findViewById(R.id.ll_timer);
        tv_timer = findViewById(R.id.tv_timer);
        tv_kirim_ulang = findViewById(R.id.tv_kirim_ulang);
        et_pin = findViewById(R.id.et_pin);

        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 6){
                    verifyVerificationCode(et_pin.getText().toString());
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        rl_login.setVisibility(View.VISIBLE);
        ll_verifikasi.setVisibility(View.GONE);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesLogin();
            }
        });

        tv_loginDisini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });

        tv_kirim_ulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimUlangOTP();
            }
        });
    }

    private void kirimUlangOTP() {
        ll_timer.setVisibility(View.VISIBLE);
        tv_kirim_ulang.setVisibility(View.GONE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62" + et_noHpLogin.getText().toString(),
                70,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            et_pin.setText(code);
                            verifyVerificationCode(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        new Bantuan(context).alertDialogPeringatan("Gagal : " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verifikasiId = s;
                        token = forceResendingToken;
                        new Bantuan(context).toastLong("Silahkan periksa kode OTP yang kami kirim.");
                    }
                }, token);

        startTimer();
    }

    private void prosesLogin() {
        if (TextUtils.isEmpty(et_noHpLogin.getText().toString())) {
            et_noHpLogin.setError("Nomer HP Tidak Boleh Kosong !");
            et_noHpLogin.requestFocus();
        } else if (et_noHpLogin.getText().length() < 10) {
            et_noHpLogin.setError("Nomer HP salah");
            et_noHpLogin.requestFocus();
        } else {

            databaseReference.child("user")
                    .child("ibu")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        boolean ketemu = false;

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.child("email").exists()) {
                                    if (Objects.requireNonNull(ds.child("email").getValue(String.class))
                                            .equalsIgnoreCase("+62" + et_noHpLogin.getText().toString())) {
                                        ketemu = true;
                                    }
                                }
                            }

                            if (ketemu) {
                                //TODO : proses login
                                rl_login.setVisibility(View.GONE);
                                ll_verifikasi.setVisibility(View.VISIBLE);

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+62" + et_noHpLogin.getText().toString(),
                                        70,
                                        TimeUnit.SECONDS,
                                        TaskExecutors.MAIN_THREAD,
                                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                            @Override
                                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                                code = phoneAuthCredential.getSmsCode();
                                                if (code != null) {
                                                    et_pin.setText(code);
                                                    verifyVerificationCode(code);
                                                }
                                            }

                                            @Override
                                            public void onVerificationFailed(FirebaseException e) {
                                                new Bantuan(context).alertDialogPeringatan("Gagal : " + e.getMessage());
                                            }

                                            @Override
                                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                super.onCodeSent(s, forceResendingToken);
                                                verifikasiId = s;
                                                token = forceResendingToken;
                                                new Bantuan(context).toastLong("Silahkan periksa kode OTP yang kami kirim.");
                                            }
                                        });

                                startTimer();
                            } else {
                                new Bantuan(context).alertDialogPeringatan(getString(R.string.oop_belum_terdaftar));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                        }
                    });
        }
    }

    private void verifyVerificationCode(String code) {
        progressDialog = ProgressDialog.show(context,
                "Tunggu Beberapa Saat",
                "Proses Login ...",
                true);
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifikasiId, code);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        startActivity(new Intent(context, MainActivityIbuActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        new Bantuan(context).alertDialogPeringatan(e.getMessage());
                    }
                });
    }

    private void startTimer() {
        waktu = 90000;
        countDownTimer = new CountDownTimer(waktu, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                waktu = millisUntilFinished;

                int menit = (int) waktu / 60000;
                int detik = (int) waktu % 60000 / 1000;

                if (detik < 10) {
                    tv_timer.setText("0" +
                            menit + " : " +
                            "0" + detik);
                } else {
                    tv_timer.setText("0" +
                            menit + " : " +
                            detik);
                }
            }

            @Override
            public void onFinish() {
                ll_timer.setVisibility(View.GONE);
                tv_kirim_ulang.setVisibility(View.VISIBLE);
            }
        }.start();
    }
}
