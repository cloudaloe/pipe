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

/**
 * 
 *
 */
  
class HttpServerPipelineFactory extends ChannelPipelineFactory {
	def getPipeline: ChannelPipeline = {	
	  // Create a default pipeline implementation.
	  val pipeline = Channels.pipeline() 
	    
      // Uncomment the following line if you want HTTPS
      //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
      //engine.setUseClientMode(false);
      //pipeline.addLast("ssl", new SslHandler(engine));
  
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
	println("http request receiveds")
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