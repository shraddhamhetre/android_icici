package com.youtility.intelliwiz20.Services;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.Utils.Constants;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by youtility4 on 4/9/17.
 */

public class YoutilityServer {

    private Context context;
    private String lastSyncTime;
    private MediaType JSON;
    private OkHttpClient client;
    private DateFormat dateFormat;
    private FileOutputStream fos;
    private InputStream is;
    byte[] buffer = null;
    int byteread = 0;
    private String ROOT = null;
    //private final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath().toString() +"/"+ Constants.FOLDER_NAME;

    public YoutilityServer(Context context)
    {
        this.context=context;
        prepareHTTPConnection();
    }
    public YoutilityServer(Context context, String lastSyncTime)
    {
        this.context=context;
        this.lastSyncTime=lastSyncTime;
        prepareHTTPConnection();
    }

    private void prepareHTTPConnection()
    {
        ROOT=context.getFilesDir().getPath();
        client = new OkHttpClient();
        JSON = MediaType.parse("application/json; charset=utf-8");
        dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    }
    public boolean downloadData(String serviceName, String query, String story, String fileName)
    {
        //"http://192.168.1.125:8000/index/"

        System.out.println("ServiceName: "+serviceName);
        System.out.println("Query: "+query);
        System.out.println("Filename: "+fileName);

        UploadParameters uploadParameters=new UploadParameters();
        uploadParameters.setServicename(serviceName);
        uploadParameters.setQuery(query);
        uploadParameters.setStory(story);

        String filepath = ROOT +"/"+ fileName;
        File f=new File(filepath);
        try {
            if(f.exists())
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL(Constants.BASE_URL); // here is your URL path
            /*JSONObject postDataParams = new JSONObject();
            postDataParams.put("servicename",serviceName);
            postDataParams.put("query", query);
            postDataParams.put("story", 1);*/

            Gson gson = new Gson();
            String upData = gson.toJson(uploadParameters);
            System.out.println("upData: "+upData);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            StringEntity data=new StringEntity(upData, HTTP.UTF_8);
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            is = data.getContent();
            buffer = new byte[1024];
            byteread = 0;
            while ((byteread = is.read(buffer)) != -1)
            {
                out.write(buffer, 0, byteread);
            }
            out.flush();
            out.close();
            is.close();

            /*OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();*/

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";

                FileWriter fWriter = new FileWriter(filepath, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fWriter);

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    bufferedWriter.write(line);
                    break;
                }

                in.close();
                bufferedWriter.flush();
                bufferedWriter.close();
                System.out.println("SB: "+fileName+" : "+sb.toString());
                return true;
            }
            else {
                System.out.println("SB: "+responseCode);
                return  false;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
            return  false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return  false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return  false;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

    }

    /*public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }*/

}
