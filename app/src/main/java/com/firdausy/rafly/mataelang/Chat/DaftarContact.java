package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

public class DaftarContact extends AppCompatActivity {
    Context context;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<String> listchat = new ArrayList<>();
    private AdapterUser adapter;
    private String owner;
    private String id_posyandu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);

        context = DaftarContact.this;
        recyclerView = findViewById(R.id.rv);
        owner = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();

        if (intent.getStringExtra("level").equals("admin")) {
            //jika dia admin maka ambil data di root ibu
            ambilid_posyannduadmin();
            dataUserIbu();
        } else {
            //jika dia admin maka ambil data di root admin
            ambilid_posyannduibu();
            dataUseradmin();
        }
    }

    private void dataUseradmin() {
        databaseReference.child("user")
                .child("admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (Objects.equals(data.child("id_posyandu").getValue(String.class),
                                        InformasiPosyandu.ID_POSYANDU)) {
                                    listchat.add(data.getKey());
                                }
                            }
                        } else {
                            new Bantuan(context).alertDialogPeringatan(getString(R.string.tidak_ada_data_admin));
                        }

                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new AdapterUser(context, owner, listchat);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void dataUserIbu() {
        databaseReference
                .child("user")
                .child("ibu")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (Objects.equals(data.child("id_posyandu").getValue(String.class),
                                        InformasiPosyandu.ID_POSYANDU)) {
                                    listchat.add(data.getKey());
                                }
                            }
                        } else {
                            new Bantuan(context).alertDialogPeringatan(getString(R.string.tidak_ada_data_bu));
                        }
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(context);
                        recyclerView.setLayoutManager(layoutManager);
                        adapter = new AdapterUser(context, owner, listchat);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
    }

    private void ambilid_posyannduadmin() {

        databaseReference.child("user")
                .child("admin")
                .child(owner)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            id_posyandu = dataSnapshot.child("id_posyandu").getValue(String.class);
                        } else {
                            new Bantuan(context).alertDialogPeringatan(getString(R.string.data_tidak_ditemukan));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
    }

    private void ambilid_posyannduibu() {
        databaseReference.child("user")
                .child("ibu")
                .child(owner)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            id_posyandu = dataSnapshot.child("id_posyandu").getValue(String.class);
                        } else {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
    }




}
