package io.eeaters.refine.processor.meta;

import javax.lang.model.element.TypeElement;
import java.util.List;

public class MetaInfo {

    private TypeElement originClass;

    private String packageName;

    private String className;

    private boolean isSpringComponent;

    private List<MethodInfo> refineMethods;

    public TypeElement getOriginClass() {
        return originClass;
    }

    public void setOriginClass(TypeElement originClass) {
        this.originClass = originClass;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isSpringComponent() {
        return isSpringComponent;
    }

    public void setSpringComponent(boolean springComponent) {
        isSpringComponent = springComponent;
    }

    public List<MethodInfo> getRefineMethods() {
        return refineMethods;
    }

    public void setRefineMethods(List<MethodInfo> refineMethods) {
        this.refineMethods = refineMethods;
    }

    public static class MethodInfo{
        private String returnType;

        private String methodName;

        private List<ParameterInfo> parameterInfoList;

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public List<ParameterInfo> getParameterInfoList() {
            return parameterInfoList;
        }

        public void setParameterInfoList(List<ParameterInfo> parameterInfoList) {
            this.parameterInfoList = parameterInfoList;
        }
    }

    public static class ParameterInfo {
        private String type;
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
