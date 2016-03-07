package flexasrearrangecodecommand.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import flexasrearrangecodecommand.preferences.PreferenceConstants;
import flexprettyprint.handlers.ActionScriptFormatter;
import flexprettyprint.handlers.MXMLLexer;
import flexprettyprint.preferences.AttrGroup;

public class MXMLRearranger 
{
	private IPreferenceStore mPrefs;
	private IDocument mSourceDocument;
	private boolean mIsSoftFailure=false;
	private String mInternalError;
	private List<Exception> mErrors;
	
	public MXMLRearranger(IPreferenceStore store)
	{
		mPrefs=store;
	}
	
	public boolean rearrangeCode(IDocument source, List<MarkerAnnotation> lineBasedAnnotations)
	{
		if (!mPrefs.getBoolean(PreferenceConstants.MXMLRearr_UseRearrangeTagOrdering))
			return true;
		
		try
		{
			mSourceDocument=source;
			MXMLLexer lex = new MXMLLexer(new ANTLRStringStream(mSourceDocument.get()));
			lex.mDOCUMENT();
			boolean success=performRearrange(lex.getTokens());
			if (success)
				return true;
		}
		catch (Exception e)
		{
			if (mErrors==null)
				mErrors=new ArrayList<Exception>();
			mErrors.add(e);
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean performRearrange(List<CommonToken> tokens) throws BadLocationException
	{
		//1. find the top tag
		//2. find the top level tags within it
		//3. reorder the tags based on the sort order
		//4. apply the changes to the document
		List<TagHolder> topLevelTags=new ArrayList<MXMLRearranger.TagHolder>();
		int tagLevel=0;
		for (int tokenIndex=0;tokenIndex<tokens.size();tokenIndex++)
		{
			CommonToken token=tokens.get(tokenIndex);
			System.out.println(token.getText());
			switch (token.getType())
			{
				case MXMLLexer.TAG_OPEN:
					if (tagLevel==1)
					{
						//capture tag
						int previousWSIndex=findPreviousWhitespaceIndex(tokens, tokenIndex-1);
						TagHolder holder=new TagHolder(tokens.get(previousWSIndex), previousWSIndex);
						addTagName(holder, tokens, tokenIndex);
						updateCommentTagNames(topLevelTags, holder.mTagName);
						topLevelTags.add(holder);
					}
					tagLevel++;
					tokenIndex=findToken(tokenIndex, tokens, MXMLLexer.TAG_CLOSE);
					break;
				case MXMLLexer.END_TAG_OPEN:
					tagLevel--;
					tokenIndex=findToken(tokenIndex, tokens, MXMLLexer.TAG_CLOSE);
					if (tagLevel==1)
					{
						markTopLevelTagEnd(tokenIndex, topLevelTags);
					}
					break;
				case MXMLLexer.EMPTY_TAG_OPEN:
					if (tagLevel==1)
					{
						//capture tag
						int previousWSIndex=findPreviousWhitespaceIndex(tokens, tokenIndex-1);
						TagHolder holder=new TagHolder(tokens.get(previousWSIndex), previousWSIndex);
						addTagName(holder, tokens, tokenIndex);
						updateCommentTagNames(topLevelTags, holder.mTagName);
						topLevelTags.add(holder);
					}
					tokenIndex=findToken(tokenIndex, tokens, MXMLLexer.EMPTYTAG_CLOSE);
					if (tagLevel==1)
						markTopLevelTagEnd(tokenIndex, topLevelTags);
					break;
				case MXMLLexer.COMMENT:
					if (tagLevel==1)
					{
						int previousWSIndex=findPreviousWhitespaceIndex(tokens, tokenIndex-1);
						TagHolder holder=new TagHolder(tokens.get(previousWSIndex), previousWSIndex);
						topLevelTags.add(holder);
						markTopLevelTagEnd(tokenIndex, topLevelTags);
					}
					break;
//				case MXMLLexer.DECL_START:
//				case MXMLLexer.CDATA:
//				case MXMLLexer.PCDATA:
//				case MXMLLexer.EOL:
//				case MXMLLexer.WS:
			}
		}
		
		List<TagHolder> unsortedList=new ArrayList<MXMLRearranger.TagHolder>();
		unsortedList.addAll(topLevelTags);
		
		//sort the elements in the tag list based on the supplied ordering
		String ordering=mPrefs.getString(PreferenceConstants.MXMLRearr_RearrangeTagOrdering);
		String[] tagNames=ordering.split(PreferenceConstants.AS_Pref_Line_Separator);
		Set<String> usedTags=new HashSet<String>();
		for (String tagName : tagNames) {
			if (!tagName.equals(PreferenceConstants.MXMLUnmatchedTagsConstant))
				usedTags.add(tagName);
		}
		List<TagHolder> sortedList=new ArrayList<MXMLRearranger.TagHolder>();
		for (String tagName : tagNames) 
		{
			boolean isSpecOther=tagName.equals(PreferenceConstants.MXMLUnmatchedTagsConstant);
			//find all the items that match
			for (int i=0;i<topLevelTags.size();i++)
			{
				TagHolder tagHolder=topLevelTags.get(i);
				
				//if the tagname matches the current specification 
				//OR if the current spec is the "other" and the current tag doesn't match any in the list
				boolean tagMatches=false;
				if (!isSpecOther)
				{
					Set<String> testTag=new HashSet<String>();
					testTag.add(tagName);
					tagMatches=matchesRegEx(tagHolder.mTagName, testTag);
				}
				else
				{
					tagMatches=(!matchesRegEx(tagHolder.mTagName, usedTags));
				}
				
				if (tagMatches)
				{
					topLevelTags.remove(i);
					sortedList.add(tagHolder);
					i--;
				}
			}
		}
		sortedList.addAll(topLevelTags);
		
		//check for changes: if no changes, do nothing
		if (sortedList.size()!=unsortedList.size())
		{
			//error, just kick out
			System.out.println("Error performing mxml rearrange; tag count doesn't match");
			mInternalError="Internal error replacing text: tag count doesn't match";
			return false;
		}
		
		boolean differences=false;
		for (int i=0;i<sortedList.size();i++)
		{
			if (sortedList.get(i)!=unsortedList.get(i))
			{
				differences=true;
				break;
			}
		}
		if (!differences)
			return true; //succeeded, just nothing done
		
		//reconstruct document in the sorted order
		String source=mSourceDocument.get();
		StringBuffer newText=new StringBuffer();
		for (TagHolder tagHolder : sortedList) 
		{
			CommonToken startToken=tokens.get(tagHolder.mStartTokenIndex);
			CommonToken endToken=tokens.get(tagHolder.mEndTokenIndex);
			String data=source.substring(startToken.getStartIndex(), endToken.getStopIndex()+1);
			newText.append(data);
		}
		
		int startOffset=tokens.get(unsortedList.get(0).mStartTokenIndex).getStartIndex();
		int endOffset=tokens.get(unsortedList.get(unsortedList.size()-1).mEndTokenIndex).getStopIndex()+1;
		String oldData=mSourceDocument.get(startOffset, endOffset-startOffset);
		if (!ActionScriptFormatter.validateNonWhitespaceCharCounts(oldData, newText.toString()))
		{
			mInternalError="Internal error replacing text: new text doesn't match replaced text("+oldData+")!=("+newText.toString()+")";
			return false;
		}
		
		mSourceDocument.replace(startOffset, endOffset-startOffset, newText.toString());
		
		return true;
	}
	
	private void updateCommentTagNames(List<TagHolder> topLevelTags, String tagName) 
	{
		for (TagHolder tagHolder : topLevelTags) {
			if (tagHolder.mTagName.trim().length()==0)
				tagHolder.setTagName(tagName);
		}
	}

	private boolean matchesRegEx(String text, Set<String> tags)
	{
		if (tags.contains(text))
			return true;
		for (String tag : tags) {
			if (AttrGroup.isRegexString(tag))
			{
				if (Pattern.matches(tag, text))
					return true;
			}
		}
		
		return false;
	}
	
	private int findPreviousWhitespaceIndex(List<CommonToken> tokens, int startIndex) 
	{
		for (int i=startIndex;i>=0;i--)
		{
			CommonToken tok=tokens.get(i);
			if (tok.getText()!=null && tok.getText().trim().length()>0)
				return i+1;
		}
		return 0;
	}

	/**
	 * Search forward to find the next tag name and add it to the holder
	 * @param holder
	 * @param tokens
	 * @param tokenIndex
	 */
	private void addTagName(TagHolder holder, List<CommonToken> tokens, int tokenIndex) 
	{
		tokenIndex++;
		for (;tokenIndex<tokens.size(); tokenIndex++)
		{
			CommonToken tok=tokens.get(tokenIndex);
			if (tok.getType()==MXMLLexer.GENERIC_ID)
			{
				holder.setTagName(tok.getText());
				return;
			}
			else if (tok.getText()!=null && tok.getText().trim().length()>0)
			{
				//kick out if non whitespace hit; ideally, we shouldn't ever hit here
				return;
			}
		}
	}

	private void markTopLevelTagEnd(int tokenIndex, List<TagHolder> topTags)
	{
		TagHolder lastTag=topTags.get(topTags.size()-1);
		lastTag.setEndTokenIndex(tokenIndex);
	}

	private int findToken(int tokenIndex, List<CommonToken> tokens, int tagClose) 
	{
		tokenIndex++;
		while (tokenIndex<tokens.size())
		{
			CommonToken token=tokens.get(tokenIndex);
			System.out.println(token.getText());
			if (token.getType()==tagClose)
				return tokenIndex;
			tokenIndex++;
		}
		return tokenIndex;
	}
	
	static class TagHolder
	{
		private CommonToken mToken;
		private int mStartTokenIndex;
		private int mEndTokenIndex;
		private String mTagName;
		public TagHolder(CommonToken token, int tokenIndex)
		{
			mToken=token;
			mStartTokenIndex=tokenIndex;
			mTagName="";
		}
		
		public void setTagName(String text) {
			mTagName=text;
		}

		public void setEndTokenIndex(int tokenIndex)
		{
			mEndTokenIndex=tokenIndex;
		}
	}

	public boolean isSoftFailure() {
		return mIsSoftFailure;
	}

	public List<Exception> getErrors() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInternalError() {
		return mInternalError;
	}
}
