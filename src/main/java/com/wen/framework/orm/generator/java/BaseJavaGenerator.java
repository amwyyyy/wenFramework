package com.wen.framework.orm.generator.java;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;

/**
 * Java类生成器基类
 * @author huangwg
 *
 */
public abstract class BaseJavaGenerator extends AbstractJavaClientGenerator {

	protected String dtoPackage;
	protected String daoPackage;
	protected String servicePackage;
	
	protected String dtoType;
	protected String daoType;
	protected String serviceType;
	protected String pkType;
	
	public BaseJavaGenerator() {
		super(false);
	}

	public BaseJavaGenerator(boolean requiresXMLGenerator) {
		super(requiresXMLGenerator);
	}
	
	protected void calculateJavaFileAttributes(FullyQualifiedTable fullyQualifiedTable){
		if (context.getJavaModelGeneratorConfiguration() == null) {
            return;
        }
		String entityTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
		String dtoTargetPackage = entityTargetPackage;
		String daoTargetPackage = entityTargetPackage;
		String serviceTargetPackage = entityTargetPackage;
		
		if (StringUtils.isNotEmpty(entityTargetPackage)) {
			String[] array = entityTargetPackage.split("\\.");
			
			array[array.length - 1] = "dto";
			dtoTargetPackage = StringUtils.join(array, ".");
			
			array[array.length - 1] = "dao";
			daoTargetPackage = StringUtils.join(array, ".");
			
			array[array.length - 1] = "service";
			serviceTargetPackage = StringUtils.join(array, ".");
		}
		
		StringBuilder sb = new StringBuilder();
		
		dtoPackage = dtoTargetPackage + fullyQualifiedTable.getSubPackage(true);
        sb.append(dtoPackage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Dto"); 
        dtoType = sb.toString();

        daoPackage = daoTargetPackage + fullyQualifiedTable.getSubPackage(true);
        sb.setLength(0);
        sb.append(daoPackage);
        sb.append('.');
        sb.append("I");
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Dao"); 
        daoType = sb.toString();

        servicePackage = serviceTargetPackage + fullyQualifiedTable.getSubPackage(true);
        sb.setLength(0);
        sb.append(servicePackage);
        sb.append(".");
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append("Service"); 
        serviceType = sb.toString();
        
        pkType = void.class.getName();
		if (introspectedTable.getPrimaryKeyColumns() != null && !introspectedTable.getPrimaryKeyColumns().isEmpty()) {
			pkType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
		}
	}
	
	public AbstractXmlGenerator getMatchedXMLGenerator(){
		return null;
	}
}
