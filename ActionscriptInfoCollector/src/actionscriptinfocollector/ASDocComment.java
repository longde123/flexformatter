package actionscriptinfocollector;

import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public class ASDocComment extends SourceItem
{
	protected String mText; 
	public ASDocComment(Token t)
	{
		super();
		mText=t.getText();
		capturePositions(t);
	}
	public String getText() {
		return mText;
	}
	
	@Override
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(mText);
		buffer.append("Start: "+getStartPos()+" End: "+getEndPos()+"\n");
		return buffer.toString();
	}
	
	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		String myText=getText();
		String docText=doc.get();
		String docSub=doc.get(getStartPos(), getEndPos()-getStartPos());
		if (!getText().equals(doc.get(getStartPos(), getEndPos()-getStartPos())))
		{
			return false;
		}
		
		return super.validateText(doc);
	}
	
}
