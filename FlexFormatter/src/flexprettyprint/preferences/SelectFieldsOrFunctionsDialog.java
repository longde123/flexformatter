package flexprettyprint.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import actionscriptinfocollector.TopLevelItemRecord;

public class SelectFieldsOrFunctionsDialog extends TitleAreaDialog
{
	private boolean mUpdateObject=false;
	
	private int mPropertyHeaderStyle;
	
	private int mOldSelectPriority;
	
	Button mFieldButton;
	Button mFunctionButton;
	private Button mGrabFirst; 
	
	Button mUseVisibility;
	Button mPublic;
	Button mProtected;
	Button mPrivate;
	Button mInternal;
	
	Button mUseName;
	Text mNameText;
	Button mInvertNames;
	
	Button mUseObjectType;
	Text mObjectTypeText;
	
	Button mUseParameterType;
	Text mParameterTypeText;
	
	Button mUseParameterName;
	Text mParameterNameText;
	
	Button mUseMetatag;
	Text mMetatagText;
	Button mInvertMetatag;
	
	Button mUseNamespace;
	Button mNoNamespaces;
	Text mNamespaceText;
	Button mInvertNamespaces;
	
	Button mUseStatic;
	Button mStatic;
	Button mNonStatic;
	
	Button mUseConst;
	Button mConst;
	Button mNonConst;
	
	Button mUseFinal;
	Button mFinal;
	Button mNonFinal;
	
	Button mUseNative;
	Button mNative;
	Button mNonNative;
	
	Button mUseOverride;
	Button mOverride;
	Button mNonOverride;
	
	Button mUseFunctionType;
	Button mConstructor;
	Button mGet;
	Button mSet;
	Button mOthers;
	
	Button mSortMembers;
	Button mSortByCase;
	Button mSortByType;
	Button mAssociateImplicitProperties;
	Button mIncludeImplicitProperties;
	Button mHeaderButton;
	Button mIncludeRealProperties; //associate real properties with selected getter/setter functions
	
	private Text mToStringArea;
	
	private MemberSelectionSpec mData;
	
