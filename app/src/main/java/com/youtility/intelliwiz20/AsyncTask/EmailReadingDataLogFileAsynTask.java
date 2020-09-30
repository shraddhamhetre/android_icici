package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.Zip_UnZip_Files;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import es.dmoral.toasty.Toasty;

/**
 * Created by youtility on 24/9/18.
 */

public class EmailReadingDataLogFileAsynTask extends AsyncTask {

    private Zip_UnZip_Files zipUnZip;
    private Context context;
    String configEmailId;
    private SharedPreferences loginPref;
    private SharedPreferences syncSummaryPref;

    public EmailReadingDataLogFileAsynTask(Context context, String configEmailId)
    {
        this.context=context;
        this.configEmailId=configEmailId;
        zipUnZip=new Zip_UnZip_Files(context);
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        syncSummaryPref=context.getSharedPreferences(Constants.SYNC_SUMMARY_PREF, Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    @Override
    protected Object doInBackground(Object[] params) {
        if(configEmailId!=null && configEmailId.trim().length()>0 && !configEmailId.equalsIgnoreCase("None"))
        {
            if(checkGoogleConnection())
            {
                try {
                    //lets now create and send the mail
                    /*final String username = "support@youtility.in";
                    final String password = "supportanjali2017";*/

                    final String username = "AKIA4QJPZVEWENJJUF7R";
                    final String password = "BKIkrUK5C1tx2mj/lViWVHKyjoTqfsbO3bEc+IE3Z1Jg";

                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "email-smtp.us-east-1.amazonaws.com");
                    props.put("mail.smtp.port", "587");

                    Session session = Session.getInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(username, password);
                                }
                            });

                    MimeMessage message = new MimeMessage(session);
                    message.addHeader("Content-Type", "text/html; charset=utf-8");


                    String recipient = configEmailId;
                    if(configEmailId.contains(","))
                    {
                        String[] recipientList = recipient.split(",");
                        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
                        int counter = 0;
                        for (String recipients : recipientList) {
                            recipientAddress[counter] = new InternetAddress(recipients.trim());
                            counter++;
                        }
                        message.setRecipients(Message.RecipientType.TO, recipientAddress);
                    }
                    else
                    {
                        //message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress("shraddha.mhetre@youtility.in "));
                        message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress(configEmailId));
                    }


                    Log.d("EmailDataFilesAsyncTask"," got message recipients as "+ InternetAddress.toString(message.getAllRecipients()));

                    message.setFrom(new InternetAddress("support@youtility.in"));
                    message.setSubject("Intelliwiz2.0 reading data zip file. "+loginPref.getString(Constants.LOGIN_PEOPLE_CODE,"")+" - "+loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE,"")+" - "+loginPref.getString(Constants.LOGIN_SITE_CODE,"")+" - "+ CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    Log.d("EmailDataFilesAsyncTask"," got message subject as "+message.getSubject());

                    Multipart multipart = new MimeMultipart();

                    MimeBodyPart b = new MimeBodyPart();
                    b.setDisposition(MimeBodyPart.INLINE);
                    b.setText("Please find Reading data files attached.");
                    //ByteArrayDataSource ds1 = new ByteArrayDataSource(mailbody.getBytes(Charset.forName("utf-8")), "text/html; charset=utf-8");
                    //b.setDataHandler(new DataHandler(ds1));
                    multipart.addBodyPart(b);

                    MimeBodyPart attachment = new MimeBodyPart();
                    attachment.setDisposition(Part.ATTACHMENT);
                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    File file[]=new File[]{new File(extStorageDirectory + "/"+ Constants.FOLDER_NAME+"/readingdata.txt"),new File(extStorageDirectory + "/"+ Constants.FOLDER_NAME+"/eventLogData.txt"),new File(extStorageDirectory + "/"+ Constants.FOLDER_NAME+"/intelliwiz20_DB_backup")};
                    File delFile=new File(extStorageDirectory + "/"+ Constants.FOLDER_NAME+"/readingdata.txt");
                    File delFile1=new File(extStorageDirectory + "/"+ Constants.FOLDER_NAME+"/eventLogData.txt");
                    File zipfile = new File(extStorageDirectory+"/"+Constants.FOLDER_NAME+"/uploading.zip");
                    System.out.println("file.length: "+file.length);

                    //if(zipfile.exists())zipfile.delete();
                    //if(zipfile.length()>0)
                    {
                        if(zipUnZip.createZip(zipfile, file))
                        {
                            Log.d("EmailDataFilesAsyncTask", " got zipfile length as "
                                    + zipfile.length());
                            FileDataSource ds = new FileDataSource(zipfile);
                            attachment.setDataHandler(new DataHandler(ds));
                            attachment.setFileName("intelliwiz_data.zip");
                            multipart.addBodyPart(attachment);
                            message.setContent(multipart);
                            Transport.send(message);
                            Log.d("EmailDataFilesAsyncTask", " message emailed");

                            syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_MAIL_SENT,"TRUE").apply();

                            if (zipfile.exists())
                                zipfile.delete();// delete the zip file on exit

                            if(delFile.exists())
                                delFile.delete();

                            if(delFile1.exists())
                                delFile1.delete();


                            zipfile = null;
                            file = null;
                            delFile =null;
                        }
                        else
                        {
                            syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_MAIL_SENT,"FALSE").apply();
                        }

                    }
                } catch (AddressException e) {
                    Log.d("EmailDataFilesAsyncTask"," Exception in sending data zip email"+e);
                    syncSummaryPref.edit().putString(Constants.SYNC_SUMMARY_MAIL_SENT,"FALSE").apply();
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(Object o) {

        super.onPostExecute(o);
        if(syncSummaryPref.getString(Constants.SYNC_SUMMARY_MAIL_SENT,"FALSE").equalsIgnoreCase("TRUE"))
            Toasty.info(context,"Thank you, Email has been sent.").show();
        else
            //Toasty.error(context,"Sorry!!, Problem in sending mail").show();
            System.out.println("Sorry!!, Problem in sending mail");
    }

    private boolean checkGoogleConnection() {
        boolean ret=false;
        String baseUrl = "https://google.com";
        try {
            URL url = new URL(baseUrl);
            ret=checkConnection(url);
        } catch (MalformedURLException e) {
            Log.e("EmailDataFilesAsyncTask","checkGoogleConnection() got Malformed URL baseUrl="+baseUrl,e);
            e.printStackTrace();
        }

        return ret;
    }

    private boolean checkConnection(URL url) {
        boolean ret=false;
        try {
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            System.setProperty("http.keepAlive", "false");
            urlc.setConnectTimeout(20*1000);//20 seconds timeout for connect attempt      // 10 s.
            urlc.setReadTimeout(20*1000);//20 seconds timeout for read attempt
            urlc.connect();
            int respcode=urlc.getResponseCode();
            Log.e("EmailDataFilesAsyncTask","checkConnection() got urlc.getResponseCode()="+respcode);
            if (respcode == 200 || respcode == 202) ret=true;
            else ret=false;
        } catch (IOException e) {
            Log.e("EmailDataFilesAsyncTask","checkConnection() got Exception for url="+url,e);
            e.printStackTrace();
        }
        return ret;
    }
}
