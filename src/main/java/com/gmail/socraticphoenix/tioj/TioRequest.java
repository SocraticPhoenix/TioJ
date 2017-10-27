package com.gmail.socraticphoenix.tioj;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Represents A request to the TIO backend.
 */
public class TioRequest {
    private List<TioFile> files = new ArrayList<>();
    private List<TioVariable> variables = new ArrayList<>();

    /**
     * Adds a file to this request. If a file with a duplicate name already exists, it will be replaced with the given file.
     *
     * @param file The file to add.
     * @return This, for method chaining.
     */
    public TioRequest addFile(TioFile file) {
        this.files.removeIf(f -> f.getName().equals(file.getName()));
        this.files.add(file);
        return this;
    }

    /**
     * Adds a variable to this request. If a variable with a duplicate name already exists, it will be replaced with the given variable.
     *
     * @param variable The variable to add.
     * @return This, for method chaining.
     */
    public TioRequest addVariable(TioVariable variable) {
        this.variables.removeIf(v -> v.getName().equals(variable.getName()));
        this.variables.add(variable);
        return this;
    }

    /**
     * Adds a file to this request. If a file with a duplicate name already exists, it will be replaced with the given file.
     *
     * @param name The name of the file.
     * @param content The content of the file.
     * @return This, for method chaining.
     */
    public TioRequest addFile(String name, byte... content) {
        return this.addFile(new TioFile(name, content));
    }

    /**
     * Adds a variable to this request. If a variable with a duplicate name already exists, it will be replaced with the given variable.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     * @return This, for method chaining.
     */
    public TioRequest addVariable(String name, String... value) {
        return this.addVariable(new TioVariable(name, value));
    }

    /**
     * Adds a file to this request. If a file with a duplicate name already exists, it will be replaced with the given file.
     *
     * @param name The name of the file.
     * @param content The content of the file.
     * @return This, for method chaining.
     */
    public TioRequest addFile(String name, String content) {
        return this.addFile(name, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Sets the programming language of the request. This is equivalent to {@code addVariable("lang", lang)}.
     *
     * @param lang The language.
     * @return This, for method chaining.
     */
    public TioRequest setLang(String lang) {
        return this.addVariable("lang", lang);
    }

    /**
     * Sets the code for this request. This is equivalent to {@code addFile(".code.tio", coded)}.
     *
     * @param code The code.
     * @return This, for method chaining.
     */
    public TioRequest setCode(String code) {
        return this.addFile(".code.tio", code);
    }

    /**
     * Sets the input for this request. This is equivalent to {@code addFile(".input.tio", input)}.
     *
     * @param input The input.
     * @return This, for method chaining.
     */
    public TioRequest setInput(String input) {
        return this.addFile(".input.tio", input);
    }

    /**
     * Sets the compiler flags for this request. This is equivalent to {@code addVariable("TIO_CFLAGS", compFlags)}.
     *
     * @param compFlags The compiler flags.
     * @return This, for method chaining.
     */
    public TioRequest setCompilerFlags(String... compFlags) {
        return this.addVariable("TIO_CFLAGS", compFlags);
    }

    /**
     * Sets the command line flags for this request. This is equivalent to {@code addVariable("TIO_OPTIONS", cmdOpts))}.
     *
     * @param cmdOpts The command line flags.
     * @return This, for method chaining.
     */
    public TioRequest setCommandLineFlags(String... cmdOpts) {
        return this.addVariable("TIO_OPTIONS", cmdOpts);
    }

    /**
     * Sets the command line arguments for this request. This is equivalent to {@code addVariable("args", args)}.
     *
     * @param args The command line arguments.
     * @return This, for method chaining.
     */
    public TioRequest setArguments(String... args) {
        return this.addVariable("args", args);
    }

    /**
     * Formats this request to a deflated array of bytes that can be sent to the TIO backend.
     *
     * @return This request as an array of bytes.
     */
    public byte[] toBytes() {
        ByteArrayOutputStream cache = new ByteArrayOutputStream();
        try {
            for (TioVariable var : this.variables) {
                this.writeVariable(cache, var.getName(), var.getContent());
            }

            for (TioFile file : this.files) {
                writeFile(cache, file.getName(), file.getContent());
            }

            cache.write("R".getBytes(StandardCharsets.UTF_8));
            cache.close();
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayOutputStream threw IOException", e);
        }

        byte[] res = cache.toByteArray();

        Deflater deflater = new Deflater(0, true);
        deflater.setLevel(Deflater.HUFFMAN_ONLY);
        deflater.setInput(res);
        deflater.finish();

        ByteArrayOutputStream deflated = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buf, 0, res.length, Deflater.NO_FLUSH);
            deflated.write(buf, 0, count);
        }

        return deflated.toByteArray();
    }

    private void writeVariable(ByteArrayOutputStream cache, String name, String... values) throws IOException {
        if (values != null && values.length > 0) {
            cache.write(("V" + name).getBytes(StandardCharsets.UTF_8));
            cache.write(0);
            cache.write(String.valueOf(values.length).getBytes(StandardCharsets.UTF_8));
            cache.write(0);
            for (String v : values) {
                cache.write(v.getBytes(StandardCharsets.UTF_8));
                cache.write(0);
            }
        }
    }

    private void writeFile(ByteArrayOutputStream cache, String name, byte[] file) throws IOException {
        cache.write(("F" + name).getBytes(StandardCharsets.UTF_8));
        cache.write(0);
        cache.write(String.valueOf(file.length).getBytes(StandardCharsets.UTF_8));
        cache.write(0);

        cache.write(file);
    }

}
