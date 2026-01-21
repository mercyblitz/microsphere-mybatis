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

import io.microsphere.logging.Logger;
import io.microsphere.spring.context.annotation.BeanCapableImportCandidate;
import io.microsphere.spring.core.annotation.ResolvablePlaceholderAnnotationAttributes;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

import static io.micrometer.common.util.StringUtils.isBlank;
import static io.microsphere.constants.SymbolConstants.EQUAL;
import static io.microsphere.constants.SymbolConstants.WILDCARD;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.spring.beans.factory.support.BeanRegistrar.registerBeanDefinition;
import static io.microsphere.spring.core.annotation.ResolvablePlaceholderAnnotationAttributes.of;
import static io.microsphere.util.ArrayUtils.arrayToString;
import static io.microsphere.util.ArrayUtils.length;
import static io.microsphere.util.ExceptionUtils.create;
import static io.microsphere.util.StringUtils.split;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

/**
 * {@link ImportBeanDefinitionRegistrar} class for {@link EnableMyBatis}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableMyBatis
 * @see BeanCapableImportCandidate
 * @see SqlSessionFactoryBean
 * @see SqlSessionFactory
 * @see SqlSessionTemplate
 * @since 1.0.0
 */
class MyBatisBeanDefinitionRegistrar extends BeanCapableImportCandidate implements ImportBeanDefinitionRegistrar {

    static final Class<EnableMyBatis> ANNOTATION_CLASS = EnableMyBatis.class;

    static final String ANNOTATION_CLASS_NAME = ANNOTATION_CLASS.getName();

    /**
     * The Spring Bean name of {@link SqlSessionFactory}
     */
    public static final String SQL_SESSION_FACTORY_BEAN_NAME = "sqlSessionFactory";

    /**
     * The Spring Bean name of {@link SqlSessionTemplate}
     */
    public static final String SQL_SESSION_TEMPLATE_BEAN_NAME = "sqlSessionTemplate";

