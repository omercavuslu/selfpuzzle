package com.omercavuslu.selfpuzzle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.omercavuslu.selfpuzzle.Adapter.MessageAdapter;
import com.omercavuslu.selfpuzzle.Fragments.APIService;
import com.omercavuslu.selfpuzzle.Model.Chat;
import com.omercavuslu.selfpuzzle.Model.User;
import com.omercavuslu.selfpuzzle.Notifications.Client;
import com.omercavuslu.selfpuzzle.Notifications.Data;
import com.omercavuslu.selfpuzzle.Notifications.MyResponse;
import com.omercavuslu.selfpuzzle.Notifications.Sender;
import com.omercavuslu.selfpuzzle.Notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.omercavuslu.selfpuzzle.CameraFragment.tempFileImage;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    CircleImageView profile_image;
    TextView username;
    FirebaseUser fuser;
    DatabaseReference reference;
    ImageButton btn_send;
    EditText text_send;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    Intent intent;
    ArrayList<String> keys;
    ValueEventListener seenListener;
    String userid;
    APIService apiService;
    public String url;
    int gelen;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        gelen = intent.getIntExtra("gelen",3);
        Log.i("aasd","messageURL GELEN INT "+gelen);
        url=intent.getStringExtra("url");

        if (gelen==4){
            sendMessage(fuser.getUid(), userid, url,true);
            Log.i("reycler View ","1." );





            Log.i("resmmi","EVET ");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {//GERİ TUŞU
            @Override
            public void onClick(View view) {
                // and this
                startActivity(new Intent(MessageActivity.this, Main3Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.txt_send);




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg,false);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });




        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.ic_strategy_thought);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
/*
       recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                Log.i("URL","TIKLADIN REİS");
                reference = FirebaseDatabase.getInstance().getReference("Chats");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Chat chat = snapshot.getValue(Chat.class);

                            if (chat.isResimmi()) {

                                Glide.with(MessageActivity.this)
                                        .asBitmap()
                                        .load(chat.getMessage())
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                //imageView.setImageBitmap(resource);
                                                onImageFromCameraClick222(resource);

                                            }
                                        });

                            }




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                Log.i("URL","TIKLADIN REİS 2.ye");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
                Log.i("URL","TIKLADIN REİS 3. ye");
            }
        });*/
/*
    recyclerView.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //int itemPosition = recyclerView.getChildLayoutPosition(v);
           // String item = mList.get(itemPosition);
            //Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
            Log.i("URL","bu be  REİS 3. ye");
        }
    });*/

        mchat = new ArrayList<>();
        keys = new ArrayList<String>();

        mchat.clear();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    //Log.i(TAG,"tıklandı position     "+ position);
                    Log.i(TAG,"tıklandı mchatsize   " + mchat.size());
                    if (chat.isResimmi()) {
                        mchat.add(chat);
                        keys.add(snapshot.getKey());


                    }else{
                        mchat.add(null);
                        keys.add(null);
                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView.addOnItemTouchListener(

                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, final int position) {

                       // Toast.makeText(MessageActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                        Log.i("glide içi",mchat.toString());
                        Log.i("glide içi",keys.toString());


                        if (mchat.get(position)!=null )
                        {
                            Log.i("saveImage","glide içi IF İÇİ NULL DEPİL "+keys.size()+"  "+(position));
                            Log.i("saveImage","Girdiasdasd "+mchat.get(position));
                            Glide.with(MessageActivity.this)
                                    .asBitmap()
                                    .load(mchat.get(position).getMessage())
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            //imageView.setImageBitmap(resource);
                                            onImageFromCameraClick222(resource,keys.get(position));
                                            Log.i("saveImage","glide içi "+keys.size()+"  "+(position));
                                            //saveImage(resource,keys.get(position));

                                        }
                                    });
                        }
                        else{
                            Log.i("saveImage","glide içi IF İÇİ NULL  "+keys.size()+"  "+(position));

                        }





                    }
                })
        );


        seenMessage(userid);



    }
    private String saveImage(Bitmap image,String name) {
        Log.i("saveImage","Girdi "+ name);
        String savedImagePath = null;

        //String imageFileName = "JPEG_" + "FILE_NAME" + ".jpg";
        String imageFileName = name + ".jpg";
        File storageDir = new File(            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/selfpuzzle");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
            Log.i("saveImage","!success "+success);
        }
        if (success) {
            Log.i("saveImage","success");
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                Log.i("saveImage","try");
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(savedImagePath);
            Toast.makeText(this, "IMAGE SAVED", Toast.LENGTH_LONG).show();
            Log.i("saveImage","image saved");
        }
        return savedImagePath;
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }





    private void onImageFromCameraClick222(Bitmap btmp,String name){
        Intent intent = new Intent(this, PuzzleActivity.class);

        String filePathh= tempFileImage(this,btmp,"name");
        intent.putExtra("mCurrentPhotoPath", filePathh);
        intent.putExtra("name",name);
       // intent.putExtra("deger",parcaSayisi);
        startActivity(intent);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message,boolean resimmi){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("resimmi",resimmi);

        reference.child("Chats").push().setValue(hashMap);


        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotifiaction(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotifiaction(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "Yeni Mesaj",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200){
                                        Log.i(TAG,"Notification göndermede hata var responce code " +response.body().success );
                                        if (response.body().success != 1){
                                           // Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG,"Notification göndermede hata var success !=");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readMesagges(final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    Log.i("Resmmi",chat.isResimmi()+"");
                    if (chat.isResimmi()){
                        Log.i("URl",""+chat.getMessage());
                        /*Glide.with(MessageActivity.this)
                                .asBitmap()
                                .load(chat.getMessage())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                        //imageView.setImageBitmap(resource);
                                        //onImageFromCameraClick222(resource);

                                    }
                                });*/

                    }

                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                        Log.i("aasd","GELEN MESAJ "+mchat);
                    }


                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

}
