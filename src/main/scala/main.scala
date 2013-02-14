import scala.sys.process._

import pipe._

object runner {
	def main(args: Array[String]) {
		
		println("Main starting")
				
		val broker = new Broker(incomingPort=8082, outgoingPort=8083)
		val cloudReceiver = new CloudReceiver(8081)

		val httpGet1 = Seq("curl", "localhost:8081/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr
		val httpGet2 = Seq("curl", "localhost:8082/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr		
	}
}