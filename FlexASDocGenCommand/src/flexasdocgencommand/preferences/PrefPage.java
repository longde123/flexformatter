package flexasdocgencommand.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import actionscriptinfocollector.TopLevelItemRecord;
import flexasdocgen.Activator;

public class PrefPage extends PreferencePage implements IWorkbenchPreferencePage
{
	private int[] mVisibility=new int[3];
	private int[] mModifiers=new int[3];
	private String[] mTemplates=new String[3];
	
	private Button mPublicBox;
	private Button mPrivateBox;
	private Button mProtectedBox;
	private Button mInternalBox;
	
	private Button mFinalButton;
	private Button mStaticButton;
	private Button mNativeButton;
	private Button mDynamicButton;
	private Button mOverrideButton;
	
	private Text mTemplate;
	
	private Combo mTypeCombo;
	
	private int mCurrentType;
	
	private static final int Settings_Class=0;
	private static final int Settings_Function=1;
	private static final int Settings_Property=2;
	
	public PrefPage()
	{
		setDescription("Options for generating AS doc");
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite comp=new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite topComp=new Composite(comp, SWT.None);
		topComp.setLayout(new GridLayout(2, false));
//		topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l=new Label(topComp, SWT.None);
		l.setText("Element type");
		
		mTypeCombo=new Combo(topComp, SWT.READ_ONLY | SWT.BORDER);
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
			}
			
		});
		
		mTypeCombo.select(0);
		mCurrentType=getCurrentModeFromCombo();
		
		Group visComp=new Group(comp, SWT.None);
		visComp.setLayout(new GridLayout(4, false));
		visComp.setText("Check all that apply");
		
		mPublicBox=new Button(visComp, SWT.CHECK);
		mPublicBox.setText("public");
		
		mPrivateBox=new Button(visComp, SWT.CHECK);
		mPrivateBox.setText("private");
		
		mProtectedBox=new Button(visComp, SWT.CHECK);
		mProtectedBox.setText("protected");
		
		mInternalBox=new Button(visComp, SWT.CHECK);
		mInternalBox.setText("internal");
		
