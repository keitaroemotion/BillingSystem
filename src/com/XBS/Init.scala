package com.XBS


object Init extends App {
  
    var sheet =new Excel().getSheet("/Users/keitaroemotion/dev/garage/portal_price.xls",0)
    new Excel().ReadExcel(sheet,233,8).keys.foreach(x => {
    	println(x)
    })
	println()
	
}

		