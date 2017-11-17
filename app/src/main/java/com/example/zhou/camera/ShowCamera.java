package com.example.zhou.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holdMe;
    private Camera theCamera;

    public ShowCamera(Context context,Camera camera) {
        super(context);
        theCamera = camera;
        holdMe = getHolder();
        holdMe.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holdMe) {
        try   {

            theCamera.setPreviewDisplay(holdMe);
            theCamera.startPreview();
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        theCamera.stopPreview();
        theCamera.release();
        theCamera = null;
    }

}