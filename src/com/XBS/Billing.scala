package com.XBS

import junit.framework.TestCase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import scala.collection.mutable.Map
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import com.sun.org.apache.xalan.internal.xsltc.compiler.Output


class Billing {
    var log = new LoggerX
  
	def ExecuteFile(infile:String, actionDslFile:Map[String,List[List[String]]])={
    		log.println
    		try{
	    		  import java.util.Date
			   	var book = new HSSFWorkbook()
			   	var sheet = book.createSheet()
				var lines = new Billing().Analyze(infile,actionDslFile)
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
   
   
   //var actionDsl = excel.ReadExcel(excel.getSheet(actionDslFile,0),13,0)
   def Analyze(infile:String, actionDsl:Map[String,List[List[String]]]):DslUnit={
	    		log.println
	    		
	    		var excel = new Excel()
		    var containingFolder = new File(infile).getParent()
		    val pattkey = popPatternKey(containingFolder, actionDsl)
		    var priceTable = popDsl(actionDsl,pattkey,".price")
		    var keyIndex = popNumber(popDsl(actionDsl,pattkey,".keyindex"))
		    var extractor  = popNumber(popDsl(actionDsl,pattkey,".extractor"))
			var pricedata = excel.ReadExcel(excel.getSheet(priceTable,popPriceSheetNum(actionDsl,pattkey)),13,keyIndex)
			
			var listifiedInfile =  new Dsl().ListifyInfile(infile)
			var unit = new DslUnit()
			unit.convertedData =  new Billing().ConvertFile(listifiedInfile, pricedata, actionDsl,pattkey,extractor)
			unit.dsl = actionDsl(pattkey)
			unit.dslext = actionDsl
			unit.idkey = pattkey
			unit
	}
  
	
	def ConvertFile(lines:List[List[String]], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]] ,fileSpecification:String, extractor:Int):List[List[String]]={
    			log.println
    			 var lists = List[List[String]]()
			 var prevlist = List[String]()
			 lines.foreach(line => {
				   var lineConverted = convertLine(line,pricedata,actionDsl,fileSpecification, prevlist, extractor)
				   lists = lists.+:(lineConverted)
				   prevlist = lineConverted
			 })
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
	
	def convertLine(line:List[String], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]], fileSpecification:String, previousline:List[String], extractor:Int):List[String]={
    		log.println
		 var list = List[String]()
		 var dslSelected = actionDsl(fileSpecification).reverse
		 
		 for(dslcount <- 0 to dslSelected.size-1){
			    var value = ""	
			 	var dsline = dslSelected(dslcount)
			 	if(dsline.size>4 && dsline(4).trim() == dateCommand ){
			 		  value = setUsualValue(line,dsline) 
			 	}else if(dsline.size>4 && dsline(4).trim().endsWith("++")){
			 		 value = popPreviousNumber(dslcount, previousline)
			 	}else if(dsline(3).startsWith(priceCommand )){
			 		value = GetPrice(value, pricedata, line,extractor, dsline)
			 	}else if(dsline.size>4 && dsline(2).trim() == ""&& dsline(4).trim() == "" && dsline(3) != ""  ){
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
			 			println("|-> "+value+" | "+priceToggle)
				 		value = new Calc().caluculateUnit(priceToggle,pricedata(line(extractor))(0)).toString
				 		println("|-> "+value+" | ")
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
  
	
  private def WriteAllSum(book: HSSFWorkbook, sheet: HSSFSheet, lines: DslUnit, out: Output)= {
    	  log.println
    	  //var _row = row	
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
