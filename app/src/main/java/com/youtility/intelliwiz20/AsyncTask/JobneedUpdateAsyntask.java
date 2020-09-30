package com.youtility.intelliwiz20.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.Interfaces.IUploadJobneedUpdateDataListener;
import com.youtility.intelliwiz20.Model.BiodataParameters;
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
 * Created by PrashantD on 12/10/17.
 *
 * not in used
 */

public class JobneedUpdateAsyntask extends AsyncTask<Void, Integer, Integer> {

    private Context context;
    private IUploadJobneedUpdateDataListener iUploadJobneedUpdateDataListener;
    private JobNeed jobNeed;

    private Gson gson;
    private JobNeedDAO jobNeedDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private ArrayList<JobNeedDetails>jobNeedDetailsArrayList;
    private UploadJobneedParameter uploadJobneedParameter;
    private ArrayList<JobNeed>jobUpdateArrayList;

    private long atog=-1;
    private long atop=-1;
    private String alertType=null;
    private long alertTo=-1;
    private SharedPreferences loginPref;
    private byte[] buffer;
    private int bytesRead;
    private InputStream is;
    private StringBuffer sb;

    public JobneedUpdateAsyntask(Context context, IUploadJobneedUpdateDataListener iUploadJobneedUpdateDataListener)
    {
        this.context=context;
        this.iUploadJobneedUpdateDataListener = iUploadJobneedUpdateDataListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        jobNeedDAO=new JobNeedDAO(context);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(context);
        gson=new Gson();
        loginPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        jobUpdateArrayList=new ArrayList<JobNeed>();
        jobUpdateArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"
                +Constants.JOB_NEED_IDENTIFIER_TASK+"','"
                +Constants.JOB_NEED_IDENTIFIER_TICKET+"','"
                +Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"','"
                +Constants.JOB_NEED_IDENTIFIER_PPM+"'",2);

        System.out.println("jobNeedArrayList.size(): "+jobUpdateArrayList.size());
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int status=-1;

/*        jobUpdateArrayList=new ArrayList<>();
        jobUpdateArrayList=jobNeedDAO.getUnsyncJobList("'"+Constants.JOB_NEED_IDENTIFIER_TOUR+"','"
                +Constants.JOB_NEED_IDENTIFIER_TASK+"','"
                +Constants.JOB_NEED_IDENTIFIER_TICKET+"','"
                +Constants.JOB_NEED_IDENTIFIER_SITEREPORT+"','"
                +Constants.JOB_NEED_IDENTIFIER_PPM+"'",2);
        System.out.println("jobNeedArrayList.size(): "+jobUpdateArrayList.size());*/


