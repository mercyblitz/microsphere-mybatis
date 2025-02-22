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

import io.microsphere.mybatis.executor.LogggingExecutorInterceptor;
import io.microsphere.mybatis.test.AbstractMyBatisTest;
import org.apache.ibatis.session.Configuration;

import java.util.Properties;

/**
 * {@link InterceptingExecutorInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see InterceptingExecutorInterceptor
 * @since 1.0.0
 */
public class InterceptingExecutorInterceptorTest extends AbstractMyBatisTest {

    @Override
    protected void customize(Configuration configuration) {
        configuration.addInterceptor(createInterceptingExecutorInterceptor());
    }

    private InterceptingExecutorInterceptor createInterceptingExecutorInterceptor() {
        LogggingExecutorInterceptor loggingExecutorInterceptor = new LogggingExecutorInterceptor();
        InterceptingExecutorInterceptor interceptingExecutorInterceptor = new InterceptingExecutorInterceptor(loggingExecutorInterceptor);
        Properties properties = new Properties();
        properties.setProperty("test.class", this.getClass().getName());
        interceptingExecutorInterceptor.setProperties(properties);
        return interceptingExecutorInterceptor;
    }
}
