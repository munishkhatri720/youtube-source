package dev.lavalink.youtube.streaming;
import java.util.Base64;

public class ByteUtils {
    public static String u8ToBase64(byte[] u8) {
        return Base64.getEncoder().encodeToString(u8);
    }

    public static byte[] base64ToU8(String base64) {
        String standardBase64 = base64.replace('-', '+').replace('_', '/');
        int padding = (4 - standardBase64.length() % 4) % 4;
        standardBase64 += "=".repeat(padding);
        return Base64.getDecoder().decode(standardBase64);
    }

    public static byte[] concatenateChunks(byte[][] chunks) {
        int totalLength = 0;
        for (byte[] chunk : chunks) {
            totalLength += chunk.length;
        }

        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, offset, chunk.length);
            offset += chunk.length;
        }

        return result;
    }
}
