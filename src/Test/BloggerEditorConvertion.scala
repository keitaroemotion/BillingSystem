package Test

import org.junit._
import scala.io.Source

class BloggerEditorConvertion {
	@Test def ConvertTest{
		val directory = "//Users//keitaroemotion//dev//garage//"
	    var txt = Convert(directory+"blog.txt")
	    CreateFile(txt,directory+"blogconverted.txt" )
	}
	
	
	def CreateFile(text:String, path:String)={
		import java.io._
		val writer = new BufferedWriter( new FileWriter(path, false))
		
	    try {
	      writer.append(text)
	    } finally {
	      writer.close()
	    }	
	    java.awt.Desktop.getDesktop().open(new File(path));
	}
	
	
	def Format(text:String):String={
		text.replace("<", "&lt;").replace(">", "&gt;")
	}
	
	def Convert(infile:String):String={
	    var tmp =""
	    for(line <- Format(scala.io.Source.fromFile(infile,"Shift_JIS").mkString).lines){
          tmp += line+"\n"  
        }
        tmp
	}
  
}