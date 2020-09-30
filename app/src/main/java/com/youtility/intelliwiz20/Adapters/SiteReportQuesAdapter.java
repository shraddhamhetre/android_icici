package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.ParentSection;
import com.youtility.intelliwiz20.R;
import com.youtility.intelliwiz20.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by PrashantD on 28/08/17.
 */

public class SiteReportQuesAdapter extends ArrayAdapter<ParentSection> implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private Context context;
    private int resource;
    private ArrayList<ParentSection> data;
    private ParentSection parentSection;
    private QuestionHolder questionHolder;
    private QuestionDAO questionDAO;
    private TypeAssistDAO typeAssistDAO;
    //private QuestionAnswerTransaction question;
    private SharedPreferences loginDetailPref;
    private CharSequence[]combVal;
    boolean[] itemsChecked ;
    private String comboValues=null;
    public SiteReportQuesAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<ParentSection> data) {
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
        parentSection=data.get(0);
        questionHolder=null;
        //if(question!=null && !(question.getQuestionsetName().toString().trim().length()>0))
        if(parentSection!=null && (parentSection.getChildSectionArrayList().size()>0))
        {
            /*LayoutInflater inflater = ((Activity)context).getLayoutInflater(); //LayoutInflater.from(context);
            convertView= inflater.inflate(resource,null);
            questionHolder = new QuestionHolder();*/

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView= (LinearLayout) inflater.inflate(R.layout.activity_incident_report_questionset_name_row,null);
            questionHolder = new QuestionHolder();

            questionHolder.questionSetNameTextview=(TextView) convertView.findViewById(R.id.qsetNameTextview);

            questionHolder.questionSetNameTextview.setText(""+parentSection.getqSetName());
            questionHolder.questionSetNameTextview.setBackgroundColor(context.getResources().getColor(R.color.screen_background_color));




            /*questionHolder.numericLinerLayout=(LinearLayout)convertView.findViewById(R.id.numericLinerLayout);
            questionHolder.singleLineLinearLayout=(LinearLayout)convertView.findViewById(R.id.singleLineLinerLayout);
            questionHolder.multiLineLinearLayout=(LinearLayout)convertView.findViewById(R.id.multiLineLinerLayout);
            questionHolder.spinnerLinearLayout=(LinearLayout)convertView.findViewById(R.id.spinnerLinerLayout);
            questionHolder.radioGroupLinearLayout=(LinearLayout)convertView.findViewById(R.id.radioLinerLayout);
            questionHolder.checkBoxLinearLayout=(LinearLayout)convertView.findViewById(R.id.checkBoxLinerLayout);
            questionHolder.signatureLinearLayout=(LinearLayout)convertView.findViewById(R.id.signatureLinerLayout);
            questionHolder.ratingLinearLayout=(LinearLayout)convertView.findViewById(R.id.ratingLinerLayout);
            questionHolder.dateLinearLayout=(LinearLayout)convertView.findViewById(R.id.dateLinerLayout);
            questionHolder.emailIDLinearLayout=(LinearLayout)convertView.findViewById(R.id.emailIdLinerLayout);
            questionHolder.timeLinearLayout=(LinearLayout)convertView.findViewById(R.id.timeLinerLayout);

            questionHolder.radioGroup=(RadioGroup)convertView.findViewById(R.id.radioGroup);
            questionHolder.radioGroup.setId(position);
            questionHolder.radioGroup.clearCheck();
            questionHolder.radioGroup.setOnCheckedChangeListener(this);
            questionHolder.radio0=(RadioButton)convertView.findViewById(R.id.radio0);
            questionHolder.radio1=(RadioButton)convertView.findViewById(R.id.radio1);
            questionHolder.radio2=(RadioButton)convertView.findViewById(R.id.radio2);
            questionHolder.radio0.setText("Yes");
            questionHolder.radio1.setText("No");
            questionHolder.radio2.setText("NA");

            if(question.getQuestAnswer()!=null) {
                if (question.getQuestAnswer().toString().trim().equalsIgnoreCase("Yes")) {
                    questionHolder.radio0.setChecked(true);
                } else if (question.getQuestAnswer().toString().trim().equalsIgnoreCase("No")) {
                    questionHolder.radio1.setChecked(true);
                } else if (question.getQuestAnswer().toString().trim().equalsIgnoreCase("NA")) {
                    questionHolder.radio2.setChecked(true);
                }
            }

            questionHolder.checkboxButton=(Button)convertView.findViewById(R.id.multipleChoiceButton);

            questionHolder.questionRemarkImageView=(ImageView)convertView.findViewById(R.id.qRemarkIV);
            questionHolder.questionRemarkImageView.setId(position);
            questionHolder.questionRemarkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //---------------------------------------------------------------------------signature view-------------------------------------------------
            questionHolder.signatureImageView=(ImageView)convertView.findViewById(R.id.drawing);
            questionHolder.signatureImageView.setId(position);
            questionHolder.signatureImageView.setOnClickListener(this);

            //---------------------------------------------------------------------------question name text-------------------------------------------------
            questionHolder.quetionNameTextview=(TextView)convertView.findViewById(R.id.qNameTextView);
            String quName=questionDAO.getQuestionName(question.getQuestionid());
            questionHolder.quetionNameTextview.setText(quName+"");

            if(question.getIsmandatory().equalsIgnoreCase("true"))
                questionHolder.quetionNameTextview.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.asterisk,0,0,0);
            else
                questionHolder.quetionNameTextview.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);


            //---------------------------------------------------------------------------numeric values-------------------------------------------------
            questionHolder.numericEditText=(EditText)convertView.findViewById(R.id.numericEdittext);
            questionHolder.numericEditText.setId(position);
            questionHolder.numericEditText.setHint(data.get(position).getMin()+" - "+data.get(position).getMax());
            questionHolder.numericEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int actionid, KeyEvent keyEvent) {
                    EditText ed=(EditText)view;
                    if(actionid==EditorInfo.IME_ACTION_DONE || actionid==EditorInfo.IME_ACTION_UNSPECIFIED)
                    {

                        try {
                            if(ed.getText().toString()!=null && ed.getText().toString().trim().length()>0) {
                                if(data.get(position).getMin()!=0.0 && data.get(position).getMax()!=0.0) {
                                    if (Double.parseDouble(ed.getText().toString().trim()) >= (data.get(position).getMin()) && Double.parseDouble(ed.getText().toString().trim()) <= (data.get(position).getMax())) {
                                        data.get(position).setQuestAnswer(ed.getText().toString().trim());
                                    } else {
                                        ed.setError(context.getResources().getString(R.string.fill_value_in_properrange_msg));

                                    }
                                }
                                else
                                {
                                    data.get(position).setQuestAnswer(ed.getText().toString().trim());
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        }


                        InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE); // use for ui related issue
                        imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
                    }

                    return false;
                }
            });
            questionHolder.numericEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            questionHolder.numericEditText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
            //questionHolder.numericEditText.setOnEditorActionListener(this);

            questionHolder.numericEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    try {
                        if(charSequence.toString().trim().length()>0) {
                            if(data.get(position).getMin()!=0.0 && data.get(position).getMax()!=0.0) {
                                if ((Double.parseDouble(charSequence.toString().trim()) >= (data.get(position).getMin()) && Double.parseDouble(charSequence.toString().trim()) <= (data.get(position).getMax()))) {
                                    data.get(position).setQuestAnswer(charSequence.toString().trim());
                                } else {
                                    data.get(position).setQuestAnswer("0.0");
                                    Toast.makeText(context,context.getResources().getString(R.string.fill_value_in_properrange_msg), Toast.LENGTH_SHORT).show();

                                }
                            }
                            else
                            {
                                data.get(position).setQuestAnswer(charSequence.toString().trim());
                            }

                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //---------------------------------------------------------------------------singleline values-------------------------------------------------
            questionHolder.singleLineEditText=(EditText)convertView.findViewById(R.id.singleLineEdittext);
            questionHolder.singleLineEditText.setId(position);
            questionHolder.singleLineEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            questionHolder.singleLineEditText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
            questionHolder.singleLineEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().trim().length()>0)
                    {
                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //---------------------------------------------------------------------------multiline values-------------------------------------------------
            questionHolder.multiLineEditText=(EditText)convertView.findViewById(R.id.multiLineEdittext);
            questionHolder.multiLineEditText.setId(position);
            questionHolder.multiLineEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().trim().length()>0)
                    {
                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //---------------------------------------------------------------------------dropbox values-------------------------------------------------
            questionHolder.spinner=(Spinner)convertView.findViewById(R.id.spinner);
            questionHolder.spinner.setId(position);
            questionHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    System.out.println("Spinner prev values: "+data.get(position).getQuestAnswer());
                    System.out.println("Spinner cur values: "+adapterView.getSelectedItem().toString());
                    data.get(position).setQuestAnswer(adapterView.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            addItemToSpinner(position);

            //---------------------------------------------------------------------------rating values-------------------------------------------------
            questionHolder.ratingBar=(RatingBar)convertView.findViewById(R.id.ratingBar);
            questionHolder.ratingBar.setId(position);
            questionHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    data.get(position).setQuestAnswer(""+(int)ratingBar.getRating());
                }
            });

            //---------------------------------------------------------------------------date values-------------------------------------------------
            questionHolder.dateEditText=(EditText)convertView.findViewById(R.id.dateEdittext);
            questionHolder.dateEditText.setId(position);
            questionHolder.dateEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText vv=(EditText)v;
                    Calendar mcurrentDate=Calendar.getInstance();
                    DatePickerDialog mDatePicker=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
                    {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                        {
                            Calendar myCal=Calendar.getInstance();
                            myCal.set(Calendar.YEAR, selectedyear);
                            myCal.set(Calendar.MONTH, selectedmonth);
                            myCal.set(Calendar.DAY_OF_MONTH, selectedday);

                            String myFormat = "dd MMM yyyy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                            vv.setText(sdf.format(myCal.getTime()));

                            data.get(position).setQuestAnswer(sdf.format(myCal.getTime()));

                        }
                    },mcurrentDate.get(Calendar.YEAR), mcurrentDate.get(Calendar.MONTH), mcurrentDate.get(Calendar.DAY_OF_MONTH));
                    mDatePicker.setTitle(context.getResources().getString(R.string.fill_value_selectdate_title));
                    mDatePicker.show();
                }
            });
            questionHolder.dateEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().trim().length()>0)
                    {
                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //---------------------------------------------------------------------------email values-------------------------------------------------
            questionHolder.emailIDEditText=(EditText)convertView.findViewById(R.id.emailIDEdittext);
            questionHolder.emailIDEditText.setId(position);
            questionHolder.emailIDEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            questionHolder.emailIDEditText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
            questionHolder.emailIDEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    EditText ed=(EditText)v;
                    if(actionId==EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_UNSPECIFIED)
                    {
                        if(ed.getText().toString().trim().length()>0)
                        {
                            if(CommonFunctions.emailValidator(ed.getText().toString().trim()))
                            {
                                data.get(position).setQuestAnswer(ed.getText().toString().trim());
                            }
                            else
                            {
                                ed.setError(context.getResources().getString(R.string.fill_value_emailid_msg));
                            }
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
                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //---------------------------------------------------------------------------------time values---------------------------------------------
            questionHolder.timeEditText=(EditText)convertView.findViewById(R.id.timeEdittext);
            questionHolder.timeEditText.setId(position);
            questionHolder.timeEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText vv=(EditText)v;
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            vv.setText(selectedHour + ":" + selectedMinute);
                            data.get(position).setQuestAnswer(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(context.getResources().getString(R.string.fill_value_selecttime_title));
                    mTimePicker.show();
                }
            });
            questionHolder.timeEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if(charSequence.toString().trim().length()>0)
                    {
                        data.get(position).setQuestAnswer(charSequence.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //-----------------------------------------------------------------------Check box values-------------------------------------------------------------

            questionHolder.checkboxButton=(Button)convertView.findViewById(R.id.multipleChoiceButton);
            questionHolder.checkboxButton.setId(position);
            questionHolder.checkboxButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(v);
                }
            });


            System.out.println("question.getType(): "+question.getType());
            if(question.getType()==0 || question.getType()==-1)
            {
                System.out.println("question.getType(): "+question.getType());
            }
            else
            {
                switch (typeAssistDAO.getEventTypeCode(question.getType())) {
                    case "NUMERIC":
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
                        break;
                    case "EMAIL":
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
                        break;
                }
            }

            if(question.getType()==0 || question.getType()==-1)
            {
                System.out.println("question.getType(): "+question.getType());
            }
            else {
                if (data.get(position).getQuestAnswer() != null && !data.get(position).getQuestAnswer().toString().trim().equalsIgnoreCase("null") && data.get(position).getQuestAnswer().toString().trim().length() > 0) {

                    switch (typeAssistDAO.getEventTypeCode(question.getType())) {
                        case "NUMERIC":
                            questionHolder.numericEditText.setText(data.get(position).getQuestAnswer().toString().trim());
                            break;
                        case "SINGLELINE":
                        case "EDITTEXT":
                            questionHolder.singleLineEditText.setText(data.get(position).getQuestAnswer().toString().trim());
                            break;
                        case "MULTILINE":
                            questionHolder.multiLineEditText.setText(data.get(position).getQuestAnswer().toString().trim());
                            break;
                        case "DROPDOWN":
                            break;
                        case "YESNONA":
                            break;
                        case "CHECKBOX":
                            break;
                        case "SIGNATURE":
                            break;
                        case "RATING":
                            questionHolder.ratingBar.setRating(Float.valueOf(question.getQuestAnswer()));
                            break;
                        case "DATE":
                            questionHolder.dateEditText.setText(data.get(position).getQuestAnswer().toString().trim());
                            break;
                        case "EMAIL":
                            questionHolder.emailIDEditText.setText(data.get(position).getQuestAnswer().toString().trim());
                            break;
                        case "TIME":
                            questionHolder.timeEditText.setText(data.get(position).getQuestAnswer().toString().trim());
                            break;
                    }

                }
            }

            convertView.setTag(questionHolder);*/
        }
        else
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView= (LinearLayout) inflater.inflate(R.layout.activity_incident_report_questionset_name_row,null);
			questionHolder = new QuestionHolder();

            questionHolder.questionSetNameTextview=(TextView) convertView.findViewById(R.id.qsetNameTextview);

            if(parentSection.getParentId()==-1) {
                questionHolder.questionSetNameTextview.setText(""+parentSection.getqSetName());
                questionHolder.questionSetNameTextview.setBackgroundColor(context.getResources().getColor(R.color.screen_background_color));
            }
            else {
                questionHolder.questionSetNameTextview.setText(parentSection.getqSetName());
                questionHolder.questionSetNameTextview.setBackgroundColor(context.getResources().getColor(R.color.colorGray));
                questionHolder.questionSetNameTextview.setGravity(Gravity.START);
            }

        }

        return  convertView;
    }


    /*private void showDialog(final View v)
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
        builder.setMultiChoiceItems(combVal, testBoolean, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                itemsChecked[which]=isChecked;
            }
        });
        builder.show();
    }*/

    public ArrayList<ParentSection> getData()
    {
        return data;
    }

    /*private void addItemToSpinner(int position)
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

    }*/

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        int chkradioButton=radioGroup.getCheckedRadioButtonId();
        switch(chkradioButton)
        {
            /*case R.id.radio0:
                data.get(radioGroup.getId()).setQuestAnswer("Yes");
                break;
            case R.id.radio1:
                data.get(radioGroup.getId()).setQuestAnswer("No");
                break;
            case R.id.radio2:
                data.get(radioGroup.getId()).setQuestAnswer("NA");
                break;*/
        }
    }

    @Override
    public void onClick(View view) {
        //showAlertForSignature(view);
    }

    /*private void showAlertForSignature(final View view)
    {
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
                    attachment.setAttachmentid(question.getQuestAnsTransId());
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
                    System.out.println("OwnerName: "+question.getParentActivity().toUpperCase());
                    attachment.setOwnername(typeAssistDAO.getEventTypeID(question.getParentActivity().toUpperCase(), Constants.IDENTIFIER_OWNER));//need to pass table name according to
                    attachment.setOwnerid(question.getJobneedid());
                    attachment.setServerPath(Constants.SERVER_FILE_LOCATION_PATH+CommonFunctions.getFolderNameFromDate(System.currentTimeMillis())+question.getParentActivity()+"/"+question.getParentFolder()+"/");

                    attachment.setBuid(question.getBuid());
                    AttachmentDAO attachmentDAO=new AttachmentDAO(context);
                    attachmentDAO.insertCommonRecord(attachment);

                    File image = new File(f.getPath());
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 150 , 150, true);
                    ((ImageView)view).setImageBitmap(bitmap);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }


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
    }*/

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
        EditText dateEditText;
        EditText emailIDEditText;
        EditText timeEditText;

        Button commentButton;
        Button audioButton;
        Button videoButton;
        Button pictureButton;

        TextView quetionNameTextview;
        TextView questionSetNameTextview;

        ImageView questionRemarkImageView;

    }
}
