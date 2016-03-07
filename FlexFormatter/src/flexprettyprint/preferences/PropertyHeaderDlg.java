package flexprettyprint.preferences;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import flexasrearrangecodecommand.preferences.PreferenceConstants;

public class PropertyHeaderDlg extends TitleAreaDialog {
	private Button mNone;
	private Button mAll;
	private Button mAssociated;
	private Button mSmart;
	private int mCachedStyle;
	private boolean mAssociationsEnabled;
	
	public PropertyHeaderDlg(Shell shell, int headerConfig, boolean associations)
	{
		super(shell);
		mAssociationsEnabled=associations;
		mCachedStyle=headerConfig;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		setMessage("Choose the type of headers that should be added for properties", IMessageProvider.INFORMATION);
		getShell().setText("Default Property Headers");
		Composite comp=new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mNone=new Button(comp, SWT.RADIO);
		mNone.setText("No property headers");
		
		mAll=new Button(comp, SWT.RADIO);
		mAll.setText("Every property gets a 'minor' header");
		
		mAssociated=new Button(comp, SWT.RADIO);
		mAssociated.setText("Only multiple properties that are associated get a header.");
		mAssociated.setEnabled(mAssociationsEnabled);
		
		mSmart=new Button(comp, SWT.RADIO);
		mSmart.setText("All properties get a header if there are any associated properties in the same group.");
		mSmart.setEnabled(mAssociationsEnabled);
		
		switch (mCachedStyle)
		{
		case PreferenceConstants.PropertyHeaders_All:
			mAll.setSelection(true);
			break;
		case PreferenceConstants.PropertyHeaders_AssociatedConditional:
			mSmart.setSelection(true);
			break;
		case PreferenceConstants.PropertyHeaders_AssociatedOnly:
			mAssociated.setSelection(true);
			break;
		default:
			mNone.setSelection(true);
		}
		
		return comp;
	}
	
	public int getStyle()
	{
		return mCachedStyle;
	}

	@Override
	protected void okPressed() {
		mCachedStyle=PreferenceConstants.PropertyHeaders_None;
		if (mNone.getSelection())
			mCachedStyle=PreferenceConstants.PropertyHeaders_None;
		else if (mAll.getSelection())
			mCachedStyle=PreferenceConstants.PropertyHeaders_All;
		else if (mAssociated.getSelection())
			mCachedStyle=PreferenceConstants.PropertyHeaders_AssociatedOnly;
		else if (mSmart.getSelection())
			mCachedStyle=PreferenceConstants.PropertyHeaders_AssociatedConditional;
		super.okPressed();
	}
	
	
}
