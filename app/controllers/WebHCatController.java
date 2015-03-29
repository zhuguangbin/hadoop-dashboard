package controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import play.Play;
import play.libs.F.Function;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

public class WebHCatController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    private static String webhcat_http_address = Play.application().configuration().getString("hadoop.hcatalog.webhcat.http.address");
    private static String serviceRoot = "http://"+webhcat_http_address+"/templeton/v1";

    public static Result databases() {

        return async(
                WS.url(serviceRoot + "/ddl/database")
                        .setQueryParameter("user.name", "hive")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("databases");

                                if (resp != null) {
                                    for (JsonNode node : resp) {
                                        ObjectNode db = Json.newObject();
                                        db.put("db_name", node);
                                        result.add(db);
                                    }
                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result tables(final String db_name) {

        return async(
                WS.url(serviceRoot + "/ddl/database/" + db_name + "/table")
                        .setQueryParameter("user.name", "hive")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("tables");

                                if (resp != null) {
                                    for (JsonNode node : resp) {
                                        ObjectNode db = Json.newObject();
                                        db.put("db_name", db_name);
                                        db.put("table_name", node);
                                        result.add(db);
                                    }
                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result partitions(final String db_name, final String table_name) {

        return async(
                WS.url(serviceRoot + "/ddl/database/" + db_name + "/table/" + table_name + "/partition")
                        .setQueryParameter("user.name", "hive")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                List<JsonNode> result = new ArrayList<JsonNode>();

                                JsonNode resp = response.asJson().get("partitions");

                                if (resp != null) {
                                    for (JsonNode node : resp) {
                                        ObjectNode db = Json.newObject();
                                        db.put("db_name", db_name);
                                        db.put("table_name", table_name);
                                        db.put("partition_name", node.get("name"));
                                        result.add(db);
                                    }
                                    return ok(mapper.valueToTree(result));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

    public static Result columns(final String db_name, final String table_name) {

        return async(
                WS.url(serviceRoot + "/ddl/database/" + db_name + "/table/" + table_name)
                        .setQueryParameter("user.name", "hive")
                        .get().map(
                        new Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                JsonNode resp = response.asJson().get("columns");

                                if (resp != null) {
                                    return ok(mapper.valueToTree(resp));
                                } else {
                                    return ok();
                                }
                            }
                        }
                )
        );
    }

}
