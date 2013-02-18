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

/**
 * Setup a client pipeline 
 */

class Broker (incomingPort: Int, cloudPort: Int) {
  
	class serverInitializer(ssl: Boolean) extends ChannelInitializer[Channel] {
	
	  //override def inboundBufferUpdate(ctx: ChannelHandlerContext, in: ByteBuf){}
	  
	  override def initChannel(channel: Channel){
	    val pipeline = channel.pipeline
	    if (ssl){
			val sslHandler = new SslHandler(new sslSetup(true).getSslEngine)
			pipeline.addLast("ssl", sslHandler);
	    }
		    //pipeline.addLast("httpCodec", new HttpServerCodec)
	    pipeline.addLast("HttpRequestEncoder", new HttpRequestEncoder)
	    //pipeline.addLast("HttpResponseDecoder", new HttpResponseDecoder)      
		//pipeline.addLast("httpAggregator", new HttpObjectAggregator(65536))
		//pipeline.addLast("chunkedWriter", new ChunkedWriteHandler)
		// MyHandler contains code that blocks so add it with the
		// EventExecutor to the pipeline.
		//pipeline.addLast(executor, "handler", new MyHandler());
		    	    
		pipeline	    
		}
	}  
    
	// netty will pick the local port for outgoing communication to the cloud side - automatically
	// this behavior can of course be changed.
    
 	println("Broker object starting")
				
	val outgoingListener = new ServerBootstrap()
 		outgoingListener.group(new NioEventLoopGroup, new NioEventLoopGroup)
 						.channel(classOf[NioServerSocketChannel])
 						.childHandler(new serverInitializer(ssl=true))

 						// Bind and start to accept incoming connections.
    
 	var channel = outgoingListener.bind(new InetSocketAddress("localhost", cloudPort)).sync().channel()
 	
 	if (!channel.isOpen()) {
      println("Connection to cloud receiver failed: ") // + future.getCause + future.getCause.printStackTrace)
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
