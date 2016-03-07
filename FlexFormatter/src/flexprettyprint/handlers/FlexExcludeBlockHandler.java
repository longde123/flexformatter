package flexprettyprint.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class FlexExcludeBlockHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
			if (editorPart.getEditorInput() instanceof IPathEditorInput)
			{
				String fileName=((IPathEditorInput)editorPart.getEditorInput()).getPath().lastSegment().toLowerCase();
				if (!fileName.endsWith(".as") && !fileName.endsWith(".mxml"))
				{
					return null;
				}
			}
			ISourceViewer viewer=(ISourceViewer)editorPart.getAdapter(ITextOperationTarget.class);
			StyledText styled=viewer.getTextWidget();
			int startLine=styled.getLineAtOffset(styled.getSelection().x);
			int endLine=styled.getLineAtOffset(styled.getSelection().y);
			if (endLine>startLine && styled.getOffsetAtLine(endLine)==styled.getSelection().y)
				endLine--;
			
			//insert the formatting preventers at column 1
			styled.setSelection(styled.getOffsetAtLine(endLine)+styled.getLine(endLine).length());
			styled.insert(styled.getLineDelimiter()+"//"+ASPrettyPrinter.mStopExcludeProcessing);
			styled.setSelection(styled.getOffsetAtLine(startLine));
			styled.insert("//"+ASPrettyPrinter.mStartExcludeProcessing+styled.getLineDelimiter());
		}
		
		return null;
	}

}
