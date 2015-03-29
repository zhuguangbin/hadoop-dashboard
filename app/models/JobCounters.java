package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "job_counters")
public class JobCounters extends Model
{
	@Id
    @Column(name = "JOBID")
    private String jobid;
    @Column(name = "HDFS_BYTES_READ")
    private Long hdfsBytesRead;
    @Column(name = "HDFS_BYTES_WRITTEN")
    private Long hdfsBytesWritten;
    @Column(name = "FILE_BYTES_READ")
    private Long fileBytesRead;
    @Column(name = "FILE_BYTES_WRITTEN")
    private Long fileBytesWritten;
    @Column(name = "TOTAL_LAUNCHED_MAPS")
    private Integer totalLaunchedMaps;
    @Column(name = "DATA_LOCAL_MAPS")
    private Integer dataLocalMaps;
    @Column(name = "RACK_LOCAL_MAPS")
    private Integer rackLocalMaps;
    @Column(name = "TOTAL_LAUNCHED_REDUCES")
    private Integer totalLaunchedReduces;
    @Column(name = "MAP_INPUT_RECORDS")
    private Long mapInputRecords;
    @Column(name = "MAP_OUTPUT_RECORDS")
    private Long mapOutputRecords;
    @Column(name = "MAP_OUTPUT_BYTES")
    private Long mapOutputBytes;
    @Column(name = "COMBINE_INPUT_RECORDS")
    private Long combineInputRecords;
    @Column(name = "COMBINE_OUTPUT_RECORDS")
    private Long combineOutputRecords;
    @Column(name = "SPILLED_RECORDS")
    private Long spilledRecords;
    @Column(name = "REDUCE_SHUFFLE_BYTES")
    private Long reduceShuffleBytes;
    @Column(name = "REDUCE_INPUT_GROUPS")
    private Long reduceInputGroups;
    @Column(name = "REDUCE_INPUT_RECORDS")
    private Long reduceInputRecords;
    @Column(name = "REDUCE_OUTPUT_RECORDS")
    private Long reduceOutputRecords;
    @Column(name = "CPU_MILLISECONDS")
    private Long cpuMilliseconds;

    public static Finder<String, JobCounters> find = new Finder<String, JobCounters>(String.class, JobCounters.class);


    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public Long getHdfsBytesRead() {
        return hdfsBytesRead;
    }

    public void setHdfsBytesRead(Long hdfsBytesRead) {
        this.hdfsBytesRead = hdfsBytesRead;
    }

    public Long getHdfsBytesWritten() {
        return hdfsBytesWritten;
    }

    public void setHdfsBytesWritten(Long hdfsBytesWritten) {
        this.hdfsBytesWritten = hdfsBytesWritten;
    }

    public Long getFileBytesRead() {
        return fileBytesRead;
    }

    public void setFileBytesRead(Long fileBytesRead) {
        this.fileBytesRead = fileBytesRead;
    }

    public Long getFileBytesWritten() {
        return fileBytesWritten;
    }

    public void setFileBytesWritten(Long fileBytesWritten) {
        this.fileBytesWritten = fileBytesWritten;
    }

    public Integer getTotalLaunchedMaps() {
        return totalLaunchedMaps;
    }

    public void setTotalLaunchedMaps(Integer totalLaunchedMaps) {
        this.totalLaunchedMaps = totalLaunchedMaps;
    }

    public Integer getDataLocalMaps() {
        return dataLocalMaps;
    }

    public void setDataLocalMaps(Integer dataLocalMaps) {
        this.dataLocalMaps = dataLocalMaps;
    }

    public Integer getRackLocalMaps() {
        return rackLocalMaps;
    }

    public void setRackLocalMaps(Integer rackLocalMaps) {
        this.rackLocalMaps = rackLocalMaps;
    }

    public Integer getTotalLaunchedReduces() {
        return totalLaunchedReduces;
    }

    public void setTotalLaunchedReduces(Integer totalLaunchedReduces) {
        this.totalLaunchedReduces = totalLaunchedReduces;
    }

    public Long getMapInputRecords() {
        return mapInputRecords;
    }

    public void setMapInputRecords(Long mapInputRecords) {
        this.mapInputRecords = mapInputRecords;
    }

    public Long getMapOutputRecords() {
        return mapOutputRecords;
    }

    public void setMapOutputRecords(Long mapOutputRecords) {
        this.mapOutputRecords = mapOutputRecords;
    }

    public Long getMapOutputBytes() {
        return mapOutputBytes;
    }

    public void setMapOutputBytes(Long mapOutputBytes) {
        this.mapOutputBytes = mapOutputBytes;
    }

    public Long getCombineInputRecords() {
        return combineInputRecords;
    }

    public void setCombineInputRecords(Long combineInputRecords) {
        this.combineInputRecords = combineInputRecords;
    }

    public Long getCombineOutputRecords() {
        return combineOutputRecords;
    }

    public void setCombineOutputRecords(Long combineOutputRecords) {
        this.combineOutputRecords = combineOutputRecords;
    }

    public Long getSpilledRecords() {
        return spilledRecords;
    }

    public void setSpilledRecords(Long spilledRecords) {
        this.spilledRecords = spilledRecords;
    }

    public Long getReduceShuffleBytes() {
        return reduceShuffleBytes;
    }

    public void setReduceShuffleBytes(Long reduceShuffleBytes) {
        this.reduceShuffleBytes = reduceShuffleBytes;
    }

    public Long getReduceInputGroups() {
        return reduceInputGroups;
    }

    public void setReduceInputGroups(Long reduceInputGroups) {
        this.reduceInputGroups = reduceInputGroups;
    }

    public Long getReduceInputRecords() {
        return reduceInputRecords;
    }

    public void setReduceInputRecords(Long reduceInputRecords) {
        this.reduceInputRecords = reduceInputRecords;
    }

    public Long getReduceOutputRecords() {
        return reduceOutputRecords;
    }

    public void setReduceOutputRecords(Long reduceOutputRecords) {
        this.reduceOutputRecords = reduceOutputRecords;
    }

    public Long getCpuMilliseconds() {
        return cpuMilliseconds;
    }

    public void setCpuMilliseconds(Long cpuMilliseconds) {
        this.cpuMilliseconds = cpuMilliseconds;
    }
}