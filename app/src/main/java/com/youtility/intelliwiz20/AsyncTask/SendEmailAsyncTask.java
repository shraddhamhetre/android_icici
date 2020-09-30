package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by PrashantD on 16/1/18.
 *
 * send mail from contact us screen
 *
 */

public class SendEmailAsyncTask extends AsyncTask <Void, Integer, Void>
{
    private String mailBody=null;
    private String[] msg=null;
    private Context context;
    public SendEmailAsyncTask(Context context,String mailBody)
    {
        this.mailBody=mailBody;
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        msg = mailBody.split(",");
    }

    @Override
    protected Void doInBackground(Void... params) {
        if(checkGoogleConnection())
        {
            try
            {
                final String username = "support@youtility.in";
                final String password = "supportanjali2017";


                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                MimeMessage message = new MimeMessage(session);
                message.addHeader("Content-Type", "text/html; charset=utf-8");

                message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress("support@youtility.in"));

                //message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress("ashish.mhashilkar@youtility.in"));
                //message.setRecipient(Message.RecipientType.TO,  new javax.mail.internet.InternetAddress("prashant.daware@youtility.in"));

                message.setFrom(new InternetAddress("support@youtility.in"));
                message.setSubject("Intelliwiz 2.0 Application Feedback");

                Multipart multipart = new MimeMultipart();

                /*MimeBodyPart b = new MimeBodyPart();
                b.setDisposition(MimeBodyPart.INLINE);
                StringBuffer bodyBuff=new StringBuffer();
                bodyBuff.append("User Name: "+msg[1]+"\n");
                bodyBuff.append("Email Id: "+msg[2]+"\n");
                bodyBuff.append("User Contact Number: "+msg[3]+"\n");
                bodyBuff.append("User Comment: "+msg[0]);
                b.setText(bodyBuff.toString().trim());
                multipart.addBodyPart(b);
                message.setContent(multipart);
                Transport.send(message);*/

                /*MimeBodyPart b = new MimeBodyPart();
                b.setDisposition(MimeBodyPart.INLINE);
                StringBuffer bodyBuff=new StringBuffer();
                bodyBuff.append("<html><body><table><tr>");
                bodyBuff.append("<td>");
                bodyBuff.append("<b>User Name: </b>"+msg[1]+"<br>");
                bodyBuff.append("</td>");
                bodyBuff.append("<td>");
                bodyBuff.append("Email Id: "+msg[2]+"<br>");
                bodyBuff.append("</td>");
                bodyBuff.append("<td>");
                bodyBuff.append("User Contact Number: "+msg[3]+"<br>");
                bodyBuff.append("</td>");
                bodyBuff.append("<td>");
                bodyBuff.append("User Comment: "+msg[0]);
                bodyBuff.append("</td>");
                bodyBuff.append("</tr></table></body></html>");

                Spanned ss=Html.fromHtml(bodyBuff.toString().trim());

                multipart.addBodyPart(b);*/


                String body="";

                String tdstyle= "style='background:#ABE7ED;font-weight:bold;font-size:14px;'";


                body+= "<table style='background:#EEF1F5;' cellpadding=8 cellspacing=2>";
                body+= "<tr> <td align='right' "+tdstyle+">User Name: </td><td>"+msg[1]+"</td></tr>";
                body+= "<tr> <td align='right' "+tdstyle+">Email Id: </td><td>"+msg[2]+"</td></tr>";
                body+= "<tr> <td align='right' "+tdstyle+">User Contact Number: </td><td>"+msg[3]+"</td></tr>";
                body+= "<tr> <td align='right' "+tdstyle+">User Comment: </td><td>"+msg[0]+"</td></tr>";
                body+="</table>";

                MimeBodyPart htmlTextPart = new MimeBodyPart();
                htmlTextPart.setContent(body, "text/html;charset=UTF-8");
                multipart.addBodyPart(htmlTextPart);

                message.setContent(multipart);
                Transport.send(message);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("No Internet connection.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private boolean checkGoogleConnection() {
        boolean ret=false;
        String baseUrl = "https://google.com";
        try {
            URL url = new URL(baseUrl);
            ret=checkConnection(url);
        } catch (MalformedURLException e) {
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
            if (respcode == 200 || respcode == 202) ret=true;
            else ret=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
