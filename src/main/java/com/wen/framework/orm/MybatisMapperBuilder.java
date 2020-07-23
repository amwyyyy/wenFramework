package com.wen.framework.orm;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.session.Configuration;

import com.wen.framework.orm.generator.statement.BaseStatementGenerator;
import com.wen.framework.orm.generator.statement.MysqlStatementGenerator;
import com.wen.framework.orm.generator.statement.OracleStatementGenerator;

/**
 * 
 * MyBatis Mapper Builder, 动态根据Model类生成与Model类相关的映射，避免手工编写dao,mapper xml文件
 * 
 */
public class MybatisMapperBuilder extends BaseBuilder {
	private static final Log logger = LogFactory.getLog(MybatisMapperBuilder.class);
	private final Configuration configuration;
	private final Class<?> dtoClass;

	public MybatisMapperBuilder(Configuration configuration, Class<?> dtoClass) {
		super(configuration);
		this.configuration = configuration;
		this.dtoClass = dtoClass;
	}

	public void build() {
		logger.info("build " + dtoClass);

		String dialect = configuration.getVariables().getProperty("dialect");
		if(StringUtils.isEmpty(dialect)) {
			throw new RuntimeException("dialect不能为空");
		}
		
		BaseStatementGenerator statementGenerator = null;
		if(dialect.equalsIgnoreCase("mysql")) {
			statementGenerator = new MysqlStatementGenerator(configuration, dtoClass);
		} else if(dialect.equalsIgnoreCase("oracle")) {
			statementGenerator = new OracleStatementGenerator(configuration, dtoClass);
		}
		
		statementGenerator.generateCacheStatement();
		statementGenerator.generateDtoResultMap();
		
		statementGenerator.generateBaseColumnListSql();
		statementGenerator.generateBaseDynamicWhereSql();
		
		statementGenerator.generateFindAllStatement();
		statementGenerator.generateFindByIdStatement();
		statementGenerator.generateFindByEntityIdStatement();
		statementGenerator.generateFindByConditionStatement();
		
		statementGenerator.generateInsertStatement();
		statementGenerator.generateUpdateByIdStatement();
		statementGenerator.generateDeleteByIdStatement();
		
		statementGenerator.generateCountAllStatement();
		statementGenerator.generateCountByConditionStatement();
	}
}
