package org.terasology.nui.reflection;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.terasology.nui.UIWidget;

import java.util.List;
import java.util.Set;

public final class UIReflectionUtil {
    private UIReflectionUtil() {
    }

    /**
     * Returns an ordered list of super classes and interfaces for the given class, that have a common base class.
     * The set is ordered with the deepest interface first, through all the interfaces, and then all the super classes.
     *
     * @return an ordered list of super classes and interfaces for the given class, that have a common base class.
     */
    public static <T> List<Class<? extends T>> getInheritanceTree(Class<? extends T> forClass, Class<T> baseClass) {
        Set<Class<? extends T>> result = Sets.newLinkedHashSet();
        for (Class<?> interfaceType : forClass.getInterfaces()) {
            if (baseClass.isAssignableFrom(interfaceType)) {
                addInterfaceToInheritanceTree((Class<? extends T>) interfaceType, baseClass, result);
            }
        }
        addClassToInheritanceTree(forClass, baseClass, result);
        return Lists.newArrayList(result);
    }

    private static <T> void addClassToInheritanceTree(Class<? extends T> element, Class<T> baseClass, Set<Class<? extends T>> result) {
        for (Class<?> interfaceType : element.getInterfaces()) {
            if (baseClass.isAssignableFrom(interfaceType)) {
                addInterfaceToInheritanceTree((Class<? extends T>) interfaceType, baseClass, result);
            }
        }
        if (element.getSuperclass() != null && baseClass.isAssignableFrom(element.getSuperclass())) {
            addClassToInheritanceTree((Class<? extends T>) element.getSuperclass(), baseClass, result);
        }
        result.add(element);
    }

    private static <T> void addInterfaceToInheritanceTree(Class<? extends T> interfaceType, Class<T> baseClass, Set<Class<? extends T>> result) {
        for (Class<?> parentInterface : interfaceType.getInterfaces()) {
            if (UIWidget.class.isAssignableFrom(parentInterface)) {
                addInterfaceToInheritanceTree((Class<? extends T>) parentInterface, baseClass, result);
            }
        }
        result.add(interfaceType);
    }
}
