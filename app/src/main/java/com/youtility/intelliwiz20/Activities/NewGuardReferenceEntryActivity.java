package com.youtility.intelliwiz20.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.youtility.intelliwiz20.BroadcastReceiver.ConnectivityReceiver;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.PersonLoggerDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Interfaces.IDialogEventListeners;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.PersonLogger;
import com.youtility.intelliwiz20.Model.TypeAssist;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.CustomAlertDialog;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NewGuardReferenceEntryActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher, RadioGroup.OnCheckedChangeListener, NumberPicker.OnValueChangeListener, ConnectivityReceiver.ConnectivityReceiverListener, IDialogEventListeners {

    private ConnectivityReceiver connectivityReceiver;
    private Boolean isSDPresent=false;
    private String extStorageDirectory="";
    private SharedPreferences loginPreference;
    private SharedPreferences deviceRelatedPref;
    private PersonLoggerDAO personLoggerDAO;
    private TypeAssistDAO typeAssistDAO;
    private AttachmentDAO attachmentDAO;
    private long currentTimeInMillis=-1;

    private File aadharFile;

    private ViewFlipper viewFlipper;
    private Button nextButton, previousButton;
    private int currPosition=-1;
    private int steps=3;

    private boolean isDocCaptured=false;

    //------------------first view
    private EditText userFirstName, userMiddleName,userLastName, userLAddress, userNAddress, userAreaCode, userMobileNumber, userDOB, userIdMark, userReligion, userCaste,referredbyEdittext,userLCity, userNCity, userNAreaCode;
    private TextInputLayout userFNameInputLayout, userMNameInputLayout, userLNameInputLayout, user_local_address_layout, user_native_address_layout, user_identifcationmark_layout, user_religion_layout, user_caste_layout, user_referedby_layout;
    private TextView calculatedAgeTextview;
    private String[] maritalStatusList={"Single","Married","Widowed","Divorced","Separated"};// single, married, widowed, divorced, separated
    private String[] genderList={"Male","Female"};
    private Spinner maritalStatusSpinner, genderSpinner,userLState, userNState;
    private ImageButton dobImageButton;
    private ArrayList<TypeAssist>lStateList;
    private ArrayList<String>lStateNameList;
    private ArrayList<TypeAssist>nStateList;
    private ArrayList<String>nStateNameList;
    //------------------- second view
    private Spinner educationDegreeSpinner;
    //private String[] educationDegreeNames={"None","SSC","HSC","Diploma","BA","B.Com","BE","ITI","BSC","BCA","BBA"};
    private ArrayList<TypeAssist>educationList=null;
    private ArrayList<String>educationNameList=null;
    private EditText otherEducationEdittext, userPhysicalConditionEdittext;
    private EditText userHeightEdittext, userWeightEdittext, userWaistEdittext;
    private RadioGroup radioGroup, handicappedRadioGroup;
    private String isUnderstandEnglish="No", isHandicapped="No";
    private TextInputLayout userPhyConditionInputLayout;
    //------------- third view------
    private EditText currentEmploymentEdittext, serviceYearEdittext, serviceMonthEdittext;
    private NumberPicker yearPicker, monthPicker;

    //----------------------forth view
    private ImageView aadharCardIV, panCardIV,facePictureIV, fullPictureIV;

    private Spinner documentTypeSpinner;
    private EditText user_docNumber;
    private Button docCaptureButton;
    private Button docSaveButton;
    private LinearLayout attachedDocumentTypeLinearLayout;
    private ArrayList<String> docTypeArrayList=null;
    private ArrayList<TypeAssist> docTATypeArrayList=null;
    private ArrayList<Attachment> attachedDocTypeArraylist=null;

    private SharedPreferences loginPref;
    private CustomAlertDialog customAlertDialog;
    private SharedPreferences deviceInfoPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_guard_reference_entry);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        currentTimeInMillis=System.currentTimeMillis();

        loginPreference=getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        deviceRelatedPref=getSharedPreferences(Constants.DEVICE_RELATED_PREF, MODE_PRIVATE);
        personLoggerDAO=new PersonLoggerDAO(NewGuardReferenceEntryActivity.this);
        typeAssistDAO=new TypeAssistDAO(NewGuardReferenceEntryActivity.this);
        attachmentDAO=new AttachmentDAO(NewGuardReferenceEntryActivity.this);

        loginPref = getSharedPreferences(Constants.LOGIN_PREFERENCE, MODE_PRIVATE);
        customAlertDialog = new CustomAlertDialog(NewGuardReferenceEntryActivity.this, this);
        deviceInfoPref = getSharedPreferences(Constants.DEVICE_RELATED_PREF,MODE_PRIVATE);

        lStateList=typeAssistDAO.getEventList("State");
        lStateNameList=new ArrayList<>();
        for(int i=0;i<lStateList.size();i++)
        {
            if(lStateList.get(i).getTaid()!=-1)
            {
                lStateNameList.add(lStateList.get(i).getTaname());
            }
        }

        nStateList=typeAssistDAO.getEventList("State");
        nStateNameList=new ArrayList<>();
        for(int i=0;i<nStateList.size();i++)
        {
            if(nStateList.get(i).getTaid()!=-1)
            {
                nStateNameList.add(nStateList.get(i).getTaname());
            }
        }

        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();

        viewFlipper=(ViewFlipper)findViewById(R.id.viewFlipper);
        currPosition++;

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);

        nextButton=(Button)findViewById(R.id.nextButton);
        previousButton=(Button)findViewById(R.id.previousButton);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

        //---------------------first view related-------------------------------------------------------
        userFirstName=(EditText)findViewById(R.id.user_fName);
        userMiddleName=(EditText)findViewById(R.id.user_mName);
        userLastName=(EditText)findViewById(R.id.user_lName);
        userLAddress=(EditText)findViewById(R.id.user_localAddress);
        userNAddress=(EditText)findViewById(R.id.user_nativeAddress);
        userAreaCode=(EditText)findViewById(R.id.user_areaCode);

        userLCity=(EditText)findViewById(R.id.user_lCity);
        userNCity=(EditText)findViewById(R.id.user_nCity);
        userNAreaCode=(EditText)findViewById(R.id.user_nareaCode);

        userLState=(Spinner)findViewById(R.id.lStateSpinner);
        userNState=(Spinner)findViewById(R.id.nStateSpinner);

        userMobileNumber=(EditText)findViewById(R.id.user_mobileNumber);
        userDOB=(EditText)findViewById(R.id.dobEdittext);
        calculatedAgeTextview=(TextView)findViewById(R.id.calculatedAgeTextview);

        userReligion=(EditText)findViewById(R.id.user_religionEdittext);
        userCaste=(EditText)findViewById(R.id.user_casteEdittext);
        dobImageButton=(ImageButton)findViewById(R.id.dobImageButton);
        referredbyEdittext=(EditText)findViewById(R.id.user_referby);

        dobImageButton.setOnClickListener(this);

        userDOB.setOnClickListener(this);

        userFirstName.addTextChangedListener(this);
        userMiddleName.addTextChangedListener(this);
        userLastName.addTextChangedListener(this);
        userLAddress.addTextChangedListener(this);
        userNAddress.addTextChangedListener(this);
        userMobileNumber.addTextChangedListener(this);
        userDOB.addTextChangedListener(this);

        userReligion.addTextChangedListener(this);
        userCaste.addTextChangedListener(this);
        referredbyEdittext.addTextChangedListener(this);

        userFNameInputLayout=(TextInputLayout)findViewById(R.id.userFname_layout);
        userMNameInputLayout=(TextInputLayout)findViewById(R.id.userMname_layout);
        userLNameInputLayout=(TextInputLayout)findViewById(R.id.userLname_layout);
        user_local_address_layout=(TextInputLayout)findViewById(R.id.user_local_address_layout);
        user_native_address_layout=(TextInputLayout)findViewById(R.id.user_native_address_layout);

        user_religion_layout=(TextInputLayout)findViewById(R.id.user_religion_layout);
        user_caste_layout=(TextInputLayout)findViewById(R.id.user_caste_layout);
        user_referedby_layout=(TextInputLayout)findViewById(R.id.user_referenceby_layout) ;

        maritalStatusSpinner=(Spinner)findViewById(R.id.maritalStatusSpinner);
        genderSpinner=(Spinner)findViewById(R.id.genderSpinner);

        ArrayAdapter maritalAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,maritalStatusList);
        maritalAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maritalStatusSpinner.setAdapter(maritalAdpt);

        ArrayAdapter genderAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,genderList);
        genderAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdpt);

        ArrayAdapter lStateAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lStateNameList);
        genderAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userLState.setAdapter(lStateAdpt);

        ArrayAdapter nStateAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nStateNameList);
        genderAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userNState.setAdapter(nStateAdpt);



        //----------------------- second view related------------------------------------------------------------------

        userIdMark=(EditText)findViewById(R.id.user_idMarkEdittext);

        userPhysicalConditionEdittext=(EditText)findViewById(R.id.user_phyConditionEdittext);
        userPhysicalConditionEdittext.addTextChangedListener(this);

        userPhyConditionInputLayout=(TextInputLayout)findViewById(R.id.user_physicalcondition_layout);
        user_identifcationmark_layout=(TextInputLayout)findViewById(R.id.user_identifcationmark_layout);

        userIdMark.addTextChangedListener(this);

        handicappedRadioGroup=(RadioGroup)findViewById(R.id.handicappedRadioGroup);
        handicappedRadioGroup.setOnCheckedChangeListener(this);

        userHeightEdittext=(EditText)findViewById(R.id.user_height);
        userWeightEdittext=(EditText)findViewById(R.id.user_weight);
        userWaistEdittext=(EditText)findViewById(R.id.user_waist);

        //--------------------------- third view related--------------------------------------------------------------

        educationList=typeAssistDAO.getEventList("Qualification");
        educationNameList=new ArrayList<>();
        if(educationList!=null && educationList.size()>0) {
            for (int i = 0; i < educationList.size(); i++) {
                if (educationList.get(i).getTaid() != -1) {
                    educationNameList.add(educationList.get(i).getTaname());
                }
            }
        }

        educationDegreeSpinner=(Spinner)findViewById(R.id.educationDegreeSpinner);
        otherEducationEdittext=(EditText)findViewById(R.id.other_qualification);

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);

        currentEmploymentEdittext=(EditText)findViewById(R.id.user_currentemployment);
        //serviceYearEdittext=(EditText)findViewById(R.id.user_serviceYears);
        //serviceMonthEdittext=(EditText)findViewById(R.id.user_serviceMonths);


        yearPicker=(NumberPicker)findViewById(R.id.yearPicker);
        monthPicker=(NumberPicker)findViewById(R.id.monthsPicker);

        yearPicker.setMinValue(0);
        yearPicker.setMaxValue(90);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);

        yearPicker.setOnValueChangedListener(this);
        monthPicker.setOnValueChangedListener(this);

        ArrayAdapter freqAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,educationNameList);
        freqAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationDegreeSpinner.setAdapter(freqAdpt);

        //---------------------------- fourth view related -----------------------------------------------------
        /*aadharCardIV=(ImageView)findViewById(R.id.aadharcard_imageview);
        panCardIV=(ImageView)findViewById(R.id.pancard_imageview);
        facePictureIV=(ImageView)findViewById(R.id.userfaceimage_imageview);
        fullPictureIV=(ImageView)findViewById(R.id.userfullimage_imageview);

        aadharCardIV.setOnClickListener(this);
        panCardIV.setOnClickListener(this);
        facePictureIV.setOnClickListener(this);
        fullPictureIV.setOnClickListener(this);*/

        docCaptureButton=(Button)findViewById(R.id.docCaptureButton);
        documentTypeSpinner=(Spinner)findViewById(R.id.documentTypeSpinner);
        user_docNumber=(EditText)findViewById(R.id.user_docNumber);
        attachedDocumentTypeLinearLayout=(LinearLayout)findViewById(R.id.attachedDocumentTypeLinearLayout);
        docSaveButton=(Button)findViewById(R.id.docSaveButton);

        docSaveButton.setOnClickListener(this);
        docCaptureButton.setOnClickListener(this);

        docTATypeArrayList=new ArrayList<>();
        docTATypeArrayList=typeAssistDAO.getEventList(Constants.IDENTIFIER_IDPROOFTYPE);
        if(docTATypeArrayList!=null && docTATypeArrayList.size()>0)
        {
            docTypeArrayList=new ArrayList<>();
            for(int i=0;i<docTATypeArrayList.size();i++)
            {
                docTypeArrayList.add(i,docTATypeArrayList.get(i).getTacode());
            }
        }

        if(docTypeArrayList!=null && docTypeArrayList.size()>0)
        {
            attachedDocTypeArraylist=new ArrayList<>();
            ArrayAdapter docTypeAdpt = new ArrayAdapter(this,android.R.layout.simple_spinner_item,docTypeArrayList);
            freqAdpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            documentTypeSpinner.setAdapter(docTypeAdpt);
        }

    }

    private boolean isValidate(int pos)
    {

        switch (pos)
        {
            case 0:
                return validateFirstInput();
                //return true;
            case 1:
                return validateSecondInput();
                //return true;
            case 2:
                return validateThirdInput();
                //return true;
            case 3:
                return true;
        }
        return false;
    }

    private boolean validateFirstInput()
    {
        if(referredbyEdittext.getText().toString().trim().length()==0)
        {
            user_referedby_layout.setErrorEnabled(true);
            user_referedby_layout.setError(getResources().getString(R.string.ngentry_error_referedname));
            return false;
        }
        else if(userFirstName.getText().toString().trim().length()==0)
        {
            userFNameInputLayout.setErrorEnabled(true);
            userFNameInputLayout.setError(getResources().getString(R.string.ngentry_error_fname));
            return false;
        }
        else if(userLastName.getText().toString().trim().length()==0)
        {
            userLNameInputLayout.setErrorEnabled(true);
            userLNameInputLayout.setError(getResources().getString(R.string.ngentry_error_lname));
            return false;
        }
        else if(userMobileNumber.getText().toString().trim().length()==0)
        {
            userMobileNumber.setError(getResources().getString(R.string.ngentry_error_mobilenumber));
            return false;
        }
        else if(userDOB.getText().toString().trim().length()==0)
        {
            userDOB.setError(getResources().getString(R.string.ngentry_error_dob));
            return false;
        }

        else if(userReligion.getText().toString().trim().length()==0)
        {
            user_religion_layout.setErrorEnabled(true);
            user_religion_layout.setError(getResources().getString(R.string.ngentry_error_religion));
            return false;
        }
        else if(userCaste.getText().toString().trim().length()==0)
        {
            user_caste_layout.setErrorEnabled(true);
            user_caste_layout.setError(getResources().getString(R.string.ngentry_error_cast));
            return false;
        }
        else
            return true;
    }

    private boolean validateSecondInput()
    {
        /*if(educationDegreeSpinner.getSelectedItem().toString().equalsIgnoreCase("None") && otherEducationEdittext.getText().toString().trim().length()==0)
            return false;

       else*/
        if(userIdMark.getText().toString().trim().length()==0)
        {
            user_identifcationmark_layout.setErrorEnabled(true);
            user_identifcationmark_layout.setError(getResources().getString(R.string.ngentry_error_idmark));
            return false;
        }
        else if(userHeightEdittext.getText().toString().trim().length()==0)
        {
            userHeightEdittext.setError(getResources().getString(R.string.ngentry_error_height));
            return false;
        }
        else if(userWeightEdittext.getText().toString().trim().length()==0)
        {
            userWeightEdittext.setError(getResources().getString(R.string.ngentry_error_weight));
            return false;
        }
        else if(userWaistEdittext.getText().toString().trim().length()==0)
        {
            userWaistEdittext.setError(getResources().getString(R.string.ngentry_error_waist));
            return false;
        }
        else if(userPhysicalConditionEdittext.getText().toString().trim().length()==0)
        {
            userPhyConditionInputLayout.setErrorEnabled(true);
            userPhyConditionInputLayout.setError(getResources().getString(R.string.ngentry_error_phycondition));
            return false;
        }
        /*else if(isHandicapped==null)
        {
            return false;
        }*/
        else
            return true;
    }

    private boolean validateThirdInput()
    {
         if(educationList==null && educationList.size()==0)
            return false;
         /*else if(isUnderstandEnglish==null)
         {
             return false;
         }*/
        /*if(serviceYearEdittext.getText().toString().trim().length()==0) {
            serviceYearEdittext.setError("Please enter years");
            return false;
        }
        else if(serviceMonthEdittext.getText().toString().trim().length()==0)
        {
            serviceMonthEdittext.setError("Please enter months");
            return false;
        }
        else*/ /*if(referredbyEdittext.getText().toString().trim().length()==0)
        {
            referredbyEdittext.setError("Enter referred name");
            return false;
        }
        else*/
            return true;
    }

    @Override
    public void onClick(View v) {
        String randomString=null;
        String dirPath=null;
        Uri uriSavedImage=null;
        Intent imageIntent=null;

        int accessValue = CommonFunctions.isAllowToAccessModules(NewGuardReferenceEntryActivity.this, loginPref.getString(Constants.LOGIN_USER_CLIENT_CODE, ""), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false), loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_DATETIME, false));
        double latitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LATITUDE, "0.0"));
        double longitude = Double.valueOf(deviceInfoPref.getString(Constants.DEVICE_LONGITUDE, "0.0"));
        System.out.println("===========" + accessValue);
        System.out.println("===========" + loginPref.getBoolean(Constants.LOGIN_USER_CLIENT_CHECK_GPS, false));

        if (accessValue == 0) {
            switch (v.getId()) {
            /*case R.id.aadharcard_imageview:
                randomString=Constants.TACODE_ADHAR+"_"+CommonFunctions.getFileNameFromDate(System.currentTimeMillis());

                dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";
                aadharFile = new File(dirPath+randomString+".png");
                imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                uriSavedImage = Uri.fromFile(aadharFile);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, 0);
                break;
            case R.id.pancard_imageview:
                randomString=Constants.TACODE_PAN+"_"+CommonFunctions.getFileNameFromDate(System.currentTimeMillis());
                dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";
                aadharFile = new File(dirPath+randomString+".png");
                imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                uriSavedImage = Uri.fromFile(aadharFile);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, 1);
                break;
            case R.id.userfaceimage_imageview:
                randomString=CommonFunctions.getFileNameFromDate(System.currentTimeMillis());
                dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";
                aadharFile = new File(dirPath+randomString+".png");
                imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                uriSavedImage = Uri.fromFile(aadharFile);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, 2);
                break;
            case R.id.userfullimage_imageview:
                randomString=CommonFunctions.getFileNameFromDate(System.currentTimeMillis());
                dirPath=extStorageDirectory+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/";
                aadharFile = new File(dirPath+randomString+".png");
                imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                uriSavedImage = Uri.fromFile(aadharFile);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivityForResult(imageIntent, 3);
                break;*/
                case R.id.nextButton:
                    if (CommonFunctions.isPermissionGranted(NewGuardReferenceEntryActivity.this)) {
                        if (currPosition < steps) {
                            if (isValidate(currPosition)) {
                                if (currPosition == 2) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            nextButton.setText(getResources().getString(R.string.button_done));
                                        }
                                    });
                                }
                                viewFlipper.showNext();
                                currPosition++;
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    saveValues();
                                }
                            });
                        }
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();

                    break;
                case R.id.previousButton:
                    if (CommonFunctions.isPermissionGranted(NewGuardReferenceEntryActivity.this)) {
                        if (currPosition != 0) {
                            if (currPosition > 0) {
                                viewFlipper.showPrevious();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        nextButton.setText(getResources().getString(R.string.button_next));
                                    }
                                });
                                currPosition--;
                            } else {
                                currPosition = (steps);
                                viewFlipper.showPrevious();
                            }
                        }
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.docSaveButton:
                    if (CommonFunctions.isPermissionGranted(NewGuardReferenceEntryActivity.this)) {
                        Attachment attachment = new Attachment();
                        attachment.setFileName(docTypeArrayList.get(documentTypeSpinner.getSelectedItemPosition()));
                        attachment.setNarration(user_docNumber.getText().toString().trim());
                        if (aadharFile != null && aadharFile.exists())
                            attachment.setFilePath(aadharFile.getPath().trim());
                        else
                            attachment.setFilePath("");
                        attachedDocTypeArraylist.add(attachment);

                /*if(!isDocCaptured)
                {
                    Attachment attachmnt=new Attachment();
                    attachmnt.setAttachmentid(currentTimeInMillis);
                    attachmnt.setFilePath("");
                    attachmnt.setFileName("");
                    attachmnt.setNarration(user_docNumber.getText().toString().trim());
                    attachmnt.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
                    attachmnt.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    attachmnt.setCuser(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    attachmnt.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    attachmnt.setMuser(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    attachmnt.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    attachmnt.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_REPLY, Constants.IDENTIFIER_ATTACHMENT));
                    attachmnt.setOwnerid(currentTimeInMillis);
                    attachmnt.setOwnername(typeAssistDAO.getEventTypeID(Constants.TACODE_PERSONLOGGER, Constants.IDENTIFIER_OWNER));
                    attachmnt.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+"peopleeventlog/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis()));
                    attachmnt.setBuid(loginPreference.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
                    attachmentDAO.insertCommonRecord(attachmnt);
                }*/

                        prepareAttachedDocList();
                        user_docNumber.setText("");
                        aadharFile = null;
                        isDocCaptured = false;
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    break;
                case R.id.docCaptureButton:
                    if (CommonFunctions.isPermissionGranted(NewGuardReferenceEntryActivity.this)) {
                        isDocCaptured = true;
                        randomString = docTypeArrayList.get(documentTypeSpinner.getSelectedItemPosition());
                        dirPath = extStorageDirectory + "/" + Constants.FOLDER_NAME + "/" + Constants.ATTACHMENT_FOLDER_NAME + "/";
                        aadharFile = new File(dirPath + randomString + ".png");
                        imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        uriSavedImage = Uri.fromFile(aadharFile);
                        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                        startActivityForResult(imageIntent, 0);
                    } else
                        Snackbar.make(v, getResources().getString(R.string.error_msg_grant_permission), Snackbar.LENGTH_LONG).show();
                    //startActivityForResult(Intent.createChooser(imageIntent, "Your title"));
                    break;
                case R.id.dobImageButton:
                    Calendar mcurrentDate = Calendar.getInstance();
                    DatePickerDialog mDatePicker = new DatePickerDialog(NewGuardReferenceEntryActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("StringFormatInvalid")
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            Calendar myCal = Calendar.getInstance();
                            myCal.set(Calendar.YEAR, selectedyear);
                            myCal.set(Calendar.MONTH, selectedmonth);
                            myCal.set(Calendar.DAY_OF_MONTH, selectedday);

                            String myFormat = "dd MMM yyyy"; //In which you need put here
                            Locale locale = new Locale("en");
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, locale);
                            //userDOB.setText(sdf.format(myCal.getTime())+" Age: "+ CommonFunctions.getAge(selectedyear,selectedmonth, selectedday));
                            userDOB.setText(sdf.format(myCal.getTime()));
                            calculatedAgeTextview.setText(getResources().getString(R.string.ngentry_calculatedAge_hint, CommonFunctions.getAge(selectedyear, selectedmonth, selectedday)));

                        }
                    }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                    mDatePicker.setTitle(getResources().getString(R.string.ngentry_seletdate));
                    mDatePicker.show();
                    break;
            }
        }else if (accessValue == 1) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autodatetimeMessage));
        } else if (accessValue == 2) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autoGPSMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 3) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autowifiMessage), accessValue);
            System.out.println("==========="+accessValue);
        } else if (accessValue == 4) {
            customAlertDialog.commonDialog2(getResources().getString(R.string.gpsalerttitle), getResources().getString(R.string.autonetworkMessage), accessValue);
            System.out.println("==========="+accessValue);
        }else if (accessValue == 5 && latitude == 0.0 && longitude == 0.0) {
            customAlertDialog.commonDialog1(getResources().getString(R.string.alerttitle), getResources().getString(R.string.autoGeoCoordinatesMessage));
            System.out.println("===========lat long==0.0");
        }
    }

    private void prepareAttachedDocList()
    {
        if(attachedDocTypeArraylist!=null && attachedDocTypeArraylist.size()>0)
        {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vv;
            TextView textSrNo;
            TextView textDocName;
            ImageView docID_imageview;

            if(attachedDocumentTypeLinearLayout.getChildCount()>0)
                attachedDocumentTypeLinearLayout.removeAllViews();

            for(int i=0;i<attachedDocTypeArraylist.size();i++)
            {
                vv = inflater.inflate(R.layout.employee_ref_attached_document_row, null);
                textSrNo = (TextView) vv.findViewById(R.id.docNoTV);
                textDocName = (TextView) vv.findViewById(R.id.docNameTV);
                docID_imageview=(ImageView)vv.findViewById(R.id.docID_imageview);

                textSrNo.setText(String.valueOf(i+1));

                textDocName.setText(attachedDocTypeArraylist.get(i).getFileName()+"\n"+attachedDocTypeArraylist.get(i).getNarration());

                if(attachedDocTypeArraylist.get(i).getFilePath().trim().length()==0)
                {

                }
                else {
                    File image = new File(attachedDocTypeArraylist.get(i).getFilePath());
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 150 , 150, true);
                    try {
                        docID_imageview.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                attachedDocumentTypeLinearLayout.addView(vv);
            }
        }
    }

    private String repalaceChar(String valueString)
    {
        return valueString.replace("'","");
    }

    /*personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose,
    scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms,
    weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,areacode,enable,cuser,muser,cdtz,mdtz*/
    private void saveValues()
    {
        PersonLogger personLogger=new PersonLogger();
        personLogger.setPersonloggerid(currentTimeInMillis);
        personLogger.setIdentifier(typeAssistDAO.getEventTypeID(Constants.TACODE_EMPLOYEEREFERENCE, Constants.IDENTIFIER_PERSONLOGGERTYPE));
        personLogger.setPeopleid(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        personLogger.setVisitoridno("");
        personLogger.setFirstname(repalaceChar(userFirstName.getText().toString().trim()));
        personLogger.setMiddlename(repalaceChar(userMiddleName.getText().toString().trim()));
        personLogger.setLastname(repalaceChar(userLastName.getText().toString().trim()));
        personLogger.setMobileno(repalaceChar(userMobileNumber.getText().toString().trim()));
        personLogger.setIdprooftype(-1);
        personLogger.setPhotoidno("");
        personLogger.setBelongings("NOTHING");
        personLogger.setMeetingpurpose("");
        personLogger.setScheduledintime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        personLogger.setScheduledouttime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        personLogger.setActualintime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        personLogger.setActualouttime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        personLogger.setReferenceid(repalaceChar(referredbyEdittext.getText().toString().trim()));
        personLogger.setDob(userDOB.getText().toString().trim());
        personLogger.setLocaladdress(repalaceChar(userLAddress.getText().toString().trim()));
        personLogger.setLareacode(repalaceChar(userAreaCode.getText().toString().trim()));

        personLogger.setNativeaddress(repalaceChar(userNAddress.getText().toString().trim()));
        personLogger.setNareacode(repalaceChar(userNAreaCode.getText().toString().trim()));

        personLogger.setLcity(repalaceChar(userLCity.getText().toString().trim()));
        personLogger.setNcity(repalaceChar(userNCity.getText().toString().trim()));

        if(lStateList!=null && lStateList.size()>0) {
            personLogger.setLstate(lStateList.get(userLState.getSelectedItemPosition()).getTaid());
            System.out.println("L State: "+lStateNameList.get(userLState.getSelectedItemPosition()));
        }
        else {
            personLogger.setLstate(-1);
        }

        if(nStateList!=null && nStateList.size()>0) {
            personLogger.setNstate(nStateList.get(userNState.getSelectedItemPosition()).getTaid());
            System.out.println("N State: "+nStateNameList.get(userNState.getSelectedItemPosition()));
        }
        else {
            personLogger.setNstate(-1);
        }

        /*if(educationDegreeSpinner.getSelectedItem().toString().equalsIgnoreCase("None"))
            personLogger.setQualification(otherEducationEdittext.getText().toString().trim());
        else
            personLogger.setQualification(educationDegreeSpinner.getSelectedItem().toString());*/
        if(educationList!=null && educationList.size()>0)
            personLogger.setQualification(educationList.get(educationDegreeSpinner.getSelectedItemPosition()).getTaid());
        else
            personLogger.setQualification(-1);

        if(isUnderstandEnglish.equalsIgnoreCase("Yes"))
            personLogger.setEnglish("true");
        else
            personLogger.setEnglish("false");
        personLogger.setCurrentemployement(repalaceChar(currentEmploymentEdittext.getText().toString().trim()));
        //personLogger.setLengthofservice(Double.valueOf(serviceYearEdittext.getText().toString().trim()+"."+serviceMonthEdittext.getText().toString().trim()));
        personLogger.setLengthofservice(Double.valueOf(yearPicker.getValue()+"."+monthPicker.getValue()));
        personLogger.setHeightincms(Double.valueOf(userHeightEdittext.getText().toString().trim()));
        personLogger.setWeightinkgs(Double.valueOf(userWeightEdittext.getText().toString().trim()));
        personLogger.setWaist(Double.valueOf(userWaistEdittext.getText().toString().trim()));
        if(isHandicapped.equalsIgnoreCase("Yes"))
            personLogger.setIshandicapped("true");
        else
            personLogger.setIshandicapped("false");

        personLogger.setIdentificationmark(repalaceChar(userIdMark.getText().toString().trim()));
        personLogger.setPhysicalcondition(repalaceChar(userPhysicalConditionEdittext.getText().toString().trim()));
        personLogger.setReligion(repalaceChar(userReligion.getText().toString().trim()));
        personLogger.setCaste(repalaceChar(userCaste.getText().toString().trim()));
        personLogger.setMaritalstatus(maritalStatusSpinner.getSelectedItem().toString());
        personLogger.setGender(genderSpinner.getSelectedItem().toString());

        personLogger.setEnable("true"); //0 for false, 1 for true
        personLogger.setCuser(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        personLogger.setMuser(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        personLogger.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        personLogger.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        personLogger.setBuid(loginPreference.getLong(Constants.LOGIN_SITE_ID,-1));
        personLogger.setClientid(loginPreference.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
        personLoggerDAO.insertRecord(personLogger,"0");

        Toast.makeText(NewGuardReferenceEntryActivity.this, getResources().getString(R.string.ngentry_added_successfully_msg), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==RESULT_OK)
        {
            if(aadharFile.exists())
            {
                addImageToAttachmentTable();
            }
        }

        /*if(requestCode==0 && resultCode==RESULT_OK)
        {
            if(aadharFile.exists())
            {
                aadharCardIV.setImageURI(Uri.fromFile(aadharFile));
                addImageToAttachmentTable();
                aadharFile=null;
            }
        }
        else if(requestCode==1 && resultCode==RESULT_OK)
        {
            if(aadharFile.exists())
            {
                panCardIV.setImageURI(Uri.fromFile(aadharFile));
                addImageToAttachmentTable();
                aadharFile=null;
            }
        }
        else if(requestCode==2 && resultCode==RESULT_OK)
        {
            if(aadharFile.exists())
            {
                facePictureIV.setImageURI(Uri.fromFile(aadharFile));
                addImageToAttachmentTable();
                aadharFile=null;
            }
        }
        else if(requestCode==3 && resultCode==RESULT_OK)
        {
            if(aadharFile.exists())
            {
                fullPictureIV.setImageURI(Uri.fromFile(aadharFile));
                addImageToAttachmentTable();
                aadharFile=null;
            }
        }*/
    }

    private void addImageToAttachmentTable()
    {
        Attachment attachment=new Attachment();
        attachment.setAttachmentid(currentTimeInMillis);
        attachment.setFilePath(aadharFile.getPath().trim());
        attachment.setFileName(aadharFile.getName());
        attachment.setNarration(user_docNumber.getText().toString().trim());
        attachment.setGpslocation(deviceRelatedPref.getString(Constants.DEVICE_LATITUDE,"0.0")+","+deviceRelatedPref.getString(Constants.DEVICE_LONGITUDE,"0.0"));
        attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setCuser(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setMuser(loginPreference.getLong(Constants.LOGIN_PEOPLE_ID,-1));
        attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
        attachment.setAttachmentType(typeAssistDAO.getEventTypeID(Constants.ATTACHMENT_TYPE_ATTACHMENT, Constants.IDENTIFIER_ATTACHMENT));
        attachment.setOwnerid(currentTimeInMillis);
        attachment.setOwnername(typeAssistDAO.getEventTypeID(Constants.TACODE_PERSONLOGGER, Constants.IDENTIFIER_OWNER));
        attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+"peopleeventlog/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis()));
        attachment.setBuid(loginPreference.getLong(Constants.LOGIN_USER_CLIENT_ID,-1));
        attachmentDAO.insertCommonRecord(attachment);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        user_referedby_layout.setErrorEnabled(false);
        user_referedby_layout.setError(null);
        userFNameInputLayout.setErrorEnabled(false);
        userFNameInputLayout.setError(null);
        userMNameInputLayout.setErrorEnabled(false);
        userMNameInputLayout.setError(null);
        userLNameInputLayout.setErrorEnabled(false);
        userLNameInputLayout.setError(null);
        user_local_address_layout.setErrorEnabled(false);
        user_local_address_layout.setError(null);
        user_native_address_layout.setErrorEnabled(false);
        user_native_address_layout.setError(null);
        userMobileNumber.setError(null);
        userDOB.setError(null);
        userHeightEdittext.setError(null);
        userWaistEdittext.setError(null);
        userWeightEdittext.setError(null);
        user_identifcationmark_layout.setErrorEnabled(false);
        user_identifcationmark_layout.setError(null);
        user_religion_layout.setErrorEnabled(false);
        user_religion_layout.setError(null);
        user_caste_layout.setErrorEnabled(false);
        user_caste_layout.setError(null);
        userPhyConditionInputLayout.setErrorEnabled(false);
        userPhyConditionInputLayout.setError(null);



    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (group.getId())
        {
            case R.id.radioGroup:
                int chkRadioButton=group.getCheckedRadioButtonId();
                switch(chkRadioButton)
                {
                    case R.id.radio0:
                        isUnderstandEnglish=getResources().getString(R.string.radio_yes);
                        break;
                    case R.id.radio1:
                        isUnderstandEnglish=getResources().getString(R.string.radio_no);
                        break;
                }
                break;
            case R.id.handicappedRadioGroup:
                int chkRadioButton1=group.getCheckedRadioButtonId();
                switch(chkRadioButton1)
                {
                    case R.id.hRadio0:
                        isHandicapped=getResources().getString(R.string.radio_yes);
                        break;
                    case R.id.hRadio1:
                        isHandicapped=getResources().getString(R.string.radio_no);
                        break;
                }
                break;

        }

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        CommonFunctions.showSnack(NewGuardReferenceEntryActivity.this, isConnected,nextButton);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        /*register connection status listener*/
        Baseclass.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNegativeClickListener(int type, String errorMessage, String errorMessage2) {

    }

    @Override
    public void onPoistiveClickListener(int type, String errorMessage, String errorMessage2) {

    }
}
