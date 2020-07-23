package com.wen.framework.orm.generator.java;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.PropertyRegistry;

import com.wen.framework.domain.BaseEntity;
import com.wen.framework.orm.annotation.Column;
import com.wen.framework.orm.annotation.Entity;

/**
 * Mybatis Generator 插件
 */
public class MybatisGeneratorPlugin extends PluginAdapter {
	
	private AbstractJavaGenerator[] extJavaFileGenerators = 
			new AbstractJavaGenerator[]{new DtoGenerator(), new ServiceGenerator()};
	
	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		topLevelClass.addImportedType(Column.class.getName());
		topLevelClass.addImportedType(Entity.class.getName());
		topLevelClass.addImportedType(Serializable.class.getName());

		
		String entityTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
		String daoTargetPackage = entityTargetPackage;
		
		if (StringUtils.isNotEmpty(entityTargetPackage)) {
			String[] array = entityTargetPackage.split("\\.");
			array[array.length - 1] = "dao";
			daoTargetPackage = StringUtils.join(array, ".");
		}
        
        
		String namespace = daoTargetPackage;
		if (namespace == null) {
			namespace = topLevelClass.getType().getShortName() + "Dao";
		} else {
			namespace += "." + topLevelClass.getType().getShortName() + "Dao";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("@" + Entity.class.getSimpleName() + "(");
		sb.append("tablename=\"").append(introspectedTable.getTableConfiguration().getTableName()).append("\"");
		sb.append(")");
		topLevelClass.addAnnotation(sb.toString());

		topLevelClass.addSuperInterface(new FullyQualifiedJavaType(Serializable.class.getName()));

		addSerialVersionUID(topLevelClass);
		return true;
	}
	
	

	public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
			IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType) {
		//BaseEntity有的公共字段不再生成
		for(java.lang.reflect.Field baseField : BaseEntity.class.getDeclaredFields()) {
			if(field.getName().equals(baseField.getName())) {
				return false;
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("@" + Column.class.getSimpleName() + "(");
		sb.append("name=\"").append(introspectedColumn.getActualColumnName()).append("\"");
		sb.append(", jdbcType=\"").append(introspectedColumn.getJdbcTypeName()).append("\"");

		if (introspectedColumn.isIdentity()
				|| isPrimaryKeyColumn(introspectedTable.getPrimaryKeyColumns(), introspectedColumn)) {
			sb.append(", pk=true");
		}

		if (introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName().equals(Double.class.getName())
				|| introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName().equals(Float.class.getName())) {
			sb.append(", precision=" + introspectedColumn.getLength());
			sb.append(", scale=" + introspectedColumn.getScale());
		}

		if (introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName().equals(String.class.getName())) {
			sb.append(", length=" + introspectedColumn.getLength());
		}

		if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
			sb.append(", remark=\"" + introspectedColumn.getRemarks() + "\"");
		}
		sb.append(")");
		field.addAnnotation(sb.toString());

		return true;
	}

	public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
		return true;
	}


	private boolean isPrimaryKeyColumn(List<IntrospectedColumn> primaryKeyColumns, IntrospectedColumn introspectedColumn) {
		if (primaryKeyColumns == null || primaryKeyColumns.isEmpty() || introspectedColumn == null) {
			return false;
		}

		for (IntrospectedColumn primaryKeyColumn : primaryKeyColumns) {
			if (primaryKeyColumn.getActualColumnName().equalsIgnoreCase(introspectedColumn.getActualColumnName())) {
				return true;
			}
		}

		return false;

	}

	private void addSerialVersionUID(TopLevelClass topLevelClass) {
		Field field = new Field("serialVersionUID", new FullyQualifiedJavaType(long.class.getName()));
		field.setFinal(true);
		field.setStatic(true);
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setInitializationString(createSerialVersionUID(topLevelClass.getType().getFullyQualifiedName()));
		topLevelClass.addField(field);
	}
	
	
	private String createSerialVersionUID(String className){
		long hashCode = (className + System.currentTimeMillis()).hashCode();
		return hashCode + "L";
		
	}
	
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        return true;
    }
    
    
    //生成其它java文件
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();

        for (AbstractJavaGenerator javaGenerator : extJavaFileGenerators) {
        	javaGenerator.setIntrospectedTable(introspectedTable);
        	javaGenerator.setContext(context);
            List<CompilationUnit> compilationUnits = javaGenerator
                    .getCompilationUnits();
            for (CompilationUnit compilationUnit : compilationUnits) {
                GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
                        context.getJavaModelGeneratorConfiguration()
                                .getTargetProject(),
                                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                                context.getJavaFormatter());
                answer.add(gjf);
            }
        }

        return answer;
    }

}
