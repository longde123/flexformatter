package actionscriptinfocollector;

import org.antlr.runtime.ParserRuleReturnScope;

public class IncludeItem extends SourceItem implements ISourceElement
{
	private String mIncludeFile;
	public IncludeItem()
	{
		mIncludeFile="";
	}
	public String getIncludeFile() {
		return mIncludeFile;
	}
	public void setIncludeFile(ParserRuleReturnScope t) {
		mIncludeFile = AntlrUtilities.getTreeText(t);
	}
}
