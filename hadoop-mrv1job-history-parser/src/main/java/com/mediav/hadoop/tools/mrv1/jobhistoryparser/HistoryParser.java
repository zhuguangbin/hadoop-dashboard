/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediav.hadoop.tools.mrv1.jobhistoryparser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapred.JobHistory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is to view job history files.
 */
class HistoryParser {
    private static final Log LOG = LogFactory.getLog(Main.class);

    private FileSystem fs;
    private Configuration conf;
    private Path historyLogDir;
    private Path parsedhistoryDir;


    private PathFilter jobLogFileFilter = new PathFilter() {
        public boolean accept(Path path) {
            return !(path.getName().endsWith(".xml"));
        }
    };

    public HistoryParser(Configuration conf, String jobhistoryDir, String parsedJobhistoryDir)
            throws IOException {
        this.conf = conf;
        historyLogDir = new Path(jobhistoryDir);
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        parsedhistoryDir = new Path(parsedJobhistoryDir, dateStr);

        try {
            fs = historyLogDir.getFileSystem(this.conf);
            if (!fs.exists(historyLogDir)) {
                throw new IOException("History directory " + historyLogDir.toString()
                        + "does not exist");
            }
            if (!fs.exists(parsedhistoryDir)) {
                fs.mkdirs(parsedhistoryDir);
            }

        } catch (Exception e) {
            throw new IOException("Not able to initialize History viewer", e);
        }
    }

    public void parse() throws IOException {

        Path[] jobFiles = FileUtil.stat2Paths(fs.listStatus(historyLogDir,
                jobLogFileFilter));
        for (Path jobfile : jobFiles) {

            if (fs.getFileStatus(jobfile).getLen() == 0) {
                continue;
            } else {
                String[] jobDetails =
                        JobHistory.JobInfo.decodeJobHistoryFileName(jobfile.getName()).split("_");

                String jobId = jobDetails[0] + "_" + jobDetails[1] + "_" + jobDetails[2];
                String confFileName = jobId+"_conf.xml";

                JobHistory.JobInfo job = new JobHistory.JobInfo(jobId);
                JobHistoryFileParser.parseJobTasks(jobfile.toString(), job, fs);
                String status = job.get(JobHistory.Keys.JOB_STATUS);
                if (status != null && !"RUNNING".equals(status)) {
                    Path targetFile = new Path(parsedhistoryDir, jobfile.getName());
                    LOG.info(jobId + " is finished, backup history file from " + historyLogDir + " to " + parsedhistoryDir);

                    fs.rename(jobfile, new Path(parsedhistoryDir, jobfile.getName()));
//                    fs.rename(new Path(historyLogDir,confFileName),new Path(parsedhistoryDir,confFileName));
                    printAsJson(job);
                }
            }
        }

    }


    public void printAsJson(JobHistory.JobInfo job) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(job));
    }

}
