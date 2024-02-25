package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.BanAnAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.databinding.ActivityBanAnBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BanAnActivity extends AppCompatActivity {
    ActivityBanAnBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    Integer maKhuVuc;
    String tenKhucVuc;
    RecyclerView recyclerView;
    ArrayList<BanAnClass> listBan;
    BanAnAdapter banAnAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBanAnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBarBan;
        toolbar = binding.toolbar;
        maKhuVuc = getIntent().getIntExtra("maKhuVuc", 0);
        tenKhucVuc = getIntent().getStringExtra("tenKhuVuc");
        setSupportActionBar(toolbar);
        toolbar.setTitle(tenKhucVuc);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcHienThiBanTheoKhuVuc(maKhuVuc);
    }

    private void funcHienThiBanTheoKhuVuc(Integer maKhuVuc) {
        recyclerView = binding.rvBan;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listBan = new ArrayList<>();
        banAnAdapter = new BanAnAdapter(this, listBan);
        recyclerView.setAdapter(banAnAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("kv_Ma").equalTo(maKhuVuc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listBan != null){
                    listBan.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    listBan.add(banAnClass);
                }
                banAnAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                binding.tvBanTrong.setVisibility(listBan.isEmpty() ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BanAnActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_them, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuThem){
            funcTaoDialogThem(maKhuVuc);
        }
        return super.onOptionsItemSelected(item);
    }

    private void funcTaoDialogThem(Integer maKhuVuc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_1_edit_text, null);
        TextView textView = view.findViewById(R.id.tvTitle);
        EditText editText = view.findViewById(R.id.edEditText);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        textView.setText("Thêm bàn");
        editText.setHint("Nhập tên bàn");
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenBan = editText.getText().toString();
                if(TextUtils.isEmpty(tenBan)){
                    editText.setError("Vui lòng nhập tên bàn");
                }else {
                    funcThemBan(tenBan, maKhuVuc, editText, alertDialog);
                }
            }
        });
        alertDialog.show();
    }

    private void funcThemBan(String tenBan, Integer maKhuVuc, EditText editText, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ten").equalTo(tenBan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editText.setError("Bàn đã tồn tại");
                }else {
                    databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer maBan;
                            if(snapshot.hasChildren()){
                                DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                                maBan = Integer.valueOf(dataSnapshot.getKey()) + 1;
                            }else {
                                maBan = 1;
                            }
                            BanAnClass banAnClass = new BanAnClass(maBan, maKhuVuc, tenBan);
                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child(String.valueOf(maBan)).setValue(banAnClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(BanAnActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();

                                    }else {
                                        try {
                                            throw task.getException();
                                        }catch (Exception e){
                                            Toast.makeText(BanAnActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    alertDialog.dismiss();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}