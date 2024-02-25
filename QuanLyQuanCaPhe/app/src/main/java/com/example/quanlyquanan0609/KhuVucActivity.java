package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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

import com.example.quanlyquanan0609.Adapter.KhuVucAdapter;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.databinding.ActivityKhuVucBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KhuVucActivity extends AppCompatActivity {
    ActivityKhuVucBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    ArrayList<KhuVucClass> lstKhuVuc;
    KhuVucAdapter khuVucAdapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKhuVucBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        progressBar = binding.progressBarKv;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcHienThiKhuVuc();
    }

    private void funcHienThiKhuVuc() {
        recyclerView = binding.rvKhuVuc;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lstKhuVuc = new ArrayList<>();
        khuVucAdapter = new KhuVucAdapter(this, lstKhuVuc);
        recyclerView.setAdapter(khuVucAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                KhuVucClass khuVucClass = snapshot.getValue(KhuVucClass.class);
                boolean isItemExists = false;
                for (KhuVucClass existingItem : lstKhuVuc) {
                    if (existingItem.getKv_Ma().equals(khuVucClass.getKv_Ma())) {
                        isItemExists = true;
                        break;
                    }
                }

                if (!isItemExists) {
                    lstKhuVuc.add(khuVucClass);
                    khuVucAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                KhuVucClass updatedKhuVuc = snapshot.getValue(KhuVucClass.class);
                for (int i = 0; i < lstKhuVuc.size(); i++) {
                    KhuVucClass currentKhuVuc = lstKhuVuc.get(i);
                    if (currentKhuVuc.getKv_Ma().equals(updatedKhuVuc.getKv_Ma())) {
                        lstKhuVuc.set(i, updatedKhuVuc);
                        khuVucAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                KhuVucClass removedKhuVuc = snapshot.getValue(KhuVucClass.class);
                for (int i = 0; i < lstKhuVuc.size(); i++) {
                    KhuVucClass currentKhuVuc = lstKhuVuc.get(i);
                    if (currentKhuVuc.getKv_Ma().equals(removedKhuVuc.getKv_Ma())) {
                        lstKhuVuc.remove(i);
                        khuVucAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(KhuVucActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        funcHienThiKhuVuc();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_them, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuThem){
            funcDialogThem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void funcDialogThem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_1_edit_text, null);
        EditText edText = view.findViewById(R.id.edEditText);
        TextView textView = view.findViewById(R.id.tvTitle);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        edText.setHint("Nhập tên khu vực");
        textView.setText("Thêm khu vực");
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenKv = edText.getText().toString();
                if(TextUtils.isEmpty(tenKv)){
                    edText.setError("Vui lòng nhập tên khu vực");
                }else {
                    funcThemKhuVuc(tenKv, edText, alertDialog);
                }
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void funcThemKhuVuc(String tenKV, EditText editText, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.orderByChild("kv_Ten").equalTo(tenKV).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    editText.setError("Khu vực đã tồn tại");
                }else {
                    databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer maKhuVuc;
                            if(snapshot.hasChildren()){
                                DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                                maKhuVuc = Integer.valueOf(dataSnapshot.getKey()) +  1;
                            }else {
                                maKhuVuc = 1;
                            }
                            KhuVucClass khuVucClass = new KhuVucClass(maKhuVuc, tenKV, "Hiển thị");
                            databaseReference.child(String.valueOf(maKhuVuc)).setValue(khuVucClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(KhuVucActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }else {
                                        try {
                                            task.getException();
                                        }catch (Exception e){
                                            Toast.makeText(KhuVucActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}