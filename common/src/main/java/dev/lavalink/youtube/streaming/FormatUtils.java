package dev.lavalink.youtube.streaming;
import dev.lavalink.youtube.protos.Common.FormatId;

public class FormatUtils {
    public static String getFormatKey(FormatId formatId) {
        return formatId.getItag() + ";" + formatId.getLastModified() + ";";
    }
}
