package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

//  CalendarView name changed to Calendar from CalendarDate
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CalendarView calendar;
    private String Date;
    private String username = "TestUser";
    private String groupName = "Example Group 1";

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
                    }
                });
        //        userRef.update("DatesAvailable", FieldValue.arrayUnion(Date));
    }

    public void backToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        intent.putExtra("data", data);
        startActivity(intent);
    }


}