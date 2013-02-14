import scala.sys.process._

import pipe._

object runner {
	def main(args: Array[String]) {
		
		println("Main starting")
				
		val cloudReceiver = new CloudReceiver(Config.cloudPort)
		val broker = new Broker(incomingPort=Config.incomingPort, cloudPort=Config.cloudPort)		

		//val httpGet1 = Seq("curl", "localhost:8081/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr
		//val httpGet2 = Seq("curl", "localhost:8082/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr		
	}
}