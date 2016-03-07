package flexprettyprintcommand;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BatchResultDialog extends Dialog
{
	List<IFile> mSuccessful=new ArrayList<IFile>();
	List<IFile> mFormatFailed=new ArrayList<IFile>();
	List<IFile> mOtherFailure=new ArrayList<IFile>();
	List<IFile> mSkipped=new ArrayList<IFile>();
	List<IFile> mFiltered=new ArrayList<IFile>();
	public BatchResultDialog(Shell shell, List<IFile> successful, List<IFile> formatFailed, List<IFile> skipped, List<IFile> filtered, List<IFile> otherFailure)
	{
		super(shell);
		mSkipped=skipped;
		mOtherFailure=otherFailure;
		mFormatFailed=formatFailed;
		mSuccessful=successful;
		mFiltered=filtered;
		setShellStyle(getShellStyle()|SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText("Batch format results");
		Composite comp=new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l=new Label(comp, SWT.None);
		String message=null;
		if (mFormatFailed.size()>0 || mOtherFailure.size()>0)
		{
			message="Some formatting failures occurred";
		}
		else if (mFiltered.size()>0)
		{
			message="Some files were filtered";
		}
		else if (mSkipped.size()>0)
		{
			message="Some files were skipped";
		}
		else
		{
			message="All files successfully formatted";
		}
		l.setText(message);
		
		Text text=new Text(comp, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd=new GridData(GridData.FILL_BOTH);
		gd.widthHint=400;
		gd.heightHint=300;
		text.setLayoutData(gd);
		text.setText(getPrintString(mSuccessful, mFormatFailed, mOtherFailure, mSkipped, mFiltered));
		
		return comp;
	}
	
	public static String getPrintString(List<IFile> successful, List<IFile> formatFailed, List<IFile> otherFailure, List<IFile> skipped, List<IFile> filtered)
	{
		StringBuffer buffer=new StringBuffer();
		if (successful.size()>0)
		{
			buffer.append("The following files were successfully formatted:\n");
			for (IFile file : successful) {
				buffer.append(file.getFullPath());
				buffer.append("\n");
			}
			buffer.append("\n");	
		}
		if (formatFailed.size()>0)
		{
			buffer.append("The following files had formatting failures:\n");
			for (IFile file : formatFailed) {
				buffer.append(file.getFullPath());
				buffer.append("\n");
			}
			buffer.append("\n");	
		}
		if (otherFailure.size()>0)
		{
			buffer.append("The following files had other (non-formatting) failures:\n");
			for (IFile file : otherFailure) {
				buffer.append(file.getFullPath());
				buffer.append("\n");
			}
			buffer.append("\n");	
		}
		if (filtered.size()>0)
		{
			buffer.append("The following files were skipped (because their paths were filtered out based on FlexFormatter preference settings):\n");
			for (IFile file : filtered) {
				buffer.append(file.getFullPath());
				buffer.append("\n");
			}
			buffer.append("\n");	
		}
		if (skipped.size()>0)
		{
			buffer.append("The following files were skipped (because they weren't identified as .as or .mxml files):\n");
			for (IFile file : skipped) {
				buffer.append(file.getFullPath());
				buffer.append("\n");
			}
			buffer.append("\n");	
		}
		return buffer.toString();
	}
	
}
