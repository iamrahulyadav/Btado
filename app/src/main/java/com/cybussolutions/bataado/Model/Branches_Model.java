package com.cybussolutions.bataado.Model;

/**
 * Created by Rizwan Jillani on 03-Apr-18.
 */
public class Branches_Model {
    String branchName;
    String branchAddress;

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getBranchContact() {
        return branchContact;
    }

    public void setBranchContact(String branchContact) {
        this.branchContact = branchContact;
    }

    public String getBranchEmail() {
        return branchEmail;
    }

    public void setBranchEmail(String branchEmail) {
        this.branchEmail = branchEmail;
    }

    public String getBranchTiming() {
        return branchTiming;
    }

    public void setBranchTiming(String branchTiming) {
        this.branchTiming = branchTiming;
    }

    String branchContact;
    String branchEmail;
    String branchTiming;
}
