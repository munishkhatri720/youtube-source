package dev.lavalink.youtube.streaming;

import dev.lavalink.youtube.protos.Common.FormatId;
import dev.lavalink.youtube.protos.FormatInitializationMetadataOuterClass.FormatInitializationMetadata;
import dev.lavalink.youtube.protos.MediaHeaderOuterClass.MediaHeader;
import dev.lavalink.youtube.protos.NextRequestPolicyOuterClass.NextRequestPolicy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.jetbrains.annotations.NotNull;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import dev.lavalink.youtube.protos.BufferedRangeOuterClass.BufferedRange;
import dev.lavalink.youtube.protos.ClientAbrStateOuterClass.ClientAbrState;
import dev.lavalink.youtube.protos.PlaybackCookieOuterClass.PlaybackCookie;
import dev.lavalink.youtube.protos.SabrErrorOuterClass.SabrError;
import dev.lavalink.youtube.protos.SabrRedirectOuterClass.SabrRedirect;
import dev.lavalink.youtube.protos.StreamProtectionStatusOuterClass.StreamProtectionStatus;
import dev.lavalink.youtube.protos.StreamerContextOuterClass.StreamerContext;
import dev.lavalink.youtube.protos.StreamerContextOuterClass.StreamerContext.ClientInfo;
import dev.lavalink.youtube.protos.VideoPlaybackAbrRequestOuterClass.VideoPlaybackAbrRequest;

import java.util.stream.Collectors;

public class ServerAbrStream {
    public static final QualityEnum DEFAULT_QUALITY = QualityEnum.HD720;
    private HttpInterface httpInterface;
    private String serverAbrStreamingUrl;
    private String videoPlaybackUstreamerConfig;
    private String poToken;
    private PlaybackCookie playbackCookie;
    private long totalDurationMs;
    private List<InitializedFormat> initializedFormats = new ArrayList<>();
    private Map<String, InitializedFormat> formatsByKey = new HashMap<>();
    private Map<Integer, String> headerIdToFormatKeyMap = new HashMap<>();
    private Map<String, List<Integer>> previousSequences = new HashMap<>();

    public ServerAbrStream(@NotNull ServerAbrStreamOptions options) {
        this.httpInterface = options.getHttpInterface();
        this.serverAbrStreamingUrl = options.getServerAbrStreamingUrl();
        this.videoPlaybackUstreamerConfig = options.getVideoPlaybackUstreamerConfig();
        this.poToken = options.getPoToken();
        this.totalDurationMs = options.getDurationMs();

    }

