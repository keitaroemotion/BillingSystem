package Test

import org.junit._
import com.XBS._
import junit.framework.TestCase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io._
import scala.collection.mutable.Map
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFBorderFormatting
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.ss.usermodel.CellStyle

class ExcelTest{
  var targetDirectory = "/Users/keitaroemotion/dev/garage/xbs/"
    
  val priceTableSample = targetDirectory+"xportal_price.xls"
  
//    @Test def dateformatTest{
//	  	import java.util.Date
//	   	var book = new HSSFWorkbook()
//	   	var sheet = book.createSheet()
//	   	var row = sheet.createRow(1)
//	   	//WriteDate(book, row, "2013/9/3 14:8:13", 1)
//	   	var cell2 = row.createCell(2)
//	   	cell2.setCellValue(new Date("2013/9/3 14:8:13"))
//	  	var fileOut = new FileOutputStream("/Users/keitaroemotion/Documents/workbook.xls");
//	    book.write(fileOut);
//	    fileOut.close();
//   }
//  
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
  
    @Test def GetMaximumRowNumber{
//      	val priceTable = priceTableSample
//    	var book = new HSSFWorkbook(new FileInputStream(new File(priceTable)))
//    	var sheet = book.getSheetAt(0)
//    	//var row = sheet.getFirstRowNum()
//    	var row = sheet.getPhysicalNumberOfRows()
//    	//print("row|"+row)
    }
    
    def notOutOfArray(lines:DslUnit, colidx:Int):Boolean={
      try{
    	  lines.dsl(colidx)(4)
    	  true
      }catch{
        case e:Exception => false
      }
    }
  
	@Test def WriteDataToExcelTest(){
	  ExecuteFile(infile)
	} 
	
  def ExecuteFile(infile:String)={
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
	  	
		for(line <- lines.convertedData){
			if(rownum > 0){
			    var row = sheet.createRow(rownum)
				var colidx = 0
				for(col <- line if notOutOfArray(lines,colidx) && rownum > 0){
				   var ty = lines.dsl.reverse(colidx)(4) match{
				   		case "$date"  =>  WriteDate(book, row, col, colidx)
				   		case "$num" => WriteNum(row, colidx, col,book)
				   		case "$num+sum" =>WriteNum(row, colidx, col,book)
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
	  			WriteSum(book,sheet, col,maxnum,row,cellStyle)
	  		}
	  		col = col +1
	  	}
	  	
        CreateOutExcelFile(lines, book)
  }	
	
   def CreateOutExcelFile(lines:DslUnit, book:HSSFWorkbook)={
        var outdir = lines.dslext(lines.idkey + ".outdir")(0)(1)
        if(!outdir.endsWith("/")) { outdir += "/"}
	  	var fileOut = new FileOutputStream(outdir +"workbook.xls");
	    book.write(fileOut);
	    fileOut.close();
   }		

   
	def popAddRange(sheet:HSSFSheet, col:Int, excelmap:Map[String,List[List[String]]], maxNum:String):String={
	  	var colAlphabet = excelmap("#"+(col).toString().trim())(0)(1).trim()
		var s ="SUM("+colAlphabet+"1"+":"+colAlphabet+maxNum+")"
		println("=> "+s)
		s
  	}
   
   
   @Test def PriceTotalTest(){
        var book = new HSSFWorkbook()
	   	var sheet = book.createSheet()
	   	var cell = sheet.createRow(1).createCell(10);
        cell.setCellValue(102)
        var cell2 = sheet.createRow(2).createCell(10);
        cell2.setCellValue(100)
        var cell12 = sheet.createRow(3).createCell(10);
        cell12.setCellValue(100)
		
		var fileOut = new FileOutputStream("/Users/keitaroemotion/dev/garage/xbs/calctest.xls");
	    book.write(fileOut);
	    fileOut.close();
		
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
	
	val infile = targetDirectory+"ssinfile.txt"
	  
	@Test def popDirectoryTest{
		import scala.io._
		println("|> "+new File(infile).getParent())
	  
	}  
	  
	@Test def ReadTextInputTest{
		  println("")
		  println("ReadTextInputTest")
		  new Dsl().ListifyInfile(infile).foreach(line =>{
		    println()
		  })
	}
	
	
//	@Test def FilterFileTest()={
//	     print("|.. "+infile)
//		 new Billing().Analyze(infile,actionDslFile).foreach(line =>{
//			 line.foreach(col=>print("|"+col+"\t"))
//			 println()
//		})
//	}
	 
	
	val actionDslFile = targetDirectory+"xbsdsl.xls"
	
	
//	@Test def ConvertLineTest()={;
//		var fileSpecification = "$ss"
//		var excel = new Excel()
//		var line = "1    SS130829-0010    800103    2013/9/2 10:22:21    3540021    0492516850 学校法人藤花学園富士見台幼稚園     埼玉県富士見市鶴馬３５１３－１階事務室     0492516850    13373620    田中　浩    黒    ミズイロ".split("\t").toList
//		var pricedata = excel.ReadExcel(excel.getSheet(priceTable,0),13,4)
//		var actionDsl = excel.ReadExcel(excel.getSheet(actionDslFile,0),13,0)
//		var list = convertLine(line, pricedata, actionDsl,fileSpecification, null)
//		println("size| "+list.size)
//	}
	
	
	
	
	
	
	// filter textfile data according as dsl 
	// 1) simply put
	// 2) fetch from excel price data using key...
	// 3) calculate according as dsl instruction(formula)
	
	
//	@Test def PriceDataExtractionTestWithKey(){
//	  //var sheet =new Excel().getSheet(priceTable,0)
//	  var map = new Excel().ReadExcel(new Excel().getSheet(priceTable,0),13,4)
//      map.keys.foreach(x => {
// //   	println(x + "+"+ map(x)(1)+"|"+ map(x)(2)+"|"+ map(x)(3)+"|"+ map(x)(5)+"|"+ map(x)(6))
//    })
  //}
}