package flexprettyprint.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import actionscriptinfocollector.AntlrUtilities;

import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprintcommand.Activator;

public class AttrGroup
{
	private int mSortMode;
	private List<String> mAttrs;
	private String mName;
	private int mWrapMode;
	private Set<String> mRegexAttrs;
	private int mData; //depends on wrap mode.  
	private boolean mIncludeStates;
	
	public static final int Wrap_Data_Use_Default=(-1);
	
	public AttrGroup(String name, List<String> attrs, int sortMode, int wrapMode, boolean includeStates)
	{
		mName=name;
		mAttrs=attrs;
		mSortMode=sortMode;
		mWrapMode=wrapMode;
		mRegexAttrs=null;
		mIncludeStates=includeStates;
		mData=Wrap_Data_Use_Default;
	}

	public int getWrapMode() {
		return mWrapMode;
	}

	public void setWrapMode(int wrapMode) {
		mWrapMode = wrapMode;
	}

	public String getName()
	{
		return mName;
	}

	public int getSortMode() {
		return mSortMode;
	}

	public void setSortMode(int sortMode) {
		mSortMode = sortMode;
	}

	public List<String> getAttrs() {
		return mAttrs;
	}

	public void setName(String name) {
		mName=name;
	}
	
	public boolean isIncludeStates() {
		return mIncludeStates;
	}

	public void setIncludeStates(boolean includeStates) {
		mIncludeStates = includeStates;
	}

	public AttrGroup copy()
	{
		List<String> attrs=new ArrayList<String>();
		attrs.addAll(getAttrs());
		AttrGroup group=new AttrGroup(getName(), attrs, getSortMode(), getWrapMode(), isIncludeStates());
		group.setData(getData());
		return group;
	}
	
	private static final String Tag_name="name=";
	private static final String Tag_sort="sort=";
	private static final String Tag_includeStates="includeStates=";
	private static final String Tag_wrap="wrap=";
	private static final String Tag_attrs="attrs=";
	private static final String Tag_data="data=";
	public static final String TagSplitter="|";
	public static final String SplitterEscape="char(Splitter)";
	public String save()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(Tag_name);
		buffer.append(getName().replace(TagSplitter, SplitterEscape));
		buffer.append(TagSplitter);
		
		buffer.append(Tag_sort);
		buffer.append(Integer.toString(getSortMode()));
		buffer.append(TagSplitter);
		
		buffer.append(Tag_includeStates);
		buffer.append(Boolean.toString(isIncludeStates()));
		buffer.append(TagSplitter);
		
		buffer.append(Tag_wrap);
		buffer.append(Integer.toString(getWrapMode()));
		buffer.append(TagSplitter);
		
		buffer.append(Tag_attrs);
		for (String attr : getAttrs()) {
			buffer.append(attr.replace(TagSplitter, SplitterEscape));
			buffer.append(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		}
		buffer.append(TagSplitter);
		
		buffer.append(Tag_data);
		buffer.append(Integer.toString(mData));
		buffer.append(TagSplitter);
		
		return buffer.toString();
	}
	
	public static String getValue(String source, String tagName)
	{
		int index=source.indexOf(tagName);
		int endIndex=source.indexOf(TagSplitter, index);
		if (index<0 || endIndex<0)
			return null;
		
		String value=source.substring(index+tagName.length(), endIndex);
		value=value.replace(SplitterEscape, TagSplitter);
		return value;
	}
	
	public static AttrGroup load(String data)
	{
		List<String> attrs=new ArrayList<String>();
		boolean includeStates=true;
		int sortMode=MXMLPrettyPrinter.MXML_Sort_AscByCase;
		int wrapMode=MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT;
		String name=getValue(data, Tag_name);
		if (name==null)
			return null;
		
		String num=getValue(data, Tag_sort);
		if (num!=null)
		{
			try
			{
				sortMode=Integer.parseInt(num);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		num=getValue(data, Tag_wrap);
		if (num!=null)
		{
			try
			{
				wrapMode=Integer.parseInt(num);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		int wrapData=Wrap_Data_Use_Default;
		num=getValue(data, Tag_data);
		if (num!=null)
		{
			try
			{
				wrapData=Integer.parseInt(num);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		String attrString=getValue(data, Tag_attrs);
		if (attrString!=null)
		{
			String atts[]=attrString.split(AttrOrderConfigDialog.Attr_Grouping_Splitter);
			for (String attr : atts) {
				attr=AntlrUtilities.asTrim(attr);
				if (attr.length()>0)
					attrs.add(attr);
			}
		}
		
		String includeStatesData=getValue(data, Tag_includeStates);
		if (includeStatesData!=null)
		{
			includeStates=Boolean.parseBoolean(includeStatesData);
		}
		
		AttrGroup group=new AttrGroup(name, attrs, sortMode, wrapMode, includeStates);
		group.setData(wrapData);
		return group;
	}
	
	private void cacheRegexAttrs()
	{
		if (mRegexAttrs!=null)
			return;
		
		mRegexAttrs=new HashSet<String>();
		for (String attr : mAttrs)
		{
			if (isRegexString(attr))
			{
				mRegexAttrs.add(attr);
			}
			if (isIncludeStates())
			{
				mRegexAttrs.add(attr+MXMLPrettyPrinter.StateRegexSuffix);
			}
		}
	}
	
	public Set<String> getRegexAttrs()
	{
		cacheRegexAttrs();
		return mRegexAttrs;
	}
	
	public boolean isRegexAttr(String attr)
	{
		cacheRegexAttrs();
		return mRegexAttrs.contains(attr);
	}
	
	public static boolean isRegexString(String str)
	{
		for (int i=0;i<str.length();i++)
		{
			char c=str.charAt(i);
			if (Character.isJavaIdentifierPart(c))
				continue;
			
			if (c==':' || c=='_' || c=='-')
				continue;
			
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(getName());
		buffer.append('(');
		for (String attr : getAttrs()) {
			buffer.append(attr);
			buffer.append(',');
		}
		buffer.append(')');
		return buffer.toString();
	}

	public int getData() {
		return mData;
	}

	public void setData(int data) {
		mData = data;
	}
	
}
