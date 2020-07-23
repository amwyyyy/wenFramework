package com.wen.framework.orm.generator.statement;

import org.apache.ibatis.session.Configuration;

/**
 * Oracle方言的StatementGenerator
 * @author huangwg
 *
 */
public class OracleStatementGenerator extends BaseStatementGenerator {

	public OracleStatementGenerator(Configuration configuration, Class<?> dtoClass) {
		super(configuration, dtoClass);
	}

	@Override
	public void generateBaseDynamicWhereSql() {
		
	}

	@Override
	protected String generateFindAllSql() {
		return null;
	}

	@Override
	protected String generateFindByIdSql() {
		return null;
	}

	@Override
	protected String generateFindByEntityIdSql() {
		return null;
	}

	@Override
	protected String generateFindByConditionSql() {
		return null;
	}

	@Override
	protected String generateInsertSql() {
		return null;
	}

	@Override
	protected String generateUpdateByIdSql() {
		return null;
	}

	@Override
	protected String generateDeleteByIdSql() {
		return null;
	}

	@Override
	protected String generateCountAllSql() {
		return null;
	}

	@Override
	protected String generateCountByConditionSql() {
		return null;
	}

	@Override
	protected String generateFindByIdsSql() {
		return null;
	}
}
