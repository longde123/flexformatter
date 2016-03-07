package
{
	import flash.display.Sprite;

	public interface FormatBugs
	{
		OTHER::allowLocal
		{
			private var field1:int;
		}

		OTHER::allowLocal
		{
			private var field1:int;

			function x();
		}

		COND::Debug
		public function FormatBugs();
	}
}
