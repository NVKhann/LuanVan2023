package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityThongTinTaiKhoanBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ThongTinTaiKhoanActivity extends AppCompatActivity {
    ActivityThongTinTaiKhoanBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    Integer dem = 2, ivClick = 0;
    String strDuongDan = "", imgUrl;
    Uri imgUri;
    String Uid, quyen = "Khách hàng";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongTinTaiKhoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Uid = getIntent().getStringExtra("uId");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcHienThiSpinner();
        binding.spinChucVu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                quyen = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.ivTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dem == 0){
                    funcChonHinh();
                    ivClick = 1;
                }
            }
        });
        if(Uid == null){
            funcHienThiThongTin();
        }else if(Uid != null ){
            funcHienThiThongTinTheoUid(Uid);
        }
    }

    // Nguoi Dung Activity
    private void funcHienThiThongTinTheoUid(String Uid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NguoiDungClass nguoiDungClass = snapshot.getValue(NguoiDungClass.class);
                if(nguoiDungClass != null){
                    binding.edTTEmail.setText(nguoiDungClass.getNd_Email());
                    binding.edTTHoTen.setText(nguoiDungClass.getNd_Ten());
                    binding.edTTDiaChi.setText(nguoiDungClass.getNd_DiaChi());
                    binding.edTTSdt.setText(nguoiDungClass.getNd_Sdt());
                    funcHIenThiDTL(nguoiDungClass.getNd_DiemTL());
                    if(!nguoiDungClass.getNd_HinhAnh().equals("Đường dẫn")){
                        Glide.with(ThongTinTaiKhoanActivity.this).load(nguoiDungClass.getNd_HinhAnh()).apply(new RequestOptions().centerCrop()).signature((new ObjectKey(nguoiDungClass.getNd_HinhAnh()))).into(binding.ivTT);
                    }else {
                        binding.ivTT.setImageResource(R.drawable.user);
                    }
                    if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        binding.lnChucVu.setVisibility(View.GONE);
                        binding.lnViewChucVu.setVisibility(View.GONE);
                        binding.spinChucVu.setVisibility(View.GONE);
                        binding.lnDTL.setVisibility(View.VISIBLE);
                        binding.lnViewBot.setVisibility(View.VISIBLE);
                    }else {
                        binding.tvChucVu.setVisibility(View.GONE);
                        binding.spinChucVu.setVisibility(View.VISIBLE);
                        binding.spinChucVu.setSelection(getIndex(binding.spinChucVu, nguoiDungClass.getNd_Quyen()));
                    }


                }else {
                    Toast.makeText(ThongTinTaiKhoanActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThongTinTaiKhoanActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void funcCapNhatThongTinTheoUid(String uID, String hoTen, String diaChi, String sdt, String quyen) {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        String uID = firebaseUser.getUid();
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.child(uID).child("nd_Ten").setValue(hoTen);
        databaseReference.child(uID).child("nd_DiaChi").setValue(diaChi);
        databaseReference.child(uID).child("nd_Sdt").setValue(sdt);
        databaseReference.child(uID).child("nd_Quyen").setValue(quyen);
        if(ivClick != 0){
            funcLuuHinhAnh(uID);
            ivClick = 0;
        }
        Toast.makeText(ThongTinTaiKhoanActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    // End

    private void funcHienThiThongTin() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NguoiDungClass nguoiDungClass = snapshot.getValue(NguoiDungClass.class);
                if(nguoiDungClass != null){
                    binding.edTTEmail.setText(nguoiDungClass.getNd_Email());
                    binding.edTTHoTen.setText(nguoiDungClass.getNd_Ten());
                    binding.edTTDiaChi.setText(nguoiDungClass.getNd_DiaChi());
                    binding.edTTSdt.setText(nguoiDungClass.getNd_Sdt());
                    binding.tvChucVu.setText(nguoiDungClass.getNd_Quyen());
                    binding.edTTDTL.setText(String.valueOf(nguoiDungClass.getNd_DiemTL()));
                    funcHIenThiDTL(nguoiDungClass.getNd_DiemTL());
                    if(!nguoiDungClass.getNd_HinhAnh().equals("Đường dẫn") && !isDestroyed() && !isFinishing()){
                        Glide.with(ThongTinTaiKhoanActivity.this).load(nguoiDungClass.getNd_HinhAnh()).apply(new RequestOptions().centerCrop()).signature((new ObjectKey(nguoiDungClass.getNd_HinhAnh()))).into(binding.ivTT);
                    }
                    if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        binding.lnChucVu.setVisibility(View.GONE);
                        binding.lnViewChucVu.setVisibility(View.GONE);
                        binding.lnDTL.setVisibility(View.VISIBLE);
                        binding.lnViewBot.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(ThongTinTaiKhoanActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ThongTinTaiKhoanActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void funcHIenThiDTL(Integer diemTl) {
        String hang = null;
        Integer km = 0;
        if(diemTl>=100 && diemTl <300){          // KH B1
            km = 1;
            hang = diemTl + " (KH bậc 1)";
        }else if(diemTl>=300 && diemTl<500){    // KH B2
            km =3;
            hang = diemTl + " (KH bậc 2)";
        }else if(diemTl>=500 && diemTl<700){    // KH B3
            km =5;
            hang = diemTl + " (KH bậc 3)";
        }else if(diemTl>=700 && diemTl<1000){    // KH B4
            km = 7;
            hang = diemTl + " (KH bậc 4)";
        }else if(diemTl>1000){    // KH B5
            km =10;
            hang = diemTl + " (KH bậc 5)";
        }else {
            hang = diemTl + " (KH bậc 0)";
        }
        binding.edTTDTL.setText(hang);
    }

    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }

    private void funcHienThiSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Chủ quán");
        items.add("Quản lý");
        items.add("Nhân viên");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinChucVu.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thong_tin_tai_khoan, menu);
        MenuItem menuThem = menu.findItem(R.id.menuTTThem);
        MenuItem menuCheck = menu.findItem(R.id.menuTTCheck);
        if(dem == 0){
            menuThem.setVisible(false);
            menuCheck.setVisible(true);
        }else if (dem == 1){
            menuThem.setVisible(true);
            menuCheck.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuTTThem){
            funcTaoBotDiaLog();
        }
        if(item.getItemId() == R.id.menuTTCheck){
            dem = 1;
            binding.edTTHoTen.setEnabled(false);
            binding.edTTHoTen.setTextColor(Color.GRAY);
            binding.edTTDiaChi.setEnabled(false);
            binding.edTTDiaChi.setTextColor(Color.GRAY);
            binding.edTTSdt.setEnabled(false);
            binding.edTTSdt.setTextColor(Color.GRAY);
            String hoTen, diaChi, sdt;
            hoTen = binding.edTTHoTen.getText().toString();
            diaChi = binding.edTTDiaChi.getText().toString();
            sdt = binding.edTTSdt.getText().toString();
            if(Uid != null){
                funcCapNhatThongTinTheoUid(Uid, hoTen, diaChi, sdt, quyen);
            }else {
                funcCapNhatThongTin(hoTen, diaChi, sdt);
            }
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }


    private void funcCapNhatThongTin(String hoTen, String diaChi, String sdt) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uID = firebaseUser.getUid();
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.child(uID).child("nd_Ten").setValue(hoTen);
        databaseReference.child(uID).child("nd_DiaChi").setValue(diaChi);
        databaseReference.child(uID).child("nd_Sdt").setValue(sdt);
//        databaseReference.child(uID).child("nd_Email").setValue(firebaseUser.getEmail());
        if(ivClick != 0){
            funcLuuHinhAnh(uID);
            ivClick = 0;
        }
        Toast.makeText(ThongTinTaiKhoanActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);

    }
    private void funcXoaHinhCu(String monHinh) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(monHinh);
        storageReference.delete();
    }
    private void funcLuuHinhAnh(String uId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Images/" + uId);
        if(imgUri!=null){
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgUrl = uri.toString();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
                            databaseReference.child(uId).child("nd_HinhAnh").setValue(imgUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ThongTinTaiKhoanActivity.this, "Lỗi khi lưu hình ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void funcTaoBotDiaLog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view =getLayoutInflater().inflate(R.layout.bot_dialog_tai_khoan, null, false);
        TextView tvDoiMk, tvDoiEmail, tvThongTin;
        tvDoiMk = view.findViewById(R.id.bdTkMatKhau);
        tvDoiEmail = view.findViewById(R.id.bdTkEmail);
        tvThongTin = view.findViewById(R.id.bdTkThongTin);
        if(Uid != null){
            tvDoiMk.setVisibility(View.GONE);
            tvDoiEmail.setText("Xóa tài khoản");
            tvDoiEmail.setTextColor(Color.RED);
            tvDoiEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ThongTinTaiKhoanActivity.this, "Đang cập nhật ...", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
            tvThongTin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                    binding.edTTHoTen.setEnabled(true);
                    binding.edTTHoTen.setTextColor(Color.BLACK);
                    binding.edTTDiaChi.setEnabled(true);
                    binding.edTTDiaChi.setTextColor(Color.BLACK);
                    binding.edTTSdt.setEnabled(true);
                    binding.edTTSdt.setTextColor(Color.BLACK);
                    dem = 0;
                    invalidateOptionsMenu();

                }
            });
        }else {
            tvThongTin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                    binding.edTTHoTen.setEnabled(true);
                    binding.edTTHoTen.setTextColor(Color.BLACK);
                    binding.edTTDiaChi.setEnabled(true);
                    binding.edTTDiaChi.setTextColor(Color.BLACK);
                    binding.edTTSdt.setEnabled(true);
                    binding.edTTSdt.setTextColor(Color.BLACK);
                    dem = 0;
                    invalidateOptionsMenu();

                }
            });
            tvDoiMk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    funcTaoDialogDoiMk();
                    bottomSheetDialog.dismiss();
                }
            });
            tvDoiEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ThongTinTaiKhoanActivity.this, "Đang cập nhật ...", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            });
        }

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void funcChonHinh() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
            binding.ivTT.setImageURI(imgUri);

        }
    }

    private void funcTaoDialogDoiMk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_doi_mat_khau, null);
        TextInputLayout tipMkHienTai, tipMkMoi, tipMkNhapLai;
        EditText edHienTai, edMoi, edNhapLai;
        Button btnHuy, btnOk;
        tipMkHienTai = view.findViewById(R.id.tipDMKHTai);
        tipMkMoi = view.findViewById(R.id.tipDMKMoi);
        tipMkNhapLai = view.findViewById(R.id.tipDMKNhapLai);
        edHienTai = view.findViewById(R.id.edDMKHTai);
        edMoi = view.findViewById(R.id.edDMKMoi);
        edNhapLai = view.findViewById(R.id.edDMKNhapLai);
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
                String strMkHienTai, strMkMoi, strMkNhapLai;
                strMkHienTai = edHienTai.getText().toString();
                strMkMoi = edMoi.getText().toString();
                strMkNhapLai = edNhapLai.getText().toString();
                if(TextUtils.isEmpty(strMkHienTai)){
                    tipMkHienTai.setHelperText("Vui lòng nhập mật khẩu hiện tại");
                    edHienTai.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            tipMkHienTai.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (TextUtils.isEmpty(strMkMoi)) {
                    tipMkMoi.setHelperText("Vui lòng nhập mật khẩu mới");
                    edMoi.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            tipMkMoi.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                } else if (TextUtils.isEmpty(strMkNhapLai)) {
                    tipMkNhapLai.setHelperText("Vui lòng nhập lại mật khẩu mới");
                    edNhapLai.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            tipMkNhapLai.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }else if (!strMkNhapLai.equals(strMkMoi)){
                    tipMkNhapLai.setHelperText("Mật khẩu nhập lại không khớp");
                    edNhapLai.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            tipMkNhapLai.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                    edMoi.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            tipMkNhapLai.setHelperTextEnabled(false);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });

                }else {
                    funcDoiMatKhau(strMkHienTai, strMkNhapLai, alertDialog);
                }
            }
        });
        alertDialog.show();
    }

    private void funcDoiMatKhau(String strMkHienTai, String strMkNhapLai, AlertDialog alertDialog) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.equals("")){
            Toast.makeText(ThongTinTaiKhoanActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }else {
            AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), strMkHienTai);
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        firebaseUser.updatePassword(strMkNhapLai).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    funcTaoDialogXacNhan();
                                    alertDialog.dismiss();
                                }else {
                                    try {
                                        throw task.getException();
                                    }catch (Exception e){
                                        Toast.makeText(ThongTinTaiKhoanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ThongTinTaiKhoanActivity.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void funcTaoDialogXacNhan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        tvTitle.setText("Thông báo");
        tvNoiDung.setText("Bạn vừa thay đổi mật khẩu. Vui lòng đăng nhập lại");
        btnHuy.setVisibility(View.GONE);
        btnOk.setText("Xác nhận");
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                binding.progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(ThongTinTaiKhoanActivity.this, DangNhapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
            }
        });
        alertDialog.show();
    }
}