package dev.lavalink.youtube.streaming;

public class Format {
    private int itag;
    private Integer width;         // nullable
    private Integer height;        // nullable
    private String lastModified;
    private String xtags;          // nullable

    public Format() {}

    public Format(int itag, Integer width, Integer height, String lastModified, String xtags) {
        this.itag = itag;
        this.width = width;
        this.height = height;
        this.lastModified = lastModified;
        this.xtags = xtags;
    }

    public int getItag() {
        return itag;
    }

    public void setItag(int itag) {
        this.itag = itag;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getXtags() {
        return xtags;
    }

    public void setXtags(String xtags) {
        this.xtags = xtags;
    }
}
