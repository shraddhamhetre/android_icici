package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadAdhocInsertDataListener;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.UploadJobneedParameter;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
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
 * Created by PrashantD on 23/11/17.
 *
 * not in used
 */

public class AdhocInsertAsynctask extends AsyncTask<Void , Integer, Integer> {
    private IUploadAdhocInsertDataListener iUploadAdhocInsertDataListener;
    private JobNeed jobNeed;
    private Context context;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private Gson gson;
    private SharedPreferences loginPref;
    private ArrayList<JobNeed>adhocJobArrayList;
    private ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
    private UploadJobneedParameter uploadJobneedParameter;

    private JobNeedDAO jobNeedDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private AttachmentDAO attachmentDAO;

    public AdhocInsertAsynctask(Context context, IUploadAdhocInsertDataListener iUploadAdhocInsertDataListener)
    {
        this.context=context;
        this.iUploadAdhocInsertDataListener=iUploadAdhocInsertDataListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jobNeedDAO=new JobNeedDAO(context);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(context);
        attachmentDAO=new AttachmentDAO(context);
        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
    }


    @Override
    protected Integer doInBackground(Void... voids) {
        int status=-1;
        adhocJobArrayList=new ArrayList<>();
        adhocJobArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"
                                                        +Constants.JOB_NEED_IDENTIFIER_TASK+"','"
                                                        +Constants.JOB_NEED_IDENTIFIER_TICKET+"','"
                                                        +Constants.JOB_NEED_IDENTIFIER_ASSET+"','"
                                                        +Constants.JOB_NEED_IDENTIFIER_ASSET_LOG+"','"
                                                        +Constants.JOB_NEED_IDENTIFIER_ASSET_AUDIT+"'",0);

        System.out.println("Thread: AdhocInsertAsynctask() current thread" + Thread.currentThread().getName());

