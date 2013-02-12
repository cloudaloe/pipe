/**
 * Broker agent
 */

package pipe

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
//import pipe.network._

class Broker {

 	println("Broker object starting")
				
	val incomingListener = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	incomingListener.setPipelineFactory(new HttpServerPipelineFactory)

    // Bind and start to accept incoming connections.
    incomingListener.bind(new InetSocketAddress(8083))
    incomingListener.bind(new InetSocketAddress(8084))	    
}

/*
object runner2 {
	def main(args: Array[String]) {
		
		println("main starting")
				
		val cr = new Broker
		
	}
} */
 