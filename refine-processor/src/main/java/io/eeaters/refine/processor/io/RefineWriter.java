package io.eeaters.refine.processor.io;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

/**
 * 参考 {@link io.spring.initializr.generator.io.IndentingWriter} ;
 * 该writer支持缩进功能
 */
public class RefineWriter extends Writer {

    private final Writer out;

    private int level = 0;

    private String indent = "";

    private boolean prependIndent = false;

    private final Function<Integer, String> indentStrategy;

    public RefineWriter(Writer writer) {
        this(writer, new SimpleIndentStrategy("    "));
    }

    public RefineWriter(Writer writer, Function<Integer, String> indentStrategy) {
        this.out = writer;
        this.indentStrategy = indentStrategy;
    }


    @Override
    public void write(char[] chars, int off, int len) {
        try {
            if (this.prependIndent) {
                this.out.write(this.indent.toCharArray(), 0, this.indent.length());
                this.prependIndent = false;
            }
            this.out.write(chars, off, len);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }


    /**
     * Write the specified text.
     * @param string the content to write
     */
    public void print(String string) {
        write(string.toCharArray(), 0, string.length());
    }

    /**
     * Write the specified text and append a new line.
     * @param string the content to write
     */
    public void
    println(String string) {
        write(string.toCharArray(), 0, string.length());
        println();
    }

    /**
     * Write a new line.
     */
    public void println() {
        String separator = System.lineSeparator();
        try {
            this.out.write(separator.toCharArray(), 0, separator.length());
            this.prependIndent = true;
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Increase the indentation level and execute the {@link Runnable}. Decrease the
     * indentation level on completion.
     * @param runnable the code to execute withing an extra indentation level
     */
    public void indented(Runnable runnable) {
        indent();
        runnable.run();
        outdent();
    }

    /**
     * Increase the indentation level.
     */
    private void indent() {
        this.level++;
        refreshIndent();
    }

    /**
     * Decrease the indentation level.
     */
    private void outdent() {
        this.level--;
        refreshIndent();
    }

    private void refreshIndent() {
        this.indent = this.indentStrategy.apply(this.level);
    }


}
