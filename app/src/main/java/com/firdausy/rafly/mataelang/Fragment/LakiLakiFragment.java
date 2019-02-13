package com.firdausy.rafly.mataelang.Fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firdausy.rafly.mataelang.Activity.PengaturanAntrompometriActivity;
import com.firdausy.rafly.mataelang.R;

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

        return v;
    }




}
