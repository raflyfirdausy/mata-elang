package com.firdausy.rafly.mataelang.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.firdausy.rafly.mataelang.Adapter.TabFragmentAdapter;
import com.firdausy.rafly.mataelang.Fragment.LakiLakiFragment;
import com.firdausy.rafly.mataelang.Fragment.PerempuanFragment;
import com.firdausy.rafly.mataelang.Fragment.TindakanAnakNormalFragment;
import com.firdausy.rafly.mataelang.Fragment.TindakanAnakStuntingFragment;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class PengaturanTindakanActivity extends AppCompatActivity {
    private Context context = PengaturanTindakanActivity.this;
    private ViewPager viewPager;
    private TabFragmentAdapter tabFragmentAdapter;
    private TabLayout tabLayout;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_tindakan);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.pengaturan);
        getSupportActionBar().setSubtitle(R.string.tindakan_untuk_anak);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.vp_konten);
        tabLayout = findViewById(R.id.tab_layout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        tabFragmentAdapter = new TabFragmentAdapter(getSupportFragmentManager());
        tabFragmentAdapter.addFragment(new TindakanAnakNormalFragment(), getString(R.string.normal));
        tabFragmentAdapter.addFragment(new TindakanAnakStuntingFragment(), getString(R.string.stunting));
        viewPager.setAdapter(tabFragmentAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(context, PengaturanActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, PengaturanActivity.class));
        finish();
    }
}
