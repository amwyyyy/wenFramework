package com.wen.framework.orm.generator;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import org.apache.commons.lang.StringUtils;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;

public class MapperXmlGenerator extends AbstractXmlGenerator {

	public MapperXmlGenerator() {
		super();
	}

	protected XmlElement getSqlMapElement() {
		FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
		progressCallback.startTask(getString("Progress.12", table.toString()));
		XmlElement answer = new XmlElement("mapper");

		String entityClassName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
		String entityTargetPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
		String daoTargetPackage = entityTargetPackage;
		
		if (StringUtils.isNotEmpty(entityTargetPackage)) {
			String[] array = entityTargetPackage.split("\\.");
			array[array.length - 1] = "dao";
			daoTargetPackage = StringUtils.join(array, ".");
		}
		String daoPackage = daoTargetPackage + table.getSubPackage(true);
		
		String namespace = daoPackage + ".I" + entityClassName + "Dao";

		answer.addAttribute(new Attribute("namespace", namespace));
		context.getCommentGenerator().addRootComment(answer);
		return answer;
	}

	protected void initializeAndExecuteGenerator(AbstractXmlElementGenerator elementGenerator, XmlElement parentElement) {
		elementGenerator.setContext(context);
		elementGenerator.setIntrospectedTable(introspectedTable);
		elementGenerator.setProgressCallback(progressCallback);
		elementGenerator.setWarnings(warnings);
		elementGenerator.addElements(parentElement);
	}

	@Override
	public Document getDocument() {
		Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
		document.setRootElement(getSqlMapElement());

		if (!context.getPlugins().sqlMapDocumentGenerated(document, introspectedTable)) {
			document = null;
		}

		return document;
	}
}