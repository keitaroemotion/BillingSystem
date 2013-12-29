package Test

import org.junit._
import java.util.Calendar
import com.XBS._
import org.scalatest.ThreadAwareness

class LogTest {
	@Test def dateTest{
		log.println("moominmamalog")
	}
	
	var log = new LoggerX()
	
	@Test def MethodTracingTest{
		println("trace |"+log.GetMethodName)
	}
	
	
	
}