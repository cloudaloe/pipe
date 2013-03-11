import pipe._
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator
import io.netty.util.internal.logging.Log4JLoggerFactory
import io.netty.util.internal.logging.InternalLoggerFactory

object brokerRunner extends App {
		
  		//val myLogger = Logger.getLogger("pipe")
		//BasicConfigurator.configure
		//myLogger.debug("test debug message")
	  
		BasicConfigurator.configure	      
		InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory)
	      
  
		println("brokerRunner starting")
		val broker = new Broker(incomingPort=Config.incomingPort, cloudPort=Config.cloudPort, ssl=false)
		
}