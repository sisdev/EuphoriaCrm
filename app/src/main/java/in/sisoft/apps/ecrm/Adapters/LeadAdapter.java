package in.sisoft.apps.ecrm.Adapters;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.sisoft.apps.ecrm.Activities.ViewLeadActivity;
import in.sisoft.apps.ecrm.Models.Lead;
import in.sisoft.apps.ecrm.R;


/**
 * Created by MRKD on 21-04-2018.
 */

public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Lead> leadArrayList;
    private ArrayList<Lead> leadSearchList;


    public LeadAdapter(Context context,ArrayList<Lead> leadArrayList) {
        this.context = context;
        this.leadArrayList = leadArrayList;
        this.leadSearchList = new ArrayList<>(leadArrayList);

    }
    @NonNull
    @Override
    public LeadAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_lead, parent, false);
        return new MyViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final LeadAdapter.MyViewHolder holder, final int position) {

        Lead task = leadArrayList.get(position);

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
        return leadArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView tv_query_type;
        private TextView company_name;
        private TextView tv_name;
        private TextView sno;
        private RelativeLayout main_layout;
       /* private Button btn_view_lead;*/

        MyViewHolder(View itemView) {
            super(itemView);

            tv_query_type = itemView.findViewById(R.id.tv_query_type);
            company_name = itemView.findViewById(R.id.company_name);
            tv_name = itemView.findViewById(R.id.name);
            sno = itemView.findViewById(R.id.sno);
            main_layout = itemView.findViewById(R.id.main_layout);
          /*  btn_view_lead = itemView.findViewById(R.id.btn_view_lead);*/


        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Lead> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(leadSearchList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Lead lead : leadSearchList) {
                    if (lead.getQry_type().toLowerCase().contains(filterPattern)
                            || lead.getName().toLowerCase().contains(filterPattern)
                            || lead.getCompany_name().toLowerCase().contains(filterPattern)
                            ) {
                        filteredList.add(lead);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            leadArrayList.clear();
            leadArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
