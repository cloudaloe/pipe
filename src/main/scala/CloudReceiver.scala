/**
 * Broker agent
 */

package pipe

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress

class CloudReceiver (port: Int) {

 	println("CloudReceiver object starting")
				
	val incomingListener = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	incomingListener.setPipelineFactory(new HttpServerPipelineFactory)

    // Bind and start to accept incoming connections.
    incomingListener.bind(new InetSocketAddress(port))
}

 