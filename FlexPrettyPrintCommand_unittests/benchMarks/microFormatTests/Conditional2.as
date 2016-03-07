package
{
    import flash.display.Sprite;

    public class FormatBugs extends Sprite
    {
		OTHER::allowLocal {
			private var field1:int;
		}
		
		OTHER::allowLocal {
			private var field1:int;
			function x(){}
		}
		
		COND::Debug
        public function FormatBugs()
        {
			map::what
			{
				{
					for (;;);
				}
			}
        }
    }
}
