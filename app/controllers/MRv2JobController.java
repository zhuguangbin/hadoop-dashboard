package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import models.JobCounters;
import models.JobSummary;
import models.MRv1JobGanttTask;
import models.TaskInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import play.Play;
import play.libs.F;
import play.libs.WS;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MRv2JobController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    private static String mapreduce_http_addresss = Play.application().configuration().getString("hadoop.mapreduce.historyserver.http.address");

    public static String serviceRoot = "http://" + mapreduce_http_addresss + "/ws/v1/history/mapreduce/";

    @BodyParser.Of(BodyParser.Json.class)
    public static Result failedJobsOfLast24hours(String orderBy, String order) {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -1);//取当前日期的前一天.
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);

        List<SqlRow> jobs = Ebean.createSqlQuery("select s.id as jobid, s.name, s.user as user, s.queue, s.uberized, s.starttime, s.finishtime, s.mapstotal, s.mapscompleted, s.reducestotal, s.reducescompleted, s.diagnostics from mrv2_job_summary s where s.state='FAILED' and s.startTime>='"+dateStr+"' order by id desc").findList();
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

        List<SqlRow> jobs = Ebean.createSqlQuery("select s.id as jobId,s.name as jobName, s.user as user, s.queue as jobQueue, s.state as jobStatus, TIMESTAMPDIFF(SECOND,startTime,finishTime) as jobRunningTime from mrv2_job_summary s where s.startTime>='"+dateStr+"' order by jobRunningTime desc limit 10").findList();
        return ok(mapper.valueToTree(jobs));

    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobSummaryOfLast2weeks(String jobtype) {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -14);//取当前日期的前一天.
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        String sql = "select DATE_FORMAT(s.startTime,'%Y-%m-%d') as rundate, sum(case when s.state=\"SUCCEEDED\" then 1 else 0 end ) as success,   sum(case when s.state=\"KILLED\" then 1 else 0 end ) as killed, sum(case when s.state=\"FAILED\" then 1 else 0 end ) as failed, sum(s.mapsTotal) as total_maps, sum(s.reducesTotal) as total_reduces , sum(c.HDFS_BYTES_READ)/1024/1024/1024/1024 as hdfs_read_tb , sum(c.HDFS_BYTES_WRITTEN)/1024/1024/1024/1024 as hdfs_written_tb from mrv2_job_summary s left join mrv2_job_counters c on s.id=c.id  where s.startTime>='" + dateStr + "' ";

        if (jobtype!=null && !"all".equals(jobtype)){
            sql += " AND substring_index(left(s.name,locate('[',s.name)-1),':',1) = '"+jobtype+"' ";
        }

        sql += " group by rundate";
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

        String sql = "select distinct( case left(s.name,1) when '[' then 'MAPRED' else left(s.name,locate(':',s.name)-1) end) as jobtype, (CASE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) END) as project, (substring_index(s.name, '[', 3)) as jobshortname, DATE_FORMAT(s.startTime,'%Y-%m-%d') as rundate, s.user, s.queue, count(s.id) as jobcount,avg(TIMESTAMPDIFF(SECOND,startTime,finishTime)) as jobrunningtime,sum(mapsTotal) as total_maps ,sum(reducesTotal) as total_reduces, sum(c.hdfs_bytes_read) as total_hdfs_bytes_read ,sum(c.hdfs_bytes_written) as total_hdfs_bytes_written , sum(c.reduce_shuffle_bytes) as total_reduce_shuffle_bytes from mrv2_job_summary s join mrv2_job_counters c on s.id=c.id where s.startTime>='" + dateStr + "'   group by 1,2,3,4";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));
    }


    @BodyParser.Of(BodyParser.Json.class)
    public static Result jobStatOfToday() {

        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        String sql = "select distinct( case left(s.name,1) when '[' then 'MAPRED' else left(s.name,locate(':',s.name)-1) end) as jobtype, (CASE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) END) as project, (substring_index(s.name, '[', 3)) as jobshortname, DATE_FORMAT(s.startTime,'%Y-%m-%d') as rundate, s.user, s.queue, count(s.id) as jobcount,avg(TIMESTAMPDIFF(SECOND,startTime,finishTime)) as jobrunningtime,sum(mapsTotal) as total_maps ,sum(reducesTotal) as total_reduces, sum(c.hdfs_bytes_read) as total_hdfs_bytes_read ,sum(c.hdfs_bytes_written) as total_hdfs_bytes_written , sum(c.reduce_shuffle_bytes) as total_reduce_shuffle_bytes from mrv2_job_summary s join mrv2_job_counters c on s.id=c.id where s.startTime>='" + dateStr + "'   group by 1,2,3";

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

        String sql = "select s.id, s.user, s.queue, s.startTime, TIMESTAMPDIFF(SECOND,startTime,finishTime) as jobrunningtime,s.mapsTotal ,s.reducesTotal, c.hdfs_bytes_read,c.hdfs_bytes_written , c.reduce_shuffle_bytes from mrv2_job_summary s join mrv2_job_counters c on s.id=c.id where s.startTime>='" + rundate + "' and s.startTime < '"+tomorrowStr+ "' and s.name like '" + jobshortname + "%'";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));

    }

    public static Result jobhistory(String user, String queue, String startedTimeBegin, String startedTimeEnd, String limit, String state) {

        System.out.println(serviceRoot+"jobs");
        return async(
                WS.url(serviceRoot + "jobs")
                        .setQueryParameter("user", user)
                        .setQueryParameter("queue", queue)
                        .setQueryParameter("startedTimeBegin", startedTimeBegin)
                        .setQueryParameter("startedTimeEnd", startedTimeEnd)
                        .setQueryParameter("limit", limit)
                        .setQueryParameter("state", state)
                        .get().map(
                        new F.Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                if ("null".equals(response.asJson().get("jobs").toString())) {
                                    return ok();
                                } else {
                                    return ok(response.asJson().get("jobs").get("job"));
                                }
                            }
                        }
                )
        );
    }

  @BodyParser.Of(BodyParser.Json.class)
  public static Result jobinfo(String jobname, String start, String end) {

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

    String sql = "SELECT  distinct( case left(s.name,1) when '[' then 'MAPRED' else left(s.name,locate(':',s.name)-1) end) as jobtype, (CASE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) END) as project, (substring_index(s.name, '[', 3)) as jobshortname,DATE_FORMAT(s.startTime,'%Y-%m-%d') as rundate, f.SUBMIT_HOST, SUBSTR(f.INPUTDIR,1,100) AS INPUTDIR, f.INPUTFORMAT, f.OUTPUTDIR, f.OUTPUTFORMAT,f.OUT_COMPRESS, f.OUT_COMPRESS_CODEC, f.MAP_MEMORY , f.REDUCE_MEMORY FROM  hadoop.mrv2_job_summary s LEFT OUTER JOIN hadoop.mrv2_job_conf f ON f.JOBID=s.id WHERE s.startTime >= '" + startDateStr + "' AND s.startTime < '" + endDateStr + "' ";
    if (jobname !=null || !"".equals(jobname)){
      sql += " AND s.name like '%"+jobname+"%' ";
    }
    sql +=" GROUP BY 1,2,3,4";
    List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
    return ok(mapper.valueToTree(jobs));
  }

}
