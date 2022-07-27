package com.example.youvoice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmployeeShowPlaceMenuActivity extends Activity {

    Button compliants_btn, suggestions_btn, evaluations_btn;

    // the place id from the previous activity
    String place_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_show_place_menu);

        compliants_btn = (Button) findViewById(R.id.compliants_btn);
        suggestions_btn = (Button) findViewById(R.id.suggestions_btn);
        evaluations_btn = (Button) findViewById(R.id.evaluations_btn);

        // get place_id from previous activity
        place_id = getIntent().getStringExtra("place_id");

        compliants_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(EmployeeShowPlaceMenuActivity.this, EmployeeShowPlaceCompliantsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        suggestions_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(EmployeeShowPlaceMenuActivity.this, EmployeeShowPlaceSuggestionsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        evaluations_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(EmployeeShowPlaceMenuActivity.this, EmployeeShowPlaceEvaluationsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });
    }

    // if the user click the back button
    @Override
    public void onBackPressed() {
        Intent in = new Intent(EmployeeShowPlaceMenuActivity.this, EmployeeShowPlacesActivity.class);
        startActivity(in);
    }
}
