package flexprettyprint.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

public class MXMLNamespaceCleaner {
	private IDocument mSource;
	private List<Exception> mParseErrors;

	public MXMLNamespaceCleaner(IDocument source) //String sourceData)
	{
		mSource=source;
	}
	
	public MXMLNamespaceCleaner(File f, String charsetName) throws IOException
	{
//		initialize();
		StringBuffer buffer=new StringBuffer();
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f), charsetName));
		try
		{
			while (true)
			{
				String line=br.readLine();
				if (line==null)
					break;
				buffer.append(line);
				buffer.append('\n');
			}
		}
		finally
		{
			br.close();
		}
		mSource=new Document(buffer.toString());
	}
	
	public boolean removeExtra() throws Exception
	{
		if (mSource.get().indexOf(ASPrettyPrinter.mIgnoreFileProcessing)>=0)
		{
			mParseErrors=new ArrayList<Exception>();
			mParseErrors.add(new Exception("File ignored: Ignore tag exists in file==> "+ASPrettyPrinter.mIgnoreFileProcessing));
			return false;
		}
	
		MXMLLexer lex = new MXMLLexer(new ANTLRStringStream(mSource.get()));
		StringBuffer buffer=new StringBuffer();
		lex.mDOCUMENT();
		List<NamespaceLocation> namespaces=new ArrayList<MXMLNamespaceCleaner.NamespaceLocation>();
		Set<String> usedNamespaces=new HashSet<String>();
		findExtraNamespaceItems(lex.getTokens(), namespaces, usedNamespaces);
		List<NamespaceLocation> unusedNS=new ArrayList<MXMLNamespaceCleaner.NamespaceLocation>();
		for (NamespaceLocation ns : namespaces) 
		{
			if (!usedNamespaces.contains(ns.getNamespace()) && !isAlwaysKept(ns.getNamespace()))
			{
				unusedNS.add(ns);
			}
		}
		
		//sort based on position
		Collections.sort(unusedNS, new Comparator<NamespaceLocation>() 
		{
			public int compare(NamespaceLocation arg0, NamespaceLocation arg1) 
			{
				return arg0.getStartPos()-arg1.getStartPos();
			}
		});

		buffer.append(mSource);
		for (int i=unusedNS.size()-1; i>=0; i--)
		{
			NamespaceLocation ns=unusedNS.get(i);
			//erase the item
			mSource.replace(ns.getStartPos(), ns.getEndPos()-ns.getStartPos(), "");
			
			//erase the previous line break if there is no other text on the line
			int prevLine=mSource.get().lastIndexOf(mSource.getLineDelimiter(0), ns.getStartPos()-1);
			int nextLine=mSource.get().indexOf(mSource.getLineDelimiter(0), ns.getStartPos());
			boolean nonWSFound=false;
			for (int k=prevLine;k<nextLine;k++)
			{
				char ch=mSource.getChar(k);
				if (!Character.isWhitespace(ch))
				{
					nonWSFound=true;
					break;
				}
			}
			if (!nonWSFound)
			{
				//delete the previous delimiter and the entire following line
				mSource.replace(prevLine, nextLine-prevLine, "");
			}
		}
		return true;
	}
	
	private boolean isAlwaysKept(String namespace)
	{
		if (namespace.equals("fx") || namespace.equals("mx") || namespace.equals("s") || namespace.equals("fb") )
			return true;
		return false;
	}

	private void findExtraNamespaceItems(List<CommonToken> tokens, List<NamespaceLocation> namespaces, Collection<String> usedNamespaces) 
	{
		for (int tokenIndex=0;tokenIndex<tokens.size();tokenIndex++)
		{
			Token token=tokens.get(tokenIndex);
			System.out.println(token.getText()+":"+token.getType());
			switch (token.getType())
			{
			case MXMLLexer.TAG_OPEN:
				tokenIndex=getAttrsAndNamespaces(tokens, tokenIndex, MXMLLexer.TAG_CLOSE, namespaces, usedNamespaces);
				break;
			case MXMLLexer.EMPTY_TAG_OPEN:
				tokenIndex=getAttrsAndNamespaces(tokens, tokenIndex, MXMLLexer.EMPTYTAG_CLOSE, namespaces, usedNamespaces);
				break;
			}
		}
	}

	private int getAttrsAndNamespaces(List<CommonToken> tokens, int startTokenIndex, int tagCloseType, List<NamespaceLocation> namespaces, Collection<String> usedNamespaces) 
	{
		String tagName=null;
		String currentAttrName=null;
		int namespaceStartPos=(-1);
		while (startTokenIndex<tokens.size())
		{
			CommonToken token=tokens.get(startTokenIndex);
			if (token.getType()==tagCloseType)
			{
				return startTokenIndex;
			}

			switch (token.getType())
			{
			case MXMLLexer.GENERIC_ID:
			case MXMLLexer.XML:
				//find name tag
				if (tagName==null)
				{
					tagName=token.getText();
					if (tagName.indexOf(':')>=0)
					{
						String namespace=tagName.substring(0, tagName.indexOf(':'));
						usedNamespaces.add(namespace);
					}
				}
				else
				{
					namespaceStartPos=token.getStartIndex();
					currentAttrName=token.getText();
					if (currentAttrName!=null && currentAttrName.indexOf(':')>=0) //find namespaces that are only on attribute names
					{
						String namespace=currentAttrName.substring(0, currentAttrName.indexOf(':'));
						if (!(namespace+":").equals(NamespacePrefix))
							usedNamespaces.add(namespace);
					}
				}
				break;
			case MXMLLexer.VALUE:
				//find name/value pairs
				if (currentAttrName!=null && currentAttrName.startsWith(NamespacePrefix))
				{
					String nsName=currentAttrName.substring(NamespacePrefix.length());
					NamespaceLocation nsLocation=new NamespaceLocation(nsName, namespaceStartPos, token.getStopIndex()+1);
					namespaces.add(nsLocation);
				}
				break;
			default:
			}
			startTokenIndex++;
		}		

		return startTokenIndex;
	}
	
	private static final String NamespacePrefix="xmlns:";
	private static class NamespaceLocation
	{
		String mNamespace; //this is the actual namespace (ex. "mx")
		int mStartPos; //start location in file
		int mEndPos; //end location in file
		public NamespaceLocation(String nameSpace, int start, int end)
		{
			mStartPos=start;
			mEndPos=end;
			mNamespace=nameSpace;
		}
		public String getNamespace() {
			return mNamespace;
		}
		public int getStartPos() {
			return mStartPos;
		}
		public int getEndPos() {
			return mEndPos;
		}
		
	}
	public List<Exception> getParseErrors() {
		return mParseErrors;
	}
}
