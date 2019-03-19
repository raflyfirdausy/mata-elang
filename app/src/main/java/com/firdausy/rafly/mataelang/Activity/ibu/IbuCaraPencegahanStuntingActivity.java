package com.firdausy.rafly.mataelang.Activity.ibu;

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
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.DataPosyanduActivity;
import com.firdausy.rafly.mataelang.Activity.LoginActivity;
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

public class IbuCaraPencegahanStuntingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = IbuCaraPencegahanStuntingActivity.this;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;

    private TextView tv_pencegahan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibu_cara_pencegahan_stunting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.cara_pencegahan_stunting);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("user_posyandu")
                .child(InformasiPosyandu.ID_POSYANDU)
                .child("pengaturan")
                .child("pencegahan");
        databaseReference.keepSynced(true);

        tv_pencegahan = findViewById(R.id.tv_pencegahan);

        setData();

    }

    private void setData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tv_pencegahan.setText(dataSnapshot.getValue(String.class));
                } else {
                    tv_pencegahan.setText(getString(R.string.oopss_pencegahan_stunting_belum_tersedia));
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
}
