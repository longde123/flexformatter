package flexprettyprint.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprintcommand.Activator;

public class EditGroupsDlg extends TitleAreaDialog
{
	private List<AttrGroup> mGroups;
	private Set<String> mUsedGroups;
	private String mChosenGroup;
	
	private Button mNewGroupButton;
	private Button mDeleteGroupButton;
	private Button mRenameGroupButton;
	
	private Button mRestoreDefaultGroupsButton;
	
	private Button mMoveAttrToTopButton;
	private Button mMoveAttrUpButton;
	private Button mMoveAttrDownButton;
	private Button mAddAttrButton;
	private Button mUpdateAttrButton;
	private Button mDeleteAttrButton;
	private Text mAddAttrText;
	
	private Button mIncludeStates;
	
	private Button mRadioSortNone;
	private Button mRadioSortAsc;
//	private Button mRadioSortAscNoCase;
	private Button mRadioSortGroup;

	private Button mRadioWrapNone;
	private Button mRadioWrapAttrCount;
	private Button mRadioWrapLineLength;
	private Button mRadioWrapDefault;
	private Spinner mCustomWrapCount;
	private Button mUseDefaultWrapCount;
	
	private Table mAttrTable;
	private Table mGroupTable;
	
	public EditGroupsDlg(Shell shell, List<AttrGroup> groups, Set<String> usedGroups)
	{
		super(shell);
		mGroups=new ArrayList<AttrGroup>();
		for (AttrGroup attrGroup : groups) {
			mGroups.add(attrGroup.copy());
		}
		mUsedGroups=usedGroups;
		setShellStyle(getShellStyle()|SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".MXML_GroupEditingDlg"); //$NON-NLS-1$
		getShell().setText("Edit attribute groups");
		
		Composite main=new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite groupComp=new Composite(main, SWT.None);
		groupComp.setLayout(new GridLayout());
		groupComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l=new Label(groupComp, SWT.None);
		l.setText("Groups");
		
		Composite groupTable=new Composite(groupComp, SWT.None);
		groupTable.setLayout(new GridLayout(2, false));
		groupTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		mGroupTable=new Table(groupTable, SWT.BORDER | SWT.SINGLE);
		mGroupTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mGroupTable.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				populateAttrTable();
				enableWidgets();
			}
		});
		
		Composite groupButtons=new Composite(groupTable, SWT.None);
		groupButtons.setLayout(new GridLayout(1, true));
		groupButtons.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mNewGroupButton=new Button(groupButtons, SWT.PUSH);
		mNewGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mNewGroupButton.setText("New...");
		mNewGroupButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final Set<String> groupNames=new HashSet<String>();
				TableItem[] items=mGroupTable.getItems();
				for (TableItem item: items) {
					AttrGroup group=(AttrGroup)item.getData();
					groupNames.add(group.getName());
				}
				InputDialog dlg=new InputDialog(getShell(), "New Group", "Enter the name of the new group", "", new IInputValidator()
				{
					public String isValid(String newText)
					{
						if (newText.length()==0)
							return "Group name must not be empty";
						
						if (!groupNames.contains(newText))
							return null;
						
						return "Group name already exists";
					}
				});
				
				if (dlg.open()==Dialog.OK)
				{
					String newGroup=dlg.getValue();
					AttrGroup group=new AttrGroup(newGroup, new ArrayList<String>(), MXMLPrettyPrinter.MXML_Sort_None, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true);
					mGroups.add(group);
					populateGroupTable();
					mGroupTable.select(mGroupTable.getItemCount()-1);
					populateAttrTable();
					enableWidgets();
				}
			}
		});
		
		mDeleteGroupButton=new Button(groupButtons, SWT.PUSH);
		mDeleteGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mDeleteGroupButton.setText("Delete");
		mDeleteGroupButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mGroupTable.getSelectionIndex();
				if (selIndex>=0)
				{
					mGroups.remove(selIndex);
					populateGroupTable();
					if (selIndex<mGroupTable.getItemCount())
					{
						mGroupTable.setSelection(selIndex);
					}
					else if (mGroupTable.getItemCount()>0)
					{
						mGroupTable.setSelection(mGroupTable.getItemCount()-1);
					}
					enableWidgets();
				}
			}
		});
		
		mRenameGroupButton=new Button(groupButtons, SWT.PUSH);
		mRenameGroupButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mRenameGroupButton.setText("Rename...");
		mRenameGroupButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mGroupTable.getSelectionIndex();
				if (selIndex>=0)
				{
					String currentGroupName=mGroups.get(selIndex).getName();
					final Set<String> groupNames=new HashSet<String>();
					TableItem[] items=mGroupTable.getItems();
					for (TableItem item: items) {
						AttrGroup group=(AttrGroup)item.getData();
						groupNames.add(group.getName());
					}
					groupNames.remove(currentGroupName);
					InputDialog dlg=new InputDialog(getShell(), "Rename Group", "Enter the name of the new group", currentGroupName, new IInputValidator()
					{
						public String isValid(String newText)
						{
							if (newText.length()==0)
								return "Group name must not be empty";
							
							if (!groupNames.contains(newText))
								return null;
							
							return "Group name already exists";
						}
					});
					
					if (dlg.open()==Dialog.OK)
					{
						String newGroup=dlg.getValue();
						AttrGroup group=mGroups.get(selIndex);
						group.setName(newGroup);
						populateGroupTable();
						mGroupTable.select(selIndex);
						enableWidgets();
					}
				}
			}
		});
		
		mRestoreDefaultGroupsButton=new Button(groupTable, SWT.PUSH);
		mRestoreDefaultGroupsButton.setText("Update default groups");
		mRestoreDefaultGroupsButton.setToolTipText("Add/Update the default groups in the group list.  Groups with identical names will have new attributes appended.\nThe idea is to allow safe updating when new attributes are added as part of the base plugin.");
		mRestoreDefaultGroupsButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String defaultData=Activator.getDefault().getPreferenceStore().getDefaultString(Initializer.Pref_MXML_AttrGroups);
				String[] defaultGroups=defaultData.split(CommonPrefComposite.LineSplitter);
				for (String g : defaultGroups) {
					AttrGroup group=AttrGroup.load(g);
					if (group!=null)
					{
						boolean found=false;
						for (int i=0;i<mGroups.size();i++)
						{
							if (group.getName().equals(mGroups.get(i).getName()))
							{
								//update group with new info
								
								//build hash of old attrs
								Set<String> existingAttrs=new HashSet<String>();
								for (String attr : mGroups.get(i).getAttrs()) {
									existingAttrs.add(attr);
								}
								
								//walk default attrs and add ones that are missing to end
								for (String attr : group.getAttrs())
								{
									//if not found
									if (!existingAttrs.contains(attr))
									{
										//add to end of group
										mGroups.get(i).getAttrs().add(attr);
									}
								}
								found=true;
							}
						}
						if (!found)
						{
							mGroups.add(group);
						}
					}
				}
				populateGroupTable();
				populateAttrTable();
				enableWidgets();
			}
		});

		
		Composite attrComp=new Composite(main, SWT.None);
		attrComp.setLayout(new GridLayout());
		attrComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		l=new Label(attrComp, SWT.None);
		l.setText("Attributes");
		
		Composite attrTable=new Composite(attrComp, SWT.None);
		attrTable.setLayout(new GridLayout(2, false));
		attrTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mAttrTable=new Table(attrTable, SWT.BORDER | SWT.SINGLE);
		GridData gd=new GridData(GridData.FILL_BOTH);
		gd.heightHint=mAttrTable.getItemHeight()*10;
		mAttrTable.setLayoutData(gd);
		mAttrTable.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selItems=mAttrTable.getSelection();
				if (selItems.length==1)
				{
					mAddAttrText.setText(selItems[0].getText());
				}
				enableWidgets();
			}
		});
		
		Composite attrButtons=new Composite(attrTable, SWT.None);
		attrButtons.setLayout(new GridLayout(1, true));
		attrButtons.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mMoveAttrUpButton=new Button(attrButtons, SWT.PUSH);
		mMoveAttrUpButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mMoveAttrUpButton.setText("Move Up");
		mMoveAttrUpButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIndex=mAttrTable.getSelectionIndex();
				if (selIndex>=0)
				{
					AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
					List<String> attrs=group.getAttrs();
					String swap=attrs.get(selIndex);
					attrs.set(selIndex, attrs.get(selIndex-1));
					attrs.set(selIndex-1, swap);
					populateAttrTable();
					mAttrTable.setSelection(selIndex-1);
					enableWidgets();
				}
			}
		});

		mMoveAttrToTopButton=new Button(attrButtons, SWT.PUSH);
		mMoveAttrToTopButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mMoveAttrToTopButton.setText("Move To Top");
		mMoveAttrToTopButton.setToolTipText("Make this attribute the first one in the list");
		mMoveAttrToTopButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				if (selIndex>=0)
				{
					AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
					List<String> attrs=group.getAttrs();
					String swap=attrs.get(selIndex);
					attrs.remove(selIndex);
					attrs.add(0, swap);
					populateAttrTable();
					if (selIndex+1<attrs.size())
						selIndex++;
					mAttrTable.setSelection(selIndex);
					enableWidgets();
				}
			}
		});
		
		mMoveAttrDownButton=new Button(attrButtons, SWT.PUSH);
		mMoveAttrDownButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mMoveAttrDownButton.setText("Move Down");
		mMoveAttrDownButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selIndex=mAttrTable.getSelectionIndex();
				if (selIndex>=0)
				{
					AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();					
					List<String> attrs=group.getAttrs();
					String swap=attrs.get(selIndex);
					attrs.set(selIndex, attrs.get(selIndex+1));
					attrs.set(selIndex+1, swap);
					populateAttrTable();
					mAttrTable.setSelection(selIndex+1);
					enableWidgets();
				}
			}
		});
		
		mDeleteAttrButton=new Button(attrButtons, SWT.PUSH);
		mDeleteAttrButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mDeleteAttrButton.setText("Delete");
		mDeleteAttrButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				if (selIndex>=0)
				{
					AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
					group.getAttrs().remove(selIndex);
					populateAttrTable();
					if (selIndex<mAttrTable.getItemCount())
						mAttrTable.setSelection(selIndex);
					else if (mAttrTable.getItemCount()>0)
						mAttrTable.setSelection(mAttrTable.getItemCount()-1);
					enableWidgets();
				}
			}
		});
		
		mIncludeStates=new Button(attrButtons, SWT.CHECK);
		mIncludeStates.setText("Include state attributes");
		mIncludeStates.setToolTipText("Include any 'state' attributes (i.e. those with a '.' in the middle) that correspond to items that would already be included in the set.");
		mIncludeStates.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
				group.setIncludeStates(mIncludeStates.getSelection());
			}
		});
		
		
		Composite addAttrGroup=new Composite(attrTable, SWT.None);
		addAttrGroup.setLayout(new GridLayout(3, false));
		addAttrGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mAddAttrText=new Text(addAttrGroup, SWT.SINGLE | SWT.BORDER);
		mAddAttrText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mAddAttrText.setToolTipText("Enter the name of an attribute or a regular expression that matches multiple attributes (following the rules of the Java Pattern class).  Ex. 'xmlns:.*' ");
		mAddAttrText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		
		mAddAttrButton=new Button(addAttrGroup, SWT.PUSH);
		mAddAttrButton.setText("Add");
		mAddAttrButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String attr=mAddAttrText.getText().trim();
				AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
				group.getAttrs().add(attr);
				populateAttrTable();
				mAttrTable.setSelection(mAttrTable.getItemCount()-1);
				enableWidgets();
			}
		});
		
		mUpdateAttrButton=new Button(addAttrGroup, SWT.PUSH);
		mUpdateAttrButton.setText("Replace");
		mUpdateAttrButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				if (selIndex>=0)
				{
					String attr=mAddAttrText.getText().trim();
					AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
					group.getAttrs().set(selIndex, attr);
					populateAttrTable();
					mAttrTable.setSelection(selIndex);
				}
			}
		});
		
		new Label(attrTable, SWT.None);
		
		Group sortComp=new Group(attrTable, SWT.None);
		sortComp.setText("Sorting");
		sortComp.setLayout(new GridLayout());
		
		SelectionListener radioListener=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
				if (mRadioSortAsc.getSelection())
					group.setSortMode(MXMLPrettyPrinter.MXML_Sort_AscByCase);
				else if (mRadioSortGroup.getSelection())
					group.setSortMode(MXMLPrettyPrinter.MXML_Sort_GroupOrder);
				else if (mRadioSortNone.getSelection())
					group.setSortMode(MXMLPrettyPrinter.MXML_Sort_None);
			}
		};
		mRadioSortNone=new Button(sortComp, SWT.RADIO);
		mRadioSortNone.setText("Leave attributes in existing order");
		mRadioSortAsc=new Button(sortComp, SWT.RADIO);
		mRadioSortAsc.setText("Sort attributes in ascending order");
		mRadioSortGroup=new Button(sortComp, SWT.RADIO);
		mRadioSortGroup.setText("Sort attributes in group order");
		mRadioSortAsc.addSelectionListener(radioListener);
		mRadioSortGroup.addSelectionListener(radioListener);
		mRadioSortNone.addSelectionListener(radioListener);
		
		new Label(attrTable, SWT.None);
		
		Group wrapComp=new Group(attrTable, SWT.None);
		wrapComp.setText("Wrapping");
		wrapComp.setLayout(new GridLayout());
		
		SelectionListener radioListener2=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
				if (mRadioWrapNone.getSelection())
					group.setWrapMode(MXMLPrettyPrinter.MXML_ATTR_WRAP_NONE);
				else if (mRadioWrapLineLength.getSelection())
					group.setWrapMode(MXMLPrettyPrinter.MXML_ATTR_WRAP_LINE_LENGTH);
				else if (mRadioWrapAttrCount.getSelection())
					group.setWrapMode(MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE);
				else
					group.setWrapMode(MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT);
				enableWidgets();
			}
		};
		mRadioWrapNone=new Button(wrapComp, SWT.RADIO);
		mRadioWrapNone.setText("Keep attributes on same line");
		mRadioWrapLineLength=new Button(wrapComp, SWT.RADIO);
		mRadioWrapLineLength.setText("Wrap attributes based on max line length");
		mRadioWrapAttrCount=new Button(wrapComp, SWT.RADIO);
		mRadioWrapAttrCount.setText("Keep n attributes per line");
		Composite wrapAttrComp=new Composite(wrapComp, SWT.None);
		wrapAttrComp.setLayout(new GridLayout(3, false));
		mUseDefaultWrapCount=new Button(wrapAttrComp, SWT.CHECK);
		mUseDefaultWrapCount.setText("Use default n");
		mUseDefaultWrapCount.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
				if (mUseDefaultWrapCount.getSelection())
					group.setData(AttrGroup.Wrap_Data_Use_Default);
				else
					group.setData(mCustomWrapCount.getSelection());
				enableWidgets();
			}
		});
		l=new Label(wrapAttrComp, SWT.None);
		l.setText("Attrs per line");
		mCustomWrapCount=new Spinner(wrapAttrComp, SWT.BORDER);
		mCustomWrapCount.setMinimum(1);
		mCustomWrapCount.setMaximum(10);
		mCustomWrapCount.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				AttrGroup group=(AttrGroup)mGroupTable.getItem(mGroupTable.getSelectionIndex()).getData();
				group.setData(mCustomWrapCount.getSelection());
			}
		});
		mRadioWrapDefault=new Button(wrapComp, SWT.RADIO);
		mRadioWrapDefault.setText("Use default wrapping scheme (from main page)");
		mRadioWrapLineLength.addSelectionListener(radioListener2);
		mRadioWrapAttrCount.addSelectionListener(radioListener2);
		mRadioWrapNone.addSelectionListener(radioListener2);
		mRadioWrapDefault.addSelectionListener(radioListener2);
		
		populateGroupTable();
		enableWidgets();
		return main;
	}

	@Override
	protected void okPressed()
	{
		// TODO Auto-generated method stub
		super.okPressed();
	}
	
	String getChosenGroup()
	{
		return mChosenGroup;
	}
	
	private void enableWidgets()
	{
		TableItem[] selGroups=mGroupTable.getSelection();
		mDeleteGroupButton.setEnabled(mGroupTable.getSelectionCount()>0 && !mUsedGroups.contains(((AttrGroup)selGroups[0].getData()).getName()) && !((AttrGroup)selGroups[0].getData()).getName().equals(Initializer.Attr_Group_Other));
		mRenameGroupButton.setEnabled(mGroupTable.getSelectionCount()>0 && !mUsedGroups.contains(((AttrGroup)selGroups[0].getData()).getName()) && !((AttrGroup)selGroups[0].getData()).getName().equals(Initializer.Attr_Group_Other));
		
		int selIndex=mAttrTable.getSelectionIndex();
		mDeleteAttrButton.setEnabled(selIndex>=0);
		mMoveAttrDownButton.setEnabled(selIndex>=0 && selIndex+1<mAttrTable.getItemCount());
		mMoveAttrUpButton.setEnabled(selIndex>=0 && selIndex>0);
		mMoveAttrToTopButton.setEnabled(selIndex>0);
		mIncludeStates.setEnabled(selGroups.length>0 && !((AttrGroup)selGroups[0].getData()).getName().equals(Initializer.Attr_Group_Other));
		
		mAttrTable.setEnabled(mGroupTable.getSelectionCount()>0);
		
		mRadioSortNone.setEnabled(mGroupTable.getSelectionCount()>0);
		mRadioSortAsc.setEnabled(mGroupTable.getSelectionCount()>0);
		mRadioSortGroup.setEnabled(mGroupTable.getSelectionCount()>0);
		mRadioWrapNone.setEnabled(mGroupTable.getSelectionCount()>0);
		mRadioWrapAttrCount.setEnabled(mGroupTable.getSelectionCount()>0);
		mRadioWrapLineLength.setEnabled(mGroupTable.getSelectionCount()>0);
		mRadioWrapDefault.setEnabled(mGroupTable.getSelectionCount()>0);
		mUseDefaultWrapCount.setEnabled(mGroupTable.getSelectionCount()>0 && mRadioWrapAttrCount.getSelection());
		mCustomWrapCount.setEnabled(mGroupTable.getSelectionCount()>0 && mRadioWrapAttrCount.getSelection() && !mUseDefaultWrapCount.getSelection());
		
		TableItem[] allAttrs=mAttrTable.getItems();
		Set<String> attrs=new HashSet<String>();
		for (TableItem tableItem : allAttrs) {
			attrs.add(tableItem.getText());
		}
		
		mAddAttrText.setEnabled(mGroupTable.getSelectionCount()>0);
		String addText=mAddAttrText.getText().trim();
		String error=CommonPrefComposite.validateRegex(addText);
		mAddAttrButton.setEnabled(error==null && addText.length()>0 && !attrs.contains(addText) && selGroups.length>0 && !((AttrGroup)selGroups[0].getData()).getName().equals(Initializer.Attr_Group_Other));
		if (error!=null)
		{
			setMessage(error, IMessageProvider.ERROR);
			return;
		}
		
//		String selText=null;
//		TableItem[] selItems=mAttrTable.getSelection();
//		if (selItems.length==1)
//		{
//			selText=selItems[0].getText();
//		}
		mUpdateAttrButton.setEnabled(addText.length()>0 && !attrs.contains(addText));

		if (addText.length()>0 && !mAddAttrButton.getEnabled() && !mUpdateAttrButton.getEnabled())
		{
			setMessage("Attribute already exists in group", IMessageProvider.WARNING);
			return;
		}
		
		for (int i=0;i<addText.length();i++)
		{
			char c=addText.charAt(i);
			if (Character.isJavaIdentifierPart(c) || c==':' || c=='-' || c=='_' || c=='.')
				continue;
			setMessage("Possible regular expression characters in attribute: '"+c+"'.  Is that your intent?", IMessageProvider.WARNING);
			return;
		}
		
		//TODO: warning for same attr in more than one group?
		
		setMessage("Add new groups or modify the contents of a group by adding/deleting attributes.\nEach group has its own sorting/wrapping setting.");
	}
	
	private void populateAttrTable()
	{
		mAttrTable.removeAll();
		TableItem[] selItems=mGroupTable.getSelection();
		if (selItems.length==1)
		{
			AttrGroup group=(AttrGroup)selItems[0].getData();
//			if (group.getName().equals(Initializer.Attr_Group_Other))
//				return; //group has no explicit attributes.
			for (String attr : group.getAttrs()) {
				TableItem item=new TableItem(mAttrTable, SWT.None);
				item.setText(attr);
			}
			if (mAttrTable.getItemCount()>0)
				mAttrTable.setSelection(0);
			mAddAttrText.setText("");
			mRadioSortAsc.setSelection(false);
			mRadioSortGroup.setSelection(false);
			mRadioSortNone.setSelection(false);
			switch (group.getSortMode())
			{
			case MXMLPrettyPrinter.MXML_Sort_None:
				mRadioSortNone.setSelection(true);
				break;
			case MXMLPrettyPrinter.MXML_Sort_GroupOrder:
				mRadioSortGroup.setSelection(true);
				break;
			case MXMLPrettyPrinter.MXML_Sort_AscByCase:
				mRadioSortAsc.setSelection(true);
				break;
			}
			mRadioWrapAttrCount.setSelection(false);
			mRadioWrapLineLength.setSelection(false);
			mRadioWrapNone.setSelection(false);
			mRadioWrapDefault.setSelection(false);
			switch (group.getWrapMode())
			{
			case MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE:
				mRadioWrapAttrCount.setSelection(true);
				break;
			case MXMLPrettyPrinter.MXML_ATTR_WRAP_LINE_LENGTH:
				mRadioWrapLineLength.setSelection(true);
				break;
			case MXMLPrettyPrinter.MXML_ATTR_WRAP_NONE:
				mRadioWrapNone.setSelection(true);
				break;
			default:
				mRadioWrapDefault.setSelection(true);
			}
			if (group.getData()==AttrGroup.Wrap_Data_Use_Default)
				mUseDefaultWrapCount.setSelection(true);
			else
			{
				mUseDefaultWrapCount.setSelection(false);
				mCustomWrapCount.setSelection(group.getData());
			}
			mIncludeStates.setSelection(group.isIncludeStates());
		}
	}
	
	private void populateGroupTable()
	{
		mGroupTable.removeAll();
		for (AttrGroup group : mGroups)
		{
			TableItem item=new TableItem(mGroupTable, SWT.None);
			item.setData(group);
			item.setText(group.getName());
		}
	}
	
	public List<AttrGroup> getGroups()
	{
		return mGroups;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getShell().setDefaultButton(mAddAttrButton);
	}
	
}
