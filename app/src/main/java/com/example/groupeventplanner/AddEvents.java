package com.example.groupeventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AddEvents extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> commonDates;
    private String username = "TestUser";
    private String groupName = "Example Group 1";
    private ListView commonDatesListView;
    private EditText eventnameEditText;
    private EditText dateEditText;
    private EditText eventinfoEditText;
    private EditText detailsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevents);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        if(data != null) {
            username = data[0];
            groupName = data[1];
        }
        commonDatesListView = findViewById(R.id.DatesList);
        commonDates = new ArrayList<>();
        eventnameEditText  = findViewById(R.id.eventnameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        eventinfoEditText = findViewById(R.id.eventinfoEditText);
        detailsEditText = findViewById(R.id.detailsEditText);
        eventnameEditText.setHint("Event Name");
        dateEditText.setHint("Date");
        detailsEditText.setHint("Details");
        eventinfoEditText.setHint("Description");
        updateCommonDates();

        commonDatesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String NewDate = commonDatesListView.getItemAtPosition(position).toString();
                dateEditText.setText(NewDate);
            }
        });
    }

    public void updateCommonDates(){
        //                  *****HANDLE EMPTY "DATESAVAILABLE" ARRAY CASE*****
        //  change document from Example Group 1 to a document parameter passed in from previous menus
        db.collection("groups").document(groupName).collection("People")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            commonDates.clear();
                            boolean firsttime = true;
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
                                if(data.get("DatesAvailable") != null) {
                                    if (firsttime) {
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
                                    if (firsttime) firsttime = !firsttime;
                                }
                            }
                        }
                    }
                });
        updateCommonDatesListView();
    }

    public void backToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void EventAdd(View view){
        System.out.println("Ok we got here");
        String EventName = eventnameEditText.getText().toString();
        String Details = detailsEditText.getText().toString();
        String Date = dateEditText.getText().toString();
        String Description = eventinfoEditText.getText().toString();
        if(!eventinfoEditText.equals("") && !detailsEditText.equals("") && !dateEditText.equals("") && !eventnameEditText.equals("")){
            Map<String, Object> Event = new HashMap<>();
            Event.put("name", EventName);
            Event.put("Creator", username);
            Event.put("Date",  Date);
            Event.put("Details", Details);
            Event.put("Description", Description );
            System.out.println("OK man we still going strong");
            db.collection("groups").document(groupName).collection("Events").document(EventName).set(Event);
            System.out.println("asdsdfsdf");
        }

    }

    public void updateCommonDatesListView(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.msgtextview, commonDates);
        commonDatesListView.setAdapter(arrayAdapter);
    }

}

