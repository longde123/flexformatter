package flexprettyprint.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import flexprettyprintcommand.Activator;

public class AutoFormatPrefPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button mASDoAutoFormatButton;
	private Button mASFormatButton;
	private Button mASIndentButton;
	private Button mMXMLDoAutoFormatButton;
	private Button mMXMLFormatButton;
	private Button mMXMLIndentButton;
	private Table mAutoExcludeTable;
	private Button mShowBatchResultsInDialog;

	private Button mNewButton;
	private Button mEditButton;
	private Button mDeleteButton;
	
	private Table mXMLExtensionTable;
	private Button mXMLNewButton;
	private Button mXMLEditButton;
	private Button mXMLDeleteButton;
	
	public AutoFormatPrefPage() {
		// TODO Auto-generated constructor stub
	}

	public AutoFormatPrefPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public AutoFormatPrefPage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".FlexFormatter_Autosave_Options"); //$NON-NLS-1$
		Composite mainComp=new Composite(parent, SWT.None);
		mainComp.setLayout(new GridLayout());
		mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		SelectionListener widgetUpdater=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				enableWidgets();
			}
		};
		
		mASDoAutoFormatButton=new Button(mainComp, SWT.CHECK);
		mASDoAutoFormatButton.addSelectionListener(widgetUpdater);
		mASDoAutoFormatButton.setText("Format Actionscript files on save");
		mASDoAutoFormatButton.setToolTipText("Attempt to format the entire file when the file is saved via explicit Save operation or as the result of a Close.  May not work in call cases.");
		Composite asAutoFormatComposite=new Composite(mainComp, SWT.None);
		asAutoFormatComposite.setLayout(new GridLayout(2, false));
		mASFormatButton=new Button(asAutoFormatComposite, SWT.RADIO);
		mASFormatButton.setText("Format");
		mASIndentButton=new Button(asAutoFormatComposite, SWT.RADIO);
		mASIndentButton.setText("Indent");
		
		mMXMLDoAutoFormatButton=new Button(mainComp, SWT.CHECK);
		mMXMLDoAutoFormatButton.addSelectionListener(widgetUpdater);
		mMXMLDoAutoFormatButton.setText("Format MXML files on save");
		mMXMLDoAutoFormatButton.setToolTipText("Attempt to format the entire file when the file is saved via explicit Save operation or as the result of a Close.  May not work in call cases.");
		Composite mxmlAutoFormatComposite=new Composite(mainComp, SWT.None);
		mxmlAutoFormatComposite.setLayout(new GridLayout(2, false));
		mMXMLFormatButton=new Button(mxmlAutoFormatComposite, SWT.RADIO);
		mMXMLFormatButton.setText("Format");
		mMXMLIndentButton=new Button(mxmlAutoFormatComposite, SWT.RADIO);
		mMXMLIndentButton.setText("Indent");
		
		Group excludeComposite=new Group(mainComp, SWT.None);
		excludeComposite.setText("Paths to exclude from auto-format");
		excludeComposite.setLayout(new GridLayout(2, false));
		excludeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mAutoExcludeTable=new Table(excludeComposite, SWT.BORDER | SWT.SINGLE);
		mAutoExcludeTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mAutoExcludeTable.addSelectionListener(widgetUpdater);
		mAutoExcludeTable.setToolTipText("This table contains paths to exclude from auto/batch formatting of ActionScript files.  The intent is to allow\nyou to exclude certain packages from processing.  This does not affect direct invocation of formatting via toolbar or keystroke.");
		
		Composite buttonComp=new Composite(excludeComposite, SWT.None);
		buttonComp.setLayout(new GridLayout());
		
		mNewButton=new Button(buttonComp, SWT.PUSH);
		mNewButton.setText("New...");
		mNewButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				editItem(null);
			}
		});
		
		mEditButton=new Button(buttonComp, SWT.PUSH);
		mEditButton.setText("Edit...");
		mEditButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAutoExcludeTable.getSelectionIndex();
				if (selIndex>=0)
					editItem(mAutoExcludeTable.getItem(selIndex));
			}
		});

		mDeleteButton=new Button(buttonComp, SWT.PUSH);
		mDeleteButton.setText("Delete");
		mDeleteButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAutoExcludeTable.getSelectionIndex();
				if (selIndex>=0)
				{
					mAutoExcludeTable.remove(selIndex);
					
					selIndex=Math.min(selIndex, mAutoExcludeTable.getItemCount()-1);
					if (selIndex>=0)
						mAutoExcludeTable.setSelection(selIndex);
					enableWidgets();
				}
			}
		});
		
		//additional xml formatting extensions
		Group xmlComposite=new Group(mainComp, SWT.None);
		xmlComposite.setText("Additional extensions to format using mxml settings");
		xmlComposite.setLayout(new GridLayout(2, false));
		xmlComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mXMLExtensionTable=new Table(xmlComposite, SWT.BORDER | SWT.SINGLE);
		mXMLExtensionTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mXMLExtensionTable.addSelectionListener(widgetUpdater);
		mXMLExtensionTable.setToolTipText("Add extensions to be formatted as xml.\nCData sections will be formatted as plain text, not ActionScript.  Otherwise, the mxml settings are used.\nThis is a convenience setting since there is no xml formatter as part of the default Eclipse distribution.\nThis applies to all formatter invocations.\nNote: The default formatting keystroke may not apply to these file types, but you can add a keymapping.");
		
		buttonComp=new Composite(xmlComposite, SWT.None);
		buttonComp.setLayout(new GridLayout());
		
		mXMLNewButton=new Button(buttonComp, SWT.PUSH);
		mXMLNewButton.setText("New...");
		mXMLNewButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				editXMLExtension(null);
			}
		});
		
		mXMLEditButton=new Button(buttonComp, SWT.PUSH);
		mXMLEditButton.setText("Edit...");
		mXMLEditButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mXMLExtensionTable.getSelectionIndex();
				if (selIndex>=0)
					editXMLExtension(mXMLExtensionTable.getItem(selIndex));
			}
		});

		mXMLDeleteButton=new Button(buttonComp, SWT.PUSH);
		mXMLDeleteButton.setText("Delete");
		mXMLDeleteButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mXMLExtensionTable.getSelectionIndex();
				if (selIndex>=0)
				{
					mXMLExtensionTable.remove(selIndex);
					
					selIndex=Math.min(selIndex, mXMLExtensionTable.getItemCount()-1);
					if (selIndex>=0)
						mXMLExtensionTable.setSelection(selIndex);
					enableWidgets();
				}
			}
		});
		
		mShowBatchResultsInDialog=new Button(mainComp, SWT.CHECK);
		mShowBatchResultsInDialog.setText("Always show batch format results in dialog (otherwise in console)");

		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mASDoAutoFormatButton.setSelection(store.getBoolean(Initializer.Pref_AS_DoAutoFormat));
		mMXMLDoAutoFormatButton.setSelection(store.getBoolean(Initializer.Pref_MXML_DoAutoFormat));
		boolean format=store.getBoolean(Initializer.Pref_AS_AutoFormatStyle);
		mASFormatButton.setSelection(format);
		mASIndentButton.setSelection(!format);
		format=store.getBoolean(Initializer.Pref_MXML_AutoFormatStyle);
		mMXMLFormatButton.setSelection(format);
		mMXMLIndentButton.setSelection(!format);
		populateExcludeTable(store.getString(Initializer.Pref_Flex_AutoFormat_ExcludePaths));
		populateXMLExtensionTable(store.getString(Initializer.Pref_MXML_AdditionalExtensions));
		mShowBatchResultsInDialog.setSelection(store.getBoolean(Initializer.Pref_Flex_ShowBatchResultsInDialog));
		
		enableWidgets();
		return mainComp;
	}

	private void populateExcludeTable(String data)
	{
		mAutoExcludeTable.removeAll();
		String[] paths=data.split(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		for (String path : paths)
		{
			if (path.length()>0)
			{
				TableItem ti=new TableItem(mAutoExcludeTable, SWT.None);
				ti.setText(path);
			}
		}
	}

	private void populateXMLExtensionTable(String data)
	{
		mXMLExtensionTable.removeAll();
		String[] extensions=data.split(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		for (String ext : extensions)
		{
			if (ext.length()>0)
			{
				TableItem ti=new TableItem(mXMLExtensionTable, SWT.None);
				ti.setText(ext);
			}
		}
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mASDoAutoFormatButton.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_DoAutoFormat));
		mMXMLDoAutoFormatButton.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_DoAutoFormat));
		boolean format=store.getDefaultBoolean(Initializer.Pref_AS_AutoFormatStyle);
		mASFormatButton.setSelection(format);
		mASIndentButton.setSelection(!format);
		format=store.getDefaultBoolean(Initializer.Pref_MXML_AutoFormatStyle);
		mMXMLFormatButton.setSelection(format);
		mMXMLIndentButton.setSelection(!format);
		populateExcludeTable(store.getDefaultString(Initializer.Pref_Flex_AutoFormat_ExcludePaths));
		populateXMLExtensionTable(store.getDefaultString(Initializer.Pref_MXML_AdditionalExtensions));
		mShowBatchResultsInDialog.setSelection(store.getDefaultBoolean(Initializer.Pref_Flex_ShowBatchResultsInDialog));
		enableWidgets();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		store.setValue(Initializer.Pref_AS_AutoFormatStyle, mASFormatButton.getSelection());
		store.setValue(Initializer.Pref_MXML_AutoFormatStyle, mMXMLFormatButton.getSelection());
		store.setValue(Initializer.Pref_AS_DoAutoFormat, mASDoAutoFormatButton.getSelection());
		store.setValue(Initializer.Pref_MXML_DoAutoFormat, mMXMLDoAutoFormatButton.getSelection());
		
		StringBuffer buffer=new StringBuffer();
		TableItem[] items=mAutoExcludeTable.getItems();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		}
		store.setValue(Initializer.Pref_Flex_AutoFormat_ExcludePaths, buffer.toString());
		
		items=mXMLExtensionTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		}
		store.setValue(Initializer.Pref_MXML_AdditionalExtensions, buffer.toString());
		store.setValue(Initializer.Pref_Flex_ShowBatchResultsInDialog, mShowBatchResultsInDialog.getSelection());
		
		return super.performOk();
	}


	private void enableWidgets()
	{
		mASFormatButton.setEnabled(mASDoAutoFormatButton.getSelection());
		mASIndentButton.setEnabled(mASDoAutoFormatButton.getSelection());
		mMXMLFormatButton.setEnabled(mMXMLDoAutoFormatButton.getSelection());
		mMXMLIndentButton.setEnabled(mMXMLDoAutoFormatButton.getSelection());
		mNewButton.setEnabled(true);
		mDeleteButton.setEnabled(mAutoExcludeTable.getSelectionIndex()>=0);
		mEditButton.setEnabled(mAutoExcludeTable.getSelectionIndex()>=0);
		mXMLNewButton.setEnabled(true);
		mXMLDeleteButton.setEnabled(mXMLExtensionTable.getSelectionIndex()>=0);
		mXMLEditButton.setEnabled(mXMLExtensionTable.getSelectionIndex()>=0);
	}
	
	private void editItem(final TableItem editedItem)
	{
		InputDialog dlg=new InputDialog(getShell(), "New Exclude Path", "Enter an exclude path that should not be auto-formatted.  Use Java-style regular expressions and forward slashes.  Ex. .*/adobe/.*", (editedItem!=null) ? editedItem.getText() : null, new IInputValidator()
		{
			public String isValid(String newText)
			{
				String text=newText.trim();
				boolean inUse=false;
				TableItem[] items=mAutoExcludeTable.getItems();
				for (TableItem tableItem : items) {
					if (text.equals(tableItem.getText()) && (editedItem==null || !editedItem.getText().equals(tableItem.getText())))
					{
						inUse=true;
						break;
					}
				}
				
				if (inUse)
					return "Exclude path already specified";
				
				if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
					return "Whitespace in middle of string";
				if (text.indexOf(',')>=0)
					return "Cannot use commas (',') in regular expression";
				
				return null;
			}
			
		});
		if (dlg.open()==Dialog.OK)
		{
			String newTag=dlg.getValue();
			if (editedItem!=null)
				editedItem.setText(newTag);
			else
			{
				TableItem newItem=new TableItem(mAutoExcludeTable, SWT.None);
				newItem.setText(newTag);
				mAutoExcludeTable.setSelection(mAutoExcludeTable.getItemCount()-1);
			}
			enableWidgets();
		}
	}
	
	private void editXMLExtension(final TableItem editedItem)
	{
		InputDialog dlg=new InputDialog(getShell(), "New file extension", "Enter a file extension (ex. xml) that should be formatted as plain xml using the mxml formatter settings.", (editedItem!=null) ? editedItem.getText() : null, new IInputValidator()
		{
			public String isValid(String newText)
			{
				String text=newText.trim();
				boolean inUse=false;
				TableItem[] items=mXMLExtensionTable.getItems();
				for (TableItem tableItem : items) {
					if (text.equals(tableItem.getText()) && (editedItem==null || !editedItem.getText().equals(tableItem.getText())))
					{
						inUse=true;
						break;
					}
				}
				
				if (inUse)
					return "Extension already specified";
				
				for (int i=0; i<text.length();i++)
				{
					if (!Character.isLetterOrDigit(text.charAt(i)))
						return "Non alpha-numeric character ("+text.charAt(i)+") in extension";
				}
				
				return null;
			}
			
		});
		if (dlg.open()==Dialog.OK)
		{
			String newTag=dlg.getValue();
			if (editedItem!=null)
				editedItem.setText(newTag);
			else
			{
				TableItem newItem=new TableItem(mXMLExtensionTable, SWT.None);
				newItem.setText(newTag);
				mXMLExtensionTable.setSelection(mXMLExtensionTable.getItemCount()-1);
			}
			enableWidgets();
		}
	}
	
}
