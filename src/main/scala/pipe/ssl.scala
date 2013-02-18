/**
 * TODO: Implement exception handling for stuff like connection closing, handshake failure 
 */

package pipe

//import scala.tools.nsc.interpreter.ILoop.{break, breakIf}
import io.netty.bootstrap.ServerBootstrap
import io.netty.handler.codec.http._
import io.netty.handler.ssl.SslHandler
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.KeyManagerFactory
import java.security.KeyStore
import java.io.FileInputStream


/**
* Creates an SSLEngine for netty to use.
*  
* Netty needs this set up handled for it before letting it run SSL.
* At run-time, The SSL handshake may fail due to any of a myriad of justified issues on either network side.
* On failure, the Java infrastructure will not always surface enough details of the root cause (such as the handshake process details),  
* And so elevated logging through -Djavax.net.debug=ssl:handshake may be used for analysis.
* 
* 'SSL family' Algorithms that can be chosen are found (respectively of the Java version) at:
* http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#Cipher and 
* http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SSLContext 
* 
* TODO: figure a way of logging handshake details only for failed handshakes (stackoverflow). 
* 		or enable orderly logging of all handshakes into a designated log
*/
class sslSetup (sslServer: Boolean){

	val sslContext : SSLContext = SSLContext.getInstance(Config.sslProtocol)
	//ssl sslContext : SSLContext = SSLContext.getInstance("SSLv3") 
	  
	// Load the keystore from disk
	val keyStore = KeyStore.getInstance("JKS")
	val keyStoreFile = new FileInputStream(Config.keyStorePath) 
	val keyStorePassword = "password"
	keyStore.load(keyStoreFile, keyStorePassword.toCharArray())
	keyStoreFile.close()

	// initialize both a KeyManagerFactory and a TrustManagerFactory with the keystore
	val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
	val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
	keyManagerFactory.init(keyStore, keyStorePassword.toCharArray())
	trustManagerFactory.init(keyStore)   
	sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null) // need to enter keystore, cipher suites etc through these params
	
	def getSslEngine: SSLEngine = {
	  val sslEngine : SSLEngine = sslContext.createSSLEngine()
	  if (sslServer) { 
	    sslEngine.setUseClientMode(false) // javax.net.ssl.SSLEngine's special way of saying 'play a server on the SSL handshake'
	    sslEngine.setNeedClientAuth(false) 	// turn to 'true' to require client auth (http://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLEngine.html#setNeedClientAuth(boolean))
	  }
	  else { 
	    sslEngine.setUseClientMode(true) 
	  }
	  sslEngine
	}
}


/**

/**
 * Setup a server pipeline 
 */
class HttpServerPipelineFactory(ssl: Boolean, sslServer: Boolean = true) extends ChannelPipelineFactory {
	/**
	 * netty pipeline creator
	 */
	override def getPipeline: ChannelPipeline = {	
	  val pipeline = Channels.pipeline()
	  if (ssl) {
	    val sslHandler = new SslHandler(new sslSetup(sslServer).getSslEngine)
	    // The following (setCloseOnSSLException) is necessary due to the odd backwards compatible default behavior of netty 3.5,
	    // as without it println(sslHandler.getCloseOnSSLException) still shows that this defaults to false.
	    //sslHandler.setCloseOnSSLException(true);   
	    pipeline.addLast("ssl", sslHandler);
	  }
  
	  pipeline.addLast("decoder", new HttpObjectDecoder())
	  
	  // Uncomment the following line to aggregate http chunks
	  //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
	  pipeline.addLast("encoder", new HttpObjectEncoder())
	  
	  // Remove the following line to compress content
	  //pipeline.addLast("deflater", new HttpContentCompressor())
	  
	  pipeline.addLast("handler", new HttpRequestHandler())
	  
	  pipeline
	}
}



/**
 * Setup a client pipeline 
 */
class HttpClientPipelineFactory(ssl: Boolean) extends ChannelPipelineFactory {
	/**
	 * netty pipeline creator
	 */
	override def getPipeline: ChannelPipeline = {	
	  val pipeline = Channels.pipeline()
	  if (ssl) {
	    val sslHandler = new SslHandler(new sslSetup(sslServer=false).getSslEngine)
	    // The following (setCloseOnSSLException) is necessary due to the odd backwards compatible default behavior of netty 3.5,
	    // as without it println(sslHandler.getCloseOnSSLException) still shows that this defaults to false.
	    sslHandler.setCloseOnSSLException(true);   
	    pipeline.addLast("ssl", sslHandler);
	  }
  
	  pipeline.addLast("codec", new HttpClientCodec);
	  
	  // Uncomment the following line for automatic content decompression.
	  //pipeline.addLast("inflater", new HttpContentDecompressor());
	  
	  // Uncomment the following line to aggregate http chunks
	  //pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
	  
	  //pipeline.addLast("decoder", new HttpRequestDecoder())
	  
	  pipeline.addLast("handler", new HttpResponseHandler)	  
  
	  pipeline
	}
}

class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	override def messageReceived(channelHandlerContext: ChannelHandlerContext, messageEvent: MessageEvent){
	  println("Http response received")
	}
}
*/