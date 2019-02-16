package com.firdausy.rafly.mataelang.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Model.BayiModel;
import com.firdausy.rafly.mataelang.Model.DataGrafikLineModel;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivityIbuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Context context = MainActivityIbuActivity.this;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;

    private AnyChartView chartPanjang, chartBerat;
    private List<DataEntry> listPanjang = new ArrayList<>();
    private List<DataEntry> listBerat = new ArrayList<>();
    private List<BayiModel> listDataBayi = new ArrayList<>();
    private List<String> listDataBayiSpinner = new ArrayList<>();
    private String selectedKeyBayi = null, jenisKelamin = null;
    private LinearLayout layout_grafik;
    private TextView tv_namaIbu;
    private MaterialSpinner spinner_anak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ibu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.data_anak);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        chartPanjang = findViewById(R.id.cv_panjangBadan);
        chartBerat = findViewById(R.id.cv_beratBadan);
        tv_namaIbu = findViewById(R.id.tv_namaIbu);
        spinner_anak = findViewById(R.id.spinner_anak);
        layout_grafik = findViewById(R.id.layout_grafik);

        chartPanjang.setProgressBar(findViewById(R.id.progress_bar));
        chartBerat.setProgressBar(findViewById(R.id.progress_bar2));
        chartPanjang.setZoomEnabled(false);
        chartBerat.setZoomEnabled(false);

        if(getIntent().hasExtra("keyselectedKeyBayi")){
            selectedKeyBayi = getIntent().getStringExtra("keyselectedKeyBayi");
        }


        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        spinner_anak.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (spinner_anak.getItems() != null) {
                    selectedKeyBayi = listDataBayi.get(spinner_anak.getSelectedIndex()).getKeyBayi();
                    jenisKelamin = listDataBayi.get(spinner_anak.getSelectedIndex()).getJenisKelamin();
                    getDataGrafik();

                    Intent intent = new Intent(context, MainActivityIbuActivity.class);
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

    private void getData() {
        databaseReference.child("user")
                .child("ibu")
                .child(firebaseAuth.getCurrentUser().getUid())
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
                .child(firebaseAuth.getCurrentUser().getUid())
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

    private void getDataGrafik() {
        if (selectedKeyBayi != null) {

            databaseReference.child("dataInput")
                    .child(firebaseAuth.getCurrentUser().getUid())
                    .child(selectedKeyBayi)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                layout_grafik.setVisibility(View.VISIBLE);
                                listPanjang.clear();
                                listBerat.clear();

                                int bulanKe = 0;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    //listPanjang.add(new DataGrafikLineModel(i, (Math.random())));
                                    listPanjang.add(new DataGrafikLineModel(bulanKe,
                                            Double.parseDouble(Objects.requireNonNull(
                                                    ds.child("panjangBadan")
                                                            .getValue(String.class)))));
                                    listBerat.add(new DataGrafikLineModel(bulanKe,
                                            Double.parseDouble(Objects.requireNonNull(
                                                    ds.child("beratBadan")
                                                            .getValue(String.class)))));
                                    bulanKe++;
                                }

//                                new Bantuan(context).alertDialogPeringatan("Panjang = " + String.valueOf(listPanjang.size()));
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        tv_emailPengguna.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference.child("user")
                    .child("ibu")
                    .child(firebaseAuth.getCurrentUser().getUid())
                    .child("namaLengkap")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                tv_namaPengguna.setText(dataSnapshot.getValue(String.class));
                                tv_tipePengguna.setText(getString(R.string.tipe_user_ibu));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                        }
                    });
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.action_dataAnak) {
            startActivity(new Intent(context, MainActivityIbuActivity.class));
            finish();
        } else if (id == R.id.action_posyandu) {
//            startActivity(new Intent(context, TambahAdminUserActivity.class));
//            finish();
        } else if (id == R.id.action_about) {
            startActivity(new Intent(context, TentangAplikasiIbuActivity.class));
            finish();
        } else if (id == R.id.action_edit) {
            startActivity(new Intent(context, IbuEditProfilActivity.class));
            finish();
        } else if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
