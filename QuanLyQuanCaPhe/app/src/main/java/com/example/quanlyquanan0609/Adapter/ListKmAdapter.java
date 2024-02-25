package com.example.quanlyquanan0609.Adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.KhuyenMaiActivity;
import com.example.quanlyquanan0609.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ListKmAdapter extends RecyclerView.Adapter<ListKmAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<KhuyenMaiClass> list;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    String ngayHTai = funcLayNgay();


    public ListKmAdapter(Context context, ArrayList<KhuyenMaiClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_km, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        KhuyenMaiClass khuyenMaiClass = list.get(position);
        holder.tvTieuDe.setText(khuyenMaiClass.getKm_TieuDe());
        holder.tvNgay.setText("Từ:    " + khuyenMaiClass.getKm_NgayBd() + "\nĐến: " + khuyenMaiClass.getKm_NgayKt());
        holder.lnKm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogSua(khuyenMaiClass);
            }
        });
        holder.ivXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogXoa(khuyenMaiClass);
            }
        });
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

    private void funcTaoDialogXoa(KhuyenMaiClass khuyenMaiClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        tvTitle.setText("Xác nhận");
        tvNoiDung.setText("Xác nhận xóa khuyến mãi " + khuyenMaiClass.getKm_TieuDe() + " ?");
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
                funcXoaKhuyenMai(khuyenMaiClass.getKm_Ma(), alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcXoaKhuyenMai(Integer kmMa, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        databaseReference.child(String.valueOf(kmMa)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void funcTaoDialogSua(KhuyenMaiClass khuyenMaiClass) {
        Calendar calendar = Calendar.getInstance();
        final int nam = calendar.get(Calendar.YEAR);
        final int thang = calendar.get(Calendar.MONTH);
        final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_khuyen_mai, null);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        Button btnHuy, btnOK;
        TextView tvNgayBD, tvNgayKT, tvTitle;
        EditText edTieuDe, edMucGiam, edPhanTram, edNoiDung;
        btnOK = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        edTieuDe = view.findViewById(R.id.edKMTieuDe);
        edMucGiam = view.findViewById(R.id.edKMMuc);
        edPhanTram = view.findViewById(R.id.edKMPhanTram);
        edNoiDung = view.findViewById(R.id.edKMNoiDung);
        tvNgayKT = view.findViewById(R.id.tvNgayKT);
        tvNgayBD = view.findViewById(R.id.tvNgayBD);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("Chỉnh sửa khuyến mãi");
        funcTextChanged(edMucGiam, edPhanTram, edNoiDung);
        edTieuDe.setText(khuyenMaiClass.getKm_TieuDe());
        edMucGiam.setText(String.valueOf(khuyenMaiClass.getKm_Muc()));
        edPhanTram.setText(String.valueOf(khuyenMaiClass.getKm_PhanTram()));
        tvNgayBD.setText(khuyenMaiClass.getKm_NgayBd());
        tvNgayKT.setText(khuyenMaiClass.getKm_NgayKt());
        edNoiDung.setText(khuyenMaiClass.getKm_NoiDung());
        tvNgayBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngayBD = String.format("%02d/%02d/%d", i2, i1, i);
                        tvNgayBD.setText(ngayBD);
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });
        tvNgayKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngayBD = String.format("%02d/%02d/%d", i2, i1, i);
                        tvNgayKT.setText(ngayBD);
                    }
                }, nam, thang, ngay);
                datePickerDialog.show();
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Date ngayHienTai = format.parse(ngayHTai);
                    Date ngayKT = format.parse(khuyenMaiClass.getKm_NgayKt());
                    if(ngayHienTai.after(ngayKT)){
                        alertDialog.dismiss();
                    }else {
                        String tieuDe, mucGiam, phanTram, ngayBd, ngayKt, noiDung;
                        tieuDe = edTieuDe.getText().toString();
                        mucGiam = edMucGiam.getText().toString();
                        phanTram = edPhanTram.getText().toString();
                        noiDung = edNoiDung.getText().toString();
                        ngayBd = tvNgayBD.getText().toString();
                        ngayKt = tvNgayKT.getText().toString();
                        try {
                            Date dNgayBd = format.parse(ngayBd);
                            Date dNgayKt = format.parse(ngayKt);
                            if(dNgayKt.before(dNgayBd)){
                                Toast.makeText(context, "Ngày kết thúc không hợp lệ", Toast.LENGTH_SHORT).show();
                            }else if (TextUtils.isEmpty(tieuDe)){
                                edTieuDe.setError("Vui lòng nhập tiêu đề");
                            } else if (TextUtils.isEmpty(mucGiam)) {
                                edMucGiam.setError("Vui lòng nhập mức áp dụng");
                            } else if (TextUtils.isEmpty(phanTram)) {
                                edPhanTram.setError("Vui lòng nhập phần trăm giảm giá");
                            } else {
                                funcSuaKhuyenMai(khuyenMaiClass.getKm_Ma(), tieuDe, mucGiam, phanTram, ngayBd, ngayKt, noiDung, alertDialog);
                            }
                        }catch (ParseException e){
                            Log.e("NgayKt", e.toString());
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        alertDialog.show();
    }

    private void funcSuaKhuyenMai(Integer kmMa, String tieuDe, String mucGiam, String phanTram, String ngayBd, String ngayKt, String noiDung, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        KhuyenMaiClass khuyenMaiClass = new KhuyenMaiClass(kmMa, Integer.valueOf(funcLaySoTuChuoi(mucGiam)), Integer.valueOf(phanTram), tieuDe, ngayBd, ngayKt, noiDung);
        databaseReference.child(String.valueOf(kmMa)).setValue(khuyenMaiClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    alertDialog.dismiss();
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
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

    public String funcLaySoTuChuoi(String input) {
        String numberStr = input.replaceAll("[^0-9]", "");
        return numberStr;
    }

    private void funcTextChanged(EditText editText, EditText edPhanTram, EditText edNoiDung) {
        edNoiDung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nd = edNoiDung.getText().toString();
                if(nd.isEmpty()){
                    edNoiDung.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                }else {
                    edNoiDung.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edPhanTram.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = edPhanTram.getText().toString();
                if(!str.isEmpty()){
                    Integer phanTram = Integer.valueOf(str);
                    if(phanTram > 100){
                        edPhanTram.setText("100");
                    }
                }
            }
        });
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        editText.addTextChangedListener(new TextWatcher() {
            private  String current = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,.]", "");

                    try {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getInstance().format(parsed);

                        current = formatted;
                        editText.setText(formatted);
                        editText.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        // Xử lý ngoại lệ nếu văn bản không thể chuyển đổi thành số
                    }

                    editText.addTextChangedListener(this);
                } else {
                    current = "";
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTieuDe, tvNgay, tvSapCo;
        ImageView ivXoa;
        LinearLayout lnKm;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvItemKmTieuDe);
            tvNgay = itemView.findViewById(R.id.tvItemKmNgay);
            tvSapCo = itemView.findViewById(R.id.tvItemKmSapCo);
            ivXoa = itemView.findViewById(R.id.ivItemKmXoa);
            lnKm = itemView.findViewById(R.id.lnKm);
        }
    }
}
