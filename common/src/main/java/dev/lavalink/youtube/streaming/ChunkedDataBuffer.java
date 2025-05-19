package dev.lavalink.youtube.streaming;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChunkedDataBuffer {
    private final List<byte[]> chunks;
    private int currentChunkOffset;
    private int currentChunkIndex;
    private ByteBuffer currentDataView; // optional, lazily created if needed
    private int totalLength;

    public ChunkedDataBuffer() {
        this(new ArrayList<>());
    }

    public ChunkedDataBuffer(List<byte[]> initialChunks) {
        this.chunks = new ArrayList<>();
        this.currentChunkOffset = 0;
        this.currentChunkIndex = 0;
        this.currentDataView = null;
        this.totalLength = 0;
        for (byte[] chunk : initialChunks) {
            append(chunk);
        }
    }

    public List<byte[]> getChunks() {
        return this.chunks;
    }

   

    public int getCurrentChunkOffset() {
        return currentChunkOffset;
    }

    public int getCurrentChunkIndex() {
        return currentChunkIndex;
    }

    /** Lazily creates/focuses and returns the ByteBuffer view. */
    public ByteBuffer getCurrentDataView() {
        if (currentDataView == null) {
            byte[] chunk = chunks.get(currentChunkIndex);
            currentDataView = ByteBuffer.wrap(chunk).order(ByteOrder.LITTLE_ENDIAN);
        }
        return currentDataView;
    }

    /** Returns the length of the current chunk. */
    public int getCurrentChunkLength() {
        return chunks.get(currentChunkIndex).length;
    }

    public int getLength() {
        return totalLength;
    }

    public void append(byte[] chunk) {
        if (canMergeWithLastChunk(chunk)) {
            byte[] last = chunks.get(chunks.size() - 1);
            byte[] merged = new byte[last.length + chunk.length];
            System.arraycopy(last, 0, merged, 0, last.length);
            System.arraycopy(chunk, 0, merged, last.length, chunk.length);
            chunks.set(chunks.size() - 1, merged);
            resetFocus();
        } else {
            chunks.add(chunk);
        }
        totalLength += chunk.length;
    }

    public SplitResult split(int position) {
        ChunkedDataBuffer extracted = new ChunkedDataBuffer();
        ChunkedDataBuffer remaining = new ChunkedDataBuffer();
        Iterator<byte[]> iter = chunks.iterator();
        while (iter.hasNext()) {
            byte[] chunk = iter.next();
            if (position >= chunk.length) {
                extracted.append(chunk);
                position -= chunk.length;
            } else if (position > 0) {
                // split this chunk
                byte[] firstPart = new byte[position];
                byte[] secondPart = new byte[chunk.length - position];
                System.arraycopy(chunk, 0, firstPart, 0, position);
                System.arraycopy(chunk, position, secondPart, 0, secondPart.length);
                extracted.append(firstPart);
                remaining.append(secondPart);
                position = 0;
            } else {
                remaining.append(chunk);
            }
        }
        return new SplitResult(extracted, remaining);
    }

    public boolean isFocused(int position) {
        if (chunks.isEmpty())
            return false;
        return position >= currentChunkOffset
                && position < currentChunkOffset + chunks.get(currentChunkIndex).length;
    }

    public void focus(int position) {
        if (!isFocused(position)) {
            if (position < currentChunkOffset) {
                resetFocus();
            }
            while (currentChunkIndex < chunks.size() - 1
                    && currentChunkOffset + chunks.get(currentChunkIndex).length <= position) {
                currentChunkOffset += chunks.get(currentChunkIndex).length;
                currentChunkIndex++;
            }
            currentDataView = null;
        }
    }

    public boolean canReadBytes(int position, int length) {
        return position + length <= totalLength;
    }

    public int getUint8(int position) {
        focus(position);
        byte[] chunk = chunks.get(currentChunkIndex);
        return chunk[position - currentChunkOffset] & 0xFF;
    }

    // --- private helpers ---

    private boolean canMergeWithLastChunk(byte[] chunk) {
        if (chunks.isEmpty())
            return false;
        byte[] last = chunks.get(chunks.size() - 1);
        // In Java arrays don't expose buffer/offset, so we only merge if they came from
        // same reference.
        return last == chunk;
        // (if you have more complex backing, you could track ByteBuffer slices instead)
    }

    private void resetFocus() {
        this.currentDataView = null;
        this.currentChunkIndex = 0;
        this.currentChunkOffset = 0;
    }

    public static class SplitResult {
        private final ChunkedDataBuffer extractedBuffer;
        private final ChunkedDataBuffer remainingBuffer;

        public SplitResult(ChunkedDataBuffer extractedBuffer, ChunkedDataBuffer remainingBuffer) {
            this.extractedBuffer = extractedBuffer;
            this.remainingBuffer = remainingBuffer;
        }

        public ChunkedDataBuffer getExtractedBuffer() {
            return extractedBuffer;
        }

        public ChunkedDataBuffer getRemainingBuffer() {
            return remainingBuffer;
        }
    }
}
