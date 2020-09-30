package com.youtility.intelliwiz20.Utils;

/**
 * Created by PrashantD on 30/10/17.
 * storing upload and download query format
 */

public class DatabaseQuries {

    //jobneedid	jobdesc	plandatetime	expirydatetime	gracetime	receivedonserver	starttime	endtime	gpslocation	remarks	aatop	assetid	jobid	jobstatus
    // jobtype	performedby	questionsetid	scantype	peopleid	groupid	identifier	parent	cuser	muser	cdtz	mdtz	isdeleted	frequency   priority
    public static final String JOBNEED_INSERT="INSERT INTO jobneed(jobdesc, plandatetime, expirydatetime, gracetime, receivedonserver, starttime, endtime, gpslocation," +
            "remarks, aatop, assetid, jobid, jobstatus, jobtype, performedby, questionsetid, scantype, peopleid, groupid, identifier, parent, cuser, muser, cdtz, mdtz," +
            "isdeleted, frequency, priority)  VALUES ";

    public static final String JOBNEED_UPDATE="UPDATE jobneed " +
            "SET (jobstatus,scantype,receivedonserver,starttime,endtime,gpslocation,remarks,muser,mdtz,performedby,groupid, aatop,peopleid) =";

    //attachmentid, filepath, filename, narration, gpslocation, datetime, ownername, ownerid, attachmenttype, cuser, muser, cdtz, mdtz, isdeleted
    public static final String ATTACHMENT_INSERT="INSERT INTO attachment(filepath, filename, narration, gpslocation, datetime," +
            " ownername, ownerid, attachmenttype, cuser, muser, cdtz, mdtz, buid) VALUES ";

    //pelogid, accuracy, datetime, gpslocation, photorecognitionthreshold, photorecognitionscore, photorecognitiontimestamp, photorecognitionserviceresponse,
    // facerecognition, peopleid, peventtype, punchstatus, verifiedby, siteid, cuser, muser, cdtz, mdtz, isdeleted, gfid, deviceid,transportmode, expamt, duration, reference, remarks, distance

    public static final String PEOPLE_EVENTLOG_INSERT="INSERT INTO peopleeventlog(accuracy, datetime, gpslocation, photorecognitionthreshold, photorecognitionscore, photorecognitiontimestamp, " +
            "photorecognitionserviceresponse, facerecognition, peopleid, peventtype, punchstatus, verifiedby, buid, cuser, muser, cdtz, mdtz, gfid, deviceid, transportmode, expamt, duration, reference, remarks, distance,otherlocation) VALUES ";

    //deviceeventlogid, deviceid, eventvalue, gpslocation, accuracy, altitude, batterylevel, signalstrength, availextmemory,
    // availintmemory, eventtype, peopleid, signalbandwidth, cuser, muser, cdtz, mdtz, isdeleted
    public static final String DEVICE_EVENTLOG_INSERT="INSERT INTO deviceeventlog(" +
            "deviceid, eventvalue, gpslocation, accuracy,altitude, batterylevel, signalstrength, availextmemory, availintmemory," +
            "cdtz, mdtz, cuser, eventtype, muser, peopleid, signalbandwidth, buid,androidosversion, applicationversion, modelname, installedapps, simserialnumber, linenumber, networkprovidername,stepcount)" +
            "VALUES ";

    public static final String SKIP_SALOG_INSERT="INSERT INTO jobneed(" +
            "jobdesc, plandatetime, expirydatetime,gracetime, starttime, endtime, gpslocation," +
            "remarks, aatop, assetid, frequency, jobid, jobtype, jobstatus, performedby,priority, questionsetid, scantype, peopleid," +
            "groupid, identifier, parent,cuser,cdtz,muser,mdtz,ticketcategory,buid, ctzoffset)" +
            "VALUES ";

    /*personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose,
        scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms,
        weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,areacode,enable,cuser,muser,cdtz,mdtz,buid,clientid*/
    public static final String PERSON_LOGGER_INSERT="INSERT INTO personlogger(" +
            "personloggerid,identifier,peopleid,visitoridno,firstname,middlename,lastname,mobileno,idprooftype,photoidno,belongings,meetingpurpose," +
            "scheduledintime,scheduledouttime,actualintime,actualouttime,referenceid,dob,localaddress,nativeaddress,qualification,english,currentemployement,lengthofservice,heightincms," +
            "weightinkgs,waist,ishandicapped,identificationmark,physicalcondition,religion,caste,maritalstatus,gender,lareacode,enable,cuser,muser,cdtz,mdtz,buid,clientid,lcity,ncity,lstate,nstate,nareacode)" +
            "VALUES ";

//bucode	buname	isvendor	webcapability	mobilecapability
// isserviceprovider	parent	identifier	enable	cuser	muser	cdtz	mdtz	reportcapability	iswh
    public static final String BU_INSERT="INSERT INTO bu(" +
            "bucode, buname, isvendor, webcapability,mobilecapability, isserviceprovider, parent, identifier, enable," +
            "cdtz, mdtz, cuser, reportcapability, muser, iswh)" +
            "VALUES ";

    //public static final String GET_ASSETS="select * from getassetsmodifiedafter";
    public static final String GET_ASSETS="select * from getassetdetails";

    public static final String GET_JOBNEED="select * from getjobneedmodifiedafter";

    public static final String GET_JOBNEEDDETAILS="select * from getjndmodifiedafter";

    public static final String GET_TYPEASSIST="select * from gettypeassistmodifiedafter";

    public static final String GET_GEOFRENCE="select * from get_geofence";

    public static final String GET_PEOPLE="select * from getpeoplemodifiedafter";

    public static final String GET_GROUP="select * from getgroupsmodifiedafter";

    public static final String GET_PEOPLEATTENDANCE="select * from get_peopleattendance";

    public static final String GET_QUESTIONS="select * from getquestionsmodifiedafter";

    public static final String GET_QUESTIONSET="select * from getquestionsetmodifiedafter";

    public static final String GET_QUESTIONSETBELONGING="select * from getqsetbelongingmodifiedafter";

    public static final String GET_PEOPLEGROUPBELONGING="select * from getpgbelonging";

    //public static final String GET_SITE="select * from get_sites_with_surveyreport";
    public static final String GET_SITE="select * from get_siteslist";

    public static final String GET_TICKET_MODIFIEDAFTER="select * from getticketmodifiedafter";
    public static final String GET_TICKET="select * from getticket";
    //getticket(peopleid, buid, offset);

    public static final String GET_ASSET_ADDRESS="select * from getassetaddress";

    public static final String GET_OTHER_SITE="select * from get_other_sites";

    public static final String GET_SITE_INFO="select * from get_siteinfo";

    //public static final String GET_TEMPLATES="select * from get_people_template_list";
    public static final String GET_TEMPLATES="select * from get_people_template_list1";

    public static final String GET_AUTO_CLOSED_TICKET="select * from getautocloseticket";

    public static final String GET_ASSIGNED_SITE_PEOPLE="select * from get_assignedsites_people_list";

    public static final String GET_CAPTCHA_CONFIG="select * from get_captchafrequency";

    public static final String get_soslog="select * from get_soslog";


    public static final String GET_SITE_LAST_CHECKINOUT="select * from get_peoplelastcheckinout";
    public static final String GET_SITE_OLD="select * from get_sites_with_surveyreport";

    public static String JOBNEEDIDS="";



}
