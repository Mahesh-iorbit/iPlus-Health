package com.iorbit.iorbithealthapp.Adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.R;


import java.util.ArrayList;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.MyViewHolder> {

    private static ClickListener clickListener;
    private ArrayList<String> dataSet;
    private ArrayList<String> name;
    private Context mContext;


    public PatientListAdapter(Context context, ArrayList<String> data, ArrayList<String> name) {
        this.dataSet = data;
        this.name = name;
        this.mContext = context;
    }



    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patinet_list_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        String sr= new SharedPreference(mContext).getCurrentPAtient().getSsid();
        if (!name.get(listPosition).equalsIgnoreCase("")) {
            holder.textViewPid.setText(name.get(listPosition));
            holder.textviewSsid.setText(dataSet.get(listPosition));
            if (listPosition==0) {
                holder.delete_button.setEnabled(false);
                holder.delete_button.setImageResource(R.drawable.delete_btn);
                holder.textViewPid.setText(name.get(listPosition));
                holder.textviewSsid.setText(dataSet.get(listPosition));
            }
            if(sr.equalsIgnoreCase(dataSet.get(listPosition))){
                holder.delete_button.setEnabled(false);
                holder.delete_button.setImageResource(R.drawable.delete_btn);
                holder.textViewPid.setText(name.get(listPosition));
                holder.textviewSsid.setText(dataSet.get(listPosition));
            }

        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onEditItemClick(int position, View v);
        void onDeleteItemClick(int position, View v);

    }



    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewPid, textviewSsid;
        ImageButton edit_button;
        ImageButton delete_button;
        LinearLayout user_select;


        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.user_select = itemView.findViewById(R.id.member_layout);
            this.textViewPid = itemView.findViewById(R.id.pid);
            this.textviewSsid = itemView.findViewById(R.id.ssid);
            this.edit_button = itemView.findViewById(R.id.edit_patient);
            this.delete_button =itemView.findViewById(R.id.delete_patient);
            this.edit_button.setOnClickListener(this);
            this.delete_button.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.edit_patient)
                clickListener.onEditItemClick(getAdapterPosition(), view);
            else if (view.getId() == R.id.delete_patient) {

                clickListener.onDeleteItemClick(getAdapterPosition(), view);

            }
            else
                clickListener.onItemClick(getAdapterPosition(), view);

        }
    }
}
