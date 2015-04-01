#! /usr/bin/python2.7

__author__ = 'guangbin'

import sys,json
import traceback
import re
from datetime import datetime
import httplib
import MySQLdb

def usage():
    print >> sys.stderr, "Usage: \n "+sys.argv[0]+ " <starttimestamp_in_ms> <endtimestamp_in_ms>"

def prepareJob(jobids):
    jobsummaries = []
    jobcounters = []
    jobconfs = []

    for jobid in jobids:
        httpClient = None
        try:
            mapreduce_hs_host = 'jt2dg.prod.mediav.com'
            mapreduce_hs_jobs_url = '/ws/v1/history/mapreduce/jobs/'+jobid
            httpClient = httplib.HTTPConnection(mapreduce_hs_host, 19888, timeout=30)
            httpClient.request('GET', mapreduce_hs_jobs_url)

            jobresponse = httpClient.getresponse().read()
            j = json.loads(jobresponse)['job']
            jobsummary = (j['id'],j['name'],j['user'],j['queue'],j['state'],datetime.fromtimestamp(int(j['startTime'])/1000).strftime('%Y-%m-%d %H:%M:%S'),datetime.fromtimestamp(int(j['finishTime'])/1000).strftime('%Y-%m-%d %H:%M:%S'), j['mapsTotal'],j['mapsCompleted'],j['reducesTotal'],j['reducesCompleted'],j['uberized'],j['diagnostics'],j['avgMapTime'],j['avgReduceTime'],j['avgShuffleTime'],j['avgMergeTime'],j['successfulMapAttempts'],j['failedMapAttempts'],j['killedMapAttempts'],j['successfulReduceAttempts'],j['failedReduceAttempts'],j['killedReduceAttempts'])
            jobsummaries.append(jobsummary)

            mapreduce_hs_jobcounters_url = '/ws/v1/history/mapreduce/jobs/'+jobid+'/counters'
            httpClient = httplib.HTTPConnection(mapreduce_hs_host, 19888, timeout=30)
            httpClient.request('GET', mapreduce_hs_jobcounters_url)
            jobcountersresponse = httpClient.getresponse().read()
            jc = json.loads(jobcountersresponse)['jobCounters']
            counters = {}
            if jc.get('counterGroup') != None:
                
                for fileSystemCounter in jc['counterGroup'][0]['counter']:
                    counters[fileSystemCounter['name']] = fileSystemCounter['totalCounterValue']
                
                for taskCounter in jc['counterGroup'][1]['counter']:
                    counters[taskCounter['name']] = taskCounter['totalCounterValue']

                jobcounter = (jobid,counters.get('HDFS_BYTES_READ',0),counters.get('HDFS_BYTES_WRITTEN',0),counters.get('MAP_INPUT_RECORDS',0),counters.get('MAP_OUTPUT_RECORDS', 0),counters.get('REDUCE_INPUT_RECORDS',0), counters.get('REDUCE_OUTPUT_RECORDS',0), counters.get('MAP_OUTPUT_BYTES',0), counters.get('REDUCE_SHUFFLE_BYTES',0),counters.get('CPU_MILLISECONDS',0), counters.get('GC_TIME_MILLIS',0))
                jobcounters.append(jobcounter)

            mapreduce_hs_jobconf_url = '/ws/v1/history/mapreduce/jobs/'+jobid+'/conf'
            httpClient = httplib.HTTPConnection(mapreduce_hs_host, 19888, timeout=30)
            httpClient.request('GET', mapreduce_hs_jobconf_url)
            jobconfresponse = httpClient.getresponse().read()
            jf = json.loads(jobconfresponse)['conf']
            conf = {}
            conf['JOBID'] = j['id']
            if jf.get('property') != None:
                for p in jf.get('property'):
                    if p['name'] == 'mapreduce.job.user.name':
                        conf['USERNAME'] = p['value']
                    elif p['name'] == 'mapreduce.job.queuename':
                        conf['QUEUE'] = p['value']
                    elif p['name'] == 'mapreduce.job.submithostname':
                        conf['SUBMIT_HOST'] = p['value']
                    elif p['name'] == 'mapreduce.input.fileinputformat.inputdir':
                        conf['INPUTDIR'] = p['value']
                    elif p['name'] == 'mapreduce.job.inputformat.class':
                        conf['INPUTFORMAT'] = p['value']
                    elif p['name'] == 'mapreduce.output.fileoutputformat.outputdir':
                        conf['OUTDIR'] = p['value']
                    elif p['name'] == 'mapreduce.job.outputformat.class':
                        conf['OUTPUTFORMAT'] = p['value']
                    elif p['name'] == 'mapreduce.map.memory.mb':
                        conf['MAP_MEMORY'] = p['value']
                    elif p['name'] == 'mapreduce.reduce.memory.mb':
                        conf['REDUCE_MEMORY'] = p['value']
                    elif p['name'] == 'mapreduce.map.java.opts':
                        conf['MAP_JAVA_OPTS'] = p['value']
                    elif p['name'] == 'mapreduce.reduce.java.opts':
                        conf['REDUCE_JAVA_OPTS'] = p['value']
                    elif p['name'] == 'mapreduce.job.map.class':
                        conf['MAP_CLASS'] = p['value']
                    elif p['name'] == 'mapreduce.job.reduce.class':
                        conf['REDUCE_CLASS'] = p['value']
                    elif p['name'] == 'mapreduce.output.fileoutputformat.compress':
                        conf['OUT_COMPRESS'] = 1 if (p['value'] == 'true')  else 0
                    elif p['name'] == 'mapreduce.output.fileoutputformat.compress.codec':
                        conf['OUT_COMPRESS_CODEC'] = p['value']

                jobconf = (conf['JOBID'], conf['USERNAME'], conf['QUEUE'], conf['SUBMIT_HOST'],
                          			conf.get('INPUTDIR',''), conf.get('INPUTFORMAT',''),
                          			conf.get('OUTPUTDIR','') , conf.get('OUTPUTFORMAT',''),
                          			conf['MAP_MEMORY'], conf['REDUCE_MEMORY'],
                          			conf['MAP_JAVA_OPTS'], conf['REDUCE_JAVA_OPTS'],
                          			conf.get('MAP_CLASS',''), conf.get('REDUCE_CLASS',''),
                          			conf['OUT_COMPRESS'], conf['OUT_COMPRESS_CODEC']
                          			)

                jobconfs.append(jobconf)

                
        except Exception, e:
            traceback.print_exc()
        finally:
            if httpClient:
              httpClient.close()

    return (jobsummaries,jobcounters,jobconfs)

