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

package io.microsphere.mybatis.spring.cloud.autoconfigure;

import io.microsphere.mybatis.spring.annotation.EnableMyBatis;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import static io.microsphere.constants.PropertyConstants.ENABLED_PROPERTY_NAME;
import static io.microsphere.mybatis.constants.PropertyConstants.MICROSPHERE_MYBATIS_SPRING_BOOT_PROPERTY_NAME_PREFIX;

/**
 * The Auto-{@link Configuration} for MyBatis Spring Cloud
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Configuration
 * @see org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
 * @since 1.0.0
 */
@EnableMyBatis
@ConditionalOnProperty(prefix = MICROSPHERE_MYBATIS_SPRING_BOOT_PROPERTY_NAME_PREFIX, name = ENABLED_PROPERTY_NAME,
        matchIfMissing = true)
@AutoConfigureAfter(name = {
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration"
})
public class MyBatisCloudAutoConfiguration {
}
