package com.youtility.intelliwiz20.Utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.youtility.intelliwiz20.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

/*
import com.google.android.gms.vision.barcode.Barcode;
*/


/**
 * Created by PrashantD on 8/9/17.
 *
 * place holder for some useful common functions
 */

public class CommonFunctions extends PhoneStateListener {
    public static int LD;
   public static String dueDate = "";
   public static SimpleDateFormat format;
   public static long d=-1l;



    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
        /*if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;*/
    }
//http://redmine.youtility.in:81/svn/youtility2/trunk/Mobile_14022019
    public static void ResponseLog(String text)
    {
        if(isExternalStorageWritable())
        {
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() +"/"+Constants.FOLDER_NAME+"/");

            int f_size = 1024 * 1024 * 10;
            String f=logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger=Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler=null;
            try{
                fileHandler=new FileHandler(f+"/ServerResponseLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO,text.trim());
                //System.out.println("file created   " + text);
            }catch(IOException e){
                logger.warning("Failed to initialize logger handler.");
            }

            finally{
                if (fileHandler != null)
                    fileHandler.close();
            }
        }

    }

    public static void EventLog(String text)
    {
        if(isExternalStorageWritable()) {
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");

            int f_size = 1024 * 1024 * 10;
            String f = logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger = Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(f + "/EventLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO, text.trim());
                //System.out.println("file created   " + text);
            } catch (IOException e) {
                logger.warning("Failed to initialize logger handler.");
            } finally {
                if (fileHandler != null)
                    fileHandler.close();
            }
        }
    }

    public static void manualSyncEventLog(String heading,String text,String timestamp)
    {
        if(isExternalStorageWritable()) {
            File pDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            try {
                if (!pDir.exists()) {
                    pDir.mkdirs();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FileWriter writer = new FileWriter(pDir + "/eventLogData.txt", true);
                writer.write("\n" + timestamp + "\n<" + heading + ">\n" + text + "\n</" + heading + ">\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void DownloadedDataLog(String text)
    {
        if(isExternalStorageWritable()) {
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            //File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/DownloadedDataLog.txt");

            int f_size = 1024 * 1024 * 10;
            String f = logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger = Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(f + "/DownloadedDataLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO, text.trim());
                //System.out.println("file created   " + text);
            } catch (IOException e) {
                logger.warning("Failed to initialize logger handler.");
            } finally {
                if (fileHandler != null)
                    fileHandler.close();
            }
        }
    }

    public static void UploadLog(String text)
    {
        if(isExternalStorageWritable()) {
            //File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/UploadedDataLog.txt");
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");

            int f_size = 1024 * 1024 * 10;
            String f = logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger = Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(f + "/UploadedDataLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO, text.trim());
                //System.out.println("file created   " + text);
            } catch (IOException e) {
                logger.warning("Failed to initialize logger handler.");
            } finally {
                if (fileHandler != null)
                    fileHandler.close();
            }
        }
    }

    public static void ErrorLog(String text)
    {
        if(isExternalStorageWritable()) {
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            //File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/ErrorLog.txt");

            int f_size = 1024 * 1024 * 10;
            String f = logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger = Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(f + "/ErrorLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.SEVERE, text.trim());
                //System.out.println("file created   " + text);
            } catch (IOException e) {
                logger.warning("Failed to initialize logger handler.");
            } finally {
                if (fileHandler != null)
                    fileHandler.close();
            }
        }
    }

    public static void ReadingLog(String text)
    {
        if(isExternalStorageWritable()) {
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            //File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/ErrorLog.txt");

            int f_size = 1024 * 10;
            String f = logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger = Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(f + "/ReadingUploadLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO, text.trim());
                //System.out.println("file created   " + text);
            } catch (IOException e) {
                logger.warning("Failed to initialize logger handler.");
            } finally {
                if (fileHandler != null)
                    fileHandler.close();
            }
        }
    }

    public static void ScannedCodeLog(String text)
    {
        if(isExternalStorageWritable()) {
            File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            //File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/ErrorLog.txt");

            int f_size = 1024 * 10;
            String f = logFile.getAbsolutePath();
            //System.out.println("file path   " + f);
            Logger logger = Logger.getLogger(CommonFunctions.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler(f + "/ScannedCodeLog.txt.%g", f_size, 2, true);
                fileHandler.setFormatter(new YLogFormatter());
                logger.addHandler(fileHandler);
                logger.log(Level.INFO, text.trim());
                //System.out.println("file created   " + text);
            } catch (IOException e) {
                logger.warning("Failed to initialize logger handler.");
            } finally {
                if (fileHandler != null)
                    fileHandler.close();
            }
        }
    }

    public static void manualSyncReadingLog(String heading,String text,String timestamp)
    {
        if(isExternalStorageWritable()) {
            File pDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            try {
                if (!pDir.exists()) {
                    pDir.mkdirs();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FileWriter writer = new FileWriter(pDir + "/readingdata.txt", true);
                writer.write("\n" + timestamp + "\n<" + heading + ">\n" + text + "\n</" + heading + ">\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeContacts(String text)
    {
        if(isExternalStorageWritable()) {
            File pDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            try {
                if (!pDir.exists()) {
                    pDir.mkdirs();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FileWriter writer = new FileWriter(pDir + "/contacts.csv", true);
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write("\n" + text + "\n");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*public static void setServerName(String serverName)
    {
        if (serverName.equalsIgnoreCase("You")) {
            Constants.BASE_URL = Constants.Y_SERVER_BASE_URL;
            Constants.IMAGE_BASE_URL = Constants.Y_SERVER_IMAGE_BASE_URL;
        } else if (serverName.equalsIgnoreCase("Intelliwiz")) {
            Constants.BASE_URL = Constants.I_SERVER_BASE_URL;
            Constants.IMAGE_BASE_URL = Constants.I_SERVER_IMAGE_BASE_URL;
        } else if (serverName.equalsIgnoreCase("Local")) {
            Constants.BASE_URL = Constants.L_SERVER_BASE_URL;
            Constants.IMAGE_BASE_URL = Constants.L_SERVER_IMAGE_BASE_URL;
        }
    }*/

    public static void setServerNameFromResponse(Context context,String clientUrl)
    {
        SharedPreferences applicationMainPref=context.getSharedPreferences(Constants.APPLICATION_MAIN_PREF,Context.MODE_PRIVATE);
        applicationMainPref.edit().putString(Constants.APPLICATION_DATA_SERVER_URL,clientUrl+"service/").apply();
        applicationMainPref.edit().putString(Constants.APPLICATION_IMAGE_SERVER_URL,clientUrl).apply();
        Constants.BASE_URL = clientUrl+"service/";
        Constants.IMAGE_BASE_URL = clientUrl;
    }

    public static void writeDomainName(String text)
    {
        if(isExternalStorageWritable()) {
            File pDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            try {
                if (!pDir.exists()) {
                    pDir.mkdirs();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try
            {
                File f = new File(pDir+"/ServerSetting.ysn");
                if(f.isFile() && f.exists())
                    f.delete();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                FileWriter writer = new FileWriter(pDir + "/ServerSetting.ysn", true);
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write(text);
                System.out.println("EncryptFile: "+text);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readDomainName()
    {
        StringBuffer stringBuffer=new StringBuffer();
        if(isExternalStorageWritable())
        {
            File pDir=new File(Environment.getExternalStorageDirectory().getPath()+"/"+Constants.FOLDER_NAME+"/ServerSetting.ysn");
            try(FileReader fileReader = new FileReader(pDir.getAbsoluteFile())) {
                int ch = fileReader.read();
                while(ch != -1) {
                    System.out.print((char)ch);
                    stringBuffer.append((char)ch);
                    ch = fileReader.read();
                }
            } catch (FileNotFoundException e) {
                // exception handling
            } catch (IOException e) {
                // exception handling
            }
        }
        return stringBuffer.toString();
    }

    public static void writeCallLog(String text)
    {
        if(isExternalStorageWritable()) {
            File pDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constants.FOLDER_NAME + "/");
            try {
                if (!pDir.exists()) {
                    pDir.mkdirs();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FileWriter writer = new FileWriter(pDir + "/callLog.txt", true);
                writer.write("\n" + text + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean isPermissionGranted(Context context)
    {
        if( ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE)==PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public static String getFormatedDate(String date) {

        d = Long.parseLong(date);//.trim().replace("/", "").replace(")", "").substring(5));
        Date dd = new Date(d);
        //Date currDate = Calendar.getInstance().getTime();
        format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        dueDate = format.format(dd);
        return dueDate;
    }

    public static String getFormatedDate(long date) {
        d = (date);
        Date dd = new Date(d);
        format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        dueDate = format.format(dd);
        return dueDate;
    }

    public static String getCurrentMonth(long date) {
        d = (date);
        Date dd = new Date(d);
        format = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        dueDate = format.format(dd);
        return dueDate;
    }

    public static String getCurrentMonthName(long date) {
        d = (date);
        Date dd = new Date(d);
        format = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        dueDate = format.format(dd);
        return dueDate;
    }

    public static String getFromToDate(int val)
    {
        Date dd1=new Date(System.currentTimeMillis());
        Calendar cal1 = Calendar.getInstance(); // locale-specific
        cal1.setTime(dd1);
        if(val==0) {
            cal1.set(Calendar.HOUR_OF_DAY, 00);
            cal1.set(Calendar.MINUTE, 00);
            cal1.set(Calendar.SECOND, 00);
            cal1.set(Calendar.MILLISECOND, 0);
        }
        else
        {
            cal1.set(Calendar.HOUR_OF_DAY, 23);
            cal1.set(Calendar.MINUTE, 59);
            cal1.set(Calendar.SECOND, 59);
            cal1.set(Calendar.MILLISECOND, 0);
        }
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(new Date(cal1.getTimeInMillis()));
    }

    public static String getFromToDate1()
    {
        Date dd1=new Date(System.currentTimeMillis());
        Calendar cal1 = Calendar.getInstance(); // locale-specific
        cal1.setTime(dd1);
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return format.format(new Date(cal1.getTimeInMillis()));
    }

    public static int isInBetweenDate(long prevDate, long nextDate)
    {
        Date prev=new Date(prevDate);
        Date nxt=new Date(nextDate);
        Date curr=new Date();
        int retVal=-1;

        System.out.println("PrevDate: "+CommonFunctions.getFormatedDate(prevDate));
        System.out.println("NextDate: "+CommonFunctions.getFormatedDate(nextDate));
        System.out.println("past task: "+curr.after(nxt));
        System.out.println("inbetween: "+(curr.after(prev) && curr.before(nxt)));
        System.out.println("futuer task: "+curr.before(prev));

        if(curr.before(prev))
            retVal= 0;//future task
        else if((curr.after(prev) && curr.before(nxt)))
            retVal= 1;//can perform
        else if(curr.after(nxt))
            retVal= 2;//expired task

        return retVal;
        //return curr.after(prev) && curr.before(nxt);

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getFileNameFromDate(long timestamp) {
        d = timestamp;
        Date dd = new Date(d);
        Date currDate = Calendar.getInstance().getTime();
        SimpleDateFormat format;
        format = new SimpleDateFormat("dd_MMM_yyyy_HHmmss", Locale.ENGLISH);
        dueDate = format.format(dd);
        return dueDate;
    }

    public static String getFolderNameFromDate(long timestamp) {
        d = timestamp;
        Date dd = new Date(d);
        //Date currDate = Calendar.getInstance().getTime();
        SimpleDateFormat format;
        format = new SimpleDateFormat("yyyy/MMM/", Locale.ENGLISH);
        dueDate = format.format(dd);
        System.out.println("getFolderNameFromDate: "+dueDate.toLowerCase());
        return dueDate.toLowerCase(Locale.ENGLISH);
    }

    public static String getFormatedDateWithoutTime(long date) {
        //d = Long.parseLong(date);//.trim().replace("/", "").replace(")", "").substring(5));
        Date dd = new Date(date);
        Date currDate = Calendar.getInstance().getTime();
        SimpleDateFormat format;
        format = new SimpleDateFormat("EEEE , dd MMMM yyyy", Locale.ENGLISH);
        dueDate = format.format(dd);
        return dueDate;
    }

    public static long getParseDate(String date) {
        /*long d = Long.parseLong(date.trim());
        Date dd = new Date(d);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        return c.getTimeInMillis();*/
        //1970-01-01 00:00:00+00:00
        try {
            SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            Date lFromDate1 = datetimeFormatter1.parse(date);
            //System.out.println("gpsdate :" + lFromDate1);
            Timestamp fromTS1 = new Timestamp(lFromDate1.getTime());
            return fromTS1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static long getParse24HrsDate(String date) {
        /*long d = Long.parseLong(date.trim());
        Date dd = new Date(d);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        return c.getTimeInMillis();*/
        //1970-01-01 00:00:00+00:00
        try {
            SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date lFromDate1 = datetimeFormatter1.parse(date);
            //System.out.println("gpsdate :" + lFromDate1);
            Timestamp fromTS1 = new Timestamp(lFromDate1.getTime());
            return fromTS1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static boolean isLong(String val)
    {
        boolean isValidLong=false;
        try {
            Long.parseLong(val);
            return isValidLong;
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }
        return isValidLong;
    }

    public static String getParseDate(long timestamp)
    {
        DateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return df.format(timestamp);
    }

    public static void deleteExistsFile(String fileName)
    {
        File f = new File(fileName);
        if(f.exists())
            f.delete();
    }

    public static boolean checkFileExists(String fileName)
    {
        boolean retVal=false;
        File f = new File(fileName);
        if(f.exists())
        {
            System.out.println("File Found"+fileName);
            retVal=true;
        }
        else
        {
            System.out.println("File Not Found"+fileName);
            retVal=false;
        }

        return retVal;
    }


    public static String getCaptchaConfigQuery(Context context)
    {
        SharedPreferences loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return DatabaseQuries.GET_CAPTCHA_CONFIG+"("+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+","+
                                    loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
    }

    public static String getSOSLog(Context context)
    {
        SharedPreferences loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        return DatabaseQuries.get_soslog+"("+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+","+
                loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
    }

    public static String getQuery(int val, Context context)
    {
        String date="1970-01-01 00:00:00";
        SharedPreferences loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences syncPref=context.getSharedPreferences(Constants.SYNC_PREF, Context.MODE_PRIVATE);
        SharedPreferences syncOffsetPref=context.getSharedPreferences(Constants.SYNC_OFFSET_PREF, Context.MODE_PRIVATE);
        /*String enteredSiteCode=loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,"");
        String enteredUserID=loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,"");
        String enteredUserPass=loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,"");*/
        String syncDate=getTimezoneDate(syncPref.getLong(Constants.SYNC_TIMESTAMP,0));
        System.out.println("Syncdate::::"+syncDate);
        String syncTicketdate=getTimezoneDate(syncOffsetPref.getLong(Constants.SYNC_TICKET_TIMESTAMP,0));

        switch(val)
        {
            case 0:
                return DatabaseQuries.GET_ASSETS+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+")";
            case 1:
                return DatabaseQuries.GET_JOBNEED+"('"+syncDate+"','"+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+"','"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+"')";
            case 2:
                return DatabaseQuries.GET_JOBNEEDDETAILS+"('"+date+"','"+DatabaseQuries.JOBNEEDIDS+"')";
            case 3:
                return DatabaseQuries.GET_TYPEASSIST+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+")";
            case 4:
                return DatabaseQuries.GET_GEOFRENCE+"("+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+","+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+")";
            case 5:
                return DatabaseQuries.GET_PEOPLE+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+","+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+")";
            case 6:
                return DatabaseQuries.GET_GROUP+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+")";
            case 7:
                return DatabaseQuries.GET_PEOPLEATTENDANCE+"()";
            case 8:
                return DatabaseQuries.GET_QUESTIONS+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+")";
            case 9:
                return DatabaseQuries.GET_QUESTIONSET+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+","+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+")";
            case 10:
                return DatabaseQuries.GET_QUESTIONSETBELONGING+"('"+date+"',"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+","+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+")";
            case 11:
                return DatabaseQuries.GET_PEOPLEGROUPBELONGING+"('"+date+"',"+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+","+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+")";
            case 12:
                return DatabaseQuries.GET_SITE+"("+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+" , "+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
            case 13:
                return DatabaseQuries.GET_TICKET+"('"+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+"','"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+"','"+syncOffsetPref.getInt(Constants.SYNC_TICKET_OFFSET,0)+"','"+syncTicketdate+"')";
            case 14:
                return DatabaseQuries.GET_ASSET_ADDRESS+"('"+syncDate+"','"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+"')";
            case 15:
                return DatabaseQuries.GET_OTHER_SITE+"("+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+" , "+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
            case 16:
                return DatabaseQuries.GET_SITE_INFO+"("+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+" , "+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
            case 17:
                return DatabaseQuries.GET_TEMPLATES+"("+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+" , "+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
            case 18:
                return DatabaseQuries.GET_ASSIGNED_SITE_PEOPLE+"("+loginPref.getLong(Constants.LOGIN_USER_CLIENT_ID,-1)+" , "+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";
            case 19://(peopleid, buid)
                return DatabaseQuries.GET_AUTO_CLOSED_TICKET+"("+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+" , "+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+")";
            case 20:
                return DatabaseQuries.GET_TICKET_MODIFIEDAFTER+"('"+syncTicketdate+"','"+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+"','"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+"')";
                default:
                return DatabaseQuries.GET_ASSETS+"('"+syncDate+"',"+loginPref.getLong(Constants.LOGIN_SITE_ID,-1)+")";
        }
    }
    //return DatabaseQuries.GET_SITE_LAST_CHECKINOUT+"("+loginPref.getLong(Constants.LOGIN_PEOPLE_ID,-1)+")";

    public static boolean isValidEmergencyInfo(String key, String value)
    {
        switch(key)
        {
            case Constants.SETTING_GENERAL_LOGIN_USER_NAME:
                return true;
            case Constants.SETTING_GENERAL_LOGIN_USER_SITE:
                return true;
            case Constants.SETTING_GENERAL_LOGIN_USER_IMEI:
                return true;
            case Constants.SETTING_GENERAL_CONTACT_NUMBER:
                if(value.length()>=10)
                    return true;
                else
                    return false;
            case Constants.SETTING_GENERAL_CONTACT_EMAILID:
                return true;
                /*if(emailValidator(value))
                    return true;
                else
                    return false;*/
            case Constants.SETTING_GENERAL_CONTACT_HELP_MESSAGE:
                return true;
            case Constants.SETTING_ORIGIN_POINT:
                return true;
            default:
                return false;
        }
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        //"^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count, int menuId) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(menuId);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(menuId, badge);
    }

    public static boolean isGPSStateOn(Context context)
    {
        LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE );
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) != true){
            LD = 1;
        }
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isWIFIStateOn(Context context)
    {
        System.out.println("called====wifi");

        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int dataState = tel.getDataState();


        System.out.println("wifi==="+wifi.isConnected()+"--"+ dataState);


        return wifi.isConnected();

    }


    public static boolean isMOBILEDATAStateOn(Context context)
    {


        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int dataState = tel.getDataState();

        System.out.println("network status::"+mobile.isConnected() +"--"+dataState);

        if(dataState == 2){
            return true;
        }

        else {
            return false;
        }

        //return mobile.isConnected();
    }

    public static boolean isAutoDateTimeEnable(Context context)
    {
        boolean retVal=false;
        try {
            if(Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME) == 1)
            {
                System.out.println("Enable");
                retVal= true;
            }
            else
            {
                System.out.println("Disable");
                retVal= false;
            }
        } catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
            retVal=false;
        }
        return retVal;
    }

    public static boolean isNeedToCheckGPSAndDATE(String clientCode)
    {
        return clientCode.equalsIgnoreCase("SUKHI");
    }

    public static int isAllowToAccessModules(Context context,String clientCode, boolean chkGPS, boolean chkDateTime)
    {
        int val=0;
        System.out.println("-----check ld"+ LD);
        System.out.println("-----check ld after"+ LD);

        System.out.println(CommonFunctions.isWIFIStateOn(context) || CommonFunctions.isMOBILEDATAStateOn(context));
        if(chkGPS && chkDateTime)
        {
            if(CommonFunctions.isAutoDateTimeEnable(context) && CommonFunctions.isGPSStateOn(context)  && LD == 0 && CommonFunctions.isWIFIStateOn(context) && CommonFunctions.isMOBILEDATAStateOn(context))
                val=0;
            else
            {
                if(!CommonFunctions.isAutoDateTimeEnable(context))
                    val=1;
                else if(!CommonFunctions.isGPSStateOn(context))
                    val=2;
                else if(!CommonFunctions.isWIFIStateOn(context) && !CommonFunctions.isMOBILEDATAStateOn(context)) {
                    System.out.println("==wifi"+ !CommonFunctions.isWIFIStateOn(context) +"==data" + !CommonFunctions.isMOBILEDATAStateOn(context));
                    val = 3;
                }
               /* else if(!CommonFunctions.isMOBILEDATAStateOn(context)) {
                    System.out.println("check mobile data====");
                    val = 4;
                }*/
                /*else if(LD == 1){
                    val=5;
                    LD = 0;
                }*/

            }
        }
        else if(!chkGPS && chkDateTime)
        {
            if(!CommonFunctions.isAutoDateTimeEnable(context))
                val=1;
        }
        else if(chkGPS && !chkDateTime)
        {
            if(!CommonFunctions.isGPSStateOn(context))
                val=2;
        }



        /*if(isNeedToCheckGPSAndDATE(clientCode))
        {
            if(CommonFunctions.isAutoDateTimeEnable(context) && CommonFunctions.isGPSStateOn(context))
                val=0;
            else
            {
                if(!CommonFunctions.isAutoDateTimeEnable(context))
                    val=1;
                else if(!CommonFunctions.isGPSStateOn(context))
                    val=2;
            }
        }
        else
        {
            if(!CommonFunctions.isAutoDateTimeEnable(context))
                val=1;
        }*/

        System.out.println("ld==="+ LD);

        return val;
    }

    public static List<Address> getAddress(Context context, String lat, String lon)
    {
        List<Address> addresses=null;
        Geocoder geocoder;
        geocoder = new Geocoder(context, Locale.getDefault());

        /*addresses= CommonFunctions.getAddress(ConveyanceActivity.this, deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,""), deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,""));
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        //C-1,603, Kolshet Rd, Dokali Pada, Dhokali, Thane West, Thane, Maharashtra 400607, India ~ Thane ~ Maharashtra ~ India ~ 400607
        System.out.println("Address: "+address+" ~ "+city+" ~ "+state+" ~ "+country+" ~ "+postalCode);*/

        //19.229513,72.9837882
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(lat), Double.valueOf(lon), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public static String getTimezoneDate(long time)
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(d);
    }

    public static String getParseDatabaseDateFormat(String dbDate)
    {
        if(dbDate!=null && !dbDate.equalsIgnoreCase("None"))
            return CommonFunctions.getDeviceTimezoneFormatDate(dbDate);
        else
            return dbDate;
    }

    public static String getParseDatabaseDateFormat1(String dbDate)
    {
        if(dbDate!=null && !dbDate.equalsIgnoreCase("None"))
            return CommonFunctions.getDeviceTimezoneFormatDate1(dbDate);
        else
            return dbDate;
    }

    public static String getDeviceTimezoneFormatDate(String dbDate)
    {

        /*String formattedDate=null;
        try {
            String dateStr = dbDate;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(dateStr);
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;*/

        String formattedDate=null;
        try {
            String dateStr = dbDate;
            Calendar current = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(dbDate);
                df.setTimeZone(TimeZone.getDefault());
                current.setTime(date);
                formattedDate = df.format(current.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;


        /*Calendar current = Calendar.getInstance();
        System.out.println("Current Time: " + current.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dbDate);
            current.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long  miliSeconds = current.getTimeInMillis();

        TimeZone tzCurrent = current.getTimeZone();
        int offset = tzCurrent.getRawOffset();
        if (tzCurrent.inDaylightTime(new Date())) {
            offset = offset + tzCurrent.getDSTSavings();
        }

        miliSeconds = miliSeconds - offset;

        Date resultdate = new Date(miliSeconds);

        TimeZone timezone = TimeZone.getDefault();//TimeZone.getTimeZone("Asia/Kolkata");
        String TimeZoneName = timezone.getDisplayName();

        System.out.println("DeviceTimezone Name: "+TimeZone.getDefault().getDisplayName());

        int TimeZoneOffset = timezone.getRawOffset()
                / (60 * 1000);

        int hrs = TimeZoneOffset / 60;
        int mins = TimeZoneOffset % 60;

        miliSeconds = miliSeconds + timezone.getRawOffset();

        resultdate = new Date(miliSeconds);
        System.out.println(format.format(resultdate));

        System.out.println(TimeZoneName + " : GMT " + hrs + "."+ mins);
        //System.out.println("" + sdf.format(resultdate));
        miliSeconds = 0;
        return format.format(resultdate);*/
    }

    public static String getDeviceTimezoneFormatDate1(String dbDate)
    {
        String formattedDate=null;
        try {
            String dateStr = dbDate;
            Calendar current = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
            try {
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = df.parse(dbDate);
                df.setTimeZone(TimeZone.getDefault());
                current.setTime(date);
                formattedDate = df.format(current.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;

    }

    public static String getApplicationVersion(Context context)
    {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    public static int getAndroidVersion(Context context)
    {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getOSVerName(int verNum)
    {
        String verName=null;
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT)
            {
                verName=Build.VERSION.RELEASE+" ("+fieldName+") SDK ("+verNum+")";
            }
        }
        return verName;
    }

    public static String getInstalledAppList(Context context)
    {
        StringBuffer stringBuff=new StringBuffer();
        PackageManager pkMgr=context.getPackageManager();
        List<PackageInfo> packageList = pkMgr.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for(int i=0;i<packageList.size();i++)
        {
            if(i>0)
                stringBuff.append("|");

            PackageInfo pkInfo = packageList.get(i);
			/*Log.d("About us", "Name: "+pkMgr.getApplicationLabel(pkInfo.applicationInfo).toString());
			Log.d("About us", "Date: "+Util.getFormatedDate(pkInfo.firstInstallTime));*/

            stringBuff.append(pkMgr.getApplicationLabel(pkInfo.applicationInfo).toString()+"("+(getTimezoneDate(pkInfo.firstInstallTime))+")");

        }
        System.out.println("App List: "+stringBuff.toString());
        return stringBuff.toString();
    }

    public static String getDeviceInformation()
    {
        System.out.println("Constants.Build_MANUFACTURER, "+Build.MANUFACTURER);
        System.out.println("Constants.Build_MODEL, "+ Build.MODEL);
        System.out.println("Constants.Build_VERSION_CODENAME, "+Build.VERSION.CODENAME);
        System.out.println("Constants.Build_VERSION_SDK_INT, "+ Build.VERSION.SDK_INT);
        System.out.println("Constants.Build_VERSION_RELEASE, "+Build.VERSION.RELEASE);

        return Build.MANUFACTURER+"_"+Build.MODEL;
    }

    public static String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        /*if(am!=null)
        {

        }*/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        System.out.println("isInBackground: -------------------------------------------------------------------------"+isInBackground);
        return isInBackground;
    }

    public static String getLocationFromAddress(Context context,String mAddress)
    {
        String geoLocation=null;

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(mAddress,1);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            //geoLocation=((double) (location.getLatitude() * 1E6)+","+(double) (location.getLongitude() * 1E6));
            geoLocation=((location.getLatitude())+","+(location.getLongitude()));

            return geoLocation;
        }
        catch (Exception e)
        {

        }
        return  geoLocation;
    }

    /*public static String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        return inputString;
    }*/

    public static String escapeMetaCharacters(String inputString){

        inputString=inputString.replace("'","''");
        return inputString;
    }

    public static String getDMSFormatLocation(double latitude, double longitude)
    {
        int latSeconds = (int) Math.round(latitude * 3600);
        int latDegrees = latSeconds / 3600;
        latSeconds = Math.abs(latSeconds % 3600);
        int latMinutes = latSeconds / 60;
        latSeconds =latSeconds % 60;

        int longSeconds = (int) Math.round(longitude * 3600);
        int longDegrees = longSeconds / 3600;
        longSeconds = Math.abs(longSeconds % 3600);
        int longMinutes = longSeconds / 60;
        longSeconds = latSeconds % 60;
        String latDegree = latDegrees >= 0 ? "N" : "S";
        String lonDegrees = longDegrees >= 0 ? "E" : "W";

        return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                + "\"" + latDegree +" "+ Math.abs(longDegrees) + "°" + longMinutes
                + "'" + longSeconds + "\"" + lonDegrees;
    }

    public static boolean isSimAvailable(Context context) {
        boolean isAvailable = false;
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT: //SimState = “No Sim Found!”;
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = “Network Locked!”;
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = “PIN Required to access SIM!”;
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = “PUK Required to access SIM!”; // Personal Unblocking Code
                break;
            case TelephonyManager.SIM_STATE_READY:
                isAvailable = true;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = “Unknown SIM State!”;
                break;
        }
        return isAvailable;
    }


    public static boolean checkNFCSupported(Context context)
    {
        NfcManager manager = (NfcManager)context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            return true;
        }else{
            return false;
        }
    }

    public static String getDateDifference(long milliseconds1, long milliseconds2)
    {
        long milliseconds = milliseconds1 - milliseconds2;
        int seconds = (int) milliseconds / 1000;

        // calculate hours minutes and seconds
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;

        /*System.out.println("Step Difference: ");
        System.out.println("Step Hours: " + hours);
        System.out.println("Step Minutes: " + minutes);
        System.out.println("Step Seconds: " + seconds);*/
        String retVal=hours+" Hr: "+minutes+" Min :"+seconds+" Sec";

        return retVal;
    }

    public static int getDateDifferenceInMin(long milliseconds1, long milliseconds2)
    {
        long milliseconds = milliseconds1 - milliseconds2;
        int seconds = (int) milliseconds / 1000;

        // calculate hours minutes and seconds
        //int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        //seconds = (seconds % 3600) % 60;

        /*System.out.println("Step Difference: ");
        System.out.println("Step Hours: " + hours);
        System.out.println("Step Minutes: " + minutes);
        System.out.println("Step Seconds: " + seconds);*/
        //String retVal=hours+" Hr: "+minutes+" Min :"+seconds+" Sec";

        return minutes;
    }
    public static int getDateDifferenceInSec(long milliseconds1, long milliseconds2)
    {
        long milliseconds = milliseconds1 - milliseconds2;
        int seconds = (int) milliseconds / 1000;

        // calculate hours minutes and seconds
        //int hours = seconds / 3600;
        //int minutes = (seconds % 3600) / 60;
        //seconds = (seconds % 3600) % 60;

        /*System.out.println("Step Difference: ");
        System.out.println("Step Hours: " + hours);
        System.out.println("Step Minutes: " + minutes);
        System.out.println("Step Seconds: " + seconds);*/
        //String retVal=hours+" Hr: "+minutes+" Min :"+seconds+" Sec";

        return seconds;
    }

    public static boolean isTimeLiesInBetween(String startTime, String endTime)
    {
        System.out.println("start"+ startTime);
        System.out.println("end"+ endTime);
        boolean retVal=false;

        if (startTime == null && endTime == null){
            retVal= true;
            System.out.println("retVal"+ retVal);
        }
        else {
            try {
                Date time1 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(startTime);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(time1);
                calendar1.add(Calendar.DATE, 1);


                Date time2 = new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(endTime);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(time2);
                calendar2.add(Calendar.DATE, 1);

                Calendar calendar3 = Calendar.getInstance();

                Date x = calendar3.getTime();
                if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                    retVal = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();

            System.out.println("Cache Directory: "+dir.getAbsolutePath());
            deleteDir(dir);

            /*File cache = context.getCacheDir();
            File appDir = new File(cache.getParent());
            if(appDir.exists()){
                String[] children = appDir.list();
                for(String s : children){
                    if(!s.equals("lib")){
                        deleteDir(new File(appDir, s));
                    }
                }
            }*/

        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void showSnack(Context context,boolean isConnected,View view) {
        String message;
        int color;
        int bColor;
        if (isConnected) {
            message = context.getResources().getString(R.string.internet_connection_available);
            color = Color.WHITE;
            bColor=Color.parseColor("#01b140");
        } else {
            message = context.getResources().getString(R.string.no_internet_connection);
            color = Color.parseColor("#ec563b");
            bColor=Color.parseColor("#ec563b");
        }

        /*Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        //sbView.setBackgroundColor(bColor);
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);

        snackbar.show();*/

        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(bColor);
        snackbar.show();
    }

}

