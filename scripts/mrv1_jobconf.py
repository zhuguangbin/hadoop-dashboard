#! /usr/bin/python2.7

__author__ = 'guangbin'

import sys,json
import traceback
import re
from datetime import datetime
import MySQLdb
import xml.etree.cElementTree as ET

def usage():
    print >> sys.stderr, "Usage: \n "+sys.argv[0]+ " <jobconffile>"


def parse(jobconffile):

	# (JOBID,USERNAME,QUEUE,SUBMIT_HOST,
	#  INPUTDIR,INPUTFORMAT,
	#  OUTPUTDIR,OUTPUTFORMAT,
	#  CHILD_JAVA_OPTS,CHILD_ULIMIT,
	#  MAP_CLASS,REDUCE_CLASS,
	#  OUT_COMPRESS,OUT_COMPRESS_CODEC
	# )
	JOBID = jobconffile[jobconffile.index('job'):-9]
	jobconf = {}
	jobconf['JOBID'] = JOBID
	
	tree = ET.ElementTree(file=jobconffile)
	for elem in tree.iterfind('property'):
		if elem[0].text == 'user.name': 
			jobconf['USERNAME'] = elem[1].text 
		elif elem[0].text == 'mapred.job.queue.name':
			jobconf['QUEUE'] = elem[1].text
		elif elem[0].text == 'mapreduce.job.submithost':
			jobconf['SUBMIT_HOST'] = elem[1].text
		elif elem[0].text == 'mapred.input.dir':
			jobconf['INPUTDIR'] = elem[1].text
		elif elem[0].text == 'mapreduce.inputformat.class':
			jobconf['INPUTFORMAT'] = elem[1].text
		elif elem[0].text == 'mapred.output.dir':
			jobconf['OUTPUTDIR'] = elem[1].text
		elif elem[0].text == 'mapreduce.outputformat.class':
			jobconf['OUTPUTFORMAT'] = elem[1].text
		elif elem[0].text == 'mapred.child.java.opts':
			jobconf['CHILD_JAVA_OPTS'] = elem[1].text
		elif elem[0].text == 'mapred.child.ulimit':
			jobconf['CHILD_ULIMIT'] = elem[1].text
		elif elem[0].text == 'mapreduce.map.class':
			jobconf['MAP_CLASS'] = elem[1].text
		elif elem[0].text == 'mapreduce.reduce.class':
			jobconf['REDUCE_CLASS'] = elem[1].text
		elif elem[0].text == 'mapred.output.compress':
			jobconf['OUT_COMPRESS'] =  1 if (elem[1].text == 'true')  else 0
		elif elem[0].text == 'mapred.output.compression.codec':
			jobconf['OUT_COMPRESS_CODEC'] = elem[1].text
		
	return (jobconf['JOBID'], jobconf['USERNAME'], jobconf['QUEUE'], jobconf['SUBMIT_HOST'],
			jobconf.get('INPUTDIR',''), jobconf.get('INPUTFORMAT',''), 
			jobconf.get('OUTPUTDIR','') , jobconf.get('OUTPUTFORMAT',''), 
			jobconf['CHILD_JAVA_OPTS'], jobconf['CHILD_ULIMIT'],
			jobconf.get('MAP_CLASS',''), jobconf.get('REDUCE_CLASS',''),
			jobconf['OUT_COMPRESS'], jobconf['OUT_COMPRESS_CODEC']
			)

def save2DB(jobconf):

    try:
        conn=MySQLdb.connect(host='st1dg',user='hadoop',passwd='RNymee2527#',port=3306)
        cur=conn.cursor()
        cur.execute('REPLACE INTO hadoop.mrv1_job_conf (JOBID,USERNAME,QUEUE,SUBMIT_HOST,INPUTDIR,INPUTFORMAT,OUTPUTDIR,OUTPUTFORMAT,CHILD_JAVA_OPTS,CHILD_ULIMIT,MAP_CLASS,REDUCE_CLASS,OUT_COMPRESS,OUT_COMPRESS_CODEC) VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)',jobconf)
        conn.commit()
        cur.close()
        conn.close()
		
    except MySQLdb.Error,e:
        print >> sys.stderr, "Mysql Error %d: %s" % (e.args[0], e.args[1])

		
def main(jobconffile):

	print 'parsing jobconf file : ' + jobconffile
	jobconf = parse(jobconffile)
	save2DB(jobconf)
	
		
if __name__ == "__main__":

    if(len(sys.argv)!=2):
        usage()
    else:
        main(sys.argv[1])
