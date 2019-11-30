package in.sisoft.apps.ecrm.Adapters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.sisoft.apps.ecrm.AppGlobal;
import in.sisoft.apps.ecrm.Models.Task;
import in.sisoft.apps.ecrm.R;

/**
 * Created by MRKD on 21-04-2018.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private ArrayList<Task> taskList;

    private CallbackListner listner;
    private Context context;


    public TaskAdapter(Context context) {
        this.taskList = AppGlobal.taskList;
        this.context = context;

    }

    public void setupCallbackListener(CallbackListner listner){
        this.listner = listner;
    }

    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_task, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TaskAdapter.MyViewHolder holder, final int position) {

        Task task = taskList.get(position);

        holder.tv_serial_no.setText("# "+(position+1));
        holder.tv_query_id.setText(task.getLeadQryId());
        holder.tv_lead_name.setText(task.getName());
        holder.tv_lead_phone.setText(task.getPhoneNo());
        holder.tv_lead_details.setText(task.getQryDetails());
        holder.tv_assigned_user.setText(task.getAssignedUser());
        holder.tv_date_time.setText(task.getTaskDateTime());
        holder.tv_task_type.setText(task.getTaskType());
        holder.tv_naration.setText(task.getNarration());
        holder.tv_status.setText(task.getStatus());
        holder.tv_created_by.setText(task.getCreatedBy());

        //onclick button
        holder.btn_update_task.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                int position = holder.getAdapterPosition();

                listner.onCallback(position);

                }
        });

        holder.btn_chat_whatsapp.setOnClickListener(view->{
            lauchWhatsapp(task.getPhoneNo());
        });


    }

    private void lauchWhatsapp(String phone) {

        if(!phone.isEmpty()){
            String smsNumber = "";
            if(phone.length()==10){
                smsNumber ="91"+ phone;
            }
            else if(phone.length()==13){
                smsNumber = phone.substring(1);
            }
            boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
            if (isWhatsappInstalled) {

                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix

                context.startActivity(sendIntent);
            } else {
                Uri uri = Uri.parse("market://details?id=com.whatsapp");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                Toast.makeText(context, "WhatsApp not Installed",
                        Toast.LENGTH_SHORT).show();
                context.startActivity(goToMarket);
            }


        }else Toast.makeText(context, "Phone number is not valid", Toast.LENGTH_SHORT).show();



    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_serial_no;
        TextView tv_query_id;
        TextView tv_lead_name;
        TextView tv_lead_phone;
        TextView tv_lead_details;
        TextView tv_assigned_user;
        TextView tv_date_time;
        TextView tv_task_type;
        TextView tv_naration;
        TextView tv_status;
        TextView tv_created_by;
        Button btn_update_task;
        Button btn_chat_whatsapp;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_serial_no = itemView.findViewById(R.id.serial_no);
            tv_query_id = itemView.findViewById(R.id.query_id);
            tv_lead_name = itemView.findViewById(R.id.lead_name);
            tv_lead_phone = itemView.findViewById(R.id.lead_phone);
            tv_lead_details = itemView.findViewById(R.id.lead_details);
            tv_assigned_user = itemView.findViewById(R.id.assigned_user);
            tv_date_time = itemView.findViewById(R.id.task_date_time);
            tv_task_type = itemView.findViewById(R.id.task_type);
            tv_naration = itemView.findViewById(R.id.naration);
            tv_status = itemView.findViewById(R.id.status);
            tv_created_by = itemView.findViewById(R.id.created_by);
            btn_update_task = itemView.findViewById(R.id.btn_update_task);
            btn_chat_whatsapp = itemView.findViewById(R.id.btn_chat_whatsapp);


        }
    }

  public interface CallbackListner{
      void onCallback(int position);
  }
}
