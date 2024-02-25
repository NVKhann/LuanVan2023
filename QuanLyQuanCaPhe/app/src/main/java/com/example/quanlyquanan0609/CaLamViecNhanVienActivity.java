package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.DangKyCaAdapter;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.Class.GiaTriClass;
import com.example.quanlyquanan0609.databinding.ActivityCaLamViecNhanVienBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CaLamViecNhanVienActivity extends AppCompatActivity {
    ActivityCaLamViecNhanVienBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    String spinTuan = "Tuần này";
    Date currentDate = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String tabNgay = dateFormat.format(currentDate);
    String ngaHienTai = dateFormat.format(currentDate);
    Integer viTriNgay;
    DangKyCaAdapter dangKyCaAdapter;
    ArrayList<ChiTietCaClass> list;
    RecyclerView recyclerView;
    private WifiManager wifiManager;
    private static final int PERMISSIONS_REQUEST_CODE = 123;

    String currentSSID;
    public String trangThai = "NO";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaLamViecNhanVienBinding.inflate(getLayoutInflater());
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
        funcHienThiSpinnerTuan();
        binding.spinTuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinTuan = adapterView.getItemAtPosition(i).toString();
                funcHienThiTabLayout(spinTuan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.tlTuan.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ngaHienTai = parseNgayThang(tab.getText().toString());
                viTriNgay = tab.getPosition();
                tabNgay = layNgayTheoSoThuVaTuan(viTriNgay, spinTuan);
                funcHienThiCa(tabNgay);
//                funcLayWifiMacDinh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        funcLayWifiMacDinh();
        funcHienThiCa(ngaHienTai);
    }
    private void funcLayWifiMacDinh() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GiaTri");
        databaseReference.orderByChild("gt_Ten").equalTo("Wifi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String wifi = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GiaTriClass giaTriClass = dataSnapshot.getValue(GiaTriClass.class);
                    wifi = giaTriClass.getGt_GiaTri();
                }
                funcGetWifi(wifi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcGetWifi(String wifi) {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            currentSSID = wifiInfo.getSSID().replace("\"", "");

            String desiredSSID = wifi;
            if (currentSSID.equals(desiredSSID)  || currentSSID.equals("AndroidWifi")) {
                // Kết nối với Wi-Fi mặc định, hiển thị nút "Next"
//                Toast.makeText(CaLamViecNhanVienActivity.this, "Kết nối hiện tại: " +  currentSSID, Toast.LENGTH_SHORT).show();
                trangThai = "OK";
            } else {
                // Không kết nối với Wi-Fi mặc định, ẩn nút "Next"
//                Toast.makeText(CaLamViecNhanVienActivity.this, "Kết nối hiện tại: " +  currentSSID, Toast.LENGTH_SHORT).show();
                trangThai = "NO";
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Xử lý khi quyền bị từ chối bởi người dùng
                Toast.makeText(this, "Permission denied. Cannot scan Wi-Fi networks.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void funcHienThiCa(String tabNgay) {
        recyclerView = binding.rvListCaNhanVien;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        dangKyCaAdapter = new DangKyCaAdapter(this, list);
        recyclerView.setAdapter(dangKyCaAdapter);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId  =firebaseUser.getUid();
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        databaseReference.orderByChild("ct_Ngay").equalTo(tabNgay).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietCaClass = dataSnapshot.getValue(ChiTietCaClass.class);
                    if(chiTietCaClass.getNd_Ma().equals(uId)){
                        list.add(chiTietCaClass);
                    }
                }

                Collections.sort(list, new Comparator<ChiTietCaClass>() {
                    @Override
                    public int compare(ChiTietCaClass chiTietCaClass, ChiTietCaClass t1) {
                        return chiTietCaClass.getCa_Ma().compareTo(t1.getCa_Ma());
                    }
                });
                binding.tvListTrong.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                dangKyCaAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiSpinnerTuan() {
        List<String> items = new ArrayList<>();
        items.add("Tuần này");
        items.add("Tuần sau");
        ArrayAdapter<String> adapterTuan = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapterTuan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinTuan.setAdapter(adapterTuan);

    }
    private void funcHienThiTabLayout(String tuan) {
        List<String> listNgayTrongTuan = new ArrayList<>();
        binding.tlTuan.removeAllTabs();
        if(tuan.equals("Tuần này")){
            listNgayTrongTuan = getCurrentWeekDays();
        }else if (tuan.equals("Tuần sau")){
            listNgayTrongTuan = getNextWeekDays();
        }
        int thu = 2;
        for (String day : listNgayTrongTuan){
            if(thu==8){
                binding.tlTuan.addTab(binding.tlTuan.newTab().setText("Chủ nhât \n" + day));

            }else {
                binding.tlTuan.addTab(binding.tlTuan.newTab().setText("Thứ " + thu + "\n" + day));

            }
            thu ++;
        }
        if(tuan.equals("Tuần này")){
            int currentDayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
            currentDayOfWeek = (currentDayOfWeek + 5) % 7;
            binding.tlTuan.getTabAt(currentDayOfWeek).select();
        }else {
            binding.tlTuan.getTabAt(0).select();
        }

    }
    public static String layNgayTheoSoThuVaTuan(int soThu, String tuan) {
        Calendar calendar = Calendar.getInstance();
        if (tuan.equals("Tuần trước")) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (tuan.equals("Tuần sau")) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        // Đặt lịch để bắt đầu từ Thứ 2 của tuần được xác định
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Thêm số ngày tương ứng với số thứ (0 đến 6)
        calendar.add(Calendar.DAY_OF_WEEK, soThu);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date ngay = calendar.getTime();
        return sdf.format(ngay);
    }
    public static String parseNgayThang(String input) {
        String[] parts = input.split("\n");
        if (parts.length != 2) {
            return null;
        }

        String ngayThang = parts[1].trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = dateFormat.parse(ngayThang + "/" + 2023);
            return dateFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
    }
    public static List<String> getCurrentWeekDays() {
        List<String> weekDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Đặt lịch vào ngày hiện tại
        calendar.setTime(new Date());

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // Giảm ngày đi 7 ngày để lấy ngày đầu tiên của tuần trước
            calendar.add(Calendar.DAY_OF_MONTH, -7);
        }
        // Đặt lịch để bắt đầu từ ngày Monday (2)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            // Lấy ngày và định dạng nó thành chuỗi
            String day = sdf.format(calendar.getTime());
            weekDays.add(day);

            // Tăng ngày lên một để lấy ngày tiếp theo trong tuần
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weekDays;
    }
    public static List<String> getNextWeekDays() {
        List<String> weekDays = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Đặt lịch vào ngày hiện tại
        calendar.setTime(new Date());
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // Giảm ngày đi 7 ngày để lấy ngày đầu tiên của tuần trước
            calendar.add(Calendar.DAY_OF_MONTH, -7);
        }
        // Tăng ngày lên 7 để lấy ngày đầu tiên của tuần sau
        calendar.add(Calendar.DAY_OF_MONTH, 7);

        // Đặt lịch để bắt đầu từ ngày Monday (2)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            // Lấy ngày và định dạng nó thành chuỗi
            String day = sdf.format(calendar.getTime());
            weekDays.add(day);

            // Tăng ngày lên một để lấy ngày tiếp theo trong tuần
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return weekDays;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_them, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuThem){
            Intent intent = new Intent(CaLamViecNhanVienActivity.this, ChiTietCaActivity.class);
            intent.putExtra("TrangThai", "DangKy");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcHienThiCa(tabNgay);
//        funcLayWifiMacDinh();

    }
}