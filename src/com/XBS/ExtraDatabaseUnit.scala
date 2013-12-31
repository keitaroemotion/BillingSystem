package com.XBS

import scala.collection.mutable.Map


class ExtraDatabaseUnit {
		var Html = Map[String,List[HtmlLocation]]()
		var PriceTable = Map[String,List[List[String]]]()
		def Construct(input:HtmlUnit):ExtraDatabaseUnit={
				var obj = new ExtraDatabaseUnit()
				obj.Html = input.HtmlReferencePointers
				obj.PriceTable = input.RefTableLocation
				obj
		}
}