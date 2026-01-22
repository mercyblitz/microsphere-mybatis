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

import io.microsphere.mybatis.spring.test.config.MyBatisDataBaseTestConfiguration;
import io.microsphere.mybatis.spring.test.config.MyBatisDataSourceTestConfiguration;
import io.microsphere.mybatis.test.mapper.ChildMapper;
import io.microsphere.mybatis.test.mapper.FatherMapper;
import io.microsphere.mybatis.test.mapper.UserMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.io.Serializable;

import static io.microsphere.mybatis.spring.annotation.MyBatisBeanDefinitionRegistrar.SQL_SESSION_FACTORY_BEAN_NAME;
import static io.microsphere.mybatis.spring.annotation.MyBatisBeanDefinitionRegistrar.SQL_SESSION_TEMPLATE_BEAN_NAME;
import static io.microsphere.mybatis.test.AbstractMapperTest.assertChildMapper;
import static io.microsphere.mybatis.test.AbstractMapperTest.assertFatherMapper;
import static io.microsphere.mybatis.test.AbstractMapperTest.assertUserMapper;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.CONFIG_RESOURCE_NAME;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.EMPTY_CONFIG_RESOURCE_NAME;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.assertConfiguration;
import static io.microsphere.spring.test.util.SpringTestUtils.testInSpringContainer;
import static org.apache.ibatis.session.ExecutorType.REUSE;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.beans.factory.BeanFactory.FACTORY_BEAN_PREFIX;

