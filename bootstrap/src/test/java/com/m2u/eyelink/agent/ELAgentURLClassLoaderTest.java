package com.m2u.eyelink.agent;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Assert;
import org.junit.Test;

public class ELAgentURLClassLoaderTest {

	// @Test
	// FIXME test error
	public void testOnLoadClass() throws Exception {

		ELAgentURLClassLoader cl = new ELAgentURLClassLoader(new URL[] {},
				Thread.currentThread().getContextClassLoader());
		try {
			cl.loadClass("com.m2u.eyelink.agent.profiler.DefaultAgent");
		} catch (ClassNotFoundException ignored) {
			Assert.fail();
		}

		// try {
		// cl.loadClass("com.navercorp.pinpoint.profiler.DefaultAgent");
		// } catch (ClassNotFoundException e) {
		//
		// }
		// should be able to test using the above code, but it is not possible
		// from bootstrap testcase.
		// it could be possible by specifying the full path to the URL
		// classloader, but it would be harder to maintain.
		// for now, just test if DefaultAgent is specified to be loaded
		Assert.assertTrue(cl
				.onLoadClass("com.m2u.eyelink.agent.profiler.DefaultAgent"));
	}

	// classloader를 이용한 jar 파일 Loading Test
	@Test
	public void testJarFile() throws MalformedURLException,
			ClassNotFoundException {
		File file = new File(
				"/Users/baesunghan/git/m2u/pinpoint/quickstart/agent/target/pinpoint-agent/pinpoint-bootstrap-1.6.1-SNAPSHOT.jar");
		File file2 = new File(
				"/Users/baesunghan/git/m2u/eyelink-agent/bootstrap/target/lib/netty-3.10.6.Final.jar");

//		URL url = file.toURL();
		URL[] urls = new URL[] { file.toURL(), file2.toURL() };

		ClassLoader cl = new URLClassLoader(urls);
//		Class cls = cl.loadClass("org.jboss.netty.channel.group.ChannelGroup");
//		Class cls3 = cl.loadClass("org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory");
//		Class cls2 = cl.loadClass("com.navercorp.pinpoint.bootstrap.LoadState");
	}

	public static void main(String[] args) {
		
		ELAgentURLClassLoaderTest et = new ELAgentURLClassLoaderTest();
		ChannelClassLoaderTest ccl = new ChannelClassLoaderTest();
		try {
			et.testJarFile();
			loadJarIndDir("/Users/baesunghan/git/m2u/eyelink-agent/bootstrap/target/lib2/");
			ccl.load();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static void loadJarIndDir(String dir){
        try{
            final URLClassLoader loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            final Method method = URLClassLoader.class
                                       .getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
             
            new File(dir).listFiles(new FileFilter() {
                public boolean accept(File jar) {
                    // jar 파일인 경우만 로딩
                    if( jar.toString().toLowerCase().contains(".jar") ){
                        try{
                            // URLClassLoader.addURL(URL url) 메소드 호출
                            method.invoke(loader, new Object[]{jar.toURI().toURL()});
                            System.out.println(jar.getName()+ " is loaded.");
                        }catch(Exception e){
                            System.out.println(jar.getName()+ " can't load.");
                        }
                    }
                    return false;
                }
            });
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


	
}
