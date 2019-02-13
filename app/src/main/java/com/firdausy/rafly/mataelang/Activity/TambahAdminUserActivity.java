package com.firdausy.rafly.mataelang.Activity;

import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TambahAdminUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context = TambahAdminUserActivity.this;
    private TextView tv_namaPengguna;
    private TextView tv_emailPengguna;
    private TextView tv_tipePengguna;

    private RadioGroup rb_tipeAkun;
    private RadioButton rb_admin;
    private RadioButton rb_userIbu;
    private EditText et_namaLengkapAdmin;
    private EditText et_alamatLengkapAdmin;
    private EditText et_noHpAdmin;
    private EditText et_emailAdmin;
    private EditText et_passwordAdmin;
    private EditText et_UlangPasswordAdmin;
    private Button btn_daftar;

    private FirebaseAuth firebaseAuth, firebaseAuth2;
    private DatabaseReference databaseReference, current_db;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_admin_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.mata_elang);
        toolbar.setSubtitle(R.string.tambah_admin_user_ibu);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();

        tv_namaPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_namaPengguna);
        tv_emailPengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_emailPengguna);
        tv_tipePengguna = navigationView.getHeaderView(0).findViewById(R.id.tv_tipePengguna);

        rb_tipeAkun = findViewById(R.id.rb_tipeAkun);
        rb_admin = findViewById(R.id.rb_admin);
        rb_userIbu = findViewById(R.id.rb_userIbu);
        et_namaLengkapAdmin = findViewById(R.id.et_namaLengkapAdmin);
        et_alamatLengkapAdmin = findViewById(R.id.et_alamatLengkapAdmin);
        et_noHpAdmin = findViewById(R.id.et_noHpAdmin);
        et_emailAdmin = findViewById(R.id.et_emailAdmin);
        et_passwordAdmin = findViewById(R.id.et_passwordAdmin);
        et_UlangPasswordAdmin = findViewById(R.id.et_UlangPasswordAdmin);
        btn_daftar = findViewById(R.id.btn_daftar);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prosesDaftar();
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        tv_emailPengguna.setText(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

        if(firebaseAuth.getCurrentUser() != null){
            databaseReference.child("user")
                    .child("admin")
                    .child(firebaseAuth.getCurrentUser().getUid())
                    .child("namaLengkap")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                tv_namaPengguna.setText(dataSnapshot.getValue(String.class));
                                tv_tipePengguna.setText(getString(R.string.tipe_admin));
                            } else {
                                databaseReference.child("user")
                                        .child("ibu")
                                        .child(firebaseAuth.getCurrentUser().getUid())
                                        .child("namaLengkap")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()) {
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
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                        }
                    });
        }
    }

    private void prosesDaftar() {
        if ((TextUtils.isEmpty(et_namaLengkapAdmin.getText().toString())) ||
                (TextUtils.isEmpty(et_alamatLengkapAdmin.getText().toString())) ||
                (TextUtils.isEmpty(et_noHpAdmin.getText().toString())) ||
                (TextUtils.isEmpty(et_emailAdmin.getText().toString())) ||
                (TextUtils.isEmpty(et_passwordAdmin.getText().toString())) ||
                (TextUtils.isEmpty(et_UlangPasswordAdmin.getText().toString()))
        ) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.masih_kosong));
        } else if (!et_passwordAdmin.getText().toString().equals(et_UlangPasswordAdmin.getText().toString())) {
            new Bantuan(context).alertDialogPeringatan(getString(R.string.pass_salah));
            et_UlangPasswordAdmin.setError(getString(R.string.pass_salah));
        } else {
            //TODO : proses daftar
            progressDialog = ProgressDialog.show(context,
                    "Tunggu Beberapa Saat",
                    "Proses Mendaftar ...",
                    true);

            final String jenis = (String) ((RadioButton) findViewById(rb_tipeAkun.getCheckedRadioButtonId())).getText();

            if (jenis.equalsIgnoreCase("admin")) {
                current_db = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("user")
                        .child("admin");
            } else {
                current_db = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("user")
                        .child("ibu");
            }

            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl(getString(R.string.databae_url))
                    .setApiKey(getString(R.string.web_api))
                    .setApplicationId(getString(R.string.project_idku))
                    .build();

            try {
                FirebaseApp firebaseApp = FirebaseApp.initializeApp(getApplicationContext(),
                        firebaseOptions,
                        getString(R.string.app_name));
                firebaseAuth2 = FirebaseAuth.getInstance(firebaseApp);
            } catch (IllegalStateException e) {
                firebaseAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance(getString(R.string.app_name)));
            }

            firebaseAuth2.createUserWithEmailAndPassword(et_emailAdmin.getText().toString(),
                    et_passwordAdmin.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //save into realtime cuk
                                String user_id = Objects.requireNonNull(firebaseAuth2.getCurrentUser()).getUid();
                                Map data = new HashMap();
                                data.put("alamatLengkap", et_alamatLengkapAdmin.getText().toString());
                                data.put("email", et_emailAdmin.getText().toString());
                                data.put("namaLengkap", et_namaLengkapAdmin.getText().toString());
                                data.put("nomerHp", et_noHpAdmin.getText().toString());

                                current_db.child(user_id)
                                        .setValue(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                if(task.isSuccessful()){
                                                    new Bantuan(context)
                                                            .toastLong(getString(R.string.berhasil_membuat_akun)
                                                                    + jenis);
                                                    firebaseAuth2.signOut();
                                                    startActivity(new Intent(context, MainActivity.class));
                                                    finish();
                                                } else {
                                                    new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                                                }
                                            }
                                        });


                            } else {
                                progressDialog.dismiss();
                                new Bantuan(context).alertDialogPeringatan(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });

//            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
//                    .setEmail(et_emailAdmin.getText().toString())
//                    .setEmailVerified(false)
//                    .setPassword(et_passwordAdmin.getText().toString())
//                    .setPhoneNumber(et_noHpAdmin.getText().toString())
//                    .setDisplayName(et_namaLengkapAdmin.getText().toString())
//                    .setDisabled(false);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        } else if (id == R.id.action_lihat) {

        } else if (id == R.id.action_pengaturan) {
            startActivity(new Intent(context, PengaturanActivity.class));
            finish();
        } else if (id == R.id.action_edit) {

        } else if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        } else if (id == R.id.action_about) {
            new Bantuan(context).alertDialogDebugging("About Coming Soon !");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
