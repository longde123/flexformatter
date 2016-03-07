package flexprettyprint.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import actionscriptinfocollector.AntlrUtilities;

import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprintcommand.Activator;

public class AttrOrderConfigDialog extends TitleAreaDialog
{
	public static final String Attr_Group_Marker="%";
	public static final String Attr_Grouping_Splitter=",";
	private int mSortMode;
	private boolean mSortExtraAttrs;
	private boolean mAddCRAfterAttrs;
	private boolean mIndentTagClose;
	private List<String> mManualSortOrder;
	private List<AttrGroup> mDefinedAttrGroups;
	
	private Button mModeCheck;
	private Button mSortExtrasCheckbox;
	private Text mAddText;
	private Table mAttrTable;
	private Button mDeleteButton;
	private Button mMoveUpButton;
	private Button mMoveDownButton;
	private Button mAddButton;
	private Button mUpdateButton;
	private Button mAddLineBreakButton;
	private Button mAddGroupButton;
	private Combo mAddGroupCombo;
	private Button mEditGroupsButton;
	private Button mAddNewLineAfterLastAttrButton;
	private Button mIndentTagCloseToAttributeLevel;
	
	public static final String NewLineFlag="\\n";
	public AttrOrderConfigDialog(Shell parentShell, List<String> sortOrder, boolean sortExtra, int sortMode, boolean addNewLineAtEnd, List<AttrGroup> attrGroups, boolean indentTagClose)
	{
		super(parentShell);
		mSortMode=sortMode;
		mSortExtraAttrs=sortExtra;
		mAddCRAfterAttrs=addNewLineAtEnd;
		mIndentTagClose=indentTagClose;
		mManualSortOrder=new ArrayList<String>();
		mManualSortOrder.addAll(sortOrder);
		
		boolean foundOther=false;
		mDefinedAttrGroups=new ArrayList<AttrGroup>();
		for (AttrGroup attrGroup : attrGroups) {
			mDefinedAttrGroups.add(attrGroup.copy());
			if (attrGroup.getName().equals(Initializer.Attr_Group_Other))
				foundOther=true;
		}
		if (!foundOther)
		{
			AttrGroup otherGroup=new AttrGroup(Initializer.Attr_Group_Other, new ArrayList<String>(), MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true); //$NON-NLS-1$;
			mDefinedAttrGroups.add(otherGroup);
		}
		
		setShellStyle(getShellStyle()|SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".MXML_AttrOrderDlg"); //$NON-NLS-1$
		getShell().setText("Configure attribute order/line breaks");
		setTitle("Configure custom attribute order and line breaks that are applied to all mxml tags");
		setDefaultInstructions();
		Composite comp=new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mModeCheck=new Button(comp, SWT.CHECK);
		mModeCheck.setText("Don't use custom attribute order and line breaks");
		mModeCheck.setSelection(mSortMode==MXMLPrettyPrinter.MXML_ATTR_ORDERING_NONE);
		mModeCheck.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mSortMode=mModeCheck.getSelection() ? MXMLPrettyPrinter.MXML_ATTR_ORDERING_NONE : MXMLPrettyPrinter.MXML_ATTR_ORDERING_USEDATA;
				enableWidgets();
			}
		});
		
		Group g=new Group(comp, SWT.None);
		g.setText("Manual ordering options");
		g.setLayout(new GridLayout(2, false));
		g.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite tableComp=new Composite(g, SWT.NONE);
		tableComp.setLayout(new GridLayout());
		tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label tableLabel=new Label(tableComp, SWT.None);
		tableLabel.setText("Order of attributes");
		mAttrTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
		GridData gd=new GridData(GridData.FILL_BOTH);
		GC gc=new GC(mAttrTable);
		gd.widthHint=gc.textExtent("w").x*30;
		gd.heightHint=gc.textExtent("w").y*10;
		mAttrTable.setLayoutData(gd);
		mAttrTable.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] selItems=mAttrTable.getSelection();
				if (selItems.length>0)
					mAddText.setText(selItems[0].getText());
				enableWidgets();
			}
		});
		mAttrTable.setToolTipText("Use the edit field to create or modify attribute groups.");
		
		Composite addComp=new Composite(tableComp, SWT.None);
		addComp.setLayout(new GridLayout(3, false));
		addComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mAddText=new Text(addComp, SWT.BORDER | SWT.SINGLE);
		mAddText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		mAddText.addKeyListener(new KeyAdapter()
//		{
//
//			@Override
//			public void keyPressed(KeyEvent e) {
//				if (e.character==SWT.CR)
//				{
//					addString(mAddText.getText());
//					mAddText.setText("");
//					return;
//				}
//				super.keyPressed(e);
//			}
//			
//		});
		mAddText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		
		mAddButton=new Button(addComp, SWT.PUSH);
		mAddButton.setText("Add");
		mAddButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String data=mAddText.getText();
				addGroup(data);
				mAddText.setFocus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		mUpdateButton=new Button(addComp, SWT.PUSH);
		mUpdateButton.setText("Update selected");
		mUpdateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				if (selIndex<0)
					return;
				
				mManualSortOrder.set(selIndex, mAddText.getText());
				repopulateTable();
				mAttrTable.setSelection(selIndex);
				mAddText.setFocus();
			}
		});
		
		
		Composite buttonComp=new Composite(g, SWT.NONE);
		buttonComp.setLayout(new GridLayout());
		
		mDeleteButton=new Button(buttonComp, SWT.PUSH);
		mDeleteButton.setText("Delete");
		mDeleteButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				mManualSortOrder.remove(selIndex);
				repopulateTable();
				if (selIndex<mAttrTable.getItemCount())
					mAttrTable.setSelection(selIndex);
				else if (mAttrTable.getItemCount()>0)
					mAttrTable.setSelection(mAttrTable.getItemCount()-1);
				enableWidgets();
			}
		});

		mMoveUpButton=new Button(buttonComp, SWT.PUSH);
		mMoveUpButton.setText("Move Up");
		mMoveUpButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				String swapAttr=mManualSortOrder.get(selIndex);
				mManualSortOrder.set(selIndex, mManualSortOrder.get(selIndex-1));
				mManualSortOrder.set(selIndex-1, swapAttr);
				repopulateTable();
				mAttrTable.setSelection(selIndex-1);
				enableWidgets();
			}
		});
		
		mMoveDownButton=new Button(buttonComp, SWT.PUSH);
		mMoveDownButton.setText("Move Down");
		mMoveDownButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selIndex=mAttrTable.getSelectionIndex();
				String swapAttr=mManualSortOrder.get(selIndex);
				mManualSortOrder.set(selIndex, mManualSortOrder.get(selIndex+1));
				mManualSortOrder.set(selIndex+1, swapAttr);
				repopulateTable();
				mAttrTable.setSelection(selIndex+1);
				enableWidgets();
			}
		});
		
		mAddLineBreakButton=new Button(buttonComp, SWT.PUSH);
		mAddLineBreakButton.setText("Add line break");
		mAddLineBreakButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String text=mAddText.getText();
				if (AntlrUtilities.asTrim(text).length()>0)
					text+=Attr_Grouping_Splitter;
				text+=NewLineFlag;
				mAddText.setText(text);
				enableWidgets();
			}
		});
		
		Composite addGroupComp=new Composite(buttonComp, SWT.None);
		GridLayout gl=new GridLayout(2, false);
		gl.marginWidth=0;
		addGroupComp.setLayout(gl);
		
		mAddGroupButton=new Button(addGroupComp, SWT.PUSH);
		mAddGroupCombo=new Combo(addGroupComp, SWT.READ_ONLY | SWT.BORDER);
		
		mAddGroupButton.setText("Add attr group");
		mAddGroupButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String group=mAddGroupCombo.getText();
				if (group!=null && group.length()>0)
				{
					String text=mAddText.getText();
					if (AntlrUtilities.asTrim(text).length()>0)
						text+=Attr_Grouping_Splitter;
					text+=Attr_Group_Marker+group+Attr_Group_Marker;
					mAddText.setText(text);
					enableWidgets();
				}
			}
		});
		
		mEditGroupsButton=new Button(g, SWT.PUSH);
		mEditGroupsButton.setText("Edit Groups...");
		mEditGroupsButton.setToolTipText("Define new attribute groups or edit the current list.");
		mEditGroupsButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Set<String> usedGroups=new HashSet<String>();
				for (String attrString : mManualSortOrder) {
					String[] attrs=attrString.split(Attr_Grouping_Splitter);
					for (String attr : attrs) {
						attr=AntlrUtilities.asTrim(attr);
						if (attr.startsWith(Attr_Group_Marker) && attr.endsWith(Attr_Group_Marker))
						{
							usedGroups.add(attr.substring(1, attr.length()-1));
						}
					}
				}
				
				EditGroupsDlg dlg=new EditGroupsDlg(getShell(), mDefinedAttrGroups, usedGroups);
				if (dlg.open()==Dialog.OK)
				{
					mDefinedAttrGroups=dlg.getGroups();
					populateGroupCombo();
					enableWidgets();
				}
			}
		});
		
		
		
		mSortExtrasCheckbox=new Button(comp, SWT.CHECK);
		mSortExtrasCheckbox.setText("Sort any remaining attrs");
		mSortExtrasCheckbox.setToolTipText("If checked, sort any attributes not covered in the custom ordering configuration.");
		mSortExtrasCheckbox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mSortExtraAttrs=mSortExtrasCheckbox.getSelection();
			}
			
		});
		mSortExtrasCheckbox.setSelection(mSortExtraAttrs);
		
		Composite tagCloseComp=new Composite(comp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginWidth=0;
		tagCloseComp.setLayout(gl);
		
		mAddNewLineAfterLastAttrButton=new Button(tagCloseComp, SWT.CHECK);
		mAddNewLineAfterLastAttrButton.setText("Add newline before tag close (ex. '>')");
		mAddNewLineAfterLastAttrButton.setToolTipText("If checked, add a newline before the '>' or '/>' that closes a tag, if the tag has at least one attribute.");
		mAddNewLineAfterLastAttrButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mAddCRAfterAttrs=mAddNewLineAfterLastAttrButton.getSelection();
				enableWidgets();
			}
			
		});
		mAddNewLineAfterLastAttrButton.setSelection(mAddCRAfterAttrs);
		
		mIndentTagCloseToAttributeLevel=new Button(tagCloseComp, SWT.CHECK);
		mIndentTagCloseToAttributeLevel.setText("Indent tag close to attribute");
		mIndentTagCloseToAttributeLevel.setToolTipText("If checked, indent the tag close char(s) to align with the first attribute.  Otherwise, align with the tag open char(s).");
		mIndentTagCloseToAttributeLevel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mIndentTagClose=mIndentTagCloseToAttributeLevel.getSelection();
			}
			
		});
		mIndentTagCloseToAttributeLevel.setSelection(mIndentTagClose);
		
		
		repopulateTable();
		populateGroupCombo();
		enableWidgets();
		return comp;
	}
	
	private void populateGroupCombo()
	{
		mAddGroupCombo.removeAll();
		for (AttrGroup group : mDefinedAttrGroups)
		{
			mAddGroupCombo.add(group.getName());
			mAddGroupCombo.setData(group.getName(), group);
		}
		
		if (mAddGroupCombo.getItemCount()>0)
			mAddGroupCombo.select(0);
	}
	
	private void addGroup(String data)
	{
		if (data.length()>0)
		{
			mManualSortOrder.add(data);
			repopulateTable();
		}
	}
	
	private void enableWidgets()
	{
		mSortExtrasCheckbox.setEnabled(!mModeCheck.getSelection());
		mAddNewLineAfterLastAttrButton.setEnabled(!mModeCheck.getSelection());
		mIndentTagCloseToAttributeLevel.setEnabled(mAddNewLineAfterLastAttrButton.getEnabled() && mAddNewLineAfterLastAttrButton.getSelection());
		mAttrTable.setEnabled(!mModeCheck.getSelection());
		mAddText.setEnabled(!mModeCheck.getSelection());
		int selIndex=mAttrTable.getSelectionIndex();
		mDeleteButton.setEnabled(!mModeCheck.getSelection() && selIndex>=0);
		mMoveDownButton.setEnabled(!mModeCheck.getSelection() && selIndex>=0 && selIndex+1<mManualSortOrder.size());
		mMoveUpButton.setEnabled(!mModeCheck.getSelection() && selIndex>=1);
		
		mAddLineBreakButton.setEnabled(!mModeCheck.getSelection());
		mAddGroupButton.setEnabled(!mModeCheck.getSelection()&& mAddGroupCombo.getItemCount()>0);
		mAddGroupCombo.setEnabled(!mModeCheck.getSelection()&& mAddGroupCombo.getItemCount()>0);
		
		mEditGroupsButton.setEnabled(!mModeCheck.getSelection());

		//get all of the attributes currently in the table (with selected group separate).
		boolean addEnabled=true;
		boolean updateEnabled=true;
//		String usedAttr=null;
		if (!mModeCheck.getSelection())
		{
			TableItem[] items=mAttrTable.getItems();
			TableItem[] selItems=mAttrTable.getSelection();
			TableItem selItem=null;
			if (selItems.length>0)
				selItem=selItems[0];
			Set<String> usedAtts=new HashSet<String>();
			Set<String> selectedAtts=new HashSet<String>();
			for (TableItem tableItem : items)
			{
				String group=tableItem.getText();
				String[] attrs=group.split(Attr_Grouping_Splitter);
				for (String attr : attrs) {
					attr=AntlrUtilities.asTrim(attr);
					if (attr.length()==0)
						continue;
					if (attr.equals(NewLineFlag))
						continue;
					if (tableItem==selItem)
						selectedAtts.add(attr);
					else
						usedAtts.add(attr);
				}
			}
	
			Set<String> addAttrs=new HashSet<String>();
			String[] attrs=mAddText.getText().split(Attr_Grouping_Splitter);
			for (String attr : attrs) {
				attr=AntlrUtilities.asTrim(attr);
				if (attr.length()==0)
					continue;
				if (!attr.equals(NewLineFlag))
					addAttrs.add(attr);
			}
			
			//add button is enabled if there are no new attrs that conflict with existing attrs.
			//update button is enabled if there are no new attrs that conflict with existing non-selected attrs
			for (String attr : addAttrs) {
				if (usedAtts.contains(attr))
				{
					addEnabled=false;
					updateEnabled=false;
//					usedAttr=attr;
					break;
				}
				
				if (selectedAtts.contains(attr))
				{
//					usedAttr=attr;
					addEnabled=false;
				}
			}
		}
		
		String text=AntlrUtilities.asTrim(mAddText.getText());
		String error=CommonPrefComposite.validateRegex(text);
		mUpdateButton.setEnabled(error==null && !mModeCheck.getSelection() && selIndex>=0 && AntlrUtilities.asTrim(mAddText.getText()).length()>0 && updateEnabled);
		mAddButton.setEnabled(error==null && !mModeCheck.getSelection() && AntlrUtilities.asTrim(mAddText.getText()).length()>0 && addEnabled);
		
		//TODO: add warning for duplicate group or same item in more than one group
//		if (!updateEnabled)
//			setMessage("(Update disabled) The attribute ("+usedAttr+") is already used in another attribute group.", IMessageProvider.WARNING);
//		else if (!addEnabled)
//			setMessage("(Add disabled) The attribute ("+usedAttr+") is already used in another attribute group.", IMessageProvider.WARNING);
//		else
		if (error!=null)
			setMessage(error, IMessageProvider.ERROR);
		else
			setDefaultInstructions();
	}

	private void repopulateTable()
	{
		mAttrTable.removeAll();
		for (String attr : mManualSortOrder)
		{
			if (attr.length()==0)
				continue;
			TableItem item=new TableItem(mAttrTable, SWT.None);
			item.setText(attr);
		}
		enableWidgets();
	}
	public int getSortMode() {
		return mSortMode;
	}
	public boolean isSortExtraAttrs() {
		return mSortExtraAttrs;
	}
	
	public boolean isAddNewlineAfterLastAttr()
	{
		return mAddCRAfterAttrs;
	}
	
	public boolean isIndentTagClose()
	{
		return mIndentTagClose;
	}
	
	public List<String> getManualSortOrder() {
		return mManualSortOrder;
	}
	
	public List<AttrGroup> getAttrGroups()
	{
		return mDefinedAttrGroups;
	}
	
	@Override
	protected void okPressed()
	{
		super.okPressed();
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getShell().setDefaultButton(mAddButton);
	}

	private void setDefaultInstructions()
	{
		setMessage("Each line in the table is a comma-separated list of attributes and newlines.  If any of the attrs in the line exist, then all of the newlines are applied.\n  Otherwise, the line is skipped.  The normal use case is to include several attrs with the newline at the end. Newlines with no attrs will always be applied.", IMessageProvider.INFORMATION);		
	}
}
