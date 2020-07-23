package com.wen.framework.orm.generator.java;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.AbstractXmlGenerator;

import com.wen.framework.dao.IBaseDao;
import com.wen.framework.orm.generator.MapperXmlGenerator;

/**
 * Dao类生成器
 * dao的产生通过配置文件来指定，不让插件生成mapper.xml内容，而由代码动态生成
 */
public class DaoGenerator extends BaseJavaGenerator {

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		super.calculateJavaFileAttributes(introspectedTable.getFullyQualifiedTable());
		CommentGenerator commentGenerator = context.getCommentGenerator();
				
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(daoType);
		Interface interfaze = new Interface(type);
		interfaze.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(interfaze);

		interfaze.addImportedType(new FullyQualifiedJavaType(IBaseDao.class.getName()));//导入包Import
		interfaze.addImportedType(new FullyQualifiedJavaType(dtoType));
		interfaze.addSuperInterface(new FullyQualifiedJavaType(IBaseDao.class.getName() + "<" + dtoType + ", "
				+ pkType + ">"));
		
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().clientGenerated(interfaze, null, introspectedTable)) {
			answer.add(interfaze);
		}

		return answer;
	}

	public AbstractXmlGenerator getMatchedXMLGenerator(){
		return new MapperXmlGenerator();
	}
}
