package flexprettyprint.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import actionscriptinfocollector.ParseErrorDialog;
import flexprettyprintcommand.Activator;

public class PrefPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private CommonPrefComposite mCommonPrefs;
	
	public PrefPage() {
	}

	public PrefPage(String title) {
		super(title);
	}

	public PrefPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".FlexFormatter_Formatting_Options"); //$NON-NLS-1$
		try
		{
			String version=(String)Activator.getDefault().getBundle().getHeaders().get("BUNDLE-VERSION");
			setTitle(getTitle()+" ("+version+")--"+"Update site: http://flexformatter.googlecode.com/svn/trunk/FlexFormatter/FlexPrettyPrintCommandUpdateSite");
		}
		catch (Exception e)
		{
			Activator.logException(e, null);
		}

		
		Composite comp=new Composite(parent, SWT.None);
		GridLayout gl=new GridLayout();
		gl.marginWidth=0;
		gl.marginHeight=0;
		comp.setLayout(gl);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mCommonPrefs=new CommonPrefComposite(comp, SWT.None, false, Activator.getDefault().getPreferenceStore());
		gl=new GridLayout();
		mCommonPrefs.setLayout(gl);
		mCommonPrefs.setLayoutData(new GridData(GridData.FILL_BOTH));
		return comp;
	}
	
	public static void showErrors(Shell shell, List<Exception> errors, String internalError)
	{
		ParseErrorDialog dlg=new ParseErrorDialog(shell, errors, internalError);
		dlg.open();
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void performDefaults()
	{
		mCommonPrefs.performDefaults();
		super.performDefaults();
	}
	
	@Override
	public boolean performOk()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		Properties props=new Properties();
		mCommonPrefs.setProperties(props);
		boolean changed=false;
		for (Map.Entry<Object, Object> entry : props.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			String oldValue=store.getString(key);
			if (!oldValue.equals(value))
				changed=true;
			store.putValue(key, value);
		}

		//OR the current change flag with the existing one.  The change flag is cleared only by loading from the autosync file
		//or below if we are saving the preferences ourselves.
		store.setValue(Initializer.Pref_Flex_AutoSyncLocalChangesDirty, changed | store.getBoolean(Initializer.Pref_Flex_AutoSyncLocalChangesDirty));

		//if we are have options autosync turned on and we are also writing back to that file, then this is where we
		//do the writing
		if (store.getBoolean(Initializer.Pref_Flex_UseAutoSyncFile) && store.getBoolean(Initializer.Pref_Flex_WriteToAutoSyncFile))
		{
			try
			{
				String workspaceSyncFilePath=store.getString(Initializer.Pref_Flex_AutoSyncFile);
				IPath path=new Path(workspaceSyncFilePath);
				IFile file=ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				File saveFile=file.getLocation().toFile();
				Properties oldProperties=new Properties();
				if (saveFile.exists())
				{
					InputStream oldStream=new FileInputStream(saveFile);
					oldProperties.load(oldStream);
					oldStream.close();
				}
				if (!oldProperties.equals(props)) //don't write file if it's the same
				{
					store.setValue(Initializer.Pref_Flex_AutoSyncLocalChangesDirty, false);
					OutputStream os=new FileOutputStream(saveFile);
					props.store(os, "FlexPrettyPrintSettings");
					os.close();
					file.refreshLocal(1, null);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		return super.performOk();
	}

}
