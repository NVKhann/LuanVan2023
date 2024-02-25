package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityDangKyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DangKyActivity extends AppCompatActivity {
    ActivityDangKyBinding binding;
    ProgressBar progressBar;
    private static final String TAG = "DangKiActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangKyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        binding.tvDKDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                startActivity(intent);
            }
        });
        binding.btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, matKhau, hoTen, diaChi, sdt;
                email = binding.edDKEmail.getText().toString();
                matKhau = binding.edDKMatKhau.getText().toString();
                hoTen = binding.edDKHoTen.getText().toString();
                diaChi = binding.edDKDiaChi.getText().toString();
                sdt = binding.edDKSdt.getText().toString();
                if(TextUtils.isEmpty(email)){
                    binding.tipDKEmail.setHelperText("Vui lòng nhập email");
                    binding.edDKEmail.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDKEmail.setHelperTextEnabled(false);
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }else if (TextUtils.isEmpty(matKhau)){
                    binding.tipDKMatKhau.setHelperText("Vui lòng nhập mật khẩu");
                    binding.edDKMatKhau.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDKMatKhau.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (TextUtils.isEmpty(hoTen)) {
                    binding.tipDKHoTen.setHelperText("Vui lòng nhập họ và tên");
                    binding.edDKHoTen.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDKHoTen.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (TextUtils.isEmpty(diaChi)) {
                    binding.tipDKDiaChi.setHelperText("Vui lòng nhâp địa chỉ");
                    binding.edDKDiaChi.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDKDiaChi.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (TextUtils.isEmpty(sdt)) {
                    binding.tipDKSdt.setHelperText("Vui lòng nhập số điện thoại");
                    binding.edDKSdt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDKSdt.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }else {
                    funcDangKiTaiKhoan(email, matKhau, hoTen, diaChi, sdt);
                }
            }
        });
    }

    private void funcDangKiTaiKhoan(String email, String matKhau, String hoTen, String diaChi, String sdt) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, matKhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String uID = firebaseUser.getUid();
                    String mail = firebaseUser.getEmail();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
                    NguoiDungClass nguoiDungClass = new NguoiDungClass(uID,hoTen, diaChi, sdt, mail, "Khách hàng", "Đường dẫn", 0);
                    databaseReference.child(uID).setValue(nguoiDungClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(DangKyActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DangKyActivity.this, DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(DangKyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthUserCollisionException e){
                        binding.tipDKEmail.setHelperText("Email đã được sử dụng");
                        binding.edDKEmail.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                binding.tipDKEmail.setHelperTextEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    }catch (FirebaseAuthWeakPasswordException e){
                        binding.tipDKMatKhau.setHelperText("Mật khẩu phải nhiều hơn 6 ký tự");
                        binding.edDKMatKhau.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                binding.tipDKMatKhau.setHelperTextEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        binding.tipDKEmail.setHelperText("Email không đúng định dạng");
                        binding.edDKEmail.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                binding.tipDKEmail.setHelperTextEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(DangKyActivity.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}