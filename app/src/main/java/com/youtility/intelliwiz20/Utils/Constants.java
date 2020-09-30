package com.youtility.intelliwiz20.Utils;

import android.os.CountDownTimer;

/**
 * Created by PrashantD on 04/08/17.
 *
 */

public class Constants {
    public static final String FOLDER_NAME="Intelliwiz2.0";
    public static final String ATTACHMENT_FOLDER_NAME="Attachments";

    public static final String APPLICATION_MAIN_PREF="applicationPref";
    public static final String APPLICATION_DATA_SERVER_URL="applicationBaseDataServerURL";
    public static final String APPLICATION_IMAGE_SERVER_URL="applicationBaseImageServerURL";
    public static final String APPLICATION_USER_ENTER_CLIENT_CODE="applicationBaseUsrEntrClientCode";

    public static final String SYNC_OFFSET_PREF="syncOffsetPref";
    public static final String SYNC_TICKET_OFFSET="syncTicketOffset";
    public static final String SYNC_TICKET_TIMESTAMP="syncTicketTimestamp";

    public static final String LOGIN_PREFERENCE="loginPref";

    public static int valid=0;

    public static final String LOGIN_MODULE_ACCESS="loginModuleAccess";
    public static final String TEMP_BASE_URL="loginBaseUrl";

    public static final String LOGIN_SITE_CODE="loginSiteCode";
    public static final String LOGIN_SITE_NAME="loginSiteName";
    public static final String LOGIN_SITE_ID="loginSiteID";

    public static final String LOGIN_ENTERED_USER_SITE="enteredSiteCode";
    public static final String LOGIN_ENTERED_USER_ID="enteredUserName";
    public static final String LOGIN_ENTERED_USER_PASS="enteredUserPassword";

    public static final String LOGIN_UESR_ISADMIN="loginUserIsadmin";
    public static final String LOGIN_USER_CLIENT_CHECK_GPS="checkGPS";
    public static final String LOGIN_USER_CLIENT_CHECK_DATETIME="checkAutoDateTime";
    public static final String LOGIN_USER_CLIENT_CAPTURE_DEVICEEVENT="captureDeviceEvent";

    public static final String LOGIN_USER_CLIENT_ID="clientid";
    public static final String LOGIN_USER_CLIENT_NAME="clientName";
    public static final String LOGIN_USER_CLIENT_CODE="clientCode";

    public static final String LOGIN_PEOPLE_ID="loginPeopleID";
    public static final String LOGIN_PEOPLE_NAME="loginPeopleName";
    public static final String LOGIN_PEOPLE_CODE="loginPeopleCode";

    public static final String LOGIN_EMERGENCY_CONTACT="loginemergencycontact";
    public static final String LOGIN_EMERGENCY_EMAIL="loginemergencyemail";

    public static final String LOGIN_USER_ID="loginUserId";
    public static final String IS_LOGIN_DONE="isLoginDone";

    public static final String pvideolength="pvideolength";
    public static final String email="email";
    public static final String mobileno="mobileno";



    public static final String LOGIN_CONFIG_SGUARD_ENABLE="enablesleepingguard";
    public static final String LOGIN_CONFIG_SGUARD_CAPTCHA_FREQ="captchaFrequency";
    public static final String LOGIN_CONFIG_SITE_AUDIT_SKIP="skipsiteaudit";

    public static final String SERVER_NAME="serverName";

    public static final String IS_TASK_PERMISSION="isTaskPermission";
    public static final String IS_GT_PERMISSION="isGTPermission";
    public static final String IS_PPM_PERMISSION="isPPMPermission";

    public static final String CURRENT_TIMEZONE_OFFSET_VALUE="currentTimezoneOffsetValue";
    public static final String CURRENT_TIMEZONE_OFFSET_NUMBER="currentTimezoneOffsetNumber";

    public static final String PREVIOUS_SELECTED_LANGUAGE="previousSelectedLanguage";
    public static final String SELECTED_LANGUAGE_CHANGE="selectedLangChanged";

    public static final String IS_SYNC_DONE="isSyncDone";

