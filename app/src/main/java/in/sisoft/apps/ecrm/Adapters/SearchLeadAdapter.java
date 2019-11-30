package in.sisoft.apps.ecrm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.sisoft.apps.ecrm.Activities.ViewLeadActivity;
import in.sisoft.apps.ecrm.Models.Lead;
import in.sisoft.apps.ecrm.R;


public class SearchLeadAdapter extends RecyclerView.Adapter<SearchLeadAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Lead> list;

    public SearchLeadAdapter(Context context,ArrayList<Lead> list) {
        this.list = list;
        this.context = context;

    }

    @NonNull
    @Override
    public SearchLeadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lead, parent,
                false);
        return new SearchLeadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchLeadAdapter.ViewHolder holder, int position) {
        Lead task = list.get(position);

        holder.sno.setText(""+(position+1));
        holder.tv_query_type.setText(task.getQry_type());
        holder.company_name.setText(task.getCompany_name());
        holder.tv_name.setText(task.getName());

        holder.main_layout.setOnClickListener(v -> {

            Intent intent = new Intent(context, ViewLeadActivity.class);
            intent.putExtra("position",holder.getAdapterPosition());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_query_type;
        private TextView company_name;
        private TextView tv_name;
        private TextView sno;
        private RelativeLayout main_layout;

        ViewHolder(View itemView) {
            super(itemView);

            tv_query_type = itemView.findViewById(R.id.tv_query_type);
            company_name = itemView.findViewById(R.id.company_name);
            tv_name = itemView.findViewById(R.id.name);
            sno = itemView.findViewById(R.id.sno);
            main_layout = itemView.findViewById(R.id.main_layout);
        }
    }
}
