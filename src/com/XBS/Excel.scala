package com.XBS

import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
import org.apache.poi.sl.usermodel.Sheet
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFRow
import scala.collection.mutable.Map


class Excel {
  
	def getSheet(file:String, sheetnum:Int):HSSFSheet={
	  if(file.endsWith(".xlsx")){ throw new Exception("file ends with the extention '.xlsx' save it as '.xls' ")}
		return  getSheetAt(sheetnum,  new HSSFWorkbook(new FileInputStream(new File(file))));
	}
  
	def ReadExcel(colmaximum:Int, keyIndex:Int, file:String, sheetNum:Int):scala.collection.mutable.Map[String,List[List[String]]]={
			ReadExcel(getSheet(file, sheetNum),colmaximum, keyIndex)
	}
	
	def ReadExcel(sheet:HSSFSheet,colmaximum:Int, keyIndex:Int):scala.collection.mutable.Map[String,List[List[String]]]={
	      //	... multiple action
		  var map = Map[String,List[List[String]]]()
		  
		  for(r <- 0 to sheet.getPhysicalNumberOfRows()){
			  var lists = List[List[String]]()
			  var list = List[String]()
			  var row = sheet.getRow(r)
			  for(c <- 0 to colmaximum){
			      list = list.+:(getValue(row,c))
			  }
			  list = list.reverse
			  lists = lists.+:(list)
			  
			  //append elem
			  var key = list(keyIndex)
			  
			  if(map.contains(key)){
				   var tmpvalue = map(key)
				   map.remove(key)
				   lists = tmpvalue.+:(list)
			  }
			  if(key != ""){
				  map = map.+(key -> lists)
			  }
	      }
		  return map
    }
	
	
	def getValue(row:HSSFRow, c:Int):String={
			try{
			  	return row.getCell(c).toString()
			}catch{
			  case e:Exception =>  return ""
			}
	}
	
	def getException(message:String, e:Exception):Exception={
			println(message +"| "+e.getMessage())
			return e
	}
	
 	def getSheetAt(i:Int, book:HSSFWorkbook):HSSFSheet={
 	  try{
 		  return book.getSheetAt(i)
 	  }catch{
 	    case e:Exception =>  throw getException("getSheetAt", e) 
 	  }
 	}
 	
}