    public static final String SETTING_GENERAL_LOGIN_USER_NAME="login_user_name";
    public static final String SETTING_GENERAL_LOGIN_USER_SITE="login_user_site";
    public static final String SETTING_GENERAL_LOGIN_USER_IMEI="login_user_imei";
    public static final String SETTING_GENERAL_LANGUAGE="language_setting";
    public static final String SETTING_GENERAL_CONTACT_NUMBER="emergency_contact_number";
    public static final String SETTING_GENERAL_CONTACT_EMAILID="emergency_contact_email";
    public static final String SETTING_GENERAL_CONTACT_HELP_MESSAGE="emergency_contact_msg";

    public static final String SETTING_ALERT_TYPE="alert_type";
    public static final String SETTING_CAPTCHA_TYPE="captcha_type";
    public static final String SETTING_ORIGIN_POINT="origin_point";

    public static final String SETTING_SYNCHRONIZATION_UPLOADDATA_FREQ="sync_frequency";
    public static final String SETTING_SYNCHRONIZATION_LOCATION_FREQ="location_frequency";
    public static final String SETTING_SYNCHRONIZATION_ACTIVITY_REQ_FREQ="activityrecognition_frequency";
    public static final String SETTING_SYNCHRONIZATION_VIDEO_REC_FREQ="videorecording_frequency";

    public static final String NETWORK_PREF="networkPref";
    public static final String NETWORK_STATE_PREVIOUS="networkAvailablePrev";

    public static final String SYNC_PREF="syncPref";
    public static final String SYNC_TIMESTAMP="syncTimeStamp";
    public static final String SYNC_MANUAL_RUNNING="manualSyncRunning";
    public static final String SYNC_VERSION="syncVersion";


    public static final String AUTO_SYNC_PREF="autoSyncPref";
    public static final String IS_AUTO_SYNC_RUNNING="isAutoAsyncRunning";
    public static final String CAMERA_ON_TIMESTAMP="cameraOnTimestamp";

    /*public static final String EMERGENCY_CONTACT_INFO_PREF="emergencyContactInfoPref";
    public static final String EMERGENCY_CONTACT_NUMBER="contactNumber";
    public static final String EMERGENCY_CONTACT_EMAIL="contactEmailAddress";
    public static final String EMERGENCY_CONTACT_HELP_MESSAGE="contactHelpMessage";*/

    public static final String APPLICATION_PREF="applicationPref";
    public static final String APPLICATION_SELECTED_NAME="appSelectedName";
    public static final String IS_APPLICATION_SELECTED="isApplicationSelected";

    public static final String TOUR_CHILD_PREF="tourChildPref";
    public static final String TOUR_CHILD_STARTTIME="tourChildStarttime";
    public static final String TOUR_PARENT_STARTTIME="tourParentStarttime";
    public static final String TOUR_CHILD_ASSETID="tourChildAssetID";
    public static final String TOUR_CHILD_QUESTIONSETID="tourChildQuestionsetID";
    public static final String TOUR_CHILD_JOBNEEDID="tourChildJobneedID";
    public static final String TOUR_CHILD_PARENT_JOBNEEDID="tourChildParentJobneedID";

    public static final String SELF_ATTENDANCE_PREF="selfAttendancePref";
    public static final String SELF_ATTENDANCE_STATUS="selfAttendanceStatus";

    public static final String APPLICATION_SYNC_TIME_PREF="applicationSyncTimePref";
    public static final String APPLICATION_PREV_SYNC_TIME="applicationPrevSyncTime";
    public static final String APPLICATION_CURR_SYNC_TIME="applicationCurrSyncTime";

    public static final String DEVICE_RELATED_PREF="deviceRelatedPref";
    public static final String DEVICE_IMEI="deviceIMEI";
    public static final String DEVICE_LATITUDE="latitude";
    public static final String DEVICE_LONGITUDE="longitude";
    public static final String DEVICE_ALTITUDE="altitude";
    public static final String DEVICE_ACCURACY="accuracy";
    public static final String DEVICE_LOC_PROVIDER="locationProvider";
    public static final String DEVICE_LOG_TIME="delTime";


