package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class KhuyenMaiTrangChuAdapter  extends RecyclerView.Adapter<KhuyenMaiTrangChuAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<KhuyenMaiClass> list;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    String ngayHTai = funcLayNgay();
    NumberFormat numberFormat = new DecimalFormat("#,###.##");

    public KhuyenMaiTrangChuAdapter(Context context, ArrayList<KhuyenMaiClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khuyen_mai_trang_chu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        KhuyenMaiClass khuyenMaiClass = list.get(position);
        holder.tvTenKm.setText(khuyenMaiClass.getKm_TieuDe());
        holder.tvNgayKm.setText("Từ: " + khuyenMaiClass.getKm_NgayBd() + " - " + khuyenMaiClass.getKm_NgayKt());
        String text = "Giảm giá " + khuyenMaiClass.getKm_PhanTram() + "% áp dụng cho hóa đơn từ " + numberFormat.format(khuyenMaiClass.getKm_Muc()) + " VNĐ";
        if(khuyenMaiClass.getKm_Muc() == 0){
            text = "Giảm giá " + khuyenMaiClass.getKm_PhanTram() + "% áp dụng cho tất cả hóa đơn";
        }
        holder.tvPhanTramKm.setText(text);
        try {
            Date ngayHienTai = format.parse(ngayHTai);
            Date ngayBd = format.parse(khuyenMaiClass.getKm_NgayBd());
            if(ngayHienTai.before(ngayBd)){
                holder.tvSapCo.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenKm, tvNgayKm, tvPhanTramKm, tvSapCo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKm = itemView.findViewById(R.id.tvItemKMTCTen);
            tvNgayKm = itemView.findViewById(R.id.tvItemKMTCNgay);
            tvPhanTramKm = itemView.findViewById(R.id.tvItemKMTCPhamTram);
            tvSapCo = itemView.findViewById(R.id.tvItemKmSapCo);

        }
    }
}
