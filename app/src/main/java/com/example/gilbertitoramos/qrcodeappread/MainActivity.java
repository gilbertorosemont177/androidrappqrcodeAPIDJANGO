package com.example.gilbertitoramos.qrcodeappread;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GetAsyncResponse{

    BarcodeDetector barcodeDetector;
    CameraSource camsource;
    SurfaceView sfv;
    TextView txtv;
    final int Requestid = 1001;
    CnxApiDjangoAsyncTask cnxApiDjango= new CnxApiDjangoAsyncTask();
    TextView usernametextvie;
    ImageView imgv;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {


            case Requestid: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        camsource.start(sfv.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sfv = findViewById(R.id.cameraqrcode);
        txtv = findViewById(R.id.txtqrcode);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        camsource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(300, 300).build();
//        new CnxApiDjangoAsyncTask().execute("http://192.168.2.14:8001/homeapp/etudiantsinfo/"+res);

        cnxApiDjango.delegateforAllResponsesfromThisAsyncTask=this;

        sfv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},Requestid);
                        return;
                    }
                    camsource.start(sfv.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                camsource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode>qrcode=detections.getDetectedItems();
                if(qrcode.size()!=0){

                    txtv.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            String resultqrcode=qrcode.valueAt(0).displayValue;
                            cnxApiDjango.execute("http://192.168.2.14:8001/homeapp/etudiantsinfo/"+resultqrcode);




                        }
                    });
                }

            }
        });

    }

    public void resultFromAsyncTask(JSONObject j) {


               if (j != null) {
                   try {
                   usernametextvie = findViewById(R.id.usernameetudiant);
                   usernametextvie.setText(j.getString("email"));
                   imgv=findViewById(R.id.imgetudiant);
                   }
                   catch (Exception e){
                       e.printStackTrace();

                   }

               }
               else {
                   Toast.makeText(this.getApplicationContext(), "username inexistant",
                           Toast.LENGTH_LONG).show();
               }

    }
}
