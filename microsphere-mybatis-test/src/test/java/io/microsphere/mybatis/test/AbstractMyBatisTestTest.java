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

package io.microsphere.mybatis.test;

import io.microsphere.mybatis.test.entity.Child;
import io.microsphere.mybatis.test.entity.Father;
import io.microsphere.mybatis.test.entity.User;
import io.microsphere.mybatis.test.mapper.ChildMapper;
import io.microsphere.mybatis.test.mapper.FatherMapper;
import io.microsphere.mybatis.test.mapper.UserMapper;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import static io.microsphere.mybatis.test.AbstractMyBatisTest.configuration;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.dataSource;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.runScript;
import static io.microsphere.mybatis.test.AbstractMyBatisTest.buildSqlSessionFactory;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The test class for {@link AbstractMyBatisTest}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AbstractMyBatisTest
 * @since 1.0.0
 */
class AbstractMyBatisTestTest {

    @Test
    void testConfiguration() throws IOException {
        Configuration configuration = configuration();
        assertConfiguration(configuration);
    }

    @Test
    void testDataSource() throws Exception {
        DataSource dataSource = dataSource();
        assertNotNull(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
        }
    }

    @Test
    void testBuildSqlSessionFactory() throws IOException {
        SqlSessionFactory sqlSessionFactory = buildSqlSessionFactory();
        Configuration configuration = sqlSessionFactory.getConfiguration();
        assertConfiguration(configuration);

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            assertNotNull(sqlSession);
        }
    }

    @Test
    void testRunScript() throws Exception {
        DataSource dataSource = dataSource();
        runScript(dataSource, "META-INF/sql/create-db.sql");
        runScript(dataSource, "META-INF/sql/destroy-db.sql");
    }

    void assertConfiguration(Configuration configuration) {
        assertFalse(configuration.isLazyLoadingEnabled());

        TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
        Map<String, Class<?>> typeAliases = typeAliasRegistry.getTypeAliases();
        assertSame(User.class, typeAliases.get("user"));
        assertSame(Child.class, typeAliases.get("child"));
        assertSame(Father.class, typeAliases.get("father"));

        MapperRegistry mapperRegistry = configuration.getMapperRegistry();
        Collection<Class<?>> mappers = mapperRegistry.getMappers();
        assertTrue(mappers.contains(UserMapper.class));
        assertTrue(mappers.contains(ChildMapper.class));
        assertTrue(mappers.contains(FatherMapper.class));
    }
}
