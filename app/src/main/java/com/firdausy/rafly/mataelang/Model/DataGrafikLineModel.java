package com.firdausy.rafly.mataelang.Model;

import com.anychart.chart.common.dataentry.ValueDataEntry;

public class DataGrafikLineModel extends ValueDataEntry {

    public DataGrafikLineModel(String x, Number value) {
        super(x, value);
    }

    public DataGrafikLineModel(Number x, Number value) {
        super(x, value);
    }
}
