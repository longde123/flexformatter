package
{
	
	public class ClassWithStaticInit
	{
		public const FIRST:ClassWithStaticInit=new ClassWithStaticInit();
		
		public var timeStamp:Number;
		
		{
			FIRST.timeStamp=new Date().time;
		}
		public function ClassWithStaticInit () {
		}
	}
}