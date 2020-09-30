package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadJobNeedInsertDataListener;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.Model.QuestionSetLevel_Two;
import com.youtility.intelliwiz20.Model.UploadIncidentReportParameter;
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

public class JobNeedInsertAsynctask extends AsyncTask<Void , Integer, Integer> {
    private IUploadJobNeedInsertDataListener iUploadJobNeedInsertDataListener;
    private JobNeed jobNeed;
    private Context context;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private ArrayList<JobNeed>incidentReportArrayList;
    private ArrayList<JobNeed> jobNeedChildArrayList;
    private ArrayList<JobNeedDetails>jobNeedChildDetailsArrayList;
    private UploadIncidentReportParameter uploadIncidentReportParameter;
    //QuestionSetLevel_One questionSetLevelOne=null;
    QuestionSetLevel_Two questionSetLevelTwo=null;
    private JobNeedDAO jobNeedDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private Gson gson;
    private SharedPreferences loginPref;
    private AttachmentDAO attachmentDAO;

    public JobNeedInsertAsynctask(Context context, IUploadJobNeedInsertDataListener iUploadJobNeedInsertDataListener)
    {
        this.context=context;
        this.iUploadJobNeedInsertDataListener=iUploadJobNeedInsertDataListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jobNeedDAO=new JobNeedDAO(context);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(context);
        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        attachmentDAO=new AttachmentDAO(context);

        incidentReportArrayList=new ArrayList<>();
        incidentReportArrayList=jobNeedDAO.getUnsyncIRList();
    }


    @Override
    protected Integer doInBackground(Void... voids) {
        int status=-1;

/*        incidentReportArrayList=new ArrayList<>();
        incidentReportArrayList=jobNeedDAO.getUnsyncIRList();*/
        if(incidentReportArrayList!=null && incidentReportArrayList.size()>0)
        {
            for(int i=0;i<incidentReportArrayList.size();i++) {
                jobNeed = new JobNeed();
                jobNeed = incidentReportArrayList.get(i);

                try {
                    ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();

                    jobNeedChildArrayList=new ArrayList<>();
                    //jobNeedChildArrayList=jobNeedDAO.getChildCheckPointList(jobNeed.getQuestionsetid());

                    jobNeedChildArrayList=jobNeedDAO.getChildCheckPointList(jobNeed.getJobneedid());


                    questionSetLevelTwoArrayList=new ArrayList<>();
                    if(jobNeedChildArrayList!=null && jobNeedChildArrayList.size()>0)
                    {
                        System.out.println("jobNeedChildArrayList: "+jobNeedChildArrayList.size());
                        for(int j=0;j<jobNeedChildArrayList.size();j++)
                        {
                            questionSetLevelTwo = new QuestionSetLevel_Two();
                            questionSetLevelTwo.setQuestionsetid(jobNeedChildArrayList.get(j).getQuestionsetid());
                            questionSetLevelTwo.setJobdesc(jobNeedChildArrayList.get(j).getJobdesc());
                            questionSetLevelTwo.setSeqno(jobNeedChildArrayList.get(j).getSeqno());

                            System.out.println("jobNeed.getJobdesc()--"+jobNeedChildArrayList.get(j).getJobdesc());

                            jobNeedChildDetailsArrayList=new ArrayList<>();
                            //jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getQuestionsetid());

                            jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getJobneedid());


                            System.out.println("jobneed array list--"+ jobNeedChildDetailsArrayList.size());
                            questionSetLevelTwo.setDetails(jobNeedChildDetailsArrayList);
                            questionSetLevelTwoArrayList.add(questionSetLevelTwo);
                        }
                    }


                    uploadIncidentReportParameter=new UploadIncidentReportParameter();
                    uploadIncidentReportParameter.setJobdesc(jobNeed.getJobdesc());
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
                    uploadIncidentReportParameter.setQuestionsetid(jobNeed.getQuestionsetid());
                    uploadIncidentReportParameter.setBuid(jobNeed.getBuid());
                    uploadIncidentReportParameter.setGpslocation(jobNeed.getGpslocation());
                    uploadIncidentReportParameter.setCdtzoffset(jobNeed.getCtzoffset());
                    uploadIncidentReportParameter.setOthersite(jobNeed.getOthersite());
                    uploadIncidentReportParameter.setChild(questionSetLevelTwoArrayList);

                    String ss=gson.toJson(uploadIncidentReportParameter);
                    System.out.println("Incident report SS: "+ss);


                    //---------------------------------------------------------------------

                    try {
                        ServerRequest serverRequest=new ServerRequest(context);
                        HttpResponse response=serverRequest.getIncidentReportLogResponse(ss.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("IRLogAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB IRLogAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();

                            CommonFunctions.ResponseLog("\n AutoSync IR Event Log Response \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                status=0;
                                jobNeedDAO.changeJobNeedSyncStatus(jobNeed.getJobneedid(),Constants.SYNC_STATUS_ONE);
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changePelogReturnID(String.valueOf(returnidResp),(jobNeed.getJobneedid()));

                            }
                            else
                            {
                                CommonFunctions.ErrorLog("\n AutoSync IR Error: \n "+jobNeed.getJobneedid()+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n");
                                status=-1;
                                break;
                            }



                        }
                        else {
                            CommonFunctions.ErrorLog("\n AutoSync IR Error: \n"+jobNeed.getJobneedid()+"\n"+"Connection not established with server."+"\n");
                            status=-1;
                            break;
                        }
                    } catch (UnrecoverableKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (KeyStoreException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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

        iUploadJobNeedInsertDataListener.finishJobNeedInsertUpload(status);


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
