package com.example.zhou.camera;


import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.widget.TextView;

import com.example.zhou.camera.R;

import java.io.File;
import java.io.RandomAccessFile;

public class MainActivity extends Activity {

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
        setContentView(R.layout.activity_main);
        pic = (ImageView) findViewById(R.id.imageView1);
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
        ShowCamera = new ShowCamera(this, cameraObject);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(ShowCamera);

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
            sb.append(location.getAccuracy());
            sb.append(",");
            sb.append(location.getSpeed());
            sb.append(",");
            sb.append(location.getLongitude());
            sb.append(",");
            sb.append(location.getLatitude());
            sb.append(",");
            sb.append(location.getBearing());
            sb.append(",");
            sb.append(location.getTime());
            sb.append("\n");
            tv.setText(sb.toString());
            String filePath = "/data/";
            String fileName = "gps.csv";
            writeTxtToFile(sb.toString(),filePath,fileName);

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



}
