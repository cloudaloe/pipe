import pipe._

object runner {
	def main(args: Array[String]) {
		
		println("main starting")
				
		val broker = new Broker(8082, 8083)
		val cloudReceiver = new CloudReceiver(8081)
		
	}
}