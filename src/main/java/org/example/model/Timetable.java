package org.example.model;

import javax.xml.crypto.Data;
import java.util.Date;

public class Timetable {
//    private Integer id;

    private String timetableName;
    private Date uploadDate;
    private Boolean isCurrent;
    private Integer previousVersionId;
    private Data actualTo;

    public Timetable(String timetableName, Date uploadDate, Boolean isCurrent, Integer previousVersionId) {
        this.timetableName = timetableName;
        this.uploadDate = uploadDate;
        this.isCurrent = isCurrent;
        this.previousVersionId = previousVersionId;
        this.actualTo = actualTo;
    }

    public String getTimetableName() {
        return timetableName;
    }

    public void setTimetableName(String timetableName) {
        this.timetableName = timetableName;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Boolean getIsActual() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public Integer getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(Integer previousVersionId) {
        this.previousVersionId = previousVersionId;
    }

    public Data getActualTo() {
        return actualTo;
    }

    public void setActualTo(Data actualTo) {
        this.actualTo = actualTo;
    }
}
