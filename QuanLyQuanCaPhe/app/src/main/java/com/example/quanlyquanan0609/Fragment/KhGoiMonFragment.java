package com.example.quanlyquanan0609.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.hardware.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.KhachHangActivity;
import com.example.quanlyquanan0609.PhieuGoiMonActivity;
import com.example.quanlyquanan0609.databinding.FragmentKhGoiMonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;

public class KhGoiMonFragment extends Fragment {
    FragmentKhGoiMonBinding binding;
    View rootView;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentKhGoiMonBinding.inflate(getLayoutInflater(), container, false);
        rootView = binding.getRoot();
        surfaceView = binding.sfView;
        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }
        surfaceHolder =surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    setCameraDisplayOrientation(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK, camera);

                    camera.startPreview();
                    camera.setPreviewCallback(previewCallback);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });
    return rootView;
    }
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate for the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, reinitialize the camera
                surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        camera = Camera.open();
                        try {
                            camera.setPreviewDisplay(holder);
                            camera.startPreview();
                            camera.setPreviewCallback(previewCallback);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        camera.setPreviewCallback(null);
                        camera.stopPreview();
                        camera.release();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle camera preview frames
    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            // Create a PlanarYUVLuminanceSource and use it to decode the QR code
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, size.width, size.height, 0, 0, size.width, size.height, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();

            try {
                Result result = reader.decode(bitmap);
                String text = result.getText();

                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                funcLayMaBanTheoTen(text);


                // Display the QR code content in the TextView
//                textView.setText(text);
            } catch (Exception e) {
                // QR code not found
            }
        }
    };
    private void funcLayMaBanTheoTen(String tenBan) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ten").equalTo(tenBan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maBan = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    maBan = banAnClass.getBan_Ma();
                }
//                Intent intent = new Intent(getActivity(), PhieuGoiMonActivity.class);
//                intent.putExtra("tenBan", tenBan);
//                intent.putExtra("maBan", maBan);
//                intent.putExtra("nguon", 100);
//                startActivity(intent);
                funcKiemTraBan(maBan, tenBan);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcKiemTraBan(Integer maBan, String tenBan) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(maBan).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean coKhach = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1") && !pgmClass.getNd_Ma().equals(uId)){
                        coKhach = true;
                        break;
                    }
                }
                if(coKhach){
                    Toast.makeText(getActivity(), "Bàn này hiện đã có khách !!!", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getActivity(), PhieuGoiMonActivity.class);
                    intent.putExtra("tenBan", tenBan);
                    intent.putExtra("maBan", maBan);
                    intent.putExtra("nguon", 100);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}