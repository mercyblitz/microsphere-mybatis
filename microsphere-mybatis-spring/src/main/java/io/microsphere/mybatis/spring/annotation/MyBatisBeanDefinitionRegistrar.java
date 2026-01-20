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

import io.microsphere.spring.context.annotation.BeanCapableImportCandidate;
import io.microsphere.spring.core.annotation.ResolvablePlaceholderAnnotationAttributes;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static io.microsphere.spring.core.annotation.ResolvablePlaceholderAnnotationAttributes.of;

/**
 * {@link ImportBeanDefinitionRegistrar} class for {@link EnableMyBatis}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableMyBatis
 * @see BeanCapableImportCandidate
 * @see SqlSessionFactoryBean
 * @since 1.0.0
 */
class MyBatisBeanDefinitionRegistrar extends BeanCapableImportCandidate implements ImportBeanDefinitionRegistrar {

    static final Class<EnableMyBatis> ANNOTATION_CLASS = EnableMyBatis.class;

    static final String ANNOTATION_CLASS_NAME = ANNOTATION_CLASS.getName();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ANNOTATION_CLASS_NAME);
        ResolvablePlaceholderAnnotationAttributes attributes = of(annotationAttributes, ANNOTATION_CLASS, getEnvironment());

    }
}