    public static final String ADHOC_JOB_TIMESTAMP_PREF="adhocJobTimeStampPref";
    public static final String ADHOC_TIMESTAMP="adhocTimeStamp";
    public static final String ADHOC_ASSET="adhocAsset";
    public static final String ADHOC_ASSET_ID="adhocAssetID";
    public static final String ADHOC_QSET="adhocQset";
    public static final String ADHOC_QSET_NAME="adhocQsetName";
    public static final String ADHOC_TYPE="adhocType";

    public static final String SITE_AUDIT_PREF="siteAuditPref";
    public static final String SITE_AUDIT_TIMESTAMP="siteAuditTimestamp";
    public static final String SITE_AUDIT_ISPERFORMED="siteAuditIsPerformed";
    public static final String SITE_AUDIT_SITENAME="siteAuditSitename";
    public static final String SITE_AUDIT_SITEID="siteAduitSiteId";
    public static final String SITE_AUDIT_SITE_CHECKIN="siteAduitSiteCheckIn";
    public static final String SITE_AUDIT_SITE_CHECKOUT="siteAduitSiteCheckOut";
    public static final String SITE_AUDIT_SITE_CHECKIN_TIMESTAMP="siteAduitSiteCheckInTimestamp";
    public static final String SITE_AUDIT_SITE_CHECKOUT_TIMESTAMP="siteAduitSiteCheckOutTimestamp";
    public static final String SITE_AUDIT_SITE_ISOTHERLOCATION="siteAduitSiteIsOtherLocation";
    public static final String SITE_AUDIT_QUESTIONSETID="siteAduitQuestionSetId";



    public static final String OTHER_SITE_LIST_PREF="otherSiteListPref";
    public static final String OTHER_SITES="otherSites";

    public static final String SITE_ATTENDANCE_PREF="siteAttendancePref";
    public static final String SITE_ATTENDANCE_QR_RESULT="siteAttendanceQRResult";
    public static final String SITE_ATTENDANCE_QR_RESULT_NAME="siteAttendanceQRResultName";

    public static final String OFFSET_PREF="offsetPref";
    public static final String TICKET_OFFSET_COUNT="ticketOffsetCount";

    public static final String CONVEYANCE_PREF="conveyancePref";
    public static final String CONVEYANCE_TIMESTAMP="conveyanceTimestamp";
    public static final String CONVEYANCE_ISTRAVELSTARTED="conveyanceTravelStarted";
    public static final String CONVEYANCE_START_ADDRESS="conveyanceStartAddress";
    public static final String CONVEYANCE_START_TIME="conveyanceStartTime";
    public static final String CONVEYANCE_START_TIMESTAMP="conveyanceStartTimeStamp";
    public static final String CONVEYANCE_START_MODE="conveyanceStartMode";
    public static final String CONVEYANCE_START_LAT_LOCATION="conveyanceStartLatLocation";
    public static final String CONVEYANCE_START_LON_LOCATION="conveyanceStartLonLocation";
    public static final String CONVEYANCE_END_ADDRESS="conveyanceEndAddress";
    public static final String CONVEYANCE_END_TIME="conveyanceEndTime";
    public static final String CONVEYANCE_END_TIMESTAMP="conveyanceEndTimeStamp";
    public static final String CONVEYANCE_END_EXPENCE="conveyanceEndExpence";
    public static final String CONVEYANCE_END_LAT_LOCATION="conveyanceEndLatLocation";
    public static final String CONVEYANCE_END_LON_LOCATION="conveyanceEndLonLocation";
    public static final String CONVEYANCE_DISTANCE="conveyanceDistance";
    public static final String CONVEYANCE_DURATION="conveyanceDuration";

    public static final String MEMORY_INFO_PREF="memoryInfoPref";
    public static final String IMEMORY_AVAILABLE="availableInternalMemory";
    public static final String EMEMORY_AVAILABLE="availableExternalMemory";

    public static final String GEOFENCE_BREACH_PREF="geoFenceBreachPref";
    public static final String GEOFENCE_IS_IN_OUT="IsUserINorOUT";
    public static final String GEOFENCE_STATUS="geofenceStatus";
    public static final String GEOFENCE_OUT_ALERT="userOut";
    public static final String GEOFENCE_Is_In= "Is_In";
    public static final String GEOFENCE= "Geofence";



