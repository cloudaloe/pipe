import scala.sys.process._
import pipe._
import pipe.Broker
import pipe.CloudReceiver
//import io.netty.util.internal.JdkLoggerFactory
//import io.netty.util.internal.Slf4JLoggerFactory
import io.netty.util.internal.logging.Log4JLoggerFactory
import io.netty.util.internal.logging.InternalLoggerFactory
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator


object runner {
	def main(args: Array[String]) {
		
		//val loggerName = this.getClass.getName
		val myLogger = Logger.getLogger("pipe")
		BasicConfigurator.configure
		myLogger.debug("test debug message")
	  
	    InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory)
	  
	    println("Main starting")
				
		val cloudReceiver = new CloudReceiver(Config.cloudPort, ssl=false)
		val broker = new Broker(incomingPort=Config.incomingPort, cloudPort=Config.cloudPort, ssl=false)		

		//curl examples at http://www.yilmazhuseyin.com/blog/dev/curl-tutorial-examples-usage/
		//val httpGet1 = Seq("curl", "localhost:8081/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr
		//val httpGet2 = Seq("curl", "localhost:8082/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr
	     
	}
}