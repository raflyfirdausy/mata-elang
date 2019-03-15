package com.firdausy.rafly.mataelang.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PerempuanFragment extends Fragment {


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

    private EditText[] editTexts;
    private Button btn_reset;
    private Button btn_simpan;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    public PerempuanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perempuan, container, false);

        btn_reset = v.findViewById(R.id.btn_reset);
        btn_simpan = v.findViewById(R.id.btn_simpan);

        editTexts = new EditText[25];
        for(int i = 0; i < 25 ; i++){
            String editTextID = "et_pb" + i;
            int resID = getResources().getIdentifier(editTextID, "id", Objects.requireNonNull(getActivity()).getPackageName());
            editTexts[i] = v.findViewById(resID);
        }

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < 25 ; i++){
                    editTexts[i].setText(String.valueOf(PB_defaultPerempuan[i]));
                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("pengaturan")
                .child("antropometri")
                .child("perempuan");

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesSimpan();
            }
        });

        getData();

        return v;
    }

    private void getData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < 25; i++) {
                    if(i < 10){
                        editTexts[i].setText(dataSnapshot.child("bulan0" + i).getValue(String.class));
                    } else {
                        editTexts[i].setText(dataSnapshot.child("bulan" + i).getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Bantuan(getActivity()).alertDialogPeringatan(databaseError.getMessage());
            }
        });
    }

    private void prosesSimpan() {
        boolean adayangKosong = false;
        for(int i = 0; i < 25 ; i++){
            if((TextUtils.isEmpty(editTexts[i].getText().toString()))){
                adayangKosong = true;
                break;
            }
        }

        if (adayangKosong){
            new Bantuan(getActivity()).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else {
            //TODO : proses Simpan
            progressDialog = ProgressDialog.show(getActivity(),
                    "Tunggu Beberapa Saat",
                    "Proses Menyimpan ...",
                    true);

            Map data = new HashMap();
            for (int i = 0; i < 25 ; i++){
                if(i < 10){
                    data.put("bulan0" + i , (editTexts[i].getText().toString()));
                } else {
                    data.put("bulan" + i , (editTexts[i].getText().toString()));
                }
            }

            databaseReference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        new Bantuan(getActivity()).alertDialogInformasi("Data Berhasil Di Simpan !");
                    } else {
                        new Bantuan(getActivity()).alertDialogInformasi(Objects.requireNonNull(task.getException()).getMessage());
                    }
                }
            });
        }
    }

}
