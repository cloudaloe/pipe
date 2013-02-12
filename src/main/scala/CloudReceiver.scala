/**
 * Broker agent
 */

package pipe

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
//import pipe.network._

class CloudReceiver {

 	println("Broker object starting")
				
	val incomingListener = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	incomingListener.setPipelineFactory(new HttpServerPipelineFactory)

    // Bind and start to accept incoming connections.
    incomingListener.bind(new InetSocketAddress(8081))
    incomingListener.bind(new InetSocketAddress(8082))	    
}

/*
object runner {
	def main(args: Array[String]) {
		
		println("main starting")
				
		val cr = new CloudReceiver
		
	}
} */
 