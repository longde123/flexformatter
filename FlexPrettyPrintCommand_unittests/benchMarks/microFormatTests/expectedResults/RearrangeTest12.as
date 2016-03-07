package
{

	public class ClassWithStaticInit
	{
		{
			FIRST.timeStamp=new Date().time;
		}

		public function ClassWithStaticInit()
		{
		}

		public const FIRST:ClassWithStaticInit=new ClassWithStaticInit();

		public var timeStamp:Number;
	}
}