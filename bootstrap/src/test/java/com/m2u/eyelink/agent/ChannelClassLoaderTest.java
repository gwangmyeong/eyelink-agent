package com.m2u.eyelink.agent;

import java.util.concurrent.Executors;

import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class ChannelClassLoaderTest {
	
	
	public void load() {
		 ChannelFactory factory =
		            new NioServerSocketChannelFactory(
		                    Executors.newCachedThreadPool(),
		                    Executors.newCachedThreadPool());
//		this.serverChannel = 
		System.out.println("ChannelClassLoaderTest Success");
	}
}
