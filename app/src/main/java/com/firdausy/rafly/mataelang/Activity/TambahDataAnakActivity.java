package com.firdausy.rafly.mataelang.Activity;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TambahDataAnakActivity extends AppCompatActivity {

    private Context context = TambahDataAnakActivity.this;
    private TextView tv_namaIbu;
    private EditText et_namaLengkapBayi;
    private RadioGroup rb_jenisKelamin;
    private RadioButton rb_lakiLaki;
    private RadioButton rb_perempuan;
    private EditText et_tanggalLahir;
    private EditText et_tempatLahir;
    private Button btn_simpan;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private int tanggal, bulan, tahun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_anak);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.mata_elang);
        getSupportActionBar().setSubtitle(R.string.input_data_anak);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tv_namaIbu = findViewById(R.id.tv_namaIbu);
        et_namaLengkapBayi = findViewById(R.id.et_namaLengkapBayi);
        rb_jenisKelamin = findViewById(R.id.rb_jenisKelamin);
        rb_lakiLaki = findViewById(R.id.rb_lakiLaki);
        rb_perempuan = findViewById(R.id.rb_perempuan);
        et_tanggalLahir = findViewById(R.id.et_tanggalLahir);
        et_tempatLahir = findViewById(R.id.et_tempatLahir);
        btn_simpan = findViewById(R.id.btn_simpan);

        et_tanggalLahir.setFocusable(false);
        et_tanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesSimpanDataBayi();
            }
        });

        getAndSetNamaIbu();

    }

    private void setTanggal() {
        final Calendar c = Calendar.getInstance();
        tanggal = c.get(Calendar.DAY_OF_MONTH);
        bulan = c.get(Calendar.MONTH);
        tahun = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_tanggalLahir.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, tahun, bulan, tanggal);
        datePickerDialog.show();
    }

    private void prosesSimpanDataBayi() {
        if ((TextUtils.isEmpty(et_namaLengkapBayi.getText().toString())) ||
                (TextUtils.isEmpty(et_tanggalLahir.getText().toString())) ||
                (TextUtils.isEmpty(et_tempatLahir.getText().toString()))) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else {
            //TODO : Proses Daftarin Bayi Ke Database !
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Mendaftarkan Bayi ...",
                    true);

            final String jenisKelamin = (String) ((RadioButton) findViewById(rb_jenisKelamin.getCheckedRadioButtonId())).getText();

            Map data = new HashMap();
            data.put("namaLengkapBayi", et_namaLengkapBayi.getText().toString());
            data.put("tanggalLahir", et_tanggalLahir.getText().toString());
            data.put("tempatLahir", et_tempatLahir.getText().toString());
            data.put("jenisKelamin", jenisKelamin);

            String keyBayi = databaseReference.child("bayi")
                    .child(getIntent().getStringExtra("keyIbu"))
                    .push()
                    .getKey();

            databaseReference.child("bayi")
                    .child(getIntent().getStringExtra("keyIbu"))
                    .child(Objects.requireNonNull(keyBayi))
                    .setValue(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                new Bantuan(context).alertDialogInformasi(getString(R.string.berhasil_menambah_data_bayi));
                                et_namaLengkapBayi.setText("");
                                et_tanggalLahir.setText("");
                                et_tempatLahir.setText("");
                            } else {
                                new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        }
    }

    private void getAndSetNamaIbu() {
        databaseReference.child("user")
                .child("ibu")
                .child(getIntent().getStringExtra("keyIbu"))
                .child("namaLengkap")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tv_namaIbu.setText(dataSnapshot.getValue(String.class));
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
                Intent intent = new Intent(context, IbuDetailActivity.class);
                intent.putExtra("keyIbu", getIntent().getStringExtra("keyIbu"));
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, IbuDetailActivity.class);
        intent.putExtra("keyIbu", getIntent().getStringExtra("keyIbu"));
        startActivity(intent);
        finish();
    }
}
