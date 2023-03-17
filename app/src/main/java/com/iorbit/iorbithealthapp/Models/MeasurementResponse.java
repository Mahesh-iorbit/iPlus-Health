package com.iorbit.iorbithealthapp.Models;

import java.util.ArrayList;
import java.util.List;

public class MeasurementResponse {
        private List<Measurement> measurement = new ArrayList<Measurement>();
        private StatusDetails statusDetails;
        public List<Measurement> getMeasurement() {
            return measurement;
        }
        public void setMeasurement(List<Measurement> measurement) {
            this.measurement = measurement;
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

        public class Measurement {
            private String patientId;
            private Integer deviceID;
            private String firstName;
            private String lastName;
            private String gender;
            private String phone;
            private String deviceName;
            private String paramName;
            private String measTimeStamp;
            private String recvdTimeStamp;
            private String readingValues;
            private String deviceImageName;
            public String getPatientId() {
                return patientId;
            }
            public void setPatientId(String patientId) {
                this.patientId = patientId;
            }
            public Integer getDeviceID() {
                return deviceID;
            }
            public void setDeviceID(Integer deviceID) {
                this.deviceID = deviceID;
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
            public String getGender() {
                return gender;
            }
            public void setGender(String gender) {
                this.gender = gender;
            }
            public String getPhone() {
                return phone;
            }
            public void setPhone(String phone) {
                this.phone = phone;
            }
            public String getDeviceName() {
                return deviceName;
            }
            public void setDeviceName(String deviceName) {
                this.deviceName = deviceName;
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
            public String getRecvdTimeStamp() {
                return recvdTimeStamp;
            }
            public void setRecvdTimeStamp(String recvdTimeStamp) {
                this.recvdTimeStamp = recvdTimeStamp;
            }
            public String getReadingValues() {
                return readingValues;
            }
            public void setReadingValues(String readingValues) {
                this.readingValues = readingValues;
            }
            public String getDeviceImageName() {
                return deviceImageName;
            }
            public void setDeviceImageName(String deviceImageName) {
                this.deviceImageName = deviceImageName;
            }
        }
}

