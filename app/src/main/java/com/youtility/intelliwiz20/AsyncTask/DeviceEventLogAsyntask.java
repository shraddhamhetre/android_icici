package com.youtility.intelliwiz20.AsyncTask;

import android.Manifest;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.youtility.intelliwiz20.DataAccessObject.DeviceEventLogDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadDeviceEventLogDataListener;
import com.youtility.intelliwiz20.Model.DeviceEventLog;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.MemoryInfo;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.DEVICE_POLICY_SERVICE;

/**
 * Created by youtility4 on 6/10/17.
 *
 * not in used
 */

public class DeviceEventLogAsyntask extends AsyncTask<Void, Integer, Integer> {
    Context context;
    IUploadDeviceEventLogDataListener iUploadDeviceEventLogDataListener;
    private ArrayList<DeviceEventLog> deviceEventLogArrayList;
    DeviceEventLog deviceEventLog;
    DeviceEventLogDAO deviceEventLogDAO;
    private SharedPreferences autoSyncPref;
    private SharedPreferences loginPref;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    private TelephonyManager telephonyManager;
    private WifiManager wifiManager;

    private Object SignalStrength;


    public DeviceEventLogAsyntask(Context context, IUploadDeviceEventLogDataListener iUploadDeviceEventLogDataListener) {
        this.context = context;
        this.iUploadDeviceEventLogDataListener = iUploadDeviceEventLogDataListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        deviceEventLogDAO = new DeviceEventLogDAO(context);
        loginPref = context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        autoSyncPref = context.getSharedPreferences(Constants.AUTO_SYNC_PREF, Context.MODE_PRIVATE);

        devicePolicyManager = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }

