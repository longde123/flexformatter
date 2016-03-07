package x
{

	class y extends m
	{
		import x.y;

		/**
		   VAR 1
		 */
		public var mVar:Number   =2;
		/**
		   VAR 2
		 */
		[bindable]
		public var mVariable:Number  =2223 * 10;

		namespace mx_internal;

		const t:Number  =2;
		public var m:int =  43;
		default xml namespace=sxy;
		var i:int;

		function x()
		{
			var f1:integer=2;
			var o:Object  =new Object();
			//a comment
			var i:int=2;
			try
			{
			}
			catch (e:exception)
			{
			}
			var object:Object=new Object();
			var z:What       =i + f1() * o;
			var l            =z;
			for (; ; )
				;
			var m:Number=2;
		}
	}
}
