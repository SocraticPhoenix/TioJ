package com.gmail.socraticphoenix.tioj;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Represents a TIO server.
 */
public class Tio {
    /**
     * The main TIO server, run by Dennis of codegolf.stackexchange.com, located at {@code https://tio.run/}.
     */
    public static final Tio MAIN;

    static {
        try {
            MAIN = new Tio(new URI("https://tio.run/"));
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private URL backend;
    private URL json;

    /**
     * Creates a new Tio instance with the given {@link URI} as the root server location.
     *
     * @param root The root of this Tio instance. For example {@code https://tio.run/} is the root location of the {@link Tio#MAIN} server.
     * @throws MalformedURLException If the given URI is not a URL.
     */
    public Tio(URI root) throws MalformedURLException {
        this.backend = root.resolve("cgi-bin/run/api/").toURL();
        this.json = root.resolve("languages.json").toURL();
    }

    /**
     * @return A new {@link TioRequest} object.
     */
    public static TioRequest newRequest() {
        return new TioRequest();
    }

    /**
     * @return A {@link Supplier} which, when called, queries the TIO server for a list of all supported languages. The
     * operation is blocking, but the Supplier may be executed on a different thread if necessary.
     */
    public Supplier<TioResponse<Set<String>>> queryLanguages() {
        return () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) this.json.openConnection();
                StringBuilder builder = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }

                reader.close();

                JSONObject object = new JSONObject(builder.toString());
                return new TioResponse<>(connection.getResponseCode(), Optional.of(object.keySet()), Optional.empty());
            } catch (IOException | JSONException e) {
                return new TioResponse<>(-1, Optional.empty(), Optional.of(e));
            }
        };
    }

    /**
     * Creates a {@link Supplier} which, when called, sends the given request to the TIO server, and retrieves the
     * {@link TioResult}. The operation is blocking, but the Supplier may be executed on a different thread if necessary.
     *
     * @param format The TioRequest to send.
     * @return The Suppler that queries the server.
     */
    public Supplier<TioResponse<TioResult>> send(TioRequest format) {
        return this.send(format.toBytes());
    }

    /**
     * Creates a {@link Supplier} which, when called, sends the given request to the TIO server, and retrieves the
     * {@link TioResult}. The operation is blocking, but the Supplier may be executed on a different thread if necessary.
     *
     * @param message The TioRequest (in byte form) to send.
     * @return The Suppler that queries the server.
     */
    public Supplier<TioResponse<TioResult>> send(byte[] message) {
        return () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) this.backend.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream stream = connection.getOutputStream();
                stream.write(message);
                stream.flush();
                stream.close();

                int response = connection.getResponseCode();
                if (response == 200) {
                    InputStream in = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int i;
                    while ((i = in.read(buf)) >= 0) {
                        bos.write(buf, 0, i);
                    }
                    in.close();

                    return new TioResponse<>(response, Optional.of(prepare(bos.toByteArray())), Optional.empty());
                } else {
                    return new TioResponse<>(response, Optional.empty(), Optional.empty());
                }
            } catch (IOException | DataFormatException | ArrayIndexOutOfBoundsException e) {
                return new TioResponse<>(-1, Optional.empty(), Optional.of(e));
            }
        };
    }

    private static TioResult prepare(byte[] response) throws DataFormatException {
        Inflater inflater = new Inflater(true);
        inflater.setInput(response, 10, response.length - 10);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buf);
            bos.write(buf, 0, count);
        }

        response = bos.toByteArray();

        byte[] separator = new byte[16];
        System.arraycopy(response, 0, separator, 0, 16);
        List<String> pieces = new ArrayList<>();

        ByteArrayOutputStream piece = new ByteArrayOutputStream();

        for (int i = 16; i < response.length; i++) {
            if (occurs(i, separator, response)) {
                pieces.add(new String(piece.toByteArray(), StandardCharsets.UTF_8));
                piece = new ByteArrayOutputStream();
                i += 15;
            } else {
                piece.write(response[i]);
            }
        }

        return new TioResult(pieces.toArray(new String[pieces.size()]));
    }

    private static boolean occurs(int index, byte[] arg, byte[] target) {
        for (int i = 0, k = index; i < arg.length && k < target.length; i++, k++) {
            if (arg[i] != target[k]) {
                return false;
            }
        }

        return index + arg.length <= target.length;
    }

}
