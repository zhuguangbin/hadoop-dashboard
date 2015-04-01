-- MySQL dump 10.13  Distrib 5.1.71, for redhat-linux-gnu (x86_64)
--
-- Host: st1dg.prod.mediav.com    Database: hadoop
-- ------------------------------------------------------
-- Server version	5.1.73-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `mrv1_job_conf`
--

DROP TABLE IF EXISTS `mrv1_job_conf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mrv1_job_conf` (
  `JOBID` varchar(30) NOT NULL DEFAULT '',
  `USERNAME` varchar(20) NOT NULL,
  `QUEUE` varchar(20) NOT NULL,
  `SUBMIT_HOST` varchar(25) NOT NULL,
  `INPUTDIR` text NOT NULL,
  `INPUTFORMAT` varchar(255) DEFAULT NULL,
  `OUTPUTDIR` text NOT NULL,
  `OUTPUTFORMAT` varchar(255) DEFAULT NULL,
  `CHILD_JAVA_OPTS` longtext NOT NULL,
  `CHILD_ULIMIT` mediumtext NOT NULL,
  `MAP_CLASS` text,
  `REDUCE_CLASS` text,
  `OUT_COMPRESS` tinyint(1) DEFAULT NULL,
  `OUT_COMPRESS_CODEC` varchar(50) NOT NULL,
  PRIMARY KEY (`JOBID`),
  UNIQUE KEY `unique_JOBID` (`JOBID`),
  KEY `SUBMIT_HOST_index` (`SUBMIT_HOST`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mrv1_job_counters`
--

DROP TABLE IF EXISTS `mrv1_job_counters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mrv1_job_counters` (
  `JOBID` varchar(30) NOT NULL DEFAULT '',
  `HDFS_BYTES_READ` bigint(20) DEFAULT NULL,
  `HDFS_BYTES_WRITTEN` bigint(20) DEFAULT NULL,
  `FILE_BYTES_READ` bigint(20) DEFAULT NULL,
  `FILE_BYTES_WRITTEN` bigint(20) DEFAULT NULL,
  `TOTAL_LAUNCHED_MAPS` int(11) NOT NULL,
  `DATA_LOCAL_MAPS` int(11) NOT NULL,
  `RACK_LOCAL_MAPS` int(11) NOT NULL,
  `TOTAL_LAUNCHED_REDUCES` int(11) NOT NULL,
  `MAP_INPUT_RECORDS` bigint(20) DEFAULT NULL,
  `MAP_OUTPUT_RECORDS` bigint(20) DEFAULT NULL,
  `MAP_OUTPUT_BYTES` bigint(20) DEFAULT NULL,
  `COMBINE_INPUT_RECORDS` bigint(20) DEFAULT NULL,
  `COMBINE_OUTPUT_RECORDS` bigint(20) DEFAULT NULL,
  `SPILLED_RECORDS` bigint(20) DEFAULT NULL,
  `REDUCE_SHUFFLE_BYTES` bigint(20) DEFAULT NULL,
  `REDUCE_INPUT_GROUPS` bigint(20) DEFAULT NULL,
  `REDUCE_INPUT_RECORDS` bigint(20) DEFAULT NULL,
  `REDUCE_OUTPUT_RECORDS` bigint(20) DEFAULT NULL,
  `CPU_MILLISECONDS` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`JOBID`),
  UNIQUE KEY `unique_JOBID` (`JOBID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mrv1_job_summary`
--

DROP TABLE IF EXISTS `mrv1_job_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mrv1_job_summary` (
  `JOBID` varchar(30) NOT NULL DEFAULT '',
  `JOBNAME` varchar(255) NOT NULL,
  `USER` varchar(10) NOT NULL,
  `JOB_QUEUE` varchar(20) DEFAULT NULL,
  `JOB_PRIORITY` varchar(10) NOT NULL,
  `JOB_STATUS` varchar(10) NOT NULL,
  `SUBMIT_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LAUNCH_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `FINISH_TIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `TOTAL_MAPS` int(11) NOT NULL,
  `FINISHED_MAPS` int(11) NOT NULL,
  `FAILED_MAPS` int(11) NOT NULL,
  `TOTAL_REDUCES` int(11) NOT NULL,
  `FINISHED_REDUCES` int(11) NOT NULL,
  `FAILED_REDUCES` int(11) NOT NULL,
  PRIMARY KEY (`JOBID`),
  UNIQUE KEY `unique_JOBID` (`JOBID`),
  KEY `JOBNAME_index` (`JOBNAME`),
  KEY `FINISH_TIME_index` (`FINISH_TIME`),
  KEY `SUBMIT_TIME_index` (`SUBMIT_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mrv2_job_conf`
--

DROP TABLE IF EXISTS `mrv2_job_conf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mrv2_job_conf` (
  `JOBID` varchar(30) NOT NULL DEFAULT '',
  `USERNAME` varchar(20) NOT NULL,
  `QUEUE` varchar(20) NOT NULL,
  `SUBMIT_HOST` varchar(25) NOT NULL,
  `INPUTDIR` text NOT NULL,
  `INPUTFORMAT` varchar(255) DEFAULT NULL,
  `OUTPUTDIR` text NOT NULL,
  `OUTPUTFORMAT` varchar(255) DEFAULT NULL,
  `MAP_MEMORY` bigint(20) NOT NULL,
  `REDUCE_MEMORY` bigint(20) NOT NULL,
  `MAP_JAVA_OPTS` longtext,
  `REDUCE_JAVA_OPTS` longtext,
  `MAP_CLASS` text,
  `REDUCE_CLASS` text,
  `OUT_COMPRESS` tinyint(1) DEFAULT NULL,
  `OUT_COMPRESS_CODEC` varchar(50) NOT NULL,
  PRIMARY KEY (`JOBID`),
  UNIQUE KEY `unique_JOBID` (`JOBID`),
  KEY `SUBMIT_HOST_index` (`SUBMIT_HOST`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mrv2_job_counters`
--

DROP TABLE IF EXISTS `mrv2_job_counters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mrv2_job_counters` (
  `id` varchar(30) NOT NULL DEFAULT '',
  `HDFS_BYTES_READ` bigint(20) NOT NULL,
  `HDFS_BYTES_WRITTEN` bigint(20) NOT NULL,
  `MAP_INPUT_RECORDS` bigint(20) NOT NULL,
  `MAP_OUTPUT_RECORDS` bigint(20) NOT NULL,
  `REDUCE_INPUT_RECORDS` bigint(20) NOT NULL,
  `REDUCE_OUTPUT_RECORDS` bigint(20) NOT NULL,
  `MAP_OUTPUT_BYTES` bigint(20) NOT NULL,
  `REDUCE_SHUFFLE_BYTES` bigint(20) NOT NULL,
  `CPU_MILLISECONDS` bigint(20) NOT NULL,
  `GC_TIME_MILLIS` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mrv2_job_summary`
--

DROP TABLE IF EXISTS `mrv2_job_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mrv2_job_summary` (
  `id` varchar(30) NOT NULL DEFAULT '',
  `name` varchar(255) DEFAULT NULL,
  `user` varchar(20) DEFAULT NULL,
  `queue` varchar(20) DEFAULT NULL,
  `state` varchar(10) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `finishTime` datetime DEFAULT NULL,
  `mapsTotal` int(11) DEFAULT NULL,
  `mapsCompleted` int(11) DEFAULT NULL,
  `reducesTotal` int(11) DEFAULT NULL,
  `reducesCompleted` int(11) DEFAULT NULL,
  `uberized` tinyint(1) DEFAULT NULL,
  `diagnostics` text,
  `avgMapTime` bigint(20) DEFAULT NULL,
  `avgReduceTime` bigint(20) DEFAULT NULL,
  `avgShuffleTime` bigint(20) DEFAULT NULL,
  `avgMergeTime` bigint(20) DEFAULT NULL,
  `successfulMapAttempts` int(11) DEFAULT NULL,
  `failedMapAttempts` int(11) DEFAULT NULL,
  `killedMapAttempts` int(11) DEFAULT NULL,
  `successfulReduceAttempts` int(11) DEFAULT NULL,
  `failedReduceAttempts` int(11) DEFAULT NULL,
  `killedReduceAttempts` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_id` (`id`),
  KEY `name_index` (`name`),
  KEY `finishTime_index` (`finishTime`),
  KEY `startTime_index` (`startTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sparksql_history`
--

DROP TABLE IF EXISTS `sparksql_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sparksql_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user` varchar(50) DEFAULT NULL,
  `sqlstr` longtext,
  `start_time` datetime NOT NULL,
  `finish_time` datetime DEFAULT NULL,
  `retcode` int(11) DEFAULT '-1',
  `message` text,
  `result_file` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