    public static final String SYNC_SUMMARY_PREF="syncSummaryPref";
    public static final String SYNC_SUMMARY_ADHOC_COUNT="syncSummaryAdhocCount";
    public static final String SYNC_SUMMARY_IR_COUNT="syncSummaryIRCount";
    public static final String SYNC_SUMMARY_JNUPDATE_COUNT="syncSummaryJobNeedUpdateCount";
    public static final String SYNC_SUMMARY_EMPREF_COUNT="syncSummaryEmpReferenceCount";
    public static final String SYNC_SUMMARY_SA_COUNT="syncSummarySiteAuditCount";
    public static final String SYNC_SUMMARY_MAIL_SENT="syncSummaryMailSent";

    public static final String SYNC_SUMMARY_PENDING_PEOPLEEVENTLOG_COUNT="syncSummaryPendingPeopleEventLogCount";
    public static final String SYNC_SUMMARY_PENDING_JOBNEED_COUNT="syncSummaryPendingJobneedCount";
    public static final String SYNC_SUMMARY_PENDING_REPLY_COUNT="syncSummaryPendingReplyCount";
    public static final String SYNC_SUMMARY_PENDING_EMPREF_COUNT="syncSummaryPendingEmpRefCount";

    public static final String JOB_ALERT_PREF="jobAlertPref";
    public static final String JOBALERT_POSITION="jobNeedPosition";
    public static final String JOBALERT_ID="jobNeedID";

    public static final String STEP_COUNTER_PREF="stepCounterPref";
    public static final String STEP_COUNTER_COUNT="lastStepCounter";
    public static final String STEP_COUNTER_TIME="lastStepCounterTime";
    public static final String STEP_COUNTER_TIMESTAMP="lastStepCounterTimestamp";
    public static final String STEP_COUNTER_LAST_TIMESTAMP="lastStepCounterLastTimestamp";
    public static final String STEP_COUNTER_LAST_COUNT="lastStepPrevCounter";
    public static final String STEP_COUNTER_START_TIME="stepCounterStartTime";
    public static final String STEP_COUNTER_END_TIME="stepCounterEndTime";
    public static final String STEP_COUNTER_START_DATE="stepCounterStartDate";
    public static final String STEP_COUNTER_END_DATE="stepCounterEndDate";
    public static final String STEP_COUNTER_BUZZ_COUNTER="stepCounterBuzzCounter";
    public static final String STEP_COUNTER_BUZZ_TIMER="stepCounterBuzzTimer";
    public static final String STEP_COUNTER_ENABLE="stepCounterEnable";
    public static final String STEP_COUNTER_ISRUNNING="stepCounterIsRunning";
    public static final String STEP_COUNTER_ID="stepCounterTimestampID";



    public static final String APPLICATION_TRACKER_PREF="applicationTrackerPref";
    public static final String APPLICATION_TRACKER_INSTALLDATE="applicationTrackerInstalledDate";
    public static final String APPLICATION_TRACKER_OPENED_COUNTER="applicationTrackerOpenCounter";
    public static final String APPLICATION_TRACKER_LASTOPENEDDATE="applicationTrackerLastOpenDate";

    /*public static final String SITE_RELATED_PREF="siteRelatedPref";
    public static final String SITE_CODE="siteCode";
    public static final String IS_SITE_CODE_AVAILABLE="isSiteCodeAvail";*/

    public static final String JOB_TYPE_ADHOC="ADHOC";
    public static final String JOB_TYPE_SCHEDULED="SCHEDULE";

    public static final String ATTACHMENT_TYPE_ATTACHMENT="ATTACHMENT";
    public static final String ATTACHMENT_TYPE_REPLY="REPLY";
    public static final String ATTACHMENT_TYPE_SIGN="SIGN";

    public static final String ATTACHMENT_OWNER_TYPE_JOBNEED="JOBNEED";
    public static final String ATTACHMENT_OWNER_TYPE_PEOPLEEVENTLOG="PEOPLEEVENTLOG";
    public static final String ATTACHMENT_OWNER_TYPE_PEOPLE="PEOPLE";