        if(adhocJobArrayList!=null && adhocJobArrayList.size()>0)
        {
            System.out.println("AutoSync adhoc job size: "+adhocJobArrayList.size());
            for(int i=0;i<adhocJobArrayList.size();i++)
            {
                jobNeed=new JobNeed();
                jobNeed=adhocJobArrayList.get(i);

                try {
                    jobNeedDetailsArrayList=new ArrayList<>();
                    jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeed.getJobneedid());
                    uploadJobneedParameter=new UploadJobneedParameter();
                    uploadJobneedParameter.setJobdesc(jobNeed.getJobdesc());
                    uploadJobneedParameter.setAatop(jobNeed.getAatop());
                    uploadJobneedParameter.setAssetid(jobNeed.getAssetid());
                    uploadJobneedParameter.setCuser(jobNeed.getCuser());
                    uploadJobneedParameter.setFrequency(jobNeed.getFrequency());
                    uploadJobneedParameter.setPlandatetime((jobNeed.getPlandatetime()));
                    uploadJobneedParameter.setExpirydatetime((jobNeed.getExpirydatetime()));
                    uploadJobneedParameter.setGracetime(jobNeed.getGracetime());
                    uploadJobneedParameter.setGroupid(jobNeed.getGroupid());
                    uploadJobneedParameter.setIdentifier(jobNeed.getIdentifier());
                    uploadJobneedParameter.setJobid(jobNeed.getJobid());
                    uploadJobneedParameter.setJobneedid(jobNeed.getJobneedid());
                    uploadJobneedParameter.setJobstatus(jobNeed.getJobstatus());
                    uploadJobneedParameter.setJobtype(jobNeed.getJobtype());
                    uploadJobneedParameter.setMuser(jobNeed.getMuser());
                    uploadJobneedParameter.setParent(jobNeed.getParent());
                    uploadJobneedParameter.setPeopleid(jobNeed.getPeopleid());
                    uploadJobneedParameter.setPerformedby(jobNeed.getPerformedby());
                    uploadJobneedParameter.setPriority(jobNeed.getPriority());
                    uploadJobneedParameter.setScantype(jobNeed.getScantype());
                    uploadJobneedParameter.setQuestionsetid(jobNeed.getQuestionsetid());
                    uploadJobneedParameter.setDetails(jobNeedDetailsArrayList);
                    uploadJobneedParameter.setBuid(jobNeed.getBuid());
                    uploadJobneedParameter.setTicketcategory(jobNeed.getTicketcategory());
                    uploadJobneedParameter.setGpslocation(jobNeed.getGpslocation());

                    uploadJobneedParameter.setStarttime(jobNeed.getStarttime());
                    uploadJobneedParameter.setEndtime(jobNeed.getEndtime());

                    uploadJobneedParameter.setCtzoffset(jobNeed.getCtzoffset());

                    String ss=gson.toJson(uploadJobneedParameter);

                    ServerRequest serverRequest=new ServerRequest(context);
                    HttpResponse response=serverRequest.getAdhocLogResponse(ss.trim(),
                            loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                            loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                    //System.out.println("ADHOCLogAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                    if(response!=null && response.getStatusLine().getStatusCode()==200)
                    {
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
                        System.out.println("SB ADHOCLogAsyntask: " + sb.toString());
                        response.getEntity().consumeContent();

                        CommonFunctions.ResponseLog("\nAutoSync ADHOC Event Log Response \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+"\n");


                        JSONObject ob = new JSONObject(sb.toString());
                        if(ob.getInt(Constants.RESPONSE_RC)==0)
                        {
                            status=0;
                            jobNeedDAO.changeJobNeedSyncStatus(jobNeed.getJobneedid(),Constants.SYNC_STATUS_ONE);
                            long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                            attachmentDAO.changeAdhocReturnID(String.valueOf(returnidResp),String.valueOf(jobNeed.getJobneedid()));
                        }
                        else
                        {
                            CommonFunctions.ErrorLog("\nAutoSync ADHOC Error: \n "+jobNeed.getJobneedid()+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n");
                            status=-1;
                            jobNeedDAO.changeJobNeedSyncStatus(jobNeed.getJobneedid(),Constants.SYNC_STATUS_ZERO);
                            System.out.println("ADHOC upload found error");
                            break;
                        }
                    }
                    else {
                        CommonFunctions.ErrorLog("\nAutoSync ADHOC Error: \n"+jobNeed.getJobneedid()+"\n"+"Connection not established with server."+"\n");
                        System.out.println("SB1 ADHOCLogAsyntask ERROR ");
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
                } catch (Exception e){
                    e.printStackTrace();
                }

            }

        }
        else
        {
            status=0;
        }


        return status;
    }

    @Override
    protected void onPostExecute(Integer status) {
        //super.onPostExecute(status);

        iUploadAdhocInsertDataListener.finishAdhocInsertUpload(status);
    }


}


//not in used but its useful for next level
/*
uploadIncidentReportParameter=new UploadIncidentReportParameter();
        ArrayList<QuestionSetLevel_One>questionSetLevelOneArrayList=new ArrayList<>();
        ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();

        questionSetLevelOne=new QuestionSetLevel_One();
        questionSetLevelOne.setQuestionsetid(jobNeed.getQuestionsetid());
        jobNeedChildArrayList=new ArrayList<>();
        jobNeedChildArrayList=jobNeedDAO.getChildCheckPointList(jobNeed.getQuestionsetid());
        questionSetLevelTwoArrayList=new ArrayList<>();
        if(jobNeedChildArrayList!=null && jobNeedChildArrayList.size()>0)
        {
        for(int j=0;j<jobNeedChildArrayList.size();j++)
        {
        questionSetLevelTwo = new QuestionSetLevel_Two();
        questionSetLevelTwo.setQuestionsetid(jobNeedChildArrayList.get(j).getQuestionsetid());
        jobNeedChildDetailsArrayList=new ArrayList<>();
        jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getQuestionsetid());
        questionSetLevelTwo.setDetails(jobNeedChildDetailsArrayList);
        questionSetLevelTwoArrayList.add(questionSetLevelTwo);
        }

        questionSetLevelOne.setSubChild(questionSetLevelTwoArrayList);
        }
        questionSetLevelOneArrayList.add(questionSetLevelOne);

        uploadIncidentReportParameter=new UploadIncidentReportParameter();
        uploadIncidentReportParameter.setJobdesc("INCIDENTREPORT");
        uploadIncidentReportParameter.setAatop(jobNeed.getAatop());
        uploadIncidentReportParameter.setAssetid(jobNeed.getAssetid());
        uploadIncidentReportParameter.setCuser(jobNeed.getCuser());
        uploadIncidentReportParameter.setFrequency(jobNeed.getFrequency());
        uploadIncidentReportParameter.setPlandatetime(jobNeed.getPlandatetime());
        uploadIncidentReportParameter.setExpirydatetime(jobNeed.getExpirydatetime());
        uploadIncidentReportParameter.setGracetime(jobNeed.getGracetime());
        uploadIncidentReportParameter.setGroupid(jobNeed.getGroupid());
        uploadIncidentReportParameter.setIdentifier(jobNeed.getIdentifier());
        uploadIncidentReportParameter.setJobid(jobNeed.getJobid());
        uploadIncidentReportParameter.setJobneedid(jobNeed.getJobneedid());
        uploadIncidentReportParameter.setJobstatus(jobNeed.getJobstatus());
        uploadIncidentReportParameter.setJobtype(jobNeed.getJobtype());
        uploadIncidentReportParameter.setMuser(jobNeed.getMuser());
        uploadIncidentReportParameter.setParent(jobNeed.getParent());
        uploadIncidentReportParameter.setPeopleid(jobNeed.getPeopleid());
        uploadIncidentReportParameter.setPerformedby(jobNeed.getPerformedby());
        uploadIncidentReportParameter.setPriority(jobNeed.getPriority());
        uploadIncidentReportParameter.setScantype(jobNeed.getScantype());
        uploadIncidentReportParameter.setChild(questionSetLevelOneArrayList);

        Gson gson=new Gson();
        String ss=gson.toJson(uploadIncidentReportParameter);
        System.out.println("SS: "+ss.toString());*/
