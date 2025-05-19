package dev.lavalink.youtube.streaming;

import dev.lavalink.youtube.protos.Common.FormatId;
import dev.lavalink.youtube.protos.TimeRangeOuterClass.TimeRange;

public class Sequence {
    private Integer itag;
    private FormatId formatId;
    private Boolean isInitSegment;
    private Long durationMs;
    private Long startMs;
    private Long startDataRange;
    private Long sequenceNumber;
    private Long contentLength;
    private TimeRange timeRange;

    public Sequence() {
    }

    public Sequence(Integer itag, FormatId formatId, Boolean isInitSegment, Long durationMs, Long startMs,
            Long startDataRange, Long sequenceNumber, Long contentLength, TimeRange timeRange) {
        this.itag = itag;
        this.formatId = formatId;
        this.isInitSegment = isInitSegment;
        this.durationMs = durationMs;
        this.startMs = startMs;
        this.startDataRange = startDataRange;
        this.sequenceNumber = sequenceNumber;
        this.contentLength = contentLength;
        this.timeRange = timeRange;
    }

    public Integer getItag() {
        return itag;
    }

    public void setItag(Integer itag) {
        this.itag = itag;
    }

    public FormatId getFormatId() {
        return formatId;
    }

    public void setFormatId(FormatId formatId) {
        this.formatId = formatId;
    }

    public Boolean getIsInitSegment() {
        return isInitSegment;
    }

    public void setIsInitSegment(Boolean isInitSegment) {
        this.isInitSegment = isInitSegment;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Long getStartMs() {
        return startMs;
    }

    public void setStartMs(Long startMs) {
        this.startMs = startMs;
    }

    public Long getStartDataRange() {
        return startDataRange;
    }

    public void setStartDataRange(Long startDataRange) {
        this.startDataRange = startDataRange;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }
}
