package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_summary")
public class JobSummary extends Model
{
	@Id
    @Column(name = "JOBID")
    private String jobid;
    @Column(name = "JOBNAME")
    private String jobName;
    @Column(name = "USER")
    private String user;
    @Column(name = "JOB_QUEUE")
    private String jobQueue;
    @Column(name = "JOB_PRIORITY")
    private String jobPriority;
    @Column(name = "JOB_STATUS")
    private String jobStatus;
    @Column(name = "SUBMIT_TIME")
    private Date submitTime;
    @Column(name = "LAUNCH_TIME")
    private Date lauchTime;
    @Column(name = "FINISH_TIME")
    private Date finishTime;
    @Column(name = "TOTAL_MAPS")
    private Integer totalMaps;
    @Column(name = "FINISHED_MAPS")
    private Integer finishedMaps;
    @Column(name = "FAILED_MAPS")
    private Integer failedMaps;
    @Column(name = "TOTAL_REDUCES")
    private Integer totalReduces;
    @Column(name = "FINISHED_REDUCES")
    private Integer finishedReduces;
    @Column(name = "FAILED_REDUCES")
    private Integer failedReduces;

    public static Finder<Long, JobSummary> find = new Finder<Long, JobSummary>(Long.class, JobSummary.class);


    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getJobQueue() {
        return jobQueue;
    }

    public void setJobQueue(String jobQueue) {
        this.jobQueue = jobQueue;
    }

    public String getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(String jobPriority) {
        this.jobPriority = jobPriority;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getLauchTime() {
        return lauchTime;
    }

    public void setLauchTime(Date lauchTime) {
        this.lauchTime = lauchTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getTotalMaps() {
        return totalMaps;
    }

    public void setTotalMaps(Integer totalMaps) {
        this.totalMaps = totalMaps;
    }

    public Integer getFinishedMaps() {
        return finishedMaps;
    }

    public void setFinishedMaps(Integer finishedMaps) {
        this.finishedMaps = finishedMaps;
    }

    public Integer getFailedMaps() {
        return failedMaps;
    }

    public void setFailedMaps(Integer failedMaps) {
        this.failedMaps = failedMaps;
    }

    public Integer getTotalReduces() {
        return totalReduces;
    }

    public void setTotalReduces(Integer totalReduces) {
        this.totalReduces = totalReduces;
    }

    public Integer getFinishedReduces() {
        return finishedReduces;
    }

    public void setFinishedReduces(Integer finishedReduces) {
        this.finishedReduces = finishedReduces;
    }

    public Integer getFailedReduces() {
        return failedReduces;
    }

    public void setFailedReduces(Integer failedReduces) {
        this.failedReduces = failedReduces;
    }
}