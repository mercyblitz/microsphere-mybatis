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

package io.microsphere.mybatis.spring.annotation;

import org.apache.ibatis.executor.loader.CglibProxyFactory;
import org.apache.ibatis.io.DefaultVFS;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.EnumTypeHandler;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static io.microsphere.collection.Sets.ofSet;
import static io.microsphere.mybatis.spring.annotation.MyBatisConfigurationBeanDefintionRegistrar.CONFIGURATION_BEAN_NAME;
import static io.microsphere.spring.test.util.SpringTestUtils.testInSpringContainer;
import static org.apache.ibatis.session.AutoMappingBehavior.NONE;
import static org.apache.ibatis.session.AutoMappingUnknownColumnBehavior.WARNING;
import static org.apache.ibatis.session.ExecutorType.REUSE;
import static org.apache.ibatis.session.LocalCacheScope.STATEMENT;
import static org.apache.ibatis.type.JdbcType.UNDEFINED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link MyBatisConfiguration} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MyBatisConfiguration
 * @since 1.0.0
 */
@MyBatisConfiguration
public class MyBatisConfigurationTest {

    @Test
    void testDefaultConfig() {
        testInSpringContainer(context -> {
            Configuration configuration = context.getBean(CONFIGURATION_BEAN_NAME, Configuration.class);
            assertNotNull(configuration);
        }, DefaultConfig.class);
    }

    @Test
    void testFullConfig() {
        testInSpringContainer(context -> {
            Configuration configuration = context.getBean(CONFIGURATION_BEAN_NAME, Configuration.class);
            assertNotNull(configuration);
        }, FullConfig.class);
    }

    @Test
    void testDefaultAttributes() {
        MyBatisConfiguration annotation = MyBatisConfigurationTest.class.getAnnotation(MyBatisConfiguration.class);
        Configuration configuration = new Configuration();

        assertEquals(annotation.cacheEnabled(), configuration.isCacheEnabled());
        assertEquals(annotation.lazyLoadingEnabled(), configuration.isLazyLoadingEnabled());
        assertEquals(annotation.aggressiveLazyLoading(), configuration.isAggressiveLazyLoading());
        assertEquals(annotation.multipleResultSetsEnabled(), configuration.isMultipleResultSetsEnabled());
        assertEquals(annotation.useColumnLabel(), configuration.isUseColumnLabel());
        assertEquals(annotation.useGeneratedKeys(), configuration.isUseGeneratedKeys());
        assertEquals(annotation.autoMappingBehavior(), configuration.getAutoMappingBehavior());
        assertEquals(annotation.autoMappingUnknownColumnBehavior(), configuration.getAutoMappingUnknownColumnBehavior());
        assertEquals(annotation.defaultExecutorType(), configuration.getDefaultExecutorType());
        assertInt(annotation::defaultStatementTimeout, configuration::getDefaultStatementTimeout);
        assertInt(annotation::defaultFetchSize, configuration::getDefaultFetchSize);
        // assertEquals(annotation.defaultResultSetType(), configuration.getDefaultResultSetType());
        assertEquals(annotation.safeRowBoundsEnabled(), configuration.isSafeRowBoundsEnabled());
        assertEquals(annotation.safeResultHandlerEnabled(), configuration.isSafeResultHandlerEnabled());
        assertEquals(annotation.mapUnderscoreToCamelCase(), configuration.isMapUnderscoreToCamelCase());
        assertEquals(annotation.localCacheScope(), configuration.getLocalCacheScope());
        assertEquals(annotation.jdbcTypeForNull(), configuration.getJdbcTypeForNull());
        assertEquals(ofSet(annotation.lazyLoadTriggerMethods()), configuration.getLazyLoadTriggerMethods());
        // assertEquals(annotation.defaultScriptingLanguage(), configuration.getDefaultScriptingLanguageInstance());
        // assertEquals(annotation.defaultEnumTypeHandler(), configuration.getEnvironment());
        assertEquals(annotation.callSettersOnNulls(), configuration.isCallSettersOnNulls());
        assertEquals(annotation.returnInstanceForEmptyRow(), configuration.isReturnInstanceForEmptyRow());
        // null
        // assertEquals(annotation.logPrefix(), configuration.getLogPrefix());
        // null
        // assertEquals(annotation.logImpl(), configuration.getLogImpl());
        assertEquals(annotation.proxyFactory(), configuration.getProxyFactory().getClass());
        // null
        // assertEquals(annotation.vfsImpl(), configuration.getVfsImpl());
        assertEquals(annotation.useActualParamName(), configuration.isUseActualParamName());
        // null
        // assertEquals(annotation.configurationFactory(), configuration.getConfigurationFactory());
        assertEquals(annotation.shrinkWhitespacesInSql(), configuration.isShrinkWhitespacesInSql());
        // null
        // assertEquals(annotation.defaultSqlProviderType(), configuration.getDefaultSqlProviderType());
        assertEquals(annotation.nullableOnForEach(), configuration.isNullableOnForEach());
        assertEquals(annotation.argNameBasedConstructorAutoMapping(), configuration.isArgNameBasedConstructorAutoMapping());
        // null
        // assertEquals(annotation.variables(), configuration.getParameterMapNames());
        // null
        // assertEquals(annotation.databaseId(), configuration.getDatabaseId());

    }

    void assertInt(Supplier<? extends Number> expected, Supplier<? extends Number> actual) {
        int expectedInt = expected.get().intValue();
        Number actualNumber = actual.get();
        if (expectedInt == -1) {
            assertNull(actualNumber);
        } else {
            int actualInt = actualNumber.intValue();
            assertEquals(expectedInt, actualInt);
        }
    }

    @MyBatisConfiguration
    static class DefaultConfig {
    }

    @MyBatisConfiguration(
            cacheEnabled = false,
            lazyLoadingEnabled = true,
            aggressiveLazyLoading = true,
            multipleResultSetsEnabled = false,
            useColumnLabel = false,
            useGeneratedKeys = true,
            autoMappingBehavior = NONE,
            autoMappingUnknownColumnBehavior = WARNING,
            defaultExecutorType = REUSE,
            defaultStatementTimeout = 10,
            defaultFetchSize = 1,
            safeRowBoundsEnabled = true,
            safeResultHandlerEnabled = false,
            mapUnderscoreToCamelCase = true,
            localCacheScope = STATEMENT,
            jdbcTypeForNull = UNDEFINED,
            lazyLoadTriggerMethods = "equals",
            defaultScriptingLanguage = XMLLanguageDriver.class,
            defaultEnumTypeHandler = EnumTypeHandler.class,
            callSettersOnNulls = true,
            returnInstanceForEmptyRow = true,
            logPrefix = "test-",
            logImpl = Slf4jImpl.class,
            proxyFactory = CglibProxyFactory.class,
            vfsImpl = DefaultVFS.class,
            useActualParamName = false,
            configurationFactory = Object.class,
            shrinkWhitespacesInSql = true,
            defaultSqlProviderType = Object.class,
            nullableOnForEach = true,
            argNameBasedConstructorAutoMapping = true,
            variables = {
                    "name = value"
            },
            databaseId = "test-database"
    )
    static class FullConfig {

    }
}