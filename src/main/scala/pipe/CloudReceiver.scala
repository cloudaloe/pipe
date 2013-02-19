/**
 * Cloud Receiver 
 */

package pipe

import io.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.Channel
import io.netty.handler.ssl.SslHandler
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.ChannelInboundMessageHandlerAdapter
import io.netty.channel.socket.nio.NioServerSocketChannel

class CloudReceiver (port: Int, ssl: Boolean) {

	class serverInitializer(ssl: Boolean) extends ChannelInitializer[Channel] {
	  
	  override def initChannel(channel: Channel){
	    val pipeline = channel.pipeline
	    if (ssl){
			val sslHandler = new SslHandler(new sslSetup(true).getSslEngine)
			pipeline.addLast("ssl", sslHandler);
	    }
		    //pipeline.addLast("httpCodec", new HttpServerCodec)
	    pipeline.addLast("HttpRequestDecoder", new HttpRequestDecoder)
		pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536))
		//pipeline.addLast("chunkedWriter", new ChunkedWriteHandler)
	    pipeline.addLast("encoder", new HttpResponseEncoder)	    
		pipeline.addLast("handler", new HttpRequestHandler)
	  }
	  
	  override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable){
	    println(e.printStackTrace)
	  }
	  
	}
	
	/**
	 * handle a connection that comes in through the pipeline
	 */
	class HttpRequestHandler extends ChannelInboundMessageHandlerAdapter[FullHttpRequest] {
		println("Http connection made")
			  
		override def messageReceived(ctx: ChannelHandlerContext, httpRequest: FullHttpRequest){
			println("Http message received from " + ctx.channel.remoteAddress)
			println("Http message uri is " + httpRequest.getUri)
			println("Http message method is " + httpRequest.getMethod.toString)
			
			httpRequest.getMethod match {
			  case HttpMethod.POST => {
			    println("Handling POST request")
			    ctx.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)) // .headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
			    // TODO: handle the POST data
			  }
			  case other => {
				println("Request of type " + other + " will be ignored")
				ctx.write(new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)) // later switch to NOT_IMPLEMENTED 
			  }
					  
			}
		}

		// Error handler for cases e.g. the peer has unexpectedly closed the connection
		override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable){
		  println("Error detected on connection with remote peer " + ctx.channel.remoteAddress() + cause.printStackTrace)
		}
	} 	
  
 	println("CloudReceiver object starting")
				
	val incomingListener = new ServerBootstrap()
 		incomingListener.group(new NioEventLoopGroup, new NioEventLoopGroup)
 						.channel(classOf[NioServerSocketChannel])
 						.childHandler(new serverInitializer(ssl))
 	
 	incomingListener.bind(new InetSocketAddress(port))// 
}
