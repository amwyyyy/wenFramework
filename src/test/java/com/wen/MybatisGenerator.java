package com.wen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * 自动生成dao,domain,service代码
 * @author amwyyyy
 *
 */
public class MybatisGenerator {

	public static void main(String[] args) throws IOException, Exception {
		List<String> warnings = new ArrayList<String>();
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(MybatisGenerator.class
				.getClassLoader().getResourceAsStream("mybatis-generatorConfig.xml"));

		DefaultShellCallback shellCallback = new DefaultShellCallback(true) {
			private String targetPackage;

			public File getDirectory(String targetProject, String targetPackage)
					throws ShellException {
				this.targetPackage = targetPackage;
				return super.getDirectory(targetProject, targetPackage);
			}

			public boolean isOverwriteEnabled() {
				return targetPackage.startsWith("com.wen.project.domain");
			}

			public boolean isMergeSupported() {
				return !isOverwriteEnabled();
			}

			public String mergeJavaFile(String newFileSource,
					String existingFileFullPath, String[] javadocTags, String fileEncoding)
					throws ShellException {
				StringBuffer source = new StringBuffer();
				// 不覆盖，直接返回文件原来的内容
				FileInputStream fis=null ;
				try {
					fis= new FileInputStream(new File(existingFileFullPath));
					int n = 0;
					byte[] bytes = new byte[1024];
					do {
						n = fis.read(bytes);
						if(n>0){
							source.append(new String(bytes, 0, n, fileEncoding));
						}
					} while (n > 0);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if(fis!=null){
						try {
							fis.close();
						} catch (IOException e) {
						}
					}
				}

				return source.toString();
			}
		};

		try {
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
			myBatisGenerator.generate(null);
		} catch (InvalidConfigurationException e) {
			throw e;
		}
	}

}
