package com.XBS

import java.io._

class Dsl {
  
	def ListifyInfile(infile:String):List[List[String]]={
		import scala.io.Source
		var lines = List[List[String]]()
		for(line <- Source.fromFile(new File(infile)).getLines){
				println("={ "+line.split('\t').toList.size )
				lines = lines.+:(line.split('\t').toList)
		}
		lines
	}
}