def save2DB(jobsummaries, jobcounters,jobconfs):
    try:
        conn=MySQLdb.connect(host='st1dg',user='hadoop',passwd='RNymee2527#',port=3306)
        cur=conn.cursor()
        cur.executemany('REPLACE INTO hadoop.mrv2_job_summary (id,name,user,queue,state,startTime,finishTime,mapsTotal,mapsCompleted,reducesTotal,reducesCompleted,uberized,diagnostics,avgMapTime,avgReduceTime,avgShuffleTime,avgMergeTime,successfulMapAttempts,failedMapAttempts,killedMapAttempts,successfulReduceAttempts,failedReduceAttempts,killedReduceAttempts) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)',jobsummaries)
        cur.executemany('REPLACE INTO hadoop.mrv2_job_counters (id,HDFS_BYTES_READ,HDFS_BYTES_WRITTEN,MAP_INPUT_RECORDS,MAP_OUTPUT_RECORDS,REDUCE_INPUT_RECORDS,REDUCE_OUTPUT_RECORDS,MAP_OUTPUT_BYTES,REDUCE_SHUFFLE_BYTES,CPU_MILLISECONDS,GC_TIME_MILLIS) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)', jobcounters)
        cur.executemany('REPLACE INTO hadoop.mrv2_job_conf (JOBID,USERNAME,QUEUE,SUBMIT_HOST,INPUTDIR,INPUTFORMAT,OUTPUTDIR,OUTPUTFORMAT,MAP_MEMORY,REDUCE_MEMORY,MAP_JAVA_OPTS,REDUCE_JAVA_OPTS,MAP_CLASS,REDUCE_CLASS,OUT_COMPRESS,OUT_COMPRESS_CODEC) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)', jobconfs)
        conn.commit()
        cur.close()
        conn.close()
 
    except MySQLdb.Error,e:
        print >> sys.stderr, "Mysql Error %d: %s" % (e.args[0], e.args[1])

def main(starttimestamp_in_ms, endtimestamp_in_ms):

    httpClient = None
    try:
        mapreduce_hs_host = 'jt2dg.prod.mediav.com'
        mapreduce_hs_jobs_url = '/ws/v1/history/mapreduce/jobs?startedTimeBegin='+starttimestamp_in_ms+'&startedTimeEnd='+endtimestamp_in_ms
        httpClient = httplib.HTTPConnection(mapreduce_hs_host, 19888, timeout=30)
        httpClient.request('GET', mapreduce_hs_jobs_url)

        response = httpClient.getresponse().read()
        jobs = json.loads(response)['jobs']

        if jobs:
            jobids = [job['id'] for job in jobs['job']]
            print 'JOBS between ' + starttimestamp_in_ms + ' and ' + endtimestamp_in_ms + ' :'
            print jobids
            (jobsummaries, jobcounters, jobconfs) = prepareJob(jobids)
            save2DB(jobsummaries, jobcounters, jobconfs)

    except Exception, e:
        traceback.print_exc()
    finally:
        if httpClient:
            httpClient.close()

if __name__ == "__main__":

    if(len(sys.argv)!=3):
        usage()
    else:
        main(sys.argv[1],sys.argv[2])
