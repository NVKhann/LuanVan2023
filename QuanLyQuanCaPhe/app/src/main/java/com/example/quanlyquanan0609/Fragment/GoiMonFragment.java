package com.example.quanlyquanan0609.Fragment;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;
import static androidx.core.content.ContextCompat.getColor;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.GoiMonAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.Interface.OnItemClickListener;
import com.example.quanlyquanan0609.MyApplication;
import com.example.quanlyquanan0609.R;
import com.example.quanlyquanan0609.databinding.FragmentGoiMonBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GoiMonFragment extends Fragment {
    FragmentGoiMonBinding binding;
    View rootView;
    TabLayout tabLayout;
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<BanAnClass> listBanAn;
    GoiMonAdapter goiMonAdapter;
    String action = "HienThi", tenBanTu, tenBanDen, titleToolbar;
    Integer kvMa, maBanTu, maBanDen;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoiMonBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();
        toolbar = binding.toolbarGoiMon;
        progressBar = binding.progressBar;
        tabLayout = binding.tabLayout;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        funcHienThiKhuVuc();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tenKhuVuc = tab.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
                databaseReference.orderByChild("kv_Ten").equalTo(tenKhuVuc).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Integer maKhuVuc = dataSnapshot.child("kv_Ma").getValue(Integer.class);
                            kvMa = maKhuVuc;
                            progressBar.setVisibility(View.VISIBLE);
                            if(action.equals("HienThi")){
                                funcHienThiBanTheoKhuVuc(maKhuVuc, "Tất cả", "Hiển thị");
                            }else if(action.equals("ChuyenBan")) {
                                funcHienThiBanCoKhachTheoKhuVuc(maKhuVuc, "Có khách", "Chuyển từ");
                            }else {
                                funcHienThiBanTheoKhuVuc(maKhuVuc, "Tất cả", "Chuyển đến");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return rootView;
    }


    private void funcHienThiKhuVuc() {
        ArrayList<KhuVucClass> listKhuVuc = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("KhuVuc").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    if(!khuVucClass.getKv_TrangThai().equals("Ẩn")){
                        listKhuVuc.add(khuVucClass);
                        tabLayout.addTab(tabLayout.newTab().setText(khuVucClass.getKv_Ten()));
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void funcHienThiBanCoKhachTheoKhuVuc(Integer maKhuVuc, String hienThi, String act) {
        recyclerView = binding.rvBanGM;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        listBanAn = new ArrayList<>();
        goiMonAdapter = new GoiMonAdapter(getActivity(), listBanAn, toolbar, hienThi, act);
        goiMonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Integer maBan) {
                action = "ChuyenBanDen";
                maBanTu = listBanAn.get(position).getBan_Ma();
                tenBanTu = listBanAn.get(position).getBan_Ten();
                toolbar.setTitle(tenBanTu + "\u2192");
                titleToolbar = (String) toolbar.getTitle();
                funcHienThiBanTheoKhuVuc(maKhuVuc, "Tất cả", "Chuyển đến");
            }
        });
        recyclerView.setAdapter(goiMonAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("kv_Ma").equalTo(maKhuVuc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listBanAn != null){
                    listBanAn.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banClass = dataSnapshot.getValue(BanAnClass.class);
                    listBanAn.add(banClass);
                }
                goiMonAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                binding.tvKvTrong.setVisibility(listBanAn.isEmpty() ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiBanTheoKhuVuc(Integer maKhuVuc, String hienThi, String act) {
        recyclerView = binding.rvBanGM;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        listBanAn = new ArrayList<>();
        goiMonAdapter = new GoiMonAdapter(getActivity(), listBanAn, toolbar, hienThi, act);
        goiMonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Integer maBan) {
                maBanDen = listBanAn.get(position).getBan_Ma();
                tenBanDen = listBanAn.get(position).getBan_Ten();
                toolbar.setTitle( titleToolbar + " " + tenBanDen);
                if(act.equals("Chuyển đến")){
                    funcHienThiDialogXacNhan();
                }
            }
        });
        recyclerView.setAdapter(goiMonAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("kv_Ma").equalTo(maKhuVuc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listBanAn != null){
                    listBanAn.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banClass = dataSnapshot.getValue(BanAnClass.class);
                    listBanAn.add(banClass);
                }
                goiMonAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                binding.tvKvTrong.setVisibility(listBanAn.isEmpty() ? View.VISIBLE : View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiDialogXacNhan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_0_edit_text, null);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        TextView tvTitle, tvNoiDung;
        Button btnOk, btnHuy;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        tvTitle.setText("Xác nhận");
        tvNoiDung.setText("Chuyển từ " + tenBanTu + " sang " + tenBanDen);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcKiemTranBan();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void funcKiemTranBan() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(maBanDen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean coKhach = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1")){
                        coKhach = true;
                        break;
                    }
                }
                if(coKhach){
                    funcLayPgmTu();
                }else {
                    funcCapNhatPgmBanTrong();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcCapNhatPgmBanCoKhach(Integer maPgmCu, Integer maPgmMoi) {
        progressBar.setVisibility(View.VISIBLE);
        if(maPgmCu != 0 && maPgmMoi != 0){
            funcCapNhatPgmHoaDon(maPgmCu);
            funcCapNhatChiTietPgm(maPgmCu, maPgmMoi);
            funcHienThiBanTheoKhuVuc(kvMa, "Tất cả", "Hiển thị");
            toolbar.setTitle("Gọi món");
            action = "HienThi";
            invalidateOptionsMenu(getActivity());
            Toast.makeText(getActivity(), "Chuyển bàn thành công", Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void funcCapNhatChiTietPgm(Integer maPgmTu, Integer maPgmDen) {
        DatabaseReference dataChiTietPgm = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        dataChiTietPgm.orderByChild("pgm_Ma").equalTo(maPgmDen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietPGMClass chiTietPGMDen = dataSnapshot.getValue(ChiTietPGMClass.class);
                    dataChiTietPgm.orderByChild("pgm_Ma").equalTo(maPgmTu).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                ChiTietPGMClass chiTietPGMTu = dataSnapshot.getValue(ChiTietPGMClass.class);
                                if(!chiTietPGMTu.getMon_Ma().equals(chiTietPGMDen.getMon_Ma())){
                                    String maCT = chiTietPGMTu.getPgm_Ma() + "_" + chiTietPGMTu.getMon_Ma();
                                    dataChiTietPgm.child(maCT).child("pgm_Ma").setValue(maPgmDen);
                                }else {
                                    String maCTTu = chiTietPGMTu.getPgm_Ma() + "_" + chiTietPGMTu.getMon_Ma();
                                    String maCTDen = chiTietPGMDen.getPgm_Ma() + "_" + chiTietPGMDen.getMon_Ma();
                                    String ghiChuTu = chiTietPGMTu.getCt_GhiChu();
                                    String ghiChuDen = chiTietPGMDen.getCt_GhiChu();
                                    if(!ghiChuTu.equals("") && !ghiChuDen.equals("")){
                                        String ghiChu = ghiChuDen + " ," + ghiChuTu;
                                        dataChiTietPgm.child(maCTDen).child("ct_GhiChu").setValue(ghiChu);
                                    }else if (!ghiChuTu.equals("") && ghiChuDen.equals("")){
                                        dataChiTietPgm.child(maCTDen).child("ct_GhiChu").setValue(ghiChuTu);
                                    }
                                    Integer sl = chiTietPGMDen.getCt_SoLuong() + chiTietPGMTu.getCt_SoLuong();
                                    dataChiTietPgm.child(maCTDen).child("ct_SoLuong").setValue(sl);
                                    dataChiTietPgm.child(maCTTu).removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcCapNhatPgmHoaDon(Integer maPgmCu) {
        DatabaseReference dataPgm = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        dataPgm.orderByChild("pgm_Ma").equalTo(maPgmCu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1")){
                        dataPgm.child(String.valueOf(pgmClass.getPgm_Ma())).child("pgm_TrangThai").setValue("0");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dataHoaDon.orderByChild("pgm_Ma").equalTo(maPgmCu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    dataHoaDon.child(String.valueOf(hoaDonClass.getHd_Ma())).child("hd_TrangThai").setValue("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void funcLayPgmTu() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(maBanTu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maPgmCu = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1")){
                        maPgmCu = pgmClass.getPgm_Ma();
                    }
                }
                funcLayPgmDen(maPgmCu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayPgmDen(Integer maPgmCu) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(maBanDen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maPgmMoi = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1")){
                        maPgmMoi = pgmClass.getPgm_Ma();
                    }
                }
                funcCapNhatPgmBanCoKhach(maPgmCu, maPgmMoi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcCapNhatPgmBanTrong() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(maBanTu).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if (pgmClass.getPgm_TrangThai().equals("1")){
                        progressBar.setVisibility(View.VISIBLE);
                        databaseReference.child(String.valueOf(pgmClass.getPgm_Ma())).child("ban_Ma").setValue(maBanDen).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    funcHienThiBanTheoKhuVuc(kvMa, "Tất cả", "Hiển thị");
                                    toolbar.setTitle("Gọi món");
                                    action = "HienThi";
                                    invalidateOptionsMenu(getActivity());
                                    Toast.makeText(getActivity(), "Chuyển bàn thành công", Toast.LENGTH_SHORT).show();
                                }else {
                                    try {
                                        throw task.getException();
                                    }catch (Exception e){
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_goi_mon, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuGoiMon){
            funcHienThiBotDialog();
        }else if(item.getItemId() == R.id.menuGoiMonHuy){
            funcHienThiBanTheoKhuVuc(kvMa, "Tất cả", "Hiển thị");
            toolbar.setTitle("Gọi món");
            action = "HienThi";
            invalidateOptionsMenu(getActivity());
            Toast.makeText(getActivity(), "Hủy chuyển bàn", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if(action.equals("ChuyenBan")){
            menu.findItem(R.id.menuGoiMonHuy).setVisible(true);
            menu.findItem(R.id.menuGoiMon).setVisible(false);
        } else if (action.equals("HienThi")) {
            menu.findItem(R.id.menuGoiMonCheck).setVisible(false);
            menu.findItem(R.id.menuGoiMonHuy).setVisible(false);
            menu.findItem(R.id.menuGoiMon).setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void funcHienThiBotDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = getLayoutInflater().inflate(R.layout.bot_dialog, null, false);
        bottomSheetDialog.setContentView(view);
        TextView tvChuyenBan = view.findViewById(R.id.sdTvSua);
        TextView tvAn = view.findViewById(R.id.sdTvXoa);
        tvAn.setVisibility(View.GONE);
        tvChuyenBan.setText("Chuyển bàn");

        tvChuyenBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = "ChuyenBan";
                funcHienThiBanCoKhachTheoKhuVuc(kvMa, "Có khách", "Chuyển từ");
                toolbar.setTitle("Chuyển bàn");
                invalidateOptionsMenu(getActivity());
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
}