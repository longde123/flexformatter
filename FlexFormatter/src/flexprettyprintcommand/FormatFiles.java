package flexprettyprintcommand;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import flexprettyprint.handlers.FlexPrettyFormatHandler;
import flexprettyprint.preferences.Initializer;

public class FormatFiles implements IActionDelegate {
	private ISelection mSelection;
	public FormatFiles() {
		mSelection=null;
	}

	public void run(IAction action) {
		FormatFiles.doFormat(FlexPrettyFormatHandler.Const_Format, mSelection);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		mSelection=selection;
	}
	
	public static void doFormat(final int formatStyle, ISelection selection)
	{
		if (selection==null)
		{
			return;
		}
		
		final List<IFile> filesToFormat=new ArrayList<IFile>();
		if (selection instanceof IFile)
		{
			filesToFormat.add((IFile)selection);
		}
		else if (selection instanceof IFolder || selection instanceof IProject)
		{
			addChildren((IResource)selection, filesToFormat);
		}
		else if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection structured=(IStructuredSelection)selection;
			for (Iterator iter = structured.iterator(); iter.hasNext();)
			{
				Object element = iter.next();
				if (element instanceof IFile)
				{
					filesToFormat.add((IFile)element);
				}
				else if (element instanceof IFolder || element instanceof IProject)
				{
					addChildren((IResource)element, filesToFormat);
				}
			}
		}
		
		if (filesToFormat.size()==0)
		{
			return;
		}
		
		ProgressMonitorDialog dlg=new ProgressMonitorDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		try {
			dlg.run(true, true, new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					monitor.beginTask("Formatting Flex files", filesToFormat.size());
					final List<IFile> successful=new ArrayList<IFile>();
					final List<IFile> formatFailed=new ArrayList<IFile>();
					final List<IFile> otherFailure=new ArrayList<IFile>();
					final List<IFile> filtered=new ArrayList<IFile>();
					final List<IFile> skipped=new ArrayList<IFile>();
					IPreferenceStore store=Activator.getDefault().getPreferenceStore();
					boolean asAutoFormatWasOn=store.getBoolean(Initializer.Pref_AS_DoAutoFormat);
					boolean mxmlAutoFormatWasOn=store.getBoolean(Initializer.Pref_MXML_DoAutoFormat);
					Set<String> mxmlExtensions=Initializer.getSet(store.getString(Initializer.Pref_MXML_AdditionalExtensions), true);
					try
					{
						store.setValue(Initializer.Pref_AS_DoAutoFormat, false);
						store.setValue(Initializer.Pref_MXML_DoAutoFormat, false);
						for (IFile file : filesToFormat)
						{
							if (monitor.isCanceled())
								break;
							final IFile fileFinal=file;
							monitor.setTaskName("Processing: "+file.getFullPath());
							String ext=file.getFileExtension();
							if (ext!=null)
								ext=ext.toLowerCase();
							if (AddAutoFormatListener.inExcludedFileList(file.getFullPath().toPortableString()))
							{
								filtered.add(file);
							}
							else if (ext!=null && (ext.equals("mxml") || ext.equals("as") || mxmlExtensions.contains(ext)))
							{
								PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
								{
									public void run() {
										try
										{
											IEditorPart editorPart=FormatFiles.isOpenInEditor(fileFinal);
											boolean notAlreadyOpen=(editorPart==null);
											if (editorPart==null)
											{
												IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
												if (window != null)
												{
													IWorkbenchPage page=window.getActivePage();
													if (page!=null)
													{
														editorPart= IDE.openEditor(page, fileFinal, true);
													}
												}
											}

											if (editorPart!=null)
											{
												boolean success=FlexPrettyFormatHandler.doFormat(editorPart, formatStyle, false, true);
												if (success)
												{
													editorPart.doSave(null);
													successful.add(fileFinal);
												}
												else
												{
													formatFailed.add(fileFinal);
												}

												//if it wasn't open before, close it now
												if (notAlreadyOpen)
												{
													IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
													if (window != null)
													{
														IWorkbenchPage page=window.getActivePage();
														if (page!=null)
														{
															page.closeEditor(editorPart, false);
														}
													}
												}
											}
											else
											{
												otherFailure.add(fileFinal);
											}
										} catch (PartInitException e) {
											Activator.logException(e, "Bulk FlexFormat");
										}
										finally{}
									}
								});


								monitor.worked(1);
							}
							else
							{
								skipped.add(file);
							}
						}
					}
					finally
					{
						monitor.done();
						if (asAutoFormatWasOn)
							store.setValue(Initializer.Pref_AS_DoAutoFormat, true);
						if (mxmlAutoFormatWasOn)
							store.setValue(Initializer.Pref_MXML_DoAutoFormat, true);
					}

					if (formatFailed.size()>0 || otherFailure.size()>0 || Activator.getDefault().getPreferenceStore().getBoolean(Initializer.Pref_Flex_ShowBatchResultsInDialog))
					{
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
						{
							public void run() {
								BatchResultDialog brd=new BatchResultDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), successful, formatFailed, skipped, filtered, otherFailure);
								brd.open();
							}
						});
					}
					else
					{
						//put the results in a console window
						FormatterConsole.getConsole().writeln("Batch format performed at: "+SimpleDateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())));
						FormatterConsole.getConsole().writeln(BatchResultDialog.getPrintString(successful, formatFailed, otherFailure, skipped, filtered));
					}
				}
			});
		} catch (InvocationTargetException e1) {
			Activator.logException(e1, null);
		} catch (InterruptedException e1) {
			Activator.logException(e1, null);
		}
	}

	private static void addChildren(IResource selection, final List<IFile> filesToFormat)
	{
		try {
			selection.accept(new IResourceVisitor()
			{
				public boolean visit(IResource resource) throws CoreException
				{
					if (resource instanceof IFile)
					{
						filesToFormat.add((IFile)resource);
					}
					else if (resource instanceof IFolder)
					{
						return true;
					}
					else if (resource instanceof IProject)
					{
						return true;
					}
					return false;
				}
			});
		} catch (CoreException e) {
			Activator.logException(e, null);
		}
		
	}

	public static IEditorPart isOpenInEditor(IFile inputElement)
	{
		IEditorInput input= new FileEditorInput(inputElement);

//		if (input != null) {
			IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null)
				return null;
			IWorkbenchPage page=window.getActivePage();
			if (page==null)
				return null;
			return page.findEditor(input);
//		}

//		return null;
	}
	
}
