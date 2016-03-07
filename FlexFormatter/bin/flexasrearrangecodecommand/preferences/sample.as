////////////////////////////////////////////////////////////
//Existing Copyright <some year>
////////////////////////////////////////////////////////////
package my.company
{
	import com.none.*;
	import mx.core.BitmapAsset;

	public namespace ns="http://aNamespaceLocation/x.html"; 

	[Effect(name="Effect_1", event="event_1")]
	[Event(name="Event_1", type="package.eventType1")]
	[Effect(name="Effect_2", event="event_2")]
	[Event(name="Event_2", type="package.eventType2")]
	[Deprecated]
	[Embed(source="photo.jpg")]
	[DefaultProperty("arr")]
	/**
	 * comment for style_1
	 */
	[Style(name="style_1")]
	[Style(name="style_2")]
	[Style(name="style_0")]
	[Style(name="style_3")]
	
	/**
	 * Photo class
	 *
	 */
	public class Photo extends BitmapAsset {

		import com.none.task.*;
		import mx.core.Bitmap;

		[Bindable]
		public var bindableProp:Boolean=true;

		[Bindable]
		[ArrayElementType("Number")]
		protected var arr:Array=[1,2,3];

		public function Photo()
		{
		}

		use namespace mx_internal;


		default xml namespace=ns;

		namespace definedNamespace="NS";

		include "snippets/pk/formulas.as";
		
		public static function DoOperation()
		{}
		
		public function get prop():int
		{
			return _prop*10;
		}

		public override function x(i:int, y:Number){} //takes 2 parameters
		
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		//
		// Existing sample header comment (i.e. not added by FlexFormatter section header feature)
		//
		//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		
		public override function y(){}
		public final dynamic mx_internal function x(i:int){} //takes one parameter
		public function x(t:Boolean){} //takea a different parameter

		mx_internal private static function DoOperation_private()
		{}
		
		private var _prop:int;

		mx_internal private var privateProp:Boolean;
		var internalProp;

		[Transient]	protected static var count:Number;


	}

}
