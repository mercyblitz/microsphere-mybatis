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

import io.microsphere.logging.Logger;
import io.microsphere.mybatis.executor.ExecutorFilter;
import io.microsphere.mybatis.executor.ExecutorInterceptor;
import io.microsphere.mybatis.executor.InterceptingExecutor;
import io.microsphere.mybatis.executor.InterceptorsExecutorFilterAdapter;
import io.microsphere.util.PriorityComparator;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.util.Properties;

import static io.microsphere.lang.function.ThrowableSupplier.execute;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.reflect.FieldUtils.findField;
import static io.microsphere.util.ArrayUtils.length;
import static io.microsphere.util.Assert.assertNoNullElements;
import static io.microsphere.util.Assert.assertTrue;
import static java.util.Arrays.sort;

/**
 * {@link Interceptor} class for {@link Executor} delegates to {@link ExecutorInterceptor} instances
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Interceptor
 * @since 1.0.0
 */
public class InterceptingExecutorInterceptor implements Interceptor {

    private static final Logger logger = getLogger(InterceptingExecutorInterceptor.class);

    private final ExecutorFilter[] executorFilters;

    private Properties properties;

    public InterceptingExecutorInterceptor(ExecutorFilter[] executorFilters, ExecutorInterceptor... executorInterceptors) {
        int executorFiltersCount = length(executorFilters);
        int executorInterceptorsCount = length(executorInterceptors);
        assertTrue(executorFiltersCount > 0 || executorInterceptorsCount > 0, () -> "No filter or interceptor for Executor");
        assertNoNullElements(executorFilters, () -> "Any element of filters must not be null!");
        if (executorInterceptorsCount > 0) {
            int newExecutorFiltersCount = executorFiltersCount + 1;
            ExecutorFilter[] newExecutorFilters = new ExecutorFilter[newExecutorFiltersCount];
            System.arraycopy(executorFilters, 0, newExecutorFilters, 0, executorFiltersCount);
            newExecutorFilters[executorFiltersCount] = new InterceptorsExecutorFilterAdapter(executorInterceptors);
            this.executorFilters = newExecutorFilters;
        } else {
            this.executorFilters = executorFilters;
        }
        // sort by its priority
        sort(this.executorFilters, PriorityComparator.INSTANCE);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        logger.warn("The intercept method should not be invoked : {}", invocation);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor && !(target instanceof InterceptingExecutor)) {
            Executor executor = ((Executor) target);

            final InterceptingExecutor interceptingExecutor;
            if (executor instanceof CachingExecutor) {
                CachingExecutor cachingExecutor = (CachingExecutor) executor;
                Executor delegate = getDelegate(cachingExecutor);
                interceptingExecutor = new InterceptingExecutor(delegate, this.properties, this.executorFilters);
                return new CachingExecutor(interceptingExecutor);
            } else {
                interceptingExecutor = new InterceptingExecutor(executor, this.properties, this.executorFilters);
                return interceptingExecutor;
            }
        }
        logger.trace("The non-executor [{}] instance simply returns without any dynamic proxy interception", target);
        return target;
    }

    private Executor getDelegate(CachingExecutor cachingExecutor) {
        Field field = findField(cachingExecutor, "delegate");
        field.setAccessible(true);
        Executor delegate = (Executor) execute(() -> field.get(cachingExecutor));
        logger.trace("The delegate of {} is : {}", cachingExecutor, delegate);
        return delegate;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
        logger.trace("setProperties : {}", properties);
    }
}
