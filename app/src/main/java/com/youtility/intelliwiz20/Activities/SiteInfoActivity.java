package com.youtility.intelliwiz20.Activities;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.SiteInformationDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.SitesInformation;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;


import java.util.regex.Pattern;

public class SiteInfoActivity extends AppCompatActivity implements View.OnClickListener, IDialogEventListeners {
    private TextView siteinfo_sitename;
    private TextView siteinfo_siteaddress;
    private TextView siteinfo_contactname;
    private TextView siteinfo_contactnumber;
    private TextView siteinfo_sitelocation;
    private TextView siteinfo_landmark;
    private TextView siteinfo_postalcode;
    private TextView siteinfo_tstrength;
    private TextView siteinfo_details;
    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;


    private SiteInformationDAO siteInformationDAO;
    private SitesInformation sitesInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_info);

        long siteId=getIntent().getLongExtra("SITE_ID",-1);
        String siteName=getIntent().getStringExtra("SITE_NAME");



        siteInformationDAO=new SiteInformationDAO(SiteInfoActivity.this);
        sitesInformation=siteInformationDAO.getSiteInformation(siteId);

        siteinfo_sitename=(TextView)findViewById(R.id.siteinfo_name);
        siteinfo_siteaddress=(TextView)findViewById(R.id.siteinfo_address);
        siteinfo_contactname=(TextView)findViewById(R.id.siteinfo_contactPersonName);
        siteinfo_contactnumber=(TextView)findViewById(R.id.siteinfo_contactPersonNumber);
        siteinfo_sitelocation=(TextView)findViewById(R.id.siteinfo_location);

        siteinfo_landmark=(TextView)findViewById(R.id.siteinfo_landmark);
        siteinfo_postalcode=(TextView)findViewById(R.id.siteinfo_postalcode);
        siteinfo_tstrength=(TextView)findViewById(R.id.siteinfo_totalstrength);
        siteinfo_details=(TextView)findViewById(R.id.siteinfo_strengthdetails);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(SiteInfoActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        siteinfo_contactnumber.setOnClickListener(this);
        siteinfo_sitelocation.setOnClickListener(this);

        siteinfo_sitename.setText(siteName);

        if(sitesInformation!=null)
        {
            if(sitesInformation.getAddress()!=null && !sitesInformation.getAddress().toString().trim().equalsIgnoreCase("none"))
                siteinfo_siteaddress.setText(sitesInformation.getAddress());

            if(sitesInformation.getSincharge()!=null && !sitesInformation.getSincharge().toString().trim().equalsIgnoreCase("none"))
                siteinfo_contactname.setText(sitesInformation.getSincharge());

            if(sitesInformation.getSimob()!=null && !sitesInformation.getSimob().toString().trim().equalsIgnoreCase("none"))
                siteinfo_contactnumber.setText(sitesInformation.getSimob());

            if(sitesInformation.getGpslocation()!=null && !sitesInformation.getGpslocation().toString().trim().equalsIgnoreCase("none"))
                siteinfo_sitelocation.setText(sitesInformation.getGpslocation());

            if(sitesInformation.getLandmark()!=null && !sitesInformation.getLandmark().toString().trim().equalsIgnoreCase("none"))
                siteinfo_landmark.setText(sitesInformation.getLandmark());

            if(sitesInformation.getPostalcode()!=null && !sitesInformation.getPostalcode().toString().trim().equalsIgnoreCase("none"))
                siteinfo_postalcode.setText(sitesInformation.getPostalcode());

            siteinfo_tstrength.setText(sitesInformation.getContract()+" ( "+sitesInformation.getTotstrength()+" )");
            StringBuilder stringBuilder=null;
            if(sitesInformation.getStrength()!=null && sitesInformation.getStrength().contains("|"))
            {
                String[] sDetails=sitesInformation.getStrength().split(Pattern.quote("|"));
                if(sDetails.length>0)
                {
                    stringBuilder=new StringBuilder();
                    for(int i=0;i<sDetails.length;i++)
                        stringBuilder.append(sDetails[i].trim()+"\n");
                }
            }
            else
            {
                stringBuilder=new StringBuilder();
                stringBuilder.append(sitesInformation.getStrength());
            }
            if(stringBuilder!=null)
                siteinfo_details.setText(stringBuilder.toString().trim());
        }

    }

    @Override
    public void onClick(View v) {
        int accessValue = CommonFunctions.isAllowToAccessModules(SiteInfoActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
        System.out.println("===========" + accessValue);
        System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if (accessValue == 0) {
            switch (v.getId()) {
                case R.id.siteinfo_contactPersonNumber:
                /*if(!siteinfo_contactnumber.getText().toString().trim().equalsIgnoreCase("none") && siteinfo_contactnumber.getText().toString().trim().length()>0)
                {
                    callPhoneIntent(siteinfo_contactnumber.getText().toString().trim());
                }*/
                    break;
                case R.id.siteinfo_location:
                    break;
            }
        }else if (accessValue == 1) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
        } else if (accessValue == 2) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 3) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 4) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
            System.out.println("==========="+accessValue);
        }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
            System.out.println("===========lat long==0.0");
        }
    }

    private void callPhoneIntent(String dialNumber) {

        /*if (ActivityCompat.checkSelfPermission(SiteInfoActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialNumber));
            startActivity(intent);
        }*/
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + dialNumber));

    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }
}
