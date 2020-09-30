package com.youtility.intelliwiz20.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.youtility.intelliwiz20.Adapters.AVPGridViewAdapter;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class AttachmentListActivity extends AppCompatActivity implements View.OnClickListener {

    /*private Boolean isFabOpen = false;
    private FloatingActionButton fabMain,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;*/
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private GridView capturedImageGridView;
    private AVPGridViewAdapter avpGridViewAdapter;
    private int fromActivity=0;
    private long timeStamp;
    private String parentActivity=null;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int MIC_RECORD_AUDIO_REQUEST_CODE = 000;

    ActionBar actionBar;

    ArrayList<Attachment> attachmentList;
    private AttachmentDAO attachmentDAO;

    private Uri fileUri;

    private Boolean isSDPresent=false;
    private String extStorageDirectory="";
    private SharedPreferences loginDetailPref;
    private SharedPreferences deviceRelatedPref;
    private long jobNeedID=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_list);

        fromActivity=getIntent().getIntExtra("FROM",0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setTitle(getActivityName(fromActivity));
        getSupportActionBar().setTitle(getResources().getString(R.string.title_attachment_list));
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginDetailPref=getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);


        attachmentDAO=new AttachmentDAO(AttachmentListActivity.this);

        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();


        timeStamp=getIntent().getLongExtra("TIMESTAMP",0l);
        //parentActivity=getIntent().getStringExtra("PARENT_ACTIVITY");


        if(getIntent().hasExtra("JOBNEEDID"))
            jobNeedID=getIntent().getLongExtra("JOBNEEDID",-1);

        attachmentList=new ArrayList<Attachment>();
        attachmentList=attachmentDAO.getAttachments(timeStamp,jobNeedID);

        componentInitialise();


       /* actionBar=getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getActivityName(fromActivity));
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCapture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromActivity==0) {
                    captureAudio();
                }
                else if(fromActivity==1) {
                    captureVideo();
                }
                else if(fromActivity==2) {
                    captureImage();
                }
                Snackbar.make(view, "Capture", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private String getActivityName(int val)
    {

        switch(val)
        {
            case 0:
                return "Audio";
            case 1:
                return "Video";
            case 2:
                return "Picture";
            default:
                return "";
        }

    }

    private void componentInitialise()
    {
        /*attachmentList=new ArrayList<>();
        for(int i=0;i<10;i++)
        {
            if(fromActivity==0)
                attachmentList.add("Audio "+i);
            else if(fromActivity==1)
                attachmentList.add("Video "+i);
            else if(fromActivity==2)
                attachmentList.add("Image "+i);

        }*/
        capturedImageGridView=(GridView)findViewById(R.id.capturedImgGrid);
        avpGridViewAdapter=new AVPGridViewAdapter(AttachmentListActivity.this,attachmentList);
        capturedImageGridView.setAdapter(avpGridViewAdapter);
        /*fabMain = (FloatingActionButton)findViewById(R.id.fabMain);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fabMain.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId())
        {
            case R.id.fabMain:
                //animateFAB();
                break;
            case R.id.fab1:
                break;
            case R.id.fab2:
                break;
        }*/
    }

    /*public void animateFAB(){

        if(isFabOpen){

            fabMain.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;


        } else {

            fabMain.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;


        }
    }*/

    private void captureAudio()
    {
        Intent mediaRecoder=new Intent(AttachmentListActivity.this,MediaRecoderView.class);
        mediaRecoder.putExtra("FROM",fromActivity);
        mediaRecoder.putExtra("TIMESTAMP",timeStamp);
        mediaRecoder.putExtra("JOBNEEDID",jobNeedID);
        mediaRecoder.putExtra("PARENT_ACTIVITY",parentActivity);
        startActivityForResult(mediaRecoder, MIC_RECORD_AUDIO_REQUEST_CODE);
    }

    private void captureVideo()
    {
        /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO, loginDetailPref.getString(Constants.LOGIN_USER_CODE,""));

        // set video quality
        // 1- for high quality video
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);*/

        Intent captureVideo=new Intent(AttachmentListActivity.this, VideoCaptureActivity.class);
        captureVideo.putExtra("FROM",fromActivity);
        captureVideo.putExtra("TIMESTAMP",timeStamp);
        captureVideo.putExtra("JOBNEEDID",jobNeedID);
        captureVideo.putExtra("PARENT_ACTIVITY",parentActivity);
        startActivityForResult(captureVideo,CAMERA_CAPTURE_VIDEO_REQUEST_CODE);

    }

    private void captureImage() {
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, loginDetailPref.getString(Constants.LOGIN_USER_CODE,""));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/
        Intent capturePic=new Intent(AttachmentListActivity.this, CapturePhotoActivity.class);
        capturePic.putExtra("FROM",fromActivity);
        capturePic.putExtra("TIMESTAMP",timeStamp);
        capturePic.putExtra("JOBNEEDID",jobNeedID);
        capturePic.putExtra("PARENT_ACTIVITY",parentActivity);
        startActivityForResult(capturePic,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type, String userCode) {
        return Uri.fromFile(getOutputMediaFile(type, userCode));
    }

    private static File getOutputMediaFile(int type, String userCode) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.FOLDER_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //userCode+"_"+System.currentTimeMillis()+"_Attendance";
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());

        File mediaFile=null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + userCode+"_"+System.currentTimeMillis()+"_ADHOC"+ ".png");
        }
        else if(type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + userCode+"_"+System.currentTimeMillis()+"_ADHOC"+ ".mp4");
        }
        else {
            return null;
        }
        System.out.println("MediaFileURL: "+mediaFile.getAbsolutePath());
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                attachmentList=new ArrayList<Attachment>();
                attachmentList=attachmentDAO.getAttachments(timeStamp, Constants.ATTACHMENT_PICTURE, jobNeedID);
                avpGridViewAdapter=new AVPGridViewAdapter(AttachmentListActivity.this,attachmentList);
                capturedImageGridView.setAdapter(avpGridViewAdapter);

                //saveToSDCard(fileUri.getPath());
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==CAMERA_CAPTURE_VIDEO_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                attachmentList=new ArrayList<Attachment>();
                attachmentList=attachmentDAO.getAttachments(timeStamp, Constants.ATTACHMENT_VIDEO, jobNeedID);
                avpGridViewAdapter=new AVPGridViewAdapter(AttachmentListActivity.this,attachmentList);
                capturedImageGridView.setAdapter(avpGridViewAdapter);
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==MIC_RECORD_AUDIO_REQUEST_CODE)
        {
            if(resultCode==RESULT_OK)
            {
                attachmentList=new ArrayList<Attachment>();
                attachmentList=attachmentDAO.getAttachments(timeStamp, Constants.ATTACHMENT_AUDIO, jobNeedID);
                avpGridViewAdapter=new AVPGridViewAdapter(AttachmentListActivity.this,attachmentList);
                capturedImageGridView.setAdapter(avpGridViewAdapter);
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getApplicationContext(), "User cancelled audio recording", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToSDCard(String path) {



        if (isSDPresent) {

            String gpsLocation=deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0");

            UUID uuid = UUID.randomUUID();
            String randomString = loginDetailPref.getString(Constants.LOGIN_PEOPLE_ID,"")+"_"+System.currentTimeMillis()+"_ADHOC";
            String dirPath = extStorageDirectory + "/" + Constants.FOLDER_NAME + "/" + Constants.ATTACHMENT_FOLDER_NAME + "/";

            try {
                if (CommonFunctions.checkFileExists(dirPath)) {
                    System.out.println("Directory already exits");
                } else {
                    File dir = new File(dirPath);
                    dir.mkdirs();
                    System.out.println("Directory created");
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String imgeURI=null;
            File file = new File(dirPath+randomString+".PNG");
            System.out.println("SAVE SD File: "+file.getAbsolutePath());
            try {

                imgeURI=dirPath+randomString+".png";
                System.out.println("imgeURI" +imgeURI);
                int fileSize=(int) file.length();
                Bitmap bitmapOrg1 = BitmapFactory.decodeFile(path);
                Bitmap immagex=bitmapOrg1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b);
                fos.close();


                //check with new data base columns

               /* Attachment attachment = new Attachment();
                attachment.setAttachmentid(timeStamp);
                attachment.setPeoplecode("TARUN");
                attachment.setPaid("None");
                attachment.setJobneedid("None");
                attachment.setJndid("None");
                attachment.setAcode("None");
                attachment.setQscode("None");
                attachment.setAttachmentType("Attachment");
                attachment.setFilePath(file.getAbsolutePath());
                attachment.setFileName(randomString + ".png");
                attachment.setNarration("");
                attachment.setGpslocation(gpsLocation);
                attachment.setDatetime(CommonFunctions.getParseDate(System.currentTimeMillis()));
                attachment.setCuser(loginDetailPref.getString(Constants.LOGIN_USER_CODE,""));
                attachment.setCdtz(CommonFunctions.getParseDate(System.currentTimeMillis()));
                attachment.setMuser(loginDetailPref.getString(Constants.LOGIN_USER_CODE,""));
                attachment.setMdtz(CommonFunctions.getParseDate(System.currentTimeMillis()));

                attachmentList.add(attachment);

                AttachmentDAO attachmentDAO = new AttachmentDAO(AttachmentListActivity.this);
                attachmentDAO.insertCommonRecord(attachment);*/

            } catch (Exception e) {
                e.printStackTrace();

            }
            /*UploadImageAsynTask uploadImageAsynTask=new UploadImageAsynTask(imgeURI);
            uploadImageAsynTask.execute();*/
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }
}
