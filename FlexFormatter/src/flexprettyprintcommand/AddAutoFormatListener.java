package flexprettyprintcommand;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListenerWithChecks;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.ITextEditor;

import flexprettyprint.handlers.FlexPrettyFormatHandler;
import flexprettyprint.preferences.AttrOrderConfigDialog;
import flexprettyprint.preferences.Initializer;

public class AddAutoFormatListener implements IStartup {

	private Map<IWorkbenchWindow, IPartListener> mWindowListenerMap=new HashMap<IWorkbenchWindow, IPartListener>();
	private static final String UndoCommand="org.eclipse.ui.edit.undo";
	private static final String RedoCommand="org.eclipse.ui.edit.redo";
	public static boolean mIsUndoing=false;

	public void earlyStartup()
	{
		ICommandService cs = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
		cs.addExecutionListener(new IExecutionListenerWithChecks() {

			private void clearUndoProcessing(String commandId)
			{
				if (commandId.equals(UndoCommand) || commandId.equals(RedoCommand))
				{
					System.out.println("undo/redo not handled/enabled/defined or the command failed");
					mIsUndoing=false;
				}
			}
			public void notEnabled(String commandId, NotEnabledException exception) 
			{
				clearUndoProcessing(commandId);
			}
			
			public void notDefined(String commandId, NotDefinedException exception) 
			{
				clearUndoProcessing(commandId);
			}
			
			public void notHandled(String commandId, NotHandledException exception) 
			{
				clearUndoProcessing(commandId);
			}

			public void postExecuteFailure(String commandId, ExecutionException exception) 
			{
				clearUndoProcessing(commandId);
			}

			public void postExecuteSuccess(String commandId, Object returnValue) {
				if (commandId.equals(UndoCommand) || commandId.equals(RedoCommand))
				{
					System.out.println("undo completed");
					mIsUndoing=false;
				}
			}

			public void preExecute(String commandId, ExecutionEvent event)
			{
				if (commandId.equals(UndoCommand) || commandId.equals(RedoCommand))
				{
					System.out.println("in undo/redo");
					mIsUndoing=true;
				}
			}
		});
		
		PlatformUI.getWorkbench().addWindowListener(new IWindowListener()
		{

			public void windowActivated(IWorkbenchWindow window) {
				addPartListeners(window);
			}

			public void windowClosed(IWorkbenchWindow window) {
				IPartListener pl=mWindowListenerMap.remove(window);
				if (pl!=null)
				{
					window.getPartService().removePartListener(pl);
				}
			}

			public void windowDeactivated(IWorkbenchWindow window)
			{
			}

			public void windowOpened(IWorkbenchWindow window)
			{
				addPartListeners(window);
			}
			
		});
		
		//code to add the editor listener to the editor that is initially open.  This is only necessary so that
		//if an editor is already open when Eclipse comes up, it gets a chance to add the save listener
		try
		{
			IWorkbenchWindow[] windows=PlatformUI.getWorkbench().getWorkbenchWindows();
			for (IWorkbenchWindow workbenchWindow : windows) {
				addPartListeners(workbenchWindow);
				final PartListener pl=(PartListener)mWindowListenerMap.get(workbenchWindow);
				if (pl!=null)
				{
					final IWorkbenchPart activePart=workbenchWindow.getPartService().getActivePart();
					if (activePart!=null)
					{
						Display.getDefault().asyncExec(new Runnable()
						{
							public void run() {
								pl.partActivated(activePart);		
							}
						});
					}
				}
			}
		}
		catch (Exception e)
		{
			Activator.logException(e, null);
		}
		
	}
	
	private void addPartListeners(IWorkbenchWindow window)
	{
		IPartListener pl=mWindowListenerMap.get(window);
		if (pl==null)
		{
			pl=new PartListener();
			mWindowListenerMap.put(window, pl);
			window.getPartService().addPartListener(pl);
		}
	}
	
	private static class PartListener implements IPartListener
	{
		private Map<IDocumentProvider, AutoFormatSaveListener> mEditorListenerMap=new HashMap<IDocumentProvider, AutoFormatSaveListener>();
		
		public void partActivated(IWorkbenchPart part)
		{
			addEditorListener(part);
		}

		public void partBroughtToTop(IWorkbenchPart part)
		{
			addEditorListener(part);
		}

