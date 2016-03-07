package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;


public class SourceItem implements Comparable<SourceItem>
{
	protected int mStartPos;
	protected int mEndPos;
	public static final String PositionCategory="ASCollectorCategory";
	private Position mTrackingStartPos;
	private List<TextItem> mHiddenTokens;
	private List<TextItem> mPostHiddenTokens;
	private int mBlanksAfter=0;
	private int mBlanksBefore=0;
//	protected ASDocComment mCurrentASDoc;
	private List<ObjectPositionHolder> mAttachedObjects;
	public SourceItem()
	{
		mStartPos=(-1);
		mEndPos=(-1);
		mTrackingStartPos=null;
		mHiddenTokens=new ArrayList<TextItem>();
		mPostHiddenTokens=new ArrayList<TextItem>();
		mAttachedObjects=new ArrayList<ObjectPositionHolder>();
	}
	
	public void capturePositions(Token t)
	{
		captureStartPos(t);
		captureEndPos(t);
	}
	
	public void capturePositions(ParserRuleReturnScope t)
	{
		captureStartPos(t);
		captureEndPos(t);
	}
	
	public void captureStartPos(Token t)
	{
		if (mStartPos<0)
			mStartPos=((CommonToken)t).getStartIndex();
	}
	
	public void captureStartPos(ParserRuleReturnScope t)
	{
		if (mStartPos<0)
			mStartPos=AntlrUtilities.getFirstTreePosition(t);
	}
	
	public void captureEndPos(Token t)
	{
		if (mEndPos<0)
			mEndPos=((CommonToken)t).getStopIndex()+1;
	}
	
