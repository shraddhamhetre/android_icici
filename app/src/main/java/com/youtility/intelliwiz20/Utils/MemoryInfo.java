package com.youtility.intelliwiz20.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

public class MemoryInfo 
{
	private Context context;
	File path=null;
    StatFs stat=null;
	public MemoryInfo(Context context)
	{
		this.context=context;
	}



    public void getMemoryInfo()
	{
		long blockSize=0;
		
		if (externalMemoryAvailable())
		{
			path = Environment.getDataDirectory();
			stat = new StatFs(path.getPath());
			blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			
			SharedPreferences memInfo=context.getSharedPreferences(Constants.MEMORY_INFO_PREF, Context.MODE_PRIVATE);
			SharedPreferences.Editor memEditor=memInfo.edit();
			//memEditor.putString(Constants.IMEMORY_TOTAL,Formatter.formatFileSize(context,getTotalInternalMemory()));
			memEditor.putString(Constants.IMEMORY_AVAILABLE, Formatter.formatFileSize(context,getAvailableInternalMemory()));
			//memEditor.putString(Constants.IMEMORY_USED, Formatter.formatFileSize(context, BusyMemory()));
			
			//for external Memory info
		    stat=new StatFs(Environment.getExternalStorageDirectory().getPath());
		    long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
		    long megAvailable = bytesAvailable / (1024 * 1024);
		    
		    //to get total memory
		    blockSize = stat.getBlockSize();
	        long totalBlocks = stat.getBlockCount();
	        
	        
	        //memEditor.putString(Constants.EMEMORY_TOTAL, Formatter.formatFileSize(context,getTotalExternalMemory()));
			memEditor.putString(Constants.EMEMORY_AVAILABLE, Formatter.formatFileSize(context, getAvailableExternalMemory()));
			
			memEditor.commit();
		}
	    
	}
	private long BusyMemory()
	{
		StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());   
		long Total = ( (long) statFs.getBlockCount() * (long) statFs.getBlockSize()) / 1048576;
        long Free  = (statFs.getAvailableBlocks() *  (long) statFs.getBlockSize()) / 1048576;
        long Busy  = Total - Free;
        return Busy;
	}
	
	public static long getTotalInternalMemory()
	{
	       File path = Environment.getDataDirectory();
	       StatFs stat = new StatFs(path.getPath());
	       long blockSize = stat.getBlockSize();
	       long totalBlocks = stat.getBlockCount();
	       return (totalBlocks * blockSize);
	}
	
	public static long getAvailableInternalMemory()
	{
	       File path = Environment.getDataDirectory();
	       StatFs stat = new StatFs(path.getPath());
	       long blockSize = stat.getBlockSize();
	       long availableBlocks = stat.getAvailableBlocks();
	       return (availableBlocks * blockSize);
	}
	
	public static long getAvailableExternalMemory()
	{
	       if (externalMemoryAvailable()) {
	           File path = Environment.getExternalStorageDirectory();
	           StatFs stat = new StatFs(path.getPath());
	           long blockSize = stat.getBlockSize();
	           long availableBlocks = stat.getAvailableBlocks();
	           return (availableBlocks * blockSize);
	       } else {
	           return 0l;
	       }
	   }

	public static long getTotalExternalMemory() 
	{
	       if (externalMemoryAvailable()) {
	           File path = Environment.getExternalStorageDirectory();
	           StatFs stat = new StatFs(path.getPath());
	           long blockSize = stat.getBlockSize();
	           long totalBlocks = stat.getBlockCount();
	           return (totalBlocks * blockSize);
	       } else {
	           return 0l;
	       }
	}
	
	public static boolean externalMemoryAvailable() 
	{
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

	public static boolean checkMemoryInternalAvailable(){

		long totalinternalMemory=getTotalInternalMemory();
		double requiremem=totalinternalMemory*(0.2);
		double availabkeinternalMemory=getAvailableInternalMemory();
		System.out.println("availabkeinternalMemory "+availabkeinternalMemory);
		if((availabkeinternalMemory > requiremem) || (availabkeinternalMemory > (200*1024*1024))){
			return true;
		}else{
			return false;
		}

	}

	public static boolean checkMemoryExternalAvailable(){
		double totalexternalMemory=getTotalExternalMemory();
		double requiremem=totalexternalMemory*(0.2);
		double availabkeexternalMemory=getAvailableExternalMemory();
		if((availabkeexternalMemory > requiremem) || (availabkeexternalMemory > (200*1024*1024))){
			return true;
		}else{
			return false;
		}
	}
	
}
