package com.youtility.intelliwiz20.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.youtility.intelliwiz20.R;

public class EmergencyContactInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mnTextView, emailTextView, helpTextView, pemTextView;
    private EditText mnEditText, emailEditText, helpEditText, pemEditView;
    private SharedPreferences emergencyContactInfoPref;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_info);
        //emergencyContactInfoPref=getSharedPreferences(Constants.EMERGENCY_CONTACT_INFO_PREF, Context.MODE_PRIVATE);
        actionBar=getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.emergency_contact_title));
        componentInitialise();
    }

    private void componentInitialise()
    {
        mnTextView=(TextView)findViewById(R.id.mnTextView);
        mnTextView.setOnClickListener(this);
        mnEditText=(EditText)findViewById(R.id.mnEditText);
        //mnEditText.setText(emergencyContactInfoPref.getString(Constants.EMERGENCY_CONTACT_NUMBER,"--"));

        emailTextView=(TextView)findViewById(R.id.emailTextView);
        emailTextView.setOnClickListener(this);
        emailEditText=(EditText)findViewById(R.id.emailEditText);
        //emailEditText.setText(emergencyContactInfoPref.getString(Constants.EMERGENCY_CONTACT_EMAIL,"--"));

        helpTextView=(TextView)findViewById(R.id.helpTextView);
        helpTextView.setOnClickListener(this);
        helpEditText=(EditText)findViewById(R.id.helpEditText);
        //helpEditText.setText(emergencyContactInfoPref.getString(Constants.EMERGENCY_CONTACT_HELP_MESSAGE,"Need help urgently"));

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.mnTextView:
                mnEditText.setEnabled(true);
                mnEditText.setSelectAllOnFocus(true);
                break;
            case R.id.emailTextView:
                emailEditText.setEnabled(true);
                mnEditText.setSelectAllOnFocus(true);
                break;
            case R.id.helpTextView:
                helpEditText.setEnabled(true);
                mnEditText.setSelectAllOnFocus(true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emergency_contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_done:
                /*emergencyContactInfoPref.edit().putString(Constants.EMERGENCY_CONTACT_NUMBER,mnEditText.getText().toString().trim())
                                                .putString(Constants.EMERGENCY_CONTACT_EMAIL,emailEditText.getText().toString().trim())
                                                .putString(Constants.EMERGENCY_CONTACT_HELP_MESSAGE, helpEditText.getText().toString().trim()).commit();*/
                onBackPressed();
                break;
        }
        return true;
    }
}