		public void partClosed(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				ITextEditor editor=findEditor((IEditorPart)part);
				if (editor!=null)
				{
					AutoFormatSaveListener listener=mEditorListenerMap.get(editor.getDocumentProvider());
					if (listener!=null)
						listener.removeEditor(editor.getEditorInput());
				}
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
			// nothing to do
		}

		public void partOpened(IWorkbenchPart part)
		{
			addEditorListener(part);
		}
		
		private ITextEditor findEditor(IEditorPart ep)
		{
			IEditorInput input=ep.getEditorInput();
			ITextEditor editor=null;
			if (ep instanceof ITextEditor)
				editor=(ITextEditor)ep;
			else if (ep instanceof MultiPageEditorPart) 
			{
				MultiPageEditorPart mep=(MultiPageEditorPart)ep;
				IEditorPart[] editors=mep.findEditors(input);
				for (IEditorPart editorPart : editors) {
					if (editorPart instanceof ITextEditor)
					{
						editor=(ITextEditor)editorPart;
						break;
					}
				}
			}
			else
			{
				//I have to do this because the flex builder mxml editor is *not* a multipage editor part but
				//instead their own derived version of it.  So I use reflection to look for the "findEditors" 
				//method in any case, with appropriate error checking
				Method[] allMethods=ep.getClass().getMethods();
				methodFor:for (Method method : allMethods) {
					if (method.getName().equals("findEditors"))
					{
						try {
							Object result=method.invoke(ep, input);
							if (result instanceof IEditorPart[])
							{
								IEditorPart[] editors=(IEditorPart[])result;
								for (IEditorPart editorPart : editors) {
									if (editorPart instanceof ITextEditor)
									{
										editor=(ITextEditor)editorPart;
										break methodFor;
									}
								}
							}
						} catch (IllegalArgumentException e) {
							Activator.logException(e, null);
						} catch (IllegalAccessException e) {
							Activator.logException(e, null);
						} catch (InvocationTargetException e) {
							Activator.logException(e, null);
						}
					}
				}
			}
			
			return editor;
		}
		
		private void addEditorListener(IWorkbenchPart part)
		{
			try
			{
				if (part instanceof IEditorPart)
				{
					IEditorPart ep=(IEditorPart)part;
					IEditorInput input=ep.getEditorInput();
					Set<String> xmlExtensions=Initializer.getSet(Activator.getDefault().getPreferenceStore().getString(Initializer.Pref_MXML_AdditionalExtensions), true);
					if (input instanceof IPathEditorInput && input.exists())
					{
						IPath path=((IPathEditorInput)input).getPath();
						String lastSeg=path.lastSegment().toLowerCase();
						int lastDot=lastSeg.lastIndexOf('.');
						if (lastDot>=0)
							lastSeg=lastSeg.substring(lastDot+1);
						if (lastSeg.equals("as") || lastSeg.equals("mxml") || xmlExtensions.contains(lastSeg))
						{
							ITextEditor editor=findEditor(ep);
							if (editor!=null)
							{
								AutoFormatSaveListener listener=mEditorListenerMap.get(editor.getDocumentProvider());
								if (listener==null)
								{
									listener=new AutoFormatSaveListener();
									mEditorListenerMap.put(editor.getDocumentProvider(), listener);
									editor.getDocumentProvider().addElementStateListener(listener);
								}
	
								listener.addEditor(editor.getEditorInput(), editor);
							}
							else
							{
								System.out.println("FlexFormatter: unable to find editor for file.");
							}
						}
					}
					else
					{
						System.out.println("FlexFormatter: ignoring editor with no IPathEditorInput");
					}
				}
			}
			catch (Exception e)
			{
				Activator.logException(e, null);
			}
		}
	}
	
//	private static class ListenerHolder
//	{
//		public Map<IEditorInput, ITextEditor> mEditors=new HashMap<IEditorInput, ITextEditor>();
//		public AutoFormatSaveListener mListener;
//		public ListenerHolder(AutoFormatSaveListener listener)
//		{
//			mListener=listener;
//		}
//		
//		public void addEditor(IEditorInput input, ITextEditor newEditor)
//		{
//			mEditors.put(input, newEditor);
//		}
//		
//		public ITextEditor getEditor(IEditorInput input)
//		{
//			return mEditors.get(input);
//		}
//	}

