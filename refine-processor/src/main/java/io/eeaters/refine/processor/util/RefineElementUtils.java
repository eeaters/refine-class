package io.eeaters.refine.processor.util;


import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static javax.lang.model.util.ElementFilter.methodsIn;

/**
 * {@link org.mapstruct.ap.internal.util.Executables}
 */
public class RefineElementUtils {


    public static void addEnclosedElementsInHierarchy(Elements elementUtils, List<ExecutableElement> alreadyAdded,
                                                       TypeElement element, TypeElement parentType) {
        if ( element != parentType ) { // otherwise the element was already checked for replacement
            element = replaceTypeElementIfNecessary( elementUtils, element );
        }

        addNotYetOverridden( elementUtils, alreadyAdded, methodsIn( element.getEnclosedElements() ), parentType );

        //父类
        if (hasNonObjectSuperclass(element)) {
            addEnclosedElementsInHierarchy(
                    elementUtils,
                    alreadyAdded,
                    asTypeElement(element.getSuperclass()),
                    parentType
            );
        }

        //父接口
        for (TypeMirror interfaceType : element.getInterfaces()) {
            addEnclosedElementsInHierarchy(
                    elementUtils,
                    alreadyAdded,
                    asTypeElement(interfaceType),
                    parentType
            );
        }

    }
    /**
     * 收集方法
     */
    private static void addNotYetOverridden(Elements elementUtils, List<ExecutableElement> alreadyCollected,
                                            List<ExecutableElement> methodsToAdd, TypeElement parentType) {
        List<ExecutableElement> safeToAdd = new ArrayList<>( methodsToAdd.size() );
        for ( ExecutableElement toAdd : methodsToAdd ) {
            if ( isNotPrivate( toAdd ) && isNotObjectEquals( toAdd )
                    && wasNotYetOverridden( elementUtils, alreadyCollected, toAdd, parentType ) ) {
                safeToAdd.add( toAdd );
            }
        }

        alreadyCollected.addAll( 0, safeToAdd );
    }
    /**
     * {@link Object}的方法不需要处理
     */
    private static boolean isNotObjectEquals(ExecutableElement executable) {
        if ( executable.getSimpleName().contentEquals( "equals" ) && executable.getParameters().size() == 1
                && asTypeElement( executable.getParameters().get( 0 ).asType() ).getQualifiedName().contentEquals(
                "java.lang.Object"
        ) ) {
            return false;
        }
        return true;
    }
    /**
     * @param elementUtils the elementUtils
     * @param alreadyCollected the list of already collected methods of one type hierarchy (order is from sub-types to
     *            super-types)
     * @param executable the method to check
     * @param parentType the type for which elements are collected
     * @return {@code true}, iff the given executable was not yet overridden by a method in the given list.
     */
    private static boolean wasNotYetOverridden(Elements elementUtils, List<ExecutableElement> alreadyCollected,
                                               ExecutableElement executable, TypeElement parentType) {
        for (ListIterator<ExecutableElement> it = alreadyCollected.listIterator(); it.hasNext(); ) {
            ExecutableElement executableInSubtype = it.next();
            if ( executableInSubtype == null ) {
                continue;
            }
            if (elementUtils.overrides(executableInSubtype, executable, parentType)) {
                return false;
            } else if (elementUtils.overrides(executable, executableInSubtype, parentType)) {
                // remove the method from another interface hierarchy that is overridden by the executable to add
                it.remove();
                return true;
            }
        }

        return true;
    }



    public static TypeElement replaceTypeElementIfNecessary(Elements elementUtils, TypeElement element) {
        if ( element.getEnclosedElements().isEmpty() ) {
            TypeElement resolvedByName = elementUtils.getTypeElement( element.getQualifiedName() );
            if ( resolvedByName != null && !resolvedByName.getEnclosedElements().isEmpty() ) {
                return resolvedByName;
            }
        }
        return element;
    }
    /**
     * @param element the type element to check
     *
     * @return {@code true}, iff the type has a super-class that is not java.lang.Object
     */
    private static boolean hasNonObjectSuperclass(TypeElement element) {
        return element.getSuperclass().getKind() == TypeKind.DECLARED
                && !asTypeElement( element.getSuperclass() ).getQualifiedName().toString().equals( "java.lang.Object" );
    }

    /**
     * @param mirror the type positionHint
     *
     * @return the corresponding type element
     */
    private static TypeElement asTypeElement(TypeMirror mirror) {
        return (TypeElement) ( (DeclaredType) mirror ).asElement();
    }

    /**
     * @param executable the executable to check
     *
     * @return {@code true}, iff the executable does not have a private modifier
     */
    private static boolean isNotPrivate(ExecutableElement executable) {
        return !executable.getModifiers().contains( Modifier.PRIVATE );
    }


}
