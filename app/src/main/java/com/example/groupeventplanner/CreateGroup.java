package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText groupNameEditText;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if(data != null) {
            username = data;
        }

        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    createGroup(groupNameEditText.getText().toString());
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

    public void createGroup(String groupName){
        Map<String, Object> newGroup = new HashMap<>();
        newGroup.put("name", groupName);
        db.collection("groups").document((String)newGroup.get("name")).set(newGroup);
        db.collection("groups").document((String)newGroup.get("name")).collection("Events").add(new HashMap<String, Object>());
        db.collection("groups").document((String)newGroup.get("name")).collection("People").add(new HashMap<String, Object>());

//        db.collection("users").document(username)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful() && task.getResult() != null){
//                            DocumentSnapshot document = task.getResult();
//                            Map<String, Object> data = document.getData();
//                            assert data != null;
//                            ArrayList<String> datesAvailable = (ArrayList<String>) data.get("DatesAvailable");
//                            if(datesAvailable.contains(Date)){
//                                userRef.update("DatesAvailable", FieldValue.arrayRemove(Date));
//                            }
//                            else{
//                                userRef.update("DatesAvailable", FieldValue.arrayUnion(Date));
//                            }
//                        }
//                    }
//                });
    }

    public void backToGroupList(View view){
        Intent intent = new Intent(this, GroupList.class);
        String data = username;
        intent.putExtra("data", data);
        startActivity(intent);
    }

}
