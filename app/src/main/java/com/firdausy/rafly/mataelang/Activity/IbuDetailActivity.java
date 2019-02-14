package com.firdausy.rafly.mataelang.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Model.BayiModel;
import com.firdausy.rafly.mataelang.Model.IbuModel;
import com.firdausy.rafly.mataelang.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IbuDetailActivity extends AppCompatActivity {

    private Context context = IbuDetailActivity.this;
    private TextView tv_namaIbu;
    private MaterialSpinner spinner_anak;
    private FloatingActionButton fab_tambahDataAnak;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private List<BayiModel> listDataBayi = new ArrayList<>();
    private List<String> listDataBayiSpinner = new ArrayList<>();
    private String selectedKeyBayi;

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
                if(spinner_anak.getItems() != null){
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                }
            }
        });

        spinner_anak.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                if(spinner_anak.getItems() != null){
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                }
            }
        });
    }

    private void getAndSetData() {
        databaseReference.child("user")
                .child("ibu")
                .child(getIntent().getStringExtra("keyIbu"))
                .child("namaLengkap")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            tv_namaIbu.setText(dataSnapshot.getValue(String.class));
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

                        if(dataSnapshot.exists()){
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
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
}
