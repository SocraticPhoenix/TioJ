package com.gmail.socraticphoenix.tioj;

/**
 * Represents a variable in a {@link TioRequest}. The {@code args}, {@code TIO_OPTIONS} and {@code TIO_CFLAGS} are the
 * most common variables.
 */
public class TioVariable {
    private String name;
    private String[] content;

    /**
     * Creates a new TioVariable.
     *
     * @param name The name of the variable.
     * @param content The value of the variable.
     */
    public TioVariable(String name, String... content) {
        this.name = name;
        this.content = content;
    }

    /**
     * @return The name of this variable.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The value of this variable.
     */
    public String[] getContent() {
        return this.content;
    }

}
