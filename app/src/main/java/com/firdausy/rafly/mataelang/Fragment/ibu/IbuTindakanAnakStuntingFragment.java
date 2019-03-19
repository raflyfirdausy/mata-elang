package com.firdausy.rafly.mataelang.Fragment.ibu;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.InformasiPosyandu;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class IbuTindakanAnakStuntingFragment extends Fragment {

    private DatabaseReference databaseReference;
    private TextView tv_tindakanStunting;

    public IbuTindakanAnakStuntingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ibu_tindakan_anak_stunting, container, false);

        tv_tindakanStunting = v.findViewById(R.id.tv_tindakanStunting);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(InformasiPosyandu.ID_POSYANDU)
                .child("pengaturan")
                .child("tindakan")
                .child("stunting");

        setData();

        return v;
    }

    private void setData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tv_tindakanStunting.setText(dataSnapshot.getValue(String.class));
                } else {
                    tv_tindakanStunting.setText(getString(R.string.oopss_tindakan_anak_stunting));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Bantuan(getActivity()).alertDialogPeringatan(databaseError.getMessage());
            }
        });
    }

}
