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
//import java.util.TimerTask
//import java.util.Timer
//import scala.actors.Future
//import scala.actors.Futures._
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
		var sendOnceDone = false
	 
		override def messageReceived(ctx: ChannelHandlerContext, response: HttpResponse){
			println("http response status: " + response.getStatus)
			canWrite=true
			if (!sendOnceDone)
			{
			  	sendOnceDone=true
				writer.write("bar")			  	
			}
		}
		
		override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable){
			println("exception at httpClientHandler" + e.printStackTrace)
		}	
	}
	
	object writer { // pending refactoring of course
	  var request: DefaultFullHttpRequest = _
	  def write(msg: String){
		  if (canWrite){
			  canWrite=false
	 		  request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"+msg)
			  //request.headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
			  try {
		 		  var writeFuture = channel.write(request).addListener(new ChannelFutureListener(){
		 			  def operationComplete(channelFuture: ChannelFuture){
		 			    if (channelFuture.isSuccess)
		 			    	println("write finished successfully")
		 			    else 
		 			    	println ("write failed: " + channelFuture.cause.getCause() + "\n" + channelFuture.cause.printStackTrace)
		 			  }
		 		  })
			  } 
			  catch {
			    case e: io.netty.channel.PartialFlushException => println("cause is " + e.getCause())
			    case unknown => println("cause is other")
			  }
			  finally {  }
		  }
		  else{
			  println("channel is not ready for writing yet")
		  }
	  }
	}
	
	var canWrite=false
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
 	var connectFuture = outgoingListener.connect(new InetSocketAddress("localhost", cloudPort)).addListener(new ChannelFutureListener(){
		    def operationComplete(channelFuture: ChannelFuture){
			 	if (!channelFuture.channel.isOpen()) {
			      println("Connection to cloud receiver failed.") // + future.getCause + future.getCause.printStackTrace)
			    }  
			 	else { 
			 		channel = channelFuture.channel
			 		if (ssl){
					 	channelFuture.channel.pipeline.get(classOf[SslHandler]).handshake.addListener(new ChannelFutureListener(){
					 		def operationComplete(channelFuture: ChannelFuture){
					 			canWrite=true
					 			start				 		  
					 		}
					 	})
			 		}
			 		else {
				 		canWrite=true
				 		start
			 		}
			 	}
		    }
		  })

	def start{ 
 	  writer.write("foo")
 	  
 	  
 	  /* val f1 = future {
 	    Thread.sleep(3000)
 	    writer.write("bar")
 	  }*/
 	  
 	  /*object defer extends java.util.TimerTask{
 		  override def run{ writer.write("bar") } 
 	  }*/
 	
 	  
 	}
}
