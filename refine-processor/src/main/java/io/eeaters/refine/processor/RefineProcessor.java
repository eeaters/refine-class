package io.eeaters.refine.processor;

import io.eeaters.refine.processor.context.RefineContext;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementKindVisitor6;
import java.util.Set;

@SupportedAnnotationTypes("io.eeaters.refine.core.RefineComponent")
public class RefineProcessor extends AbstractProcessor {



    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            if ( annotation.getKind() != ElementKind.ANNOTATION_TYPE ) {
                continue;
            }

            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                TypeElement typeElement = asTypeElement( element );
                RefineContext refineContext = new RefineContext(processingEnv, roundEnv, typeElement);
                refineContext.refine();
            }
        }
        return false;
    }



    private TypeElement asTypeElement(Element element) {
        return element.accept(
                new ElementKindVisitor6<TypeElement, Void>() {
                    @Override
                    public TypeElement visitTypeAsInterface(TypeElement e, Void p) {
                        return e;
                    }

                    @Override
                    public TypeElement visitTypeAsClass(TypeElement e, Void p) {
                        return e;
                    }

                }, null
        );
    }
}
