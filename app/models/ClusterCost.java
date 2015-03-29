package models;

public class ClusterCost{

    private String project;
    private String date;
    private Long mrv1JobCount;
    private Double mrv1Cost;
    private Long mrv2JobCount;
    private Double mrv2Cost;
    private Double totalCost;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getMrv1JobCount() {
        return mrv1JobCount;
    }

    public void setMrv1JobCount(Long mrv1JobCount) {
        this.mrv1JobCount = mrv1JobCount;
    }

    public Long getMrv2JobCount() {
        return mrv2JobCount;
    }

    public void setMrv2JobCount(Long mrv2JobCount) {
        this.mrv2JobCount = mrv2JobCount;
    }

    public Double getMrv1Cost() {
        return mrv1Cost;
    }

    public void setMrv1Cost(Double mrv1Cost) {
        this.mrv1Cost = mrv1Cost;
    }

    public Double getMrv2Cost() {
        return mrv2Cost;
    }

    public void setMrv2Cost(Double mrv2Cost) {
        this.mrv2Cost = mrv2Cost;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}