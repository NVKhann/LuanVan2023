package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;


import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.Fragment.CaiDatFragment;

import com.example.quanlyquanan0609.Fragment.KhGoiMonFragment;

import com.example.quanlyquanan0609.Fragment.TrangChuFragment;
import com.example.quanlyquanan0609.databinding.ActivityKhachHangBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class KhachHangActivity extends AppCompatActivity {
    ActivityKhachHangBinding binding;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKhachHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        funcDoiFragment(new TrangChuFragment());

        binding.bottomAppBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.menuBotTrangChu){
                    funcDoiFragment(new TrangChuFragment());
                } else if (id == R.id.menuBotGoiMon) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = firebaseUser.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
                    databaseReference.orderByChild("nd_Ma").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean coBan = false;
                            Integer maBan = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                                if(pgmClass.getPgm_TrangThai().equals("1") && pgmClass.getNd_Ma().equals(uId)){
                                    coBan = true;
                                    maBan = pgmClass.getBan_Ma();
                                    break;
                                }
                            }
                            if(coBan){
                                funcLayTenBan(maBan);

                            }else {
                                funcDoiFragment(new KhGoiMonFragment());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    funcDoiFragment(new CaiDatFragment());
                }
                return true;
            }
        });
    }

    private void funcLayTenBan(Integer maBan) {
        DatabaseReference dataBan = FirebaseDatabase.getInstance().getReference("BanAn");
        dataBan.orderByChild("ban_Ma").equalTo(maBan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tenBan = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    tenBan = banAnClass.getBan_Ten();
                }
                Intent intent = new Intent(KhachHangActivity.this, PhieuGoiMonActivity.class);
                intent.putExtra("tenBan", tenBan);
                intent.putExtra("maBan", maBan);
                intent.putExtra("nguon", 100);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcDoiFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();

    }
}