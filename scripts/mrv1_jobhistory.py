#! /usr/bin/python2.7

__author__ = 'guangbin'

import sys,json
import traceback
import re
from datetime import datetime
import MySQLdb

def usage():
    print >> sys.stderr, "Usage: \n "+sys.argv[0]+ " <historyfile>"

def prepareJob(jobinfo):
    jobsummary = (jobinfo['JOBID'],jobinfo['JOBNAME'],jobinfo['USER'],
                   jobinfo['JOB_QUEUE'],jobinfo['JOB_PRIORITY'],jobinfo['JOB_STATUS'],
                   datetime.fromtimestamp(int(jobinfo['SUBMIT_TIME'])/1000).strftime('%Y-%m-%d %H:%M:%S'),
		   datetime.fromtimestamp(int(jobinfo['LAUNCH_TIME'])/1000).strftime('%Y-%m-%d %H:%M:%S'),
		   datetime.fromtimestamp(int(jobinfo['FINISH_TIME'])/1000).strftime('%Y-%m-%d %H:%M:%S'),
                   jobinfo['TOTAL_MAPS'],jobinfo['FINISHED_MAPS'],jobinfo['FAILED_MAPS'],
                   jobinfo['TOTAL_REDUCES'],jobinfo['FINISHED_REDUCES'],jobinfo['FAILED_REDUCES']
                   )
	
    job_counters_raw = jobinfo.get('COUNTERS')

    if job_counters_raw:

        p=re.compile(r'\(\w+\)\([\\\(\)\w\s:-]+\)\(\d+\)')
        found=p.findall(job_counters_raw)

        counters = {}
        for i in found:
            key=i.split('(')[1][:-1]
            value=i.split('(')[-1][:-1]
            counters[key]=value
			
        jobcounter = (jobinfo['JOBID'], counters.get('HDFS_BYTES_READ',0), counters.get('HDFS_BYTES_WRITTEN',0), 
		counters.get('FILE_BYTES_READ',0), counters.get('FILE_BYTES_WRITTEN',0),
		counters.get('TOTAL_LAUNCHED_MAPS',0), counters.get('DATA_LOCAL_MAPS',0), counters.get('RACK_LOCAL_MAPS',0), counters.get('TOTAL_LAUNCHED_REDUCES',0), 
		counters.get('MAP_INPUT_RECORDS',0), counters.get('MAP_OUTPUT_RECORDS',0), counters.get('MAP_OUTPUT_BYTES',0),
                counters.get('COMBINE_INPUT_RECORDS',0), counters.get('COMBINE_OUTPUT_RECORDS',0), counters.get('SPILLED_RECORDS',0),
		counters.get('REDUCE_SHUFFLE_BYTES',0), counters.get('REDUCE_INPUT_GROUPS',0), 
		counters.get('REDUCE_INPUT_RECORDS',0), counters.get('REDUCE_OUTPUT_RECORDS',0), counters.get('CPU_MILLISECONDS',0)
                        )


    return (jobsummary,jobcounter)

def save2DB(jobsummary, jobcounter):

    try:
        conn=MySQLdb.connect(host='st1dg',user='hadoop',passwd='RNymee2527#',port=3306)
        cur=conn.cursor()
        cur.execute('REPLACE INTO hadoop.mrv1_job_summary (JOBID,JOBNAME,USER,JOB_QUEUE,JOB_PRIORITY,JOB_STATUS,SUBMIT_TIME,LAUNCH_TIME,FINISH_TIME,TOTAL_MAPS,FINISHED_MAPS,FAILED_MAPS,TOTAL_REDUCES,FINISHED_REDUCES,FAILED_REDUCES) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)',jobsummary)
        cur.execute('REPLACE INTO hadoop.mrv1_job_counters (JOBID,HDFS_BYTES_READ,HDFS_BYTES_WRITTEN,FILE_BYTES_READ,FILE_BYTES_WRITTEN,TOTAL_LAUNCHED_MAPS,DATA_LOCAL_MAPS,RACK_LOCAL_MAPS,TOTAL_LAUNCHED_REDUCES,MAP_INPUT_RECORDS,MAP_OUTPUT_RECORDS,MAP_OUTPUT_BYTES,COMBINE_INPUT_RECORDS,COMBINE_OUTPUT_RECORDS,SPILLED_RECORDS,REDUCE_SHUFFLE_BYTES,REDUCE_INPUT_GROUPS,REDUCE_INPUT_RECORDS,REDUCE_OUTPUT_RECORDS,CPU_MILLISECONDS) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)', jobcounter)
        conn.commit()
        cur.close()
        conn.close()
		
    except MySQLdb.Error,e:
        print >> sys.stderr, "Mysql Error %d: %s" % (e.args[0], e.args[1])


def main(historyfile):

    with open(historyfile) as f:
        err = 0
        for i,line in enumerate(f, 1):
            try:
                jobinfo = json.loads(line)['values']
		if jobinfo:
			print 'parsing ' + jobinfo.get('JOBID')
			(jobsummary, jobcounter) = prepareJob(jobinfo)
			save2DB(jobsummary, jobcounter)
                
            except Exception,e:
                err+=1
                print >> sys.stderr, " error json line : " + str(i) + ", total err count : " + str(err) + ", " + str(e)
                traceback.print_exc()

if __name__ == "__main__":

    if(len(sys.argv)!=2):
        usage()
    else:
        main(sys.argv[1])
