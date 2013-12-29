package com.XBS

import junit.framework.TestCase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import scala.collection.mutable.Map
import org.apache.poi.hssf.usermodel.HSSFRow


class Billing {
    var log = new LoggerX
  
	def ExecuteFile(infile:String, actionDslFile:String)={
    	log.println
	  	import java.util.Date
	   	var book = new HSSFWorkbook()
	   	var sheet = book.createSheet()
		var lines = new Billing().Analyze(infile,actionDslFile)
		var colidx = 0
		var row = sheet.createRow(0)
		for(line <- lines.dsl.reverse){
			println("====  "+line(1))
			row.createCell(colidx).setCellValue(line(1))
			colidx = colidx +1
		}
	  	
		var rownum = 0	
	  	
		var out = new Output()
		
		for(line <- lines.convertedData){
			if(rownum > 0){
			    var row = sheet.createRow(rownum)
				var colidx = 0
				for(col <- line if out.notOutOfArray(lines,colidx) && rownum > 0){
				   var ty = lines.dsl.reverse(colidx)(4) match{
				   		case "$date"  =>  out.WriteDate(book, row, col, colidx)
				   		case "$num" => out.WriteNum(row, colidx, col,book)
				   		case "$num+sum" =>out.WriteNum(row, colidx, col,book)
				   		case _ => row.createCell(colidx).setCellValue(col)
				   }
				  colidx = colidx + 1
				}
			}
		    rownum = rownum + 1    
		}
	  	
	  	
	  	var maxnum = sheet.getPhysicalNumberOfRows()
	  	
	  	var col = 0
	  	var cellStyle = book.createCellStyle()
	    cellStyle.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("#,##0"))
	    row = sheet.createRow(maxnum)
	  	
	    
	    
	  	for(line <- lines.dslext(lines.idkey).reverse){
	  		if(line(4).contains("sum")){
	  		  println("..>> "+col+" : "+line(1)++" | "+maxnum)
	  			out.WriteSum(book,sheet, col,maxnum,row,cellStyle)
	  		}
	  		col = col +1
	  	}
        out.CreateOutExcelFile(lines, book)
  }	

  
  
   def popPriceSheetNum(actionDsl:Map[String,List[List[String]]], pattkey:String):Int={
    		log.println
		   try{
			   actionDsl(pattkey+".pricesheet")(0)(1).split('.')(0).toInt
		   }catch{
		     case e:Exception => 100
		   }
   }
  
   def Analyze(infile:String, actionDslFile:String):DslUnit={
    	log.println
		var actionDsl = new Excel().ReadExcel(new Excel().getSheet(actionDslFile,0),13,0)
	    var containingFolder = new File(infile).getParent()
	    val pattkey = new Billing().popPatternKey(containingFolder, actionDsl)
	    var priceTable = actionDsl(pattkey+".price")(0)(1)
	    var keyIndex = actionDsl(pattkey+".keyindex")(0)(1).replace("#", "").toInt
	    var extractor = actionDsl(pattkey+".extractor")(0)(1).replace("#", "").toInt
	    
	    println("keyindex | "+keyIndex)
		var pricedata = new Excel().ReadExcel(new Excel().getSheet(priceTable,popPriceSheetNum(actionDsl,pattkey)),13,keyIndex)
		
		
		var listifiedInfile =  new Dsl().ListifyInfile(infile)
		var unit = new DslUnit()
		unit.convertedData =  new Billing().ConvertFile(listifiedInfile, pricedata, actionDsl,pattkey,extractor)
		unit.dsl = actionDsl(pattkey)
		unit.dslext = actionDsl
		unit.idkey = pattkey
		unit
	}
  
	def setHeader(actionDsl:Map[String,List[List[String]]], fileSpecification:String):List[List[String]]={
    		log.println
			var lists = List[List[String]]()
			var listInit = List[String]()
			 actionDsl(fileSpecification).foreach(col =>{
				 listInit = listInit.+:(col(1))
			 })
			 lists.+:(listInit)
	}
	
	def ConvertFile(lines:List[List[String]], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]] ,fileSpecification:String, extractor:Int):List[List[String]]={
    		log.println
			 var lists = setHeader(actionDsl,fileSpecification)
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
	
	def convertLine(line:List[String], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]], fileSpecification:String, previousline:List[String], extractor:Int):List[String]={
    	log.println
		 var list = List[String]()
		 var dslSelected = actionDsl(fileSpecification).reverse
		 
		 for(dslcount <- 0 to dslSelected.size-1){
			    var value = ""	
			 	var dsline = dslSelected(dslcount)
			 	if(dsline.size>4 && dsline(4).trim() == "$date"){
			 		  value = setUsualValue(line,dsline) 
			 	}else if(dsline.size>4 && dsline(4).trim().endsWith("++")){
			 		 value = popPreviousNumber(dslcount, previousline)
			 	}else if(dsline(3).startsWith("$p(")){
			 		if(PopPrice(pricedata, line, extractor) == "" ){
			 				value = "-1"
			 		}else{
					 		var priceToggle = dsline(3).replace("$p(", "").replace(")","").trim()
					 		if(priceToggle.contains('+')||priceToggle.contains('-')||priceToggle.contains('*')||priceToggle.contains('/')){
						 		value = new Calc().caluculateUnit(priceToggle,pricedata(line(extractor))(0)).toString
					 		}else{
					 			//value = pricedata(line(extractor))(0)(priceToggle.toInt)
					 		    value = popPrice(pricedata, line, extractor, priceToggle)
					 		}
			 		}
			 	}else if(dsline.size>4 && dsline(2).trim() == ""&& dsline(4).trim() == "" && dsline(3) != ""  ){
			 		 value = setUsualValue(line,dsline)
			 	}
			    list = list.+:(value)
		 }
		 list.reverse
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
		     case e:Exception => 
		       	"-1"
		        // writelog
		   }
	}
	
}
