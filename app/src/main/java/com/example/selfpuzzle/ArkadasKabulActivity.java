package com.example.selfpuzzle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.selfpuzzle.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArkadasKabulActivity extends AppCompatActivity {

    private static final String TAG = "myApp";
    Button btn_red;
    TextView username;
    CircleImageView image_profile;
    Button btn_kabul;
    Intent intent;
    String gonderenID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arkadas_kabul);

        username = findViewById(R.id.username_other_user_kabul);
        image_profile = findViewById(R.id.profile_image_other_user_kabul);
        btn_kabul = findViewById(R.id.btn_kabul);
        btn_red = findViewById(R.id.btn_red);

        intent = getIntent();
        gonderenID = intent.getStringExtra("gonderenID");
        Log.i(TAG,"Arkadas Kabul activity "+gonderenID);

        btn_kabul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Arkadas Kabul activity on click "+gonderenID);
                kabul();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(gonderenID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // Log.i(TAG,"OTHERUSERACTIVITY"+user.getUsername());
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    image_profile.setImageResource(R.drawable.ic_strategy_thought);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void kabul(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

      // mDatabase.child("Friends").child(firebaseUser.getUid()).setValue(gonderenID);
       // mDatabase.child("Friends").child(firebaseUser.getUid()).setValue(gonderenID);



        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Friends")
                .child(firebaseUser.getUid()).push()
                .child(gonderenID);
        chatRefReceiver.setValue("kabul");

        mDatabase.child("Friends").child(gonderenID).push().child(firebaseUser.getUid()).setValue("kabul");
        startActivity(new Intent(ArkadasKabulActivity.this, Main3Activity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        final Query query = FirebaseDatabase.getInstance().getReference("FriendsReq").child("send")
                .orderByChild("receiver")
                .equalTo(firebaseUser.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Log.i(TAG,"silinecek istek id si "+ child.getKey());
                    child.getRef().removeValue();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
