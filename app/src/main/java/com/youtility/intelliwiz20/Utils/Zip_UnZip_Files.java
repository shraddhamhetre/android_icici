package com.youtility.intelliwiz20.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.util.Log;

public class Zip_UnZip_Files {
	
	private Context context;
	public Zip_UnZip_Files(Context context)
	{
	this.context=context;	
	}

	public boolean createZip(File zipFileName, File[] selected)  {
		boolean ret=false;
		try { 
			byte[] buffer = new byte[1024];
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipFileName,false));//create a new zip file every time
			out.setLevel(Deflater.DEFAULT_COMPRESSION);
			for (int i = 0; i < selected.length; i++) {
				if (selected[i].exists()) {
					try {
						FileInputStream in = new FileInputStream(selected[i]);
						out.putNextEntry(new ZipEntry(selected[i].getPath()));
						int len;
						while ((len = in.read(buffer)) > 0) {
							out.write(buffer, 0, len);
						}
						out.closeEntry();
						in.close();
						ret = true;
					} catch (FileNotFoundException fnfe) {
						Log.d("Zip_UnZip_Files", " FileNotFoundException " + fnfe);
						fnfe.printStackTrace();
					} catch (IOException ioe) {
						Log.d("Zip_UnZip_Files", " IOException " + ioe);
						ioe.printStackTrace();
					}
				}
			}
			out.close();
		
		} catch (FileNotFoundException fnfe) {
			Log.d("Zip_UnZip_Files"," FileNotFoundException "+fnfe);
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			Log.d("Zip_UnZip_Files"," IOException "+ioe);
			ioe.printStackTrace();
		}
		return ret;
	}
	
	
	public void unzipFunction(String destinationFolder, String zipFile) 
	{
		ArrayList<String>fileList=new ArrayList<String>();
		File directory = new File(destinationFolder);
        
		// if the output directory doesn't exist, create it
		if(!directory.exists()) 
			directory.mkdirs();

		// buffer for read and write data to file
		byte[] buffer = new byte[2048];
        
		try {
			FileInputStream fInput = new FileInputStream(zipFile);
			ZipInputStream zipInput = new ZipInputStream(fInput);
            
			ZipEntry entry = zipInput.getNextEntry();
            
			while(entry != null){
				String entryName = entry.getName();
				File file = new File(destinationFolder + File.separator + entryName);
                
				System.out.println("Unzip file " + entryName + " to " + file.getAbsolutePath());
				fileList.add(file.getAbsolutePath());
				// create the directories of the zip directory
				if(entry.isDirectory()) {
					File newDir = new File(file.getAbsolutePath());
					if(!newDir.exists()) {
						boolean success = newDir.mkdirs();
						if(success == false) {
							System.out.println("Problem creating Folder");
						}
					}
                }
				else {
					FileOutputStream fOutput = new FileOutputStream(file);
					int count = 0;
					while ((count = zipInput.read(buffer)) > 0) {
						// write 'count' bytes to the file output stream
						fOutput.write(buffer, 0, count);
					}
					fOutput.close();
				}
				// close ZipEntry and take the next one
				zipInput.closeEntry();
				entry = zipInput.getNextEntry();
			}
            
			// close the last ZipEntry
			zipInput.closeEntry();
            
			zipInput.close();
			fInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(fileList.size()>0)
		{
			for(int i=0;i<fileList.size();i++)
			{
				deleteFileOrFolder(fileList.get(i).toString().trim());
			}
		}
		deleteFileOrFolder(destinationFolder);
		deleteFileOrFolder(zipFile);
	}
	
	private void deleteFileOrFolder(String fileName)
	{
		File zipfile = new File(fileName);
		if(zipfile.exists())
			zipfile.delete();
	}
	

}
