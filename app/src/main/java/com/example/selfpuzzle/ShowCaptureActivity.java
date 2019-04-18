package com.example.selfpuzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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

import static com.example.selfpuzzle.CameraFragment.tempFileImage;


public class ShowCaptureActivity extends AppCompatActivity {
    String filePath;
    String Uid;
    int parcaSayisi;
    Bitmap rotateBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);
        parcaSayisi=6;


        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        // seekBar.setProgress(2);
        //seekBar.incrementProgressBy(0);
        //seekBar.setMax(20);
        final TextView seekBarValue = (TextView)findViewById(R.id.seekBarValue);
        //seekBarValue.setText(tvRadius.getText().toString().trim());



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //progress = progress / 2;
                // progress = progress * 2;

                //parcaSayisi = progress;
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
            image.setImageBitmap(rotateBitmap);
        }
        Uid = FirebaseAuth .getInstance().getUid();
        Button mStory = findViewById(R.id.story);
        mStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToStories();
            }
        });
        Button mPuzzleYap = findViewById(R.id.puzzleyap);
        mPuzzleYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onImageFromCameraClick22();

            }
        });

    }
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
                        // mDriverDatabase.updateChildren(newImage);
                        userStoryDb.updateChildren(newImage);
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
    String mCurrentPhotoPath;

    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;
/*
    public void onImageFromCameraClick22() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (this.getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {//telefonda kamera olup olmadığını kontrol ediyor
            File photoFile = null;
            try {
                photoFile = createImageFile2();
            } catch (IOException e) {
                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile2() throws IOException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
            */

    //    imageFileName,  /* prefix */
    //          ".jpg",         /* suffix */
    //             storageDir      /* directory */
      /*      );
            mCurrentPhotoPath = image.getAbsolutePath(); // save this to use in the intent

            return image;
        }

        return null;
    }
*/
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, PuzzleActivity.class);




            intent.putExtra("mCurrentPhotoPath", rotateBitmap);
            startActivity(intent);
        }
    }*/

    private void onImageFromCameraClick22(){
        Intent intent = new Intent(this, PuzzleActivity.class);

        String filePathh= tempFileImage(this,rotateBitmap,"name");
        intent.putExtra("mCurrentPhotoPath", filePathh);
        intent.putExtra("deger",parcaSayisi);
        startActivity(intent);
    }
}
