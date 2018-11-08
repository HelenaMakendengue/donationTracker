package edu.gatech.cs2340.donationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private DatabaseReference databaseUsers;
    private FirebaseAuth mAuth;

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private Spinner accountType;
    private static final Map<String, User> userDatabase = new HashMap<>();
    private static final List<String> accountTypes = Arrays.asList("Customer", "Admin", "Manager", "Location Employee");


    // String loginName, String password, boolean accountState, String contactInfo, AccountType accountType
    private User createUser(String name, String pass, String email, AccountType type) {

        String id = databaseUsers.push().getKey();
        User newUser = new User(name, pass, true, email, type);
        databaseUsers.child(Objects.requireNonNull(id)).setValue(newUser);
        return newUser;
    }

    private void createAuth(String user, String pass, String email, AccountType type) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user1 = mAuth.getCurrentUser();
                        Toast.makeText(RegistrationActivity.this, ":)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        userDatabase.put(email, createUser(user, pass, email, type));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button submit = findViewById(R.id.button_submit);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        accountType = findViewById(R.id.spinner);
        Button cancel = findViewById(R.id.button_cancel);

        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, accountTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountType.setAdapter(adapter);

        //Database References
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");


        submit.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();

            if ("".equals(username) || "".equals(password) || "".equals(email)) {
                Toast.makeText(getApplicationContext(), "One or more empty field(s)", Toast.LENGTH_SHORT).show();
            } else if (userDatabase.containsKey(username)) {
                Toast.makeText(getApplicationContext(), "Username taken, please try again", Toast.LENGTH_SHORT).show();
            } else if ("Admin".equals(accountType.getSelectedItem().toString()) && password.contains("Ez7R")) {
                // admin pass case
                createAuth(username, password, email, AccountType.ADMIN);
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            } else if ("Admin".equals(accountType.getSelectedItem().toString())) {
                Toast.makeText(getApplicationContext(), "Admin Permissions Not Granted", Toast.LENGTH_SHORT).show();
            } else if ("Manager".equals(accountType.getSelectedItem().toString()) && password.contains("QffJ")) {
                // manager pass case
                createAuth(username, password, email, AccountType.MANAGER);
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            } else if ("Manager".equals(accountType.getSelectedItem().toString())) {
                Toast.makeText(getApplicationContext(), "Manager Permissions Not Granted", Toast.LENGTH_SHORT).show();
            } else if ("Location Employee".equals(accountType.getSelectedItem().toString()) && password.contains("OIU8")) {
                // location employee pass case
                createAuth(username, password, email, AccountType.LOCATION_EMPLOYEE);
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            } else if ("Location Employee".equals(accountType.getSelectedItem().toString())) {
                Toast.makeText(getApplicationContext(), "Location Employee Permissions Not Granted", Toast.LENGTH_SHORT).show();
            } else {
                // default - customer
                createAuth(username, password, email, AccountType.CUSTOMER);
            }
        });

        cancel.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this, WelcomeActivity.class)));
    }
}
