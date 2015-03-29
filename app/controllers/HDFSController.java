package controllers;

import org.apache.hadoop.util.Shell;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONObject;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HDFSController extends Controller {
    public static ObjectMapper mapper = new ObjectMapper();

    private static String namenode_http_address = Play.application().configuration().getString("hadoop.hdfs.namenode.http.address");
    private static String namenode_jmx_url = "http://" + namenode_http_address + "/jmx";
    private static String webhdfs_base_url = "http://" + namenode_http_address + "/webhdfs/v1";

    public static Result nodes() {
        return async(play.libs.WS.url(namenode_jmx_url).get().map(
                new F.Function<play.libs.WS.Response, Result>() {
                    public Result apply(play.libs.WS.Response response) {

                        List<JsonNode> result = new ArrayList<JsonNode>();

                        JsonNode resp = response.asJson().get("beans");

                        // Hadoop:service=NameNode,name=FSNamesystemState
                        JsonNode hdfsState = resp.get(16);

                        if (hdfsState != null) {

                            ObjectNode live = Json.newObject();
                            int liveDataNodes = hdfsState.get("NumLiveDataNodes").asInt();
                            live.put("category", "live");
                            live.put("value", liveDataNodes);
                            result.add(live);

                            ObjectNode dead = Json.newObject();
                            int deadDataNodes = hdfsState.get("NumDeadDataNodes").asInt();
                            dead.put("category", "dead");
                            dead.put("value", deadDataNodes);
                            result.add(dead);

                            return ok(mapper.valueToTree(result));
                        } else {
                            return ok();
                        }


                    }
                }));
    }

    public static Result capacity() {
        return async(play.libs.WS.url(namenode_jmx_url).get().map(
                new F.Function<play.libs.WS.Response, Result>() {
                    public Result apply(play.libs.WS.Response response) {

                        List<JsonNode> result = new ArrayList<JsonNode>();

                        JsonNode resp = response.asJson().get("beans");

                        // Hadoop:service=NameNode,name=FSNamesystem
                        JsonNode hdfsState = resp.get(22);

                        if (hdfsState != null) {

                            ObjectNode used = Json.newObject();
                            int capacityUsedGB = hdfsState.get("CapacityUsedGB").asInt();
                            used.put("category", "used");
                            used.put("value", capacityUsedGB);
                            result.add(used);

                            ObjectNode remaining = Json.newObject();
                            int capacityRemainingGB = hdfsState.get("CapacityRemainingGB").asInt();
                            remaining.put("category", "remaining");
                            remaining.put("value", capacityRemainingGB);
                            result.add(remaining);

                            return ok(mapper.valueToTree(result));
                        } else {
                            return ok();
                        }


                    }
                }));
    }

    public static Result blocks() {
        return async(play.libs.WS.url(namenode_jmx_url).get().map(
                new F.Function<play.libs.WS.Response, Result>() {
                    public Result apply(play.libs.WS.Response response) {

                        List<JsonNode> result = new ArrayList<JsonNode>();

                        JsonNode resp = response.asJson().get("beans");

                        // Hadoop:service=NameNode,name=FSNamesystem
                        JsonNode hdfsState = resp.get(22);

                        if (hdfsState != null) {

                            ObjectNode used = Json.newObject();
                            int blocksTotal = hdfsState.get("BlocksTotal").asInt();
                            used.put("category", "used");
                            used.put("value", blocksTotal);
                            result.add(used);

                            ObjectNode remaining = Json.newObject();
                            int blockCapacity = hdfsState.get("BlockCapacity").asInt();
                            int remainingBlocks = blockCapacity - blocksTotal;
                            remaining.put("category", "remaining");
                            remaining.put("value", remainingBlocks);
                            result.add(remaining);

                            ObjectNode missing = Json.newObject();
                            int missingBlocks = hdfsState.get("MissingBlocks").asInt();
                            missing.put("category", "missing");
                            missing.put("value", missingBlocks);
                            result.add(missing);

                            ObjectNode corrupt = Json.newObject();
                            int corruptBlocks = hdfsState.get("CorruptBlocks").asInt();
                            corrupt.put("category", "corrupt");
                            corrupt.put("value", corruptBlocks);
                            result.add(corrupt);

                            ObjectNode scheduledReplication = Json.newObject();
                            int scheduledReplicationBlocks = hdfsState.get("ScheduledReplicationBlocks").asInt();
                            scheduledReplication.put("category", "scheduledReplication");
                            scheduledReplication.put("value", scheduledReplicationBlocks);
                            result.add(scheduledReplication);

                            ObjectNode pendingReplication = Json.newObject();
                            int pendingReplicationBlocks = hdfsState.get("PendingReplicationBlocks").asInt();
                            pendingReplication.put("category", "pendingReplication");
                            pendingReplication.put("value", pendingReplicationBlocks);
                            result.add(pendingReplication);

                            ObjectNode underReplicated = Json.newObject();
                            int underReplicatedBlocks = hdfsState.get("UnderReplicatedBlocks").asInt();
                            underReplicated.put("category", "underReplicated");
                            underReplicated.put("value", underReplicatedBlocks);
                            result.add(underReplicated);

                            ObjectNode postponedMisreplicated = Json.newObject();
                            int postponedMisreplicatedBlocks = hdfsState.get("PostponedMisreplicatedBlocks").asInt();
                            postponedMisreplicated.put("category", "postponedMisreplicated");
                            postponedMisreplicated.put("value", postponedMisreplicatedBlocks);
                            result.add(postponedMisreplicated);

                            ObjectNode pendingDeletion = Json.newObject();
                            int pendingDeletionBlocks = hdfsState.get("PendingDeletionBlocks").asInt();
                            pendingDeletion.put("category", "pendingDeletion");
                            pendingDeletion.put("value", pendingDeletionBlocks);
                            result.add(pendingDeletion);

                            ObjectNode excess = Json.newObject();
                            int excessBlocks = hdfsState.get("ExcessBlocks").asInt();
                            excess.put("category", "excess");
                            excess.put("value", excessBlocks);
                            result.add(excess);

                            return ok(mapper.valueToTree(result));
                        } else {
                            return ok();
                        }

                    }
                }));
    }


    public static Result getContentSummary(String pathSuffix) {

        String url = webhdfs_base_url + pathSuffix;

        return async(play.libs.WS.url(url).setQueryParameter("user.name", "hdfs").setQueryParameter("op", "GETCONTENTSUMMARY").get().map(
                new F.Function<play.libs.WS.Response, Result>() {
                    public Result apply(play.libs.WS.Response response) {

                        List<JsonNode> result = new ArrayList<JsonNode>();

                        JsonNode contentSummary = response.asJson().get("ContentSummary");


                        if (contentSummary != null) {
                            result.add(contentSummary);
                            return ok(contentSummary);
                        } else {
                            return ok();
                        }
                    }
                }
        ));
    }

    public static Result logSize() {

        List<JsonNode> result = new ArrayList<JsonNode>();

        JsonNode logs = duOfPath("/mvad/{rawlog,sessionlog}/*");
        if (logs.isArray()) {


            for (int i = 14; i >= 0; i--) {
                ObjectNode eachday = Json.newObject();

                Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
                cal.add(Calendar.DAY_OF_MONTH, -i);//取当前日期的前一天.
                Date date = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = sdf.format(date);
                eachday.put("date", dateStr);

                for (JsonNode path : logs) {
                    String logdate = path.get("pathSuffix").asText();
                    String fullPath = path.get("fullPath").asText();
                    String logName = fullPath.substring(0,fullPath.lastIndexOf("/")).replaceAll("/", "_").replaceAll("-","_");
                    Long size = path.get("size").asLong() / 1024 / 1024 / 1024;
                    if (dateStr.equalsIgnoreCase(logdate)) {
                        eachday.put(logName, size);
                    }

                }
                result.add(eachday);
            }
        }

        return ok(mapper.valueToTree(result));
    }

    public static Result du(final String path) {
        return ok(mapper.valueToTree(duOfPath(path)));
    }

    public static JsonNode duOfPath(final String path) {

        List<JsonNode> result = new ArrayList<JsonNode>();

        Shell.ShellCommandExecutor shExec = null;
        // Setup command to run
        String[] command = {"sudo", "-iu", "hdfs", "hadoop", "fs", "-du", path};

        Logger.info("executing command: " + Arrays.toString(command));
        try {

            shExec = new Shell.ShellCommandExecutor(command, new File("/tmp"));
            shExec.execute();

            int exitCode = shExec.getExitCode();
            Logger.warn("Exit code from command is : " + exitCode);
            String message = shExec.getOutput();
            String[] lines = message.split("\n");
            for (String line : lines) {

                ObjectNode eachfile = Json.newObject();
                eachfile.put("parent", path);
                String fullPath = line.split("\\s+")[1];
                String size = line.split("\\s+")[0];
                eachfile.put("pathSuffix", fullPath.substring(fullPath.lastIndexOf("/") + 1));
                eachfile.put("fullPath", fullPath);
                eachfile.put("size", Long.valueOf(size));

                result.add(eachfile);
            }

        } catch (IOException e) {

            int exitCode = shExec.getExitCode();
            String message = shExec.getOutput();
            Logger.error("IOException when running command : " + Arrays.toString(command) + ", exitcode is " + exitCode + ", " + e.getMessage() + ", message: " + message);

        }

        return mapper.valueToTree(result);
    }


    public static Result listStatus(final String pathSuffix) {

        mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, true);

        String url = webhdfs_base_url + pathSuffix;

        return async(play.libs.WS.url(url).setQueryParameter("user.name", "hdfs").setQueryParameter("op", "LISTSTATUS").get().map(
                new F.Function<play.libs.WS.Response, Result>() {
                    public Result apply(play.libs.WS.Response response) {

                        List<JsonNode> result = new ArrayList<JsonNode>();

                        JsonNode fileStatus = response.asJson().get("FileStatuses").get("FileStatus");

                        String root = null;
                        for (JsonNode file : fileStatus) {
                            ObjectNode eachfile = (ObjectNode) file;
//                            if ("/".equalsIgnoreCase(pathSuffix)){
                            eachfile.put("parentPath", root);
//                            }else {
//                                eachfile.put("parentPath", pathSuffix);
//                            }
                            result.add(eachfile);
                        }

                        return ok(mapper.valueToTree(result));
                    }
                }
        ));
    }
}
