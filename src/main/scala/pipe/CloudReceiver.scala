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
import javax.net.ssl.TrustManagerFactory

import org.jboss.netty.handler.ssl.SslHandler

/**
 * 
 *
 */
  
class HttpServerPipelineFactory extends ChannelPipelineFactory {
	
  def getSslEngine: SSLEngine = {
   	  //val sslContext : SSLContext = SSLContext.getDefault
	  //println("default parameters " + sslContext.getDefaultSSLParameters().toString())    
	  val sslContext : SSLContext = SSLContext.getInstance("SSLv3")
	  
	  val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
	  
	  sslContext.init(null, null, null) // need to enter keystore, cipher suites etc through these params
	  //sslContext.init(null, trustManagerFactory.getTrustManagers(), null) // need to enter keystore, cipher suites etc through these params	  
	  //println("supported: " + sslContext.getSupportedSSLParameters.toString())	  
	  val sslEngine : SSLEngine = sslContext.createSSLEngine()
	  sslEngine.setUseClientMode(false) // javax.net.ssl.SSLEngine's special way of saying 'play a server on the SSL handshake'
	  sslEngine.setNeedClientAuth(true) // require client auth (http://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLEngine.html#setNeedClientAuth(boolean))
	  sslEngine
	  } 
  
  def getPipeline: ChannelPipeline = {	
	  // Create a Netty pipeline
	  val pipeline = Channels.pipeline() 
	  val sslHandler = new SslHandler(getSslEngine)
	  // The following (setCloseOnSSLException) is necessary due to the odd backwards compatible default behavior of netty 3.5,
	  // as without it println(sslHandler.getCloseOnSSLException) still shows that this defaults to false.
	  sslHandler.setCloseOnSSLException(true); //  

	  
	  pipeline.addLast("ssl", sslHandler);
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