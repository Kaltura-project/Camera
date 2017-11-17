package com.example.zhou.camera;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.hardware.Camera;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.widget.TextView;

import com.example.zhou.camera.R;

import java.io.File;
import java.io.RandomAccessFile;

public class MainActivity extends Activity {

    private int MY_PERMISSIONS_REQUEST_CAMERA;
    private int MY_PERMISSIONS_REQUEST_GPS;
    private int MY_PERMISSIONS_REQUEST_AUDIO;

    private Camera cameraObject;
    public ShowCamera ShowCamera;
    private ImageView pic;
    private TextView tv;
    private LocationManager mLocaationManager;


    public static Camera isCameraAvailiable(){
        Camera object = null;
        try {
            object = Camera.open();
        }
        catch (Exception e){
        }
        return object;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean cameraperms = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED;
        boolean locperms = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        boolean micperms = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED;
        if ((micperms) || (locperms)|| (cameraperms)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_GPS);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_AUDIO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (MY_PERMISSIONS_REQUEST_AUDIO==0 && MY_PERMISSIONS_REQUEST_CAMERA==0 && MY_PERMISSIONS_REQUEST_GPS==0){
            setContentView(R.layout.activity_main);
            //pic = (ImageView) findViewById(R.id.imageView1);
            tv = (TextView) findViewById(R.id.tv);
            mLocaationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = mLocaationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocaationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0,new LocationListener(){
                @Override
                public void onStatusChanged(String provider,int status, Bundle extras){
                }

                @Override
                public void onProviderEnabled(String provider){
                    update(mLocaationManager.getLastKnownLocation(provider));
                }

                @Override
                public void onProviderDisabled(String provider){
                }

                @Override
                public void onLocationChanged(Location location){
                    update(location);
                }
            });
            update(location);
            cameraObject = isCameraAvailiable();
            Camera.CameraInfo camObjInfo = new Camera.CameraInfo();
            cameraObject.setDisplayOrientation(getCorrectCameraOrientation(camObjInfo, cameraObject));
            cameraObject.getParameters().setRotation(getCorrectCameraOrientation(camObjInfo, cameraObject));
            ShowCamera = new ShowCamera(this, cameraObject);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(ShowCamera);
        }

    }

    public void ClickCapture(View view) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.addCategory("android.intent.catergory.DEFAULT");

        File VideoFile = new File("/data/video.mp4");
        Uri VideoUri = Uri.fromFile(VideoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, VideoUri);
        startActivityForResult(intent, 0);

        mLocaationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = mLocaationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocaationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0,new LocationListener(){
            @Override
            public void onStatusChanged(String provider,int status, Bundle extras){
            }

            @Override
            public void onProviderEnabled(String provider){
                update(mLocaationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(String provider){
            }

            @Override
            public void onLocationChanged(Location location){
                update(location);
            }
        });
        update(location);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    private  void update(Location location){
        if (location != null){
            StringBuilder sb = new StringBuilder();
            sb.append("Accuracy:");
            sb.append(location.getAccuracy());
            sb.append(",Speed:");
            sb.append(location.getSpeed());
            sb.append(",Longtitude");
            sb.append(location.getLongitude());
            sb.append(",Latitude");
            sb.append(location.getLatitude());
            sb.append(",Bearing");
            sb.append(location.getBearing());
            sb.append(",Time");
            sb.append(location.getTime());
            sb.append("\n");
            tv.setText(sb.toString());

            String filePath = "/data/";
            String fileName = "gps.csv";
            StringBuilder Savesb = new StringBuilder();
            Savesb.append(location.getAccuracy());
            Savesb.append(",");
            Savesb.append(location.getSpeed());
            Savesb.append(",");
            Savesb.append(location.getLongitude());
            Savesb.append(",");
            Savesb.append(location.getLatitude());
            Savesb.append(",");
            Savesb.append(location.getBearing());
            Savesb.append(",");
            Savesb.append(location.getTime());
            Savesb.append("\n");
            writeTxtToFile(Savesb.toString(),filePath,fileName);

        }
    }

    public void writeTxtToFile(String strContent, String filePath, String fileName){
        makeFilePath(filePath, fileName);
        String strFilePath = filePath+fileName;
        try{
            File file = new File(strFilePath);
            if (!file.exists()){
                Log.d("TestFile","Create the file:"+strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file,"rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        }catch (Exception e){
            Log.e("TestFile","Error on write File:" + e);
        }
    }

    public File makeFilePath(String filePath, String fileName){
        File file = null;
        makeRootDirectory(filePath);
        try{
            file = new File(filePath+fileName);
            if(!file.exists()){
                file.createNewFile();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath){
        File file = null;
        try{
            file = new File(filePath);
            if(!file.exists()){
                file.mkdir();
            }
        }catch (Exception e){
            Log.i("error:",e+"");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public int getCorrectCameraOrientation(Camera.CameraInfo info, Camera camera) {

        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                degrees = 270;
                break;

            case Surface.ROTATION_90:
                degrees = 180;
                break;

            case Surface.ROTATION_180:
                degrees = 90;
                break;

            case Surface.ROTATION_270:
                degrees = 0;
                break;

        }

        int result;
        if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        }else{
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }

}
