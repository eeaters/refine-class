package io.eeaters.refine.processor.io;

import io.eeaters.refine.processor.meta.MetaInfo;

public class NoneExceptionMetaWriter extends MetaWriter{
    @Override
    protected void writeConstructor(RefineWriter writer, MetaInfo metaInfo) {

    }

    @Override
    protected void writeField(RefineWriter writer, MetaInfo metaInfo) {

    }

    @Override
    protected void writeMethodBody(RefineWriter writer, MetaInfo metaInfo, MetaInfo.MethodInfo methodInfo) {
        writer.println("return null;");
    }
}
