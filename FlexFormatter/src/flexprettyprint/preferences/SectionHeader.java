package flexprettyprint.preferences;

import java.util.ArrayList;
import java.util.List;

import flexasrearrangecodecommand.handlers.ASRearranger;
import flexprettyprintcommand.Activator;

public class SectionHeader {
	
	public static final int AS_Section_Style_SlashSlash = 1;
	public static final int AS_Section_Style_SlashStarblock = 2;
	public static final int AS_Section_Style_SlashStarLine = 3;
	
	private int mWidth;
	private String[] mText;
	private int mStyle;
	private int mExtraInternalLines;
	private String mFillChar;
	private int mLinesBefore;
	
	
	public SectionHeader(int style, int width, int contentLineCount, String fillChar, String[] text, int linesBefore)
	{
		mStyle=style;
		mWidth=width;
		mExtraInternalLines=contentLineCount;
		mText=text;
		mFillChar=fillChar;
		mLinesBefore=linesBefore;
	}

	public int getWidth() {
		return mWidth;
	}

	public String[] getText() {
		return mText;
	}

	public int getStyle() {
		return mStyle;
	}

	public int getExtraInternalLines() {
		return mExtraInternalLines;
	}

	public String getFillChar() {
		return mFillChar;
	}
	
	public int getLinesBefore() {
		return mLinesBefore;
	}

	public String[] getCommentLines()
	{
		List<String> results=new ArrayList<String>();
		int lines=getExtraInternalLines();
		String textIndent="";
		String[] textLines=getText();
		
		switch (getStyle())
		{
		case SectionHeader.AS_Section_Style_SlashSlash:
			results.add(emitLine(ASRearranger.SlashSlash, "", mFillChar, ""));
			for (int i=0;i<lines/2;i++)
				results.add(emitLine(ASRearranger.SlashSlash, "", "", ""));
			for (String text : textLines) {
				results.add(emitLine(ASRearranger.SlashSlash+textIndent, " ", "", text));	
			}
			for (int i=0;i<lines/2;i++)
				results.add(emitLine(ASRearranger.SlashSlash, "", "", ""));
				results.add(emitLine(ASRearranger.SlashSlash, "", mFillChar, ""));
			break;
		case SectionHeader.AS_Section_Style_SlashStarLine:
			results.add(emitLine(ASRearranger.SlashStar, ASRearranger.StarSlash, mFillChar, ""));
			for (int i=0;i<lines/2;i++)
				results.add(emitLine(ASRearranger.SlashStar, ASRearranger.StarSlash, " ", ""));
			for (String text : textLines) {
				results.add(emitLine(ASRearranger.SlashStar+textIndent, ASRearranger.StarSlash, " ", text));	
			}
			
			for (int i=0;i<lines/2;i++)
				results.add(emitLine(ASRearranger.SlashStar, ASRearranger.StarSlash, " ", ""));
				results.add(emitLine(ASRearranger.SlashStar, ASRearranger.StarSlash, mFillChar, ""));
			break;
//		case SectionHeader.AS_Section_Style_SlashStarblock:
//			emitLine(buffer, ASRearranger.SlashSlash, "", "-");
//			for (int i=0;i<lines.intValue()/2;i++)
//				emitLine(buffer, ASRearranger.SlashSlash, "", "", 0);
//			emitLine(buffer, ASRearranger.SlashSlash, "Sample header name", "", 3);
//			for (int i=0;i<lines.intValue()/2;i++)
//				emitLine(buffer, ASRearranger.SlashSlash, "", "", 0);
//			emitLine(buffer, ASRearranger.SlashSlash, "", "-");
//			break;
		}
		
		return results.toArray(new String[]{});
	}
	
	protected String emitLine(String prefix, String suffix, String repeatString, String content)
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(prefix);
		buffer.append(content);
		if (repeatString.length()>0)
		{
			for (int i=0;i<getWidth()-prefix.length()-suffix.length()-content.length();)
			{
				buffer.append(repeatString);
				i+=(repeatString.length());
			}
		}
		buffer.append(suffix);
		return buffer.toString();
	}
	
	private static final String Tag_style="style=";
	private static final String Tag_width="width=";
	private static final String Tag_blankLines="blankLines=";
	private static final String Tag_fillChar="fillChar=";
	private static final String Tag_text="text=";
	private static final String Tag_LinesBefore="linesBefore=";
	public String save()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append(Tag_style);
		buffer.append(Integer.toString(getStyle()));
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_width);
		buffer.append(Integer.toString(getWidth()));
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_blankLines);
		buffer.append(Integer.toString(getExtraInternalLines()));
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_fillChar);
		buffer.append(getFillChar());
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_text);
		StringBuffer text=new StringBuffer();
		for (int i = 0; i < getText().length; i++) {
			String line = getText()[i];
			line=line.replace(AttrGroup.TagSplitter, AttrGroup.SplitterEscape);
			buffer.append(line);
			if (i+1<getText().length)
				buffer.append('\n');
		}
		buffer.append(text.toString());
		buffer.append(AttrGroup.TagSplitter);
		
		buffer.append(Tag_LinesBefore);
		buffer.append(Integer.toString(getLinesBefore()));
		buffer.append(AttrGroup.TagSplitter);

		return buffer.toString();
	}
	
	public static SectionHeader load(String data)
	{
		String styleString=AttrGroup.getValue(data, Tag_style);
		int style=AS_Section_Style_SlashSlash;
		if (styleString!=null)
		{
			try
			{
				style=Integer.parseInt(styleString);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		String widthString=AttrGroup.getValue(data, Tag_width);
		int width=60;
		if (widthString!=null)
		{
			try
			{
				width=Integer.parseInt(widthString);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		String heightString=AttrGroup.getValue(data, Tag_blankLines);
		int height=5;
		if (heightString!=null)
		{
			try
			{
				height=Integer.parseInt(heightString);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		String fillChar=AttrGroup.getValue(data, Tag_fillChar);
		if (fillChar==null)
			fillChar="-"; //this seems like the best default
		
		List<String> lines=new ArrayList<String>();
		String text=AttrGroup.getValue(data, Tag_text);
		if (text!=null)
		{
			String[] tempLines=text.split("\n");
			for (int i = 0; i < tempLines.length; i++) {
				String line = tempLines[i];
				lines.add(line);
			}
		}
		
		String linesString=AttrGroup.getValue(data, Tag_LinesBefore);
		int linesBefore=1;
		if (linesString!=null)
		{
			try
			{
				linesBefore=Integer.parseInt(linesString);
			}
			catch (NumberFormatException e)
			{
				Activator.logException(e, null);
			}
		}
		
		return new SectionHeader(style, width, height, fillChar, lines.toArray(new String[]{}), linesBefore);
	}

	@Override
	public String toString() {
		return save();
	}
	
	public String generateHeader(String lineDelim, String indent)
	{
		String[] output=getCommentLines();
		StringBuffer buffer=new StringBuffer();
		for (int i = 0; i < output.length; i++) {
			String line = output[i];
			buffer.append(indent);
			buffer.append(line);
			if (i+1<output.length)
				buffer.append(lineDelim);
		}
		return buffer.toString();
	}
}
