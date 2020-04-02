package com.example.productivitybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class StatusBuilderActivity extends AppCompatActivity {
    private String name = "";
    private EditText nameField;
    private String message = "";
    private EditText messageField;
    private String escapeMessage = "";
    private EditText escapeMessageField;
    private ArrayList<String> numbersToIgnore = new ArrayList<>(Arrays.asList("None"));
    private EditText numbersToIgnoreField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_builder);
        nameField = findViewById(R.id.status_nameField);
        messageField = findViewById(R.id.status_messageField);
        escapeMessageField = findViewById(R.id.status_keywordField);
        numbersToIgnoreField = findViewById(R.id.status_ntiField);
        //TODO: Replace editText fields with pop up dialog boxes like Remem-Birthday

        nameField.setText("Enter Status name here");
        messageField.setText("Set automated response here");
        escapeMessageField.setText("Enter your urgent keyword here");
        numbersToIgnoreField.setText("Enter numbers that can bypass the status here");
    }

    public void makeStatus(View view) {
        name = nameField.getText().toString();
        message = messageField.getText().toString();
        escapeMessage = escapeMessageField.getText().toString();
        if(!numbersToIgnoreField.getText().toString().trim().equals("")) {
            numbersToIgnore.clear();
            modifyArrayList(numbersToIgnoreField.getText().toString());
        }

        if (name.trim().equals("")) {
            name = "Default Status Name";
        }
        if (message.trim().equals("")) {
            message = "Hi there! this is an automated message.";
        }
        if (escapeMessage.trim().equals("")) {
            escapeMessage = "urgent";
        }
        Intent i = new Intent(this, ListOfStatuses.class);
        i.putExtra("name", name);
        i.putExtra("message", message);
        i.putExtra("escapeMessage", escapeMessage);
        i.putStringArrayListExtra("nti", numbersToIgnore);
        startActivity(i);
        finish();
    }
    private void modifyArrayList(String s) {
        Scanner sc = new Scanner(s);
        while(sc.hasNext()) {
            numbersToIgnore.add(sc.next());
        }
        //TODO: Convert from names to numbers using contacts once initial list is made (code past this line)
    }
}
