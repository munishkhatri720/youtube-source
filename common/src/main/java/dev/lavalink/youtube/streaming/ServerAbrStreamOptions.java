package dev.lavalink.youtube.streaming;

import org.jetbrains.annotations.NotNull;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;

public class ServerAbrStreamOptions {
    private HttpInterface httpInterface;
    private String serverAbrStreamingUrl;
    private String videoPlaybackUstreamerConfig;
    private String poToken;
    private long durationMs;

    public ServerAbrStreamOptions(@NotNull HttpInterface httpInterface, @NotNull String serverAbrStreamingUrl,
            @NotNull String videoPlaybackUstreamerConfig, @NotNull long durationMs, String poToken) {
        this.httpInterface = httpInterface;
        this.serverAbrStreamingUrl = serverAbrStreamingUrl;
        this.videoPlaybackUstreamerConfig = videoPlaybackUstreamerConfig;
        this.durationMs = durationMs;
        this.poToken = poToken;

    }

    public HttpInterface getHttpInterface() {
        return this.httpInterface;
    }

    public void setHttpInterface(HttpInterface httpInterface) {
        this.httpInterface = httpInterface;
    }

    public String getServerAbrStreamingUrl() {
        return this.serverAbrStreamingUrl;
    }

    public void setServerAbrStreamingUrl(String serverAbrStreamingUrl) {
        this.serverAbrStreamingUrl = serverAbrStreamingUrl;
    }

    public String getVideoPlaybackUstreamerConfig() {
        return this.videoPlaybackUstreamerConfig;
    }

    public void setVideoPlaybackUstreamerConfig(String videoPlaybackUstreamerConfig) {
        this.videoPlaybackUstreamerConfig = videoPlaybackUstreamerConfig;
    }

    public String getPoToken() {
        return this.poToken;
    }

    public void setPoToken(String poToken) {
        this.poToken = poToken;
    }

    public long getDurationMs() {
        return this.durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

}
