package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name = "task_info")
public class TaskInfo extends Model
{
	@Id
    @Column(name = "TASKID")
    private String taskId;
    @Column(name = "JOBID")
    private String jobId;
    @Column(name = "TASK_TYPE")
    private String taskType;
    @Column(name = "START_TIME")
    private Date startTime;
    @Column(name = "FINISH_TIME")
    private Date finishTime;
    @Column(name = "TASK_STATUS")
    private String taskStatus;
    @Column(name = "SPLITS")
    private String splits;
    @Column(name = "ERROR")
    private String error;


    public static Finder<Long, TaskInfo> find = new Finder<Long, TaskInfo>(Long.class, TaskInfo.class);


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getSplits() {
        return splits;
    }

    public void setSplits(String splits) {
        this.splits = splits;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}