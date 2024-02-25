package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.KetNoiAdapter;
import com.example.quanlyquanan0609.Class.GiaTriClass;
import com.example.quanlyquanan0609.databinding.ActivityKetNoiBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class KetNoiActivity extends AppCompatActivity {
    ActivityKetNoiBinding binding;
    private static final int PERMISSIONS_REQUEST_CODE = 123;
    ProgressBar progressBar;
    Toolbar toolbar;
    private WifiManager wifiManager;
    private List<ScanResult> wifiScanResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKetNoiBinding.inflate(getLayoutInflater());
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
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID = wifiInfo.getSSID().replace("\"", "");
        funcLayWifiMacDinh();
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            funcLayWifi();
        }

    }

    private void funcLayWifiMacDinh() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GiaTri");
        databaseReference.orderByChild("gt_Ten").equalTo("Wifi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GiaTriClass giaTriClass = dataSnapshot.getValue(GiaTriClass.class);
                    binding.tvTenKN.setText(giaTriClass.getGt_GiaTri());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                funcLayWifi();
            } else {
                // Xử lý khi quyền bị từ chối bởi người dùng
                Toast.makeText(this, "Permission denied. Cannot scan Wi-Fi networks.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void funcLayWifi() {
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = binding.rvKetNoi;
        ArrayList<String> list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        try {
            wifiScanResults = wifiManager.getScanResults();
            String[] wifiNames = new String[wifiScanResults.size()];
            for (int i = 0; i < wifiScanResults.size(); i++) {
                wifiNames[i] = wifiScanResults.get(i).SSID;
                list.add(wifiNames[i]);
            }

            KetNoiAdapter ketNoiAdapter = new KetNoiAdapter(this, list);
            recyclerView.setAdapter(ketNoiAdapter);
            ketNoiAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);


        } catch (SecurityException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Permission denied. Cannot scan Wi-Fi networks.", Toast.LENGTH_SHORT).show();
        }
    }
}