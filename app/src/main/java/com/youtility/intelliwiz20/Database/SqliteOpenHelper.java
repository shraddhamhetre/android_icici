package com.youtility.intelliwiz20.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.youtility.intelliwiz20.Tables.Address_Table;
import com.youtility.intelliwiz20.Tables.AssetDetail_Table;
import com.youtility.intelliwiz20.Tables.AssignedSitePeople_Table;
import com.youtility.intelliwiz20.Tables.Attachment_Table;
import com.youtility.intelliwiz20.Tables.AttendanceHistoy_Table;
import com.youtility.intelliwiz20.Tables.AttendanceSheet_Table;
import com.youtility.intelliwiz20.Tables.CaptchaConfig_Table;
import com.youtility.intelliwiz20.Tables.DeviceEventLog_Table;
import com.youtility.intelliwiz20.Tables.Geofence_Table;
import com.youtility.intelliwiz20.Tables.Group_Table;
import com.youtility.intelliwiz20.Tables.JOBNeedDetails_Table;
import com.youtility.intelliwiz20.Tables.JOBNeed_Table;
import com.youtility.intelliwiz20.Tables.PeopleEventLog_Table;
import com.youtility.intelliwiz20.Tables.PeopleGroupBelongin_Table;
import com.youtility.intelliwiz20.Tables.People_Table;
import com.youtility.intelliwiz20.Tables.PersonLogger_Table;
import com.youtility.intelliwiz20.Tables.QuestionSetBelonging_Table;
import com.youtility.intelliwiz20.Tables.QuestionSet_Table;
import com.youtility.intelliwiz20.Tables.Question_Table;
import com.youtility.intelliwiz20.Tables.SiteList_Table;
import com.youtility.intelliwiz20.Tables.SitePerformTemplateList_Table;
import com.youtility.intelliwiz20.Tables.SitesInfo_Table;
import com.youtility.intelliwiz20.Tables.SitesVisitedLog_Table;
import com.youtility.intelliwiz20.Tables.StepCountLog_Table;
import com.youtility.intelliwiz20.Tables.TemplateList_Table;
import com.youtility.intelliwiz20.Tables.Test_Table;
import com.youtility.intelliwiz20.Tables.TypeAssist_Table;

import java.io.File;
import java.io.IOException;

