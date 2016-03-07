class Mediator1
{
	public function get me(arg:Boolean):void  { return true; }

	function get me(a:int, b:CCC=null):void  { return 4; }

	override function get me():void  { return "value"; }

	public function get me():void  { if (x) return "{}}{"; }

	public function get me(arg:Boolean):void  { return true; }

	function get me(a:int, b:CCC=null):void
	{
		return 4;
	}

	override function get me():void
	{
		return "value";
	}

	public function get me():void
	{
		if (x)
			return "{}}{";
	}
}
