import pipe._

object brokerRunner extends App {
		
		println("brokerRunner starting")
		val broker = new Broker(incomingPort=Config.incomingPort, cloudPort=Config.cloudPort, ssl=true)
		
}