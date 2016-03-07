package flexprettyprintcommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import flexprettyprint.preferences.Initializer;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "FlexPrettyPrintCommand";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if (getPreferenceStore().getBoolean(Initializer.Pref_Flex_CheckForUpdates))
			checkForUpdates(false);
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener()
		{
			public void resourceChanged(IResourceChangeEvent event)
			{
				IPreferenceStore store=getPreferenceStore();
				if (!store.getBoolean(Initializer.Pref_Flex_UseAutoSyncFile))
					return;
				
				String syncFile=store.getString(Initializer.Pref_Flex_AutoSyncFile);
				if (syncFile.length()==0)
					return;
				
				IPath path=new Path(syncFile);
				
				IResourceDelta delta=event.getDelta();
				IResourceDelta changedItem=delta.findMember(path);
				if (changedItem!=null)
				{
					IFile settingsResource=ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					File settingsFile=settingsResource.getLocation().toFile();
					if (settingsFile.exists())
					{
						reloadSettingsFromFile(settingsFile);
					}
					else
					{
						logException(null, "FlexFormatter: autosync file not found->"+path.toPortableString());
					}
				}
				
				
			}
		}, IResourceChangeEvent.POST_CHANGE);
	}
	
	public static void reloadSettingsFromFile(File propFile)
	{
		Properties props=new Properties();
		try
		{
			IPreferenceStore store=Activator.getDefault().getPreferenceStore();
			
			boolean localChanges=store.getBoolean(Initializer.Pref_Flex_AutoSyncLocalChangesDirty);
			if (localChanges)
			{
				if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Overwrite local FlexFormatter settings", "The FlexFormatter settings auto-sync file has changed.  However, it appears that you have made local settings changes.  Do you want to continue and replace your local settings?"))
				{
					store.setValue(Initializer.Pref_Flex_AutoSyncLocalChangesDirty, false);
				}
				else
					return;
			}
			
			InputStream stream=new FileInputStream(propFile);
			props.load(stream);
			stream.close();
			
			for (Map.Entry<Object, Object> entry : props.entrySet())
			{
				String key=(String)entry.getKey();
				String value=(String)entry.getValue();
				store.setValue(key, value);
			}
		}
		catch (Exception e)
		{
			Activator.logException(e, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void logException(Exception t, String msg)
	{
		if (null == msg) {
			msg = "Flex pretty print command exception"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, msg, t);
		getDefault().getLog().log(status);
	}

	/**
	 * This method puts out a check for updates on a separate thread (to prevent locking the
	 * UI if there are network problems).  It posts back to the main thread with a dialog
	 * if there are updates (or always if the flag is true)
	 */
	public void checkForUpdates(final boolean alwaysShowResult)
	{
		Job j=new Job("FlexFormatter-check for updates")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				String siteURL="http://flexformatter.googlecode.com/svn/trunk/FlexFormatter/CurrentOfficialVersion.txt?sid=" + System.currentTimeMillis();
				BufferedReader in = null;
				try
				{
					String version=(String)Activator.getDefault().getBundle().getHeaders().get("BUNDLE-VERSION");
					URL ffSite = new URL(siteURL);
					in = new BufferedReader(new InputStreamReader(ffSite.openStream()));
					String officialVersion=in.readLine();
					in.close();
					
					if (officialVersion==null || officialVersion.trim().length()==0)
					{
						if (alwaysShowResult)
						{
							showMessage(IMessageProvider.ERROR, "Failed to read data from the GoogleCode server.  Maybe try again later.");
						}
						return Status.OK_STATUS;
					}
					if (version.equals(officialVersion))
					{
						if (alwaysShowResult)
						{
							showMessage(IMessageProvider.INFORMATION, "You have the latest official version: "+officialVersion);
						}
						return Status.OK_STATUS;
					}
	
					int compare=compareVersions(version, officialVersion);
					if (compare==mCompareError)
					{
						if (alwaysShowResult)
						{
							showMessage(IMessageProvider.ERROR, "Internal Error comparing versions.");
						}
					}
					else if (compare==0)
					{
						if (alwaysShowResult)
						{
							showMessage(IMessageProvider.INFORMATION, "You have the latest official release: "+officialVersion);
						}
					}
					else if (compare<0)
					{
						showMessage(IMessageProvider.WARNING, "A newer version of FlexFormatter exists: "+officialVersion+". Go to https://sourceforge.net/projects/flexformatter/ to download the official version.");
					}
					else //if compare>0
					{
						if (alwaysShowResult)
						{
							showMessage(IMessageProvider.INFORMATION, "You have an experimental version of FlexFormatter: "+version+". The latest official version is "+officialVersion);
						}
					}
				}
				catch (ConnectException ex)
				{
					if (alwaysShowResult)
						showMessage(IMessageProvider.ERROR, "Could not connect to external site to check version of FlexFormatter: "+siteURL+"\nThe external site may be down or, if you are behind a firewall, you may need to configure Eclipse proxy settings under Window->Preferences(General/Network Connections)");
				}
				catch (Exception ex)
				{
					Activator.logException(ex, null);
				}
				finally
				{
					if (in!=null)
					{
						try {
							in.close();
						} catch (IOException e1) {
							Activator.logException(e1, null);
						}
					}
				}
				return Status.OK_STATUS;
			}
			
			private void showMessage(final int messageType, final String message)
			{
				final String title="Check for FlexFormatter updates";
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						if (messageType==IMessageProvider.ERROR)
							MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
						else if (messageType==IMessageProvider.WARNING)
							MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, message);
						else
							MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);
					}
				});
			}
			
		};
		
		if (alwaysShowResult)
			j.setUser(true);
		j.schedule();
	}
	
	private static final int mCompareError=-2;
	public static int compareVersions(String v1, String v2)
	{
		String[] parts1=v1.split("\\.");
		String[] parts2=v2.split("\\.");
		if (parts1.length<3 || parts2.length<3)
			return mCompareError;
		
		for (int i = 0; i < 3; i++) {
			String p1 = parts1[i];
			String p2 = parts2[i];
			try
			{
				int n1=Integer.parseInt(p1);
				int n2=Integer.parseInt(p2);
				if (n1<n2)
					return -1;
				else if (n1>n2)
					return 1;
			}
			catch (NumberFormatException e)
			{
				return mCompareError;
			}
		}
		
		//must be equal in major/minor/micro versions
		return 0;
	}

//	public static String getExtension(IEditorPart editor)
//	{
//		if (editor==null)
//			return "";
//		
//		IEditorInput input=editor.getEditorInput();
//		if (input instanceof IPathEditorInput)
//		{
//			IPath path=((IPathEditorInput)input).getPath();
//			String lastSeg=path.lastSegment().toLowerCase();
//			int lastDot=lastSeg.lastIndexOf('.');
//			if (lastDot>=0)
//				return lastSeg.substring(lastDot+1);
//		}
//		
//		return "";
//	}
	
}
