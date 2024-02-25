package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.Fragment.CaNhanFragment;
import com.example.quanlyquanan0609.Fragment.CaiDatFragment;
import com.example.quanlyquanan0609.Fragment.GoiMonFragment;
import com.example.quanlyquanan0609.Fragment.QuanLyFragment;
import com.example.quanlyquanan0609.Fragment.TrangChuFragment;
import com.example.quanlyquanan0609.databinding.ActivityMainNhanVienBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainNhanVienActivity extends AppCompatActivity {
    ActivityMainNhanVienBinding binding;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainNhanVienBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        funcDoiFragment(new GoiMonFragment());

        binding.bottomAppBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menuBotGoiMon) {
                    funcDoiFragment(new GoiMonFragment());
                } else if (id == R.id.menuBotCaNhan) {
                    funcDoiFragment(new CaNhanFragment());
                }else {
                    funcDoiFragment(new CaiDatFragment());
                }
                return true;
            }
        });
        funcDataChange();
        funcHoaDonDataChange();
    }

    private void funcHoaDonDataChange() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HoaDonClass hoaDonClass = snapshot.getValue(HoaDonClass.class);
                if(hoaDonClass.getHd_TrangThai().equals("0") && hoaDonClass.getHd_HinhThuc().equals("Chuyển khoản")){
                    funcLayPGMTheoHoaDon(hoaDonClass.getPgm_Ma());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcDataChange() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                PGMClass pgmClass = snapshot.getValue(PGMClass.class);
                funcLayNguoiDung(pgmClass.getNd_Ma(),pgmClass.getBan_Ma());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                PGMClass pgmClass = snapshot.getValue(PGMClass.class);
                if(pgmClass.getPgm_TrangThai().equals("2")){
                    funcLayThongTin(pgmClass.getBan_Ma(), "Thanh toán");
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayNguoiDung(String ndMa, Integer banMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByChild("nd_Ma").equalTo(ndMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ndQuyen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    ndQuyen = nguoiDungClass.getNd_Quyen();
                }

                if(!ndQuyen.equals(null) && ndQuyen.equals("Khách hàng")){
                    funcLayThongTin(banMa, "Gọi món");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayPGMTheoHoaDon(Integer pgmMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer banMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    Log.i("ThongBao", "BanMa" + pgmClass.getBan_Ma());
                    banMa = pgmClass.getBan_Ma();
                }
                funcLayThongTin(banMa, "Thanh toán thành công");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayThongTin(Integer banMa, String act) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ma").equalTo(banMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                String banTen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    banTen = banAnClass.getBan_Ten();
                    kvMa = banAnClass.getKv_Ma();
                }
                funcLayKhuVuc(banTen, kvMa, act);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayKhuVuc(String banTen, Integer kvMa, String act) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String kvTen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvTen = khuVucClass.getKv_Ten();
                }
                funcGuiThongBao(banTen, kvTen, act);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcGuiThongBao(String banTen, String kvTen, String act) {
        String moTa = "Thông báo";
        if(act.equals("Thanh toán")){
            moTa = "Có yêu cầu thanh toán từ " + kvTen + " - " + banTen;
        }else if(act.equals("Gọi món")){
            moTa = "Có yêu cầu gọi món từ " + kvTen + " - " + banTen;
        } else if(act.equals("Thanh toán thành công")){
            moTa = kvTen + " - " + banTen + " đã thanh toán bằng ZaloPay";
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(act)
                .setContentText(moTa)
                .setSmallIcon(R.drawable.logo_notification)
                .setColor(getResources().getColor(R.color.bg_color))
                .setLargeIcon(bitmap)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager != null){
            int notificationId = funcGetNotifiId();
            notificationManager.notify(notificationId, notification);

            // Lưu trữ notificationId vào danh sách
            addNotificationId(notificationId);

            // Kiểm tra và xóa thông báo cũ khi danh sách vượt quá 10 phần tử
            if (notificationIds.size() > 10) {
                int oldNotificationId = notificationIds.remove(0); // Lấy ra notificationId cũ nhất
                notificationManager.cancel(oldNotificationId); // Xóa thông báo từ NotificationManager
            }
        }
    }
    private List<Integer> notificationIds = new ArrayList<>();

    private void addNotificationId(int notificationId) {
        notificationIds.add(notificationId);
    }

    private int funcGetNotifiId() {
        int newId = (int) new Date().getTime();
        return newId;
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }


    private void funcDoiFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();

    }
}