package Test

import org.junit._
import scala.collection._

class HtmlAnalyzer {
   @Test def PopLastTagIndex()={
	   var s  = """<td bgcolor="#d5e0ff"><font color="red"><b>むうみんむら</b></font></td>"""
	   println("$ "+extractValue(s))
   }

   
   
   def extractValue(s:String):String={
		 var tmp = ""  
	     for(i <- s.size-1 to 0 by -1){
		       if(i>1 &&  (s(i).toString== "<") && s(i-1).toString != ">"){
		    	   	 var fin = false
		    	     for(j <- i-1 to 0 by -1){
		    	     	 if(s(j) == '>'){ fin = true }
		    	    	 if(fin == false){tmp += s(j)}
		    	     }  
		       }
		 }
		 tmp.reverse
   }
  
   @Test def PopValueFromSentence()={
	   var s1 = """<td bgcolor="#d5e0ff">0978380450</td>"""
	     
	   var s2  = """<td bgcolor="#d5e0ff"><font color="red"><b>未</b></font></td>"""
   }
  
	@Test def HtmlParsingTest(){
		var file = "/Users/keitaroemotion/dev/garage/sample.html"
		for(line <- ParseHtml(file)){
		   for(l <- line){print(l+'	')}
		   println()
		}
	}
	
	def ParseHtml(file:String):List[List[String]]={
		var data =  List[List[String]]()  
		var unit = List[String]() 
		var col = 0
	    for (f <- scala.io.Source.fromFile(file).mkString.lines if(f.startsWith("""<td bgcolor="#d5e0ff">""")) ){
	      unit = unit.+:(extractValue(f))
	      if(col == 9){
	    	 data = data.+:(unit)
	    	 unit = List[String]()
	         col = 0
	      }
	      col = col+1
	    }
		data
	}
	
	
}