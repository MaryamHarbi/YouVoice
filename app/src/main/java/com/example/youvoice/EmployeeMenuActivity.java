package com.example.youvoice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmployeeMenuActivity extends Activity {

    Button logout_btn, profile_btn, places_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_menu);

        logout_btn = (Button) findViewById(R.id.logout_btn);
        profile_btn = (Button) findViewById(R.id.profile_btn);
        places_btn = (Button) findViewById(R.id.places_btn);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EmployeeMenuActivity.this, MainActivity.class);
                startActivity(in);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EmployeeMenuActivity.this, EmployeeUpdateProfileActivity.class);
                startActivity(in);
            }
        });

        places_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(EmployeeMenuActivity.this, EmployeeShowPlacesActivity.class);
                startActivity(in);
            }
        });
    }

    // if the user click the back button
    // do nothing
    @Override
    public void onBackPressed() {

    }
}
