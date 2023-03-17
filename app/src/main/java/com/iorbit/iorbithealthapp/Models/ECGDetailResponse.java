package com.iorbit.iorbithealthapp.Models;

import java.util.ArrayList;
import java.util.List;

public class ECGDetailResponse {
    private List<Measure> measure = new ArrayList<Measure>();
    private StatusDetails statusDetails;
    public List<Measure> getMeasure() {
        return measure;
    }
    public void setMeasure(List<Measure> measure) {
        this.measure = measure;
    }
    public StatusDetails getStatusDetails() {
        return statusDetails;
    }
    public void setStatusDetails(StatusDetails statusDetails) {
        this.statusDetails = statusDetails;
    }

    public class StatusDetails {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
    public class Measure {
        private String measurementid;
        private String patientId;
        private String firstName;
        private String lastName;
        private String paramName;
        private String measTimeStamp;
        private String readingValues;

        public String getParamFraction() {
            return paramFraction;
        }

        public void setParamFraction(String paramFraction) {
            this.paramFraction = paramFraction;
        }

        private String paramFraction;
        public String getMeasurementid() {
            return measurementid;
        }
        public void setMeasurementid(String measurementid) {
            this.measurementid = measurementid;
        }
        public String getPatientId() {
            return patientId;
        }
        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
        public String getParamName() {
            return paramName;
        }
        public void setParamName(String paramName) {
            this.paramName = paramName;
        }
        public String getMeasTimeStamp() {
            return measTimeStamp;
        }
        public void setMeasTimeStamp(String measTimeStamp) {
            this.measTimeStamp = measTimeStamp;
        }
        public String getReadingValues() {
            return readingValues;
        }
        public void setReadingValues(String readingValues) {
            this.readingValues = readingValues;
        }
    }

    public class Leads
    {
        private List<String> lead1 = new ArrayList<String>();
        private List<String> lead3 = new ArrayList<String>();
        private List<String> lead2 = new ArrayList<String>();
        private List<String> lead5 = new ArrayList<String>();
        private List<String> lead4 = new ArrayList<String>();
        private List<String> lead6 = new ArrayList<String>();
        public List<String> getLead1() {
            return lead1;
        }
        public void setLead1(List<String> lead1) {
            this.lead1 = lead1;
        }
        public List<String> getLead3() {
            return lead3;
        }
        public void setLead3(List<String> lead3) {
            this.lead3 = lead3;
        }
        public List<String> getLead2() {
            return lead2;
        }
        public void setLead2(List<String> lead2) {
            this.lead2 = lead2;
        }
        public List<String> getLead5() {
            return lead5;
        }
        public void setLead5(List<String> lead5) {
            this.lead5 = lead5;
        }
        public List<String> getLead4() {
            return lead4;
        }
        public void setLead4(List<String> lead4) {
            this.lead4 = lead4;
        }
        public List<String> getLead6() {
            return lead6;
        }
        public void setLead6(List<String> lead6) {
            this.lead6 = lead6;
        }
    }
}
