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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditPasswordFragment extends Fragment {

    private EditText et_passwordSaatIni;
    private EditText et_PasswordBaru;
    private EditText et_ulangiPasswordBaru;
    private Button btn_simpan;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private AuthCredential credential;

    private ProgressDialog progressDialog;


    public EditPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_password, container, false);

        et_passwordSaatIni = v.findViewById(R.id.et_passwordSaatIni);
        et_PasswordBaru = v.findViewById(R.id.et_PasswordBaru);
        et_ulangiPasswordBaru = v.findViewById(R.id.et_ulangiPasswordBaru);
        btn_simpan = v.findViewById(R.id.btn_simpan);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesUpdatePassword();
            }
        });

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        return v;
    }

    private void prosesUpdatePassword() {
        if ((TextUtils.isEmpty(et_passwordSaatIni.getText().toString())) ||
                (TextUtils.isEmpty(et_PasswordBaru.getText().toString())) ||
                (TextUtils.isEmpty(et_ulangiPasswordBaru.getText().toString()))) {
            new Bantuan(getActivity()).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else if (!et_PasswordBaru.getText().toString().equals(et_ulangiPasswordBaru.getText().toString())) {
            new Bantuan(getActivity()).alertDialogPeringatan(getString(R.string.pass_salah));
        } else {
            //TODO : proses Simpan Data
            progressDialog = ProgressDialog.show(getActivity(),
                    "Tunggu Beberapa Saat",
                    "Proses Update Password ...",
                    true);

            credential = EmailAuthProvider.getCredential(Objects.requireNonNull(
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()),
                    et_passwordSaatIni.getText().toString());

            firebaseAuth.getCurrentUser()
                    .reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser()
                                        .updatePassword(et_PasswordBaru.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    new Bantuan(getActivity())
                                                            .alertDialogInformasi(
                                                                    getString(R.string.pass_berhasil_update));

                                                    et_passwordSaatIni.setText("");
                                                    et_PasswordBaru.setText("");
                                                    et_ulangiPasswordBaru.setText("");
                                                } else {
                                                    new Bantuan(getActivity()).alertDialogPeringatan(
                                                            Objects.requireNonNull(
                                                                    task.getException()).getMessage());
                                                }
                                            }
                                        });
                            } else {
                                new Bantuan(getActivity()).alertDialogPeringatan(getString(R.string.pass_lama_salahh));
                            }
                        }
                    });
        }
    }
}
