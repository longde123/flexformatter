package actionscriptinfocollector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ParseErrorDialog extends Dialog
{
	private List<Exception> mExceptions;
	private String mInternalErrorText;
	
	private Table mExceptionTable; //shows line number and message
	private Text mTextViewer; //shows stack trace
	
	public ParseErrorDialog(Shell parentShell, List<Exception> parseErrors, String internalError)
	{
		super(parentShell);
		mExceptions=parseErrors;
		if (mExceptions==null)
			mExceptions=new ArrayList<Exception>();
		mInternalErrorText=internalError;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		boolean internalError=((mInternalErrorText!=null && mInternalErrorText.length()>0) || mExceptions==null);
		if (internalError)
			getShell().setText("Internal error");
		else
			getShell().setText("Some errors occurred parsing the input document (normally the last error is the correct one)");
		
		if (internalError)
		{
			Composite comp=new Composite(parent, SWT.None);
			comp.setLayout(new GridLayout(1, false));
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Text data=new Text(comp, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
			data.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			if (mInternalErrorText.length()>0)
				data.setText(mInternalErrorText);
			else
				data.setText("No further information");
			return comp;
		}
		else
		{
			Composite comp=new Composite(parent, SWT.None);
			comp.setLayout(new GridLayout(2, false));
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mExceptionTable=new Table(comp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
			mExceptionTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			TableColumn messageCol=new TableColumn(mExceptionTable, SWT.None);
			messageCol.setText("Message");
			messageCol.setWidth(200);
			TableColumn locationCol=new TableColumn(mExceptionTable, SWT.None);
			locationCol.setText("Location");
			locationCol.setWidth(100);
			mExceptionTable.setHeaderVisible(true);
			
			mExceptionTable.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					TableItem[] selItems=mExceptionTable.getSelection();
					StringBuffer buffer=new StringBuffer();
					for (TableItem tableItem : selItems)
					{
						Exception ex=(Exception)tableItem.getData();
						StringWriter sw=new StringWriter();
						PrintWriter writer=new PrintWriter(sw);
						ex.printStackTrace(writer);
						buffer.append(sw.toString());
						buffer.append("\n");
					}
					mTextViewer.setText(buffer.toString());
					super.widgetSelected(e);
				}
			});
			
			//populate the table
			for (Exception ex : mExceptions)
			{
				TableItem item=new TableItem(mExceptionTable, SWT.None);
				item.setData(ex);
				if (ex instanceof NoViableAltException)
				{
					NoViableAltException nvae=(NoViableAltException)ex;
					Token t=nvae.token;
					String message=nvae.grammarDecisionDescription;
					if (t!=null)
						message="Unexpected token: "+t.getText();
					item.setText(0, message);
					int line=nvae.line;
					if (t!=null)
						line=t.getLine();
					item.setText(1, "Line: "+line);
				}
				else
				{
					item.setText(0, ex.toString());
					if (ex instanceof RecognitionException)
					{
						Token t=((RecognitionException)ex).token;
						if (t!=null)
						{
							item.setText(1, t.getLine()+"/"+t.getText());
						}
						else
						{
							item.setText(1, Integer.toString(((RecognitionException)ex).line));
						}
					}
				}
			}
			
			mTextViewer=new Text(comp, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
			GridData gd=new GridData(GridData.FILL_BOTH);
			gd.widthHint=300;
			gd.heightHint=300;
			mTextViewer.setLayoutData(gd);
			
			return comp;
		}
	}

	protected int getShellStyle()
	{
		return (super.getShellStyle() | SWT.RESIZE);
	}

}
