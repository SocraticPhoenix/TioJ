package com.gmail.socraticphoenix.tioj;

import java.util.Optional;

/**
 * Represents some response from the TIO server.
 *
 * @param <T> The type of value a successful result contains.
 */
public class TioResponse<T> {
    private int code;
    private Optional<T> result;
    private Optional<Throwable> error;

    /**
     * Creates a new TioResponse.
     *
     * @param code The HTTP response code.
     * @param result The result, if successful.
     * @param error The error, if one is known.
     */
    public TioResponse(int code, Optional<T> result, Optional<Throwable> error) {
        this.code = code;
        this.result = result;
        this.error = error;
    }

    /**
     * @return The HTTP response code for this response, or -1 if an error occurred and the response code is unknown.
     */
    public int getCode() {
        return this.code;
    }

    /**
     * @return An {@link Optional} containing the result of this response, if this is a successful response.
     */
    public Optional<T> getResult() {
        return this.result;
    }

    /**
     * @return An {@link Optional} containing the error that caused this response to fail, if the error is known and this is a failed response.
     */
    public Optional<Throwable> getError() {
        return this.error;
    }
}
