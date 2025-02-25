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
import io.microsphere.mybatis.test.entity.User;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static org.apache.ibatis.io.Resources.getResourceAsReader;

/**
 * Abstract MyBatis Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see AbstractSqlSessionTest
 * @see AbstractExecutorTest
 * @see AbstractMapperTest
 * @since 1.0.0
 */
public abstract class AbstractMyBatisTest {

    protected final Logger logger = getLogger(this.getClass());

    private SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory buildSqlSessionFactory() throws IOException {
        String resource = "META-INF/mybatis/config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);
        return factory;
    }

    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        try (Connection connection = ds.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            AbstractMyBatisTest.runScript(runner, resource);
        }
    }

    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
        try (Reader reader = getResourceAsReader(resource)) {
            runner.runScript(reader);
        }
    }

    @BeforeEach
    public void init() throws Throwable {
        this.sqlSessionFactory = createSqlSessionFactory();
        initData();
    }

    private SqlSessionFactory createSqlSessionFactory() throws IOException {
        SqlSessionFactory factory = AbstractMyBatisTest.buildSqlSessionFactory();
        customize(factory);
        customize(factory.getConfiguration());
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

    protected Connection getConnection(Executor executor) throws SQLException {
        return this.getTransaction(executor).getConnection();
    }

    protected Transaction getTransaction(Executor executor) {
        return executor.getTransaction();
    }

    protected Connection getConnection() throws SQLException {
        return this.getDataSource().getConnection();
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

    @AfterEach
    public void destroy() throws Throwable {
        destroyDB();
    }

    private void destroyDB() throws Throwable {
        runScript("META-INF/sql/destroy-db.sql");
    }
}
