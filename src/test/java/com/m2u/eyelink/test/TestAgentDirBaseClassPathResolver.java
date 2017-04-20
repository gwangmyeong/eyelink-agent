package com.m2u.eyelink.test;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m2u.eyelink.agent.AgentDirBaseClassPathResolver;

public class TestAgentDirBaseClassPathResolver {
	private static final Logger logger = LoggerFactory.getLogger(TestAgentDirBaseClassPathResolver.class
			.getName());
	
    private static final String BOOTSTRAP_JAR = "eyelink-agent-.jar";
    private static final String TEST_AGENT_DIR = "testagent";
    private static final String SEPARATOR = File.separator;
    
    private static final AtomicInteger AGENT_ID_ALLOCATOR = new AtomicInteger();
    
    private static String agentBuildDir;
    private static String agentBootstrapPath;
    
	@BeforeClass
	public static void beforeClass() throws Exception {
		logger.debug("before start");
		String classLocation = getClassLocation(TestAgentDirBaseClassPathResolver.class);
		logger.debug("buildDir:{}", classLocation);

		agentBuildDir = classLocation + SEPARATOR + TEST_AGENT_DIR + '_'
				+ AGENT_ID_ALLOCATOR.incrementAndGet();

		logger.debug("agentBuildDir:{}", agentBuildDir);

		agentBootstrapPath = agentBuildDir + SEPARATOR + BOOTSTRAP_JAR;

		logger.debug("agentPath:{}", agentBootstrapPath);

//		createAgentDir(agentBuildDir);

		logger.debug("before end");
	}

//    private static void createAgentDir(String tempAgentDir) throws IOException {
//
//        agentDirGenerator = new AgentDirGenerator(tempAgentDir);
//        agentDirGenerator.create();
//
//    }
    
    private static String getClassLocation(Class<?> clazz) {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        URL location = codeSource.getLocation();
        logger.debug("codeSource.getLocation:{}", location);
        File file = FileUtils.toFile(location);
        return file.getPath();
    }
    
	@AfterClass
	public static void afterClass() throws Exception {
	}

	@Test
	public void testFindAgentJar() throws Exception {

		logger.debug("TEST_AGENT_DIR:{}", agentBuildDir);
		logger.debug("agentPath:{}", agentBootstrapPath);

		AgentDirBaseClassPathResolver classPathResolver = new AgentDirBaseClassPathResolver(
				agentBootstrapPath);
		Assert.assertTrue("verify agent directory ", classPathResolver.verify());

		boolean findAgentJar = classPathResolver.findAgentJar();
		Assert.assertTrue(findAgentJar);

		String agentJar = classPathResolver.getAgentJarName();
		Assert.assertEquals(BOOTSTRAP_JAR, agentJar);

		String agentPath = classPathResolver.getAgentJarFullPath();
		Assert.assertEquals(agentBootstrapPath, agentPath);

		String agentDirPath = classPathResolver.getAgentDirPath();
		Assert.assertEquals(agentBuildDir, agentDirPath);

		String agentLibPath = classPathResolver.getAgentLibPath();
		Assert.assertEquals(agentBuildDir + File.separator + "lib",
				agentLibPath);

//		BootstrapJarFile bootstrapJarFile = classPathResolver
//				.getBootstrapJarFile();
//		closeJarFile(bootstrapJarFile);

	}

}
