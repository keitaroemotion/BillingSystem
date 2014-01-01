package Test

import org.junit._
import scala.collection._
import com.XBS.HtmlUnit

class HtmlAnalyzer {
   @Test def PopLastTagIndex()={
	   var s  = """<td bgcolor="#d5e0ff"><font color="red"><b>むうみんむら</b></font></td>"""
	   import com.XBS.Billing
	   //println("$ "+ new Billing().extractValue(s))
   }
   
   var log = new com.Kei.LogX
	@Test def HtmlParsingTest(){
        import com.XBS._
        val dslFilePath = "/Users/keitaroemotion/dev/garage/xbs/xbsdsl.xls"
        val htmlfolder = "/Users/keitaroemotion/dev/garage/html"
        var key = "$ss"

        for(elem <- new Billing().PopXtraDatabaseUnit(dslFilePath, key).Html){
        			print(elem._1+"|") ; for(e <- elem._2){print(e.htmlIndex+":"+e.priceFileIndex+"	")}; println
        }
       for(elem <- new Billing().PopXtraDatabaseUnit(dslFilePath, key).PriceTable){
    	   			for(e<- elem._2){ print(e+"	")}; println
       }
	}
   
      
	
	
}