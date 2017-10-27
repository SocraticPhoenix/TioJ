package com.gmail.socraticphoenix.tioj;

/**
 * Represents a file in a {@link TioRequest}. The {@code .code.tio} and {@code .input.tio} files are the most common
 * files.
 */
public class TioFile {
    private String name;
    private byte[] content;

    /**
     * Creates a new TioFile.
     *
     * @param name The name of the file.
     * @param content The content of the file.
     */
    public TioFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    /**
     * @return The name of this file.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The content of this file.
     */
    public byte[] getContent() {
        return this.content;
    }

}
