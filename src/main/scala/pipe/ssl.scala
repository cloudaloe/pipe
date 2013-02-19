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
* TODO: Further implement exception handling
* 
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


