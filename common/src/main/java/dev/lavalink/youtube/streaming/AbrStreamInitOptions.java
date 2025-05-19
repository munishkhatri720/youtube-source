package dev.lavalink.youtube.streaming;
import java.util.List;
import dev.lavalink.youtube.protos.ClientAbrStateOuterClass.ClientAbrState;

public class AbrStreamInitOptions {
    private List<Format> audioFormats;
    private List<Format> videoFormats;
    private ClientAbrState clientAbrState;

    public AbrStreamInitOptions(List<Format> audioFormats, List<Format> videoFormats, ClientAbrState clientAbrState) {
        this.audioFormats = audioFormats;
        this.videoFormats = videoFormats;
        this.clientAbrState = clientAbrState;
    }

    public List<Format> getAudioFormats() {
        return audioFormats;
    }

    public void setAudioFormats(List<Format> audioFormats) {
        this.audioFormats = audioFormats;
    }

    public List<Format> getVideoFormats() {
        return videoFormats;
    }

    public void setVideoFormats(List<Format> videoFormats) {
        this.videoFormats = videoFormats;
    }

    public ClientAbrState getClientAbrState() {
        return clientAbrState;
    }

    public void setClientAbrState(ClientAbrState clientAbrState) {
        this.clientAbrState = clientAbrState;
    }
}
