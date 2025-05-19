package dev.lavalink.youtube.streaming;
import dev.lavalink.youtube.protos.ClientAbrStateOuterClass.ClientAbrState;
import dev.lavalink.youtube.protos.Common.FormatId;
import java.util.List;

public class AbrStreamMediaOptions {
    private ClientAbrState clientAbrState;
    private List<FormatId> audioFormatIds;
    private List<FormatId> videoFormatIds;
    
    public AbrStreamMediaOptions(ClientAbrState clientAbrState, List<FormatId> audioFormatIds, List<FormatId> videoFormatIds) {
        this.clientAbrState = clientAbrState;
        this.audioFormatIds = audioFormatIds;
        this.videoFormatIds = videoFormatIds;
    }

    public ClientAbrState getClientAbrState() {
        return clientAbrState;
    }

    public void setClientAbrState(ClientAbrState clientAbrState) {
        this.clientAbrState = clientAbrState;
    }

    public List<FormatId> getAudioFormatIds() {
        return audioFormatIds;
    }

    public void setAudioFormatIds(List<FormatId> audioFormatIds) {
        this.audioFormatIds = audioFormatIds;
    }

    public List<FormatId> getVideoFormatIds() {
        return videoFormatIds;
    }

    public void setVideoFormatIds(List<FormatId> videoFormatIds) {
        this.videoFormatIds = videoFormatIds;
    }
}