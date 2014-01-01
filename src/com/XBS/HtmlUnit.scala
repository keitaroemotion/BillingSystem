package com.XBS

import scala.collection.mutable.Map
import scala.Tuple2

class HtmlUnit {
     var key = ""
     var infileIndex = 0
     var RefTableLocation = Map[String,List[List[String]]]()
     var HtmlReferencePointers = Map[String,List[HtmlLocation]]()
     var log = new com.Kei.LogX
     val sheetCommand = "/sheet"
     var priceFileCommand = "/pricefile"
       
       
      def popValue(settings:String, Command:String, currentvalue:Int):Int={
    	 			log.pln
    	 			if(settings.contains(Command)){
 			    		return settings.replace(Command, "").trim().split(':')(1).toInt
    	 			}
    	 			currentvalue
     }
       
     def ReadFromSecondPriceFile(file:String, sheetIndex:Int):Map[String,List[List[String]]]={
		    	 		log.pln
		    	 		try{
			    	 			var sheetIndex =  0
				    	 		var excel = new Excel()
				    	 		excel.ReadExcel(excel.getSheet(file.trim().replace(")", ""),sheetIndex),20,0)
		    	 		}catch{
		    	 		  		case e:Exception => throw log.err(e)
		    	 		}
     }
     
     def pop(list:List[String], i:Int):String={
    	 			try{
    	 				list(i)
    	 			}catch{
    	 			  case e:Exception => ""
    	 			}
     }
     def pop(list:Array[String], i:Int):String={
    	 			log.pln("index:"+i)
    	 			try{
    	 				list(i)
    	 			}catch{
    	 			  case e:Exception => ""
    	 			}
     }
     
     
     def popPriceFile(stripSplit:List[String]):String={
    	 		stripSplit(1).replace("ref(", "").replaceAll("\\)+$", "")
     }
     
     def FormatObject(stripSplit:List[String], htmldb:List[List[String]]):HtmlUnit={
		    	 		log.pln
		    	 		try{
			    	 		var unit = new HtmlUnit()
			    	 		var htmlDescripaton = stripSplit(2)
			    	 		var stripSplitTokens = htmlDescripaton.replace("html(","").split(' ')
			    	 		unit.key = pop(stripSplitTokens,(0))
			    	 		
			    	 		unit.HtmlReferencePointers =  Tuplify(Listify(stripSplit), htmldb)
			    	 		
			    	 		var sheetIndex = 0
			    	 		for(s <- stripSplitTokens){
			    	 				println("|s:| "+s)
			    	 				if(s.startsWith("/infileidx")){unit.infileIndex = s.replace("/infileidx:","").trim.toInt}
			    	 				if(s.startsWith("/sheet")){sheetIndex = s.replace("/sheet:","").trim.toInt}
			    	 		}
			    	 		
			    	 		unit.RefTableLocation = ReadFromSecondPriceFile(popPriceFile(stripSplit),sheetIndex)
			    	 		
			    	 		unit
		    	 		}catch{
		    	 		  case e:Exception => throw log.err(e)
		    	 		}
     }
     
     def Listify(stripSplit:List[String]):List[String]={
    	 		log.pln
    	 		stripSplit(2).split(',').toList
     }
     
     def popKeyOfAnHtml(text:List[String],  html:List[String]):List[HtmlLocation]={
    	 					log.pln
    	 					

 						var list = List[HtmlLocation]()
	    	 		  		var i = 0
	    	 				for(t <- text){
	    	 					  log.pln("T| "+t)
	    	 					  if(i > 0){
		    	 					  list = list.+:(new HtmlLocation().FormatObject(t,html))
	      	 				  }
	    	 					  i=i+1
	    	 				}
 	    	 		  		list
     }
     
     
     def ParseHtml(file:String):List[List[String]]={
		//log.pln
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
     
//     def pop(text:List[String], i :Int):String={
//    	 		log.pln(i)	
//    	 		text(0)
//     }
     
     def convertToInt(text:String):Int={
    	 		log.pln(text)
    	 		text.toInt
     }
     
     
     def Tuplify(text:List[String], htmls:List[List[String]]):Map[String,List[HtmlLocation]]={
    	 				log.pln
    	 				try{
    	 				  
    	 				  println("|> .. "+text)
    	 				  println("")
    	 				  println("==============")
    	 				   for(t <- text){
    	 					   print(t+"	")
    	 				   }
    	 				  println("")
    	 				   println("==============")
    	 				  
    	 				    var map = Map[String,List[HtmlLocation]]()
    	 				    log.pln("text(0) | "+text(0))
    	 				    var TopIndex = text(0).replace("html(", "").split(' ')(0).replace(")", "").trim
    	 				    
    	 				    for(html <- htmls){
    	 				    			var keyIndex = convertToInt(TopIndex)
    	 				    			log.pln("|K|EY: | "+keyIndex)
    	 				    			var key = html(keyIndex)
    	 				    			log.pln("|key: | "+key)
    	 				            map = map.+=(key -> popKeyOfAnHtml(text,html))
    	 				    }
    	 				    	map    	 				    
    	 				}catch{
    	 				  	case e:Exception => throw log.err(e,"key| "+key) 
    	 				}
    	 }
}



 