package com.iorbit.iorbithealthapp.Models;


import java.util.List;

public class DisplayMeasurements {
    private List<Measurement> measure;
    private StatusDetails statusDetails;
    // getters and setters
    public List<Measurement> getMeasure() {
        return measure;
    }

    public void setMeasure(List<Measurement> measure) {
        this.measure = measure;
    }

    public StatusDetails getStatusDetails() {
        return statusDetails;
    }

    public void setStatusDetails(StatusDetails statusDetails) {
        this.statusDetails = statusDetails;
    }
    public class Measurement {
        private String measurementid;
        private String bpsystolicvalue;
        private String bpdiastolicvalue;
        private String patientId;
        private String firstName;
        private String lastName;
        private String paramName;
        private String readingValues;
        private String measuredatetime;

        // getters and setters
        public String getMeasurementid() {
            return measurementid;
        }

        public void setMeasurementid(String measurementid) {
            this.measurementid = measurementid;
        }

        public String getBpsystolicvalue() {
            return bpsystolicvalue;
        }

        public void setBpsystolicvalue(String bpsystolicvalue) {
            this.bpsystolicvalue = bpsystolicvalue;
        }

        public String getBpdiastolicvalue() {
            return bpdiastolicvalue;
        }

        public void setBpdiastolicvalue(String bpdiastolicvalue) {
            this.bpdiastolicvalue = bpdiastolicvalue;
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

        public String getReadingValues() {
            return readingValues;
        }

        public void setReadingValues(String readingValues) {
            this.readingValues = readingValues;
        }

        public String getMeasuredatetime() {
            return measuredatetime;
        }

        public void setMeasuredatetime(String measuredatetime) {
            this.measuredatetime = measuredatetime;
        }
    }

    public class StatusDetails {
        private String message;

        // getters and setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}