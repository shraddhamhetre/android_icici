package com.youtility.intelliwiz20.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.youtility.intelliwiz20.Adapters.TransportModeGridViewAdapter;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.PeopleEventLogDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Interfaces.IGridviewChangeBackgroundItemListeners;
import com.youtility.intelliwiz20.Model.PeopleEventLog;
import com.youtility.intelliwiz20.Model.TransportMode;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;
import com.youtility.intelliwiz20.Utils.PeopleEventLogInsertion;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ConveyanceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener, IGridviewChangeBackgroundItemListeners, ConnectivityReceiver.ConnectivityReceiverListener, LocationListener, IDialogEventListeners {

    private ConnectivityReceiver connectivityReceiver;

    private TextView startGpsAddress;
    private EditText startGpsAddressManually;
    private Spinner travelModeSpinner;
    private LinearLayout startTravelLinearLayout;
    private TextView endGpsAddress;
    private EditText endGpsAddressManually;
    private EditText travelExpenceAmount;
    private LinearLayout endTravelLinearLayout;

    private Button startTravelButton;
    private Button stopTravelButton;
    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;

    /*private TextView startTravelTimeTextview;
    private TextView startTravelLocationTextview;
    private TextView travelModeTextview;
    private TextView endTravelTimeTextview;
    private TextView endTravelLocationTextview;
    private TextView travelExpenceTextview;
    private TextView travelDistanceTextView;
    private TextView travelDurationTextView;*/

    private TextView conveyanceRunningState;

    private List<Address> addresses;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences conveyancePref;

    private String gpsAddress = null;

    private TypeAssistDAO typeAssistDAO;
    private PeopleEventLogDAO peopleEventLogDAO;
    private ArrayList<TypeAssist> transportModeList;
    private ArrayList<String> transportModeNameList;
    private PeopleEventLogInsertion peopleEventLogInsertion;
    private LatLng originPoint, destPoint;
    private Handler handler = new Handler();
    private boolean isThreadRunning = false;
    private TextView travelModeBike, travelModeBus, travelModeTrain, travelModeRickshaw;
    private TextView travelModeCab, travelModeCar, travelModeBoat, travelModePlane;

    private ImageView startReadingImage, stopReadingImage;

    //--------------------------to get current location
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String latitude=null,longitude=null;

    //---------------------------for new ui

    private GridView travelModeGridview;
    /*private Button overButton, pauseButton;
    private TextView pTimeTextview, pDistanceTextview, pMoneyTextview;
    private TextView runningStoryTextview;
    private EditText remarkEdittext;
    private EditText travelExpenceAmountEdittext;
    private Button reallyButton;*/
    private TransportModeGridViewAdapter transportModeGridViewAdapter;
    private ArrayList<TransportMode> transportModeArrayList;
    private int travelModePosition = -1;
    private int prevTravelModePosition = -1;
    private Timer updateTimer;
    private Thread t;

    @Override
    protected void onStart() {
        super.onStart();
        getUpdatedGPSLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conveyance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.setTitle(getResources().getString(R.string.title_activity_conveyance));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        conveyanceRunningState=(TextView)findViewById(R.id.conveyanceRunningState);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(ConveyanceActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        /*travelModeBike=(TextView)findViewById(R.id.travelModeBike);
        travelModeBus=(TextView)findViewById(R.id.travelModeBus);
        travelModeTrain=(TextView)findViewById(R.id.travelModeTrain);
        travelModeRickshaw=(TextView)findViewById(R.id.travelModeRickshaw);

        travelModeCab=(TextView)findViewById(R.id.travelModeCab);
        travelModeCar=(TextView)findViewById(R.id.travelModeCar);
        travelModeBoat=(TextView)findViewById(R.id.travelModeBoat);
        travelModePlane=(TextView)findViewById(R.id.travelModePlane);

        travelModeBike.setOnClickListener(this);
        travelModeBus.setOnClickListener(this);
        travelModeTrain.setOnClickListener(this);
        travelModeRickshaw.setOnClickListener(this);
        travelModeCab.setOnClickListener(this);
        travelModeCar.setOnClickListener(this);
        travelModeBoat.setOnClickListener(this);
        travelModePlane.setOnClickListener(this);*/

        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        conveyancePref=getSharedPreferences(Constants.CONVEYANCE_PREF, MODE_PRIVATE);



        typeAssistDAO=new TypeAssistDAO(ConveyanceActivity.this);
        peopleEventLogDAO=new PeopleEventLogDAO(ConveyanceActivity.this);

        transportModeList=typeAssistDAO.getEventList("Mode of Transport");
        transportModeNameList=new ArrayList<>();
        for(int i=0;i<transportModeList.size();i++)
        {
            if(transportModeList.get(i).getTaid()!=-1)
            {
                transportModeNameList.add(transportModeList.get(i).getTaname());
            }
        }

        if(conveyancePref.getBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED,false))
            isThreadRunning=true;
        else
            isThreadRunning=false;


        startReadingImage=(ImageView)findViewById(R.id.startReadingImage);
        stopReadingImage=(ImageView)findViewById(R.id.stopReadingImage);

        startGpsAddress=(TextView)findViewById(R.id.startGpsAddress);
        startGpsAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("11");
                getGPSAddress();

                /*Intent mapIntent=new Intent(ConveyanceActivity.this, UserLocationViewActivity.class);
                mapIntent.putExtra("FROM", "GETLOCATION");
                startActivityForResult(mapIntent,100);*/
            }
        });
        startGpsAddressManually=(EditText)findViewById(R.id.startGpsAddressManually);
        travelModeSpinner=(Spinner)findViewById(R.id.travelModeSpinner);
        startTravelLinearLayout=(LinearLayout)findViewById(R.id.startTravelLinearLayout);

        endGpsAddress=(TextView)findViewById(R.id.endGpsAddress);
        endGpsAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGPSAddress();
            }
        });
        endGpsAddressManually=(EditText)findViewById(R.id.endGpsAddressManually);
        travelExpenceAmount=(EditText)findViewById(R.id.travelExpenceAmount);
        endTravelLinearLayout=(LinearLayout)findViewById(R.id.endTravelLinearLayout);

        startTravelButton=(Button)findViewById(R.id.startTravelButton);
        startTravelButton.setOnClickListener(this);

        stopTravelButton=(Button)findViewById(R.id.stopTravelButton);
        stopTravelButton.setOnClickListener(this);

        ArrayAdapter transportAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,transportModeNameList);
        transportAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelModeSpinner.setAdapter(transportAdpt);

        /*startTravelTimeTextview=(TextView)findViewById(R.id.startTravelTimeTextview);
        startTravelLocationTextview=(TextView)findViewById(R.id.startTravelLocationTextview);
        travelModeTextview=(TextView)findViewById(R.id.travelModeTextview);

        endTravelTimeTextview=(TextView)findViewById(R.id.endTravelTimeTextview);
        endTravelLocationTextview=(TextView)findViewById(R.id.endTravelLocationTextview);
        travelExpenceTextview=(TextView)findViewById(R.id.travelExpenceTextview);

        travelDistanceTextView=(TextView)findViewById(R.id.travelDistanceTextview);
        travelDurationTextView=(TextView)findViewById(R.id.travelDurationTextview);*/


        //text = "<font color=#18B064>"+ (mValues.get(position).getPlandatetime())+"</font> <font color=#919191>"+" "+mValues.get(position).getJobdesc()+"</font>";
        conveyanceRunningState.setText(getResources().getString(R.string.conveyance_runningstory_empty_msg));

        //getGPSAddress();

        getLocationFromGPS();

        //commented for new UI
        updateInfoView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabView);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("----start");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*travelModeBike.setTextColor(getResources().getColor(R.color.button_background));
        travelModeBus.setTextColor(getResources().getColor(R.color.listviewDivider));
        travelModeTrain.setTextColor(getResources().getColor(R.color.listviewDivider));
        travelModeRickshaw.setTextColor(getResources().getColor(R.color.listviewDivider));
        travelModeCab.setTextColor(getResources().getColor(R.color.listviewDivider));
        travelModeCar.setTextColor(getResources().getColor(R.color.listviewDivider));
        travelModeBoat.setTextColor(getResources().getColor(R.color.listviewDivider));
        travelModePlane.setTextColor(getResources().getColor(R.color.listviewDivider));*/

        //----------------------------------------------for new UI

        transportModeArrayList=new ArrayList<>();
        for(int i=0;i<transportModeList.size();i++)
        {
            TransportMode transportMode=new TransportMode();
            transportMode.setTravelMode(transportModeList.get(i).getTacode());
            transportMode.setTravelDistance("D");
            transportMode.setTravelMoney("M");
            transportMode.setTravelTime("T");
            transportMode.setSelected(false);
            transportModeArrayList.add(transportMode);

        }
        travelModeGridview=(GridView)findViewById(R.id.travelModeGridview);
        transportModeGridViewAdapter=new TransportModeGridViewAdapter(ConveyanceActivity.this, transportModeArrayList,this);
        travelModeGridview.setAdapter(transportModeGridViewAdapter);

        /*runningStoryTextview=(TextView)findViewById(R.id.runningStroyTextview);
        reallyButton=(Button)findViewById(R.id.reallyButton);
        reallyButton.setOnClickListener(this);
        reallyButton.setVisibility(View.GONE);
        overButton=(Button)findViewById(R.id.overButton);
        overButton.setOnClickListener(this);
        pauseButton=(Button)findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(this);
        pTimeTextview=(TextView)findViewById(R.id.pTimeTextview);
        pDistanceTextview=(TextView)findViewById(R.id.pDistanceTextview);
        pMoneyTextview=(TextView)findViewById(R.id.pMoneyTextview);*/
    }

    private void highlightedPauseButton()
    {
        /*pauseButton.setTextColor(Color.RED);
        pauseButton.setBackgroundColor(getResources().getColor(R.color.button_background));*/
    }

    private void resetPauseButton()
    {
        /*pauseButton.setTextColor(Color.WHITE);
        pauseButton.setBackgroundColor(getResources().getColor(R.color.web_screen_background));*/
    }

    private void getGPSAddress()
    {
        /*if(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"")!=null && deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"").trim().length()>0 &&
                deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"")!=null && deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"").trim().length()>0)*/
        if(latitude!=null && longitude!=null)
        {
            addresses= CommonFunctions.getAddress(ConveyanceActivity.this, latitude,longitude);
            if(addresses!=null && addresses.size()>0)
                gpsAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            if(gpsAddress!=null)
                System.out.println("Updated GPS Address: "+gpsAddress.trim());

            if(conveyancePref.getBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED,false))
            {
                conveyancePref.edit().putString(Constants.CONVEYANCE_END_LAT_LOCATION,latitude).apply();
                conveyancePref.edit().putString(Constants.CONVEYANCE_END_LON_LOCATION,longitude).apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(gpsAddress!=null) {
                            endGpsAddress.setText(gpsAddress.trim());
                            conveyancePref.edit().putString(Constants.CONVEYANCE_END_ADDRESS, gpsAddress).apply();
                            endGpsAddressManually.setEnabled(false);
                            endGpsAddressManually.setHint("");
                        }
                        else
                        {
                            //endGpsAddressManually.setVisibility(View.VISIBLE);
                            endGpsAddressManually.setEnabled(true);
                            endGpsAddressManually.setHint(getString(R.string.expence_endlocaddress_hint));
                            endGpsAddress.setText(getResources().getString(R.string.conveyance_not_getting_address_frm_gps));
                        }
                    }
                });
            }
            else
            {

                conveyancePref.edit().putString(Constants.CONVEYANCE_START_LAT_LOCATION,latitude).apply();
                conveyancePref.edit().putString(Constants.CONVEYANCE_START_LON_LOCATION,longitude).apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(gpsAddress!=null) {
                            startGpsAddress.setText(gpsAddress.trim());
                            conveyancePref.edit().putString(Constants.CONVEYANCE_START_ADDRESS, gpsAddress).apply();
                            startGpsAddressManually.setEnabled(false);
                            startGpsAddressManually.setHint("");
                        }
                        else
                        {
                            //startGpsAddressManually.setVisibility(View.VISIBLE);
                            startGpsAddressManually.setEnabled(true);
                            startGpsAddressManually.setHint(getString(R.string.expence_startlocaddress_hint));
                            startGpsAddress.setText(getResources().getString(R.string.conveyance_not_getting_address_frm_gps));
                        }
                    }
                });
            }

        }


    }

    private void updateInfoView()
    {
        /*startTravelTimeTextview.setText(getResources().getString(R.string.expence_travelstartat, conveyancePref.getString(Constants.CONVEYANCE_START_TIME,""))+"");
        startTravelLocationTextview.setText(getResources().getString(R.string.expence_travelstartlocation, conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS,"")));
        travelModeTextview.setText(getResources().getString(R.string.expence_travelmode,conveyancePref.getString(Constants.CONVEYANCE_START_MODE,"")));

        endTravelTimeTextview.setText(getResources().getString(R.string.expence_travelstopat, conveyancePref.getString(Constants.CONVEYANCE_END_TIME,"")));
        endTravelLocationTextview.setText(getResources().getString(R.string.expence_travelstoplocation, conveyancePref.getString(Constants.CONVEYANCE_END_ADDRESS,"")));
        travelExpenceTextview.setText(getResources().getString(R.string.expence_travelexpence, conveyancePref.getString(Constants.CONVEYANCE_END_EXPENCE,"")));

        travelDistanceTextView.setText(getResources().getString(R.string.expence_traveldistance,conveyancePref.getString(Constants.CONVEYANCE_DISTANCE,"0.0")));
        travelDurationTextView.setText(getResources().getString(R.string.expence_travelduration,conveyancePref.getString(Constants.CONVEYANCE_DURATION,"0.0")));*/
    }


    private void getLocationFromGPS()
    {
        if(!conveyancePref.getBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED,false))
        {
            startTravelLinearLayout.setVisibility(View.VISIBLE);
            endTravelLinearLayout.setVisibility(View.GONE);
            startTravelButton.setEnabled(true);
            stopTravelButton.setEnabled(false);

            startTravelButton.setBackgroundResource(R.drawable.rounder_corner_button);
            stopTravelButton.setBackgroundResource(R.drawable.rounder_cancel_button);

            if(gpsAddress!=null) {
                startGpsAddress.setText(gpsAddress.trim());
                conveyancePref.edit().putString(Constants.CONVEYANCE_START_ADDRESS,gpsAddress).apply();
                startGpsAddressManually.setEnabled(false);
                startGpsAddressManually.setHint("");
            }
            else
            {
                //startGpsAddressManually.setVisibility(View.VISIBLE);
                startGpsAddressManually.setEnabled(true);
                startGpsAddressManually.setHint(getString(R.string.expence_startlocaddress_hint));
                startGpsAddress.setText(getResources().getString(R.string.conveyance_not_getting_address_frm_gps));
            }

        }
        else
        {
            startTravelLinearLayout.setVisibility(View.GONE);
            endTravelLinearLayout.setVisibility(View.VISIBLE);
            startTravelButton.setEnabled(false);
            stopTravelButton.setEnabled(true);

            startTravelButton.setBackgroundResource(R.drawable.rounder_cancel_button);
            stopTravelButton.setBackgroundResource(R.drawable.rounder_delete_button);

            if(gpsAddress!=null) {
                endGpsAddress.setText(gpsAddress.trim());
                conveyancePref.edit().putString(Constants.CONVEYANCE_END_ADDRESS, gpsAddress).apply();
                endGpsAddressManually.setEnabled(false);
                endGpsAddressManually.setHint("");
            }
            else
            {
                //endGpsAddressManually.setVisibility(View.VISIBLE);
                endGpsAddressManually.setEnabled(true);
                endGpsAddressManually.setHint(getString(R.string.expence_endlocaddress_hint));
                endGpsAddress.setText(getResources().getString(R.string.conveyance_not_getting_address_frm_gps));
            }
        }
    }

    private void getUpdatedGPSLocation()
    {
        System.out.println("getUpdatedGPSLocation: "+isThreadRunning);
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (isThreadRunning) {
                        Thread.sleep(60*1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                getGPSAddress();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void callPictureActivity(String travelPoint,String travelMode, int requestCode)
    {

        System.out.println("ref id2:"+ conveyancePref.getLong(Constants.CONVEYANCE_TIMESTAMP, -1));

        Intent capturePic=new Intent(ConveyanceActivity.this, CapturePhotoActivity.class);
        capturePic.putExtra("FROM",Constants.ATTACHMENT_PICTURE);
        capturePic.putExtra("JOBNEEDID",conveyancePref.getLong(Constants.CONVEYANCE_TIMESTAMP,System.currentTimeMillis()));
        capturePic.putExtra("PARENT_ACTIVITY",Constants.ATTACHMENT_OWNER_TYPE_PEOPLEEVENTLOG);
        capturePic.putExtra("FOLDER",Constants.EVENT_TYPE_CONVEYANCE);
        capturePic.putExtra("CAMERA",0);
        if(requestCode==0)
        {
            capturePic.putExtra("TIMESTAMP",conveyancePref.getLong(Constants.CONVEYANCE_START_TIMESTAMP,System.currentTimeMillis()));
        }
        else if(requestCode==1)
        {
            capturePic.putExtra("TIMESTAMP",conveyancePref.getLong(Constants.CONVEYANCE_END_TIMESTAMP,System.currentTimeMillis()));
        }

        startActivityForResult(capturePic,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                Bitmap imgthumBitmap=null;
                try
                {

                    final int THUMBNAIL_SIZE = 64;

                    FileInputStream fis = new FileInputStream(data.getStringExtra("IMG_PATH"));
                    imgthumBitmap = BitmapFactory.decodeStream(fis);

                    imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                            THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                    ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
                    imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);
                    startReadingImage.setImageBitmap(imgthumBitmap);

                }
                catch(Exception ex) {

                }
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(ConveyanceActivity.this, "Please capture the photo to start tour.",Toast.LENGTH_LONG).show();
                //callPictureActivity("START","BIKE",0);

            }
        }
        else if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                Bitmap imgthumBitmap=null;
                try
                {
                    final int THUMBNAIL_SIZE = 64;

                    FileInputStream fis = new FileInputStream(data.getStringExtra("IMG_PATH"));
                    imgthumBitmap = BitmapFactory.decodeStream(fis);

                    imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap,
                            THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

                    ByteArrayOutputStream bytearroutstream = new ByteArrayOutputStream();
                    imgthumBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytearroutstream);
                    stopReadingImage.setImageBitmap(imgthumBitmap);

                }
                catch(Exception ex) {

                }

                resetUI();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(ConveyanceActivity.this, "Please capture the photo to end tour.",Toast.LENGTH_LONG).show();

                /*new AlertDialog.Builder(ConveyanceActivity.this)
                        .setTitle("Alert")
                        .setMessage("DO you want to take picture?").setCancelable(false)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                callPictureActivity("STOP","BIKE",1);

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/
            }
        }
        else if(requestCode==100)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                String[] ss=data.getStringExtra("LAT_LON").split(",");
                latitude=ss[0];
                longitude=ss[1];
                getGPSAddress();
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void startTraveling()
    {
        String address=null;
        boolean isGpsAddress;

        isThreadRunning=true;
        if (!TextUtils.isEmpty(conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, ""))) {
            address = conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, "");
            isGpsAddress=true;
        }
        else {
            address = startGpsAddressManually.getText().toString().trim();
            isGpsAddress=false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mGeoCode=CommonFunctions.getLocationFromAddress(ConveyanceActivity.this, startGpsAddressManually.getText().toString().trim());
                    System.out.println("start geoCode: "+mGeoCode);
                    if(mGeoCode!=null) {
                        String[] gPoint = mGeoCode.split(",");
                        conveyancePref.edit().putString(Constants.CONVEYANCE_START_LAT_LOCATION, gPoint[0].trim()).apply();
                        conveyancePref.edit().putString(Constants.CONVEYANCE_START_LON_LOCATION, gPoint[1].trim()).apply();
                    }
                }
            });
        }
        if (address != null && address.trim().length() > 0) {
            System.out.println("Address: " + address);
            System.out.println("Mode: " + transportModeList.get(travelModeSpinner.getSelectedItemPosition()).getTaname());
            conveyancePref.edit().putBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED, true).apply();
            conveyancePref.edit().putLong(Constants.CONVEYANCE_TIMESTAMP, System.currentTimeMillis()).apply();
            conveyancePref.edit().putString(Constants.CONVEYANCE_START_TIME, CommonFunctions.getFormatedDate(System.currentTimeMillis())).apply();
            conveyancePref.edit().putLong(Constants.CONVEYANCE_START_TIMESTAMP, System.currentTimeMillis()).apply();
            //conveyancePref.edit().putString(Constants.CONVEYANCE_START_MODE, transportModeList.get(travelModeSpinner.getSelectedItemPosition()).getTacode()).apply();
            conveyancePref.edit().putString(Constants.CONVEYANCE_START_MODE, transportModeArrayList.get(travelModePosition).getTravelMode()).apply();
            if(isGpsAddress) {
                conveyancePref.edit().putString(Constants.CONVEYANCE_START_LAT_LOCATION, latitude).apply();
                conveyancePref.edit().putString(Constants.CONVEYANCE_START_LON_LOCATION, longitude).apply();
            }

            conveyancePref.edit().putString(Constants.CONVEYANCE_START_ADDRESS, address).apply();

            //temp commented for new ui
                        /*startTravelTimeTextview.setText(getResources().getString(R.string.expence_travelstartat, conveyancePref.getString(Constants.CONVEYANCE_START_TIME, "")));
                        startTravelLocationTextview.setText(getResources().getString(R.string.expence_travelstartlocation, conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, "")));
                        travelModeTextview.setText(getResources().getString(R.string.expence_travelmode, conveyancePref.getString(Constants.CONVEYANCE_START_MODE, "")));*/

            //conveyanceRunningState.setText("Your travel started from "+conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, "")+" by "+ conveyancePref.getString(Constants.CONVEYANCE_START_MODE, "")+" at "+conveyancePref.getString(Constants.CONVEYANCE_START_TIME, ""));
            conveyanceRunningState.setText(getResources().getString(R.string.conveyance_runningstory_start_msg,
                    conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_START_MODE, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_START_TIME, "")));

            startGpsAddress.setText("");
            startGpsAddressManually.setText("");

            startTravelLinearLayout.setVisibility(View.GONE);
            endTravelLinearLayout.setVisibility(View.VISIBLE);

            startTravelButton.setEnabled(false);
            stopTravelButton.setEnabled(true);

            startTravelButton.setBackgroundResource(R.drawable.rounder_cancel_button);
            stopTravelButton.setBackgroundResource(R.drawable.rounder_delete_button);
            getLocationFromGPS();
            getUpdatedGPSLocation();

            System.out.println("CONVEYANCE_DISTANCE"+conveyancePref.getString(Constants.CONVEYANCE_DISTANCE, "0"));

            //String punchType, String gpsLocation,long refValue, String remark, String transportMode, double expamout, int distance, int duration
             addPeopleEventLogValues(Constants.ATTENDANCE_PUNCH_TYPE_IN,
                    conveyancePref.getString(Constants.CONVEYANCE_START_LAT_LOCATION, "0.0") + "," + conveyancePref.getString(Constants.CONVEYANCE_START_LON_LOCATION, "0.0"),
                    conveyancePref.getLong(Constants.CONVEYANCE_TIMESTAMP, -1),
                    conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_START_MODE, ""),
                    Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_END_EXPENCE, "0.0")),
                    0,
                    //Integer.parseInt(conveyancePref.getString(Constants.CONVEYANCE_DISTANCE, "0")),
                    Integer.parseInt(conveyancePref.getString(Constants.CONVEYANCE_DURATION, "0")),
                    conveyancePref.getLong(Constants.CONVEYANCE_START_TIMESTAMP,-1));

            String tMode=transportModeArrayList.get(travelModePosition).getTravelMode();
            System.out.println("start tMode::" +tMode);
            if(tMode.equalsIgnoreCase("BIKE") || tMode.equalsIgnoreCase("CAR") || tMode.equalsIgnoreCase("RICKSHAW") ||
                    tMode.equalsIgnoreCase("BOAT") || tMode.equalsIgnoreCase("BUS") || tMode.equalsIgnoreCase("TRAIN") || tMode.equalsIgnoreCase("PLANE"))
            {

                System.out.println("ref id:"+ conveyancePref.getLong(Constants.CONVEYANCE_TIMESTAMP, -1));
                callPictureActivity("START",tMode,0);
            }

        } else {
            Snackbar.make(travelExpenceAmount, getResources().getString(R.string.conveyance_error_fill_all_info), Snackbar.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void endTraveling()
    {
        String address=null;
        String expence=null;
        boolean isGpsAddress;

        isThreadRunning=false;
        if (!TextUtils.isEmpty(conveyancePref.getString(Constants.CONVEYANCE_END_ADDRESS, ""))) {
            address = conveyancePref.getString(Constants.CONVEYANCE_END_ADDRESS, "");
            isGpsAddress=true;
        }
        else {
            address = endGpsAddressManually.getText().toString().trim();
            isGpsAddress=false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String geoCode=CommonFunctions.getLocationFromAddress(ConveyanceActivity.this, endGpsAddressManually.getText().toString().trim());
                    System.out.println("end geoCode: "+geoCode);
                    if(geoCode!=null)
                    {
                        String[] gPoint=geoCode.split(",");
                        conveyancePref.edit().putString(Constants.CONVEYANCE_END_LAT_LOCATION, gPoint[0].trim()).apply();
                        conveyancePref.edit().putString(Constants.CONVEYANCE_END_LON_LOCATION, gPoint[1].trim()).apply();
                    }
                }
            });
        }

        expence = travelExpenceAmount.getText().toString().trim();

        if (address != null && address.trim().length() > 0 && expence != null && expence.trim().length() > 0) {
            System.out.println("Address: " + address);
            //conveyancePref.edit().clear().apply();

            conveyancePref.edit().putString(Constants.CONVEYANCE_END_TIME, CommonFunctions.getFormatedDate(System.currentTimeMillis())).apply();
            conveyancePref.edit().putLong(Constants.CONVEYANCE_END_TIMESTAMP, System.currentTimeMillis()).apply();
            conveyancePref.edit().putString(Constants.CONVEYANCE_END_EXPENCE, travelExpenceAmount.getText().toString()).apply();
            conveyancePref.edit().putBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED, false).apply();

            if(isGpsAddress) {
                conveyancePref.edit().putString(Constants.CONVEYANCE_END_LAT_LOCATION, latitude).apply();
                conveyancePref.edit().putString(Constants.CONVEYANCE_END_LON_LOCATION, longitude).apply();
            }

            conveyancePref.edit().putString(Constants.CONVEYANCE_END_ADDRESS, address).apply();


            conveyanceRunningState.setText(getResources().getString(R.string.conveyance_runningstory_end_msg,
                    conveyancePref.getString(Constants.CONVEYANCE_START_ADDRESS, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_START_MODE, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_START_TIME, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_END_ADDRESS, ""),
                    conveyancePref.getString(Constants.CONVEYANCE_END_TIME, "")
            ));

            endGpsAddressManually.setText("");
            travelExpenceAmount.setText("");
            endGpsAddressManually.setText("");

            startTravelButton.setEnabled(true);
            stopTravelButton.setEnabled(false);

            startTravelButton.setBackgroundResource(R.drawable.rounder_corner_button);
            stopTravelButton.setBackgroundResource(R.drawable.rounder_cancel_button);
            calculateDist();

            refreshGridView();

            getLocationFromGPS();

            startTravelLinearLayout.setVisibility(View.VISIBLE);
            endTravelLinearLayout.setVisibility(View.GONE);

        } else {
            Snackbar.make(travelExpenceAmount, getResources().getString(R.string.conveyance_error_fill_all_info), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        String address=null;
        String expence=null;
        boolean isGpsAddress;
        double latitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
        double longitude=Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        int accessValue = CommonFunctions.isAllowToAccessModules(ConveyanceActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        ///int accessValue=0;
        System.out.println("===========D"+accessValue);
        System.out.println("===========D"+loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));
        if (accessValue == 0) {
            switch (v.getId()) {
                case R.id.startTravelButton:
                    if (CommonFunctions.isPermissionGranted(ConveyanceActivity.this)) {
                        if (travelModePosition != -1) {
                            System.out.println("start");
                            startTraveling();
                        } else {
                            Toast.makeText(ConveyanceActivity.this, "Travel Mode not selected", Toast.LENGTH_SHORT).show();
                        }

                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.stopTravelButton:
                    if (CommonFunctions.isPermissionGranted(ConveyanceActivity.this)) {
                        System.out.println("stop");

                        endTraveling();

                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;

            }
        }
        else if (accessValue == 1) {
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




    private void calculateDist()
    {
        originPoint=new LatLng(Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_START_LAT_LOCATION,"0.0")), Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_START_LON_LOCATION,"0.0")));
        destPoint=new LatLng(Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_END_LAT_LOCATION,"0.0")), Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_END_LON_LOCATION,"0.0")));

        double dis=distance(Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_START_LAT_LOCATION,"0.0")),
                Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_START_LON_LOCATION,"0.0")),
                Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_END_LAT_LOCATION,"0.0")),
                        Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_END_LON_LOCATION,"0.0")));

        System.out.println("Distance by dist: "+dis);

        int duration=CommonFunctions.getDateDifferenceInMin(conveyancePref.getLong(Constants.CONVEYANCE_END_TIMESTAMP,-1), conveyancePref.getLong(Constants.CONVEYANCE_START_TIMESTAMP,-1) );
        System.out.println("Duration: "+duration);

        conveyancePref.edit().putString(Constants.CONVEYANCE_DISTANCE, getValues(String.valueOf(dis))).apply();
        conveyancePref.edit().putString(Constants.CONVEYANCE_DURATION, String.valueOf(duration)).apply();
        System.out.println("Distance by dist1: "+conveyancePref.getString(Constants.CONVEYANCE_DISTANCE,"0"));

        //added for new UI
        if(prevTravelModePosition!=-1) {
            transportModeArrayList.get(prevTravelModePosition).setTravelTime("0");
            transportModeArrayList.get(prevTravelModePosition).setTravelDistance(getValues(String.valueOf(dis)));
            transportModeArrayList.get(prevTravelModePosition).setSelected(false);
        }

        addPeopleEventLogValues(Constants.ATTENDANCE_PUNCH_TYPE_OUT,
                conveyancePref.getString(Constants.CONVEYANCE_END_LAT_LOCATION,"0.0")+","+conveyancePref.getString(Constants.CONVEYANCE_END_LON_LOCATION,"0.0"),
                conveyancePref.getLong(Constants.CONVEYANCE_TIMESTAMP,-1),
                conveyancePref.getString(Constants.CONVEYANCE_END_ADDRESS,""),
                "",
                Double.valueOf(conveyancePref.getString(Constants.CONVEYANCE_END_EXPENCE,"0.0")),
                Integer.parseInt(conveyancePref.getString(Constants.CONVEYANCE_DISTANCE,"0")),
                Integer.parseInt(conveyancePref.getString(Constants.CONVEYANCE_DURATION,"0")),
                conveyancePref.getLong(Constants.CONVEYANCE_END_TIMESTAMP,-1));


        //commented for new UI
        updateInfoView();

        //String tMode=transportModeArrayList.get(travelModePosition).getTravelMode();
        String tMode = conveyancePref.getString(Constants.CONVEYANCE_START_MODE, "");
        System.out.println("end tMode::" +tMode);


        if(tMode.equalsIgnoreCase("BIKE") || tMode.equalsIgnoreCase("CAR") || tMode.equalsIgnoreCase("RICKSHAW") )
        {
            callPictureActivity("END",tMode,1);
        }


        /*DownloadInfoAsyntask downloadInfoAsyntask=new DownloadInfoAsyntask();
        downloadInfoAsyntask.execute(url);*/
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {

        System.out.println("lat1"+ lat1+"lon1"+ lon1+"lat2"+ lat2+"lat1"+ lon2);
/*        double theta = lon1 - lon2;
        System.out.println("theta--::"+theta);

        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        System.out.println("dist1--::"+dist);

        dist = Math.acos(dist);
        System.out.println("dist2--::"+dist);

        dist = rad2deg(dist);
        System.out.println("dist3--::"+dist);

        dist = dist * 60 * 1.1515;

        System.out.println("dist4--::"+dist);
        if (Double.isNaN(dist)){
            System.out.println("true=======");
            dist = 0;
        }else {
            System.out.println("false=======");

        }*/

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);

        double dist=startPoint.distanceTo(endPoint);

        System.out.println("Distance:="+dist);
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onGridViewItemClick(final int position, String appName, Button view) {
        //Snackbar.make(travelModeGridview, appName,Snackbar.LENGTH_LONG).show();
        if(prevTravelModePosition==-1)
            prevTravelModePosition=position;
        else
            prevTravelModePosition=travelModePosition;

        travelModePosition=position;
        /*reallyButton.setVisibility(View.VISIBLE);
        reallyButton.setBackgroundColor(getResources().getColor(R.color.colorGray));*/
        getGPSAddress();

        for(int i=0;i<transportModeArrayList.size();i++)
        {
            if(i==position)
                transportModeArrayList.get(i).setSelected(true);
            else
                transportModeArrayList.get(i).setSelected(false);

         }
        transportModeGridViewAdapter.notifyDataSetChanged();
        transportModeGridViewAdapter=new TransportModeGridViewAdapter(ConveyanceActivity.this, transportModeArrayList,this);
        travelModeGridview.invalidateViews();
        travelModeGridview.setAdapter(transportModeGridViewAdapter);

        /*runningStoryTextview.setText("You have clicked on "+transportModeArrayList.get(position).getTravelMode()+"\n"+gpsAddress);
        reallyButton.setText("Really "+transportModeArrayList.get(position).getTravelMode());*/

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(ConveyanceActivity.this, isConnected,startTravelButton);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null)
        {
            latitude=String.valueOf(location.getLatitude());
            longitude=String.valueOf(location.getLongitude());
            //System.out.println(location.getLatitude()+":"+location.getLongitude());
            getGPSAddress();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }


    private void refreshGridView()
    {
        transportModeArrayList=new ArrayList<>();
        for(int i=0;i<transportModeList.size();i++)
        {
            TransportMode transportMode=new TransportMode();
            transportMode.setTravelMode(transportModeList.get(i).getTacode());
            transportMode.setTravelDistance("D");
            transportMode.setTravelMoney("M");
            transportMode.setTravelTime("T");
            transportMode.setSelected(false);
            transportModeArrayList.add(transportMode);

        }
        travelModeGridview=(GridView)findViewById(R.id.travelModeGridview);
        transportModeGridViewAdapter=new TransportModeGridViewAdapter(ConveyanceActivity.this, transportModeArrayList,this);
        travelModeGridview.setAdapter(transportModeGridViewAdapter);

        /*transportModeGridViewAdapter.notifyDataSetChanged();
        transportModeGridViewAdapter=new TransportModeGridViewAdapter(ConveyanceActivity.this, transportModeArrayList,this);
        travelModeGridview.invalidateViews();
        travelModeGridview.setAdapter(transportModeGridViewAdapter);*/
    }

    private void resetUI()
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                conveyancePref.edit().clear().apply();
                //updateInfoView();
                conveyanceRunningState.setText("");
                startReadingImage.setImageBitmap(null);
                stopReadingImage.setImageBitmap(null);
            }
        }, 10000);
    }

    private String getValues(String str)
    {
        String[] st=str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        return st[0];
    }




    //String punchType, String gpsLocation,long refValue, String remark, String transportMode, double expamout, int distance, int duration
    private void addPeopleEventLogValues(String punchType, String gpsLocation,long refValue, String remark, String transportMode, double expamout, int distance, int duration,long pelogid)
    {
        peopleEventLogInsertion=new PeopleEventLogInsertion(ConveyanceActivity.this);
        peopleEventLogInsertion.addConveyanceEvent(punchType, gpsLocation, refValue, remark, transportMode, expamout, distance, duration,pelogid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!conveyancePref.getBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED,false))
        {
            conveyancePref.edit().clear().apply();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!conveyancePref.getBoolean(Constants.CONVEYANCE_ISTRAVELSTARTED,false))
        {
            conveyancePref.edit().clear().apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.conveyance_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_refresh:
                String currRef=null, prefref=null;
                ArrayList<PeopleEventLog> peopleEventLogs=new ArrayList<>();
                peopleEventLogs=peopleEventLogDAO.getConveyanceLog();
                if(peopleEventLogs!=null && peopleEventLogs.size()>0)
                {

                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(ConveyanceActivity.this);
                    final NameAdapter AlertDialogueAdapter = new NameAdapter(this, R.layout.activity_conveyance_history_log_row, peopleEventLogs);

                    builderSingle.setIcon(R.drawable.youtility_icon);
                    builderSingle.setTitle(getResources().getString(R.string.conveyance_history_title));
                    builderSingle.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.setAdapter(AlertDialogueAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builderSingle.show();

                }
                else {
                    Snackbar.make(startTravelButton,getResources().getString(R.string.conveyance_history_not_fount),Snackbar.LENGTH_LONG).show();
                    System.out.println("Log size zero");
                }
                return true;
            case R.id.action_currentLoc:
                Intent mapIntent=new Intent(ConveyanceActivity.this, UserLocationViewActivity.class);
                mapIntent.putExtra("FROM", "GETLOCATION");
                startActivityForResult(mapIntent,100);
                return  true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    public class NameAdapter extends ArrayAdapter<PeopleEventLog>{

        public NameAdapter(Context context,int resource, List<PeopleEventLog> objects) {
            super(context,resource, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {

                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.activity_conveyance_history_log_row, parent, false);
            }
            final PeopleEventLog message = getItem(position);
            //TextView NameOfRequester = (TextView) convertView.findViewById(R.id.conveyanceReferTextView);
            //NameOfRequester.setText(message.getReference());
            System.out.println(message.getReference()+"====id");
            TextView punchType = (TextView) convertView.findViewById(R.id.punchTypeTextView);
            if(typeAssistDAO.getEventTypeCode(message.getPunchstatus()).equalsIgnoreCase("IN")) {
                punchType.setText(getResources().getString(R.string.button_start));
                //NameOfRequester.setText("START");
                punchType.setTextColor(Color.GREEN);
            }
            else if(typeAssistDAO.getEventTypeCode(message.getPunchstatus()).equalsIgnoreCase("OUT")) {
                punchType.setText(getResources().getString(R.string.button_stop));
                //NameOfRequester.setText("STOP");

                punchType.setTextColor(Color.RED);
            }
            else {
                punchType.setText(getResources().getString(R.string.button_start));
                //NameOfRequester.setText("START");

                punchType.setTextColor(Color.GREEN);
            }

            TextView punchTime = (TextView) convertView.findViewById(R.id.punchTimestampTextView);
            punchTime.setText(CommonFunctions.getFormatedDate(message.getDatetime()));
            TextView convAddress = (TextView) convertView.findViewById(R.id.conveyanceAddressTextView);
            convAddress.setText((message.getRemarks()));
            TextView convDistance = (TextView) convertView.findViewById(R.id.conveyanceDistanceTextView);
            TextView convDuration = (TextView) convertView.findViewById(R.id.conveyanceDurationTextView);
            TextView convExpense = (TextView) convertView.findViewById(R.id.conveyanceExpenseTextView);
            if(typeAssistDAO.getEventTypeCode(message.getPunchstatus()).equalsIgnoreCase("OUT"))
            {
                convDistance.setVisibility(View.VISIBLE);
                //convDuration.setVisibility(View.GONE);*/
                convExpense.setVisibility(View.VISIBLE);
                convDistance.setText("Distance in meter: "+(message.getDistance())+"");
                //convDuration.setText("Duration in min: "+(message.getDuration())+"");
                convExpense.setText(getResources().getString(R.string.expence_amout_hint)+":"+(message.getExpamt())+"");
            }
            else
            {
                convDistance.setVisibility(View.GONE);
                convDuration.setVisibility(View.GONE);
                convExpense.setVisibility(View.GONE);
            }


            return convertView;
        }
    }


}
