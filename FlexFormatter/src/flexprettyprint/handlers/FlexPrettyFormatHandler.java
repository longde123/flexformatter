package flexprettyprint.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.NoViableAltException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import utilities.FormatUtility;
import actionscriptinfocollector.AntlrUtilities;
import flexasrearrangecodecommand.handlers.ASRearranger;
import flexasrearrangecodecommand.handlers.MXMLRearranger;
import flexasrearrangecodecommand.preferences.PreferenceConstants;
import flexprettyprint.preferences.Initializer;
import flexprettyprint.preferences.PrefPage;
import flexprettyprint.preferences.ProjectProperties;
import flexprettyprintcommand.Activator;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class FlexPrettyFormatHandler extends AbstractHandler {
	
	public static final int Const_Format=1;
	public static final int Const_Indent=2;
	public static final int Const_Rearrange=3;
	public static final int Const_RemoveNamespaces=4;
	
	
	/**
	 * The constructor.
	 */
	public FlexPrettyFormatHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		doFormat();
		return null;
	}
		
	/**
	 * @param editorPart
	 * @param processingStyle @see Const_Indent,Const_Format,Const_Rearrange
	 * @param alterSelectedLines if true, use the current selection to perform operation, if there is one
	 * @param batchMode if true, this is a non-interactive call, so don't throw up error dialogs
	 * @return true if successful, false otherwise.  No detail information is captured about problems; maybe in the future
	 */
	public static boolean doFormat(IEditorPart editorPart, int processingStyle, boolean alterSelectedLines, boolean batchMode)
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		String inputName=editorPart.getEditorInput().getName();
		
		if (editorPart.getEditorInput() instanceof IFileEditorInput)
		{
			IFileEditorInput fileInput = (IFileEditorInput) editorPart.getEditorInput();
			IProject currentProject = fileInput.getFile().getProject();
			store=ProjectProperties.getProjectFormatterPreferences(currentProject, false);
		}
		
		ISourceViewer viewer=(ISourceViewer)editorPart.getAdapter(ITextOperationTarget.class);
		IRewriteTarget rewriteTarget=null;
//		DocumentRewriteSession rewriteSession= null;		
		IDocument document=viewer.getDocument();
		Point resultSelection=null;
		Position cursorPos=null;
		if (viewer instanceof ITextViewerExtension)
		{
			rewriteTarget=((ITextViewerExtension)viewer).getRewriteTarget();
			rewriteTarget.setRedraw(false);
			rewriteTarget.beginCompoundChange();
		}
		
		Set<String> xmlExtensions=Initializer.getSet(store.getString(Initializer.Pref_MXML_AdditionalExtensions), true);
		StyledText styled=viewer.getTextWidget();
		int linesFromTop=0;
		int cursorLine=0;
		try
		{
			int tabSize=styled.getTabs();
			String fileData=document.get();
			if (fileData.indexOf(ASPrettyPrinter.mIgnoreFileProcessing)>=0)
			{
				if (!batchMode)
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "File not formatted", "File not formatted because it contains the FlexFormatter 'ignore' comment marker.");
				return true; //this isn't really an error, so we return true
			}
			
