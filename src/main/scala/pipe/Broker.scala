/**
 * Broker agent
 */

package pipe

import java.net.InetSocketAddress
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.handler.codec.http._
import io.netty.channel.ChannelInitializer
import io.netty.handler.ssl.SslHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.Bootstrap
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.ChannelInboundMessageHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelFutureListener
import io.netty.channel.socket.nio.NioSocketChannel
//import akka.actor.Actor

/**
 * Send out to the cloud
 */
class Broker (incomingPort: Int, cloudPort: Int, ssl: Boolean) {
  
	/**
	 * Setup a client pipeline 
	 */
	class clientInitializer(ssl: Boolean) extends ChannelInitializer[Channel] {
	  override def initChannel(channel: Channel){
	    val pipeline = channel.pipeline
	    if (ssl){
			var sslHandler = new SslHandler(new sslSetup(false).getSslEngine)
			pipeline.addLast("ssl", sslHandler);
	    }
	    pipeline.addLast("HttpRequestEncoder", new HttpClientCodec)
	    //pipeline.addLast("inflater", new HttpContentDecompressor());
	    //pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536))
		//pipeline.addLast("chunkedWriter", new ChunkedWriteHandler)
	    pipeline.addLast("handler", new httpClientHandler)	    
		}
	}  
	
	/** 
	 * handle server's http response or connection errors
	 */
	class httpClientHandler extends ChannelInboundMessageHandlerAdapter[HttpResponse]{
	  
		override def messageReceived(ctx: ChannelHandlerContext, response: HttpResponse){
			println("http response status: " + response.getStatus)
		}
		
		override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable){
			println("exception at httpClientHandler" + e.printStackTrace)
		}	
	}
	
	object writer { // pending refactoring of course
	  var canWrite=false
	  var request: DefaultHttpRequest = _
	  def write(msg: String){
		  if (canWrite){
	 		  request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"+msg)
	 		  var writeFuture = channel.write(request).addListener(new ChannelFutureListener(){
	 			  def operationComplete(channelFuture: ChannelFuture){
	 				  println("write finished")
	 			  }
	 		  })
		  }
		  else println("channel is not ready for writing yet")
	  }
	}
	
 	println("Broker object starting")

 	/**
 	 * 
 	 * Initialize a single outgoing connection to the cloud
 	 * 
 	 * Netty will pick the local port for outgoing communication to the cloud side - automatically. This behavior can of course be changed.
 	 * TODO: Will this hang if the server stalls?
 	 * 
 	 */
	val outgoingListener = new Bootstrap()
 		outgoingListener.group(new NioEventLoopGroup)
 						.channel(classOf[NioSocketChannel])
 						.handler(new clientInitializer(ssl))
 						
 	var channel: Channel = _

 	// connect and explicitly wait on handshake
 	val connectFuture = outgoingListener.connect(new InetSocketAddress("localhost", cloudPort)).addListener(new ChannelFutureListener(){
		    def operationComplete(channelFuture: ChannelFuture){
			 	if (!channelFuture.channel.isOpen()) {
			      println("Connection to cloud receiver failed.") // + future.getCause + future.getCause.printStackTrace)
			    }  
			 	else {
			 		channel = channelFuture.channel			 	  
				 	channelFuture.channel.pipeline.get(classOf[SslHandler]).handshake.addListener(new ChannelFutureListener(){
				 		def operationComplete(channelFuture: ChannelFuture){
				 			writer.canWrite=true
				 			start				 		  
				 		}
				 	})
			 	}
		    }
		  })

	def start{ 
 	  writer.write("foo") 
 	  writer.write("bar") 
 	}
		  
}
