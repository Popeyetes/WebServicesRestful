package com.example.webservicesrestful.model;

import com.google.gson.annotations.SerializedName;

public class Report {

    @SerializedName("id")
    private int id;

    @SerializedName(value = "municipality", alternate = {"title", "name"})
    private String municipality;

    @SerializedName(value = "symptomSummary", alternate = {"body", "summary"})
    private String symptomSummary;

    @SerializedName(value = "riskLevel", alternate = {"userId", "status"})
    private String riskLevel;

    public Report() {
    }

    // Constructor for creating new records (POST)
    public Report(String municipality, String symptomSummary, String riskLevel) {
        this.municipality = municipality;
        this.symptomSummary = symptomSummary;
        this.riskLevel = riskLevel;
    }

    public Report(int id, String municipality, String symptomSummary, String riskLevel) {
        this.id = id;
        this.municipality = municipality;
        this.symptomSummary = symptomSummary;
        this.riskLevel = riskLevel;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getSymptomSummary() {
        return symptomSummary;
    }

    public void setSymptomSummary(String symptomSummary) {
        this.symptomSummary = symptomSummary;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    public String toString() {
        return "Report {" +
                "id=" + id +
                ", municipality='" + municipality + '\'' +
                ", symptomSummary='" + symptomSummary + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                '}';
    }
}
