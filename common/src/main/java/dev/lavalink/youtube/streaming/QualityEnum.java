package dev.lavalink.youtube.streaming;

public enum QualityEnum {
    AUTO(0),
    TINY(144),
    SMALL(240),
    MEDIUM(360),
    LIGHT(144),
    LARGE(480),
    HD720(720),
    HD1080(1080),
    HD1440(1440),
    HD2160(2160),
    HD2880(2880),
    HIGHRES(4320);

    private final int value;

    QualityEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