    public void initialize(@NotNull AbrStreamInitOptions options) {
        List<Format> audioFormats = options.getAudioFormats();
        List<Format> videoFormats = options.getVideoFormats();
        ClientAbrState initialAbrState = options.getClientAbrState();

        Format firstVideoFormat = videoFormats.size() > 0 ? videoFormats.get(0) : null;

        int videoResolution = firstVideoFormat != null && firstVideoFormat.getWidth() != null
                ? firstVideoFormat.getWidth()
                : DEFAULT_QUALITY.getValue();

        ClientAbrState clientAbrState = ClientAbrState.newBuilder()
                .setLastManualDirection(0)
                .setTimeSinceLastManualFormatSelectionMs(0)
                .setVisibility(0)
                .setPlayerTimeMs(0)
                .setEnabledTrackTypesBitfield(0)
                .setLastManualSelectedResolution(videoResolution)
                .setStickyResolution(videoResolution).mergeFrom(initialAbrState)
                .build();

        List<FormatId> audioFormatIds = audioFormats.stream()
                .map(fmt -> FormatId.newBuilder().setItag(fmt.getItag())
                        .setLastModified(Long.parseLong(fmt.getLastModified())).setXtags(fmt.getXtags()).build())
                .collect(Collectors.toList());

        List<FormatId> videoFormatIds = videoFormats.stream()
                .map(fmt -> FormatId.newBuilder().setItag(fmt.getItag())
                        .setLastModified(Long.parseLong(fmt.getLastModified())).setXtags(fmt.getXtags()).build())
                .collect(Collectors.toList());

        try {
            while (clientAbrState.getPlayerTimeMs() < this.totalDurationMs) {
                AbrStreamMediaOptions streamMediaOptions = new AbrStreamMediaOptions(clientAbrState, audioFormatIds,
                        videoFormatIds);
                ServerAbrResponse data = this.fetchMedia(streamMediaOptions);
                this.onData(data);

                if (data.getSabrError() != null) {
                    break;
                }

                InitializedFormat mainInitializedFormat = clientAbrState.getEnabledTrackTypesBitfield() == 0
                        ? data.getInitializedFormats().stream()
                                .filter(fmt -> fmt.getMimeType() != null && fmt.getMimeType().contains("video"))
                                .findFirst().orElse(null)
                        : data.getInitializedFormats().get(0);
                for (InitializedFormat fmt : data.getInitializedFormats()) {
                    this.previousSequences.put(fmt.getFormatKey(), fmt.getSequenceList().stream()
                            .map(seq -> {
                                long l = seq.getSequenceNumber();
                                if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                                    throw new IllegalArgumentException("Sequence out of int range: " + l);
                                }
                                return (int) l;
                            })
                            .collect(Collectors.toList()));
                }

                if (mainInitializedFormat == null
                        || mainInitializedFormat.getSequenceCount() == mainInitializedFormat.getSequenceList()
                                .get(mainInitializedFormat.getSequenceList().size() - 1)
                                .getSequenceNumber()) {
                    this.onEnd(data);
                    break;
                }

                clientAbrState = clientAbrState.toBuilder()
                        .setPlayerTimeMs(clientAbrState.getPlayerTimeMs() + mainInitializedFormat.getSequenceList()
                                .stream().mapToLong(seq -> seq.getDurationMs() != null ? seq.getDurationMs() : 0L)
                                .sum())
                        .build();

            }
        } catch (IOException e) {
            this.onError(e);
        }

    }

    public void onEnd(ServerAbrResponse data) {

    }

    public void onData(ServerAbrResponse data) {

    }

    public void onError(IOException error) {

    }

    private ServerAbrResponse fetchMedia(@NotNull AbrStreamMediaOptions options) throws IOException {
        ClientAbrState abrState = options.getClientAbrState();
        List<FormatId> audioFormatIds = options.getAudioFormatIds();
        List<FormatId> videoFormatIds = options.getVideoFormatIds();

        ByteString playbackCookie = this.playbackCookie != null
                ? this.playbackCookie.toByteString()
                : null;

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .setClientName(1)
                .setClientVersion("2.2040620.05.00")
                .setOsName("Windows")
                .setOsVersion("10.0")
                .build();

        StreamerContext streamerContext = StreamerContext.newBuilder()
                .addAllField5(List.of())
                .addAllField6(List.of())
                .setClientInfo(clientInfo)
                .build();

        if (playbackCookie != null) {
            streamerContext = streamerContext.toBuilder().setPlaybackCookie(playbackCookie).build();
        }

        if (this.poToken != null) {
            byte[] poTokenBytes = this.poToken != null ? ByteUtils.base64ToU8(this.poToken) : null;
            streamerContext = streamerContext.toBuilder().setPoToken(ByteString.copyFrom(poTokenBytes)).build();
        }

        VideoPlaybackAbrRequest request = VideoPlaybackAbrRequest.newBuilder()
                .setClientAbrState(abrState)
                .addAllSelectedAudioFormatIds(audioFormatIds)
                .addAllSelectedVideoFormatIds(videoFormatIds)
                .addAllSelectedFormatIds(
                        this.initializedFormats.stream().map(fmt -> fmt.getFormatId()).collect(Collectors.toList()))
                .setVideoPlaybackUstreamerConfig(
                        ByteString.copyFrom(ByteUtils.base64ToU8(videoPlaybackUstreamerConfig)))
                .addAllBufferedRanges(
                        this.initializedFormats.stream().map(fmt -> fmt.getState()).collect(Collectors.toList()))
                .addAllField1000(List.of())

                .build();

        HttpPost httpRequest = new HttpPost(this.serverAbrStreamingUrl);
        httpRequest.setEntity(new ByteArrayEntity(request.toByteArray()));

        try (CloseableHttpResponse response = this.httpInterface.execute(httpRequest)) {
            int statusCode = response.getStatusLine().getStatusCode();
            byte[] responseBytes = response.getEntity().getContent().readAllBytes();

            if (statusCode != 200 || responseBytes.length == 0) {
                throw new RuntimeException("Received an invalid response from the server: " + statusCode);

            }
            return this.parseUMPResponse(responseBytes);

        }
    }

    private ServerAbrResponse parseUMPResponse(byte[] response) throws IOException {
        this.headerIdToFormatKeyMap.clear();

        this.initializedFormats.forEach((fmt) -> {
            fmt.setSequenceList(List.of());
            fmt.setMediaChunks(List.of());
        });

        AtomicReference<SabrError> sabrErrorRef = new AtomicReference<>();
        AtomicReference<SabrRedirect> sabrRedirectRef = new AtomicReference<>();
        AtomicReference<StreamProtectionStatus> protectionRef = new AtomicReference<>();

        UMP ump = new UMP(new ChunkedDataBuffer(List.of(response)));

        ump.parse(part -> {
            byte[] data = part.getData().getChunks().get(0);
            Optional<PartEnum> maybeType = PartEnum.fromCode(part.getType());
            if (maybeType.isEmpty())
                return;

            switch (maybeType.get()) {
                case MEDIA_HEADER:
                    try {
                        this.processMediaHeader(data);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("media header parse failed", e);
                    }
                    break;

                case MEDIA:
                    this.processMediaData(part.getData());
                    break;

                case MEDIA_END:
                    this.processEndOfMedia(part.getData());
                    break;

                case NEXT_REQUEST_POLICY:
                    try {
                        this.processNextRequestPolicy(data);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("next-request policy parse failed", e);
                    }
                    break;

                case FORMAT_INITIALIZATION_METADATA:
                    try {
                        this.processFormatInitialization(data);
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("format-init metadata parse failed", e);
                    }
                    break;

                case SABR_ERROR:
                    try {
                        sabrErrorRef.set(SabrError.parseFrom(data));
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("failed to parse SABR error", e);
                    }
                    break;

                case SABR_REDIRECT:
                    try {
                        sabrRedirectRef.set(this.processSabrRedirect(data));
                    } catch (IOException e) {
                        throw new RuntimeException("failed to parse sabr redirect ", e);
                    }
                    break;

                case STREAM_PROTECTION_STATUS:
                    try {
                        protectionRef.set(StreamProtectionStatus.parseFrom(data));
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("failed to parse stream protection status", e);
                    }
                    break;

                default:
                    break;
            }
        });

        return new ServerAbrResponse(
                this.initializedFormats,
                protectionRef.get(),
                sabrRedirectRef.get(),
                sabrErrorRef.get());

    }

    private void processMediaHeader(byte[] data) throws InvalidProtocolBufferException {
        MediaHeader mediaHeader = MediaHeader.parseFrom(data);

        if (!mediaHeader.hasFormatId()) {
            return;

        }

        String formatKey = FormatUtils.getFormatKey(mediaHeader.getFormatId());
        InitializedFormat currentFormat = this.formatsByKey.getOrDefault(formatKey, this.registerFormat(mediaHeader));

        if (currentFormat == null) {
            return;
        }

        if (mediaHeader.hasSequenceNumber() && (this.previousSequences.containsKey(formatKey)
                && this.previousSequences.get(formatKey).contains((int) mediaHeader.getSequenceNumber()))) {
            return;
        }

        if (mediaHeader.hasHeaderId()) {
            if (!this.headerIdToFormatKeyMap.containsKey(mediaHeader.getHeaderId())) {
                this.headerIdToFormatKeyMap.put(mediaHeader.getHeaderId(), formatKey);

            }
        }

        long seqNum = mediaHeader.hasSequenceNumber()
                ? mediaHeader.getSequenceNumber()
                : 0L;
        if (!currentFormat.getSequenceList().stream().anyMatch(s -> s.getSequenceNumber() == seqNum)) {
            currentFormat.getSequenceList()
                    .add(new Sequence(mediaHeader.getItag(), mediaHeader.getFormatId(), mediaHeader.getIsInitSeg(),
                            mediaHeader.getDurationMs(), mediaHeader.getStartMs(), mediaHeader.getStartRange(),
                             mediaHeader.getSequenceNumber(), mediaHeader.getContentLength(),
                            mediaHeader.getTimeRange()));

            if (mediaHeader.hasSequenceNumber()) {
                BufferedRange state = currentFormat.getState();
                BufferedRange newState = state.toBuilder()
                        .setDurationMs(state.getDurationMs() + mediaHeader.getDurationMs())
                        .setEndSegmentIndex(state.getEndSegmentIndex() + 1).build();
                currentFormat.setState(newState);

            }
        }

    }

    private void processMediaData(ChunkedDataBuffer data) {
        int headerId = data.getUint8(0);
        ChunkedDataBuffer streamdata = data.split(1).getRemainingBuffer();
        String formatKey = this.headerIdToFormatKeyMap.get(headerId);

        if (formatKey == null) {
            return;
        }

        InitializedFormat currentFormat = this.formatsByKey.get(formatKey);
        if (currentFormat == null) {
            return;
        }
        currentFormat.getMediaChunks().add(streamdata.getChunks().get(0));
    }

    private void processEndOfMedia(ChunkedDataBuffer data) {
        int headerId = data.getUint8(0);
        this.headerIdToFormatKeyMap.remove(headerId);
    }

    private void processNextRequestPolicy(byte[] data) throws InvalidProtocolBufferException {
        NextRequestPolicy nextRequestPolicy = NextRequestPolicy.parseFrom(data);
        this.playbackCookie = nextRequestPolicy.getPlaybackCookie();
    }

    private void processFormatInitialization(byte[] data) throws InvalidProtocolBufferException {
        FormatInitializationMetadata formatInitializationMetadata = FormatInitializationMetadata.parseFrom(data);
        this.registerFormat(formatInitializationMetadata);
    }

    private SabrRedirect processSabrRedirect(byte[] data) throws InvalidProtocolBufferException, IOException {
        SabrRedirect sabrRedirect = SabrRedirect.parseFrom(data);
        if (!sabrRedirect.hasUrl()) {
            throw new RuntimeException("Invalid SABR Redirect url.");
        }
        this.serverAbrStreamingUrl = sabrRedirect.getUrl();
        return sabrRedirect;
    }

    private InitializedFormat registerFormat(MediaHeader data) {
        return this.registerFormat(data);

    }

    private InitializedFormat registerFormat(FormatInitializationMetadata data) {
        if (!data.hasFormatId()) {
            return null;
        }

        String formatKey = FormatUtils.getFormatKey(data.getFormatId());

        if (!this.formatsByKey.containsKey(formatKey)) {
            InitializedFormat format = new InitializedFormat();
            format.setFormatId(data.getFormatId());
            format.setFormatKey(formatKey);
            format.setMimeType(data.getMimeType());
            format.setDurationMs(Long.valueOf(data.getDurationMs()));
            format.setSequenceCount(data.getEndSegmentNumber());
            format.setSequenceList(List.of());
            format.setMediaChunks(List.of());
            format.setState(BufferedRange.newBuilder().setFormatId(data.getFormatId()).setStartTimeMs(0)
                    .setDurationMs(0).setStartSegmentIndex(1).setEndSegmentIndex(0).build());

            this.initializedFormats.add(format);
            InitializedFormat lastFmt = initializedFormats.get(initializedFormats.size() - 1);
            this.formatsByKey.put(formatKey, lastFmt);

            return format;

        }
        return null;
    }

}
