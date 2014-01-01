//package com.XBS
//
//import org.apache.poi.hssf.usermodel.HSSFSheet
//import org.apache.poi.hssf.usermodel.HSSFWorkbook
//import java.io._
//import com.sun.org.apache.xalan.internal.xsltc.compiler.ForEach
//import org.apache.poi.sl.usermodel.Sheet
//import org.apache.poi.hssf.usermodel.HSSFSheet
//import org.apache.poi.hssf.usermodel.HSSFRow
//import scala.collection.mutable.Map
//import oracle.jrockit.jfr.Logger
//
//
//class Excel {
//   var log = new com.Kei.LogX
//  
//	def getSheet(file:String, sheetnum:Int):HSSFSheet={
//     log.pln("file| "+file + " |sheetnum|"+sheetnum)
//	  if(file.endsWith(".xlsx")){ throw new Exception("file ends with the extention '.xlsx' save it as '.xls' ")}
//        var _file = new File(file)
//        if(!_file.exists()){
//        		throw log.err("the following file does not exist physically! >"+file)
//        }
//		return  getSheetAt(sheetnum,  new HSSFWorkbook(new FileInputStream(_file)));
//	}
//  
//	def ReadDomainSpecificFile(colmaximum:Int, keyIndex:Int, file:String, sheetNum:Int):scala.collection.mutable.Map[String,List[List[String]]]={
//			ReadExcel(getSheet(file, sheetNum),colmaximum, keyIndex)
//	}
//	
//	def ReadExcel(sheet:HSSFSheet,colmaximum:Int, keyIndex:Int):scala.collection.mutable.Map[String,List[List[String]]]={
//		   try{
//				  log.pln("keyIndex| "+keyIndex)
//			      //	... multiple action
//				  var map = Map[String,List[List[String]]]()
//				  
//				  for(r <- 0 to sheet.getPhysicalNumberOfRows()){
//					  var lists = List[List[String]]()
//					  var list = List[String]()
//					  var row = sheet.getRow(r)
//					  
//					  for(c <- 0 to colmaximum){
//					      list = list.+:(getValue(row,c))
//					  }
//					  
//					  list = list.reverse
//					  lists = lists.+:(list)
//					  
//					  var key = list(keyIndex)
//					  if(map.contains(key)){
//						   var tmpvalue = map(key)
//						   map.remove(key)
//						   lists = tmpvalue.+:(list)
//					  }
//					  if(key != ""){ map = map.+(key -> lists)}
//			      }
//				  return map
//		   }catch{
//		     	case e:Exception => throw log.err(e)
//		   }
//    }
//	
//	
//	def getValue(row:HSSFRow, c:Int):String={
//			try{
//			  	return row.getCell(c).toString()
//			}catch{
//			  case e:Exception =>  return ""
//			}
//	}
//	
//	def getException(message:String, e:Exception):Exception={
//			println(message +"| "+e.getMessage())
//			return e
//	}
//	
// 	def getSheetAt(i:Int, book:HSSFWorkbook):HSSFSheet={
// 	  try{
// 		  return book.getSheetAt(i)
// 	  }catch{
// 	    case e:Exception =>  throw getException("getSheetAt", e) 
// 	  }
// 	}
// 	
//}