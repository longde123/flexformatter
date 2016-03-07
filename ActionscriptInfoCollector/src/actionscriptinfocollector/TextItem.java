package actionscriptinfocollector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

public abstract class TextItem extends SourceItem 
{
	public abstract String getText();

	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		String testText=doc.get(getStartPos(), getEndPos()-getStartPos());
		if (!equalsIgnoringWhitespace(getText(), testText))
		{
			String docData=doc.get();
			System.out.println(docData);
			return false;
		}
		
		return super.validateText(doc);
	}
	
	public static boolean equalsIgnoringWhitespace(String t1, String t2)
	{
		StringBuffer s1=new StringBuffer();
		for (int i=0;i<t1.length();i++)
		{
			char c=t1.charAt(i);
			if (!AntlrUtilities.isASWhitespace(c))
			{
				s1.append(c);
			}
		}
		StringBuffer s2=new StringBuffer();
		for (int i=0;i<t2.length();i++)
		{
			char c=t2.charAt(i);
			if (!AntlrUtilities.isASWhitespace(c))
			{
				s2.append(c);
			}
		}
		return s1.toString().equals(s2.toString());
	}
}
