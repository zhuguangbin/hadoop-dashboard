package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import models.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MRv1JobController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    @BodyParser.Of(BodyParser.Json.class)
    public static Result failedJobsOfLast24hours(String orderBy, String order) {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -1);//取当前日期的前一天.
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);

        List<SqlRow> jobs = Ebean.createSqlQuery("select s.JOBID as jobId,s.JOBNAME as jobName, s.USER as user, s.JOB_QUEUE as jobQueue, s.JOB_PRIORITY as jobPriority, s.JOB_STATUS as jobStatus,s.SUBMIT_TIME, s.FINISH_TIME from mrv1_job_summary s where s.job_status='FAILED' and s.submit_time>='" + dateStr + "' order by jobid desc ").findList();
        JsonNode result = mapper.valueToTree(jobs);

        return ok(result);

    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result longRunningJobsOfLast24hours() {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -1);//取当前日期的前一天.
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);


        List<SqlRow> jobs = Ebean.createSqlQuery("select s.JOBID as jobId,s.JOBNAME as jobName, s.USER as user, s.JOB_QUEUE as jobQueue, s.JOB_PRIORITY as jobPriority, s.JOB_STATUS as jobStatus, TIMESTAMPDIFF(SECOND,submit_time,finish_time) as jobRunningTime from mrv1_job_summary s where s.SUBMIT_TIME>='" + dateStr + "' order by jobRunningTime desc limit 10").findList();
        return ok(mapper.valueToTree(jobs));

    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobSummaryOfLast2weeks() {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -14);//取当前日期的前一天.
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        String sql = "select DATE_FORMAT(s.SUBMIT_TIME,'%Y-%m-%d') as rundate, sum(case when s.JOB_STATUS=\"SUCCESS\" then 1 else 0 end ) as success,   sum(case when s.JOB_STATUS=\"KILLED\" then 1 else 0 end ) as killed, sum(case when s.JOB_STATUS=\"FAILED\" then 1 else 0 end ) as failed, sum(s.total_maps) as total_maps, sum(s.TOTAL_REDUCES) as total_reduces , sum(c.HDFS_BYTES_READ)/1024/1024/1024/1024 as hdfs_read_tb, sum(c.HDFS_BYTES_WRITTEN)/1024/1024/1024/1024 as hdfs_written_tb from mrv1_job_summary s left join mrv1_job_counters c on s.jobid=c.jobid  where s.SUBMIT_TIME>='" + dateStr + "' group by rundate";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobStatOfLast2weeks() {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -14);//取当前日期的前一天.
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        String sql = "select distinct( case left(s.jobname,1) when '[' then 'MAPRED' else left(s.jobname,locate(':',s.JOBNAME)-1) end) as jobtype, (CASE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) END) as project, (substring_index(s.jobname, '[', 3)) as jobshortname, DATE_FORMAT(s.SUBMIT_TIME,'%Y-%m-%d') as rundate, s.user, s.job_queue, count(s.jobid) as jobcount,avg(TIMESTAMPDIFF(SECOND,submit_time,finish_time)) as jobrunningtime,avg(total_maps) as avg_maps ,avg(total_reduces) as avg_reduce, avg(c.hdfs_bytes_read) as avg_hdfs_bytes_read ,avg(c.hdfs_bytes_written) as avg_hdfs_bytes_written , avg(c.reduce_shuffle_bytes) as avg_reduce_shuffle_bytes from mrv1_job_summary s join mrv1_job_counters c on s.jobid=c.jobid where s.SUBMIT_TIME>='" + dateStr + "'   group by 1,2,3,4";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobStatOfToday() {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        String sql = "select distinct( case left(s.jobname,1) when '[' then 'MAPRED' else left(s.jobname,locate(':',s.JOBNAME)-1) end) as jobtype, (CASE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) END) as project, (substring_index(s.jobname, '[', 3)) as jobshortname, DATE_FORMAT(s.SUBMIT_TIME,'%Y-%m-%d') as rundate, s.user, s.job_queue, count(s.jobid) as jobcount,avg(TIMESTAMPDIFF(SECOND,submit_time,finish_time)) as jobrunningtime,avg(total_maps) as avg_maps ,avg(total_reduces) as avg_reduce, avg(c.hdfs_bytes_read) as avg_hdfs_bytes_read ,avg(c.hdfs_bytes_written) as avg_hdfs_bytes_written , avg(c.reduce_shuffle_bytes) as avg_reduce_shuffle_bytes from mrv1_job_summary s join mrv1_job_counters c on s.jobid=c.jobid where s.SUBMIT_TIME>='" + dateStr + "' group by 1,2,3";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobhistoryPerDay(String jobshortname, String rundate) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date jobrundate = null;
        try {
            jobrundate = sdf.parse(rundate);
        } catch (ParseException e) {
            e.printStackTrace();
            return ok("error date format: " + rundate + ", pls use yyyy-MM-dd");
        }


        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.setTime(jobrundate);
        cal.add(Calendar.DAY_OF_MONTH, +1);//取当前日期的前一天.
        Date tomorrow = cal.getTime();
        String tomorrowStr = sdf.format(tomorrow);

        String sql = "select s.jobid, s.user, s.job_queue, s.submit_time, TIMESTAMPDIFF(SECOND,submit_time,finish_time) as jobrunningtime,s.total_maps ,s.total_reduces, c.hdfs_bytes_read,c.hdfs_bytes_written , c.reduce_shuffle_bytes from mrv1_job_summary s join mrv1_job_counters c on s.jobid=c.jobid where s.SUBMIT_TIME>='" + rundate + "' and s.SUBMIT_TIME < '" + tomorrowStr + "' and s.JOBNAME like '" + jobshortname + "%'";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));

    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobConfStat(String jobname, String start, String end) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDateStr = start;
        String endDateStr = end;
        if (start == null || "".equals(start)) {
            Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
            cal.add(Calendar.DAY_OF_MONTH, -1);//取当前日期的前一天.
            Date date = cal.getTime();

            startDateStr = sdf.format(date);
        }
        if (end == null || "".equals(end)) {
            endDateStr = sdf.format(new Date());
        }

        String sql = "SELECT  distinct( case left(s.JOBNAME,1) when '[' then 'MAPRED' else left(s.JOBNAME,locate(':',s.JOBNAME)-1) end) as jobtype, (CASE SUBSTRING_INDEX(substring(s.JOBNAME,locate('[',s.JOBNAME)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.JOBNAME,locate('[',s.JOBNAME)),'[',2) END) as project, (substring_index(s.JOBNAME, '[', 3)) as jobshortname,DATE_FORMAT(s.SUBMIT_TIME,'%Y-%m-%d') as rundate, f.SUBMIT_HOST, SUBSTR(f.INPUTDIR,1,100) AS INPUTDIR, f.INPUTFORMAT, f.OUTPUTDIR, f.OUTPUTFORMAT,f.OUT_COMPRESS, f.OUT_COMPRESS_CODEC, SUBSTRING_INDEX(f.CHILD_JAVA_OPTS,' ', 1) as child_heapsize , f.CHILD_ULIMIT FROM  hadoop.mrv1_job_summary s LEFT OUTER JOIN hadoop.mrv1_job_conf f ON f.JOBID=s.JOBID WHERE s.SUBMIT_TIME >= '" + startDateStr + "' AND s.SUBMIT_TIME < '" + endDateStr + "' ";
        if (jobname !=null || !"".equals(jobname)){
            sql += " AND s.JOBNAME like '%"+jobname+"%' ";
        }
        sql +=" GROUP BY 1,2,3,4";
        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result getJobCounters(String jobId) {

        JobCounters counters = JobCounters.find.byId(jobId);

        if (counters != null) {
            JsonNode result = mapper.valueToTree(counters);
            return ok(result);
        } else {
            return ok();
        }

    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result listTasks(String jobId, String orderBy, String order) {

        Query<TaskInfo> query = TaskInfo.find.where().query();

        List<TaskInfo> taskInfos = query.where().eq("jobId", jobId).orderBy(orderBy + " " + order).findList();

        JsonNode result = mapper.valueToTree(taskInfos);

        return ok(result);

    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result gantt(String starttime, String endtime, String user, String jobname, String jobqueue, String jobstatus) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        Date start = null;
        Date end = null;
        try {
            start = sdf.parse(starttime);
            end = sdf.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
            return ok("error date format , pls use yyyyMMddHHmmss");
        }

        String startStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start);
        String endStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end);

        List<MRv1JobGanttTask> tasks = new ArrayList<MRv1JobGanttTask>();

        String sql = "select s.JOBID as jobId,s.JOBNAME as jobName, s.USER as user, s.JOB_QUEUE as jobQueue, s.JOB_PRIORITY as jobPriority, s.JOB_STATUS as jobStatus, s.SUBMIT_TIME as submitTime, s.FINISH_TIME as finishTime, TIMESTAMPDIFF(SECOND,submit_time,finish_time) as jobRunningTime from mrv1_job_summary s where s.SUBMIT_TIME >= '" + startStr + "' and s.SUBMIT_TIME <= '" + endStr + "' ";

        if (user != null && !"".equals(user)) {
            sql += " and s.USER='" + user + "' ";
        }

        if (jobname != null && !"".equals(jobname)) {
            sql += " and s.jobName like '%" + jobname + "%'";
        }

        if (jobqueue != null && !"".equals(jobqueue)) {
            sql += " and s.JOB_QUEUE='" + jobqueue + "' ";
        }

        if (jobstatus != null && !"".equals(jobstatus)) {
            sql += " and s.JOB_STATUS='" + jobstatus + "' ";
        }

        sql += "  order by jobId asc ";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();

        for (SqlRow job : jobs) {
            MRv1JobGanttTask jobinfo = new MRv1JobGanttTask();
            jobinfo.setId(job.getString("jobId"));
            jobinfo.setParentID(null);
            jobinfo.setStart(job.getUtilDate("submitTime"));
            jobinfo.setEnd(job.getUtilDate("finishTime"));
            jobinfo.setTitle(job.getString("jobName"));
            jobinfo.setSummary(true);
            jobinfo.setExpanded(false);
            tasks.add(jobinfo);
        }

        return ok(mapper.valueToTree(tasks));


    }


}
