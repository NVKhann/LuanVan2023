package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import com.example.quanlyquanan0609.Adapter.ThongKeHoaDonAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.databinding.ActivityThongKeDoanhThuKhuVucBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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
import java.util.Date;
import java.util.List;

public class ThongKeDoanhThuKhuVucActivity extends AppCompatActivity {
    ActivityThongKeDoanhThuKhuVucBinding binding;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongKeDoanhThuKhuVucBinding.inflate(getLayoutInflater());
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
        binding.tvTKDTKVNgayBD.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        binding.tvTKDTKVNgayKT.setText(String.format("%02d/%02d/%d", ngay, thang+1, nam));
        binding.tvTKDTKVNgayBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThongKeDoanhThuKhuVucActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvTKDTKVNgayBD.setText(ngay);
                        ngayBD = ngay;
                        funcLayDanhSachKhuVuc();
                        I1 = i;
                        I2 = i1 - 1;
                        I3 = i2;

                    }
                }, I1, I2, I3);
                datePickerDialog.show();
            }
        });
        binding.tvTKDTKVNgayKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ThongKeDoanhThuKhuVucActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvTKDTKVNgayKT.setText(ngay);
                        ngayKT = ngay;
                        funcLayDanhSachKhuVuc();
                        Nam = i;
                        Thang = i1 - 1;
                        Ngay = i2;
                    }
                }, Nam, Thang, Ngay);
                datePickerDialog.show();
            }
        });
        funcTaoPieChart();
        funcLayDanhSachKhuVuc();
    }

    private void funcLayDanhSachKhuVuc(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
//                    funcLayDanhSachBanTheoKhuVuc(khuVucClass.getKv_Ma()); // Loi
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayDanhSachBanTheoKhuVuc(Integer kvMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    funcLayDanhSachPgmTheoBan(banAnClass.getBan_Ma());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayDanhSachPgmTheoBan(Integer banMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(banMa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    funcLayHoaDonTheoPgm(pgmClass.getPgm_Ma());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayHoaDonTheoPgm(Integer pgmMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HoaDonClass> listHoaDon = new ArrayList<>();
                try {
                    Date tuNgay = format.parse(ngayBD);
                    Date denNgay = format.parse(ngayKT);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                        Date hdNgay = format.parse(hoaDonClass.getHd_Ngay());
                        if(hdNgay.equals(tuNgay) || hdNgay.equals(denNgay) || hdNgay.after(tuNgay) && hdNgay.before(denNgay)){
                            if(hoaDonClass.getHd_TongTien() != 0){
                                listHoaDon.add(hoaDonClass);
                            }
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                funcTaoDanhSachPieChart(listHoaDon);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDanhSachPieChart(ArrayList<HoaDonClass> listHoaDon) {
        for (HoaDonClass list : listHoaDon){
            Log.i("ListHD", "" + list.getHd_Ma());
        }
    }

    private void funcTaoPieChart() {
        PieChart pieChart = binding.pieChart;
        pieChart.setHoleRadius(0f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setRotationEnabled(false); // Không cho biểu đồ xoay

        int[] colors = new int[10];

//        colors[0] = Color.parseColor("#003f5c");
//        colors[1] = Color.parseColor("#2f4b7c");
//        colors[2] = Color.parseColor("#665191");
//        colors[3] = Color.parseColor("#a05195");
//        colors[4] = Color.parseColor("#d45087");
//        colors[5] = Color.parseColor("#f95d6a");
//        colors[6] = Color.parseColor("#ff7c43");
//        colors[7] = Color.parseColor("#ffa600");
//        colors[8] = Color.parseColor("#008000");
//        colors[9] = Color.parseColor("#FFC0CB");
        colors[0] = Color.parseColor("#003f5c");
        colors[1] = Color.parseColor("#2f4b7c");
        colors[2] = Color.parseColor("#665191");
        colors[3] = Color.parseColor("#a05195");
        colors[4] = Color.parseColor("#d45087");
        colors[5] = Color.parseColor("#f95d6a");
        colors[6] = Color.parseColor("#ff7c43");
        colors[7] = Color.parseColor("#ffa600");
        colors[8] = Color.parseColor("#ff7c43");
        colors[9] = Color.parseColor("#d45087");

        List<PieEntry> entries = new ArrayList<>();

        List<Float> values = new ArrayList<>();
        values.add(100000f);
        values.add(122000f);
        values.add(30000f);
        values.add(35000f);
        values.add(36000f);

        float total = 0f;
        int key = 1;
        for (float value : values) {
            total += value;
        }
        for (float value : values) {
            float percentage = (value / total) * 100;
            entries.add(new PieEntry(percentage, "Khu vực " + key + "                              " + numberFormat.format(value) + " VND"));
            key = key + 1;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(1.5f); // Đặt khoảng cách giữa các phần tử là 5f
        dataSet.setValueTextColor(Color.WHITE); // Đổi màu cho giá trị phần trăm
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.1f%%", value); // Thêm ký tự "%" vào phía sau giá trị
            }
        });
        dataSet.setValueTextSize(12f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        Legend legend = pieChart.getLegend();
        legend.setDrawInside(false); // Hiển thị chú thích bên ngoài biểu đồ
        legend.setFormSize(12f); // Kích thước của các hình dạng chú thích
        legend.setTextSize(14f); // Kích thước của chữ trong chú thích
        legend.setTextColor(Color.BLACK); // Màu chữ trong chú thích
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Hướng chú thích
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Vị trí dọc của chú thích
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT); // Vị trí ngang của chú thích
        legend.setWordWrapEnabled(true);
        pieChart.setDrawMarkers(false); // Ẩn các đánh dấu trong chú thích

        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}