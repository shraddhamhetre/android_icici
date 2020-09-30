package com.youtility.intelliwiz20.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.GeofenceDAO;
import com.youtility.intelliwiz20.DataAccessObject.PeopleDAO;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Model.Geofence;
import com.youtility.intelliwiz20.Model.People;
import com.youtility.intelliwiz20.Model.ResponseData;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.RetrofitServices;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserLocationViewActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private Spinner userNameSpinner;
    private ImageButton getUserLocationButton;
    private ArrayList<String>peopleNameList;
    private ArrayList<People>peopleArrayList;
    private PeopleDAO peopleDAO;
    private SharedPreferences loginPref;
    private GeofenceDAO geofenceDAO;
    private ArrayList<Geofence>geofenceArrayList=null;
    private List<LatLng>list=null;
    private ArrayList<List<LatLng>>listArrayList=null;
    private String fromActivity;
    private TextView userLastKnCordinates;
    private SharedPreferences deviceRelatedPref;
    private SharedPreferences applicationMainPref;

    private ArrayList<DeviceEventLog> deviceEventLogArrayList;
    private ConnectivityReceiver connectivityReceiver;

    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fromActivity=getIntent().getStringExtra("FROM");

        loginPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);
        applicationMainPref=getSharedPreferences(Constants.APPLICATION_MAIN_PREF,MODE_PRIVATE);

        userNameSpinner = (Spinner) findViewById(R.id.userNameSpinner);
        getUserLocationButton = (ImageButton) findViewById(R.id.getCurrentLocButton);
        userLastKnCordinates=(TextView)findViewById(R.id.userLastKnowLocationCordinate);


        if(fromActivity.equalsIgnoreCase("GEOFENCE")) {
            geofenceDAO = new GeofenceDAO(UserLocationViewActivity.this);
            geofenceArrayList = geofenceDAO.getGeofenceList(loginPref.getLong(Constants.LOGIN_SITE_ID, -1));
            if (geofenceArrayList != null && geofenceArrayList.size() > 0) {

                listArrayList=new ArrayList<>();
                for (int i = 0; i < geofenceArrayList.size(); i++) {
                    System.out.println("GeoFence: " + geofenceArrayList.get(i).getGeofence());
                    String[] geoFencPoint = geofenceArrayList.get(i).getGeofence().split("~");
                    if (geoFencPoint.length > 0) {
                        list = new ArrayList<>();
                        for (int j = 0; j < geoFencPoint.length; j++) {
                            String[] latlonpoint = geoFencPoint[j].split(",");
                            LatLng latLng = new LatLng(Double.valueOf(latlonpoint[0]), Double.valueOf(latlonpoint[1]));
                            list.add(j, latLng);
                        }
                    }
                    listArrayList.add(i,list);
                }
            } else
                System.out.println("Geofence not active");

        }

        if(fromActivity.equalsIgnoreCase("USERLOCATION")) {

            peopleDAO = new PeopleDAO(UserLocationViewActivity.this);
            peopleArrayList = peopleDAO.getPeopleList();
            if (peopleArrayList != null && peopleArrayList.size() > 0) {
                peopleNameList = new ArrayList<>();
                for (int i = 0; i < peopleArrayList.size(); i++) {
                    peopleNameList.add(peopleArrayList.get(i).getPeoplename());
                }
            }

            if (peopleNameList != null && peopleNameList.size() > 0) {
                ArrayAdapter statusAdpt = new ArrayAdapter(UserLocationViewActivity.this, android.R.layout.simple_spinner_item, peopleNameList);
                statusAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userNameSpinner.setAdapter(statusAdpt);
            }

            getUserLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonFunctions.isPermissionGranted(UserLocationViewActivity.this)) {
                        if (peopleArrayList != null && peopleArrayList.size() > 0) {
                            System.out.println("Selected user: " + userNameSpinner.getSelectedItem().toString());
                            //select * from deviceeventlog where peopleid= 5017475717563657 order by cdtz desc limit 1
                            /*GetUserLastKnownLocationAsyntask getUserLastKnownLocationAsyntask = new GetUserLastKnownLocationAsyntask(peopleArrayList.get(userNameSpinner.getSelectedItemPosition()).getPeopleid());
                            getUserLastKnownLocationAsyntask.execute();*/

                            getUserLastKnowLocation(peopleArrayList.get(userNameSpinner.getSelectedItemPosition()).getPeopleid());
                        }
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

                }
            });

        }
        else
        {
            userNameSpinner.setVisibility(View.GONE);
            getUserLocationButton.setVisibility(View.GONE);
        }

        updateLocationText();

    }

    private void updateLocationText()
    {
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                double latitude=Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0"));
                double longitude=Double.valueOf(deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                userLastKnCordinates.setText(CommonFunctions.getDMSFormatLocation(latitude, longitude));
                //handler.postDelayed( this, 60 * 1000 );
            }
        }, 30 * 1000 );
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            // Add a marker in Sydney and move the camera
            /*LatLng thane = new LatLng(19.194385, 72.999201);
            mMap.addMarker(new MarkerOptions().position(thane).title("Marker in Thane"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(thane));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));*/
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Done")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Intent ii=new Intent();
                            ii.putExtra("LAT_LON",(marker.getPosition().latitude)+","+(marker.getPosition().longitude));
                            setResult(RESULT_OK,ii);
                            finish();
                            return false;
                        }
                    });
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                }
            });
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                return;
            }
            else
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            if(listArrayList!=null && listArrayList.size()>0)
            {
                googleMap.clear();

                for(int i=0;i<listArrayList.size();i++)
                {
                    List<LatLng> listCor=listArrayList.get(i);
                    if(listCor!=null && listCor.size()>0)
                    {
                        PolylineOptions polyOptions = new PolylineOptions();
                        polyOptions.color(Color.RED);
                        polyOptions.width(5);
                        polyOptions.addAll(listCor);

                        googleMap.addPolyline(polyOptions);
                    }
                }

                for(int i=0;i<listArrayList.size();i++)
                {
                    LatLng thane = listArrayList.get(i).get(0);
                    mMap.addMarker(new MarkerOptions().position(thane).title("geofence "+i));
                }

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : listArrayList.get(0)) {
                    builder.include(latLng);
                }

                final LatLngBounds bounds = builder.build();

                //BOUND_PADDING is an int to specify padding of bound.. try 100.
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
                googleMap.animateCamera(cu);
            }
            /*if(list!=null && list.size()>0) {
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(Color.RED);
                polyOptions.width(5);
                polyOptions.addAll(list);

                googleMap.clear();
                googleMap.addPolyline(polyOptions);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : list) {
                    builder.include(latLng);
                }

                final LatLngBounds bounds = builder.build();

                //BOUND_PADDING is an int to specify padding of bound.. try 100.
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
                googleMap.animateCamera(cu);
            }*/


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getUserLastKnowLocation(long peopleId)
    {


        final ProgressDialog progressDialog = new ProgressDialog(UserLocationViewActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage(getResources().getString(R.string.userlocationview_findinguserlocation)); // set message
        progressDialog.show(); // show progress dialog

        String queryInfo="select * from deviceeventlog where peopleid= "+peopleId+" order by cdtz desc limit 1";

        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(Constants.SERVICE_SELECT);
        uploadParameters.setQuery(queryInfo);
        uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);
        uploadParameters.setTzoffset(String.valueOf(loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)));
        uploadParameters.setSitecode(applicationMainPref.getString(Constants.APPLICATION_USER_ENTER_CLIENT_CODE,"")+"."+loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""));
        uploadParameters.setLoginid(loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""));
        uploadParameters.setPassword(loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));
        //uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getLong(Constants.DEVICE_IMEI, -1)));
        uploadParameters.setDeviceid(String.valueOf(deviceRelatedPref.getString(Constants.DEVICE_IMEI, "-1")));

        Gson gson=new Gson();
        System.out.println("uploadParameters: "+gson.toJson(uploadParameters));

        /*RetrofitServices retrofitServices= RetrofitClient.getClient().create(RetrofitServices.class);
        Call<ResponseData> call=retrofitServices.getServerResponse(Constants.SERVICE_SELECT,uploadParameters);*/

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.IMAGE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitServices retrofitServices = retrofit.create(RetrofitServices.class);
        Call<ResponseData> call=retrofitServices.getServerResponse1(uploadParameters);

        System.err.println(call);
        System.out.println(call);
        call.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(@NonNull Call<ResponseData> call, @NonNull Response<ResponseData> response) {
                progressDialog.dismiss();
                Type listType;
                mMap.clear();
                if(response.isSuccessful() && response.body()!=null)
                {
                    System.out.println("response.getStatus(): "+response.body().getStatus());
                    System.out.println("response.getRow_data(): "+response.body().getRow_data());
                    System.out.println("response.getNrow(): "+response.body().getNrow()+"");
                    System.out.println("response.getRc(): "+response.body().getRc());
                    System.out.println("response.getColumns(): "+response.body().getColumns());
                    try {
                        if(response.body().getNrow()>0)
                        {
                            String mainSplitRowChar = String.valueOf(response.body().getRow_data().charAt(0));
                            String mainSplitColumnChar=String.valueOf(response.body().getColumns().charAt(0));

                            if (mainSplitRowChar.trim().equalsIgnoreCase("|")) {
                                mainSplitRowChar = "\\|";
                            } else if (mainSplitRowChar.trim().equalsIgnoreCase("$")) {
                                mainSplitRowChar = "\\$";
                            }

                            if (mainSplitColumnChar.trim().equalsIgnoreCase("|")) {
                                mainSplitColumnChar = "\\|";
                            } else if (mainSplitColumnChar.trim().equalsIgnoreCase("$")) {
                                mainSplitColumnChar = "\\$";
                            }

                            String[] responseSplit = response.body().getRow_data().split(mainSplitRowChar);
                            String[] cols=response.body().getColumns().split(mainSplitColumnChar);
                            System.out.println("Length: " + responseSplit.length);

                            JSONArray dataArray = new JSONArray();
                            JSONObject dataObject = new JSONObject();


                            for (int i = 1; i < (responseSplit.length); i++) {
                                System.out.println("split string: " + responseSplit[i]);
                                //System.out.println("split string number: "+i);
                                if (responseSplit[i].trim().length() > 0) {
                                    Character startDelimitor = responseSplit[i].charAt(0);
                                    System.out.println("Start Delimeter: " + startDelimitor);
                                    String[] respRow = null;
                                    if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                        respRow = responseSplit[i].trim().split("\\$");
                                    } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                        respRow = responseSplit[i].trim().split("\\|");
                                    } else {
                                        respRow = responseSplit[i].trim().split(startDelimitor.toString(), 0);
                                    }


                                    if(respRow!=null && respRow.length>0) {
                                        JSONObject jsonObject = new JSONObject();
                                        for (int c = 1; c < respRow.length; c++) {
                                            jsonObject.put(cols[c], respRow[c]);
                                        }
                                        dataArray.put(jsonObject);
                                    }


                                }

                            }

                            dataObject.put("Data", dataArray);
                            Gson gson = new Gson();
                            JSONArray data1 = dataObject.getJSONArray("Data");
                            listType = new TypeToken<ArrayList<DeviceEventLog>>() {
                            }.getType();
                            deviceEventLogArrayList = gson.fromJson(data1.toString(), listType);
                            redirectLocationOnMap();

                        }
                        else
                            Snackbar.make(getUserLocationButton,getResources().getString(R.string.userlocation_notfound_error),Snackbar.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseData> call, Throwable t) {
                progressDialog.dismiss();
                mMap.clear();
            }
        });
    }

    private void redirectLocationOnMap()
    {
        List<Address> addresses;
        if(deviceEventLogArrayList!=null && deviceEventLogArrayList.size()>0) {
            System.out.println("User Location: " + deviceEventLogArrayList.get(0).getGpslocation());
            String[] gpsLoc = deviceEventLogArrayList.get(0).getGpslocation().split(",");
            try {
                if (gpsLoc != null && gpsLoc.length > 0) {
                    if (Double.valueOf(gpsLoc[0]) != 0.0 || Double.valueOf(gpsLoc[1]) != 0.0) {
                        mMap.clear();
                        LatLng thane = new LatLng(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1]));
                        Geocoder geoCoder=new Geocoder(UserLocationViewActivity.this, Locale.ENGLISH);
                        addresses=geoCoder.getFromLocation(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1]), 1);
                        Address returnAddress = addresses.get(0);
                        mMap.addMarker(new MarkerOptions().position(thane).title(CommonFunctions.getDeviceTimezoneFormatDate(deviceEventLogArrayList.get(0).getCdtz())).snippet(returnAddress.getAddressLine(0)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(thane));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                    else {
                        Snackbar.make(getUserLocationButton,"GPS Location: "+gpsLoc[0]+","+gpsLoc[1],Snackbar.LENGTH_LONG).show();
                    }


                }

            } catch (IOException e) {
                mMap.clear();
                LatLng thane = new LatLng(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1]));
                mMap.addMarker(new MarkerOptions().position(thane).title(CommonFunctions.getDeviceTimezoneFormatDate(deviceEventLogArrayList.get(0).getCdtz())));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(thane));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                e.printStackTrace();
            }
        }
        else
        {
            mMap.clear();
            Snackbar.make(getUserLocationButton,getResources().getString(R.string.userlocation_notfound_error),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(UserLocationViewActivity.this, isConnected,getUserLocationButton);
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



    private class GetUserLastKnownLocationAsyntask extends AsyncTask<Void , Integer, Void>
    {
        long peopleId=-1;
        StringBuffer sb;
        InputStream is;
        byte[] buffer = null;
        int byteread = 0;
        private Type listType;
        private ArrayList<DeviceEventLog> deviceEventLogArrayList;
        private ProgressDialog dialog;
        List<Address> addresses;
        public GetUserLastKnownLocationAsyntask(long peopleId)
        {
            this.peopleId=peopleId;
            dialog = new ProgressDialog(UserLocationViewActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deviceEventLogArrayList=new ArrayList<>();
            dialog.setMessage("Finding user last known location, Please wait...");
            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                //---------------------------------------------------------------------
                String queryInfo="select * from deviceeventlog where peopleid= "+peopleId+" order by cdtz desc limit 1";
                Gson gson = new Gson();
                ServerRequest serverRequest=new ServerRequest(UserLocationViewActivity.this);
                HttpResponse response=serverRequest.getUserLocationLogResponse(queryInfo.trim(),
                                                                            loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                                                            loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                                                            loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                                                            loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                System.out.println("getUserlocation response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                if(response.getStatusLine().getStatusCode()==200)
                {
                    is = response.getEntity().getContent();

                    sb = new StringBuffer("");
                    buffer = new byte[1024];
                    byteread = 0;
                    try {
                        while ((byteread = is.read(buffer)) != -1) {
                            sb.append(new String(buffer));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    is.close();
                    System.out.println("SB getUserlocation: " + sb.toString());
                    response.getEntity().consumeContent();

                    JSONObject ob = new JSONObject(sb.toString());

                    int status = ob.getInt(Constants.RESPONSE_RC);
                    int nrow = ob.getInt(Constants.RESPONSE_NROW);
                    if(status==0 && nrow>0)
                    {
                        String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                        String colums=ob.getString(Constants.RESPONSE_COLUMNS);
                        System.out.println("status: " + status);
                        System.out.println("response: " + resp.toString());
                        if(resp.toString().trim().length()>0)
                        {
                            String mainSplitRowChar = String.valueOf(resp.charAt(0));
                            String mainSplitColumnChar=String.valueOf(colums.charAt(0));
                            System.out.println("Starting split char: " + resp.charAt(0));

                            if (mainSplitRowChar.toString().trim().equalsIgnoreCase("|")) {
                                mainSplitRowChar = "\\|";
                            } else if (mainSplitRowChar.toString().trim().equalsIgnoreCase("$")) {
                                mainSplitRowChar = "\\$";
                            }

                            if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                                mainSplitColumnChar = "\\|";
                            } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                                mainSplitColumnChar = "\\$";
                            }

                            String[] responseSplit = resp.split(mainSplitRowChar);
                            String[] cols=colums.split(mainSplitColumnChar);
                            System.out.println("Length: " + responseSplit.length);

                            JSONArray dataArray = new JSONArray();
                            JSONObject dataObject = new JSONObject();


                            for (int i = 1; i < (responseSplit.length); i++) {
                                System.out.println("split string: " + responseSplit[i].toString());
                                //System.out.println("split string number: "+i);
                                if (responseSplit[i].toString().trim().length() > 0) {
                                    Character startDelimitor = responseSplit[i].charAt(0);
                                    System.out.println("Start Delimeter: " + startDelimitor);
                                    String[] respRow = null;
                                    if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                        respRow = responseSplit[i].toString().trim().split("\\$");
                                    } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                        respRow = responseSplit[i].toString().trim().split("\\|");
                                    } else {
                                        respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                                    }


                                    if(respRow!=null && respRow.length>0) {
                                        JSONObject jsonObject = new JSONObject();
                                        for (int c = 1; c < respRow.length; c++) {
                                            jsonObject.put(cols[c].toString(), respRow[c]);
                                        }
                                        dataArray.put(jsonObject);
                                    }


                                }

                            }

                            dataObject.put("Data", dataArray);

                            JSONArray data1 = dataObject.getJSONArray("Data");
                            listType = new TypeToken<ArrayList<DeviceEventLog>>() {
                            }.getType();
                            deviceEventLogArrayList = gson.fromJson(data1.toString(), listType);

                        }
                    }

                }


                //----------------------------------------------------------------------------------------


                /*URL url = new URL(Constants.BASE_URL); // here is your URL path

                UploadParameters uploadParameters=new UploadParameters();
                uploadParameters.setServicename(Constants.SERVICE_SELECT);
                uploadParameters.setStory(Constants.STORY_NOT_REQUIRED);

                String queryInfo="select * from deviceeventlog where peopleid= "+peopleId+" order by cdtz desc limit 1";

                uploadParameters.setQuery(queryInfo);

                Gson gson = new Gson();
                String upData = gson.toJson(uploadParameters);
                System.out.println("upData: "+upData);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 *//* milliseconds *//*);
                conn.setConnectTimeout(15000 *//* milliseconds *//*);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");


                StringEntity data=new StringEntity(upData, HTTP.UTF_8);
                OutputStream out = new BufferedOutputStream(conn.getOutputStream());


                is = data.getContent();
                buffer = new byte[1024];
                byteread = 0;
                while ((byteread = is.read(buffer)) != -1)
                {
                    out.write(buffer, 0, byteread);
                }
                out.flush();
                out.close();
                is.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    in.close();
                    System.out.println("SB: "+sb.toString());

                }
                else {
                    System.out.println("SB: "+responseCode);
                }

                JSONObject ob = new JSONObject(sb.toString());

                int status = ob.getInt(Constants.RESPONSE_RC);
                int nrow = ob.getInt(Constants.RESPONSE_NROW);
                if(status==0 && nrow>0)
                {
                    String resp = ob.getString(Constants.RESPONSE_ROWDATA);
                    String colums=ob.getString(Constants.RESPONSE_COLUMNS);
                    System.out.println("status: " + status);
                    System.out.println("response: " + resp.toString());
                    if(resp.toString().trim().length()>0)
                    {
                        String mainSplitRowChar = String.valueOf(resp.charAt(0));
                        String mainSplitColumnChar=String.valueOf(colums.charAt(0));
                        System.out.println("Starting split char: " + resp.charAt(0));

                        if (mainSplitRowChar.toString().trim().equalsIgnoreCase("|")) {
                            mainSplitRowChar = "\\|";
                        } else if (mainSplitRowChar.toString().trim().equalsIgnoreCase("$")) {
                            mainSplitRowChar = "\\$";
                        }

                        if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("|")) {
                            mainSplitColumnChar = "\\|";
                        } else if (mainSplitColumnChar.toString().trim().equalsIgnoreCase("$")) {
                            mainSplitColumnChar = "\\$";
                        }

                        String[] responseSplit = resp.split(mainSplitRowChar);
                        String[] cols=colums.split(mainSplitColumnChar);
                        System.out.println("Length: " + responseSplit.length);

                        JSONArray dataArray = new JSONArray();
                        JSONObject dataObject = new JSONObject();


                        for (int i = 1; i < (responseSplit.length); i++) {
                            System.out.println("split string: " + responseSplit[i].toString());
                            //System.out.println("split string number: "+i);
                            if (responseSplit[i].toString().trim().length() > 0) {
                                Character startDelimitor = responseSplit[i].charAt(0);
                                System.out.println("Start Delimeter: " + startDelimitor);
                                String[] respRow = null;
                                if (startDelimitor.toString().equalsIgnoreCase("$")) {
                                    respRow = responseSplit[i].toString().trim().split("\\$");
                                } else if (startDelimitor.toString().equalsIgnoreCase("|")) {
                                    respRow = responseSplit[i].toString().trim().split("\\|");
                                } else {
                                    respRow = responseSplit[i].toString().trim().split(startDelimitor.toString(), 0);
                                }


                                if(respRow!=null && respRow.length>0) {
                                    JSONObject jsonObject = new JSONObject();
                                    for (int c = 1; c < respRow.length; c++) {
                                        jsonObject.put(cols[c].toString(), respRow[c]);
                                    }
                                    dataArray.put(jsonObject);
                                }


                            }

                        }

                        dataObject.put("Data", dataArray);

                        JSONArray data1 = dataObject.getJSONArray("Data");
                        listType = new TypeToken<ArrayList<DeviceEventLog>>() {
                        }.getType();
                        deviceEventLogArrayList = gson.fromJson(data1.toString(), listType);

                    }
                }*/


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(deviceEventLogArrayList!=null && deviceEventLogArrayList.size()>0) {
                System.out.println("User Location: " + deviceEventLogArrayList.get(0).getGpslocation());
                String[] gpsLoc = deviceEventLogArrayList.get(0).getGpslocation().split(",");
                try {
                    if (gpsLoc != null && gpsLoc.length > 0) {
                        if (Double.valueOf(gpsLoc[0]) != 0.0 || Double.valueOf(gpsLoc[1]) != 0.0) {
                            mMap.clear();
                            LatLng thane = new LatLng(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1]));
                            Geocoder geoCoder=new Geocoder(UserLocationViewActivity.this, Locale.ENGLISH);
                            addresses=geoCoder.getFromLocation(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1]), 1);
                            Address returnAddress = addresses.get(0);
                            mMap.addMarker(new MarkerOptions().position(thane).title(CommonFunctions.getDeviceTimezoneFormatDate(deviceEventLogArrayList.get(0).getCdtz())).snippet(returnAddress.getAddressLine(0)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(thane));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                        }
                    }
                } catch (IOException e) {
                    mMap.clear();
                    LatLng thane = new LatLng(Double.valueOf(gpsLoc[0]), Double.valueOf(gpsLoc[1]));
                    mMap.addMarker(new MarkerOptions().position(thane).title(CommonFunctions.getDeviceTimezoneFormatDate(deviceEventLogArrayList.get(0).getCdtz())));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(thane));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    e.printStackTrace();
                }
            }
            else
            {
                mMap.clear();
                Snackbar.make(getUserLocationButton,getResources().getString(R.string.userlocation_notfound_error),Snackbar.LENGTH_LONG).show();
            }

        }

    }
}
