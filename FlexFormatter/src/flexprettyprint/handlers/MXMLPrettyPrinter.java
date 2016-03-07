package flexprettyprint.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import actionscriptinfocollector.AntlrUtilities;
import actionscriptinfocollector.Utilities;
import flexasrearrangecodecommand.handlers.ASRearranger;
import flexprettyprint.preferences.AttrGroup;
import flexprettyprint.preferences.AttrOrderConfigDialog;
import flexprettyprint.preferences.Initializer;
import flexprettyprintcommand.Activator;

public class MXMLPrettyPrinter
{
	public static final int MXML_ATTR_ORDERING_NONE=0;
//	public static final int MXML_ATTR_ORDERING_SORT=1;
	public static final int MXML_ATTR_ORDERING_USEDATA=2;
	
	public static final int MXML_ATTR_WRAP_LINE_LENGTH=51;
	public static final int MXML_ATTR_WRAP_COUNT_PER_LINE = 52;
	public static final int MXML_ATTR_WRAP_NONE = 53;
	public static final int MXML_ATTR_WRAP_DEFAULT= 54; //for groups; same as main setting
	
	public static final int MXML_Sort_AscByCase=11;
	public static final int MXML_Sort_AscNoCase=12;
	public static final int MXML_Sort_GroupOrder=13;
	public static final int MXML_Sort_None=14;

	public static final String CDataEnd="]]>";
	public static final String CDataStart="<![CDATA[";
	
	public static final String StateRegexSuffix="\\..*";
	
	private int mAttrOrderMode=MXML_ATTR_ORDERING_NONE;
	private int mIndentAmount;
	private String mSource;
	private int mCurrentIndent;
	private int mMaxLineLength;
	private int mAttrsPerLine;
	private int mWrapMode;
	private int mWrapStyle;
	private int mHangingIndentSize;
	private boolean mUseSpacesInsideAttrBraces=false; //by default, don't affect the spacing around braces
	private int mSpacesInsideAttrBraces=0;
	private boolean mFormatBoundAttributes=false;
	private boolean mKeepRelativeCommentIndent=true;
	
	private IPreferenceStore mStore;
	
	private Map<Integer, ReplacementRange> mReplaceMap;

	private boolean mSkipNextIndent;
	private List<TagStackEntry> mTagStack;
	
	private Point mSelectedRange=null; //x=start line, y=end line (1-based)(lines 5 and 6 would be 5,6)
	private Point mOutputRange=null; //x=start offset, y=end offset (0-based offsets into outputbuffer)
	private Point mReplaceRange=null; //same semantics as mSelectedRange
	
	private boolean mIsPlainXML;
	
	private boolean mRearrangeOnly=false;
	
	private boolean mAllowMultiplePasses=true;
	private boolean mNeedAnotherPass=false;
	private int mLastCommentStart=(-1);

	private boolean mKeepCDataOnSameLine=false;
	private int mCDATAIndentTabs=0;
	private int mScriptIndentTabs=0;
	private int mSpacesBetweenSiblingTags=0;
	private int mSpacesAfterParentTags=0;
	private int mSpacesBeforeEmptyTagEnd=0;
	private boolean mKeepBlankLines=true;
	private boolean mDoFormat;
	private boolean mSortOtherAttrs=true;
	private boolean mAddNewlineAfterLastAttr=false;
	private boolean mIndentCloseTag=true;
	private List<String> mManualAttrSortOrder;
	private Map<String, AttrGroup> mAttrGroups;
	private int mSpacesAroundEquals=1;
	private boolean mUseTabs;
	private int mTabSize;
	private String mEnclosingTagName; //holds name of last open tag temporarily.  Use getEnclosingTag() for the correct data
	private Set<String> mTagsWhoseTextContentsCanBeFormatted;
	private Set<String> mTagsWhoseTextContentCanNeverBeFormatted;
	private Set<String> mTagsWithBlankLinesBeforeThem;
	private Set<String> mParentTagsWithBlankLinesAfterThem=new HashSet<String>();

	private boolean mUsePrivateTags; //use the list of tags to ignore internal formatting
	private List<String> mPrivateTags; //list of tags whose internal formatting shouldn't be touched
	
	private Set<String> mASScriptTags;
	private boolean mRequireCDATAForASContent;
	
	private int mBlankLinesAtCDataStart=(-1);
	private int mBlankLinesAtCDataEnd=(-1);
	private int mBlankLinesBeforeComments=0;
	private int mBlankLinesBeforeTags=0;
	private int mBlankLinesAfterParentTags=0;
	private int mBlankLinesBeforeCloseTags=0;
	private boolean mUseAttrsToKeepOnSameLine=false;
	private int mAttrsToKeepOnSameLine=4;
	private boolean mAlwaysObeyMaxLineLength=false;
//	private String mResumeFormattingTag=null;
	
	private StringBuffer mAddedText;
	private StringBuffer mRemovedText;
	
	private Map<String, Set<String>> mHashedGroupAttrs=new HashMap<String, Set<String>>();
	
	public boolean isUseTabs() {
		return mUseTabs;
	}

	public void setUseTabs(boolean useTabs) {
		mUseTabs = useTabs;
	}

	public int getTabSize() {
		return mTabSize;
	}

	public void setTabSize(int tabSize) {
		mTabSize = tabSize;
	}

	private List<Exception> mParseErrors;
	private ASPrettyPrinter mASPrinter;
	
	private void initialize()
	{
		mASPrinter=new ASPrettyPrinter(false, "");
		mManualAttrSortOrder=new ArrayList<String>();
		mIndentAmount=4;
		mDoFormat=true;
		mWrapStyle=WrapOptions.WRAP_STYLE_INDENT_NORMAL;
		mTagsWhoseTextContentsCanBeFormatted=new HashSet<String>();
		mTagsWhoseTextContentsCanBeFormatted.add("mx:List");
		mTagsWhoseTextContentsCanBeFormatted.add("fx:List");
		mTagsWhoseTextContentCanNeverBeFormatted=new HashSet<String>();
		mTagsWhoseTextContentCanNeverBeFormatted.add("mx:String");
		mTagsWhoseTextContentCanNeverBeFormatted.add("fx:String");
		mTagsWithBlankLinesBeforeThem=new HashSet<String>();
		mASScriptTags=new HashSet<String>();
		mASScriptTags.add(".*:Script");
		mASScriptTags.add("fx:Script");
		mASScriptTags.add("mx:Script");
		mRequireCDATAForASContent=false;
		mIsPlainXML=false;
	}
	
