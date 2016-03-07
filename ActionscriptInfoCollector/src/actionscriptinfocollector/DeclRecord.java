package actionscriptinfocollector;

import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class DeclRecord extends SourceItem {
	private TextItem mName;
	private TextItem mType;
	public DeclRecord(Token name, ParserRuleReturnScope type)
	{
		captureStartPos(name);
		if (type!=null)
			captureEndPos(type);
		else
			captureEndPos(name);
		mName=new SimpleTextItem(name);
		if (type!=null)
			mType=new TreeTextItem(type);
	}
	public DeclRecord(ParserRuleReturnScope name, ParserRuleReturnScope type)
	{
		captureStartPos(name);
		if (type!=null)
			captureEndPos(type);
		else
			captureEndPos(name);
		mName=new TreeTextItem(name);
		if (type!=null)
			mType=new TreeTextItem(type);
	}
	public TextItem getName() {
		return mName;
	}
	public TextItem getType() {
		return mType;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("Name="+getName()+", Type="+getType()+"\n");
		return buffer.toString();
	}
	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		super.applyDocument(doc);
		if (mName!=null)
			mName.applyDocument(doc);
		
		if (mType!=null)
			mType.applyDocument(doc);
	}
	@Override
	public void nailDownPositions() {
		mName.nailDownPositions();
		if (mType!=null)
			mType.nailDownPositions();
		super.nailDownPositions();
	}
	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		
		mName.resetPositions(delta, doc);
		if (mType!=null)
			mType.resetPositions(delta, doc);
		super.resetPositions(delta, doc);
	}
	
	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		if (mType!=null && !mType.validateText(doc))
			return false;
		
		if (mName!=null && !mName.validateText(doc))
			return false;
		
		return super.validateText(doc);
	}
	
	
}
