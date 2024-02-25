package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityThemTaiKhoanBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ThemTaiKhoanActivity extends AppCompatActivity {
    ActivityThemTaiKhoanBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    String spinQuyen = "Nhân viên";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThemTaiKhoanBinding.inflate(getLayoutInflater());
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
        funcLayQuyenHienTai();
        binding.spinThemChucVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinQuyen = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void funcLayQuyenHienTai() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uIdHienTai = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(uIdHienTai).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String quyenHienTai = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    quyenHienTai = nguoiDungClass.getNd_Quyen();
                }
                if(quyenHienTai != null){
                    funcHienThiSpinner(quyenHienTai);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcThemTaiKhoan(String email, String matKhau, String hoTen, String diaChi, String sdt, String spinQuyen) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, matKhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String uId = firebaseUser.getUid();
                    String mail = firebaseUser.getEmail();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
                    NguoiDungClass nguoiDungClass = new NguoiDungClass(uId, hoTen, diaChi, sdt, mail, spinQuyen, "Đường dẫn", 0);
                    databaseReference.child(uId).setValue(nguoiDungClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ThemTaiKhoanActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ThemTaiKhoanActivity.this, NguoiDungActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(ThemTaiKhoanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthUserCollisionException e){
                        binding.edThemEmail.setError("Email đã được sử dụng");
                    }catch (FirebaseAuthWeakPasswordException e){
                        binding.edThemMatKhau.setError("Mật khẩu phải hơn 6 ký tự");
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        binding.edThemEmail.setError("Email không đúng định dạng");
                    }catch (Exception e){
                        Toast.makeText(ThemTaiKhoanActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }

    private void funcHienThiSpinner(String quyenHienTai) {
        List<String> items = new ArrayList<>();
        if(quyenHienTai.equals("Chủ quán")){
            items.add("Chủ quán");
            items.add("Quản lý");
            items.add("Nhân viên");
        }else {
            items.add("Quản lý");
            items.add("Nhân viên");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinThemChucVu.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuCheck){
            String email = binding.edThemEmail.getText().toString();
            String matKhau = binding.edThemMatKhau.getText().toString();
            String hoTen = binding.edThemHoTen.getText().toString();
            String diaChi = binding.edThemDiaChi.getText().toString();
            String sdt = binding.edThemSdt.getText().toString();
            if(TextUtils.isEmpty(email)){
                binding.edThemEmail.setError("Vui lòng nhập email");
            }else if(TextUtils.isEmpty(matKhau)){
                binding.edThemEmail.setError("Vui lòng nhập mật khẩu");
            }else if (TextUtils.isEmpty(hoTen)){
                binding.edThemHoTen.setError("Vui lòng nhập họ tên");
            }else if(TextUtils.isEmpty(diaChi)){
                binding.edThemDiaChi.setError("Vui lòng nhập địa chỉ");
            }else if (TextUtils.isEmpty(sdt)){
                binding.edThemSdt.setError("Vui lòng nhập số điện thoại");
            }else {
                funcThemTaiKhoan(email, matKhau, hoTen, diaChi, sdt, spinQuyen);
            }
        }
        return super.onOptionsItemSelected(item);
    }



}