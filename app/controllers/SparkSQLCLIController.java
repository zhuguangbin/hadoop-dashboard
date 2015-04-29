package controllers;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import models.SparkSQLHistroy;
import org.apache.hadoop.util.Shell;
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
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SparkSQLCLIController extends Controller {
  public static ObjectMapper mapper = new ObjectMapper();


  public static Result runsql() {

    DynamicForm request = Form.form().bindFromRequest();
    String sql = request.get("sql").trim();
    Logger.info("get SQL query request : " + sql);

    SparkSQLHistroy histroy = null;
    JsonNode response = null;

    try {
      // log history to db for analysis
      histroy = new SparkSQLHistroy();
      histroy.setUser("hadoop");
      histroy.setSqlstr(sql);
      histroy.setStartTime(new Date());
      histroy.save();

      // run sql
      response = executeSQL(histroy.getId(), sql);
      Logger.info("DONE executing sql : " + sql);
      return ok(response);
    } catch (Exception e) {
      Logger.error("ERROR executing sql : " + sql + ", Internal Server Exception" + e.getMessage());
      ObjectNode errResponse = Json.newObject();
      errResponse.put("retcode", -1);
      errResponse.put("message", "Internal Server Exception: " + e.getMessage());
      return ok(errResponse);
    } finally {
      // update history to db
      String savedFilename = histroy.getId() + ".result.json";
      histroy.setFinishTime(new Date());
      histroy.setMessage(response.get("message") != null ? response.get("message").asText() : "");
      histroy.setRetcode(response.get("retcode") != null ? response.get("retcode").asInt() : -1);
      histroy.setResultFile(savedFilename);
      histroy.save();

    }

  }


  public static ObjectNode executeSQL(Long execId, String sql) {

    ObjectNode response = Json.newObject();

    File workingDir = initWorkingDir(execId);
    File sqlScript = writeSqlScript(workingDir, sql);
    File execScript = writeExecScript(workingDir, sqlScript.getAbsolutePath());


    Shell.ShellCommandExecutor shExec = null;
    // Setup command to run
    String[] command = {"sh",  execScript.getAbsolutePath()};

    Logger.info("executing command: " + Arrays.toString(command));
    try {

      shExec = new Shell.ShellCommandExecutor(command, workingDir);
      shExec.execute();

      int exitCode = shExec.getExitCode();
      Logger.warn("Exit code from command is : " + exitCode);
      String message = shExec.getOutput();

      response.put("retcode", exitCode);
      response.put("message", message);
      return response;

    } catch (IOException e) {

      int exitCode = shExec.getExitCode();
      String message = shExec.getOutput();
      Logger.error("IOException when running command : " + Arrays.toString(command) + ", exitcode is " + exitCode + ", " + e.getMessage() + ", message: " + message);

      response.put("retcode", exitCode);
      response.put("message", message);
      return response;
    }
  }

  private static File initWorkingDir(Long execId) {

    File parent = new File("/data/sparksqlweb/executions");
    if (!parent.exists()) {
      parent.mkdir();
    }

    File workingDir = new File(parent, execId.toString());
    if (!workingDir.exists()) {
      workingDir.mkdir();
    }

    return workingDir;
  }


  @BodyParser.Of(BodyParser.Json.class)
  public static JsonNode resultSet2Json(ResultSet rs) throws SQLException {

    List<JsonNode> result = new ArrayList<JsonNode>();

    if (rs != null) {
      while (rs.next()) {
        result.add(mapRow(rs, 1));
      }
    }

    return mapper.valueToTree(result);

  }

  public static JsonNode mapRow(ResultSet rs, int rowNum) throws SQLException {
    ObjectNode objectNode = mapper.createObjectNode();
    ResultSetMetaData rsmd = rs.getMetaData();
    int columnCount = rsmd.getColumnCount();
    for (int index = 1; index <= columnCount; index++) {
      String column = rsmd.getColumnName(index);
      Object value = rs.getObject(column);
      if (value == null) {
        objectNode.putNull(column);
      } else if (value instanceof Integer) {
        objectNode.put(column, (Integer) value);
      } else if (value instanceof String) {
        objectNode.put(column, (String) value);
      } else if (value instanceof Boolean) {
        objectNode.put(column, (Boolean) value);
      } else if (value instanceof Date) {
        objectNode.put(column, ((Date) value).getTime());
      } else if (value instanceof Long) {
        objectNode.put(column, (Long) value);
      } else if (value instanceof Double) {
        objectNode.put(column, (Double) value);
      } else if (value instanceof Float) {
        objectNode.put(column, (Float) value);
      } else if (value instanceof BigDecimal) {
        objectNode.put(column, (BigDecimal) value);
      } else if (value instanceof Byte) {
        objectNode.put(column, (Byte) value);
      } else if (value instanceof byte[]) {
        objectNode.put(column, (byte[]) value);
      } else {
        throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
      }
    }
    return objectNode;
  }

  public static File writeSqlScript(File workingDir, String sql) {
    File sqlScript = new File(workingDir, "run.hql");

    Writer writer = null;

    try {
      writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(sqlScript), "utf-8"));
      writer.write(sql);
    } catch (IOException ex) {
      // report
      Logger.warn("error saving sql to file : " + sqlScript.getAbsolutePath() + " , " + ex.getMessage());
    } finally {
      try {
        writer.close();
      } catch (Exception ex) {
      }
      return sqlScript;
    }
  }

  public static File writeExecScript(File workingDir, String sqlScript) {
    File execScript = new File(workingDir, "run.sh");

    Writer writer = null;

    try {
      String command = "/opt/spark/bin/spark-sql --master yarn-client --queue sparksql -f " + sqlScript + ">"+workingDir.getAbsolutePath()+"/stdout 2>"+workingDir.getAbsolutePath()+"/stderr";
      Logger.info("Writting command to execScript : " + execScript.getAbsolutePath() + ", Command: " + command);
      writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(execScript), "utf-8"));
      writer.write(command);
    } catch (IOException ex) {
      // report
      Logger.warn("error saving sql to file : " + execScript.getAbsolutePath() + " , " + ex.getMessage());
    } finally {
      try {
        writer.close();
      } catch (Exception ex) {
      }
      return execScript;
    }
  }

}
