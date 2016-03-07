package flexprettyprint.preferences;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import actionscriptinfocollector.SourceItem;

public class NewSectionHeaderItem extends SourceItem {
	private SectionSpec mSpec;
	private SectionHeader mHeader;
	private int mCRCount=0;
	public NewSectionHeaderItem(SectionSpec spec)
	{
		mSpec=spec;
	}
	public NewSectionHeaderItem(SectionHeader header)
	{
		mHeader=header;
	}
	public NewSectionHeaderItem(int crs)
	{
		mCRCount=crs;
	}
	
	@Override
	public void nailDownPositions() {
		//do nothing.  This is new data
	}
	@Override
	public boolean validateText(IDocument doc) throws BadLocationException {
		//do nothing.  This is new data
		return true;
	}
	@Override
	public int getStartPos() {
		return 0;
	}
	
	public String getText(Map<Integer, SectionHeader> baseHeaders, String lineDelim, String indent)
	{
		if (mSpec!=null)
		{
			return mSpec.generateHeader(baseHeaders, lineDelim, indent);
		}
		else if (mHeader!=null)
		{
			return mHeader.generateHeader(lineDelim, indent);
		}
		else
		{
			StringBuffer buffer=new StringBuffer();
			for (int i=0;i<mCRCount;i++)
			{
				buffer.append(lineDelim);
			}
			return buffer.toString();
		}
	}
	
	public boolean isWhitespace()
	{
		if (mHeader==null && mSpec==null)
			return true;
		
		return false;
	}
	
	public boolean isCopyright()
	{
		return mHeader!=null;
	}
}
