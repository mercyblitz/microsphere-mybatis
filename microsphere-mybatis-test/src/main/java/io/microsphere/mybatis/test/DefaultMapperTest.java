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

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static java.util.Collections.emptyList;
import static org.apache.ibatis.session.RowBounds.DEFAULT;

/**
 * Default Test for MyBatis Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see AbstractMapperTest
 * @since 1.0.0
 */
public class DefaultMapperTest extends AbstractMapperTest {

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
                Connection connection = getConnection(executor);
                connection.close();
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

        });

        doInExecutor(executor -> {
            // test Executor#getTransaction
            runSafely(() -> {
                executor.close(false);
                getConnection(executor);
            });
        });

        doInExecutor(executor -> {
            // test Executor#commit
            runSafely(() -> {
                executor.close(false);
                executor.commit(true);
            });
        });

        doInExecutor(executor -> {
            // test Executor#rollback
            runSafely(() -> {
                Connection connection = getConnection(executor);
                connection.close();
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
}
