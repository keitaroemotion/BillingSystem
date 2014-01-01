package com.XBS

class HtmlLocation {
           var log = new com.Kei.LogX
    	 		var htmlIndex:String = ""
    	 		var priceFileIndex:Int = 0
    	 		def FormatObject(t:String, html:List[String]) : HtmlLocation={
    	 		  		log.pln
	 		  		//|T| html(7 /sheet:0 /infileidx:3  
	 			    //|T| 3(2)
    	 				var splitTokens = t.split('(')
    	 				var loc = new HtmlLocation()
    	 		  		log.pln("|splitTokens(0: "+splitTokens(0))
    	 		  		log.pln("|splitTokens(1): "+splitTokens(1))
    	 				loc.htmlIndex = html(splitTokens(0).replace(")","").toInt)
    	 				loc.priceFileIndex = splitTokens(1).replace(")","").toInt
    	 				loc
    	 		}
}
