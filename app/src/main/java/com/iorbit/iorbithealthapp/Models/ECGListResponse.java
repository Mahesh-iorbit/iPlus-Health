package com.iorbit.iorbithealthapp.Models;

import java.util.ArrayList;
import java.util.List;

public class ECGListResponse {

    private List<Ecgschema> ecgschema = new ArrayList<Ecgschema>();
    public List<Ecgschema> getEcgschema() {
        return ecgschema;
    }
    public void setEcgschema(List<Ecgschema> ecgschema) {
        this.ecgschema = ecgschema;
    }

    public static class Ecgschema {
        private String measTimeStamp;
        private Integer measurementId;
        private String firstname;
        private String lastname;
        private String phone;
        public String getMeasTimeStamp() {
            return measTimeStamp;
        }
        public void setMeasTimeStamp(String measTimeStamp) {
            this.measTimeStamp = measTimeStamp;
        }
        public Integer getMeasurementId() {
            return measurementId;
        }
        public void setMeasurementId(Integer measurementId) {
            this.measurementId = measurementId;
        }
        public String getFirstname() {
            return firstname;
        }
        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }
        public String getLastname() {
            return lastname;
        }
        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
