package com.firdausy.rafly.mataelang.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = MainActivity.this;
    private FirebaseAuth firebaseAuth;
    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;
    private TextView tv_totalIbu;
    private TextView tv_totalBayi;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.dashboard);

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
        tv_totalIbu = findViewById(R.id.tv_totalIbu);
        tv_totalBayi = findViewById(R.id.tv_totalBayi);

        LinearLayout ll_ibu = findViewById(R.id.ll_ibu);
        LinearLayout ll_bayi = findViewById(R.id.ll_bayi);

        ll_ibu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, InputDataAntropometriActivity.class));
                finish();
            }
        });

        ll_bayi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LihatDataAntropometriActivity.class));
                finish();
            }
        });

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        getAndSetData();


    }

    private void getAndSetData() {
        databaseReference.child("user")
                .child("ibu")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int jumlahIbu = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                jumlahIbu++;
                            }
                            tv_totalIbu.setText(String.valueOf(jumlahIbu) + " ibu");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        databaseReference.child("bayi")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int jumlahBayi = 0;
                        if(dataSnapshot.exists()){
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                for (DataSnapshot DS : ds.getChildren()){
                                    jumlahBayi ++;
                                }
                            }
                        }
                        tv_totalBayi.setText(String.valueOf(jumlahBayi) + " bayi");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        tv_emailPengguna.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference.child("user")
                    .child("admin")
                    .child(firebaseAuth.getCurrentUser().getUid())
                    .child("namaLengkap")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                tv_namaPengguna.setText(dataSnapshot.getValue(String.class));
                                tv_tipePengguna.setText(getString(R.string.tipe_admin));
                            } else {
                                startActivity(new Intent(context, MainActivityIbuActivity.class));
                                finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.action_about){
//            new Bantuan(context).alertDialogDebugging("About Coming Soon !");
//        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        if (id == R.id.action_dashboard) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        } else if (id == R.id.action_tambahUser) {
            startActivity(new Intent(context, TambahAdminUserActivity.class));
            finish();
        } else if (id == R.id.action_input) {
            startActivity(new Intent(context, InputDataAntropometriActivity.class));
            finish();
        } else if (id == R.id.action_lihat) {
            startActivity(new Intent(context, LihatDataAntropometriActivity.class));
            finish();
        } else if (id == R.id.action_pengaturan) {
            startActivity(new Intent(context, PengaturanActivity.class));
            finish();
        } else if (id == R.id.action_edit) {

        } else if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        } else if (id == R.id.action_about) {
            startActivity(new Intent(context, TentangAplikasiActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
