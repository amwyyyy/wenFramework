package com.wen.framework.orm.generator.statement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.CacheBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;

import com.wen.framework.domain.BaseEntity;
import com.wen.framework.orm.annotation.Association;
import com.wen.framework.orm.annotation.Column;
import com.wen.framework.orm.annotation.Entity;
import com.wen.framework.utils.BeanUtils;

/**
 * 自动生成Statement基础类
 * @author huangwg
 */
public abstract class BaseStatementGenerator {
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	protected Class<?> modelClass;
	protected final Class<?> dtoClass;
	protected String namespace;
	protected String tableName;
	protected boolean useGeneratedKeys = false;
	protected Configuration configuration;
	protected Cache currentCache;
	protected Map<String, Field> columnFiledMap = new HashMap<String, Field>();
	protected Map<String, Column> columnMap = new HashMap<String, Column>();
	protected Map<String, Association> associationMap = new HashMap<String, Association>();
	protected List<String> columnNameList = new ArrayList<String>();
	protected List<String> pkColumnList = new ArrayList<String>();
	protected List<String> pkPropertyList = new ArrayList<String>();
	protected List<String> insertablecolumnNameList = new ArrayList<String>();
	protected List<String> updatablecolumnNameList = new ArrayList<String>();
	protected List<String> associationColumnNameList = new ArrayList<String>();
	
	public BaseStatementGenerator(Configuration configuration, Class<?> dtoClass) {
		this.configuration = configuration;
		this.dtoClass = dtoClass;
		
		Entity table = getModel(dtoClass);
		modelClass = getModelClass(dtoClass);
		if (table == null || modelClass == null) {
			return;
		}
		namespace = getNamespace(table);
		useGeneratedKeys = table.useGeneratedKeys();
		tableName = modelClass.getSimpleName();
		if (StringUtils.isNotEmpty(table.tablename())) {
			tableName = table.tablename();
		}
	}
	
	private String getNamespace(Entity model) {
		String namespace = model.namespace();
		if (StringUtils.isEmpty(namespace)) {
			String[] array = dtoClass.getPackage().getName().split("\\.");
			array[array.length - 1] = "dao";
			namespace = StringUtils.join(array, ".");		
			if (StringUtils.isNotEmpty(namespace)) {
				namespace += ".I" + modelClass.getSimpleName() + "Dao.";
			} else {
				namespace = "I" + modelClass.getSimpleName() + "Dao.";
			}
		}

		return namespace;
	}
	
	private Class<?> getModelClass(Class<?> clazz) {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass.equals(BaseEntity.class)) {
			return clazz;
		}

