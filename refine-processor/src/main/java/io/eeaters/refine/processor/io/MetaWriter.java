package io.eeaters.refine.processor.io;

import io.eeaters.refine.processor.meta.MetaInfo;

import java.util.List;

public class MetaWriter {

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
     * 其中import直接代码中全类名， field和construct暂不需要使用
     * <br>
     * 因此，流程为： package -> annotation -> class -> method 即可
     * @param writer
     * @param metaInfo
     */
    public void writeTo(RefineWriter writer, MetaInfo metaInfo) {
        writePackage(writer, metaInfo.getPackageName());
        if (metaInfo.isSpringComponent()) {
            writer.println("@org.springframework.stereotype.Component");
        }
        writeClass(writer, metaInfo, () -> {
            writeMethod(writer, metaInfo);
        });
    }

    private void writePackage(RefineWriter writer, String packageName) {
        writer.println("package " + packageName + ";");
    }

    private void writeMethod(RefineWriter writer, MetaInfo metaInfo) {
        metaInfo.getRefineMethods().forEach(methodInfo -> writeMethod(writer, methodInfo));
    }

    /**
     * 方法构成:
     * method (){
     *     method_body
     * }
     * @param writer
     * @param methodInfo
     */
    private void writeMethod(RefineWriter writer, MetaInfo.MethodInfo methodInfo) {
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
        writer.indented(() -> writer.println("return null;"));
        writer.println("}");
    }

    private void writeClass(RefineWriter writer, MetaInfo metaInfo, Runnable runnable) {
        writer.println("public class " + metaInfo.getClassName() + " extends " + metaInfo.getOriginClass().getSimpleName().toString() + " {");
        writer.indented(()-> runnable.run());
        writer.println("}");
    }

}
