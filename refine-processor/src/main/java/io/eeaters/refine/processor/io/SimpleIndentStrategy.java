package io.eeaters.refine.processor.io;

import java.util.function.Function;

/**
 * 缩进策略
 */
public class SimpleIndentStrategy implements Function<Integer, String> {

    private final String indent;


    public SimpleIndentStrategy(String indent) {
        this.indent = indent;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param deep the function argument
     * @return the function result
     */
    @Override
    public String apply(Integer deep) {
        if (deep < 0) {
            throw new IllegalArgumentException("深度不能为负数");
        }
        StringBuilder sb = new StringBuilder();
        for (Integer i = 0; i < deep; i++) {
            sb.append(indent);
        }
        return sb.toString();
    }
}
