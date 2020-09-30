package com.youtility.intelliwiz20.Activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.youtility.intelliwiz20.R;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private Button submitReqButton;
    private EditText userCode, userEmail, siteCode;
    private TextInputLayout siteCode_layout,userCode_layout,userEmail_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        componentInitialise();
    }

    private void componentInitialise()
    {
        submitReqButton=(Button)findViewById(R.id.submitRequestButton);
        submitReqButton.setOnClickListener(this);

        userCode=(EditText)findViewById(R.id.user_code);
        userCode.addTextChangedListener(this);
        userCode_layout=(TextInputLayout)findViewById(R.id.usercode_layout);

        userEmail=(EditText)findViewById(R.id.user_email);
        userEmail.addTextChangedListener(this);
        userEmail_layout=(TextInputLayout)findViewById(R.id.useremail_layout);

        siteCode=(EditText)findViewById(R.id.site_code);
        siteCode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        siteCode.addTextChangedListener(this);
        siteCode_layout=(TextInputLayout)findViewById(R.id.sitecode_layout);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submitRequestButton:
                if(isValidateData())
                {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        siteCode_layout.setError(null);
        siteCode_layout.setErrorEnabled(false);
        userCode_layout.setError(null);
        userCode_layout.setErrorEnabled(false);
        userEmail_layout.setError(null);
        userEmail_layout.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private boolean isValidateData()
    {
        if(siteCode.getText().toString().trim().length()==0)
        {
            siteCode_layout.setErrorEnabled(true);
            siteCode_layout.setError(getResources().getString(R.string.login_user_sitecode_error));
            return false;
        }
        else if(userCode.getText().toString().trim().length()==0) {
            userCode_layout.setErrorEnabled(true);
            userCode_layout.setError(getResources().getString(R.string.login_user_code_error));
            return false;
        }
        else if(userEmail.getText().toString().trim().length()==0)
        {
            userEmail_layout.setErrorEnabled(true);
            userEmail_layout.setError(getResources().getString(R.string.contents_email));
            return false;
        }

        else
            return  true;

    }
}
