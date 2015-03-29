#!/bin/sh

set -e

DIR=$(cd `dirname $0`;pwd)
EXECTIME="$(date +%Y%m%d%H%M%S)"
DATE="$(date +%Y%m%d)"
HISTORYLOGDIR="/hadoop/userjoblog/_logs/history"
TEMPDIR="/tmp/jobconf"
BACKUPDIR="/hadoop/userjoblog/backup/"$DATE
PYTHON_SCRIPT=$DIR/mrv1_jobconf.py


echo "INFO|`date "+%F %T"`:begin ${EXECTIME} parsing jobconf.." 

hadoop fs -test -d $BACKUPDIR
if [ $? -ne 0 ]; then
  /usr/bin/hadoop fs -mkdir $BACKUPDIR
fi

hadoop fs -test -d $TEMPDIR
if [ $? -ne 0 ]; then
  /usr/bin/hadoop fs -mkdir $TEMPDIR
fi

/usr/bin/hadoop fs -mv $HISTORYLOGDIR/*conf.xml $TEMPDIR
/usr/bin/hadoop fs -get $TEMPDIR/*conf.xml

for i in `ls *conf.xml`
 do 
  /usr/bin/python2.7 $PYTHON_SCRIPT $i
done

/usr/bin/hadoop fs -mv  $TEMPDIR/*conf.xml $BACKUPDIR

rm -f *conf.xml

echo "INFO|`date "+%F %T"`:end parsing jobconf .." 
