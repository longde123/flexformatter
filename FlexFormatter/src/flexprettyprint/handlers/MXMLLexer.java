// $ANTLR 3.1.1 MXMLLexer.g3 2011-08-05 17:41:17

package flexprettyprint.handlers;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/** XML parser by Oliver Zeigermann October 10, 2005; posted to Antlr examples */
public class MXMLLexer extends Lexer {
    public static final int VALUE_int=35;
    public static final int END_TAG=27;
    public static final int CDATA=26;
    public static final int EMPTY_TAG_OPEN=4;
    public static final int EQ_int=31;
    public static final int TAG_CLOSE=17;
    public static final int EMPTY_ELEMENT=28;
    public static final int LETTER=37;
    public static final int DECL_START=18;
    public static final int ATTRIBUTE=19;
    public static final int END_TAG_OPEN=33;
    public static final int XMLDECL=5;
    public static final int EOL_HELPER=39;
    public static final int START_TAG=23;
    public static final int DECL_STOP=20;
    public static final int EMPTYTAG_CLOSE=30;
    public static final int VALUE=14;
    public static final int INTERNAL_DTD=16;
    public static final int EOF=-1;
    public static final int DOCTYPE_tag=11;
    public static final int GENERIC_ID_int=36;
    public static final int TAG_OPEN=29;
    public static final int PCDATA=25;
    public static final int EOL=24;
    public static final int WS=9;
    public static final int DOCUMENT=10;
    public static final int GENERIC_ID=12;
    public static final int ELEMENT=7;
    public static final int PI=21;
    public static final int DOCTYPE=6;
    public static final int EQ=32;
    public static final int COMMENT=8;
    public static final int OTHERWS=38;
    public static final int SYSTEM_tag=13;
    public static final int XML=22;
    public static final int COMMENT_int=34;
    public static final int PUBLIC_tag=15;

       private List<CommonToken> mRawTokens=new ArrayList<CommonToken>();
       int lastLine=1;
       int lastCharPos=0;
       public void addToken(Token t, int type, int channel)
       {
       		((CommonToken)t).setType(type);
       		((CommonToken)t).setChannel(channel);
       		t.setLine(lastLine);
       		lastLine=input.getLine();
       		t.setCharPositionInLine(lastCharPos);
       		lastCharPos=input.getCharPositionInLine();
       		mRawTokens.add((CommonToken)t);
       }
       public List<CommonToken> getTokens()
       {
       		return mRawTokens;
       }
       
    public void reset()
    {
    	super.reset(); // reset all recognizer state variables
    	if (input instanceof ANTLRStringStream)
    	{
    		((ANTLRStringStream)input).reset();
    	}
    }

       


    // delegates
    // delegators

