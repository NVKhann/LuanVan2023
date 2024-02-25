package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.databinding.ActivityQrCodeDiemDanhBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

public class QrCodeDiemDanhActivity extends AppCompatActivity {
    ActivityQrCodeDiemDanhBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat date = new SimpleDateFormat("ddMMyyyy");
    String currentDate = dateFormat.format(calendar.getTime());
    String ngayHomNay = date.format(calendar.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrCodeDiemDanhBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.tvNgay.setText("Ngày: " + currentDate);
        generateQRCode();

    }

    private void generateQRCode(){
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix matrix = writer.encode(currentDate, BarcodeFormat.QR_CODE, 300, 300, hintMap);


            //Tạo Bitmap kích thước 250x250
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            //Vẽ phần trong của QR lên Bitmap
            for(int i = 0; i < 290; i++){
                for(int j = 0; j < 290; j++){
                    bitmap.setPixel(i, j, matrix.get(i, j)? Color.BLACK : Color.WHITE);
                }
            }

            //Vẽ phần rìa ngoài màu trắng
            for(int i = 290; i < 300; i++){
                for(int j = 0; j < 300; j++){
                    bitmap.setPixel(i, j, Color.WHITE);
                }
            }

            for(int i = 0; i < 300; i++){
                for(int j = 290; j < 300; j++){
                    bitmap.setPixel(i, j, Color.WHITE);
                }
            }
            binding.ivCode.setImageBitmap(bitmap);
        }catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tai, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuTai){
            funcTaiQr();
        }
        return super.onOptionsItemSelected(item);
    }

    private void funcTaiQr() {
        LinearLayout lnHoaDon = binding.lnQrCode;
        Bitmap bitmap = Bitmap.createBitmap(
                lnHoaDon.getWidth(),
                lnHoaDon.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        lnHoaDon.draw(new Canvas(bitmap));
        saveBitMap(bitmap);
        Toast.makeText(QrCodeDiemDanhActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
    }

    private void saveBitMap(Bitmap bitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "Qr" + ngayHomNay + ".png";
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}