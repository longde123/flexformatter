package flexasdocgen.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import actionscriptinfocollector.ASCollector;
import actionscriptinfocollector.ASDocComment;
import actionscriptinfocollector.AntlrUtilities;
import actionscriptinfocollector.ClassRecord;
import actionscriptinfocollector.DeclRecord;
import actionscriptinfocollector.FunctionRecord;
import actionscriptinfocollector.ParseErrorDialog;
import actionscriptinfocollector.PropertyLine;
import actionscriptinfocollector.SourceItem;
import flexasdocgen.Activator;
import flexasdocgencommand.preferences.PreferenceConstants;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ASDocGenHandler extends AbstractHandler {
	
	public static final String Tag_Author="%author%";
	public static final String Doc_Author="@author ";
	public static final String Tag_Param="%param%";
	public static final String Doc_Param="@param";
	public static final String Tag_Throws="%throws%";
	public static final String Doc_Throws="@throws";
	public static final String Tag_Return="%return%";
	public static final String Doc_Return="@return ";
	public static final String Tag_Default="%default%";
	public static final String Doc_Default="@default ";
	/**
	 * The constructor.
	 */
	public ASDocGenHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		addASDoc(false);
		return null;
	}
	
	public static void addASDoc(boolean currentElementOnly)
	{
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
			String inputName=editorPart.getEditorInput().getName();
			if (!inputName.endsWith(".as"))
			{
				MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Generate ASDoc", "Generating ASDoc comments only supported for .as files");
				return;
			}
			
			ISourceViewer viewer=(ISourceViewer)editorPart.getAdapter(ITextOperationTarget.class);
//			IRewriteTarget rewriteTarget=null;
			IDocument document=viewer.getDocument();
//			Point resultSelection=null;
//			Position cursorPos=null;
//			if (viewer instanceof ITextViewerExtension)
//			{
//				rewriteTarget=((ITextViewerExtension)viewer).getRewriteTarget();
//				rewriteTarget.setRedraw(false);
//				rewriteTarget.beginCompoundChange();
//			}
			
//			StyledText styled=viewer.getTextWidget();
			
//			String text=document.get();
			StyledText styled=viewer.getTextWidget();
			int cursorPos=(-1);
			if (currentElementOnly)
				cursorPos=styled.getCaretOffset();
			List<ASCollector> collectors=new ArrayList<ASCollector>();
			List<Exception> failures=ASCollector.parse(document, collectors);
			if (failures==null)
			{
				IRewriteTarget rewriteTarget=null;
				IPositionUpdater updater=null;
				try
				{
					updater=new DefaultPositionUpdater(SourceItem.PositionCategory);
					document.addPositionUpdater(updater);
					if (viewer instanceof ITextViewerExtension)
					{
						rewriteTarget=((ITextViewerExtension)viewer).getRewriteTarget();
						rewriteTarget.setRedraw(false);
						rewriteTarget.beginCompoundChange();
					}
					
					for (ASCollector collector : collectors) {
						collector.applyDocument(document);						
					}
					
					mainLoop: for (ASCollector collector : collectors) {
						List<FunctionRecord> functions=collector.getFunctionRecords();
						boolean keepProcessing=true;
						String functionTemplate=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Function);
						functionTemplate=functionTemplate.replace("\r\n", "\n").replace("\r", "\n");
						for (FunctionRecord func : functions)
						{
							keepProcessing=addFunctionDoc(func, cursorPos, functionTemplate, document);
							if (!keepProcessing)
								break mainLoop;
						}
						
						if (keepProcessing)
						{
							List<PropertyLine> props=collector.getPropertyRecords();
							String propertyTemplate=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Property);
							propertyTemplate=propertyTemplate.replace("\r\n", "\n").replace("\r", "\n");
							for (PropertyLine prop : props)
							{
								keepProcessing=addPropertyDoc(prop, cursorPos, propertyTemplate, document);
								if (!keepProcessing)
									break mainLoop;
							}
						}
						
						if (keepProcessing)
						{
							List<ClassRecord> classes=collector.getClassRecords();
							for (ClassRecord classRecord : classes)
							{
								keepProcessing=addClassDoc(classRecord, document, cursorPos);
								if (!keepProcessing)
									break mainLoop;
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (updater!=null)
						document.removePositionUpdater(updater);
					try {
						document.removePositionCategory(SourceItem.PositionCategory);
					} catch (BadPositionCategoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (rewriteTarget!=null)
					{
						rewriteTarget.endCompoundChange();
						rewriteTarget.setRedraw(true);
					}
				}
			}
			else
			{
				ParseErrorDialog dlg=new ParseErrorDialog(Display.getDefault().getActiveShell(), failures, "");
				dlg.open();
			}
		}
	}
	
	private static String getIndent(IDocument document, int charPos)
	{
		try {
			int lineIndex=document.getLineOfOffset(charPos);
			int lineStart=document.getLineOffset(lineIndex);
			return document.get(lineStart, charPos-lineStart);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	private static String getLinePrefix(String template, String tag)
	{
		int pos=template.indexOf(tag);
		if (pos<0)
			return "";
		int prevCR=template.lastIndexOf('\n', pos);
		if (prevCR<0)
			return "";
		
		return template.substring(prevCR+1, pos);
	}

	private static boolean addPropertyDoc(PropertyLine prop, int elementCursorPos, String propertyTemplate, IDocument document) throws BadLocationException
	{
		if (elementCursorPos>=0 || canAddASDoc(prop.getModifierFlags(), PreferenceConstants.ASDoc_Property))
		{
			ASDocComment comment=prop.getComment();
			
			boolean generateDoc=true;
			if (elementCursorPos>=0 && (elementCursorPos<prop.getStartPos() || elementCursorPos>=prop.getEndPos()))
				generateDoc=false;
			
			if (generateDoc && (elementCursorPos>=0 || comment==null))
			{
				String indentString=getIndent(document, prop.getStartPos());
				String thisDoc=propertyTemplate.replace("\n", document.getLineDelimiter(0)+indentString);
				thisDoc=replaceCommonTemplateItems(thisDoc);
				thisDoc=replaceTag(thisDoc, Tag_Default, Doc_Default);
				
				StringBuffer buffer=new StringBuffer();
				for (DeclRecord decl : prop.getProperties()) {
					buffer.append(decl.getName().getText());
				}
				document.replace(prop.getStartPos(), 0, thisDoc); //"/**\n* Property "+buffer.toString()+"\n*/\n");
				if (elementCursorPos>=0)
					return false;
			}
		}
		
		return true;
	}
	
	private static boolean addFunctionDoc(FunctionRecord func, int elementCursorPos, String functionTemplate, IDocument document) throws BadLocationException
	{
		if (elementCursorPos>=0 || canAddASDoc(func.getModifierFlags(), PreferenceConstants.ASDoc_Function))
		{
			ASDocComment comment=func.getComment();

			boolean generateDoc=true;
			if (elementCursorPos>=0 && (elementCursorPos<func.getStartPos() || elementCursorPos>=func.getEndPos()))
				generateDoc=false;
			
			if (generateDoc && (elementCursorPos>=0 || comment==null))
			{
				String replacementText=generateTagText(functionTemplate, Tag_Param, Doc_Param, func.getParameters(), new INamePrinter()
				{
					public String getName(Object obj)
					{
						return ((DeclRecord)obj).getName().getText();
					}
				});
				String thisDoc=replaceTag(functionTemplate, Tag_Param, replacementText);
				thisDoc=replaceCommonTemplateItems(thisDoc);
				
				StringBuffer returnBuffer=new StringBuffer();
				if (func.getReturnType()!=null && !func.getReturnType().getText().equals("void"))
				{
					returnBuffer.append(Doc_Return);
				}
				thisDoc=replaceTag(thisDoc, Tag_Return, returnBuffer.toString());
				
				replacementText=generateTagText(functionTemplate, Tag_Throws, Doc_Throws, func.getThrowsExceptions(), new INamePrinter()
				{
					public String getName(Object obj)
					{
						return obj.toString();
					}
				});
				thisDoc=replaceTag(thisDoc, Tag_Throws, replacementText);
				
				String indentString=getIndent(document, func.getStartPos());
				thisDoc=thisDoc.replace("\n", document.getLineDelimiter(0)+indentString);
				document.replace(func.getStartPos(), 0, thisDoc);
				if (elementCursorPos>=0)
					return false;
			}
		}
		
		return true;
	}
	
	private static String replaceTag(String thisDoc, String tagName, String replacementText)
	{
		if (replacementText.length()==0)
		{
			int index=0;
			while (true)
			{
				index=thisDoc.indexOf(tagName, index);
				if (index<0)
					break;
				
				//delete tag text
				thisDoc=thisDoc.substring(0, index)+thisDoc.substring(index+tagName.length());
				
				//now, delete the entire line
				index--; //move back a spot since we have removed the position that was 'index'
				int previousCR=thisDoc.lastIndexOf('\n', index);
				int nextCR=thisDoc.indexOf('\n', index);
				if (previousCR<0 || nextCR<0)
					break;
				String line=thisDoc.substring(previousCR, nextCR);
				boolean deleteLine=true;
				for (int i=0;i<line.length();i++)
				{
					char ch=line.charAt(i);
					if (!AntlrUtilities.isASWhitespace(ch) && ch!='*')
					{
						deleteLine=false;
						break;
					}
				}
				if (deleteLine)
				{
					thisDoc=thisDoc.substring(0, previousCR)+thisDoc.substring(nextCR);
					index=previousCR;
				}
			}
		}
		else
		{
			thisDoc=thisDoc.replace(tagName, replacementText);
		}
		return thisDoc;
	}


	private static boolean addClassDoc(ClassRecord classRecord, IDocument document, int elementCursorPos) throws BadLocationException
	{
		String functionTemplate=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Function);
		functionTemplate=functionTemplate.replace("\r\n", "\n").replace("\r", "\n");
		for (FunctionRecord func : classRecord.getFunctions())
		{
			boolean keepGoing=addFunctionDoc(func, elementCursorPos, functionTemplate, document);
			if (!keepGoing)
				return false;
		}
		
		String propertyTemplate=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Property);
		propertyTemplate=propertyTemplate.replace("\r\n", "\n").replace("\r", "\n");
		for (PropertyLine prop: classRecord.getProperties())
		{
			boolean keepGoing=addPropertyDoc(prop, elementCursorPos, propertyTemplate, document);
			if (!keepGoing)
				return false;
		}
		
		
		
		//TODO: handle internal classes
		
		ASDocComment comment=classRecord.getComment();
		if (elementCursorPos>=0 || canAddASDoc(classRecord.getModifierFlags(), PreferenceConstants.ASDoc_Class))
		{
			boolean generateDoc=true;
			if (elementCursorPos>=0 && (elementCursorPos<classRecord.getStartPos() || elementCursorPos>=classRecord.getEndPos()))
				generateDoc=false;
			
			if (generateDoc && (elementCursorPos>=0 || comment==null))
			{
				String classTemplate=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Class);
				classTemplate=classTemplate.replace("\r\n", "\n").replace("\r", "\n");
				classTemplate=replaceCommonTemplateItems(classTemplate);
				String indentString=getIndent(document, classRecord.getStartPos());
				
				
				classTemplate=classTemplate.replace("\n", document.getLineDelimiter(0)+indentString);
				document.replace(classRecord.getStartPos(), 0, classTemplate); //"/**\n* Class "+classRecord.getName().getText()+"\n*/\n");
				if (elementCursorPos>=0)
					return false;
			}
			else
			{}
		}
		
		return true;
	}
	
	private static String replaceCommonTemplateItems(String template)
	{
		String userID=System.getProperty("user.name");
		template=replaceTag(template, Tag_Author, Doc_Author+userID);
		return template;
	}

	private static boolean canAddASDoc(int modifierFlags, String typeString)
	{
		//if any of visibility flags match, then we still assume we're going to doc this item
		int visFlags=Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+typeString);
		if ((visFlags & modifierFlags)==0) //if no overlap between the modifiers for this item and the visibility flags, then we won't doc
			return false;
		
		//for other modifiers, we don't doc if any of the items that is turned off in the settings (i.e. filtered out) is
		//present in the flags for this item
		int modFlags=Activator.getDefault().getPreferenceStore().getInt(PreferenceConstants.ASDoc_ModifierFilter_Root+typeString);
		if ((~modFlags & (modifierFlags & 0xfff0))!=0)
			return false;
		
		return true;
	}

	private static String generateTagText(String template, String tag, String docTag, List<? extends Object> parameters, INamePrinter namePrinter)
	{
		StringBuffer buffer=new StringBuffer();
		String prefix=getLinePrefix(template, tag);
		for (int i=0;i<parameters.size();i++)
		{
			Object o=parameters.get(i);
			if (i>0)
				buffer.append(prefix);
			buffer.append(docTag+" "+namePrinter.getName(o));
			if (i+1<parameters.size())
				buffer.append("\n");
		}
		
		return buffer.toString();
	}

	public interface INamePrinter
	{
		public String getName(Object obj);
	}
}
