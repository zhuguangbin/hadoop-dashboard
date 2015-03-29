#!/bin/sh

set -e

DIR=$(cd `dirname $0`;pwd)
EXECTIME="$(date +%Y%m%d%H%M%S)"
HISTORYLOGDIR="/hadoop/userjoblog/_logs/history"
BACKUPDIR="/hadoop/userjoblog/backup"
PARSEDDIR="/hadoop/parsedjobhistory"
HISTORYPARSER=$DIR/jobhistoryparser-1.0.jar
PYTHON_SCRIPT=$DIR/mrv1_jobhistory.py
JOBINFO_JSONFILE=$DIR/job.json.$EXECTIME

function runJobHistoryParser(){

  echo "INFO|`date "+%F %T"`:begin ${EXECTIME} running jobhistory parser." 
  export HADOOP_CONF_DIR=/etc/hadoop/conf.prod-hadoop
  export HADOOP_MAPRED_HOME=/usr/lib/hadoop-0.20-mapreduce
  /usr/bin/hadoop jar $HISTORYPARSER com.mediav.hadoop.tools.mrv1.jobhistoryparser.Main $HISTORYLOGDIR $BACKUPDIR > $JOBINFO_JSONFILE

  if [ $? -ne 0 ]; then
    echo "ERROR|`date "+%F %T"`:run job history parser error: ${EXECTIME} ." 
    exit -1
  fi
}

function save2DB(){

  echo "INFO|`date "+%F %T"`:begin ${EXECTIME} saving to DB." 

  /usr/bin/python2.7 $PYTHON_SCRIPT $JOBINFO_JSONFILE 

  if [ $? -ne 0 ]; then
    echo "ERROR|`date "+%F %T"`:convert json to tsv: ${EXECTIME} ." 
    exit -1
  fi
}

function cleanup(){

  echo "INFO|`date "+%F %T"`:begin cleaning up local file" 
  rm -f $JOBINFO_JSONFILE 

}


runJobHistoryParser
save2DB
cleanup
