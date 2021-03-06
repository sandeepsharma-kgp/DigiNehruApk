package com.example.sandeepsharma.diginehru.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
import com.example.sandeepsharma.diginehru.Frameworks.NetworkCallBack;
import com.example.sandeepsharma.diginehru.Frameworks.Response;
import com.example.sandeepsharma.diginehru.Network.LoginNetwork;
import com.example.sandeepsharma.diginehru.R;
import com.example.sandeepsharma.diginehru.Utils.ProjectUtil;

public class StartActivity extends AppCompatActivity implements View.OnClickListener, NetworkCallBack {

    Button button_login;
    Button button_sign_up;
    TextInputLayout til_user_id;
    TextInputEditText edit_user_id;
    TextInputLayout til_password;
    TextInputEditText edit_password;
    LinearLayout ll_forgot_passeord;
    RadioGroup radio_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        button_login = (Button) findViewById(R.id.login);
        button_sign_up = (Button) findViewById(R.id.signup);
        til_user_id = (TextInputLayout) findViewById(R.id.til_id);
        edit_user_id = (TextInputEditText) findViewById(R.id.edit_id);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        edit_password = (TextInputEditText) findViewById(R.id.edit_password);
        ll_forgot_passeord = (LinearLayout) findViewById(R.id.forgot_password);
        radio_type = (RadioGroup) findViewById(R.id.radio_group_type);

        button_login.setOnClickListener(this);
        button_sign_up.setOnClickListener(this);
        ll_forgot_passeord.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if (id == R.id.login) {
            login();
        } else if (id == R.id.signup) {
            intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

    }

    private void login() {
        boolean success = true;

        if (edit_user_id.getText().toString().isEmpty()) {
            success = false;
            til_user_id.setError(getString(R.string.field_required));
        }

        if (edit_password.getText().toString().isEmpty()) {
            success = false;
            til_password.setError(getString(R.string.field_required));
        }

        if (success) {
            if (radio_type.getCheckedRadioButtonId() == R.id.radio_staff) {
                new LoginNetwork(this, AppConstants.LOGIN_NETWORK, edit_user_id.getText().toString(), edit_password.getText().toString(), false);
            } else {
                new LoginNetwork(this, AppConstants.LOGIN_NETWORK, edit_user_id.getText().toString(), edit_password.getText().toString(), true);

            }

        } else {
            return;
        }
    }

    @Override
    public void setResult(Response result, int eventType) {
        if (eventType == AppConstants.LOGIN_NETWORK) {
            if (result.getReponseCode() == 200) {
                if (radio_type.getCheckedRadioButtonId() == R.id.radio_staff) {
                } else {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    startActivity(intent);
                }
            }
            ProjectUtil.showToast(getBaseContext(), result.getErrorText());
        }
    }
}
