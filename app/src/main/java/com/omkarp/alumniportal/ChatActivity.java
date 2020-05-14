package com.omkarp.alumniportal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omkarp.alumniportal.adapters.AdapterChat;
import com.omkarp.alumniportal.models.ModelChat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    //declare views
    Toolbar chatToolbar;
    RecyclerView recyclerView;
    ImageView chatProfileIV;
    EditText chatET;
    ImageButton sendBTN;
    TextView chatNameTV, onlineStatusTV;

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userReference;

    //check if seen or not
    ValueEventListener seenListener;
    DatabaseReference seenReference;

    //chat
    List<ModelChat> chatList;
    AdapterChat adapterChat;

    //get IDs
    String userUID;
    String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //initialise views
        chatToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(chatToolbar);
        chatToolbar.setTitle("");
        recyclerView = findViewById(R.id.chatRecyclerView);
        chatProfileIV = findViewById(R.id.chatProfileIV);
        chatET = findViewById(R.id.chatET);
        chatNameTV = findViewById(R.id.chatNameTV);
        sendBTN = findViewById(R.id.sendBTN);
        onlineStatusTV = findViewById(R.id.onlineStatusTV);

        //linear layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        //recyclerview properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //intent
        Intent intent = getIntent();
        userUID = intent.getStringExtra("userUID");

        //firebase inti
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userReference = firebaseDatabase.getReference("Users");

        //query
        Query query = userReference.orderByChild("uid").equalTo(userUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String profilePicture = "" + ds.child("profilePicture").getValue();

                    chatNameTV.setText(name);
                    try {
                        Picasso.get().load(profilePicture).into(chatProfileIV);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_add_image).into(chatProfileIV);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatET.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    sendMessage(message);
                } else {
                    Toast.makeText(ChatActivity.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        readMessages();

        seenMessages();
    }

    private void seenMessages() {
        seenReference = firebaseDatabase.getReference("Chat");
        seenListener = seenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReciever().equals(myUID) && chat.getSender().equals(userUID)) {
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dBRef = firebaseDatabase.getReference("Chat");
        dBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReciever().equals(myUID) && chat.getSender().equals(userUID) ||
                            chat.getReciever().equals(userUID) && chat.getSender().equals(myUID)) {
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList);
                    adapterChat.notifyDataSetChanged();

                    //set adapter
                    recyclerView.setAdapter(adapterChat);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String message) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference databaseReference = firebaseDatabase.getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUID);
        hashMap.put("reciever", userUID);
        hashMap.put("message", message);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chat").push().setValue(hashMap);

        chatET.setText("");
    }

    private void checkUserStatus() {
        if (user != null) {
            //stay here
            myUID = user.getUid();
        } else {
            //goto mainactivity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.search_action).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        seenReference.removeEventListener(seenListener);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_action) {
            mAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
