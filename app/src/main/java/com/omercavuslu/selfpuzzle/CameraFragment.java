package com.omercavuslu.selfpuzzle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    public static String tempFileImage(Context context, Bitmap bitmap, String name) {

        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }
    Camera camera;

    Camera.PictureCallback jpegCallback;
    SurfaceView mSurdaceView;
    SurfaceHolder mSurdaceHolder;

    final int CAMERA_REQUEST_CODE =1;
    public static CameraFragment newInstance(){
        CameraFragment fragment = new CameraFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera,container,false);

        mSurdaceView = view.findViewById(R.id.surfaceView);
        mSurdaceHolder = mSurdaceView.getHolder();//surfaceview e bişey eklememiz için gerekli dedi videoda


        //izin yoksa kullanıcıdan kamera için izin istiyor
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        else{
            mSurdaceHolder.addCallback(this);
            mSurdaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        //layouttaki button tanımlamaları

        ImageButton resim_cek = view.findViewById(R.id.resim_cek);

        resim_cek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });


        //Capture butonuna tıklandığında captureImage fonk çağırılacak

        //creates a temporary file and return the absolute file path

        //burada kullanıcılara göstermek ya da başka bir kullanıcıya göndermek için resim hazırlanıyor.
        //resim çekildikten sonra kullanıcıya gösterdiği kısma showCaptureActivity dedi burada da resim çekildikten sonra oraya yönlendiriyor
        jpegCallback = new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                //çekilen resim i showCaptureActivity e yollamak için bytes dizisini putExtra metodu ile yolluyor
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
                String filePath= tempFileImage(getActivity(),decodedBitmap,"name");
                Intent intent = new Intent(getActivity(),ShowCaptureActivity.class);
                intent.putExtra("capture",filePath);
                startActivity(intent);
                return;
            }
        };

        return view;
    }
    public void resimAl(){
        captureImage();
    }
    //MainActivity ma = new MainActivity();
    //burada resim çekiyor sonra da jpegCallback e atıyor
    private void captureImage() {

        //ma.onImageFromCameraClick(getView());
        camera.takePicture(null, null, jpegCallback);

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();

        Camera.Parameters parameters;
        parameters = camera.getParameters();
        //kamera aslında ters gösteriyo ama 90 derece döndürdüğümüzde düz olacak
        camera.setDisplayOrientation(90);
        //fps i ayarlıyo adam 30 yaptı ama benim s9+ ım olduğu için 60 yaptım xd
        parameters.setPreviewFrameRate(30);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);



        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for (int i = 1;i<sizeList.size();i++){
            if (sizeList.get(i).width * sizeList.get(i).height>(bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        parameters.setPreviewSize(bestSize.width,bestSize.height);
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.startPreview();
    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mSurdaceHolder.addCallback(this);
                    mSurdaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
                else{
                    Toast.makeText(getContext() , "PLEASE İZİN VER",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void LogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return;
    }
}
