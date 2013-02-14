/**
 * Broker agent
 */

package pipe

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress

class Broker (incomingPort: Int, outgoingPort: Int) {

 	println("Broker object starting")
				
	val incomingListener = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	incomingListener.setPipelineFactory(new HttpServerPipelineFactory(ssl=false))

    // Bind and start to accept incoming connections.
    incomingListener.bind(new InetSocketAddress(incomingPort))
    
    
    val outgoing = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	outgoing.setPipelineFactory(new HttpServerPipelineFactory(ssl=true, sslServer=false))

    // Bind and start to accept incoming connections.
    outgoing.bind(new InetSocketAddress(outgoingPort))
    
}
