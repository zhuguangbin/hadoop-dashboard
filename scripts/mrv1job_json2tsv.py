__author__ = 'guangbin'


import sys,json
import traceback
import re
from datetime import datetime

def usage():
    print >> sys.stderr, "Usage: \n "+sys.argv[0]+ " <historyfile>"



def print_job_summary(jobinfo, file):

    f=open(file,'a')

    job_summary = [jobinfo['values'].get('JOBID'),jobinfo['values'].get('JOBNAME'),jobinfo['values'].get('USER'),
                   jobinfo['values'].get('JOB_QUEUE'),jobinfo['values'].get('JOB_PRIORITY'),jobinfo['values'].get('JOB_STATUS'),
                   datetime.fromtimestamp(int(jobinfo['values'].get('SUBMIT_TIME') if jobinfo['values'].get('SUBMIT_TIME')!=None else 0)/1000).strftime('%Y-%m-%d %H:%M:%S'),datetime.fromtimestamp(int(jobinfo['values'].get('LAUNCH_TIME') if jobinfo['values'].get('LAUNCH_TIME')!=None else 0)/1000).strftime('%Y-%m-%d %H:%M:%S'),datetime.fromtimestamp(int(jobinfo['values'].get('FINISH_TIME') if jobinfo['values'].get('FINISH_TIME')!=None else 0)/1000).strftime('%Y-%m-%d %H:%M:%S'),
                   jobinfo['values'].get('TOTAL_MAPS') if jobinfo['values'].get('TOTAL_MAPS')!=None else '0',jobinfo['values'].get('FINISHED_MAPS') if jobinfo['values'].get('FINISHED_MAPS')!=None else '0' ,jobinfo['values'].get('FAILED_MAPS') if jobinfo['values'].get('FAILED_MAPS')!=None else '0',
                   jobinfo['values'].get('TOTAL_REDUCES') if jobinfo['values'].get('TOTAL_REDUCES')!=None else '0',jobinfo['values'].get('FINISHED_REDUCES') if jobinfo['values'].get('FINISHED_REDUCES')!=None else '0' ,jobinfo['values'].get('FAILED_REDUCES') if jobinfo['values'].get('FAILED_REDUCES')!=None else '0'
                   ]
    job_summary_str = "\t".join(job_summary)

    print >> f, job_summary_str
    f.close()

def print_job_counters(jobinfo, file):

    f=open(file,'a')

    job_counters_raw = jobinfo['values'].get('COUNTERS')

    if job_counters_raw!=None:

        p=re.compile(r'\(\w+\)\([\\\(\)\w\s:-]+\)\(\d+\)')
        found=p.findall(job_counters_raw)

        counters = {}
        for i in found:
            key=i.split('(')[1][:-1]
            value=i.split('(')[-1][:-1]
            counters[key]=value
        
        JOBID=jobinfo['values'].get('JOBID')
        HDFS_BYTES_READ=(counters.get('HDFS_BYTES_READ')) if counters.get('HDFS_BYTES_READ')!=None else '0'
        HDFS_BYTES_WRITTEN=(counters.get('HDFS_BYTES_WRITTEN')) if counters.get('HDFS_BYTES_WRITTEN')!=None else '0'
        FILE_BYTES_READ=(counters.get('FILE_BYTES_READ')) if counters.get('FILE_BYTES_READ')!=None else '0'
        FILE_BYTES_WRITTEN=(counters.get('FILE_BYTES_WRITTEN')) if counters.get('FILE_BYTES_WRITTEN')!=None else '0'
        TOTAL_LAUNCHED_MAPS=(counters.get('TOTAL_LAUNCHED_MAPS')) if counters.get('TOTAL_LAUNCHED_MAPS')!=None else '0'
        DATA_LOCAL_MAPS=(counters.get('DATA_LOCAL_MAPS')) if counters.get('DATA_LOCAL_MAPS')!=None else '0'
        RACK_LOCAL_MAPS=(counters.get('RACK_LOCAL_MAPS')) if counters.get('RACK_LOCAL_MAPS')!=None else '0'
        TOTAL_LAUNCHED_REDUCES=(counters.get('TOTAL_LAUNCHED_REDUCES')) if counters.get('TOTAL_LAUNCHED_REDUCES')!=None else '0'
        MAP_INPUT_RECORDS=(counters.get('MAP_INPUT_RECORDS')) if counters.get('MAP_INPUT_RECORDS')!=None else '0'
        MAP_OUTPUT_RECORDS=(counters.get('MAP_OUTPUT_RECORDS')) if counters.get('MAP_OUTPUT_RECORDS')!=None else '0'
        MAP_OUTPUT_BYTES=(counters.get('MAP_OUTPUT_BYTES')) if counters.get('MAP_OUTPUT_BYTES')!=None else '0'
        COMBINE_INPUT_RECORDS=(counters.get('COMBINE_INPUT_RECORDS')) if counters.get('COMBINE_INPUT_RECORDS')!=None else '0'
        COMBINE_OUTPUT_RECORDS=(counters.get('COMBINE_OUTPUT_RECORDS')) if counters.get('COMBINE_OUTPUT_RECORDS')!=None else '0'
        SPILLED_RECORDS=(counters.get('SPILLED_RECORDS')) if counters.get('SPILLED_RECORDS')!=None else '0'
        REDUCE_SHUFFLE_BYTES=(counters.get('REDUCE_SHUFFLE_BYTES')) if counters.get('REDUCE_SHUFFLE_BYTES')!=None else '0'
        REDUCE_INPUT_GROUPS=(counters.get('REDUCE_INPUT_GROUPS')) if counters.get('REDUCE_INPUT_GROUPS')!=None else '0'
        REDUCE_INPUT_RECORDS=(counters.get('REDUCE_INPUT_RECORDS')) if counters.get('REDUCE_INPUT_RECORDS')!=None else '0'
        REDUCE_OUTPUT_RECORDS=(counters.get('REDUCE_OUTPUT_RECORDS')) if counters.get('REDUCE_OUTPUT_RECORDS')!=None else '0'
        CPU_MILLISECONDS=(counters.get('CPU_MILLISECONDS')) if counters.get('CPU_MILLISECONDS')!=None else '0'

        job_counters = [JOBID, HDFS_BYTES_READ, HDFS_BYTES_WRITTEN, FILE_BYTES_READ, FILE_BYTES_WRITTEN, TOTAL_LAUNCHED_MAPS, DATA_LOCAL_MAPS, RACK_LOCAL_MAPS, TOTAL_LAUNCHED_REDUCES, MAP_INPUT_RECORDS, MAP_OUTPUT_RECORDS, MAP_OUTPUT_BYTES,
                        COMBINE_INPUT_RECORDS, COMBINE_OUTPUT_RECORDS, SPILLED_RECORDS, REDUCE_SHUFFLE_BYTES, REDUCE_INPUT_GROUPS, REDUCE_INPUT_RECORDS, REDUCE_OUTPUT_RECORDS, CPU_MILLISECONDS
                        ]

        job_counters_str = "\t".join(job_counters)

        print >> f, job_counters_str
    f.close()

