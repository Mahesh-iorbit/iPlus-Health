package com.iorbit.iorbithealthapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPatientModel {
    @SerializedName("patientschema")
    @Expose
    private List<PatientModel> patientschema = null;
    @SerializedName("status")
    @Expose
    private Status status;

    public List<PatientModel> getPatientschema() {
        return patientschema;
    }

    public void setPatientschema(List<PatientModel> patientschema) {
        this.patientschema = patientschema;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public class Status {

        @SerializedName("message")
        @Expose
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
