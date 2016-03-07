package flexprettyprint.preferences;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import flexprettyprintcommand.Activator;

public class UpdateCheckerPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button mFlexCheckForUpdatesAtStartupButton;
	private Text mSettingsLocation;
	private Button mAutoSyncSettings;
	private Button mAutoSyncSave;
	private Button mBrowseButton;
	
	public UpdateCheckerPage() {
	}

	public UpdateCheckerPage(String title) {
		super(title);
	}

	public UpdateCheckerPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".FlexFormatter_Update_Options"); //$NON-NLS-1$

		Composite comp=new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		
		Text location=new Text(comp, SWT.MULTI | SWT.READ_ONLY);
		location.setText("Eclipse update site: http://flexformatter.googlecode.com/svn/trunk/FlexFormatter/FlexPrettyPrintCommandUpdateSite\nOr look for new updates at: http://sourceforge.net/project/showfiles.php?group_id=248408\nYou can also use the button or checkbox below to see if there is a new version.");
		
		Group updateGroup=new Group(comp, SWT.None);
		updateGroup.setLayout(new GridLayout(2, false));
		updateGroup.setText("FlexFormatter updates");
		
		Button checkForUpdatesButton=new Button(updateGroup, SWT.PUSH);
		checkForUpdatesButton.setText("Check for updates...");
		checkForUpdatesButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Activator.getDefault().checkForUpdates(true);
			}
		});
		
		mFlexCheckForUpdatesAtStartupButton=new Button(updateGroup, SWT.CHECK);
		mFlexCheckForUpdatesAtStartupButton.setText("Check for updates at startup");
		mFlexCheckForUpdatesAtStartupButton.setSelection(store.getBoolean(Initializer.Pref_Flex_CheckForUpdates));
		
		
		Group autoSyncGroup=new Group(comp, SWT.None);
		autoSyncGroup.setLayout(new GridLayout(4, false));
		autoSyncGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		autoSyncGroup.setText("Settings updates");
		
		mAutoSyncSettings=new Button(autoSyncGroup, SWT.CHECK);
		mAutoSyncSettings.setText("Auto sync from file:");
		mAutoSyncSettings.setToolTipText("If checked, use the workspace-relative path to a properties file to update the formatter settings.  This file should be pushed to source control and be located in a project that is always open in the workspace.");
		mAutoSyncSettings.setSelection(store.getBoolean(Initializer.Pref_Flex_UseAutoSyncFile));
		mAutoSyncSettings.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableWidgets();
			}
		});
		
//		Label l=new Label(autoSyncGroup, SWT.None);
//		l.setText("Settings file location:");
		
		mSettingsLocation=new Text(autoSyncGroup, SWT.SINGLE | SWT.BORDER);
		mSettingsLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mSettingsLocation.setText(store.getString(Initializer.Pref_Flex_AutoSyncFile));
		
		mBrowseButton=new Button(autoSyncGroup, SWT.PUSH);
		mBrowseButton.setText("Browse...");
		mBrowseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
//				PlatformUI.getWorkbench().get
				ResourceListSelectionDialog dlg=new ResourceListSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
				dlg.setTitle("Select FlexFormatter settings file");
//				if (mSettingsLocation.getText().length()>0)
//				{
//					IFile selectedFile=null;
//					try
//					{
//						selectedFile=ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(mSettingsLocation.getText()));	
//					}
//					catch (Exception e1)
//					{
//						Activator.logException(e1, null);
//					}
//					dlg.setInitialSelections(new Object[]{selectedFile});
//				}
				if (dlg.open()==Dialog.OK)
				{
					Object[] results=dlg.getResult();
					if (results.length>=1)
					{
						mSettingsLocation.setText(((IResource)results[0]).getFullPath().toPortableString());
					}
				}
				
//				FileDialog dlg=new FileDialog(getShell());//ResourceSelectionDialog(getShell(), ResourcesPlugin.getWorkspace(), "Select FlexFormatter settings file");
//				String selectedFile=dlg.open();
//				if (selectedFile!=null)
//				{
//					mSettingsLocation.setText(selectedFile);
//				}
				
			}
		});
		
		mAutoSyncSave=new Button(autoSyncGroup, SWT.CHECK);
		mAutoSyncSave.setText("Save to sync file:");
		mAutoSyncSave.setToolTipText("If checked, save to the autosync file when modifying preferences via the preference page.");
		mAutoSyncSave.setSelection(store.getBoolean(Initializer.Pref_Flex_WriteToAutoSyncFile));
		
		enableWidgets();
		return comp;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performDefaults()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mFlexCheckForUpdatesAtStartupButton.setSelection(store.getDefaultBoolean(Initializer.Pref_Flex_CheckForUpdates));
		mAutoSyncSettings.setSelection(store.getDefaultBoolean(Initializer.Pref_Flex_UseAutoSyncFile));
		mAutoSyncSave.setSelection(store.getDefaultBoolean(Initializer.Pref_Flex_WriteToAutoSyncFile));
		mSettingsLocation.setText(store.getDefaultString(Initializer.Pref_Flex_AutoSyncFile));
		enableWidgets();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		store.setValue(Initializer.Pref_Flex_CheckForUpdates, mFlexCheckForUpdatesAtStartupButton.getSelection());
		store.setValue(Initializer.Pref_Flex_UseAutoSyncFile, mAutoSyncSettings.getSelection());
		store.setValue(Initializer.Pref_Flex_AutoSyncFile, mSettingsLocation.getText());
		store.setValue(Initializer.Pref_Flex_WriteToAutoSyncFile, mAutoSyncSave.getSelection());
		return super.performOk();
	}


	private void enableWidgets()
	{
		mSettingsLocation.setEnabled(mAutoSyncSettings.getSelection());
		mBrowseButton.setEnabled(mAutoSyncSettings.getSelection());
		mAutoSyncSave.setEnabled(mAutoSyncSettings.getSelection());
	}
	
}
