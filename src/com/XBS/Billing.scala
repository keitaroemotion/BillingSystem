package com.XBS

import junit.framework.TestCase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import scala.collection.mutable.Map
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import com.Kei.LogX

class Billing {
    var log = new LogX
  
	def ExecuteFile(infile:String, actionDslFile:Map[String,List[List[String]]], eunit:ExtraDatabaseUnit, pattkey:String)={
    		log.println
    		try{
	    		    import java.util.Date
			   	var book = new HSSFWorkbook()
			   	var sheet = book.createSheet()
				var lines = new Billing().Analyze(infile,actionDslFile, pattkey,eunit )
				WriteHeader(lines, sheet)
				var out = new Output()
				WriteBodyAccordingAsDataType(lines, sheet,out, book)
	    		    WriteAllSum(book, sheet, lines, out)
		        out.CreateOutExcelFile(lines, book)
    		}catch{
	    		  case e:Exception => log.println("ERROR| "+e.getMessage)
    		}
  }	

 
    
    def WriteBodyAccordingAsDataType(lines:DslUnit, sheet:HSSFSheet, out:Output, book:HSSFWorkbook)={
    			try{
    			  log.println
    			  var _rownum = 1
       		   for(line <- lines.convertedData){
					if(_rownum > 0){
					    var row = sheet.createRow(_rownum)
						var colidx = 0
						for(col <- line if out.notOutOfArray(lines,colidx) && _rownum > 0){
						   var ty = lines.dsl.reverse(colidx)(4) match{
						   		case "$date"  =>  out.WriteDate(book, row, col, colidx)
						   		case "$num" => out.WriteNum(row, colidx, col,book)
						   		case "$num+sum" =>out.WriteNum(row, colidx, col,book)
						   		case _ => row.createCell(colidx).setCellValue(col)
						   }
						  colidx = colidx + 1
						}
					}
				    _rownum = _rownum + 1    
				}
    			}catch{
    			  	case e :Exception =>  log.println(e);  throw e 
    			}
    }
  
   def popPriceSheetNum(actionDsl:Map[String,List[List[String]]], pattkey:String):Int={
    		log.println
		   try{
			   actionDsl(pattkey+".pricesheet")(0)(1).split('.')(0).toInt
		   }catch{
		     case e:Exception => 100
		   }
   }
  
   def popDsl(actionDsl:Map[String,List[List[String]]],pattkey:String, appension:String):String={
    		   log.println
    		   try{
    			   actionDsl(pattkey+appension)(0)(1)
    		   }catch{
    		     case e:Exception =>  log.println(e) ;  throw e
    		   }
   }
   
   def popNumber(text:String):Int={
    			text.replace("#", "").toInt
   }

   
   def popContainingFolder(infile:String):String={
    		new File(infile).getParent()
   }
   
   //var actionDsl = excel.ReadExcel(excel.getSheet(actionDslFile,0),13,0)
   def Analyze(infile:String, actionDsl:Map[String,List[List[String]]], pattkey:String, eunit:ExtraDatabaseUnit):DslUnit={
	    		log.println
	    		var excel = new Excel()
		    var priceTable = popDsl(actionDsl,pattkey,".price")
		    var keyIndex = popNumber(popDsl(actionDsl,pattkey,".keyindex"))
		    var extractor  = popNumber(popDsl(actionDsl,pattkey,".extractor"))
			var pricedata = excel.ReadExcel(excel.getSheet(priceTable,popPriceSheetNum(actionDsl,pattkey)),13,keyIndex)
			var listifiedInfile =  new Dsl().ListifyInfile(infile)
			var unit = new DslUnit()
			unit.convertedData =  new Billing().ConvertFile(listifiedInfile, pricedata, actionDsl,pattkey,extractor,eunit)
			unit.dsl = actionDsl(pattkey)
			unit.dslext = actionDsl
			unit.idkey = pattkey
			unit
	}
  
   
	   import com.XBS._
	   def PopXtraDatabaseUnit(dslFilePath:String, key:String):ExtraDatabaseUnit={
	     	try{
				   	var ex = new Excel()
			        var dictionary = ex.ReadExcel(ex.getSheet(dslFilePath,0),13,0)
			       	var indsl = ex.ReadExcel(ex.getSheet(dslFilePath,0),13,0)(key+".db")(0)
			       	var htmlfolder = ex.ReadExcel(ex.getSheet(dslFilePath,0),13,0)("$htmldirloc")(0)(1)
				    new ExtraDatabaseUnit().Construct(new HtmlUnit().FormatObject(indsl,DatabasifyHtmlsInFolder(htmlfolder)))
	     	}catch{
	     			case e:Exception => throw log.err(e)
	     	}
	   }