	public MXMLPrettyPrinter(File f, String charsetName) throws IOException
	{
		initialize();
		StringBuffer buffer=new StringBuffer();
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f), charsetName));
		try
		{
			while (true)
			{
				String line=br.readLine();
				if (line==null)
					break;
				buffer.append(line);
				buffer.append('\n');
			}
		}
		finally
		{
			br.close();
		}
		mSource=buffer.toString();
	}
	
	public MXMLPrettyPrinter(String sourceData)
	{
		initialize();
		mSource=sourceData;
	}
	
	public String print(int startIndent) throws RecognitionException, Exception
	{
		if (mSource.indexOf(ASPrettyPrinter.mIgnoreFileProcessing)>=0)
		{
			mParseErrors=new ArrayList<Exception>();
			mParseErrors.add(new Exception("File ignored: Ignore tag exists in file==> "+ASPrettyPrinter.mIgnoreFileProcessing));
			return null;
		}

		//Note: no need to loop here at present, because the mxml stuff never requires more than one pass
		mTagStack=new ArrayList<TagStackEntry>();
		mSkipNextIndent=false;
		mNeedAnotherPass=false;
		mAddedText=new StringBuffer();
		mRemovedText=new StringBuffer();
		mSource=Utilities.convertCarriageReturnsToLineFeeds(mSource);
		mCurrentIndent=startIndent;
		mReplaceMap=null;
		mLastCommentStart=(-1);
		mReplaceRange=null;
		mOutputRange=null;
		MXMLLexer lex = new MXMLLexer(new ANTLRStringStream(mSource));
		StringBuffer buffer=new StringBuffer();
		lex.mDOCUMENT();
		prettyPrint(lex.getTokens(), buffer);
		if (!(ActionScriptFormatter.validateNonWhitespaceCharCounts(buffer.toString()+mRemovedText.toString(), mSource+mAddedText.toString())))
		{
			if (mParseErrors==null)
				mParseErrors=new ArrayList<Exception>();
			mParseErrors.add(new Exception("Internal error: Formatted text doesn't match source. "+buffer.toString()+"!="+mSource));
			return null;
		}
		return buffer.toString();
	}
	
	private TagStackEntry getCurrentTag()
	{
		if (mTagStack.size()==0)
			return null;
		
		return mTagStack.get(mTagStack.size()-1);
	}
	
	private String getEnclosingTag()
	{
		if (mTagStack.size()==0)
			return "";
		
		return mTagStack.get(mTagStack.size()-1).getTagName();
	}
	
	private int getPCDataTokens(List<CommonToken> tokens, int tokenIndex, List<Token> pcDataTokens, boolean[] foundNonWhitespace)
	{
		foundNonWhitespace[0]=false;
		int newTokenIndex=tokenIndex+1;
		while (true)
		{
			if (newTokenIndex>=tokens.size())
				break;
			
			Token token=tokens.get(newTokenIndex);
			if (token.getType()==MXMLLexer.PCDATA || token.getType()==MXMLLexer.EOL || token.getType()==MXMLLexer.WS)
			{
				pcDataTokens.add(token);
				if (AntlrUtilities.asTrim(token.getText()).length()>0)
					foundNonWhitespace[0]=true;
			}
			else
			{
				//not a content type we are handling
				break;
			}
			
			newTokenIndex++;
		}
		
		newTokenIndex--; //back up 1 since the main token loop will increment
		return newTokenIndex;
	}
	
	private int processPostTagText(List<CommonToken> tokens, int tokenIndex, StringBuffer buffer, int linesToInsert) throws Exception
	{
		//if this is an actionscript tag, then we need to grab all of the next contents and 
		//run it through the parser, if there is not a CData tag
		if (!isPlainXML() && isASFormattingTag(getEnclosingTag()))
		{
			boolean[] hasChars=new boolean[1];
			List<Token> pcDataTokens=new ArrayList<Token>();
			int newTokenIndex=getPCDataTokens(tokens, tokenIndex, pcDataTokens, hasChars);
			if (pcDataTokens.size()>0)
			{
				Token tempToken=new CommonToken(pcDataTokens.get(0));
				StringBuffer tokenText=new StringBuffer();
				for (Token token : pcDataTokens) {
					tokenText.append(token.getText());
				}
				String tokenString=tokenText.toString();
				if (AntlrUtilities.asTrim(tokenString).length()>0)
				{
					tempToken.setText(tokenString);
					boolean success=processActionScriptBlock(tempToken, buffer);
					if (success)
						return newTokenIndex;
				}
			}
		}
		
		boolean foundNonWhitespace=false;
		
		//if this is not a tag whose contents can be formatted, then we need to peek at the 
		//subsequent pcdata/whitespace tokens to see if there are any non-whitespace contents
		if (!mTagsWhoseTextContentsCanBeFormatted.contains(getEnclosingTag()))
		{
			boolean[] hasChars=new boolean[1];
			List<Token> pcDataTokens=new ArrayList<Token>();
			int newTokenIndex=getPCDataTokens(tokens, tokenIndex, pcDataTokens, hasChars);
			foundNonWhitespace=hasChars[0];
			
			//if we found characters, indicating that we must preserve the current formatting exactly,
			//or if this is a tag where even whitespace is significant, then output the tokens as 
			//is.
			if (foundNonWhitespace || mTagsWhoseTextContentCanNeverBeFormatted.contains(getEnclosingTag()))
			{
				for (Token pcdataToken : pcDataTokens)
				{
					//convert to \n delimiters to match the rest of my output
					String data=pcdataToken.getText().replace("\r\n", "\n");
					data=data.replace("\r", "\n");
					buffer.append(data);
				}
				mSkipNextIndent=true;
				return newTokenIndex;
			}
		}
		
		//we will drop through to here if 
		//1. this is a tag that explicitly allows indenting of text content, or
		//2. there is only whitespace in the text content, and this tag is not explicitly barred from formatting
		for (int i=0;i<linesToInsert;i++)
		{
			insertCR(buffer, false);
		}
		
		return tokenIndex;
	}
	
	private void prettyPrint(List<CommonToken> tokens, StringBuffer buffer) throws RecognitionException, Exception
	{
		for (int tokenIndex=0;tokenIndex<tokens.size();tokenIndex++)
		{
			Token token=tokens.get(tokenIndex);
			System.out.println(token.getText()+":"+token.getType());
			switch (token.getType())
			{
				case MXMLLexer.COMMENT:
					mLastCommentStart=buffer.length();
					if (mRearrangeOnly)
					{
						buffer.append(token.getText());
					}
					else
					{
						updatePartialFormattingBoundaries(tokens.get(tokenIndex), tokens.get(tokenIndex), buffer);
						String[] commentLines=token.getText().split("\n");
						
						//add extra blank lines here if the comment starts on a new line.  First, count existing
						//blank lines.
						if (mDoFormat && ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
						{
							int currentBlankLines=ActionScriptFormatter.getNumberOfEmptyLinesAtEnd(buffer);
							for (int i=currentBlankLines; i<getBlankLinesBeforeComments();i ++)
							{
								buffer.append('\n');
							}
						}
						
						//reset this value because we might have added some additional blank lines
						mLastCommentStart=buffer.length();

						int originalIndent=0;
						if (isKeepRelativeCommentIndent())
						{
							//find original indent
							StringBuffer lineData=new StringBuffer();
							int currentLine=token.getLine();
							Token aToken=token;
							int tempTokenIndex=tokenIndex;
							while (aToken.getLine()==currentLine)
							{
								lineData.insert(0, aToken.getText());
								if (tempTokenIndex<=0)
									break;
								tempTokenIndex--;
								aToken=tokens.get(tempTokenIndex);
							}
							originalIndent=ASPrettyPrinter.findIndent(lineData.toString(), getTabSize());
						}
						for (int j=0;j<commentLines.length;j++)
						{
							boolean onLastLine=(j==commentLines.length-1);
							int indentAmount=mCurrentIndent;
							String data=AntlrUtilities.asTrim(commentLines[j]);
							if (j>0)
							{
								if (isKeepRelativeCommentIndent() && originalIndent>=0)
								{
									int existingIndent=ASPrettyPrinter.findIndent(commentLines[j], getTabSize());
									indentAmount=Math.max(0, mCurrentIndent+(existingIndent-originalIndent));
								}
								else
								{
									//on a middle line, indent the text to the right of the <!-- .
									if (!onLastLine || !data.startsWith("-->"))
										indentAmount+=5;
								}
							}
							
							//only add indent if on an empty line
							if (data.length()>0)
							{
								if (ActionScriptFormatter.isLineEmpty(buffer))
								{
									buffer.append(generateIndent(indentAmount));
								}
								buffer.append(data);
							}
							if (!onLastLine)
								buffer.append('\n');
						}
					}
					
					tokenIndex=processPostTagText(tokens, tokenIndex, buffer, 1);
//					insertCR(buffer, false);
					break;
				case MXMLLexer.DECL_START:
					tokenIndex=printTag(tokens, tokenIndex, buffer, MXMLLexer.DECL_STOP);
					insertCR(buffer, false);
					break;
				case MXMLLexer.TAG_OPEN:
					tokenIndex=printTag(tokens, tokenIndex, buffer, MXMLLexer.TAG_CLOSE);
					if (mEnclosingTagName!=null)
						mTagStack.add(new TagStackEntry(mEnclosingTagName));
					mCurrentIndent+=mIndentAmount;
					if (isPrivateTag(mEnclosingTagName))
					{
						tokenIndex++;
						//walk and output item until I see the matching close tag.  Note: one side-effect of this 
						//code is that unmatched tags inside this tag are legal.  
						while (tokenIndex<tokens.size())
						{
							Token testToken=tokens.get(tokenIndex);
							//see if this token is then end tag that matches our start tag
							if (testToken.getType()==MXMLLexer.END_TAG_OPEN)
							{
								Token nextNonWS=getNextNonWSToken(tokens, tokenIndex+1);
								if (nextNonWS!=null && nextNonWS.getText().equals(mEnclosingTagName))
								{
									tokenIndex--;
									break;
								}
							}
							//otherwise emit the tag data and continue
							buffer.append(testToken.getText());
							tokenIndex++;
						}
					}
					else
					{
						tokenIndex=processPostTagText(tokens, tokenIndex, buffer, 1);
					}
						//insertCR(buffer, false);
					break;
				case MXMLLexer.END_TAG_OPEN:
					mCurrentIndent-=mIndentAmount;
					tokenIndex=printTag(tokens, tokenIndex, buffer, MXMLLexer.TAG_CLOSE);
					if (mTagStack.size()>0)
						mTagStack.remove(mTagStack.size()-1);
					tokenIndex=processPostTagText(tokens, tokenIndex, buffer, 1);
//					insertCR(buffer, false);
					break;
				case MXMLLexer.EMPTY_TAG_OPEN:
					tokenIndex=printTag(tokens, tokenIndex, buffer, MXMLLexer.EMPTYTAG_CLOSE);
					tokenIndex=processPostTagText(tokens, tokenIndex, buffer, 1);
//					insertCR(buffer, false);
					break;
				case MXMLLexer.CDATA:
						if (AntlrUtilities.asTrim(token.getText()).length()>0)
							mLastCommentStart=(-1);
						updatePartialFormattingBoundaries(tokens.get(tokenIndex), tokens.get(tokenIndex), buffer);
						//if no enclosing tag, or there is one but it's not one that contains scripts, then just do regular intra-tag data processing
						if (isPlainXML() || !isASFormattingTag(getEnclosingTag())) //mEnclosingTagName.endsWith(":Script"))
						{
							processPCData(token, buffer);
							insertCR(buffer, false);
							break;
						}
						
						//handle script
						boolean succeeded=processActionScriptBlock(token, buffer);
						if (succeeded)
							break;

						buffer.append(token.getText());
						break;
				case MXMLLexer.PCDATA:
					if (AntlrUtilities.asTrim(token.getText()).length()>0)
						mLastCommentStart=(-1);
					updatePartialFormattingBoundaries(tokens.get(tokenIndex), tokens.get(tokenIndex), buffer);
					processPCData(token, buffer);
					break;
				case MXMLLexer.EOL:
					if (!mDoFormat)
					{
						insertCR(buffer, true);
					}
					else if (isKeepBlankLines())
					{
						//special handling to grab subsequent newlines and determine the proper number of
						//blank lines.  Alg: walk next tokens until I hit EOF or a non-EOL, non-WS token.
						//Insert blank lines on each found EOL.
						tokenIndex++;
						BlankLineLoop: while (tokenIndex<tokens.size())
						{
							token=tokens.get(tokenIndex);
							switch (token.getType())
							{
								case MXMLLexer.EOL:
									insertCR(buffer, true);
									tokenIndex++;
									break;
								case MXMLLexer.WS:
									tokenIndex++;
									//do nothing
									break;
								case MXMLLexer.PCDATA:
									String nonWS=AntlrUtilities.asTrim(token.getText());
									if (nonWS.length()==0)
									{
										tokenIndex++;
										break;
									}
									//otherwise drop through to default case
								default:
									//non-whitespace, need to kick out
									tokenIndex--; //revert back to previous token, whatever it was
									break BlankLineLoop; //kick out of loop
							}
						}
						
						//if we kick out because we're at the end of the token stream, we should drop out of the "for" loop correctly
					}
					break;
				case MXMLLexer.WS:
					if (mRearrangeOnly)
					{
						buffer.append(token.getText());
						break;
					}

					if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
					{
						buffer.append(token.getText());
					}
					break;
				default:
					updatePartialFormattingBoundaries(tokens.get(tokenIndex), tokens.get(tokenIndex), buffer);
					buffer.append(token.getText());
			}
		}
		
		if (mOutputRange!=null && mOutputRange.y<0 && mReplaceRange!=null)
		{
			mOutputRange.y=buffer.length();
			mReplaceRange.y=mSource.length();
		}
		
		if (mOutputRange!=null)
		{
			//set the output range to start at the previous line start
			String bufferString=buffer.toString();
			if (bufferString.charAt(mOutputRange.x)=='\n')
				mOutputRange.x++;
			int lastCR=buffer.lastIndexOf("\n", mOutputRange.x);
			if (lastCR>=0)
				mOutputRange.x=lastCR+1;
			else
				mOutputRange.x=0;

			//move outputRange.y to contain the next CR, if we are right on it
			if (mOutputRange.y<bufferString.length() && bufferString.charAt(mOutputRange.y)=='\n')
				mOutputRange.y++;
			
			//now, find the previous CR, which we will *not* include in the range
			int nextCR=buffer.lastIndexOf("\n", mOutputRange.y);
			if (nextCR>=0)
				mOutputRange.y=nextCR; //don't need to actually include the CR character
		}
		
		if (mReplaceRange!=null)
		{
			//set the replace range to start at the previous line start
			//if we're already at the end of line, then move forward past the end of line
			if (mSource.charAt(mReplaceRange.x)=='\n')
				mReplaceRange.x++;
			else if (isCarriageReturnPair(mSource, mReplaceRange.x))
				mReplaceRange.x+=2;
			int lastCR=mSource.lastIndexOf('\n', mReplaceRange.x);
			if (lastCR>=0)
				mReplaceRange.x=lastCR+1;
			else
				mReplaceRange.x=0;

			//again, move past the end of line, if we're on it
			if (mReplaceRange.y<mSource.length() && mSource.charAt(mReplaceRange.y)=='\n')
				mReplaceRange.y++;
			else if (isCarriageReturnPair(mSource, mReplaceRange.y))
				mReplaceRange.y+=2;

			//now, go backwards to the previous EOL
			int nextCR=mSource.lastIndexOf('\n', mReplaceRange.y);
			if (nextCR>=0 && nextCR>mReplaceRange.x)
			{
				mReplaceRange.y=nextCR; //don't need to actually include the CR
				if (nextCR>0 && isCarriageReturnPair(mSource, mReplaceRange.y-1)) //mSource.charAt(mReplaceRange.y)=='\n' && mSource.charAt(mReplaceRange.y-1)=='\r')
					mReplaceRange.y--; //need to move before CR pair, if it's a pair
			}
		}
		
	}
	
	private CommonToken getNextNonWSToken(List<CommonToken> tokens, int tokenIndex)
	{
		while (tokenIndex<tokens.size())
		{
			CommonToken token=tokens.get(tokenIndex);
			if (AntlrUtilities.asTrim(token.getText()).length()>0)
				return token;
			tokenIndex++;
		}
		return null;
	}

	private boolean isPrivateTag(String tag)
	{
		if (!mUsePrivateTags)
			return false;
		
		//process list using regex to compare each private tag with endTag
		for (String privateTag : mPrivateTags)
		{
			if (Pattern.matches(privateTag, tag))
				return true;
		}
		
		return false;
	}

	private boolean processActionScriptBlock(Token token, StringBuffer buffer) throws Exception
	{
		int methodSavedIndent=mCurrentIndent;
		try
		{
			int startIndex=token.getText().indexOf(CDataStart);
			int endIndex=token.getText().lastIndexOf(CDataEnd);

			if (mKeepCDataOnSameLine)
			{
				//get rid of carriage return
				ActionScriptFormatter.trimAllWhitespaceOnEndOfBuffer(buffer);
				mCurrentIndent-=mIndentAmount;
			}
			else
			{
				if (startIndex>=0)
					mCurrentIndent+=((mCDATAIndentTabs-1)*getTabSize());  //-1 because we had already adjusted the indent in the caller method
				else
					mCurrentIndent-=mIndentAmount;
			}

			//if we found the cdata start and end or if we don't require those
			if (!isRequireCDATAForASContent() || (startIndex>=0 && endIndex>=0))
			{
				int cdataOffset=0;
				int preTextCRCount=0;
				String text=token.getText();
				if (endIndex>=0)
					text=text.substring(0, endIndex);
				if (startIndex>=0)
				{
					text=text.substring(startIndex+CDataStart.length());
					cdataOffset=CDataStart.length();
				}
				for (int k=0;k<text.length();k++)
				{
					char c=text.charAt(k);
					if (!AntlrUtilities.isASWhitespace(c))
						break;
					if (c=='\n' || c=='\r')
					{
						if (isCarriageReturnPair(text, k))
							k++; //skip an extra character
						preTextCRCount++;
					}
				}

				mASPrinter.setSelectedRange(null);

				//if we are attempting a partial format and we haven't already captured the boundaries
				boolean includesEndOfActionScript=false;
				int lineCount=0;
				if (mDoFormat && mSelectedRange!=null && (mOutputRange==null || mOutputRange.y<0))
				{
					//								String[] lines=splitTextOnLineBreaks(text);
					lineCount=countLines(text);
					int startLine=token.getLine();
					int endLine=startLine+lineCount-1; //lines.length-1;
					if (mOutputRange==null)
					{
						//if the selected lines start inside the script block
						if (mSelectedRange.x>=startLine && mSelectedRange.x<=endLine)
						{
							//now, determine whether the selection end is also included and determine the
							//appropriate selection setting for the actionscript printer selection range

							if (mSelectedRange.y<endLine) //ends during block (< so that it doesn't include last line of block)
							{
								mASPrinter.setSelectedRange(new Point(mSelectedRange.x-startLine+1, mSelectedRange.y-startLine+1 ));
								includesEndOfActionScript=false;
							}
							else
							{
								//doesn't end during block, so go until end of block 
								mASPrinter.setSelectedRange(new Point(mSelectedRange.x-startLine+1, lineCount)); //lines.length));
								includesEndOfActionScript=true;
							}
						}
					}
					else
					{
						//we are in the middle of the selected area.  Does the selected area end during
						//the actionscript block, or does it cover the entire block.
						if (mSelectedRange.y<=endLine) //ends during block
						{
							mASPrinter.setSelectedRange(new Point(1, mSelectedRange.y-startLine+1));
							includesEndOfActionScript=false;
						}
					}
				}

				//Handle rearranging.  Only do this if we are in format mode and we are performing the change on the entire document
				boolean markBlockAsValidated=false;
				String addedText="";
				String removedText="";
				boolean changesMade=false;
				if (mRearrangeOnly || (mDoFormat && mASPrinter.getSelectedRange()==null && (mStore!=null && mStore.getBoolean(Initializer.Pref_AS_RearrangeAsPartOfFormat))))
				{
					ASRearranger rearranger=new ASRearranger(mStore);
					IDocument doc=new Document(text);
					boolean success=rearranger.rearrangeCode(doc, new ArrayList<MarkerAnnotation>(), true);
					if (!success)
					{
						System.out.println("Failed to rearrange: "+text);
						if (!rearranger.isSoftFailure())
							return false;
					}
					changesMade=rearranger.hasChanges();
					mAddedText.append(rearranger.getAddedText());
					mRemovedText.append(rearranger.getRemovedText());
					addedText=rearranger.getAddedText();
					removedText=rearranger.getRemovedText();
					text=doc.get();
					markBlockAsValidated=true;
				}

				String trimmedResult=AntlrUtilities.asTrim(text);
				int oldLength=0;
				int leadingWhitespaceCount=0;
				int codeStartIndent=mCurrentIndent;
				//			if (startIndex>=0)
				codeStartIndent+=mIndentAmount*mScriptIndentTabs;
				String resultData=trimmedResult;
				if (!mRearrangeOnly)
				{
					mASPrinter.setDoFormat(mDoFormat);
					mASPrinter.setData(text);
					resultData=mASPrinter.print(codeStartIndent);
					if (resultData==null)
					{
						mParseErrors=mASPrinter.getParseErrors();
						if (mParseErrors!=null)
						{
							//translate exception positions to main document
							for (Exception ex : mParseErrors) {
								if (ex instanceof RecognitionException)
								{
									RecognitionException rex=(RecognitionException)ex;
									Token t=rex.token;
									int offset=token.getLine()-1;
									if (t!=null)
									{
										t.setLine(t.getLine()+offset);
									}
									rex.line+=offset;
								}
							}
						}
						throw new Exception();
					}

					//otherwise, capture the extra characters that might have been added by the format
					mAddedText.append(mASPrinter.getAddedText());
					mRemovedText.append(mASPrinter.getRemovedText());
					addedText+=mASPrinter.getAddedText();
					removedText+=mASPrinter.getRemovedText();
					if (mDoFormat)
					{
						mNeedAnotherPass|=mASPrinter.needAnotherPass(); //there might be multiple script blocks
					}
					if (!markBlockAsValidated && (mASPrinter.getAddedText().length()>0 || mASPrinter.getRemovedText().length()>0))
					{
						markBlockAsValidated=true; //too complicated to determine the exact character positions, at least in the original doc

						//TODO: Need to take into account the leading ws trimming and the correct buffer length
						//grab replace maps too, and update positions if necessary. Whitespace also might be 
						//adjusted at the start of the block.  So this code needs to be done where the main
						//replace block code is done, AND more adjustments need to be made.
						//					Map<Integer, ReplacementRange> asRanges=mASPrinter.getReplaceMap();
						//					if (asRanges!=null)
						//					{
						//						if (mReplaceMap==null)
						//							mReplaceMap=new HashMap<Integer, ReplacementRange>();
						//						CommonToken ct=(CommonToken)token;
						//						for (Map.Entry<Integer, ReplacementRange> entry : asRanges.entrySet()) {
						//							entry.getValue().mRangeInOriginalDoc.x+=ct.getStartIndex();
						//							entry.getValue().mRangeInOriginalDoc.y+=ct.getStartIndex();
						//							entry.getValue().mRangeInFormattedDoc.x+=buffer.length();
						//							entry.getValue().mRangeInFormattedDoc.y+=buffer.length();
						//							mReplaceMap.put(entry.getKey()+ct.getStartIndex(), entry.getValue());
						//						}
						//					}
					}				
				}

				if (startIndex>=0)
				{
					addIndentIfAtStartOfLine(buffer, true);
					buffer.append(CDataStart);
				}
				if (mDoFormat)
				{
					insertCR(buffer, false);
					for (int i=0;i<mBlankLinesAtCDataStart;i++)
						insertCR(buffer, false);
				}
				else
				{
					for (int k=0;k<preTextCRCount;k++)
					{
						insertCR(buffer, true);
					}
				}
				int saveIndent=mCurrentIndent;
				//			if (startIndex>=0)
				mCurrentIndent=codeStartIndent; //+=mIndentAmount;
				addIndentIfAtStartOfLine(buffer, true);
				mCurrentIndent=saveIndent;

				//TODO: change to determine the amount of leading whitespace first and capture
				//the previous buffer length
				int leadingCRCount=0;
				for (;leadingWhitespaceCount<resultData.length();leadingWhitespaceCount++)
				{
					char c=resultData.charAt(leadingWhitespaceCount);
					if (!AntlrUtilities.isASWhitespace(c))
					{
						break;
					}
					if (c=='\n')
						leadingCRCount++;
				}

				oldLength=buffer.length();
				trimmedResult=AntlrUtilities.asTrim(resultData);

				if (markBlockAsValidated && (AntlrUtilities.asTrim(addedText).length()>0 || AntlrUtilities.asTrim(removedText).length()>0 || changesMade))
				{
					if (mReplaceMap==null)
						mReplaceMap=new HashMap<Integer, ReplacementRange>();
					int replacementStartIndex=((CommonToken)token).getStartIndex();
					if (startIndex>=0)
						replacementStartIndex+=(startIndex+CDataStart.length());
					int replacementEndIndex=((CommonToken)token).getStartIndex();
					if (endIndex>=0)
						replacementEndIndex+=endIndex;
					else
						replacementEndIndex+=((CommonToken)token).getText().length();
					ReplacementRange range=new ReplacementRange(new Point(buffer.length(), buffer.length()+trimmedResult.length()), new Point(replacementStartIndex, replacementEndIndex));
					range.setChangedText(addedText, removedText);
					mReplaceMap.put(buffer.length(), range);
				}

				buffer.append(trimmedResult);

				//now, patch up the partial format return boundaries if necessary
				if (mASPrinter.getSelectedRange()!=null)
				{
					Point outputRange=mASPrinter.getOutputRange();
					Point replaceRange=mASPrinter.getReplaceRange();
					if (outputRange!=null && replaceRange!=null)
					{
						if (mOutputRange==null)
						{
							mOutputRange=new Point(0, -1);
							mReplaceRange=new Point(0, -1);

							//establish the beginning boundaries
							mOutputRange.x=oldLength+outputRange.x-leadingWhitespaceCount;
							//mReplaceRange.x=token.getLine()+replaceLines.x-1; //adjust for leading CRs and existing whitespace
							mReplaceRange.x=((CommonToken)token).getStartIndex()+cdataOffset+replaceRange.x; //-leadingWhitespaceCount;

							//selected range starts, possibly also ends inside actionscript block
							if (includesEndOfActionScript)
							{
								//goes to end of actionscript; don't need to do anything here
							}
							else
							{
								//if it stops part way through the block; we need to finish output/replace boundaries here
								mOutputRange.y=oldLength+Math.min(trimmedResult.length(), outputRange.y-leadingWhitespaceCount); //outputRange.y-leadingWhitespaceCount;//-trailingWhitespaceCount;
								//											mReplaceRange.y=token.getLine()+replaceLines.y-1; //adjust for leading CRs and existing whitespace
								mReplaceRange.y=((CommonToken)token).getStartIndex()+cdataOffset+replaceRange.y;
							}
						}
						else
						{
							//it stops part way through the block; we need to finish output/replace boundaries here
							mOutputRange.y=oldLength+Math.min(trimmedResult.length(), outputRange.y-leadingWhitespaceCount);
							//										mReplaceRange.y=token.getLine()+replaceLines.y-1; //adjust for leading CRs and existing whitespace
							mReplaceRange.y=((CommonToken)token).getStartIndex()+cdataOffset+replaceRange.y; //-leadingWhitespaceCount;
						}
					}
				}

				insertCR(buffer, false);

				if (mDoFormat)
				{
					for (int i=0;i<mBlankLinesAtCDataEnd;i++)
					{
						insertCR(buffer, false);
					}
				}
				
				addIndentIfAtStartOfLine(buffer, true);
				if (!mDoFormat)
				{
					for (int k=text.length()-1;k>=0;k--)
					{
						char c=text.charAt(k);
						if (!AntlrUtilities.isASWhitespace(c))
							break;
						if (c=='\n')
						{
							insertCR(buffer, true);
						}
					}
					addIndentIfAtStartOfLine(buffer, true);
				}

				//update the formatting boundary to catch the case where
				//the start of the selection only catches the end of the code block.
				if (mDoFormat && mSelectedRange!=null && mOutputRange==null)
				{
					if (token.getLine()+lineCount-1>=mSelectedRange.x)
					{
						mOutputRange=new Point(buffer.length(), -1);
						mReplaceRange=new Point(0, -1);
						mReplaceRange.x=((CommonToken)token).getStartIndex()+endIndex;
					}
				}
				if (endIndex>=0)
				{
					buffer.append(CDataEnd);
					if (mDoFormat && !isKeepCDataOnSameLine())
						insertCR(buffer, false);
				}
				else
				{
					//if we're not sticking the end CDATA tag on, then just delete the whitespace at the end of the line
					ActionScriptFormatter.trimWhitespaceOnEndOfBuffer(buffer);
				}

				return true;
			}

			return false;
		}
		finally
		{
			mCurrentIndent=methodSavedIndent;
		}
	}

	private boolean isASFormattingTag(String enclosingTagName)
	{
		if (mASScriptTags.contains(enclosingTagName))
			return true;
		
		for (String scriptTag : mASScriptTags)
		{
			if (Pattern.matches(scriptTag, enclosingTagName))
				return true;
		}
		
		return false;
	}

	private int countLines(String text)
	{
		int count=1;
		for (int i=0;i<text.length();i++)
		{
			char c=text.charAt(i);
			
			if (c=='\r')
			{
				if (isCarriageReturnPair(text, i))
					i++;
				count++;
			}
			else if (c=='\n')
				count++;
		
		}
		return count;
	}

	private boolean isCarriageReturnPair(String source, int loc)
	{
		if (loc+1<source.length())
		{
			if (source.charAt(loc)=='\r' && source.charAt(loc+1)=='\n')
				return true;
		}
		
		return false;
	}

	private void processPCData(Token token, StringBuffer buffer)
	{
		if (mRearrangeOnly)
		{
			buffer.append(token.getText());
			return;
		}
		
		//if we are inside a CData section and the enclosing tag is not explicitly set to allow formatting,
		//then we just spit out the data.  I'm going to ignore the case of an empty CData section.
		if (!mTagsWhoseTextContentsCanBeFormatted.contains(getEnclosingTag()) && AntlrUtilities.asTrim(token.getText()).startsWith(CDataStart))
		{
			//put start of tag on new line and indent it
			if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
				insertCR(buffer, false);
			addIndentIfAtStartOfLine(buffer, false);
			
			//spit out the text
			buffer.append(token.getText().replace("\r\n", "\n").replace('\r', '\n'));
			
			//TODO: add a carriage return here if we can determine the correct behavior based on format/indent mode
			//and "keep blank lines"
			return;
		}
		
		if (mDoFormat)
		{
			String[] lines=AntlrUtilities.asTrim(token.getText()).split("\n");
			if (lines.length==0 && token.getText().equals("\n"))
				lines=new String[]{"", ""};
			//if all whitespace but no carriage returns, then we don't want to go through the loop
			else if (token.getText().indexOf('\n')<0 && AntlrUtilities.asTrim(token.getText()).length()==0) 
				lines=new String[]{};
			for (int k=0;k<lines.length;k++)
			{
				String lineData=lines[k];
				if (mDoFormat)
				{
					if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
						insertCR(buffer, false);
					lineData=AntlrUtilities.asTrim(lineData);
					if (lineData.length()==0)
						continue;
					addIndentIfAtStartOfLine(buffer, false);
					buffer.append(lineData);
					insertCR(buffer, false);
				}
			}
		}
		else
		{
			String text=token.getText();
			boolean beforeTextOnLine=ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer);
			for (int k=0;k<text.length();k++)
			{
				char c=text.charAt(k);
				if (AntlrUtilities.isASWhitespace(c))
				{
					if (c=='\n' || c=='\r')
					{
						if (isCarriageReturnPair(text, k))
							k++; //advanced extra position to account for \r\n
						buffer.append('\n');
						beforeTextOnLine=true;
					}
					else if (!beforeTextOnLine)
					{
						buffer.append(c);
					}
				}
				else
				{
					addIndentIfAtStartOfLine(buffer, false);
					beforeTextOnLine=false;
					buffer.append(c);
				}
			}
		}
	}
	

	private void insertCR(StringBuffer buffer, boolean override)
	{
		if (mDoFormat || override)
		{
			buffer.append('\n');
		}
	}
	
	public static class Attr implements Comparable<Attr>
	{
		public String mName;
		public String mValue;
		public Attr()
		{
			mName="";
			mValue="";
		}
		public int compareTo(Attr other)
		{
			return (mName.compareTo(other.mName));
		}
		
		@Override
		public int hashCode()
		{
			return mName.hashCode();
		}
	}
	
	private void movePartialFormattingBoundaries(int position, int count)
	{
		if (!mDoFormat)
			return;
		
		if (mSelectedRange!=null)
		{
			if (mOutputRange!=null)
			{
				if (mOutputRange.x>=position)
					mOutputRange.x+=count;
				if (mOutputRange.y>position)
					mOutputRange.y+=count;
			}
		}
	}
	
	private void updatePartialFormattingBoundaries(Token startToken, Token endToken, StringBuffer buffer)
	{
		if (!mDoFormat)
			return;
		
		if (mSelectedRange!=null)
		{
			if (mOutputRange==null)
			{
				if (endToken.getLine()>=mSelectedRange.x)
				{
					mOutputRange=new Point(buffer.length(), -1);
					mReplaceRange=new Point(0, -1);
					mReplaceRange.x=((CommonToken)startToken).getStartIndex();
				}
			}
			else
			{
				if (mOutputRange.y<0 && startToken.getLine()>mSelectedRange.y)
				{
					mOutputRange.y=buffer.length();
					
					mReplaceRange.y=((CommonToken)startToken).getStartIndex();
				}
			}
		}
		
	}
	
	private int printTag(List<CommonToken> tokens, int tokenIndex, StringBuffer buffer, int stopType)
	{
		CommonToken startToken=tokens.get(tokenIndex);
		boolean attrOrderChanged=false;
		if (mSelectedRange!=null)
		{
			//find end token, so I can determine whether I need to capture the formatting boundary at this point
			CommonToken endToken=tokens.get(tokenIndex);
			int endTokenIndex=tokenIndex;
			while (endTokenIndex<tokens.size())
			{
				CommonToken token=tokens.get(endTokenIndex);
				if (token.getType()==stopType)
				{
					endToken=token;
					break;
				}
				
				endTokenIndex++;
			}
			
			updatePartialFormattingBoundaries(tokens.get(tokenIndex), endToken, buffer);
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
		/////handle blanks lines before/between tags
		//we have special handling if the tag is a start tag (either regular open or an empty tag)
		boolean isOpenTag=(startToken.getType()==MXMLLexer.EMPTY_TAG_OPEN || startToken.getType()==MXMLLexer.TAG_OPEN);
		boolean hasSeenFirstChild=false;
//		if (isOpenTag)
		{
			//This is part of blank lines between sibling tags.
			//if this is an open tag, then see if we have already seen the first child of the parent tag.
			TagStackEntry entry=getCurrentTag();
			if (entry!=null)
			{
				hasSeenFirstChild=entry.isSeenFirstChild();
				if (isOpenTag)
					entry.setSeenFirstChild();
			}
		}
		
		//We may need to add some blank lines before this tag.  If we are formatting AND it's an open tag AND
		//either we have spaces between sibling tags or we have blank lines before arbitrary tags.
		if (mDoFormat && (getBlankLinesBeforeTags()>0 || getBlankLinesAfterParentTags()>0 || (getSpacesBetweenSiblingTags()>0 && hasSeenFirstChild) || (getSpacesAfterParentTags()>0 && !hasSeenFirstChild)) && isOpenTag)
		{
			//walk through tokens to find the tag name, which we need for the "blanks before tags" option.
			int testTokenIndex=tokenIndex;
			while (testTokenIndex<tokens.size())
			{
				CommonToken token=tokens.get(testTokenIndex);
				if (token.getType()==MXMLLexer.GENERIC_ID)
				{
					//we've walked through the whitespace tokens and arrived at the tag name.  We'll handle the blank lines between
					//sibling tags here as well so that we can determine the correct number of lines between the two settings.
					int blankLinesToEnsure=0;
					//if the tag is one that should have blank lines before it, then capture that number of lines
					if (mTagsWithBlankLinesBeforeThem.contains(token.getText()) || matchesRegEx(token.getText(), mTagsWithBlankLinesBeforeThem))
						blankLinesToEnsure=Math.max(blankLinesToEnsure, getBlankLinesBeforeTags());
					
					if (getCurrentTag()!=null && !hasSeenFirstChild && (mParentTagsWithBlankLinesAfterThem.contains(getCurrentTag().getTagName()) || matchesRegEx(getCurrentTag().getTagName(), mParentTagsWithBlankLinesAfterThem)))
						blankLinesToEnsure=Math.max(blankLinesToEnsure, getBlankLinesAfterParentTags());
					
					//if we've previously seen the first child of the tag, then get the blank lines between sibling tags
					if (hasSeenFirstChild)
						blankLinesToEnsure=Math.max(blankLinesToEnsure, getSpacesBetweenSiblingTags());
					else if (getCurrentTag()!=null) //if we haven't seen the first child, then this is the first child, and we are interested in blank lines after parent
						blankLinesToEnsure=Math.max(blankLinesToEnsure, getSpacesAfterParentTags());
					
					//if we need to ensure any blank lines, then continue
					if (blankLinesToEnsure>0)
					{
						addBlankLines(buffer, blankLinesToEnsure);
					}
					break;
				}
				testTokenIndex++;
			}
		}
		else if (mDoFormat && !isOpenTag && hasSeenFirstChild && getBlankLinesBeforeCloseTags()>0)
		{
			addBlankLines(buffer, getBlankLinesBeforeCloseTags());
		}
		/////////////////////////////////////////////////////////////////////////////////////////
		
		mLastCommentStart=(-1);
		
		addIndentIfAtStartOfLine(buffer, false);
		int startOfTagInBuffer=buffer.length(); //we need this to be the point right where the first token gets added (it needs to match startToken)
		buffer.append(tokens.get(tokenIndex).getText());
		tokenIndex++;
		List<Attr> attrs=null;
		if (mDoFormat && stopType!=MXMLLexer.DECL_STOP)
			attrs=new ArrayList<Attr>();
//		boolean seenTagName=false;
		String tagName=null;
		String currentAttrName=null;
		CommonToken endToken=null;
		String spaceString=ActionScriptFormatter.generateSpaceString(mSpacesAroundEquals);
		int extraWrappedLineIndent=0;
		
		while (tokenIndex<tokens.size())
		{
//			addIndentIfAtStartOfLine(buffer);
			CommonToken token=tokens.get(tokenIndex);
			if (token.getType()==stopType)
			{
				endToken=token;
				if (attrs==null)
				{
					addIndentIfAtStartOfLine(buffer, false);
					buffer.append(token.getText());
				}
				break;
			}
			
			if (mDoFormat)
			{
				switch (token.getType())
				{
				case MXMLLexer.EOL:
				case MXMLLexer.WS:
					//ignore
					break;
				case MXMLLexer.EQ:
					if (attrs==null)
					{
						buffer.append(spaceString);
						buffer.append(token.getText());
						buffer.append(spaceString);
					}
					break;
				case MXMLLexer.GENERIC_ID:
				case MXMLLexer.XML:
					if (attrs==null)
					{
						if (tagName!=null)
							buffer.append(' ');
						buffer.append(token.getText());
					}
					
					if (tagName==null)
						tagName=token.getText();
					else
					{
						currentAttrName=token.getText();
					}
					
//					if (seenTagName)
//						buffer.append(' ');
//					buffer.append(token.getText());
//					seenTagName=true;
					break;
				case MXMLLexer.VALUE:
					if (attrs!=null)
					{
						if (currentAttrName!=null)
						{
							Attr a=new Attr();
							a.mName=currentAttrName;
							currentAttrName=null;
							a.mValue=token.getText();
							if (isUseSpacesInsideAttrBraces())
							{
								fixupBindingExpressions(a);
							}
							attrs.add(a);
						}
					}
					else
					{
						buffer.append(token.getText());
					}
					break;
				default:
					if (attrs==null)
					{
						buffer.append(token.getText());
					}
				}
			}
			else
			{
				switch (token.getType())
				{
				case MXMLLexer.EOL:
					buffer.append(token.getText());
					break;
				case MXMLLexer.WS:
					if (mRearrangeOnly)
					{
						buffer.append(token.getText());
						break;
					}
					
					//only print if there's text on the line already
					if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
						buffer.append(token.getText());
					break;
				default:
					boolean capturedTagName=false;
					if (tagName==null)
					{
						tagName=token.getText();
						capturedTagName=true;
					}
					addIndentIfAtStartOfLine(buffer, false);
					buffer.append(token.getText());
					if (capturedTagName)
					{
						if (getWrapStyle()==WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT)
							extraWrappedLineIndent=getLastLineColumnLength(buffer)+1-mCurrentIndent;
						else
							extraWrappedLineIndent=mHangingIndentSize*getIndentAmount();
						mCurrentIndent+=extraWrappedLineIndent;
					}
				}
			}
			
			tokenIndex++;
		}
		
		mCurrentIndent-=extraWrappedLineIndent;
		
		mEnclosingTagName=tagName;
		
		//spit out data here if we're formatting
		boolean allAttrsArePartOfCustomOrdering=false;
		if (mDoFormat && attrs!=null)
		{
			if (tagName!=null)
			{
				buffer.append(tagName);
			}
			
			int originalAttrCount=attrs.size();
			if (attrs.size()>0)
			{
				buffer.append(ActionScriptFormatter.generateSpaceString(1)); //TODO: configurable number of spaces before first attr
				
				List<Attr> oldAttrOrder=new ArrayList<Attr>();
				oldAttrOrder.addAll(attrs);
				
				//now, order attrs based on user settings
				int sortStart=0;
				if (mAttrOrderMode==MXML_ATTR_ORDERING_USEDATA)
				{
					//rewrite to capture all attributes in groups on a first pass.  Then walk attributes on the second pass
					//to populate the newAttrOrder list
					Map<String, List<AttrMapping>> groupMap=new HashMap<String, List<AttrMapping>>(); //hold items in each group in group order
					Map<String, List<AttrMapping>> literalMap=new HashMap<String, List<AttrMapping>>();
					
					//1st pass captures the attributes in groups
					//2nd pass performs reordering etc.
					for (int i=0;i<mManualAttrSortOrder.size();i++)
					{
						String sortItem=mManualAttrSortOrder.get(i);
						String[] attrItems=sortItem.split(",");

						//if we found at least one attr or none were missing
						for (String attr : attrItems)
						{
							attr=AntlrUtilities.asTrim(attr);
							if (attr.length()==0)
								continue;
							if (!attr.equals(AttrOrderConfigDialog.NewLineFlag))
							{
								String groupName=isGroupAttr(attr);
								if (groupName!=null)
								{
									AttrGroup group=mAttrGroups.get(groupName);
									if (group!=null)
									{
										List<MXMLPrettyPrinter.AttrMapping> attrsForGroup=new ArrayList<AttrMapping>();
										groupMap.put(groupName, attrsForGroup);
										//in this case, we want to find items in the tag and keep
										//them in that order
										Set<String> groupAttrSet=mHashedGroupAttrs.get(group.getName());
										if (groupAttrSet!=null && groupAttrSet.size()>0)
										{
											for (int k=attrs.size()-1;k>=0;k--)
											{
												String attrName=attrs.get(k).mName;
												boolean matchFound=groupAttrSet.contains(attrName);
												String attrSpec=attrName;
												if (!matchFound)
												{
													//check for regular expressions matching attr
													for (String regexAttr : group.getRegexAttrs()) {
														matchFound=matchesSpec(attrName, regexAttr, true, false); //group.isIncludeStates());
														if (matchFound)
														{
															attrSpec=regexAttr;
															break;
														}
													}
												}

												if (matchFound)
												{
													attrsForGroup.add(new AttrMapping(attrs.get(k), attrSpec));
													attrs.remove(k);
												}
											}
											Collections.reverse(attrsForGroup);
										}
									}
								}
								else
								{
									List<MXMLPrettyPrinter.AttrMapping> attrsForGroup=new ArrayList<AttrMapping>();
									literalMap.put(attr, attrsForGroup);
									for (int k=attrs.size()-1;k>=0;k--)
									{
										String attrName=attrs.get(k).mName;
										if (attrName.equals(attr))
										{
											//add in reverse order to maintain file order
											attrsForGroup.add(0, new AttrMapping(attrs.get(k), attr));
											attrs.remove(k);
										}
									}
								}
							}
						}
					}
					
					if (groupMap.containsKey(Initializer.Attr_Group_Other))
					{
						List<AttrMapping> attrsForGroup=new ArrayList<AttrMapping>();
						groupMap.put(Initializer.Attr_Group_Other, attrsForGroup);
						for (int k=0;k<attrs.size();k++)
						{
							String attrName=attrs.get(k).mName;
							attrsForGroup.add(new AttrMapping(attrs.get(k), ""));
						}
						attrs.clear();
					}
					
					List<Attr> newAttrOrder=new ArrayList<Attr>();
					for (int i=0;i<mManualAttrSortOrder.size();i++)
					{
						String sortItem=mManualAttrSortOrder.get(i);
						String[] attrItems=sortItem.split(",");
						boolean missingAttr=false;
						boolean existingAttr=false;
						
						for (String attrSpec : attrItems) {
							attrSpec=AntlrUtilities.asTrim(attrSpec);
							if (attrSpec.length()==0)
								continue;
							if (!attrSpec.equals(AttrOrderConfigDialog.NewLineFlag))
							{
								boolean found=false;
								String groupName=isGroupAttr(attrSpec);
								if (groupName!=null)
								{
									List<AttrMapping> cachedAttrs=groupMap.get(groupName);
									if (cachedAttrs!=null && cachedAttrs.size()>0) //AttrMapping.hasAttr(cachedAttrs, attrSpec))
										found=true;
								}
								else
								{
									List<AttrMapping> cachedAttrs=literalMap.get(attrSpec);
									found=(cachedAttrs!=null && cachedAttrs.size()>0);
								}
								
								if (!found)
									missingAttr=true;
								else
									existingAttr=true;
							}
						}
						
						//if we found at least one attr or none were missing
						if (existingAttr || !missingAttr)
						{
							for (String attr : attrItems)
							{
								attr=AntlrUtilities.asTrim(attr);
								if (attr.length()==0)
									continue;
								if (attr.equals(AttrOrderConfigDialog.NewLineFlag))
								{
									Attr newLineAttr=new Attr();
									newLineAttr.mName=AttrOrderConfigDialog.NewLineFlag;
									newAttrOrder.add(newLineAttr);
								}
								else 
								{
									String groupName=isGroupAttr(attr);
									if (groupName!=null)
									{
										AttrGroup group=mAttrGroups.get(groupName);
										if (group!=null)
										{
											//make a copy of mappings so I can remove them in one
											//of the codepaths below
											List<AttrMapping> mappingsForGroup=new ArrayList<AttrMapping>();
											mappingsForGroup.addAll(groupMap.get(groupName));
											List<Attr> attrsForGroup=new ArrayList<Attr>();
											
											switch (group.getSortMode())
											{
											case MXMLPrettyPrinter.MXML_Sort_None:
											case MXMLPrettyPrinter.MXML_Sort_AscByCase:
												for (AttrMapping mapping : mappingsForGroup) {
													attrsForGroup.add(mapping.mAttr);
												}
												if (group.getSortMode()==MXMLPrettyPrinter.MXML_Sort_AscByCase)
												{
													Collections.sort(attrsForGroup);
												}
												break;
											case MXMLPrettyPrinter.MXML_Sort_GroupOrder:
												//this one needs to be done in reverse: walk the group items and find items
												//that match and keep them in group order
												List<String> groupAttrs=group.getAttrs();
												for (String attrSpec : groupAttrs)
												{
													List<Attr> newAttrs=new ArrayList<Attr>();
													for (int j=mappingsForGroup.size()-1;j>=0;j--)
													{
														AttrMapping mapping=mappingsForGroup.get(j);
														if (mapping.mAttrSpec.equals(attrSpec) || mapping.mAttrSpec.equals(attrSpec+StateRegexSuffix))
														{
															newAttrs.add(mapping.mAttr);
															mappingsForGroup.remove(j);
														}
													}
													Collections.sort(newAttrs);
													attrsForGroup.addAll(newAttrs);
												}
												break;
											}
										
											if (attrsForGroup.size()>0)
											{
												//add wrap mode
												insertAttrs(-1, group, attrsForGroup, newAttrOrder);
											}
										}										
									}
									else
									{
										List<AttrMapping> mappingsForItem=literalMap.get(attr);
										if (mappingsForItem!=null)
										{
											for (AttrMapping attrMapping : mappingsForItem) {
												newAttrOrder.add(attrMapping.mAttr);
											}
										}
									}
								}
							}
						}
					}
					
					//if we didn't have any leftover attrs, then clean up extra newlines
					if (attrs.size()==0)
					{
						//remove extra newlines at end
						while (newAttrOrder.size()>0)
						{
							Attr attr=newAttrOrder.get(newAttrOrder.size()-1);
							if (attr.mName.equals(AttrOrderConfigDialog.NewLineFlag))
							{
								newAttrOrder.remove(newAttrOrder.size()-1);
								allAttrsArePartOfCustomOrdering=true;
							}
							else
							{
								break;
							}
						}
					}
					
					sortStart=newAttrOrder.size();
					newAttrOrder.addAll(attrs);
					if (mSortOtherAttrs)
					{
						if (sortStart<newAttrOrder.size()-1)
						{
							Collections.sort(newAttrOrder.subList(sortStart, newAttrOrder.size()));
						}
					}
					
					//compare old attr order and new attr order and see if the attributes are in the same order
					for (int i=0,j=0;i<oldAttrOrder.size() && j<newAttrOrder.size();)
					{
						//skip newlines and other meta flags
						String newAttrName=newAttrOrder.get(j).mName;
						if (newAttrName.equals(AttrOrderConfigDialog.NewLineFlag) || newAttrName.startsWith("<"))
						{
							j++;
							continue;
						}
						 
						if (!newAttrOrder.get(j).mName.equals(oldAttrOrder.get(i).mName))
						{
							attrOrderChanged=true;
							break;
						}
						i++;
						j++;
					}
					
					//replace attributes with the custom ordering
					attrs=newAttrOrder;
				}

			}
			
			//find last hard newline in attr list (some options only apply after the last newline
			int lastNewLine=(-1);
			if (allAttrsArePartOfCustomOrdering)
				lastNewLine=attrs.size();
			int lastAttr=(-1);
			for (int j = 0; j < attrs.size(); j++)
			{
				Attr attr = attrs.get(j);
				if (!allAttrsArePartOfCustomOrdering && attr.mName.equals(AttrOrderConfigDialog.NewLineFlag))
					lastNewLine=j;
				if (!attr.mName.startsWith("<"))
					lastAttr=j;
			}
			
			//add the ending newline if we  need it
			if (mAttrOrderMode==MXML_ATTR_ORDERING_USEDATA && mAddNewlineAfterLastAttr && attrs.size()>0)
			{
				Attr newLineAttr=new Attr();
				newLineAttr.mName=AttrOrderConfigDialog.NewLineFlag;
				attrs.add(newLineAttr);
			}
			
			int firstIndent=(-1);
			int attrsOnLine=0;
			int wrapMode=MXML_ATTR_WRAP_NONE;
			int attrsPerLine=getAttrsPerLine();
			boolean inGroup=false;
			if (lastNewLine<0)
				wrapMode=getWrapMode();
			boolean disableWrapping=false;
			if (isUseAttrsToKeepOnSameLine() && originalAttrCount<=getAttrsToKeepOnSameLine())
			{
				disableWrapping=true;
				//we use the getter here for wrapmode to get the default wrap mode instead of the wrapmode
				//we would use if we were processing some hardcode attribute rows at the start
				if (mAlwaysObeyMaxLineLength && getWrapMode()==MXML_ATTR_WRAP_LINE_LENGTH)
				{
					//attempt to pre-calculate the width so that I can reverse the disableWrapping mode if the
					//line is too long
//					boolean seenFirstAttr=false;
					int currentLineLength=ASPrettyPrinter.determineLastLineLength(buffer, getTabSize());
					for (Attr attr : attrs) {
						if (attr.mName.startsWith("<"))
						{
							//do nothing
							continue;
						}
						else if (attr.mName.equals(AttrOrderConfigDialog.NewLineFlag))
						{
							//do nothing
							continue;
						}
						
						currentLineLength++; //for space before first attrs and between attrs (it's hardcoded to 1 space right now)
						currentLineLength+=(attr.mName.length());
						currentLineLength+=spaceString.length();
						currentLineLength+=1; //'='
						currentLineLength+=spaceString.length();
						currentLineLength+=attr.mValue.length();
					}
					
					//if the line length we've calculated beyond the max length, then reverse the disableWrapping flag.
					if (currentLineLength>=mMaxLineLength)
						disableWrapping=false;
					
				}
			}
			for (int j = 0; j < attrs.size(); j++)
			{
				Attr attr = attrs.get(j);
				
				//check for a group wrap mode
				if (attr.mName.startsWith("<"))
				{
					if (disableWrapping)
						continue;
					
//					wrapMode=(j<lastNewLine) ? MXML_ATTR_WRAP_NONE : getWrapMode();
					wrapMode=getWrapMode(); //set back to default until I determine otherwise
					if (attr.mName.startsWith("<Wrap="))
					{
						inGroup=true;
						String dataString=attr.mName.substring("<Wrap=".length(), attr.mName.length()-1);
						int commaPos=dataString.indexOf(',');
						String modeString=dataString;
						if (commaPos>0)
						{
							modeString=dataString.substring(0, commaPos);
							String nPerLineString=dataString.substring(commaPos+1);
							try
							{
								attrsPerLine=Integer.parseInt(nPerLineString);
								if (attrsPerLine==AttrGroup.Wrap_Data_Use_Default)
									attrsPerLine=getAttrsPerLine();
							}
							catch (NumberFormatException e)
							{
								attrsPerLine=getAttrsPerLine();
								Activator.logException(e, null);
							}
						}
						try
						{
							wrapMode=Integer.parseInt(modeString);
						}
						catch (NumberFormatException e)
						{
							Activator.logException(e, null);
						}
					}
					else if (attr.mName.equals("</Wrap>"))
					{
						inGroup=false;
						attrsPerLine=getAttrsPerLine(); //set count back to the default
					}
					
					continue;
				}
				
				//establish indent first
				if (firstIndent<0)
				{
					if (getWrapStyle()==WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT)
						firstIndent=getLastLineColumnLength(buffer);
					else
						firstIndent=mCurrentIndent+mHangingIndentSize*getIndentAmount();
				}
				
				boolean isNewline=attr.mName.equals(AttrOrderConfigDialog.NewLineFlag);
				
				//go ahead and precalculate the string for the attribute/value pair
				StringBuffer attrString=new StringBuffer();
				if (!isNewline)
				{
					attrString.append(attr.mName);
					attrString.append(spaceString);
					attrString.append('=');
					attrString.append(spaceString);
					attrString.append(attr.mValue);
				}
				
				boolean justWrapped=false;
				if (!disableWrapping && j<=lastAttr && wrapMode!=MXML_ATTR_WRAP_NONE)
				{
					if (wrapMode==MXML_ATTR_WRAP_COUNT_PER_LINE)
					{
						if (attrsOnLine>0 && attrsOnLine>=attrsPerLine)
						{
							if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
							{
								buffer.append('\n');
								buffer.append(generateIndent(firstIndent));
								attrsOnLine=0;
								justWrapped=true;
							}
						}
					}
					else if (wrapMode==MXML_ATTR_WRAP_LINE_LENGTH && !isNewline)
					{
						//we'll add a line break if the next attribute will push the length beyond max, *unless* we're 
						//already on a new line, in which case we just go ahead and stick the text on this line.
						int currentLineLength=ASPrettyPrinter.determineLastLineLength(buffer, getTabSize());
						if (currentLineLength+attrString.length()>=mMaxLineLength)
						{
							if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
							{
								buffer.append('\n');
								buffer.append(generateIndent(firstIndent));
								attrsOnLine=0;
								justWrapped=true; //I don't think this needs to be here now, because we're checking for isNewline in the 'if'
							}
						}
					} 
				}					
				
				if (isNewline)
				{
					if (disableWrapping)
						continue;
					
					//reset the default wrap mode if we are beyond the end of the custom wrap mode
					if (j>=lastNewLine && !inGroup)
						wrapMode=getWrapMode();
					
					if (justWrapped) //don't add another carriage return if one was added via wrapping
					{
//						wrappedLastIteration=false;
						continue;
					}
					
					buffer.append('\n');
					attrsOnLine=0;
					buffer.append(generateIndent(firstIndent));
					continue;
				}
				
//				wrappedLastIteration=false;
				
				if (buffer.length()>0 && !ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
				{
					if (buffer.charAt(buffer.length()-1)!=' ')
						buffer.append(ActionScriptFormatter.generateSpaceString(1)); //TODO: configurable number of spaces between attrs?
				}
				
				buffer.append(attrString);
				
				attrsOnLine++;
			}
			
			//if newline before end tag, BUT we don't want the indent to align with the first attribute, then
			//we clear the whitespace on the current line.  It appears that we always add the indent to the
			//first attribute after we add a newline, even in this case where the newline was at the end
			//of the attributes.
			if (mAddNewlineAfterLastAttr && !mIndentCloseTag && ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
				ActionScriptFormatter.trimWhitespaceOnEndOfBuffer(buffer);
			addIndentIfAtStartOfLine(buffer, false); //add an indent to the tag level if there wasn't one.
			if (endToken!=null)
			{
				if (!ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
				{
					if (endToken.getText().equals("/>"))
					{
						//print configurable number of spaces before empty tag end
						buffer.append(ActionScriptFormatter.generateSpaceString(getSpacesBeforeEmptyTagEnd()));
					}
				}
				buffer.append(endToken.getText());
			}
			
			if (attrOrderChanged)
			{
				if (mReplaceMap==null)
					mReplaceMap=new HashMap<Integer, ReplacementRange>();
				ReplacementRange range=new ReplacementRange(new Point(startOfTagInBuffer, buffer.length()), new Point(startToken.getStartIndex(),endToken.getStopIndex()+1));
				mReplaceMap.put(startOfTagInBuffer, range);
			}
		}
	
		return tokenIndex;
	}

	private void addBlankLines(StringBuffer buffer, int blankLinesToEnsure) {
		//count blank lines at end of current buffer (or before last comment) to see if there are already some there
		int emptyLinesAlreadyThere=ActionScriptFormatter.getNumberOfEmptyLinesAtEnd(buffer, mLastCommentStart);
		int linesToAdd=blankLinesToEnsure-emptyLinesAlreadyThere;
		if (linesToAdd>0)
		{
			if (emptyLinesAlreadyThere==0 && !ActionScriptFormatter.isOnlyWhitespaceOnLastLine(buffer))
				linesToAdd++;
			for (int i=0;i<linesToAdd;i++)
			{
				if (mLastCommentStart<0)
					insertCR(buffer, true);
				else
				{
					//if we are inserting before the comment, then we have to do that manually
					buffer.insert(mLastCommentStart, '\n');
					movePartialFormattingBoundaries(mLastCommentStart, 1);
				}
			}
		}
	}
	
	
	private void insertAttrs(int insertLocation, AttrGroup group, List<Attr> attrsForGroup, List<Attr> newAttrOrder)
	{
		int wrapMode=group.getWrapMode();
		if (wrapMode==MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT)
			wrapMode=getWrapMode();
		Attr wrapAttr=new Attr();
		//	wrapAttr.mName="<Wrap="+wrapMode+">";
		wrapAttr.mName="<Wrap="+wrapMode;
		if (group.getWrapMode()==MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE)
		{
			wrapAttr.mName+=","+group.getData();
		}
		wrapAttr.mName+=">";
		List<Attr> newItems=new ArrayList<Attr>();
		newItems.add(wrapAttr);
		newItems.addAll(attrsForGroup);
		wrapAttr=new Attr();
		wrapAttr.mName="</Wrap>";
		newItems.add(wrapAttr);
		newAttrOrder.addAll(insertLocation>=0 ? insertLocation : newAttrOrder.size(), newItems);
	}

//	private void findOtherAttrs(List<Attr> codeAttrs, Map<String, Set<String>> hashedGroupAttrs, Collection<String> outputAttrs) 
//	{
//		mainLoop: for (Attr attr : codeAttrs) {
//			for (String groupName : hashedGroupAttrs.keySet()) {
//				AttrGroup group=mAttrGroups.get(groupName);
//				if (group==null)
//					continue;
//				for (String groupAttrSpec : group.getAttrs()) {
//					boolean isRegex=AttrGroup.isRegexString(groupAttrSpec);
//					if (matchesSpec(attr.mName, groupAttrSpec, isRegex, group.isIncludeStates()))
//					{
//						outputAttrs.add(attr.mName);
//						continue mainLoop;
//					}
//				}
//			}
//		}
//	}

	private void fixupBindingExpressions(Attr a)
	{
		//Add the spacing within braces if this appears to be a binding to a method call.  There could be multiple
		//binding expressions, so we should look for brace pairs in the string.
		
		//don't process for non-mxml files
		if (isPlainXML())
			return;
			
		if (a.mValue==null)
			return;
		
		//kick out quickly if we don't have any potential brace pairs at all
		int startBrace=a.mValue.indexOf('{');
		if (startBrace<0)
			return;
		
		StringBuffer output=new StringBuffer();
		for (int i=0;i<a.mValue.length();i++)
		{
			char c=a.mValue.charAt(i);
			output.append(c); //always append the character
			if (c=='{')
			{
				//find end brace and process between them
				int endPos=(-1);
				for (int k=i+1;k<a.mValue.length();k++)
				{
					char newC=a.mValue.charAt(k);
					if (newC=='}')
					{
						endPos=k;
						break;
					}
					else if (newC=='\\')
					{
						k++; //skip over next char
					}
				}
				
				//only if we found an end pos do we need to do anything
				if (endPos>=0)
				{
					String data=a.mValue.substring(i+1, endPos);
					data=AntlrUtilities.asTrim(data);
					
					//format data string here to handle internal spacing
					if (isFormatBoundAttributes() && mDoFormat)
					{
						try {
							//NOTE: the validation code below will fail if braces are added.  Hopefully people won't be doing
							//any control structures in an attribute string.
							mASPrinter.setDoFormat(mDoFormat);
							mASPrinter.setData(data);
							String newValue=mASPrinter.print(0);
							if ((mASPrinter.getParseErrors()==null || mASPrinter.getParseErrors().size()==0) && newValue!=null && newValue.length()>0 && newValue.indexOf('\n')<0) //I don't really want to add carriage returns
							{
								data=AntlrUtilities.asTrim(newValue); //we don't want to keep any whitespace on the ends; only internal whitespace
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					String spaceString=ActionScriptFormatter.generateSpaceString(mSpacesInsideAttrBraces);
					output.append(spaceString);
					output.append(data);
					output.append(spaceString);
					output.append('}');
					i=endPos; //reset i so that the whole block will be skipped.
				}
			}
			else if (c=='\\')
			{
				i++; //skip over next char; might be an escaped '{' or something else
				output.append(a.mValue.charAt(i)); //append next char too
			}
		}
		
		//verify that the strings are equal
		if (ActionScriptFormatter.validateNonWhitespaceIdentical(a.mValue, output.toString()))
		{
			a.mValue=output.toString();
		}
		
	}

	private boolean matchesRegEx(String text, Set<String> tagsWithBlankLinesBeforeThem)
	{
		for (String tag : tagsWithBlankLinesBeforeThem) {
			if (AttrGroup.isRegexString(tag))
			{
				if (Pattern.matches(tag, text))
					return true;
			}
		}
		
		return false;
	}

	private boolean matchesSpec(String testAttrName, String groupAttrSpec, boolean isRegex, boolean includeStateAttributes)
	{
		try
		{
			boolean matches=testAttrName.equals(groupAttrSpec);
			if (!matches && isRegex)
			{
				matches=Pattern.matches(groupAttrSpec, testAttrName);
			}
			if (!matches && includeStateAttributes)
			{
				matches=Pattern.matches(groupAttrSpec+StateRegexSuffix, testAttrName);
			}
			return matches;
		}
		catch (PatternSyntaxException e)
		{
			Activator.logException(e, "Bad attribute regular expression: "+groupAttrSpec);
		}
		
		return false;
	}
	
	private int getLastLineColumnLength(StringBuffer buffer)
	{
		int lastCR=buffer.lastIndexOf("\n");
		String lastLine=null;
		if (lastCR<0)
			lastLine=buffer.toString();
		else
			lastLine=buffer.substring(lastCR+1);
		int columnCount=0;
		for (int i=0;i<lastLine.length();i++)
		{
			char c=lastLine.charAt(i);
			if (c=='\t')
			{
				int remainder=columnCount%getTabSize();
				if (remainder==0)
					columnCount+=getTabSize();
				else
				{
					columnCount+=remainder;
				}
			}
			else
			{
				columnCount++;
			}
		}
		
		return columnCount;
	}

	public void addIndentIfAtStartOfLine(StringBuffer buffer, boolean force)
	{
		if (mRearrangeOnly && !force)
			return;
		
		if (!mSkipNextIndent && ActionScriptFormatter.isLineEmpty(buffer))
		{
			buffer.append(generateIndent(mCurrentIndent));
		}
		
		mSkipNextIndent=false;
	}
	
	private String generateIndent(int spaces)
	{
		return ActionScriptFormatter.generateIndent(spaces, mUseTabs, mTabSize);
	}

	public List<Exception> getParseErrors() {
		List<Exception> allErrors=new ArrayList<Exception>();
		if (mParseErrors!=null)
			allErrors.addAll(mParseErrors);
		if (mASPrinter.getParseErrors()!=null)
			allErrors.addAll(mASPrinter.getParseErrors());
		return allErrors;
	}

	public void setDoFormat(boolean b)
	{
		mDoFormat=b;
	}

	public boolean isDoFormat()
	{
		return mDoFormat;
	}
	
	public ASPrettyPrinter getASPrinter()
	{
		return mASPrinter;
	}

	public int getIndentAmount() {
		return mIndentAmount;
	}

	public void setIndentAmount(int indentAmount) {
		mIndentAmount = indentAmount;
	}

	public int getSpacesAroundEquals() {
		return mSpacesAroundEquals;
	}

	public void setSpacesAroundEquals(int spacesAroundEquals) {
		mSpacesAroundEquals = spacesAroundEquals;
	}

	public boolean isSortOtherAttrs() {
		return mSortOtherAttrs;
	}

	public void setSortOtherAttrs(boolean sortOtherAttrs) {
		mSortOtherAttrs = sortOtherAttrs;
	}

	public void setAttrSortMode(int sortMode)
	{
		mAttrOrderMode=sortMode;
	}
	
	public int getAttrSortMode()
	{
		return mAttrOrderMode;
	}
	
	public void setManualAttrSortData(List<String> attrOrder)
	{
		if (attrOrder==null)
			mManualAttrSortOrder.clear();
		else
			mManualAttrSortOrder=attrOrder;
	}

	public List<String> getManualAttrSortData()
	{
		return mManualAttrSortOrder;
	}
	public int getMaxLineLength() {
		return mMaxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		mMaxLineLength = maxLineLength;
	}

	public Point getSelectedRange() {
		return mSelectedRange;
	}

	public void setSelectedRange(Point selectedRange) {
		mSelectedRange = selectedRange;
	}

	public Point getOutputRange() {
		return mOutputRange;
	}

	public Point getReplaceRange() {
		return mReplaceRange;
	}

	public int getAttrsPerLine() {
		return mAttrsPerLine;
	}

	public void setAttrsPerLine(int attrsPerLine) {
		mAttrsPerLine = attrsPerLine;
	}

	public int getWrapMode() {
		return mWrapMode;
	}

	public void setWrapMode(int wrapMode) {
		mWrapMode = wrapMode;
	}

	public boolean isKeepBlankLines() {
		return mKeepBlankLines;
	}

	public void setKeepBlankLines(boolean keepBlankLines) {
		mKeepBlankLines = keepBlankLines;
	}
	public int getWrapStyle()
	{
		return mWrapStyle;
	}
	
	public void setWrapStyle(int style)
	{
		mWrapStyle=style;
	}
	
	public void setTagsThatCanBeFormatted(Set<String> tagNames)
	{
		mTagsWhoseTextContentsCanBeFormatted.clear();
		mTagsWhoseTextContentsCanBeFormatted.addAll(tagNames);
	}
	
	public Set<String> getTagsThatCanBeFormatted()
	{
		return mTagsWhoseTextContentsCanBeFormatted;
	}
	
	public void setTagsThatCannotBeFormatted(Set<String> tagNames)
	{
		mTagsWhoseTextContentCanNeverBeFormatted.clear();
		mTagsWhoseTextContentCanNeverBeFormatted.addAll(tagNames);
	}
	
	public Set<String> getTagsThatCannotBeFormatted()
	{
		return mTagsWhoseTextContentCanNeverBeFormatted;
	}

	public void setAttrGroups(List<AttrGroup> attrGroups)
	{
		mHashedGroupAttrs=new HashMap<String, Set<String>>();
		mAttrGroups=new HashMap<String, AttrGroup>();
		for (AttrGroup group : attrGroups) {
			mAttrGroups.put(group.getName(), group);
			Set<String> attrSet=new HashSet<String>();
			for (String attr : group.getAttrs()) {
				attrSet.add(attr);
			}
			mHashedGroupAttrs.put(group.getName(), attrSet);
		}
		
		
	}

	public static String isGroupAttr(String attr)
	{
		if (attr.length()>=2 && attr.startsWith(AttrOrderConfigDialog.Attr_Group_Marker) && attr.endsWith(AttrOrderConfigDialog.Attr_Group_Marker))
			return attr.substring(1, attr.length()-1);
		return null;
	}

	public void setAddNewlineAfterLastAttr(boolean addNewlineAfterLastAttr)
	{
		mAddNewlineAfterLastAttr=addNewlineAfterLastAttr;
	}

	public void setIndentCloseTag(boolean value)
	{
		mIndentCloseTag=value;
	}

	public Map<Integer, ReplacementRange> getReplaceMap()
	{
		return mReplaceMap;
	}

	public boolean isUseAttrsToKeepOnSameLine() {
		return mUseAttrsToKeepOnSameLine;
	}

	public void setUseAttrsToKeepOnSameLine(boolean useAttrsToKeepOnSameLine) {
		mUseAttrsToKeepOnSameLine = useAttrsToKeepOnSameLine;
	}

	public int getAttrsToKeepOnSameLine() {
		return mAttrsToKeepOnSameLine;
	}

	public void setAttrsToKeepOnSameLine(int attrsToKeepOnSameLine) {
		mAttrsToKeepOnSameLine = attrsToKeepOnSameLine;
	}

	public int getSpacesBeforeEmptyTagEnd() {
		return mSpacesBeforeEmptyTagEnd;
	}

	public void setSpacesBeforeEmptyTagEnd(int spacesBeforeEmptyTagEnd) {
		mSpacesBeforeEmptyTagEnd = spacesBeforeEmptyTagEnd;
	}

	public Set<String> getTagsWithBlankLinesBeforeThem() {
		return mTagsWithBlankLinesBeforeThem;
	}

	public void setTagsWithBlankLinesBeforeThem(
			Set<String> tagsWithBlankLinesBeforeThem) {
		mTagsWithBlankLinesBeforeThem.clear();
		mTagsWithBlankLinesBeforeThem.addAll(tagsWithBlankLinesBeforeThem);
	}

	public int getBlankLinesBeforeTags() {
		return mBlankLinesBeforeTags;
	}

	public void setBlankLinesBeforeTags(int blankLinesBeforeTags) {
		mBlankLinesBeforeTags = blankLinesBeforeTags;
	}
	
	public int getBlankLinesAfterParentTags() {
		return mBlankLinesAfterParentTags;
	}

	public void setBlankLinesAfterSpecificParentTags(int blankLinesAfterTags) {
		mBlankLinesAfterParentTags = blankLinesAfterTags;
	}

	public int getBlankLinesBeforeCloseTags() {
		return mBlankLinesBeforeCloseTags;
	}

	public void setBlankLinesBeforeCloseTags(int count) {
		mBlankLinesBeforeCloseTags = count;
	}

	public Set<String> getASScriptTags() {
		return mASScriptTags;
	}

	public void setASScriptTags(Set<String> scriptTags) {
		mASScriptTags.clear();
		mASScriptTags.addAll(scriptTags);
	}

	public boolean isRequireCDATAForASContent() {
		return mRequireCDATAForASContent;
	}

	public void setRequireCDATAForASContent(boolean requireCDATAForASContent) {
		mRequireCDATAForASContent = requireCDATAForASContent;
	}

	private static class TagStackEntry
	{
		private String mTagName;
		private boolean mSeenFirstChild;
		public TagStackEntry(String tagName)
		{
			mTagName=tagName;
			mSeenFirstChild=false;
		}
		public String getTagName() {
			return mTagName;
		}
		public boolean isSeenFirstChild() {
			return mSeenFirstChild;
		}
		public void setSeenFirstChild()
		{
			mSeenFirstChild = true;
		}
	}

	public int getSpacesBetweenSiblingTags() {
		return mSpacesBetweenSiblingTags;
	}

	public void setSpacesBetweenSiblingTags(int spacesBetweenSiblingTags) {
		mSpacesBetweenSiblingTags = spacesBetweenSiblingTags;
	}

	public int getSpacesAfterParentTags() {
		return mSpacesAfterParentTags;
	}

	public void setSpacesAfterParentTags(int spacesAfterParentTags) {
		mSpacesAfterParentTags = spacesAfterParentTags;
	}

	public boolean isPlainXML() {
		return mIsPlainXML;
	}

	public void setPlainXML(boolean isPlainXML) {
		mIsPlainXML = isPlainXML;
	}

	public Set<String> getParentTagsWithBlankLinesAfterThem() {
		return mParentTagsWithBlankLinesAfterThem;
	}

	public void setParentTagsWithBlankLinesAfterThem(
			Set<String> parentTagsWithBlankLinesAfterThem) {
		mParentTagsWithBlankLinesAfterThem = parentTagsWithBlankLinesAfterThem;
	}

	public IPreferenceStore getStore() {
		return mStore;
	}

	public void setStore(IPreferenceStore store) {
		mStore = store;
	}

	public void setObeyMaxLineLength(boolean obey)
	{
		mAlwaysObeyMaxLineLength=obey;
	}

	public void setRearrangeOnlyMode(boolean rearrangeOnly)
	{
		mRearrangeOnly=rearrangeOnly;
	}
	
	public void disableMultiPassMode()
	{
		mAllowMultiplePasses=false;
		mASPrinter.disableMultiPassMode();
	}

	public void setData(String data)
	{
		mSource=data;
	}

	public boolean needAnotherPass()
	{
		//no notion of needing another pass at mxml level, but as printer might require it
		return mNeedAnotherPass;
	}

	public void setHangingIndentTabs(int tabCount)
	{
		mHangingIndentSize=tabCount;
	}
	
	public int getBlankLinesBeforeComments() {
		return mBlankLinesBeforeComments;
	}

	public void setBlankLinesBeforeComments(int mBlankLinesBeforeComments) {
		this.mBlankLinesBeforeComments = mBlankLinesBeforeComments;
	}

	public boolean isUsePrivateTags() {
		return mUsePrivateTags;
	}

	public void setUsePrivateTags(boolean usePrivateTags) {
		this.mUsePrivateTags = usePrivateTags;
	}

	public List<String> getPrivateTags() {
		return mPrivateTags;
	}

	public void setPrivateTags(List<String> privateTags) {
		this.mPrivateTags = privateTags;
	}

	public boolean isUseSpacesInsideAttrBraces() {
		return mUseSpacesInsideAttrBraces;
	}

	public void setUseSpacesInsideAttrBraces(boolean useSpacesInsideAttrBraces) {
		mUseSpacesInsideAttrBraces = useSpacesInsideAttrBraces;
	}

	public int getSpacesInsideAttrBraces() {
		return mSpacesInsideAttrBraces;
	}

	public void setSpacesInsideAttrBraces(int spacesInsideAttrBraces) {
		mSpacesInsideAttrBraces = spacesInsideAttrBraces;
	}

	public boolean isFormatBoundAttributes() {
		return mFormatBoundAttributes;
	}

	public void setFormatBoundAttributes(boolean formatBoundAttributes) {
		mFormatBoundAttributes = formatBoundAttributes;
	}
	
	//holds mapping of an attribute and the attribute spec that matched it.
	static class AttrMapping
	{
		public AttrMapping(Attr attr, String spec)
		{
			mAttr=attr;
			mAttrSpec=spec;
		}
		public static boolean hasAttr(List<AttrMapping> cachedAttrs, String attrSpec)
		{
			for (AttrMapping attrMapping : cachedAttrs) {
				if (attrMapping.mAttrSpec.equals(attrSpec))
					return true;
			}
			return false;
		}
		
		public Attr mAttr;
		public String mAttrSpec;
	}
	
	public boolean isKeepRelativeCommentIndent()
	{
		return mKeepRelativeCommentIndent;
	}

	public void setKeepRelativeCommentIndent(boolean value)
	{
		mKeepRelativeCommentIndent=value;
	}

	public int getCDATAIndentTabs() {
		return mCDATAIndentTabs;
	}

	public void setCDATAIndentTabs(int tabs) {
		mCDATAIndentTabs = tabs;
	}

	public int getScriptIndentTabs() {
		return mScriptIndentTabs;
	}

	public void setScriptIndentTabs(int tabs) {
		mScriptIndentTabs = tabs;
	}

	public boolean isKeepCDataOnSameLine() {
		return mKeepCDataOnSameLine;
	}

	public void setKeepCDataOnSameLine(boolean keep) {
		mKeepCDataOnSameLine = keep;
	}

	public int getBlankLinesAtCDataStart() {
		return mBlankLinesAtCDataStart;
	}

	public void setBlankLinesAtCDataStart(int blankLinesAtCDataStart) {
		mBlankLinesAtCDataStart = blankLinesAtCDataStart;
	}

	public int getBlankLinesAtCDataEnd() {
		return mBlankLinesAtCDataEnd;
	}

	public void setBlankLinesAtCDataEnd(int blankLinesAtCDataEnd) {
		mBlankLinesAtCDataEnd = blankLinesAtCDataEnd;
	}
	
	
}
