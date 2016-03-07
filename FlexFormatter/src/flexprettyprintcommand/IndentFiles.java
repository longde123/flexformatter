package flexprettyprintcommand;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;

import flexprettyprint.handlers.FlexPrettyFormatHandler;

public class IndentFiles implements IActionDelegate {
	private ISelection mSelection;
	public IndentFiles() {
		mSelection=null;
	}

	public void run(IAction action) {
		FormatFiles.doFormat(FlexPrettyFormatHandler.Const_Indent, mSelection);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		mSelection=selection;
	}

}
