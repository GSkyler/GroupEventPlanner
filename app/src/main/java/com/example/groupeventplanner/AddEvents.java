package com.example.groupeventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class AddEvents extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> commonDates;
    private String username = "TestUser";
    private String groupName = "Example Group 1";
    private ListView commonDatesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevents);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        if(data != null) {
            username = data[0];
            groupName = data[1];
        }
        commonDatesListView = findViewById(R.id.DatesList);
        commonDates = new ArrayList<>();
        updateCommonDates();

    }

    public void updateCommonDates(){
        //                  *****HANDLE EMPTY "DATESAVAILABLE" ARRAY CASE*****
        //  change document from Example Group 1 to a document parameter passed in from previous menus
        db.collection("groups").document("Example Group 1").collection("People")
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
                                        for (String date : (ArrayList<String>) data.get("DatesAvailable")){
                                            commonDates.add(date);
                                        }
                                    }
                                    else{
                                        ArrayList<String> userDates = (ArrayList<String>) data.get("DatesAvailable");
                                        HashSet<String> set = new HashSet<>();
                                        set.addAll(commonDates);
                                        set.retainAll(userDates);
                                        commonDates.clear();
                                        for(String date: set){
                                            commonDates.add(date);
                                        }
                                    }
                                }
                            }

                        }
                    }
                });
        updateCommonDatesListView();
    }

    public void backToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String[] data = {};
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void EventAdd(View view){
        System.out.println("asdsdfsdf");
    }

    public void updateCommonDatesListView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, commonDates);
        commonDatesListView.setAdapter(arrayAdapter);
        System.out.println("Done!!!!");
    }

}

