package com.iorbit.iorbithealthapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.iorbit.iorbithealthapp.Models.BodyTempAndSPO2Model;
import com.iorbit.iorbithealthapp.R;


import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<BodyTempAndSPO2Model> dataSet;
    private String testType,unit;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewValue;
        TextView textViewType;
        TextView textViewDate;
        TextView textViewHeading;
        ImageView imageViewIcon;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewValue = (TextView) itemView.findViewById(R.id.value);
            this.textViewType = (TextView) itemView.findViewById(R.id.type);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.img);
            this.textViewDate = itemView.findViewById(R.id.date);
            this.textViewHeading = itemView.findViewById(R.id.heading);
        }
    }

    public CustomAdapter(ArrayList<BodyTempAndSPO2Model> data, String testType, String unit) {
        this.dataSet = data;
        this.testType = testType;
        this.unit= unit;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chart_card_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewValue = holder.textViewValue;
        TextView textViewType = holder.textViewType;
        ImageView imageView = holder.imageViewIcon;
        TextView textViewDate = holder.textViewDate;
        TextView textViewHeading = holder.textViewHeading;

        textViewValue.setText(dataSet.get(listPosition).getValue() + unit);
        textViewType.setText(dataSet.get(listPosition).getType());
        imageView.setImageResource(dataSet.get(listPosition).getImg());
        textViewDate.setText(dataSet.get(listPosition).getDate());
        textViewHeading.setText(testType);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