def print_task_info(jobinfo, file):

    f=open(file,'a')

    task_info_dict = jobinfo['allTasks']
    for k,v in task_info_dict.items():
        task_info = []
        task_info.append(k)
        l = k.split('_')
        JOBID='job_'+l[1]+'_'+l[2]
        task_info.append(JOBID)
        task_info.append(v['values'].get('TASK_TYPE'))
        task_info.append(datetime.fromtimestamp(int(v['values'].get('START_TIME'))/1000).strftime('%Y-%m-%d %H:%M:%S'))
        FINISH_TIME = int(v['values'].get('FINISH_TIME')) if v['values'].get('FINISH_TIME')!=None else 0
        task_info.append(datetime.fromtimestamp(FINISH_TIME/1000).strftime('%Y-%m-%d %H:%M:%S'))
        task_info.append(str(v['values'].get('TASK_STATUS')))
        task_info.append(v['values'].get('SPLITS'))
        task_info.append(str(v['values'].get('ERROR')).replace('\t','').replace('\n',''))

        task_info_str = "\t".join(task_info)
        print >> f, task_info_str

    f.close()

def print_taskattempt_info(jobinfo, file):

    f=open(file,'a')

    for k,v in jobinfo['allTasks'].items():
        taskattempt_info = []
        for k,v in v['taskAttempts'].items():
            taskattempt_info.append(k)
            taskattempt_info.append(v['values'].get('TASKID'))
            taskattempt_info.append(v['values'].get('TASK_TYPE'))
            taskattempt_info.append(v['values'].get('HOSTNAME'))
            taskattempt_info.append(v['values'].get('TRACKER_NAME'))
            taskattempt_info.append(v['values'].get('START_TIME'))
            taskattempt_info.append(v['values'].get('FINISH_TIME'))
            taskattempt_info.append(v['values'].get('TASK_STATUS'))
            taskattempt_info.append(str(v['values'].get('ERROR')).replace('\t','').replace('\n',''))

            taskattempt_info_str = "\t".join(taskattempt_info)

            print >> f, taskattempt_info_str

    f.close()


def main(historyfile):

    with open(historyfile) as f:
        err = 0
        jobsummary_file = historyfile+".jobsummary"
        print >> sys.stderr, "outputting job summary to file : " + jobsummary_file

        jobcounters_file = historyfile+".jobcounters"
        print >> sys.stderr, "outputting job counters to file : " + jobcounters_file

        taskinfo_file = historyfile+".taskinfo"
        print >> sys.stderr, "outputting task info to file : " + taskinfo_file

        taskattemptinfo_file = historyfile+".taskattemptinfo"
        print >> sys.stderr, "outputting task attempt to file : " + taskattemptinfo_file

        for i,line in enumerate(f, 1):
            try:
                jobinfo = json.loads(line)

                print_job_summary(jobinfo,jobsummary_file)
                print_job_counters(jobinfo,jobcounters_file)
                #print_task_info(jobinfo, taskinfo_file)
                #print_taskattempt_info(jobinfo,taskattemptinfo_file)

            except Exception,e:
                err+=1
                print >> sys.stderr, " error json line : " + str(i) + ", total err count : " + str(err) + ", " + str(e)
                traceback.print_exc()

if __name__ == "__main__":

    if(len(sys.argv)!=2):
        usage()
    else:
        main(sys.argv[1])
