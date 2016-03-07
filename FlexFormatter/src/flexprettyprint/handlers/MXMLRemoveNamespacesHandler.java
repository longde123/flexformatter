package flexprettyprint.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewerExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import flexprettyprint.preferences.PrefPage;

public class MXMLRemoveNamespacesHandler extends AbstractHandler  
{
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
			boolean isMXML=false;
			if (editorPart.getEditorInput() instanceof IPathEditorInput)
			{
				String fileName=((IPathEditorInput)editorPart.getEditorInput()).getPath().lastSegment().toLowerCase();
				if (fileName.toLowerCase().endsWith(".mxml"))
				{
					isMXML=true;
				}
			}
			
			if (isMXML)
			{
				FlexPrettyFormatHandler.doFormat(editorPart, FlexPrettyFormatHandler.Const_RemoveNamespaces, false, false);
				
//				ISourceViewer viewer=(ISourceViewer)editorPart.getAdapter(ITextOperationTarget.class);
//				MXMLNamespaceCleaner cleaner=new MXMLNamespaceCleaner(viewer.getDocument());
//				IRewriteTarget rewriteTarget=null;
//				try {
//					if (viewer instanceof ITextViewerExtension)
//					{
//						rewriteTarget=((ITextViewerExtension)viewer).getRewriteTarget();
//						rewriteTarget.setRedraw(false);
//						rewriteTarget.beginCompoundChange();
//					}					
//					boolean success=cleaner.removeExtra();
//					if (!success)
//						PrefPage.showErrors(Display.getCurrent().getActiveShell(), cleaner.getParseErrors(), "");
//				} 
//				catch (Exception e) {
//					e.printStackTrace();
//					List<Exception> exceptions=new ArrayList<Exception>();
//					if (cleaner.getParseErrors()!=null)
//						exceptions.addAll(cleaner.getParseErrors());
//					exceptions.add(e);
//					PrefPage.showErrors(Display.getCurrent().getActiveShell(), exceptions, "");
//				}
//				finally
//				{
//					if (rewriteTarget!=null)
//					{
//						rewriteTarget.endCompoundChange();
//						rewriteTarget.setRedraw(true);
//					}
//				}
			}
		}
		
		return null;
	}

}
