package io.eeaters.refine.processor.io;

import io.eeaters.refine.processor.meta.MetaInfo;

public class ExceptionMetaWriter extends MetaWriter {

    @Override
    protected void writeField(RefineWriter writer, MetaInfo metaInfo) {
        writer.println("private Throwable throwable;");
    }

    @Override
    protected void writeConstructor(RefineWriter writer, MetaInfo metaInfo) {
        writer.println("public " + metaInfo.getClassName() + " (Throwable throwable) {");
        writer.indented(() -> {
            writer.println("super(throwable);");
            writer.println("this.throwable = throwable;");
        });
        writer.println("}");
    }

    @Override
    protected void writeMethodBody(RefineWriter writer, MetaInfo metaInfo, MetaInfo.MethodInfo methodInfo) {
        writer.println("throw new RuntimeException(throwable);");
    }
}
