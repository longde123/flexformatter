package x
{

	class y
	{
		import a.b.*;
		import a.b.c;

		import d.e.f;
		import d.e.z;

		include "abc";
		include "ddd2";
		include "xyz";

		public namespace a="bs2";
		private namespace a="bs2";
		protected namespace a="bs2";
		namespace a="bs2";
		public namespace m="bs";
		private namespace m="bs";
		protected namespace m="bs";
		namespace m="bs";
		public namespace zx="bs3";
		private namespace zx="bs3";
		protected namespace zx="bs3";
		namespace zx="bs3";

//default xml namespace="abc";
		/**
		 * default namespace z22
		 */
//default xml namespace="z22";
//default xml namespace="_abc";
		use namespace abc;
//use namespace bbbbb
		use namespace bbbbb;
		use namespace x, y;

		public static var b:int=3;
		private static var b:int=3;
		protected static var b:int=3;
		static var b:int=3;
		public static var h, z:int=4;
		private static var h, z:int=4;
		protected static var h, z:int=4;
		static var h, z:int=4;
		public static var i:int=2;
		private static var i:int=2;
		protected static var i:int=2;
		static var i:int=2;

		native public static function abc(y:int):void
		{
		}

//double comment on abc
//second part of double comment on abc
		native protected static function abc(y:int):void
		{
		}

		native private static function abc(y:int):void
		{
		}

		native static function abc(y:int):void
		{
		}

		public static dynamic function dr2(x:int):void
		{
		}

		protected static dynamic function dr2(x:int):void
		{
		}

		private static dynamic function dr2(x:int):void
		{
		}

		static dynamic function dr2(x:int):void
		{
		}

		final public static override function dr2(x:int, y:int):void
		{
		}

		protected final static override function dr2(x:int, y:int):void
		{
		}

		private final static override function dr2(x:int, y:int):void
		{
		}

		final static override function dr2(x:int, y:int):void
		{
		}

		public var b:int=3;
		private var b:int=3;
		protected var b:int=3;
		var b:int=3;
		public var h, z:int=4;
		private var h, z:int=4;
		protected var h, z:int=4;
		var h, z:int=4;
		public var i:int=2;
		private var i:int=2;
		protected var i:int=2;
		var i:int=2;

		native public function abc(y:int):void
		{
		}

		native protected function abc(y:int):void
		{
		}

		native private function abc(y:int):void
		{
		}

		native function abc(y:int):void
		{
		}

		public dynamic function dr2(x:int):void
		{
		}

		protected dynamic function dr2(x:int):void
		{
		}

		private dynamic function dr2(x:int):void
		{
		}

		dynamic function dr2(x:int):void
		{
		}

		/**
		 * 1st comment on dr2
		 */
		/**
		 * 2nd comment on dr2
		 */
		final public override function dr2(x:int, y:int):void
		{
		}

		protected final override function dr2(x:int, y:int):void
		{
		}

		private final override function dr2(x:int, y:int):void
		{
		}

		final override function dr2(x:int, y:int):void
		{
		}
	}
}
