package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.net.ParseException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.quanlyquanan0609.Class.DonGiaClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.Class.NgayClass;
import com.example.quanlyquanan0609.databinding.ActivityThemMonAnBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThemMonAnActivity extends AppCompatActivity {
    ActivityThemMonAnBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    String imgUrl, monTen, monGia, monLoai, monHinh, giaHienTai, tenHienTai;
    Uri imgUri;
    Integer maSuaMon, ivClick = 0;
    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemMonAnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        maSuaMon = getIntent().getIntExtra("maSuaMon", 0);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcHienThiSpinner();
        binding.spinLoaiMon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                monLoai = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.edTMAGia.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        binding.edTMAGia.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !s.toString().equals(current)) {
                    binding.edTMAGia.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,.]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getInstance().format(parsed);

                        current = formatted;
                        binding.edTMAGia.setText(formatted);
                        binding.edTMAGia.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        // Xử lý ngoại lệ nếu văn bản không thể chuyển đổi thành số
                    }

                    binding.edTMAGia.addTextChangedListener(this);
                } else {
                    current = "";
                }
            }
        });
        binding.ivMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                funcChonHinh();
            }
        });
        if(maSuaMon!=0){
            toolbar.setTitle("Sửa món ăn");
            funcHienThiMon(maSuaMon);
        }

    }
    // Hien thi mon theo ma
    private void funcHienThiMon(Integer maSuaMon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(String.valueOf(maSuaMon)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MonAnClass monAnClass = snapshot.getValue(MonAnClass.class);
                if(monAnClass != null){
                    binding.edTMATenMon.setText(monAnClass.getMon_Ten());
                    binding.spinLoaiMon.setSelection(getIndex(binding.spinLoaiMon, monAnClass.getMon_Loai()));
                    monHinh = monAnClass.getMon_HinhAnh();
                    tenHienTai = monAnClass.getMon_Ten();
                    if(!monAnClass.getMon_HinhAnh().equals("Đường dẫn") && !isDestroyed() && !isFinishing()){
                        Glide.with(ThemMonAnActivity.this).load(monAnClass.getMon_HinhAnh()).apply(new RequestOptions().centerCrop()).signature((new ObjectKey(monAnClass.getMon_HinhAnh()))).into(binding.ivMonAn);
                    }

                }else {
                    Log.e("SuaMonActivity", "Lỗi tải dữ liệu");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference dataDonGia = FirebaseDatabase.getInstance().getReference("DonGia");
        dataDonGia.orderByChild("mon_Ma").equalTo(maSuaMon).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gia = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DonGiaClass donGiaClass = dataSnapshot.getValue(DonGiaClass.class);
                    gia = donGiaClass.getDg_Gia();
                    giaHienTai = donGiaClass.getDg_Gia();
                }
                binding.edTMAGia.setText(gia);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SuaMonActivity", "Lỗi tải dữ liệu");
            }
        });
    }
    // Them mon an
    private void funcThemMonAn(String monTen, String monGia, String monLoai) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.orderByChild("mon_Ten").equalTo(monTen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.edTMATenMon.setError("Món đã tồn tại");
                }else {
                    databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer maMon;
                            String monHinh, ngay;
                            ngay = funcLayNgayGio();
                            if(snapshot.hasChildren()){
                                DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                                maMon = Integer.valueOf(dataSnapshot.getKey()) + 1;
                            }else {
                                maMon = 1;
                            }
                            monHinh = maMon + "_" + monTen;
                            funcLuuHinhAnh(maMon, monHinh);
                            MonAnClass monAnClass = new MonAnClass(maMon, monTen, monLoai, "Đường dẫn");
                            progressBar.setVisibility(View.VISIBLE);
                            databaseReference.child(String.valueOf(maMon)).setValue(monAnClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        funcLuuHinhAnh(maMon, monHinh);
                                        Toast.makeText(ThemMonAnActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                        funcLuuDonGia(maMon, ngay, funcLaySoTuChuoi(monGia));
                                        onBackPressed();
                                    }else {
                                        try {
                                            task.getException();
                                        }catch (Exception e){
                                            Toast.makeText(ThemMonAnActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
    // Cap nhat mon an
    private void funcCapNhatMonAn(Integer maSuaMon, String monTen, String monGia, String monLoai) {
        String ngay = funcLayNgayGio();
        if(!funcLaySoTuChuoi(monGia).equals(giaHienTai)){
            funcKiemTraDonGia(maSuaMon, ngay, funcLaySoTuChuoi(monGia));
        }
        DatabaseReference dataMonAn = FirebaseDatabase.getInstance().getReference("MonAn");
        MonAnClass monAnClass = new MonAnClass(maSuaMon, monTen, monLoai, monHinh);
        String tenHinh = maSuaMon+"_"+monTen;
        if(monTen.equals(tenHienTai)){
            progressBar.setVisibility(View.VISIBLE);
            dataMonAn.child(String.valueOf(maSuaMon)).setValue(monAnClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        if(ivClick!=0){
                            funcXoaHinhCu(monHinh);
                            funcLuuHinhAnh(maSuaMon, tenHinh);
                        }
                        Toast.makeText(ThemMonAnActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ThemMonAnActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else {
            progressBar.setVisibility(View.VISIBLE);
            dataMonAn.orderByChild("mon_Ten").equalTo(monTen).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        binding.edTMATenMon.setError("Món ăn đã tồn tại");
                    }else {
                        dataMonAn.child(String.valueOf(maSuaMon)).setValue(monAnClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    if(ivClick!=0){
                                        funcXoaHinhCu(monHinh);
                                        funcLuuHinhAnh(maSuaMon, tenHinh);
                                    }
                                    Toast.makeText(ThemMonAnActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }else {
                                    try {
                                        throw task.getException();
                                    }catch (Exception e){
                                        Toast.makeText(ThemMonAnActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ThemMonAnActivity.this, "Lỗi !!!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    private void funcXoaHinhCu(String monHinh) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(monHinh);
        storageReference.delete();
    }

    private void funcKiemTraDonGia(Integer maMon, String ngay, String monGia) {
        DatabaseReference dataDonGia = FirebaseDatabase.getInstance().getReference("DonGia");
        dataDonGia.orderByChild("mon_Ma").equalTo(maMon).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean tonTai = false;
                String key = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    key = dataSnapshot.getKey();
                    DonGiaClass donGiaClass = dataSnapshot.getValue(DonGiaClass.class);
                    if(donGiaClass.getNgay().equals(ngay)){
                        tonTai = true;
                        break;
                    }
                }
                if(tonTai==true){
                    funcCapNhatDonGia(key, maMon, ngay, monGia);
                }else {
                    funcLuuDonGia(maMon, ngay, monGia);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // Cap nhat don gia
    private void funcCapNhatDonGia(String key, Integer maMon, String ngay, String monGia) {
        DatabaseReference dataDonGia = FirebaseDatabase.getInstance().getReference("DonGia");
        dataDonGia.child(key).child("dg_Gia").setValue(monGia);
    }

    // Luu don gia
    private void funcLuuDonGia(Integer maMon, String ngay, String monGia){
        DatabaseReference dataNgay = FirebaseDatabase.getInstance().getReference("Ngay");
        DatabaseReference dataDonGia = FirebaseDatabase.getInstance().getReference("DonGia");
        long ngayThangTimestamp = System.currentTimeMillis();
        NgayClass ngayClass = new NgayClass(ngay);
        dataNgay.orderByChild("ngay").equalTo(ngay).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    dataNgay.child(String.valueOf(ngayThangTimestamp)).setValue(ngayClass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DonGiaClass donGiaClass = new DonGiaClass(ngay, maMon, monGia);
        dataDonGia.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer key;
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    key = Integer.valueOf(dataSnapshot.getKey()) + 1;
                }else {
                    key = 1;
                }
                dataDonGia.child(String.valueOf(key)).setValue(donGiaClass);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // Lay item spinner
    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }
    // Chon hinh anh
    private void funcChonHinh() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
            binding.ivMonAn.setImageURI(imgUri);
            if(maSuaMon!=0){
                ivClick = 1;
            }
        }
    }
    // Hien thi item spinner
    private void funcHienThiSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Đồ ăn");
        items.add("Đồ uống");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinLoaiMon.setAdapter(adapter);
    }
    // Tao menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuCheck){
            monTen = binding.edTMATenMon.getText().toString();
            monGia = binding.edTMAGia.getText().toString();
            if(TextUtils.isEmpty(monTen)){
                binding.edTMATenMon.setError("Vui lòng nhập tên món");
            } else if (TextUtils.isEmpty(monGia)) {
                binding.edTMAGia.setError("Vui lòng nhập giá");
            }else {
                if(maSuaMon!=0){
                    funcCapNhatMonAn(maSuaMon, monTen, monGia, monLoai);
                }else {
                    funcThemMonAn(monTen, monGia, monLoai);

                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Luu hinh anh
    private void funcLuuHinhAnh(Integer monMa, String monHinh) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images/" + monHinh);
        if(imgUri!=null){
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
                            databaseReference.child(String.valueOf(monMa)).child("mon_HinhAnh").setValue(imgUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ThemMonAnActivity.this, "Lỗi khi lưu hình ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // Lay ngay gio
    public String funcLayNgayGio() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //dd/MM/yyyy HH:mm:ss
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }
    // Lay chuoi tu so
    public static String funcLaySoTuChuoi(String input) {
        String numberStr = input.replaceAll("[^0-9]", "");
        return numberStr;
    }
}