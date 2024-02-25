package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.CaLamViecAdapter;
import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.databinding.ActivityHienThiCaLamViecBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class HienThiCaLamViecActivity extends AppCompatActivity {
    ActivityHienThiCaLamViecBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    CaLamViecAdapter caLamViecAdapter;
    RecyclerView recyclerView;
    ArrayList<CaLamViecClass> list;
    int hour, minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHienThiCaLamViecBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcHienThiCa();
    }
    private void funcHienThiCa() {
        recyclerView = binding.rvCaLamViec;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        caLamViecAdapter = new CaLamViecAdapter(this, list);
        recyclerView.setAdapter(caLamViecAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CaLamViecClass caLamViecClass = snapshot.getValue(CaLamViecClass.class);
                boolean isItemExists = false;
                for (CaLamViecClass existingItem : list) {
                    if (existingItem.getCa_Ma().equals(caLamViecClass.getCa_Ma())) {
                        isItemExists = true;
                        break;
                    }
                }

                if (!isItemExists) {
                    list.add(caLamViecClass);
                    caLamViecAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CaLamViecClass caLamViecClass = snapshot.getValue(CaLamViecClass.class);
                for (int i = 0; i < list.size(); i++) {
                    CaLamViecClass currentCa = list.get(i);
                    if (currentCa.getCa_Ma().equals(caLamViecClass.getCa_Ma())) {
                        list.set(i, caLamViecClass);
                        caLamViecAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                CaLamViecClass removedCa = snapshot.getValue(CaLamViecClass.class);
                for (int i = 0; i < list.size(); i++) {
                    CaLamViecClass currentCa = list.get(i);
                    if (currentCa.getCa_Ma().equals(removedCa.getCa_Ma())) {
                        list.remove(i);
                        caLamViecAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HienThiCaLamViecActivity.this, "Lỗi tải dữ liệu !!!", Toast.LENGTH_SHORT).show();
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
            funcTaoDiaLogThemCa();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcHienThiCa();
    }

    private void funcTaoDiaLogThemCa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_them_ca, null);
        builder.setView(view).setCancelable(false);
        EditText edTenCa, edSoLuong;
        TextView tvGioBD, tvGioKT;
        Button btnOk, btnHuy;
        edTenCa = view.findViewById(R.id.edCaTen);
        edSoLuong = view.findViewById(R.id.edCaSoLuong);
        tvGioBD = view.findViewById(R.id.tvCaGioBD);
        tvGioKT = view.findViewById(R.id.tvCaGioKT);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
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
                String caTen, caGioBD, caGioKT, caSoLuong;
                caTen = edTenCa.getText().toString();
                caGioBD = tvGioBD.getText().toString();
                caGioKT = tvGioKT.getText().toString();
                caSoLuong = edSoLuong.getText().toString();
                if(TextUtils.isEmpty(caTen)){
                    edTenCa.setError("Vui lòng nhập tên ca");
                }else if(TextUtils.isEmpty(caSoLuong)) {
                    edSoLuong.setError("Vui lòng nhập số lượng nhân viên");
                } else{
                    funcThemCaLamViec(caTen, caGioBD, caGioKT, caSoLuong, edTenCa, alertDialog);
                }
            }
        });

        tvGioBD.setText(funcLayGioHienTai());
        tvGioKT.setText(funcLayGioHienTai());
        tvGioBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogTime(tvGioBD);
            }
        });
        tvGioKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogTime(tvGioKT);
            }
        });
        alertDialog.show();
    }

    private void funcTaoDialogTime(TextView textView) {

        String currentTime = textView.getText().toString();

        String[] time = currentTime.split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);

        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                hour = selectedHour;
                minute = selectedMinute;
                String time = String.format("%02d:%02d", hour, minute);
                textView.setText(time);
            }

        }, hour, minute, true);
        timePicker.show();
    }
    private void funcThemCaLamViec(String caTen, String caGioBD, String caGioKT, String caSoLuong,  EditText edCaTen, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ten").equalTo(caTen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    edCaTen.setError("Tên đã tồn tại");
                }else {
                    databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer caMa;
                            if(snapshot.hasChildren()){
                                DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                                caMa = Integer.valueOf(dataSnapshot.getKey()) + 1;
                            }else {
                                caMa = 1;
                            }
                            CaLamViecClass caLamViecClass = new CaLamViecClass(caMa, Integer.parseInt(caSoLuong), caTen, caGioBD, caGioKT);
                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child(String.valueOf(caMa)).setValue(caLamViecClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(HienThiCaLamViecActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }else {
                                        try {
                                            throw task.getException();
                                        }catch (Exception e){
                                            Toast.makeText(HienThiCaLamViecActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String funcLayGioHienTai() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return String.format("%02d:%02d", hour, minute);
    }

}