package com.XBS

class Calc {
	def f(dsline:List[String], num:Float):Float={
		try{
			dsline(num.toInt).toFloat
		}catch{
		  case e:Exception =>  0f  
		}
	}

	def caluculateUnit(text:String, dslfile:List[String]):Float={
	  try{
		  var addition = List[Float]()
		  var subtraction = List[Float]()
		  for(t <- text.split('+')){
				if(!t.contains("-")){ addition = addition.+:(multiply(t,dslfile))}	
				else{
				  var i = 0
				  for(s <- t.split('-')){
					if(i == 0){
					  addition = addition.+:(multiply(s,dslfile))
					}else{
					  subtraction = subtraction.+:(multiply(s,dslfile))
					}  
				    i = i+1
				  }
				}
			}
			
			var tmp = 0f
			for(elem <- addition){tmp += elem}
			for(elem <- subtraction){ tmp -=  elem}
			tmp
	  }catch{
	    case e:Exception => -1f
	    // writelog
	  }
	}
	
	def multiply(text:String, dslfile:List[String]):Float={
		var tmp = 1.0f  
		
		for(t <- text.split('*')){
		    println("t:"+t)
			tmp *= devide(t,dslfile)
		}
		tmp
	}
	def devide(strip:String ,dslfile:List[String]):Float={
	    var elems = strip.split("/").toList  
		var tmp = f(dslfile,elems(0).toFloat)
		var i = 0
		
		if(!strip.contains('/')){
			println("str	   "+strip+"|"+f(dslfile,strip.toFloat))
		    f(dslfile,strip.toFloat)
		}
		
		for(s <- strip.split("/")){
			if(i > 0){tmp = tmp/f(dslfile, s.toFloat)}
			i = i + 1
		}
	  tmp
	}

}