        if(jobUpdateArrayList!=null && jobUpdateArrayList.size()>0) {
            for (int i = 0; i < jobUpdateArrayList.size(); i++) {
                jobNeed = new JobNeed();
                jobNeed = jobUpdateArrayList.get(i);

                try {
                    if(jobNeed.getPeopleid()==-1)
                        atop=-1;
                    else
                        atop=jobNeed.getPeopleid();

                    if(jobNeed.getGroupid()==-1)
                        atog=-1;
                    else
                        atog=jobNeed.getGroupid();

                    System.out.println("atop: "+atop);
                    System.out.println("atog: "+atog);

                    if(atog!=-1) {
                        alertTo=atog;
                        alertType="GROUP";
                    }
                    if(atop!=-1)
                    {
                        alertTo=atop;
                        alertType="PEOPLE";
                    }
                    System.out.println("alertTo: "+alertTo);
                    System.out.println("alertType: "+alertType);

                    String dev= "false";
                    System.out.println("jobNeed.getDeviation()"+jobNeed.getDeviation());
                    if(jobNeed.getDeviation() != null && (jobNeed.getDeviation()).equals("1")){
                        dev= "true";
                        System.out.println("dev======"+dev+"="+jobNeed.getDeviation());
                    }else {
                        System.out.println("dev======"+dev+"="+jobNeed.getDeviation());
                    }
                    uploadJobneedParameter=new UploadJobneedParameter();

                    jobNeedDetailsArrayList=new ArrayList<>();

                    jobNeedDetailsArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeed.getJobneedid());

                    uploadJobneedParameter=new UploadJobneedParameter();
                    uploadJobneedParameter.setJobdesc(jobNeed.getJobdesc());
                    uploadJobneedParameter.setAatop(jobNeed.getAatop());
                    uploadJobneedParameter.setAssetid(jobNeed.getAssetid());
                    uploadJobneedParameter.setCuser(jobNeed.getCuser());
                    uploadJobneedParameter.setFrequency(jobNeed.getFrequency());
                    System.out.println("jobNeed.getPlandatetime(): "+jobNeed.getPlandatetime());
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
                    uploadJobneedParameter.setCdtz(jobNeed.getCdtz());
                    uploadJobneedParameter.setMdtz(jobNeed.getMdtz());
                    System.out.println("upload deviation"+ jobNeed.getDeviation());
                    //uploadJobneedParameter.setDeviation(jobNeed.getDeviation().);
                    uploadJobneedParameter.setDeviation(dev);


                    String ss=gson.toJson(uploadJobneedParameter);
                    System.out.println("SS: "+ss);

                    BiodataParameters biodataParameters=new BiodataParameters();
                    biodataParameters.setJobneedid(jobNeed.getJobneedid());
                    biodataParameters.setJobdesc(jobNeed.getJobdesc());
                    biodataParameters.setJobstatus(jobNeed.getJobstatus());
                    biodataParameters.setCuser(jobNeed.getCuser());
                    biodataParameters.setRemarks(jobNeed.getRemarks());
                    biodataParameters.setAlertto(alertTo);
                    biodataParameters.setAssigntype(alertType);

                    String bioData=gson.toJson(biodataParameters);
                    System.out.println("bioData: "+bioData);


                    //---------------------------------------------------------------------

                    try {
                        ServerRequest serverRequest=new ServerRequest(context);
                        HttpResponse response=serverRequest.getJOBUpdateLogResponse(ss.trim(), ss.trim(),
                                loginPref.getInt(Constants.CURRENT_TIMEZONE_OFFSET_NUMBER,0),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_SITE,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_ID,""),
                                loginPref.getString(Constants.LOGIN_ENTERED_USER_PASS,""));

                        //System.out.println("JOBUpdateAsyntask response.getStatusLine().getStatusCode(): "+response.getStatusLine().getStatusCode());
                        if(response!=null && response.getStatusLine().getStatusCode()==200)
                        {
                            sb = new StringBuffer();

                            is = response.getEntity().getContent();

                            CommonFunctions.ResponseLog("\n AutoSync JOB Update Event Log Response \n"+jobNeed.getJobneedid()+"\n"+sb.toString()+"\n");



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
                            System.out.println("SB JOBUpdateAsyntask: " + sb.toString());
                            response.getEntity().consumeContent();

                            JSONObject ob = new JSONObject(sb.toString());
                            if(ob.getInt(Constants.RESPONSE_RC)==0)
                            {
                                status=0;
                                jobNeedDAO.changeJobNeedSyncStatus(jobNeed.getJobneedid(),Constants.SYNC_STATUS_ONE);
                            }
                            else
                            {
                                CommonFunctions.ErrorLog("\n AutoSync JOB Update Error: \n"+jobNeed.getJobneedid()+"\n"+"RESPONSE_RC: "+ob.getInt(Constants.RESPONSE_RC)+"\n"+sb.toString()+" \n");
                                status=-1;
                                break;
                            }

                        }
                        else {
                            CommonFunctions.ErrorLog("\n AutoSync JOB Update Error: \n"+jobNeed.getJobneedid()+"\n"+"Connection not established with server."+"\n");
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

        iUploadJobneedUpdateDataListener.finishJobneedUpdateUpload(status);
        //iUploadPELogDataListener.uploadSOSPELog(queryStatus,returnidResp);

    }

}
