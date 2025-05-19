package dev.lavalink.youtube.streaming;

import java.util.Arrays;
import java.util.Optional;

public enum PartEnum {
    ONESIE_HEADER(10),
    ONESIE_DATA(11),
    MEDIA_HEADER(20),
    MEDIA(21),
    MEDIA_END(22),
    LIVE_METADATA(31),
    HOSTNAME_CHANGE_HINT(32),
    LIVE_METADATA_PROMISE(33),
    LIVE_METADATA_PROMISE_CANCELLATION(34),
    NEXT_REQUEST_POLICY(35),
    USTREAMER_VIDEO_AND_FORMAT_DATA(36),
    FORMAT_SELECTION_CONFIG(37),
    USTREAMER_SELECTED_MEDIA_STREAM(38),
    FORMAT_INITIALIZATION_METADATA(42),
    SABR_REDIRECT(43),
    SABR_ERROR(44),
    SABR_SEEK(45),
    RELOAD_PLAYER_RESPONSE(46),
    PLAYBACK_START_POLICY(47),
    ALLOWED_CACHED_FORMATS(48),
    START_BW_SAMPLING_HINT(49),
    PAUSE_BW_SAMPLING_HINT(50),
    SELECTABLE_FORMATS(51),
    REQUEST_IDENTIFIER(52),
    REQUEST_CANCELLATION_POLICY(53),
    ONESIE_PREFETCH_REJECTION(54),
    TIMELINE_CONTEXT(55),
    REQUEST_PIPELINING(56),
    SABR_CONTEXT_UPDATE(57),
    STREAM_PROTECTION_STATUS(58),
    SABR_CONTEXT_SENDING_POLICY(59),
    LAWNMOWER_POLICY(60),
    SABR_ACK(61),
    END_OF_TRACK(62),
    CACHE_LOAD_POLICY(63),
    LAWNMOWER_MESSAGING_POLICY(64),
    PREWARM_CONNECTION(65);

    private final int code;

    PartEnum(int code) {
        this.code = code;
    }

    public static Optional<PartEnum> fromCode(int code) {
        return Arrays.stream(values())
                     .filter(e -> e.code == code)
                     .findFirst();
    }

    public int getCode() {
        return code;
    }
}