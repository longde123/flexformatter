package flexasrearrangecodecommand.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import actionscriptinfocollector.ASCollector;
import actionscriptinfocollector.ClassRecord;
import actionscriptinfocollector.FunctionRecord;
import actionscriptinfocollector.PropertyLine;
import actionscriptinfocollector.SourceItem;
import actionscriptinfocollector.TextItem;
import actionscriptinfocollector.TopLevelItemRecord;
import flexasrearrangecodecommand.Activator;
import flexasrearrangecodecommand.preferences.PreferenceConstants;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ASRearrangeCodeHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public ASRearrangeCodeHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
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
			ASCollector creator=new ASCollector(document);
			boolean success=creator.parse();
			if (success)
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
					
					List<ClassRecord> classes=creator.getClassRecords();
					for (ClassRecord classRecord : classes)
					{
						reorderClassModifiers(classRecord, document);
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
			
		}
		return null;
	}
	
	

	private void reorderClassModifiers(ClassRecord classRecord, IDocument document)
	{
		reorderModifiers(classRecord, document, getOrdering(PreferenceConstants.ASRearr_Class));

		List<FunctionRecord> functions=classRecord.getFunctions();
		List<String> ordering=getOrdering(PreferenceConstants.ASRearr_Function);
		for (FunctionRecord functionRecord : functions) {
			reorderModifiers(functionRecord, document, ordering);
		}

		List<PropertyLine> properties=classRecord.getProperties();
		ordering=getOrdering(PreferenceConstants.ASRearr_Property);
		for (PropertyLine propRecord : properties) {
			reorderModifiers(propRecord, document, ordering);
		}
	}

	private List<String> getOrdering(String elementType)
	{
		if (!Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+elementType))
			return null;
		
		String order=Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.ASRearr_ModifierOrder_Root+elementType);
		if (order.length()==0)
			return null;
		
		String[] items=order.split(",");
		List<String> results=new ArrayList<String>();
		for (String item : items) {
			if (item.length()>0)
				results.add(item);
		}
		return results;
	}

	private void reorderModifiers(TopLevelItemRecord record, IDocument document, List<String> ordering)
	{
		if (ordering==null)
			return;
		
		Set<TextItem> mods=record.getModifiers();
		List<TextItem> currentOrder=new ArrayList<TextItem>();
		currentOrder.addAll(mods);
		Collections.sort(currentOrder);
		
		List<TextItem> desiredOrder=new ArrayList<TextItem>();
		desiredOrder.addAll(currentOrder);
		sortByOrdering(desiredOrder, ordering);
		
		SourceItem firstMod=null;
		if (currentOrder.size()>0)
			firstMod=currentOrder.get(0);
		
		for (int i=0;i<desiredOrder.size();i++)
		{
			TextItem currentItem=currentOrder.get(i);
			TextItem newItem=desiredOrder.get(i);
			if (currentItem.getText().equals(newItem.getText()))
				continue; //if the same, then do nothing
					
			try {
				//determine insertion location
				int newPos=currentItem.getStartPos();
				if (currentItem!=firstMod)
					newPos=currentItem.getPreStartPos();
				
				int startCopyPos=newItem.getStartPos();
				if (newItem!=currentItem)
					startCopyPos=newItem.getPreStartPos();
				String moveText=document.get(startCopyPos, newItem.getEndPos()-startCopyPos);
				
				document.replace(startCopyPos, moveText.length(), "");
				document.replace(newPos, 0, moveText);
				
				int modStartPos=newPos+moveText.length()-newItem.getText().length();
				newItem.resetPositions(modStartPos, modStartPos+newItem.getText().length(), document);
				currentOrder.add(i, newItem);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private void sortByOrdering(List<TextItem> mods, List<String> ordering)
	{
		final Map<String, Integer> orderMap=new HashMap<String, Integer>();
		for (int i=0;i<ordering.size();i++)
		{
			orderMap.put(ordering.get(i), i);
		}
		
		Collections.sort(mods, new Comparator<TextItem>()
		{
			public int compare(TextItem o1, TextItem o2)
			{
				Integer int1=orderMap.get(o1);
				Integer int2=orderMap.get(o2);
				if (int1==null)
					int1=orderMap.get(PreferenceConstants.NamespaceGeneric);
				if (int2==null)
					int2=orderMap.get(PreferenceConstants.NamespaceGeneric);
				if (int1!=null && int2!=null)
				{
					int i1=int1.intValue();
					int i2=int2.intValue();
					if (i1!=i2)
						return i1-i2;
				}
				
				return o1.getText().compareTo(o2.getText());
			}
		});
		
		
//		List<TextItem> working=new ArrayList<TextItem>();
//		//note: this is n-squared, but small n
//		for (String mod : ordering)
//		{
//			for (int i = 0; i < mods.size(); i++) {
//				TextItem modItem=mods.get(i);
//				if (modItem.getText().equals(mod))
//				{
//					working.add(modItem);
//					mods.remove(i);
//					break;
//				}
//			}
//		}
//		mods.clear();
//		mods.addAll(working);
	}
	
	
}
