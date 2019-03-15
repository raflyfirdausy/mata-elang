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

import java.util.Objects;

public class PengaturanPencegahanActivity extends AppCompatActivity {
    private Context context = PengaturanPencegahanActivity.this;
    private Button btn_edit;
    private Button btn_simpan;
    private EditText et_caraPencegahanStunting;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_pencegahan);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.pengaturan);
        getSupportActionBar().setSubtitle(R.string.cara_pencegahan_stunting);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btn_edit = findViewById(R.id.btn_edit);
        btn_simpan = findViewById(R.id.btn_simpan);
        et_caraPencegahanStunting = findViewById(R.id.et_caraPencegahanStunting);
        et_caraPencegahanStunting.setEnabled(false);
        btn_simpan.setEnabled(false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("pengaturan").child("pencegahan");

        setData();
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_edit.getText().toString().equalsIgnoreCase("edit")){
                    et_caraPencegahanStunting.setEnabled(true);
                    btn_simpan.setEnabled(true);
                    btn_edit.setText(getString(R.string.batal));
                } else {
                    et_caraPencegahanStunting.setEnabled(false);
                    btn_simpan.setEnabled(false);
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

    private void prosesSimpan() {
        if ((TextUtils.isEmpty(et_caraPencegahanStunting.getText().toString()))){
            new Bantuan(context).alertDialogPeringatan(getString(R.string.cara_pencegahan_tidak_boleh_kosong));
        } else {
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Menyimpan Data ...",
                    true);

            databaseReference.setValue(et_caraPencegahanStunting.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                new Bantuan(context).alertDialogInformasi(getString(R.string.data_berhasil_di_simpan));
                                et_caraPencegahanStunting.setEnabled(false);
                                btn_simpan.setEnabled(false);
                                btn_edit.setText(getString(R.string.edit));
                            } else {
                                new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }
    }

    private void setData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    et_caraPencegahanStunting.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
            }
        });
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
