package controllers;

import models.MRv2JobWatcher;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import play.Logger;
import play.data.Form;
import play.libs.F;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.mrv2jobwatcher;

import static play.data.Form.form;

public class MRv2JobWatcherController extends Controller {
  public static ObjectMapper mapper = new ObjectMapper();

  public static String smsurl = "http://sms.prod.mediav.com:1111/empp/tsend";


  public static Result sendSMSIfError(final String jobId, final String jobStatus) {

    if (!"SUCCEEDED".equals(jobStatus)) {
      Logger.info("JOB is " + jobStatus + ", Sending SMS to oncall & jobowner! ");
      return async(
              WS.url(MRv2JobController.serviceRoot + "jobs/" + jobId)
                      .get().map(
                      new F.Function<WS.Response, Result>() {
                        public Result apply(WS.Response response) {

                          String jobname = null;
                          // got result
                          if (!"null".equals(response.asJson().get("job").toString())) {
                            jobname = response.asJson().get("job").get("name").asText();
                            int i = StringUtils.ordinalIndexOf(jobname, "[", 3);
                            Logger.info("index is : " + String.valueOf(i));
                            // jobname if normallized
                            if (i != -1) {
                              String jobshortname = jobname.substring(0, i);
                              Logger.info("Job ShortName is : " + jobshortname);

                              MRv2JobWatcher jobWatcher = MRv2JobWatcher.findByJobName(jobshortname);

                              if (jobWatcher != null) {
                                String onCallPhone = jobWatcher.getOnCallPhone();
                                String jobOwnerPhone = jobWatcher.getJobOwnerPhone();

                                String msg = "JobId:" + jobId + ",jobStatus:" + jobStatus;

                                if (onCallPhone != null && !"".equalsIgnoreCase(onCallPhone)) {
                                  Logger.info("Sending alter message for job " + jobId + " jobname: " + jobname + " , because job is " + jobStatus + ". Job Watcher : " + jobWatcher.toString());
                                  WS.url(smsurl).setQueryParameter("to", onCallPhone).setQueryParameter("msg", msg).get().get();
                                }

                                if (jobOwnerPhone != null && !"".equalsIgnoreCase(jobOwnerPhone)) {
                                  WS.url(smsurl).setQueryParameter("to", jobOwnerPhone).setQueryParameter("msg", msg).get().get();
                                }

                              } else {
                                Logger.warn("no watcher on job : " + jobshortname);
                              }
                            }

                          }

                          return ok("Job " + jobId + " is " + jobStatus);
                        }
                      }));
    } else {
      return ok("Job " + jobId + " is " + jobStatus);
    }

  }

  /**
   * Defines a form wrapping the MRv2JobWatcher class.
   */
  final static Form<MRv2JobWatcher> jobwatcherForm = form(MRv2JobWatcher.class);


  public static Result findByJobName(String jobName) {

    MRv2JobWatcher jobWatcher = MRv2JobWatcher.findByJobName(jobName);

    if (jobWatcher != null) {
      return ok(mrv2jobwatcher.render(jobwatcherForm.fill(jobWatcher)));
    } else {
      jobWatcher = new MRv2JobWatcher();
      jobWatcher.setJobName(jobName);
      return ok(mrv2jobwatcher.render(jobwatcherForm.fill(jobWatcher)));
    }


  }

  /**
   * Handle the form submission.
   */
  public static Result save() {
    Form<MRv2JobWatcher> filledForm = jobwatcherForm.bindFromRequest();

    if (filledForm.hasErrors()) {
      return badRequest(mrv2jobwatcher.render(filledForm));
    } else {
      MRv2JobWatcher updated = filledForm.get();
      try {
        MRv2JobWatcher.saveOrUpdate(updated);
        return redirect("/#/mrv2job/jobconf");
      } catch (Exception e) {
        e.printStackTrace();
        return badRequest(mrv2jobwatcher.render(filledForm));
      }
    }
  }


}