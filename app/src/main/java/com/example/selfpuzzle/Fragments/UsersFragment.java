package com.example.selfpuzzle.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.selfpuzzle.Main3Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.selfpuzzle.Adapter.UserAdapter;
import com.example.selfpuzzle.Model.User;
import com.example.selfpuzzle.R;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    EditText search_users;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUsers();

        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getId().equals(fuser.getUid())){
                        mUsers.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, false,1,"");
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private static final String TAG = "myApp";
    public static String idd;
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
                        Log.i(TAG,"yapma "+mUsers);
                    userAdapter = new UserAdapter(getContext(), mUsers, false,3,"");
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
