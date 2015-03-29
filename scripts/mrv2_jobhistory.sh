#! /bin/bash 
set -e

DIR=$(cd `dirname $0`;pwd)
PYTHON_SCRIPT="mrv2_jobhistory.py"
LOG="mrv2_jobhistory.log"

FOUR_HOUR_AGO_IN_MS="$(date --date='4 hours ago' +%s)"000
NOW_IN_MS="$(date +%s)"000
echo -e "`date "+%F %T"`\t INFO ----LOADING MRv2 JOB HISTORY TO DB BETWEEN $FOUR_HOUR_AGO_IN_MS AND $NOW_IN_MS----"

/usr/bin/python2.7 $DIR/$PYTHON_SCRIPT $FOUR_HOUR_AGO_IN_MS $NOW_IN_MS 

if [ $? -ne 0 ]; then
    echo -e "`date +"%Y-%m-%d %T"`\tERROR\t LOADING MRv2 JOB to DB failed, exit." 
    exit 1
else
   echo -e "`date +"%Y-%m-%d %T"`\tINFO\t LOADING MRv2 JOB to DB success." 
fi
