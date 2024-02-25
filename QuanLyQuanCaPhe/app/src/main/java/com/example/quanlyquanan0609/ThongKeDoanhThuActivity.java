package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.databinding.ActivityThongKeDoanhThuBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ThongKeDoanhThuActivity extends AppCompatActivity {
    ActivityThongKeDoanhThuBinding binding;
    String tabTitle = "Theo ngày";
    Toolbar toolbar;
    ProgressBar progressBar;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    NumberFormat numberFormat = new DecimalFormat("#,###.##");

    final int nam = calendar.get(Calendar.YEAR);
    final int thang = calendar.get(Calendar.MONTH);
    final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
    int year = nam, month = thang, day = ngay;
    int yearKt = nam, monthKt = thang, dayKt = ngay;

    String ngayTk = String.format("%02d/%02d/%d", ngay, thang+1, nam);
    String thangTk = String.format("%02d/%d", thang+1, nam);
    String namTk = String.format("%d", nam);

    String ngayTkKt = String.format("%02d/%02d/%d", ngay, thang+1, nam);
    String thangTkKt = String.format("%02d/%d", thang+1, nam);
    String namTkKt = String.format("%d", nam);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongKeDoanhThuBinding.inflate(getLayoutInflater());
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
        binding.tvTKDTNgayBD.setText(ngayTk);
        binding.tvTKDTNgayKT.setText(ngayTkKt);
        binding.tvTKDTMoTa.setText("Báo cáo doanh thu ngày " + ngayTk);
        binding.tlNgay.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabTitle = tab.getText().toString();
                if(tabTitle.equals("Theo ngày")){
                    binding.tvTKDTNgayBD.setText(ngayTk);
                    binding.tvTKDTNgayKT.setText(ngayTkKt);
                    binding.tvTKDTMoTa.setText("Báo cáo doanh thu ngày " + ngayTk);
                    funcLayDanhSachTheoNgay(ngayTk, ngayTkKt);

                    year = nam;
                    month = thang;
                    day = ngay;

                    yearKt = nam;
                    monthKt = thang;
                    dayKt = ngay;

                    thangTk = String.format("%02d/%d", thang+1, nam);
                    thangTkKt = String.format("%02d/%d", thang+1, nam);
                    namTk = String.format("%d", nam);
                    namTkKt = String.format("%d", nam);
                } else if(tabTitle.equals("Theo tháng")){
                    binding.tvTKDTNgayBD.setText(thangTk);
                    binding.tvTKDTNgayKT.setText(thangTkKt);
                    binding.tvTKDTMoTa.setText("Báo cáo doanh thu tháng " + thangTk);
                    funcLayDanhSachTheoThang(thangTk, thangTkKt, "0");

                    year = nam;
                    month = thang;
                    day = ngay;

                    yearKt = nam;
                    monthKt = thang;
                    dayKt = ngay;

                    ngayTk = String.format("%02d/%02d/%d", ngay, thang+1, nam);
                    ngayTkKt = String.format("%02d/%02d/%d", ngay, thang+1, nam);
                    namTk = String.format("%d", nam);
                    namTkKt = String.format("%d", nam);
                } else if(tabTitle.equals("Theo năm")){
                    binding.tvTKDTNgayBD.setText(namTk);
                    binding.tvTKDTNgayKT.setText(namTkKt);
                    binding.tvTKDTMoTa.setText("Báo cáo doanh thu năm " + namTk);
                    funcLayDanhSachTheoNam(namTk, namTkKt);
                    year = nam;
                    month = thang;
                    day = ngay;

                    yearKt = nam;
                    monthKt = thang;
                    dayKt = ngay;

                    ngayTk = String.format("%02d/%02d/%d", ngay, thang+1, nam);
                    ngayTkKt = String.format("%02d/%02d/%d", ngay, thang+1, nam);
                    thangTk = String.format("%02d/%d", thang+1, nam);
                    thangTkKt = String.format("%02d/%d", thang+1, nam);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.tvTKDTNgayBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDatePickerDialog(tabTitle, binding.tvTKDTNgayBD);
            }
        });
        binding.tvTKDTNgayKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDatePickerDialogKT(tabTitle, binding.tvTKDTNgayKT);
            }
        });
        funcLayDanhSachTheoNgay(ngayTk, ngayTkKt);
    }

    private void funcLayDanhSachTheoNgay(String ngayBD, String ngayKT) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();
                Set<String> uniqueDates = new HashSet<>();
                ArrayList<HoaDonClass> hoaDonNgay = new ArrayList<>();
                Map<String, Double> tongTienTheoNgay = new HashMap<>();
                Integer tkTongTien = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    try {
                        Date hdNgay = format.parse(hoaDonClass.getHd_Ngay());
                        Date ngayBd = format.parse(ngayBD);
                        Date ngayKt = format.parse(ngayKT);
                        if(hoaDonClass.getHd_TongTien() != 0){
                            if(hdNgay.equals(ngayBd) || hdNgay.equals(ngayKt) || hdNgay.after(ngayBd) && hdNgay.before(ngayKt)){
                                hoaDonNgay.add(hoaDonClass);
                                uniqueDates.add(hoaDonClass.getHd_Ngay());
                                tkTongTien = tkTongTien + Integer.valueOf(hoaDonClass.getHd_TongTien());
                            }
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                }

                List<String> stringList = new ArrayList<>(uniqueDates);
                Collections.sort(stringList, new DayComparator());

                for (HoaDonClass hoaDon : hoaDonNgay) {
                    String ngayLap = hoaDon.getHd_Ngay(); // Giả sử có phương thức getNgayLap() trả về ngày dạng String
                    double tongTien = hoaDon.getHd_TongTien(); // Giả sử có phương thức getTongTien() trả về tổng tiền dạng double
                    if (tongTienTheoNgay.containsKey(ngayLap)) {
                        double tongTienHienTai = tongTienTheoNgay.get(ngayLap);
                        tongTienTheoNgay.put(ngayLap, tongTienHienTai + tongTien);
                    } else {
                        tongTienTheoNgay.put(ngayLap, tongTien);
                    }
                }
                // Sắp xếp danh sách theo ngayLap
                Map<String, Double> sortedMap = new TreeMap<>(new DayComparator());
                sortedMap.putAll(tongTienTheoNgay);
                List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(sortedMap.entrySet());

                int index = 0;
                for (Map.Entry<String, Double> entry : sortedEntries) {
                    double tongTien = entry.getValue();
                    entries.add(new BarEntry(index, (float) tongTien));
                    index++;
                }
                if(!stringList.isEmpty()){
                    binding.tvTKDTTrong.setVisibility(View.GONE);
                    binding.barChart.setVisibility(View.VISIBLE);
                    binding.tvTKDTTong.setText(numberFormat.format(tkTongTien) + " VNĐ");
                    funcTaoBarChart(entries, stringList, "Ngày");
                }else {
                    binding.tvTKDTTrong.setVisibility(View.VISIBLE);
                    binding.barChart.setVisibility(View.INVISIBLE);
                    binding.tvTKDTTong.setText("0 VNĐ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayDanhSachTheoThang(String thangBD, String thangKT, String ngayCuoi) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();
                ArrayList<HoaDonClass> hoaDonThang = new ArrayList<>();
                Map<String, Double> tongTienTheoNgay = new HashMap<>();
                Set<String> uniqueDates = new HashSet<>();
                Integer tkTongTien = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    try {
                        Date hdNgay = format.parse(hoaDonClass.getHd_Ngay());
                        String strThangBD = "01/"+thangBD;
                        String strThangKT = ngayTk;
                        if(!ngayCuoi.equals("0")){
                            strThangKT = ngayCuoi+ "/" +thangKT;
                        }

                        Date dThangBD = format.parse(strThangBD);
                        Date dThangKT = format.parse(strThangKT);

                        if(hdNgay.equals(dThangBD) || hdNgay.equals(dThangKT) || hdNgay.after(dThangBD) && hdNgay.before(dThangKT)){
                            hoaDonThang.add(hoaDonClass);
                            uniqueDates.add("01" + hoaDonClass.getHd_Ngay().substring(2));
                            tkTongTien = tkTongTien + Integer.valueOf(hoaDonClass.getHd_TongTien());
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                // Thêm các ngày vào danh sách stringList
                List<String> stringList = new ArrayList<>(uniqueDates);
                Collections.sort(stringList, new DayComparator());


                // Tinh tong theo ngay
                for (HoaDonClass hoaDon : hoaDonThang) {
                    String ngayLap = "01" + hoaDon.getHd_Ngay().substring(2); // Giả sử có phương thức getNgayLap() trả về ngày dạng String
                    double tongTien = hoaDon.getHd_TongTien(); // Giả sử có phương thức getTongTien() trả về tổng tiền dạng double
                    if (tongTienTheoNgay.containsKey(ngayLap)) {
                        double tongTienHienTai = tongTienTheoNgay.get(ngayLap);
                        tongTienTheoNgay.put(ngayLap, tongTienHienTai + tongTien);
                    } else {
                        tongTienTheoNgay.put(ngayLap, tongTien);
                    }
                }

                // Sắp xếp danh sách theo ngayLap
                Map<String, Double> sortedMap = new TreeMap<>(new DayComparator());
                sortedMap.putAll(tongTienTheoNgay);
                List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(sortedMap.entrySet());

                int index = 0;
                for (Map.Entry<String, Double> entry : sortedEntries) {
                    double tongTien = entry.getValue();
                    entries.add(new BarEntry(index, (float) tongTien));
                    index++;
                }
                if(!stringList.isEmpty()){
                    binding.tvTKDTTrong.setVisibility(View.GONE);
                    binding.barChart.setVisibility(View.VISIBLE);
                    binding.tvTKDTTong.setText(numberFormat.format(tkTongTien) + " VNĐ");
                    funcTaoBarChart(entries, stringList, "Tháng");
                }else {
                    binding.tvTKDTTrong.setVisibility(View.VISIBLE);
                    binding.barChart.setVisibility(View.INVISIBLE);
                    binding.tvTKDTTong.setText("0 VNĐ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayDanhSachTheoNam(String namBD, String namKT) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();
                ArrayList<HoaDonClass> hoaDonNam = new ArrayList<>();
                Map<String, Double> tongTienTheoNgay = new HashMap<>();
                Set<String> uniqueDates = new HashSet<>();
                Integer tkTongTien = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    try {
                        Date hdNgay = format.parse(hoaDonClass.getHd_Ngay());
                        String strThangBD = "01/01/" + namBD;
                        String strThangKT = "31/12/"+ namKT;

                        Date dThangBD = format.parse(strThangBD);
                        Date dThangKT = format.parse(strThangKT);

                        if(hdNgay.equals(dThangBD) || hdNgay.equals(dThangKT) || hdNgay.after(dThangBD) && hdNgay.before(dThangKT)){
                            hoaDonNam.add(hoaDonClass);
                            uniqueDates.add("01/01" + hoaDonClass.getHd_Ngay().substring(5));
                            tkTongTien = tkTongTien + Integer.valueOf(hoaDonClass.getHd_TongTien());
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                // Thêm các ngày vào danh sách stringList
                List<String> stringList = new ArrayList<>(uniqueDates);
                Collections.sort(stringList, new DayComparator());

                // Tinh tong theo ngay
                for (HoaDonClass hoaDon : hoaDonNam) {
                    String ngayLap = "01/01" + hoaDon.getHd_Ngay().substring(5);
                    double tongTien = hoaDon.getHd_TongTien(); // Giả sử có phương thức getTongTien() trả về tổng tiền dạng double
                    if (tongTienTheoNgay.containsKey(ngayLap)) {
                        double tongTienHienTai = tongTienTheoNgay.get(ngayLap);
                        tongTienTheoNgay.put(ngayLap, tongTienHienTai + tongTien);
                    } else {
                        tongTienTheoNgay.put(ngayLap, tongTien);
                    }
                }
                // Sắp xếp danh sách theo ngayLap
                Map<String, Double> sortedMap = new TreeMap<>(new DayComparator());
                sortedMap.putAll(tongTienTheoNgay);
                List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(sortedMap.entrySet());

                int index = 0;
                for (Map.Entry<String, Double> entry : sortedEntries) {
                    String ngayLap = entry.getKey();
                    double tongTien = entry.getValue();
                    entries.add(new BarEntry(index, (float) tongTien));
                    index++;

                }
                if(!stringList.isEmpty()){
                    binding.tvTKDTTrong.setVisibility(View.GONE);
                    binding.barChart.setVisibility(View.VISIBLE);
                    binding.tvTKDTTong.setText(numberFormat.format(tkTongTien) + " VNĐ");
                    funcTaoBarChart(entries, stringList, "Năm");
                }else {
                    binding.tvTKDTTrong.setVisibility(View.VISIBLE);
                    binding.barChart.setVisibility(View.INVISIBLE);
                    binding.tvTKDTTong.setText("0 VNĐ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoBarChart(List<BarEntry> entries, List<String> stringList, String label) {
        BarChart barChart = findViewById(R.id.barChart);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.setFitBars(true);
        List<String> listLabel = new ArrayList<>();
        for (String list : stringList){
            if(label.equals("Tháng")){
                listLabel.add(list.substring(3));
            }else if(label.equals("Năm")){
                listLabel.add(list.substring(6));
            }else {
                listLabel.add(list.substring(0,5));
            }

        }
        // Tùy chỉnh biểu đồ
        barChart.getDescription().setEnabled(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        YAxis rightYAxis = barChart.getAxisRight();
        rightYAxis.setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(entries, label);
        barDataSet.setColors(getColor(R.color.primaryPurple));

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setValueTextSize(12f);

        // Tùy chỉnh trục x (ngày)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(listLabel)); // Sử dụng định dạng ngày
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Giữa mỗi ngày
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisLineColor(getColor(R.color.bg_color));


        // Tùy chỉnh trục y (tổng tiền)
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Giá trị tối thiểu của trục y
        yAxis.setGranularity(10f); // Giữa mỗi đơn vị trục y
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisLineWidth(1f);
        yAxis.setAxisLineColor(getColor(R.color.bg_color));

        // Hiển thị biểu đồ
        barChart.animateY(1000);
        barChart.invalidate();

    }

    private void funcTaoDatePickerDialog(String tabTitle, TextView textView) {
        if(tabTitle.equals("Theo ngày")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeDoanhThuActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_day_month_year_picker, null);
            builder.setView(dialogView);

            final NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
            final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
            final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

            // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
            dayPicker.setMinValue(1);
            dayPicker.setMaxValue(31);
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            yearPicker.setMinValue(2019);
            yearPicker.setMaxValue(nam);
            yearPicker.setWrapSelectorWheel(false);

            dayPicker.setValue(day);
            monthPicker.setValue(month + 1);
            yearPicker.setValue(year);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedDay = dayPicker.getValue();
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth, selectedYear);
                    textView.setText(selectedDate);
                    if(selectedDate.equals(ngayTkKt)){
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu ngày " + selectedDate);
                    }else {
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu từ ngày " + selectedDate + " đến ngày " + ngayTkKt);
                    }
                    funcLayDanhSachTheoNgay(selectedDate, ngayTkKt);
//                    day = selectedDay;
//                    month = selectedMonth - 1;
//                    year = selectedYear;
                    ngayTk = selectedDate;

                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();

        }else if(tabTitle.equals("Theo tháng")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeDoanhThuActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);
            builder.setView(dialogView);

            final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
            final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

            // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            yearPicker.setMinValue(2010);
            yearPicker.setMaxValue(nam);
            yearPicker.setWrapSelectorWheel(false);


            monthPicker.setValue(month + 1);
            yearPicker.setValue(year);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = String.format("%02d/%d", selectedMonth, selectedYear);
                    textView.setText(selectedDate);
                    if(selectedDate.equals(thangTkKt)){
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu tháng " + selectedDate);
                    }else {
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu từ tháng " + selectedDate + " đến tháng " + thangTkKt);
                    }
                    thangTk = selectedDate;
                    day = ngay;
                    month = selectedMonth - 1;
                    year = selectedYear;
                    funcLayDanhSachTheoThang(selectedDate, thangTkKt, "0");

                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();

        }else if(tabTitle.equals("Theo năm")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeDoanhThuActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_year_picker, null);
            builder.setView(dialogView);

            final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

            // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
            yearPicker.setMinValue(2020);
            yearPicker.setMaxValue(nam);
            yearPicker.setWrapSelectorWheel(false);

            yearPicker.setValue(year);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = String.format("%d",selectedYear);
                    textView.setText(selectedDate);
                    if(selectedDate.equals(nam)){
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu năm " + selectedDate);
                    }else {
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu từ năm " + selectedDate + " đến nam " + namTkKt);
                    }
                    funcLayDanhSachTheoNam(selectedDate, namTkKt);
                    namTk = selectedDate;
                    day = ngay;
                    month = thang;
                    year = selectedYear;

                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }

    private void funcTaoDatePickerDialogKT(String tabTitle, TextView textView) {
        if(tabTitle.equals("Theo ngày")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeDoanhThuActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_day_month_year_picker, null);
            builder.setView(dialogView);

            final NumberPicker dayPicker = dialogView.findViewById(R.id.dayPicker);
            final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
            final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

            // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
            dayPicker.setMinValue(1);
            dayPicker.setMaxValue(31);
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            yearPicker.setMinValue(2019);
            yearPicker.setMaxValue(nam);
            yearPicker.setWrapSelectorWheel(false);

            dayPicker.setValue(dayKt);
            monthPicker.setValue(monthKt + 1);
            yearPicker.setValue(yearKt);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedDay = dayPicker.getValue();
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth, selectedYear);
                    textView.setText(selectedDate);
                    if(selectedDate.equals(ngayTk)){
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu ngày " + selectedDate);
                    }else {
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu từ ngày " + ngayTk + " đến ngày " + selectedDate);
                    }
                    funcLayDanhSachTheoNgay(ngayTk, selectedDate);
//                    dayKt = selectedDay;
//                    monthKt = selectedMonth - 1;
//                    yearKt = selectedYear;
                    ngayTkKt = selectedDate;

                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();

        }else if(tabTitle.equals("Theo tháng")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeDoanhThuActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);
            builder.setView(dialogView);

            final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
            final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

            // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);
            yearPicker.setMinValue(2010);
            yearPicker.setMaxValue(nam);
            yearPicker.setWrapSelectorWheel(false);


            monthPicker.setValue(monthKt + 1);
            yearPicker.setValue(yearKt);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = String.format("%02d/%d", selectedMonth, selectedYear);
                    textView.setText(selectedDate);
                    if(selectedDate.equals(thangTk)){
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu tháng " + selectedDate);
                    }else {
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu từ tháng " + thangTk + " đến tháng " + selectedDate);
                    }
                    thangTkKt= selectedDate;
                    dayKt = ngay;
                    monthKt = selectedMonth - 1;
                    yearKt = selectedYear;
                    LocalDate date = LocalDate.of(yearKt, monthKt, 1);
                    int lastDay = date.minusDays(1).getDayOfMonth();
                    funcLayDanhSachTheoThang(thangTk, selectedDate, String.valueOf(lastDay));


                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();

        }else if(tabTitle.equals("Theo năm")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ThongKeDoanhThuActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_year_picker, null);
            builder.setView(dialogView);

            final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

            // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
            yearPicker.setMinValue(2020);
            yearPicker.setMaxValue(nam);
            yearPicker.setWrapSelectorWheel(false);

            yearPicker.setValue(yearKt);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = String.format("%d",selectedYear);
                    textView.setText(selectedDate);
                    if(selectedDate.equals(namTk)){
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu năm " + selectedDate);
                    }else {
                        binding.tvTKDTMoTa.setText("Báo cáo doanh thu từ năm " + namTk + " đến năm " + selectedDate);
                    }
                    funcLayDanhSachTheoNam(namTk, selectedDate);
                    namTkKt = selectedDate;
                    dayKt = ngay;
                    monthKt = thang;
                    yearKt = selectedYear;

                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
    }
}

class DayComparator implements Comparator<String> {

    @Override
    public int compare(String day1, String day2) {

        LocalDate date1 = parse(day1);
        LocalDate date2 = parse(day2);

        return date1.compareTo(date2);
    }

    private LocalDate parse(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date, formatter);
    }

}



