/**
 * Broker agent
 */

package pipe

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
import org.jboss.netty.channel.Channel
import org.jboss.netty.channel.ChannelFuture
import org.jboss.netty.bootstrap.ClientBootstrap
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory


class Broker (incomingPort: Int, cloudPort: Int) {
// netty will pick the local port for outgoing communication to the cloud side - automatically
// this behavior can of course be changed.
    
 	println("Broker object starting")
				
	val incomingListener = new ServerBootstrap(
	    new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	incomingListener.setPipelineFactory(new HttpServerPipelineFactory(ssl=false))

    // Bind and start to accept incoming connections.
    incomingListener.bind(new InetSocketAddress(incomingPort))
    
    
    val outgoingListener = new ClientBootstrap(
	    new NioClientSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	outgoingListener.setPipelineFactory(new HttpClientPipelineFactory(ssl=false))

    // Bind and start to accept incoming connections.
    var future = outgoingListener.connect(new InetSocketAddress("localhost", cloudPort));
 	var channel: Channel = future.awaitUninterruptibly.getChannel
 	if (!future.isSuccess) {
      println("Connection to cloud receiver failed: " + future.getCause + future.getCause.printStackTrace)
      outgoingListener.releaseExternalResources;
    } //TODO: also check for success v.s. not completed. Will this hang if the server stalls?
 	
	var request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/hello")
	//request.setHeader(HttpHeaders.Names.HOST, "localhost")
    //request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE)
    //request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP)
	
	//println(request.getMethod)
	//println(request.getProtocolVersion)
	//println(request.getUri)
	var writeFuture = channel.write(request)
	println("isDone :", writeFuture.isDone)
	println("isSuccess :", writeFuture.isSuccess)
}
