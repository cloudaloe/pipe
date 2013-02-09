/**
 * need to use FileChannel.transferTo for moving data directly from files to outbound sockets,
 * as in http://kafka.apache.org/design.html, but only for non SSL.
 */

/**
 * may use the Non-blocking IO approach for scalability, as the approach of
 * http://stackoverflow.com/questions/2508131/the-scala-way-to-use-one-actor-per-socket-connection 
 */

import pipe.CloudReceiver

object runner {
	def main(args: Array[String]) {
		
		println("main starting")
				
		val cr = new CloudReceiver
		
	}
}


