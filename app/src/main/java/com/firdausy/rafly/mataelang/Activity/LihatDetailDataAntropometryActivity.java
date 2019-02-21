package com.firdausy.rafly.mataelang.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.app.feng.fixtablelayout.FixTableLayout;
import com.app.feng.fixtablelayout.inter.ILoadMoreListener;
import com.firdausy.rafly.mataelang.Adapter.TableAdapter;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Model.BayiModel;
import com.firdausy.rafly.mataelang.Model.DataGrafikLineModel;
import com.firdausy.rafly.mataelang.Model.TableModelAntropometri;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class LihatDetailDataAntropometryActivity extends AppCompatActivity {

    private Context context = LihatDetailDataAntropometryActivity.this;
    private AnyChartView chartPanjang, chartBerat;
    private List<DataEntry> listPanjang = new ArrayList<>();
    private List<DataEntry> listBerat = new ArrayList<>();
    private List<BayiModel> listDataBayi = new ArrayList<>();
    private List<String> listDataBayiSpinner = new ArrayList<>();
    private String selectedKeyBayi = null, jenisKelamin = null;
    private LinearLayout layout_grafik;
    private FixTableLayout fixTableLayout;
    private TableAdapter tableAdapter;

    private TextView tv_namaIbu;
    private MaterialSpinner spinner_anak;
    private DatabaseReference databaseReference;

    public String[] title = {"Bulan ke","Tanggal Input","Berat Badan","Panjang Badan","Keterangan"};
    public List<TableModelAntropometri> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_detail_data_antropometry);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.mata_elang);
        getSupportActionBar().setSubtitle(R.string.input_data_antropometri);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        chartPanjang = findViewById(R.id.cv_panjangBadan);
        chartBerat = findViewById(R.id.cv_beratBadan);
        tv_namaIbu = findViewById(R.id.tv_namaIbu);
        spinner_anak = findViewById(R.id.spinner_anak);
        layout_grafik = findViewById(R.id.layout_grafik);

        fixTableLayout = findViewById(R.id.fixTableLayout);

        chartPanjang.setProgressBar(findViewById(R.id.progress_bar));
        chartBerat.setProgressBar(findViewById(R.id.progress_bar2));
        chartPanjang.setZoomEnabled(false);
        chartBerat.setZoomEnabled(false);

        if(getIntent().hasExtra("keyselectedKeyBayi")){
            selectedKeyBayi = getIntent().getStringExtra("keyselectedKeyBayi");
        }


        databaseReference = FirebaseDatabase.getInstance().getReference();

        spinner_anak.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (spinner_anak.getItems() != null) {
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                    jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                    getDataGrafik();

                    Intent intent = new Intent(context, LihatDetailDataAntropometryActivity.class);
                    intent.putExtra("keyselectedKeyBayi", listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi());
                    intent.putExtra("keyIbu", getIntent().getStringExtra("keyIbu"));
                    intent.putExtra("selectedIndex", spinner_anak.getSelectedIndex());
                    startActivity(intent);
                    finish();
                }
            }
        });

        spinner_anak.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {
            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                if (spinner_anak.getItems() != null) {
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                    jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                }
            }
        });

        getData();
    }


    private void getDataGrafik() {
        if (selectedKeyBayi != null) {

            databaseReference.child("dataInput")
                    .child(getIntent().getStringExtra("keyIbu"))
                    .child(selectedKeyBayi)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                layout_grafik.setVisibility(View.VISIBLE);
                                listPanjang.clear();
                                listBerat.clear();
                                data.clear();

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    listPanjang.add(new DataGrafikLineModel(
                                            String.valueOf(Objects.requireNonNull(ds.getKey()).charAt(8)) +
                                                    String.valueOf(Objects.requireNonNull(ds.getKey()).charAt(9)),
                                            Double.parseDouble(Objects.requireNonNull(
                                                    ds.child("panjangBadan")
                                                            .getValue(String.class)))));
                                    listBerat.add(new DataGrafikLineModel(
                                            String.valueOf(Objects.requireNonNull(ds.getKey()).charAt(8)) +
                                            String.valueOf(Objects.requireNonNull(ds.getKey()).charAt(9)),
                                            Double.parseDouble(Objects.requireNonNull(
                                                    ds.child("beratBadan")
                                                            .getValue(String.class)))));

                                    data.add(new TableModelAntropometri(
                                            String.valueOf(Objects.requireNonNull(ds.getKey()).charAt(8)) +
                                                    String.valueOf(Objects.requireNonNull(ds.getKey()).charAt(9)),
                                            ds.child("tanggalInput").getValue(String.class),
                                            ds.child("beratBadan").getValue(String.class) + " Kg",
                                            ds.child("panjangBadan").getValue(String.class) + " Cm",
                                            ds.child("hasil").getValue(String.class)
                                    ));
                                }

                                tableAdapter = new TableAdapter(title,data);
                                fixTableLayout.setAdapter(tableAdapter);

                                getGrafikPanjang(listPanjang);
                                getGrafikBerat(listBerat);
                            } else {
                                layout_grafik.setVisibility(View.GONE);
                                new Bantuan(context).alertDialogPeringatan("Belum Ada Data Pada Anak Tersebut !");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            layout_grafik.setVisibility(View.GONE);
        }
    }

    private void getData() {
        databaseReference.child("user")
                .child("ibu")
                .child(getIntent().getStringExtra("keyIbu"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("namaLengkap").exists()) {
                            tv_namaIbu.setText(dataSnapshot.child("namaLengkap").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

        //set spinner
        databaseReference.child("bayi")
                .child(getIntent().getStringExtra("keyIbu"))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        BayiModel bayiModel = null;
                        listDataBayi.clear();

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                bayiModel = ds.getValue(BayiModel.class);
                                Objects.requireNonNull(bayiModel).setKeyBayi(ds.getKey());
                                bayiModel.setKeyIbu(dataSnapshot.getKey());
                                listDataBayi.add(bayiModel);
                            }

                            for (int i = 0; i < listDataBayi.size(); i++) {
                                listDataBayiSpinner.add(listDataBayi.get(i).getNamaLengkapBayi());
                            }

                            spinner_anak.setHint(getString(R.string.pilih_data_bayi));
                            spinner_anak.setItems(listDataBayiSpinner);
                        } else {
                            spinner_anak.setHint(getString(R.string.belum_ada_data_anak));
                        }

                        if (spinner_anak.getItems() != null) {
                            if(selectedKeyBayi == null){
                                selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                                jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                            } else {

                                spinner_anak.setSelectedIndex(getIntent().getIntExtra("selectedIndex", 0));
                            }

                            getDataGrafik();

                        } else {
                            layout_grafik.setVisibility(View.GONE);
                            selectedKeyBayi = null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(context, LihatDataAntropometriActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, LihatDataAntropometriActivity.class));
        finish();
    }

    private void getGrafikPanjang(List<DataEntry> listPanjang) {
        APIlib.getInstance().setActiveAnyChartView(chartPanjang);
        List<DataEntry> listPanjangFix = new ArrayList<>();
        listPanjangFix.clear();

        listPanjangFix.addAll(listPanjang);

        Cartesian cartesianPanjang = AnyChart.line();
        cartesianPanjang.animation(true);
        cartesianPanjang.padding(10d, 20d, 20d, 10d);
        cartesianPanjang.crosshair().enabled(false);
        cartesianPanjang.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesianPanjang.title("Grafik Panjang Badan");
        cartesianPanjang.tooltip().title("Detail");
        cartesianPanjang.yAxis(0).title("Panjang Badan (Cm)");
        cartesianPanjang.xAxis(0).title("Bulan Ke");


        Set dataSetPanjang = Set.instantiate();
        dataSetPanjang.data(listPanjangFix);
        Mapping mappingPanjang = dataSetPanjang.mapAs("{ x: 'x', value: 'value' }");
        Line linePanjang = cartesianPanjang.line(mappingPanjang);
        linePanjang.name("Panjang ");
        linePanjang.markers().enabled();
        linePanjang.hovered().markers().enabled(true);
        linePanjang.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);

        linePanjang.tooltip()
                .title(true)
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesianPanjang.legend().enabled(false);
        chartPanjang.setChart(cartesianPanjang);
        APIlib.getInstance().setActiveAnyChartView(null);
    }

    private void getGrafikBerat(List<DataEntry> listBerat) {
        APIlib.getInstance().setActiveAnyChartView(chartBerat);
        List<DataEntry> listBeratFix = new ArrayList<>();
        listBeratFix.clear();

        listBeratFix.addAll(listBerat);


        Cartesian cartesianBerat = AnyChart.line();
        cartesianBerat.animation(true);
        cartesianBerat.padding(10d, 20d, 20d, 10d);
        cartesianBerat.crosshair().enabled(false);
        cartesianBerat.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesianBerat.title("Grafik Berat Badan");
        cartesianBerat.tooltip().title("Detail");
        cartesianBerat.yAxis(0).title("Berat Badan (Kg)");
        cartesianBerat.xAxis(0).title("Bulan Ke");

        Set dataSet = Set.instantiate();
        dataSet.data(listBeratFix);
        Mapping mappingBerat = dataSet.mapAs("{ x: 'x', value: 'value' }");
        Line lineBerat = cartesianBerat.line(mappingBerat);
        lineBerat.name("Berat ");
        lineBerat.hovered().markers().enabled(true);
        lineBerat.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);

        lineBerat.tooltip()
                .title(true)
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesianBerat.legend().enabled(false);
        chartBerat.setChart(cartesianBerat);
        APIlib.getInstance().setActiveAnyChartView(null);
    }
}
