package com.firdausy.rafly.mataelang.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.InformasiPosyandu;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DaftarPosyanduActivity extends AppCompatActivity {
    private Context context = DaftarPosyanduActivity.this;
    private EditText et_namaPosyandu;
    private EditText et_emailPosyandu;
    private EditText et_passwordPosyandu;
    private EditText et_UlangPasswordPosyandu;
    private Button btn_simpan;
    private FirebaseAuth firebaseAuth, firebaseAuth2;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private TextView tv_daftarDisini;

    private double[] PB_defaultLakiLaki = {
            46.1,
            50.8,
            54.4,
            57.3,
            59.7,
            61.7,
            63.3,
            64.8,
            66.2,
            67.6,
            68.7,
            69.9,
            71.0,
            72.1,
            73.1,
            74.1,
            75.0,
            76.0,
            76.9,
            77.7,
            78.6,
            79.4,
            80.2,
            81.0,
            81.7

    };

    private double[] PB_defaultPerempuan = {
            45.4,
            49.8,
            53.0,
            55.6,
            57.8,
            59.8,
            61.2,
            62.7,
            64.0,
            65.3,
            66.5,
            67.7,
            68.9,
            70.0,
            71.0,
            72.0,
            73.0,
            74.0,
            74.9,
            75.8,
            76.7,
            77.5,
            78.4,
            79.2,
            80.0
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_posyandu);

        et_namaPosyandu = findViewById(R.id.et_namaPosyandu);
        et_emailPosyandu = findViewById(R.id.et_emailPosyandu);
        et_passwordPosyandu = findViewById(R.id.et_passwordPosyandu);
        et_UlangPasswordPosyandu = findViewById(R.id.et_UlangPasswordPosyandu);
        tv_daftarDisini = findViewById(R.id.tv_daftarDisini);
        btn_simpan = findViewById(R.id.btn_simpan);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user_posyandu");
        databaseReference.keepSynced(true);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesDaftar();
            }
        });
        tv_daftarDisini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DaftarIbuActivity.class));
            }
        });

    }

    private void prosesDaftar() {
        if ((TextUtils.isEmpty(et_namaPosyandu.getText().toString())) ||
                (TextUtils.isEmpty(et_emailPosyandu.getText().toString())) ||
                (TextUtils.isEmpty(et_passwordPosyandu.getText().toString())) ||
                (TextUtils.isEmpty(et_UlangPasswordPosyandu.getText().toString()))
        ) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else if (!et_passwordPosyandu.getText().toString().equals(et_UlangPasswordPosyandu.getText().toString())) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.pass_salah));
            et_UlangPasswordPosyandu.setError(getString(R.string.pass_salah));
        } else {
            //TODO : proses daftar
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Mendaftar ...",
                    true);

            firebaseAuth.createUserWithEmailAndPassword(et_emailPosyandu.getText().toString(),
                    et_passwordPosyandu.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.dismiss();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    String user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                                    Map data = new HashMap();
                                    data.put("namaPosyandu", et_namaPosyandu.getText().toString());

                                    setKodePosyandu(user_id);

                                    databaseReference.child(user_id)
                                            .child("detailPosyandu")
                                            .setValue(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseAuth.getInstance().signOut();
                                                    AlertDialog.Builder builder;
                                                    builder = new AlertDialog.Builder(context);
                                                    builder.setTitle("Informasi")
                                                            .setMessage(getString(R.string.pendaftaran_berhasil))
                                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    startActivity(new Intent(context, LoginActivity.class));
                                                                    finish();
                                                                }
                                                            })
                                                            .setCancelable(false)
                                                            .show();
                                                }
                                            });

                                    setKodePosyandu(user_id);
                                    setPengaturan(user_id);
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
                            new Bantuan(context).alertDialogPeringatan(e.getMessage());
                        }
                    });
        }
    }

    private void setPengaturan(String user_id) {

        Map data = new HashMap();
        for (int i = 0; i < 25 ; i++){
            if(i < 10){
                data.put("bulan0" + i , String.valueOf(PB_defaultLakiLaki[i]));
            } else {
                data.put("bulan" + i , String.valueOf(PB_defaultLakiLaki[i]));
            }
        }

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(user_id)
                .child("pengaturan")
                .child("antropometri")
                .child("lakilaki")
                .setValue(data);

        Map data2 = new HashMap();
        for (int i = 0; i < 25 ; i++){
            if(i < 10){
                data2.put("bulan0" + i , String.valueOf(PB_defaultPerempuan[i]));
            } else {
                data2.put("bulan" + i , String.valueOf(PB_defaultPerempuan[i]));
            }
        }

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(user_id)
                .child("pengaturan")
                .child("antropometri")
                .child("perempuan")
                .setValue(data2);

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(user_id)
                .child("pengaturan")
                .child("tindakan")
                .child("normal")
                .setValue(getString(R.string.default_tindakan_normal));

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(user_id)
                .child("pengaturan")
                .child("tindakan")
                .child("stunting")
                .setValue(getString(R.string.default_tindakan_stunting));

        FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(user_id)
                .child("pengaturan")
                .child("pencegahan")
                .setValue(getString(R.string.default_pencegahan));

    }

    private void setKodePosyandu(final String KeyPosyandu){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    boolean kosong = true;
                    String kode = new Bantuan().generateString(6);
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.child("kodePosyandu").exists()){
                                if(Objects.requireNonNull(ds.child("kodePosyandu").getValue(String.class))
                                        .equalsIgnoreCase(kode)){
                                    kosong = false;
                                }
                            }
                        }

                        if(kosong){
                            //TODO : set Kode Posyandu
                            databaseReference.child(KeyPosyandu)
                                    .child("kodePosyandu")
                                    .setValue(kode);
                        } else {
                            setKodePosyandu(KeyPosyandu);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