//		Composite tableComp=new Composite(comp, SWT.None);
//		tableComp.setLayout(new GridLayout(4, true));
//		
//		l=new Label(tableComp, SWT.None);
//		l=new Label(tableComp, SWT.None);
//		l.setText("Include");
//		l=new Label(tableComp, SWT.None);
//		l.setText("Exclude");
//		l=new Label(tableComp, SWT.None);
//		l.setText("Don't care");
		
		
		Group keywordFilterComp=new Group(comp, SWT.None);
		keywordFilterComp.setText("Uncheck to exclude items with the attribute");
		keywordFilterComp.setLayout(new GridLayout());
		
		mFinalButton=new Button(keywordFilterComp, SWT.CHECK);
		mFinalButton.setText("final");
		
		mStaticButton=new Button(keywordFilterComp, SWT.CHECK);
		mStaticButton.setText("static");
		
		mNativeButton=new Button(keywordFilterComp, SWT.CHECK);
		mNativeButton.setText("native");
		
		mDynamicButton=new Button(keywordFilterComp, SWT.CHECK);
		mDynamicButton.setText("dynamic");
		
		mOverrideButton=new Button(keywordFilterComp, SWT.CHECK);
		mOverrideButton.setText("override");
		
		l=new Label(comp, SWT.None);
		l.setText("Template text");
		
		mTemplate=new Text(comp, SWT.MULTI | SWT.BORDER);
		mTemplate.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//load initial settings from perf store
		loadFromPrefStore();
		
		return comp;
	}
	
	private void loadFromPrefStore()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mTemplates[Settings_Class]=store.getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Class);
		mTemplates[Settings_Function]=store.getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Function);
		mTemplates[Settings_Property]=store.getString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Property);
		
		mVisibility[Settings_Class]=store.getInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Class);
		mVisibility[Settings_Function]=store.getInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Function);
		mVisibility[Settings_Property]=store.getInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Property);

		mModifiers[Settings_Class]=store.getInt(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Class);
		mModifiers[Settings_Function]=store.getInt(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Function);
		mModifiers[Settings_Property]=store.getInt(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Property);
		
		updateSettingsForCombo();
	}
	
	private int getCurrentModeFromCombo()
	{
		int currentMode=((Integer)mTypeCombo.getData(mTypeCombo.getText())).intValue();
		if (currentMode<0 || currentMode>2)
		{
			System.out.println("Internal error");
			return Settings_Function;			
		}
		
		return currentMode;
	}
	
	private void updateSettingsForCombo()
	{
		int currentMode=mCurrentType;
		mPublicBox.setSelection((mVisibility[currentMode] & TopLevelItemRecord.ASDoc_Public)!=0);
		mPrivateBox.setSelection((mVisibility[currentMode] & TopLevelItemRecord.ASDoc_Private)!=0);
		mProtectedBox.setSelection((mVisibility[currentMode] & TopLevelItemRecord.ASDoc_Protected)!=0);
		mInternalBox.setSelection((mVisibility[currentMode] & TopLevelItemRecord.ASDoc_Internal)!=0);

		mDynamicButton.setSelection((mModifiers[currentMode] & TopLevelItemRecord.ASDoc_Dynamic)!=0);
		mFinalButton.setSelection((mModifiers[currentMode] & TopLevelItemRecord.ASDoc_Final)!=0);
		mNativeButton.setSelection((mModifiers[currentMode] & TopLevelItemRecord.ASDoc_Native)!=0);
		mOverrideButton.setSelection((mModifiers[currentMode] & TopLevelItemRecord.ASDoc_Override)!=0);
		mStaticButton.setSelection((mModifiers[currentMode] & TopLevelItemRecord.ASDoc_Static)!=0);

		mTemplate.setText(mTemplates[currentMode]);
	}
	
	private void saveSettingsForCombo(int currentMode)
	{
//		int currentMode=((Integer)mTypeCombo.getData(mTypeCombo.getText())).intValue();
//		if (currentMode<0 || currentMode>2)
//		{
//			System.out.println("Internal error");
//			return;			
//		}

		mVisibility[currentMode]=0;
		mVisibility[currentMode]+=(mPublicBox.getSelection() ? TopLevelItemRecord.ASDoc_Public : 0);
		mVisibility[currentMode]+=(mPrivateBox.getSelection() ? TopLevelItemRecord.ASDoc_Private: 0);
		mVisibility[currentMode]+=(mProtectedBox.getSelection() ? TopLevelItemRecord.ASDoc_Protected: 0);
		mVisibility[currentMode]+=(mInternalBox.getSelection() ? TopLevelItemRecord.ASDoc_Internal: 0);
		
		mModifiers[currentMode]=0;
		mModifiers[currentMode]+=(mDynamicButton.getSelection() ? TopLevelItemRecord.ASDoc_Dynamic: 0);
		mModifiers[currentMode]+=(mFinalButton.getSelection() ? TopLevelItemRecord.ASDoc_Final: 0);
		mModifiers[currentMode]+=(mNativeButton.getSelection() ? TopLevelItemRecord.ASDoc_Native: 0);
		mModifiers[currentMode]+=(mOverrideButton.getSelection() ? TopLevelItemRecord.ASDoc_Override: 0);
		mModifiers[currentMode]+=(mStaticButton.getSelection() ? TopLevelItemRecord.ASDoc_Static: 0);
		
		mTemplates[currentMode]=mTemplate.getText();
	}
	

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		mTemplates[Settings_Class]=store.getDefaultString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Class);
		mTemplates[Settings_Function]=store.getDefaultString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Function);
		mTemplates[Settings_Property]=store.getDefaultString(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Property);
		
		mVisibility[Settings_Class]=store.getDefaultInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Class);
		mVisibility[Settings_Function]=store.getDefaultInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Function);
		mVisibility[Settings_Property]=store.getDefaultInt(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Property);

		mModifiers[Settings_Class]=store.getDefaultInt(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Class);
		mModifiers[Settings_Function]=store.getDefaultInt(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Function);
		mModifiers[Settings_Property]=store.getInt(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Property);
		
		updateSettingsForCombo();
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		saveSettingsForCombo(mCurrentType);
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		store.setValue(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Class, mTemplates[Settings_Class]);
		store.setValue(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Function, mTemplates[Settings_Function]);
		store.setValue(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Property, mTemplates[Settings_Property]);
		
		store.setValue(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Class, mVisibility[Settings_Class]);
		store.setValue(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Function, mVisibility[Settings_Function]);
		store.setValue(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Property, mVisibility[Settings_Property]);

		store.setValue(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Class, mModifiers[Settings_Class]);
		store.setValue(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Function, mModifiers[Settings_Function]);
		store.setValue(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Property, mModifiers[Settings_Property]);
		
		return super.performOk();
	}

	
}