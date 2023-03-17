package com.iorbit.iorbithealthapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.iorbit.iorbithealthapp.Models.ECGListResponse;
import com.iorbit.iorbithealthapp.R;
import java.util.ArrayList;

public class ECGAdapter extends RecyclerView.Adapter<ECGAdapter.MyViewHolder> {
    private static ArrayList<ECGListResponse.Ecgschema> data;
    private static ClickListener clickListener;


    public ECGAdapter(ArrayList<ECGListResponse.Ecgschema> ecgschema) {
        this.data=ecgschema;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ecg_adapter_layout, parent, false);

        //view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.textViewHeading.setText(String.valueOf(data.get(position).getMeasurementId()));
       holder.textViewType.setText(data.get(position).getFirstname());
       holder.textViewDate.setText(data.get(position).getMeasTimeStamp());
       holder.imageViewIcon.setImageResource(R.drawable.ecgp);

    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onEditItemClick(int position, View v);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

      //  TextView textViewValue;
        TextView textViewType;
        TextView textViewDate;
        TextView textViewHeading;
        ImageView imageViewIcon;


        public MyViewHolder(View itemView) {
            super(itemView);
         //   this.textViewValue = (TextView) itemView.findViewById(R.id.value);
            this.textViewType = (TextView) itemView.findViewById(R.id.type);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.img);
            this.textViewDate = itemView.findViewById(R.id.date);
            this.textViewHeading = itemView.findViewById(R.id.heading);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
