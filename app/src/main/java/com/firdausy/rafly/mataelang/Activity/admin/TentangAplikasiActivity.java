package com.firdausy.rafly.mataelang.Activity.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.LoginActivity;
import com.firdausy.rafly.mataelang.Activity.MainActivity;
import com.firdausy.rafly.mataelang.Chat.ListUser;
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

public class TentangAplikasiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = TentangAplikasiActivity.this;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;
    private TextView pengembang1;
    private TextView pengembang2;
    private TextView pengembang3;
    private TextView pengembang4;
    private TextView jabatan1;
    private TextView jabatan2;
    private TextView jabatan3;
    private TextView jabatan4;
    private TextView tv_tentangApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang_aplikasi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.tentang_aplikasi);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(6).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        pengembang1 = findViewById(R.id.pengembang1);
        pengembang2 = findViewById(R.id.pengembang2);
        pengembang3 = findViewById(R.id.pengembang3);
        pengembang4 = findViewById(R.id.pengembang4);
        jabatan1 = findViewById(R.id.jabatan1);
        jabatan2 = findViewById(R.id.jabatan2);
        jabatan3 = findViewById(R.id.jabatan3);
        jabatan4 = findViewById(R.id.jabatan4);
        tv_tentangApp = findViewById(R.id.tv_tentangApp);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        databaseReference.child("build_config").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    tv_tentangApp.setText(getString(R.string.versi_aplikasi) +
                            dataSnapshot.child("VERSION_NAME").getValue(String.class) +
                            "\n" +
                            getString(R.string.terakhir_update) +
                            dataSnapshot.child("TERAKHIR_UPDATE").getValue(String.class)
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new  Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
            }
        });

        setData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        tv_emailPengguna.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference.child("user_posyandu").child(firebaseAuth.getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                InformasiPosyandu.IS_SUPER_USER = true;
                                InformasiPosyandu.ID_POSYANDU = firebaseAuth.getCurrentUser().getUid();
                                tv_namaPengguna.setText(dataSnapshot.child("detailPosyandu").child("namaPosyandu").getValue(String.class));
                                tv_tipePengguna.setText("Jenis Akun : " + getString(R.string.kepala));
                            } else {
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
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                        }
                    });
        }

    }

    private void setData() {
        databaseReference.child("about")
                .child("pengembang")
                .child("pengembang1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            pengembang1.setText(dataSnapshot.child("nama").getValue(String.class));
                            jabatan1.setText(dataSnapshot.child("jabatan").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

        databaseReference.child("about")
                .child("pengembang")
                .child("pengembang2")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            pengembang2.setText(dataSnapshot.child("nama").getValue(String.class));
                            jabatan2.setText(dataSnapshot.child("jabatan").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

        databaseReference.child("about")
                .child("pengembang")
                .child("pengembang3")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            pengembang3.setText(dataSnapshot.child("nama").getValue(String.class));
                            jabatan3.setText(dataSnapshot.child("jabatan").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

        databaseReference.child("about")
                .child("programmer")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(Objects.requireNonNull(dataSnapshot.child("status").getValue(String.class))
                                    .equalsIgnoreCase("1")){

                                pengembang4.setText(dataSnapshot.child("nama").getValue(String.class));
                                jabatan4.setText(dataSnapshot.child("jabatan").getValue(String.class));

                                pengembang4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse("http://instagram.com/rafly_firdausy")));
                                    }
                                });

                                pengembang4.setVisibility(View.VISIBLE);
                                jabatan4.setVisibility(View.VISIBLE);
                            } else {
                                pengembang4.setVisibility(View.GONE);
                                jabatan4.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
            startActivity(new Intent(context, EditProfilActivity.class));
            finish();
        } else if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        } else if (id == R.id.action_about) {
            startActivity(new Intent(context, TentangAplikasiActivity.class));
            finish();
        } else if(id == R.id.action_chat) {
            startActivity(new Intent(context, ListUser.class).putExtra("level","admin"));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