	protected SelectFieldsOrFunctionsDialog(Shell parentShell, MemberSelectionSpec existingData)
	{
		super(parentShell);
		if (existingData!=null)
			mData=existingData.copy();
		else
			mData=new MemberSelectionSpec();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Select functions/properties");
		
		Composite mainComp=new Composite(parent, SWT.NULL);
		mainComp.setLayout(new GridLayout());
		mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ScrolledComposite scrollComp=new ScrolledComposite(mainComp, SWT.V_SCROLL | SWT.H_SCROLL);
		GridLayout gl=new GridLayout();
		gl.marginWidth=0;
		gl.marginHeight=0;
		scrollComp.setLayout(gl);
		GridData gd=new GridData(GridData.FILL_BOTH);
		scrollComp.setLayoutData(gd);
		scrollComp.setExpandHorizontal(true);
		scrollComp.setExpandVertical(true);

		Composite comp=new Composite(scrollComp, SWT.NULL);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrollComp.setContent(comp);
//		scrollComp.setAlwaysShowScrollBars(true);
		
		SelectionAdapter enableUpdater=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				enableWidgets();
			}
		};
		
		Composite typeComp=new Composite(comp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginHeight=0;
		gl.marginWidth=0;
		typeComp.setLayout(gl);
		
		mFieldButton=new Button(typeComp, SWT.RADIO);
		mFieldButton.setText("Select properties");
		mFieldButton.addSelectionListener(enableUpdater);
		mFieldButton.setToolTipText("Choose this option to match properties");
		mFunctionButton=new Button(typeComp, SWT.RADIO);
		mFunctionButton.setText("Select functions");
		mFunctionButton.addSelectionListener(enableUpdater);
		mFunctionButton.setToolTipText("Choose this option to match functions");
		
		mGrabFirst=new Button(comp, SWT.CHECK);
		mGrabFirst.setText("Filter the members for this element before processing other elements");
		mGrabFirst.setToolTipText("Ex. If you want to use the standard function grabber to get functions, but you want this filter \nto select functions that start with 'put', then you could check this box so that functions that start\nwith 'put' get plucked out of the list first, and it doesn't matter whether they are ordered before or after\nthe other functions in the output.");
		
		mUseVisibility=new Button(comp, SWT.CHECK);
		mUseVisibility.setText("Select for visibility");
		mUseVisibility.addSelectionListener(enableUpdater);
		mUseVisibility.setToolTipText("Choose this option to match member visibility.  Not selecting means that any visibility is matched");
		Composite visComp=new Composite(comp, SWT.None);
		gl=new GridLayout(4, false);
		gl.marginHeight=0;
		visComp.setLayout(gl);
		
		mPublic=new Button(visComp, SWT.CHECK);
		mPublic.setText("public");
		mPublic.addSelectionListener(enableUpdater);
		mPublic.setToolTipText("Choose this option to match public members");
		mProtected=new Button(visComp, SWT.CHECK);
		mProtected.setText("protected");
		mProtected.addSelectionListener(enableUpdater);
		mProtected.setToolTipText("Choose this option to match protected members");
		mPrivate=new Button(visComp, SWT.CHECK);
		mPrivate.setText("private");
		mPrivate.addSelectionListener(enableUpdater);
		mPrivate.setToolTipText("Choose this option to match private members");
		mInternal=new Button(visComp, SWT.CHECK);
		mInternal.setText("internal");
		mInternal.addSelectionListener(enableUpdater);
		mInternal.setToolTipText("Choose this option to match internal members");
		
		mUseName=new Button(comp, SWT.CHECK);
		mUseName.setText("Select for name");
		mUseName.addSelectionListener(enableUpdater);
		mUseName.setToolTipText("Choose this option to match based on the member name");
		Composite nameComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginHeight=0;
		nameComp.setLayout(gl);
		nameComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label l=new Label(nameComp, SWT.None);
		l.setText("Members to include (comma separated)");
		mNameText=new Text(nameComp, SWT.SINGLE | SWT.BORDER);
		mNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mNameText.setToolTipText("Specify the names of members you want to match, using Java regular expression notation.  Ex. add.* matches any member starting with 'add'.");
		mNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		mInvertNames=new Button(nameComp, SWT.CHECK);
		mInvertNames.setText("Exclude these names");
		mInvertNames.addSelectionListener(enableUpdater);
		mInvertNames.setToolTipText("Checking this box matches any member names EXCEPT for ones matching the names in the text field.");
		
		mUseMetatag=new Button(comp, SWT.CHECK);
		mUseMetatag.setText("Select for metatag");
		mUseMetatag.addSelectionListener(enableUpdater);
		mUseMetatag.setToolTipText("Choose this option to match based on the member metatag(s)");
		Composite metatagComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginHeight=0;
		metatagComp.setLayout(gl);
		metatagComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		l=new Label(metatagComp, SWT.None);
		l.setText("Metatags to include (comma separated)");
		mMetatagText=new Text(metatagComp, SWT.SINGLE | SWT.BORDER);
		mMetatagText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mMetatagText.setToolTipText("Specify the names of metatags you want to match, using Java regular expression notation.  Ex. Eff.* matches any tag starting with 'Eff'.");
		mMetatagText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		mInvertMetatag=new Button(metatagComp, SWT.CHECK);
		mInvertMetatag.setText("Exclude these tags");
		mInvertMetatag.addSelectionListener(enableUpdater);
		mInvertMetatag.setToolTipText("Checking this box matches any member tags EXCEPT for ones matching the tags in the text field.");
		
		Composite tempComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		tempComp.setLayout(gl);
		tempComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mUseObjectType=new Button(tempComp, SWT.CHECK);
		mUseObjectType.setText("Select for type");
		mUseObjectType.addSelectionListener(enableUpdater);
		mUseObjectType.setToolTipText("If checked, filter on the type (function return type or variable declaration type");
		l=new Label(tempComp, SWT.None);
		l.setText("Member types to include (comma separated)");
		mObjectTypeText=new Text(tempComp, SWT.SINGLE | SWT.BORDER);
		mObjectTypeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mObjectTypeText.setToolTipText("Specify the types of members you want to match (the type of a variable or the return type of a function), using Java regular expression notation.\nEx. .*Event matches any member type ending with 'Event'.");
		mObjectTypeText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		
		tempComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		tempComp.setLayout(gl);
		tempComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mUseParameterName=new Button(tempComp, SWT.CHECK);
		mUseParameterName.setText("Select for first parameter name");
		mUseParameterName.addSelectionListener(enableUpdater);
		mUseParameterName.setToolTipText("If checked, filter on the name of the function parameter for functions with exactly 1 parameter");
		l=new Label(tempComp, SWT.None);
		l.setText("Parameter names to include (comma separated)");
		mParameterNameText=new Text(tempComp, SWT.SINGLE | SWT.BORDER);
		mParameterNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mParameterNameText.setToolTipText("Specify the names of parameters you want to match, using Java regular expression notation.\nEx. event.* matches a parameter starting with 'event'.");
		mParameterNameText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		
		tempComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		tempComp.setLayout(gl);
		tempComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mUseParameterType=new Button(tempComp, SWT.CHECK);
		mUseParameterType.setText("Select for first parameter type");
		mUseParameterType.addSelectionListener(enableUpdater);
		mUseParameterType.setToolTipText("If checked, filter on the type of the function parameter for functions with exactly 1 parameter");
		l=new Label(tempComp, SWT.None);
		l.setText("Parameter types to include (comma separated)");
		mParameterTypeText=new Text(tempComp, SWT.SINGLE | SWT.BORDER);
		mParameterTypeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mParameterTypeText.setToolTipText("Specify the types of parameters you want to match, using Java regular expression notation.\nThe type is the type used in the code, not the fully qualified type name\nEx. .*Event matches a parameter type ending with 'Event'.");
		mParameterTypeText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		
		mUseNamespace=new Button(comp, SWT.CHECK);
		mUseNamespace.setText("Select for namespace");
		mUseNamespace.addSelectionListener(enableUpdater);
		mUseNamespace.setToolTipText("Choose this option to match based on the member namespace");
		Composite namespaceComp=new Composite(comp, SWT.None);
		gl=new GridLayout(4, false);
		gl.marginHeight=0;
		namespaceComp.setLayout(gl);
		namespaceComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		mNoNamespaces=new Button(namespaceComp, SWT.CHECK);
		mNoNamespaces.setText("No namespaces");
		mNoNamespaces.addSelectionListener(enableUpdater);
		mNoNamespaces.setToolTipText("Choose this option to match only members without any namespace specified.");
		l=new Label(namespaceComp, SWT.None);
		l.setText("Namespaces to select (comma separated)");
		mNamespaceText=new Text(namespaceComp, SWT.SINGLE | SWT.BORDER);
		mNamespaceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mNamespaceText.setToolTipText("Specify the namespaces of members you want to match, using Java regular expression notation.  Ex. mx_.* matches any member starting with 'mx_'.");
		mNamespaceText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				enableWidgets();
			}
		});
		mInvertNamespaces=new Button(namespaceComp, SWT.CHECK);
		mInvertNamespaces.setText("Exclude these namespaces");
		mInvertNamespaces.addSelectionListener(enableUpdater);
		mInvertNamespaces.setToolTipText("Check this box to match any member namespace EXCEPT for ones matching the namespaces in the text field.");
		
		Composite staticComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		staticComp.setLayout(gl);
		mUseStatic=new Button(staticComp, SWT.CHECK);
		mUseStatic.setText("Select for static members");
		mUseStatic.addSelectionListener(enableUpdater);
		mUseStatic.setToolTipText("Choose this option to match based on the whether the member is static.  Otherwise, the static keyword is ignored in matching.");
		
		mStatic=new Button(staticComp, SWT.RADIO);
		mStatic.setText("static");
		mStatic.addSelectionListener(enableUpdater);
		mStatic.setToolTipText("Match static members");
		mNonStatic=new Button(staticComp, SWT.RADIO);
		mNonStatic.setText("non-static");
		mNonStatic.addSelectionListener(enableUpdater);
		mNonStatic.setToolTipText("Match non-static members");
		
		Composite constComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		constComp.setLayout(gl);
		mUseConst=new Button(constComp, SWT.CHECK);
		mUseConst.setText("Select for const members");
		mUseConst.addSelectionListener(enableUpdater);
		mUseConst.setToolTipText("Choose this option to match based on the whether the member is const.  Otherwise, the const keyword is ignored in matching.");
		
		mConst=new Button(constComp, SWT.RADIO);
		mConst.setText("constants");
		mConst.addSelectionListener(enableUpdater);
		mConst.setToolTipText("Match const members");
		mNonConst=new Button(constComp, SWT.RADIO);
		mNonConst.setText("variables");
		mNonConst.addSelectionListener(enableUpdater);
		mNonConst.setToolTipText("Match var members");
		
		Composite finalComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		finalComp.setLayout(gl);
		mUseFinal=new Button(finalComp, SWT.CHECK);
		mUseFinal.setText("Select for final functions");
		mUseFinal.addSelectionListener(enableUpdater);
		mUseFinal.setToolTipText("Choose this option to match based on the whether the member is final.  Otherwise, the final keyword is ignored in matching.");
		
		mFinal=new Button(finalComp, SWT.RADIO);
		mFinal.setText("final");
		mFinal.addSelectionListener(enableUpdater);
		mFinal.setToolTipText("Match final members");
		mNonFinal=new Button(finalComp, SWT.RADIO);
		mNonFinal.setText("non-final");
		mNonFinal.addSelectionListener(enableUpdater);
		mNonFinal.setToolTipText("Match non-final members");
		
		Composite nativeComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		nativeComp.setLayout(gl);
		mUseNative=new Button(nativeComp, SWT.CHECK);
		mUseNative.setText("Select for native functions");
		mUseNative.addSelectionListener(enableUpdater);
		mUseNative.setToolTipText("Choose this option to match based on the whether the member is a native function.  Otherwise, the native keyword is ignored in matching.");
		
		mNative=new Button(nativeComp, SWT.RADIO);
		mNative.setText("native");
		mNative.addSelectionListener(enableUpdater);
		mNative.setToolTipText("Match native functions");
		mNonNative=new Button(nativeComp, SWT.RADIO);
		mNonNative.setText("non-native");
		mNonNative.addSelectionListener(enableUpdater);
		mNonNative.setToolTipText("Match non-native functions");
		
		Composite overrideComp=new Composite(comp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		overrideComp.setLayout(gl);
		mUseOverride=new Button(overrideComp, SWT.CHECK);
		mUseOverride.setText("Select for override functions");
		mUseOverride.addSelectionListener(enableUpdater);
		mUseOverride.setToolTipText("Choose this option to match based on the whether the member is a function override.  Otherwise, the override keyword is ignored in matching.");
		
		mOverride=new Button(overrideComp, SWT.RADIO);
		mOverride.setText("override");
		mOverride.addSelectionListener(enableUpdater);
		mOverride.setToolTipText("Match override functions");
		mNonOverride=new Button(overrideComp, SWT.RADIO);
		mNonOverride.setText("non-override");
		mNonOverride.addSelectionListener(enableUpdater);
		mNonOverride.setToolTipText("Match non-override functions");
		
		Composite ftComp=new Composite(comp, SWT.None);
		gl=new GridLayout(5, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		ftComp.setLayout(gl);
		mUseFunctionType=new Button(ftComp, SWT.CHECK);
		mUseFunctionType.setText("Select for function type");
		mUseFunctionType.addSelectionListener(enableUpdater);
		mUseFunctionType.setToolTipText("Choose this option to match based on the type of the function.  Otherwise, any functions are matched.");
		
		mConstructor=new Button(ftComp, SWT.CHECK);
		mConstructor.setText("constructor");
		mConstructor.addSelectionListener(enableUpdater);
		mConstructor.setToolTipText("Match class constructors");
		mGet=new Button(ftComp, SWT.CHECK);
		mGet.setText("get");
		mGet.addSelectionListener(enableUpdater);
		mGet.setToolTipText("Match getter methods");
		mSet=new Button(ftComp, SWT.CHECK);
		mSet.setText("set");
		mSet.addSelectionListener(enableUpdater);
		mSet.setToolTipText("Match setter methods");
		mOthers=new Button(ftComp, SWT.CHECK);
		mOthers.setText("others");
		mOthers.addSelectionListener(enableUpdater);
		mOthers.setToolTipText("Match 'normal' methods, i.e. not getters/setters/constructors");
		
		mIncludeImplicitProperties=new Button(comp, SWT.CHECK);
		mIncludeImplicitProperties.setText("Include getter and setter functions as properties");
		mIncludeImplicitProperties.setToolTipText("If checked, include getter/setter functions in the list of properties that is selected by this member selector.");
		mIncludeImplicitProperties.addSelectionListener(enableUpdater);

		mIncludeRealProperties=new Button(comp, SWT.CHECK);
		mIncludeRealProperties.setText("Include associated real properties");
		mIncludeRealProperties.setToolTipText("If checked, include properties associated with getter/setter functions in the list of items selected by this member selector.");
		mIncludeRealProperties.addSelectionListener(enableUpdater);
		
		Group sortGroup=new Group(comp, SWT.None);
		sortGroup.setText("Sorting");
		sortGroup.setLayout(new GridLayout(2, false));
		sortGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		mSortMembers=new Button(sortGroup, SWT.CHECK);
		mSortMembers.setText("Sort members by name");
		mSortMembers.addSelectionListener(enableUpdater);
		mSortMembers.setToolTipText("Choosing this option sorts the matched members by name.  Not selecting this option means that the items matched by this specification will be grouped together in the file, but will retain the relative order that they had previously.");
		mSortByCase=new Button(sortGroup, SWT.CHECK);
		mSortByCase.setText("Sort with case sensitivity");
		mSortByCase.addSelectionListener(enableUpdater);
		mSortByCase.setToolTipText("If checked, then sort names with regard to character case.  If not checked, then lowercase letters will be treated just like uppercase when sorting");
		
		mSortByType=new Button(sortGroup, SWT.CHECK);
		mSortByType.setText("Group members first by type (return type for functions)");
		mSortByType.addSelectionListener(enableUpdater);
		mSortByType.setToolTipText("Choosing this option first groups the matched members by type before sorting by name.  Not selecting this option means that type will be ignored.");
		
		mAssociateImplicitProperties=new Button(sortGroup, SWT.CHECK);
		mAssociateImplicitProperties.setText("Associate implicit properties");
		mAssociateImplicitProperties.addSelectionListener(enableUpdater);
		mAssociateImplicitProperties.setToolTipText("If checked, then sort the real property with its associated implicit properties (getter/setter), if they can be identified.");
		
		mHeaderButton=new Button(comp, SWT.PUSH);
		mHeaderButton.setText("Headers...");
		mHeaderButton.setToolTipText("Configure whether to create a default minor property header for properties in this group.");
		mHeaderButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				PropertyHeaderDlg dlg=new PropertyHeaderDlg(getShell(), mPropertyHeaderStyle, mAssociateImplicitProperties.getSelection() && (mIncludeImplicitProperties.getSelection() || mIncludeRealProperties.getSelection()));
				if (dlg.open()==Dialog.OK)
				{
					mPropertyHeaderStyle=dlg.getStyle();
				}
			}
		});
		
		mToStringArea=new Text(comp, SWT.SINGLE | SWT.H_SCROLL | SWT.READ_ONLY);
		mToStringArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mToStringArea.setToolTipText("This string indicates the arguments in the specification.  This string is what is used in the ordering table to let you quickly know what attributes you are selecting for.");

		updateWidgets(mData);
		mUpdateObject=true;
		enableWidgets();
		scrollComp.setMinSize(scrollComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return mainComp;
	}

	private void updateWidgets(MemberSelectionSpec data)
	{
		mConstructor.setSelection((data.getFunctionTypeFlags() & MemberSelectionSpec.FuncType_Constructor)!=0);
		mSet.setSelection((data.getFunctionTypeFlags() & MemberSelectionSpec.FuncType_Setter)!=0);
		mGet.setSelection((data.getFunctionTypeFlags() & MemberSelectionSpec.FuncType_Getter)!=0);
		mOthers.setSelection((data.getFunctionTypeFlags() & MemberSelectionSpec.FuncType_Other)!=0);
		
		mFieldButton.setSelection(!data.isFunction());
		mFunctionButton.setSelection(data.isFunction());
		
		mGrabFirst.setSelection(data.getPreselectPriority()>0);
		
		mFinal.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Final)!=0);
		mNonFinal.setSelection((data.getExcludeAttrs() & TopLevelItemRecord.ASDoc_Final)!=0);
		
		mOverride.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Override)!=0);
		mNonOverride.setSelection((data.getExcludeAttrs() & TopLevelItemRecord.ASDoc_Override)!=0);
		
		mStatic.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Static)!=0);
		mNonStatic.setSelection((data.getExcludeAttrs() & TopLevelItemRecord.ASDoc_Static)!=0);
		
		mConst.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Const)!=0);
		mNonConst.setSelection((data.getExcludeAttrs() & TopLevelItemRecord.ASDoc_Const)!=0);
		
		mNative.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Native)!=0);
		mNonNative.setSelection((data.getExcludeAttrs() & TopLevelItemRecord.ASDoc_Native)!=0);
		
		mInvertNames.setSelection(data.isInvertNames());
		mInvertNamespaces.setSelection(data.isInvertNamespaces());
		mInvertMetatag.setSelection(data.isInvertMetatags());
		
		mNameText.setText(MemberSelectionSpec.getString(data.getNames(), false));
		mMetatagText.setText(MemberSelectionSpec.getString(data.getMetatags(), false));
		mObjectTypeText.setText(MemberSelectionSpec.getString(data.getObjectTypes(), false));
		mParameterNameText.setText(MemberSelectionSpec.getString(data.getFirstArgNames(), false));
		mParameterTypeText.setText(MemberSelectionSpec.getString(data.getFirstArgTypes(), false));
		mNamespaceText.setText(MemberSelectionSpec.getString(data.getNamespaces(), false));		
		
		mInternal.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Internal)!=0);
		mPrivate.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Private)!=0);
		mProtected.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Protected)!=0);
		mPublic.setSelection((data.getIncludeAttrs() & TopLevelItemRecord.ASDoc_Public)!=0);
		
		mNoNamespaces.setSelection((data.getExcludeAttrs() & TopLevelItemRecord.ASDoc_Namespace)!=0);
		
		mSortByCase.setSelection((data.getSortFlags() & MemberSelectionSpec.Sort_CaseSensitive)!=0);
		mSortMembers.setSelection((data.getSortFlags() & MemberSelectionSpec.Sort_On)!=0);
		mSortByType.setSelection((data.getSortFlags() & MemberSelectionSpec.Sort_ByType)!=0);
		mAssociateImplicitProperties.setSelection((data.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0);
		
		mUseFinal.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Final)!=0);
		mUseFunctionType.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_FunctionType)!=0);
		mUseName.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Names)!=0);
		mUseNamespace.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Namespaces)!=0);
		mUseNative.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Native)!=0);
		mUseOverride.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Override)!=0);
		mUseStatic.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Static)!=0);
		mUseConst.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Const)!=0);
		mUseVisibility.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_Visibility)!=0);
		mUseObjectType.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_ObjectType)!=0);
		mUseParameterName.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_ParameterName)!=0);
		mUseParameterType.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_ParameterType)!=0);
		mUseMetatag.setSelection((data.getUseFlags() & MemberSelectionSpec.Use_MetaTag)!=0);
		
		mIncludeImplicitProperties.setSelection(data.isIncludeGetters());
		mIncludeRealProperties.setSelection(data.isIncludeAssociatedProperty());
		
		mPropertyHeaderStyle=data.getPropertyHeaderStyle();
	}
	
	@Override
	protected void okPressed()
	{
		saveData();
		super.okPressed();
	}
	
	private void saveData()
	{
		if (!mUpdateObject)
			return;
		
		int useFlags=0;
		if (mUseFinal.getSelection())
			useFlags|=MemberSelectionSpec.Use_Final;
		if (mUseFunctionType.getSelection())
			useFlags|=MemberSelectionSpec.Use_FunctionType;
		if (mUseName.getSelection())
			useFlags|=MemberSelectionSpec.Use_Names;
		if (mUseNamespace.getSelection())
			useFlags|=MemberSelectionSpec.Use_Namespaces;
		if (mUseNative.getSelection())
			useFlags|=MemberSelectionSpec.Use_Native;
		if (mUseOverride.getSelection())
			useFlags|=MemberSelectionSpec.Use_Override;
		if (mUseStatic.getSelection())
			useFlags|=MemberSelectionSpec.Use_Static;
		if (mUseConst.getSelection())
			useFlags|=MemberSelectionSpec.Use_Const;
		if (mUseVisibility.getSelection())
			useFlags|=MemberSelectionSpec.Use_Visibility;
		if (mUseObjectType.getSelection())
			useFlags|=MemberSelectionSpec.Use_ObjectType;
		if (mUseParameterName.getSelection())
			useFlags|=MemberSelectionSpec.Use_ParameterName;
		if (mUseParameterType.getSelection())
			useFlags|=MemberSelectionSpec.Use_ParameterType;
		if (mUseMetatag.getSelection())
			useFlags|=MemberSelectionSpec.Use_MetaTag;
		mData.setUseFlags(useFlags);
		
		int funcFlags=0;
		if (mConstructor.getSelection())
			funcFlags|=MemberSelectionSpec.FuncType_Constructor;
		if (mOthers.getSelection())
			funcFlags|=MemberSelectionSpec.FuncType_Other;
		if (mGet.getSelection())
			funcFlags|=MemberSelectionSpec.FuncType_Getter;
		if (mSet.getSelection())
			funcFlags|=MemberSelectionSpec.FuncType_Setter;
		mData.setFunctionTypeFlags(funcFlags);
		
		mData.setFunction(mFunctionButton.getSelection());
		
		mData.setInvertNames(mInvertNames.getSelection());
		mData.setInvertNamespaces(mInvertNamespaces.getSelection());
		mData.setInvertMetatags(mInvertMetatag.getSelection());
		
		int excludeFlags=0;
		if (mNonFinal.getSelection())
			excludeFlags|=TopLevelItemRecord.ASDoc_Final;
		if (mNonNative.getSelection())
			excludeFlags|=TopLevelItemRecord.ASDoc_Native;
		if (mNonOverride.getSelection())
			excludeFlags|=TopLevelItemRecord.ASDoc_Override;
		if (mNonStatic.getSelection())
			excludeFlags|=TopLevelItemRecord.ASDoc_Static;
		if (mNonConst.getSelection())
			excludeFlags|=TopLevelItemRecord.ASDoc_Const;
		if (mNoNamespaces.getSelection())
			excludeFlags|=TopLevelItemRecord.ASDoc_Namespace;
		mData.setExcludeAttrs(excludeFlags);
		
		int includeFlags=0;
		if (mFinal.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Final;
		if (mNative.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Native;
		if (mOverride.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Override;
		if (mPrivate.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Private;
		if (mProtected.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Protected;
		if (mPublic.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Public;
		if (mStatic.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Static;
		if (mConst.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Const;
		if (mInternal.getSelection())
			includeFlags|=TopLevelItemRecord.ASDoc_Internal;
		mData.setIncludeAttrs(includeFlags);
		
		int sortFlags=0;
		if (mSortMembers.getSelection())
			sortFlags|=MemberSelectionSpec.Sort_On;
		if (mSortByCase.getSelection())
			sortFlags|=MemberSelectionSpec.Sort_CaseSensitive;
		if (mAssociateImplicitProperties.getSelection())
			sortFlags|=MemberSelectionSpec.Associate_Getters;
		if (mSortByType.getSelection())
			sortFlags|=MemberSelectionSpec.Sort_ByType;
		mData.setSortFlags(sortFlags);
		
		mData.setIncludeGetters(mIncludeImplicitProperties.getSelection());
		mData.setIncludeAssociatedProperty(mIncludeRealProperties.getSelection());
		
		mData.setNames(MemberSelectionSpec.getItems(mNameText.getText(), false));
		mData.setMetatags(MemberSelectionSpec.getItems(mMetatagText.getText(), false));
		mData.setObjectTypes(MemberSelectionSpec.getItems(mObjectTypeText.getText(), false));
		mData.setFirstArgNames(MemberSelectionSpec.getItems(mParameterNameText.getText(), false));
		mData.setFirstArgTypes(MemberSelectionSpec.getItems(mParameterTypeText.getText(), false));
		mData.setNamespaces(MemberSelectionSpec.getItems(mNamespaceText.getText(), false));
		mData.setPropertyHeaderStyle(mPropertyHeaderStyle);
		if (mGrabFirst.getSelection())
			mData.setPreselectPriority(Math.max(1, mOldSelectPriority)); //we need to give it at least 1 so that it's identified as having a preselect priority
		else
			mData.setPreselectPriority(0);
	}
	
	public MemberSelectionSpec getSelectionSpec()
	{
		return mData;
	}

	private void enableWidgets()
	{
		mPublic.setEnabled(mUseVisibility.getSelection());
		mProtected.setEnabled(mUseVisibility.getSelection());
		mPrivate.setEnabled(mUseVisibility.getSelection());
		mInternal.setEnabled(mUseVisibility.getSelection());
		
		mNameText.setEnabled(mUseName.getSelection());
		mInvertNames.setEnabled(mUseName.getSelection());
		
		mObjectTypeText.setEnabled(mUseObjectType.getSelection());
		mParameterNameText.setEnabled(mUseParameterName.getSelection() && mFunctionButton.getSelection());
		mParameterTypeText.setEnabled(mUseParameterType.getSelection() && mFunctionButton.getSelection());
		mUseParameterName.setEnabled(mFunctionButton.getSelection());
		mUseParameterType.setEnabled(mFunctionButton.getSelection());
		
		mNoNamespaces.setEnabled(mUseNamespace.getSelection());
		mNamespaceText.setEnabled(mUseNamespace.getSelection() && !mNoNamespaces.getSelection());
		mInvertNamespaces.setEnabled(mUseNamespace.getSelection() && !mNoNamespaces.getSelection());

		mInvertMetatag.setEnabled(mUseMetatag.getSelection());
		mMetatagText.setEnabled(mUseMetatag.getSelection());
		
		mStatic.setEnabled(mUseStatic.getSelection());
		mNonStatic.setEnabled(mUseStatic.getSelection());
		
		mUseConst.setEnabled(mFieldButton.getSelection());
		if (!mFieldButton.getSelection())
			mUseConst.setSelection(false);
		mConst.setEnabled(mUseConst.getEnabled() && mUseConst.getSelection());
		mNonConst.setEnabled(mUseConst.getEnabled() && mUseConst.getSelection());
		
		mUseFinal.setEnabled(mFunctionButton.getSelection());
		if (!mFunctionButton.getSelection())
			mUseFinal.setSelection(false);
		mFinal.setEnabled(mUseFinal.getEnabled() && mUseFinal.getSelection());
		mNonFinal.setEnabled(mUseFinal.getEnabled() && mUseFinal.getSelection());
		
		mUseNative.setEnabled(mFunctionButton.getSelection());
		if (!mFunctionButton.getSelection())
			mUseNative.setSelection(false);
		mNative.setEnabled(mUseNative.getEnabled() && mUseNative.getSelection());
		mNonNative.setEnabled(mUseNative.getEnabled() && mUseNative.getSelection());
		
		mUseOverride.setEnabled(mFunctionButton.getSelection());
		if (!mFunctionButton.getSelection())
			mUseOverride.setSelection(false);
		mOverride.setEnabled(mUseOverride.getEnabled() && mUseOverride.getSelection());
		mNonOverride.setEnabled(mUseOverride.getEnabled() && mUseOverride.getSelection());
		
		mUseFunctionType.setEnabled(mFunctionButton.getSelection());
		if (!mFunctionButton.getSelection())
			mUseFunctionType.setSelection(false);
		mConstructor.setEnabled(mUseFunctionType.getEnabled() && mUseFunctionType.getSelection());
		mGet.setEnabled(mUseFunctionType.getEnabled() && mUseFunctionType.getSelection());
		mSet.setEnabled(mUseFunctionType.getEnabled() && mUseFunctionType.getSelection());
		mOthers.setEnabled(mUseFunctionType.getEnabled() && mUseFunctionType.getSelection());
		
		mSortByCase.setEnabled(mSortMembers.getSelection());
		mSortByType.setEnabled(mSortMembers.getSelection());
		mAssociateImplicitProperties.setEnabled((mFieldButton.getSelection() && /*mSortMembers.getSelection() &&*/ mIncludeImplicitProperties.getSelection())
				|| (mFunctionButton.getSelection() && /*mSortMembers.getSelection() &&*/ mIncludeRealProperties.getSelection()));
		
		mIncludeImplicitProperties.setEnabled(mFieldButton.getSelection());
		mIncludeRealProperties.setEnabled(mFunctionButton.getSelection() && (mGet.getSelection() || mSet.getSelection() || !mUseFunctionType.getSelection()));
		mHeaderButton.setEnabled(mFieldButton.getSelection() || (mFunctionButton.getSelection() && mIncludeRealProperties.getSelection()));
		
		if (!mAssociateImplicitProperties.isEnabled())
			mAssociateImplicitProperties.setSelection(false);
		if (!mIncludeImplicitProperties.isEnabled())
			mIncludeImplicitProperties.setSelection(false);
		if (!mIncludeRealProperties.isEnabled())
			mIncludeRealProperties.setSelection(false);
		
		validateSelections();
		saveData();
		mToStringArea.setText(mData.toString());
	}
	
	private void validateSelections()
	{
		Button okButton=getButton(IDialogConstants.OK_ID);
		if (okButton!=null)
			okButton.setEnabled(true);
		if (mUseFinal.getSelection())
		{
			if (!mFinal.getSelection() && !mNonFinal.getSelection())
			{
				setErrorMessage("If selecting for the final keyword, you must choose to include or exclude it");
				okButton.setEnabled(false);
				return;
			}
		}
		if (mUseFunctionType.getSelection())
		{
			if (!mSet.getSelection() && !mGet.getSelection() && !mOthers.getSelection() && !mConstructor.getSelection())
			{
				setErrorMessage("If selecting based on function type, you must choose at least one category");
				okButton.setEnabled(false);
				return;
			}
		}
		if (mUseNative.getSelection())
		{
			if (!mNative.getSelection() && !mNonNative.getSelection())
			{
				setErrorMessage("If selecting for the native keyword, you must choose to include or exclude it");
				okButton.setEnabled(false);
				return;
			}
		}
		if (mUseOverride.getSelection())
		{
			if (!mOverride.getSelection() && !mNonOverride.getSelection())
			{
				setErrorMessage("If selecting for the override keyword, you must choose to include or exclude it");
				okButton.setEnabled(false);
				return;
			}
		}
		if (mUseStatic.getSelection())
		{
			if (!mStatic.getSelection() && !mNonStatic.getSelection())
			{
				setErrorMessage("If selecting for the static keyword, you must choose to include or exclude it");
				okButton.setEnabled(false);
				return;
			}
		}
		if (mUseConst.getSelection())
		{
			if (!mConst.getSelection() && !mNonConst.getSelection())
			{
				setErrorMessage("If selecting for the const keyword, you must choose to include or exclude it");
				okButton.setEnabled(false);
				return;
			}
		}
		if (mUseVisibility.getSelection())
		{
			if (!mPublic.getSelection() && !mPrivate.getSelection() && !mProtected.getSelection() && !mInternal.getSelection())
			{
				setErrorMessage("If selecting based on function visibility, you must choose at least one category");
				okButton.setEnabled(false);
				return;
			}
		}
		
		if (mUseName.getSelection())
		{
			if (mNameText.getText().length()==0)
			{
				setErrorMessage("If selecting based on function name, you must specify at least one name");
				okButton.setEnabled(false);
				return;
			}
		}
		
		if (mUseMetatag.getSelection())
		{
			if (mMetatagText.getText().length()==0)
			{
				setErrorMessage("If selecting based on metatag, you must specify at least one tag");
				okButton.setEnabled(false);
				return;
			}
		}
		
		if (mUseObjectType.getSelection())
		{
			if (mObjectTypeText.getText().length()==0)
			{
				setErrorMessage("If selecting based on member type, you must specify at least one type");
				okButton.setEnabled(false);
				return;
			}
		}
		
		if (mUseParameterName.getSelection())
		{
			if (mParameterNameText.getText().length()==0)
			{
				setErrorMessage("If selecting based on parameter name, you must specify at least one name");
				okButton.setEnabled(false);
				return;
			}
		}
		
		if (mUseParameterType.getSelection())
		{
			if (mParameterTypeText.getText().length()==0)
			{
				setErrorMessage("If selecting based on parameter type, you must specify at least one type");
				okButton.setEnabled(false);
				return;
			}
		}
		
		if (mUseNamespace.getSelection() && !mNoNamespaces.getSelection())
		{
			if (mNamespaceText.getText().length()==0)
			{
				setErrorMessage("If selecting based on function namespace, you must specify at least one namespace");
				okButton.setEnabled(false);
				return;
			}
		}
		
		setErrorMessage(null);
		setMessage("Select the options that pick the functions or properties that you want to include in this group");
	}
	
	protected int getShellStyle()
	{
		return (super.getShellStyle() | SWT.RESIZE);
	}
}