/**
 * {@link EnableMyBatis} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableMyBatis
 * @see MyBatisBeanDefinitionRegistrar
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
        testInSpringContainer(this::assertTest, MapperConfig2.class);
    }

    @Test
    void testConfigurationPropetiesConfig() {
        testInSpringContainer(this::assertTest, ConfigurationPropetiesConfig.class);
        testInSpringContainer(this::assertTest, ConfigurationPropetiesConfig2.class);
        assertThrows(IllegalArgumentException.class, () -> {
            new AnnotationConfigApplicationContext(InvaidConfigurationPropetiesConfig.class);
        });
    }

    @Test
    void testMultipleDataSourceConfig() {
        testInSpringContainer(this::assertTest, MultipleDataSourceConfig.class);
    }

    @EnableMyBatis(configLocation = CONFIG_RESOURCE_NAME)
    @Import(value = {MyBatisDataSourceTestConfiguration.class, MyBatisDataBaseTestConfiguration.class})
    static class DefaultConfig {
    }

    @EnableMyBatis(configLocation = "not-found.xml", checkConfigLocation = true)
    static class NotFoundConfig {
    }

    @EnableMyBatis(dataSource = "dataSource",
            configLocation = CONFIG_RESOURCE_NAME,
            checkConfigLocation = true,
            objectWrapperFactory = "",
            databaseIdProvider = "",
            cache = "",
            plugins = {},
            typeHandlers = {""},
            scriptingLanguageDrivers = {"", " "}
    )
    @Import(value = {MyBatisDataSourceTestConfiguration.class, MyBatisDataBaseTestConfiguration.class})
    static class DataSourceConfig {
    }

    @EnableMyBatis(
            dataSource = "dataSource",
            configLocation = EMPTY_CONFIG_RESOURCE_NAME,
            mapperLocations = {
                    "META-INF/mybatis/UserMapper.xml",
                    "META-INF/mybatis/ChildMapper.xml",
                    "META-INF/mybatis/FatherMapper.xml"
            },
            typeAliasesPackage = "io.microsphere.mybatis.test.entity",
            typeHandlersPackage = {
                    "",
                    " "
            }
    )
    @Import(value = {MyBatisDataSourceTestConfiguration.class, MyBatisDataBaseTestConfiguration.class})
    static class MapperConfig {
    }

    @EnableMyBatis(
            configLocation = EMPTY_CONFIG_RESOURCE_NAME,
            mapperLocations = {
                    "${user-mapper-resource}",
                    "${child-mapper-resource}",
                    "${father-mapper-resource}"
            },
            typeAliasesPackage = "io.microsphere.mybatis.test.entity",
            typeAliasesSuperType = Serializable.class,
            typeHandlersPackage = "${not-found:}",
            executorType = REUSE
    )
    @PropertySource(value = "classpath:META-INF/mybatis/mybatis.properties")
    @Import(value = {MyBatisDataSourceTestConfiguration.class, MyBatisDataBaseTestConfiguration.class})
    static class MapperConfig2 {
    }

    @EnableMyBatis(
            configLocation = CONFIG_RESOURCE_NAME,
            configurationProperties = {
                    "jdbc.driver = org.h2.Driver",
                    "jdbc.url = jdbc:h2:mem:test_mem",
                    "jdbc.username = sa",
                    "jdbc.password ="
            }
    )
    @PropertySource(value = "classpath:META-INF/mybatis/mybatis.properties")
    @Import(value = {HardCodeDataSourceConfiguration.class, MyBatisDataBaseTestConfiguration.class})
    static class ConfigurationPropetiesConfig {
    }

    @EnableMyBatis(
            configLocation = EMPTY_CONFIG_RESOURCE_NAME,
            mapperLocations = {
                    "${user-mapper-resource}",
                    "${child-mapper-resource}",
                    "${father-mapper-resource}"
            },
            typeAliasesPackage = "io.microsphere.mybatis.test.entity",
            configurationPropertiesImportPropertySources = true
    )
    @PropertySource(value = "classpath:META-INF/mybatis/mybatis.properties")
    @Import(value = {HardCodeDataSourceConfiguration.class, MyBatisDataBaseTestConfiguration.class})
    static class ConfigurationPropetiesConfig2 {
    }

    @EnableMyBatis(
            configLocation = EMPTY_CONFIG_RESOURCE_NAME,
            configurationProperties = {"a"}
    )
    static class InvaidConfigurationPropetiesConfig {
    }


    @EnableMyBatis(
            configLocation = CONFIG_RESOURCE_NAME
    )
    @Import(value = {
            MyBatisDataSourceTestConfiguration.class,
            HardCodeDataSourceConfiguration.class,
            MyBatisDataBaseTestConfiguration.class
    })
    static class MultipleDataSourceConfig {
    }

    static class HardCodeDataSourceConfiguration {

        @Bean(initMethod = "forceCloseAll")
        public DataSource hardCodeDataSource() {
            PooledDataSource pooledDataSource = new PooledDataSource();
            pooledDataSource.setDriver("org.h2.Driver");
            pooledDataSource.setUrl("jdbc:h2:mem:test_mem");
            pooledDataSource.setUsername("sa");
            pooledDataSource.setPassword("");
            return pooledDataSource;
        }
    }

    private SqlSessionFactoryBean getSqlSessionFactoryBean(ConfigurableApplicationContext context) {
        return context.getBean(FACTORY_BEAN_PREFIX + SQL_SESSION_FACTORY_BEAN_NAME, SqlSessionFactoryBean.class);
    }

    private SqlSessionTemplate getSqlSessionTemplate(ConfigurableApplicationContext context) {
        return context.getBean(SQL_SESSION_TEMPLATE_BEAN_NAME, SqlSessionTemplate.class);
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


        SqlSessionTemplate sqlSessionTemplate = getSqlSessionTemplate(context);

        assertSame(configuration, sqlSessionTemplate.getConfiguration());
        assertSame(sqlSessionFactory, sqlSessionTemplate.getSqlSessionFactory());

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = configuration.getMapper(UserMapper.class, sqlSession);
            assertUserMapper(userMapper);

            ChildMapper childMapper = configuration.getMapper(ChildMapper.class, sqlSession);
            assertChildMapper(childMapper);

            FatherMapper fatherMapper = configuration.getMapper(FatherMapper.class, sqlSession);
            assertFatherMapper(fatherMapper);
        }
    }
}
