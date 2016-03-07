package actionscriptinfocollector;

import org.antlr.runtime.ParserRuleReturnScope;

public class TreeTextItem extends TextItem {
	protected String mText; 
	public TreeTextItem(ParserRuleReturnScope t)
	{
		mText=AntlrUtilities.getTreeText(t);
		setStartPos(AntlrUtilities.getFirstTreePosition(t));
		setEndPos(AntlrUtilities.getLastTreePosition(t));
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
