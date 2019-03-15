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

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class TindakanAnakNormalFragment extends Fragment {

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private EditText et_tindakan;
    private Button btn_edit;
    private Button btn_simpan;

    public TindakanAnakNormalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tindakan_anak_normal, container, false);

        et_tindakan = v.findViewById(R.id.et_tindakan);
        btn_edit = v.findViewById(R.id.btn_edit);
        btn_simpan = v.findViewById(R.id.btn_simpan);
        btn_simpan.setEnabled(false);
        et_tindakan.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("pengaturan")
                .child("tindakan")
                .child("normal");

        setData();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn_edit.getText().toString().equalsIgnoreCase("edit")){
                    et_tindakan.setEnabled(true);
                    btn_simpan.setEnabled(true);
                    btn_edit.setText(getString(R.string.batal));
                } else {
                    et_tindakan.setEnabled(false);
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

        return v;
    }

    private void prosesSimpan() {
        if ((TextUtils.isEmpty(et_tindakan.getText().toString()))){
            new Bantuan(getActivity()).alertDialogPeringatan(getString(R.string.tindakan_tidak_boleh_kosong));
        } else {
            progressDialog = ProgressDialog.show(getActivity(),
                    "Tunggu Beberapa Saat",
                    "Proses Menyimpan Data ...",
                    true);

            databaseReference.setValue(et_tindakan.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                new Bantuan(getActivity()).alertDialogInformasi(getString(R.string.data_berhasil_di_simpan));
                                et_tindakan.setEnabled(false);
                                btn_simpan.setEnabled(false);
                                btn_edit.setText(getString(R.string.edit));
                            } else {
                                new Bantuan(getActivity()).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
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
                    et_tindakan.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Bantuan(getActivity()).alertDialogPeringatan(databaseError.getMessage());
            }
        });
    }

}
