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


    public LakiLakiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laki_laki, container, false);
    }

}
