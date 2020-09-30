package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import com.youtility.intelliwiz20.Model.Attachment;
import com.youtility.intelliwiz20.R;

/**
 * Created by PrashantD on 11/08/17.
 */

public class AVPGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Attachment>appList;
    public AVPGridViewAdapter(Context context, List<Attachment> appList)
    {
        this.context=context;
        this.appList=appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View grid=null;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            grid=new View(context);
            grid=inflater.inflate(R.layout.app_gridview_row,null);
            TextView appName = (TextView)grid.findViewById(R.id.appName);
            TextView appDesc = (TextView)grid.findViewById(R.id.appDesc);
            ImageView appImage = (ImageView)grid.findViewById(R.id.appIcon);

            appName.setText(appList.get(position).getFileName().toString().trim());
            appImage.setImageResource(R.mipmap.ic_launcher);

            Log.d("AVPT File name: ", appList.get(position).getFileName().toString());
            String[] splitname=appList.get(position).getFileName().toString().split("\\.");

            Log.d("AVPT File path: ", appList.get(position).getFilePath().toString());

            if(splitname[1].equalsIgnoreCase("png")) {
                File image = new File(appList.get(position).getFilePath().toString());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 150 , 150, true);
                appImage.setImageBitmap(bitmap);

                //ThumbnailUtils.extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

            }
            else if(splitname[1].equalsIgnoreCase("mp4"))
            {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(appList.get(position).getFilePath().toString(), MediaStore.Video.Thumbnails.MICRO_KIND);
                appImage.setImageBitmap(thumb);
            }
            else if(splitname[1].equalsIgnoreCase("3gp"))
            {
                appImage.setImageDrawable(context.getResources().getDrawable(R.drawable.microphone_active));
            }

        }
        else
        {
            grid = (View)convertView;
        }

        return grid;
    }
}