//			String otherData=styled.getText();
			boolean selectionExists=styled.getSelectionCount()>0;
	
			cursorLine=document.getLineOfOffset(styled.getSelection().x);
			linesFromTop=Math.max(0, cursorLine-styled.getTopIndex());
			try
			{
				cursorPos=new Position(styled.getSelection().x);
				document.addPosition(cursorPos);
			}
			catch (BadLocationException e)
			{
				cursorPos=null;
				Activator.logException(e, null);
			}
			
			String extension="";
			int lastDot=inputName.lastIndexOf('.');
			if (lastDot>=0)
				extension=inputName.substring(lastDot+1).toLowerCase();
			if (extension.equals("mxml") || xmlExtensions.contains(extension))
			{
				MXMLPrettyPrinter printer=new MXMLPrettyPrinter(fileData);
				printer.setDoFormat(processingStyle==Const_Format);
				printer.setPlainXML(!extension.equals("mxml"));
				
				if (processingStyle==Const_Format && alterSelectedLines && selectionExists)
				{
					//determine selection
					Point selRange=styled.getSelectionRange();
					if (selRange.x==0 && selRange.y==document.getLength())
						alterSelectedLines=false;
					int startLine=document.getLineOfOffset(selRange.x);
					int endLine=document.getLineOfOffset(selRange.x+selRange.y);
					printer.setSelectedRange(new Point(startLine+1, endLine+1));
				}
		
				FormatUtility.configureMXMLPrinter(printer, store, tabSize);
				
				//rearrange MXML if appropriate
				if ((printer.isDoFormat() && store.getBoolean(PreferenceConstants.MXMLRearr_RearrangeWhileFormatting) && printer.getSelectedRange()==null) || processingStyle==Const_Rearrange)
				{
					MXMLRearranger rearranger=new MXMLRearranger(store);
					List<MarkerAnnotation> lineBasedAnnotations=new ArrayList<MarkerAnnotation>();
					try
					{
						Iterator it=viewer.getAnnotationModel().getAnnotationIterator();
						while (it.hasNext())
						{
							Object o=it.next();
							if (o instanceof MarkerAnnotation)
							{
								MarkerAnnotation ma=(MarkerAnnotation)o;
								lineBasedAnnotations.add(ma);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					boolean success=rearranger.rearrangeCode(document, lineBasedAnnotations);
					if (!success)
					{
						if (!batchMode)
							PrefPage.showErrors(Display.getCurrent().getActiveShell(), rearranger.getErrors(), rearranger.getInternalError());
						if (!rearranger.isSoftFailure())
							return false;
					}
					printer.setData(document.get()); //reset the text from the document, since it might have been altered
				}
					
				if (alterSelectedLines && selectionExists)
				{
					//don't allow adding braces if the whole document isn't being modified
					printer.getASPrinter().setConditionalBraceMode(ASPrettyPrinter.Braces_NoModify);
					printer.getASPrinter().setLoopBraceMode(ASPrettyPrinter.Braces_NoModify);
				}

				if (processingStyle==Const_Rearrange)
					printer.setRearrangeOnlyMode(true);
				
				if ((printer.isDoFormat() && store.getBoolean(Initializer.Pref_MXML_RemoveNamespacesAsPartOfFormat) && printer.getSelectedRange()==null) || processingStyle==Const_RemoveNamespaces)
				{
					MXMLNamespaceCleaner cleaner=new MXMLNamespaceCleaner(document);
					List<Exception> exceptions=new ArrayList<Exception>();
					boolean success=true;
					try {
						cleaner.removeExtra();
					} catch (Exception e) {
						e.printStackTrace();
						if (e.getMessage()!=null || e instanceof NoViableAltException)
						{
							exceptions.add(e);
						}
						success=false;
					}
					if (!success)
					{
						if (cleaner.getParseErrors()!=null)
							exceptions.addAll(cleaner.getParseErrors());
						if (!batchMode)
							PrefPage.showErrors(Display.getCurrent().getActiveShell(), exceptions, "");
						return false;
					}

					printer.setData(document.get()); //reset the text from the document, since it might have been altered
				}
				
//				if (processingStyle==Const_Indent || processingStyle==Const_Format)
				{
					//run as multiple passes so that the text diffs can be applied each time rather than
					//after multiple transformations (when it may not be possible to translate diffs into minimal edits)
					int maxPasses=2;
					int currentPasses=0;
					while (currentPasses<maxPasses)
					{
						currentPasses++;
						try {
							printer.disableMultiPassMode();
							printer.setData(document.get());
							String resultData=printer.print(0);
							if (resultData!=null)
							{
								resultSelection=applyResults(styled, document, resultData, printer.getOutputRange(), printer.getReplaceRange(), alterSelectedLines ? -1 : cursorLine, processingStyle==Const_Indent, alterSelectedLines, printer.getReplaceMap(), batchMode);
								if (printer.needAnotherPass())
									continue;
								return true;
							}
							else
							{
								if (!batchMode)
									PrefPage.showErrors(Display.getCurrent().getActiveShell(), printer.getParseErrors(), "");
							}
						} catch (Exception e) {
							e.printStackTrace();
							List<Exception> exceptions=new ArrayList<Exception>();
							if (printer.getParseErrors()!=null)
								exceptions.addAll(printer.getParseErrors());
							if (e.getMessage()!=null || e instanceof NoViableAltException)
							{
								exceptions.add(e);
							}
							if (!batchMode)
								PrefPage.showErrors(Display.getCurrent().getActiveShell(), exceptions, "");
						}
					}
				}
			}
			else if (extension.equals("as"))
			{
				ASPrettyPrinter printer=new ASPrettyPrinter(true, fileData);
				printer.setDoFormat(processingStyle==Const_Format);
				if (processingStyle==Const_Format && alterSelectedLines && selectionExists)
				{
					//determine selection
					Point selRange=styled.getSelectionRange();
					if (selRange.x==0 && selRange.y==document.getLength())
						alterSelectedLines=false;
					int startLine=document.getLineOfOffset(selRange.x);
					int endLine=document.getLineOfOffset(selRange.x+selRange.y);
					printer.setSelectedRange(new Point(startLine+1, endLine+1));
				}
				
				if ((printer.isDoFormat() && store.getBoolean(Initializer.Pref_AS_RearrangeAsPartOfFormat) && printer.getSelectedRange()==null) || processingStyle==Const_Rearrange)
				{
					ASRearranger rearranger=new ASRearranger(store);
					List<MarkerAnnotation> lineBasedAnnotations=new ArrayList<MarkerAnnotation>();
					try
					{
						Iterator it=viewer.getAnnotationModel().getAnnotationIterator();
						while (it.hasNext())
						{
							Object o=it.next();
							if (o instanceof MarkerAnnotation)
							{
								MarkerAnnotation ma=(MarkerAnnotation)o;
								lineBasedAnnotations.add(ma);
							}
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					boolean success=rearranger.rearrangeCode(document, lineBasedAnnotations, false);
					if (!success)
					{
						if (!batchMode)
							PrefPage.showErrors(Display.getCurrent().getActiveShell(), rearranger.getErrors(), rearranger.getInternalError());
						if (!rearranger.isSoftFailure())
							return false;
					}

					printer.setData(document.get()); //reset the text from the document, since it might have been altered
				}
				
				if (processingStyle!=Const_Rearrange)
				{
					int maxPasses=2;
					int currentPasses=0;
					while (currentPasses<maxPasses)
					{
						currentPasses++;
						printer.setData(document.get());
						FormatUtility.configureASPrinter(printer, store, tabSize);
						if (alterSelectedLines && selectionExists)
						{
							//don't allow adding braces if the whole document isn't being modified
							printer.setConditionalBraceMode(ASPrettyPrinter.Braces_NoModify);
							printer.setLoopBraceMode(ASPrettyPrinter.Braces_NoModify);
						}
						try {
							printer.disableMultiPassMode();
							String resultData=printer.print(0);
							if (resultData!=null)
							{
								resultSelection=applyResults(styled, document, resultData, printer.getOutputRange(), printer.getReplaceRange(), alterSelectedLines ? -1 : cursorLine, processingStyle==Const_Indent, alterSelectedLines, printer.getReplaceMap(), batchMode);
								if (printer.needAnotherPass())
									continue;
								return true;
							}
							else
							{
								if (!batchMode)
									PrefPage.showErrors(Display.getCurrent().getActiveShell(), printer.getParseErrors(), "");
							}
						} catch (Exception e) {
							e.printStackTrace();
							List<Exception> exceptions=new ArrayList<Exception>();
							if (printer.getParseErrors()!=null)
								exceptions.addAll(printer.getParseErrors());
							exceptions.add(e);
							if (!batchMode)
								PrefPage.showErrors(Display.getCurrent().getActiveShell(), exceptions, "");
						}
						break;
					}
				}
			}
			else
			{
				if (!batchMode)
				{
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Unknown file extension", "Don't recognize the file extension. Valid extensions are .mxml, .as"+(xmlExtensions.size()>0 ? ", "+xmlExtensions.toString() : ""));
				}
			}
		}
		catch (BadLocationException e)
		{
			String message="Error replacing formatted text.  See the Eclipse error log for the exception.";
			Activator.logException(e, message);
//			if (!batchMode)
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Internal Error", "Error replacing formatted text.  See the Eclipse error log for the exception.");
		}
//		catch (BadPositionCategoryException e) {
//			Activator.logException(e, null);
//			if (!batchMode)
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Internal Error", "Error rearranging code.  See the Eclipse error log for the exception.");
//		}
		finally
		{
			if (rewriteTarget!=null)
			{
				rewriteTarget.endCompoundChange();
				rewriteTarget.setRedraw(true);
			}

			if (resultSelection!=null)
			{
				styled.setSelection(resultSelection);
			}
			else if (cursorPos!=null)
			{
				//also, attempt to maintain line distance from top of screen
				styled.setSelection(cursorPos.getOffset(), cursorPos.getOffset());
				try {
					styled.setTopIndex(document.getLineOfOffset(cursorPos.getOffset())-linesFromTop);
				} catch (BadLocationException e) {
					Activator.logException(e, null);
				}
			}

			if (cursorPos!=null)
				document.removePosition(cursorPos);
		}
		
		return false;
	}

	private static Point applyResults(StyledText styled, IDocument document, String resultData, Point outputRange, Point replaceRange, int cursorLine, boolean indentOnly, boolean alterSelectedLines, Map<Integer, ReplacementRange> replaceMap, boolean batchMode) throws BadLocationException
	{
		if (alterSelectedLines)
		{
			if (indentOnly)
			{
				Point resultSelection=replaceSelectedLines(styled, document, resultData);
				return resultSelection;
			}
			else
			{
				if (outputRange!=null && replaceRange!=null)
				{
					Point resultSelection=replaceSelectedLines(styled, document, resultData, outputRange, replaceRange, replaceMap);
					return resultSelection;
				}
			}
		}
		resultData=convertLineDelimiters(resultData, styled.getLineDelimiter(), replaceMap, 0);
//		if (indentOnly)
//			replaceLeadingWhitespace(styled, document, document.get(), resultData, 0);
//		else
		return replaceEntireDocument(styled, document, document.get(), resultData, cursorLine, replaceMap, batchMode);
	}

	private static Point replaceEntireDocument(StyledText styled, IDocument document, String oldData, String resultData, int cursorLine, Map<Integer, ReplacementRange> replaceMap, boolean batchMode)
	{
		applyDiffsToText(document, oldData, resultData, styled.getLineDelimiter(), 0, replaceMap, 0, batchMode);
		return null;
	}

	private static String convertLineDelimiters(String initialData, String lineDelimiter, Map<Integer, ReplacementRange> replaceMap, int offsetIntoDoc)
	{
		//The parser spits out \n as the line delimiter.  This method adds back the \r if necessary or changes it
		//to \r if appropriate.  Also, the replaceMap positions are adjusted in the document.
		
		//if delimiter isn't changing, do nothing
		if (lineDelimiter.equals("\n"))
			return initialData;
		
		char firstChar=lineDelimiter.charAt(0);
		int delDiff=lineDelimiter.length()-1;
		
		StringBuffer result=new StringBuffer((int)(initialData.length()*1.2));
		for (int i=0;i<initialData.length();i++)
		{
			char c=initialData.charAt(i);
			if (c=='\n')
			{
				if (delDiff!=0 && replaceMap!=null)
				{
					for (ReplacementRange range : replaceMap.values()) {
						if (offsetIntoDoc+result.length()<range.getRangeInFormattedDoc().x)
							range.getRangeInFormattedDoc().x+=delDiff;
						if (offsetIntoDoc+result.length()<range.getRangeInFormattedDoc().y)
							range.getRangeInFormattedDoc().y+=delDiff;
						range.getRangeInFormattedDoc().y=Math.max(range.getRangeInFormattedDoc().x, range.getRangeInFormattedDoc().y); //just in case the range is empty in formatted doc
					}
				}
				result.append(lineDelimiter);
			}
			else if (c==firstChar && initialData.length()>=i+lineDelimiter.length() && initialData.substring(i, i+lineDelimiter.length()).equals(lineDelimiter)) //handle case where \r\n is already in document (ex. captured line comment)
			{
				result.append(lineDelimiter);
				i+=lineDelimiter.length()-1; //take one off because the loop incrementer will grab it; we don't want to look at the next char since it's part of the newline
			}
			else
				result.append(c);
		}
		return result.toString();
	}

	private static Point replaceSelectedLines(StyledText styled, IDocument doc, String resultData, Point outputRange, Point replaceRange, Map<Integer, ReplacementRange> replaceMap) throws BadLocationException
	{
		String data=resultData.substring(outputRange.x, outputRange.y);
		
		System.out.println("Formatted Data---");
		System.out.println(data);
		System.out.println("---End Formatted Data");
		
		System.out.println("Original Data---");
//		String oldText=doc.get();
		String originalData=doc.get(replaceRange.x, replaceRange.y-replaceRange.x);
		int length=originalData.length();
		while (length>0)
		{
			char c=originalData.charAt(length-1);
			if (c=='\n') //don't check for \r here because we could end up replacing in the middle of a delimiter (bad!)
				break;
			if (AntlrUtilities.isASWhitespace(c))
			{
				replaceRange.y--;
				length--;
			}
			else
				break;
		}
		if (originalData.length()>length)
		{
			originalData=originalData.substring(0, length);
		}
		System.out.println(originalData);
		System.out.println("---End Original Data");
		
		data=convertLineDelimiters(data, styled.getLineDelimiter(), replaceMap, outputRange.x);
		if (!ActionScriptFormatter.validateNonWhitespaceCharCounts(data, originalData))
		{
//			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", "Internal error replacing text: new text doesn't match replaced text("+originalData+")!=("+data+")");
			Activator.logException(null, "Internal error replacing text: new text doesn't match replaced text("+originalData+")!=("+data+")");
			return null;
		}
		
//		String completeText=doc.get();
		
//		boolean useOldStyleReplace=!Activator.getDefault().getPreferenceStore().getBoolean(Initializer.Pref_FlexCommand_UseMicroEdits);
//		if (useOldStyleReplace)
//		{
//			styled.replaceTextRange(replaceRange.x, replaceRange.y-replaceRange.x, data);
//			return new Point(replaceRange.x, replaceRange.x+data.length());
//		}
//		else
		{
			String delim=styled.getLineDelimiter();
			return applyDiffsToText(doc, originalData, data, delim, replaceRange.x, replaceMap, outputRange.x, false);
		}
	}
	
	private static class ReplacementHolder
	{
		ReplacementRange mRange;
		Position mPosition;
		public ReplacementHolder(ReplacementRange range, Position pos)
		{
			mPosition=pos;
			mRange=range;
		}
	}
	
	private static Point applyDiffsToText(IDocument doc, String oldData, String newData, String delim, int startPos, Map<Integer, ReplacementRange> replaceMap, int offsetIntoFormattedDoc, boolean batchMode)
	{
		boolean replacingWholeDoc=(doc.getLength()==oldData.length());
		String[] lineDelimiters=doc.getLegalLineDelimiters();
		Arrays.sort(lineDelimiters, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				//sort by descending string length so that the longest delimiter will appear first
				return -(o1.length()-o2.length());
			}
		});
//		char firstDelimChar=delim.charAt(0);
		int currentPositionInDocument=startPos;
		int newIndex=0;
		int oldIndex=0;
		
		
		List<ReplacementHolder> reps=null;
	
		try
		{
			if (replaceMap!=null)
			{
				reps=new ArrayList<ReplacementHolder>();
				for (ReplacementRange range : replaceMap.values()) {
					Position p=new Position(range.getRangeInOriginalDoc().x, 1);
					doc.addPosition(p);
					reps.add(new ReplacementHolder(range, p));
				}
				
				//sort by position in output document; ran into a problem with adding braces where there could
				//be multiple items with the same insert point in the source document.
				Collections.sort(reps, new Comparator<ReplacementHolder>()
				{
					public int compare(ReplacementHolder o1, ReplacementHolder o2)
					{
						return o1.mRange.getRangeInFormattedDoc().x-o2.mRange.getRangeInFormattedDoc().x;
					}
				});
			}
			
			//this algorithm assumes that there is no extra whitespace at the end of the replacement string.  Is that a valid
			//assumption?  I think so, because we never leave whitespace after the end of a line, and I don't think we
			//leave extra newlines either.  
			mainLoop: while (newIndex<newData.length() && oldIndex<oldData.length())
			{
				if (reps!=null && reps.size()>0)
				{
					for (int k=0;k<reps.size();k++)
					{
						ReplacementHolder replacementHolder=reps.get(k);
						int testOffset=replacementHolder.mPosition.getOffset();
						if (testOffset==currentPositionInDocument)
						{
							ReplacementRange range=replacementHolder.mRange;
							int formatStart=offsetIntoFormattedDoc;
							
							//remove the item from the list and from the document
							doc.removePosition(replacementHolder.mPosition);
							reps.remove(k);
							
							String replaceText=newData.substring(range.getRangeInFormattedDoc().x-formatStart, range.getRangeInFormattedDoc().y-formatStart);
							String deleteText=doc.get(replacementHolder.mPosition.getOffset(), range.getRangeInOriginalDoc().y-range.getRangeInOriginalDoc().x);
							//validate replacement text against replaced text
							if (!ActionScriptFormatter.validateNonWhitespaceCharCounts(replaceText+range.getDeletedText(), deleteText+range.getAddedText()))
							{
								String message="Internal error document data: char counts don't match ("+replaceText+" != "+deleteText+")";
//								if (batchMode)
									Activator.logException(null, message);
//								else
//									MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", message);				
								return null;
							}
							
							//special case if there is extra whitespace in the new document that hasn't been discovered
							//because we hit the replacement block.  We need to grab the extra chars or the newIndex will
							//be misaligned with the old index.
							if (newIndex<range.getRangeInFormattedDoc().x)
							{
								String whitespaceText=newData.substring(newIndex, range.getRangeInFormattedDoc().x);
								//add this special check for \r because I ran into a case where I split up a CR/LF
								//pair and created weird line endings in the file.
								if (AntlrUtilities.asTrim(whitespaceText).length()==0 && !whitespaceText.equals("\r"))
								{
									replaceText=whitespaceText+replaceText;
								}
							}
							doc.replace(currentPositionInDocument, deleteText.length(), replaceText);
							
							//update positions to move past the replaced text region
							currentPositionInDocument+=replaceText.length();
							oldIndex+=deleteText.length();
							newIndex+=replaceText.length();
								
							continue mainLoop;
						}
					}
				}
				char oldC=oldData.charAt(oldIndex);
				char newC=newData.charAt(newIndex);
				if (oldC==newC)
				{
					///debugging code
	//				try {
	//					if (doc.get(currentPositionInDocument, 1).charAt(0)!=oldC)
	//					{
	//						String allText=doc.get();
	//						return null;
	//					}
	//				} catch (BadLocationException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
					oldIndex++;
					newIndex++;
					currentPositionInDocument++;
					continue;
				}
				
				//if neither is whitespace, then this is a bug
				if (!AntlrUtilities.isASWhitespace(oldC) && !AntlrUtilities.isASWhitespace(newC))
				{
					//error condition
					String message="Internal error replacing text: chars don't match ("+oldC+"!="+newC+")";
//					if (batchMode)
						Activator.logException(null, message);
//					else
//						MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", message);
					return null;
				}
				
				//can be less than zero if both old and new data have different whitespace at the end
				int nextNewIndex=findNextNonWhitespaceChar(newData, newIndex);
				if (nextNewIndex<0)
					nextNewIndex=newData.length();
				int nextOldIndex=findNextNonWhitespaceChar(oldData, oldIndex);
				if (nextOldIndex<0)
					nextOldIndex=oldData.length();
				
				//now, we do special handling to prevent carriage returns from being replaced unnecessarily
				String newSpaceToChar=newData.substring(newIndex, nextNewIndex);
				String oldSpaceToChar=oldData.substring(oldIndex, nextOldIndex);
				
				newIndex=nextNewIndex;
				oldIndex=nextOldIndex;
				
				int oldSpaceIndex=0;
				int newSpaceIndex=0;
//				int savedCurrentPos=currentPositionInDocument;
				while (true)
				{
					if (newSpaceIndex>=newSpaceToChar.length())
					{
						if (oldSpaceIndex<oldSpaceToChar.length())
						{
							//if we have run out of formatted data, but not old data, then we need to delete the
							//old data
							int delLength=oldSpaceToChar.length()-oldSpaceIndex;
							String replacedString=AntlrUtilities.asTrim(doc.get(currentPositionInDocument, delLength));
							if (replacedString.length()>0)
							{
								//error
								String message="Internal error replacing text: replaced string has non-WS ("+replacedString+")";
//								if (batchMode)
									Activator.logException(null, message);
//								else
//									MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", message);
								return null;
							}
							doc.replace(currentPositionInDocument, delLength, "");
						}
						break;
					}
					else if (oldSpaceIndex>=oldSpaceToChar.length())
					{
						//if we have run out of old data, but we still have formatted data left, then insert it
						String insertString=newSpaceToChar.substring(newSpaceIndex);
						doc.replace(currentPositionInDocument, 0, insertString);
						currentPositionInDocument+=insertString.length();
						break;
					}
					
					int nextCR=newSpaceToChar.indexOf(delim, newSpaceIndex); //I am converting the line endings beforehand, so this should be safe
					boolean foundCRs=(nextCR>=0);
					if (nextCR<0)
						nextCR=newSpaceToChar.length();
					String newSpaceData=newSpaceToChar.substring(newSpaceIndex, nextCR);
					newSpaceIndex=nextCR;
					int savedEditIndex=oldSpaceIndex;
					
					String foundDelim=null;
					//TODO: change this to find the *first* delimiter (by position), in case there are multiple hits (like when whitespace crosses multiple lines)
					nextCR=(-1);
					int testNextCR=(-1);
					for (String del : lineDelimiters) {
						testNextCR=oldSpaceToChar.indexOf(del, oldSpaceIndex);
						//if we got a hit AND either we didn't have any previous hits or we are earlier than previous hits.
						if (testNextCR>=0 && (nextCR<0 || testNextCR<nextCR))
						{
							nextCR=testNextCR;
							foundDelim=del;
						}
					}
					foundCRs=foundCRs && (nextCR>=0);
					if (nextCR<0)
						nextCR=oldSpaceToChar.length();
	//				String oldData=oldSpaceToChar.substring(oldSpaceIndex, nextCR);
					oldSpaceIndex=nextCR;
					if (foundCRs)
					{
						oldSpaceIndex+=foundDelim.length();
						newSpaceIndex+=delim.length();
					}
					
					int delLength=nextCR-savedEditIndex;
					String replacedString=doc.get(currentPositionInDocument, delLength);
					if (AntlrUtilities.asTrim(replacedString).length()>0)
					{
						//error
						//						String allText=doc.get();
						String message="Internal error replacing text: replaced string has non-WS ("+replacedString.trim()+")";
//						if (batchMode)
							Activator.logException(null, message);
//						else
//							MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", message);
						return null;
					}
					doc.replace(currentPositionInDocument, delLength, newSpaceData);
					currentPositionInDocument+=newSpaceData.length();
					
					if (foundCRs)
						currentPositionInDocument+=foundDelim.length();
				}

				//removing because it actually breaks us in the case of mixed line delimiters in the file
//				//it should already be equal to the expected location, unless we have screwed up
//				if (currentPositionInDocument!=savedCurrentPos+newSpaceToChar.length())
//					currentPositionInDocument=savedCurrentPos+newSpaceToChar.length();
			}
			
			//see if we have left over data in either string, requiring either an insert or delete.
			if (newIndex<newData.length())
			{
				//this can happen if we are formatting the whole file and the last line doesn't have a newline after it
				//just stick rest of data in doc
				String restOfData=newData.substring(newIndex);
				doc.replace(currentPositionInDocument, 0, restOfData);
			}
			else if (oldIndex<oldData.length())
			{
				//I don't know how this would happen.  Somehow, we have extra whitespace on the end of the original document
				doc.replace(currentPositionInDocument, oldData.length()-oldIndex, "");
			}
			
			//add a carriage return at end if there isn't one already (if we're formatting the entire doc)
			if (replacingWholeDoc && !doc.get().endsWith(doc.getLineDelimiter(0)))
			{
				doc.replace(doc.getLength(), 0, doc.getLineDelimiter(0));
			}
		} catch (BadLocationException e) {
			String message="Internal error replacing text: bad location exception.  See Eclipse log.\nPlease report this problem on the SourceForge site.";
//			if (!batchMode)
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", message);
			Activator.logException(e, message);
		} catch (Exception e) {
			String message="Logic error replacing text: See Eclipse log.\nPlease report this problem on the SourceForge site.";
//			if (!batchMode)
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error formatting text", message);
			Activator.logException(e, message);
		}
		
		finally
		{
			if (reps!=null)
			{
				for (ReplacementHolder holder : reps) {
					doc.removePosition(holder.mPosition);
				}
			}
			
			//perform a sanity check.  If we are replacing the whole document, then the data passed in
			//should be exactly the same as the actual document, except perhaps for the leading/trailing whitespace.
			//If different, then there is some internal error and we'll just replace the whole doc.
			String editedDoc=doc.get();
			if (replacingWholeDoc && !newData.trim().equals(editedDoc.trim())) //ActionScriptFormatter.validateNonWhitespaceIdentical(newData, editedDoc))
			{
				System.err.println("Internal error in applyDiffsToText().");
				//perform undo?
				doc.set(newData); //set it to new data, since I don't really know how to undo my changes, and restoring the old data will lose line markers anyway
				System.err.println("Setting document data all at once.");
			}
		}
	
		return new Point(startPos, currentPositionInDocument);
	}

//	private static int findCRCount(String data, char delimChar)
//	{
//		int count=0;
//		for (int i=0;i<data.length();i++)
//		{
//			if (data.charAt(i)==delimChar)
//				count++;
//		}
//		return count;
//	}
	
	private static int findNextNonWhitespaceChar(String source, int startIndex)
	{
		for (int i=startIndex;i<source.length();i++)
		{
			char c=source.charAt(i);
			if (!AntlrUtilities.isASWhitespace(c))
				return i;
		}
		
		return -1;
	}
	
	
	private static Point replaceSelectedLines(StyledText styled, IDocument doc, String indentedText) throws BadLocationException
	{
		Point selRange=styled.getSelectionRange();
		int startLine=doc.getLineOfOffset(selRange.x);
		int endLine=doc.getLineOfOffset(selRange.x+selRange.y);
		int cursorPositionOnLine=(-1);
		if (selRange.y==0) //no selection
		{
			IRegion lineRegion=doc.getLineInformation(startLine);
			cursorPositionOnLine=selRange.x-doc.getLineOffset(startLine);
			String lineText=doc.get(lineRegion.getOffset(), lineRegion.getLength());
			for (int i=0;i<lineText.length();i++)
			{
				if (!AntlrUtilities.isASWhitespace(lineText.charAt(i)))
				{
					if (cursorPositionOnLine<=i)
						cursorPositionOnLine=(-1);
					else
					{
						cursorPositionOnLine-=i; //make it an offset into the line data
					}
					break;
				}
			}
		}
		int startOfStartLine=doc.getLineOffset(startLine);
//		int docLength=doc.getLength();
		int startOfAfterEndLine=0;
		if (endLine+1<doc.getNumberOfLines())
			startOfAfterEndLine=doc.getLineOffset(endLine+1);
		else
			startOfAfterEndLine=doc.getLength();
		String[] splitLines=indentedText.split("\n");
		StringBuffer buffer=new StringBuffer();
		if (endLine>=splitLines.length)
			endLine=splitLines.length-1;
		for (int i=startLine;i<=endLine;i++)
		{
			buffer.append(splitLines[i]);
			buffer.append('\n');
		}
		
		String existingText="";
		if (startOfStartLine<startOfAfterEndLine)
			existingText=doc.get(startOfStartLine, startOfAfterEndLine-startOfStartLine);
		String data=buffer.toString();
//		boolean useOldStyleReplace=!Activator.getDefault().getPreferenceStore().getBoolean(Initializer.Pref_FlexCommand_UseMicroEdits);
		data=convertLineDelimiters(data, styled.getLineDelimiter(), null, 0);
		if (!ActionScriptFormatter.validateNonWhitespaceIdentical(data, existingText))
		{
			Activator.logException(null, "Internal error replacing text: new text doesn't match replaced text("+buffer.toString()+")!=("+existingText+")");
//			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error indenting text", "Internal error replacing text: new text doesn't match replaced text("+buffer.toString()+")!=("+existingText+")");
			return null;
		}
//		if (useOldStyleReplace)
//		{
//			styled.replaceTextRange(startOfStartLine, startOfAfterEndLine-startOfStartLine, data);
//			return new Point(startOfStartLine, startOfStartLine+data.length());
//		}
//		else
		{
			Point resultPos=applyDiffsToText(doc, existingText, data, styled.getLineDelimiter(), startOfStartLine, null, -1, false);
			if (selRange.y==0)
			{
				IRegion newLineRegion=doc.getLineInformation(startLine);
				String lineText=doc.get(newLineRegion.getOffset(), newLineRegion.getLength());
				resultPos.x=doc.getLineOffset(startLine);
				resultPos.y=resultPos.x;
				for (int i=0;i<lineText.length();i++)
				{
					if (!AntlrUtilities.isASWhitespace(lineText.charAt(i)))
					{
						resultPos.x=doc.getLineOffset(startLine)+i;
						if (cursorPositionOnLine>=0)
						{
							resultPos.x+=cursorPositionOnLine;
						}
						resultPos.y=resultPos.x;
						break;
					}
				}
			}
			return resultPos;
		}
	}
	
	public static void doFormat()
	{
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
			doFormat(editorPart, Const_Format, true, false);
		}
	}

}
