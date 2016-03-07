package flexprettyprint.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import flexprettyprintcommand.Activator;

public class SectionSpec
{
	public static final int MAJOR = 1;
	public static final int MINOR = 2;
	
	private boolean mUseHeader;
	private int mSectionType;
	private String mID;
	private String[] mText;
	private String mEndSpanSectionID;
	
	public SectionSpec(String sectionID, int sectionType, String[] sectionText, boolean useHeader)
	{
		mUseHeader=useHeader;
		mSectionType=sectionType;
		mID=sectionID;
		mText=sectionText;
		mEndSpanSectionID=mID;
	}

	public boolean isUseHeader() {
		return mUseHeader;
	}

	public int getSectionType() {
		return mSectionType;
	}

	public String getID() {
		return mID;
	}

	public String[] getText() {
		return mText;
	}
	
	private static final String Tag_type="type=";
	private static final String Tag_id="id=";
	private static final String Tag_endID="endID=";
	private static final String Tag_enabled="enabled=";
	private static final String Tag_text="text=";
	private static final String NewlineEscape="\\n";
	public String save()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(Tag_type);
		buffer.append(Integer.toString(getSectionType()));
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_id);
		buffer.append(getID());
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_enabled);
		buffer.append(Boolean.toString(isUseHeader()));
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_text);
		StringBuffer text=new StringBuffer();
		for (int i = 0; i < getText().length; i++) {
			String line = getText()[i];
			line=line.replace(AttrGroup.TagSplitter, AttrGroup.SplitterEscape);
			buffer.append(line);
			if (i+1<getText().length)
				buffer.append(NewlineEscape);
		}
		buffer.append(text.toString());
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_endID);
		buffer.append(getEndSpanSectionID());
		buffer.append(AttrGroup.TagSplitter);
		
		return buffer.toString();
	}
	
	public static SectionSpec load(String data)
	{
		String typeString=AttrGroup.getValue(data, Tag_type);
		int type=MAJOR;
		if (typeString!=null)
		{
			try
			{
				type=Integer.parseInt(typeString);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		String enabledString=AttrGroup.getValue(data, Tag_enabled);
		boolean enabled=false;
		if (enabledString!=null)
		{
			try
			{
				enabled=Boolean.parseBoolean(enabledString);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		String id=AttrGroup.getValue(data, Tag_id);
		if (id==null)
			id="";
		
		String text=AttrGroup.getValue(data, Tag_text);
		String[] tempLines=text.split(NewlineEscape);
		List<String> lines=new ArrayList<String>();
		for (int i = 0; i < tempLines.length; i++) {
			String line = tempLines[i];
			lines.add(line);
		}
		
		String endID=AttrGroup.getValue(data, Tag_endID);
		if (endID==null)
			endID=id;
		
		SectionSpec spec=new SectionSpec(id, type, lines.toArray(new String[]{}), enabled);
		spec.setEndSpanSectionID(endID);
		return spec;
	}

	@Override
	public String toString() {
		return save();
	}
	
	public String generateHeader(Map<Integer, SectionHeader> baseHeaders, String lineDelim, String indent)
	{
		SectionHeader baseHeader=baseHeaders.get(getSectionType());
		if (baseHeader==null)
			return "";
		SectionHeader header=new SectionHeader(baseHeader.getStyle(), baseHeader.getWidth(), baseHeader.getExtraInternalLines(), baseHeader.getFillChar(), getText(), baseHeader.getLinesBefore());
		String[] output=header.getCommentLines();
		StringBuffer buffer=new StringBuffer();
		for (int i=0;i<baseHeader.getLinesBefore();i++)
			buffer.append(lineDelim);
		for (int i = 0; i < output.length; i++) {
			String line = output[i];
			buffer.append(indent);
			buffer.append(line);
			if (i+1<output.length)
				buffer.append(lineDelim);
		}
		return buffer.toString();
	}

	public String getEndSpanSectionID() {
		return mEndSpanSectionID;
	}

	
	/**
	 * @param endSpanSectionID the sectionID of the end of this span.
	 */
	public void setEndSpanSectionID(String endSpanSectionID) {
		mEndSpanSectionID = endSpanSectionID;
	}

	public String getContentPrintString()
	{
		StringBuffer buffer=new StringBuffer();
		for (String text : getText()) {
			buffer.append(text.trim());
		}
		return buffer.toString();
	}
}