    public MXMLLexer() {;} 
    public MXMLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public MXMLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "MXMLLexer.g3"; }

    // $ANTLR start "DOCUMENT"
    public final void mDOCUMENT() throws RecognitionException {
        try {
            int _type = DOCUMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // MXMLLexer.g3:49:7: ( ( XMLDECL | DOCTYPE | ELEMENT | COMMENT | WS )* )
            // MXMLLexer.g3:50:7: ( XMLDECL | DOCTYPE | ELEMENT | COMMENT | WS )*
            {
            // MXMLLexer.g3:50:7: ( XMLDECL | DOCTYPE | ELEMENT | COMMENT | WS )*
            loop1:
            do {
                int alt1=6;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='<') ) {
                    switch ( input.LA(2) ) {
                    case '?':
                        {
                        alt1=1;
                        }
                        break;
                    case '!':
                        {
                        int LA1_5 = input.LA(3);

                        if ( (LA1_5=='D') ) {
                            alt1=2;
                        }
                        else if ( (LA1_5=='-') ) {
                            alt1=4;
                        }


                        }
                        break;
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ':
                    case ':':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '_':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'm':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'q':
                    case 'r':
                    case 's':
                    case 't':
                    case 'u':
                    case 'v':
                    case 'w':
                    case 'x':
                    case 'y':
                    case 'z':
                        {
                        alt1=3;
                        }
                        break;

                    }

                }
                else if ( ((LA1_0>='\t' && LA1_0<='\n')||LA1_0=='\r'||LA1_0==' ') ) {
                    alt1=5;
                }


                switch (alt1) {
            	case 1 :
            	    // MXMLLexer.g3:50:9: XMLDECL
            	    {
            	    mXMLDECL(); 

            	    }
            	    break;
            	case 2 :
            	    // MXMLLexer.g3:50:19: DOCTYPE
            	    {
            	    mDOCTYPE(); 

            	    }
            	    break;
            	case 3 :
            	    // MXMLLexer.g3:50:29: ELEMENT
            	    {
            	    mELEMENT(); 

            	    }
            	    break;
            	case 4 :
            	    // MXMLLexer.g3:50:39: COMMENT
            	    {
            	    mCOMMENT(); 

            	    }
            	    break;
            	case 5 :
            	    // MXMLLexer.g3:50:49: WS
            	    {
            	    mWS(); 

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOCUMENT"

    // $ANTLR start "DOCTYPE"
    public final void mDOCTYPE() throws RecognitionException {
        try {
            Token dt=null;
            Token rootElementName=null;
            Token sys=null;
            Token sys1=null;
            Token pub=null;
            Token sys2=null;
            Token dtd=null;
            Token close=null;

            // MXMLLexer.g3:55:5: (dt= DOCTYPE_tag WS rootElementName= GENERIC_ID WS ( (sys= SYSTEM_tag WS sys1= VALUE | pub= PUBLIC_tag WS pub= VALUE WS sys2= VALUE ) ( WS )? )? (dtd= INTERNAL_DTD )? close= TAG_CLOSE )
            // MXMLLexer.g3:56:9: dt= DOCTYPE_tag WS rootElementName= GENERIC_ID WS ( (sys= SYSTEM_tag WS sys1= VALUE | pub= PUBLIC_tag WS pub= VALUE WS sys2= VALUE ) ( WS )? )? (dtd= INTERNAL_DTD )? close= TAG_CLOSE
            {
            int dtStart107 = getCharIndex();
            mDOCTYPE_tag(); 
            dt = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dtStart107, getCharIndex()-1);
            addToken(dt, DOCTYPE_tag, 0);
            mWS(); 
            int rootElementNameStart115 = getCharIndex();
            mGENERIC_ID(); 
            rootElementName = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, rootElementNameStart115, getCharIndex()-1);
             System.out.println("ROOTELEMENT: "+rootElementName.getText()); 
            mWS(); 
            // MXMLLexer.g3:59:9: ( (sys= SYSTEM_tag WS sys1= VALUE | pub= PUBLIC_tag WS pub= VALUE WS sys2= VALUE ) ( WS )? )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='P'||LA4_0=='S') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // MXMLLexer.g3:60:13: (sys= SYSTEM_tag WS sys1= VALUE | pub= PUBLIC_tag WS pub= VALUE WS sys2= VALUE ) ( WS )?
                    {
                    // MXMLLexer.g3:60:13: (sys= SYSTEM_tag WS sys1= VALUE | pub= PUBLIC_tag WS pub= VALUE WS sys2= VALUE )
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0=='S') ) {
                        alt2=1;
                    }
                    else if ( (LA2_0=='P') ) {
                        alt2=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 2, 0, input);

                        throw nvae;
                    }
                    switch (alt2) {
                        case 1 :
                            // MXMLLexer.g3:60:15: sys= SYSTEM_tag WS sys1= VALUE
                            {
                            int sysStart168 = getCharIndex();
                            mSYSTEM_tag(); 
                            sys = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, sysStart168, getCharIndex()-1);
                            addToken(sys, SYSTEM_tag, 0);
                            mWS(); 
                            int sys1Start176 = getCharIndex();
                            mVALUE(); 
                            sys1 = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, sys1Start176, getCharIndex()-1);
                             System.out.println("SYSTEM: "+sys1.getText()); 

                            }
                            break;
                        case 2 :
                            // MXMLLexer.g3:63:15: pub= PUBLIC_tag WS pub= VALUE WS sys2= VALUE
                            {
                            int pubStart232 = getCharIndex();
                            mPUBLIC_tag(); 
                            pub = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, pubStart232, getCharIndex()-1);
                            addToken(pub, PUBLIC_tag, 0);
                            mWS(); 
                            int pubStart240 = getCharIndex();
                            mVALUE(); 
                            pub = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, pubStart240, getCharIndex()-1);
                            mWS(); 
                            int sys2Start246 = getCharIndex();
                            mVALUE(); 
                            sys2 = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, sys2Start246, getCharIndex()-1);
                             System.out.println("PUBLIC: "+pub.getText()); 
                             System.out.println("SYSTEM: "+sys2.getText()); 

                            }
                            break;

                    }

                    // MXMLLexer.g3:67:13: ( WS )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( ((LA3_0>='\t' && LA3_0<='\n')||LA3_0=='\r'||LA3_0==' ') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // MXMLLexer.g3:67:15: WS
                            {
                            mWS(); 

                            }
                            break;

                    }


                    }
                    break;

            }

            // MXMLLexer.g3:69:9: (dtd= INTERNAL_DTD )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='[') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // MXMLLexer.g3:69:11: dtd= INTERNAL_DTD
                    {
                    int dtdStart346 = getCharIndex();
                    mINTERNAL_DTD(); 
                    dtd = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dtdStart346, getCharIndex()-1);
                     System.out.println("INTERNAL DTD: "+dtd.getText()); 

                    }
                    break;

            }

            int closeStart380 = getCharIndex();
            mTAG_CLOSE(); 
            close = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, closeStart380, getCharIndex()-1);
            addToken(close, TAG_CLOSE, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "DOCTYPE"

    // $ANTLR start "SYSTEM_tag"
    public final void mSYSTEM_tag() throws RecognitionException {
        try {
            // MXMLLexer.g3:77:2: ( 'SYSTEM' )
            // MXMLLexer.g3:77:4: 'SYSTEM'
            {
            match("SYSTEM"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "SYSTEM_tag"

    // $ANTLR start "PUBLIC_tag"
    public final void mPUBLIC_tag() throws RecognitionException {
        try {
            // MXMLLexer.g3:80:2: ( 'PUBLIC' )
            // MXMLLexer.g3:80:4: 'PUBLIC'
            {
            match("PUBLIC"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "PUBLIC_tag"

    // $ANTLR start "DOCTYPE_tag"
    public final void mDOCTYPE_tag() throws RecognitionException {
        try {
            // MXMLLexer.g3:83:2: ( '<!DOCTYPE' )
            // MXMLLexer.g3:83:4: '<!DOCTYPE'
            {
            match("<!DOCTYPE"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "DOCTYPE_tag"

    // $ANTLR start "INTERNAL_DTD"
    public final void mINTERNAL_DTD() throws RecognitionException {
        try {
            // MXMLLexer.g3:85:23: ( '[' ( options {greedy=false; } : . )* ']' )
            // MXMLLexer.g3:85:25: '[' ( options {greedy=false; } : . )* ']'
            {
            match('['); 
            // MXMLLexer.g3:85:29: ( options {greedy=false; } : . )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==']') ) {
                    alt6=2;
                }
                else if ( ((LA6_0>='\u0000' && LA6_0<='\\')||(LA6_0>='^' && LA6_0<='\uFFFF')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // MXMLLexer.g3:85:56: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            match(']'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "INTERNAL_DTD"

    // $ANTLR start "PI"
    public final void mPI() throws RecognitionException {
        try {
            Token ds=null;
            Token target=null;
            Token de=null;

            // MXMLLexer.g3:87:13: (ds= DECL_START target= GENERIC_ID ( WS )? ( ATTRIBUTE ( WS )? )* de= DECL_STOP )
            // MXMLLexer.g3:88:9: ds= DECL_START target= GENERIC_ID ( WS )? ( ATTRIBUTE ( WS )? )* de= DECL_STOP
            {
            int dsStart471 = getCharIndex();
            mDECL_START(); 
            ds = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dsStart471, getCharIndex()-1);
            int targetStart475 = getCharIndex();
            mGENERIC_ID(); 
            target = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, targetStart475, getCharIndex()-1);
            // MXMLLexer.g3:88:41: ( WS )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>='\t' && LA7_0<='\n')||LA7_0=='\r'||LA7_0==' ') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // MXMLLexer.g3:88:41: WS
                    {
                    mWS(); 

                    }
                    break;

            }

             System.out.println("PI: "+target.getText()); 
            // MXMLLexer.g3:90:9: ( ATTRIBUTE ( WS )? )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==':'||(LA9_0>='A' && LA9_0<='Z')||LA9_0=='_'||(LA9_0>='a' && LA9_0<='z')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // MXMLLexer.g3:90:11: ATTRIBUTE ( WS )?
            	    {
            	    mATTRIBUTE(); 
            	    // MXMLLexer.g3:90:21: ( WS )?
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);

            	    if ( ((LA8_0>='\t' && LA8_0<='\n')||LA8_0=='\r'||LA8_0==' ') ) {
            	        alt8=1;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // MXMLLexer.g3:90:21: WS
            	            {
            	            mWS(); 

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            int deStart514 = getCharIndex();
            mDECL_STOP(); 
            de = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, deStart514, getCharIndex()-1);
            addToken(de, DECL_STOP, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "PI"

    // $ANTLR start "XMLDECL"
    public final void mXMLDECL() throws RecognitionException {
        try {
            Token ds=null;
            Token xml=null;
            Token de=null;

            // MXMLLexer.g3:93:18: (ds= DECL_START xml= XML ( WS )? ( ATTRIBUTE ( WS )? )* de= DECL_STOP )
            // MXMLLexer.g3:94:9: ds= DECL_START xml= XML ( WS )? ( ATTRIBUTE ( WS )? )* de= DECL_STOP
            {
            int dsStart538 = getCharIndex();
            mDECL_START(); 
            ds = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dsStart538, getCharIndex()-1);
            addToken(ds, DECL_START, 0);
            int xmlStart544 = getCharIndex();
            mXML(); 
            xml = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xmlStart544, getCharIndex()-1);
            addToken(xml, XML, 0);
            // MXMLLexer.g3:94:89: ( WS )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>='\t' && LA10_0<='\n')||LA10_0=='\r'||LA10_0==' ') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // MXMLLexer.g3:94:89: WS
                    {
                    mWS(); 

                    }
                    break;

            }

             System.out.println("XML declaration"); 
            // MXMLLexer.g3:96:9: ( ATTRIBUTE ( WS )? )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==':'||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // MXMLLexer.g3:96:11: ATTRIBUTE ( WS )?
            	    {
            	    mATTRIBUTE(); 
            	    // MXMLLexer.g3:96:21: ( WS )?
            	    int alt11=2;
            	    int LA11_0 = input.LA(1);

            	    if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
            	        alt11=1;
            	    }
            	    switch (alt11) {
            	        case 1 :
            	            // MXMLLexer.g3:96:21: WS
            	            {
            	            mWS(); 

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            int deStart585 = getCharIndex();
            mDECL_STOP(); 
            de = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, deStart585, getCharIndex()-1);
            addToken(de, DECL_STOP, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "XMLDECL"

    // $ANTLR start "XML"
    public final void mXML() throws RecognitionException {
        try {
            // MXMLLexer.g3:100:2: ( ( 'x' | 'X' ) ( 'm' | 'M' ) ( 'l' | 'L' ) )
            // MXMLLexer.g3:100:4: ( 'x' | 'X' ) ( 'm' | 'M' ) ( 'l' | 'L' )
            {
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='M'||input.LA(1)=='m' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "XML"

    // $ANTLR start "DECL_START"
    public final void mDECL_START() throws RecognitionException {
        try {
            // MXMLLexer.g3:102:3: ( '<?' )
            // MXMLLexer.g3:102:5: '<?'
            {
            match("<?"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "DECL_START"

    // $ANTLR start "DECL_STOP"
    public final void mDECL_STOP() throws RecognitionException {
        try {
            // MXMLLexer.g3:106:3: ( '?>' )
            // MXMLLexer.g3:106:5: '?>'
            {
            match("?>"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "DECL_STOP"

    // $ANTLR start "ELEMENT"
    public final void mELEMENT() throws RecognitionException {
        try {
            Token t=null;
            Token pi=null;

            // MXMLLexer.g3:110:5: ( ( START_TAG ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )* END_TAG | EMPTY_ELEMENT ) )
            // MXMLLexer.g3:110:7: ( START_TAG ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )* END_TAG | EMPTY_ELEMENT )
            {
            // MXMLLexer.g3:110:7: ( START_TAG ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )* END_TAG | EMPTY_ELEMENT )
            int alt14=2;
            alt14 = dfa14.predict(input);
            switch (alt14) {
                case 1 :
                    // MXMLLexer.g3:110:9: START_TAG ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )* END_TAG
                    {
                    mSTART_TAG(); 
                    // MXMLLexer.g3:111:13: ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )*
                    loop13:
                    do {
                        int alt13=7;
                        alt13 = dfa13.predict(input);
                        switch (alt13) {
                    	case 1 :
                    	    // MXMLLexer.g3:111:14: ELEMENT
                    	    {
                    	    mELEMENT(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // MXMLLexer.g3:112:15: EOL
                    	    {
                    	    mEOL(); 

                    	    }
                    	    break;
                    	case 3 :
                    	    // MXMLLexer.g3:113:15: t= PCDATA
                    	    {
                    	    int tStart713 = getCharIndex();
                    	    mPCDATA(); 
                    	    t = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, tStart713, getCharIndex()-1);
                    	     addToken(t, PCDATA, 0); System.out.println("PCDATA: \""+t.getText()+"\""); 

                    	    }
                    	    break;
                    	case 4 :
                    	    // MXMLLexer.g3:115:15: t= CDATA
                    	    {
                    	    int tStart749 = getCharIndex();
                    	    mCDATA(); 
                    	    t = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, tStart749, getCharIndex()-1);
                    	     addToken(t, CDATA, 0); System.out.println("CDATA: \""+t.getText()+"\""); 

                    	    }
                    	    break;
                    	case 5 :
                    	    // MXMLLexer.g3:117:15: t= COMMENT
                    	    {
                    	    int tStart785 = getCharIndex();
                    	    mCOMMENT(); 
                    	    t = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, tStart785, getCharIndex()-1);
                    	    System.out.println("Comment: \""+t.getText()+"\""); 

                    	    }
                    	    break;
                    	case 6 :
                    	    // MXMLLexer.g3:119:15: pi= PI
                    	    {
                    	    int piStart821 = getCharIndex();
                    	    mPI(); 
                    	    pi = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, piStart821, getCharIndex()-1);

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);

                    mEND_TAG(); 

                    }
                    break;
                case 2 :
                    // MXMLLexer.g3:122:11: EMPTY_ELEMENT
                    {
                    mEMPTY_ELEMENT(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "ELEMENT"

    // $ANTLR start "START_TAG"
    public final void mSTART_TAG() throws RecognitionException {
        try {
            Token open=null;
            Token name=null;
            Token close=null;

            // MXMLLexer.g3:127:5: (open= TAG_OPEN ( WS )? name= GENERIC_ID ( WS )? ( ATTRIBUTE ( WS )? )* close= TAG_CLOSE )
            // MXMLLexer.g3:127:7: open= TAG_OPEN ( WS )? name= GENERIC_ID ( WS )? ( ATTRIBUTE ( WS )? )* close= TAG_CLOSE
            {
            int openStart894 = getCharIndex();
            mTAG_OPEN(); 
            open = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, openStart894, getCharIndex()-1);
            addToken(open, TAG_OPEN, 0);
            // MXMLLexer.g3:127:53: ( WS )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>='\t' && LA15_0<='\n')||LA15_0=='\r'||LA15_0==' ') ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // MXMLLexer.g3:127:53: WS
                    {
                    mWS(); 

                    }
                    break;

            }

            int nameStart903 = getCharIndex();
            mGENERIC_ID(); 
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart903, getCharIndex()-1);
            // MXMLLexer.g3:127:73: ( WS )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( ((LA16_0>='\t' && LA16_0<='\n')||LA16_0=='\r'||LA16_0==' ') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // MXMLLexer.g3:127:73: WS
                    {
                    mWS(); 

                    }
                    break;

            }

             System.out.println("Start Tag: "+name.getText()); 
            // MXMLLexer.g3:129:9: ( ATTRIBUTE ( WS )? )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==':'||(LA18_0>='A' && LA18_0<='Z')||LA18_0=='_'||(LA18_0>='a' && LA18_0<='z')) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // MXMLLexer.g3:129:11: ATTRIBUTE ( WS )?
            	    {
            	    mATTRIBUTE(); 
            	    // MXMLLexer.g3:129:21: ( WS )?
            	    int alt17=2;
            	    int LA17_0 = input.LA(1);

            	    if ( ((LA17_0>='\t' && LA17_0<='\n')||LA17_0=='\r'||LA17_0==' ') ) {
            	        alt17=1;
            	    }
            	    switch (alt17) {
            	        case 1 :
            	            // MXMLLexer.g3:129:21: WS
            	            {
            	            mWS(); 

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            int closeStart940 = getCharIndex();
            mTAG_CLOSE(); 
            close = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, closeStart940, getCharIndex()-1);
            addToken(close, TAG_CLOSE, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "START_TAG"

    // $ANTLR start "EMPTY_ELEMENT"
    public final void mEMPTY_ELEMENT() throws RecognitionException {
        try {
            Token open=null;
            Token name=null;
            Token close=null;

            // MXMLLexer.g3:133:5: (open= TAG_OPEN ( WS )? name= GENERIC_ID ( WS )? ( ATTRIBUTE ( WS )? )* close= EMPTYTAG_CLOSE )
            // MXMLLexer.g3:133:7: open= TAG_OPEN ( WS )? name= GENERIC_ID ( WS )? ( ATTRIBUTE ( WS )? )* close= EMPTYTAG_CLOSE
            {
            int openStart964 = getCharIndex();
            mTAG_OPEN(); 
            open = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, openStart964, getCharIndex()-1);
            addToken(open, EMPTY_TAG_OPEN, 0);
            // MXMLLexer.g3:133:59: ( WS )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>='\t' && LA19_0<='\n')||LA19_0=='\r'||LA19_0==' ') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // MXMLLexer.g3:133:59: WS
                    {
                    mWS(); 

                    }
                    break;

            }

            int nameStart973 = getCharIndex();
            mGENERIC_ID(); 
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart973, getCharIndex()-1);
            // MXMLLexer.g3:133:79: ( WS )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>='\t' && LA20_0<='\n')||LA20_0=='\r'||LA20_0==' ') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // MXMLLexer.g3:133:79: WS
                    {
                    mWS(); 

                    }
                    break;

            }

             System.out.println("Empty Element: "+name.getText()); 
            // MXMLLexer.g3:135:9: ( ATTRIBUTE ( WS )? )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==':'||(LA22_0>='A' && LA22_0<='Z')||LA22_0=='_'||(LA22_0>='a' && LA22_0<='z')) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // MXMLLexer.g3:135:11: ATTRIBUTE ( WS )?
            	    {
            	    mATTRIBUTE(); 
            	    // MXMLLexer.g3:135:21: ( WS )?
            	    int alt21=2;
            	    int LA21_0 = input.LA(1);

            	    if ( ((LA21_0>='\t' && LA21_0<='\n')||LA21_0=='\r'||LA21_0==' ') ) {
            	        alt21=1;
            	    }
            	    switch (alt21) {
            	        case 1 :
            	            // MXMLLexer.g3:135:21: WS
            	            {
            	            mWS(); 

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);

            int closeStart1010 = getCharIndex();
            mEMPTYTAG_CLOSE(); 
            close = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, closeStart1010, getCharIndex()-1);
            addToken(close, EMPTYTAG_CLOSE, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "EMPTY_ELEMENT"

    // $ANTLR start "EMPTYTAG_CLOSE"
    public final void mEMPTYTAG_CLOSE() throws RecognitionException {
        try {
            // MXMLLexer.g3:139:2: ( '/>' )
            // MXMLLexer.g3:139:4: '/>'
            {
            match("/>"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "EMPTYTAG_CLOSE"

    // $ANTLR start "TAG_OPEN"
    public final void mTAG_OPEN() throws RecognitionException {
        try {
            // MXMLLexer.g3:143:2: ( '<' )
            // MXMLLexer.g3:143:4: '<'
            {
            match('<'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "TAG_OPEN"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            Token eq=null;

            // MXMLLexer.g3:147:2: (eq= EQ_int )
            // MXMLLexer.g3:148:2: eq= EQ_int
            {
            int eqStart1067 = getCharIndex();
            mEQ_int(); 
            eq = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, eqStart1067, getCharIndex()-1);
            addToken(eq, EQ, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "EQ_int"
    public final void mEQ_int() throws RecognitionException {
        try {
            // MXMLLexer.g3:152:2: ( '=' )
            // MXMLLexer.g3:153:2: '='
            {
            match('='); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "EQ_int"

    // $ANTLR start "ATTRIBUTE"
    public final void mATTRIBUTE() throws RecognitionException {
        try {
            Token name=null;
            Token value=null;

            // MXMLLexer.g3:157:5: (name= GENERIC_ID ( WS )? EQ ( WS )? value= VALUE )
            // MXMLLexer.g3:157:7: name= GENERIC_ID ( WS )? EQ ( WS )? value= VALUE
            {
            int nameStart1105 = getCharIndex();
            mGENERIC_ID(); 
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart1105, getCharIndex()-1);
            // MXMLLexer.g3:157:23: ( WS )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( ((LA23_0>='\t' && LA23_0<='\n')||LA23_0=='\r'||LA23_0==' ') ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // MXMLLexer.g3:157:23: WS
                    {
                    mWS(); 

                    }
                    break;

            }

            mEQ(); 
            // MXMLLexer.g3:157:30: ( WS )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( ((LA24_0>='\t' && LA24_0<='\n')||LA24_0=='\r'||LA24_0==' ') ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // MXMLLexer.g3:157:30: WS
                    {
                    mWS(); 

                    }
                    break;

            }

            int valueStart1117 = getCharIndex();
            mVALUE(); 
            value = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, valueStart1117, getCharIndex()-1);
             System.out.println("Attr: "+name.getText()+"="+value.getText()); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "ATTRIBUTE"

    // $ANTLR start "END_TAG_OPEN"
    public final void mEND_TAG_OPEN() throws RecognitionException {
        try {
            // MXMLLexer.g3:161:22: ( '</' )
            // MXMLLexer.g3:162:2: '</'
            {
            match("</"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "END_TAG_OPEN"

    // $ANTLR start "TAG_CLOSE"
    public final void mTAG_CLOSE() throws RecognitionException {
        try {
            // MXMLLexer.g3:165:3: ( '>' )
            // MXMLLexer.g3:165:5: '>'
            {
            match('>'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "TAG_CLOSE"

    // $ANTLR start "END_TAG"
    public final void mEND_TAG() throws RecognitionException {
        try {
            Token open=null;
            Token name=null;
            Token close=null;

            // MXMLLexer.g3:168:5: (open= END_TAG_OPEN ( WS )? name= GENERIC_ID ( WS )? close= TAG_CLOSE )
            // MXMLLexer.g3:168:7: open= END_TAG_OPEN ( WS )? name= GENERIC_ID ( WS )? close= TAG_CLOSE
            {
            int openStart1175 = getCharIndex();
            mEND_TAG_OPEN(); 
            open = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, openStart1175, getCharIndex()-1);
            addToken(open, END_TAG_OPEN, 0);
            // MXMLLexer.g3:168:61: ( WS )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( ((LA25_0>='\t' && LA25_0<='\n')||LA25_0=='\r'||LA25_0==' ') ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // MXMLLexer.g3:168:61: WS
                    {
                    mWS(); 

                    }
                    break;

            }

            int nameStart1184 = getCharIndex();
            mGENERIC_ID(); 
            name = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, nameStart1184, getCharIndex()-1);
            // MXMLLexer.g3:168:81: ( WS )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( ((LA26_0>='\t' && LA26_0<='\n')||LA26_0=='\r'||LA26_0==' ') ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // MXMLLexer.g3:168:81: WS
                    {
                    mWS(); 

                    }
                    break;

            }

            int closeStart1191 = getCharIndex();
            mTAG_CLOSE(); 
            close = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, closeStart1191, getCharIndex()-1);
            addToken(close, TAG_CLOSE, 0);
             System.out.println("End Tag: "+name.getText()); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "END_TAG"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            Token c=null;

            // MXMLLexer.g3:172:17: (c= COMMENT_int )
            // MXMLLexer.g3:173:2: c= COMMENT_int
            {
            int cStart1220 = getCharIndex();
            mCOMMENT_int(); 
            c = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, cStart1220, getCharIndex()-1);
            addToken(c, COMMENT, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "COMMENT_int"
    public final void mCOMMENT_int() throws RecognitionException {
        try {
            // MXMLLexer.g3:176:2: ( '<!--' ( options {greedy=false; } : . )* '-->' )
            // MXMLLexer.g3:176:4: '<!--' ( options {greedy=false; } : . )* '-->'
            {
            match("<!--"); 

            // MXMLLexer.g3:176:11: ( options {greedy=false; } : . )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='-') ) {
                    int LA27_1 = input.LA(2);

                    if ( (LA27_1=='-') ) {
                        int LA27_3 = input.LA(3);

                        if ( (LA27_3=='>') ) {
                            alt27=2;
                        }
                        else if ( ((LA27_3>='\u0000' && LA27_3<='=')||(LA27_3>='?' && LA27_3<='\uFFFF')) ) {
                            alt27=1;
                        }


                    }
                    else if ( ((LA27_1>='\u0000' && LA27_1<=',')||(LA27_1>='.' && LA27_1<='\uFFFF')) ) {
                        alt27=1;
                    }


                }
                else if ( ((LA27_0>='\u0000' && LA27_0<=',')||(LA27_0>='.' && LA27_0<='\uFFFF')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // MXMLLexer.g3:176:38: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);

            match("-->"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "COMMENT_int"

    // $ANTLR start "CDATA"
    public final void mCDATA() throws RecognitionException {
        try {
            // MXMLLexer.g3:180:2: ( '<![CDATA[' ( options {greedy=false; } : . )* ']]>' )
            // MXMLLexer.g3:180:4: '<![CDATA[' ( options {greedy=false; } : . )* ']]>'
            {
            match("<![CDATA["); 

            // MXMLLexer.g3:180:16: ( options {greedy=false; } : . )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==']') ) {
                    int LA28_1 = input.LA(2);

                    if ( (LA28_1==']') ) {
                        int LA28_3 = input.LA(3);

                        if ( (LA28_3=='>') ) {
                            alt28=2;
                        }
                        else if ( ((LA28_3>='\u0000' && LA28_3<='=')||(LA28_3>='?' && LA28_3<='\uFFFF')) ) {
                            alt28=1;
                        }


                    }
                    else if ( ((LA28_1>='\u0000' && LA28_1<='\\')||(LA28_1>='^' && LA28_1<='\uFFFF')) ) {
                        alt28=1;
                    }


                }
                else if ( ((LA28_0>='\u0000' && LA28_0<='\\')||(LA28_0>='^' && LA28_0<='\uFFFF')) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // MXMLLexer.g3:180:43: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            match("]]>"); 


            }

        }
        finally {
        }
    }
    // $ANTLR end "CDATA"

    // $ANTLR start "PCDATA"
    public final void mPCDATA() throws RecognitionException {
        try {
            // MXMLLexer.g3:184:3: ( (~ ( '<' | '\\n' | '\\r' ) )+ )
            // MXMLLexer.g3:185:4: (~ ( '<' | '\\n' | '\\r' ) )+
            {
            // MXMLLexer.g3:185:4: (~ ( '<' | '\\n' | '\\r' ) )+
            int cnt29=0;
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( ((LA29_0>='\u0000' && LA29_0<='\t')||(LA29_0>='\u000B' && LA29_0<='\f')||(LA29_0>='\u000E' && LA29_0<=';')||(LA29_0>='=' && LA29_0<='\uFFFF')) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // MXMLLexer.g3:185:4: ~ ( '<' | '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<=';')||(input.LA(1)>='=' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt29 >= 1 ) break loop29;
                        EarlyExitException eee =
                            new EarlyExitException(29, input);
                        throw eee;
                }
                cnt29++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "PCDATA"

    // $ANTLR start "VALUE"
    public final void mVALUE() throws RecognitionException {
        try {
            Token v=null;

            // MXMLLexer.g3:188:16: (v= VALUE_int )
            // MXMLLexer.g3:189:3: v= VALUE_int
            {
            int vStart1328 = getCharIndex();
            mVALUE_int(); 
            v = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, vStart1328, getCharIndex()-1);
            addToken(v, VALUE, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "VALUE"

    // $ANTLR start "VALUE_int"
    public final void mVALUE_int() throws RecognitionException {
        try {
            // MXMLLexer.g3:192:20: ( ( '\\\"' (~ '\\\"' )* '\\\"' | '\\'' (~ '\\'' )* '\\'' ) )
            // MXMLLexer.g3:193:9: ( '\\\"' (~ '\\\"' )* '\\\"' | '\\'' (~ '\\'' )* '\\'' )
            {
            // MXMLLexer.g3:193:9: ( '\\\"' (~ '\\\"' )* '\\\"' | '\\'' (~ '\\'' )* '\\'' )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0=='\"') ) {
                alt32=1;
            }
            else if ( (LA32_0=='\'') ) {
                alt32=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // MXMLLexer.g3:193:11: '\\\"' (~ '\\\"' )* '\\\"'
                    {
                    match('\"'); 
                    // MXMLLexer.g3:193:16: (~ '\\\"' )*
                    loop30:
                    do {
                        int alt30=2;
                        int LA30_0 = input.LA(1);

                        if ( ((LA30_0>='\u0000' && LA30_0<='!')||(LA30_0>='#' && LA30_0<='\uFFFF')) ) {
                            alt30=1;
                        }


                        switch (alt30) {
                    	case 1 :
                    	    // MXMLLexer.g3:193:17: ~ '\\\"'
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop30;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;
                case 2 :
                    // MXMLLexer.g3:194:11: '\\'' (~ '\\'' )* '\\''
                    {
                    match('\''); 
                    // MXMLLexer.g3:194:16: (~ '\\'' )*
                    loop31:
                    do {
                        int alt31=2;
                        int LA31_0 = input.LA(1);

                        if ( ((LA31_0>='\u0000' && LA31_0<='&')||(LA31_0>='(' && LA31_0<='\uFFFF')) ) {
                            alt31=1;
                        }


                        switch (alt31) {
                    	case 1 :
                    	    // MXMLLexer.g3:194:17: ~ '\\''
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "VALUE_int"

    // $ANTLR start "GENERIC_ID"
    public final void mGENERIC_ID() throws RecognitionException {
        try {
            Token id=null;

            // MXMLLexer.g3:199:2: (id= GENERIC_ID_int )
            // MXMLLexer.g3:200:2: id= GENERIC_ID_int
            {
            int idStart1411 = getCharIndex();
            mGENERIC_ID_int(); 
            id = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, idStart1411, getCharIndex()-1);
            addToken(id, GENERIC_ID, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "GENERIC_ID"

    // $ANTLR start "GENERIC_ID_int"
    public final void mGENERIC_ID_int() throws RecognitionException {
        try {
            // MXMLLexer.g3:204:5: ( ( LETTER | '_' | ':' ) ( options {greedy=true; } : LETTER | '0' .. '9' | '.' | '-' | '_' | ':' )* )
            // MXMLLexer.g3:204:7: ( LETTER | '_' | ':' ) ( options {greedy=true; } : LETTER | '0' .. '9' | '.' | '-' | '_' | ':' )*
            {
            if ( input.LA(1)==':'||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // MXMLLexer.g3:205:9: ( options {greedy=true; } : LETTER | '0' .. '9' | '.' | '-' | '_' | ':' )*
            loop33:
            do {
                int alt33=7;
                switch ( input.LA(1) ) {
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt33=1;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt33=2;
                    }
                    break;
                case '.':
                    {
                    alt33=3;
                    }
                    break;
                case '-':
                    {
                    alt33=4;
                    }
                    break;
                case '_':
                    {
                    alt33=5;
                    }
                    break;
                case ':':
                    {
                    alt33=6;
                    }
                    break;

                }

                switch (alt33) {
            	case 1 :
            	    // MXMLLexer.g3:205:36: LETTER
            	    {
            	    mLETTER(); 

            	    }
            	    break;
            	case 2 :
            	    // MXMLLexer.g3:205:45: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;
            	case 3 :
            	    // MXMLLexer.g3:205:56: '.'
            	    {
            	    match('.'); 

            	    }
            	    break;
            	case 4 :
            	    // MXMLLexer.g3:205:62: '-'
            	    {
            	    match('-'); 

            	    }
            	    break;
            	case 5 :
            	    // MXMLLexer.g3:205:68: '_'
            	    {
            	    match('_'); 

            	    }
            	    break;
            	case 6 :
            	    // MXMLLexer.g3:205:74: ':'
            	    {
            	    match(':'); 

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "GENERIC_ID_int"

    // $ANTLR start "LETTER"
    public final void mLETTER() throws RecognitionException {
        try {
            // MXMLLexer.g3:209:2: ( 'a' .. 'z' | 'A' .. 'Z' )
            // MXMLLexer.g3:
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            Token ws=null;

            // MXMLLexer.g3:213:14: ( (ws= OTHERWS | EOL )+ )
            // MXMLLexer.g3:214:9: (ws= OTHERWS | EOL )+
            {
            // MXMLLexer.g3:214:9: (ws= OTHERWS | EOL )+
            int cnt34=0;
            loop34:
            do {
                int alt34=3;
                int LA34_0 = input.LA(1);

                if ( (LA34_0=='\t'||LA34_0==' ') ) {
                    alt34=1;
                }
                else if ( (LA34_0=='\n'||LA34_0=='\r') ) {
                    alt34=2;
                }


                switch (alt34) {
            	case 1 :
            	    // MXMLLexer.g3:214:10: ws= OTHERWS
            	    {
            	    int wsStart1535 = getCharIndex();
            	    mOTHERWS(); 
            	    ws = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, wsStart1535, getCharIndex()-1);
            	    addToken(ws, WS, 0);

            	    }
            	    break;
            	case 2 :
            	    // MXMLLexer.g3:215:11: EOL
            	    {
            	    mEOL(); 

            	    }
            	    break;

            	default :
            	    if ( cnt34 >= 1 ) break loop34;
                        EarlyExitException eee =
                            new EarlyExitException(34, input);
                        throw eee;
                }
                cnt34++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "OTHERWS"
    public final void mOTHERWS() throws RecognitionException {
        try {
            // MXMLLexer.g3:226:17: ( ( ' ' | '\\t' ) )
            // MXMLLexer.g3:227:11: ( ' ' | '\\t' )
            {
            if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "OTHERWS"

    // $ANTLR start "EOL"
    public final void mEOL() throws RecognitionException {
        try {
            Token ws=null;

            // MXMLLexer.g3:230:14: (ws= EOL_HELPER )
            // MXMLLexer.g3:231:10: ws= EOL_HELPER
            {
            int wsStart1640 = getCharIndex();
            mEOL_HELPER(); 
            ws = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, wsStart1640, getCharIndex()-1);
            addToken(ws, EOL, 0);

            }

        }
        finally {
        }
    }
    // $ANTLR end "EOL"

    // $ANTLR start "EOL_HELPER"
    public final void mEOL_HELPER() throws RecognitionException {
        try {
            // MXMLLexer.g3:235:3: ( ( '\\n' | '\\r\\n' | '\\r' ) )
            // MXMLLexer.g3:235:5: ( '\\n' | '\\r\\n' | '\\r' )
            {
            // MXMLLexer.g3:235:5: ( '\\n' | '\\r\\n' | '\\r' )
            int alt35=3;
            int LA35_0 = input.LA(1);

            if ( (LA35_0=='\n') ) {
                alt35=1;
            }
            else if ( (LA35_0=='\r') ) {
                int LA35_2 = input.LA(2);

                if ( (LA35_2=='\n') ) {
                    alt35=2;
                }
                else {
                    alt35=3;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // MXMLLexer.g3:235:6: '\\n'
                    {
                    match('\n'); 

                    }
                    break;
                case 2 :
                    // MXMLLexer.g3:235:13: '\\r\\n'
                    {
                    match("\r\n"); 


                    }
                    break;
                case 3 :
                    // MXMLLexer.g3:235:22: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "EOL_HELPER"

    public void mTokens() throws RecognitionException {
        // MXMLLexer.g3:1:8: ( DOCUMENT )
        // MXMLLexer.g3:1:10: DOCUMENT
        {
        mDOCUMENT(); 

        }


    }


    protected DFA14 dfa14 = new DFA14(this);
    protected DFA13 dfa13 = new DFA13(this);
    static final String DFA14_eotS =
        "\67\uffff";
    static final String DFA14_eofS =
        "\67\uffff";
    static final String DFA14_minS =
        "\1\74\17\11\2\uffff\20\11\2\0\12\11\1\0\1\11\1\0\6\11";
    static final String DFA14_maxS =
        "\1\74\17\172\2\uffff\11\172\1\47\3\172\3\47\2\uffff\6\172\3\75"+
        "\1\47\1\uffff\1\172\1\uffff\1\172\1\75\4\172";
    static final String DFA14_acceptS =
        "\20\uffff\1\2\1\1\45\uffff";
    static final String DFA14_specialS =
        "\42\uffff\1\2\1\1\12\uffff\1\3\1\uffff\1\0\6\uffff}>";
    static final String[] DFA14_transitionS = {
            "\1\1",
            "\1\2\1\3\2\uffff\1\4\22\uffff\1\2\31\uffff\1\5\6\uffff\32"+
            "\5\4\uffff\1\5\1\uffff\32\5",
            "\1\2\1\3\2\uffff\1\4\22\uffff\1\2\31\uffff\1\5\6\uffff\32"+
            "\5\4\uffff\1\5\1\uffff\32\5",
            "\1\2\1\3\2\uffff\1\4\22\uffff\1\2\31\uffff\1\5\6\uffff\32"+
            "\5\4\uffff\1\5\1\uffff\32\5",
            "\1\2\1\6\2\uffff\1\4\22\uffff\1\2\31\uffff\1\5\6\uffff\32"+
            "\5\4\uffff\1\5\1\uffff\32\5",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\14\uffff\1\12\1\11\1"+
            "\20\12\10\1\14\3\uffff\1\21\2\uffff\32\7\4\uffff\1\13\1\uffff"+
            "\32\7",
            "\1\2\1\3\2\uffff\1\4\22\uffff\1\2\31\uffff\1\5\6\uffff\32"+
            "\5\4\uffff\1\5\1\uffff\32\5",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\14\uffff\1\12\1\11\1"+
            "\20\12\10\1\14\3\uffff\1\21\2\uffff\32\7\4\uffff\1\13\1\uffff"+
            "\32\7",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\14\uffff\1\12\1\11\1"+
            "\20\12\10\1\14\3\uffff\1\21\2\uffff\32\7\4\uffff\1\13\1\uffff"+
            "\32\7",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\14\uffff\1\12\1\11\1"+
            "\20\12\10\1\14\3\uffff\1\21\2\uffff\32\7\4\uffff\1\13\1\uffff"+
            "\32\7",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\15\1\35\2\uffff\1\17\22\uffff\1\15\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "",
            "",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\14\uffff\1\25\1\24\1"+
            "\20\12\23\1\27\2\uffff\1\33\1\21\2\uffff\32\22\4\uffff\1\26"+
            "\1\uffff\32\22",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\16\uffff\1\20\12\uffff"+
            "\1\34\2\uffff\1\33\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32"+
            "\34",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\16\uffff\1\20\12\uffff"+
            "\1\34\2\uffff\1\33\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32"+
            "\34",
            "\1\30\1\36\2\uffff\1\32\22\uffff\1\30\16\uffff\1\20\12\uffff"+
            "\1\34\2\uffff\1\33\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32"+
            "\34",
            "\1\37\1\40\2\uffff\1\41\22\uffff\1\37\1\uffff\1\42\4\uffff"+
            "\1\43",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\15\1\16\2\uffff\1\17\22\uffff\1\15\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\30\1\31\2\uffff\1\32\22\uffff\1\30\16\uffff\1\20\12\uffff"+
            "\1\34\2\uffff\1\33\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32"+
            "\34",
            "\1\37\1\40\2\uffff\1\41\22\uffff\1\37\1\uffff\1\42\4\uffff"+
            "\1\43",
            "\1\37\1\40\2\uffff\1\41\22\uffff\1\37\1\uffff\1\42\4\uffff"+
            "\1\43",
            "\1\37\1\55\2\uffff\1\41\22\uffff\1\37\1\uffff\1\42\4\uffff"+
            "\1\43",
            "\42\56\1\57\uffdd\56",
            "\47\60\1\61\uffd8\60",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\14\uffff\1\47\1\46\1"+
            "\uffff\12\45\1\51\2\uffff\1\33\3\uffff\32\44\4\uffff\1\50\1"+
            "\uffff\32\44",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\34\uffff\1\33",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\34\uffff\1\33",
            "\1\52\1\62\2\uffff\1\54\22\uffff\1\52\34\uffff\1\33",
            "\1\37\1\40\2\uffff\1\41\22\uffff\1\37\1\uffff\1\42\4\uffff"+
            "\1\43",
            "\42\56\1\57\uffdd\56",
            "\1\63\1\64\2\uffff\1\65\22\uffff\1\63\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\47\60\1\61\uffd8\60",
            "\1\63\1\64\2\uffff\1\65\22\uffff\1\63\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\52\1\53\2\uffff\1\54\22\uffff\1\52\34\uffff\1\33",
            "\1\63\1\64\2\uffff\1\65\22\uffff\1\63\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\63\1\64\2\uffff\1\65\22\uffff\1\63\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\63\1\66\2\uffff\1\65\22\uffff\1\63\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34",
            "\1\63\1\64\2\uffff\1\65\22\uffff\1\63\16\uffff\1\20\12\uffff"+
            "\1\34\3\uffff\1\21\2\uffff\32\34\4\uffff\1\34\1\uffff\32\34"
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "110:7: ( START_TAG ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )* END_TAG | EMPTY_ELEMENT )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_48 = input.LA(1);

                        s = -1;
                        if ( (LA14_48=='\'') ) {s = 49;}

                        else if ( ((LA14_48>='\u0000' && LA14_48<='&')||(LA14_48>='(' && LA14_48<='\uFFFF')) ) {s = 48;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA14_35 = input.LA(1);

                        s = -1;
                        if ( ((LA14_35>='\u0000' && LA14_35<='&')||(LA14_35>='(' && LA14_35<='\uFFFF')) ) {s = 48;}

                        else if ( (LA14_35=='\'') ) {s = 49;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA14_34 = input.LA(1);

                        s = -1;
                        if ( ((LA14_34>='\u0000' && LA14_34<='!')||(LA14_34>='#' && LA14_34<='\uFFFF')) ) {s = 46;}

                        else if ( (LA14_34=='\"') ) {s = 47;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA14_46 = input.LA(1);

                        s = -1;
                        if ( (LA14_46=='\"') ) {s = 47;}

                        else if ( ((LA14_46>='\u0000' && LA14_46<='!')||(LA14_46>='#' && LA14_46<='\uFFFF')) ) {s = 46;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA13_eotS =
        "\12\uffff";
    static final String DFA13_eofS =
        "\12\uffff";
    static final String DFA13_minS =
        "\1\0\1\11\3\uffff\1\55\4\uffff";
    static final String DFA13_maxS =
        "\1\uffff\1\172\3\uffff\1\133\4\uffff";
    static final String DFA13_acceptS =
        "\2\uffff\1\2\1\3\1\7\1\uffff\1\6\1\1\1\4\1\5";
    static final String DFA13_specialS =
        "\1\0\11\uffff}>";
    static final String[] DFA13_transitionS = {
            "\12\3\1\2\2\3\1\2\56\3\1\1\uffc3\3",
            "\2\7\2\uffff\1\7\22\uffff\1\7\1\5\15\uffff\1\4\12\uffff\1"+
            "\7\4\uffff\1\6\1\uffff\32\7\4\uffff\1\7\1\uffff\32\7",
            "",
            "",
            "",
            "\1\11\55\uffff\1\10",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "()* loopback of 111:13: ( ELEMENT | EOL | t= PCDATA | t= CDATA | t= COMMENT | pi= PI )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA13_0 = input.LA(1);

                        s = -1;
                        if ( (LA13_0=='<') ) {s = 1;}

                        else if ( (LA13_0=='\n'||LA13_0=='\r') ) {s = 2;}

                        else if ( ((LA13_0>='\u0000' && LA13_0<='\t')||(LA13_0>='\u000B' && LA13_0<='\f')||(LA13_0>='\u000E' && LA13_0<=';')||(LA13_0>='=' && LA13_0<='\uFFFF')) ) {s = 3;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 13, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}