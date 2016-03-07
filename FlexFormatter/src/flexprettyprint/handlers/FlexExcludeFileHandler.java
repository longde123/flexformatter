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

public class FlexExcludeFileHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow window=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IPartService partService=window.getPartService();
		IWorkbenchPart part=partService.getActivePart();
		if (part instanceof IEditorPart)
		{
			IEditorPart editorPart=(IEditorPart)part;
			boolean isActionscript=false;
			if (editorPart.getEditorInput() instanceof IPathEditorInput)
			{
				String fileName=((IPathEditorInput)editorPart.getEditorInput()).getPath().lastSegment().toLowerCase();
				if (fileName.toLowerCase().endsWith(".as"))
				{
					isActionscript=true;
				}
			}
			
			ISourceViewer viewer=(ISourceViewer)editorPart.getAdapter(ITextOperationTarget.class);
			StyledText styled=viewer.getTextWidget();
			if (styled.getText().contains(ASPrettyPrinter.mIgnoreFileProcessing))
				return null; //don't add the tag again if it's already there
			
			if (isActionscript)
			{
				styled.setSelection(0);
				styled.insert("//"+ASPrettyPrinter.mIgnoreFileProcessing+styled.getLineDelimiter());	
			}
			else
			{
				//assume it's an xml style file.
				int xmlMetaStart=styled.getText().indexOf("<?");
				if (xmlMetaStart<0)
				{
					styled.setSelection(0);
				}
				else
				{
					int nextLineStart=styled.getOffsetAtLine(styled.getLineAtOffset(xmlMetaStart)+1);
					styled.setSelection(nextLineStart);
				}
				styled.insert("<!--"+ASPrettyPrinter.mIgnoreFileProcessing+"-->"+styled.getLineDelimiter());	
			}
		}
		
		return null;
	}

}
