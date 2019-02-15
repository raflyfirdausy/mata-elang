package com.firdausy.rafly.mataelang.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.InputFilterMinMax;
import com.firdausy.rafly.mataelang.Model.BayiModel;
import com.firdausy.rafly.mataelang.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IbuDetailActivity extends AppCompatActivity {

    private Context context = IbuDetailActivity.this;
    private TextView tv_namaIbu;
    private MaterialSpinner spinner_anak;
    private FloatingActionButton fab_tambahDataAnak;
    private EditText et_tanggalInput;
    private EditText et_panjangBadan;
    private EditText et_bulanKe;
    private EditText et_beratBadan;
    private Button btn_simpan;
    private Button btn_panggil;
    private Button btn_lihatData;
    private LinearLayout layout_input;
    private int tanggal, bulan, tahun;

    private DatabaseReference databaseReference, databaseCek;
    private ProgressDialog progressDialog;

    private List<BayiModel> listDataBayi = new ArrayList<>();
    private List<String> listDataBayiSpinner = new ArrayList<>();
    private String selectedKeyBayi, jenisKelamin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibu_detail);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.mata_elang);
        getSupportActionBar().setSubtitle(R.string.input_data_antropometri);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tv_namaIbu = findViewById(R.id.tv_namaIbu);
        spinner_anak = findViewById(R.id.spinner_anak);
        fab_tambahDataAnak = findViewById(R.id.fab_tambahDataAnak);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_panggil = findViewById(R.id.btn_panggil);
        btn_lihatData = findViewById(R.id.btn_lihatData);
        et_tanggalInput = findViewById(R.id.et_tanggalInput);
        et_panjangBadan = findViewById(R.id.et_panjangBadan);
        et_beratBadan = findViewById(R.id.et_beratBadan);
        et_bulanKe = findViewById(R.id.et_bulanKe);
        et_bulanKe.setFilters(new InputFilter[]{ new InputFilterMinMax(0, 24)});

        et_tanggalInput.setFocusable(false);
        layout_input = findViewById(R.id.layout_input);

        et_tanggalInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTanggal();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesSimpan();
            }
        });

        btn_lihatData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LihatDetailDataAntropometryActivity.class);
                intent.putExtra("keyIbu", getIntent().getStringExtra("keyIbu"));
                startActivity(intent);
                finish();
            }
        });

        fab_tambahDataAnak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TambahDataAnakActivity.class);
                intent.putExtra("keyIbu", getIntent().getStringExtra("keyIbu"));
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        getAndSetData();

        spinner_anak.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (spinner_anak.getItems() != null) {
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                    jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                }
            }
        });

        spinner_anak.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                if (spinner_anak.getItems() != null) {
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                    jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                }
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void prosesSimpan() {
        if ((TextUtils.isEmpty(et_bulanKe.getText().toString())) ||
//                (TextUtils.isEmpty(et_tanggalInput.getText().toString())) ||
                (TextUtils.isEmpty(et_panjangBadan.getText().toString())) ||
                (TextUtils.isEmpty(et_beratBadan.getText().toString()))) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else {
            //TODO : proses simpan data
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Menyimpan data ...",
                    true);

            Map data = new HashMap();
//            data.put("tanggalInput", et_tanggalInput.getText().toString());
            data.put("panjangBadan", et_panjangBadan.getText().toString());
            data.put("beratBadan", et_beratBadan.getText().toString());
            data.put("tanggalInput", new SimpleDateFormat(getString(R.string.format_tanggal)).format(new Date()));
//
//            String keyInput = databaseReference.child(getIntent().getStringExtra("keyIbu"))
//                    .child(selectedKeyBayi)
//                    .push()
//                    .getKey();

            final String bulanKey;
            if(Integer.parseInt(et_bulanKe.getText().toString()) < 10){
                bulanKey = "bulanKe-0" + Integer.parseInt(et_bulanKe.getText().toString());
            } else {
                bulanKey = "bulanKe-" + Integer.parseInt(et_bulanKe.getText().toString());
            }

            databaseReference.child("dataInput")
                    .child(getIntent().getStringExtra("keyIbu"))
                    .child(selectedKeyBayi)
                    .child(bulanKey)
                    .setValue(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull final Task<Void> task) {
                            if(task.isSuccessful()){
                                int parseBulanKe = Integer.parseInt(et_bulanKe.getText().toString());
                                String bulanKe;
                                if(parseBulanKe < 10){
                                    bulanKe = String.valueOf("0" + parseBulanKe);
                                } else {
                                    bulanKe = String.valueOf(parseBulanKe);
                                }

                                if(jenisKelamin.equalsIgnoreCase("perempuan")){
                                    databaseCek = FirebaseDatabase.getInstance().getReference()
                                            .child("antropometri")
                                            .child("perempuan")
                                            .child("bulan" + bulanKe);
                                } else {
                                    databaseCek = FirebaseDatabase.getInstance().getReference()
                                            .child("antropometri")
                                            .child("lakilaki")
                                            .child("bulan" + bulanKe);
                                }

                                databaseCek.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        progressDialog.dismiss();
                                        if(dataSnapshot.exists()){
                                            double data = Double.parseDouble(Objects.requireNonNull(
                                                    dataSnapshot.getValue(String.class)));

                                            double panjangBadan = Double.parseDouble(et_panjangBadan.getText().toString());

                                            if(panjangBadan < data){
                                                databaseReference.child("dataInput")
                                                        .child(getIntent().getStringExtra("keyIbu"))
                                                        .child(selectedKeyBayi)
                                                        .child(bulanKey)
                                                        .child("hasil")
                                                        .setValue("Beresiko Stunting");
                                                new Bantuan(context).alertDialogPeringatan(getString(R.string.beresiko_stunting));
                                            } else {
                                                databaseReference.child("dataInput")
                                                        .child(getIntent().getStringExtra("keyIbu"))
                                                        .child(selectedKeyBayi)
                                                        .child(bulanKey)
                                                        .child("hasil")
                                                        .setValue("Normal");
                                                new Bantuan(context).alertDialogInformasi(getString(R.string.anak_normal));
                                            }

                                            et_tanggalInput.setText("");
                                            et_bulanKe.setText("");
                                            et_panjangBadan.setText("");
                                            et_beratBadan.setText("");

                                        } else {
                                            progressDialog.dismiss();
                                            new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });

        }
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
                        et_tanggalInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, tahun, bulan, tanggal);
        datePickerDialog.show();
    }

    private void getAndSetData() {
        databaseReference.child("user")
                .child("ibu")
                .child(getIntent().getStringExtra("keyIbu"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("namaLengkap").exists()) {
                            tv_namaIbu.setText(dataSnapshot.child("namaLengkap").getValue(String.class));
                        }

                        if (dataSnapshot.child("nomerHp").exists()) {
                            btn_panggil.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                                                == PackageManager.PERMISSION_GRANTED) {
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse("tel:" + dataSnapshot.child("nomerHp")
                                                    .getValue(String.class)));
                                            startActivity(intent);
                                        } else {
                                            ActivityCompat.requestPermissions(IbuDetailActivity.this,
                                                    new String[]{Manifest.permission.CALL_PHONE}, 1);
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

        databaseReference.child("bayi")
                .child(getIntent().getStringExtra("keyIbu"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        BayiModel bayiModel = null;
                        listDataBayi.clear();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                bayiModel = ds.getValue(BayiModel.class);
                                Objects.requireNonNull(bayiModel).setKeyBayi(ds.getKey());
                                bayiModel.setKeyIbu(dataSnapshot.getKey());
                                listDataBayi.add(bayiModel);
                            }

                            for (int i = 0; i < listDataBayi.size(); i++) {
                                listDataBayiSpinner.add(listDataBayi.get(i).getNamaLengkapBayi());
                            }

                            spinner_anak.setHint(getString(R.string.pilih_data_bayi));
                            spinner_anak.setItems(listDataBayiSpinner);
                        } else {
                            spinner_anak.setHint(getString(R.string.belum_ada_data_anak));
                        }

                        if (spinner_anak.getItems() != null) {
                            layout_input.setVisibility(View.VISIBLE);
                            selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                            jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                        } else {
                            layout_input.setVisibility(View.GONE);
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
                startActivity(new Intent(context, InputDataAntropometriActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, InputDataAntropometriActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Bantuan(context).toastLong(getString(R.string.granted));
                } else {
                    new Bantuan(context).toastLong(getString(R.string.denied));
                }
                return;
            }
        }
    }
}
