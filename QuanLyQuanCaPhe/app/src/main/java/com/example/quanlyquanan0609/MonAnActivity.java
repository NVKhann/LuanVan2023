package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.MonAnAdapter;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityMonAnBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MonAnActivity extends AppCompatActivity {
    ActivityMonAnBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    TabLayout tabLayout;
    RecyclerView recyclerView;
    MonAnAdapter monAnAdapter;
    ArrayList<MonAnClass> listMonAn;
    String title = "Đồ uống";
    Integer maBan;
    boolean dong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMonAnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        maBan = getIntent().getIntExtra("maBan", 0);
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        tabLayout = binding.tlMonAn;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIndex = tab.getPosition(); // Lấy vị trí của TabItem được chọn
                if(tabIndex == 0){
                    title = "Đồ uống";
                }else {
                    title = "Đồ ăn";
                }
                funcHienThiMon(title);
                binding.tvKetQua.setVisibility(View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                funcTimKiem(newText);
                return true;
            }
        });
        funcHienThiSoLuong();
    }

    private void funcHienThiSoLuong() {
        TabLayout.Tab tabDU = tabLayout.getTabAt(0);
        TabLayout.Tab tabDA = tabLayout.getTabAt(1);
        recyclerView = binding.rvListMonAn;
        recyclerView.setHasFixedSize(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countDA = 0;
                int countDU = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MonAnClass monAnClass = dataSnapshot.getValue(MonAnClass.class);
                    if(monAnClass.getMon_Loai().equals("Đồ ăn")){
                        countDA = countDA + 1;
                    }else {
                        countDU = countDU + 1;
                    }
                }
                tabDA.setText("Đồ ăn (" + countDA + ")");
                tabDU.setText("Đồ uống (" + countDU + ")");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTimKiem(String newText) {
        String searchText = newText.toLowerCase();
        if (newText.isEmpty()) {
           funcHienThiMon(title);
        }
        recyclerView = binding.rvListMonAn;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listMonAn = new ArrayList<>();
        monAnAdapter = new MonAnAdapter(this, listMonAn, maBan);
        recyclerView.setAdapter(monAnAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listMonAn != null){
                    listMonAn.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MonAnClass monAnClass = dataSnapshot.getValue(MonAnClass.class);
                    String ten = monAnClass.getMon_Ten().toLowerCase();
                    if(ten.contains(searchText)){
                        listMonAn.add(monAnClass);
                    }
                }
                Collections.sort(listMonAn, new Comparator<MonAnClass>() {
                    @Override
                    public int compare(MonAnClass monAnClass, MonAnClass t1) {
                        return monAnClass.getMon_Ten().compareTo(t1.getMon_Ten());
                    }
                });
                monAnAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if (listMonAn.isEmpty()) {
                    binding.tvKetQua.setVisibility(View.VISIBLE);
                }else {
                    binding.tvKetQua.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcHienThiMon(String loaiTen) {
        recyclerView = binding.rvListMonAn;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listMonAn = new ArrayList<>();
        monAnAdapter = new MonAnAdapter(this, listMonAn, maBan);
        recyclerView.setAdapter(monAnAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.orderByChild("mon_Loai").equalTo(loaiTen).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listMonAn != null){
                    listMonAn.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MonAnClass monAnClass = dataSnapshot.getValue(MonAnClass.class);
                    listMonAn.add(monAnClass);
                }
                Collections.sort(listMonAn, new Comparator<MonAnClass>() {
                    @Override
                    public int compare(MonAnClass monAnClass, MonAnClass t1) {
                        return monAnClass.getMon_Ten().compareTo(t1.getMon_Ten());
                    }
                });
                monAnAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcHienThiMon(title);
        funcHienThiSoLuong();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mon_an, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        Intent intent = getIntent();
        if(intent != null && maBan != 0){
            menu.findItem(R.id.menuThem).setVisible(false);
            menu.findItem(R.id.menuTimKiem).setVisible(true);
        }else {
            menu.findItem(R.id.menuThem).setVisible(true);
            menu.findItem(R.id.menuTimKiem).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuThem){
            Intent intent = new Intent(MonAnActivity.this, ThemMonAnActivity.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.menuTimKiem){
            if(dong == false){
                binding.searchView.setVisibility(View.VISIBLE);
                dong = true;
            }else if(dong == true){
                binding.searchView.setVisibility(View.GONE);
                funcHienThiMon(title);
                binding.tvKetQua.setVisibility(View.GONE);
                dong = false;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}