public class SqliteOpenHelper extends SQLiteOpenHelper {

	
	private static SqliteOpenHelper objSurveySQLOpenHelper;
	private static SQLiteDatabase objSQLiteDatabase;
	public static final String DB_NAME = "intelliwiz20_DB";
	//public static final String DB_NAME = "istaging_DB";
	public static int OLD_DB_version = 0; //playstore available 6
	public static final int DB_version = 9; //playstore available 6
	private String DB_PATH = null;
	private static boolean isDBOpen = false;
	Context context;
	
	
	public SqliteOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_version);
		this.context=context;

        //DB_PATH=context.getFilesDir().getPath()+"/databases/"+DB_NAME;
        DB_PATH="data/data/com.youtility.intelliwiz20/databases/"+DB_NAME;
        //DB_PATH="data/data/com.youtility.istaging/databases/"+DB_NAME;
		// TODO Auto-generated constructor stub
	}


    public static SqliteOpenHelper getInstance(Context context){
		
		if(objSurveySQLOpenHelper == null){
			objSurveySQLOpenHelper=new SqliteOpenHelper(context); 
		}
			return objSurveySQLOpenHelper;
		
	}
	
	public SQLiteDatabase getDatabase(){
		boolean dbexist=checkdatabase();
		System.out.println("check db is dbexist  "+dbexist);
		
		if(dbexist){
			System.out.println("Database exists isDBOpen "+isDBOpen);
			if(!isDBOpen){
				opendatabase();
			}
		}else{
			System.out.println("Database doesn't exist");
			try{
				createdatabase();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(objSQLiteDatabase == null){
			opendatabase();
		}
		return objSQLiteDatabase;
	}

    public void CreateTables(SQLiteDatabase db){
        System.out.println("onCreate called for DB");
        // TODO Auto-generated method stub
        Log.d("Debug", " in SQLOpenHelper onCreate ");
        AssetDetail_Table.OnCreate(db);
        JOBNeed_Table.OnCreate(db);
        JOBNeedDetails_Table.OnCreate(db);
        TypeAssist_Table.OnCreate(db);
        Geofence_Table.OnCreate(db);
        Attachment_Table.OnCreate(db);
        People_Table.OnCreate(db);
        Group_Table.OnCreate(db);
        PeopleEventLog_Table.OnCreate(db);
        AttendanceHistoy_Table.OnCreate(db);
        Question_Table.OnCreate(db);
        QuestionSet_Table.OnCreate(db);
        QuestionSetBelonging_Table.OnCreate(db);
        PeopleGroupBelongin_Table.OnCreate(db);
        DeviceEventLog_Table.OnCreate(db);
        //Sites_Table.OnCreate(db);
        AttendanceSheet_Table.OnCreate(db);
        SitesVisitedLog_Table.OnCreate(db);
        Address_Table.OnCreate(db);
        PersonLogger_Table.OnCreate(db);
        SitesInfo_Table.OnCreate(db);
        SitePerformTemplateList_Table.OnCreate(db);
        StepCountLog_Table.OnCreate(db);
        SiteList_Table.OnCreate(db);
        TemplateList_Table.OnCreate(db);
        AssignedSitePeople_Table.OnCreate(db);
        Test_Table.OnCreate(db);
        CaptchaConfig_Table.OnCreate(db);

    }
	@Override
	public void onCreate(SQLiteDatabase db) {

        CreateTables(db);
/*		System.out.println("onCreate called for DB");
		// TODO Auto-generated method stub
		Log.d("Debug", " in SQLOpenHelper onCreate ");
        AssetDetail_Table.OnCreate(db);
        JOBNeed_Table.OnCreate(db);
        JOBNeedDetails_Table.OnCreate(db);
        TypeAssist_Table.OnCreate(db);
        Geofence_Table.OnCreate(db);
        Attachment_Table.OnCreate(db);
        People_Table.OnCreate(db);
        Group_Table.OnCreate(db);
        PeopleEventLog_Table.OnCreate(db);
        AttendanceHistoy_Table.OnCreate(db);
        Question_Table.OnCreate(db);
        QuestionSet_Table.OnCreate(db);
        QuestionSetBelonging_Table.OnCreate(db);
        PeopleGroupBelongin_Table.OnCreate(db);
        DeviceEventLog_Table.OnCreate(db);
        //Sites_Table.OnCreate(db);
		AttendanceSheet_Table.OnCreate(db);
		SitesVisitedLog_Table.OnCreate(db);
		Address_Table.OnCreate(db);
		PersonLogger_Table.OnCreate(db);
		SitesInfo_Table.OnCreate(db);
		SitePerformTemplateList_Table.OnCreate(db);
		StepCountLog_Table.OnCreate(db);
		SiteList_Table.OnCreate(db);
		TemplateList_Table.OnCreate(db);
		AssignedSitePeople_Table.OnCreate(db);
		Test_Table.OnCreate(db);
		CaptchaConfig_Table.OnCreate(db);*/
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		System.out.println("oldVersion: "+oldVersion);
		System.out.println("newVersion: "+newVersion);
		OLD_DB_version=oldVersion;

		switch(oldVersion)
		{
			case 6:
				Test_Table.OnCreate(db);
				break;
			case 7:
				JOBNeed_Table.OnUpgarde(db,oldVersion,newVersion);
				break;
			case 8:
				JOBNeed_Table.OnUpgarde(db,oldVersion,newVersion);
				break;
		}

		//JOBNeed_Table.OnUpgarde(db,oldVersion,newVersion);
		/*//for new ver as 2
		DeviceEventLog_Table.OnUpgarde(db,oldVersion,newVersion);
		JOBNeed_Table.OnUpgarde(db,oldVersion,newVersion);

		//for new version as 3
		AssetDetail_Table.OnUpgarde(db,oldVersion,newVersion);
		SitesVisitedLog_Table.OnUpgarde(db,oldVersion,newVersion);

		//for new version as 4
		PeopleEventLog_Table.OnUpgarde(db,oldVersion,newVersion);
		Sites_Table.OnUpgarde(db,oldVersion,newVersion);

		//for new version as 5
		PersonLogger_Table.OnUpgarde(db,oldVersion,newVersion);

		People_Table.onUpgrade(db,oldVersion,newVersion);
		QuestionSet_Table.OnUpgarde(db,oldVersion,newVersion);
		AttendanceSheet_Table.onUpgrade(db,oldVersion,newVersion);

		SiteList_Table.OnUpgarde(db,oldVersion,newVersion);*/
	}
	
	private boolean checkdatabase(){
		boolean checkdb=false;
		try{
			
			String myPath=DB_PATH;
			Log.i("myPath","chekdatabase "+myPath);
			File file=new File(myPath);
			checkdb=file.exists();
			
		}catch(SQLiteException e){
			e.printStackTrace();
			System.out.println("exception checkdatabase Database doesn't exist");
		}
		return checkdb;
		
	}
	
	public void opendatabase(){
		try{
			
			String dbPath=DB_PATH;
			objSQLiteDatabase= SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
			isDBOpen=true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void createdatabase(){
		boolean dbexist = checkdatabase();
		System.out.println("dbexist "+dbexist);
		if(!dbexist) {
			this.getReadableDatabase();
			try {
				copydatabase();
			} catch(IOException e) {
				e.printStackTrace();
				//throw new Error("Error copying database");
			}
			
			closeDatabases();
		}
	}
	public synchronized void closeDatabases(){
		isDBOpen = false;
		
		if(objSQLiteDatabase != null)
			objSQLiteDatabase.close();
		if(objSurveySQLOpenHelper != null)
		{
			objSurveySQLOpenHelper.close();
			System.out.println("database closed");
		}
			objSQLiteDatabase = null;
		objSurveySQLOpenHelper = null;
	}
	private void copydatabase() throws IOException
	{
		/*InputStream is = new FileInputStream(new File(DB_PATH));
		String out = Environment.getExternalStorageDirectory().toString() + "/"+ Constants.FOLDER_NAME+"/" + SqliteOpenHelper.DB_NAME;
		System.out.println("external dir: "+out.toString());
		OutputStream os = new FileOutputStream(out);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		os.flush();
		os.close();
		is.close();
		os=null;
		is=null;*/
	}
}
