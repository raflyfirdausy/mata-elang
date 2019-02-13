package com.firdausy.rafly.mataelang.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PengaturanPosyanduActivity extends AppCompatActivity {

    private Context context = PengaturanPosyanduActivity.this;
    private EditText et_namaPosyandu;
    private EditText et_alamatPosyandu;
    private EditText et_kecamatan;
    private EditText et_nomerHpKantor;
    private Button btn_edit;
    private Button btn_simpan;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_posyandu);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.pengaturan);
        getSupportActionBar().setSubtitle(R.string.posyandu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        et_namaPosyandu = findViewById(R.id.et_namaPosyandu);
        et_alamatPosyandu = findViewById(R.id.et_alamatPosyandu);
        et_kecamatan = findViewById(R.id.et_kecamatan);
        et_nomerHpKantor = findViewById(R.id.et_nomerHpKantor);
        btn_edit = findViewById(R.id.btn_edit);
        btn_simpan = findViewById(R.id.btn_simpan);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("posyandu");

        setData();

        setUpDisable();
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_edit.getText().toString().equalsIgnoreCase("edit")){
                    setUpEnable();
                    btn_edit.setText(getString(R.string.batal));
                } else {
                    setUpDisable();
                    btn_edit.setText(getString(R.string.edit));
                }

            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesSimpan();
            }
        });
    }

    private void setData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    et_namaPosyandu.setText(dataSnapshot.child("namaPosyandu").getValue(String.class));
                    et_alamatPosyandu.setText(dataSnapshot.child("alamatPosyandu").getValue(String.class));
                    et_kecamatan.setText(dataSnapshot.child("kecamatan").getValue(String.class));
                    et_nomerHpKantor.setText(dataSnapshot.child("nomerHpKantor").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
            }
        });
    }

    private void prosesSimpan() {
        if ((TextUtils.isEmpty(et_namaPosyandu.getText().toString())) ||
                (TextUtils.isEmpty(et_alamatPosyandu.getText().toString())) ||
                (TextUtils.isEmpty(et_kecamatan.getText().toString())) ||
                (TextUtils.isEmpty(et_nomerHpKantor.getText().toString()))) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else {
            //TODO : proses Simpan Data
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Menyimpan Data Posyandu ...",
                    true);

            Map data = new HashMap();
            data.put("namaPosyandu", et_namaPosyandu.getText().toString());
            data.put("alamatPosyandu", et_alamatPosyandu.getText().toString());
            data.put("kecamatan", et_kecamatan.getText().toString());
            data.put("nomerHpKantor", et_nomerHpKantor.getText().toString());

            databaseReference.setValue(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                new Bantuan(context).alertDialogInformasi(getString(R.string.data_berhasil_di_simpan));
                                setUpDisable();
                            } else {
                                new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }
    }

    private void setUpDisable() {
        et_namaPosyandu.setEnabled(false);
        et_alamatPosyandu.setEnabled(false);
        et_kecamatan.setEnabled(false);
        et_nomerHpKantor.setEnabled(false);
    }

    private void setUpEnable() {
        et_namaPosyandu.setEnabled(true);
        et_alamatPosyandu.setEnabled(true);
        et_kecamatan.setEnabled(true);
        et_nomerHpKantor.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(context, PengaturanActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, PengaturanActivity.class));
        finish();
    }
}