	private static class AutoFormatSaveListener implements IElementStateListener
	{
		public Map<IEditorInput, ITextEditor> mEditors=new WeakHashMap<IEditorInput, ITextEditor>();
		private boolean mAlreadyFormatting;
		public AutoFormatSaveListener()
		{
			mAlreadyFormatting=false;
		}
		
		public void removeEditor(IEditorInput input) {
			mEditors.remove(input);
		}

		public void addEditor(IEditorInput input, ITextEditor newEditor)
		{
			mEditors.put(input, newEditor);
		}
		
		public ITextEditor getEditor(IEditorInput input)
		{
			return mEditors.get(input);
		}
		
		public void elementContentAboutToBeReplaced(Object element) {
			// nothing to do
		}

		public void elementContentReplaced(Object element) {
			// nothing to do
		}

		public void elementDeleted(Object element) {
			// nothing to do
		}

		public void elementDirtyStateChanged(Object element, boolean isDirty)
		{
			//TODO: do I need to keep undo/alreadyformatting flags per editor?  I hope not.
			if (isDirty)
				return;
			
			if (element instanceof IPathEditorInput)
			{
				final IPathEditorInput input=(IPathEditorInput)element;
				String fileName=input.getName();
				String ext="";
				int lastDot=fileName.lastIndexOf('.');
				if (lastDot>=0)
					ext=fileName.substring(lastDot+1).toLowerCase();
				boolean actionScriptFile=true;
				
				if (ext.equals("as"))
				{
					actionScriptFile=true;
				}
				else if (ext.equals("mxml"))
				{
					actionScriptFile=false;
				}
				else
				{
					Set<String> xmlExtensions=Initializer.getSet(Activator.getDefault().getPreferenceStore().getString(Initializer.Pref_MXML_AdditionalExtensions), true);
					if (!xmlExtensions.contains(ext))
						return;
					actionScriptFile=false;
				}
				
				final IPreferenceStore store=Activator.getDefault().getPreferenceStore();
				boolean autoFormat=actionScriptFile ? store.getBoolean(Initializer.Pref_AS_DoAutoFormat) : store.getBoolean(Initializer.Pref_MXML_DoAutoFormat);
				if (!autoFormat)
					return;
				
				final ITextEditor editor=getEditor(input);
				if (editor==null)
					return;
				
				//don't perform auto format if this file has been filtered out
				if (inExcludedFileList(input.getPath().toPortableString()))
					return;

				if (!mAlreadyFormatting && !AddAutoFormatListener.mIsUndoing)
				{
					mAlreadyFormatting=true;
					final boolean isASFile=actionScriptFile;
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							try
							{
								//make sure that the file still exists
								File absFile=input.getPath().toFile();
//								IFile fileResource=ResourcesPlugin.getWorkspace().getRoot().getFile(input.getPath());
//								if (fileResource.exists())
								if (absFile.exists())
								{
									boolean format=isASFile ? store.getBoolean(Initializer.Pref_AS_AutoFormatStyle) : store.getBoolean(Initializer.Pref_MXML_AutoFormatStyle);
									FlexPrettyFormatHandler.doFormat(editor, format ? FlexPrettyFormatHandler.Const_Format : FlexPrettyFormatHandler.Const_Indent, false, true);
									editor.doSave(null);
								}
								else
								{
									System.out.println("FlexFormatter: file doesn't exist: "+absFile.getAbsolutePath());
								}
							}
							catch (Exception e)
							{
								//to prevent a nullpointer exception or similar from disrupting Eclipse
								Activator.logException(e, "Auto format error");
							}
							finally
							{
								mAlreadyFormatting=false;
							}
						}
					});
				}
			}
		}

		public void elementMoved(Object originalElement, Object movedElement) {
			// nothing to do
		}
		
	}

	public static boolean inExcludedFileList(String filePath)
	{
		String canonicalPath=filePath.replace('\\', '/');
		String itemString=Activator.getDefault().getPreferenceStore().getString(Initializer.Pref_Flex_AutoFormat_ExcludePaths);
		String[] items=itemString.split(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		for (String path : items) {
			if (Pattern.matches(path, canonicalPath))
				return true;
		}
		
		return false;
	}
	
}