    public static final String EVENT_TYPE_CONVEYANCE="CONVEYANCE";
    public static final String EVENT_TYPE_INSTALLED_APPLICATIONS="INSTALLEDAPPLICATIONS";
    public static final String TRANSPORT_MODE="Mode of Transport";
    public static final String EVENT_TYPE="Event Type";

    public static final String SHIFT_TYPE="Shift Type";
    public static final String SCAN_TYPE="Scan Type";

    /*public static final String FROM_ACTIVITY_JOBNEED="JOBNEED";
    public static final String FROM_ACTIVITY_JOBNEED="TOUR";
    public static final String FROM_ACTIVITY_JOBNEED="CHECKPOINT";
    public static final String FROM_ACTIVITY_JOBNEED="ADHOC";*/

    public static final String JOB_NEED_IDENTIFIER_TASK="TASK";
    public static final String JOB_NEED_IDENTIFIER_TICKET="TICKET";
    public static final String JOB_NEED_IDENTIFIER_TOUR="TOUR";
    public static final String JOB_NEED_IDENTIFIER_INCIDENT="INCIDENTREPORT";
    public static final String JOB_NEED_IDENTIFIER_SITEREPORT="SITEREPORT";
    public static final String JOB_NEED_IDENTIFIER_ASSET="ASSETMAINTENANCE";
    public static final String JOB_NEED_IDENTIFIER_ASSET_LOG="ASSETLOG";
    public static final String JOB_NEED_IDENTIFIER_ASSET_AUDIT="ASSETAUDIT";
    public static final String JOB_NEED_IDENTIFIER_PPM="PPM";
    public static final String JOB_NEED_IDENTIFIER_INTERNAL_REQUEST="INTERNALREQUEST";

    public static final String REQUEST_TYPE="INTERNALTRANSFERREQUEST";

    public static final String OTHER_SITE_CODE="OTHERSITE";
    public static final String IDENTIFIER_BU="Bu Identifier";


    public static final String IDENTIFIER_JOBNEED="Job Identifier";
    public static final String IDENTIFIER_RUNNINGSTATUS="Running Status";
    public static final String IDENTIFIER_ATTACHMENT="Attachment";
    public static final String IDENTIFIER_JOBTYPE="Job Type";
    public static final String IDENTIFIER_SCANTYPE="Scan Type";
    public static final String IDENTIFIER_OWNER="Owner";
    public static final String IDENTIFIER_ATTENDANCE="Attendance";
    public static final String IDENTIFIER_PUNCHSTATUS="Punch Status";
    public static final String IDENTIFIER_PRIORITY="Priority";
    public static final String IDENTIFIER_EVENTTYPE="Event Type";

    public static final String IDENTIFIER_IDPROOFTYPE="Id Proof Type";
    public static final String TACODE_ADHAR="AADHAR";
    public static final String TACODE_PAN="PAN";


    public static final String IDENTIFIER_PERSONLOGGERTYPE="Person Logger";
    public static final String TACODE_PERSONLOGGER="PERSONLOGGER";
    public static final String TACODE_VISITOR="VISITOR";
    public static final String TACODE_EMPLOYEEREFERENCE="EMPLOYEEREFERENCE";

    public static final String IDENTIFIER_ASSET="Asset Identifier";
    public static final String TACODE_LOCATION="LOCATION";

    public static final String STATUS_TYPE_JOBNEED="Job Status";
    public static final String STATUS_TYPE_TICKET="Ticket Status";

    public static final String SCAN_TYPE_ENTERED="ENTERED";
    public static final String SCAN_TYPE_QR="QR";
    public static final String SCAN_TYPE_NFC="NFC";
    public static final String SCAN_TYPE_SKIP="SKIP";

    public static final int CHECKPOINT_GUARD_TOUR_LIST=0;
    public static final int CHECKPOINT_ADHOC_LIST=1;

    public static final String ATTENDANCE_PUNCH_TYPE_IN="IN";
    public static final String ATTENDANCE_PUNCH_TYPE_OUT="OUT";


