package com.example.groupeventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class EventDetails extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String username;
    public String groupName;
    public String evtName;
    public String Description;
    public String Creator;
    public String Date;
    public String Details;

    TextView CreatorView;
    TextView DateView;
    TextView DescriptionView;
    TextView DetailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdetails);
        Intent intent = getIntent();
        String[] data = intent.getStringArrayExtra("data");
        if(data != null) {
            username = data[0];
            groupName = data[1];
            evtName = data[2];
        }
        TextView IDK = findViewById(R.id.NameOfThing);
        CreatorView = findViewById(R.id.CreatorView);
         DateView = findViewById(R.id.DateView);
         DescriptionView = findViewById(R.id.DescriptionView);
         DetailsView = findViewById(R.id.DetailsView);
        IDK.setText(evtName);
        System.out.println(evtName);

        db.collection("groups").document(groupName).collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
                                if((data.get("name").equals(evtName))) {
                                    Creator = data.get("Creator").toString();
                                    Date = data.get("Date").toString();
                                    Description = data.get("Description").toString();
                                    Details = data.get("Details").toString();
                                    System.out.println(Description);
                                    Display();
                                }

                            }

                        }
                    }

                });

        Display();
    }

    public void Display(){
        CreatorView.setText(Creator);
        DateView.setText(Date);
        DescriptionView.setText(Description);
        DetailsView.setText(Details);
    }

    public void backToMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        intent.putExtra("data", data);
        startActivity(intent);
    }

    public void DeleteEvent(View view){
        if (Creator.equals(username)){
            db.collection("groups").document(groupName).collection("Events").document(evtName).delete();
            System.out.println("good");
        }

    }
}
