/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.microsphere.mybatis.test.junit.jupiter.resolver;

import io.microsphere.annotation.Nonnull;
import io.microsphere.mybatis.test.junit.jupiter.MyBatisRuntime;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.junit.jupiter.api.extension.ExtensionContext.StoreScope.EXTENSION_CONTEXT;

/**
 * The interface to resolve the MyBatis component
 *
 * @param <T> the type of MyBatis component
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ParameterContext
 * @since 1.0.0
 */
public interface ComponentResolver<T> {

    /**
     * Determine if this resolver supports resolution of the field
     *
     * @param field {@link Field}
     * @return {@code true} if this resolver supports the field , otherwise {@code false}
     */
    boolean supportsField(Field field, ExtensionContext extensionContext);

    /**
     * Get the MyBatis component type
     *
     * @return non-null
     */
    @Nonnull
    Class<T> getComponentType();

    /**
     * Determine if this resolver supports resolution of the static field
     *
     * @return {@code true} if this resolver supports the static field , otherwise {@code false}
     */
    default boolean supportsStaticField() {
        return true;
    }

    /**
     * Resolve the MyBatis component
     *
     * @param extensionContext {@link ExtensionContext}
     * @return the MyBatis component
     * @throws Exception
     */
    T resolve(ExtensionContext extensionContext) throws Exception;

    static boolean isMyBatisRuntime(AnnotatedElement annotatedElement) {
        return annotatedElement.isAnnotationPresent(MyBatisRuntime.class);
    }

    static void store(ExtensionContext context, Object component, boolean forAll) {
        Store store = getStore(context, forAll);
        store.put(component.getClass(), component);
    }

    static <T> T get(ExtensionContext context, Class<T> componentType) {
        T component = get(context, componentType, false, false);
        if (component == null) {
            component = get(context, componentType, true, false);
        }
        return component;
    }

    static <T> T get(ExtensionContext context, Class<T> componentType, boolean forAll, boolean forRemoval) {
        Store store = getStore(context, forAll);
        return forRemoval ? store.remove(componentType, componentType) : store.get(componentType, componentType);
    }

    static Store getStore(ExtensionContext context, boolean forAll) {
        Class<?> testClass = context.getRequiredTestClass();
        Namespace namespace = forAll ? create("FOR_ALL", testClass) : create("FOR_EACH", testClass);
        return context.getStore(EXTENSION_CONTEXT, namespace);
    }
}