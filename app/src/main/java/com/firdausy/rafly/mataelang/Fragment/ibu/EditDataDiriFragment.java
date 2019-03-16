package com.firdausy.rafly.mataelang.Fragment.ibu;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditDataDiriFragment extends Fragment {

    private EditText et_email;
    private EditText et_namaLengkap;
    private EditText et_nomerHp;
    private EditText et_alamatLengkap;
    private Button btn_edit;
    private Button btn_simpan;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    public EditDataDiriFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_data_diri, container, false);

        et_email = v.findViewById(R.id.et_email);
        et_namaLengkap = v.findViewById(R.id.et_namaLengkap);
        et_nomerHp = v.findViewById(R.id.et_nomerHp);
        et_alamatLengkap = v.findViewById(R.id.et_alamatLengkap);
        btn_edit = v.findViewById(R.id.btn_edit);
        btn_simpan = v.findViewById(R.id.btn_simpan);
        btn_simpan.setEnabled(false);

        et_namaLengkap.setEnabled(false);
        et_nomerHp.setEnabled(false);
        et_alamatLengkap.setEnabled(false);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_edit.getText().toString().equalsIgnoreCase(getString(R.string.edit))) {
                    btn_edit.setText(getString(R.string.batall));
                    et_namaLengkap.setEnabled(true);
                    et_nomerHp.setEnabled(true);
                    et_alamatLengkap.setEnabled(true);
                    btn_simpan.setEnabled(true);
                } else {
                    btn_edit.setText(getString(R.string.edit));
                    et_namaLengkap.setEnabled(false);
                    et_nomerHp.setEnabled(false);
                    et_alamatLengkap.setEnabled(false);
                    btn_simpan.setEnabled(false);
                }
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesSimpan();
            }
        });

        setData();

        return v;
    }

    private void setData() {

        databaseReference.child("user")
                .child("ibu")
                .child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    et_email.setText(dataSnapshot.child("email").getValue(String.class));
                    et_namaLengkap.setText(dataSnapshot.child("namaLengkap").getValue(String.class));
                    et_nomerHp.setText(dataSnapshot.child("nomerHp").getValue(String.class));
                    et_alamatLengkap.setText(dataSnapshot.child("alamatLengkap").getValue(String.class));
                } 
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Bantuan(getActivity()).alertDialogPeringatan(databaseError.getMessage());
            }
        });
    }

    private void prosesSimpan() {
        if ((TextUtils.isEmpty(et_email.getText().toString())) ||
                (TextUtils.isEmpty(et_namaLengkap.getText().toString())) ||
                (TextUtils.isEmpty(et_nomerHp.getText().toString())) ||
                (TextUtils.isEmpty(et_alamatLengkap.getText().toString()))) {
            new Bantuan(getActivity()).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else {
            //TODO : proses Simpan Data
            progressDialog = ProgressDialog.show(getActivity(),
                    "Tunggu Beberapa Saat",
                    "Proses Menyimpan Data ...",
                    true);

            Map data = new HashMap();
            data.put("email", et_email.getText().toString());
            data.put("namaLengkap", et_namaLengkap.getText().toString());
            data.put("nomerHp", et_nomerHp.getText().toString());
            data.put("alamatLengkap", et_alamatLengkap.getText().toString());

            databaseReference.child("user")
                    .child("ibu")
                    .child(firebaseAuth.getCurrentUser().getUid())
                    .setValue(data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                new Bantuan(getActivity()).alertDialogInformasi(getString(R.string.data_berhasil_di_simpan));
                                et_namaLengkap.setEnabled(false);
                                et_nomerHp.setEnabled(false);
                                et_alamatLengkap.setEnabled(false);
                            } else {
                                new Bantuan(getActivity()).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });

        }
    }
}
