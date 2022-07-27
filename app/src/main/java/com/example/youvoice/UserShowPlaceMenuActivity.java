package com.example.youvoice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserShowPlaceMenuActivity extends Activity {

    Button add_compliant_btn, compliants_btn, add_suggestion_btn, suggestions_btn, add_evaluation_btn, evaluations_btn;
    Button my_suggestions_btn, my_evaluations_btn;

    // the place id from the previous activity
    String place_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_place_menu);

        add_compliant_btn = (Button) findViewById(R.id.add_compliant_btn);
        compliants_btn = (Button) findViewById(R.id.compliants_btn);
        add_suggestion_btn = (Button) findViewById(R.id.add_suggestion_btn);
        suggestions_btn = (Button) findViewById(R.id.suggestions_btn);
        add_evaluation_btn = (Button) findViewById(R.id.add_evaluation_btn);
        evaluations_btn = (Button) findViewById(R.id.evaluations_btn);
        my_suggestions_btn = (Button) findViewById(R.id.my_suggestions_btn);
        my_evaluations_btn = (Button) findViewById(R.id.my_evaluations_btn);

        // get place_id from previous activity
        place_id = getIntent().getStringExtra("place_id");

        add_compliant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserAddPlaceCompliantActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        compliants_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserShowPlaceCompliantsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        add_suggestion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserAddPlaceSuggestionActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        suggestions_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserShowPlaceSuggestionsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        add_evaluation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserAddPlaceEvaluationActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        evaluations_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserShowPlaceEvaluationsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        my_suggestions_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserShowPlaceMySuggestionsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });

        my_evaluations_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(UserShowPlaceMenuActivity.this, UserShowPlaceMyEvaluationsActivity.class);
                in.putExtra("place_id", place_id);
                startActivity(in);
            }
        });
    }

    // if the user click the back button
    @Override
    public void onBackPressed() {
        Intent in = new Intent(UserShowPlaceMenuActivity.this, UserShowPlacesActivity.class);
        startActivity(in);
    }
}
