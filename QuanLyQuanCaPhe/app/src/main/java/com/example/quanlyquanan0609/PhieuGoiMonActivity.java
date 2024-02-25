package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.PgmAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.databinding.ActivityPhieuGoiMonBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class PhieuGoiMonActivity extends AppCompatActivity {
    ActivityPhieuGoiMonBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    Integer maBan;
    Integer maPgm = null, nguon;

    String tenBan;
    ArrayList<ChiTietPGMClass> listCTPgm;
    PgmAdapter pgmAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhieuGoiMonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        maBan = getIntent().getIntExtra("maBan", 0);
        tenBan = getIntent().getStringExtra("tenBan");
        nguon = getIntent().getIntExtra("nguon", 0);
        toolbar.setTitle(tenBan);
        progressBar = binding.progressBar;
        listCTPgm = new ArrayList<>();
        invalidateOptionsMenu();
    }



    public void funcLayPgmTheoBan(Integer maBan) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        Query query = databaseReference.orderByChild("ban_Ma").equalTo(maBan);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer pgmMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(!pgmClass.getPgm_TrangThai().equals("0")){
                        maPgm = pgmClass.getPgm_Ma();
                        pgmMa = pgmClass.getPgm_Ma();
                    }
                }
                funcHienThiPgm(pgmMa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiPgm(Integer maPgm) {
        recyclerView = binding.rvPGM;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pgmAdapter = new PgmAdapter(this, listCTPgm);
        recyclerView.setAdapter(pgmAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        Query query = databaseReference.orderByChild("pgm_Ma").equalTo(maPgm);
        progressBar.setVisibility(View.VISIBLE);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listCTPgm != null){
                    listCTPgm.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietPGMClass chiTietPGMClass = dataSnapshot.getValue(ChiTietPGMClass.class);
                    listCTPgm.add(chiTietPGMClass);
                }
                pgmAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                binding.tvChoMon.setVisibility(listCTPgm.isEmpty() ? View.VISIBLE : View.GONE);
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcLayPgmTheoBan(maBan);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phieu_goi_mon, menu);
        MenuItem menuChuyen = menu.findItem(R.id.menuPgmChuyen);
        MenuItem menuXoa = menu.findItem(R.id.menuPgmXoa);
        boolean isListEmpty = listCTPgm.isEmpty();
        menuChuyen.setVisible(!isListEmpty);
        menuXoa.setVisible(!isListEmpty);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(nguon == 100){
            menu.findItem(R.id.menuPgmXoa).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuPgmThem){
            Intent intent = new Intent(PhieuGoiMonActivity.this, MonAnActivity.class);
            intent.putExtra("maBan", maBan);
            startActivity(intent);
        }else if (item.getItemId() == R.id.menuPgmXoa){
            funcTaoDialogXoaPgm(maBan, tenBan);
        }else if(item.getItemId() == R.id.menuPgmChuyen){
            Intent intent = new Intent(PhieuGoiMonActivity.this, HoaDonActivity.class);
            intent.putExtra("maPGM", maPgm);
            intent.putExtra("maBan", maBan);
            intent.putExtra("ac", "thanhToan");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void funcTaoDialogXoaPgm(Integer maBan, String tenBan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNd;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNd = view.findViewById(R.id.tvTextView);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        tvTitle.setText("Xóa phiếu gọi món");
        tvNd.setText("Xác nhận xóa phiếu gọi món của " + tenBan);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcXoaPgm(alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcXoaPgm(AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.child(String.valueOf(maPgm)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference dataChiTiet = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
                dataChiTiet.orderByChild("pgm_Ma").equalTo(maPgm).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
                dataHoaDon.orderByChild("pgm_Ma").equalTo(maPgm).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(PhieuGoiMonActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                binding.tvChoMon.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            }
        });
    }

}