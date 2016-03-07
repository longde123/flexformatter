package actionscriptinfocollector;

import org.antlr.runtime.ParserRuleReturnScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class ImportRecord extends TopLevelItemRecord
{
	private TextItem mType;
	private boolean mWildcard;
	public ImportRecord()
	{
		mWildcard=false;
	}
	
	public void setType(ParserRuleReturnScope start, boolean isWildcard)
	{
		mType=new TreeTextItem(start);
		mWildcard=isWildcard;
	}

	public TextItem getType() {
		return mType;
	}

	public boolean isWildcard() {
		return mWildcard;
	}

	@Override
	public void nailDownPositions() {
		if (mType!=null)
			mType.nailDownPositions();
		super.nailDownPositions();
	}

	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		if (mType!=null)
			mType.resetPositions(delta, doc);
		super.resetPositions(delta, doc);
	}

	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		if (mType!=null)
			mType.applyDocument(doc);
		super.applyDocument(doc);
	}

	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		if (mType!=null && !mType.validateText(doc))
			return false;
		return super.validateText(doc);
	}

	@Override
	public String toString()
	{
		return mType+(mWildcard ? ".*" : "");
	}

	
}
