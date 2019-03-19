package com.firdausy.rafly.mataelang.Activity.admin;

import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.IbuDetailActivity;
import com.firdausy.rafly.mataelang.Activity.LoginActivity;
import com.firdausy.rafly.mataelang.Activity.MainActivity;
import com.firdausy.rafly.mataelang.Adapter.IbuAdapterInput;
import com.firdausy.rafly.mataelang.Helper.AdManager;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.InformasiPosyandu;
import com.firdausy.rafly.mataelang.Model.IbuModel;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InputDataAntropometriActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = InputDataAntropometriActivity.this;
    private FirebaseAuth firebaseAuth;
    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;
    private DatabaseReference databaseReference;
    private ListView lv_konten;
    private List<IbuModel> list = new ArrayList<>();
    private IbuAdapterInput ibuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data_antropometri);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.input_data_antropometri);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        lv_konten = findViewById(R.id.lv_konten);

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
                        IbuModel ibuModel = null;
                        list.clear();

                        if(dataSnapshot.exists()){
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                if(ds.child("id_posyandu").getValue(String.class).equals(InformasiPosyandu.ID_POSYANDU)){
                                    ibuModel = ds.getValue(IbuModel.class);
                                    ibuModel.setKeyIbu(ds.getKey());
                                    list.add(ibuModel);
                                }
                            }

                            ibuAdapter = new IbuAdapterInput(InputDataAntropometriActivity.this , list);
                            lv_konten.setAdapter(ibuAdapter);
                            lv_konten.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //TODO : pindah ke activity isi data Antropometri\
                                    Intent intent = new Intent(context, IbuDetailActivity.class);
                                    intent.putExtra("keyIbu", list.get(position).getKeyIbu());
                                    startActivity(intent);
                                }
                            });

                        } else {
                            new Bantuan(context).alertDialogPeringatan(getString(R.string.tidak_ditemukan));
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_antropometri, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO : Action ketika tombol submit :v
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //TODO : Action ketika textnya berubah seperti dia yang sekarang bukan lagi yang dulu :(
                if (TextUtils.isEmpty(newText)) {
                    ibuAdapter.cariPesan("");
                    lv_konten.clearTextFilter();
                } else {
                    ibuAdapter.cariPesan(newText);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
