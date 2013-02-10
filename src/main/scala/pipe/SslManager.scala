import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManagerFactory

/**
 *	Reads key stores for SSL/TLS
 *
 * 	For further inspiration see about setting up an SSL context and loading from a key store see 
 * 	http://docs.jboss.org/netty/3.2/xref/org/jboss/netty/example/securechat/SecureChatSslContextFactory.html
 */
class SslFactory {
	val sslContext : SSLContext = SSLContext.getInstance("TLS")
	val sslEngine : SSLEngine = sslContext.createSSLEngine() 	
}