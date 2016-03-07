//------------------------------------------------------------------------------
//
//   Copyright 2010 Ernest corporation. 
//   All rights reserved. 
//
//------------------------------------------------------------------------------


package com.ernest
{

	public class ABC
	{

		public function x():int
		{
		}

		public var myLabel:Label;
		[SkinPart]
		public var myTextInput:TextInput;
		[SkinPart1]
		public var _me:int;

		public function get me():int
		{
		}

		[SkinPart(required="true")]
		var a:void;

		public function set me(value:int):void
		{
		}
	}
}

