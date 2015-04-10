package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "mrv2_job_watcher")
public class MRv2JobWatcher extends Model {
  @Id
  @Column(name = "JOBNAME")
  private String jobName;
  @Column(name = "JOBOWNER")
  private String jobOwner;
  @Column(name = "JOBOWNER_PHONE")
  private String jobOwnerPhone;
  @Column(name = "ONCALL_NAME")
  private String onCallName;
  @Column(name = "ONCALL_PHONE")
  private String onCallPhone;

  public static Finder<String, MRv2JobWatcher> find = new Finder<String, MRv2JobWatcher>(String.class, MRv2JobWatcher.class);

  public static List<MRv2JobWatcher> all() {
    return find.all();
  }

  public static void saveOrUpdate(MRv2JobWatcher jobWatcher) {
    MRv2JobWatcher existed = findByJobName(jobWatcher.getJobName());
    if (existed != null) {
      jobWatcher.update();
    } else {
      jobWatcher.save();
    }

  }

  public static MRv2JobWatcher findByJobName(String jobName) {
    return find.byId(jobName);
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getJobOwner() {
    return jobOwner;
  }

  public void setJobOwner(String jobOwner) {
    this.jobOwner = jobOwner;
  }

  public String getJobOwnerPhone() {
    return jobOwnerPhone;
  }

  public void setJobOwnerPhone(String jobOwnerPhone) {
    this.jobOwnerPhone = jobOwnerPhone;
  }

  public String getOnCallName() {
    return onCallName;
  }

  public void setOnCallName(String onCallName) {
    this.onCallName = onCallName;
  }

  public String getOnCallPhone() {
    return onCallPhone;
  }

  public void setOnCallPhone(String onCallPhone) {
    this.onCallPhone = onCallPhone;
  }

  @Override
  public String toString() {
    return "MRv2JobWatcher{" +
            "jobName='" + jobName + '\'' +
            ", jobOwner='" + jobOwner + '\'' +
            ", jobOwnerPhone='" + jobOwnerPhone + '\'' +
            ", onCallName='" + onCallName + '\'' +
            ", onCallPhone='" + onCallPhone + '\'' +
            '}';
  }
}