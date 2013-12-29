package Test

import org.junit._

class Calc {
  
  
	@Test def Sep(){
		var strip  = "100000/2.3/3/2/5/5"
		//var strip  = "100/5/5/2"
		var elems = strip.split("/").toList  
		var tmp = elems(0).toFloat
		var i = 0
		for(s <- strip.split("/")){
			if(i > 0){tmp = tmp/s.toFloat}
			i = i + 1
		}  
		println("dev:"+tmp)
	}
  
	
	//() ... no bracket allowed currently
//	@Test def dslineCalcTest()={
//	    var list = List[String]("22","2","13","3","4","5","6","7","0","")
//	    println("list.size | "+list.size)
//	    println("-- "+caluculateUnit("2+4", list))
//	    println("-- "+caluculateUnit("2+4*1", list))
//	    println("-- "+caluculateUnit("0+8", list))
//	    println("-- "+caluculateUnit("0+9", list))
//	}
	
	
	
	
	@Test def multiplyTest(){
		var text = "22*3/3*5/2*7/3/2*22/100"
//		println("mul:"+multiply(text))
	}
	
	
	
	
	@Test def AddTest(){
		var text = "22/3+4*2-11/2-2"
		
		//println("final:"+caluculateUnit(text))
		
	}
	
	@Test def calculateBracketEnhancingFormulaeTest(){
	   var addition = ""
	   var rate = ""
	     
	}
	
	
		
}