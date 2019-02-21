package com.firdausy.rafly.mataelang.Adapter;

import android.widget.TextView;

import com.app.feng.fixtablelayout.inter.IDataAdapter;
import com.firdausy.rafly.mataelang.Model.TableModelAntropometri;

import java.util.List;

public class TableAdapter implements IDataAdapter {

    public String[] titles;

    public List<TableModelAntropometri> data;

    public TableAdapter(String[] titles, List<TableModelAntropometri> data) {
        this.titles = titles;
        this.data = data;
    }

    public void setData(List<TableModelAntropometri> data) {
        this.data = data;
    }


    @Override
    public String getTitleAt(int i) {
        return titles[i];
    }

    @Override
    public int getTitleCount() {
        return titles.length;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void convertData(int i, List<TextView> list) {
        TableModelAntropometri modelAntropometri = data.get(i);

        list.get(0)
                .setText(modelAntropometri.data1);
        list.get(1)
                .setText(modelAntropometri.data2);
        list.get(2)
                .setText(modelAntropometri.data3);
        list.get(3)
                .setText(modelAntropometri.data4);
        list.get(4)
                .setText(modelAntropometri.data5);
    }

    @Override
    public void convertLeftData(int i, TextView textView) {
        textView.setText(data.get(i).data1);
    }
}
