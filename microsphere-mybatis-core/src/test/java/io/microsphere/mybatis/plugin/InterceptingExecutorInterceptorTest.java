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

import io.microsphere.lang.function.ThrowableConsumer;
import io.microsphere.mybatis.executor.LogggingExecutorInterceptor;
import io.microsphere.mybatis.test.AbstractMyBatisTest;
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
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        InterceptingExecutorInterceptor interceptingExecutorInterceptor = new InterceptingExecutorInterceptor(asList(loggingExecutorInterceptor));
        Properties properties = new Properties();
        properties.setProperty("test.class", this.getClass().getName());
        interceptingExecutorInterceptor.setProperties(properties);
        return interceptingExecutorInterceptor;
    }
}
