import pipe._

object cloudReceiverRunner extends App {
		
		println("cloudReceiverRunner starting")
		val cloudReceiver = new CloudReceiver(Config.cloudPort, ssl=true)
		
}	