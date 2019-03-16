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

public class LakiLakiFragment extends Fragment {

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

    private EditText[] editTexts;
    private Button btn_reset;
    private Button btn_simpan;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;


    public LakiLakiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_laki_laki, container, false);

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
                    editTexts[i].setText(String.valueOf(PB_defaultLakiLaki[i]));
                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("pengaturan")
                .child("antropometri")
                .child("lakilaki");

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
