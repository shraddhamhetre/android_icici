package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadSiteReportDataListener;
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
 * Created by youtility on 26/7/18.
 */

public class SiteReportLogAsynctask extends AsyncTask <Void , Integer, Integer>{

    private Context context;
    private IUploadSiteReportDataListener iUploadSiteReportDataListener;

    private SharedPreferences loginPref;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;
    private Gson gson;

    private JobNeed jobNeed;
    private UploadIncidentReportParameter uploadIncidentReportParameter;
    private ArrayList<JobNeed> jobNeedChildArrayList;
    private ArrayList<JobNeedDetails>jobNeedChildDetailsArrayList;
    private QuestionSetLevel_Two questionSetLevelTwo=null;
    private ArrayList<JobNeed>incidentReportArrayList;
    private JobNeedDAO jobNeedDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private AttachmentDAO attachmentDAO;

    public SiteReportLogAsynctask(Context context, IUploadSiteReportDataListener iUploadSiteReportDataListener)
    {
        this.context=context;
        this.iUploadSiteReportDataListener=iUploadSiteReportDataListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jobNeedDAO=new JobNeedDAO(context);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(context);
        attachmentDAO=new AttachmentDAO(context);

        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        incidentReportArrayList=new ArrayList<>();
        incidentReportArrayList=jobNeedDAO.getUnsyncSiteReportList();
    }


    @Override
    protected Integer doInBackground(Void... params) {
        int queryStatus=-1;
        if(incidentReportArrayList!=null && incidentReportArrayList.size()>0)
        {
            System.out.println("SiteAuditReport ArrayList: "+incidentReportArrayList.size());
            for(int i=0;i<incidentReportArrayList.size();i++)
            {
                try {
                    jobNeed=new JobNeed();
                    jobNeed=incidentReportArrayList.get(i);
                    ArrayList<QuestionSetLevel_Two>questionSetLevelTwoArrayList=new ArrayList<>();

                    jobNeedChildArrayList=new ArrayList<>();
                    jobNeedChildArrayList=jobNeedDAO.getSiteReportChildSectionList(jobNeed.getJobneedid());
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
                            jobNeedChildDetailsArrayList=new ArrayList<>();
                            jobNeedChildDetailsArrayList=jobNeedDetailsDAO.test_getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getJobneedid());
                            //jobNeedChildDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeedChildArrayList.get(j).getJobneedid());
                            questionSetLevelTwo.setDetails(jobNeedChildDetailsArrayList);
                            questionSetLevelTwoArrayList.add(questionSetLevelTwo);
                        }
                    }


                    if(jobNeedChildDetailsArrayList!=null && jobNeedChildDetailsArrayList.size()>0) {
                        uploadIncidentReportParameter = new UploadIncidentReportParameter();
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

                        String ss = gson.toJson(uploadIncidentReportParameter);
                        System.out.println("Incident report SS: " + ss);


                        //---------------------------------------------------------------------

                        ServerRequest serverRequest = new ServerRequest(context);
                        HttpResponse response = serverRequest.getIncidentReportLogResponse(ss.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER, 0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE, ""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID, ""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS, ""));

                        //System.out.println("SiteReportAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
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
                            System.out.println("SB IRLogAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();

                            CommonFunctions.ResponseLog("\n AutoSync SA Event Log Response \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+"\n");

                            JSONObject ob = new JSONObject(sb.toString());
                            if (ob.getInt(Constants.RESPONSE_RC) == 0) {
                                queryStatus = 0;
                                jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(), Constants.SYNC_STATUS_ONE);
                                if (jobNeedChildArrayList != null && jobNeedChildArrayList.size() > 0) {
                                    for (int cJob = 0; cJob < jobNeedChildArrayList.size(); cJob++) {
                                        jobNeedDAO.changeJobNeedSyncStatus(jobNeedChildArrayList.get(cJob).getJobneedid(), Constants.SYNC_STATUS_ONE);
                                    }
                                }
                                long returnidResp = ob.getLong(Constants.RESPONSE_RETURNID);
                                attachmentDAO.changePelogReturnID(String.valueOf(returnidResp), (incidentReportArrayList.get(i).getJobneedid()));
                                jobNeedDAO.getCount();
                            } else {
                                CommonFunctions.ErrorLog("\n AutoSync SA Error: \n"+jobNeed.getJobneedid()+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n");
                                queryStatus = -1;
                                break;
                            }
                        } else {
                            CommonFunctions.ErrorLog("\nAutoSync SA Error: \n"+jobNeed.getJobneedid()+"\n"+"Connection not established with server."+"\n");
                            System.out.println("SB1 IRLogAsyntask: " + response.getStatusLine().getStatusCode());
                            queryStatus=-1;
                            break;
                        }
                    }
                    else
                    {
                        jobNeedDAO.changeJobNeedSyncStatus(incidentReportArrayList.get(i).getJobneedid(), Constants.SYNC_STATUS_ONE);
                        jobNeedDAO.deleteRec(incidentReportArrayList.get(i).getJobneedid());
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
            queryStatus=0;
        }
        return queryStatus;
    }




    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        iUploadSiteReportDataListener.finishSiteReportUpload(status);
    }
}
