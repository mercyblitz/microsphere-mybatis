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
import io.microsphere.mybatis.executor.ExecutorInterceptor;
import io.microsphere.mybatis.executor.InterceptingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.util.Collection;
import java.util.Properties;

import static io.microsphere.logging.LoggerFactory.getLogger;

/**
 * {@link Interceptor} class for {@link Executor} delegates to {@link ExecutorInterceptor} instances
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Interceptor
 * @since 1.0.0
 */
public class InterceptingExecutorInterceptor implements Interceptor {

    private static final Logger logger = getLogger(InterceptingExecutorInterceptor.class);

    private final ExecutorInterceptor[] executorInterceptors;

    private Properties properties;

    public InterceptingExecutorInterceptor(Collection<ExecutorInterceptor> executorInterceptors) {
        this(executorInterceptors.toArray(new ExecutorInterceptor[0]));
    }

    public InterceptingExecutorInterceptor(ExecutorInterceptor[] executorInterceptors) {
        this.executorInterceptors = executorInterceptors;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (logger.isWarnEnabled()) {
            logger.warn("The intercept method should not be invoked : {}", invocation);
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor && !(target instanceof InterceptingExecutor)) {
            InterceptingExecutor interceptingExecutor = new InterceptingExecutor((Executor) target, executorInterceptors);
            interceptingExecutor.setProperties(this.properties);
            return interceptingExecutor;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("The non-executor [{}] instance simply returns without any dynamic proxy interception", target.getClass());
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
        if (logger.isTraceEnabled()) {
            logger.trace("setProperties : {}", properties);
        }
    }
}
