package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JoinGroup extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username = "TestUser";
    private EditText groupNameEditText;
    private TextView updatesTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if(data != null) {
            username = data;
        }

        updatesTextview = findViewById(R.id.updatesTextview);
        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    if(!groupNameEditText.getText().toString().isEmpty()) {
                        joinGroup(groupNameEditText.getText().toString());
                    }
                    try  {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                    return true;
                }
                return false;
            }
        });

    }

    public void joinGroup(final String groupName){
        final DocumentReference groupRef = db.collection("groups").document(groupName);
        groupRef.get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        addUserToGroup(groupName);
                        db.collection("users").document(username).update("groups", FieldValue.arrayUnion(groupName));
                        goToMainActivity(groupName);
                    }
                    else{
                        updatesTextview.setText("Group doesn't exist");
                    }
                }
            }
        });

    }

    public void addUserToGroup(String groupName){
        final Map<String, Object> user = new HashMap<>();
        user.put("name", username);
        user.put("DatesAvailable", Collections.emptyList());
        user.put("message", "Hello!");

        db.collection("groups").document(groupName).collection("People").document(username).set(user);
    }

    public void goToMainActivity(String groupName){
        Intent intent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        intent.putExtra("data", data);
        startActivity(intent);
    }

}
