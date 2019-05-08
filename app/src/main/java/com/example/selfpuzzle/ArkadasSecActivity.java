package com.example.selfpuzzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.selfpuzzle.CameraFragment.tempFileImage;

public class ArkadasSecActivity extends AppCompatActivity {

    private UserAdapter userAdapter;
    EditText search_users ;
    RecyclerView recyclerView;
    TextView txt_arkadas_sec;
    private List<User> mUsers;
    public static String idd;
    int parcaSayisi;
    String url;
    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arkadas_sec);
        txt_arkadas_sec = findViewById(R.id.txt_arkadas_sec);
        search_users = findViewById(R.id.arkadasSec_search_users);
        recyclerView = findViewById(R.id.arkadasSec_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        Log.i("asd","URL ARKADAS SEC "+url);
        readUsers();

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private static final String TAG = "myApp";

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        //Query query = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid()).orderByChild("receiver").equalTo("kabul");
        Log.i(TAG,"Yapmaya calistigim dıs ");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (search_users.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final User user = snapshot.getValue(User.class);
                        //final Query query = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid()).orderByChild(user.getId()).equalTo("kabul");
                        Query query = mDatabase.child("Friends").child(firebaseUser.getUid()).orderByChild(user.getId()).equalTo("kabul");
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            Log.i(TAG,"Yapmaya calistigim value iç ");

                            query.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                        // do something with the individual "issues"
                                        idd  = issue.getValue().toString().substring(1,29);
                                        Log.i(TAG,"Yapmaya calistigim      b  "+idd);
                                        Log.i(TAG,"Yapmaya calistigim      a  "+user.getId());
                                        mUsers.add(user);
                                        recyclerView.setAdapter(userAdapter);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            recyclerView.setAdapter(userAdapter);
                            Log.i(TAG,"Yapmaya calıstıgım ife sokmadan "+idd);
                            if (user.getId().equals(idd)){

                                Log.i(TAG, "yapma TRUEEEEEE");
                            }

                        }

                    }

                    userAdapter = new UserAdapter(ArkadasSecActivity.this, mUsers, false,4,url);
                    recyclerView.setAdapter(userAdapter);
                    Log.i(TAG,"URL USER ADAPTERE YOLLANAN "+url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ResmiFireDanAl() {
        ImageView gecici = findViewById(R.id.gecici);


        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/selfpuzzle-32f6c.appspot.com/o/captures%2F-LdZGY2I85HEEJFb-4y_?alt=media&token=122bb576-3f76-40e7-9427-5317229654e4").into(gecici);
        gecici.buildDrawingCache();
        Bitmap bmap = gecici.getDrawingCache();


        Intent intent = new Intent(this, PuzzleActivity.class);

        String filePathh= tempFileImage(this,bmap,"name");
        intent.putExtra("mCurrentPhotoPath", filePathh);
        intent.putExtra("deger",parcaSayisi);
        startActivity(intent);

    }




}
