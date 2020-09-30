package com.youtility.intelliwiz20.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youtility.intelliwiz20.DataAccessObject.JobNeedDAO;
import com.youtility.intelliwiz20.DataAccessObject.JobNeedDetailsDAO;
import com.youtility.intelliwiz20.DataAccessObject.QuestionDAO;
import com.youtility.intelliwiz20.DataAccessObject.TypeAssistDAO;
import com.youtility.intelliwiz20.Model.JobNeed;
import com.youtility.intelliwiz20.Model.JobNeedDetails;
import com.youtility.intelliwiz20.R;

import java.util.ArrayList;

public class RequestViewDetailsActivity extends AppCompatActivity {

    private JobNeed jobNeed;
    private JobNeedDAO jobNeedDAO;
    private TypeAssistDAO typeAssistDAO;
    private QuestionDAO questionDAO;
    private JobNeedDetailsDAO jobNeedDetailsDAO;
    private TextView requestDetails;
    private ArrayList<JobNeedDetails> jobNeedDetailsHistoryArrayList;
    private long jobneedid=-1;
    private RecyclerView mRecyclerView;
    private RequestDetailsAdapter requestDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_view_details);

        jobNeedDAO=new JobNeedDAO(RequestViewDetailsActivity.this);
        typeAssistDAO=new TypeAssistDAO(RequestViewDetailsActivity.this);
        questionDAO=new QuestionDAO(RequestViewDetailsActivity.this);
        jobNeedDetailsDAO=new JobNeedDetailsDAO(RequestViewDetailsActivity.this);

        jobneedid=getIntent().getLongExtra("REQUESTID",-1);
        if(jobneedid!=-1)
        {
            jobNeed=jobNeedDAO.getJobNeedDetails(jobneedid);
        }
        requestDetails=(TextView)findViewById(R.id.request_details);
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view_request_details);
        if(jobNeed!=null)
        {
            jobNeedDetailsHistoryArrayList=new ArrayList<JobNeedDetails>();
            jobNeedDetailsHistoryArrayList=jobNeedDetailsDAO.getJobNeedDetailQuestList(jobNeed.getJobneedid());

            if (jobNeedDetailsHistoryArrayList != null && jobNeedDetailsHistoryArrayList.size() > 0) {
                requestDetailsAdapter = new RequestDetailsAdapter(jobNeedDetailsHistoryArrayList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RequestViewDetailsActivity.this);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(requestDetailsAdapter);
            }

            ((TextView) findViewById(R.id.request_id)).setText(String.valueOf(jobNeed.getJobneedid()));
            ((TextView) findViewById(R.id.request_type)).setText(String.valueOf(jobNeed.getRemarks()));
            ((TextView) findViewById(R.id.request_on)).setText((jobNeed.getPlandatetime()));

            requestDetails.setText(questionDAO.getQuestionSetName(jobNeed.getQuestionsetid()));
        }

    }

    public class RequestDetailsAdapter extends RecyclerView.Adapter<RequestDetailsAdapter.ViewHolder>
    {
        private ArrayList<JobNeedDetails> jobNeedHistories;

        public RequestDetailsAdapter(ArrayList<JobNeedDetails> jobNeedHistories)
        {
            this.jobNeedHistories=jobNeedHistories;
        }

        @Override
        public RequestDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.task_history_reading_row, parent, false);
            return new RequestDetailsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RequestDetailsAdapter.ViewHolder holder, int position) {
            holder.txtSrNo.setText(String.valueOf(position+1)+": ");
            holder.txtQName.setText(questionDAO.getQuestionName(jobNeedHistories.get(position).getQuestionid()));
            holder.txtQName.setTextColor(getResources().getColor(R.color.colorGray));
            holder.txtQAns.setText(jobNeedHistories.get(position).getAnswer());
        }

        @Override
        public int getItemCount() {
            return jobNeedHistories.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView txtSrNo, txtQName, txtQAns;

            ViewHolder(View itemView) {
                super(itemView);
                txtSrNo = (TextView) itemView.findViewById(R.id.srNoTextView);
                txtQName = (TextView) itemView.findViewById(R.id.questNameTextView);
                txtQAns = (TextView) itemView.findViewById(R.id.questAnsTextView);
            }
        }
    }
}
