package com.youtility.intelliwiz20.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.AssetDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

public class AssetAuditAdapter extends RecyclerView.Adapter<AssetAuditAdapter.AssetAuditViewHolder> {

    private Context mContext;
    private ArrayList<JobNeed>jobNeedArrayList;
    private AssetDAO assetDAO;
    public AssetAuditAdapter(Context mContext, ArrayList<JobNeed> jobNeedArrayList)
    {
        this.mContext=mContext;
        this.jobNeedArrayList=jobNeedArrayList;
        assetDAO=new AssetDAO(mContext);
    }

    @Override
    public AssetAuditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_asset_audit_cardsview, parent, false);
        //view.setOnClickListener(AssetAuditActivity.myOnClickListener);

        return new AssetAuditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AssetAuditViewHolder holder, final int position) {
        System.out.println("jobNeedArrayList.get(position).getSyncstatus(): "+jobNeedArrayList.get(position).getSyncstatus());
        if(jobNeedArrayList.get(position).getSyncstatus()==0)
        {
            holder.assetNameTextview.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.bulletinprogress),null,null,null);
        }
        else
        {
            holder.assetNameTextview.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.drawable.bulletcompleted),null,null,null);
        }
        holder.assetNameTextview.setText(assetDAO.getAssetName(jobNeedArrayList.get(position).getAssetid()));
        String text = "<font color=#18B064>"+((jobNeedArrayList.get(position).getPlandatetime()))+"</font> <font color=#919191>"+" "+jobNeedArrayList.get(position).getJobdesc()+"</font>";
        holder.planDateTextview.setText(Html.fromHtml(text));
    }

    @Override
    public int getItemCount() {
        return jobNeedArrayList.size();
    }

    class AssetAuditViewHolder extends RecyclerView.ViewHolder {

        TextView assetNameTextview;
        TextView planDateTextview;

        AssetAuditViewHolder(View itemView) {
            super(itemView);
            assetNameTextview = (TextView) itemView.findViewById(R.id.aauditAssetName);
            planDateTextview=(TextView)itemView.findViewById(R.id.aauditPlanDate);
        }
    }
}
