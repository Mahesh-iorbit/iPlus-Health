package com.iorbit.iorbithealthapp.Models;



public class PatientModel {

    String emailID, city, firstName, lastName, dateOfBirth, timeZone,Age;
    String alternateEmailID;
    String address;
    String country;
    String honorific;
    String zip;
    String gender;
    String deviceID;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;
    String alternateContactNo, ssid, weight, otherDetails, height, contactNo, bloodGroup;
    String alcohol, highBP, diabetic, smoke, createdBy,age, createdByRole;
    String stdSBP,stdDBP,factorSBP,factorDBP;

    public PatientModel(){
        this.deviceID = "";
        this.createdByRole = "";
        this.createdBy ="";
        this.emailID = "";
        this.city = "";
        this.alcohol = "0";
        this.firstName = "";
        this.lastName = "";
        this.highBP = "0";
        this.dateOfBirth = "";
        this.timeZone = "";
        this.alternateEmailID = "";
        this.address = "";
        this.country = "";
        this.honorific = "";
        this.zip = "";
        this.gender = "Male";
        this.gender = "";
        this.diabetic = "0";
        this.smoke = "0";
        this.alternateContactNo = "";
        this.ssid = "";
        this.weight = "";
        this.otherDetails = "";
        this.height = "";
        this.contactNo = "";
        this.bloodGroup = "";
        this.age = "0";
        this.stdSBP = "";
        this.stdDBP="";
        this.factorSBP="";
        this.factorDBP="";
        this.image="";
    }

    public void addPatients(String PID,String createdBy,String emailID, String city, String firstName, String lastName, String userAge, String gender,String contactNumber) {
        this.deviceID = deviceID;
        this.createdByRole = "";
        this.createdBy = createdBy;
        this.alcohol = "0";
        this.highBP = "0";
        this.dateOfBirth = "";
        this.timeZone = "";
        this.alternateEmailID = "";
        this.address = "";
        this.country = "";
        this.honorific = "";
        this.zip = "";
        this.emailID = emailID;
        this.city = city;
        this.firstName = firstName;
        this.lastName = lastName;
        age = userAge;
        this.gender = gender;
        contactNo =contactNumber;
        this.diabetic = "0";
        this.smoke = "0";
        this.alternateContactNo = "";
        this.ssid = PID;
        this.weight = "";
        this.otherDetails = "";
        this.height = "";
        this.bloodGroup = "";
        this.stdSBP = "";
        this.stdDBP="";
        this.factorSBP="";
        this.factorDBP="";
    }

    public void addPatient(String PID, String phone, String age, String city, String gender) {
        this.deviceID = deviceID;
        this.createdByRole = "";
        this.createdBy = "";
        this.emailID = "";
        this.city = city;
        this.alcohol = "0";
        this.firstName = "";
        this.lastName = "";
        this.highBP = "0";
        this.dateOfBirth = "";
        this.timeZone = "";
        this.alternateEmailID = "";
        this.address = "";
        this.country = "";
        this.honorific = "";
        this.zip = "";
        this.gender = gender;
        this.diabetic = "0";
        this.smoke = "0";
        this.alternateContactNo = "";
        this.ssid = "";
        this.weight = "";
        this.otherDetails = "";
        this.height = "";
        this.contactNo = phone;
        this.bloodGroup = "";
        this.age = age;
        this.ssid = PID;
        this.stdSBP = "";
        this.stdDBP="";
        this.factorSBP="";
        this.factorDBP="";
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getAlternateEmailID() {
        return alternateEmailID;
    }

    public void setAlternateEmailID(String alternateEmailID) {
        this.alternateEmailID = alternateEmailID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHonorific() {
        return honorific;
    }

    public void setHonorific(String honorific) {
        this.honorific = honorific;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAlternateContactNo() {
        return alternateContactNo;
    }

    public void setAlternateContactNo(String alternateContactNo) {
        this.alternateContactNo = alternateContactNo;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getHighBP() {
        return highBP;
    }

    public void setHighBP(String highBP) {
        this.highBP = highBP;
    }

    public String getDiabetic() {
        return diabetic;
    }

    public void setDiabetic(String diabetic) {
        this.diabetic = diabetic;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCreatedByRole() {
        return createdByRole;
    }

    public void setCreatedByRole(String createdByRole) {
        this.createdByRole = createdByRole;
    }


    public String getStdSBP() {
        return stdSBP;
    }

    public void setStdSBP(String stdSBP) {
        this.stdSBP = stdSBP;
    }

    public String getStdDBP() {
        return stdDBP;
    }

    public void setStdDBP(String stdDBP) {
        this.stdDBP = stdDBP;
    }

    public String getFactorSBP() {
        return factorSBP;
    }

    public void setFactorSBP(String factorSBP) {
        this.factorSBP = factorSBP;
    }

    public String getFactorDBP() {
        return factorDBP;
    }

    public void setFactorDBP(String factorDBP) {
        this.factorDBP = factorDBP;
    }



}
