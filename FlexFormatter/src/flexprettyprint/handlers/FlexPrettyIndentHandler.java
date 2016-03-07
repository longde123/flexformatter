package flexprettyprint.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class FlexPrettyIndentHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public FlexPrettyIndentHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
			FlexPrettyFormatHandler.doFormat(editorPart, FlexPrettyFormatHandler.Const_Indent, true, false);
		}
		return null;
	}
}
