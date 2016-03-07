package actionscriptinfocollector;

import org.antlr.runtime.Token;

public class SimpleTextItem extends TextItem {
	protected String mText; 
	public SimpleTextItem(Token t)
	{
		mText=t.getText();
		capturePositions(t);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(mText);
		buffer.append("("+getStartPos()+","+getEndPos()+")");
		return buffer.toString();
	}
	public String getText() {
		return mText;
	}
	
}
