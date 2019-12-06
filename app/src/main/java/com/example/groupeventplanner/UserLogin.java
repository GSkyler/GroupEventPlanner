package com.example.groupeventplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText usernameEditText;
    private TextView textView;
    private String username;
//  REMOVE GROUPNAME; MAKE NEW ACTIVITY OF GROUP LISTS
    private String groupName = "Example Group 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        textView = findViewById(R.id.textView);
        usernameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
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
    }

    public void signIn(View view){
        checkUsernameDatabase(usernameEditText.toString(), true);
    }
    public void register(View view){
        checkUsernameDatabase(usernameEditText.toString(), false);
    }

    public void checkUsernameDatabase(final String name, final boolean signIn){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            boolean foundName = false;
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> data = document.getData();
                                System.out.println(data.get("name"));
                                if(data.get("name").equals(name)){
                                    System.out.println("name matches input");
                                    if(signIn){
                                        System.out.println("logging in");
                                        username = (String)data.get("name");
                                        foundName = true;
                                        goToGroup();
                                    }
                                    else{
                                        System.out.println("duplicate username");
                                        textView.setText("Username already taken!");
                                        foundName = true;
                                    }
                                }
                                if(foundName){
                                    return;
                                }
                            }
                            if(!foundName){
                                if(signIn){
                                    System.out.println("incorrect username");
                                    textView.setText("Username is incorrect");
                                }
                                else{
                                    System.out.println("creating new user");
                                    username = name;
                                    System.out.println("set username, creating newUser map");
                                    Map<String, Object> newUser = new HashMap<>();
                                    newUser.put("name", name);
                                    newUser.put("id", 77777);
                                    newUser.put("groups", Arrays.asList("Example Group 1"));
                                    System.out.println("calling addUser");
                                    addUser(newUser);
                                    goToGroup();
                                }
                            }
                        }
                    }
                });
    }

    public void goToGroup(){
        Intent myintent = new Intent(this, MainActivity.class);
        String[] data = {username, groupName};
        myintent.putExtra("data", data);
        startActivity(myintent);
    }

    public void addUser(Map<String, Object> newUser){
        System.out.println("in addUser, " + (String)newUser.get("name"));
        db.collection("users").document((String)newUser.get("name")).set(newUser);
    }

}
