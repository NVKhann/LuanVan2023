package com.example.quanlyquanan0609.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quanlyquanan0609.Adapter.KhuyenMaiTrangChuAdapter;
import com.example.quanlyquanan0609.Adapter.MonAnAdapter;
import com.example.quanlyquanan0609.Adapter.MonAnTrangChuAdapter;
import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.R;
import com.example.quanlyquanan0609.databinding.FragmentGoiMonBinding;
import com.example.quanlyquanan0609.databinding.FragmentTrangChuBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TrangChuFragment extends Fragment {
    FragmentTrangChuBinding binding;
    View rootView;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    String ngayHienTai = funcLayNgay();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTrangChuBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        funcHienThiKhuyenMai();
        funcHienThiMon();
        return rootView;
    }

    private void funcHienThiMon() {
        RecyclerView recyclerView = binding.rvMonAnTrangChu;
        ArrayList<MonAnClass> list = new ArrayList<>();
        MonAnTrangChuAdapter monAnTrangChuAdapter = new MonAnTrangChuAdapter(getActivity(), list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(monAnTrangChuAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.orderByKey().limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MonAnClass monAnClass = dataSnapshot.getValue(MonAnClass.class);
                    list.add(monAnClass);
                }
                Collections.sort(list, new Comparator<MonAnClass>() {
                    @Override
                    public int compare(MonAnClass monAnClass, MonAnClass t1) {
                        return t1.getMon_Ma().compareTo(monAnClass.getMon_Ma());
                    }
                });
                monAnTrangChuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiKhuyenMai() {
        RecyclerView recyclerView = binding.rvKhuyMaiTrangChu;
        ArrayList<KhuyenMaiClass> list = new ArrayList<>();
        KhuyenMaiTrangChuAdapter khuyenMaiAdapter = new KhuyenMaiTrangChuAdapter(getActivity(), list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(khuyenMaiAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuyenMaiClass khuyenMaiClass = dataSnapshot.getValue(KhuyenMaiClass.class);
                    try {
                        Date ngayHt = format.parse(ngayHienTai);
                        Date ngayBd = format.parse(khuyenMaiClass.getKm_NgayBd());
                        Date ngayKt = format.parse(khuyenMaiClass.getKm_NgayKt());
                        if(ngayHt.equals(ngayBd) || ngayHt.equals(ngayKt) || ngayHt.before(ngayBd) || ngayHt.after(ngayBd) && ngayHt.before(ngayKt)){
                            list.add(khuyenMaiClass);
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                khuyenMaiAdapter.notifyDataSetChanged();
                binding.lnKhuyenMaiTrangChu.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
                binding.lnImg.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String funcLayNgay() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }
}