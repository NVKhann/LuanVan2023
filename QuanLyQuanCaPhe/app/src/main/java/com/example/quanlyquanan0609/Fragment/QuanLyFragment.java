package com.example.quanlyquanan0609.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quanlyquanan0609.CaLamViecActivity;
import com.example.quanlyquanan0609.CauHinhActivity;
import com.example.quanlyquanan0609.KhuVucActivity;
import com.example.quanlyquanan0609.KhuyenMaiActivity;
import com.example.quanlyquanan0609.MonAnActivity;
import com.example.quanlyquanan0609.NguoiDungActivity;
import com.example.quanlyquanan0609.ThemMonAnActivity;
import com.example.quanlyquanan0609.ThongKeActivity;
import com.example.quanlyquanan0609.databinding.FragmentQuanLyBinding;

public class QuanLyFragment extends Fragment {
    FragmentQuanLyBinding binding;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuanLyBinding.inflate(inflater, container, false);
        rootView = binding.getRoot();
        binding.cardKhuVuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), KhuVucActivity.class);
                startActivity(intent);
            }
        });
        binding.cardMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MonAnActivity.class);
                startActivity(intent);
            }
        });
        binding.cardKhuyenMai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), KhuyenMaiActivity.class);
                startActivity(intent);
            }
        });
        binding.cardNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NguoiDungActivity.class);
                startActivity(intent);
            }
        });
        binding.cardCaLamViec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CaLamViecActivity.class);
                startActivity(intent);
            }
        });
        binding.cardThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ThongKeActivity.class);
                startActivity(intent);
            }
        });
//        binding.cardCauHinh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), CauHinhActivity.class);
//                startActivity(intent);
//            }
//        });
        return rootView;
    }
}