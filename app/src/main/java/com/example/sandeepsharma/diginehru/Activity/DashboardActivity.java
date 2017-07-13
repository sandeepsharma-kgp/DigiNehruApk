package com.example.sandeepsharma.diginehru.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sandeepsharma.diginehru.R;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_meal_register;
    Button button_recieve_meal;
    Button button_menu;
    Button button_history;
    Button button_change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        button_meal_register = (Button) findViewById(R.id.button_registration);
        button_recieve_meal = (Button) findViewById(R.id.button_receive_meal);
        button_menu = (Button) findViewById(R.id.button_menu);
        button_history = (Button) findViewById(R.id.button_history);
        button_change_password = (Button) findViewById(R.id.button_change_password);


        button_meal_register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_registration) {

        }
    }
}
