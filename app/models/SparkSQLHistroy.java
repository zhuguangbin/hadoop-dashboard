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
@Table(name = "sparksql_history")
public class SparkSQLHistroy extends Model {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "user")
    private String user;
    @Column(name = "sqlstr")
    private String sqlstr;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "finish_time")
    private Date finishTime;
    @Column(name = "retcode")
    private Integer retcode;
    @Column(name = "message")
    private String message;
    @Column(name = "result_file")
    private String resultFile;

    public static Finder<Long, SparkSQLHistroy> find = new Finder<Long, SparkSQLHistroy>(Long.class, SparkSQLHistroy.class);


    public static List<SparkSQLHistroy> all() {
        return find.all();
    }

    public static void save(SparkSQLHistroy histroy) {
        histroy.save();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSqlstr() {
        return sqlstr;
    }

    public void setSqlstr(String sqlstr) {
        this.sqlstr = sqlstr;
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

    public Integer getRetcode() {
        return retcode;
    }

    public void setRetcode(Integer retcode) {
        this.retcode = retcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }
}