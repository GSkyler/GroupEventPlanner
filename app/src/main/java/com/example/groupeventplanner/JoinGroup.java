package com.example.groupeventplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class JoinGroup extends AppCompatActivity {

    private String username = "TestUser";
    private EditText groupNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if(data != null) {
            username = data;
        }

        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    joinGroup(groupNameEditText.getText().toString());
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

    public void joinGroup(String groupName){

    }

}
