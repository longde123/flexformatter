package flexprettyprint.preferences;

import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.ui.preferences.WorkingCopyManager;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import flexprettyprintcommand.Activator;

public class ProjectProperties extends PropertyPage implements IWorkbenchPropertyPage {

	private CommonPrefComposite mCommonPrefs;
	private static final String Key_UseProjectSettings="UseProjectSettings";
	private Button mEnableProjectSettings;
	
	public ProjectProperties() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite mainComp=new Composite(parent, SWT.None);
		GridLayout gl=new GridLayout();
//		gl.marginHeight=0;
//		gl.marginWidth=0;
		mainComp.setLayout(gl);
		mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite projectSpecificComp=new Composite(mainComp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginHeight=0;
		gl.marginWidth=0;
		projectSpecificComp.setLayout(gl);
		projectSpecificComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//TODO: add button to update with workspace settings
		mEnableProjectSettings=new Button(projectSpecificComp, SWT.CHECK);
		mEnableProjectSettings.setText("Enable project-specific settings");
		
		final Button copyGlobalButton=new Button(projectSpecificComp, SWT.PUSH);
		copyGlobalButton.setText("Copy from workspace preferences");

		
		mEnableProjectSettings.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				enableSpecificSettings(copyGlobalButton, mEnableProjectSettings.getSelection());
			}
		});
		copyGlobalButton.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				//get the global preferences and just update the widgets with them.
				IPreferenceStore prefStore=Activator.getDefault().getPreferenceStore();
				mCommonPrefs.updateWidgets(prefStore);
			}
		});

		//get the preference store to use to populate the common composite
		IPreferenceStore prefStore=Activator.getDefault().getPreferenceStore();
		IAdaptable item=this.getElement();
		if (item instanceof IProject)
		{
			prefStore=getProjectFormatterPreferences((IProject)item, true);
		}
		
		mCommonPrefs=new CommonPrefComposite(mainComp, SWT.None, !mEnableProjectSettings.getSelection(), prefStore);
		gl=new GridLayout();
		gl.marginHeight=0;
		gl.marginWidth=0;
		mCommonPrefs.setLayout(gl);
		mCommonPrefs.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//populate widgets
		mEnableProjectSettings.setSelection(prefStore.getBoolean(Key_UseProjectSettings));
		enableSpecificSettings(copyGlobalButton, mEnableProjectSettings.getSelection());
		return mainComp;
	}
	
	private void enableSpecificSettings(Button copyGlobalButton, boolean enable)
	{
		recursiveSetEnabled(mCommonPrefs, enable);
		copyGlobalButton.setEnabled(enable);
		mCommonPrefs.setDisableMode(!enable);
		if (enable)
			mCommonPrefs.enableWidgets();
	}
	
	public static IPreferenceStore getProjectFormatterPreferences(IProject project, boolean overrideToGetProject)
	{
		IWorkingCopyManager manager= new WorkingCopyManager();
		ProjectScope prefScope=new ProjectScope(project);
		IEclipsePreferences prefs=getPreferences(manager, prefScope);
		boolean useProjectSpecific=false;
		if (prefs!=null)
		{
			useProjectSpecific=prefs.getBoolean(Key_UseProjectSettings, false);
		}
		
		if (useProjectSpecific || overrideToGetProject)
		{
			try
			{
				IPreferenceStore baseStore=new PreferenceStore();
				IPreferenceStore prefStore=new ChainedPreferenceStore(new IPreferenceStore[]{baseStore, Activator.getDefault().getPreferenceStore()});
				for (String key : prefs.keys()) {
					String value=prefs.get(key, null);
					if (value!=null)
					{
						baseStore.setValue(key, value);
					}
				}
				return prefStore;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		//if project-specific flag not turned on or some error occurred
		return Activator.getDefault().getPreferenceStore();
	}

	public void recursiveSetEnabled(Control ctrl, boolean enabled) {
		if (ctrl instanceof Composite) {
			Composite comp = (Composite) ctrl;
			for (Control c : comp.getChildren())
				recursiveSetEnabled(c, enabled);
			comp.setEnabled(enabled);
		} else {
			ctrl.setEnabled(enabled);
		}
	}

	@Override
	protected void performDefaults() {
		mCommonPrefs.performDefaults();
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		//populate a properties object from the widgets
		Properties props=new Properties();
		mCommonPrefs.setProperties(props);
		props.setProperty(Key_UseProjectSettings, Boolean.toString(mEnableProjectSettings.getSelection()));
		
		boolean changed=false;
		IWorkingCopyManager manager= new WorkingCopyManager();
		IAdaptable item=this.getElement();
		if (item instanceof IProject)
		{
			IProject project=(IProject)item;
			ProjectScope prefScope=new ProjectScope(project);
			IEclipsePreferences prefs=getPreferences(manager, prefScope);
			for (Map.Entry<Object, Object> entry : props.entrySet())
			{
				String key=(String)entry.getKey();
				String value=(String)entry.getValue();
				String oldValue=prefs.get(key, "");
				if (!oldValue.equals(value))
					changed=true;
				setPreference(prefs, key, value);
			}
			
			if (changed)
			{
				try {
					manager.applyChanges();
				} catch (BackingStoreException e) {
					e.printStackTrace();
				}
			}
		}
		
		return super.performOk();
	}
	
	private static void setPreference(final IWorkingCopyManager manager, final IScopeContext context, final String key, final String value) {
		final IEclipsePreferences preferences= getPreferences(manager, context);
		setPreference(preferences, key, value);
	}
	
	private static void setPreference(IEclipsePreferences preferences, String key, String value)
	{
		if (value != null)
			preferences.put(key, value);
		else
			preferences.remove(key);
	}
	
	private static IEclipsePreferences getPreferences(final IWorkingCopyManager manager, final IScopeContext context) {
		final IEclipsePreferences preferences= context.getNode(Activator.PLUGIN_ID);
		if (manager != null)
			return manager.getWorkingCopy(preferences);
		return preferences;
	}
	
}
