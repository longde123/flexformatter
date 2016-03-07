package actionscriptinfocollector;

public class ObjectPositionHolder {
	private Object mObject;
	private int mLinesOffset;
	private SourceItem mAssociatedItem;
	public ObjectPositionHolder(Object obj, int linesOffset, SourceItem item)
	{
		mObject=obj;
		mLinesOffset=linesOffset;
		mAssociatedItem=item;
	}
	public Object getObject() {
		return mObject;
	}
	public int getLinesOffset() {
		return mLinesOffset;
	}
	public SourceItem getAssociatedItem() {
		return mAssociatedItem;
	}
	
}
