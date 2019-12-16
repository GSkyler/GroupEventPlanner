package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView groupNameTextView;
    private ListView messageListView;
    private ArrayList<String> messages;
    private ArrayList<String> commonDates;
    private TextView usernameTextView;
    private EditText messageEditText;
    private ListView commonDatesListView;
    private Button backBtn;

//  change to paramater passed in from prev activity
    private String username = "TestUser";
    private String groupName = "Example Group 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        if(data != null) {
            username = data[0];
            groupName = data[1];
        }

        messages = new ArrayList<>();
        commonDates = new ArrayList<>();

        backBtn = findViewById(R.id.backBtn);
        groupNameTextView = findViewById(R.id.groupNameTextView);
        messageListView = findViewById(R.id.messageListView);
        commonDatesListView = findViewById(R.id.commonDatesListView);
        usernameTextView = findViewById(R.id.usernameTextView);
        messageEditText = findViewById(R.id.messageEditText);
        messageEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    setUserMessage();
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

        db.collection("groups").document(groupName).collection("People")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        updateMessages();
                        updateCommonDates();
                    }
                });

        updateGroupName();
        updateMessages();
        updateCommonDates();

    }

    public void goToCalendar(View view){
        Intent myintent = new Intent(this, CalendarActivity.class);
        String[] data = {username, groupName};
        myintent.putExtra("data", data);
        startActivity(myintent);
    }
    public void backToGroupList(View view){
        Intent myintent = new Intent(this, GroupList.class);
        String data = username;
        myintent.putExtra("data", data);
        startActivity(myintent);
    }

    public void updateGroupName(){
        db.collection("groups").document(groupName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document != null){
                                Map<String, Object> data = document.getData();
                                String name = (String)data.get("name");
                                groupNameTextView.setText(name);
                                groupName = name;
                            }
                        }
                    }
                });
    }

    public void updateMessages(){
//  change document from Example Group 1 to a document parameter passed in from previous menus
        db.collection("groups").document(groupName).collection("People")
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
                                    String username = (String)data.get("name") + ":";
                                    usernameTextView.setText(username);
                                    messageEditText.setText((String)data.get("message"));
                                }
                            }
                            updateMessageListView();
                        }
                    }
                });
    }

    public void updateCommonDates(){
        //                  *****HANDLE EMPTY "DATESAVAILABLE" ARRAY CASE*****
        db.collection("groups").document(groupName).collection("People")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            commonDates.clear();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
                                if(data.get("DatesAvailable") != null) {
                                    if (commonDates.size() == 0) {
                                        commonDates.addAll((ArrayList<String>) data.get("DatesAvailable"));
                                    }
                                    else{
                                        ArrayList<String> userDates = (ArrayList<String>) data.get("DatesAvailable");
                                        HashSet<String> set = new HashSet<>(commonDates);
                                        assert userDates != null;
                                        set.retainAll(userDates);
                                        commonDates.clear();
                                        commonDates.addAll(set);
                                    }
                                }
                            }
                            updateCommonDatesListView();
                        }
                    }
                });
    }

    public void updateMessageListView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, messages);
        messageListView.setAdapter(arrayAdapter);
    }

    public void updateCommonDatesListView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, commonDates);
        commonDatesListView.setAdapter(arrayAdapter);
    }

    public void setUserMessage(){
        String newMessage = messageEditText.getText().toString();

        DocumentReference userRef = db.collection("groups").document(groupName).collection("People").document(username);
        userRef.update("message", newMessage);

    }

}
