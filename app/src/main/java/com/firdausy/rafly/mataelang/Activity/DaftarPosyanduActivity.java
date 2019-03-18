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

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_posyandu);

        et_namaPosyandu = findViewById(R.id.et_namaPosyandu);
        et_emailPosyandu = findViewById(R.id.et_emailPosyandu);
        et_passwordPosyandu = findViewById(R.id.et_passwordPosyandu);
        et_UlangPasswordPosyandu = findViewById(R.id.et_UlangPasswordPosyandu);
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
}