    public static final String SYNC_STATUS_ZERO="0";
    public static final String SYNC_STATUS_ONE="1";
    public static final String SYNC_STATUS_TWO="2";
    public static final String SYNC_STATUS_THREE="3";

    public static final int ATTACHMENT_AUDIO=0;
    public static final int ATTACHMENT_VIDEO=1;
    public static final int ATTACHMENT_PICTURE=2;


    public static final int MIC_RECORD_AUDIO_REQUEST_CODE = 0;
    public static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 1;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2;



    /*public static final String BASE_URL="http://192.168.1.118:8000/service/";
    public static final String IMAGE_BASE_URL="http://192.168.1.118:8000/";*/

    /*public static final String BASE_URL="http://192.168.1.122:8000/service/";
    public static final String IMAGE_BASE_URL="http://192.168.1.122:8000/";*/

    /*public static final String BASE_URL="http://192.168.1.101:8001/service/";
    public static final String IMAGE_BASE_URL="http://192.168.1.101:8001/";*/

    //public static final String BASE_URL="http://192.168.1.254:8000/service/";
    //public static final String IMAGE_BASE_URL="http://intelliwiz.youtility.in/";

    /*public static final String BASE_URL="http://192.168.1.150:8000/service/";
    public static final String IMAGE_BASE_URL="http://192.168.1.150:8000/";*/

    /*public static final String BASE_URL="http://192.168.1.150:8002/service/";
    public static final String IMAGE_BASE_URL="http://192.168.1.150:8002/";*/

    //dinesh.dd1   dinesh dinesh
    /*public static final String BASE_URL="https://you.youtility.in/service/";
    public static final String IMAGE_BASE_URL="https://you.youtility.in/";*/

    /*public static final String BASE_URL="https://intelliwiz.youtility.in/service/";
    public static final String IMAGE_BASE_URL="https://intelliwiz.youtility.in/";*/

    /*public static final String BASE_URL="https://alstom.youtility.in/service/";
    public static final String IMAGE_BASE_URL="https://alstom.youtility.in/";*/
    public static String BASE_URL="";
    public static String IMAGE_BASE_URL="";

    //public static final String INITIAL_SERVER_URL="https://intelliwiz.youtility.in/service/";
    //public static final String INITIAL_SERVER_URL="http://barfi.youtility.in:7777/service/";
    //public static final String INITIAL_SERVER_URL="https://you.youtility.in/service/";
    //public static final String INITIAL_SERVER_URL="https://stg.youtility.in/service/";




    //http://barfi.youtility.in:11900/
    //http://barfi.youtility.in:7777/index
    //public static final String INITIAL_SERVER_URL="http://barfi.youtility.in:11900/service/";

    //public static final String INITIAL_SERVER_URL="http://192.168.1.150:8000/service/";
    //public static final String INITIAL_SERVER_URL="http://192.168.1.122:8000/service/";
    public static final String INITIAL_SERVER_URL="http://192.168.1.118:8000/service/";


    public static final String INITIAL_SERVER_ERP_URL="http://192.168.1.115:8000/";


    /*sukhi
    homumbai
            p065410
    bala1987*/


    /*public static final String I_SERVER_BASE_URL="https://intelliwiz.youtility.in/service/";
    public static final String I_SERVER_IMAGE_BASE_URL="https://intelliwiz.youtility.in/";

    public static final String Y_SERVER_BASE_URL="https://you.youtility.in/service/";
    public static final String Y_SERVER_IMAGE_BASE_URL="https://you.youtility.in/";

    public static final String L_SERVER_BASE_URL="http://192.168.1.150:8000/service/";
    public static final String L_SERVER_IMAGE_BASE_URL="http://192.168.1.150:8000/";*/

    //"http://192.168.1.150:8000/service/"

