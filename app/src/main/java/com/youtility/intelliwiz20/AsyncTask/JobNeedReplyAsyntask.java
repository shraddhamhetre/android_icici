package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadJobNeedReplyDataListener;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.DatabaseQuries;
import com.youtility.intelliwiz20.Utils.ServerRequest;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

/**
 * Created by PrashantD on 12/10/17.
 *
 * not in used
 */

public class JobNeedReplyAsyntask extends AsyncTask<Void, Integer, Integer> {

    private Context context;
    private IUploadJobNeedReplyDataListener iUploadJobNeedReplyDataListener;
    private Attachment attachment;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private Gson gson;
    private SharedPreferences loginPref;
    private AttachmentDAO attachmentDAO;
    private ArrayList<Attachment> jobNeedReplyAttachmentArrayList;

    public JobNeedReplyAsyntask(Context context, IUploadJobNeedReplyDataListener iUploadJobNeedReplyDataListener)
    {
        this.context=context;
        this.iUploadJobNeedReplyDataListener = iUploadJobNeedReplyDataListener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        attachmentDAO=new AttachmentDAO(context);

    }


    @Override
    protected Integer doInBackground(Void... voids) {
        int status=-1;

        jobNeedReplyAttachmentArrayList=new ArrayList<>();
        jobNeedReplyAttachmentArrayList=attachmentDAO.getUnsyncJobNeedReplyAttachments();
        if(jobNeedReplyAttachmentArrayList!=null && jobNeedReplyAttachmentArrayList.size()>0)
        {
            for(int i=0;i<jobNeedReplyAttachmentArrayList.size();i++)
            {
                attachment=new Attachment();
                attachment=jobNeedReplyAttachmentArrayList.get(i);

                try {
                    String insertAttachment= DatabaseQuries.ATTACHMENT_INSERT+"( null, null,'"+attachment.getNarration()+"','"+attachment.getGpslocation()+"','"+attachment.getDatetime()+"',"+
                            attachment.getOwnername()+","+attachment.getOwnerid()+","+attachment.getAttachmentType()+","+attachment.getCuser()+","+
                            attachment.getMuser()+",'"+attachment.getCdtz()+"','"+attachment.getMdtz()+"',"+attachment.getBuid()+") returning attachmentid;";

                    System.out.println("Insert Reply: "+insertAttachment);

                    ServerRequest serverRequest=new ServerRequest(context);
                    HttpResponse response=serverRequest.getReplyLogResponse(insertAttachment,
                            String.valueOf(loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0)),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                    //System.out.println("ReplyUploadAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                    if(response!=null && response.getStatusLine().getStatusCode()==200)
                    {
                        is = response.getEntity().getContent();

                        sb = new StringBuffer();
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
                        System.out.println("SB ReplyUploadAsyntask: " + sb.toString());
                        response.getEntity().consumeContent();

                        CommonFunctions.ResponseLog("\n AutoSync Reply Event Log Response \n"+attachment.getAttachmentid()+"\n"+sb.toString()+"\n");

                        JSONObject ob = new JSONObject(sb.toString());
                        if(ob.getInt(Constants.RESPONSE_RC)==0)
                        {
                            status=0;
                            attachmentDAO.changeJNReplySycnStatus(attachment.getAttachmentid(),attachment.getAttachmentType());
                        }
                        else
                        {
                            CommonFunctions.ErrorLog("\nAutoSync Reply Error: \n"+attachment.getAttachmentid()+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n");
                            status=-1;
                            break;
                        }

                    }
                    else {
                        CommonFunctions.ErrorLog("\n AutoSync Reply Error: \n"+attachment.getAttachmentid()+"\n"+"Connection not established with server."+"\n");
                        System.out.println("SB1 ReplyUploadAsyntask: ERROR ");
                        status=-1;
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
                }
            }
        }
        else
            status=0;




        return status;
    }

    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(status);

        iUploadJobNeedReplyDataListener.finishJobNeedReplyUpload(status);
    }

}
