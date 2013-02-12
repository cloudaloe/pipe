/**
 * Cloud simulation server
 */

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
import pipe._

class CloudReceiver {

 	println("CloudReceiver object starting")
				
	val incomingListener = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	incomingListener.setPipelineFactory(new HttpServerPipelineFactory)

    // Bind and start to accept incoming connections.
    incomingListener.bind(new InetSocketAddress(8081))
    incomingListener.bind(new InetSocketAddress(8082))	    
}

object runner {
	def main(args: Array[String]) {
		
		println("main starting")
				
		val cr = new CloudReceiver
		
	}
}

/**
 * ideas:
 * 
 * need to use FileChannel.transferTo for moving data directly from files to outbound sockets,
 * as in http://kafka.apache.org/design.html, but only for non SSL.
 *
 * may use the Non-blocking IO approach for scalability, as the approach of
 * http://stackoverflow.com/questions/2508131/the-scala-way-to-use-one-actor-per-socket-connection 
 */
