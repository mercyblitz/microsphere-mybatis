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

import io.microsphere.lang.function.ThrowableAction;
import io.microsphere.lang.function.ThrowableConsumer;
import io.microsphere.logging.Logger;
import io.microsphere.mybatis.test.entity.Child;
import io.microsphere.mybatis.test.entity.User;
import io.microsphere.mybatis.test.mapper.UserMapper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static java.util.Collections.emptyList;
import static org.apache.ibatis.io.Resources.getResourceAsReader;
import static org.apache.ibatis.session.RowBounds.DEFAULT;
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

    protected final Logger logger = getLogger(this.getClass());

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
        runScript("META-INF/sql/create-db.sql");
    }

    protected void doInExecutor(ThrowableConsumer<Executor> consumer) throws Throwable {
        doInConnection(connection -> {
            Configuration configuration = getConfiguration();
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

    protected void doInConnection(ThrowableConsumer<Connection> consumer) throws Throwable {
        doInSqlSession(sqlSession -> consumer.accept(sqlSession.getConnection()));
    }

    protected <M> void doInMapper(Class<M> mapperClass, ThrowableConsumer<M> mapperConsumer) throws Throwable {
        doInSqlSession(sqlSession -> {
            M mapper = sqlSession.getMapper(mapperClass);
            mapperConsumer.accept(mapper);
        });
    }

    protected void doInSqlSession(ThrowableConsumer<SqlSession> consumer) throws Throwable {
        SqlSession sqlSession = openSqlSession();
        try {
            consumer.accept(sqlSession);
        } finally {
            sqlSession.close();
        }
    }

    protected User createUser() {
        int id = 1;
        String name = "Mercy";
        return new User(id, name);
    }

    @Test
    public void testMapper() throws Throwable {
        getConfiguration().setCacheEnabled(false);

        doInMapper(UserMapper.class, userMapper -> {
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

    void deferLoadAfterResultHandler(SqlSession sqlSession) {
        class MyResultHandler implements ResultHandler {
            private final List<Child> children = new ArrayList<>();

            @Override
            public void handleResult(ResultContext context) {
                Child child = (Child) context.getResultObject();
                children.add(child);
            }
        }
        MyResultHandler myResultHandler = new MyResultHandler();
        sqlSession.select("io.microsphere.mybatis.test.mapper.ChildMapper.selectAll", myResultHandler);
        for (Child child : myResultHandler.children) {
            assertNotNull(child.getFather());
        }
    }

    @Test
    public void testExecutor() throws Throwable {
        doInExecutor(executor -> {
            MappedStatement ms = getMappedStatement(MS_ID_SAVE_USER);
            User user = createUser();

            // Test update
            assertEquals(1, executor.update(ms, user));

            // Test query
            ms = getMappedStatement(MS_ID_USER_BY_ID);
            List<User> users = executor.query(ms, user.getId(), new RowBounds(), Executor.NO_RESULT_HANDLER);
            assertEquals(1, users.size());
            assertEquals(users.get(0), user);
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

            // Test deferLoad
            deferLoadAfterResultHandler(sqlSession);

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
    public void testOnFailed() throws Throwable {
        // test Executor#update
        doInExecutor(executor -> {

            // test Executor#update
            runSafely(() -> {
                MappedStatement ms = getMappedStatement(MS_ID_SAVE_USER);
                executor.update(ms, null);
            });

            // test Executor#query
            runSafely(() -> {
                MappedStatement ms = getMappedStatement(MS_ID_USER_BY_ID);
                executor.query(ms, null, DEFAULT, Executor.NO_RESULT_HANDLER);
            });

            runSafely(() -> {
                MappedStatement ms = getMappedStatement(MS_ID_USER_BY_ID);
                BoundSql boundSql = new BoundSql(getConfiguration(), MS_ID_USER_BY_ID, emptyList(), null);

                CacheKey cacheKey = executor.createCacheKey(ms, null, new RowBounds(), boundSql);
                executor.query(ms, null, DEFAULT, Executor.NO_RESULT_HANDLER, cacheKey, boundSql);
            });

            // test Executor#queryCursor
            runSafely(() -> {
                MappedStatement ms = getMappedStatement(MS_ID_USER_BY_ID);
                executor.queryCursor(ms, null, DEFAULT);
            });

            // test Executor#createCacheKey
            runSafely(() -> {
                executor.createCacheKey(null, null, DEFAULT, null);
            });

            runSafely(() -> {
                executor.close(false);
                executor.createCacheKey(null, null, DEFAULT, null);
            });

            // test Executor#commit
            runSafely(() -> {
                executor.close(false);
                executor.commit(true);
            });

            // test Executor#rollback
            runSafely(() -> {
                executor.close(false);
                executor.rollback(true);
            });
        });

        doInSqlSession(sqlSession -> {
            runSafely(() -> {
                sqlSession.close();
                deferLoadAfterResultHandler(sqlSession);
            });
        });

    }

    protected void runSafely(ThrowableAction action) {
        try {
            action.execute();
        } catch (Throwable e) {
            logger.warn("error message : {}", e.getMessage());
        }
    }

    protected void runScript(String resource) throws IOException, SQLException {
        DataSource dataSource = this.getDataSource();
        runScript(dataSource, resource);
    }

    protected DataSource getDataSource() {
        return this.getEnvironment().getDataSource();
    }

    protected Environment getEnvironment() {
        return this.getConfiguration().getEnvironment();
    }

    protected MappedStatement getMappedStatement(String id) {
        return this.getConfiguration().getMappedStatement(id);
    }

    protected Configuration getConfiguration() {
        return this.sqlSessionFactory.getConfiguration();
    }

    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        try (Connection connection = ds.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runScript(runner, resource);
        }
    }

    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
        try (Reader reader = getResourceAsReader(resource)) {
            runner.runScript(reader);
        }
    }

    @AfterEach
    public void destroy() throws Throwable {
        destroyDB();
    }

    private void destroyDB() throws Throwable {
        runScript("META-INF/sql/destroy-db.sql");
    }
}



