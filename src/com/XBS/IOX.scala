package com.XBS

class IOX {
   def GetFiles(infiledir:String):Array[java.io.File]={
	   new java.io.File(infiledir).listFiles
   }
}