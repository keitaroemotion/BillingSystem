package com.XBS

import junit.framework.TestCase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import scala.collection.mutable.Map
import org.apache.poi.hssf.usermodel.HSSFRow


class Billing {
  
  
   def Analyze(infile:String, actionDslFile:String):DslUnit={
		var actionDsl = new Excel().ReadExcel(new Excel().getSheet(actionDslFile,0),13,0)
	    var containingFolder = new File(infile).getParent()
	    val pattkey = new Billing().popPatternKey(containingFolder, actionDsl)
	    var priceTable = actionDsl(pattkey+".price")(0)(1)
	    var keyIndex = actionDsl(pattkey+".keyindex")(0)(1).replace("#", "").toInt
	    var extractor = actionDsl(pattkey+".extractor")(0)(1).replace("#", "").toInt
		var pricedata = new Excel().ReadExcel(new Excel().getSheet(priceTable,0),13,keyIndex)
		var listifiedInfile =  new Dsl().ListifyInfile(infile)
		var unit = new DslUnit()
		unit.convertedData =  new Billing().ConvertFile(listifiedInfile, pricedata, actionDsl,pattkey,extractor)
		unit.dsl = actionDsl(pattkey)
		unit.dslext = actionDsl
		unit.idkey = pattkey
		unit
	}
  
	def setHeader(actionDsl:Map[String,List[List[String]]], fileSpecification:String):List[List[String]]={
			var lists = List[List[String]]()
			var listInit = List[String]()
			 actionDsl(fileSpecification).foreach(col =>{
				 listInit = listInit.+:(col(1))
			 })
			 lists.+:(listInit)
	}
	
	def ConvertFile(lines:List[List[String]], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]] ,fileSpecification:String, extractor:Int):List[List[String]]={
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
	  try{
	    if(previousline(dslcount) == ""){  "2"}
	    (previousline(dslcount).toInt +1).toString
	  }catch{
	    case e:Exception => "1"  
	  }
	}
	
	def setUsualValue(line:List[String], dsline:List[String]):String={
		 val idx = dsline(3).replace("#","").trim().toInt
		 if(line.size > idx){
			 return line(idx)
		 }
		 ""
	}
	
	def convertLine(line:List[String], pricedata: Map[String,List[List[String]]], actionDsl:Map[String,List[List[String]]], fileSpecification:String, previousline:List[String], extractor:Int):List[String]={
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
			 		var priceToggle = dsline(3).replace("$p(", "").replace(")","").trim()
			 		if(priceToggle.contains('+')||priceToggle.contains('-')||priceToggle.contains('*')||priceToggle.contains('/')){
				 		value = new Calc().caluculateUnit(priceToggle,pricedata(line(extractor))(0)).toString
			 		}else{
			 			value = pricedata(line(extractor))(0)(priceToggle.toInt)
			 		}
			 	}else if(dsline.size>4 && dsline(2).trim() == ""&& dsline(4).trim() == "" && dsline(3) != ""  ){
			 		 value = setUsualValue(line,dsline)
			 	}
			    list = list.+:(value)
		 }
		 list.reverse
	}
}