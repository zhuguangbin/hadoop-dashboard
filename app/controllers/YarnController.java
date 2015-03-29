package controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.Play;
import play.libs.F.Function;
import play.libs.Json;
import play.libs.WS;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;

public class YarnController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    private static String resourcemanager_http_addresss = Play.application().configuration().getString("hadoop.yarn.resourcemanager.http.address");
    private static String sparkonyarn_resourcemanager_http_addresss = Play.application().configuration().getString("hadoop.sparkonyarn.resourcemanager.http.address");

    private static String serviceRoot = "http://" + resourcemanager_http_addresss + "/ws/v1/cluster/";
    private static String sparkserviceRoot = "http://" + sparkonyarn_resourcemanager_http_addresss + "/ws/v1/cluster/";

    public static Result nodes() {

        return async(
                WS.url(serviceRoot + "metrics")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("clusterMetrics");

                                if (resp != null) {
                                    ObjectNode active = Json.newObject();
                                    int activeNodes = resp.get("activeNodes").asInt();
                                    active.put("category", "active");
                                    active.put("value", activeNodes);
                                    result.add(active);

                                    ObjectNode lost = Json.newObject();
                                    int lostNodes = resp.get("lostNodes").asInt();
                                    lost.put("category", "lost");
                                    lost.put("value", lostNodes);
                                    result.add(lost);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result vcores() {

        return async(
                WS.url(serviceRoot + "scheduler")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("scheduler").get("schedulerInfo").get("rootQueue");

                                if (resp != null) {
                                    ObjectNode total = Json.newObject();
                                    int total_vcores = resp.get("clusterResources").get("vCores").asInt();
                                    int used_vcores = resp.get("usedResources").get("vCores").asInt();
                                    int free_vcores = total_vcores - used_vcores;

                                    ObjectNode used = Json.newObject();
                                    used.put("category", "used");
                                    used.put("value", used_vcores);
                                    result.add(used);

                                    ObjectNode free = Json.newObject();
                                    free.put("category", "free");
                                    free.put("value", free_vcores);
                                    result.add(free);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );


    }

    public static Result memory() {

        return async(
                WS.url(serviceRoot + "scheduler")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("scheduler").get("schedulerInfo").get("rootQueue");

                                if (resp != null) {
                                    ObjectNode total = Json.newObject();
                                    int total_memory = resp.get("clusterResources").get("memory").asInt();
                                    int used_memory = resp.get("usedResources").get("memory").asInt();
                                    int free_memory = total_memory - used_memory;

                                    ObjectNode used = Json.newObject();
                                    used.put("category", "used");
                                    used.put("value", used_memory);
                                    result.add(used);

                                    ObjectNode free = Json.newObject();
                                    free.put("category", "free");
                                    free.put("value", free_memory);
                                    result.add(free);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );

    }


    public static Result sparknodes() {

        return async(
                WS.url(sparkserviceRoot + "metrics")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("clusterMetrics");

                                if (resp != null) {
                                    ObjectNode active = Json.newObject();
                                    int activeNodes = resp.get("activeNodes").asInt();
                                    active.put("category", "active");
                                    active.put("value", activeNodes);
                                    result.add(active);

                                    ObjectNode lost = Json.newObject();
                                    int lostNodes = resp.get("lostNodes").asInt();
                                    lost.put("category", "lost");
                                    lost.put("value", lostNodes);
                                    result.add(lost);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result sparkvcores() {

        return async(
                WS.url(sparkserviceRoot + "scheduler")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("scheduler").get("schedulerInfo").get("rootQueue");

                                if (resp != null) {
                                    ObjectNode total = Json.newObject();
                                    int total_vcores = resp.get("clusterResources").get("vCores").asInt();
                                    int used_vcores = resp.get("usedResources").get("vCores").asInt();
                                    int free_vcores = total_vcores - used_vcores;

                                    ObjectNode used = Json.newObject();
                                    used.put("category", "used");
                                    used.put("value", used_vcores);
                                    result.add(used);

                                    ObjectNode free = Json.newObject();
                                    free.put("category", "free");
                                    free.put("value", free_vcores);
                                    result.add(free);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );

    }

    public static Result sparkmemory() {

        return async(
                WS.url(sparkserviceRoot + "scheduler")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("scheduler").get("schedulerInfo").get("rootQueue");

                                if (resp != null) {
                                    ObjectNode total = Json.newObject();
                                    int total_memory = resp.get("clusterResources").get("memory").asInt();
                                    int used_memory = resp.get("usedResources").get("memory").asInt();
                                    int free_memory = total_memory - used_memory;

                                    ObjectNode used = Json.newObject();
                                    used.put("category", "used");
                                    used.put("value", used_memory);
                                    result.add(used);

                                    ObjectNode free = Json.newObject();
                                    free.put("category", "free");
                                    free.put("value", free_memory);
                                    result.add(free);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );

    }

    public static Result appSummary() {

        return async(
                WS.url(serviceRoot + "metrics")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("clusterMetrics");

                                if (resp != null) {
                                    ObjectNode completed = Json.newObject();
                                    int appsCompleted = resp.get("appsCompleted").asInt();
                                    completed.put("category", "completed");
                                    completed.put("value", appsCompleted);
                                    result.add(completed);

                                    ObjectNode running = Json.newObject();
                                    int appsRunning = resp.get("appsRunning").asInt();
                                    running.put("category", "running");
                                    running.put("value", appsRunning);
                                    result.add(running);

                                    ObjectNode pending = Json.newObject();
                                    int appsPending = resp.get("appsPending").asInt();
                                    pending.put("category", "pending");
                                    pending.put("value", appsPending);
                                    result.add(pending);

                                    ObjectNode failed = Json.newObject();
                                    int appsFailed = resp.get("appsFailed").asInt();
                                    failed.put("category", "failed");
                                    failed.put("value", appsFailed);
                                    result.add(failed);

                                    ObjectNode killed = Json.newObject();
                                    int appsKilled = resp.get("appsKilled").asInt();
                                    killed.put("category", "killed");
                                    killed.put("value", appsKilled);
                                    result.add(killed);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result apps(String user, String queue, String startedTimeBegin, String startedTimeEnd, String limit, String state, String finalStatus) {

        return async(
                WS.url(serviceRoot + "apps")
                        .setQueryParameter("user", user)
                        .setQueryParameter("queue", queue)
                        .setQueryParameter("startedTimeBegin", startedTimeBegin)
                        .setQueryParameter("startedTimeEnd", startedTimeEnd)
                        .setQueryParameter("limit", limit)
                        .setQueryParameter("state", state)
                        .setQueryParameter("finalStatus", finalStatus)
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                if ("null".equals(response.asJson().get("apps").toString())) {
                                    return ok();
                                } else {
                                    return ok(response.asJson().get("apps").get("app"));
                                }
                            }
                        }
                )
        );
    }

    public static Result sparkappSummary() {

        return async(
                WS.url(sparkserviceRoot + "metrics")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {

                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("clusterMetrics");

                                if (resp != null) {
                                    ObjectNode completed = Json.newObject();
                                    int appsCompleted = resp.get("appsCompleted").asInt();
                                    completed.put("category", "completed");
                                    completed.put("value", appsCompleted);
                                    result.add(completed);

                                    ObjectNode running = Json.newObject();
                                    int appsRunning = resp.get("appsRunning").asInt();
                                    running.put("category", "running");
                                    running.put("value", appsRunning);
                                    result.add(running);

                                    ObjectNode pending = Json.newObject();
                                    int appsPending = resp.get("appsPending").asInt();
                                    pending.put("category", "pending");
                                    pending.put("value", appsPending);
                                    result.add(pending);

                                    ObjectNode failed = Json.newObject();
                                    int appsFailed = resp.get("appsFailed").asInt();
                                    failed.put("category", "failed");
                                    failed.put("value", appsFailed);
                                    result.add(failed);

                                    ObjectNode killed = Json.newObject();
                                    int appsKilled = resp.get("appsKilled").asInt();
                                    killed.put("category", "killed");
                                    killed.put("value", appsKilled);
                                    result.add(killed);

                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result sparkapps(String user, String queue, String startedTimeBegin, String startedTimeEnd, String limit, String state, String finalStatus) {

        return async(
                WS.url(sparkserviceRoot + "apps")
                        .setQueryParameter("user", user)
                        .setQueryParameter("queue", queue)
                        .setQueryParameter("startedTimeBegin", startedTimeBegin)
                        .setQueryParameter("startedTimeEnd", startedTimeEnd)
                        .setQueryParameter("limit", limit)
                        .setQueryParameter("state", state)
                        .setQueryParameter("finalStatus", finalStatus)
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                if ("null".equals(response.asJson().get("apps").toString())) {
                                    return ok();
                                } else {
                                    return ok(response.asJson().get("apps").get("app"));
                                }
                            }
                        }
                )
        );
    }

}
