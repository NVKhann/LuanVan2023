package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.DonGiaClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.R;
import com.example.quanlyquanan0609.ThemMonAnActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MonAnAdapter extends RecyclerView.Adapter<MonAnAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<MonAnClass> listMonAn;
    private Integer maBanNguon;

    public MonAnAdapter(Context context, ArrayList<MonAnClass> listMonAn, Integer maBanNguon) {
        this.context = context;
        this.listMonAn = listMonAn;
        this.maBanNguon = maBanNguon;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MonAnClass monAnClass = listMonAn.get(position);
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        holder.tvTenMon.setText(monAnClass.getMon_Ten());
        if(!monAnClass.getMon_HinhAnh().equals("Đường dẫn")){
            Glide.with(holder.itemView.getContext()).load(monAnClass.getMon_HinhAnh()).apply(new RequestOptions().centerCrop()).into(holder.ivHinhMon);
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DonGia");
        databaseReference.orderByChild("mon_Ma").equalTo(monAnClass.getMon_Ma()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gia = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DonGiaClass donGiaClass = dataSnapshot.getValue(DonGiaClass.class);
                    gia = Integer.valueOf(donGiaClass.getDg_Gia());
                }
                String giaF = numberFormat.format(gia);
                if(gia!=null){
                    holder.tvGiaMon.setText(giaF);

                }else {
                    holder.tvGiaMon.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.lnMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(maBanNguon!=0){
                    funcTaoDialogThemPgm(monAnClass);
                }else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
                    view = LayoutInflater.from(context).inflate(R.layout.bot_dialog, null, false);
                    TextView tvSua, tvXoa;
                    tvSua = view.findViewById(R.id.sdTvSua);
                    tvXoa = view.findViewById(R.id.sdTvXoa);
                    tvSua.setText("Chỉnh sửa món");
                    tvXoa.setText("Xóa món");
                    Integer maMon;
                    String tenMon, hinhMon;
                    maMon = monAnClass.getMon_Ma();
                    tenMon = monAnClass.getMon_Ten();
                    hinhMon = monAnClass.getMon_HinhAnh();

                    tvSua.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                            Intent intent = new Intent(context, ThemMonAnActivity.class);
                            intent.putExtra("maSuaMon", maMon);
                            context.startActivity(intent);
                        }
                    });
                    tvXoa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.dismiss();
                            funcTaoDialogXoa(maMon, tenMon, hinhMon);
                        }
                    });
                    bottomSheetDialog.setContentView(view);
                    bottomSheetDialog.show();
                }
            }
        });
    }
    // THEM PHIEU GOI MON
    private void funcTaoDialogThemPgm(MonAnClass monAnClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_them_pgm, null);
        builder.setCancelable(false).setView(view);
        AlertDialog alertDialog = builder.create();
        TextView tvTitle;
        EditText edSl, edGc;
        TextView tvGia;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvGia = view.findViewById(R.id.tvTPGMGia);
        edSl = view.findViewById(R.id.edTPGMSoLuong);
        edGc = view.findViewById(R.id.edTPGMGhiChu);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        tvTitle.setText("Thêm " + monAnClass.getMon_Ten().toLowerCase());
        funcHienThiGiaTheoMon(monAnClass.getMon_Ma(), tvGia);
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gia, soLuong, ghiChu;
                gia = tvGia.getText().toString();
                soLuong = edSl.getText().toString();
                ghiChu = edGc.getText().toString();
                if(soLuong.isEmpty()){
                    soLuong = "1";
                }
                if(ghiChu.isEmpty()){
                    ghiChu = "";
                }
                funcKiemTraBan(maBanNguon, monAnClass.getMon_Ma(), gia, soLuong, ghiChu);
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void funcKiemTraBan(Integer maBan, Integer monMa, String monGia, String monSoLuong, String monGhiChu) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        Query query = databaseReference.orderByChild("ban_Ma").equalTo(maBan);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(!pgmClass.getPgm_TrangThai().equals("0")){
                        found = true;
                        break;
                    }
                }
                if(found){
                    funcLayMaPgmTheoBan(maBanNguon, monMa, monGia, monSoLuong, monGhiChu);
                }else {
                    funcThemPGM(maBanNguon, monMa, monGia, monSoLuong, monGhiChu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcThemPGM(Integer maBan, Integer monMa, String monGia, String monSoLuong, String monGhiChu) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference dataPGM = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        dataPGM.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maPgm;
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    maPgm =Integer.valueOf(dataSnapshot.getKey()) + 1;
                }else {
                    maPgm = 1;
                }
                PGMClass pgmClass = new PGMClass(maPgm, maBan, uId, "1");
                dataPGM.child(String.valueOf(maPgm)).setValue(pgmClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            funcThemHoaDon(maPgm);
                            funcLayMaPgmTheoBan(maBan, monMa, monGia, monSoLuong, monGhiChu);
//                            funcLayKhuVucTheoBan(maBan);
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void funcLayKhuVucTheoBan(Integer maBan) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ma").equalTo(maBan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer khuVucMa = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    khuVucMa = banAnClass.getKv_Ma();
                }
                funcKiemTraKhuVuc(khuVucMa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcKiemTraKhuVuc(Integer khuVucMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(khuVucMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    if(!khuVucClass.getKv_TrangThai().equals("Ẩn") && !khuVucClass.getKv_TrangThai().equals("Có khách")){
                        databaseReference.child(String.valueOf(khuVucMa)).child("kv_TrangThai").setValue("Có khách");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcThemHoaDon(Integer maPgm) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
        String ngay = funcLayNgay();
        String gioVao = funcLayGio();
        Query query = databaseReference.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer hdMa = 0;
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    hdMa = Integer.valueOf(dataSnapshot.getKey()) + 1;
                }else {
                    hdMa = 1;
                }
                HoaDonClass hoaDonClass = new HoaDonClass(hdMa, maPgm, ngay, gioVao, "", 0, "", "1");
                databaseReference.child(String.valueOf(hdMa)).setValue(hoaDonClass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayMaPgmTheoBan(Integer maBan, Integer monMa, String monGia, String monSoLuong, String monGhiChu) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        Query query = databaseReference.orderByChild("ban_Ma").equalTo(maBan);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maPGM = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1")){
                        maPGM = pgmClass.getPgm_Ma();
                    }
                }
                funcThemChiTietPGM(maPGM, monMa, monGia, monSoLuong, monGhiChu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcThemChiTietPGM(Integer maPgm, Integer monMa, String monGia, String monSoLuong, String monGhiChu) {
        ChiTietPGMClass chiTietPGMClass = new ChiTietPGMClass(maPgm, monMa, Integer.valueOf(funcLaySoTuChuoi(monGia)), Integer.valueOf(monSoLuong), monGhiChu);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        Query query = databaseReference.orderByChild("pgm_Ma").equalTo(maPgm);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean tonTai = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietPGMClass chiTietKiemTra = dataSnapshot.getValue(ChiTietPGMClass.class);
                    if(chiTietKiemTra.getMon_Ma().equals(monMa)){
                        tonTai = true;
                        break;
                    }
                }
                if(!tonTai){
                    databaseReference.child(maPgm + "_" + monMa).setValue(chiTietPGMClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            }else {
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }else {
                    funcCapNhatChiTiet(maPgm, monMa, monSoLuong, monGhiChu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void funcCapNhatChiTiet(Integer maPgm, Integer monMa, String monSoLuong, String monGhiChu) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        Query query = databaseReference.orderByChild("pgm_Ma").equalTo(maPgm);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer slMoi = 1;
                String ghiChuHienTai = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietPGMClass chiTietPGMClass = dataSnapshot.getValue(ChiTietPGMClass.class);
                    if(chiTietPGMClass.getMon_Ma().equals(monMa)){
                        ghiChuHienTai = chiTietPGMClass.getCt_GhiChu();
                        slMoi = chiTietPGMClass.getCt_SoLuong() + Integer.valueOf(monSoLuong);
                    }
                }
                databaseReference.child(maPgm+"_"+monMa).child("ct_SoLuong").setValue(slMoi);
                if(!monGhiChu.equals("")){
                    databaseReference.child(maPgm+"_"+monMa).child("ct_GhiChu").setValue(monGhiChu);
                }else {
                    databaseReference.child(maPgm+"_"+monMa).child("ct_GhiChu").setValue(ghiChuHienTai);
                }
                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiGiaTheoMon(Integer mon_ma, TextView tvGia) {
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DonGia");
        databaseReference.orderByChild("mon_Ma").equalTo(mon_ma).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gia = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DonGiaClass donGiaClass = dataSnapshot.getValue(DonGiaClass.class);
                    gia = Integer.valueOf(donGiaClass.getDg_Gia());
                }
                String giaF = numberFormat.format(gia);
                if(gia!=null){
                    tvGia.setText(giaF);

                }else {
                    tvGia.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static String funcLaySoTuChuoi(String input) {
        String numberStr = input.replaceAll("[^0-9]", "");
        return numberStr;
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

    public String funcLayGio() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }
    // END THEM PHIEU GOI MON


    // MON AN
    private void funcTaoDialogXoa(Integer maMon, String tenMon, String hinhMon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView textView = view.findViewById(R.id.tvTextView);
        textView.setText("Xác nhận xóa " + tenMon);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
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
                if(!hinhMon.equals("Đường dẫn")){
                    FirebaseStorage.getInstance().getReferenceFromUrl(hinhMon).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseDatabase.getInstance().getReference("MonAn").child(String.valueOf(maMon)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        FirebaseDatabase.getInstance().getReference("DonGia").orderByChild("mon_Ma").equalTo(maMon).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    dataSnapshot.getRef().removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    }else {
                                        try {
                                            task.getException();
                                        }catch (Exception e){
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Lỗi khi xóa món", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    FirebaseDatabase.getInstance().getReference("MonAn").child(String.valueOf(maMon)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference("DonGia").orderByChild("mon_Ma").equalTo(maMon).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            dataSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            }else {
                                try {
                                    task.getException();
                                }catch (Exception e){
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return listMonAn.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenMon, tvGiaMon;
        ImageView ivHinhMon;
        LinearLayout lnMonAn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvItemMonAn);
            tvGiaMon = itemView.findViewById(R.id.tvItemMonAnGia);
            ivHinhMon = itemView.findViewById(R.id.ivItemMonAn);
            lnMonAn = itemView.findViewById(R.id.lnMonAn);
        }
    }
}
