package com.firdausy.rafly.mataelang.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firdausy.rafly.mataelang.R;

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

        return v;
    }

}
