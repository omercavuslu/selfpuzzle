package com.example.selfpuzzle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.selfpuzzle.Adapter.UserAdapter;
import com.example.selfpuzzle.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserActivity extends AppCompatActivity {
    private static final String TAG = "myApp";
    String userid;
    Intent intent;
    FirebaseUser fuser;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    TextView username;
    CircleImageView image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);
        username = findViewById(R.id.username_other_user);
         image_profile = findViewById(R.id.profile_image_other_user);

        final Button btn_arkadas_ekle  = findViewById(R.id.btn_arkadas_ekle);
        btn_arkadas_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arkadasEkleme();
                btn_arkadas_ekle.setText("Arkadaşlık İsteği Gönderildi");
                btn_arkadas_ekle.setClickable(false);
            }
        });
        TextView textView2 = findViewById(R.id.textView2);
        intent = getIntent();
        userid = intent.getStringExtra("userid");

             //recyclerView = findViewById(R.id.arkadasIstekleri);
        // recyclerView.setHasFixedSize(true);

        //kendi uid mizi almak için
        fuser = FirebaseAuth.getInstance().getCurrentUser();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
               // Log.i(TAG,"OTHERUSERACTIVITY"+user.getUsername());
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(OtherUserActivity.this).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void arkadasEkleme(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", fuser.getUid());
        hashMap.put("receiver", userid);


        mDatabase.child("FriendsReq").child("send").push().setValue(hashMap);


    }

    private void arama(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OtherUserActivity post = dataSnapshot.getValue(OtherUserActivity.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

/*
    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }

                    }

                   // userAdapter = new UserAdapter(OtherUserActivity, mUsers, false);
                    recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