		return getModelClass(superClass);
	}
	
	private Entity getModel(Class<?> clazz) {
		if (clazz.equals(BaseEntity.class)) {
			return null;
		}

		Entity model = clazz.getAnnotation(Entity.class);
		if (model != null) {
			return model;
		}

		return getModel(clazz.getSuperclass());
	}
	
	private String getSimpleNamespace(String namespace) {
		return namespace.substring(0, namespace.length()-1);
	}
	
	private String getResultMapName(Class<?> clazz) {
		return namespace + clazz.getSimpleName() + "ResultMap";
	}
	
	/**
	 * 根据XML生成XNode
	 */
	protected XNode createXNode(String xml, String express) {
		XPathParser parser = new XPathParser(xml);
		return parser.evalNode(express);
	}
	
	/**
	 * 注解Column，添加列
	 */
	protected void addColumnMapper(List<ResultMapping> resultMappings, Column column, Field field) {
		if (column == null || StringUtils.isEmpty(column.name())) {
			return;
		}
		String columnName = column.name();
		String jdbcTypeStr = column.jdbcType();
		JdbcType jdbcType = resolveJdbcType(jdbcTypeStr);
		ResultMapping.Builder builder = new ResultMapping.Builder(configuration, field.getName(), columnName,
				field.getType());
		builder.jdbcType(jdbcType);
		resultMappings.add(builder.build());
	}
	
	/**
	 * 级联注解Association
	 */
	protected void addAssociationMapper(List<ResultMapping> resultMappings, Association association, Field field) {

		if (association == null || StringUtils.isEmpty(association.columnName())
				|| StringUtils.isEmpty(association.select())) {
			return;
		}

		String columnName = association.columnName();
		associationColumnNameList.add(association.columnName());
		associationMap.put(columnName, association);

		String jdbcTypeStr = association.jdbcType();
		JdbcType jdbcType = resolveJdbcType(jdbcTypeStr);
		ResultMapping.Builder builder = new ResultMapping.Builder(configuration, field.getName(), columnName,
				field.getType());
		builder.jdbcType(jdbcType);
		builder.nestedQueryId(association.select());
		builder.lazy(association.lazy());
		resultMappings.add(builder.build());
	}
	
	private JdbcType resolveJdbcType(String alias) {
		if (alias == null) {
			return null;
		}
		
		try {
			return JdbcType.valueOf(alias);
		} catch (IllegalArgumentException e) {
			throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
		}
	}
	
	/** 生成动态where语句Statement */
	public abstract void generateBaseDynamicWhereSql();
	
	/** 
	 * 生成缓存Statement 
	 */
	@SuppressWarnings("unchecked")
	public void generateCacheStatement() {
		String eviction = configuration.getVariables().getProperty("cache.eviction", "LRU");
		String flushInterval = configuration.getVariables().getProperty("cache.flushInterval", "60000");
		String size = configuration.getVariables().getProperty("cache.size", "1024");
		String readOnly = configuration.getVariables().getProperty("cache.readOnly", "false");
		String blocking = configuration.getVariables().getProperty("cache.blocking", "false");
		
		CacheBuilder cacheBuilder = new CacheBuilder(getSimpleNamespace(namespace))
			.clearInterval(Long.parseLong(flushInterval))
			.size(Integer.parseInt(size))
			.readWrite(readOnly.equals("true"))
			.blocking(blocking.equals("true"))
			.addDecorator((Class<? extends Cache>) configuration.getTypeAliasRegistry().resolveAlias(eviction));
		
		Cache cache = cacheBuilder.build();
		configuration.addCache(cache);
		currentCache = cache;
	}
	
	/** 
	 * 生成查询结果列Statement 
	 */
	public void generateBaseColumnListSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<sql id=\"Base_Column_List\">");
		sb.append(StringUtils.join(columnNameList, ","));
		sb.append("</sql>");

		if (logger.isDebugEnabled()) {
			logger.debug("Base_Column_List:");
			logger.debug(sb.toString());
		}

		XNode nodeBaseColumnListSql = createXNode(sb.toString(), "/sql");
		configuration.getSqlFragments().put(namespace + "Base_Column_List", nodeBaseColumnListSql);
	}
	
	/** 
	 * 生成dto类的映射,默认名称为:类名Dto+ResultMap 
	 */
	public void generateDtoResultMap() {
		List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
		Field[] fields = BeanUtils.findSuperFields(dtoClass);
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			Association association = field.getAnnotation(Association.class);
			if (column != null) {
				if (StringUtils.isEmpty(column.name())) {
					continue;
				}

				String columnName = column.name();
				columnFiledMap.put(columnName, field);
				columnMap.put(columnName, column);
				columnNameList.add(columnName);

				if (column.pk()) {
					pkColumnList.add(columnName);
					pkPropertyList.add(field.getName());
				}

				if (column.insertable()) {
					insertablecolumnNameList.add(columnName);
				}

				if (column.updatable()) {
					updatablecolumnNameList.add(columnName);
				}
				addColumnMapper(resultMappings, column, field);
			} else {
				if (association != null) {
					addAssociationMapper(resultMappings, association, field);
				}
			}
		}

		ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, getResultMapName(dtoClass), dtoClass,
				resultMappings, false);
		configuration.addResultMap(resultMapBuilder.build());
	}

	/** 
	 * 生成查询全部Statement 
	 */
	public void generateFindAllStatement() {
		SqlSource sqlSource = new RawSqlSource(configuration, generateFindAllSql(), null);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "findAll",
				sqlSource, SqlCommandType.SELECT);
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		resultMaps.add(configuration.getResultMap(getResultMapName(dtoClass)));
		statementBuilder.resultMaps(resultMaps);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		statementBuilder.useCache(false);
		statementBuilder.cache(currentCache);
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateFindAllSql();
	
	/** 
	 * 生成根据ID查询Statement 
	 */
	public void generateFindByIdStatement() {
		Class<?> parameterType = null;
		if (pkColumnList.size() > 1) {
			parameterType = Map.class;
		}
		SqlSource sqlSource = new RawSqlSource(configuration, generateFindByIdSql(), parameterType);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "findById",
				sqlSource, SqlCommandType.SELECT);
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		resultMaps.add(configuration.getResultMap(getResultMapName(dtoClass)));
		statementBuilder.resultMaps(resultMaps);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		statementBuilder.useCache(false);
		statementBuilder.cache(currentCache);
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateFindByIdSql();
	
	/** 
	 * 生成根据对象ID查询Statement 
	 */
	public void generateFindByEntityIdStatement() {
		SqlSource sqlSource = new RawSqlSource(configuration, generateFindByEntityIdSql(), dtoClass);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "findByEntityId",
				sqlSource, SqlCommandType.SELECT);
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		resultMaps.add(configuration.getResultMap(getResultMapName(dtoClass)));
		statementBuilder.resultMaps(resultMaps);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		statementBuilder.useCache(false);
		statementBuilder.cache(currentCache);
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateFindByEntityIdSql();
	
	/** 
	 * 生成根据条件查询Statement 
	 */
	public void generateFindByConditionStatement() {
		XNode xNode = createXNode(generateFindByConditionSql(), "/select");
		XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode, dtoClass);
		SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace
				+ "findByCondition", sqlSource, SqlCommandType.SELECT);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		resultMaps.add(configuration.getResultMap(getResultMapName(dtoClass)));
		statementBuilder.resultMaps(resultMaps);
		statementBuilder.useCache(false);
		statementBuilder.cache(currentCache);
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateFindByConditionSql();
	
	/** 
	 * 生成插入语句Statement 
	 */
	public void generateInsertStatement() {
		SqlSource sqlSource = new RawSqlSource(configuration, generateInsertSql(), dtoClass);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "insert",
				sqlSource, SqlCommandType.INSERT);
		statementBuilder.statementType(StatementType.PREPARED);
		KeyGenerator keyGenerator = (useGeneratedKeys || configuration.isUseGeneratedKeys()) ? new Jdbc3KeyGenerator()
				: new NoKeyGenerator();
		statementBuilder.keyGenerator(keyGenerator);
		statementBuilder.keyColumn(StringUtils.join(pkColumnList, ","));
		statementBuilder.keyProperty(StringUtils.join(pkPropertyList, ","));
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateInsertSql();
	
	/** 
	 * 生成根据ID更新语句Statement 
	 */
	public void generateUpdateByIdStatement() {
		XNode xNode = createXNode(generateUpdateByIdSql(), "/update");
		XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode, dtoClass);
		SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "updateById",
				sqlSource, SqlCommandType.INSERT);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateUpdateByIdSql();
	
	/** 
	 * 生成根据ID删除数据Statement 
	 */
	public void generateDeleteByIdStatement() {
		Class<?> parameterType = null;
		if (pkColumnList.size() > 1) {
			parameterType = Map.class;
		}
		SqlSource sqlSource = new RawSqlSource(configuration, generateDeleteByIdSql(), parameterType);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "deleteById",
				sqlSource, SqlCommandType.DELETE);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateDeleteByIdSql();

	/** 
	 * 生成取记录数Statement 
	 */
	public void generateCountAllStatement() {
		SqlSource sqlSource = new RawSqlSource(configuration, generateCountAllSql(), null);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace + "countAll",
				sqlSource, SqlCommandType.SELECT);
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, statementBuilder.id()
				+ "-Inline", Integer.class, new ArrayList<ResultMapping>(), null);
		resultMaps.add(inlineResultMapBuilder.build());
		statementBuilder.resultMaps(resultMaps);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateCountAllSql();
	
	/**
	 *  生成按条件取记录数Statement 
	 */
	public void generateCountByConditionStatement() {
		XNode xNode = createXNode(generateCountByConditionSql(), "/select");
		XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode, dtoClass);
		SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace
				+ "countByCondition", sqlSource, SqlCommandType.SELECT);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(configuration, statementBuilder.id()
				+ "-Inline", Integer.class, new ArrayList<ResultMapping>(), null);
		resultMaps.add(inlineResultMapBuilder.build());
		statementBuilder.resultMaps(resultMaps);
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateCountByConditionSql();
	
	/**
	 *  生成按id数组取记录数Statement 
	 */
	public void generateFindByIdsStatement() {
		XNode xNode = createXNode(generateFindByIdsSql(), "/select");
		XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, xNode, dtoClass);
		SqlSource sqlSource = xmlScriptBuilder.parseScriptNode();
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, namespace
				+ "findByIds", sqlSource, SqlCommandType.SELECT);
		statementBuilder.statementType(StatementType.PREPARED);
		statementBuilder.keyGenerator(new NoKeyGenerator());
		List<ResultMap> resultMaps = new ArrayList<ResultMap>();
		resultMaps.add(configuration.getResultMap(getResultMapName(dtoClass)));
		statementBuilder.resultMaps(resultMaps);
		MappedStatement statement = statementBuilder.build();
		configuration.addMappedStatement(statement);
	}
	
	protected abstract String generateFindByIdsSql();
}
