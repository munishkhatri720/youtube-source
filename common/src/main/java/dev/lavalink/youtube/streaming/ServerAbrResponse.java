package dev.lavalink.youtube.streaming;
import java.util.List;
import dev.lavalink.youtube.protos.SabrErrorOuterClass.SabrError;
import dev.lavalink.youtube.protos.SabrRedirectOuterClass.SabrRedirect;
import dev.lavalink.youtube.protos.StreamProtectionStatusOuterClass.StreamProtectionStatus;

public class ServerAbrResponse {
    private List<InitializedFormat> initializedFormats;
    private StreamProtectionStatus streamProtectionStatus; // nullable
    private SabrRedirect sabrRedirect;                     // nullable
    private SabrError sabrError;                           // nullable


    public ServerAbrResponse(List<InitializedFormat> initializedFormats,
                             StreamProtectionStatus streamProtectionStatus,
                             SabrRedirect sabrRedirect,
                             SabrError sabrError) {
        this.initializedFormats = initializedFormats;
        this.streamProtectionStatus = streamProtectionStatus;
        this.sabrRedirect = sabrRedirect;
        this.sabrError = sabrError;
    }

    public List<InitializedFormat> getInitializedFormats() {
        return initializedFormats;
    }

    public void setInitializedFormats(List<InitializedFormat> initializedFormats) {
        this.initializedFormats = initializedFormats;
    }

    public StreamProtectionStatus getStreamProtectionStatus() {
        return streamProtectionStatus;
    }

    public void setStreamProtectionStatus(StreamProtectionStatus streamProtectionStatus) {
        this.streamProtectionStatus = streamProtectionStatus;
    }

    public SabrRedirect getSabrRedirect() {
        return sabrRedirect;
    }

    public void setSabrRedirect(SabrRedirect sabrRedirect) {
        this.sabrRedirect = sabrRedirect;
    }

    public SabrError getSabrError() {
        return sabrError;
    }

    public void setSabrError(SabrError sabrError) {
        this.sabrError = sabrError;
    }
}
