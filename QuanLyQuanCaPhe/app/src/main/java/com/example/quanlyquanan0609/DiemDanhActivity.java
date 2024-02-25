package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.databinding.ActivityDiemDanhBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DiemDanhActivity extends AppCompatActivity {
    ActivityDiemDanhBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String currentDate = dateFormat.format(calendar.getTime());
    String trangThai, key, ngayDki;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiemDanhBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        trangThai = getIntent().getStringExtra("TrangThai");
        key = getIntent().getStringExtra("Key");
        ngayDki = getIntent().getStringExtra("NgayDki");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcMoCam();
        binding.btnThuLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
    }

    private void funcMoCam() {
        surfaceView = binding.sfViewDiemDanh;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        surfaceHolder =surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                camera = Camera.open();
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                    setCameraDisplayOrientation(DiemDanhActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, camera);

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
                Toast.makeText(DiemDanhActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
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
                if(text.equals(currentDate) && currentDate.equals(ngayDki) && !trangThai.equals(null)){
                    if(trangThai.equals("VaoCa")){
                        funcVaoCa(key);
                    }else if (trangThai.equals("RaCa")){
                        funcRaCa(key);
                    }
                }else {
                    Toast.makeText(DiemDanhActivity.this, "QrCode không hợp lệ !!!", Toast.LENGTH_SHORT).show();
                    binding.btnThuLai.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                // QR code not found
            }
        }
    };

    private void funcRaCa(String key) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String gioHienTai = funcLayGio();
        databaseReference.child(key).child("ct_RaCa").setValue(gioHienTai).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DiemDanhActivity.this, "Ra ca thành công", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }

    private void funcVaoCa(String key) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String gioHienTai = funcLayGio();
        databaseReference.child(key).child("ct_VaoCa").setValue(gioHienTai).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(DiemDanhActivity.this, "Vào ca thành công", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

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


}