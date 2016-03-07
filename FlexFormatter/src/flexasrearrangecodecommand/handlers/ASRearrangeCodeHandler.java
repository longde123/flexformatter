package flexasrearrangecodecommand.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import flexprettyprint.handlers.FlexPrettyFormatHandler;

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
			
			String inputName=editorPart.getEditorInput().getName().toLowerCase();
			if (!inputName.endsWith(".as") && !inputName.endsWith(".mxml"))
			{
				boolean batchMode=false;
				if (!batchMode)
				{
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Rearrange Actionscript code", "The Rearrange Actionscript code standalone operation is only supported for the .as and .mxml file extensions.");
					return null;
				}
			}
			
			if (inputName.endsWith(".as"))
			{
				FlexPrettyFormatHandler.doFormat(editorPart, FlexPrettyFormatHandler.Const_Rearrange, false, false);
			}
			else if (inputName.endsWith(".mxml"))
			{
				FlexPrettyFormatHandler.doFormat(editorPart, FlexPrettyFormatHandler.Const_Rearrange, false, false);
			}
		}
		return null;
	}
	
	

}
