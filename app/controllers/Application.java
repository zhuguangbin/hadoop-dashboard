package controllers;

import models.ClusterCost;
import org.codehaus.jackson.JsonNode;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Application extends Controller {
    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result dailyBill(String project, String date, String orderBy, String order) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date jobrundate = null;
        try {
            jobrundate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ok("error date format: " + date + ", pls use yyyy-MM-dd");
        }


        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        cal.setTime(jobrundate);
        cal.add(Calendar.DAY_OF_MONTH, +1);//取当前日期的前一天.
        Date tomorrow = cal.getTime();
        String tomorrowStr = sdf.format(tomorrow);

        JsonNode costs = ClusterCostController.costOfProject(project, date, tomorrowStr, orderBy, order);

        List<ClusterCost> clusterCosts = new ArrayList<ClusterCost>();

        if (costs.isArray()) {
            for (JsonNode node : costs) {
                ClusterCost costOfProject = new ClusterCost();
                costOfProject.setProject(node.get("project").asText());
                costOfProject.setDate(node.get("rundate").asText());
                costOfProject.setMrv1JobCount(node.get("mrv1_jobcount").asLong());
                costOfProject.setMrv1Cost(node.get("mrv1_cost").asDouble());
                costOfProject.setMrv2JobCount(node.get("mrv2_jobcount").asLong());
                costOfProject.setMrv2Cost(node.get("mrv2_cost").asDouble());
                costOfProject.setTotalCost(node.get("total_cost").asDouble());
                clusterCosts.add(costOfProject);
            }
        }
        return ok(views.html.dailybill.render(date, clusterCosts));
    }
}
