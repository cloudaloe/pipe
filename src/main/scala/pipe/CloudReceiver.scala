/**
 *
 */

package pipe

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.{ ChannelPipeline, ChannelPipelineFactory, Channels }
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.handler.codec.http._
import java.util.concurrent.Executors
import java.net.InetSocketAddress

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import org.jboss.netty.handler.ssl.SslHandler

/**
 * 
 *
 */
  
/**class a extends SimpleChannelHandler {
	def createSSLEngine(): SSLEngine = {	
		val sslContext : SSLContext = SSLContext.getInstance("TLS")
		val sslEngine : SSLEngine = sslContext.createSSLEngine()
		sslEngine
	}
}*/
	  

class HttpServerPipelineFactory extends ChannelPipelineFactory {
	def getPipeline: ChannelPipeline = {	
	  // Create a default pipeline implementation.
	  val pipeline = Channels.pipeline() 
	    
      // Uncomment the following line if you want HTTPS
      //val engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
	  val sslContext : SSLContext = SSLContext.getInstance("TLS")
	  sslContext.init(null, null, null) // need to enter keystore, cipher suites etc through these params
	  val sslEngine : SSLEngine = sslContext.createSSLEngine()
	  sslEngine.setUseClientMode(false); // javax.net.ssl.SSLEngine's special way of saying 'play a server on the SSL handshake'   
	  pipeline.addLast("ssl", new SslHandler(sslEngine));
	  
  
      pipeline.addLast("decoder", new HttpRequestDecoder())
      // Uncomment the following line if you don't want to handle HttpChunks.
      //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
      pipeline.addLast("encoder", new HttpResponseEncoder())
      // Remove the following line if you don't want automatic content compression.
      //pipeline.addLast("deflater", new HttpContentCompressor())
      pipeline.addLast("handler", new HttpRequestHandler())
      pipeline
     }
}

class HttpRequestHandler extends SimpleChannelUpstreamHandler {
	println("http request received")
	
}

class CloudReceiver {

 		println("CloudReceiver object starting")
				
		val incomingListener = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
		incomingListener.setPipelineFactory(new HttpServerPipelineFactory)

	    // Bind and start to accept incoming connections.
	    incomingListener.bind(new InetSocketAddress(8081))
	    incomingListener.bind(new InetSocketAddress(8082))	    
}