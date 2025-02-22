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

import io.microsphere.lang.function.ThrowableConsumer;
import io.microsphere.mybatis.test.entity.User;
import io.microsphere.mybatis.test.mapper.UserMapper;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Abstract Test for MyBatis
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see #testMapper()
 * @see #testExecutor()
 * @see #testSqlSession()
 * @since 1.0.0
 */
public abstract class AbstractMyBatisTest {

    public static final String MS_ID_SAVE_USER = "io.microsphere.mybatis.test.mapper.UserMapper.saveUser";

    public static final String MS_ID_USER_BY_ID = "io.microsphere.mybatis.test.mapper.UserMapper.getUserById";

    public static final String MS_ID_USER_BY_NAME = "io.microsphere.mybatis.test.mapper.UserMapper.getUserByName";

    private SqlSessionFactory sqlSessionFactory;

    @BeforeEach
    public void init() throws Throwable {
        this.sqlSessionFactory = createSqlSessionFactory();
        initData();
    }

    private SqlSessionFactory createSqlSessionFactory() throws IOException {
        SqlSessionFactory factory = buildSqlSessionFactory();
        customize(factory);
        customize(factory.getConfiguration());
        return factory;
    }

    public static SqlSessionFactory buildSqlSessionFactory() throws IOException {
        String resource = "META-INF/mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        return factory;
    }

    /**
     * Customize the {@link SqlSessionFactory}
     *
     * @param sqlSessionFactory {@link SqlSessionFactory}
     */
    protected void customize(SqlSessionFactory sqlSessionFactory) {
    }

    /**
     * Customize the {@link Configuration}
     *
     * @param configuration {@link Configuration}
     */
    protected void customize(Configuration configuration) {
    }

    private SqlSession openSqlSession() {
        return this.sqlSessionFactory.openSession();
    }

    private UserMapper getUserMapper(SqlSession sqlSession) throws Throwable {
        return sqlSession.getMapper(UserMapper.class);
    }

    private void initData() throws Throwable {
        doInStatement(statement -> {
            statement.execute("CREATE TABLE users (id INT, name VARCHAR(50))");
        });
    }

    private void doInStatement(ThrowableConsumer<Statement> consumer) throws Throwable {
        doInConnection(connection -> {
            Statement statement = connection.createStatement();
            try {
                consumer.accept(statement);
            } finally {
                statement.close();
            }
        });
    }

    protected void doInConnection(ThrowableConsumer<Connection> consumer) throws Throwable {
        doInSqlSession(sqlSession -> consumer.accept(sqlSession.getConnection()));
    }

    protected void doInSqlSession(ThrowableConsumer<SqlSession> consumer) throws Throwable {
        SqlSession sqlSession = openSqlSession();
        try {
            consumer.accept(sqlSession);
        } finally {
            sqlSession.close();
        }
    }

    protected void doInExecutor(ThrowableConsumer<Executor> consumer) throws Throwable {
        doInConnection(connection -> {
            Configuration configuration = this.sqlSessionFactory.getConfiguration();
            Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            Transaction transaction = transactionFactory.newTransaction(connection);
            Executor executor = configuration.newExecutor(transaction);
            try {
                consumer.accept(executor);
            } finally {
                executor.close(false);
            }
        });
    }


    @AfterEach
    public void destroy() throws Throwable {
        destroyData();
    }

    private void destroyData() throws Throwable {
        doInStatement(statement -> {
            statement.execute("DROP TABLE users");
        });
    }

    protected User createUser() {
        int id = 1;
        String name = "Mercy";
        return new User(id, name);
    }

    @Test
    public void testMapper() throws Throwable {
        doInSqlSession(sqlSession -> {
            UserMapper userMapper = getUserMapper(sqlSession);
            User user = createUser();
            // Test saveUser
            userMapper.saveUser(user);

            // Test getUserById
            User foundUser = userMapper.getUserById(user.getId());
            assertEquals(foundUser, user);

            // Test getUserByName
            foundUser = userMapper.getUserByName(user.getName());
            assertEquals(foundUser, user);
        });
    }


    @Test
    public void testSqlSession() throws Throwable {
        doInSqlSession(sqlSession -> {

            User user = createUser();

            // Test insert
            assertEquals(1, sqlSession.insert(MS_ID_SAVE_USER, user));

            // Test selectCursor
            Cursor<User> cursor = sqlSession.selectCursor(MS_ID_USER_BY_ID, user.getId());
            assertNotNull(cursor);
            assertFalse(cursor.isOpen());
            assertFalse(cursor.isConsumed());
            assertEquals(-1, cursor.getCurrentIndex());
            cursor.forEach(foundUser -> assertEquals(foundUser, user));

            // Test selectOne
            User foundUser = sqlSession.selectOne(MS_ID_USER_BY_NAME, user.getName());
            assertEquals(foundUser, user);

            // Test selectList
            List<User> users = sqlSession.selectList(MS_ID_USER_BY_NAME, user.getName());
            assertEquals(1, users.size());
            assertEquals(users.get(0), user);

            // Test flushStatements
            assertNotNull(sqlSession.flushStatements());

            // Test commit
            sqlSession.commit();
            sqlSession.commit(true);

            // Test rollback
            sqlSession.rollback();
            sqlSession.rollback(true);

            // Test clearCache
            sqlSession.clearCache();
        });

    }

    @Test
    public void testExecutor() throws Throwable {
        doInExecutor(executor -> {
            Configuration configuration = this.sqlSessionFactory.getConfiguration();
            MappedStatement ms = configuration.getMappedStatement(MS_ID_SAVE_USER);
            User user = createUser();

            // Test update
            assertEquals(1, executor.update(ms, user));

            // Test query
            ms = configuration.getMappedStatement(MS_ID_USER_BY_ID);
            List<User> users = executor.query(ms, user.getId(), new RowBounds(), Executor.NO_RESULT_HANDLER);
            assertEquals(1, users.size());
            assertEquals(users.get(0), user);
        });
    }
}
