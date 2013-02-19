import scala.sys.process._
import pipe._
import pipe.Broker
import pipe.CloudReceiver

object runner {
	def main(args: Array[String]) {
		
		println("Main starting")
				
		val cloudReceiver = new CloudReceiver(Config.cloudPort, ssl=true)
		val broker = new Broker(incomingPort=Config.incomingPort, cloudPort=Config.cloudPort, ssl=true)		

		//curl examples at http://www.yilmazhuseyin.com/blog/dev/curl-tutorial-examples-usage/
		//val httpGet1 = Seq("curl", "localhost:8081/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr
		//val httpGet2 = Seq("curl", "localhost:8082/aaaaa") lines_! ProcessLogger(line => ()) // execute and suppress stderr		
	}
}