    public boolean checkActivity() {
        boolean checkActivity = false;
        boolean captureActivity = false;
        long currentTimestamp = System.currentTimeMillis();
        long previousTimestamp = autoSyncPref.getLong(Constants.CAMERA_ON_TIMESTAMP, 0l);
        int diffCaptureActivity = CommonFunctions.getDateDifferenceInMin(currentTimestamp, previousTimestamp);

        System.out.println("diffCaptureActivity ::" + diffCaptureActivity);

        String[] ListActivities = {"AttendanceCapturePhotoActivity", "CaptureActivity", "SelfAttendanceActivity", "CapturePhotoActivity", "SiteListActivity"};
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        System.out.println("topActivity CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        String activityName = taskInfo.get(0).topActivity.getClassName();
        //System.out.println("@@activityName:="+activityName +"::"+(activityName.endsWith("CheckpointListActivity")));
        boolean readyToSync = true;
        for (int activity = 0; activity < ListActivities.length; activity++) {
            checkActivity = activityName.endsWith(ListActivities[activity]);
            captureActivity = activityName.endsWith("CaptureActivity");
            System.out.println("cameraActivity ::" + captureActivity);

            if (captureActivity) {
                if (diffCaptureActivity < 3) {
                    readyToSync = false;
                }
            } else if (checkActivity) {
                System.out.println("@@ CURRENT Activity(camera) :: true");
                readyToSync = false;
                break;
            }
        }
        System.out.println("readyToSync del" + readyToSync);
        return readyToSync;
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected Integer doInBackground(Void... voids) {
        int status = -1;
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            {
                System.out.println("check permission");
            }
        } else {
            try {
                checkNetworkCpuInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*        if (checkActivity()) {*/
        deviceEventLogArrayList = new ArrayList<>();
        deviceEventLogArrayList = deviceEventLogDAO.getUnsyncDeviceEvents(loginPref.getLong(Constants.LOGIN_SITE_ID, -1));
        if (deviceEventLogArrayList != null && deviceEventLogArrayList.size() > 0) {
            System.out.println("deviceEventLogArrayList : " + deviceEventLogArrayList.size());
            for (int i = 0; i < deviceEventLogArrayList.size(); i++) {
                if (checkActivity()) {
                    DeviceEventLog deviceEventLog = new DeviceEventLog();
                    deviceEventLog = deviceEventLogArrayList.get(i);

                    try {
                        String insertQuery = DatabaseQuries.DEVICE_EVENTLOG_INSERT + "( '" + deviceEventLog.getDeviceid() + "', '" + deviceEventLog.getEventvalue() + "', '" + deviceEventLog.getGpslocation() + "', " + deviceEventLog.getAccuracy() + "," +
                                deviceEventLog.getAltitude() + ", '" + deviceEventLog.getBatterylevel() + "', '" + deviceEventLog.getSignalstrength() + "', '" + deviceEventLog.getAvailextmemory() + "', '" + deviceEventLog.getAvailintmemory() + "', " +
                                "'" + deviceEventLog.getCdtz() + "','" + deviceEventLog.getMdtz() + "', " + deviceEventLog.getCuser() + "," + deviceEventLog.getEventtype() + "," + deviceEventLog.getMuser() + "," +
                                deviceEventLog.getPeopleid() + ",'" + deviceEventLog.getSignalbandwidth() + "'," + deviceEventLog.getBuid() + ",'" + deviceEventLog.getAndroidosversion() + "','" + deviceEventLog.getApplicationversion() + "'," +
                                "'" + deviceEventLog.getModelname() + "','" + deviceEventLog.getInstalledapps() + "','" + deviceEventLog.getSimserialnumber() + "','" + deviceEventLog.getLinenumber() + "','" + deviceEventLog.getNetworkprovidername() + "','" + deviceEventLog.getStepCount() + "') returning deviceeventlogid;";

                        System.out.println("del query==" + insertQuery);
                        ServerRequest serverRequest = new ServerRequest(context);
                        HttpResponse response = serverRequest.getDeviceEventLogResponse(insertQuery.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER, 0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE, ""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID, ""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS, ""));


                        if (response != null && response.getStatusLine().getStatusCode() == 200) {
                            is = response.getEntity().getContent();

                            sb = new StringBuffer("");
                            buffer = new byte[1024];
                            bytesRead = 0;
                            try {
                                while ((bytesRead = is.read(buffer)) != -1) {
                                    sb.append(new String(buffer));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            is.close();
                            System.out.println("SB DeviceEventLog: " + sb.toString());
                            CommonFunctions.UploadLog("\n <DELOG> \n" + sb + "\n" + insertQuery + "\n");

                            response.getEntity().consumeContent();

                            JSONObject ob = new JSONObject(sb.toString());
                            if (ob.getInt(Constants.RESPONSE_RC) == 0) {
                                status = 0;
                                deviceEventLogDAO.changeSyncStatus(deviceEventLog.getCdtz());
                                deviceEventLogDAO.deleteRec(deviceEventLog.getCdtz());
                            } else {
                                status = -1;
                                break;
                            }
                        } else {
                            CommonFunctions.UploadLog("\n <DELOG Network Status::>  Network Status:  \n" + response.getStatusLine().getStatusCode() + "\n Signal Strength : " + telephonyManager.getSignalStrength().getGsmSignalStrength() + "\n" +"getSimState"+ telephonyManager.getSimState());
                            System.out.println("SB1 DeviceEventLog ERROR ");
                            status = -1;
                            break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnrecoverableKeyException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("Ready to Sync del:: " + checkActivity());
                }
            }
        } else {
            status = 0;
        }
        return status;

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void checkNetworkCpuInfo() throws IOException {

        //CPU Information

        String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
        ProcessBuilder processBuilder = new ProcessBuilder(DATA);
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        byte[] byteArry = new byte[1024];
        String output = "";
        while (inputStream.read(byteArry) != -1) {
            output = output + new String(byteArry);
        }
        inputStream.close();

        //Wifi Strength

        List<ScanResult> wifiList = wifiManager.getScanResults();
        for (ScanResult scanResult : wifiList) {
            int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
        }
        int rssi = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 5);

        //Mobile network connected or not

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }

        //Memory Status in percentage

        double avalb = MemoryInfo.getAvailableInternalMemory() / 0x100000L;
        double totalMem = MemoryInfo.getTotalInternalMemory() / 0x100000L;
        double percentAvail = avalb / (double)totalMem * 100.0;


        MemoryInfo.checkMemoryInternalAvailable();
        CommonFunctions.UploadLog("\n <Network Status> Available Internal memory :" + percentAvail + "\n Mobile network is on? :" + mobileDataEnabled + "\n Wifi Level is :"+level + "out of 5"+ "\nCPU INFO :"+ output + "\ngetSignalStrength : " +telephonyManager.getSignalStrength().getGsmSignalStrength()  );
        System.out.println("Available Internal memory :"+ avalb + " " + totalMem + " " + percentAvail);
        System.out.println("Mobile network is on? :"+mobileDataEnabled);
        System.out.println("Wifi Level is :" + level + " out of 5");
        System.out.println("CPU INFO :"+output);
        System.out.println("getSignalStrength : "+telephonyManager.getSignalStrength().getGsmSignalStrength() + telephonyManager.getSimState());

    }

    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(status);
        iUploadDeviceEventLogDataListener.finishDeviceEventLogUpload(status);
    }

}
