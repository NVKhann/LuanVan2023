package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.ThongKeHoaDonAdapter;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.databinding.ActivityThongKeHoaDonBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ThongKeHoaDonActivity extends AppCompatActivity {
    ActivityThongKeHoaDonBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    Calendar calendar = Calendar.getInstance();
    final int nam = calendar.get(Calendar.YEAR);
    final int thang = calendar.get(Calendar.MONTH);
    final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
    int I1 = nam, I2 = thang, I3 = ngay;
    int Nam = nam, Thang = thang, Ngay = ngay;
    String ngayBD = String.format("%02d/%02d/%d", ngay, thang+1, nam);
    String ngayKT = String.format("%02d/%02d/%d", ngay, thang+1, nam);
    ThongKeHoaDonAdapter thongKeHoaDonAdapter;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    NumberFormat numberFormat = new DecimalFormat("#,###.##");
    String sapXep = "Mặc định";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongKeHoaDonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.tvTKHDNgayDB.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        binding.tvTKHDNgayKT.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        binding.tvTKHDNgayDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThongKeHoaDonActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvTKHDNgayDB.setText(ngay);
                        ngayBD = ngay;
                        funcHienThiDanhSachHoaDon(ngay, ngayKT, sapXep);
                        I1 = i;
                        I2 = i1 - 1;
                        I3 = i2;

                    }
                }, I1, I2, I3);
                datePickerDialog.show();
            }
        });
        binding.tvTKHDNgayKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThongKeHoaDonActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvTKHDNgayKT.setText(ngay);
                        ngayKT = ngay;
                        funcHienThiDanhSachHoaDon(ngayBD, ngay, sapXep);
                        Nam = i;
                        Thang = i1 - 1;
                        Ngay = i2;
                    }
                }, Nam, Thang, Ngay);
                datePickerDialog.show();
            }
        });
        binding.tvTKHDSapXep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoBotDialogSapXep();
            }
        });
        funcHienThiDanhSachHoaDon(ngayBD, ngayKT, sapXep);

    }

    private void funcTaoBotDialogSapXep() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.bot_dialog_sap_xep, null, false);
        TextView tvMacDinh, tvMoi, tvCu, tvCao, tvThap;
        tvMacDinh = view.findViewById(R.id.sdTvMacDinh);
        tvMoi = view.findViewById(R.id.sdTvMoiNhat);
        tvCu = view.findViewById(R.id.sdTvCuNhat);
        tvCao = view.findViewById(R.id.sdTvCaoThap);
        tvThap = view.findViewById(R.id.sdTvThapCao);
        tvMacDinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sapXep = tvMacDinh.getText().toString();
                binding.tvTKHDSapXep.setText(sapXep);
                funcHienThiDanhSachHoaDon(ngayBD, ngayKT, sapXep);
                bottomSheetDialog.dismiss();
            }
        });
        tvMoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sapXep = tvMoi.getText().toString();
                binding.tvTKHDSapXep.setText(sapXep);
                funcHienThiDanhSachHoaDon(ngayBD, ngayKT, sapXep);
                bottomSheetDialog.dismiss();
            }
        });
        tvCu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sapXep = tvCu.getText().toString();
                binding.tvTKHDSapXep.setText(sapXep);
                funcHienThiDanhSachHoaDon(ngayBD, ngayKT, sapXep);
                bottomSheetDialog.dismiss();
            }
        });
        tvCao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sapXep = tvCao.getText().toString();
                binding.tvTKHDSapXep.setText(sapXep);
                funcHienThiDanhSachHoaDon(ngayBD, ngayKT, sapXep);
                bottomSheetDialog.dismiss();
            }
        });
        tvThap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sapXep = tvThap.getText().toString();
                binding.tvTKHDSapXep.setText(sapXep);
                funcHienThiDanhSachHoaDon(ngayBD, ngayKT, sapXep);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void funcHienThiDanhSachHoaDon(String ngayBD, String ngayKT, String sapXep) {
        RecyclerView recyclerView = binding.rvTKHD;
        ArrayList<HoaDonClass> list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        thongKeHoaDonAdapter = new ThongKeHoaDonAdapter(this, list);
        recyclerView.setAdapter(thongKeHoaDonAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                Integer tongTien = 0;
                Integer slHD = 0;
                try {
                    Date tuNgay = format.parse(ngayBD);
                    Date denNgay = format.parse(ngayKT);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                        Date ngay = format.parse(hoaDonClass.getHd_Ngay());
                        if(ngay.equals(tuNgay) || ngay.equals(denNgay) || ngay.after(tuNgay) && ngay.before(denNgay)){
                            if(hoaDonClass.getHd_TongTien() != 0){
                                list.add(hoaDonClass);
                                tongTien = tongTien + hoaDonClass.getHd_TongTien();
                                slHD = slHD + 1;
                            }
                        }
                        if(sapXep.equals("Mới nhất")){
                            Collections.sort(list, new Comparator<HoaDonClass>() {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                                @Override
                                public int compare(HoaDonClass hoaDon1, HoaDonClass hoaDon2) {
                                    try {
                                        Date date1 = dateFormat.parse(hoaDon1.getHd_Ngay());
                                        Date date2 = dateFormat.parse(hoaDon2.getHd_Ngay());
                                        // Để sắp xếp theo ngày giảm dần, hãy đảo ngược thứ tự:
                                         return date2.compareTo(date1);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
                                }
                            });

                        } else if(sapXep.equals("Cũ nhất")){
                            Collections.sort(list, new Comparator<HoaDonClass>() {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                                @Override
                                public int compare(HoaDonClass hoaDon1, HoaDonClass hoaDon2) {
                                    try {
                                        Date date1 = dateFormat.parse(hoaDon1.getHd_Ngay());
                                        Date date2 = dateFormat.parse(hoaDon2.getHd_Ngay());

                                        // So sánh theo ngày tăng dần
                                        return date1.compareTo(date2);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
                                }
                            });

                        } else if(sapXep.equals("Giá trị cao đến thấp")){
                            Collections.sort(list, new Comparator<HoaDonClass>() {
                                @Override
                                public int compare(HoaDonClass hoaDon1, HoaDonClass hoaDon2) {
                                    // So sánh giá của HoaDonClass 1 và HoaDonClass 2
                                    return hoaDon2.getHd_TongTien().compareTo(hoaDon1.getHd_TongTien());
                                    // Nếu bạn muốn sắp xếp tăng dần theo giá, hãy đảo ngược thứ tự:
                                    // return hoaDon1.getHd_TongTien().compareTo(hoaDon2.getHd_TongTien());
                                }
                            });
                        } else if(sapXep.equals("Giá trị thấp đến cao")){
                            Collections.sort(list, new Comparator<HoaDonClass>() {
                                @Override
                                public int compare(HoaDonClass hoaDon1, HoaDonClass hoaDon2) {
                                    // So sánh giá của HoaDonClass 1 và HoaDonClass 2
//                                    return hoaDon2.getHd_TongTien().compareTo(hoaDon1.getHd_TongTien());
                                    // Nếu bạn muốn sắp xếp tăng dần theo giá, hãy đảo ngược thứ tự:
                                     return hoaDon1.getHd_TongTien().compareTo(hoaDon2.getHd_TongTien());
                                }
                            });
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    thongKeHoaDonAdapter.notifyDataSetChanged();
                    binding.tvDSHDTrong.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                    binding.lnTongSL.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.tvTKHDTongTien.setText(numberFormat.format(tongTien) + " VNĐ");
                    binding.tvTKHDTongHD.setText("Tổng " + slHD +" hóa đơn");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}