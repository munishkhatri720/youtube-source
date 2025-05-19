package dev.lavalink.youtube.streaming;
/**
 * Represents one parsed part.
 */
public class PartData {
    private final int type;
    private final int size;
    private final ChunkedDataBuffer data;

    public PartData(int type, int size, ChunkedDataBuffer data) {
        this.type = type;
        this.size = size;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public ChunkedDataBuffer getData() {
        return data;
    }
}
