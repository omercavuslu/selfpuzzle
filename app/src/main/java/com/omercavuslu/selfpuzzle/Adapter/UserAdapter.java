package com.omercavuslu.selfpuzzle.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.omercavuslu.selfpuzzle.ArkadasKabulActivity;
import com.omercavuslu.selfpuzzle.OtherUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omercavuslu.selfpuzzle.MessageActivity;
import com.omercavuslu.selfpuzzle.Model.Chat;
import com.omercavuslu.selfpuzzle.Model.User;
import com.omercavuslu.selfpuzzle.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "myApp";
    private String mCurrent_state;
    private Context mContext;
    private int gelen;
    private List<User> mUsers;
    private boolean ischat;
    private String url;
    static boolean resimmi;


    String theLastMessage;

    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat, int gelen,String url){

        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
        this.url = url;
        this.gelen = gelen;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        Log.i(TAG,"UserAdapter getUid "+user.getId());
        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.ic_strategy_thought);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat){
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        mCurrent_state = "not_friends";
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gelen==2){
                    Intent intent = new Intent(mContext, ArkadasKabulActivity.class);
                    intent.putExtra("gonderenID",user.getId());

                    Log.i(TAG,"User Adapter 2 "+user.getId());
                    mContext.startActivity(intent);
                }
                else if (gelen ==1) {
                    //users dan kişi seçildiğinde message activitye atmakta
                    // Intent intent = new Intent(mContext, MessageActivity.class);
                    Intent intent = new Intent(mContext, OtherUserActivity.class);

                    intent.putExtra("userid", user.getId());
                    mContext.startActivity(intent);
                }
                else if (gelen==3){
                    Intent intent = new Intent(mContext, MessageActivity.class);

                    intent.putExtra("userid", user.getId());
                    intent.putExtra("gelen",gelen);
                    mContext.startActivity(intent);
                }
                else if (gelen==4){
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userid", user.getId());
                    intent.putExtra("gelen",gelen);
                    intent.putExtra("url",url);
                    Log.i("asd", "URL MESSAGEYE YOLLANAN "+url);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                            resimmi = chat.isResimmi();
                            Log.i("lastmessage"," "+resimmi);
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        if (resimmi == true) {

                            last_msg.setText("Kullanıcı size bir resim gönderdi");
                        }
                        else{
                            last_msg.setText(theLastMessage);
                        }

                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
