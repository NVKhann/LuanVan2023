package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.ListKmAdapter;
import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.databinding.ActivityKhuyenMaiBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class KhuyenMaiActivity extends AppCompatActivity {
    ActivityKhuyenMaiBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    TabLayout tabLayout;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    ListKmAdapter kmAdapter;
    ArrayList<KhuyenMaiClass> list;
    RecyclerView recyclerView;
    String ngayHienTai = funcLayNgay();
    String title = "Áp dụng";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKhuyenMaiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        tabLayout = binding.tlKm;
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
                    title = "Áp dụng";
                }else {
                    title = "Hết hạn";
                }
                funcHienThiDanhSachKm(title);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void funcHienThiDanhSachKm(String title) {
        recyclerView = binding.rvListKhuyenMai;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        kmAdapter = new ListKmAdapter(this, list);
        recyclerView.setAdapter(kmAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuyenMaiClass khuyenMaiClass = dataSnapshot.getValue(KhuyenMaiClass.class);
                    try {
                        Date ngayHt = format.parse(ngayHienTai);
                        Date ngayBd = format.parse(khuyenMaiClass.getKm_NgayBd());
                        Date ngayKt = format.parse(khuyenMaiClass.getKm_NgayKt());
                        if(title.equals("Áp dụng")){
                            if(ngayHt.equals(ngayBd) || ngayHt.equals(ngayKt) || ngayHt.before(ngayBd) || ngayHt.after(ngayBd) && ngayHt.before(ngayKt)){
                                list.add(khuyenMaiClass);
                            }
                        }else if (title.equals("Hết hạn")){
                            if(ngayHt.after(ngayKt)){
                                list.add(khuyenMaiClass);
                            }
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                kmAdapter.notifyDataSetChanged();
                binding.tvKMTrong.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            funcTaoDialogThem();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcHienThiDanhSachKm(title);
    }

    private void funcTaoDialogThem() {
        Calendar calendar = Calendar.getInstance();
        final int nam = calendar.get(Calendar.YEAR);
        final int thang = calendar.get(Calendar.MONTH);
        final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_khuyen_mai, null);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        Button btnHuy, btnOK;
        TextView tvNgayBD, tvNgayKT;
        EditText edTieuDe, edMucGiam, edPhanTram, edNoiDung;
        btnOK = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        edTieuDe = view.findViewById(R.id.edKMTieuDe);
        edMucGiam = view.findViewById(R.id.edKMMuc);
        edPhanTram = view.findViewById(R.id.edKMPhanTram);
        edNoiDung = view.findViewById(R.id.edKMNoiDung);
        tvNgayKT = view.findViewById(R.id.tvNgayKT);
        tvNgayBD = view.findViewById(R.id.tvNgayBD);
        funcTextChanged(edMucGiam, edPhanTram, edNoiDung);
        tvNgayBD.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        tvNgayKT.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        tvNgayBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        KhuyenMaiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngayBD = String.format("%02d/%02d/%d", i2, i1, i);
                        tvNgayBD.setText(ngayBD);
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });
        tvNgayKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        KhuyenMaiActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngayBD = String.format("%02d/%02d/%d", i2, i1, i);
                        tvNgayKT.setText(ngayBD);
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tieuDe, mucGiam, phanTram, ngayBd, ngayKt, noiDung;
                tieuDe = edTieuDe.getText().toString();
                mucGiam = edMucGiam.getText().toString();
                phanTram = edPhanTram.getText().toString();
                noiDung = edNoiDung.getText().toString();
                ngayBd = tvNgayBD.getText().toString();
                ngayKt = tvNgayKT.getText().toString();
                try {
                    Date dNgayBd = format.parse(ngayBd);
                    Date dNgayKt = format.parse(ngayKt);
                    if(dNgayKt.before(dNgayBd)){
                        Toast.makeText(KhuyenMaiActivity.this, "Ngày kết thúc không hợp lệ", Toast.LENGTH_SHORT).show();
                    }else if (TextUtils.isEmpty(tieuDe)){
                        edTieuDe.setError("Vui lòng nhập tiêu đề");
                    } else if (TextUtils.isEmpty(mucGiam)) {
                        edMucGiam.setError("Vui lòng nhập mức áp dụng");
                    } else if (TextUtils.isEmpty(phanTram)) {
                        edPhanTram.setError("Vui lòng nhập phần trăm giảm giá");
                    } else {
                        funcThemKhuyenMai(tieuDe, mucGiam, phanTram, ngayBd, ngayKt, noiDung, alertDialog);
                    }
                }catch (ParseException e){
                    Log.e("NgayKt", e.toString());
                }
            }
        });
        alertDialog.show();
    }

    private void funcThemKhuyenMai(String tieuDe, String mucGiam, String phanTram, String ngayBd, String ngayKt, String noiDung, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kmMa;
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    kmMa = Integer.valueOf(dataSnapshot.getKey()) + 1;
                }else {
                    kmMa = 1;
                }
                KhuyenMaiClass khuyenMaiClass = new KhuyenMaiClass(kmMa, Integer.valueOf(funcLaySoTuChuoi(mucGiam)), Integer.valueOf(phanTram), tieuDe, ngayBd, ngayKt, noiDung);
                progressBar.setVisibility(View.VISIBLE);
                databaseReference.child(String.valueOf(kmMa)).setValue(khuyenMaiClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            alertDialog.dismiss();
                            Toast.makeText(KhuyenMaiActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                throw task.getException();
                            }catch (Exception e){
                                Toast.makeText(KhuyenMaiActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void funcTextChanged(EditText editText, EditText edPhanTram, EditText edNoiDung) {
        edNoiDung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nd = edNoiDung.getText().toString();
                if(nd.isEmpty()){
                    edNoiDung.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }else {
                    edNoiDung.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edPhanTram.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = edPhanTram.getText().toString();
                if(!str.isEmpty()){
                    Integer phanTram = Integer.valueOf(str);
                    if(phanTram > 100){
                        edPhanTram.setText("100");
                    }
                }
            }
        });
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        editText.addTextChangedListener(new TextWatcher() {
            private  String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,.]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getInstance().format(parsed);

                        current = formatted;
                        editText.setText(formatted);
                        editText.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        // Xử lý ngoại lệ nếu văn bản không thể chuyển đổi thành số
                    }

                    editText.addTextChangedListener(this);
                } else {
                    current = "";
                }
            }
        });

    }
    public String funcLaySoTuChuoi(String input) {
        String numberStr = input.replaceAll("[^0-9]", "");
        return numberStr;
    }
    public String funcLayNgay() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }
}