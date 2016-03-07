package testpackage
{

	class xClass
	{

		//--------------------------------------
		// noPhysicalVar 
		//--------------------------------------

		public function get noPhysicalVar():int
		{

		}

		public function set noPhysicalVar():int
		{
		}

		//--------------------------------------
		// propVar 
		//--------------------------------------

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

		protected var m:int;
		protected var r:int;


		//--------------------------------------
		// _y 
		//--------------------------------------

		private var _y:int;

		//--------------------------------------
		// x 
		//--------------------------------------

		private var x:int;

		private function get x():int
		{
		}

		//--------------------------------------
		// z 
		//--------------------------------------

		private var _z:int;

		private function set z():int
		{
		}

		//----------------------------------------------------------
		//
		//
		//   Property 
		//
		//
		//----------------------------------------------------------

		var anotherVar:int;
	}
}
