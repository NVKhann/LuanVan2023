package com.example.quanlyquanan0609.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quanlyquanan0609.CaLamViecNhanVienActivity;
import com.example.quanlyquanan0609.R;
import com.example.quanlyquanan0609.XemLaiDangKyCaActivity;
import com.example.quanlyquanan0609.databinding.FragmentCaNhanBinding;

public class CaNhanFragment extends Fragment {
    FragmentCaNhanBinding binding;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCaNhanBinding.inflate(getLayoutInflater(), container, false);
        rootView = binding.getRoot();
        binding.cardCaNhanCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CaLamViecNhanVienActivity.class);
                startActivity(intent);
            }
        });

        binding.cardCNDanhSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), XemLaiDangKyCaActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}