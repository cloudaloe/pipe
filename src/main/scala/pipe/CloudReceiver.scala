/**
 * TODO: Implement exception handling
 */

package pipe

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.{ ChannelPipeline, ChannelPipelineFactory, Channels }
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.ExceptionEvent
import org.jboss.netty.channel.MessageEvent
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.handler.codec.http._
import java.util.concurrent.Executors
import java.net.InetSocketAddress

// packages for setting up for SSL before passing over to Netty to operate SSL
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.KeyManagerFactory
import java.security.KeyStore
import java.io.FileInputStream

import org.jboss.netty.handler.ssl.SslHandler


/**
 * Setup a netty pipeline
 */
class HttpServerPipelineFactory extends ChannelPipelineFactory {

  	/**
	* Creates an SSLEngine for netty to use.
	*  
	* Netty needs this set up for it before letting it run SSL.
	* At run-time, The SSL handshake may fail due to any of a myriad of issues on either network side.
	* On failure, the Java infrastructure will not always surface enough details of the root cause (such as the handshake process details),  
	* And so elevated logging through -Djavax.net.debug=ssl:handshake may be used for analysis.
	* 
	* 'SSL family' Algorithms that can be chosen are found (respectively of Java version) at:
	* http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#Cipher and 
	* http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SSLContext 
	* 
	* TODO: figure a way of logging handshake details only for failed handshakes (stackoverflow). 
	* 		or enable orderly logging of all handshakes into a designated log
	*/
	object sslSetup {

		val sslContext : SSLContext = SSLContext.getInstance("TLS")
		//ssl sslContext : SSLContext = SSLContext.getInstance("SSLv3") 
		  
		// Load the keystore from disk
		val keyStore = KeyStore.getInstance("JKS")
		val keyStoreFile = new FileInputStream("keystore.jks") 
		val keyStorePassword = "password"
		keyStore.load(keyStoreFile, keyStorePassword.toCharArray())
		keyStoreFile.close()
	
		// initialize both a KeyManagerFactory and a TrustManagerFactory with the key store
		val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
		val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
		keyManagerFactory.init(keyStore, keyStorePassword.toCharArray())
		trustManagerFactory.init(keyStore)   
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null) // need to enter keystore, cipher suites etc through these params
		
		def getSslEngine: SSLEngine = {
		  
	   	  //val sslContext : SSLContext = SSLContext.getDefault
		  //println("default parameters " + sslContext.getDefaultSSLParameters().toString())
		  //println("Trust manager default algorithm " + TrustManagerFactory.getDefaultAlgorithm)
		  //println("supported: " + sslContext.getSupportedSSLParameters.toString())
		  //println("supported cipher suites: " + sslEngine.getSupportedCipherSuites.mkString(", "))
		  //println("enabled cipher suites: " + sslEngine.getEnabledCipherSuites.mkString(", "))
	     	  
		  val sslEngine : SSLEngine = sslContext.createSSLEngine()
		  sslEngine.setUseClientMode(false) 	// javax.net.ssl.SSLEngine's special way of saying 'play a server on the SSL handshake'
		  sslEngine.setNeedClientAuth(false) 	// turn to 'true' to require client auth (http://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLEngine.html#setNeedClientAuth(boolean))
		  sslEngine
		}
	}
		
	/**
	 * Create a netty pipeline
	 */
	override def getPipeline: ChannelPipeline = {	
	  val pipeline = Channels.pipeline() 
	  val sslHandler = new SslHandler(sslSetup.getSslEngine)
	  // The following (setCloseOnSSLException) is necessary due to the odd backwards compatible default behavior of netty 3.5,
	  // as without it println(sslHandler.getCloseOnSSLException) still shows that this defaults to false.
	  sslHandler.setCloseOnSSLException(true);   
	  
	  //uncomment the following line for SSL
	  //pipeline.addLast("ssl", sslHandler);
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
	println("Http connection made")
	override def messageReceived(channelHandlerContext: ChannelHandlerContext, messageEvent: MessageEvent){
	  println("Http connection made from " + messageEvent.getRemoteAddress.toString)
	  println("Http message received: \n" + messageEvent.getMessage.toString)
	} 
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