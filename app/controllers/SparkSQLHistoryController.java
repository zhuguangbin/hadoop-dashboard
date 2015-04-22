package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlRow;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import models.SparkSQLHistroy;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SparkSQLHistoryController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    public static Result jobSummaryOfLast2weeks(){
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, -14);//取当前日期的前一天.
        Date date = cal.getTime();
        date.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);

        String sql = "select DATE_FORMAT(s.start_time,'%Y-%m-%d') as rundate, sum(case when s.retcode = 0 then 1 else 0 end ) as success,  sum(case when s.retcode = 1 then 1 else 0 end ) as AnalysisException, sum(case when s.retcode = 2 then 1 else 0 end ) as SparkException, sum(case when s.retcode = 3 then 1 else 0 end ) as SparkThriftServerException, sum(case when s.retcode = 100 then 1 else 0 end ) as OtherException from sparksql_history s where s.start_time > '" + dateStr + "' group by 1";

        List<SqlRow> jobs = Ebean.createSqlQuery(sql).findList();
        return ok(mapper.valueToTree(jobs));
    }

    public static Result list(String user, String startedTimeBegin, String startedTimeEnd, String retcode, String orderBy, String order) {

        Date startedTimeBeginInDate = null;
        Date startedTimeEndInDate = null;

        try {
            startedTimeBeginInDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(startedTimeBegin);
            startedTimeEndInDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(startedTimeEnd);
        } catch (ParseException e) {
            Logger.error("WRONG Format of parameter startedTimeBegin or startedTimeEnd, must be yyyyMMddHHmmss. " + e.getMessage());
        }

        List<SparkSQLHistroy> histories = null;

        Query<SparkSQLHistroy> query = SparkSQLHistroy.find.query();
        query.where().ge("startTime",startedTimeBeginInDate).le("startTime",startedTimeEndInDate);
        if (user != null && !"".equalsIgnoreCase(user)) {
            query.where().ilike("user", "%" + user + "%");
        }
        if (retcode != null && !"".equalsIgnoreCase(retcode)){
            query.where().eq("retcode", retcode);
        }
        histories = query.orderBy(orderBy + " " + order).findList();

        JsonNode node = mapper.valueToTree(histories);
        return ok(node);
    }


    public static Result findById(Long id) {

        SparkSQLHistroy history = SparkSQLHistroy.find.ref(id);
        return ok(mapper.valueToTree(history));
    }
}
