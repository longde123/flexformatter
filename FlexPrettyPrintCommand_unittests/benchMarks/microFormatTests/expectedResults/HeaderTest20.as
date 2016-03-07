package testpackage
{

	class x
	{
		private function get x():int
		{
		}

		//----------------------------------------------------------
		//
		//
		//   Property 
		//
		//
		//----------------------------------------------------------

		//----------------------------------------------------------
		//
		//
		//   Property#public 
		//
		//
		//----------------------------------------------------------

		public function get noPhysicalVar():int
		{

		}

		public function set noPhysicalVar():int
		{
		}

		/**
		 * bindable
		 */
		[Bindable]
		var _propVar:int;

		public function get propVar():int
		{

		}

		public function set propVar():int
		{
		}

		//----------------------------------------------------------
		//
		//
		//   Property#protected 
		//
		//
		//----------------------------------------------------------

		protected var m:int;
		protected var r:int;

		var anotherVar:int;

		//----------------------------------------------------------
		//
		//
		//   Property#private 
		//
		//
		//----------------------------------------------------------

		private var _y:int;

		private var x:int;

		private var _z:int;

		private function set z():int
		{
		}
	}
}
