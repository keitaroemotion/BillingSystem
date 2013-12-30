package Test

import org.junit._
import scala.collection._
import com.XBS.HtmlUnit
import com.XBS.LoggerX

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
  
  
   @Test def TrimEndTest(){
	   	println("())()()()()()()()()()()()()()(()()()()(()()()()()a)".replaceAll("\\)+$", ""));
   }
   
   
   
   var log = new LoggerX
	@Test def HtmlParsingTest(){
       import com.XBS._
       val dslFilePath = "/Users/keitaroemotion/dev/garage/xbs/xbsdsl.xls"
       var ex = new Excel()
       var key = "$ss.db"
       var dictionary = ex.ReadExcel(ex.getSheet(dslFilePath,0),13,0)
       log.println("dictioanry contains key : "+dictionary.contains(key))
       var r = ex.ReadExcel(ex.getSheet(dslFilePath,0),13,0)("$ss.db")
	   val indsl = r(0)
        log.pln
        val htmlfolder = "/Users/keitaroemotion/dev/garage/html"
	    var x = new HtmlUnit().FormatObject(indsl,DatabasifyHtmlsInFolder(htmlfolder))
	    var htmlKeysExtracted = x.HtmlReferencePointers
	    
	    // extract SW only
	    // make bigger object map
//	    for(key <- x.HtmlReferencePointers	.keys){
//	    			for(location <- x.HtmlReferencePointers(key)){
//	    			     	
//	    					if(key.startsWith("SW")){
//	    							for(r <- x.RefTableLocation.keys){
//	    								  for(line <- x.RefTableLocation(r)){
//	    									  		
//	    								  }
//	    								//
//	    							}
//	    					}
//	    			}
//	    }
	}
	
	def ParseHtml(file:String):List[List[String]]={
		log.pln
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
  
	
	private def DatabasifyHtmlsInFolder(directory:String): List[List[String]]= {
	  log.pln
      import java.io._
	  import com.XBS._
	  var iox = new IOX()
      var list = List[List[String]]()
	  for( file <- iox.GetFiles(directory).filter( _.getName.endsWith(".html") ))
		   	list = list.:::(ParseHtml(file.toString))
      list
   }
	
	
}