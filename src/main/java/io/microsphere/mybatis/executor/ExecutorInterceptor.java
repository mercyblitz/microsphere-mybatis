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

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The Interceptor of {@link Executor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Executor
 * @since 1.0.0
 */
public interface ExecutorInterceptor {

    /**
     * Callback before execute {@link Executor#update(MappedStatement, Object)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms         {@link MappedStatement}
     * @param parameter  the parameter object
     */
    default void beforeUpdate(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter) {
    }

    /**
     * Callback after execute {@link Executor#update(MappedStatement, Object)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms         {@link MappedStatement}
     * @param parameter  the parameter object
     * @param result     (optional) the result of {@link Executor#update(MappedStatement, Object)}
     * @param failure    (optional) the {@link SQLException} if occurred
     */
    default void afterUpdate(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter,
                             @Nullable Integer result, @Nullable SQLException failure) {
    }

    /**
     * Callback before execute {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler)} or
     * {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}
     *
     * @param executor      the underlying {@link Executor} instance
     * @param properties    the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms            {@link MappedStatement}
     * @param parameter     the parameter object
     * @param rowBounds     {@link RowBounds}
     * @param resultHandler {@link ResultHandler}
     * @param cacheKey      (optional) {@link CacheKey}
     * @param boundSql      (optional) {@link BoundSql}
     */
    default void beforeQuery(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter,
                             RowBounds rowBounds, ResultHandler resultHandler, @Nullable CacheKey cacheKey, @Nullable BoundSql boundSql) {
    }

    /**
     * Callback after execute {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler)} or
     * {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}
     *
     * @param executor      the underlying {@link Executor} instance
     * @param properties    the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms            {@link MappedStatement}
     * @param parameter     the parameter object
     * @param rowBounds     {@link RowBounds}
     * @param resultHandler {@link ResultHandler}
     * @param cacheKey      (optional) {@link CacheKey}
     * @param boundSql      (optional) {@link BoundSql}
     * @param result        (optional) the result of {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler)} or
     *                      {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}
     * @param failure       (optional) the {@link SQLException} if occurred
     * @param <E>           the type of result
     */
    default <E> void afterQuery(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter,
                                RowBounds rowBounds, ResultHandler resultHandler, @Nullable CacheKey cacheKey, @Nullable BoundSql boundSql,
                                @Nullable List<E> result, @Nullable SQLException failure) {
    }

    /**
     * Callback before execute {@link Executor#queryCursor(MappedStatement, Object, RowBounds)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms         {@link MappedStatement}
     * @param parameter  the parameter object
     * @param rowBounds  {@link RowBounds}
     */
    default void beforeQueryCursor(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter, RowBounds rowBounds) {
    }

    /**
     * Callback after execute {@link Executor#queryCursor(MappedStatement, Object, RowBounds)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms         {@link MappedStatement}
     * @param parameter  the parameter object
     * @param rowBounds  {@link RowBounds}
     * @param result     (optional) the result of {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler)} or
     *                   {@link Executor#query(MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}
     * @param failure    (optional) the {@link SQLException} if occurred
     * @param <E>        the type of result
     */
    default <E> void afterQueryCursor(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameter,
                                      RowBounds rowBounds, @Nullable Cursor<E> result, @Nullable SQLException failure) {
    }

    /**
     * Callback before execute {@link Executor#commit(boolean)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param required   <code>true</code> means the transaction will be {@link Transaction#commit() committed} really,
     *                   otherwise ignored
     */
    default void beforeCommit(Executor executor, Map<String, String> properties, boolean required) {
    }

    /**
     * Callback after execute {@link Executor#commit(boolean)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param required   <code>true</code> means the transaction will be {@link Transaction#commit() committed} really,
     *                   otherwise ignored
     */
    default void afterCommit(Executor executor, Map<String, String> properties, boolean required, @Nullable SQLException failure) {
    }

    /**
     * Callback before execute {@link Executor#rollback(boolean)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param required   <code>true</code> means the transaction will be {@link Transaction#rollback() rollback}, otherwise ignored
     */
    default void beforeRollback(Executor executor, Map<String, String> properties, boolean required) {
    }

    /**
     * Callback after execute {@link Executor#rollback(boolean)}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param required   <code>true</code> means the transaction will be {@link Transaction#rollback() rollback}, otherwise ignored
     */
    default void afterRollback(Executor executor, Map<String, String> properties, boolean required, @Nullable SQLException failure) {
    }

    /**
     * Callback before execute {@link Executor#getTransaction()}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     */
    default void beforeGetTransaction(Executor executor, Map<String, String> properties) {
    }

    /**
     * Callback after execute {@link Executor#getTransaction()}
     *
     * @param executor   the underlying {@link Executor} instance
     * @param properties the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     */
    default void afterGetTransaction(Executor executor, Map<String, String> properties) {
    }

    /**
     * Callback before execute {@link Executor#createCacheKey(MappedStatement, Object, RowBounds, BoundSql)}
     *
     * @param executor        the underlying {@link Executor} instance
     * @param properties      the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms              {@link MappedStatement}
     * @param parameterObject the parameter object
     * @param rowBounds       {@link RowBounds}
     * @param boundSql        {@link BoundSql}
     */
    default void beforeCreateCacheKey(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameterObject,
                                      RowBounds rowBounds, BoundSql boundSql) {
    }

    /**
     * Callback after execute {@link Executor#createCacheKey(MappedStatement, Object, RowBounds, BoundSql)}
     *
     * @param executor        the underlying {@link Executor} instance
     * @param properties      the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms              {@link MappedStatement}
     * @param parameterObject the parameter object
     * @param rowBounds       {@link RowBounds}
     * @param boundSql        {@link BoundSql}
     * @param key             {@link CacheKey}
     */
    default void afterCreateCacheKey(Executor executor, Map<String, String> properties, MappedStatement ms, Object parameterObject,
                                     RowBounds rowBounds, BoundSql boundSql, @Nullable CacheKey key) {
    }

    /**
     * Callback before execute {@link Executor#deferLoad(MappedStatement, MetaObject, String, CacheKey, Class)}
     *
     * @param executor     the underlying {@link Executor} instance
     * @param properties   the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms           {@link MappedStatement}
     * @param resultObject {@link MetaObject}
     * @param property     {@link RowBounds}
     * @param key          {@link CacheKey}
     * @param targetType   the target type
     */
    default void beforeDeferLoad(Executor executor, Map<String, String> properties, MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
    }

    /**
     * Callback after execute {@link Executor#deferLoad(MappedStatement, MetaObject, String, CacheKey, Class)}
     *
     * @param executor     the underlying {@link Executor} instance
     * @param properties   the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param ms           {@link MappedStatement}
     * @param resultObject {@link MetaObject}
     * @param property     {@link RowBounds}
     * @param key          {@link CacheKey}
     * @param targetType   the target type
     */
    default void afterDeferLoad(Executor executor, Map<String, String> properties, MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
    }

    /**
     * Callback before execute {@link Executor#close(boolean)}
     *
     * @param executor      the underlying {@link Executor} instance
     * @param properties    the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param forceRollback <code>true</code> means the transaction will be {@link Transaction#rollback() rollback}
     */
    default void beforeClose(Executor executor, Map<String, String> properties, boolean forceRollback) {
    }

    /**
     * Callback after execute {@link Executor#close(boolean)}
     *
     * @param executor      the underlying {@link Executor} instance
     * @param properties    the copy {@link Map} of {@link Interceptor#setProperties(Properties)}
     * @param forceRollback <code>true</code> means the transaction will be {@link Transaction#rollback() rollback}
     */
    default void afterClose(Executor executor, Map<String, String> properties, boolean forceRollback) {
    }
}
