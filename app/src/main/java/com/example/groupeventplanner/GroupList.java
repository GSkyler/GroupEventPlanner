package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class GroupList extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username = "TestUser";
    private String groupName = "Example Group 1";
    private ListView groupsListView;
    private ArrayList<String> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if(data != null) {
            username = data;
        }

        groupsListView = findViewById(R.id.groupsListView);

        db.collection("users").document(username)
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> data = task.getResult().getData();
                    groups.addAll((ArrayList<String>) data.get("groups"));
                }
                updateGroupListView();
            }
        });
    }

    public void updateGroupListView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, groups);
        groupsListView.setAdapter(arrayAdapter);
        groupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                groupName = groups.get(i);
                goToGroup();
            }
        });
    }

    public void goToGroup(){
        Intent myintent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        myintent.putExtra("data", data);
        startActivity(myintent);
    }

    public void goToSignIn(View view){
        Intent intent = new Intent(this, UserLogin.class);
        startActivity(intent);
    }

}
