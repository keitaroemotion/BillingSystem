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
  
  def ReadActionDsl(file:String):Map[String,List[List[String]]]={
	  var ex = new Excel	  
	  ex.ReadExcel(ex.getSheet(actionDslFile,0),13,0)
  }
  
  @Test def WriteDataToExcelTest{
   	   var actionDsl = ReadActionDsl(actionDslFile)
   	   var bill = new Billing
   	   val pattkey = bill.popPatternKey(bill.popContainingFolder(infile), actionDsl )
       var extraData = bill.PopXtraDatabaseUnit(actionDslFile, "$ss")
	   bill.ExecuteFile(infile,actionDsl,extraData,pattkey)
  } 
  
  
  //var actionDsl = ReadActionDsl(actionDslFile)
//   	   var bill = new Billing
//   	   val pattkey = bill.popPatternKey(bill.popContainingFolder(infile), actionDsl )
//       var extraData = bill.PopXtraDatabaseUnit(actionDslFile, "$ss")
//	   bill.ExecuteFile(infile,actionDsl,extraData,pattkey)

  @Test def  DirectoryLevelExecutionTest{
    	   var bill = new Billing
	   var actiondsl = ReadActionDsl(actionDslFile)
	   val pattkey = bill.popPatternKey(bill.popContainingFolder(infile), actiondsl)
	   var datekey = ""
	   var infiledir = actiondsl("infiledir")(0)(1)+datekey
	   var iox = new IOX()
	   var extraData = bill.PopXtraDatabaseUnit(actionDslFile, pattkey)
	   for(directory <- iox.GetFiles(infiledir).filter(!_.getName.contains('.') ))
		   for(file <- iox.GetFiles(directory.getAbsolutePath).filter(_.getName.endsWith(".txt"))){		
		        bill.ExecuteFile(file.toPath.toString,actiondsl,extraData,pattkey)
			   	println("dirlevel | "+file.toString)
		   }
	} 
  
   @Test def PriceTotalTest{
        var book = new HSSFWorkbook
	   	var sheet = book.createSheet
	   	var cell = sheet.createRow(1).createCell(10);
        cell.setCellValue(102)
        var cell2 = sheet.createRow(2).createCell(10);
        cell2.setCellValue(100)
        var cell12 = sheet.createRow(3).createCell(10);
        cell12.setCellValue(100)
		var fileOut = new FileOutputStream("/Users/keitaroemotion/dev/garage/xbs/calctest.xls");
	    book.write(fileOut);
	    fileOut.close;
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