    //public static String BASE_URL="";
    public static final String SERVER_FILE_LOCATION_PATH="youtility2_avpt/transaction/";
    public static final String SERVER_FILE_LOCATION_PATH_1="youtility2_avpt/master/people/";
    public static final String FILE_ASSET="fileAssets";
    public static final String FILE_JOBNEED="fileJOBNeed";
    public static final String FILE_JOBNEED_DETAILS="fileJOBNeedDetails";
    public static final String FILE_TYPE_ASSIST="fileTypeAssist";
    public static final String FILE_GEOFENCE="fileGeofence";
    public static final String FILE_PEOPLE="filePeopleMaster";
    public static final String FILE_GROUP="fileGroupMaster";
    public static final String FILE_ATTENDANCE_HISTORY="fileAttendanceHistory";
    public static final String FILE_QUESTION_SET="fileQuestionSet";
    public static final String FILE_QUESTIONS="fileQuestions";
    public static final String FILE_QUESTION_SET_BELONGING="fileQuestionSetBelonging";
    public static final String FILE_PEOPLE_GROUP_BELONGING="filePeopleGroupBelonging";
    public static final String FILE_SITES="fileSites";


    public static final String SERVICE_LOGIN="Login";
    public static final String SERVICE_LOGOUT="Logout";
    public static final String SERVICE_INSERT="Insert";
    public static final String SERVICE_SELECT="Select";
    public static final String SERVICE_JOBNEED_INSERT="JobNeedInsert";
    public static final String SERVICE_IR_INSERT="IncidentReport";
    public static final String SERVICE_ADHOC="ADHOC";
    public static final String SERVICE_TASK_TOUR_UPDATE="TaskAndTourUpdate";
    public static final String SERVICE_UPLOAD_ATTACHMENT="UploadAttachment";
    public static final String SERVICE_CLIENTURL="ClientURL";

    public static final String STORY_REQUIRED="0";
    public static final String STORY_NOT_REQUIRED="1";

    public static int QR_CODE_SCANNER_CAMERA=-1;

    public static final String RESPONSE_RC="rc";
    public static final String RESPONSE_RETURNID="returnid";
    public static final String RESPONSE_NROW="nrow";
    public static final String RESPONSE_ROWDATA="row_data";
    public static final String RESPONSE_COLUMNS="columns";
    public static final String RESPONSE_AUTH="auth";
    public static final String RESPONSE_MSG="msg";
    public static final String RESPONSE_ERROR="reason";

    public static final String JOBNEED_STATUS_ASSIGNED="ASSIGNED";
    public static final String JOBNEED_STATUS_INPROGRESS="INPROGRESS";
    public static final String JOBNEED_STATUS_AUTOCLOSED="AUTOCLOSED";
    public static final String JOBNEED_STATUS_COMPLETED="COMPLETED";
    public static final String JOBNEED_STATUS_PARTIALLY_COMPLETED="PARTIALLYCOMPLETED";

    public static final String TICKET_STATUS_NEW="NEW";
    public static final String TICKET_STATUS_OPEN="OPEN";
    public static final String TICKET_STATUS_RESOLVED="RESOLVED";
    public static final String TICKET_STATUS_ESCALATED="ESCALATED";
    public static final String TICKET_STATUS_CANCELLED="CANCELLED";

    public static double travelledDist=0.0;

    public static CountDownTimer countDownTimer;
}

/*
dependencies {
    //implementation 'com.android.support:support-v4:28.+'
    implementation ('com.google.apis:google-api-services-translate:v2-rev20170525-1.27.0')
    implementation ('com.google.cloud:google-cloud-translate:0.5.0')
}

implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
implementation 'com.squareup.retrofit2:adapter-rxjava2:2.4.0'

implementation 'com.google.code.gson:gson:2.8.5'

implementation 'com.jakewharton:butterknife:5.1.1'
annotationProcessor 'com.jakewharton:butterknife:5.1.1'

compile ('com.google.apis:google-api-services-translate:v2-rev47-1.22.0') {
    exclude group: 'com.google.guava'
}
compile ('com.google.cloud:google-cloud-translate:0.5.0') {
    exclude group: 'io.grpc', module: 'grpc-all'
    exclude group: 'com.google.protobuf', module: 'protobuf-java'
    exclude group: 'com.google.api-client', module: 'google-api-client-appengine'
}

annotationProcessor 'com.google.auto.value:auto-value:1.5.2'*/
