package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityDangNhapBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DangNhapActivity extends AppCompatActivity {
    ActivityDangNhapBinding binding;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDangNhapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        binding.tvDNDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DangNhapActivity.this, DangKyActivity.class);
                startActivity(intent);
            }
        });
        binding.btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, matKhau;
                email = binding.edDNEmail.getText().toString();
                matKhau = binding.edDNMatKhau.getText().toString();
                if(TextUtils.isEmpty(email)){
                    binding.tipDNEmail.setHelperText("Vui lòng nhập email");
                    binding.edDNEmail.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDNEmail.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }else if(TextUtils.isEmpty(matKhau)){
                    binding.tipDNMatKhau.setHelperText("Vui lòng nhập mật khẩu");
                    binding.edDNMatKhau.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            binding.tipDNMatKhau.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else {
                    funcDangNhap(email, matKhau);
                }
            }
        });

        binding.tvDNQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogQuenMatKhau();
            }
        });
    }

    private void funcTaoDialogQuenMatKhau() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_1_edit_text, null);
        EditText edDialog = view.findViewById(R.id.edEditText);
        MaterialButton btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
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
                String email = edDialog.getText().toString();
                funcQuenMatKhau(email, edDialog, alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcQuenMatKhau(String strEmail, EditText editText, AlertDialog alertDialog) {
        progressBar.setVisibility(View.VISIBLE);
        if(strEmail.trim().isEmpty()){
            editText.setError("Vui lòng nhập email");
            progressBar.setVisibility(View.GONE);
        }else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(DangNhapActivity.this);
                        builder.setTitle("Kiểm tra email").setMessage("Vui lòng kiển tra email của bạn để cập nhật lại mật khẩu!");
                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setCancelable(false).show();
                        alertDialog.dismiss();
                    }else {
                        try {
                            throw task.getException();
                        }catch (FirebaseAuthInvalidUserException e){
                            editText.setError("Ngươi dùng không tồn tại");
                            progressBar.setVisibility(View.GONE);
                        }catch (Exception e){
                            Toast.makeText(DangNhapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
    private void funcDangNhap(String strEmail, String strMatKhau) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(strEmail, strMatKhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String uId = firebaseUser.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
                    databaseReference.orderByKey().equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                                if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                                    Intent intent = new Intent(DangNhapActivity.this, KhachHangActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else if(nguoiDungClass.getNd_Quyen().equals("Nhân viên")) {
                                    Intent intent = new Intent(DangNhapActivity.this, MainNhanVienActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                        Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        binding.tipDNEmail.setHelperText("Người dùng không tồn tại");
                        binding.edDNEmail.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                binding.tipDNEmail.setHelperTextEnabled(false);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(DangNhapActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    } catch (Exception e){
                        Log.i("DangNhap", e.getMessage());
                        Toast.makeText(DangNhapActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}