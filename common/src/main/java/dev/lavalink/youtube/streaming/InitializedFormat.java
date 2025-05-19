package dev.lavalink.youtube.streaming;
import dev.lavalink.youtube.protos.BufferedRangeOuterClass.BufferedRange;
import java.util.List;
import dev.lavalink.youtube.protos.Common.FormatId;

public class InitializedFormat {
    private FormatId formatId;
    private String formatKey;
    private Long durationMs; 
    private String mimeType; 
    private Long sequenceCount; 
    private List<Sequence> sequenceList;
    private List<byte[]> mediaChunks; 
    private BufferedRange state;

    public InitializedFormat() {}

    public InitializedFormat(FormatId formatId, String formatKey, Long durationMs, String mimeType, Long sequenceCount,
                             List<Sequence> sequenceList, List<byte[]> mediaChunks, BufferedRange state) {
        this.formatId = formatId;
        this.formatKey = formatKey;
        this.durationMs = durationMs;
        this.mimeType = mimeType;
        this.sequenceCount = sequenceCount;
        this.sequenceList = sequenceList;
        this.mediaChunks = mediaChunks;
        this.state = state;
    }

    public FormatId getFormatId() {
        return formatId;
    }

    public void setFormatId(FormatId formatId) {
        this.formatId = formatId;
    }

    public String getFormatKey() {
        return formatKey;
    }

    public void setFormatKey(String formatKey) {
        this.formatKey = formatKey;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSequenceCount() {
        return sequenceCount;
    }

    public void setSequenceCount(Long sequenceCount) {
        this.sequenceCount = sequenceCount;
    }

    public List<Sequence> getSequenceList() {
        return sequenceList;
    }

    public void setSequenceList(List<Sequence> sequenceList) {
        this.sequenceList = sequenceList;
    }

    public List<byte[]> getMediaChunks() {
        return mediaChunks;
    }

    public void setMediaChunks(List<byte[]> mediaChunks) {
        this.mediaChunks = mediaChunks;
    }

    public BufferedRange getState() {
        return state;
    }

    public void setState(BufferedRange state) {
        this.state = state;
    }
}
