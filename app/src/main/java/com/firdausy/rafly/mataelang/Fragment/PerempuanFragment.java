package com.firdausy.rafly.mataelang.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firdausy.rafly.mataelang.R;


/**
 * A simple {@link Fragment} subclass.
 */
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

    public PerempuanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perempuan, container, false);
    }

}
