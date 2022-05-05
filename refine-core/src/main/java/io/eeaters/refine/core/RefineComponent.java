package io.eeaters.refine.core;

import io.eeaters.refine.core.enums.ExceptionStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RefineComponent {

    boolean isComponent() default false;

    /**
     *  在feign的降级中,fallbackFactory是比fallback强大的功能,强大的点就是可以在回退中感知到调用的异常,
     *  如果使用了fallbackFactory没有穿异常,那还不如直接用fallback了
     *  <br/>
     * 注意: 一旦使用该策略,抽象类必须有一个throwable的构造器;
     * @return
     */
    ExceptionStrategy exceptionStrategy() default ExceptionStrategy.NONE;

}
