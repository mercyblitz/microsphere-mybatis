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

import io.microsphere.mybatis.spring.test.config.MyBatisDataSourceTestConfiguration;
import io.microsphere.mybatis.test.mapper.UserMapper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import static io.microsphere.mybatis.spring.annotation.MyBatisBeanDefinitionRegistrar.SQL_SESSION_FACTORY_BEAN_NAME;
import static io.microsphere.mybatis.test.AbstractMapperTest.assertUserMapper;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.assertConfiguration;
import static io.microsphere.spring.test.util.SpringTestUtils.testInSpringContainer;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

/**
 * {@link EnableMyBatis} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableMyBatis
 * @since 1.0.0
 */
class EnableMyBatisTest {

    @Test
    void testDefaultConfig() {
        testInSpringContainer(this::assertTest, DefaultConfig.class);
    }

    @Test
    void testNotFoundConfig() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AnnotationConfigApplicationContext(NotFoundConfig.class);
        });
    }

    @Test
    void testDataSourceConfig() {
        testInSpringContainer(this::assertTest, DataSourceConfig.class);
    }

    @Test
    void testMapperConfig() {
        testInSpringContainer(this::assertTest, MapperConfig.class);
    }


    @EnableMyBatis(configLocation = "classpath:/META-INF/mybatis/config.xml")
    @Import(MyBatisDataSourceTestConfiguration.class)
    static class DefaultConfig {
    }

    @EnableMyBatis(configLocation = "not-found.xml", checkConfigLocation = true)
    static class NotFoundConfig {
    }

    @EnableMyBatis(dataSource = "dataSource", configLocation = "classpath:/META-INF/mybatis/config.xml")
    @Import(MyBatisDataSourceTestConfiguration.class)
    static class DataSourceConfig {
    }

    @EnableMyBatis(
            dataSource = "dataSource",
            configLocation = "classpath:/META-INF/mybatis/empty-config.xml",
            mapperLocations = {
                    "META-INF/mybatis/UserMapper.xml",
                    "META-INF/mybatis/ChildMapper.xml",
                    "META-INF/mybatis/FatherMapper.xml"
            },
            typeAliasesPackage = "io.microsphere.mybatis.test.entity"
    )
    @Import(MyBatisDataSourceTestConfiguration.class)
    static class MapperConfig {
    }

    private SqlSessionFactoryBean getSqlSessionFactoryBean(ConfigurableApplicationContext context) {
        return context.getBean(FACTORY_BEAN_PREFIX + SQL_SESSION_FACTORY_BEAN_NAME, SqlSessionFactoryBean.class);
    }

    private SqlSessionFactory getSqlSessionFactory(ConfigurableApplicationContext context) {
        return context.getBean(SQL_SESSION_FACTORY_BEAN_NAME, SqlSessionFactory.class);
    }

    void assertTest(ConfigurableApplicationContext context) {
        SqlSessionFactoryBean sqlSessionFactoryBean = getSqlSessionFactoryBean(context);
        assertNull(sqlSessionFactoryBean.getDatabaseIdProvider());
        assertNull(sqlSessionFactoryBean.getCache());
        assertNull(sqlSessionFactoryBean.getVfs());

        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory(context);
        Configuration configuration = sqlSessionFactory.getConfiguration();
        assertConfiguration(configuration);

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = configuration.getMapper(UserMapper.class, sqlSession);
            assertUserMapper(userMapper);
        }
    }
}
