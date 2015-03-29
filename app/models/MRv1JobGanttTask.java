package models;

import play.db.ebean.Model;

import java.util.Date;

@SuppressWarnings("serial")
public class MRv1JobGanttTask extends Model {

    private String id;
    private String parentID;
    private Integer orderId;
    private String title;
    private Date start;
    private Date end;
    private Double percentComplete;
    private Boolean summary;
    private Boolean expanded;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Double getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(Double percentComplete) {
        this.percentComplete = percentComplete;
    }

    public Boolean getSummary() {
        return summary;
    }

    public void setSummary(Boolean summary) {
        this.summary = summary;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }
}