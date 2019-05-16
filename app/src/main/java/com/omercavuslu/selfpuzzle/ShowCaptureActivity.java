package com.omercavuslu.selfpuzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.omercavuslu.selfpuzzle.CameraFragment.tempFileImage;


public class ShowCaptureActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    private static final String TAG = "myApp";
    final String imgUrl = "https://firebasestorage.googleapis.com/v0/b/selfpuzzle-32f6c.appspot.com/o/captures%2F-Ld_CITYs4Mi26MV3cI_?alt=media&token=7dc073ac-4c88-4010-8ae8-12c6f82204f1";
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;
    String filePath;
    String Uid;
    int parcaSayisi;
    Bitmap rotateBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);
        ImageButton btn_puzzle_yap = findViewById(R.id.btn_puzzle_yap);
        ImageButton btn_send_friend = findViewById(R.id.btn_send_friend);
        parcaSayisi=6;


        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        final TextView seekBarValue = (TextView)findViewById(R.id.seekBarValue);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                switch (progress){

                    case 0:
                        seekBarValue.setText("Kolay");
                        parcaSayisi = 6;
                        break;

                    case 1:
                        seekBarValue.setText("Orta");
                        parcaSayisi = 10;
                        break;
                    case 2:
                        seekBarValue.setText("Zor");
                        parcaSayisi = 14;
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        //putExtra ile capture tagında yollanan resimi b dizisine attık
        // Bundle extras = getIntent().getExtras();
        // assert extras != null;//boş olup olmamasını kontrol ediyor
        // byte[] b = extras.getByteArray("capture");

//gets the file path
        filePath = getIntent().getStringExtra("capture");

//loads the file
        File file = new File(filePath);



        if (filePath!=null){// b nin boş olmadığını bir daha kontrol ediyor.
            ImageView image = findViewById(R.id.imageCaptured);

            //bitmap e dönüştürüyo diziyi
            //Bitmap decodedBitmap = BitmapFactory.decodeByteArray(b, 0,b.length);
            Bitmap decodedBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            rotateBitmap = rotate(decodedBitmap);
            Log.i(TAG,"Show Captured "+rotateBitmap);
            int nh = (int) ( rotateBitmap.getHeight() * (512.0 / rotateBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(rotateBitmap, 512, nh, true);
            image.setImageBitmap(scaled);

        }
        Uid = FirebaseAuth .getInstance().getUid();
        Button mStory = findViewById(R.id.story);
        mStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToStories();
            }
        });



        btn_puzzle_yap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageFromCameraClick22();
            }
        });
        btn_send_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(ShowCaptureActivity.this, ArkadasSecActivity.class);
                //String filePathh= tempFileImage(ShowCaptureActivity.this,rotateBitmap,"name");
               // intent.putExtra("mCurrentPhotoPath", filePathh);
               // Log.i(TAG,"RESIM NEYMIS " +filePathh);
               // intent.putExtra("deger",parcaSayisi);

               // startActivity(intent);
                saveToStories();
            }
        });

    }
    private Bitmap my_image;
    ImageView tempImageView;



    byte[] dataToUpload;
    private void saveToStories() {

        final DatabaseReference userStoryDb = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("story");
        final String key = userStoryDb.push().getKey();

        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotateBitmap.compress(Bitmap.CompressFormat.JPEG, 20,baos);
        dataToUpload = baos.toByteArray();
        UploadTask uploadTask = filePath.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        Map newImage = new HashMap();
                        newImage.put("profileImageUrl", uri.toString());
                        Log.i("asd","URL " + uri.toString());
                        // mDriverDatabase.updateChildren(newImage);
                        userStoryDb.updateChildren(newImage);


                        Intent intent = new Intent(ShowCaptureActivity.this, ArkadasSecActivity.class);
                        intent.putExtra("url",uri.toString());
                        startActivity(intent);

                        finish();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        finish();
                        return;
                    }
                });
            }
        });
    }

    //daha önceden 90 derece olarak döndürdüğümüz resim çekildiğinde yan görüktüğü için
    private Bitmap rotate(Bitmap decodedBitmap) {
        int w = decodedBitmap.getWidth();
        int h = decodedBitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        return Bitmap.createBitmap(decodedBitmap,0,0,w,h,matrix,true);
    }



    private void onImageFromCameraClick22(){
        Intent intent = new Intent(this, PuzzleActivity.class);

        String filePathh= tempFileImage(this,rotateBitmap,"name");
        intent.putExtra("mCurrentPhotoPath", filePathh);
        intent.putExtra("deger",parcaSayisi);
        startActivity(intent);
    }
}
