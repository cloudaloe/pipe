/**
 * Broker agent
 */

package pipe

import io.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelPipeline
import io.netty.handler.ssl.SslHandler
import io.netty.handler.codec.http._
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpObjectDecoder
import io.netty.handler.codec.http.HttpObjectEncoder
import io.netty.handler.codec.serialization.ObjectDecoder
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.ChannelInboundMessageHandlerAdapter

class CloudReceiver (port: Int) {

	class serverInitializer(ssl: Boolean) extends ChannelInitializer[Channel] {
	  //override def inboundBufferUpdate(ctx: ChannelHandlerContext, in: ByteBuf){}
	  
	  override def initChannel(channel: Channel){
	    val pipeline = channel.pipeline
	    if (ssl){
			val sslHandler = new SslHandler(new sslSetup(true).getSslEngine)
			pipeline.addLast("ssl", sslHandler);
	    }
		    //pipeline.addLast("httpCodec", new HttpServerCodec)
	    pipeline.addLast("HttpRequestDecoder", new HttpRequestDecoder)
	    //pipeline.addLast("HttpResponseDecoder", new HttpResponseDecoder)      
		pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536))
		//pipeline.addLast("chunkedWriter", new ChunkedWriteHandler)
		// MyHandler contains code that blocks so add it with the
		// EventExecutor to the pipeline.
		//pipeline.addLast(executor, "handler", new MyHandler());
		
		pipeline.addLast("handler", new HttpRequestHandler)
		
		pipeline
	  }
	  
	  override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable){}
	}
	
	/**
	 * handle a connection that comes in through the pipeline
	 */
	class HttpRequestHandler extends ChannelInboundMessageHandlerAdapter[FullHttpRequest] {
		//println("Http connection made")
		
		/*
		override def channelConnected(channelHandlerContext: ChannelHandlerContext, channelStateEvent: ChannelStateEvent){
			println("Channel connected to peer " + channelHandlerContext.getChannel.getRemoteAddress)	  
		}
		*/
		
		override def messageReceived(channelHandlerContext: ChannelHandlerContext, httpRequest: FullHttpRequest){
			println("Http message received from " + channelHandlerContext.channel.remoteAddress)
			println("Http message uri is " + httpRequest.getUri)
			println("Http message method is " + httpRequest.getMethod.toString)
			
			httpRequest.getMethod match {
			  case HttpMethod.POST => {
			    println("Handling POST request")
			  }
			  case other => {
				println("Request of type " + other + " will be ignored")
			  }
					  
			}
		}
		
		/* 
		 * This will fire when a peer is disconnected
		override def channelDisconnected(channelHandlerContext: ChannelHandlerContext, channelStateEvent: ChannelStateEvent){
		  println("Channel disconnected from peer " + channelHandlerContext.getChannel.getRemoteAddress)
		}
		*/
		
		/*
		 * Error handler for cases e.g. the peer has unexpectedly closed the connection
		override def exceptionCaught(channelHandlerContext: ChannelHandlerContext, exceptionEvent: ExceptionEvent){
		  println("Error detected on connection: " + exceptionEvent.getCause.toString)
		  // print more details if this ever becomes very helpful
		}
		*/
	} 	
  
 	println("CloudReceiver object starting")
				
	val incomingListener = new ServerBootstrap()
 		incomingListener.group(new NioEventLoopGroup, new NioEventLoopGroup)
 						.channel(classOf[NioServerSocketChannel])
 						.childHandler(new serverInitializer(ssl=true))
 	
 	incomingListener.bind(new InetSocketAddress(port)).sync().channel().closeFuture().sync()
 						
    //new NioServerSocketChannelFactory(Executors.newCachedThreadPool, Executors.newCachedThreadPool))
		
	//incomingListener.setPipelineFactory(new HttpServerPipelineFactory(ssl=false, sslServer=true))

    // Bind and start to accept incoming connections.
    //incomingListener.bind(new InetSocketAddress(port))
}

 