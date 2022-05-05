package io.eeaters.refine.example;

import io.eeaters.refine.core.RefineComponent;
import io.eeaters.refine.core.enums.ExceptionStrategy;

@RefineComponent(exceptionStrategy = ExceptionStrategy.THROW)
public abstract class FactoryEcho implements Echo {

    public FactoryEcho(Throwable throwable) {
    }
}
