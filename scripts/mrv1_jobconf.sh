#!/bin/sh

set -e

DIR=$(cd `dirname $0`;pwd)
EXECTIME="$(date +%Y%m%d%H%M%S)"
DATE="$(date +%Y%m%d)"
HISTORYLOGDIR="/hadoop/userjoblog/_logs/history"
TEMPDIR="/tmp/jobconf"
LOCALTEMPDIR="/tmp"
BACKUPDIR="/hadoop/userjoblog/backup/"$DATE
PYTHON_SCRIPT=$DIR/mrv1_jobconf.py


echo "INFO|`date "+%F %T"`:BEGIN ${EXECTIME} ensuring dir .."
hadoop fs -test -d $BACKUPDIR
if [ $? -ne 0 ]; then
  /usr/bin/hadoop fs -mkdir $BACKUPDIR
fi

hadoop fs -test -d $TEMPDIR
if [ $? -ne 0 ]; then
  /usr/bin/hadoop fs -mkdir $TEMPDIR
fi

echo "INFO|`date "+%F %T"`:begin ${EXECTIME} moving conf files to tmpdir .."
/usr/bin/hadoop fs -mv $HISTORYLOGDIR/*conf.xml $TEMPDIR

echo "INFO|`date "+%F %T"`:begin ${EXECTIME} getting jobconf file from HDFS.."
/usr/bin/hadoop fs -get $TEMPDIR/*conf.xml $LOCALTEMPDIR

echo "INFO|`date "+%F %T"`:begin ${EXECTIME} parsing jobconf.."
for i in `ls $LOCALTEMPDIR/*conf.xml`
 do 
  /usr/bin/python2.7 $PYTHON_SCRIPT $i
done

echo "INFO|`date "+%F %T"`:begin ${EXECTIME} moving conf files to backupdir.."
/usr/bin/hadoop fs -mv  $TEMPDIR/*conf.xml $BACKUPDIR

echo "INFO|`date "+%F %T"`:begin ${EXECTIME} cleaning local jobconf files.."
rm -f $LOCALTEMPDIR/*conf.xml

echo "INFO|`date "+%F %T"`: END" 
