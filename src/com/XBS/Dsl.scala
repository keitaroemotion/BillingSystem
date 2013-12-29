package com.XBS

import java.io._
import scala.io.Source

class Dsl {
    val log = new LoggerX
  
	def ListifyInfile(infile:String):List[List[String]]={
	  try{
	       log.println
			var lines = List[List[String]]()
			for(line <- Source.fromFile(new File(infile)).getLines){lines = lines.+:(line.split('\t').toList)}
			lines
	  }catch{
	    		case e:Exception =>  log.println(e) ;throw e 
	  }
	}
}