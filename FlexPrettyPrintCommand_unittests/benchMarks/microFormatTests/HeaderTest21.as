package testpackage
{

	class xClass
	{
		/**
		 * bindable
		 */
		[Bindable]
		var _propVar:int;
		
		var anotherVar:int;
		
		public function get propVar():int
		{
			
		}
		
		public function set propVar():int
		{}
		
		private function set z():int{}
		
		public function get noPhysicalVar():int
		{
			
		}
		private function get x():int{}
		
		public function set noPhysicalVar():int
		{}
		
		private var x:int;
		private var _y:int;
		
		private var _z:int;
		
		protected var m:int;
		protected var r:int;
	}
}
