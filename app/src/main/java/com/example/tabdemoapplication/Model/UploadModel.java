package com.example.tabdemoapplication.Model;

public class UploadModel {
    String strImgurl, strName,strPhone,strDob,strCountry;

    public UploadModel(){

    }

    public UploadModel(String strImgurl, String strName, String strPhone, String strDob, String strCountry){
this.strImgurl=strImgurl;
this.strName=strName;
this.strPhone=strPhone;
this.strDob=strDob;
this.strCountry=strCountry;
    }

    public String getStrImgurl() {
        return strImgurl;
    }

    public void setStrImgurl(String strImgurl) {
        this.strImgurl = strImgurl;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrPhone() {
        return strPhone;
    }

    public void setStrPhone(String strPhone) {
        this.strPhone = strPhone;
    }

    public String getStrDob() {
        return strDob;
    }

    public void setStrDob(String strDob) {
        this.strDob = strDob;
    }

    public String getStrCountry() {
        return strCountry;
    }

    public void setStrCountry(String strCountry) {
        this.strCountry = strCountry;
    }
}
