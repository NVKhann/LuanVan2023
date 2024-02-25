package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.DoanhThuMonAnClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityThongKeDoanhThuMonAnBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ThongKeDoanhThuMonAnActivity extends AppCompatActivity {
    ActivityThongKeDoanhThuMonAnBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    Calendar calendar = Calendar.getInstance();
    final int nam = calendar.get(Calendar.YEAR);
    final int thang = calendar.get(Calendar.MONTH);
    final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
    int I1 = nam, I2 = thang, I3 = ngay;
    int Nam = nam, Thang = thang, Ngay = ngay;
    String ngayBD = String.format("%02d/%02d/%d", ngay, thang+1, nam);
    String ngayKT = String.format("%02d/%02d/%d", ngay, thang+1, nam);
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    NumberFormat numberFormat = new DecimalFormat("#,###.##");
    Double tongTien = 0.0;
    ArrayList<DoanhThuMonAnClass> listDTMA = new ArrayList<>();
    Map<String, Double> tongTienTheoNgay = new HashMap<>();

    int dem = 0;
    String tenMon = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongKeDoanhThuMonAnBinding.inflate(getLayoutInflater());
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
        binding.tvTKDTMANgayBD.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        binding.tvTKDTMANgayKT.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        binding.tvTKDTMANgayBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThongKeDoanhThuMonAnActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvTKDTMANgayBD.setText(ngay);
                        ngayBD = ngay;
                        funcLayDanhSachHoaDon(ngay, ngayKT);
                        I1 = i;
                        I2 = i1 - 1;
                        I3 = i2;

                    }
                }, I1, I2, I3);
                datePickerDialog.show();
            }
        });
        binding.tvTKDTMANgayKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThongKeDoanhThuMonAnActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvTKDTMANgayKT.setText(ngay);
                        ngayKT = ngay;
                        funcLayDanhSachHoaDon(ngayBD, ngay);
                        Nam = i;
                        Thang = i1 - 1;
                        Ngay = i2;
                    }
                }, Nam, Thang, Ngay);
                datePickerDialog.show();
            }
        });

        funcHienThiSpinnerMon();
        funcLayDanhSachHoaDon(ngayBD, ngayKT);
    }

    private void funcLayDanhSachHoaDon(String ngayBD, String ngayKT) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HoaDonClass> listHoaDon = new ArrayList<>();
                Set<String> uniqueDates = new HashSet<>();
                try {
                    Date tuNgay = format.parse(ngayBD);
                    Date denNgay = format.parse(ngayKT);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                        Date hdNgay = format.parse(hoaDonClass.getHd_Ngay());
                        if(hdNgay.equals(tuNgay) || hdNgay.equals(denNgay) || hdNgay.after(tuNgay) && hdNgay.before(denNgay)){
                            listHoaDon.add(hoaDonClass);
                            uniqueDates.add(hoaDonClass.getHd_Ngay());

                        }
                    }
                    List<String> stringList = new ArrayList<>(uniqueDates);
                    funcLayHoaDonTheoNgay(listHoaDon, stringList);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayHoaDonTheoNgay(ArrayList<HoaDonClass> listHoaDon, List<String> stringList) {
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        binding.tvTKDTTrong.setVisibility(listHoaDon.isEmpty() ? View.VISIBLE : View.GONE);
        binding.barChartTKDTMA.setVisibility(listHoaDon.isEmpty() ? View.GONE : View.VISIBLE);
        for (String list : stringList){
            dataHoaDon.orderByChild("hd_Ngay").equalTo(list).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<HoaDonClass> hDTheoNgay = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                        hDTheoNgay.add(hoaDonClass);
                    }
                    funcLayChiTietHDTheoNgay(hDTheoNgay, list, stringList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//        for (HoaDonClass hoaDonClass : listHoaDon){
//            dataChiTietPgm.orderByChild("mon_Ma").equalTo("1").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    ArrayList<ChiTietPGMClass> listChiTiet = new ArrayList<>();
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        ChiTietPGMClass chiTietPGMClass = dataSnapshot.getValue(ChiTietPGMClass.class);
//                        listChiTiet.add(chiTietPGMClass);
//                    }
//
//                    // Tinh tong theo mon
//                    for (ChiTietPGMClass chiTietPGMClass : listChiTiet) {
//                        String maMon = String.valueOf(chiTietPGMClass.getMon_Ma());
//                        double tongTien = chiTietPGMClass.getCt_DonGia() * chiTietPGMClass.getCt_SoLuong(); // Giả sử có phương thức getTongTien() trả về tổng tiền dạng double
//                        if (tongTienTheoMon.containsKey(maMon)) {
//                            double tongTienHienTai = tongTienTheoMon.get(maMon);
//                            tongTienTheoMon.put(maMon, tongTienHienTai + tongTien);
//                        } else {
//                            tongTienTheoMon.put(maMon, tongTien);
//                        }
//                    }
//
//                    // In ra tổng tiền theo món khi vòng lặp kết thúc
//                    if (hoaDonClass.equals(listHoaDon.get(listHoaDon.size() - 1))) {
////                        funcLayTenMon(tongTienTheoMon);
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }

    }
    int demFor = 0;


    private void funcLayChiTietHDTheoNgay(ArrayList<HoaDonClass> hDTheoNgay, String ngayLayHD, int size) {
        DatabaseReference dataChiTietPgm = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        for (HoaDonClass hDNgay : hDTheoNgay){
            dataChiTietPgm.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshotPgm : snapshot.getChildren()){
                        ChiTietPGMClass chiTietPGMClass = dataSnapshotPgm.getValue(ChiTietPGMClass.class);
                        if(chiTietPGMClass.getPgm_Ma().equals(hDNgay.getPgm_Ma()) && chiTietPGMClass.getMon_Ma() == 1){
                            tongTien = tongTien + chiTietPGMClass.getCt_SoLuong() * chiTietPGMClass.getCt_DonGia();
                            Log.i("CTTT", hDNgay.getHd_Ngay() + " " + tongTien);
                            DoanhThuMonAnClass doanhThuMonAnClass = new DoanhThuMonAnClass(1, ngayLayHD, tongTien);
                            listDTMA.add(doanhThuMonAnClass);
                        }
                    }
                    for (DoanhThuMonAnClass doanhThuMonAnClass : listDTMA){
                        String ngayLap = doanhThuMonAnClass.ngay;
                        double tongTien = doanhThuMonAnClass.tongTien; // Giả sử có phương thức getTongTien() trả về tổng tiền dạng double
                        if (tongTienTheoNgay.containsKey(ngayLap)) {
                            double tongTienHienTai = tongTienTheoNgay.get(ngayLap);
                            tongTienTheoNgay.put(ngayLap, tongTienHienTai + tongTien);
                        } else {
                            tongTienTheoNgay.put(ngayLap, tongTien);
                        }
                    }
                    Log.i("Dem", " " + dem + " " + size);
                    if(demFor == hDTheoNgay.size()){
                        dem ++ ;
                    }
                    if(dem == size){
                        for (Map.Entry<String, Double> entry : tongTienTheoNgay.entrySet()) {
                            double tongTien = entry.getValue();
                            Log.i("TongTien", entry.getKey() + " " + tongTien);
                        }
                    }
                    demFor ++ ;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void funcThemMap(ArrayList<DoanhThuMonAnClass> list) {
        if(list.isEmpty()){
            Log.i("DT", "Trong");

        }

    }

    private void funcMa(ArrayList<DoanhThuMonAnClass> list) {
        if(list.isEmpty()){
            Toast.makeText(ThongKeDoanhThuMonAnActivity.this, "Ko co du lieu", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(ThongKeDoanhThuMonAnActivity.this, "OK", Toast.LENGTH_SHORT).show();
        }
//        for (Map.Entry<String, Double> entry : tongTienTheoMon.entrySet()) {
//            double tongTien = entry.getValue();
//            Log.i("Map", " - " + tongTien);
//
//        }
    }

    private void funcHienThiSpinnerMon() {
        List<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MonAnClass monAnClass = dataSnapshot.getValue(MonAnClass.class);
                    items.add(monAnClass.getMon_Ten());
                }
                Collections.sort(items);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.spinTKDTMA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tenMon = adapterView.getItemAtPosition(i).toString();
//                funcLayMaNhanVien(nvTen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.spinTKDTMA.setAdapter(adapter);
    }


}