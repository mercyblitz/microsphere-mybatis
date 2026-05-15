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

import io.microsphere.mybatis.test.junit.jupiter.MyBatisRuntime;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static io.microsphere.mybatis.test.junit.jupiter.resolver.ComponentResolver.get;
import static io.microsphere.mybatis.test.junit.jupiter.resolver.ComponentResolver.isMyBatisRuntime;
import static io.microsphere.mybatis.test.junit.jupiter.resolver.ComponentResolver.store;
import static io.microsphere.reflect.JavaType.from;
import static io.microsphere.reflect.MemberUtils.isStatic;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

/**
 * Abstract class of {@link ComponentResolver}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ComponentResolver
 * @since 1.0.0
 */
public abstract class AbstractComponentResolver<T> implements ComponentResolver<T> {

    private static final Namespace MYBATIS = create(MyBatisRuntime.class);

    private final Class<T> componentType;

    public AbstractComponentResolver() {
        this.componentType = from(getClass())
                .as(ComponentResolver.class)
                .getGenericType(0)
                .toClass();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Parameter parameter = parameterContext.getParameter();
        return isMyBatisRuntime(parameter) && isComponentType(parameter.getType());
    }

    @Override
    public boolean supportsField(Field field, ExtensionContext extensionContext) {
        if (isMyBatisRuntime(field) && isComponentType(field.getType())) {
            if (isStatic(field)) {
                return supportsStaticField();
            }
            return true;
        }
        return false;
    }

    @Override
    public T resolve(ExtensionContext extensionContext) throws Exception {
        T component = get(extensionContext, this.componentType);
        if (component == null) {
            component = doResolve(extensionContext);
            if (supportsStaticField()) {
                store(extensionContext, component, true);
            }
            store(extensionContext, component, false);
        }
        return component;
    }

    @Override
    public Class<T> getComponentType() {
        return this.componentType;
    }

    protected abstract T doResolve(ExtensionContext extensionContext) throws Exception;

    protected boolean isComponentType(Class<?> type) {
        return this.componentType.equals(type);
    }
}