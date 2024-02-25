package com.example.quanlyquanan0609.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.CaLamViecNhanVienActivity;
import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.Class.GiaTriClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.DiemDanhActivity;
import com.example.quanlyquanan0609.Listener.DangKyCaListener;
import com.example.quanlyquanan0609.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class DangKyCaAdapter extends RecyclerView.Adapter<DangKyCaAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ChiTietCaClass> list;
    private WifiManager wifiManager;
    private static final int PERMISSIONS_REQUEST_CODE = 123;
    String currentSSID;
//    String trangThai = "NO";

    public DangKyCaAdapter(Context context, ArrayList<ChiTietCaClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_ca_nhan_vien, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChiTietCaClass chiTietCaClass = list.get(position);
        funcLayTenKV(chiTietCaClass.getKv_Ma(), holder.tvKhuVuc);
        funcLayTenCa(chiTietCaClass.getCa_Ma(), holder.tvTenCa);
        funcHienThiNhanCa(chiTietCaClass, holder.tvVao, holder.tvRa);
        funcOnClickCa(chiTietCaClass, holder.tvVao, holder.tvRa);
        funcHienThiTrangThaiCa(chiTietCaClass, holder.tvVao, holder.tvRa);

        holder.lnCaNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!chiTietCaClass.getCt_VaoCa().equals("")){
                    funcTaoDialogThongTinCa(chiTietCaClass);

                }else {
                    funcTaoBotdialog(chiTietCaClass);
                }
            }
        });
    }

    private void funcHienThiTrangThaiCa(ChiTietCaClass chiTietCaClass, TextView tvVao, TextView tvRa) {
       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
       databaseReference.orderByChild("ca_Ma").equalTo(chiTietCaClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                   CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                   LocalTime start = LocalTime.parse(caLamViecClass.getCa_GioDB());
                   LocalTime end = LocalTime.parse(caLamViecClass.getCa_GioKT());

                   if(!chiTietCaClass.getCt_VaoCa().equals("")){
                       String gioDki = chiTietCaClass.getCt_VaoCa().substring(chiTietCaClass.getCt_VaoCa().length() - 5);
                       String ngayDKiVao = chiTietCaClass.getCt_VaoCa().substring(0, chiTietCaClass.getCt_VaoCa().length() - 6);
                       LocalTime gioBDDK = LocalTime.parse(gioDki);
                       long phutVao = funcTinhPhut(start, gioBDDK);
                       if(phutVao >= -5 && phutVao <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiVao)){
                           tvVao.setTextColor(context.getColor(R.color.dark_green));
                           Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_green);
                           tvVao.setBackground(drawable);
                       }else {
                           tvVao.setTextColor(Color.RED);
                           Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_red);
                           tvVao.setBackground(drawable);
                       }
                   }

                   if(!chiTietCaClass.getCt_RaCa().equals("")) {
                       String gioDki = chiTietCaClass.getCt_RaCa().substring(chiTietCaClass.getCt_RaCa().length() - 5);
                       String ngayDKiRa = chiTietCaClass.getCt_RaCa().substring(0, chiTietCaClass.getCt_RaCa().length() - 6);
                       LocalTime gioKTDK = LocalTime.parse(gioDki);
                       long phutRa = funcTinhPhut(end, gioKTDK);
                       if(phutRa >= -5 && phutRa <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiRa)){
                           tvRa.setTextColor(context.getColor(R.color.dark_green));
                           Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_green);
                           tvRa.setBackground(drawable);
                       }else {
                           tvRa.setTextColor(Color.RED);
                           Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_red);
                           tvRa.setBackground(drawable);
                       }
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }

    private void funcOnClickCa(ChiTietCaClass chiTietCaClass, TextView tvVao, TextView tvRa) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String key = chiTietCaClass.getCt_Ngay().replaceAll("/", "") + chiTietCaClass.getKv_Ma() + chiTietCaClass.getCa_Ma() + uId.substring(uId.length()-3);
        databaseReference.orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietCaClass1 = dataSnapshot.getValue(ChiTietCaClass.class);
                    if(chiTietCaClass1.getCt_VaoCa().equals("")){
                        tvVao.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                funcLayWifiMacDinh(chiTietCaClass, tvVao, "Vao");
//                                funcTaoDialogVaoCa(chiTietCaClass, tvVao, "Vao");
                                Intent intent = new Intent(context, DiemDanhActivity.class);
                                intent.putExtra("TrangThai", "VaoCa");
                                intent.putExtra("Key", key);
                                intent.putExtra("NgayDki", chiTietCaClass1.getCt_Ngay());
                                context.startActivity(intent);
                            }
                        });
                    }else if (chiTietCaClass1.getCt_RaCa().equals("")){
                        tvRa.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                funcLayWifiMacDinh(chiTietCaClass, tvRa, "Ra");
//                                funcTaoDialogVaoCa(chiTietCaClass, tvRa, "Ra");
                                Intent intent = new Intent(context, DiemDanhActivity.class);
                                intent.putExtra("TrangThai", "RaCa");
                                intent.putExtra("Key", key);
                                intent.putExtra("NgayDki", chiTietCaClass1.getCt_Ngay());
                                context.startActivity(intent);

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


    private void funcHienThiNhanCa(ChiTietCaClass chiTietCaClass, TextView tvVao, TextView tvRa) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String key = chiTietCaClass.getCt_Ngay().replaceAll("/", "") + chiTietCaClass.getKv_Ma() + chiTietCaClass.getCa_Ma() + uId.substring(uId.length()-3);
        databaseReference.orderByKey().equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietCaClass1= dataSnapshot.getValue(ChiTietCaClass.class);
                    if(chiTietCaClass1.getCt_VaoCa().equals("")){
                        tvRa.setEnabled(false);
                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_gray);
                        tvRa.setBackground(drawable);
                    }else if(chiTietCaClass1.getCt_RaCa().equals("")) {
                        tvVao.setText(chiTietCaClass1.getCt_VaoCa().substring(chiTietCaClass1.getCt_VaoCa().length() - 6));
                        tvRa.setEnabled(true);
                    }else {
                        tvVao.setText(chiTietCaClass1.getCt_VaoCa().substring(chiTietCaClass1.getCt_VaoCa().length() - 6));
                        tvRa.setText(chiTietCaClass1.getCt_RaCa().substring(chiTietCaClass1.getCt_RaCa().length() - 6));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayWifiMacDinh(ChiTietCaClass chiTietCaClass, TextView textView, String act) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GiaTri");
        databaseReference.orderByChild("gt_Ten").equalTo("Wifi").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String wifi = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GiaTriClass giaTriClass = dataSnapshot.getValue(GiaTriClass.class);
                    wifi = giaTriClass.getGt_GiaTri();
                }
                funcGetWifi(wifi, chiTietCaClass, textView, act);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcGetWifi(String wifi, ChiTietCaClass chiTietCaClass, TextView textView, String act) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
        } else {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            currentSSID = wifiInfo.getSSID().replace("\"", "");
            String wifiHienTai = currentSSID;
            String desiredSSID = wifi;
            if (currentSSID.equals(desiredSSID)  || currentSSID.equals("AndroidWifi")) {
                // Kết nối với Wi-Fi mặc định, hiển thị nút "Next"
//                Toast.makeText(CaLamViecNhanVienActivity.this, "Kết nối hiện tại: " +  currentSSID, Toast.LENGTH_SHORT).show();
//                trangThai = "OK";
                funcTaoDialogVaoCa(chiTietCaClass, textView, act, wifiHienTai, wifi, "OK");
            } else {
                // Không kết nối với Wi-Fi mặc định, ẩn nút "Next"
//                Toast.makeText(CaLamViecNhanVienActivity.this, "Kết nối hiện tại: " +  currentSSID, Toast.LENGTH_SHORT).show();
//                trangThai = "NO";
                funcTaoDialogVaoCa(chiTietCaClass, textView, act, wifiHienTai, wifi, "NO");
            }
        }
    }

    private void funcTaoDialogVaoCa(ChiTietCaClass chiTietCaClass, TextView textView, String act, String wifiHienTai, String wifiMacDinh, String trangThai) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_ca, null);
        TextView tvTitle, tvNoiDung, tvLoi;
        Button btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        tvLoi = view.findViewById(R.id.tvLoi);
        btnOk = view.findViewById(R.id.btnOk);
        tvNoiDung.setText(wifiHienTai);
        if(!trangThai.equals("OK")){
            tvLoi.setVisibility(View.VISIBLE);
            tvLoi.setText("Kết nối không hợp lệ (Yêu cầu: " + wifiMacDinh + ")" );
            btnOk.setEnabled(false);
            btnOk.setTextColor(Color.GRAY);
        }
        if(act.equals("Vao")){
            tvTitle.setText("Vào ca");
        }else {
            tvTitle.setText("Ra ca");
        }
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(act.equals("Vao")){
                   funcVaoCa(chiTietCaClass, textView);
                    alertDialog.dismiss();
                }else {
                    funcRaCa(chiTietCaClass, textView);
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    private void funcRaCa(ChiTietCaClass chiTietCaClass, TextView textView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String key = chiTietCaClass.getCt_Ngay().replaceAll("/", "") + chiTietCaClass.getKv_Ma() + chiTietCaClass.getCa_Ma() + uId.substring(uId.length()-3);
        String gioHienTai = funcLayGio();
        databaseReference.child(key).child("ct_RaCa").setValue(gioHienTai).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    textView.setText(gioHienTai.substring(gioHienTai.length() - 6));
                    Toast.makeText(context, "Ra ca thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void funcVaoCa(ChiTietCaClass chiTietCaClass, TextView textView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String key = chiTietCaClass.getCt_Ngay().replaceAll("/", "") + chiTietCaClass.getKv_Ma() + chiTietCaClass.getCa_Ma() + uId.substring(uId.length()-3);
        String gioHienTai = funcLayGio();
        databaseReference.child(key).child("ct_VaoCa").setValue(gioHienTai).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    textView.setText(gioHienTai.substring(gioHienTai.length() - 6));
                    Toast.makeText(context, "Vào ca thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static long funcTinhPhut(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes();
    }
    public String funcLayGio() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }

    private void funcTaoBotdialog(ChiTietCaClass chiTietCaClass) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bot_dialog, null, false);
        TextView tvBotSua, tvBotXoa;
        tvBotSua = view.findViewById(R.id.sdTvSua);
        tvBotXoa = view.findViewById(R.id.sdTvXoa);
        tvBotXoa.setText("Xóa ca làm việc");
        tvBotSua.setText("Thông tin ca làm việc");
        tvBotSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogThongTinCa(chiTietCaClass);
                bottomSheetDialog.dismiss();
            }
        });
        tvBotXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogXacNhan(chiTietCaClass);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void funcTaoDialogThongTinCa(ChiTietCaClass chiTietCaClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_hien_thi_chi_tiet_ca_nhan_vien, null);
        LinearLayout lnVao, lnVaoTT, lnRa, lnRaTT;
        TextView tvNgay, tvKhuVuc, tvTenCa, tvThoiGian, tvVaoCa, tvVaoCaTT, tvRaCa, tvRaCaTT;
        Button btnOK;
        lnVao = view.findViewById(R.id.lnTTCTCVao);
        lnVaoTT = view.findViewById(R.id.lnTTCTCVaoTT);
        lnRa = view.findViewById(R.id.lnTTCTCRa);
        lnRaTT = view.findViewById(R.id.lnTTCTCRaTT);
        tvNgay = view.findViewById(R.id.tvTTCTCNgay);
        tvKhuVuc = view.findViewById(R.id.tvTTCTCKhuVuc);
        tvTenCa = view.findViewById(R.id.tvTTCTCCa);
        tvThoiGian = view.findViewById(R.id.tvTTCTCTG);
        tvVaoCa = view.findViewById(R.id.tvTTCTCVaoCa);
        tvVaoCaTT = view.findViewById(R.id.tvTTCTCVaoCaTT);
        tvRaCa = view.findViewById(R.id.tvTTCTCRaCa);
        tvRaCaTT = view.findViewById(R.id.tvTTCTCRaCaTT);
        btnOK = view.findViewById(R.id.btnOk);

        tvNgay.setText(chiTietCaClass.getCt_Ngay());

        if(chiTietCaClass.getCt_VaoCa().equals("")){
            lnVao.setVisibility(View.GONE);
            lnVaoTT.setVisibility(View.GONE);
        }else {
            tvVaoCa.setText(chiTietCaClass.getCt_VaoCa());
            funcHienThiTrangThaiTv(chiTietCaClass, tvVaoCaTT, "Vao");
        }

        if(chiTietCaClass.getCt_RaCa().equals("")){
            lnRa.setVisibility(View.GONE);
            lnRaTT.setVisibility(View.GONE);
        }else {
            tvRaCa.setText(chiTietCaClass.getCt_RaCa());
            funcHienThiTrangThaiTv(chiTietCaClass, tvRaCaTT, "Ra");

        }

        funcLayTenKV(chiTietCaClass.getKv_Ma(), tvKhuVuc);
        funcLayTenCa(chiTietCaClass.getCa_Ma(), tvTenCa);
        funcLayThoiGian(chiTietCaClass.getCa_Ma(), tvThoiGian);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void funcHienThiTrangThaiTv(ChiTietCaClass chiTietCaClass, TextView textView, String act) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(chiTietCaClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    LocalTime start = LocalTime.parse(caLamViecClass.getCa_GioDB());
                    LocalTime end = LocalTime.parse(caLamViecClass.getCa_GioKT());

                    if(!chiTietCaClass.getCt_VaoCa().equals("") && act.equals("Vao")){
                        String gioDki = chiTietCaClass.getCt_VaoCa().substring(chiTietCaClass.getCt_VaoCa().length() - 5);
                        String ngayDKiVao = chiTietCaClass.getCt_VaoCa().substring(0, chiTietCaClass.getCt_VaoCa().length() - 6);
                        LocalTime gioBDDK = LocalTime.parse(gioDki);
                        long phutVao = funcTinhPhut(start, gioBDDK);
                        if(phutVao >= -5 && phutVao <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiVao)){
                            textView.setText("Hợp lệ");
                        }else {
                            textView.setText("Không hợp lệ");
                        }
                    }

                    if(!chiTietCaClass.getCt_RaCa().equals("") && act.equals("Ra")) {
                        String gioDki = chiTietCaClass.getCt_RaCa().substring(chiTietCaClass.getCt_RaCa().length() - 5);
                        String ngayDKiRa = chiTietCaClass.getCt_RaCa().substring(0, chiTietCaClass.getCt_RaCa().length() - 6);
                        LocalTime gioKTDK = LocalTime.parse(gioDki);
                        long phutRa = funcTinhPhut(end, gioKTDK);
                        if(phutRa >= -5 && phutRa <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiRa)){
                            textView.setText("Hợp lệ");
                        }else {
                            textView.setText("Không hợp lệ");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayThoiGian(Integer caMa, TextView tvThoiGian) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(caMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    tvThoiGian.setText(caLamViecClass.getCa_GioDB() + " ~ " + caLamViecClass.getCa_GioKT());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDialogXacNhan(ChiTietCaClass chiTietCaClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnOk, btnHuy;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        tvTitle.setText("Xác nhận");
        tvNoiDung.setText("Xác nhận xóa ca làm việc này ?");
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
                funcXoaDangKyCaLamViec(chiTietCaClass, alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcXoaDangKyCaLamViec(ChiTietCaClass chiTietCaClass, AlertDialog alertDialog) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String key = chiTietCaClass.getCt_Ngay().replaceAll("/", "") + chiTietCaClass.getKv_Ma() + chiTietCaClass.getCa_Ma() + uId.substring(uId.length()-3);
        databaseReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    alertDialog.dismiss();
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void funcLayTenCa(Integer caMa, TextView tvTenCa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(caMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    tvTenCa.setText(caLamViecClass.getCa_Ten());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayTenKV(Integer kvMa, TextView tvKhuVuc) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    tvKhuVuc.setText(khuVucClass.getKv_Ten());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout lnCaNV;
        public TextView tvKhuVuc, tvTenCa, tvVao, tvRa;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lnCaNV = itemView.findViewById(R.id.lnItemListCaNV);
            tvKhuVuc = itemView.findViewById(R.id.tvItemListCaNVKhuVuc);
            tvTenCa = itemView.findViewById(R.id.tvItemListCaNVTen);
            tvVao = itemView.findViewById(R.id.tvItemListCaNVVao);
            tvRa = itemView.findViewById(R.id.tvItemListCaNVRa);
        }
    }
}
