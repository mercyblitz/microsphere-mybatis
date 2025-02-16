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
package io.microsphere.mybatis.plugin;

import io.microsphere.lang.function.ThrowableConsumer;
import io.microsphere.mybatis.executor.LogggingExecutorInterceptor;
import io.microsphere.mybatis.test.entity.User;
import io.microsphere.mybatis.test.mapper.UserMapper;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link InterceptingExecutorInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see InterceptingExecutorInterceptor
 * @since 1.0.0
 */
public class InterceptingExecutorInterceptorTest {

    private InterceptingExecutorInterceptor interceptor;

    private SqlSessionFactory sqlSessionFactory;

    @BeforeEach
    public void init() throws Throwable {
        this.interceptor = createInterceptingExecutorInterceptor();
        this.sqlSessionFactory = buildSqlSessionFactory();
        initData();
    }

    private InterceptingExecutorInterceptor createInterceptingExecutorInterceptor() {
        LogggingExecutorInterceptor loggingExecutorInterceptor = new LogggingExecutorInterceptor();
        InterceptingExecutorInterceptor interceptingExecutorInterceptor = new InterceptingExecutorInterceptor(asList(loggingExecutorInterceptor));
        Properties properties = new Properties();
        properties.setProperty("test.class", this.getClass().getName());
        interceptingExecutorInterceptor.setProperties(properties);
        return interceptingExecutorInterceptor;
    }

    private SqlSessionFactory buildSqlSessionFactory() throws IOException {
        String resource = "META-INF/mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        factory.getConfiguration().addInterceptor(this.interceptor);
        return factory;
    }

    private SqlSession openSqlSession() {
        return this.sqlSessionFactory.openSession();
    }

    private UserMapper getUserMapper(SqlSession sqlSession) throws Throwable {
        return sqlSession.getMapper(UserMapper.class);
    }

    private void initData() throws Throwable {
        executeStatement(statement -> {
            statement.execute("CREATE TABLE users (id INT, name VARCHAR(50))");
        });
    }

    private void executeStatement(ThrowableConsumer<Statement> consumer) throws Throwable {
        executeConnection(connection -> {
            Statement statement = connection.createStatement();
            try {
                consumer.accept(statement);
            } finally {
                statement.close();
            }
        });
    }

    private void executeConnection(ThrowableConsumer<Connection> consumer) throws Throwable {
        Connection connection = openSqlSession().getConnection();
        try {
            consumer.accept(connection);
        } finally {
            connection.close();
        }
    }


    @AfterEach
    public void destroy() throws Throwable {
        destroyData();
    }

    private void destroyData() throws Throwable {
        executeStatement(statement -> {
            statement.execute("DROP TABLE users");
        });
    }


    @Test
    public void testMapper() throws Throwable {
        SqlSession sqlSession = openSqlSession();
        UserMapper userMapper = getUserMapper(sqlSession);
        int id = 1;
        String name = "Mercy";
        User user = new User(id, name);
        userMapper.saveUser(user);
        User foundUser = userMapper.getUserById(1);
        assertEquals(foundUser, user);
        sqlSession.close();
    }

    @Test
    public void testSqlSession() throws Throwable {
        SqlSession sqlSession = openSqlSession();
        Cursor<User> cursor = sqlSession.selectCursor("io.microsphere.mybatis.test.mapper.UserMapper.getUserById", 1);
        assertNotNull(cursor);
        assertNotNull(sqlSession.flushStatements());
        sqlSession.commit();
        sqlSession.commit(true);
        sqlSession.rollback();
        sqlSession.rollback(true);
        sqlSession.clearCache();
        sqlSession.close();
    }
}
