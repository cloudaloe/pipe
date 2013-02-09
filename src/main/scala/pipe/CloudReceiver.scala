/**
 *
 */
package main.scala.pipe

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.{ ChannelPipeline, ChannelPipelineFactory, Channels }
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import java.util.concurrent.Executors
import java.net.InetSocketAddress

/**
 * 
 *
 */
class CloudReceiver {

 		println("CloudReceiver object starting")
				
		val incomingListener = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
		incomingListener.setPipelineFactory(new ChannelPipelineFactory {
			override def getPipeline: ChannelPipeline = Channels.pipeline(new handler)
		})

	    // Bind and start to accept incoming connections.
	    incomingListener.bind(new InetSocketAddress(8081))
	    incomingListener.bind(new InetSocketAddress(8082))	    
}
  
class handler extends SimpleChannelUpstreamHandler {

}