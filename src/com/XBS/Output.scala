package com.XBS

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import scala.collection.mutable.Map
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.ss.usermodel.CellStyle



class Output {
	def CreateOutExcelFile(lines:DslUnit, book:HSSFWorkbook)={
        var outdir = lines.dslext("outdir")(0)(1)
        if(!outdir.endsWith("/")) { outdir += "/"}
	  	var fileOut = new FileOutputStream(outdir +"workbook.xls");
	    book.write(fileOut);
	    fileOut.close();
   }
	
	def WriteDate(book:HSSFWorkbook, row:HSSFRow, value:String, colnum:Int)={
        try{
		    import java.util.Date
		   	var cellStyle = book.createCellStyle()
		   	println("date| "+value)
		   	cellStyle.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("yyyy/mm/dd"));	   	
		   	var cell = row.createCell(colnum)
		   	cell.setCellValue(new Date(value))
		    cell.setCellStyle(cellStyle)
        }catch{
          case e:Exception =>
        }
   }
	
	def notOutOfArray(lines:DslUnit, colidx:Int):Boolean={
      try{
    	  lines.dsl(colidx)(4)
    	  true
      }catch{
        case e:Exception => false
      }
    }
	
	def popAddRange(sheet:HSSFSheet, col:Int, excelmap:Map[String,List[List[String]]], maxNum:String):String={
	  	var colAlphabet = excelmap("#"+(col).toString().trim())(0)(1).trim()
		"SUM("+colAlphabet+"1"+":"+colAlphabet+maxNum+")"
  	}
	
	def WriteSum(book:HSSFWorkbook, sheet:HSSFSheet, col:Int, maxNum:Int, row:HSSFRow, cellStyle:CellStyle){
       var cell = row.createCell(col)
		var mapexcel =new Excel().ReadExcel(3,0, "/Users/keitaroemotion/dev/garage/xbs/eref.xls", 0)
		cell.setCellStyle(cellStyle)
		cell.setCellFormula(popAddRange(sheet,col,mapexcel,(maxNum).toString).trim());
   }
   
   def WriteNum(row:HSSFRow, colidx:Int, col:String, book:HSSFWorkbook){
     try{
        import org.apache.poi.hssf.usermodel.HSSFDataFormat
        var cellStyle = book.createCellStyle();
        cellStyle.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("#,##0"));
	   	var cell = row.createCell(colidx)
	   	cell.setCellValue(col.trim().split('.')(0).toShort)
	    cell.setCellStyle(cellStyle)
     }catch{
       //case e:NumberFormatException => row.createCell(colidx).setCellValue(col) 
       case ex:Exception => WriteNum(row, colidx, "0", book)
     }
   }
}