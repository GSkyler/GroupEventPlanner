package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class UserLogin extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText usernameEditText;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        textView = findViewById(R.id.textView);
    }

    public void signIn(View view){

    }

    public void checkDuplicateUsername(String username){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
//                            messages.clear();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
//                                if(!(data.get("name").equals(username))) {
//                                    messages.add(data.get("name") + ": " + data.get("message"));
//                                }
//                                else{
//                                    String username = (String)data.get("name") + ":";
//                                    usernameTextView.setText(username);
//                                    messageEditText.setText((String)data.get("message"));
//                                }
                            }
//                            updateMessageListView();
                        }
                    }
                });
    }

}
