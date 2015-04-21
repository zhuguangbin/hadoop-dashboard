package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import models.JobCounters;
import models.MRv1JobGanttTask;
import models.TaskInfo;
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

public class ClusterCostController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    @BodyParser.Of(BodyParser.Json.class)
    public static Result summary(String start,String end, String orderBy, String order) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String startDateStr = start;
        String endDateStr = end;
        if (start == null || "".equals(start)) {
            Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
            cal.add(Calendar.DAY_OF_MONTH, -14);//取当前日期的前14天.
            Date date = cal.getTime();

            startDateStr = sdf.format(date);
        }
        if (end == null || "".equals(end)){
            Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, +1);//tomorrow.
            Date tomorrow = cal.getTime();
            endDateStr = sdf.format(tomorrow);
        }

        String sql = "SELECT rundate, ROUND(sum(CASE cluster WHEN 'mrv1' THEN cost ELSE 0 END ),2) as mrv1_cost, sum(CASE cluster WHEN 'mrv1' THEN jobcount ELSE 0 END ) as mrv1_jobcount," +
                " ROUND(sum(CASE cluster WHEN 'mrv2' THEN cost ELSE 0 END ),2) as mrv2_cost, sum(CASE cluster WHEN 'mrv2' THEN jobcount ELSE 0 END ) as mrv2_jobcount FROM\n" +
                " (    SELECT\n" +
                "      DATE_FORMAT(s.submit_time, '%Y-%m-%d')                       AS rundate,\n" +
                "      'mrv1'                                                       AS cluster,\n" +
                "      count(\n" +
                "          s.jobid)                                                 AS jobcount,\n" +
                "      sum(\n" +
                "          c.TOTAL_LAUNCHED_MAPS)                                            AS total_maps,\n" +
                "      sum(\n" +
                "          c.TOTAL_LAUNCHED_REDUCES)                                         AS total_reduces,\n" +
                "      sum(\n" +
                "          c.CPU_MILLISECONDS)                                      AS cpu_milliseconds,\n" +
                "      sum(c.hdfs_bytes_read / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_read_GB,\n" +
                "      sum(c.hdfs_bytes_written / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_written_GB,\n" +
                "      sum(c.reduce_shuffle_bytes / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_reduce_shuffle_GB,\n" +
                "      sum(c.CPU_MILLISECONDS) / 3600000 * 0.2 + sum(c.HDFS_BYTES_READ / 1024 / 1024 / 1024) * 0.01 +\n" +
                "      sum(c.HDFS_BYTES_WRITTEN / 1024 / 1024 / 1024) * 0.03 + sum(c.REDUCE_SHUFFLE_BYTES / 1024 / 1024 / 1024) *\n" +
                "                                                              0.05 AS cost\n" +
                "    FROM hadoop.mrv1_job_summary s JOIN hadoop.mrv1_job_counters c ON s.jobid = c.jobid\n" +
                "    WHERE s.submit_time >= '" + startDateStr + "' AND s.submit_time <'" + endDateStr +"' " +
                "    GROUP BY 1, 2\n" +
                "    UNION\n" +
                "    SELECT\n" +
                "      DATE_FORMAT(s.startTime, '%Y-%m-%d')                         AS rundate,\n" +
                "      'mrv2'                                                       AS cluster,\n" +
                "      count(\n" +
                "          s.id)                                                    AS jobcount,\n" +
                "      sum(\n" +
                "          mapsCompleted)                                               AS total_maps,\n" +
                "      sum(\n" +
                "          reducesCompleted)                                            AS total_reduces,\n" +
                "      sum(\n" +
                "          c.CPU_MILLISECONDS)                                      AS cpu_milliseconds,\n" +
                "      sum(c.hdfs_bytes_read / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_read_GB,\n" +
                "      sum(c.hdfs_bytes_written / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_written_GB,\n" +
                "      sum(c.reduce_shuffle_bytes / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_reduce_shuffle_GB,\n" +
                "      sum(c.CPU_MILLISECONDS) / 3600000 * 0.2 + sum(c.HDFS_BYTES_READ / 1024 / 1024 / 1024) * 0.01 +\n" +
                "      sum(c.HDFS_BYTES_WRITTEN / 1024 / 1024 / 1024) * 0.03 + sum(c.REDUCE_SHUFFLE_BYTES / 1024 / 1024 / 1024) *\n" +
                "                                                              0.05 AS cost\n" +
                "    FROM hadoop.mrv2_job_summary s JOIN hadoop.mrv2_job_counters c ON s.id = c.id\n" +
                "    WHERE s.startTime >= '" + startDateStr + "' AND s.startTime < '" + endDateStr + "' " +
                "    GROUP BY 1, 2\n" +
                "    \n" +
                "  ) a GROUP BY rundate";
        sql += " ORDER BY " + orderBy + " "+order;

        List<SqlRow> cost = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(cost));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result byProject(String project, String start, String end, String orderBy, String order) {
        JsonNode result = costOfProject(project, start, end, orderBy, order);
        return ok(result);
    }

    public static JsonNode costOfProject(String project, String start, String end, String orderBy, String order) {

        String sql = "SELECT  project, rundate,  sum(CASE cluster WHEN 'mrv1' THEN jobcount ELSE 0 END) as mrv1_jobcount, sum(CASE cluster WHEN 'mrv2' THEN jobcount ELSE 0 END) as mrv2_jobcount, " +
                "  ROUND(sum(CASE cluster WHEN 'mrv1' THEN cost ELSE 0 END ),2) as mrv1_cost, ROUND(sum(CASE cluster WHEN 'mrv2' THEN cost ELSE 0 END ),2) as mrv2_cost, ROUND(sum(cost),2) as total_cost FROM\n" +
                "  (\n" +
                "    SELECT\n" +
                "      DATE_FORMAT(s.submit_time, '%Y-%m-%d')                       AS rundate,\n" +
                "      (CASE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) END) as project,\n" +
                "      (substring_index(s.jobname, '[', 3)) as jobshortname,\n" +
                "      'mrv1'                                                       AS cluster,\n" +
                "      ( case left(s.jobname,1) when '[' then 'MAPRED' else left(s.jobname,locate(':',s.jobname)-1) end) as jobtype,\n" +
                "      count(\n" +
                "          s.jobid)                                                 AS jobcount,\n" +
                "      sum(\n" +
                "          c.TOTAL_LAUNCHED_MAPS)                                            AS total_maps,\n" +
                "      sum(\n" +
                "          c.TOTAL_LAUNCHED_REDUCES)                                         AS total_reduces,\n" +
                "      sum(\n" +
                "          c.CPU_MILLISECONDS)                                      AS cpu_milliseconds,\n" +
                "      sum(c.HDFS_BYTES_READ / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_read_GB,\n" +
                "      sum(c.HDFS_BYTES_WRITTEN / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_written_GB,\n" +
                "      sum(c.REDUCE_SHUFFLE_BYTES / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_reduce_shuffle_GB,\n" +
                "      (sum(c.CPU_MILLISECONDS) / 3600000 * 0.2 + sum(c.HDFS_BYTES_READ / 1024 / 1024 / 1024) * 0.01 +\n" +
                "      sum(c.HDFS_BYTES_WRITTEN / 1024 / 1024 / 1024) * 0.03 + sum(c.REDUCE_SHUFFLE_BYTES / 1024 / 1024 / 1024) * 0.05)   AS cost\n" +
                "    \n" +
                "    FROM hadoop.mrv1_job_summary s JOIN hadoop.mrv1_job_counters c ON s.jobid = c.jobid\n" +
                "    WHERE s.submit_time >= '" + start + "' and s.submit_time < '" + end + "'\n" +
                "    GROUP BY 1, 2\n" +
                "    UNION\n" +
                "    SELECT\n" +
                "      DATE_FORMAT(s.startTime, '%Y-%m-%d')                         AS rundate,\n" +
                "      (CASE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) END) as project,\n" +
                "      (substring_index(s.name, '[', 3)) as jobshortname,\n" +
                "      'mrv2'                                                       AS cluster,\n" +
                "      ( case left(s.name,1) when '[' then 'MAPRED' else left(s.name,locate(':',s.name)-1) end) as jobtype,\n" +
                "      count(\n" +
                "          s.id)                                                    AS jobcount,\n" +
                "      sum(\n" +
                "          s.mapsCompleted)                                               AS total_maps,\n" +
                "      sum(\n" +
                "          s.reducesCompleted)                                            AS total_reduces,\n" +
                "      sum(\n" +
                "          c.CPU_MILLISECONDS)                                      AS cpu_milliseconds,\n" +
                "      sum(c.HDFS_BYTES_READ / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_read_GB,\n" +
                "      sum(c.HDFS_BYTES_WRITTEN / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_hdfs_written_GB,\n" +
                "      sum(c.REDUCE_SHUFFLE_BYTES / 1024 / 1024 /\n" +
                "          1024)                                                    AS total_reduce_shuffle_GB,\n" +
                "      (sum(c.CPU_MILLISECONDS) / 3600000 * 0.2 + sum(c.HDFS_BYTES_READ / 1024 / 1024 / 1024) * 0.01 +\n" +
                "      sum(c.HDFS_BYTES_WRITTEN / 1024 / 1024 / 1024) * 0.03 + sum(c.REDUCE_SHUFFLE_BYTES / 1024 / 1024 / 1024) * 0.05)   AS cost\n" +
                "    FROM hadoop.mrv2_job_summary s JOIN hadoop.mrv2_job_counters c ON s.id = c.id\n" +
                "    WHERE s.startTime >= '" + start + "' and s.startTime < '" + end + "'\n" +
                "    GROUP BY 1, 2\n" +
                "    \n" +
                "  ) a ";

        if (project != null && !"".equalsIgnoreCase(project)) {
            sql += " WHERE project like '%" + project + "%'";
        }

        sql += " GROUP BY project, rundate ";
        sql += " ORDER BY " + orderBy + " "+order;

        List<SqlRow> cost = Ebean.createSqlQuery(sql).findList();

        return mapper.valueToTree(cost);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result bill(String project, String start, String end, String orderBy, String order){
        JsonNode result = detailbill(project, start, end, orderBy,  order);

        return ok(result);
    }

    public static JsonNode detailbill(String project, String start, String end, String orderBy, String order) {

        String sql = "select * from (" +
                "select  (CASE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.name,locate('[',s.name)),'[',2) END) as project, 'mrv2' as cluster, s.user, (substring_index(s.name, '[', 3)) as jobshortname, " +
                        "DATE_FORMAT(s.startTime,'%Y-%m-%d') as rundate,  count(s.id) as jobcount,sum(mapsCompleted) as total_maps ,sum(reducesCompleted) as total_reduces, " +
                        "sum(c.CPU_MILLISECONDS) as cpu_milliseconds, sum(c.HDFS_BYTES_READ/1024/1024/1024) as total_hdfs_read_GB, sum(c.HDFS_BYTES_WRITTEN/1024/1024/1024) as total_hdfs_written_GB , sum(c.REDUCE_SHUFFLE_BYTES/1024/1024/1024) as total_reduce_shuffle_GB, " +
                        "ROUND((sum(c.CPU_MILLISECONDS)/3600000 * 0.2 + sum(c.HDFS_BYTES_READ/1024/1024/1024)*0.01+sum(c.HDFS_BYTES_WRITTEN/1024/1024/1024)*0.03+sum(c.REDUCE_SHUFFLE_BYTES/1024/1024/1024)*0.05),2) as cost " +
                        "" +
                        "from hadoop.mrv2_job_summary s join hadoop.mrv2_job_counters c on s.id=c.id where s.startTime>='" + start + "' and s.startTime <'" + end + "' GROUP BY 1,2,3,4,5 \n"
                        +
                        "UNION "
                        +
                        "select  (CASE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) WHEN '' THEN 'UNDEFINED' ELSE SUBSTRING_INDEX(substring(s.jobname,locate('[',s.jobname)),'[',2) END) as project, 'mrv1' as cluster, s.user, (substring_index(s.jobname, '[', 3)) as jobshortname, " +
                        "DATE_FORMAT(s.submit_time,'%Y-%m-%d') as rundate,  count(s.jobid) as jobcount,sum(c.TOTAL_LAUNCHED_MAPS) as total_maps ,sum(c.TOTAL_LAUNCHED_REDUCES) as total_reduces, " +
                        "sum(c.CPU_MILLISECONDS) as cpu_milliseconds, sum(c.HDFS_BYTES_READ/1024/1024/1024) as total_hdfs_read_GB, sum(c.HDFS_BYTES_WRITTEN/1024/1024/1024) as total_hdfs_written_GB , sum(c.REDUCE_SHUFFLE_BYTES/1024/1024/1024) as total_reduce_shuffle_GB, " +
                        "ROUND((sum(c.CPU_MILLISECONDS)/3600000 * 0.2+ sum(c.HDFS_BYTES_READ/1024/1024/1024)*0.01+sum(c.HDFS_BYTES_WRITTEN/1024/1024/1024)*0.03+sum(c.REDUCE_SHUFFLE_BYTES/1024/1024/1024)*0.05),2) as cost " +
                        "from hadoop.mrv1_job_summary s join hadoop.mrv1_job_counters c on s.jobid=c.jobid where s.submit_time>='" + start + "' and s.submit_time <'" + end + "'GROUP BY 1,2,3,4,5 " +
                ") as bill " ;
        if (project != null && !"".equalsIgnoreCase(project)) {
            sql += " WHERE project like '%" + project + "%'";
        }
        sql += " ORDER BY " + orderBy + " "+order;

        List<SqlRow> cost = Ebean.createSqlQuery(sql).findList();
        return mapper.valueToTree(cost);
    }

}
