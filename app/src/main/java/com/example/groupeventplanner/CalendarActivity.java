package com.example.groupeventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

//  CalendarView name changed to Calendar from CalendarDate
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CalendarView calendar;
    private String Date;
    private String username = "TestUser";
    private String groupName = "Example Group 1";
    private ArrayList<String> yourdates = new ArrayList<>();
    private ListView yourdatesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        if(data != null) {
            username = data[0];
            groupName = data[1];
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        calendar = findViewById(R.id.Cal);

        yourdatesListView = findViewById(R.id.yourdatesListView);
        Dates();


        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Date = (month+1) + "/" + dayOfMonth + "/" + year;
                updateDate();
            }
        });
    }

    // Adds a date Available
    public void updateDate(){
        final DocumentReference userRef = db.collection("groups").document(groupName).collection("People").document(username);
        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> data = document.getData();
                            assert data != null;
                            ArrayList<String> datesAvailable = (ArrayList<String>) data.get("DatesAvailable");
                            if(datesAvailable.contains(Date)){
                                userRef.update("DatesAvailable", FieldValue.arrayRemove(Date));
                            }
                            else{
                                userRef.update("DatesAvailable", FieldValue.arrayUnion(Date));
                            }
                        }
                        Dates();
                    }
                });
    }

    public void backToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        intent.putExtra("data", data);
        startActivity(intent);
    }


    public void Dates(){
        yourdates.clear();
        db.collection("groups").document(groupName).collection("People").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
                                if((data.get("name").equals(username))) {
                                    for (String date : (ArrayList<String>) data.get("DatesAvailable")){
                                        yourdates.add(date);
                                        System.out.println(date);
                                    }
                                }

                            }

                        }
                        Datesout();
                    }});

    }

    public void Datesout(){
        System.out.println(yourdates);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, yourdates);
        yourdatesListView.setAdapter(arrayAdapter);
    }


}