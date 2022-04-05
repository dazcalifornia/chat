package com.example.chattapp20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    private TextView textView;
    private EditText editText;
    private Button button;

    private String user_name, room_name;

    private DatabaseReference root;

    private String temp_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        initializeViews();

        user_name = getIntent().getExtras().get("user_name").toString();
        room_name = getIntent().getExtras().get("room_name").toString();

        root = FirebaseDatabase.getInstance().getReference().child(room_name);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generate the unique key for each user
                Map<String, Object> uniqueKeyMap = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(uniqueKeyMap);

                DatabaseReference ref = root.child(temp_key);
                Map<String, Object> userMessageMap = new HashMap<String, Object>();
                userMessageMap.put("message",editText.getText().toString());
                userMessageMap.put("name", user_name);
                ref.updateChildren(userMessageMap);

                editText.setText("");
                editText.requestFocus();
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                retrieveMessages(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                retrieveMessages(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void retrieveMessages(DataSnapshot snapshot) {

        String userName, message;

        Iterator iterator = snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            message = (String) ((DataSnapshot)iterator.next()).getValue();
            userName = (String) ((DataSnapshot)iterator.next()).getValue();
            textView.append(userName+": "+ message+"\n\n");
        }
    }

    private void initializeViews() {
        textView = findViewById(R.id.message_tv);
        editText = findViewById(R.id.message_et);
        button = findViewById(R.id.message_btn);
    }
}