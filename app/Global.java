import com.google.inject.Guice;
import com.google.inject.Injector;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public class Global extends GlobalSettings
{
    private Injector injector;

    @Override
    public void onStart(Application application)
    {
        super.onStart(application);

        injector = Guice.createInjector(new ProjectModule());

        DateTime now = new DateTime();

        DateTime plannedStart = new DateTime()
                .withHourOfDay(8)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);

        DateTime nextRun = (now.isAfter(plannedStart))
                ? plannedStart.plusDays(1)
                : plannedStart;

        Long nextRunInSeconds = (long) Seconds.secondsBetween(now, nextRun).getSeconds();

        Akka.system().scheduler().schedule(
                Duration.create(nextRunInSeconds, TimeUnit.SECONDS),
                Duration.create(1, TimeUnit.DAYS) ,
                new Runnable() {
                    public void run() {
                        Logger.info("-------------------------scheduler start : " + new DateTime());
                    }
                },
                Akka.system().dispatcher()
        );

    }
}
