package flexasrearrangecodecommand.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import flexasrearrangecodecommand.Activator;

public class RearrangePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private int mCurrentType;
	private Combo mTypeCombo;
	private static final int Settings_Class=0;
	private static final int Settings_Function=1;
	private static final int Settings_Property=2;
//	private static final int Settings_Global=3;
	private Table mModifierTable;
	private String[] mModifierOrders=new String[3];
	private boolean[] mUseModifierOrders=new boolean[3];
//	private List<String> mKeywordOrder=new ArrayList<String>();
	private Button mUseGlobalOrderButton;
	private Button mUseModifierOrder;
	private Button mMoveUp;
	private Button mMoveDown;
	
	public RearrangePage() {
		setTitle("Rearrangement Options");
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite comp=new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group g=new Group(comp, SWT.None);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		g.setText("Modifier order");
		
		SelectionListener enableUpdater=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableItems();
			}
		};
		
		Composite typeComp=new Composite(g, SWT.None);
		GridLayout gl=new GridLayout(2, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		typeComp.setLayout(gl);
		typeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mUseGlobalOrderButton=new Button(typeComp, SWT.CHECK);
		mUseGlobalOrderButton.setText("Use same order for all element types");
		mUseGlobalOrderButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				saveSettingsForCombo(mCurrentType);
				mTypeCombo.select(0);
				mCurrentType=0;
				updateSettingsForCombo();
				enableItems();
			}
			
		});
		
		mTypeCombo=new Combo(typeComp, SWT.READ_ONLY | SWT.BORDER);
