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
package io.microsphere.mybatis.executor;

import io.microsphere.logging.Logger;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * {@link InterceptingExecutor} for Logging with debug level
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see InterceptingExecutor
 * @since 1.0.0
 */
public class LogggingExecutorInterceptor implements ExecutorInterceptor {

    private final static Logger logger = getLogger(LogggingExecutorInterceptor.class);

    @Override
    public void beforeUpdate(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter) {
        logger.debug("beforeUpdate() : {} , {} , {} , {}", executor, properties, parameter, ms, parameter);
    }

    @Override
    public void afterUpdate(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter, @Nullable Integer result, @Nullable SQLException failure) {
        logger.debug("afterUpdate() : {} , {} , {} , {} , {} , {}", executor, properties, ms, parameter, result, failure);
    }

    @Override
    public void beforeQuery(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, @Nullable CacheKey cacheKey, @Nullable BoundSql boundSql) {
        logger.debug("beforeQuery() : {} , {} , {} , {} , {} , {} , {} , {}", executor, properties, ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
    }

    @Override
    public <E> void afterQuery(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, @Nullable CacheKey cacheKey, @Nullable BoundSql boundSql, @Nullable List<E> result, @Nullable SQLException failure) {
        logger.debug("afterQuery() : {} , {} , {} , {} , {} , {} , {} , {} , {} , {}", executor, properties, ms, parameter, rowBounds, resultHandler, cacheKey, boundSql, result, failure);
    }

    @Override
    public void beforeQueryCursor(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter, RowBounds rowBounds) {
        logger.debug("beforeQueryCursor() : {} , {} , {} , {} , {}", executor, properties, ms, parameter, rowBounds);
    }

    @Override
    public <E> void afterQueryCursor(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter, RowBounds rowBounds, @Nullable Cursor<E> result, @Nullable SQLException failure) {
        logger.debug("afterQueryCursor() : {} , {} , {} , {} , {} , {} , {}", executor, properties, parameter, ms, parameter, rowBounds, result, failure);
    }

    @Override
    public void beforeCommit(Executor executor, Map<String, String> properties, boolean required) {
        logger.debug("beforeCommit() : {} , {} , {}", executor, properties, required);
    }

    @Override
    public void afterCommit(Executor executor, Map<String, String> properties, boolean required, @Nullable SQLException failure) {
        logger.debug("afterCommit() : {} , {} , {} , {}", executor, properties, required, failure);
    }

    @Override
    public void beforeRollback(Executor executor, Map<String, String> properties, boolean required) {
        logger.debug("beforeRollback() : {} , {} , {}", executor, properties, required);
    }

    @Override
    public void afterRollback(Executor executor, Map<String, String> properties, boolean required, @Nullable SQLException failure) {
        logger.debug("afterRollback() : {} , {} , {} , {}", executor, properties, required, failure);
    }

    @Override
    public void beforeGetTransaction(Executor executor, Map<String, String> properties) {
        logger.debug("beforeGetTransaction() : {} , {}", executor, properties);
    }

    @Override
    public void afterGetTransaction(Executor executor, Map<String, String> properties) {
        logger.debug("afterGetTransaction() : {} , {}", executor, properties);
    }

    @Override
    public void beforeCreateCacheKey(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        logger.debug("beforeCreateCacheKey() : {} , {} , {} , {} , {} , {}", executor, properties, ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public void afterCreateCacheKey(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql, @Nullable CacheKey key) {
        logger.debug("afterCreateCacheKey() : {} , {} , {} , {} , {} , {} , {}", executor, properties, ms, parameterObject, rowBounds, boundSql, key);
    }

    @Override
    public void beforeDeferLoad(Executor executor, Map<String, String> properties, MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        logger.debug("beforeDeferLoad() : {} , {} , {} , {} , {} , {} , {}", executor, properties, ms, resultObject, property, key, targetType);
    }

    @Override
    public void afterDeferLoad(Executor executor, Map<String, String> properties, MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        logger.debug("afterDeferLoad() : {} , {} , {} , {} , {} , {} , {}", executor, resultObject, property, key, targetType);
    }

    @Override
    public void beforeClose(Executor executor, Map<String, String> properties, boolean forceRollback) {
        logger.debug("beforeClose() : {} , {} , {}", executor, forceRollback);
    }

    @Override
    public void afterClose(Executor executor, Map<String, String> properties, boolean forceRollback) {
        logger.debug("afterClose() : {} , {} , {}", executor, forceRollback);
    }
}
