package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Activity.DataPosyanduActivity;
import com.firdausy.rafly.mataelang.Activity.LoginActivity;
import com.firdausy.rafly.mataelang.Activity.MainActivity;
import com.firdausy.rafly.mataelang.Activity.SplashScreenActivity;
import com.firdausy.rafly.mataelang.Activity.admin.AdminKodePosyanduActivity;
import com.firdausy.rafly.mataelang.Activity.admin.EditProfilActivity;
import com.firdausy.rafly.mataelang.Activity.admin.InputDataAntropometriActivity;
import com.firdausy.rafly.mataelang.Activity.admin.LihatDataAntropometriActivity;
import com.firdausy.rafly.mataelang.Activity.admin.PengaturanActivity;
import com.firdausy.rafly.mataelang.Activity.admin.TambahAdminUserActivity;
import com.firdausy.rafly.mataelang.Activity.admin.TentangAplikasiActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.IbuCaraPencegahanStuntingActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.IbuEditProfilActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.IbuTindakanUntukAnak;
import com.firdausy.rafly.mataelang.Activity.ibu.MainActivityIbuActivity;
import com.firdausy.rafly.mataelang.Activity.ibu.TentangAplikasiIbuActivity;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.Helper.InformasiPosyandu;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ListUser extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context context;
    private RecyclerView recyclerView;
    private FloatingActionButton fabnewchat;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<String> listchat = new ArrayList<>();
    private ArrayList<ListModel> listchat2 = new ArrayList<>();
    private AdapterListChat adapter;
    private String owner;
    private RelativeLayout rl_belumChatSiapapun;

    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
            setContentView(R.layout.list_user_baru_lagi_hehehe);
        } else {
            setContentView(R.layout.list_user_new);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.chat_kader_dan_ibu);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
            navigationView.getMenu().getItem(5).setChecked(true);
        } else {
            navigationView.getMenu().getItem(4).setChecked(true);
        }
        navigationView.setNavigationItemSelectedListener(this);

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        context = ListUser.this;
        recyclerView = findViewById(R.id.rv);
        fabnewchat = findViewById(R.id.fab_new_chat);
        rl_belumChatSiapapun = findViewById(R.id.rl_belumChatSiapapun);
        rl_belumChatSiapapun.setVisibility(View.GONE);
        owner = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chat");

        firebaseAuth = FirebaseAuth.getInstance();
        final Intent intent = getIntent();

        datachat();

        fabnewchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DaftarContact.class)
                        .putExtra("level", intent.getStringExtra("level")));
                finish();
            }
        });

        databaseReference.child("user_posyandu")
                .child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            navigationView.getMenu().getItem(1).setVisible(true);
                        } else {
                            navigationView.getMenu().getItem(1).setVisible(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

    }

    private void datachat() {
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference.child(owner)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listchat.clear();
                        if (dataSnapshot.exists()) {
                            rl_belumChatSiapapun.setVisibility(View.GONE);
                            for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                listchat.add(data.getKey());
                            }
                            adapter = new AdapterListChat(context, owner, listchat, getIntent().getStringExtra("level"));
                            recyclerView.setAdapter(adapter);
                        } else {
                            rl_belumChatSiapapun.setVisibility(View.VISIBLE);
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
        if(getIntent().getStringExtra("level").equalsIgnoreCase("admin")){
            tv_emailPengguna.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());
        } else {
            tv_emailPengguna.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhoneNumber());
        }


        if (firebaseAuth.getCurrentUser() != null) {
            if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
                databaseReference.child("user")
                        .child("admin")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    InformasiPosyandu.ID_POSYANDU = dataSnapshot.child("id_posyandu").getValue(String.class);
                                    tv_namaPengguna.setText(dataSnapshot.child("namaLengkap").getValue(String.class));
                                    tv_tipePengguna.setText(getString(R.string.tipe_user_ibu));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                            }
                        });
            } else {
                databaseReference.child("user")
                        .child("ibu")
                        .child(firebaseAuth.getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    InformasiPosyandu.ID_POSYANDU = dataSnapshot.child("id_posyandu").getValue(String.class);
                                    tv_namaPengguna.setText(dataSnapshot.child("namaLengkap").getValue(String.class));
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

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(context, MainActivityIbuActivity.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
                    startActivity(new Intent(context, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(context, MainActivityIbuActivity.class));
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
            if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
                startActivity(new Intent(context, TentangAplikasiActivity.class));
                finish();
            } else {
                startActivity(new Intent(context, TentangAplikasiIbuActivity.class));
                finish();
            }
        } else if (id == R.id.action_edit) {
            if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
                startActivity(new Intent(context, EditProfilActivity.class));
                finish();
            } else {
                startActivity(new Intent(context, IbuEditProfilActivity.class));
                finish();
            }
        } else if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(context, SplashScreenActivity.class));
            finish();
        } else if (id == R.id.action_chat) {
            if (getIntent().getStringExtra("level").equalsIgnoreCase("admin")) {
                startActivity(new Intent(context, ListUser.class).putExtra("level", "admin"));
            } else {
                startActivity(new Intent(context, ListUser.class).putExtra("level", "ibu"));
            }
        } else if (id == R.id.action_dashboard) {
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
        } else if(id == R.id.action_kodePosyandu) {
            startActivity(new Intent(context, AdminKodePosyanduActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
