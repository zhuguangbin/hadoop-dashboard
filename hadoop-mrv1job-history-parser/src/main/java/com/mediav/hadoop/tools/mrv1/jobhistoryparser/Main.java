
package com.mediav.hadoop.tools.mrv1.jobhistoryparser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

/*
 */
public class Main extends Configured implements Tool {
  private static final Log LOG = LogFactory.getLog(Main.class);

  static {
    Configuration.addDefaultResource("mapred-default.xml");
    Configuration.addDefaultResource("mapred-site.xml");
  }

  /**
   * Display usage of the command-line tool and terminate execution
   */
  private void displayUsage() {
    String cmd = "Usage: Main ";
    System.err.println(cmd + "[" + " <jobhistory_dir> <parsed_jobhistory_dir> ]");
  }

  public int run(String[] argv) throws Exception {
    int exitCode = -1;
    String jobhistoryDir = null;
    String parsedJobhistoryDir = null;
    if (argv.length != 2) {
      displayUsage();
      return exitCode;
    } else {
      jobhistoryDir = argv[0];
      parsedJobhistoryDir = argv[1];

    }
    startParsingJobHistory(jobhistoryDir, parsedJobhistoryDir);
    exitCode = 0;

    return exitCode;
  }

  private void startParsingJobHistory(String jobhistoryDir, String parsedJobhistoryDir)
          throws IOException {
    HistoryParser historyViewer = new HistoryParser(getConf(), jobhistoryDir, parsedJobhistoryDir);
    historyViewer.parse();
  }

  /**
   */
  public static void main(String argv[]) throws Exception {
    int res = ToolRunner.run(new Main(), argv);
    System.exit(res);
  }

}