	private def DatabasifyHtmlsInFolder(directory:String): List[List[String]]= {
		  log.pln
	      import java.io._
		  import com.XBS._
		  var iox = new IOX()
	      var list = List[List[String]]()
	      var hunit = new HtmlUnit()
		  for( file <- iox.GetFiles(directory).filter( _.getName.endsWith(".html") ))
			   	list = list.:::(hunit.ParseHtml(file.toString))
	      list
	}
	
	def ConvertFile(lines:List[List[String]], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]] ,fileSpecification:String, extractor:Int, eunit:ExtraDatabaseUnit):List[List[String]]={
    			log.println
    			 var lists = List[List[String]]()
			 var prevlist = List[String]()
			 for(line <- lines){
				   var lineConverted = convertLine(line,pricedata,actionDsl,fileSpecification, prevlist, extractor,eunit)
				   lists = lists.+:(lineConverted)
				   prevlist = lineConverted
			 }
			 lists.reverse
	}
	
	def popPatternKey(containingFolder:String, actionDsl:Map[String,List[List[String]]]):String={
    	 	log.println
		 var pattkey = ""
		 actionDsl("$pattern").foreach(line =>{
	             if(containingFolder.trim().endsWith(line(1).replace("dir.eq(","").replace(")","").trim())){
	               	pattkey = line(2)   
	             }
	   	 }
		 )
			if(pattkey == ""){ throw new Exception("patternkey is not detected:"+containingFolder)}
			pattkey
	}
	
	def popPreviousNumber(dslcount:Int, previousline:List[String]):String={
	      log.println 
		  try{
			    if(previousline(dslcount) == ""){  "2"}
			    (previousline(dslcount).toInt +1).toString
		  }catch{
		  		case e:Exception => "1"  
		  }
	}
	
	def setUsualValue(line:List[String], dsline:List[String]):String={
    		log.println
		 val idx = dsline(3).replace("#","").trim().toInt
		 if(line.size > idx){
			 return line(idx)
		 }
		 ""
	}
	
  	 val priceCommand = "$p("
	 val dateCommand =    "$date"
     def SelectFromPriceTable(eunit:ExtraDatabaseUnit, pricekey:String, orderNumKey:String, exvalue:String, comms:List[String]):String={
  	   			println("pk| "+pricekey)
  	   			var value = exvalue
  	   			var htmlKey = eunit.Html(orderNumKey)
			    var pline = eunit.PriceTable(pricekey)(0)
    				var c = 0
    				for(h <- htmlKey){
    						if(h.htmlIndex == pline(h.priceFileIndex)){c = c + 1}
    				}
    				if(c == htmlKey.size){
    						value = pline(comms(2).replace(">(", "").toInt)
    				}
    				value
	}
	  
  	 def popValueFromExtraPriceMaster(indexColumn:String,line:List[String],eunit:ExtraDatabaseUnit, xval:String):String={
  	   				  println("indexColumn | "+indexColumn)
  	   			      var value = xval
                      var comms = indexColumn.split("|")(1).replace("if(line(", "").split(')').toList
                    	  var orderNumKey = line(comms(0).toInt)
                    	  println("orderNumKey  | "+orderNumKey )
                      if (orderNumKey.startsWith(comms(1).replace("*",""))){
                    	  	    for(pricekey <- eunit.PriceTable.keys)
                    	  	    			value = SelectFromPriceTable(eunit, pricekey, orderNumKey, value, comms) 
                      }
  	   				  value
  	 }
  	 
	def convertLine(line:List[String], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]], fileSpecification:String, previousline:List[String], extractor:Int,eunit:ExtraDatabaseUnit):List[String]={
    		log.println
		 var list = List[String]()
		 var dslSelected = actionDsl(fileSpecification).reverse
		 
		 for(dslcount <- 0 to dslSelected.size-1){
			    var value = ""	
			 	var dsline = dslSelected(dslcount)
			 	var formatColumn = dsline(2).trim
			 	var indexColumn = dsline(3).trim
			 	var attribueColumn = dsline(4) .trim
			 	
			 	if(indexColumn.contains("|if(line")){
			 	    value = popValueFromExtraPriceMaster(indexColumn,line,eunit, value)
 			 	}else if(dsline.size>4 && attribueColumn == dateCommand ){
			 		  value = setUsualValue(line,dsline) 
			 	}else if(dsline.size>4 && attribueColumn.endsWith("++")){
			 		 value = popPreviousNumber(dslcount, previousline)
			 	}else if(indexColumn.startsWith(priceCommand)){
			 		 value = GetPrice(value, pricedata, line,extractor, dsline)
			 	}else if(dsline.size>4 && formatColumn == ""&& attribueColumn == "" && indexColumn != ""  ){
			 		 value = setUsualValue(line,dsline)
			 	}
			    list = list.+:(value)
		 }
		 list.reverse
	}
	
	
	def GetPrice(valueString:String, pricedata:Map[String,List[List[String]]], line:List[String], extractor:Int, dsline:List[String]):String={
    			log.println
    			var value = valueString
    			
    			if(PopPrice(pricedata, line, extractor) == "" ){
			 		    value = "-1"
	 		}else{
			 		var priceToggle = dsline(3).replace(priceCommand , "").replace(")","").trim()
			 		if(priceToggle.contains('+')||priceToggle.contains('-')||priceToggle.contains('*')||priceToggle.contains('/')){
				 		value = new Calc().caluculateUnit(priceToggle,pricedata(line(extractor))(0)).toString
			 		}else{
			 			value = popPrice(pricedata, line, extractor, priceToggle)
			 		}
	 		}
			value
	}
	
	def PopPrice(pricedata:Map[String,List[List[String]]], line:List[String], extractor:Int):String={
	    			   log.println
				   try{
					   		pricedata(line(extractor))(0)(0)
				   }catch{
				     		case e:Exception => ""
				   }
	}
	
	
	def popPrice(pricedata:Map[String,List[List[String]]], line:List[String], extractor:Int, priceToggle:String):String={
	       log.println
		   try{
			  pricedata(line(extractor))(0)(priceToggle.toInt)
		   }catch{
		     case e:Exception => log.println(e); "-1"
		   }
	}
  
	
  private def WriteAllSum(book: HSSFWorkbook, sheet: HSSFSheet, lines: DslUnit, out: com.XBS.Output)= {
    	  log.println
      var maxnum = sheet.getPhysicalNumberOfRows()
      var col = 0
      var cellStyle = book.createCellStyle()
      cellStyle.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("#,##0"))
      var _row = sheet.createRow(maxnum)
      
      for(line <- lines.dslext(lines.idkey).reverse){
	      	if(line(4).contains("sum")){
	      		out.WriteSum(book,sheet, col,maxnum,_row,cellStyle)
	      	}
	      	col = col +1
      }
    }
  
  private def WriteHeader(lines: DslUnit,sheet:HSSFSheet)= {
    		  log.println
    		  var colidx = 0
		  var row = sheet.createRow(0)
	      for(line <- lines.dsl.reverse){
					row.createCell(colidx).setCellValue(line(1))
					colidx = colidx +1
		 }
    }
	
}
