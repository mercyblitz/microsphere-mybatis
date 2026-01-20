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

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Properties;
import java.util.Set;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apache.ibatis.mapping.ResultSetType.DEFAULT;
import static org.apache.ibatis.session.AutoMappingBehavior.PARTIAL;
import static org.apache.ibatis.session.AutoMappingUnknownColumnBehavior.NONE;
import static org.apache.ibatis.session.ExecutorType.SIMPLE;
import static org.apache.ibatis.session.LocalCacheScope.SESSION;
import static org.apache.ibatis.type.JdbcType.OTHER;

/**
 * The annotation for mybatis core modules' configuration properties, which cannot be used at the same time with the
 * {@link EnableMyBatis#configLocation()}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see EnableMyBatis
 * @see Configuration
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@Inherited
public @interface MyBatisConfiguration {

    /**
     * Allows using RowBounds on nested statements. If allow, set the false. Default is false.
     *
     * @see Configuration#setSafeRowBoundsEnabled(boolean)
     */
    boolean safeRowBoundsEnabled() default false;

    /**
     * Allows using ResultHandler on nested statements. If allow, set the false. Default is true.
     *
     * @see Configuration#setSafeResultHandlerEnabled(boolean)
     */
    boolean safeResultHandlerEnabled() default true;

    /**
     * Enables automatic mapping from classic database column names A_COLUMN to camel case classic Java property names
     * aColumn. Default is false.
     *
     * @see Configuration#setMapUnderscoreToCamelCase(boolean)
     */
    boolean mapUnderscoreToCamelCase() default false;

    /**
     * When enabled, any method call will load all the lazy properties of the object. Otherwise, each property is loaded
     * on demand (see also lazyLoadTriggerMethods). Default is false.
     *
     * @see Configuration#setAggressiveLazyLoading(boolean)
     */
    boolean aggressiveLazyLoading() default false;

    /**
     * Allows or disallows multiple ResultSets to be returned from a single statement (compatible driver required).
     * Default is true.
     *
     * @see Configuration#setMultipleResultSetsEnabled(boolean)
     * @deprecated
     */
    @Deprecated
    boolean multipleResultSetsEnabled() default true;

    /**
     * Allows JDBC support for generated keys. A compatible driver is required. This setting forces generated keys to be
     * used if set to true, as some drivers deny compatibility but still work (e.g. Derby). Default is false.
     *
     * @see Configuration#setUseGeneratedKeys(boolean)
     */
    boolean useGeneratedKeys() default false;

    /**
     * Uses the column label instead of the column name. Different drivers behave differently in this respect. Refer to
     * the driver documentation, or test out both modes to determine how your driver behaves. Default is true.
     *
     * @see Configuration#setUseColumnLabel(boolean)
     */
    boolean useColumnLabel() default true;

    /**
     * Globally enables or disables any caches configured in any mapper under this configuration. Default is true.
     *
     * @see Configuration#setCacheEnabled(boolean)
     */
    boolean cacheEnabled() default true;

    /**
     * Specifies if setters or map's put method will be called when a retrieved value is null. It is useful when you
     * rely on Map.keySet() or null value initialization. Note primitives such as (int,boolean,etc.) will not be set to
     * null. Default is false.
     *
     * @see Configuration#setCallSettersOnNulls(boolean)
     */
    boolean callSettersOnNulls() default false;

    /**
     * Allow referencing statement parameters by their actual names declared in the method signature. To use this
     * feature, your project must be compiled in Java 8 with -parameters option. Default is true.
     *
     * @see Configuration#setUseActualParamName(boolean)
     */
    boolean useActualParamName() default true;

    /**
     * MyBatis, by default, returns null when all the columns of a returned row are NULL. When this setting is enabled,
     * MyBatis returns an empty instance instead. Note that it is also applied to nested results (i.e. collection and
     * association). Default is false.
     *
     * @see Configuration#setReturnInstanceForEmptyRow(boolean)
     */
    boolean returnInstanceForEmptyRow() default false;

    /**
     * Removes extra whitespace characters from the SQL. Note that this also affects literal strings in SQL. Default is
     * false.
     *
     * @see Configuration#setShrinkWhitespacesInSql(boolean)
     */
    boolean shrinkWhitespacesInSql() default false;

    /**
     * Specifies the default value of 'nullable' attribute on 'foreach' tag. Default is false.
     *
     * @see Configuration#setNullableOnForEach(boolean)
     * @since MyBatis 3.5.9
     */
    boolean nullableOnForEach() default false;

    /**
     * When applying constructor auto-mapping, argument name is used to search the column to map instead of relying on
     * the column order. Default is false.
     *
     * @see Configuration#setArgNameBasedConstructorAutoMapping(boolean)
     */
    boolean argNameBasedConstructorAutoMapping() default false;

    /**
     * Globally enables or disables lazy loading. When enabled, all relations will be lazily loaded. This value can be
     * superseded for a specific relation by using the fetchType attribute on it. Default is False.
     *
     * @see Configuration#setLazyLoadingEnabled(boolean)
     */
    boolean lazyLoadingEnabled() default false;

    /**
     * Sets the number of seconds the driver will wait for a response from the database.
     *
     * @return <code>-1</code> as default, indicates no timeout specified
     * @see Configuration#setDefaultStatementTimeout(Integer)
     */
    int defaultStatementTimeout() default -1;

    /**
     * Sets the driver a hint as to control fetching size for return results. This parameter value can be override by a
     * query setting.
     *
     * @return <code>-1</code> as default, indicates no fetch size specified
     * @see Configuration#setDefaultFetchSize(Integer)
     * @since MyBatis 3.3.0
     */
    int defaultFetchSize() default -1;

    /**
     * MyBatis uses local cache to prevent circular references and speed up repeated nested queries. By default
     * (SESSION) all queries executed during a session are cached. If localCacheScope=STATEMENT local session will be
     * used just for statement execution, no data will be shared between two different calls to the same SqlSession.
     * Default is SESSION.
     *
     * @see Configuration#setLocalCacheScope(LocalCacheScope)
     */
    LocalCacheScope localCacheScope() default SESSION;

    /**
     * Specifies the JDBC type for null values when no specific JDBC type was provided for the parameter. Some drivers
     * require specifying the column JDBC type but others work with generic values like NULL, VARCHAR or OTHER. Default
     * is OTHER.
     *
     * @see Configuration#setJdbcTypeForNull(JdbcType)
     */
    JdbcType jdbcTypeForNull() default OTHER;

    /**
     * Specifies a scroll strategy when omit it per statement settings.
     *
     * @see Configuration#setDefaultResultSetType(ResultSetType)
     * @since MyBatis 3.5.2
     */
    ResultSetType defaultResultSetType() default DEFAULT;

    /**
     * Configures the default executor. SIMPLE executor does nothing special. REUSE executor reuses prepared statements.
     * BATCH executor reuses statements and batches updates. Default is SIMPLE.
     *
     * @see Configuration#setDefaultExecutorType(ExecutorType)
     */
    ExecutorType defaultExecutorType() default SIMPLE;

    /**
     * Specifies if and how MyBatis should automatically map columns to fields/properties. NONE disables auto-mapping.
     * PARTIAL will only auto-map results with no nested result mappings defined inside. FULL will auto-map result
     * mappings of any complexity (containing nested or otherwise). Default is PARTIAL.
     *
     * @see Configuration#setAutoMappingBehavior(AutoMappingBehavior)
     */
    AutoMappingBehavior autoMappingBehavior() default PARTIAL;

    /**
     * Specify the behavior when detects an unknown column (or unknown property type) of automatic mapping target.
     * Default is NONE.
     *
     * @see Configuration#setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior)
     * @since MyBatis 3.4.0
     */
    AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior() default NONE;

    /**
     * Specifies the prefix string that MyBatis will add to the logger names.
     *
     * @see Configuration#setLogPrefix(String)
     */
    String logPrefix() default "";

    /**
     * Specifies which Object's methods trigger a lazy load. Default is [equals,clone,hashCode,toString].
     *
     * @see Configuration#setLazyLoadTriggerMethods(Set)
     */
    String[] lazyLoadTriggerMethods() default {"equals", "clone", "hashCode", "toString"};

    /**
     * Specifies which logging implementation MyBatis should use. If this setting is not present logging implementation
     * will be autodiscovered.
     *
     * @return the {@link Class} of {@link Log} as default, indicates no class specified
     * @see Configuration#setLogImpl(Class)
     */
    Class<? extends Log> logImpl() default Log.class;

    /**
     * Specifies VFS implementations.
     *
     * @return the {@link Class} of {@link VFS} as default, indicates no class specified
     * @see Configuration#setVfsImpl(Class)
     */
    Class<? extends VFS> vfsImpl() default VFS.class;

    /**
     * Specifies an sql provider class that holds provider method. This class apply to the type(or value) attribute on
     * sql provider annotation(e.g. @SelectProvider), when these attribute was omitted.
     *
     * @return the {@link Class} of {@link Void} as default, indicates no class specified
     * @see Configuration#setDefaultSqlProviderType(Class)
     * @since MyBatis 3.5.6
     */
    Class<?> defaultSqlProviderType() default Void.class;

    /**
     * Specifies the TypeHandler used by default for Enum.
     *
     * @return the {@link Class} of {@link TypeHandler} as default, indicates no class specified
     * @see Configuration#setDefaultEnumTypeHandler(Class)
     * @since MyBatis 3.4.5
     */
    Class<? extends TypeHandler> defaultEnumTypeHandler() default TypeHandler.class;

    /**
     * Specifies the class that provides an instance of Configuration. The returned Configuration instance is used to
     * load lazy properties of deserialized objects. This class must have a method with a signature static Configuration
     * getConfiguration().
     *
     * @return the {@link Class} of {@link Void} as default, indicates no class specified
     * @see Configuration#setConfigurationFactory(Class)
     */
    Class<?> configurationFactory() default Void.class;

    /**
     * Specify any configuration variables.
     *
     * @return empty array as default, indicates no {@link Properties} speicified
     * @see Configuration#setVariables(Properties)
     */
    String[] variables() default {};

    /**
     * Specifies the database identify value for switching query to use.
     *
     * @return the empty string as default, indicates no database ID specified
     * @see Configuration#setDatabaseId(String)
     */
    String databaseId() default "";
}