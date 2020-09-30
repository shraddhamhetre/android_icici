package com.youtility.intelliwiz20.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.youtility.intelliwiz20.Activities.IncidentReportQuestionActivity;
import com.youtility.intelliwiz20.DataAccessObject.AttachmentDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.Model.QuestionAnswerTransaction;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.CommonFunctions;
import com.youtility.intelliwiz20.Utils.Constants;
import com.youtility.intelliwiz20.Utils.SignatureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

/**
 * Created by PrashantD on 28/08/17.
 */

public class JobNeedDetailsQuesAdapter extends ArrayAdapter<QuestionAnswerTransaction> implements TextView.OnEditorActionListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private Context context;
    private int resource;
    private QuestionDAO questionDAO;
    private TypeAssistDAO typeAssistDAO;
    private ArrayList<QuestionAnswerTransaction> data;
    private QuestionHolder questionHolder;
    private QuestionAnswerTransaction question;
    private SharedPreferences loginDetailPref;
    private CharSequence[]combVal;
    boolean[] itemsChecked ;
    private String comboValues=null;
    public JobNeedDetailsQuesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<QuestionAnswerTransaction> data) {
        super(context, resource, data);
        this.context=context;
        this.resource=resource;
        this.data=data;
        questionDAO=new QuestionDAO(context);
        typeAssistDAO=new TypeAssistDAO(context);
        loginDetailPref=context.getSharedPreferences(Constants.LOGIN_PREFERENCE, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);
        question=data.get(position);

        questionHolder=null;
        //if(convertView==null) {
            if (question != null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater(); //LayoutInflater.from(context);
                convertView = inflater.inflate(resource, null);
                questionHolder = new QuestionHolder();

                questionHolder.numericLinerLayout = (LinearLayout) convertView.findViewById(R.id.numericLinerLayout);
                questionHolder.singleLineLinearLayout = (LinearLayout) convertView.findViewById(R.id.singleLineLinerLayout);
                questionHolder.multiLineLinearLayout = (LinearLayout) convertView.findViewById(R.id.multiLineLinerLayout);
                questionHolder.spinnerLinearLayout = (LinearLayout) convertView.findViewById(R.id.spinnerLinerLayout);
                questionHolder.radioGroupLinearLayout = (LinearLayout) convertView.findViewById(R.id.radioLinerLayout);
                questionHolder.checkBoxLinearLayout = (LinearLayout) convertView.findViewById(R.id.checkBoxLinerLayout);
                questionHolder.signatureLinearLayout = (LinearLayout) convertView.findViewById(R.id.signatureLinerLayout);
                questionHolder.ratingLinearLayout = (LinearLayout) convertView.findViewById(R.id.ratingLinerLayout);
                questionHolder.dateLinearLayout = (LinearLayout) convertView.findViewById(R.id.dateLinerLayout);
                questionHolder.emailIDLinearLayout = (LinearLayout) convertView.findViewById(R.id.emailIdLinerLayout);
                questionHolder.timeLinearLayout = (LinearLayout) convertView.findViewById(R.id.timeLinerLayout);
                questionHolder.fCameraLinerLayout = (LinearLayout)convertView.findViewById(R.id.fCameraLinerLayout);
                questionHolder.bCameraLinerLayout = (LinearLayout)convertView.findViewById(R.id.bCameraLinerLayout);
                questionHolder.qrcodeLinearLayout=(LinearLayout)convertView.findViewById(R.id.qrcodeLinerLayout);

                questionHolder.radioGroup = (RadioGroup) convertView.findViewById(R.id.radioGroup);
                questionHolder.radioGroup.setId(position);
                questionHolder.radioGroup.clearCheck();
                questionHolder.radioGroup.setOnCheckedChangeListener(this);
                questionHolder.radio0 = (RadioButton) convertView.findViewById(R.id.radio0);
                questionHolder.radio1 = (RadioButton) convertView.findViewById(R.id.radio1);
                questionHolder.radio2 = (RadioButton) convertView.findViewById(R.id.radio2);
                questionHolder.radio0.setText("Yes");
                questionHolder.radio1.setText("No");
                questionHolder.radio2.setText("NA");

                if (question.getQuestAnswer() != null) {
                    if (question.getQuestAnswer().toString().trim().equalsIgnoreCase("Yes")) {
                        questionHolder.radio0.setChecked(true);
                    } else if (question.getQuestAnswer().toString().trim().equalsIgnoreCase("No")) {
                        questionHolder.radio1.setChecked(true);
                    } else if (question.getQuestAnswer().toString().trim().equalsIgnoreCase("NA")) {
                        questionHolder.radio2.setChecked(true);
                    }
                }

                questionHolder.qrcodeTextview=(TextView)convertView.findViewById(R.id.qrcodeTextView);
                questionHolder.qrcodeTextview.setId(position);
                questionHolder.qrcodeTextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((IncidentReportQuestionActivity)context).captureQrCode(position);
                    }
                });


                questionHolder.checkboxButton = (Button) convertView.findViewById(R.id.multipleChoiceButton);

                questionHolder.signatureImageView = (ImageView) convertView.findViewById(R.id.drawing);
                questionHolder.signatureImageView.setId(position);
                questionHolder.signatureImageView.setOnClickListener(this);

                //--------------------------------------------------------------------------back camera view------------------------------------------------
                questionHolder.bCameraImageView= (ImageView)convertView.findViewById(R.id.bCameraQtype);
                questionHolder.bCameraImageView.setId(position);
                questionHolder.bCameraImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("Hello back camerea-----------------------------------------------------"+position);
                        ((IncidentReportQuestionActivity)context).captureImage(position,0);
                    }
                });


                //----------------------------------------------------------------------------front camera view------------------------------------------------
                questionHolder.fCameraImageView = (ImageView)convertView.findViewById(R.id.fCameraQtype);
                questionHolder.fCameraImageView.setId(position);
                questionHolder.fCameraImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("Hello front camerea-----------------------------------------------------");
                        ((IncidentReportQuestionActivity)context).captureImage(position,1);
                    }
                });


                //---------------------------------------------------------------------------question name text-------------------------------------------------
                questionHolder.quetionNameTextview = (TextView) convertView.findViewById(R.id.qNameTextView);
                String quName = questionDAO.getQuestionName(question.getQuestionid());
                questionHolder.quetionNameTextview.setText(quName + "");

                if (question.getIsmandatory().equalsIgnoreCase("true"))
                    questionHolder.quetionNameTextview.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.asterisk, 0, 0, 0);
                else
                    questionHolder.quetionNameTextview.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

                questionHolder.translateQuest=(ImageView)convertView.findViewById(R.id.qRemarkIV);
                questionHolder.translateQuest.setId(position);
                questionHolder.translateQuest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isAppInstalled = appInstalledOrNot("com.google.android.apps.translate");
                        if(isAppInstalled)
                        {
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_SEND);
                            i.setComponent(new ComponentName("com.google.android.apps.translate", "com.google.android.apps.translate.TranslateActivity"));
                            context.startActivity(i);
                        }
                        else
                        {
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.translate"));
                            context.startActivity(i);
                        }
                    }
                });

                //---------------------------------------------------------------------------numeric values-------------------------------------------------
                questionHolder.numericEditText = (EditText) convertView.findViewById(R.id.numericEdittext);
                questionHolder.numericEditText.setId(position);
            /*questionHolder.numericEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    EditText ed=(EditText)view;
                    try {
                        if(ed.getText()!=null && ed.getText().toString().trim().length()>0) {
                            if (Double.parseDouble(ed.getText().toString().trim()) >= (data.get(position).getMin()) && Double.parseDouble(ed.getText().toString().trim()) <= (data.get(position).getMax())) {
                                data.get(position).setQuestAnswer(ed.getText().toString().trim());
                            } else {
                                ed.setError(context.getResources().getString(R.string.fill_value_in_properrange_msg));

                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });*/
                questionHolder.numericEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                questionHolder.numericEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                questionHolder.numericEditText.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);
                questionHolder.numericEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        try
                        {
                            EditText editText=(EditText)v;
                            if(actionId==EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_UNSPECIFIED)
                            {
                                if(v.getText().toString().trim().length()>0)
                                {
                                    if(isNumeric(v.getText().toString().trim()))
                                    {
                                        if (Double.parseDouble(v.getText().toString().trim()) >= (data.get(position).getMin()) && Double.parseDouble(v.getText().toString().trim()) <= (data.get(position).getMax()))
                                        {
                                            data.get(position).setQuestAnswer(v.getText().toString().trim());
                                            data.get(position).setCorrect(true);
                                        }
                                        else
                                        {
                                            data.get(position).setQuestAnswer(v.getText().toString().trim());
                                            data.get(position).setCorrect(false);
                                            Toasty.warning(context, context.getResources().getString(R.string.fill_value_in_properrange_msg), Toast.LENGTH_LONG, true).show();
                                            //Toast.makeText(context,context.getResources().getString(R.string.fill_value_in_properrange_msg), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        data.get(position).setQuestAnswer("0.0");
                                        data.get(position).setCorrect(false);
                                        //Toast.makeText(context,context.getResources().getString(R.string.fill_value_enternumval_msg), Toast.LENGTH_SHORT).show();
                                        Toasty.error(context, context.getResources().getString(R.string.fill_value_enternumval_msg), Toast.LENGTH_LONG, true).show();
                                    }
                                }
                                else
                                {
                                    if(data.get(position).getIsmandatory().equalsIgnoreCase("true"))
                                        data.get(position).setCorrect(false);
                                    else if(data.get(position).getIsmandatory().equalsIgnoreCase("false"))
                                        data.get(position).setCorrect(true);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
                //questionHolder.numericEditText.setOnEditorActionListener(this);

                questionHolder.numericEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        try {
                            if(charSequence.toString().trim().length()>0)
                            {
                                if(isNumeric(charSequence.toString().trim()))
                                {
                                    if (Double.parseDouble(charSequence.toString().trim()) >= (data.get(position).getMin()) && Double.parseDouble(charSequence.toString().trim()) <= (data.get(position).getMax()))
                                    {
                                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                                        data.get(position).setCorrect(true);
                                    }
                                    else
                                    {
                                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                                        data.get(position).setCorrect(false);
                                        //Toast.makeText(context,context.getResources().getString(R.string.fill_value_in_properrange_msg), Toast.LENGTH_SHORT).show();
                                        Toasty.warning(context, context.getResources().getString(R.string.fill_value_in_properrange_msg), Toast.LENGTH_LONG, true).show();
                                    }
                                }
                                else
                                {
                                    data.get(position).setQuestAnswer("0.0");
                                    data.get(position).setCorrect(false);
                                    //Toast.makeText(context,context.getResources().getString(R.string.fill_value_enternumval_msg), Toast.LENGTH_SHORT).show();
                                    Toasty.error(context, context.getResources().getString(R.string.fill_value_enternumval_msg), Toast.LENGTH_LONG, true).show();
                                }
                            }
                            else
                            {
                                if(data.get(position).getIsmandatory().equalsIgnoreCase("true"))
                                    data.get(position).setCorrect(false);
                                else if(data.get(position).getIsmandatory().equalsIgnoreCase("false"))
                                    data.get(position).setCorrect(true);
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        /*try {
                            if (charSequence.toString().trim().length() > 0) {
                                if (!charSequence.toString().trim().equalsIgnoreCase("NA") || !charSequence.toString().trim().equalsIgnoreCase("null")) {
                                    if (isNumeric(charSequence.toString().trim())) {
                                        if (Double.parseDouble(charSequence.toString().trim()) >= (data.get(position).getMin()) && Double.parseDouble(charSequence.toString().trim()) <= (data.get(position).getMax())) {
                                            data.get(position).setQuestAnswer(charSequence.toString().trim());
                                        } else {
                                            Toast.makeText(context, context.getResources().getString(R.string.fill_value_in_properrange_msg), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        //questionHolder.numericEditText.setText("");
                                        Toast.makeText(context, "Please enter number value", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }*/
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                //---------------------------------------------------------------------------singleline values-------------------------------------------------
                questionHolder.singleLineEditText = (EditText) convertView.findViewById(R.id.singleLineEdittext);
                questionHolder.singleLineEditText.setId(position);
                questionHolder.singleLineEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                questionHolder.singleLineEditText.setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT);

                questionHolder.singleLineEdittextCharCount=(TextView) convertView.findViewById(R.id.singleLineEdittextCharCount);

                questionHolder.singleLineEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().trim().length() > 0) {
                            data.get(position).setQuestAnswer(charSequence.toString().trim());
                            data.get(position).setCorrect(true);
                        }
                        else
                        {
                            if(data.get(position).getIsmandatory().equalsIgnoreCase("true"))
                                data.get(position).setCorrect(false);
                            else if(data.get(position).getIsmandatory().equalsIgnoreCase("false"))
                                data.get(position).setCorrect(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        //questionHolder.singleLineEdittextCharCount.setText(250-editable.toString().length()+"/250");
                        //showToast(questionHolder.singleLineEditText,context.getString(R.string.fill_value_charremains,250-editable.toString().trim().length()));
                    }
                });

                //---------------------------------------------------------------------------multiline values-------------------------------------------------
                questionHolder.multiLineEditText = (EditText) convertView.findViewById(R.id.multiLineEdittext);
                questionHolder.multiLineEditText.setId(position);

                questionHolder.multiLineEdittextCharCount=(TextView)convertView.findViewById(R.id.multiLineEdittextCharCount);

                questionHolder.multiLineEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().trim().length() > 0) {
                            data.get(position).setQuestAnswer(charSequence.toString().trim());
                            data.get(position).setCorrect(true);
                        }
                        else
                        {
                            if(data.get(position).getIsmandatory().equalsIgnoreCase("true"))
                                data.get(position).setCorrect(false);
                            else if(data.get(position).getIsmandatory().equalsIgnoreCase("false"))
                                data.get(position).setCorrect(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        //questionHolder.multiLineEdittextCharCount.setText(250-editable.toString().length()+"/250");
                        //showToast(questionHolder.multiLineEditText,context.getString(R.string.fill_value_charremains,250-editable.toString().trim().length()));
                    }
                });

                //---------------------------------------------------------------------------dropbox values-------------------------------------------------
                questionHolder.spinner = (Spinner) convertView.findViewById(R.id.spinner);
                questionHolder.spinner.setId(position);
                questionHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        data.get(position).setQuestAnswer(adapterView.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                addItemToSpinner(position);

                //---------------------------------------------------------------------------rating values-------------------------------------------------
                questionHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                questionHolder.ratingBar.setId(position);
                questionHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        data.get(position).setQuestAnswer("" + ratingBar.getRating());
                    }
                });

                //---------------------------------------------------------------------------date values-------------------------------------------------
                questionHolder.dateEditText = (TextView) convertView.findViewById(R.id.dateEdittext);
                questionHolder.dateEditText.setId(position);
                questionHolder.dateEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TextView vv = (TextView) v;
                        Calendar mcurrentDate = Calendar.getInstance();
                        DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCal = Calendar.getInstance();
                                myCal.set(Calendar.YEAR, selectedyear);
                                myCal.set(Calendar.MONTH, selectedmonth);
                                myCal.set(Calendar.DAY_OF_MONTH, selectedday);

                                String myFormat = "dd MMM yyyy"; //In which you need put here
                                Locale locale = new Locale("en");
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, locale);
                                vv.setText(sdf.format(myCal.getTime()));

                                data.get(position).setQuestAnswer(sdf.format(myCal.getTime()));
                                data.get(position).setCorrect(true);

                            }
                        }, mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                        mDatePicker.setTitle(context.getResources().getString(R.string.fill_value_selectdate_title));
                        mDatePicker.show();
                    }
                });
                /*questionHolder.dateEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().trim().length() > 0) {
                            data.get(position).setQuestAnswer(charSequence.toString().trim());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });*/

                //---------------------------------------------------------------------------email values-------------------------------------------------
                questionHolder.emailIDEditText = (EditText) convertView.findViewById(R.id.emailIDEdittext);
                questionHolder.emailIDEditText.setId(position);
                questionHolder.emailIDEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                questionHolder.emailIDEditText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
                questionHolder.emailIDEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        EditText ed = (EditText) v;
                        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                            if (ed.getText().toString().trim().length() > 0) {
                                if (CommonFunctions.emailValidator(ed.getText().toString().trim())) {
                                    data.get(position).setQuestAnswer(ed.getText().toString().trim());
                                    data.get(position).setCorrect(true);
                                } else {
                                    data.get(position).setCorrect(false);
                                    ed.setError(context.getResources().getString(R.string.fill_value_emailid_msg));
                                }
                            }
                            else
                            {
                                if(data.get(position).getIsmandatory().equalsIgnoreCase("true"))
                                    data.get(position).setCorrect(false);
                                else if(data.get(position).getIsmandatory().equalsIgnoreCase("false"))
                                    data.get(position).setCorrect(true);
                            }
                        }
                        return false;
                    }
                });
                questionHolder.emailIDEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(charSequence.toString().trim().length()>0)
                        {
                            if(CommonFunctions.emailValidator(charSequence.toString().trim()))
                            {
                                data.get(position).setCorrect(true);
                                data.get(position).setQuestAnswer(charSequence.toString().trim());
                            }
                            else
                            {
                                data.get(position).setCorrect(false);
                            }

                        }
                        else
                        {
                            if(data.get(position).getIsmandatory().equalsIgnoreCase("true"))
                                data.get(position).setCorrect(false);
                            else if(data.get(position).getIsmandatory().equalsIgnoreCase("false"))
                                data.get(position).setCorrect(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                //------------------------------------------------------------------------------time values-------------------------------------------------
                questionHolder.timeEditText = (TextView) convertView.findViewById(R.id.timeEdittext);
                questionHolder.timeEditText.setId(position);
                questionHolder.timeEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final TextView vv = (TextView) v;
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                vv.setText(selectedHour + ":" + selectedMinute);
                                data.get(position).setQuestAnswer(selectedHour + ":" + selectedMinute);
                                data.get(position).setCorrect(true);
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle(context.getResources().getString(R.string.fill_value_selecttime_title));
                        mTimePicker.show();
                    }
                });

                //-----------------------------------------------------------------------Check box values-------------------------------------------------------------

                questionHolder.checkboxButton = (Button) convertView.findViewById(R.id.multipleChoiceButton);
                questionHolder.checkboxButton.setId(position);
                questionHolder.checkboxButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(v);
                    }
                });

                if (question.getType() == 0 || question.getType() == -1) {
                    System.out.println("question.getType(): " + question.getType());
                } else {
                    switch (typeAssistDAO.getEventTypeCode(question.getType())) {

                        case "NUMERIC":
                            questionHolder.numericEditText.setHint("(" + question.getMin() + "-" + question.getMax() + ")");
                            questionHolder.numericLinerLayout.setVisibility(View.VISIBLE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "SINGLELINE":
                        case "EDITTEXT":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "MULTILINE":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "DROPDOWN":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "YESNONA":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "CHECKBOX":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "SIGNATURE":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "RATING":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "DATE":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "EMAILID":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "TIME":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.VISIBLE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "FRONTCAMERA":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.VISIBLE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "BACKCAMERA":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.VISIBLE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;
                        case "QRCODE":
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.VISIBLE);
                            break;
                        default:
                            questionHolder.numericLinerLayout.setVisibility(View.GONE);
                            questionHolder.singleLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.multiLineLinearLayout.setVisibility(View.GONE);
                            questionHolder.spinnerLinearLayout.setVisibility(View.GONE);
                            questionHolder.radioGroupLinearLayout.setVisibility(View.GONE);
                            questionHolder.checkBoxLinearLayout.setVisibility(View.GONE);
                            questionHolder.signatureLinearLayout.setVisibility(View.GONE);
                            questionHolder.ratingLinearLayout.setVisibility(View.GONE);
                            questionHolder.dateLinearLayout.setVisibility(View.GONE);
                            questionHolder.emailIDLinearLayout.setVisibility(View.GONE);
                            questionHolder.timeLinearLayout.setVisibility(View.GONE);
                            questionHolder.fCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.bCameraLinerLayout.setVisibility(View.GONE);
                            questionHolder.qrcodeLinearLayout.setVisibility(View.GONE);
                            break;

                    }
                }


                if (question.getQuestAnswer().trim().length() > 0 && !data.get(position).getQuestAnswer().trim().equalsIgnoreCase("null")) {
                    switch (typeAssistDAO.getEventTypeCode(question.getType())) {
                        case "NUMERIC":
                            if (data.get(position).getQuestAnswer().trim().equalsIgnoreCase("NA"))
                                questionHolder.numericEditText.setText("");
                            else
                                questionHolder.numericEditText.setText(data.get(position).getQuestAnswer().trim());
                            break;
                        case "SINGLELINE":
                        case "EDITTEXT":
                            questionHolder.singleLineEditText.setText(data.get(position).getQuestAnswer().trim());
                            break;
                        case "MULTILINE":
                            questionHolder.multiLineEditText.setText(data.get(position).getQuestAnswer().trim());
                            break;
                        case "DROPDOWN":
                            break;
                        case "YESNONA":
                            break;
                        case "CHECKBOX":
                            break;
                        case "SIGNATURE":
                            File image = new File(data.get(position).getImagePath());
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                            questionHolder.signatureImageView.setImageBitmap(bitmap);
                            break;
                        case "RATING":
                            questionHolder.ratingBar.setRating(Float.valueOf(data.get(position).getQuestAnswer()));
                            break;
                        case "DATE":
                            questionHolder.dateEditText.setText(data.get(position).getQuestAnswer().trim());
                            break;
                        case "EMAILID":
                            questionHolder.emailIDEditText.setText(data.get(position).getQuestAnswer().trim());
                            break;
                        case "TIME":
                            questionHolder.timeEditText.setText(data.get(position).getQuestAnswer().trim());
                            break;
                        case "FRONTCAMERA":
                            try {
                            Bitmap icon=null;
                            File imgFile = new File(data.get(position).getImagePath());
                            if (imgFile.exists()) {
                                icon = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                questionHolder.fCameraImageView.setImageBitmap(icon);
                            }else{
                                System.out.println("FRONTCAMERA");
                            }
                            }catch (RuntimeException e){
                                System.out.println("FRONTCAMERA");

                            }

                            break;
                        case "BACKCAMERA":
                            try {
                                Bitmap icon1 = null;
                                File imgFile1 = new File(data.get(position).getImagePath());
                                if (imgFile1.exists()) {
                                    icon1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                                    questionHolder.bCameraImageView.setImageBitmap(icon1);
                                } else {
                                    System.out.println("BACKCAMERA");
                                }
                            }catch (RuntimeException e){
                                System.out.println("BACKCAMERA");

                            }

                            break;
                        case "QRCODE":
                            questionHolder.qrcodeTextview.setText(data.get(position).getQuestAnswer().trim());
                            break;
                    }
                }


                convertView.setTag(questionHolder);
            }
        /*}
        else
        {
            questionHolder=(QuestionHolder)convertView.getTag();
        }*/



        return  convertView;
    }


    private boolean isNumeric(String str)
    {
        //return str.matches("-?\\d+(.\\d+)?");
        return str.matches("^(?:(?:\\-{1})?\\d+(?:\\.{1}\\d+)?)$");
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public ArrayList<QuestionAnswerTransaction> getData()
    {
        return data;
    }


    private void showDialog(final View v)
    {
        final Button testBtn=(Button) v;
        if(data.get(v.getId()).getOptions()!=null)
            comboValues=(data.get(v.getId())).getOptions();


        String[] splitVal=comboValues.split(",");
        System.out.println("splitVal length: "+splitVal.length);
        combVal=new String[splitVal.length];
        for(int k=0;k<(splitVal.length);k++)
        {
            combVal[k]=splitVal[k];
            System.out.println("splitVal: "+k+" : "+combVal[k]);
        }

        //combVal=comboValues.split(",");
        System.out.println("combVal.length: "+combVal.length);

        itemsChecked = new boolean[combVal.length];
        boolean[] testBoolean;
        if((data.get(v.getId())).getQuestAnswer()!=null)
        {
            String[] feedback=(data.get(v.getId())).getQuestAnswer().split(",");
            testBoolean=new boolean[combVal.length];
            for(int ii=0;ii<combVal.length;ii++)
            {
                for(int jj=0;jj<feedback.length;jj++)
                {
                    System.out.println("compair: "+combVal[ii]+" : "+feedback[jj]);
                    if(combVal[ii].equals(feedback[jj]))
                    {
                        testBoolean[ii]=true;
                        break;
                    }
                }
            }
        }
        else
        {
            testBoolean=new boolean[combVal.length];
            for(int kk=0;kk<testBoolean.length;kk++)
            {
                testBoolean[kk]=false;
            }
        }

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.fill_value_multipleoption_title));
        builder.setPositiveButton(context.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                int icon=-2;
                String selectedTech="";
                for (int i = 0; i < (combVal.length); i++)
                {
                    if (itemsChecked[i])
                    {
                        selectedTech=selectedTech+combVal[i]+",";
                        //selectedTech=selectedTech+i+",";
                        itemsChecked[i]=false;
                    }
                }
                if(selectedTech!=null && selectedTech.length()>0)
                {
                    selectedTech=selectedTech.substring(0, (selectedTech.length()-1));
                    System.out.println("Selcted Values: "+selectedTech);
                    (data.get(v.getId())).setQuestAnswer(selectedTech);
                }
            }
        });
        //new boolean[]{false,false,false}
        builder.setMultiChoiceItems(combVal, testBoolean, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                itemsChecked[which]=isChecked;
            }
        });
        builder.show();
    }


    private void addItemToSpinner(int position)
    {
        List<String> list = new ArrayList<String>();
        if(data.get(position).getOptions()!=null)
        {
            String[] spinnVal=data.get(position).getOptions().toString().trim().split(",");
            for(int i=0;i<spinnVal.length;i++)
            {
                list.add(spinnVal[i].trim());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        questionHolder.spinner.setAdapter(dataAdapter);

        if(data.get(position).getQuestAnswer()!=null)
        {
            int assignToPosition=-1;
            for(int i=0;i<list.size();i++)
            {
                if(list.get(i).equalsIgnoreCase(question.getQuestAnswer().toString()))
                {
                    assignToPosition=i;
                }
            }
            questionHolder.spinner.setSelection(assignToPosition);
        }

    }


    @Override
    public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
        EditText ed=(EditText)textView;
        if(actionid==EditorInfo.IME_ACTION_DONE)
        {
            ed.setFocusable(false);
            InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE); // use for ui related issue
            imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        int chkradioButton=radioGroup.getCheckedRadioButtonId();
        switch(chkradioButton)
        {
            case R.id.radio0:
                data.get(radioGroup.getId()).setQuestAnswer("Yes");
                break;
            case R.id.radio1:
                data.get(radioGroup.getId()).setQuestAnswer("No");
                break;
            case R.id.radio2:
                data.get(radioGroup.getId()).setQuestAnswer("NA");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        showAlertForSignature(view);
    }

    private void showAlertForSignature(final View view)
    {

        System.out.println("Signature buid JobneedDetails: "+ question.getBuid());
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.question_signature_type_view, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final SignatureView dr=(SignatureView)promptsView.findViewById(R.id.drawing);
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.button_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dr.setDrawingCacheEnabled(true);
                Bitmap bm = dr.getDrawingCache();
                data.get(view.getId()).setQuestAnswer("Done");

                File fPath = Environment.getExternalStorageDirectory();
                File f = null;
                String randomString=null;
                randomString= CommonFunctions.getFileNameFromDate(System.currentTimeMillis());
                f = new File(fPath+"/"+ Constants.FOLDER_NAME+"/"+ Constants.ATTACHMENT_FOLDER_NAME+"/"+randomString + ".png");
                try {
                    FileOutputStream strm = new FileOutputStream(f);
                    bm.compress(Bitmap.CompressFormat.PNG, 80, strm);
                    strm.close();

                    Attachment attachment=new Attachment();
                    attachment.setAttachmentid(question.getJobneedid());
                    attachment.setAttachmentType(typeAssistDAO.getEventTypeID("SIGN", Constants.IDENTIFIER_ATTACHMENT));
                    attachment.setFilePath(f.getPath());
                    attachment.setFileName(randomString+".png");
                    attachment.setNarration("");
                    attachment.setGpslocation("19,19");
                    attachment.setDatetime(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    attachment.setCuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    attachment.setCdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    attachment.setMuser(loginDetailPref.getLong(Constants.LOGIN_PEOPLE_ID,-1));
                    attachment.setMdtz(CommonFunctions.getTimezoneDate(System.currentTimeMillis()));
                    /*attachment.setOwnername(typeAssistDAO.getEventTypeIDForAdhoc("JOB"));//need to pass table name according to
                    attachment.setOwnerid(question.getJobneedid());//need to pass jobneedid/peopleeventlogid
                    attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+"JOB"+"/"+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+"/");*/

                    attachment.setOwnername(typeAssistDAO.getEventTypeID(question.getParentActivity().toUpperCase(),Constants.IDENTIFIER_OWNER));//need to pass table name according to
                    attachment.setOwnerid(question.getJobneedid());//need to pass jobneedid/peopleeventlogid
                    attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+question.getParentActivity()+"/"+question.getParentFolder()+"/");

                    attachment.setBuid(question.getBuid());

                    AttachmentDAO attachmentDAO=new AttachmentDAO(context);
                    attachmentDAO.insertCommonRecord(attachment);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                File image = new File(f.getPath());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 150 , 150, true);
                ((ImageView)view).setImageBitmap(bitmap);
                data.get(view.getId()).setImagePath(f.getPath());

                if (bm != null && !bm.isRecycled()) {
                    bm.recycle();
                    bm = null;
                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog=alertDialogBuilder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                dr.reset();
            }
        });
    }

    private Toast mToastToShow;
    public void showToast(View view, String ss) {
        // Set the toast and duration
        int toastDurationInMilliSeconds = 10;
        mToastToShow = Toast.makeText(context, ss, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 10 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }
            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        mToastToShow.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        View view1=mToastToShow.getView();
        view1.setBackgroundColor(context.getResources().getColor(R.color.button_background));
        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();

    }

    public static class QuestionHolder
    {
        LinearLayout numericLinerLayout;
        LinearLayout singleLineLinearLayout;
        LinearLayout multiLineLinearLayout;
        LinearLayout spinnerLinearLayout;
        LinearLayout radioGroupLinearLayout;
        LinearLayout checkBoxLinearLayout;
        LinearLayout ratingLinearLayout;
        LinearLayout signatureLinearLayout;
        LinearLayout dateLinearLayout;
        LinearLayout emailIDLinearLayout;
        LinearLayout timeLinearLayout;
        LinearLayout fCameraLinerLayout;
        LinearLayout bCameraLinerLayout;
        LinearLayout qrcodeLinearLayout;

        EditText numericEditText;
        EditText singleLineEditText;
        EditText multiLineEditText;
        Spinner spinner;
        RadioGroup radioGroup;
        RadioButton radio0;
        RadioButton radio1;
        RadioButton radio2;
        Button checkboxButton;
        ImageView signatureImageView;
        RatingBar ratingBar;
        TextView dateEditText;
        EditText emailIDEditText;
        TextView timeEditText;
        ImageView fCameraImageView;
        ImageView bCameraImageView;
        TextView qrcodeTextview;

        Button commentButton;
        Button audioButton;
        Button videoButton;
        Button pictureButton;

        TextView quetionNameTextview;
        TextView singleLineEdittextCharCount;
        TextView multiLineEdittextCharCount;

        ImageView translateQuest;

    }

    public void setImageInItem(int position, String imagePath) {

        System.out.println("setImageInItem position: "+position);
        System.out.println("setImageInItem imagePath: "+imagePath);

        data.get(position).setQuestAnswer("Done");
        data.get(position).setImagePath(imagePath);

        /*GetSet dataSet = (GetSet) _data.get(position);
        dataSet.setImage(imageSrc);
        dataSet.setStatus(false);
        dataSet.setHaveImage(true);*/
        //((ImageView)imgView).setImageBitmap(imageSrc);
        notifyDataSetChanged();
    }
}
