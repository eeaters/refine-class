package io.eeaters.refine.processor.io;

import io.eeaters.refine.core.enums.ExceptionStrategy;
import io.eeaters.refine.processor.meta.MetaInfo;

import java.util.List;

public abstract class MetaWriter {

    public static MetaWriter getInstance(ExceptionStrategy strategy) {
        if (strategy == ExceptionStrategy.NONE) {
            return new NoneExceptionMetaWriter();
        }
        return new ExceptionMetaWriter();
    }


    /**
     * 一个类大体有以下步骤写入
     * <ul>
     *     <li>package</li>
     *     <li>import</li>
     *     <li>annotation</li>
     *     <li>class</li>
     *     <li>field</li>
     *     <li>construct</li>
     *     <li>method</li>
     * </ul>
     *
     * 其中import直接代码中全类名，后面也可以在所有入参/返回值中将所有的类存放到一个set集合中,这样子更适合一些
     * @param writer
     * @param metaInfo
     */
    public void writeTo(RefineWriter writer, MetaInfo metaInfo) {
        writePackage(writer, metaInfo.getPackageName());
        writerAnnotation(writer, metaInfo);
        writeClass(writer, metaInfo, () -> {
            writeField(writer, metaInfo);
            writeConstructor(writer, metaInfo);
            writeMethod(writer, metaInfo);
        });
    }

    private void writerAnnotation(RefineWriter writer, MetaInfo metaInfo) {
        if (metaInfo.isSpringComponent()) {
            writer.println("@org.springframework.stereotype.Component");
        }
    }

    private void writePackage(RefineWriter writer, String packageName) {
        writer.println("package " + packageName + ";");
    }

    private void writeMethod(RefineWriter writer, MetaInfo metaInfo) {
        metaInfo.getRefineMethods().forEach(methodInfo -> writeMethod(writer, metaInfo, methodInfo));
    }

    /**
     * 方法构成:
     * <pre>
     * method (){
     *     method_body
     * }
     * </pre>
     * throws Exception很容易处理,针对降级没啥用,不做考虑
     *
     * @param writer
     * @param methodInfo
     */
    private void writeMethod(RefineWriter writer, MetaInfo metaInfo, MetaInfo.MethodInfo methodInfo) {
        String returnType = methodInfo.getReturnType();
        String methodName = methodInfo.getMethodName();
        List<MetaInfo.ParameterInfo> parameterInfos = methodInfo.getParameterInfoList();
        StringBuilder method = new StringBuilder("public ");
        method.append(returnType)
                .append(" ")
                .append(methodName)
                .append(" (");
        if (parameterInfos != null && !parameterInfos.isEmpty()) {
            for (MetaInfo.ParameterInfo info : parameterInfos) {
                method.append(info.getType())
                        .append(" ")
                        .append(info.getName())
                        .append(",");
            }
        }
        String methodTitle = method.substring(0, method.length() - 1) + " ){";
        writer.println(methodTitle);
        writer.indented(() -> writeMethodBody(writer, metaInfo, methodInfo));
        writer.println("}");
    }

    protected abstract void writeConstructor(RefineWriter writer, MetaInfo metaInfo);

    protected abstract void writeField(RefineWriter writer, MetaInfo metaInfo);

    protected abstract void writeMethodBody(RefineWriter writer, MetaInfo metaInfo, MetaInfo.MethodInfo methodInfo);


    private void writeClass(RefineWriter writer, MetaInfo metaInfo, Runnable runnable) {
        writer.println("public class " + metaInfo.getClassName() + " extends " + metaInfo.getOriginClass().getSimpleName().toString() + " {");
        writer.indented(()-> runnable.run());
        writer.println("}");
    }

}
