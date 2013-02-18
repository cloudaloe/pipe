/**
 * Broker agent
 */

package pipe

import io.netty.bootstrap.ServerBootstrap
import java.util.concurrent.Executors
import java.net.InetSocketAddress
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.handler.codec.http._
import io.netty.channel.ChannelInitializer
import io.netty.handler.ssl.SslHandler
import io.netty.channel.ChannelPipeline
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.bootstrap.Bootstrap
import java.util.concurrent.TimeUnit
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.ChannelInboundMessageHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelFutureListener

/**
 * Setup a client pipeline 
 */

class Broker (incomingPort: Int, cloudPort: Int, ssl: Boolean) {
	class clientInitializer(ssl: Boolean) extends ChannelInitializer[Channel] {
	
	  //override def inboundBufferUpdate(ctx: ChannelHandlerContext, in: ByteBuf){}
	  
	  override def initChannel(channel: Channel){
	    val pipeline = channel.pipeline
	    if (ssl){
			var sslHandler = new SslHandler(new sslSetup(false).getSslEngine)
			pipeline.addLast("ssl", sslHandler);
	    }
		    //pipeline.addLast("httpCodec", new HttpServerCodec)
	    pipeline.addLast("HttpRequestEncoder", new HttpClientCodec)
	    //pipeline.addLast("inflater", new HttpContentDecompressor());
	    
	    pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536))
		//pipeline.addLast("chunkedWriter", new ChunkedWriteHandler)
		// MyHandler contains code that blocks so add it with the
		// EventExecutor to the pipeline.
		//pipeline.addLast(executor, "handler", new MyHandler());
	    
	    pipeline.addLast("handler", new httpClientHandler)	    

	    //var handshakeFuture = sslHandler.handshake()
		//handshakeFuture.sync()
	    
		pipeline	    
		}
	}  
	
	/** 
	 * handle server's http response
	 */
	class httpClientHandler extends ChannelInboundMessageHandlerAdapter[HttpResponse]{

	override def messageReceived(ctx: ChannelHandlerContext, response: HttpResponse){
		println(response.getStatus)
	}
  
	override def exceptionCaught(ctx: ChannelHandlerContext, e: Throwable){
		println(e.printStackTrace)
	}	
		
	}
	
	// netty will pick the local port for outgoing communication to the cloud side - automatically
	// this behavior can of course be changed.
    
 	println("Broker object starting")
				
	val outgoingListener = new Bootstrap()
 		outgoingListener.group(new NioEventLoopGroup)
 						.channel(classOf[NioSocketChannel])
 						.handler(new clientInitializer(ssl))

 	val connectFuture = outgoingListener.connect(new InetSocketAddress("localhost", cloudPort)).addListener(new ChannelFutureListener(){
		    def operationComplete(channelFuture: ChannelFuture){
		    	println("in operation complete 2")		      
			  	println(channelFuture.channel.isOpen.toString + channelFuture.channel.isActive.toString + channelFuture.channel.isRegistered.toString + channelFuture.channel.localAddress + channelFuture.channel.remoteAddress)
			 	if (!channelFuture.channel.isOpen()) {
			      println("Connection to cloud receiver failed: ") // + future.getCause + future.getCause.printStackTrace)
			    } //TODO: also check for success v.s. not completed. Will this hang if the server stalls?
			 	
				var request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello")
				//request.headers.set(HttpHeaders.Names.HOST, "localhost")
			    //request.headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE)
			    //request.headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP)
				//println(request.getMethod)
				//println(request.getProtocolVersion)
				//println(request.getUri)
				var writeFuture = channelFuture.channel.write(request)
				//channel.flush
				//channel.closeFuture.sync
				channelFuture.await(7, TimeUnit.SECONDS)
				println("isDone :", channelFuture.isDone)
				println("isSuccess :", channelFuture.isSuccess)
		    }
		  })				
}
