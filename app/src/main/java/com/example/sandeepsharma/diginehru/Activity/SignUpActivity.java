package com.example.sandeepsharma.diginehru.Activity;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
import com.example.sandeepsharma.diginehru.Frameworks.NetworkCallBack;
import com.example.sandeepsharma.diginehru.Frameworks.Response;
import com.example.sandeepsharma.diginehru.Network.StudentRegistrationNetwork;
import com.example.sandeepsharma.diginehru.R;
import com.example.sandeepsharma.diginehru.Utils.ProjectUtil;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, NetworkCallBack {

    TextInputEditText edit_name, edit_roll, edit_room, edit_email, edit_mobile, edit_password;
    TextInputLayout til_name, til_roll, til_room, til_email, til_mobile, til_password;
    Button button_signup;
    ImageView image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Registration");

        edit_name = (TextInputEditText) findViewById(R.id.edit_username);
        edit_roll = (TextInputEditText) findViewById(R.id.edit_roll);
        edit_room = (TextInputEditText) findViewById(R.id.edit_room);
        edit_email = (TextInputEditText) findViewById(R.id.edit_email);
        edit_mobile = (TextInputEditText) findViewById(R.id.edit_mobile);
        edit_password = (TextInputEditText) findViewById(R.id.edit_password);

        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_roll = (TextInputLayout) findViewById(R.id.til_roll);
        til_room = (TextInputLayout) findViewById(R.id.til_room);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_mobile = (TextInputLayout) findViewById(R.id.til_mobile);
        til_password = (TextInputLayout) findViewById(R.id.til_password);
        image_profile = (ImageView) findViewById(R.id.profile);
        image_profile.setOnClickListener(this);

        button_signup = (Button) findViewById(R.id.button_signup);
        button_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_signup) {
            signup();
        } else if (id == R.id.profile) {
            Intent intent = new Intent(SignUpActivity.this, ImageCropperActivity.class);
            intent.putExtra("type", "image_bill");
            startActivityForResult(intent, 0);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    if (data.hasExtra("file")) {
                        String file = data.getStringExtra("file");
                        ProjectUtil.setImage(file, image_profile, 60, 50, getBaseContext());

                    }
                }

            }
        }
    }

    private void signup() {
        boolean succes = true;
        if (edit_name.getText() != null && !edit_name.getText().toString().isEmpty()) {
            til_name.setError("");

        } else {
            til_name.setError(getResources().getString(R.string.field_required));
            succes = false;
        }

        if (edit_roll.getText() != null && !edit_roll.getText().toString().isEmpty()) {
            til_roll.setError("");
        } else {
            til_roll.setError(getResources().getString(R.string.field_required));
            succes = false;
        }

        if (edit_room.getText() != null && !edit_room.getText().toString().isEmpty()) {
            til_room.setError("");
        } else {
            til_room.setError(getResources().getString(R.string.field_required));
            succes = false;
        }

        if (edit_mobile.getText() != null && !edit_mobile.getText().toString().isEmpty()) {
            til_mobile.setError("");
        } else {
            til_mobile.setError(getResources().getString(R.string.field_required));
            succes = false;
        }

        if (edit_email.getText() != null && !edit_email.getText().toString().isEmpty()) {
            til_email.setError("");
        } else {
            til_email.setError(getResources().getString(R.string.field_required));
            succes = false;
        }

        if (edit_password.getText() != null && !edit_password.getText().toString().isEmpty()) {
            til_password.setError("");
        } else {
            til_password.setError(getResources().getString(R.string.field_required));
            succes = false;
        }

        if (succes) {
            new StudentRegistrationNetwork(this, AppConstants.SIGN_UP_NETWORK, edit_name.getText().toString(),
                    edit_roll.getText().toString(),
                    edit_room.getText().toString(),
                    edit_email.getText().toString(),
                    edit_mobile.getText().toString(),
                    edit_password.getText().toString());
        } else {
            return;
        }

    }

    @Override
    public void setResult(Response result, int eventType) {
        if (eventType == AppConstants.SIGN_UP_NETWORK) {
            ProjectUtil.showToast(this, result.getErrorText());
            if (result.getReponseCode() == 200) {
                Intent intent = new Intent(this, StartActivity.class);
                startActivity(intent);

            }
        }
    }
}
