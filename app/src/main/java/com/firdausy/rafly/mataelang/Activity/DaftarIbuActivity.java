package com.firdausy.rafly.mataelang.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DaftarIbuActivity extends AppCompatActivity {

    private Context context = DaftarIbuActivity.this;
    private LinearLayout ll_daftar;
    private EditText et_namaIbu;
    private EditText et_alamatLengkap;
    private EditText et_kodePosyandu;
    private ImageButton ib_scanBarcode;
    private EditText et_noHpLogin;
    private Button btn_lanjut;
    private LinearLayout ll_verifikasi;
    private EditText et_pin;
    private LinearLayout ll_timer;
    private TextView tv_timer;
    private TextView tv_kirim_ulang;
    private TextView tv_daftarDisini;
    private CountDownTimer countDownTimer;
    private long waktu;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String code;
    private String verifikasiId;
    private String keyPosyandu = null;
    private PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_ibu);

        ll_daftar = findViewById(R.id.ll_daftar);
        et_namaIbu = findViewById(R.id.et_namaIbu);
        et_alamatLengkap = findViewById(R.id.et_alamatLengkap);
        et_kodePosyandu = findViewById(R.id.et_kodePosyandu);
        ib_scanBarcode = findViewById(R.id.ib_scanBarcode);
        et_noHpLogin = findViewById(R.id.et_noHpLogin);
        btn_lanjut = findViewById(R.id.btn_lanjut);
        ll_verifikasi = findViewById(R.id.ll_verifikasi);
        et_pin = findViewById(R.id.et_pin);
        ll_timer = findViewById(R.id.ll_timer);
        tv_timer = findViewById(R.id.tv_timer);
        tv_daftarDisini = findViewById(R.id.tv_daftarDisini);
        tv_kirim_ulang = findViewById(R.id.tv_kirim_ulang);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ll_daftar.setVisibility(View.VISIBLE);
        ll_verifikasi.setVisibility(View.GONE);

        btn_lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesLanjut();
            }
        });

        tv_kirim_ulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimUlangOTP();
            }
        });

        ib_scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DaftarIbuActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(context, ScanBarcodeActivity.class), 1);
                }

            }
        });

        tv_daftarDisini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DaftarPosyanduActivity.class));
            }
        });

        et_pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    verifyVerificationCode(et_pin.getText().toString());
                }
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

    private void prosesLanjut() {
        //TODO : cek kode neng DB disit

        if ((TextUtils.isEmpty(et_namaIbu.getText().toString())) ||
                (TextUtils.isEmpty(et_alamatLengkap.getText().toString())) ||
                (TextUtils.isEmpty(et_kodePosyandu.getText().toString())) ||
                (TextUtils.isEmpty(et_noHpLogin.getText().toString()))
        ) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else if (et_noHpLogin.getText().length() < 10) {
            et_noHpLogin.setError("Nomer HP salah");
            et_noHpLogin.requestFocus();
        } else {
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Mendaftar ...",
                    true);

            databaseReference.child("user_posyandu")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean ketemu = false;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.child("kodePosyandu").exists()) {
                                    if (et_kodePosyandu.getText().toString()
                                            .equalsIgnoreCase(ds.child("kodePosyandu")
                                                    .getValue(String.class))) {
                                        keyPosyandu = ds.getKey();
                                        ketemu = true;
                                    }
                                }
                            }

                            if (ketemu) {
                                //TODO : cek nomer udah ada yang make belum

                                databaseReference.child("user")
                                        .child("ibu")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            boolean udahKepake = false;

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        if (ds.child("email").exists()) {
                                                            if (ds.child("email").getValue(String.class)
                                                                    .equalsIgnoreCase("+62" + et_noHpLogin.getText()
                                                                            .toString())) {
                                                                udahKepake = true;
                                                            }
                                                        }
                                                    }
                                                }

                                                if (udahKepake) {
                                                    progressDialog.dismiss();
                                                    new Bantuan(context).alertDialogPeringatan("Oops, Nomer sudah di pakai user lain !");
                                                } else {
                                                    progressDialog.dismiss();
                                                    ll_daftar.setVisibility(View.GONE);
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
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                                            }
                                        });
                            } else {
                                progressDialog.dismiss();
                                new Bantuan(context).alertDialogInformasi("Kode Posyandu Salah !");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                        }
                    });
        }
    }

    private void verifyVerificationCode(String code) {
        progressDialog = ProgressDialog.show(context,
                "Tunggu Beberapa Saat",
                "Proses Daftar ...",
                true);
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifikasiId, code);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        String user_id = authResult.getUser().getUid();
                        Map data = new HashMap();
                        data.put("alamatLengkap", et_alamatLengkap.getText().toString());
                        data.put("email", "+62" + et_noHpLogin.getText().toString());
                        data.put("namaLengkap", et_namaIbu.getText().toString());
                        data.put("nomerHp", "+62" + et_noHpLogin.getText().toString());
                        data.put("id_posyandu", keyPosyandu);

                        databaseReference.child("user")
                                .child("ibu")
                                .child(user_id)
                                .setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    et_kodePosyandu.setText(Objects.requireNonNull(data).getStringExtra("data"));
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(context, ScanBarcodeActivity.class));
                } else {
                    new Bantuan(context).toastLong("Akses Kamera Di Tolak !");
                }
        }
    }
}
