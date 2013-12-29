package com.XBS

import java.io._
import org.apache.poi.hwpf.usermodel.DateAndTime
import java.util.Calendar

class LoggerX {
    var pathdef = "/Users/keitaroemotion/dev/"
      
    def println{
    	print("\n")
    }  
    def println(str:String){
    	print(str+"\n")
    }
      
    def GetMethodName:String={
		Thread.currentThread().getStackTrace()(2).toString()
	}
    def GetMethodName(idx:Int):String={
		Thread.currentThread().getStackTrace()(idx).toString()
	}
    
    
	def print(str:String){
	    val mn = GetMethodName(4).replace('.', '|')
		val fw = new BufferedWriter( new FileWriter(pathdef+ popDateShort+ ".log", true))
	    try {
	      fw.append(mn+"|"+str)
	    } finally {
	      fw.close()
	    }
	}
    
    
        def popDateLong:String={
				popDate("yyyyMMddmmss")
		}
		
		def popDateShort:String={
				popDate("yyyyMMdd")
		}
		
		def popDate(template:String):String={
			var dateFormat = new java.text.SimpleDateFormat(template)
			var cal = Calendar.getInstance()
			dateFormat.format(cal.getTime())
		}

}