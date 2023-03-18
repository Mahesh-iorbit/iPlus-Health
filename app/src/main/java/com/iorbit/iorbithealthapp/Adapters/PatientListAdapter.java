package com.iorbit.iorbithealthapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iorbit.iorbithealthapp.Models.PatientModel;
import com.iorbit.iorbithealthapp.R;

import java.io.IOException;
import java.util.List;

public class PatientListAdapter  extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    private  final Context mContext;
    private final List<PatientModel> patientModels;
    private ClickListener clickListener;

    public PatientListAdapter(Context context, List<PatientModel> patientModels, ClickListener clickListener) {
        this.mContext=context;
        this.patientModels = patientModels;
        this.clickListener = clickListener;

    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patinet_list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.bind(patientModels.get(position));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override public int getItemCount() {
        return patientModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btn_Edit,btn_Delete;
        TextView PatientName,ProductSSid;
        LinearLayout Member_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            btn_Edit = itemView.findViewById(R.id.edit_patient);
            btn_Delete = itemView.findViewById(R.id.delete_patient);
            PatientName = itemView.findViewById(R.id.pid);
            ProductSSid = itemView.findViewById(R.id.ssid);
            Member_layout = itemView.findViewById(R.id.member_layout);
        }

        public void bind(final PatientModel patient) throws IOException {
            PatientName.setText(patient.getFirstName());
            ProductSSid.setText(patient.getSsid());
            Member_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemclick(patient);
                }
            });
            btn_Edit.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    clickListener.onEditclick(patient);
                }
            });
            btn_Delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    clickListener.onDeleteclick(patient);
                }
            });
        }
    }

    public interface ClickListener{
        void onItemclick(PatientModel patientModel);
        void onDeleteclick(PatientModel patientModel);
        void onEditclick(PatientModel patientModel);
    }

}