//		mTypeCombo.add("All Elements");
//		mTypeCombo.setData("All Elements", Settings_Global); //use the function settings if we are in global state
		mTypeCombo.add("Functions");
		mTypeCombo.setData("Functions", Settings_Function);
		mTypeCombo.add("Properties");
		mTypeCombo.setData("Properties", Settings_Property);
		mTypeCombo.add("Classes");
		mTypeCombo.setData("Classes", Settings_Class);
		mTypeCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				saveSettingsForCombo(mCurrentType);
				mCurrentType=getCurrentModeFromCombo();
				updateSettingsForCombo();
				enableItems();
			}
			
		});
		mTypeCombo.select(0);
		
		mUseModifierOrder=new Button(g, SWT.CHECK);
		mUseModifierOrder.setText("Enable Reordering");
		mUseModifierOrder.addSelectionListener(enableUpdater);
		
		Composite tableComp=new Composite(g, SWT.None);
		tableComp.setLayout(new GridLayout(2, false));
		tableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mModifierTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
		GridData gd=new GridData();
		gd.widthHint=100;
		gd.heightHint=mModifierTable.getItemHeight()*5;
		mModifierTable.setLayoutData(gd);
		mModifierTable.addSelectionListener(enableUpdater);
		
		Composite buttonComp=new Composite(tableComp, SWT.None);
		buttonComp.setLayout(new GridLayout());
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		mMoveUp=new Button(buttonComp, SWT.PUSH);
		mMoveUp.setText("Up");
		mMoveUp.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] mods=getModsFromTable();
				int selIndex=mModifierTable.getSelectionIndex();
				String swap=mods[selIndex];
				mods[selIndex]=mods[selIndex-1];
				mods[selIndex-1]=swap;
				addTableItems(mods);
				mModifierTable.setSelection(selIndex-1);
				enableItems();
			}
		});
		mMoveDown=new Button(buttonComp, SWT.PUSH);
		mMoveDown.setText("Down");
		mMoveDown.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] mods=getModsFromTable();
				int selIndex=mModifierTable.getSelectionIndex();
				String swap=mods[selIndex];
				mods[selIndex]=mods[selIndex+1];
				mods[selIndex+1]=swap;
				addTableItems(mods);
				mModifierTable.setSelection(selIndex+1);
				enableItems();
			}
		});
		
		loadFromPrefStore();
		enableItems();
		return comp;
	}

	private void addTableItems(String[] keywords)
	{
		mModifierTable.removeAll();
		for (String key : keywords) {
			TableItem item=new TableItem(mModifierTable, SWT.None);
			item.setText(key);
		}
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}
	
	private void enableItems()
	{
//		mUseModifierOrder.setEnabled(!mUseGlobalOrderButton.getSelection());
		int selIndex=mModifierTable.getSelectionIndex();
		mMoveDown.setEnabled(selIndex>=0 && selIndex+1<mModifierTable.getItemCount());
		mMoveUp.setEnabled(selIndex>=0 && selIndex>0);
		mModifierTable.setEnabled(mUseModifierOrder.getSelection());
		mTypeCombo.setEnabled(!mUseGlobalOrderButton.getSelection());
	}

	private void loadFromPrefStore()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mModifierOrders[Settings_Class]=store.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class);
		mModifierOrders[Settings_Function]=store.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		mModifierOrders[Settings_Property]=store.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property);
		
		mUseModifierOrders[Settings_Class]=store.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class);
		mUseModifierOrders[Settings_Function]=store.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		mUseModifierOrders[Settings_Property]=store.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property);
		
		mUseGlobalOrderButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements));
		
		updateSettingsForCombo();
	}
	
	private int getCurrentModeFromCombo()
	{
		int currentMode=((Integer)mTypeCombo.getData(mTypeCombo.getText())).intValue();
		if (currentMode<0 || currentMode>3)
		{
			System.out.println("Internal error");
			return Settings_Function;			
		}
		
		return currentMode;
	}
	
	private String[] getModsFromTable()
	{
		TableItem[] items=mModifierTable.getItems();
		String[] list=new String[items.length];
		for (int i = 0; i < items.length; i++) {
			TableItem tableItem = items[i];
			list[i]=tableItem.getText();
		}
		return list;
	}
	
	private void updateSettingsForCombo()
	{
		int currentMode=mCurrentType;
//		if (currentMode==Settings_Global)
//			currentMode=Settings_Function;
		String[] order=mModifierOrders[currentMode].split(",");
		addTableItems(order);
		
		mUseModifierOrder.setSelection(mUseModifierOrders[currentMode]);
	}
	
	private void saveSettingsForCombo(int currentMode)
	{
//		if (currentMode==Settings_Global)
//			currentMode=Settings_Function;
		mUseModifierOrders[currentMode]=mUseModifierOrder.getSelection();
		
		String[] mods=getModsFromTable();
		StringBuffer buffer=new StringBuffer();
		boolean first=true;
		for (String mod : mods) {
			if (!first)
				buffer.append(',');
			first=false;
			buffer.append(mod);
		}
		mModifierOrders[currentMode]=buffer.toString();
	}
	
	@Override
	protected void performDefaults() {
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mModifierOrders[Settings_Class]=store.getDefaultString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class);
		mModifierOrders[Settings_Function]=store.getDefaultString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		mModifierOrders[Settings_Property]=store.getDefaultString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property);
		
		mUseModifierOrders[Settings_Class]=store.getDefaultBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class);
		mUseModifierOrders[Settings_Function]=store.getDefaultBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		mUseModifierOrders[Settings_Property]=store.getDefaultBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property);
		
		mUseGlobalOrderButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements));
//		if (store.getDefaultBoolean(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements))
		if (mUseGlobalOrderButton.getSelection())
		{
			mTypeCombo.select(0);
		}
		
		updateSettingsForCombo();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		saveSettingsForCombo(mCurrentType);
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class, mModifierOrders[Settings_Class]);
		store.setValue(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function, mModifierOrders[Settings_Function]);
		store.setValue(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property, mModifierOrders[Settings_Property]);
		
		store.setValue(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class, mUseModifierOrders[Settings_Class]);
		store.setValue(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function, mUseModifierOrders[Settings_Function]);
		store.setValue(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property, mUseModifierOrders[Settings_Property]);
		
		store.setValue(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements, mUseGlobalOrderButton.getSelection());//mCurrentType==Settings_Global);
		return super.performOk();
	}
	
}
