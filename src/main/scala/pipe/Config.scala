package pipe

/**
 * Configuration object class.
 * TODO: obtain the configuration from file, or https://github.com/typesafehub/config | https://github.com/twitter/ostrich#readme
 *
 */
object Config {
	val keyStorePath = "keystore.jks"
    val sslProtocol = "TLS"
    val incomingPort = 8082
    val cloudPort = 8081
}