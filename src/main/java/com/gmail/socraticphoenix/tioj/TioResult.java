package com.gmail.socraticphoenix.tioj;

/**
 * Represents a successful execution of code on the TIO backend.
 */
public class TioResult {
    private String[] pieces;

    /**
     * Creates a new TioResult.
     *
     * @param pieces The result.
     */
    public TioResult(String[] pieces) {
        this.pieces = pieces;
    }

    /**
     * @return All parts of this result.
     */
    public String[] getPieces() {
        return this.pieces;
    }

    /**
     * Returns the part of this result corresponding to the field.
     *
     * @param field The field to get.
     * @return The result for the given field.
     */
    public String get(Field field) {
        return this.pieces[field.index];
    }

    /**
     * Checks if this result contains a result for the given field.
     *
     * @param field The field to check.
     * @return True if this result has a result for the given field, false otherwise.
     */
    public boolean has(Field field) {
        return this.pieces.length > field.index;
    }

    /**
     * Represents the fields a {@link TioResult} can have.
     */
    public enum Field {
        /**
         * The output field, the STDOUT of the program.
         */
        OUTPUT(0),
        /**
         * The debug field, the STDERR and timing information of the program.
         */
        DEBUG(1)
        ;
        int index;

        Field(int index) {
            this.index = index;
        }

    }

}
