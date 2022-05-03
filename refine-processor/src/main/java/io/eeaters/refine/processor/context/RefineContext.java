package io.eeaters.refine.processor.context;

import io.eeaters.refine.core.RefineComponent;
import io.eeaters.refine.processor.io.MetaWriter;
import io.eeaters.refine.processor.io.RefineWriter;
import io.eeaters.refine.processor.meta.MetaInfo;
import io.eeaters.refine.processor.util.RefineElementUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RefineContext {

    private final ProcessingEnvironment processingEnv;

    private final RoundEnvironment roundEnvironment;

    private final TypeElement typeElement;

    private Elements elementUtils;

    // 将用于操作io流,将信息写入
    private MetaWriter metaWriter;

    // 用于创建实现类
    private Filer filer;

    private Types typeUtils;
    // 用于输出信息
    private Messager messager;

    public RefineContext(ProcessingEnvironment processingEnv, RoundEnvironment roundEnvironment, TypeElement typeElement) {
        this.processingEnv = processingEnv;
        this.roundEnvironment = roundEnvironment;
        this.elementUtils = processingEnv.getElementUtils();
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
        this.typeUtils = processingEnv.getTypeUtils();
        this.metaWriter = new MetaWriter();
        this.typeElement = typeElement;
    }

    public void refine() {
        //第一步: 获取元信息
        MetaInfo metaInfo = buildMetaInfo();
        //第二部: 实现类的文件流
        JavaFileObject source = createSource(metaInfo);
        //第三步: 写入到流中
        writeIO(metaInfo, source);
    }

    /**
     * 这只是编译器,无法通过反射等方式获取所有abstract方法 ; 还需要将他父类和实现的接口给拎出来
     * @return
     */
    private MetaInfo buildMetaInfo() {
        List<ExecutableElement> abstractList = new ArrayList<>();
        RefineElementUtils.addEnclosedElementsInHierarchy(elementUtils, abstractList, typeElement, typeElement);
        RefineComponent annotation = typeElement.getAnnotation(RefineComponent.class);

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setOriginClass(typeElement);
        metaInfo.setSpringComponent(annotation.isComponent());

        String qualifiedName = typeElement.getQualifiedName().toString();
        int lastIndexOf = qualifiedName.lastIndexOf(".");
        String packageName = qualifiedName.substring(0, lastIndexOf);
        String className = qualifiedName.substring(lastIndexOf + 1);
        metaInfo.setPackageName(packageName);
        metaInfo.setClassName(className + "Impl");

        Set<String> importClasses = new HashSet<>();

        metaInfo.setRefineMethods(
                abstractList.stream()
                        .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
                        .map(element -> buildMethodInfo(element,importClasses))
                        .collect(Collectors.toList())
        );

        return metaInfo;
    }

    public MetaInfo.MethodInfo buildMethodInfo(ExecutableElement element, Set<String> importClasses) {
        String methodName = element.getSimpleName().toString();

        String returnType = element.getReturnType().toString();

        ExecutableType methodType = (ExecutableType)typeUtils.asMemberOf((DeclaredType) typeElement.asType(), element);

        List<MetaInfo.ParameterInfo> parameterInfoList = new ArrayList<>();
        int i = 0;
        for (TypeMirror parameterType : methodType.getParameterTypes()) {
            MetaInfo.ParameterInfo parameterInfo = new MetaInfo.ParameterInfo();
            parameterInfo.setName("var" + i++);
            parameterInfo.setType(parameterType.toString());
            parameterInfoList.add(parameterInfo);
        }
        MetaInfo.MethodInfo methodInfo = new MetaInfo.MethodInfo();
        methodInfo.setReturnType(returnType);
        methodInfo.setParameterInfoList(parameterInfoList);
        methodInfo.setMethodName(methodName);
        return methodInfo;
    }




    private JavaFileObject createSource(MetaInfo info) {
        String className = info.getClassName();
        try {
            return filer.createSourceFile(className, info.getOriginClass());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "创建子类文件失败, 异常为: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void writeIO(MetaInfo metaInfo, JavaFileObject source) {
        try (RefineWriter refineWriter = new RefineWriter(source.openWriter())) {
            metaWriter.writeTo(refineWriter, metaInfo);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "写入实现类失败, 异常为: " + e.getMessage());
        }
    }

}
