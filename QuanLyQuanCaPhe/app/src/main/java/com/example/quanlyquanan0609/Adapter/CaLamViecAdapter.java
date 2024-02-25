package com.example.quanlyquanan0609.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Listener.HienThiChiTietCaListener;
import com.example.quanlyquanan0609.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CaLamViecAdapter extends RecyclerView.Adapter<CaLamViecAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<CaLamViecClass> list;
    private int hour, minute;


    public CaLamViecAdapter(Context context, ArrayList<CaLamViecClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ca_lam_viec, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CaLamViecClass caLamViecClass = list.get(position);
        holder.tvCaTen.setText(caLamViecClass.getCa_Ten());
        holder.tvCaGio.setText("Thời gian: " + caLamViecClass.getCa_GioDB() + " ~ " + caLamViecClass.getCa_GioKT());
        holder.tvCaSoLuong.setText("SL: " + caLamViecClass.getCa_SoLuong());
        holder.lnCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoBotDialog(caLamViecClass);
            }
        });
    }

    private void funcTaoBotDialog(CaLamViecClass caLamViecClass){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bot_dialog,null);
        bottomSheetDialog.setContentView(view);
        TextView tvSua, tvXoa;
        tvSua = view.findViewById(R.id.sdTvSua);
        tvXoa = view.findViewById(R.id.sdTvXoa);
        tvSua.setText("Chỉnh sửa ca làm việc");
        tvXoa.setText("Xóa ca làm việc");
        tvSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogSua(caLamViecClass);
                bottomSheetDialog.dismiss();
            }
        });
        tvXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogXoa(caLamViecClass);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    private void funcTaoDialogXoa(CaLamViecClass caLamViecClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        builder.setCancelable(false).setView(view);
        AlertDialog alertDialog = builder.create();
        TextView tvTitle, tvNoiDung;
        Button btnOk, btnHuy;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        tvTitle.setText("Xác nhận");
        tvNoiDung.setText("Xác nhận xóa " + caLamViecClass.getCa_Ten() + " ?");
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcXoaCa(caLamViecClass.getCa_Ma(), alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcXoaCa(Integer caMa, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.child(String.valueOf(caMa)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
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

    private void funcTaoDialogSua(CaLamViecClass caLamViecClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_them_ca, null);
        EditText edCaTen, edCaSL;
        TextView tvTitle, tvGioBD, tvGioKT;
        Button btnOk, btnHuy;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvGioBD = view.findViewById(R.id.tvCaGioBD);
        tvGioKT = view.findViewById(R.id.tvCaGioKT);
        edCaTen = view.findViewById(R.id.edCaTen);
        edCaSL = view.findViewById(R.id.edCaSoLuong);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        tvTitle.setText("Chỉnh sửa ca làm việc");
        edCaTen.setText(caLamViecClass.getCa_Ten());
        edCaSL.setText(String.valueOf(caLamViecClass.getCa_SoLuong()));
        tvGioBD.setText(caLamViecClass.getCa_GioDB());
        tvGioKT.setText(caLamViecClass.getCa_GioKT());
        tvGioBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogTime(tvGioBD);
            }
        });
        tvGioKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogTime(tvGioKT);
            }
        });
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
                Integer caMa, caSoLuong;
                String caTenHienTai, caTenMoi, caGioBD, caGioKT;
                caMa = caLamViecClass.getCa_Ma();
                caTenHienTai = caLamViecClass.getCa_Ten();
                caTenMoi = edCaTen.getText().toString();
                caGioBD = tvGioBD.getText().toString();
                caGioKT = tvGioKT.getText().toString();
                caSoLuong = Integer.valueOf(edCaSL.getText().toString());
                funcCapNhatCa(caMa, caTenHienTai, caTenMoi, caGioBD, caGioKT, caSoLuong, edCaTen, alertDialog);
            }
        });
        alertDialog.show();

    }

    private void funcCapNhatCa(Integer caMa, String caTenHienTai, String caTenMoi, String caGioBD, String caGioKT, Integer caSoLuong, EditText edCaTen, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        if(!caTenHienTai.equals(caTenMoi)){
            databaseReference.orderByChild("ca_Ten").equalTo(caTenMoi).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){
                       edCaTen.setError("Tên đã tồn tại");
                   }else {
                       CaLamViecClass caLamViecClass = new CaLamViecClass(caMa, caSoLuong, caTenMoi, caGioBD, caGioKT);
                       databaseReference.child(String.valueOf(caMa)).setValue(caLamViecClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                   alertDialog.dismiss();
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            CaLamViecClass caLamViecClass = new CaLamViecClass(caMa, caSoLuong, caTenMoi, caGioBD, caGioKT);
            databaseReference.child(String.valueOf(caMa)).setValue(caLamViecClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
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

    }

    private void funcTaoDialogTime(TextView textView) {

        String currentTime = textView.getText().toString();

        String[] time = currentTime.split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);

        TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

                hour = selectedHour;
                minute = selectedMinute;
                String time = String.format("%02d:%02d", hour, minute);
                textView.setText(time);
            }

        }, hour, minute, true);
        timePicker.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvCaTen, tvCaGio, tvCaSoLuong;
        public LinearLayout lnCa;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCaGio = itemView.findViewById(R.id.tvItemCaGio);
            tvCaTen = itemView.findViewById(R.id.tvItemCaTen);
            tvCaSoLuong = itemView.findViewById(R.id.tvItemCaSL);
            lnCa = itemView.findViewById(R.id.lnItemCa);
        }
    }

}
