package com.wen.framework.orm.generator.java;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wen.framework.dao.IBaseDao;
import com.wen.framework.service.BaseService;

/**
 * service类生成器
 *
 */
public class ServiceGenerator extends BaseJavaGenerator {

	public ServiceGenerator() {
		super();
	}

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		super.calculateJavaFileAttributes(introspectedTable.getFullyQualifiedTable());
		CommentGenerator commentGenerator = context.getCommentGenerator();
		String entityClassName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
		
		TopLevelClass topLevelClass = new TopLevelClass(serviceType);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(topLevelClass);

		topLevelClass.addImportedType(new FullyQualifiedJavaType(IBaseDao.class.getName()));
		topLevelClass.addImportedType(new FullyQualifiedJavaType(BaseService.class.getName()));
		topLevelClass.addImportedType(new FullyQualifiedJavaType(dtoType));
		topLevelClass.setSuperClass(new FullyQualifiedJavaType(BaseService.class.getName()+ "<" + dtoType + ", " + pkType + ">"));
		topLevelClass.addAnnotation("@Service");
		topLevelClass.addImportedType(new FullyQualifiedJavaType(Service.class.getName()));
		addDaoField(topLevelClass, daoPackage, entityClassName);
		addDaoMethod(topLevelClass, entityClassName);
		
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		answer.add(topLevelClass);

		return answer;
	}
	
	private void addDaoField(TopLevelClass topLevelClass, String daoPackage, String entityClassName) {
		topLevelClass.addImportedType(new FullyQualifiedJavaType(daoType));
		
		Field field = new Field(lowcaseFirstChar(entityClassName) + "Dao", new FullyQualifiedJavaType(daoType));
		field.setVisibility(JavaVisibility.PRIVATE);
		field.addAnnotation("@Autowired");
		topLevelClass.addImportedType(new FullyQualifiedJavaType(Autowired.class.getName()));
		topLevelClass.addField(field);
	}
	
	private void addDaoMethod(TopLevelClass topLevelClass, String entityClassName){
		Method method = new Method("getDao");
		method.setReturnType(new FullyQualifiedJavaType(IBaseDao.class.getName()+ "<" + dtoType + ", " + pkType + ">"));
		method.addBodyLine("return " + lowcaseFirstChar(entityClassName) + "Dao;");
		method.addAnnotation("@Override");
		method.setVisibility(JavaVisibility.PROTECTED);
		topLevelClass.addMethod(method);
	}
	
	
	private String lowcaseFirstChar(String s){
		if(s==null || s.length()==0){
			return s;
		}
		
		String result = s.substring(0,1).toLowerCase() + s.substring(1);
		
		return result;
	}

}
