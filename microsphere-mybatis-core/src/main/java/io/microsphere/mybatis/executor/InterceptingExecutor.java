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
import io.microsphere.mybatis.plugin.InterceptorContext;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import static io.microsphere.collection.MapUtils.isNotEmpty;
import static io.microsphere.collection.MapUtils.newHashMap;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.util.Assert.assertNotEmpty;
import static io.microsphere.util.Assert.assertNotNull;
import static java.util.Collections.emptyMap;

/**
 * Delegating {@link Executor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Executor
 * @see ExecutorInterceptor
 * @since 1.0.0
 */
public class InterceptingExecutor implements Executor {

    private static final Logger logger = getLogger(InterceptingExecutor.class);

    private final Executor delegate;

    private Map<String, String> properties = emptyMap();

    private final ExecutorInterceptor[] executorInterceptors;

    private final int executorInterceptorsCount;

    public InterceptingExecutor(Executor delegate, ExecutorInterceptor... executorInterceptors) {
        assertNotNull(delegate, () -> "The Executor executor must not be null");
        assertNotEmpty(executorInterceptors, () -> "The ExecutorInterceptor array must not be empty");
        this.delegate = delegate;
        this.executorInterceptors = executorInterceptors;
        this.executorInterceptorsCount = executorInterceptors.length;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        InterceptorContext<Executor> context = buildContext();
        beforeUpdate(context, ms, parameter);
        Integer result = null;
        SQLException failure = null;
        try {
            result = delegate.update(ms, parameter);
        } catch (SQLException e) {
            failure = e;
            throw e;
        } finally {
            afterUpdate(context, ms, parameter, result, failure);
        }
        return result;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler,
                             CacheKey cacheKey, BoundSql boundSql) throws SQLException {
        InterceptorContext<Executor> context = buildContext();
        beforeQuery(context, ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        List<E> result = null;
        SQLException failure = null;
        try {
            result = delegate.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        } catch (SQLException e) {
            failure = e;
            throw e;
        } finally {
            afterQuery(context, ms, parameter, rowBounds, resultHandler, cacheKey, boundSql, result, failure);
        }
        return result;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        InterceptorContext<Executor> context = buildContext();
        beforeQuery(context, ms, parameter, rowBounds, resultHandler, null, null);
        List<E> result = null;
        SQLException failure = null;
        try {
            result = delegate.query(ms, parameter, rowBounds, resultHandler);
        } catch (SQLException e) {
            failure = e;
            throw e;
        } finally {
            afterQuery(context, ms, parameter, rowBounds, resultHandler, null, null, result, failure);
        }
        return result;
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        InterceptorContext<Executor> context = buildContext();

        beforeQueryCursor(context, ms, parameter, rowBounds);
        Cursor<E> result = null;
        SQLException failure = null;
        try {
            result = delegate.queryCursor(ms, parameter, rowBounds);
        } catch (SQLException e) {
            failure = e;
            throw e;
        } finally {
            afterQueryCursor(context, ms, parameter, rowBounds, result, failure);
        }
        return result;
    }


    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        return delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        InterceptorContext<Executor> context = buildContext();
        beforeCommit(context, required);
        SQLException failure = null;
        try {
            delegate.commit(required);
        } catch (SQLException e) {
            failure = e;
            throw e;
        } finally {
            afterCommit(context, required, failure);
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        InterceptorContext<Executor> context = buildContext();
        beforeRollback(context, required);
        SQLException failure = null;
        try {
            delegate.rollback(required);
        } catch (SQLException e) {
            failure = e;
            throw e;
        } finally {
            afterRollback(context, required, failure);
        }
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        InterceptorContext<Executor> context = buildContext();
        beforeCreateCacheKey(context, ms, parameterObject, rowBounds, boundSql);
        CacheKey result = null;
        try {
            result = delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
        } finally {
            afterCreateCacheKey(context, ms, parameterObject, rowBounds, boundSql, result);
        }
        return result;
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return delegate.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        delegate.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        InterceptorContext<Executor> context = buildContext();
        beforeDeferLoad(context, ms, resultObject, property, key, targetType);
        try {
            delegate.deferLoad(ms, resultObject, property, key, targetType);
        } finally {
            afterDeferLoad(context, ms, resultObject, property, key, targetType);
        }
    }

    @Override
    public Transaction getTransaction() {
        InterceptorContext<Executor> context = buildContext();
        beforeGetTransaction(context);
        Transaction transaction = null;
        try {
            transaction = delegate.getTransaction();
        } finally {
            afterGetTransaction(context);
        }
        return transaction;
    }

    @Override
    public void close(boolean forceRollback) {
        InterceptorContext<Executor> context = buildContext();
        beforeClose(context, forceRollback);
        try {
            delegate.close(forceRollback);
        } finally {
            afterClose(context, forceRollback);
        }
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
        if (executor instanceof InterceptingExecutor) {
            return;
        }
        InterceptingExecutor interceptingExecutor = new InterceptingExecutor(executor, this.executorInterceptors);
        Properties properties = new Properties();
        properties.putAll(this.properties);
        interceptingExecutor.setProperties(properties);
        delegate.setExecutorWrapper(interceptingExecutor);
    }

    public void setProperties(Properties properties) {
        if (isNotEmpty(properties)) {
            this.properties = (Map) newHashMap(properties);
        }
    }

    void beforeUpdate(InterceptorContext<Executor> context, MappedStatement ms, Object parameter) {
        iterate(executorInterceptor -> executorInterceptor.beforeUpdate(context, ms, parameter));
    }

    void afterUpdate(InterceptorContext<Executor> context, MappedStatement ms, Object parameter,
                     @Nullable Integer result, @Nullable SQLException failure) {
        iterate(executorInterceptor -> executorInterceptor.afterUpdate(context, ms, parameter, result, failure));
    }

    void beforeQuery(InterceptorContext<Executor> context, MappedStatement ms, Object parameter,
                     RowBounds rowBounds, ResultHandler resultHandler, @Nullable CacheKey cacheKey, @Nullable BoundSql boundSql) {
        iterate(executorInterceptor ->
                executorInterceptor.beforeQuery(context, ms, parameter, rowBounds, resultHandler, cacheKey, boundSql));
    }

    <E> void afterQuery(InterceptorContext<Executor> context, MappedStatement ms, Object parameter,
                        RowBounds rowBounds, ResultHandler resultHandler, @Nullable CacheKey cacheKey, @Nullable BoundSql boundSql,
                        @Nullable List<E> result, @Nullable SQLException failure) {
        iterate(executorInterceptor ->
                executorInterceptor.afterQuery(context, ms, parameter, rowBounds, resultHandler, cacheKey, boundSql, result, failure));
    }

    void beforeQueryCursor(InterceptorContext<Executor> context, MappedStatement ms, Object parameter, RowBounds rowBounds) {
        iterate(executorInterceptor ->
                executorInterceptor.beforeQueryCursor(context, ms, parameter, rowBounds));
    }

    <E> void afterQueryCursor(InterceptorContext<Executor> context, MappedStatement ms, Object parameter,
                              RowBounds rowBounds, @Nullable Cursor<E> result, @Nullable SQLException failure) {
        iterate(executorInterceptor ->
                executorInterceptor.afterQueryCursor(context, ms, parameter, rowBounds, result, failure));
    }

    void beforeCommit(InterceptorContext<Executor> context, boolean required) {
        iterate(executorInterceptor -> executorInterceptor.beforeCommit(context, required));
    }

    void afterCommit(InterceptorContext<Executor> context, boolean required, @Nullable SQLException failure) {
        iterate(executorInterceptor -> executorInterceptor.afterCommit(context, required, failure));
    }

    void beforeRollback(InterceptorContext<Executor> context, boolean required) {
        iterate(executorInterceptor -> executorInterceptor.beforeRollback(context, required));
    }

    void afterRollback(InterceptorContext<Executor> context, boolean required, @Nullable SQLException failure) {
        iterate(executorInterceptor -> executorInterceptor.afterRollback(context, required, failure));
    }

    void beforeGetTransaction(InterceptorContext<Executor> context) {
        iterate(executorInterceptor -> executorInterceptor.beforeGetTransaction(context));
    }

    void afterGetTransaction(InterceptorContext<Executor> context) {
        iterate(executorInterceptor -> executorInterceptor.afterGetTransaction(context));
    }

    void beforeCreateCacheKey(InterceptorContext<Executor> context, MappedStatement ms, Object parameterObject,
                              RowBounds rowBounds, BoundSql boundSql) {
        iterate(executorInterceptor ->
                executorInterceptor.beforeCreateCacheKey(context, ms, parameterObject, rowBounds, boundSql));
    }

    void afterCreateCacheKey(InterceptorContext<Executor> context, MappedStatement ms, Object parameterObject,
                             RowBounds rowBounds, BoundSql boundSql, @Nullable CacheKey result) {
        iterate(executorInterceptor ->
                executorInterceptor.afterCreateCacheKey(context, ms, parameterObject, rowBounds, boundSql, result));
    }

    void beforeDeferLoad(InterceptorContext<Executor> context, MappedStatement ms, MetaObject resultObject, String property,
                         CacheKey key, Class<?> targetType) {
        iterate(executorInterceptor ->
                executorInterceptor.beforeDeferLoad(context, ms, resultObject, property, key, targetType));
    }

    void afterDeferLoad(InterceptorContext<Executor> context, MappedStatement ms, MetaObject resultObject,
                        String property, CacheKey key, Class<?> targetType) {
        iterate(executorInterceptor ->
                executorInterceptor.afterDeferLoad(context, ms, resultObject, property, key, targetType));
    }

    void beforeClose(InterceptorContext<Executor> context, boolean forceRollback) {
        iterate(executorInterceptor -> executorInterceptor.beforeClose(context, forceRollback));
    }

    void afterClose(InterceptorContext<Executor> context, boolean forceRollback) {
        iterate(executorInterceptor -> executorInterceptor.afterClose(context, forceRollback));
    }

    void iterate(Consumer<ExecutorInterceptor> executorInterceptorConsumer) {
        for (int i = 0; i < executorInterceptorsCount; i++) {
            ExecutorInterceptor executorInterceptor = executorInterceptors[i];
            try {
                executorInterceptorConsumer.accept(executorInterceptor);
            } catch (Throwable e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("The ExecutorInterceptor[ index : {} , class : {}] execution is failed",
                            i, executorInterceptor.getClass().getName(), e);
                }
            }
        }
    }

    private InterceptorContext<Executor> buildContext() {
        return new InterceptorContext<>(delegate, properties);
    }

}
