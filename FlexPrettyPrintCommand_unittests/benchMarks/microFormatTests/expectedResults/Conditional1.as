package com.x
{
	name::config
	{

		x::debug
		[Bindable]
		public static class MyClass
		{
			x::debug
			{
				var i:int=2;
			}
		}
	}

	name::debug
	{
		public var i:int;
	}

}

x::debug
{
	var i:int=2;

	public function x():void
	{
		x::debug
		{
			var i:int=2;
		}
	}
}

var i:int;

function x()
{
}
x::debug
{
}
