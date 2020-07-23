package com.wen.framework.orm.generator.statement;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;

/**
 * Mysql方言的StatementGenerator
 * @author huangwg
 *
 */
public class MysqlStatementGenerator extends BaseStatementGenerator {

	public MysqlStatementGenerator(Configuration configuration, Class<?> dtoClass) {
		super(configuration, dtoClass);
	}

	private String generateDynamicWhereSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<trim prefix=\"where\" prefixOverrides=\"and\">").append("\n");
		for (int i = 0; i < columnNameList.size(); i++) {
			String columnName = columnNameList.get(i);
			String propertyName = columnFiledMap.get(columnName).getName();
			sb.append("  <if test=\"" + propertyName + " != null\">").append("\n");
			sb.append("    and " + columnName + " = #{" + propertyName + "} ").append("\n");
			sb.append("  </if>").append("\n");
		}
		sb.append("</trim>");
		return sb.toString();
	}

	@Override
	public void generateBaseDynamicWhereSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<sql id=\"Base_Dynamic_Where\">");
		sb.append(generateDynamicWhereSql());
		sb.append("</sql>");

		if (logger.isDebugEnabled()) {
			logger.debug("Base_Dynamic_Where:");
			logger.debug(sb.toString());
		}
		XNode nodeBaseDynamicWhereSql = createXNode(sb.toString(), "/sql");
		configuration.getSqlFragments().put(namespace + "Base_Dynamic_Where", nodeBaseDynamicWhereSql);
	}

	/**
	 * 生成findAll Sql
	 */
	protected String generateFindAllSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb.append(StringUtils.join(columnNameList, ","));
		sb.append(" from ").append(this.tableName);
		
		if (logger.isDebugEnabled()) {
			logger.debug("generateFindAllSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}

	/**
	 * 生成where语句 
	 */
	private String generateByIdSql() {
		String whereSql = "";
		for (int i = 0; i < pkColumnList.size(); i++) {
			String columnName = pkColumnList.get(i);
			String condition = columnName + "=#{" + columnFiledMap.get(columnName).getName();
			if (StringUtils.isNotEmpty(columnMap.get(columnName).jdbcType())) {
				condition += ",jdbcType=" + columnMap.get(columnName).jdbcType();
			}
			condition += "}";
			if (i == 0) {
				whereSql += " where " + condition;
			} else {
				whereSql += " and " + condition;
			}
		}
		return whereSql;
	}
	
	/**
	 * 生成findByPK Sql
	 */
	protected String generateFindByIdSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb.append(StringUtils.join(columnNameList, ","));
		sb.append(" from ").append(this.tableName);
		sb.append(generateByIdSql());

		if (logger.isDebugEnabled()) {
			logger.debug("generateFindByIdSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}
	
	/**
	 * 生成findByPK Sql
	 */
	protected String generateFindByEntityIdSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		sb.append(StringUtils.join(columnNameList, ","));
		sb.append(" from ").append(this.tableName);
		sb.append(generateByIdSql());

		if (logger.isDebugEnabled()) {
			logger.debug("generateFindByIdSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}	

	/**
	 * 生成FindByCondition Sql
	 */
	protected String generateFindByConditionSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<select id=\"findByCondition\" resultMap=\"" + dtoClass.getName() + "\"> ").append("\n");
		sb.append("  select ").append("\n");
		sb.append("    " + StringUtils.join(columnNameList, ",")).append("\n");
		sb.append("  from ").append(this.tableName).append("\n");
		sb.append(generateDynamicWhereSql()).append("\n");
		sb.append("</select>");

		if (logger.isDebugEnabled()) {
			logger.debug("generateFindByConditionSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}

	/**
	 * 生成insert sql
	 */
	protected String generateInsertSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ").append(this.tableName);
		sb.append("(");
		sb.append(StringUtils.join(insertablecolumnNameList, ","));
		sb.append(") values (");

		for (int i = 0; i < insertablecolumnNameList.size(); i++) {
			String columnName = insertablecolumnNameList.get(i);
			sb.append("#{" + columnFiledMap.get(columnName).getName());
			if (StringUtils.isNotEmpty(columnMap.get(columnName).jdbcType())) {
				sb.append(",jdbcType=" + columnMap.get(columnName).jdbcType());
			}
			sb.append("}");
			if (i < insertablecolumnNameList.size() - 1) {
				sb.append(", ");
			}
		}

		sb.append(")");

		if (logger.isDebugEnabled()) {
			logger.debug("generateInsertSql:");
			logger.debug(sb.toString());
		}
		return sb.toString();
	}

	/**
	 * 生成UpdateById sql
	 */
	protected String generateUpdateByIdSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<update id=\"updateByPK\" parameterType=\"" + dtoClass.getName() + "\">").append("\n");
		sb.append("  update ").append(this.tableName).append("\n");
		sb.append("  <set>").append("\n");

		for (int i = 0; i < updatablecolumnNameList.size(); i++) {
			String columnName = updatablecolumnNameList.get(i);
			if (pkColumnList.indexOf(columnName) > -1) {
				continue;
			}
			sb.append("    <if test=\"" + columnFiledMap.get(columnName).getName() + " != null\">").append("\n");
			sb.append("      " + columnName + " = #{" + columnFiledMap.get(columnName).getName());
			if (StringUtils.isNotEmpty(columnMap.get(columnName).jdbcType())) {
				sb.append(",jdbcType=" + columnMap.get(columnName).jdbcType());
			}
			sb.append("},").append("\n");
			sb.append("    </if>").append("\n");
		}
		sb.append("  </set>").append("\n");
		sb.append(" " + generateByIdSql()).append("\n");
		sb.append("</update>");

		if (logger.isDebugEnabled()) {
			logger.debug("generateUpdateByIdSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();

	}

	/**
	 * 生成deleteByPK Sql
	 */
	protected String generateDeleteByIdSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ").append(this.tableName);
		sb.append(generateByIdSql());

		if (logger.isDebugEnabled()) {
			logger.debug("generateDeleteByIdSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}
	
	/**
	 * 生成countAll Sql
	 */
	protected String generateCountAllSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("select count(1) from ").append(this.tableName);

		if (logger.isDebugEnabled()) {
			logger.debug("generateFindAllSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}

	/**
	 * 生成CountByCondition Sql
	 */
	protected String generateCountByConditionSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<select id=\"countByCondition\" resultType=\"java.lang.Integer\"> ").append("\n");
		sb.append("  select count(1) from ").append(this.tableName).append("\n");
		sb.append(generateDynamicWhereSql());
		sb.append("</select>");

		if (logger.isDebugEnabled()) {
			logger.debug("generateCountByConditionSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}
	
	/**
	 * 生成findByPKs Sql
	 */
	protected String generateFindByIdsSql() {
		StringBuffer sb = new StringBuffer();
		sb.append("<select id=\"findByIds\" resultMap=\"" + dtoClass.getName() + "\"> ").append("\n");
		sb.append("  select ").append("\n");
		sb.append("    " + StringUtils.join(columnNameList, ",")).append("\n");
		sb.append("  from ").append(this.tableName).append("\n");
		String pkName = pkColumnList.get(0);	//暂不考虑多主键的情况
		String jdbcType = columnMap.get(pkName).jdbcType();
		sb.append("  where " + pkName + " in ").append("\n");
		sb.append("  <foreach collection=\"array\" item=\"id\" open=\"(\" separator=\",\" close=\")\">").append("\n");
		sb.append("    #{id, jdbcType="+jdbcType+"}").append("\n");
		sb.append("  </foreach>").append("\n");
		sb.append("</select>");
		
		if (logger.isDebugEnabled()) {
			logger.debug("generateFindByIdsSql:");
			logger.debug(sb.toString());
		}

		return sb.toString();
	}
}
