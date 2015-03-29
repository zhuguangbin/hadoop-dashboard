package controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MRv1Controller extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    private static String jobtracker_http_address = Play.application().configuration().getString("hadoop.mrv1.jobtracker.http.address");
    private static String jobtracker_jmx_url = "http://"+jobtracker_http_address+"/jmx";

    public static Result nodes() {
        return async(play.libs.WS.url(jobtracker_jmx_url).get().map(
                new F.Function<play.libs.WS.Response, Result>() {
                    public Result apply(play.libs.WS.Response response) {

                        List<JsonNode> result = new ArrayList<JsonNode>();

                        JsonNode resp = response.asJson().get("beans");

                        // hadoop:service=JobTracker,name=JobTrackerInfo
                        JsonNode jtState = resp.get(11);


                        if (jtState != null) {

                            try {
                                JsonNode summary = mapper.readTree(jtState.get("SummaryJson").asText());
                                ObjectNode alive = Json.newObject();
                                int aliveNodes = summary.get("alive").asInt();
                                alive.put("category", "alive");
                                alive.put("value", aliveNodes);
                                result.add(alive);

                                ObjectNode blacklisted = Json.newObject();
                                int blacklistedNodes = summary.get("blacklisted").asInt();
                                blacklisted.put("category", "blacklisted");
                                blacklisted.put("value", blacklistedNodes);
                                result.add(blacklisted);

                            } catch (IOException e) {
                                e.printStackTrace();
                                return ok();
                            }

                            return ok(mapper.valueToTree(result));
                        } else {
                            return ok();
                        }


                    }
                }));
    }

}
