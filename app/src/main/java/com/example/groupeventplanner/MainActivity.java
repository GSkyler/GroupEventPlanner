package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView groupNameTextView;
    private ListView messageListView;
    private ArrayList<String> messages;
    private TextView usernameTextView;
    private EditText messageEditText;

//  change to paramater passed in from prev activity
    private String username = "TestUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messages = new ArrayList<>();

        groupNameTextView = findViewById(R.id.groupNameTextView);
        messageListView = findViewById(R.id.messageListView);
        usernameTextView = findViewById(R.id.usernameTextView);
        messageEditText = findViewById(R.id.messageEditText);
        messageEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    setUserMessage();
                    return true;
                }
                return false;
            }
        });

        db.collection("groups").document("Example Group 1").collection("People")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        updateMessages();
                    }
                });

        updateGroupName();
        updateMessages();
        updateMessageListView();

    }

    public void updateGroupName(){
        db.collection("groups").document("Example Group 1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Map<String, Object> data = document.getData();
                                String groupName = (String)data.get("name");
                                groupNameTextView.setText(groupName);
                            }
                        }
                    }
                });
    }

    public void updateMessages(){
//  change document from Example Group 1 to a document parameter passed in from previous menus
        db.collection("groups").document("Example Group 1").collection("People")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            messages.clear();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
                                if(!(data.get("name").equals(username))) {
                                    messages.add(data.get("name") + ": " + data.get("message"));
                                }
                                else{
                                    usernameTextView.setText((String)data.get("name"));
                                    messageEditText.setText((String)data.get("message"));
                                }
                            }
                            updateMessageListView();
                        }
                    }
                });
    }

    public void updateMessageListView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, messages);
        messageListView.setAdapter(arrayAdapter);
    }

    public void setUserMessage(){
        String newMessage = messageEditText.getText().toString();

        DocumentReference userRef = db.collection("groups").document("Example Group 1").collection("People").document("TestUser");
        userRef.update("message", newMessage);

    }

}
