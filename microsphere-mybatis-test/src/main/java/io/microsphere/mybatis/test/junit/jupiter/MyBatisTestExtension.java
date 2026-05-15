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

package io.microsphere.mybatis.test.junit.jupiter;

import io.microsphere.mybatis.test.junit.jupiter.resolver.ComponentResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.ConfigurationResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.ConnectionResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.DataSourceResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.EnvironmentResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.ExecutorResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.PropertiesResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.SqlSessionFactoryResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.SqlSessionResolver;
import io.microsphere.mybatis.test.junit.jupiter.resolver.TransactionResolver;
import io.microsphere.reflect.MemberUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.Transaction;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;

import static io.microsphere.collection.Maps.ofMap;
import static io.microsphere.lang.function.ThrowableSupplier.execute;
import static io.microsphere.mybatis.test.junit.jupiter.resolver.ComponentResolver.get;
import static io.microsphere.mybatis.test.junit.jupiter.resolver.ComponentResolver.isMyBatisRuntime;
import static io.microsphere.reflect.FieldUtils.findAllDeclaredFields;
import static io.microsphere.reflect.FieldUtils.setFieldValue;

/**
 * The JUnit Jupiter Test {@link Extension} of MyBatis
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MyBatisTest
 * @see MyBatisRuntime
 * @since 1.0.0
 */
public class MyBatisTestExtension implements BeforeAllCallback, AfterAllCallback, AfterEachCallback,
        TestInstancePostProcessor, ParameterResolver {

    private final static Map<Class<?>, ComponentResolver> componentResolversMap = ofMap(
            Configuration.class, ConfigurationResolver.INSTANCE,
            Environment.class, EnvironmentResolver.INSTANCE,
            SqlSessionFactory.class, SqlSessionFactoryResolver.INSTANCE,
            DataSource.class, DataSourceResolver.INSTANCE,
            Properties.class, PropertiesResolver.INSTANCE,
            SqlSession.class, SqlSessionResolver.INSTANCE,
            Transaction.class, TransactionResolver.INSTANCE,
            Executor.class, ExecutorResolver.INSTANCE,
            Connection.class, ConnectionResolver.INSTANCE
    );

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        ConfigurationResolver.INSTANCE.resolve(context);
        injectFields(context, null);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        close(context, true);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        injectFields(context, testInstance);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        close(context, false);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return isMyBatisRuntime(parameter)
                && getComponent(extensionContext, parameter.getType()) != null;
    }

    @Override
    public @Nullable Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();
        return getComponent(extensionContext, type);
    }

    private void injectFields(ExtensionContext extensionContext, @Nullable Object testInstance) {
        Class<?> testClass = extensionContext.getRequiredTestClass();

        boolean isStatic = testInstance == null;

        Predicate<Field> predicate = isStatic ? MemberUtils::isStatic : MemberUtils::isNonStatic;
        predicate = predicate.and(ComponentResolver::isMyBatisRuntime);

        Set<Field> allFields = findAllDeclaredFields(testClass, predicate);
        for (Field field : allFields) {
            Class<?> fieldType = field.getType();
            ComponentResolver resolver = getComponentResolver(fieldType);
            Object component = null;
            if (resolver == null) {
                if (!isStatic) {
                    component = getMapper(extensionContext, fieldType);
                }
            } else if (resolver.supportsField(field, extensionContext)) {
                component = resolveComponent(extensionContext, resolver);
            }
            setFieldValue(testInstance, field, component);
        }
    }

    private void close(ExtensionContext context, boolean forAll) throws Exception {
        Collection<ComponentResolver> componentResolvers = componentResolversMap.values();
        for (ComponentResolver componentResolver : componentResolvers) {
            Class componentType = componentResolver.getComponentType();
            Object component = null;
            if (forAll == componentResolver.supportsStaticField()) {
                component = get(context, componentType, forAll, true);
            }
            if (component instanceof AutoCloseable closeable) {
                closeable.close();
            }
        }
    }

    static ComponentResolver getComponentResolver(Class<?> componentType) {
        return componentResolversMap.get(componentType);
    }

    static <T> T getComponent(ExtensionContext context, Class<T> type) {
        T component = resolveComponent(context, type);
        return component == null ? getMapper(context, type) : component;
    }

    static <T> T resolveComponent(ExtensionContext context, Class<T> type) {
        ComponentResolver resolver = componentResolversMap.get(type);
        return resolver == null ? null : resolveComponent(context, resolver);
    }

    static <T> T resolveComponent(ExtensionContext context, ComponentResolver resolver) {
        return execute(() -> (T) resolver.resolve(context));
    }

    static <T> T getMapper(ExtensionContext context, Class<T> type) {
        return execute(() -> {
            SqlSession sqlSession = SqlSessionResolver.INSTANCE.resolve(context);
            return sqlSession.getMapper(type);
        });
    }

    public static boolean isComponentType(Class<?> type) {
        return componentResolversMap.containsKey(type);
    }
}