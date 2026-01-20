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

import io.microsphere.constants.SymbolConstants;
import io.microsphere.util.StringUtils;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static io.microsphere.constants.SymbolConstants.WILDCARD;
import static io.microsphere.util.StringUtils.EMPTY_STRING;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.ibatis.session.ExecutorType.SIMPLE;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

/**
 * Enables Spring's annotation-driven MyBatis capability, similar to the offical
 * <a href="https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/#Configuration">MyBatis Spring Boot Starter</a>
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see MyBatisConfiguration
 * @see MapperScan
 * @see MapperScans
 * @see Configuration
 * @see SqlSessionFactoryBean
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
@Import(MyBatisBeanDefinitionRegistrar.class)
public @interface EnableMyBatis {

    /**
     * Location of MyBatis xml config file, for example,
     * {@code "classpath:/com/acme/config.xml"}
     *
     * @return non-null
     */
    String configLocation();

    /**
     * Indicates whether perform presence check of the MyBatis xml config file.
     *
     * @return <code>false</code> as default
     */
    boolean checkConfigLocation() default false;

    /**
     * Locations of Mapper xml config file, for example, {@code "classpath*:/com/acme/mapper/*.xml"} or
     * {@code "file:/path/to/mapper.xml"}.
     *
     * @return empty array as default
     */
    String[] mapperLocations() default {};

    /**
     * Packages to search for type aliases. (Package delimiters are “,; \t\n”)
     *
     * @return empty array as default
     */
    String[] typeAliasesPackage() default {};

    /**
     * The super class for filtering type alias. If this not specifies, the MyBatis deal as type alias all classes that
     * searched from {@link #typeAliasesPackage()}.
     *
     * @return empty array as default
     */
    Class[] typeAliasesSuperType() default {};

    /**
     * Packages to search for type handlers. (Package delimiters are “,; \t\n”)
     *
     * @return empty array as default
     */
    String[] typeHandlersPackage() default {};

    /**
     * Executor type: {@link ExecutorType#SIMPLE}, {@link ExecutorType#REUSE}, {@link ExecutorType#BATCH}
     *
     * @return {@link ExecutorType#SIMPLE} as default
     */
    ExecutorType executorType() default SIMPLE;

    /**
     * The default scripting language driver class. This feature requires to use together with mybatis-spring 2.0.2+.
     *
     * @return {@link LanguageDriver} as the default, indicates no {@link LanguageDriver} specified.
     */
    Class<? extends LanguageDriver> defaultScriptingLanguageDriver() default LanguageDriver.class;

    /**
     * Externalized properties for MyBatis configuration. Specified properties can be used as placeholder on MyBatis config file and Mapper file.
     * For detail see the <a href="https://mybatis.org/mybatis-3/configuration.html#properties">MyBatis reference page</a>.
     *
     * @return empty array as default
     */
    String[] configurationProperties() default {};

    /**
     * Whether enable lazy initialization of mapper bean. Set true to enable lazy initialization.
     * This feature requires to use together with mybatis-spring 2.0.2+.
     *
     * @return <code>false</code> as default
     */
    boolean lazyInitialization() default false;

    /**
     * Default scope for mapper bean that scanned by auto-configure.
     * This feature requires to use together with mybatis-spring 2.0.6+.
     *
     * @return {@link ConfigurableBeanFactory#SCOPE_SINGLETON} as default
     */
    String mapperDefaultScope() default SCOPE_SINGLETON;

    /**
     * Set whether inject a SqlSessionTemplate or SqlSessionFactory bean
     * (If you want to back to the behavior of 2.2.1 or before, specify false).
     * If you use together with spring-native, should be set true(default).
     *
     * @return <code>true</code> as default
     */
    boolean injectSqlSessionOnMapperScan() default true;

    /**
     * The Spring Bean name of {@link ObjectWrapperFactory}
     *
     * @return the empty string as default, indicates no bean specified
     * @see SqlSessionFactoryBean#setObjectWrapperFactory(ObjectWrapperFactory)
     * @see ObjectWrapperFactory
     * @since MyBatis Spring 1.1.2
     */
    String objectWrapperFactory() default EMPTY_STRING;

    /**
     * The Spring Bean name of {@link DatabaseIdProvider}
     *
     * @return the {@link SymbolConstants#WILDCARD "*"} as default, indicates any bean should be applied.
     * If the value is empty string, it indicates no bean specified.
     * @see SqlSessionFactoryBean#setDatabaseIdProvider(DatabaseIdProvider)
     * @see DatabaseIdProvider
     * @since MyBatis Spring 1.1.0
     */
    String databaseIdProvider() default WILDCARD;

    /**
     * The Spring Bean name of {@link Cache}
     *
     * @return the {@link StringUtils#EMPTY_STRING empty string} as default, indicates no bean specified
     * @see SqlSessionFactoryBean#setCache(Cache)
     * @see Cache
     * @since MyBatis Spring 1.1.0
     */
    String cache() default EMPTY_STRING;

    /**
     * The Spring Bean names of {@link Interceptor} as plugins
     *
     * @return the {@link SymbolConstants#WILDCARD "*"} as default, indicates any bean should be applied.
     * If the value is {@link StringUtils#EMPTY_STRING empty string}, it indicates no bean specified.
     * @see SqlSessionFactoryBean#setPlugins(Interceptor...)
     * @see Interceptor
     * @since MyBatis Spring 1.0.1
     */
    String[] plugins() default WILDCARD;

    /**
     * The Spring Bean names of {@link TypeHandler}
     *
     * @return the {@link SymbolConstants#WILDCARD "*"} as default, indicates any bean should be applied.
     * If the value is {@link StringUtils#EMPTY_STRING empty string}, it indicates no bean specified.
     * @see SqlSessionFactoryBean#setTypeHandlers(TypeHandler...)
     * @see TypeHandler
     * @since MyBatis Spring 1.0.1
     */
    String[] typeHandlers() default WILDCARD;

    /**
     * The Spring Bean names of {@link LanguageDriver}
     *
     * @return the {@link SymbolConstants#WILDCARD "*"} as default, indicates any bean should be applied.
     * If the value is {@link StringUtils#EMPTY_STRING empty string}, it indicates no bean specified.
     * @see SqlSessionFactoryBean#setScriptingLanguageDrivers(LanguageDriver...)
     * @see LanguageDriver
     * @since MyBatis Spring 2.0.2
     */
    String[] scriptingLanguageDrivers() default WILDCARD;
}