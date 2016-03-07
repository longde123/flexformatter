package {

import flash.display.Sprite;

public class Test extends Sprite {
  
   public function Test () {
   
   }
   
   private var _noHeader : String;
   private var _vvv;
   private var _text : String;
   public function get vvv():Boolean {}
   public function get text () : String {
      return _text;
   }
   
   public function set text ( value : String ) : void {
      _text = value;
   }
   public function set yyy(m:Boolean):void {}
   
}
}
