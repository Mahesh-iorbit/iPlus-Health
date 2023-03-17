package com.iorbit.iorbithealthapp.Models;

public class RegisterUserModel {

    int status, IsHospitalAdmin;
    String HospitalName;
    String HospitalID;
    String RegNo;
    String MedicalRegistrationCertificateLink,DoctorLink;
    String HospitalPrefix;
    int id;
    String lastName;
    String userPassword;
    String EstablishmentCertificateLink;
    String userName;
    String userEmail;
    String userId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsHospitalAdmin() {
        return IsHospitalAdmin;
    }

    public void setIsHospitalAdmin(int isHospitalAdmin) {
        IsHospitalAdmin = isHospitalAdmin;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }

    public String getHospitalID() {
        return HospitalID;
    }

    public void setHospitalID(String hospitalID) {
        HospitalID = hospitalID;
    }

    public String getRegNo() {
        return RegNo;
    }

    public void setRegNo(String regNo) {
        RegNo = regNo;
    }

    public String getMedicalRegistrationCertificateLink() {
        return MedicalRegistrationCertificateLink;
    }

    public void setMedicalRegistrationCertificateLink(String medicalRegistrationCertificateLink) {
        MedicalRegistrationCertificateLink = medicalRegistrationCertificateLink;
    }

    public String getDoctorLink() {
        return DoctorLink;
    }

    public void setDoctorLink(String doctorLink) {
        DoctorLink = doctorLink;
    }

    public String getHospitalPrefix() {
        return HospitalPrefix;
    }

    public void setHospitalPrefix(String hospitalPrefix) {
        HospitalPrefix = hospitalPrefix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getEstablishmentCertificateLink() {
        return EstablishmentCertificateLink;
    }

    public void setEstablishmentCertificateLink(String establishmentCertificateLink) {
        EstablishmentCertificateLink = establishmentCertificateLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
