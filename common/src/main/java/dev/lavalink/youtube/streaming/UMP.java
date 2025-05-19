package dev.lavalink.youtube.streaming;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

/**
 * Parses UMP-formatted data from a ChunkedDataBuffer.
 */
public class UMP {
    private ChunkedDataBuffer chunkedDataBuffer;

    /**
     * Creates a new UMP parser.
     *
     * @param chunkedDataBuffer Buffer containing UMP format data.
     */
    public UMP(ChunkedDataBuffer chunkedDataBuffer) {
        this.chunkedDataBuffer = chunkedDataBuffer;
    }

    /**
     * Parses parts from the buffer and calls the handler for each complete part.
     *
     * @param handlePart Function called with each complete part.
     * @return Partial PartData if parsing is incomplete, or null otherwise.
     */
    public PartData parse(Consumer<PartData> handlePart) {
        while (true) {
            int offset = 0;

            VarIntResult typeResult = readVarInt(offset);
            int partType = typeResult.value;
            offset = typeResult.offset;

            VarIntResult sizeResult = readVarInt(offset);
            int partSize = sizeResult.value;
            offset = sizeResult.offset;

            if (partType < 0 || partSize < 0) {
                break;
            }

            if (!chunkedDataBuffer.canReadBytes(offset, partSize)) {
                if (!chunkedDataBuffer.canReadBytes(offset, 1)) {
                    break;
                }
                return new PartData(partType, partSize, chunkedDataBuffer);
            }

            ChunkedDataBuffer.SplitResult splitResult = chunkedDataBuffer.split(offset);
            // extract partSize bytes from extractedBuffer
            ChunkedDataBuffer.SplitResult finalSplit = splitResult.getRemainingBuffer().split(partSize);
            ChunkedDataBuffer extracted = finalSplit.getExtractedBuffer();
            ChunkedDataBuffer remaining = finalSplit.getRemainingBuffer();
            offset = 0;

            handlePart.accept(new PartData(partType, partSize, extracted));
            chunkedDataBuffer = remaining;
        }
        return null;
    }

    /**
     * Reads a variable-length integer from the buffer.
     *
     * @param offset Position to start reading from.
     * @return VarIntResult(value, newOffset), or ( -1, offset ) if incomplete.
     */
    public VarIntResult readVarInt(int offset) {
        int byteLength;
        // Determine length
        if (chunkedDataBuffer.canReadBytes(offset, 1)) {
            int firstByte = chunkedDataBuffer.getUint8(offset);
            if (firstByte < 0x80)
                byteLength = 1;
            else if (firstByte < 0xC0)
                byteLength = 2;
            else if (firstByte < 0xE0)
                byteLength = 3;
            else if (firstByte < 0xF0)
                byteLength = 4;
            else
                byteLength = 5;
        } else {
            byteLength = 0;
        }

        if (byteLength < 1 || !chunkedDataBuffer.canReadBytes(offset, byteLength)) {
            return new VarIntResult(-1, offset);
        }

        int value;
        switch (byteLength) {
            case 1:
                value = chunkedDataBuffer.getUint8(offset++);
                break;
            case 2:
                int b1 = chunkedDataBuffer.getUint8(offset++);
                int b2 = chunkedDataBuffer.getUint8(offset++);
                value = (b1 & 0x3F) + 64 * b2;
                break;
            case 3:
                b1 = chunkedDataBuffer.getUint8(offset++);
                int b21 = chunkedDataBuffer.getUint8(offset++);
                int b3 = chunkedDataBuffer.getUint8(offset++);
                value = (b1 & 0x1F) + 32 * (b21 + 256 * b3);
                break;
            case 4:
                b1 = chunkedDataBuffer.getUint8(offset++);
                b21 = chunkedDataBuffer.getUint8(offset++);
                int b31 = chunkedDataBuffer.getUint8(offset++);
                int b4 = chunkedDataBuffer.getUint8(offset++);
                value = (b1 & 0x0F) + 16 * (b21 + 256 * (b31 + 256 * b4));
                break;
            default:
                int tempOffset = offset + 1;
                chunkedDataBuffer.focus(tempOffset);
                ByteBuffer view = getCurrentDataView();
                if (canReadFromCurrentChunk(tempOffset, 4)) {
                    int local = tempOffset - chunkedDataBuffer.getCurrentChunkOffset();
                    value = view.order(ByteOrder.LITTLE_ENDIAN).getInt(local);
                } else {
                    int x3 = chunkedDataBuffer.getUint8(tempOffset + 2)
                            + 256 * chunkedDataBuffer.getUint8(tempOffset + 3);
                    value = chunkedDataBuffer.getUint8(tempOffset)
                            + 256 * (chunkedDataBuffer.getUint8(tempOffset + 1) + 256 * x3);
                }
                offset += 5;
                break;
        }

        return new VarIntResult(value, offset);
    }

    public boolean canReadFromCurrentChunk(int offset, int length) {
        int localOffset = offset - chunkedDataBuffer.getCurrentChunkOffset();
        int chunkLen = chunkedDataBuffer.getCurrentChunkLength();
        return localOffset + length <= chunkLen;
    }

    public ByteBuffer getCurrentDataView() {
        return chunkedDataBuffer.getCurrentDataView();
    }

    /**
     * Simple struct to hold varint parsing result.
     */
    public static class VarIntResult {
        public final int value;
        public final int offset;

        public VarIntResult(int value, int offset) {
            this.value = value;
            this.offset = offset;
        }
    }
}