	public void captureEndPos(ParserRuleReturnScope t)
	{
		if (mEndPos<0)
			mEndPos=AntlrUtilities.getLastTreePosition(t);
	}
	
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException
	{
		if (mStartPos<0 || mEndPos<0)
		{
			System.out.println("Internal error");
			return;
		}
		
		mTrackingStartPos=new Position(mStartPos, mEndPos-mStartPos);
		try {
			doc.addPositionCategory(PositionCategory);
			doc.addPosition(PositionCategory, mTrackingStartPos);
		} catch (BadLocationException e)
		{
			String wholeText=doc.get();
			e.printStackTrace();
			throw e;
		} catch (BadPositionCategoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		for (TextItem item : mHiddenTokens) {
			item.applyDocument(doc);
		}
		
		for (TextItem item : mPostHiddenTokens) {
			item.applyDocument(doc);
		}
		
		String allText=doc.get();
		//find previous blank lines
		for (int i=getPreStartPos()-1;i>=0;i--)
		{
			try {
				char c=doc.getChar(i);
				if (!AntlrUtilities.isASWhitespace(c))
				{
					int startLine=doc.getLineOfOffset(getPreStartPos());
					int previousTextLine=doc.getLineOfOffset(i);
					mBlanksBefore=Math.max(0, startLine-previousTextLine);
					break;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException
	{
		mStartPos+=delta;
		mEndPos+=delta;
		for (TextItem item : mHiddenTokens) {
			item.resetPositions(delta, doc);
		}
		for (TextItem item : mPostHiddenTokens) {
			item.resetPositions(delta, doc);
		}
		applyDocument(doc);
	}
	
//	public final void resetPositions(int startPos, int endPos, int preStartPos, IDocument doc)
//	{
//		mStartPos=startPos;
//		mEndPos=endPos;
//		int workingPos=preStartPos;
//		for (TextItem item : mHiddenTokens) {
//			item.resetPositions(workingPos, workingPos+item.getText().length(), workingPos, doc);
//			workingPos+=item.getText().length();
//		}
//		applyDocument(doc);
//	}
	
	public int getStartPos() {
		if (mTrackingStartPos!=null)
			return mTrackingStartPos.getOffset();
		return mStartPos;
	}
	public void setStartPos(int startPos) {
		mStartPos = startPos;
	}
	public int getEndPos() {
		if (mTrackingStartPos!=null)
			return mTrackingStartPos.getOffset()+mTrackingStartPos.getLength();
		return mEndPos;
	}
	public void setEndPos(int endPos) {
		mEndPos = endPos;
	}
//	public ASDocComment getCurrentASDoc() {
//		return mCurrentASDoc;
//	}
//	public void setCurrentASDoc(ASDocComment currentASDoc) {
//		mCurrentASDoc = currentASDoc;
//	}

	public int compareTo(SourceItem other)
	{
//		if (other instanceof SourceItem)
//		{
			return getStartPos()-((SourceItem)other).getStartPos();
//		}
//		return 0;
	}
	
	public void clearPreTokens()
	{
		mHiddenTokens.clear();
	}
	
	public List<TextItem> getPreTokens()
	{
		return mHiddenTokens;
	}
	
	public List<TextItem> getPostTokens()
	{
		return mPostHiddenTokens;
	}
	
	public void addPreTokens(List<Token> hiddenTokens)
	{
		for (Token token : hiddenTokens) {
			TextItem t=new SimpleTextItem(token);
			mHiddenTokens.add(t);
		}
	}
	
	public void addPostTokens(List<Token> hiddenTokens)
	{
		for (Token token : hiddenTokens) {
			TextItem t=new SimpleTextItem(token);
			mPostHiddenTokens.add(t);
		}
	}
	
	public int getPreStartPos()
	{
		if (mHiddenTokens.size()>0)
		{
			return mHiddenTokens.get(0).getStartPos();
		}
		
		return getStartPos();
	}

	public int getPostEndPos()
	{
		if (mPostHiddenTokens.size()>0)
		{
			return mPostHiddenTokens.get(mPostHiddenTokens.size()-1).getEndPos();
		}
		
		return getEndPos();
	}
	
	
	public void nailDownPositions()
	{
		mStartPos=mTrackingStartPos.getOffset();
		mEndPos=mStartPos+mTrackingStartPos.getLength();
		for (TextItem item : mHiddenTokens) {
			item.nailDownPositions();
		}
		for (TextItem item : mPostHiddenTokens) {
			item.nailDownPositions();
		}
	}
	
	public int getNailedDownStartPos()
	{
		if (mHiddenTokens.size()>0)
			return mHiddenTokens.get(0).getNailedDownStartPos();
		return mStartPos;
	}
	
	public int getBlanksAfter() {
		return mBlanksAfter;
	}

	public void setBlanksAfter(int blanksAfter) {
		mBlanksAfter = blanksAfter;
	}

	public int getBlanksBefore() {
		return mBlanksBefore;
	}

	public void setBlanksBefore(int blanksBefore) {
		mBlanksBefore = blanksBefore;
	}
	
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		for (TextItem item : mHiddenTokens)
		{
			if (!item.validateText(doc))
			{
				return false;
			}
		}

		for (TextItem item : mPostHiddenTokens)
		{
			if (!item.validateText(doc))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void trimLeadingWhitespaceTokens()
	{
		int i=0;
		while (i<mHiddenTokens.size())
		{
			if (AntlrUtilities.asTrim(mHiddenTokens.get(i).getText()).length()==0)
			{
				mHiddenTokens.remove(i);
				continue;
			}
			i++;
		}
	}
	
	protected void addAttachedObject(Object o, int linesOffset)
	{
		mAttachedObjects.add(new ObjectPositionHolder(o, linesOffset, this));
	}
	
	public List<ObjectPositionHolder> getAttachedObjects()
	{
		return mAttachedObjects;
	}
	
	public boolean captureObject(IDocument doc, Object o, int lineNumber) throws BadLocationException
	{
		int textPos=doc.getLineOffset(lineNumber);
		if (getPreStartPos()<=textPos && getPostEndPos()>textPos)
		{
			//store position relative to startpos
			int startLine=doc.getLineOfOffset(getStartPos());
			addAttachedObject(o, lineNumber-startLine);
			return true;
		}
		return false;
	}
	
	public void removePreTokens(int start, int end)
	{
		if (start>=0 && end<=mHiddenTokens.size())
		{
			int charCount=0;
			for (int i=start;i<end;i++)
			{
				charCount+=mHiddenTokens.get(start).getText().length();
				mHiddenTokens.remove(start);
			}
			//increment the positions of earlier items so that we remove the gap caused by the removal of the tokens.
			for (int i=0;i<start;i++)
			{
				mHiddenTokens.get(i).mStartPos+=charCount;
				mHiddenTokens.get(i).mEndPos+=charCount;
			}
		}
	}
	
	/**
	 * Remove leading carriage returns from the hidden tokens, down to the last line where the token starts.  Only remove
	 * whitespace tokens, so stop if we hit a comment
	 */
	public void trimLeadingLines()
	{
		//find first item that is non-whitespace, if any
		int foundNonWS=mHiddenTokens.size()-1;
		for (int i=0;i<mHiddenTokens.size();i++)
		{
			TextItem item=mHiddenTokens.get(i);
			if (AntlrUtilities.asTrim(item.getText()).length()>0)
			{
				foundNonWS=i-1;
				break;
			}
		}
		
		//I either found some non-whitespace, or I'm at the end of the hidden tokens.  
		//Walk backward until I find a carriage return token, and remove it and all previous tokens.  If no
		//CR token, then do nothing.
		while (foundNonWS>=0)
		{
			TextItem item=mHiddenTokens.get(foundNonWS);
			if (item.getText().contains("\r") || item.getText().contains("\n"))
			{
				mHiddenTokens=mHiddenTokens.subList(foundNonWS+1, mHiddenTokens.size());
				break;
			}
			foundNonWS--;
		}
	}
	
	public int getEarliestRelatedPos()
	{
		return getPreStartPos();
	}
}
