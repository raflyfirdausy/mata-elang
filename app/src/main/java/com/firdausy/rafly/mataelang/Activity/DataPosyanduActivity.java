package com.firdausy.rafly.mataelang.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.ibu.IbuCaraPencegahanStuntingActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.IbuEditProfilActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.IbuTindakanUntukAnak;
import com.firdausy.rafly.mataelang.Activity.ibu.MainActivityIbuActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.TentangAplikasiIbuActivity;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.InformasiPosyandu;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DataPosyanduActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = DataPosyanduActivity.this;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;

    private TextView tv_namaPosyandu;
    private TextView tv_alamatPosyandu;
    private TextView tv_kecamatan;
    private TextView tv_nomerHpKantor;
    private Button btn_hubungi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_posyandu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.data_posyandu);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        tv_namaPosyandu = findViewById(R.id.tv_namaPosyandu);
        tv_alamatPosyandu = findViewById(R.id.tv_alamatPosyandu);
        tv_kecamatan = findViewById(R.id.tv_kecamatan);
        tv_nomerHpKantor = findViewById(R.id.tv_nomerHpKantor);
        btn_hubungi = findViewById(R.id.btn_hubungi);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("user_posyandu")
                .child(InformasiPosyandu.ID_POSYANDU)
                .child("detailPosyandu");
        databaseReference.keepSynced(true);

        setData();


    }

    private void setData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tv_namaPosyandu.setText(dataSnapshot.child("namaPosyandu").getValue(String.class));
                            tv_alamatPosyandu.setText(dataSnapshot.child("alamatPosyandu").getValue(String.class));
                            tv_kecamatan.setText(dataSnapshot.child("kecamatan").getValue(String.class));
                            tv_nomerHpKantor.setText(dataSnapshot.child("nomerHpKantor").getValue(String.class));

                            btn_hubungi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                                                == PackageManager.PERMISSION_GRANTED) {
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse("tel:" + dataSnapshot.child("nomerHpKantor")
                                                    .getValue(String.class)));
                                            startActivity(intent);
                                        } else {
                                            ActivityCompat.requestPermissions(DataPosyanduActivity.this,
                                                    new String[]{Manifest.permission.CALL_PHONE}, 1);
                                        }
                                    }
                                }
                            });

                        } else {
                            new Bantuan(context).alertDialogPeringatan(getString(R.string.data_tidak_ditemukann));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
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
            startActivity(new Intent(context, DataPosyanduActivity.class));
            finish();
        } else if (id == R.id.action_pencegahan) {
            startActivity(new Intent(context, IbuCaraPencegahanStuntingActivity.class));
            finish();
        } else if (id == R.id.action_tindakan) {
            startActivity(new Intent(context, IbuTindakanUntukAnak.class));
            finish();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Bantuan(context).toastLong(getString(R.string.granted));
                } else {
                    new Bantuan(context).toastLong(getString(R.string.denied));
                }
                return ;
            }
        }
    }
}
