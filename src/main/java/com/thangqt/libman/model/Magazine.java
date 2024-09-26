package com.thangqt.libman.model;

public class Magazine extends Material{
    private String issueNumber;
    private String currentIssue;
    private String issn;

    public Magazine(String title, String publisher, String issn) {

    }

    public void setIssn(String issn) {
        this.issn = issn;
    }
}
