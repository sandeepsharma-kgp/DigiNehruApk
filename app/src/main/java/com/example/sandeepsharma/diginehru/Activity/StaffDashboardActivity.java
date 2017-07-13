package com.example.sandeepsharma.diginehru.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sandeepsharma.diginehru.R;

public class StaffDashboardActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_food_entry;
    Button button_menu_entry;
    Button button_menu;
    Button button_statistic;
    Button button_change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        button_food_entry = (Button) findViewById(R.id.button_food_entry);
        button_menu_entry = (Button) findViewById(R.id.button_menu_entry);
        button_menu = (Button) findViewById(R.id.button_menu);
        button_statistic = (Button) findViewById(R.id.button_statistic);
        button_change_password = (Button) findViewById(R.id.button_change_password);


        button_food_entry.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_registration) {

        }
    }
}