    private static final Logger logger = getLogger(ANNOTATION_CLASS_NAME);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ANNOTATION_CLASS_NAME);
        ResolvablePlaceholderAnnotationAttributes attributes = of(annotationAttributes, ANNOTATION_CLASS, getEnvironment());
        // Register the BeanDefinition of SqlSessionFactoryBean if absent
        registerSqlSessionFactoryBeanIfAbsent(attributes, registry);

        // Register the BeanDefinition of SqlSessionTemplate if absent
        registerSqlSessionTemplateIfAbsent(attributes, registry);
    }

    /**
     * Register the {@link BeanDefinition} of {@link SqlSessionFactoryBean} if absent
     *
     * @param attributes {@link AnnotationAttributes}
     * @param registry   {@link BeanDefinitionRegistry}
     */
    void registerSqlSessionFactoryBeanIfAbsent(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {
        BeanDefinition beanDefinition = buildSqlSessionFactoryBeanDefinition(attributes);
        registerBeanDefinition(registry, SQL_SESSION_FACTORY_BEAN_NAME, beanDefinition);
    }

    /**
     * Register the {@link BeanDefinition} of {@link SqlSessionFactoryBean} if absent
     *
     * @param attributes {@link AnnotationAttributes}
     * @param registry   {@link BeanDefinitionRegistry}
     */
    void registerSqlSessionTemplateIfAbsent(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = genericBeanDefinition(SqlSessionTemplate.class);
        ExecutorType executorType = attributes.getEnum("executorType");
        builder.addConstructorArgReference(SQL_SESSION_FACTORY_BEAN_NAME);
        builder.addConstructorArgValue(executorType);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        registerBeanDefinition(registry, SQL_SESSION_TEMPLATE_BEAN_NAME, beanDefinition);
    }

    BeanDefinition buildSqlSessionFactoryBeanDefinition(AnnotationAttributes attributes) {

        checkConfigLocation(attributes);

        BeanDefinitionBuilder builder = genericBeanDefinition(SqlSessionFactoryBean.class);

        // References the DataSource Bean
        setBeanReference(builder, attributes, "dataSource", DataSource.class);
        // Set the attribute "configLocation"
        setBeanAttribute(builder, attributes, "configLocation");
        // Set the attribute "mapperLocations"
        setBeanAttribute(builder, attributes, "mapperLocations");
        // Set the attribute "typeAliasesPackage"
        setBeanAttribute(builder, attributes, "typeAliasesPackage");
        // Set the attribute "typeAliasesSuperType"
        setBeanAttribute(builder, attributes, "typeAliasesSuperType");
        // Set the attribute "typeHandlersPackage"
        setBeanAttribute(builder, attributes, "typeHandlersPackage");
        // Set the attribute "defaultScriptingLanguageDriver"
        setBeanAttribute(builder, attributes, "defaultScriptingLanguageDriver");
        // Set the attribute "configurationProperties"
        Properties configurationProperties = resolveConfigurationProperties(attributes);
        builder.addPropertyValue("configurationProperties", configurationProperties);

        // References the ObjectWrapperFactory Bean
        setBeanReference(builder, attributes, "objectWrapperFactory", ObjectWrapperFactory.class);
        // References the DatabaseIdProvider Bean
        setBeanReference(builder, attributes, "databaseIdProvider", DatabaseIdProvider.class);
        // References the Cache Bean
        setBeanReference(builder, attributes, "cache", Cache.class);
        // References the Interceptor Beans
        setBeanReferences(builder, attributes, "plugins", Interceptor.class);
        // References the TypeHandler Beans
        setBeanReferences(builder, attributes, "typeHandlers", TypeHandler.class);
        // References the LanguageDriver Beans
        setBeanReferences(builder, attributes, "scriptingLanguageDrivers", LanguageDriver.class);

        return builder.getBeanDefinition();
    }

    private void checkConfigLocation(AnnotationAttributes attributes) {
        boolean checkConfigLocation = attributes.getBoolean("checkConfigLocation");
        if (checkConfigLocation) {
            String configLocation = attributes.getString("configLocation");
            ResourceLoader resourceLoader = getResourceLoader();
            Resource resource = resourceLoader.getResource(configLocation);
            if (!resource.exists()) {
                throw create(IllegalArgumentException.class, "The resource can't be found by the attribute 'configLocation' : '{}'", configLocation);
            }
        }
    }

    private Properties resolveConfigurationProperties(AnnotationAttributes attributes) {
        String[] configurationProperties = attributes.getStringArray("configurationProperties");
        Properties properties = new Properties(configurationProperties.length);
        for (String configurationProperty : configurationProperties) {
            String[] keyAndValue = split(configurationProperty, EQUAL);
            properties.setProperty(keyAndValue[0], keyAndValue[1]);
        }
        return properties;
    }

    void setBeanAttribute(BeanDefinitionBuilder builder, AnnotationAttributes attributes, String attributeName) {
        Object attributeValue = attributes.get(attributeName);
        builder.addPropertyValue(attributeName, attributeValue);
    }

    void setBeanReference(BeanDefinitionBuilder builder, AnnotationAttributes attributes, String attributeName, Class<?> beanType) {
        String beanName = attributes.getString(attributeName);
        logger.trace("Try to reference a Spring Bean[{}, name : '{}'] by the attribute[name : '{}']", beanType, beanName, attributeName);
        setBeanReference(builder, attributeName, beanName, beanType);
    }

    void setBeanReferences(BeanDefinitionBuilder builder, AnnotationAttributes attributes, String attributeName, Class<?> beanType) {
        String[] beanNames = attributes.getStringArray(attributeName);
        logger.trace("Try to reference a Spring Beans[{}, names : '{}'] by the attribute[name : '{}']", beanType, arrayToString(beanNames), attributeName);
        int length = length(beanNames);
        if (length == 0) {
            logger.debug("No Spring Bean was speicified by the attribute[name : '{}']", attributeName);
        } else {
            for (int i = 0; i < length; i++) {
                String beanName = beanNames[i];
                setBeanReference(builder, attributeName, beanName, beanType);
            }
        }
    }

    void setBeanReference(BeanDefinitionBuilder builder, String attributeName, String beanName, Class<?> beanType) {
        if (isBlank(beanName)) {
            logger.debug("No Spring Bean[{}] was speicified by the attribute[name : '{}']", beanType, attributeName);
        } else if (WILDCARD.equals(beanName)) {
            builder.addPropertyValue(attributeName, new RuntimeBeanReference(beanType));
        } else {
            builder.addPropertyValue(attributeName, new RuntimeBeanNameReference(beanName));
        }
    }
}