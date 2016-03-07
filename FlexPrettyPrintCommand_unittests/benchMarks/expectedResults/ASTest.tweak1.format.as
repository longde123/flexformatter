/**package comment*/
package com.sas {
    import com.sas.C2;
    import com.sas.*;



    public class MyClass extends com.sas.C3 implements com.sas.C1 ,C2 {
        use namespace xyz;
        use namespace y ,z;
        default xml namespace = xyz;
        private var x : Array = new Array( [   1 ,2 ,3   ] );
        private var v : Vector.<MyData> = new Vector.<MyData>( [   {  "label": 1 ,
                                                                       "used": 5 ,
                                                                       "free": 10  }   ] ,
                                                               [   {  "label": 2 ,
                                                                       "used": 5 ,
                                                                       "free": 36  }   ] );
        [Bindable]
        [Transient]
        public static var i : int = 2;
        public static var d : int = 3 ,m : String = "sldfkj";
        public const z : String = "A string value";
        public const z2 : String = "A string value" ,z3 : String = "sdlfkj" ,i : int =
            2*3;
        var x : * = 2;
        var x : com.internal.static.default.to.each.namespace.dynamic.final.override.
            native.get.set;
        import com.none.classZ; //comment after end of line


        xyz internal override function doSomething() {
        }


        [Bindable]
        public function MyClass( x : int ,y : com.sas.MyClass = null ,... others : Array ) : com.
            sas.MyClass {
            use namespace t ,q;
            var v : Vector = new Vector( [   {  "label": 1 ,"used": 5 ,"free": 10  }   ] ,
                                         [   {  "label": 2 ,"used": 5 ,"free": 36  }   ] );
            var x : int;
            var x;
            var x : int = 2;
            var x : com.company.ClassName = new com.company.ClassName( 3 );




            if ( x>=7 ) {
                {
                }
            } else
                if ( x<5 ) {
                    x++;




                    if ( x>3 )
                        throw new IOException( 34 ,4 ,4 ,3 ,3 ,"error message" );
                } else {
                }
            var x : Boolean = !!true;




            do {
                i--;
            } while ( i>10 );




            mainLoop: for ( var i : int = 3 ;i<4 ;i += 2 ) {
                if ( i>10 )
                    continue;




                for ( var z : int = 6 ;z>9 ;z-- ) {
                    if ( z>100 )
                        continue mainLoop;
                }
            }




            for ( var i : int = 3 ;i<funcCall( 3 ) ;i++ )
                while ( i>10 )
                    i--;




            for ( var i : int = 3 ;i<funcCall( 3 ) ;i++ )
                for ( var i : int = 3 ;i<funcCall( 3 ) ;i++ )
                    i -= 3;




            switch ( i+10 ) {
                case 1:
                    i++;
                    break;
                case func( i ):
                    func( z );
                    break;
                default:
                    func( 9 );
            }




            while ( true ) {
                while ( false )
                    x += 3;
            }
            {
                switch ( x ) {
                }
            }
            Config::debug {
                trace( "debugging output" );
            }




            label: while ( true ) {
                labeli: i++;




                label2: if ( i<=2 )
                    break label;
            }
            {
                {
                    var x : int = 2;
                }
            }




            try {
                readFile( 10 ,"c:\\file path" );
            } catch ( e : IOException ) {
                trace( e );
            } finally {
                closeFile( lastRead.path );
            }




            for ( i in variable ) {
            }




            for each ( i in variable ) {
                i<<2;
                i>>2;
                i += 2;
                i = i+2;
                i -= 2;
                b = ( i>=2 );
                b = ( i<=2 );
                b = ( i==j );
                b = ( i===j );
                b = ( i!=j );
                b = ( i+2 );
                b = ( i-2 );
                b = ( i/2 );
                b = i*2;
                b = i%2;
                b = i>2;
                b = i<2;
                b = i!==2;
                b++;
                b--;
                b>>>2;
                b = i&z;
                b = i|z;
                b = i^z;
                b = !b;
                b = ~b;
                b = i&&z;
                b = i||z;
                b = ( i>2 )?1:2;
                b *= 2;
                b /= 2;
                b %= 2;
                b <<= 2;
                b >>= 2;
                b >>>= 2;
                b &= z;
                b ^= i;
                b |= i;
            }
            var x : XML = <a><tagB/><tagc/><tag attr1="attr" attr2="sdlfkjsdf">
                        some data1
                        some data2

                        some data3</tag></a>
            var x : XML =
                <>
                    <abc/>
                    <tag attr1="sldkfjsdlfkj" attr2="sldkjfsdlkfj"><subTag/></tag>
                </>
            var x = 2
            var y : Integer
            [Bindable]
            var z : BindableType;




            /**
             * this is a multiline comment
             * that is on several lines
             */
            /** this is another
             * comment that is on multiple
             * lines, some without
               asterisks
             */
            while ( true )
                ;
            b = ( a+( b+funcCall( 3 ,4 ,4 ,5 ,3 ,"sldkjfsdlkfj" ) )/( c+d ) )>>( ( ( ( 2*
                74+funcCall( 3 ) ) ) ) )
            b = /abc/;




            //comment on column 1
            //comment on column 2 			
            with ( variable.getName() ) {
                i++;
            }
            x.y::z;
            return new com.sas.MyClass( 3 );
        }


        public override function thisFunctionHasAVeryLongNameThatRequiresWrappingSometimes( x : com.
                                                                                            sas.
                                                                                            Type1 ,
                                                                                            z : com.
                                                                                            sas.
                                                                                            Type2 ,
                                                                                            ... z : int ) : com.
            sas.ClassType {
            firstMethodCall( arg1 ,secondMethodCall( arg2 ,arg3 ) ,thirdMethodCall( arg4 ,
                                                                                    fourthMethodCall( "sdfsf" ,
                                                                                                      "sldkfsjldfkj" ,
                                                                                                      true ) ,
                                                                                    arg5 ) );
            a[    4    ] = 2;
            super.thisFunctionHasAVeryLongNameThatRequiresWrappingSometimes( 2 );
            this.thisFunctionHasAVeryLongNameThatRequiresWrappingSometimes( 4 );
        }


        public function get varName() : varType {
            return varName;
        }


        public function set varName( value : Integer ) : void {
            varName = value;
        }
    }


    function x( i : int ) : boolean {
        return true;
    }
}


function x( i : int ) : boolean {
    return true;
}



interface iface extends myInterface {
    public function set varName( i : int );
    public function get varName() : int;
}



class x extends z {
    function y() {
    }
}
