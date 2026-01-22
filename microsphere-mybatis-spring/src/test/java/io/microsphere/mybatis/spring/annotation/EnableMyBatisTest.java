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

package io.microsphere.mybatis.spring.annotation;

import io.microsphere.mybatis.spring.test.config.MyBatisDataSourceTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static io.microsphere.spring.test.util.SpringTestUtils.testInSpringContainer;

/**
 * {@link EnableMyBatis} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableMyBatis
 * @since 1.0.0
 */
class EnableMyBatisTest {

    @Test
    void testDefaultConfig() {
        testInSpringContainer(context -> {

        }, DefaultConfig.class);
    }


    @EnableMyBatis(configLocation = "classpath:/META-INF/mybatis/config.xml")
    @Import(MyBatisDataSourceTestConfiguration.class)
    static class DefaultConfig {

    }
}
