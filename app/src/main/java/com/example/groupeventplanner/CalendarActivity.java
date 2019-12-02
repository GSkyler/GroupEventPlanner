package com.example.groupeventplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CalendarView CalendarDate;
    private TextView Out;
    private String Date;
    private String username = "TestUser";
    private String groupName = "Example Group 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
//        String[] data = intent.getStringArrayExtra("data");
//        if(data != null) {
//            username = data[0];
//            groupName = data[1];
//        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        CalendarDate = findViewById(R.id.Cal);
        Out = findViewById(R.id.Date);

        CalendarDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Date = (month+1) + "/" + dayOfMonth + "/" + year;
                Out.setText(Date);
            }
        });
    }

    // Adds a date Available
    public void Date(View view){
        DocumentReference userRef = db.collection("groups").document("Example Group 1").collection("People").document(username);
        userRef.update("DatesAvailable", FieldValue.arrayUnion(Date));
    }



}