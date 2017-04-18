package com.m2u.eyelink.agent;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgentDirBaseClassPathResolver implements ClassPathResolver {
	private final ELLogger logger = ELLogger.getLogger(this.getClass()
			.getName());

	static final String VERSION_PATTERN = "(-[0-9]+\\.[0-9]+\\.[0-9]+((\\-SNAPSHOT)|(-RC[0-9]+))?)?";
	static final Pattern DEFAULT_AGENT_PATTERN = compile("ELAgent"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_AGENT_COMMONS_PATTERN = compile("ELAgent-commons"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_AGENT_CORE_PATTERN = compile("ELAgent-core"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_AGENT_CORE_OPTIONAL_PATTERN = compile("ELAgent-optional"
			+ VERSION_PATTERN + "\\.jar");
	static final Pattern DEFAULT_ANNOTATIONS = compile("ELAgent-annotations"
			+ VERSION_PATTERN + "\\.jar");

	private final Pattern agentPattern;
	private final Pattern agentCommonsPattern;
	private final Pattern agentCorePattern;
	private final Pattern agentCoreOptionalPattern;
	private final Pattern annotationsPattern;

	private String classPath;

	private String agentJarName;
	private String agentJarFullPath;
	private String agentDirPath;

	private List<String> fileExtensionList;
	private String ElAgentCommonsJar;
	private String ElAgentCoreJar;
	private String ElAgentOptionalJar;
	private String annotationsJar;

	private ELAgentJarFile agentJarFile;

	private static Pattern compile(String regex) {
		return Pattern.compile(regex);
	}

	public AgentDirBaseClassPathResolver() {
		this(getClassPathFromSystemProperty());
	}

	public AgentDirBaseClassPathResolver(String classPath) {
		this.classPath = classPath;
		this.agentPattern = DEFAULT_AGENT_PATTERN;
		this.agentCommonsPattern = DEFAULT_AGENT_COMMONS_PATTERN;
		this.agentCorePattern = DEFAULT_AGENT_CORE_PATTERN;
		this.agentCoreOptionalPattern = DEFAULT_AGENT_CORE_OPTIONAL_PATTERN;
		this.annotationsPattern = DEFAULT_ANNOTATIONS;
		this.fileExtensionList = getDefaultFileExtensionList();
	}

	public List<String> getDefaultFileExtensionList() {
		List<String> extensionList = new ArrayList<String>();
		extensionList.add("jar");
		extensionList.add("xml");
		extensionList.add("properties");
		return extensionList;
	}

	public static String getClassPathFromSystemProperty() {
		return System.getProperty("java.class.path");
	}

	@Override
	public boolean verify() {
		final ELAgentJarFile agentJarFile = new ELAgentJarFile();

		// find ELAgent.jar file
		final boolean agentJarFileNotFound = this.findAgentJar();
		if (agentJarFileNotFound) {
			logger.warn("ELAgent-x.x.x(-SNAPSHOT).jar not found.");
			return false;
		}

		this.agentJarFile = agentJarFile;
		return true;
	}

	boolean findAgentJar() {
		Matcher matcher = agentPattern.matcher(classPath);
		if (!matcher.find()) {
			return false;
		}
		this.agentJarName = parseAgentJar(matcher);
		this.agentJarFullPath = parseAgentJarPath(classPath, agentJarName);
		if (agentJarFullPath == null) {
			return false;
		}
		this.agentDirPath = parseAgentDirPath(agentJarFullPath);
		if (agentDirPath == null) {
			return false;
		}

		logger.info("Agent original-path:" + agentDirPath);
		// defense alias change
		this.agentDirPath = toCanonicalPath(agentDirPath);
		logger.info("Agent canonical-path:" + agentDirPath);

		this.ElAgentCommonsJar = findFromBootDir("ELAgent-commons.jar",
				agentCommonsPattern);
		this.ElAgentCoreJar = findFromBootDir("ELAgent-core.jar",
				agentCorePattern);
		this.ElAgentOptionalJar = findFromBootDir(
				"ELAgent-core-optional.jar",
				agentCoreOptionalPattern);
		this.annotationsJar = findFromBootDir("ELAgent-annotations.jar",
				annotationsPattern);
		return true;
	}

	private String parseAgentJar(Matcher matcher) {
		int start = matcher.start();
		int end = matcher.end();
		return this.classPath.substring(start, end);
	}

	private String parseAgentJarPath(String classPath, String agentJar) {
		String[] classPathList = classPath.split(File.pathSeparator);
		for (String findPath : classPathList) {
			boolean find = findPath.contains(agentJar);
			if (find) {
				return findPath;
			}
		}
		return null;
	}

	private String parseAgentDirPath(String agentJarFullPath) {
		int index1 = agentJarFullPath.lastIndexOf("/");
		int index2 = agentJarFullPath.lastIndexOf("\\");
		int max = Math.max(index1, index2);
		if (max == -1) {
			return null;
		}
		return agentJarFullPath.substring(0, max);
	}

	private String toCanonicalPath(String path) {
		final File file = new File(path);
		return toCanonicalPath(file);
	}

	private String toCanonicalPath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			logger.warn(file.getPath() + " getCanonicalPath() error. Error:"
					+ e.getMessage(), e);
			return file.getAbsolutePath();
		}
	}

	private String findFromBootDir(final String name, final Pattern pattern) {
		String bootDirPath = agentDirPath + File.separator + "boot";
		File[] files = listFiles(name, pattern, bootDirPath);
		if (files == null || files.length == 0) {
			logger.info(name + " not found.");
			return null;
		} else if (files.length == 1) {
			File file = files[0];
			return toCanonicalPath(file);
		} else {
			logger.info("too many " + name + " found. "
					+ Arrays.toString(files));
			return null;
		}
	}
	
    private File[] listFiles(final String name, final Pattern pattern, String bootDirPath) {
        File bootDir = new File(bootDirPath);
        return bootDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.matches()) {

                    logger.info("found " + name + ". " + dir.getAbsolutePath() + File.separator + fileName);
                    return true;
                }
                return false;
            }
        });
    }


	@Override
	public ELAgentJarFile getELAgentJarFile() {
		return agentJarFile;
	}

	@Override
	public String getELAgentCommonsJar() {
		return ElAgentCommonsJar;
	}

	@Override
	public String getELAgentCoreJar() {
		return ElAgentCoreJar;
	}

	@Override
	public String getELAgentOptionalJar() {
		return ElAgentOptionalJar;
	}

	@Override
	public String getAgentJarName() {
		return this.agentJarName;
	}

	@Override
	public String getAgentJarFullPath() {
		return this.agentJarFullPath;
	}

	@Override
	public String getAgentLibPath() {
		return this.agentDirPath + File.separator + "Lib";
	}

	@Override
	public String getAgentLogFilePath() {
		return this.agentDirPath + File.separator + "Log";
	}

	@Override
	public String getAgentPluginPath() {
		return this.agentDirPath + File.separator + "plugin";
	}

	@Override
	public List<URL> resolveLib() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL[] resolvePlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAgentDirPath() {
		return agentDirPath;
	}

	@Override
	public String getAgentConfigPath() {
		return agentDirPath + File.separator + "ELAgent.config";
	}
}
