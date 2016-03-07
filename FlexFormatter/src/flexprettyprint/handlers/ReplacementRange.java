package flexprettyprint.handlers;

import org.eclipse.swt.graphics.Point;

public class ReplacementRange
{
	private Point mRangeInFormattedDoc;
	private Point mRangeInOriginalDoc;
	private String mAddedText;
	private String mDeletedText;
	public ReplacementRange(Point rangeInFormattedDoc, Point rangeInOrigDoc)
	{
		mRangeInOriginalDoc=rangeInOrigDoc;
		mRangeInFormattedDoc=rangeInFormattedDoc;
		mAddedText="";
		mDeletedText="";
	}
	public void setChangedText(String added, String removed)
	{
		if (added!=null)
			mAddedText=added;
		if (removed!=null)
			mDeletedText=removed;
	}
	
	public Point getRangeInFormattedDoc() {
		return mRangeInFormattedDoc;
	}
	public Point getRangeInOriginalDoc() {
		return mRangeInOriginalDoc;
	}
	public String getAddedText() {
		return mAddedText;
	}
	public String getDeletedText() {
		return mDeletedText;
	}
}
