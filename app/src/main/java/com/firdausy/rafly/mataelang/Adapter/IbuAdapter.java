package com.firdausy.rafly.mataelang.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firdausy.rafly.mataelang.Model.IbuModel;
import com.firdausy.rafly.mataelang.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class IbuAdapter extends BaseAdapter {
    private Activity activity;
    private List<IbuModel> data;
    private List<IbuModel> dataSementara;

    public IbuAdapter(Activity activity, List<IbuModel> data) {
        this.activity = activity;
        this.data = data;
        this.dataSementara = new ArrayList<>();
        this.dataSementara.addAll(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ibuHolder ibuHolder = null;
        if (convertView == null) {
            ibuHolder = new ibuHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.item_ibu, parent, false);

            ibuHolder.tv_namaIbu = convertView.findViewById(R.id.tv_namaIbu);
            ibuHolder.tv_emailIbu = convertView.findViewById(R.id.tv_emailIbu);
            ibuHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            convertView.setTag(ibuHolder);
        } else {
            ibuHolder = (ibuHolder) convertView.getTag();
        }

//        ibuHolder.tv_namaIbu.setId(position);
//        ibuHolder.iv_icon.setId(position);
//        ibuHolder.tv_emailIbu.setId(position);

        String firstLetter = String.valueOf(data.get(position).getNamaLengkap().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(getItem(position));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);
        ibuHolder.iv_icon.setImageDrawable(drawable);
        ibuHolder.tv_namaIbu.setText(data.get(position).getNamaLengkap());
        ibuHolder.tv_emailIbu.setText(data.get(position).getEmail());
        return convertView;
    }

    public void cariPesan(String text) {
        text = text.toLowerCase(Locale.getDefault());
        data.clear();
        if (text.length() == 0) {
            data.addAll(dataSementara);
        } else {
            for (int i = 0; i < dataSementara.size(); i++) {
                if (dataSementara.get(i).getNamaLengkap().toLowerCase(Locale.getDefault()).contains(text)) {
                    data.add(dataSementara.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }
}

class ibuHolder{
    TextView tv_namaIbu,tv_emailIbu;
    ImageView iv_icon;
}
