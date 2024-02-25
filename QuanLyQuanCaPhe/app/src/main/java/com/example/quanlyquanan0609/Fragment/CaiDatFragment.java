package com.example.quanlyquanan0609.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.DangNhapActivity;
import com.example.quanlyquanan0609.HoaDonKhachHangActivity;
import com.example.quanlyquanan0609.R;
import com.example.quanlyquanan0609.ThongTinTaiKhoanActivity;
import com.example.quanlyquanan0609.ThongTinUngDungActivity;
import com.example.quanlyquanan0609.databinding.FragmentCaiDatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CaiDatFragment extends Fragment {
    FragmentCaiDatBinding binding;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCaiDatBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        funcHienThiCard();
        binding.cardDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogXacNhan();
            }
        });
        binding.cardThongTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ThongTinTaiKhoanActivity.class);
                startActivity(intent);
            }
        });
        binding.cardLichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HoaDonKhachHangActivity.class);
                startActivity(intent);
            }
        });
        binding.cardTTUD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ThongTinUngDungActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void funcHienThiCard() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByChild("nd_Ma").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        binding.cardLichSu.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDialogXacNhan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        tvTitle.setText("Đăng xuất");
        tvNoiDung.setText("Xác nhận đăng xuất ?");
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
                binding.progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(), DangNhapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
                funcDongFragment();
            }
        });
        alertDialog.show();
    }

    private void funcDongFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }
}