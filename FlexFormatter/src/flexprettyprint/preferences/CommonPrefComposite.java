package flexprettyprint.preferences;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import utilities.CommandLine;
import utilities.FormatUtility;

import flexasrearrangecodecommand.handlers.ASRearranger;
import flexasrearrangecodecommand.handlers.MXMLRearranger;
import flexasrearrangecodecommand.preferences.PreferenceConstants;
import flexprettyprint.handlers.ASPrettyPrinter;
import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprint.handlers.WrapOptions;
import flexprettyprintcommand.Activator;

public class CommonPrefComposite extends Composite {

	//management variables
	private boolean mVisualsInitialized;
	private boolean mDisableMode=false;
	private IPreferenceStore mPrefStore;
	
	private Map<String, SectionSpec> mHeaderSpecs=new HashMap<String, SectionSpec>();

	private Text mNoFormatWarning;
	
	private Group mSpacesGroup;
	private String mOriginalASSampleText;
	private String mOriginalMXMLSampleText;
	private String mOriginalASRearrangingSampleText;
	private String mOriginalMXMLRearrangingSampleText;
	private StyledText mSampleASText;
	private StyledText mSampleMXMLText;
	private StyledText mSampleRearrangingText;
	private StyledText mSampleMXMLRearrangingText;
	private Spinner mSpacesBeforeComma;
	private Spinner mSpacesAfterComma;
	private Spinner mSpacesAroundAssignment;
	private Spinner mASSpacesAroundColons;
	private Spinner						mASAdvancedSpacesBeforeColonsInDeclarations;
	private Spinner						mASAdvancedSpacesAfterColonsInDeclarations;
	private Spinner						mASAdvancedSpacesBeforeColonsInFunctions;
	private Spinner						mASAdvancedSpacesAfterColonsInFunctions;
	private Button mASUseGlobalSpacesAroundColons;
	private Spinner mSpacesAroundSymbolicOperator;
	private Spinner mASBlankLinesBeforeControlStatement;
	private Spinner mASBlankLinesBeforeImports;
	private Spinner mASBlankLinesBeforeProperties;
	private Spinner mASBlankLinesBeforeClass;
	private Spinner mASBlankLinesAtFunctionStart;
	private Spinner mASBlankLinesAtFunctionEnd;
//	private Button mASTrimTrailingWS;
	private Spinner mASSpacesAfterLabelColon;
	private Spinner mASSpacesBeforeOpenControlParen;
	private Spinner mASSpacesBeforeDeclParameters;
	private Spinner mASSpacesBeforeArguments;
	private Button mASAlwaysGenerateIndent;
	
	private Button mASDontIndentPackageElements;
	private Button mASDontIndentSwitchCases;
	private Button mASLeaveExtraWhitespaceAroundVarDecls;
	private Button mASAlignDeclEquals;
	private Button mASAlignDeclEqualsConsecutive;
	private Button mASAlignDeclEqualsScope;
	private Button mASKeepSingleLineFunctions;
//	private Button mASAlignSubsequentAssignmentEquals;
	private Button mASEnsureLoopBraces;
	private Button mASNoModifyLoopBraces;
	private Button mASSmartAddLoopBraces;
	private Button mASSmartAddRemoveLoopBraces;
	private Button mASUseGnuBraceIndent;

	//TODO: option for making all if/else in chain the same
	private Button mASEnsureConditionalBraces;
	private Button mASNoModifyConditionalBraces;
	private Button mASSmartAddConditionalBraces;
	private Button mASSmartAddRemoveConditionalBraces;

	private Button mASSmartAddSwitchBraces;
	private Button mASNoModifySwitchBraces;
	private Button mASRemoveSwitchBraces;
	
	private Button mASKeepSpacesBeforeLineComments;
	private Button mASAlignCommentsAtColumn;
	private Button mASAddOneSpaceBeforeLineComments;
	private Spinner mASAlignCommentsColumn;
	private Button mASKeepRelativeIndentOfMultilineComments;
	private Button mASWrapLineComments;
	private Button mASWrapASDocComments;
	private Button mASWrapMLComments;
	private Button mASWrapMLCommentsReflow;
	private Button mASWrapMLCommentsKeepBlankLines;
	private Button mASWrapMLCommentsSeparateHeader;
	private Combo mASWrapMLCommentsAsteriskMode;
	private Spinner mASWrapDocCommentsHangingTabs;
	private Button mASWrapDocCommentsReflow;
	private Button mASWrapDocCommentsKeepBlankLines;
	

	private Button mASEmptyStatementsOnNewLine;
	private Button mASUseGlobalSpacesInsideParens;
	private Spinner mASSpacesInsideParens;
	private Spinner mASAdvancedSpacesInsideParensInOtherPlaces;
	private Spinner mASAdvancedSpacesInsideParensInParameterLists;
	private Spinner mASAdvancedSpacesInsideParensInArgumentLists;
	private Spinner mASAdvancedSpacesInsideObjectLiteralBraces;
	private Spinner mASAdvancedSpacesInsideArrayRefBrackets;
	private Spinner mASAdvancedSpacesInsideArrayDeclBrackets;
	private Spinner mASSpacesAroundAssignmentInParameters;
	private Button mASUseSpacesAroundAssignmentInParameters;
	private Button mASRearrangeDuringFormatting;
	private Button mASUseSpacesAroundAssignmentInMetatags;
	private Spinner mASSpacesAroundAssignmentInMetatags;
	
	private Button mASNoIndentForExpressionTerminatorButton;
	
	private Button mASUseAdvancedWrapping;
	private Table mASWrappingItemsTable;
	private Spinner mASWrappingPostMaxLeniency;
	private Button mASWrappingBreakOnPhrases;
	private Button mASWrappingEnforceMax;
	private Button mASWrappingAllArgs;
	private Button mASWrappingFirstArg;
	private Button mASWrappingAllParms;
	private Button mASWrappingFirstParm;
	private Button mASWrappingAllObjectItems;
	private Button mASWrappingFirstObjectItem;
	private Button mASWrappingAllArrayItems;
	private Button mASWrappingFirstArrayItem;
	private Button mASWrappingAlignArrayItems;
	private Button mASWrappingAlignObjectItems;
	
	private Button mASCollapseAdjacentParens;
	private Button mASNewlineBeforeBindableProperty;
	private Button mASNewlineBeforeBindableFunction;
	private Spinner mMXMLSpacesAroundEquals;
	private Button mMXMLKeepBlankLines;
	private Spinner mMXMLBlankLinesBeforeComments;
	private Spinner mBlankLinesBeforeFunctions;
	private Button mKeepBlankLines;
	private Spinner mASBlankLinesToKeep;
	private Button mKeepSLCommentsOnColumn1;

	private Button mOpenBraceOnNewLine;
	private Button mASUseGlobalOpenBraceOnNewLine;
	private List<Button> mASAdvancedOpenBraceButtons=new ArrayList<Button>();
	private List<Text> mASAdvancedOpenBraceInheritedValues=new ArrayList<Text>();
	private Button mKeepElseIfOnSameLine;
	private Button mASUseBraceStyle;
	private Combo mASBraceStyle;
	private Button mASCRBeforeElse;
	private Button mASCRBeforeWhile; //in a do..while loop
	private Button mASCRBeforeCatch;
	private Button mASNoCRBeforeContinue;
	private Button mASNoCRBeforeBreak;
	private Button mASNoCRBeforeReturn;
	private Button mASNoCRBeforeThrow;
	private Button mASNoCRBeforeExpression;
	private Text mOpenBraceOnNewLineInheritedVal;
	private Text mKeepElseIfOnSameLineInheritedVal;
	private Text mASCRBeforeElseInheritedVal;
	private Text mASCRBeforeWhileInheritedVal;
	private Text mASCRBeforeCatchInheritedVal;
	private Text mASNoCRBeforeBreakInheritedVal;
	private Text mASNoCRBeforeContinueInheritedVal;
	private Text mASNoCRBeforeReturnInheritedVal;
	private Text mASNoCRBeforeThrowInheritedVal;
	private Text mASNoCRBeforeExpressionInheritedVal;
	private Spinner mASHangingIndentSize;
	
	private Button mMXMLRearrangeWhileFormatting;
	private Button mUseMXMLTagOrdering;
	private Table mMXMLOrderTable;
	private Button mMXMLOrderAddButton;
	private Button mMXMLOrderDeleteButton;
	private Button mMXMLOrderEditButton;
	private Button mMXMLOrderUpButton;
	private Button mMXMLOrderDownButton;
	private Button mMXMLRestoreDefaultsButton;
	
	private Button mASBreakLinesBeforeComma;
	private Button mASBreakLinesBeforeArithmeticOperator;
	private Button mASBreakLinesBeforeLogicalOperator;
	private Button mASBreakLinesBeforeAssignment;
	private Spinner mASMaxLineLength;
	private Button mUseTabsRadio;
	private Button mUseSpacesRadio;
	private Combo mASMethodDeclWrapCombo;
	private Combo mASMethodCallWrapCombo;
	private Combo mASExpressionWrapCombo;
	private Combo mASArrayDeclWrapCombo;
	private Combo mASXMLWrapCombo;
	private Button mASSpecialWrapCommaItems;
	private Button mMXMLSpecialWrapTags;
	private Spinner mMXMLHangingIndentSize;
	private Spinner mMXMLSpacesBeforeEmptyTagEnd;
	private Button mMXMLUseSpacesInsideBraces;
	private Spinner mMXMLSpacesInsideBraces;
	private Button mMXMLFormatBindingExpressions;
	private Button mMXMLKeepRelativeIndentInsideMultilineComments;
	
	private Spinner mMXMLAttrsToKeepOnSameLineSpinner;
	private Button mMXMLUseAttrsToKeepOnSameLineButton;
	private Button mMXMLObeyMaxLength;
	
	private Button mMXMLRequireCDataButton;
	private Table mMXMLTagsContainingActionScriptTable;
	private Button mMXMLASTagsAddButton;
	private Button mMXMLASTagsDeleteButton;
	private Button mMXMLASTagsEditButton;

	private int mSortMode;
	private boolean mSortExtraAttrs;
	private boolean mAddNewlineAfterLastAttr;
	private boolean mIndentTagClose;
	private List<String> mManualSortOrder;
	private List<AttrGroup> mMXMLAttrGroups;
	
	private Button mWrapNoneButton;
	private Button mWrapLineLengthButton;
	private Button mWrapItemsPerLineButton;
	private Spinner mMaxLineLengthSpinner;
	private Spinner mAttrsPerLineSpinner;
	
	private Button mNeverFormatAddButton;
	private Button mNeverFormatRemoveButton;
	private Button mAlwaysFormatAddButton;
	private Button mAlwaysFormatRemoveButton;
	private Table mNeverFormatTable;
	private Table mAlwaysFormatTable;
	private Table mExcludeSubTagsTable;
	private Button mUseExcludeSubTags;
	private Button mExcludeAddButton;
	private Button mExcludeEditButton;
	private Button mExcludeRemoveButton;
	
	private Table mTagsWithLeadingBlankLinesTable;
	private Table mTagsWithTrailingBlankLinesTable;
	private Spinner mMXMLBlankLinesBeforeTagsSpinner;
	private Spinner mMXMLBlankLinesAfterSpecificTagsSpinner;
	private Spinner mMXMLBlankLinesBetweenSiblingTagsSpinner;
	private Spinner mMXMLBlankLinesAfterParentTagsSpinner;
	private Spinner mMXMLBlankLinesBeforeCloseTagsSpinner;
	private Button mTagLeadingLinesRemoveButton;
	private Button mTagLeadingLinesAddButton;
	private Button mTagTrailingLinesRemoveButton;
	private Button mTagTrailingLinesAddButton;
	
	private Table mSingleLineMetaTagsTable;
	private Button mSingleLineMetaTagAddButton;
	private Button mSingleLineMetaTagRemoveButton;
	
	private Spinner mMXMLTabsBeforeCDATASpinner;
	private Spinner mMXMLTabsBeforeScriptCodeSpinner;
	private Button mMXMLKeepScriptCDATAOnSameLine;
	private Spinner mMXMLBlankLinesInsideCDATASpinner;
	
	private Button mMXMLRemoveUnusedNamespacesButton;
	
	public static final String LineSplitter="\n";
	
	private ASLineStyleListener mASLineStyleListener;
	private MXMLLineStyleListener mMXMLLineStyleListener;
	private ASLineStyleListener mRearrangeLineStyleListener;
	private MXMLLineStyleListener mMXMLRearrangeLineStyleListener;
	
	//////////////////////////////////////////////////////////////////////////////////
	// Rearranging variables
//	private TabItem mRearrangeTab;
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
	
	private TabFolder mElementTypeTabFolder;
	
	private Map<String, Integer> mBlankLinesMap=new HashMap<String, Integer>();
	private Table mElementOrderTable;
	private Combo mElementBlankLinesCombo;
	private Text mSpanInfoText;
	private Button mUseElementOrder;
	private Button mElementUpButton;
	private Button mElementDownButton;
	private Button mElementPriorityUpButton;
	private Button mElementPriorityDownButton;
	private Button mElementDeleteButton;
	private Button mElementEditButton;
	private Button mElementSectionEditButton;
	private Button mElementSectionSpanEditButton;
	private Button mElementNewButton;
	
	private Table mFunctionOrderTable;
	private Button mFunctionUseSortOrder;
	private Button mFunctionSort;
	private Button mFunctionUpButton;
	private Button mFunctionDownButton;
	private Button mFunctionSectionEditButton;
	
	private Table mPropertyOrderTable;
	private Button mPropertyUseSortOrder;
	private Button mPropertySort;
	private Button mPropertyUpButton;
	private Button mPropertyDownButton;
	private Button mPropertySectionEditButton;
	private Button mPropertyGrabGettersButton;
	private Button mPropertyAssociateGettersButton;
	private int mPropertyHeadersStyle;
	private Button mPropertyGettersHeaderButton;
//	private Button mPropertyAlwaysHeaderButton;
	
	private Table mStaticFunctionOrderTable;
	private Button mStaticFunctionUseSortOrder;
	private Button mStaticFunctionSort;
	private Button mStaticFunctionUpButton;
	private Button mStaticFunctionDownButton;
	private Button mStaticFunctionSectionEditButton;
	
	private Table mStaticPropertyOrderTable;
	private Button mStaticPropertyUseSortOrder;
	private Button mStaticPropertySort;
	private Button mStaticPropertyUpButton;
	private Button mStaticPropertyDownButton;
	private Button mStaticPropertySectionEditButton;
	private Button mStaticPropertyGrabGettersButton;
	private Button mStaticPropertyAssociateGettersButton;
	private int mStaticPropertyHeadersStyle;
	private Button mStaticPropertyGettersHeaderButton;
//	private Button mStaticPropertyAlwaysHeaderButton;
	
	private Button mIncludeSort;
	
	private Button mMetatagUseSortOrder;
	private Table mMetatagOrderTable;
	private Button mMetatagUpButton;
	private Button mMetatagDownButton;
	private Button mMetatagNewButton;
	private Button mMetatagDeleteButton;
	private Button mMetatagEditButton;
	private Button mMetatagSort;
//	private Button mMetatagSectionEditButton;

	private Table mImportTable;
	private Button mImportMoveOut;
	private Button mImportSort;
	private Button mImportEnableOrdering;
	private Button mImportUpButton;
	private Button mImportDownButton;
	private Button mImportNewButton;
	private Button mImportNewSeparatorButton;
	private Button mImportEditButton;
	private Button mImportDeleteButton;
	
	private Button mNamespaceSort;
	///////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////
	//copyright header variables
	private Button mUseCopyrightHeader;
	private Button mRemoveExistingCopyright;
	private Combo mCopyrightSectionSize;
	private Combo mCopyrightSectionStyle;
	private Spinner mCopyrightSectionWidth;
	private Combo mCopyrightSectionFillChar;
	private Combo mCopyrightSectionPostLines;
	private Text mCopyrightText;
	private Text mSampleCopyrightHeader;
	/////////////////////////////
	
	///////////////////////////////////
	//section comment variables
	private Button mUseSectionComments;
	private Button mUseSectionCommentsInMXML;
	private Button mRemoveExistingSectionComments;
	private Text mSampleMajorSectionHeader;
	private Combo mMajorSectionSize;
	private Combo mMajorSectionStyle;
	private Spinner mMajorSectionWidth;
	private Combo mMajorSectionFillChar;
	private Combo mMajorSectionPreLines;
	private Text mSampleMinorSectionHeader;
	private Combo mMinorSectionSize;
	private Combo mMinorSectionStyle;
	private Spinner mMinorSectionWidth;
	private Combo mMinorSectionFillChar;
	private Combo mMinorSectionPreLines;

	public CommonPrefComposite(Composite parent, int style, boolean disableMode, IPreferenceStore store) {
		super(parent, style);
		
		mPrefStore=store;
		mDisableMode=disableMode;
		mASAdvancedOpenBraceButtons=new ArrayList<Button>();
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".FlexFormatter_Formatting_Options"); //$NON-NLS-1$
//		try
//		{
//			String version=(String)Activator.getDefault().getBundle().getHeaders().get("BUNDLE-VERSION");
////			setTitle(getTitle()+" ("+version+")--"+"Update site: http://flexformatter.googlecode.com/svn/trunk/FlexFormatter/FlexPrettyPrintCommandUpdateSite");
//		}
//		catch (Exception e)
//		{
//			Activator.logException(e, null);
//		}
		
		mVisualsInitialized=false;
		
		Composite mainComp=new Composite(this, SWT.None);
		GridLayout gl=new GridLayout();
		gl.marginHeight=0;
		gl.marginWidth=0;
		mainComp.setLayout(gl);
		mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		SelectionListener textUpdater=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				reformatText();
				enableWidgets();
			}
		};
		Composite labelComp;
		
		TabFolder tabFolder=new TabFolder(mainComp, SWT.None);
		GridData gd=new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gd);
		
		TabItem generalTab=new TabItem(tabFolder, SWT.None);
		generalTab.setText("General");
		
		TabItem asTab=new TabItem(tabFolder, SWT.None);
		asTab.setText("ActionScript");
		
		TabItem rearrangeTab=new TabItem(tabFolder, SWT.None);
		rearrangeTab.setText("AS Rearranging");
		
		TabItem mxmlTab=new TabItem(tabFolder, SWT.None);
		mxmlTab.setText("MXML");
		

		Composite mainGeneralComp=new Composite(tabFolder, SWT.None);
		mainGeneralComp.setLayout(new GridLayout());
		mainGeneralComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group indentGroup=new Group(mainGeneralComp, SWT.None);
		indentGroup.setLayout(new GridLayout());
		indentGroup.setText("Indentation");
		Composite radioComp=new Composite(indentGroup, SWT.NONE);
		radioComp.setLayout(new GridLayout(2, true));
		mUseTabsRadio=new Button(radioComp, SWT.RADIO);
		mUseTabsRadio.setText("**Use tabs");
		mUseTabsRadio.setToolTipText("If selected, tab characters will be used for leading whitespace.  This setting should match the FlexBuilder setting.");
		mUseSpacesRadio=new Button(radioComp, SWT.RADIO);
		mUseSpacesRadio.setText("**Use spaces");
		mUseSpacesRadio.setToolTipText("If selected, space characters will be used for leading whitespace  This setting should match the FlexBuilder setting.");
		mUseTabsRadio.addSelectionListener(textUpdater);
		mUseSpacesRadio.addSelectionListener(textUpdater);
		
		Composite helpLinkComp=new Composite(mainGeneralComp, SWT.None);
		helpLinkComp.setLayout(new GridLayout(2, false));
		helpLinkComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		Label l=new Label(helpLinkComp, SWT.None);
		l.setText("General preference info:");
		Text aText=new Text(helpLinkComp, SWT.SINGLE | SWT.READ_ONLY);
		aText.setText("http://sourceforge.net/apps/mediawiki/flexformatter/index.php?title=Preferences");
		l=new Label(helpLinkComp, SWT.None);
		l.setText("Links to other info:");
		aText=new Text(helpLinkComp, SWT.SINGLE | SWT.READ_ONLY);
		aText.setText("http://sourceforge.net/apps/mediawiki/flexformatter/index.php?title=Main_Page");		
//		Link helpLink=new Link(helpLinkComp, SWT.None);
//		helpLink.setText("<A>https://sourceforge.net/projects/flexformatter/</A>");
		
		generalTab.setControl(mainGeneralComp);
		
		Composite mainASComp=createActionscriptSettingsTab(tabFolder, textUpdater);
		
		Composite mainRearrangeComp=createRearrangingWidgetsTab(tabFolder);

		Composite mainMXMLComp=new Composite(tabFolder, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginHeight=0;
		mainMXMLComp.setLayout(gl);
		mainMXMLComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		TabFolder subTabFolder=new TabFolder(mainMXMLComp, SWT.None);
		gd=new GridData(GridData.FILL_VERTICAL);
		subTabFolder.setLayoutData(gd);
		{
			Composite settingsComp=new Composite(subTabFolder, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			settingsComp.setLayout(gl);
			settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			TabItem spacesTab=new TabItem(subTabFolder, SWT.None);
			spacesTab.setText("Main");
			
			spacesTab.setControl(settingsComp);
			
			Composite dualComp=new Composite(settingsComp, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			dualComp.setLayout(gl);

			labelComp=createLabelComp(dualComp, "Spaces around equals");
			mMXMLSpacesAroundEquals=new Spinner(labelComp, SWT.BORDER);
			mMXMLSpacesAroundEquals.setMinimum(0);
			mMXMLSpacesAroundEquals.setMaximum(10);
			mMXMLSpacesAroundEquals.addSelectionListener(textUpdater);
			mMXMLSpacesAroundEquals.setToolTipText("Number of spaces around attribute assignments in tags");
			
			labelComp=createLabelComp(dualComp, "Spaces before />");
			mMXMLSpacesBeforeEmptyTagEnd=new Spinner(labelComp, SWT.BORDER);
			mMXMLSpacesBeforeEmptyTagEnd.setMinimum(0);
			mMXMLSpacesBeforeEmptyTagEnd.setMaximum(10);
			mMXMLSpacesBeforeEmptyTagEnd.addSelectionListener(textUpdater);
			mMXMLSpacesBeforeEmptyTagEnd.setToolTipText("Number of spaces around before the end of an empty tag");
			
			Composite attrSpaceComp=new Composite(settingsComp, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			attrSpaceComp.setLayout(gl);
			
			mMXMLUseSpacesInsideBraces=new Button(attrSpaceComp, SWT.CHECK);
			mMXMLUseSpacesInsideBraces.setText("Set spaces inside { }");
			mMXMLUseSpacesInsideBraces.addSelectionListener(textUpdater);
			mMXMLUseSpacesInsideBraces.setToolTipText("If checked, control the number of spaces inside the braces of a binding expression in an mxml attribute.\nEnabling this option allows formatting of the binding text as well.");
			
			labelComp=createLabelComp(attrSpaceComp, "Spaces");
			mMXMLSpacesInsideBraces=new Spinner(labelComp, SWT.BORDER);
			mMXMLSpacesInsideBraces.setMinimum(0);
			mMXMLSpacesInsideBraces.setMaximum(10);
			mMXMLSpacesInsideBraces.addSelectionListener(textUpdater);
			mMXMLSpacesInsideBraces.setToolTipText("The number of spaces between the open and closing braces and the bound text.  Ex. \"{ x }\"");

			Composite formatComp=new Composite(settingsComp, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			gl.marginLeft=50;
			formatComp.setLayout(gl);
			
			mMXMLFormatBindingExpressions=new Button(formatComp, SWT.CHECK);
			mMXMLFormatBindingExpressions.setText("Format binding expressions in attributes");
			mMXMLFormatBindingExpressions.addSelectionListener(textUpdater);
			mMXMLFormatBindingExpressions.setToolTipText("If checked, attempt to format the text of the bound expression.  This is mainly for adding operator and paren spacing.");
			
			Composite indentComp=new Composite(settingsComp, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginHeight=0;
			gl.marginWidth=0;
			indentComp.setLayout(gl);
			
			mMXMLSpecialWrapTags=new Button(indentComp, SWT.CHECK);
			mMXMLSpecialWrapTags.setText("**Indent to first attr");
			mMXMLSpecialWrapTags.addSelectionListener(textUpdater);
			mMXMLSpecialWrapTags.setToolTipText("If true, indent wrapped lines to the column of the first attribute of that tag.  Otherwise, indent one tab's worth.");
			
			labelComp=createLabelComp(indentComp, "**Hanging indent tab stops");
			mMXMLHangingIndentSize=new Spinner(labelComp, SWT.BORDER);
			mMXMLHangingIndentSize.setMinimum(0);
			mMXMLHangingIndentSize.setMaximum(10);
			mMXMLHangingIndentSize.addSelectionListener(textUpdater);
			mMXMLHangingIndentSize.setToolTipText("Number of tab stops to use for hanging indents caused by wrapping.  This is used if not indenting to the first item.");
			
			Button xmlAttrSort=new Button(settingsComp, SWT.PUSH);
			xmlAttrSort.setText("Configure custom attribute order and line breaks...");
			xmlAttrSort.setToolTipText("Use this button to configure line breaks and ordering of specific tag attributes (if they exist)");
			
			xmlAttrSort.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					AttrOrderConfigDialog dlg=new AttrOrderConfigDialog(getShell(), mManualSortOrder, mSortExtraAttrs, mSortMode, mAddNewlineAfterLastAttr, mMXMLAttrGroups, mIndentTagClose);
					if (dlg.open()==Dialog.OK)
					{
						mSortMode=dlg.getSortMode();
						mSortExtraAttrs=dlg.isSortExtraAttrs();
						mManualSortOrder=dlg.getManualSortOrder();
						mMXMLAttrGroups=dlg.getAttrGroups();
						mAddNewlineAfterLastAttr=dlg.isAddNewlineAfterLastAttr();
						mIndentTagClose=dlg.isIndentTagClose();
						reformatText();
						enableWidgets();
					}
				}
			});
			
			Group wrapGroup=new Group(settingsComp, SWT.None);
			wrapGroup.setText("Default attribute wrap options");
			wrapGroup.setLayout(new GridLayout());
			wrapGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			mWrapNoneButton=new Button(wrapGroup, SWT.RADIO);
			mWrapNoneButton.setText("No auto wrapping");
			mWrapNoneButton.addSelectionListener(textUpdater);
			mWrapNoneButton.setToolTipText("Each tag will be placed on a single line");
			
			mWrapLineLengthButton=new Button(wrapGroup, SWT.RADIO);
			mWrapLineLengthButton.setText("Wrap to max line length");
			mWrapLineLengthButton.addSelectionListener(textUpdater);
			mWrapLineLengthButton.setToolTipText("Wrap each tag based on the line length specified");
			
			mWrapItemsPerLineButton=new Button(wrapGroup, SWT.RADIO);
			mWrapItemsPerLineButton.setText("Wrap after each n attributes");
			mWrapItemsPerLineButton.addSelectionListener(textUpdater);
			mWrapItemsPerLineButton.setToolTipText("Wrap each tag after the specified number of attributes.  There will always be at least one attribute on the first line");

			Composite wrapOptionsComp=new Composite(wrapGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			wrapOptionsComp.setLayout(gl);
			
			labelComp=createLabelComp(wrapOptionsComp, "Max line length");
			mMaxLineLengthSpinner=new Spinner(labelComp, SWT.BORDER);
			mMaxLineLengthSpinner.setMinimum(50);
			mMaxLineLengthSpinner.setMaximum(1000);
			mMaxLineLengthSpinner.addSelectionListener(textUpdater);
			mMaxLineLengthSpinner.setToolTipText("This column size is a hint, so it's not appropriate for printing");

			labelComp=createLabelComp(wrapOptionsComp, "Attrs per line");
			mAttrsPerLineSpinner=new Spinner(labelComp, SWT.BORDER);
			mAttrsPerLineSpinner.setMinimum(1);
			mAttrsPerLineSpinner.setMaximum(20);
			mAttrsPerLineSpinner.addSelectionListener(textUpdater);
			mAttrsPerLineSpinner.setToolTipText("Number of attributes to keep on each line of an open tag");
			
			Composite limitWrappingComp=new Composite(wrapGroup, SWT.None);
			gl=new GridLayout(4, false);
			gl.marginHeight=0;
			limitWrappingComp.setLayout(gl);
			mMXMLUseAttrsToKeepOnSameLineButton=new Button(limitWrappingComp, SWT.CHECK);
			mMXMLUseAttrsToKeepOnSameLineButton.addSelectionListener(textUpdater);
			Label sameLineLabel=new Label(limitWrappingComp, SWT.None);
			sameLineLabel.setText("Keep on 1 line if attribute count <=");
			mMXMLAttrsToKeepOnSameLineSpinner=new Spinner(limitWrappingComp, SWT.BORDER);
			mMXMLAttrsToKeepOnSameLineSpinner.setMinimum(1);
			mMXMLAttrsToKeepOnSameLineSpinner.setMaximum(20);
			mMXMLAttrsToKeepOnSameLineSpinner.addSelectionListener(textUpdater);
			mMXMLAttrsToKeepOnSameLineSpinner.setToolTipText("If this number of attributes or fewer exist in a tag, then don't perform any line wrapping of tag.  Just include all the attributes on a single line.\nThis overrides all other wrap settings.");
			
			mMXMLObeyMaxLength=new Button(limitWrappingComp, SWT.CHECK);
			mMXMLObeyMaxLength.setText("Obey max");
			mMXMLObeyMaxLength.setToolTipText("Obey the maximum length anyway.  This is an override to the override of the default wrapping options.");
			mMXMLObeyMaxLength.addSelectionListener(textUpdater);
			
			{
				Group blankLinesComp=new Group(settingsComp, SWT.None);
				gl=new GridLayout();
				gl.marginHeight=0;
				blankLinesComp.setLayout(gl);
				blankLinesComp.setText("Blank lines");
				blankLinesComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				mMXMLKeepBlankLines=new Button(blankLinesComp, SWT.CHECK);
				mMXMLKeepBlankLines.setText("Don't delete existing blank lines between tags");
				mMXMLKeepBlankLines.addSelectionListener(textUpdater);
				mMXMLKeepBlankLines.setToolTipText("Attempt to preserve blank lines that exist in the text.  This only applies to blank lines between tags and in text content.  It does not apply to lines within an open tag or ActionScript content");
				
				labelComp=createLabelComp(blankLinesComp, "Blank lines before comment tags:");
				mMXMLBlankLinesBeforeComments=new Spinner(labelComp, SWT.BORDER);
				mMXMLBlankLinesBeforeComments.setMinimum(0);
				mMXMLBlankLinesBeforeComments.setMaximum(10);
				mMXMLBlankLinesBeforeComments.addSelectionListener(textUpdater);
				mMXMLBlankLinesBeforeComments.setToolTipText("Add number of blank lines before comment tags.");
				
				labelComp=createLabelComp(blankLinesComp, "Blank lines between sibling tags:");
				mMXMLBlankLinesBetweenSiblingTagsSpinner=new Spinner(labelComp, SWT.BORDER);
				mMXMLBlankLinesBetweenSiblingTagsSpinner.setMinimum(0);
				mMXMLBlankLinesBetweenSiblingTagsSpinner.setMaximum(10);
				mMXMLBlankLinesBetweenSiblingTagsSpinner.addSelectionListener(textUpdater);
				mMXMLBlankLinesBetweenSiblingTagsSpinner.setToolTipText("Add number of blank lines between sibling tags.  This groups blocks together visually.");
				
				Composite defaultBlankLinesBeforeTagsComp=new Composite(blankLinesComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				gl.marginWidth=0;
				defaultBlankLinesBeforeTagsComp.setLayout(gl);
				defaultBlankLinesBeforeTagsComp.setLayoutData(new GridData(GridData.FILL_BOTH));

				labelComp=createLabelComp(defaultBlankLinesBeforeTagsComp, "Blank lines after parent tags:");
				mMXMLBlankLinesAfterParentTagsSpinner=new Spinner(labelComp, SWT.BORDER);
				mMXMLBlankLinesAfterParentTagsSpinner.setMinimum(0);
				mMXMLBlankLinesAfterParentTagsSpinner.setMaximum(10);
				mMXMLBlankLinesAfterParentTagsSpinner.addSelectionListener(textUpdater);
				mMXMLBlankLinesAfterParentTagsSpinner.setToolTipText("Add this number of blank lines after parent tags.");
				
				labelComp=createLabelComp(defaultBlankLinesBeforeTagsComp, "...before close tags:");
				mMXMLBlankLinesBeforeCloseTagsSpinner=new Spinner(labelComp, SWT.BORDER);
				mMXMLBlankLinesBeforeCloseTagsSpinner.setMinimum(0);
				mMXMLBlankLinesBeforeCloseTagsSpinner.setMaximum(10);
				mMXMLBlankLinesBeforeCloseTagsSpinner.addSelectionListener(textUpdater);
				mMXMLBlankLinesBeforeCloseTagsSpinner.setToolTipText("Add this number of blank lines before closing tags (with at least one contained tag).");
				
				Composite blankLinesBeforeTagsComp=new Composite(blankLinesComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				gl.marginWidth=0;
				blankLinesBeforeTagsComp.setLayout(gl);
				blankLinesBeforeTagsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				labelComp=createLabelComp(blankLinesBeforeTagsComp, "Add lines before:");
				mMXMLBlankLinesBeforeTagsSpinner=new Spinner(labelComp, SWT.BORDER);
				mMXMLBlankLinesBeforeTagsSpinner.setMinimum(0);
				mMXMLBlankLinesBeforeTagsSpinner.setMaximum(10);
				mMXMLBlankLinesBeforeTagsSpinner.addSelectionListener(textUpdater);
				mMXMLBlankLinesBeforeTagsSpinner.setToolTipText("Add number of blank lines before tags listed in table");
				
				Composite tableComp=new Composite(blankLinesBeforeTagsComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				gl.marginWidth=0;
				tableComp.setLayout(gl);
				tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				mTagsWithLeadingBlankLinesTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
				gd=new GridData(GridData.FILL_BOTH);
				gd.heightHint=30;
				gd.widthHint=50;
				mTagsWithLeadingBlankLinesTable.setLayoutData(gd);
				mTagsWithLeadingBlankLinesTable.addSelectionListener(textUpdater);
				mTagsWithLeadingBlankLinesTable.setToolTipText("Each item in the table will have the number of blank lines added before it that are listed in the spin box.");
				
				Composite buttonComp=new Composite(tableComp, SWT.None);
				buttonComp.setLayout(new GridLayout());
				mTagLeadingLinesAddButton=new Button(buttonComp, SWT.PUSH);
				mTagLeadingLinesAddButton.setText("Add...");
				mTagLeadingLinesAddButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						InputDialog dlg=new InputDialog(getShell(), "Lines before tags", "Enter a tag name that should have blank lines before it.\nUse Java regex syntax to match multiple tag names.\nExamples: mx:List OR .* OR .*:List", null, new IInputValidator()
						{
							public String isValid(String newText)
							{
								String text=newText.trim();
								boolean inUse=false;
								TableItem[] items=mTagsWithLeadingBlankLinesTable.getItems();
								for (TableItem tableItem : items) {
									if (text.equals(tableItem.getText()))
									{
										inUse=true;
										break;
									}
								}
								
								if (inUse)
									return "Tag already exists in the list box";
								
								if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
									return "Whitespace in middle of string";
								
								String error=validateRegex(text);
								if (error!=null)
									return error;
								
								return null;
							}
							
						});
						if (dlg.open()==Dialog.OK)
						{
							String newTag=dlg.getValue();
							TableItem newItem=new TableItem(mTagsWithLeadingBlankLinesTable, SWT.None);
							newItem.setText(newTag);
							reformatText();
							enableWidgets();
						}
					}
				});
				mTagLeadingLinesRemoveButton=new Button(buttonComp, SWT.PUSH);
				mTagLeadingLinesRemoveButton.setText("Remove");
				mTagLeadingLinesRemoveButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						int selIndex=mTagsWithLeadingBlankLinesTable.getSelectionIndex();
						if (selIndex>=0)
						{
							mTagsWithLeadingBlankLinesTable.remove(selIndex);
							if (mTagsWithLeadingBlankLinesTable.getItemCount()>0)
								mTagsWithLeadingBlankLinesTable.setSelection(Math.min(selIndex, mTagsWithLeadingBlankLinesTable.getItemCount()-1));
							reformatText();
							enableWidgets();
						}
					}
				});
				
				labelComp=createLabelComp(blankLinesBeforeTagsComp, "Lines after parent:");
				mMXMLBlankLinesAfterSpecificTagsSpinner=new Spinner(labelComp, SWT.BORDER);
				mMXMLBlankLinesAfterSpecificTagsSpinner.setMinimum(0);
				mMXMLBlankLinesAfterSpecificTagsSpinner.setMaximum(10);
				mMXMLBlankLinesAfterSpecificTagsSpinner.addSelectionListener(textUpdater);
				mMXMLBlankLinesAfterSpecificTagsSpinner.setToolTipText("Add number of blank lines after parent open tags listed in table.  So this is blank lines inside the tag.");
				
				tableComp=new Composite(blankLinesBeforeTagsComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				tableComp.setLayout(gl);
				gd=new GridData(GridData.FILL_BOTH);
				gd.widthHint=50;
				tableComp.setLayoutData(gd);
				
				mTagsWithTrailingBlankLinesTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
				gd=new GridData(GridData.FILL_BOTH);
				gd.heightHint=30;
				gd.widthHint=50;
				mTagsWithTrailingBlankLinesTable.setLayoutData(gd);
				mTagsWithTrailingBlankLinesTable.addSelectionListener(textUpdater);
				mTagsWithTrailingBlankLinesTable.setToolTipText("Each item in the table will have the number of blank lines added after it that are listed in the spin box.");
				
				buttonComp=new Composite(tableComp, SWT.None);
				buttonComp.setLayout(new GridLayout());
				mTagTrailingLinesAddButton=new Button(buttonComp, SWT.PUSH);
				mTagTrailingLinesAddButton.setText("Add...");
				mTagTrailingLinesAddButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						InputDialog dlg=new InputDialog(getShell(), "Lines after open tags", "Enter a tag name that should have blank lines before its children.\nUse Java regex syntax to match multiple tag names.\nExamples: mx:List OR .* OR .*:List", null, new IInputValidator()
						{
							public String isValid(String newText)
							{
								String text=newText.trim();
								boolean inUse=false;
								TableItem[] items=mTagsWithTrailingBlankLinesTable.getItems();
								for (TableItem tableItem : items) {
									if (text.equals(tableItem.getText()))
									{
										inUse=true;
										break;
									}
								}
								
								if (inUse)
									return "Tag already exists in the list box";
								
								if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
									return "Whitespace in middle of string";
								String error=validateRegex(text);
								if (error!=null)
									return error;
								
								return null;
							}

						});
						if (dlg.open()==Dialog.OK)
						{
							String newTag=dlg.getValue();
							TableItem newItem=new TableItem(mTagsWithTrailingBlankLinesTable, SWT.None);
							newItem.setText(newTag);
							reformatText();
							enableWidgets();
						}
					}
				});
				mTagTrailingLinesRemoveButton=new Button(buttonComp, SWT.PUSH);
				mTagTrailingLinesRemoveButton.setText("Remove");
				mTagTrailingLinesRemoveButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						int selIndex=mTagsWithTrailingBlankLinesTable.getSelectionIndex();
						if (selIndex>=0)
						{
							mTagsWithTrailingBlankLinesTable.remove(selIndex);
							if (mTagsWithTrailingBlankLinesTable.getItemCount()>0)
								mTagsWithTrailingBlankLinesTable.setSelection(Math.min(selIndex, mTagsWithTrailingBlankLinesTable.getItemCount()-1));
							reformatText();
							enableWidgets();
						}
					}
				});
				
			}
		}
		
		{
			Composite settingsComp=new Composite(subTabFolder, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			settingsComp.setLayout(gl);
			settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			TabItem tagsTab=new TabItem(subTabFolder, SWT.None);
			tagsTab.setText("Special tags");
			
			tagsTab.setControl(settingsComp);
			
			Group textContentGroup=new Group(settingsComp, SWT.None);
			textContentGroup.setText("Text content within tags");
			textContentGroup.setLayout(new GridLayout(2, true));
			textContentGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Composite tagsToFormatComp=new Composite(textContentGroup, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			tagsToFormatComp.setLayout(gl);
			tagsToFormatComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			l=new Label(tagsToFormatComp, SWT.None);
			l.setText("Tags to never indent");
			
			Composite tableComp=new Composite(tagsToFormatComp, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginHeight=0;
			tableComp.setLayout(gl);
			tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mNeverFormatTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
			mNeverFormatTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			mNeverFormatTable.addSelectionListener(textUpdater);
			mNeverFormatTable.setToolTipText("These are tags where whitespace is meaningful within the text content.  The default algorithm doesn't modify the whitespace in text content unless it's all whitespace.");
			
			Composite buttonComp=new Composite(tableComp, SWT.None);
			buttonComp.setLayout(new GridLayout());
			mNeverFormatAddButton=new Button(buttonComp, SWT.PUSH);
			mNeverFormatAddButton.setText("Add...");
			mNeverFormatAddButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dlg=new InputDialog(getShell(), "Never indent text", "Enter a tag name that should never have its text content whitespace modified", null, new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							if (tagExists(text))
								return "Tag already exists in either the 'Always indent' or 'Never indent' list box";
							
							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of string";
							
							return null;
						}
						
					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						TableItem newItem=new TableItem(mNeverFormatTable, SWT.None);
						newItem.setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});
			mNeverFormatRemoveButton=new Button(buttonComp, SWT.PUSH);
			mNeverFormatRemoveButton.setText("Remove");
			mNeverFormatRemoveButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					int selIndex=mNeverFormatTable.getSelectionIndex();
					if (selIndex>=0)
					{
						mNeverFormatTable.remove(selIndex);
						if (mNeverFormatTable.getItemCount()>0)
							mNeverFormatTable.setSelection(Math.min(selIndex, mNeverFormatTable.getItemCount()-1));
						reformatText();
						enableWidgets();
					}
				}
			});

			Composite tagsToNotFormatComp=new Composite(textContentGroup, SWT.None);
			tagsToNotFormatComp.setLayout(new GridLayout());
			tagsToNotFormatComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			l=new Label(tagsToNotFormatComp, SWT.None);
			l.setText("Tags to always indent");

			tableComp=new Composite(tagsToNotFormatComp, SWT.None);
			tableComp.setLayout(new GridLayout(2, false));
			tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mAlwaysFormatTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
			mAlwaysFormatTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			mAlwaysFormatTable.addSelectionListener(textUpdater);
			mAlwaysFormatTable.setToolTipText("These are tags where whitespace is never meaningful within the text content.  The default algorithm doesn't modify the whitespace in text content unless it's all whitespace.");
			
			buttonComp=new Composite(tableComp, SWT.None);
			buttonComp.setLayout(new GridLayout());
			mAlwaysFormatAddButton=new Button(buttonComp, SWT.PUSH);
			mAlwaysFormatAddButton.setText("Add...");
			mAlwaysFormatAddButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dlg=new InputDialog(getShell(), "Always indent text", "Enter a tag name that can always have its text content whitespace modified", null, new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							if (tagExists(text))
								return "Tag already exists in either the 'Always indent' or 'Never indent' list box";
							
							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of string";
							
							return null;
						}
						
					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						TableItem newItem=new TableItem(mAlwaysFormatTable, SWT.None);
						newItem.setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});
			mAlwaysFormatRemoveButton=new Button(buttonComp, SWT.PUSH);
			mAlwaysFormatRemoveButton.setText("Remove");
			mAlwaysFormatRemoveButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					int selIndex=mAlwaysFormatTable.getSelectionIndex();
					if (selIndex>=0)
					{
						mAlwaysFormatTable.remove(selIndex);
						if (mAlwaysFormatTable.getItemCount()>0)
							mAlwaysFormatTable.setSelection(Math.min(selIndex, mAlwaysFormatTable.getItemCount()-1));
						reformatText();
						enableWidgets();
					}
				}
			});
			
			Button restoreDefaultTagsButton=new Button(textContentGroup, SWT.PUSH);
			restoreDefaultTagsButton.setText("Restore defaults to both tables");
			restoreDefaultTagsButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					IPreferenceStore store=Activator.getDefault().getPreferenceStore();
					updateTagTables(store.getDefaultString(Initializer.Pref_MXML_TagsCannotFormat), store.getDefaultString(Initializer.Pref_MXML_TagsCanFormat));
					enableWidgets();
					reformatText();
				}
			});
			
			Composite subTagComp=new Composite(settingsComp, SWT.None);
			subTagComp.setLayout(new GridLayout(3, false));
			subTagComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mUseExcludeSubTags=new Button(subTagComp, SWT.CHECK);
			mUseExcludeSubTags.setText("Don't format subtags of:");
			mUseExcludeSubTags.addSelectionListener(textUpdater);
			
			mExcludeSubTagsTable=new Table(subTagComp, SWT.BORDER);
			mExcludeSubTagsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			mExcludeSubTagsTable.addSelectionListener(textUpdater);
			
			Composite subButtonComp=new Composite(subTagComp, SWT.None);
			subButtonComp.setLayout(new GridLayout());
			subButtonComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			mExcludeAddButton=new Button(subButtonComp, SWT.PUSH);
			mExcludeAddButton.setText("Add...");
			mExcludeAddButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dlg=new InputDialog(getShell(), "Don't format subtags", "Enter a tag name that should not have any of its contained content modified\nUse Java regex syntax to match multiple tag names.\nExamples: mx:List OR .* OR .*:List", null, new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							boolean inUse=false;
							TableItem[] items=mExcludeSubTagsTable.getItems();
							for (TableItem tableItem : items) {
								if (text.equals(tableItem.getText()))
								{
									inUse=true;
									break;
								}
							}
							
							if (inUse)
								return "Tag already exists in the list box";
							
							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of string";
							
							String error=validateRegex(text);
							if (error!=null)
								return error;
							
							return null;
						}
						
					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						TableItem newItem=new TableItem(mExcludeSubTagsTable, SWT.None);
						newItem.setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});

			mExcludeEditButton=new Button(subButtonComp, SWT.PUSH);
			mExcludeEditButton.setText("Edit...");
			mExcludeEditButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					final TableItem[] selItems=mExcludeSubTagsTable.getSelection();
					InputDialog dlg=new InputDialog(getShell(), "Actionscript Tag", "Enter a tag name whose text content should be formatted as ActionScript code (Java style regex allowed)", selItems[0].getText(), new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							
							TableItem[] items=mExcludeSubTagsTable.getItems();
							for (TableItem tableItem : items) {
								if (tableItem.getText().equals(text) && !selItems[0].getText().equals(text))
									return "Tag already exists in table";
							}
							
							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of tag name";
							
							return null;
						}
						
					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						selItems[0].setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});
			
			mExcludeRemoveButton=new Button(subButtonComp, SWT.PUSH);
			mExcludeRemoveButton.setText("Remove");
			mExcludeRemoveButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					int selIndex=mExcludeSubTagsTable.getSelectionIndex();
					if (selIndex>=0)
					{
						mExcludeSubTagsTable.remove(selIndex);
						if (mExcludeSubTagsTable.getItemCount()>0)
							mExcludeSubTagsTable.setSelection(Math.min(selIndex, mExcludeSubTagsTable.getItemCount()-1));
						reformatText();
						enableWidgets();
					}
				}
			});
			
			
			Group asTags=new Group(settingsComp, SWT.None);
			asTags.setText("Actionscript tags");
			asTags.setLayout(new GridLayout());
			asTags.setLayoutData(new GridData(GridData.FILL_BOTH));
			mMXMLRequireCDataButton=new Button(asTags, SWT.CHECK);
			mMXMLRequireCDataButton.setText("Only format ActionScript within CDATA markers");
			mMXMLRequireCDataButton.setToolTipText("If checked, only format contents as ActionScript if there is a CDATA tag.  Otherwise, attempt formatting regardless.");
			
			l=new Label(asTags, SWT.None);
			l.setText("Tags whose contents should be formatted as ActionScript");
			
			tableComp=new Composite(asTags, SWT.None);
			tableComp.setLayout(new GridLayout(2, false));
			tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mMXMLTagsContainingActionScriptTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
			gd=new GridData(GridData.FILL_BOTH);
			gd.heightHint=50;
			mMXMLTagsContainingActionScriptTable.setLayoutData(gd);
			mMXMLTagsContainingActionScriptTable.addSelectionListener(textUpdater);
			mMXMLTagsContainingActionScriptTable.setToolTipText("This table lists tags whose text content should be formatted as ActionScript code.  Normally Script and event tags.");
			
			buttonComp=new Composite(tableComp, SWT.None);
			buttonComp.setLayout(new GridLayout());
//			buttonComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mMXMLASTagsAddButton=new Button(buttonComp, SWT.PUSH);
			mMXMLASTagsAddButton.setText("Add...");
			mMXMLASTagsAddButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dlg=new InputDialog(getShell(), "Actionscript Tag", "Enter a tag name whose text content should be formatted as ActionScript code (Java style regex allowed)", null, new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							
							TableItem[] items=mMXMLTagsContainingActionScriptTable.getItems();
							for (TableItem tableItem : items) {
								if (tableItem.getText().equals(text))
									return "Tag already exists in table";
							}
							
							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of tag name";
							
							String error=validateRegex(text);
							if (error!=null)
								return error;
							
							return null;
						}
						
					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						TableItem newItem=new TableItem(mMXMLTagsContainingActionScriptTable, SWT.None);
						newItem.setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});
			
			mMXMLASTagsEditButton=new Button(buttonComp, SWT.PUSH);
			mMXMLASTagsEditButton.setText("Edit...");
			mMXMLASTagsEditButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					final TableItem[] selItems=mMXMLTagsContainingActionScriptTable.getSelection();
					InputDialog dlg=new InputDialog(getShell(), "Actionscript Tag", "Enter a tag name whose text content should be formatted as ActionScript code (Java style regex allowed)", selItems[0].getText(), new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							
							TableItem[] items=mMXMLTagsContainingActionScriptTable.getItems();
							for (TableItem tableItem : items) {
								if (tableItem.getText().equals(text) && !selItems[0].getText().equals(text))
									return "Tag already exists in table";
							}
							
							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of tag name";
							
							return null;
						}
						
					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						selItems[0].setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});
			
			mMXMLASTagsDeleteButton=new Button(buttonComp, SWT.PUSH);
			mMXMLASTagsDeleteButton.setText("Remove");
			mMXMLASTagsDeleteButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					int selIndex=mMXMLTagsContainingActionScriptTable.getSelectionIndex();
					if (selIndex>=0)
					{
						mMXMLTagsContainingActionScriptTable.remove(selIndex);
						if (mMXMLTagsContainingActionScriptTable.getItemCount()>0)
							mMXMLTagsContainingActionScriptTable.setSelection(Math.min(selIndex, mMXMLTagsContainingActionScriptTable.getItemCount()-1));
						reformatText();
						enableWidgets();
					}
				}
			});

			Button restoreDefaultASTagsButton=new Button(asTags, SWT.PUSH);
			restoreDefaultASTagsButton.setText("Restore default AS tags");
			restoreDefaultASTagsButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					IPreferenceStore store=Activator.getDefault().getPreferenceStore();
					updateTable(mMXMLTagsContainingActionScriptTable, store.getDefaultString(Initializer.Pref_MXML_TagsWithASContent));
					enableWidgets();
					reformatText();
				}
			});
			
		}
		
		{
			Composite settingsComp=new Composite(subTabFolder, SWT.NONE);
			settingsComp.setLayout(new GridLayout());
			settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			TabItem tagsTab=new TabItem(subTabFolder, SWT.None);
			tagsTab.setText("Tweaks");
			
			tagsTab.setControl(settingsComp);
			
			mMXMLKeepRelativeIndentInsideMultilineComments=new Button(settingsComp, SWT.CHECK);
			mMXMLKeepRelativeIndentInsideMultilineComments.setText("Keep relative indent inside multi-line comments");
			mMXMLKeepRelativeIndentInsideMultilineComments.addSelectionListener(textUpdater);
			mMXMLKeepRelativeIndentInsideMultilineComments.setToolTipText("If checked, keep the relative indent of lines 2 to n of multi-line comments with respect to line 1.");
			
			Group scriptGroup=new Group(settingsComp, SWT.None);
			scriptGroup.setText("Script blocks");
			scriptGroup.setLayout(new GridLayout());
			
			
			mMXMLKeepScriptCDATAOnSameLine=new Button(scriptGroup, SWT.CHECK);
			mMXMLKeepScriptCDATAOnSameLine.setText("Keep CDATA on line with open Script tag");
			mMXMLKeepScriptCDATAOnSameLine.addSelectionListener(textUpdater);
			mMXMLKeepScriptCDATAOnSameLine.setToolTipText("If checked, keep the CDATA tag associated with an mx:Script tag on the same line with the script open tag.  Same for close tag.\nIf not checked, the tab indent below applies.");
			
			labelComp=createLabelComp(scriptGroup, "Number of tabs to indent script CDATA:");
			mMXMLTabsBeforeCDATASpinner=new Spinner(labelComp, SWT.BORDER);
			mMXMLTabsBeforeCDATASpinner.setMinimum(0);
			mMXMLTabsBeforeCDATASpinner.setMaximum(10);
			mMXMLTabsBeforeCDATASpinner.addSelectionListener(textUpdater);
			mMXMLTabsBeforeCDATASpinner.setToolTipText("Number of tabs to add before the CDATA tag inside an mxml script tag.");
			
			labelComp=createLabelComp(scriptGroup, "Number of tabs to indent script code:");
			mMXMLTabsBeforeScriptCodeSpinner=new Spinner(labelComp, SWT.BORDER);
			mMXMLTabsBeforeScriptCodeSpinner.setMinimum(0);
			mMXMLTabsBeforeScriptCodeSpinner.setMaximum(10);
			mMXMLTabsBeforeScriptCodeSpinner.addSelectionListener(textUpdater);
			mMXMLTabsBeforeScriptCodeSpinner.setToolTipText("Number of tabs to add before the Actionscript code inside an mxml script tag.");
			
			labelComp=createLabelComp(scriptGroup, "Number of blank lines inside CDATA block:");
			mMXMLBlankLinesInsideCDATASpinner=new Spinner(labelComp, SWT.BORDER);
			mMXMLBlankLinesInsideCDATASpinner.setMinimum(0);
			mMXMLBlankLinesInsideCDATASpinner.setMaximum(10);
			mMXMLBlankLinesInsideCDATASpinner.addSelectionListener(textUpdater);
			mMXMLBlankLinesInsideCDATASpinner.setToolTipText("Number of blank lines after the CDATA tag and before the end tag.  The original line count is not maintained.");
			
			mMXMLRemoveUnusedNamespacesButton=new Button(settingsComp, SWT.CHECK);
			mMXMLRemoveUnusedNamespacesButton.setText("Remove unused namespaces (EXPERIMENTAL)");
			mMXMLRemoveUnusedNamespacesButton.addSelectionListener(textUpdater);
			mMXMLRemoveUnusedNamespacesButton.setToolTipText("If checked, remove any unused namespaces when the entire files is formatted.");
		}		
		
		{
			createMXMLRearrangingTab(tabFolder);
//			Composite settingsComp=new Composite(subTabFolder, SWT.NONE);
//			settingsComp.setLayout(new GridLayout());
//			settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
//			
//			TabItem mxmlReTab=new TabItem(subTabFolder, SWT.None);
//			mxmlReTab.setText("MXML Rearranging");
//			
//			mxmlReTab.setControl(settingsComp);
//			
//			mMXMLRearrangeWhileFormatting=new Button(settingsComp, SWT.CHECK);
//			mMXMLRearrangeWhileFormatting.setText("Rearrange while formatting");
//			mMXMLRearrangeWhileFormatting.setToolTipText("");
//			mMXMLRearrangeWhileFormatting.addSelectionListener(textUpdater);
//			
//			l=new Label(settingsComp, SWT.None);
//			l.setText("Order of top-level MXML tags");
//			
//			Composite tableComp=new Composite(settingsComp, SWT.None);
//			tableComp.setLayout(new GridLayout(2, false));
//			tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
//			
//			mMXMLOrderTable=new Table(tableComp, SWT.BORDER);
//			gd=new GridData(GridData.FILL_BOTH);
//			gd.heightHint=mMXMLOrderTable.getItemHeight()*10;
//			mMXMLOrderTable.setLayoutData(gd);
//			mMXMLOrderTable.addSelectionListener(textUpdater);
//			mMXMLOrderTable.setToolTipText("");
//			
//			Composite buttonComp=new Composite(tableComp, SWT.None);
//			buttonComp.setLayout(new GridLayout());
//			buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
//			
//			mMXMLOrderAddButton=new Button(buttonComp, SWT.PUSH);
//			mMXMLOrderAddButton.setText("Add...");
//			mMXMLOrderAddButton.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent e)
//				{
//					InputDialog dlg=new InputDialog(getShell(), "Top level tag", "Enter a tag name to use as a top-level tag.\nUse Java regex syntax to match multiple tag names.\nExamples: mx:List OR .* OR .*:List", null, new IInputValidator()
//					{
//						public String isValid(String newText)
//						{
//							String text=newText.trim();
//							boolean inUse=false;
//							TableItem[] items=mMXMLOrderTable.getItems();
//							for (TableItem tableItem : items) {
//								if (text.equals(tableItem.getText()))
//								{
//									inUse=true;
//									break;
//								}
//							}
//
//							if (inUse)
//								return "Tag already exists in the list box";
//
//							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
//								return "Whitespace in middle of tag name";
//
//							String error=validateRegex(text);
//							if (error!=null)
//								return error;
//
//							return null;
//						}
//
//					});
//					if (dlg.open()==Dialog.OK)
//					{
//						String newTag=dlg.getValue();
//						TableItem newItem=new TableItem(mMXMLOrderTable, SWT.None);
//						newItem.setText(newTag);
//						reformatText();
//						enableWidgets();
//					}
//				}
//			});
//			
//			mMXMLOrderDeleteButton=new Button(buttonComp, SWT.PUSH);
//			mMXMLOrderDeleteButton.setText("Delete");
//			mMXMLOrderDeleteButton.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent e)
//				{
//					TableItem[] selItems=mMXMLOrderTable.getSelection();
//					int selIndex=mMXMLOrderTable.getSelectionIndex();
//					if (selItems.length>0)
//					{
//						selItems[0].dispose();
//						updateRearrangeText();
//						if (mMXMLOrderTable.getItemCount()>0)
//						{
//							mMXMLOrderTable.setSelection(Math.min(mMXMLOrderTable.getItemCount()-1, selIndex));
//						}
//					}
//				}
//			});
////			
////			mMXMLOrderEditButton=new Button(buttonComp, SWT.PUSH);
////			mMXMLOrderEditButton.setText("Edit...");
//
//			mMXMLOrderUpButton=new Button(buttonComp, SWT.PUSH);
//			mMXMLOrderUpButton.setText("Move up");
//			mMXMLOrderUpButton.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					moveUpHelper(mMXMLOrderTable);
//				}
//			});
//			
//			mMXMLOrderDownButton=new Button(buttonComp, SWT.PUSH);
//			mMXMLOrderDownButton.setText("Move down");
//			mMXMLOrderDownButton.addSelectionListener(new SelectionAdapter()
//			{
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					moveDownHelper(mMXMLOrderTable);
//				}
//			});
//			
			
		}		

		Composite editorComp=new Composite(mainMXMLComp, SWT.None);
		editorComp.setLayout(new GridLayout());
		editorComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Button allowUserMXMLText=new Button(editorComp, SWT.CHECK);
		allowUserMXMLText.setText("Allow custom text");
		allowUserMXMLText.setToolTipText("If checked, you can edit the text and it will be reformatted on the next settings change.\nNOTE: you may get errors if you introduce syntax errors.");
		allowUserMXMLText.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				mSampleMXMLText.setEditable(allowUserMXMLText.getSelection());
			}
		});
		
		mSampleMXMLText=createCodeArea(editorComp);
		mMXMLLineStyleListener=new MXMLLineStyleListener(mSampleMXMLText);
		mSampleMXMLText.addLineStyleListener(mMXMLLineStyleListener);
		mOriginalMXMLSampleText=loadSampleText("flexprettyprint/preferences/sample.mxml");
		mSampleMXMLText.setText(mOriginalMXMLSampleText);
		
		Composite exportComp=new Composite(mainComp, SWT.None);
		gl=new GridLayout(7, false);
		gl.marginHeight=0;
		exportComp.setLayout(gl);
		
		final String propertiesExtension=".properties";
		Button importButton=new Button(exportComp, SWT.PUSH);
		importButton.setText("Import...");
		importButton.setToolTipText("Import formatter settings from an external properties file");
		importButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dlg=new FileDialog(getShell());
				dlg.setText("Choose Profile");
				dlg.setFilterExtensions(new String[]{"*"+propertiesExtension});
				String filePath=dlg.open();
				if (filePath!=null)
				{
					Properties props=new Properties();
					try
					{
						InputStream stream=new FileInputStream(new File(filePath));
						props.load(stream);
						stream.close();
						
//						migrateProperties(props);
						
						setPrefsFromProperties(props);
						MessageDialog.openInformation(getShell(), "Settings imported", "Settings successfully imported");
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		
		Button exportButton=new Button(exportComp, SWT.PUSH);
		exportButton.setText("Export...");
		exportButton.setToolTipText("Export the current formatter settings to a properties file");
		exportButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dlg=new FileDialog(getShell());
				dlg.setText("Choose Export Location");
				dlg.setFilterExtensions(new String[]{"*"+propertiesExtension});
				String filePath=dlg.open();
				if (filePath!=null)
				{
					try
					{
						Properties props=new Properties();
						setProperties(props);
						if (!filePath.endsWith(propertiesExtension))
							filePath+=propertiesExtension;
						OutputStream os=new FileOutputStream(new File(filePath));
						props.store(os, "FlexPrettyPrintSettings");
						os.close();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		
		Button importButton2=new Button(exportComp, SWT.PUSH);
		importButton2.setText("Import(2)...");
		importButton2.setToolTipText("Import formatter settings from a string");
		importButton2.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String results=showFormatterSettings("Paste the new settings into the text area", null);
				if (results!=null)
				{
					try
					{
						Properties props=new Properties();
						props.load(new ByteArrayInputStream(results.getBytes()));
//						migrateProperties(props);
						setPrefsFromProperties(props);
						MessageDialog.openInformation(getShell(), "Settings imported", "Settings successfully imported");
					} catch (IOException e1) {
						Activator.logException(e1, "Error reading settings from string");
					}
				}
			}
		});
		
		Button exportButton2=new Button(exportComp, SWT.PUSH);
		exportButton2.setText("Export(2)...");
		exportButton2.setToolTipText("Export formatter settings to a string");
		exportButton2.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				Properties props=new Properties();
				setProperties(props);
				OutputStream os=new ByteArrayOutputStream();
				try
				{
					props.store(os, "FlexFormatter settings");
					//show the string to user in a dialog with a text field
					showFormatterSettings("Copy the settings from the text area", os.toString());
				} catch (IOException e1) {
					Activator.logException(e1, "Error writing settings to string");
				}
			}
		});
		
		Button setToAdobe=new Button(exportComp, SWT.PUSH);
		setToAdobe.setText("Set to Adobe standards");
		setToAdobe.setToolTipText("Change settings to the Adobe standards where possible.  Other settings are not modified.");
		setToAdobe.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mASUseBraceStyle.setSelection(true);
				mASBraceStyle.select(0); //should be "adobe"
				mSpacesBeforeComma.setSelection(0);
				mSpacesAfterComma.setSelection(1);
				mSpacesAroundAssignment.setSelection(1);
				mASSpacesAroundColons.setSelection(0);
				mASUseGlobalSpacesAroundColons.setSelection(true);
				mASUseGlobalSpacesInsideParens.setSelection(false);
				mASSpacesInsideParens.setSelection(0);
				mASAdvancedSpacesInsideObjectLiteralBraces.setSelection(1);
				mASAdvancedSpacesInsideArrayRefBrackets.setSelection(0);
				mASAdvancedSpacesInsideArrayDeclBrackets.setSelection(1);
				mASUseSpacesAroundAssignmentInParameters.setSelection(false);
				mASUseSpacesAroundAssignmentInMetatags.setSelection(false);
				mASNewlineBeforeBindableFunction.setSelection(true);
				mASNewlineBeforeBindableProperty.setSelection(true);
				mMXMLSpacesAroundEquals.setSelection(0);
				mMXMLSpecialWrapTags.setSelection(false);
				mMXMLHangingIndentSize.setSelection(1);
				
				mMaxLineLengthSpinner.setSelection(80);
				mBlankLinesBeforeFunctions.setSelection(1);
				mASBlankLinesBeforeProperties.setSelection(1);
				mASSpacesBeforeOpenControlParen.setSelection(1);
				mASAlwaysGenerateIndent.setSelection(false);
				
				mUseSpacesRadio.setSelection(true);
				mUseTabsRadio.setSelection(false);
				
				mASSpecialWrapCommaItems.setSelection(true);
				mASBreakLinesBeforeComma.setSelection(false);
				mASBreakLinesBeforeArithmeticOperator.setSelection(false);
				mASBreakLinesBeforeLogicalOperator.setSelection(false);
				mASBreakLinesBeforeAssignment.setSelection(false);
				mASMaxLineLength.setSelection(80);
				
				mMajorSectionFillChar.setText("-");
				mMinorSectionFillChar.setText("-");
				mMajorSectionWidth.setSelection(80);
				mMinorSectionWidth.setSelection(40);
				mMajorSectionStyle.select(0);
				mMinorSectionStyle.select(0);
				mMajorSectionSize.select(1);
				mMinorSectionSize.select(0);
				mMajorSectionPreLines.select(1);
				mMinorSectionPreLines.select(1);
				mCopyrightSectionPostLines.select(1);

				mMXMLUseAttrsToKeepOnSameLineButton.setSelection(true);
				mMXMLAttrsToKeepOnSameLineSpinner.setSelection(1);
				mSortMode=MXMLPrettyPrinter.MXML_ATTR_ORDERING_USEDATA;
				mSortExtraAttrs=false;
				mWrapItemsPerLineButton.setSelection(true);
				mWrapLineLengthButton.setSelection(false);
				mWrapNoneButton.setSelection(false);
				mAddNewlineAfterLastAttr=false;
				mIndentTagClose=true;
				mManualSortOrder=new ArrayList<String>();
				mManualSortOrder.add("\\n,%namespace%");
				mManualSortOrder.add("%identification%");
				mManualSortOrder.add("\\n,%layout-attributes%");
				mManualSortOrder.add("\\n,%layout-constraints%");
				mManualSortOrder.add("\\n,%styles%");
				mManualSortOrder.add("\\n,%effects%");
				mManualSortOrder.add("\\n,%properties%");
				mManualSortOrder.add("\\n,%events%");
				List<String> attrs=new ArrayList<String>();
				attrs.add("x");
				attrs.add("y");
				attrs.add("width");
				attrs.add("height");
				AttrGroup group=new AttrGroup("layout-attributes", attrs, MXMLPrettyPrinter.MXML_Sort_GroupOrder, MXMLPrettyPrinter.MXML_ATTR_WRAP_NONE, true);
				mMXMLAttrGroups.add(group);
				attrs=new ArrayList<String>();
				attrs.add("id");
				attrs.add("name");
				attrs.add("automationName");
				group=new AttrGroup("identification", attrs, MXMLPrettyPrinter.MXML_Sort_GroupOrder, MXMLPrettyPrinter.MXML_ATTR_WRAP_NONE, true);
				mMXMLAttrGroups.add(group);
				attrs=new ArrayList<String>();
				attrs.add("left");
				attrs.add("right");
				attrs.add("top");
				attrs.add("bottom");
				group=new AttrGroup("layout-constraints", attrs, MXMLPrettyPrinter.MXML_Sort_GroupOrder, MXMLPrettyPrinter.MXML_ATTR_WRAP_NONE, true);
				mMXMLAttrGroups.add(group);
				attrs=new ArrayList<String>();
				attrs.add("xmlns");
				attrs.add("xmlns:.*");
				group=new AttrGroup("xmlns", attrs, MXMLPrettyPrinter.MXML_Sort_None, MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE, true);
				mMXMLAttrGroups.add(group);
				
				updateRearrangeText();
				reformatText();
				enableWidgets();
			}
		});
		
		Label indentLabel=new Label(exportComp, SWT.None);
		indentLabel.setText("Items preceded by ** affect the behavior of 'indent'");
		
		asTab.setControl(mainASComp);
		rearrangeTab.setControl(mainRearrangeComp);
		mxmlTab.setControl(mainMXMLComp);
		mVisualsInitialized=true;
		
		updateWidgets(mPrefStore);
	}

	protected void setPrefsFromProperties(Properties props)
	{
		//migrate to supply missing properties, if any
		CommandLine.migrateProperties(props);
		
		//grab current properties
		Properties existingProps=new Properties();
		setProperties(existingProps);
		
		//put new properties in pref store
		IPreferenceStore store=new PreferenceStore();
		for (Map.Entry<Object, Object> entry : props.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			store.setValue(key, value);
		}
		
		//now, add in properties that weren't included in the passed-in Properties object
		for (Map.Entry<Object, Object> entry : existingProps.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			if (!props.containsKey(key))
				store.setValue(key, value);
		}
		
		updateWidgets(store);
	}

	protected String showFormatterSettings(String title, String settingsData)
	{
		DataDialog d=new DataDialog(getShell(), title, settingsData);
		if (d.open()==Dialog.OK)
		{
			String result=d.getData();
			return result;
		}
		
		return null;
	}

	protected void performDefaults()
	{
		IPreferenceStore store=Activator.getDefault().getPreferenceStore();
		boolean useTabs=store.getDefaultBoolean(Initializer.Pref_Flex_UseTabs);
		mUseTabsRadio.setSelection(useTabs);
		mUseSpacesRadio.setSelection(!useTabs);
//		mIndentSizeSpinner.setSelection(store.getDefaultInt(Initializer.Pref_Flex_IndentSize));
//		mTabSizeSpinner.setSelection(store.getDefaultInt(Initializer.Pref_Flex_TabSize));
		
		mSpacesBeforeComma.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesBeforeComma));
		mSpacesAfterComma.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesAfterComma));
		mBlankLinesBeforeFunctions.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesBeforeFunctions));
		mASBlankLinesBeforeClass.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesBeforeClasses));
		mASBlankLinesBeforeControlStatement.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesBeforeControlStatements));
		mASBlankLinesBeforeImports.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesBeforeImportBlock));
		mASBlankLinesBeforeProperties.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesBeforeProperties));
		mASBlankLinesAtFunctionStart.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesAtFunctionStart));
		mASBlankLinesAtFunctionEnd.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesAtFunctionEnd));
		mASSpacesInsideParens.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesInsideParens));
		mASUseGlobalSpacesInsideParens.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseGlobalSpacesInsideParens));
		mASAdvancedSpacesInsideArrayDeclBrackets.setSelection(store.getDefaultInt(Initializer.Pref_AS_AdvancedSpacesInsideArrayDeclBrackets));
		mASAdvancedSpacesInsideArrayRefBrackets.setSelection(store.getDefaultInt(Initializer.Pref_AS_AdvancedSpacesInsideArrayRefBrackets));
		mASAdvancedSpacesInsideObjectLiteralBraces.setSelection(store.getDefaultInt(Initializer.Pref_AS_AdvancedSpacesInsideLiteralBraces));
		mASAdvancedSpacesInsideParensInOtherPlaces.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInOtherPlaces) );
		mASAdvancedSpacesInsideParensInParameterLists.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInParameterLists) );
		mASAdvancedSpacesInsideParensInArgumentLists.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInArgumentLists) );
		
		mASCollapseAdjacentParens.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_CollapseSpacesForAdjacentParens));
		mOpenBraceOnNewLine.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_OpenBraceOnNewLine));
		mASCRBeforeCatch.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_CatchOnNewLine));
		mASUseBraceStyle.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseBraceStyle));
		mASUseGlobalOpenBraceOnNewLine.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseGlobalCRBeforeBrace));
		int braceItems=store.getDefaultInt(Initializer.Pref_AS_AdvancedCRBeforeBraceSettings);
		for (Button braceButton : mASAdvancedOpenBraceButtons) {
			Integer braceCode=(Integer)braceButton.getData();
			boolean selected=(braceCode!=null && (braceCode.intValue() & braceItems)!=0);
			braceButton.setSelection(selected);
		}
		
		mASBraceStyle.select(getComboIndexFromBraceStyle(store.getDefaultInt(Initializer.Pref_AS_BraceStyle)));
		mASCRBeforeElse.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_ElseOnNewLine));
		mASCRBeforeWhile.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_WhileOnNewLine));
		mASNoCRBeforeBreak.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NoNewCRsBeforeBreak));
		mASNoCRBeforeContinue.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NoNewCRsBeforeContinue));
		mASNoCRBeforeReturn.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NoNewCRsBeforeReturn));
		mASNoCRBeforeThrow.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NoNewCRsBeforeThrow));
		mASNoCRBeforeExpression.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NoNewCRsBeforeExpression));
		mKeepElseIfOnSameLine.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_ElseIfOnSameLine));
//		mASTrimTrailingWS.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_TrimTrailingWhitespace));
		mASSpacesAfterLabelColon.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesAfterLabel));
		mKeepBlankLines.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_KeepBlankLines));
		mASBlankLinesToKeep.setSelection(store.getDefaultInt(Initializer.Pref_AS_BlankLinesToKeep));
		mASMaxLineLength.setSelection(store.getDefaultInt(Initializer.Pref_AS_MaxLineLength));
		mSpacesAroundAssignment.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesAroundAssignment));
		mASSpacesAroundAssignmentInParameters.setSelection(store.getDefaultInt(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInOptionalParameters));
		mASSpacesAroundAssignmentInMetatags.setSelection(store.getDefaultInt(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInMetatags));
		mASUseSpacesAroundAssignmentInParameters.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInOptionalParameters));
		mASUseSpacesAroundAssignmentInMetatags.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInMetatags));
		mSpacesAroundSymbolicOperator.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesAroundSymbolicOperator));
		mASSpacesAroundColons.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesAroundColons));
		mASAdvancedSpacesAfterColonsInDeclarations.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesAfterColonsInDeclarations ) );
		mASAdvancedSpacesBeforeColonsInDeclarations.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInDeclarations ) );
		mASAdvancedSpacesAfterColonsInFunctions.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes ) );
		mASAdvancedSpacesBeforeColonsInFunctions.setSelection( store.getDefaultInt( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes ) );
		mASUseGlobalSpacesAroundColons.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseGlobalSpacesAroundColons));
		mKeepSLCommentsOnColumn1.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_KeepSLCommentsOnColumn1));
		mASAlwaysGenerateIndent.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AlwaysGenerateIndent));
		mASSpacesBeforeOpenControlParen.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesBeforeOpenControlParen));
		mASSpacesBeforeDeclParameters.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesBeforeFormalParameters));
		mASSpacesBeforeArguments.setSelection(store.getDefaultInt(Initializer.Pref_AS_SpacesBeforeArguments));
		mASNewlineBeforeBindableFunction.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NewlineBeforeBindableFunction));
		mASNewlineBeforeBindableProperty.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_NewlineBeforeBindableProperty));
		mASEmptyStatementsOnNewLine.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_PutEmptyStatementsOnNewLine));
		mASDontIndentPackageElements.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_DontIndentPackageItems));
		mASDontIndentSwitchCases.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_DontIndentSwitchCases));
		mASNoIndentForExpressionTerminatorButton.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UnindentExpressionTerminators));
		mASLeaveExtraWhitespaceAroundVarDecls.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_LeaveExtraWhitespaceAroundVarDecls));
		mASAlignDeclEquals.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AlignDeclEquals));
		mASAlignDeclEqualsConsecutive.setSelection(store.getDefaultInt(Initializer.Pref_AS_AlignDeclMode)!=ASPrettyPrinter.Decl_Align_Scope);
		mASAlignDeclEqualsScope.setSelection(store.getDefaultInt(Initializer.Pref_AS_AlignDeclMode)==ASPrettyPrinter.Decl_Align_Scope);
		mASKeepSingleLineFunctions.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_LeaveSingleLineFunctions));
//		mASEnsureConditionalBraces.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_EnsureConditionalsHaveBraces));
		mASEnsureConditionalBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_AddIfMissing)!=0);
		mASSmartAddConditionalBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_AddSmart)!=0);
		mASSmartAddRemoveConditionalBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_AddRemoveSmart)!=0);
		mASNoModifyConditionalBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_NoModify)!=0);
//		mASEnsureLoopBraces.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_EnsureLoopsHaveBraces));
		mASEnsureLoopBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_AddIfMissing)!=0);
		mASSmartAddLoopBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_AddSmart)!=0);
		mASSmartAddRemoveLoopBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_AddRemoveSmart)!=0);
		mASNoModifyLoopBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_NoModify)!=0);
//		mASEnsureSwitchBraces.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_EnsureSwitchCasesHaveBraces));
		mASSmartAddSwitchBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToCases) & ASPrettyPrinter.Braces_AddSmart)!=0);
		mASRemoveSwitchBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToCases) & ASPrettyPrinter.Braces_RemoveUnnecessary)!=0);
		mASNoModifySwitchBraces.setSelection((store.getDefaultInt(Initializer.Pref_AS_AddBracesToCases) & ASPrettyPrinter.Braces_NoModify)!=0);
		mASUseGnuBraceIndent.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseGnuBraceIndent));

		mASKeepSpacesBeforeLineComments.setSelection(false);
		mASAlignCommentsAtColumn.setSelection(false);
		mASAddOneSpaceBeforeLineComments.setSelection(false);
		if (store.getDefaultBoolean(Initializer.Pref_AS_KeepSpacesBeforeLineComments))
		{
			mASKeepSpacesBeforeLineComments.setSelection(true);
		}
		else if (store.getDefaultInt(Initializer.Pref_AS_AlignLineCommentsAtColumn)>0)
		{
			mASAlignCommentsAtColumn.setSelection(true);
			mASAlignCommentsColumn.setSelection(store.getDefaultInt(Initializer.Pref_AS_AlignLineCommentsAtColumn));
		}
		else
		{
			mASAddOneSpaceBeforeLineComments.setSelection(true);
		}
//		mASKeepSpacesBeforeLineComments.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_KeepSpacesBeforeLineComments));

		mASWrapLineComments.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseLineCommentWrapping));
		mASWrapASDocComments.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseDocCommentWrapping));
		mASWrapMLComments.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseMLCommentWrapping));
		mASWrapMLCommentsReflow.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_MLCommentReflow));
		mASWrapDocCommentsReflow.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_DocCommentReflow));
		mASWrapMLCommentsKeepBlankLines.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_MLCommentKeepBlankLines));
		mASWrapMLCommentsSeparateHeader.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_MLCommentHeaderOnSeparateLine));
		mASWrapMLCommentsAsteriskMode.select(getComboIndexFromCommentAsteriskMode(store.getDefaultInt(Initializer.Pref_AS_MLCommentAsteriskMode)));
		mASWrapDocCommentsHangingTabs.setSelection(store.getDefaultInt(Initializer.Pref_AS_DocCommentHangingIndentTabs));
		mASWrapDocCommentsKeepBlankLines.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_DocCommentKeepBlankLines));

		mASKeepRelativeIndentOfMultilineComments.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_KeepRelativeIndentInDocComments));
		mASRearrangeDuringFormatting.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_RearrangeAsPartOfFormat));
		mASBreakLinesBeforeComma.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_BreakLinesBeforeComma));
		mASBreakLinesBeforeArithmeticOperator.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_BreakLinesBeforeArithmetic));
		mASBreakLinesBeforeLogicalOperator.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_BreakLinesBeforeLogical));
		mASBreakLinesBeforeAssignment.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_BreakLinesBeforeAssignment));
		mASHangingIndentSize.setSelection(store.getDefaultInt(Initializer.Pref_AS_TabsInHangingIndent));
		mASArrayDeclWrapCombo.select(store.getDefaultInt(Initializer.Pref_AS_WrapArrayDeclMode));
		mASMethodCallWrapCombo.select(store.getDefaultInt(Initializer.Pref_AS_WrapMethodCallMode));
		mASMethodDeclWrapCombo.select(store.getDefaultInt(Initializer.Pref_AS_WrapMethodDeclMode));
		mASExpressionWrapCombo.select(store.getDefaultInt(Initializer.Pref_AS_WrapExpressionMode));
		mASXMLWrapCombo.select(store.getDefaultInt(Initializer.Pref_AS_WrapXMLMode));
		mASSpecialWrapCommaItems.setSelection(store.getDefaultInt(Initializer.Pref_AS_WrapIndentStyle)==WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT);
		
		mASUseAdvancedWrapping.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_UseAdvancedWrapping));
		mASWrappingBreakOnPhrases.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingPreservePhrases));
		mASWrappingEnforceMax.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingEnforceMax));
		mASWrappingAllArgs.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingAllArgs));
		mASWrappingAllParms.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingAllParms));
		mASWrappingFirstArg.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingFirstArg));
		mASWrappingFirstParm.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingFirstParm));
		mASWrappingAlignArrayItems.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingAlignArrayItems));
		mASWrappingAlignObjectItems.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingAlignObjectItems));
		mASWrappingFirstArrayItem.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingFirstArrayItem));
		mASWrappingFirstObjectItem.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingFirstObjectItem));
		mASWrappingAllArrayItems.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingAllArrayItems));
		mASWrappingAllObjectItems.setSelection(store.getDefaultBoolean(Initializer.Pref_AS_AdvancedWrappingAllObjectItems));
		mASWrappingPostMaxLeniency.setSelection(store.getDefaultInt(Initializer.Pref_AS_AdvancedWrappingGraceColumns));
		updateWrappingTable(store.getDefaultInt(Initializer.Pref_AS_AdvancedWrappingElements));
		
		mMXMLRearrangeWhileFormatting.setSelection(store.getDefaultBoolean(PreferenceConstants.MXMLRearr_RearrangeWhileFormatting));
		mUseMXMLTagOrdering.setSelection(store.getDefaultBoolean(PreferenceConstants.MXMLRearr_UseRearrangeTagOrdering));
//		mMXMLBlockIndent.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlockIndent));
		mUseExcludeSubTags.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_UseTagsDoNotFormatInside));
		mMXMLSpacesAroundEquals.setSelection(store.getDefaultInt(Initializer.Pref_MXML_SpacesAroundEquals));
		mMXMLSpacesBeforeEmptyTagEnd.setSelection(store.getDefaultInt(Initializer.Pref_MXML_SpacesBeforeEmptyTagEnd));
		mMXMLUseSpacesInsideBraces.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_UseSpacesInsideAttributeBraces));
		mMXMLSpacesInsideBraces.setSelection(store.getDefaultInt(Initializer.Pref_MXML_SpacesInsideAttributeBraces));
		mMXMLFormatBindingExpressions.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_UseFormattingOfBoundAttributes));
		mMXMLKeepBlankLines.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_KeepBlankLines));
		mMXMLKeepRelativeIndentInsideMultilineComments.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_KeepRelativeIndentInMultilineComments));
		mMXMLTabsBeforeCDATASpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_ScriptCDataIndentTabs));
		mMXMLBlankLinesInsideCDATASpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesAtCDataStart));
		mMXMLTabsBeforeScriptCodeSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_ScriptIndentTabs));
		mMXMLKeepScriptCDATAOnSameLine.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_KeepScriptCDataOnSameLine));
		mMXMLRemoveUnusedNamespacesButton.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_RemoveNamespacesAsPartOfFormat));
		mMXMLBlankLinesBeforeComments.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesBeforeComments));
		mMXMLSpecialWrapTags.setSelection(store.getDefaultInt(Initializer.Pref_MXML_WrapIndentStyle)==WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT);
		mMXMLHangingIndentSize.setSelection(store.getDefaultInt(Initializer.Pref_MXML_TabsInHangingIndent));
		mMXMLRequireCDataButton.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_RequireCDATAForASFormatting));
		mSortMode=store.getDefaultInt(Initializer.Pref_MXML_SortAttrMode);
		mSortExtraAttrs=store.getDefaultBoolean(Initializer.Pref_MXML_SortExtraAttrs);
		mAddNewlineAfterLastAttr=store.getDefaultBoolean(Initializer.Pref_MXML_AddNewlineAfterLastAttr);
		mIndentTagClose=store.getDefaultBoolean(Initializer.Pref_MXML_IndentTagClose);
		String manualSortStrings=store.getDefaultString(Initializer.Pref_MXML_SortAttrData);
		mManualSortOrder.clear();
		mManualSortOrder.addAll(Arrays.asList(manualSortStrings.split("\n")));
		for (int i=mManualSortOrder.size()-1; i>=0; i--)
		{
			if (mManualSortOrder.get(i).trim().length()==0)
				mManualSortOrder.remove(i);
		}
		//NOTE: I'm not going to kill the defined groups here, because I think that would be counterintuitive
		mMaxLineLengthSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_MaxLineLength));
		int wrapType=store.getDefaultInt(Initializer.Pref_MXML_AttrWrapMode);
		switch (wrapType)
		{
		case MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE:
			mWrapItemsPerLineButton.setSelection(true);
			break;
		case MXMLPrettyPrinter.MXML_ATTR_WRAP_LINE_LENGTH:
			mWrapLineLengthButton.setSelection(true);
			break;
		default:
			mWrapNoneButton.setSelection(true);
			break;
		}
		mAttrsPerLineSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_AttrsPerLine));
		mMXMLUseAttrsToKeepOnSameLineButton.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_UseAttrsToKeepOnSameLine));
		mMXMLAttrsToKeepOnSameLineSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_AttrsToKeepOnSameLine));
		mMXMLObeyMaxLength.setSelection(store.getDefaultBoolean(Initializer.Pref_MXML_AlwaysUseMaxLineLength));
		mMXMLBlankLinesBeforeTagsSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesBeforeTags));
		mMXMLBlankLinesAfterSpecificTagsSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesAfterSpecificParentTags));
		mMXMLBlankLinesBetweenSiblingTagsSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesBetweenSiblingTags));
		mMXMLBlankLinesAfterParentTagsSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesAfterParentTags));
		mMXMLBlankLinesBeforeCloseTagsSpinner.setSelection(store.getDefaultInt(Initializer.Pref_MXML_BlankLinesBeforeClosingTags));

		//TODO: should I update these with defaults?
		updateTagTables(store.getDefaultString(Initializer.Pref_MXML_TagsCannotFormat), store.getDefaultString(Initializer.Pref_MXML_TagsCanFormat));
		updateTable(mTagsWithLeadingBlankLinesTable, store.getDefaultString(Initializer.Pref_MXML_TagsWithBlankLinesBefore));
		updateTable(mTagsWithTrailingBlankLinesTable, store.getDefaultString(Initializer.Pref_MXML_ParentTagsWithBlankLinesAfter));
		updateTable(mMXMLTagsContainingActionScriptTable, store.getDefaultString(Initializer.Pref_MXML_TagsWithASContent));
		updateTable(mExcludeSubTagsTable, store.getDefaultString(Initializer.Pref_MXML_TagsDoNotFormatInside));
		updateTable(mSingleLineMetaTagsTable, store.getDefaultString(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetProperty));
		addItemsToSimpleOrderingTable(store.getDefaultString(PreferenceConstants.MXMLRearr_RearrangeTagOrdering), mMXMLOrderTable);
		
		/////////////////////////////////////////////////////////////////////////
		//rearrange widgets
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
		
		//element ordering stuff
		String orderData=store.getDefaultString(PreferenceConstants.ASRearr_ElementOrder);
		String[] items=orderData.split(PreferenceConstants.AS_Pref_Line_Separator);
		updateElementOrderTable(items);
		
		mFunctionSort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortFunctions));
		mFunctionUseSortOrder.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseFunctionVisibilityOrder));
		mImportEnableOrdering.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseImportOrder));
		mImportMoveOut.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_MoveImportsOutsideClass));
		mImportSort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortImports));
		mIncludeSort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortIncludes));
		mMetatagSort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortMetatags));
		mNamespaceSort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortNamespaces));
		mPropertySort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortProperties));
		mPropertyUseSortOrder.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UsePropertyVisibilityOrder));
		mPropertyGrabGettersButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties));
		mPropertyAssociateGettersButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithProperties));
		mPropertyHeadersStyle=store.getDefaultInt(PreferenceConstants.ASRearr_AddDefaultHeaderForProperties);
//		mPropertyGettersHeaderButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedProperties));
//		mPropertyAlwaysHeaderButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAllProperties));
		mStaticFunctionSort.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortStaticFunctions));
		mStaticPropertyUseSortOrder.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortStaticProperties));
		mStaticPropertyGrabGettersButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties));
		mStaticPropertyAssociateGettersButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithStaticProperties));
		mStaticPropertyHeadersStyle=store.getDefaultInt(PreferenceConstants.ASRearr_AddDefaultHeaderForStaticProperties);
//		mStaticPropertyGettersHeaderButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedStaticProperties));
//		mStaticPropertyAlwaysHeaderButton.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAllStaticProperties));
		mUseElementOrder.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseElementOrder));
		mMetatagUseSortOrder.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseMetatagOrder));
		String blankLinesData=store.getDefaultString(PreferenceConstants.ASRearr_BlankLinesBeforeElement);
		ASRearranger.updateBlankLinesMap(mBlankLinesMap, blankLinesData);
		
		addItemsToOrderingTable(store.getDefaultString(PreferenceConstants.ASRearr_FunctionVisibilityOrder), mFunctionOrderTable);
		addItemsToOrderingTable(store.getDefaultString(PreferenceConstants.ASRearr_PropertyVisibilityOrder), mPropertyOrderTable);
		addItemsToOrderingTable(store.getDefaultString(PreferenceConstants.ASRearr_StaticFunctionVisibilityOrder), mStaticFunctionOrderTable);
		addItemsToOrderingTable(store.getDefaultString(PreferenceConstants.ASRearr_StaticPropertyVisibilityOrder), mStaticPropertyOrderTable);
		addItemsToSimpleOrderingTable(store.getDefaultString(PreferenceConstants.ASRearr_ImportOrder), mImportTable);
		addItemsToSimpleOrderingTable(store.getDefaultString(PreferenceConstants.ASRearr_MetatagOrder), mMetatagOrderTable);
		
		SectionHeader header=SectionHeader.load(store.getDefaultString(PreferenceConstants.ASRearr_MajorSectionHeader));
		mMajorSectionSize.select(mMajorSectionSize.indexOf(Integer.toString(header.getExtraInternalLines())));
		mMajorSectionStyle.select(findDataIndex(mMajorSectionStyle, header.getStyle()));
		mMajorSectionWidth.setSelection(header.getWidth());
		mMajorSectionFillChar.setText(header.getFillChar());
		mMajorSectionPreLines.select(header.getLinesBefore());
		
		header=SectionHeader.load(store.getDefaultString(PreferenceConstants.ASRearr_MinorSectionHeader));
		mMinorSectionSize.select(mMinorSectionSize.indexOf(Integer.toString(header.getExtraInternalLines())));
		mMinorSectionStyle.select(findDataIndex(mMinorSectionStyle, header.getStyle()));
		mMinorSectionWidth.setSelection(header.getWidth());
		mMinorSectionFillChar.setText(header.getFillChar());
		mMinorSectionPreLines.select(header.getLinesBefore());
		
		header=SectionHeader.load(store.getDefaultString(PreferenceConstants.ASRearr_CopyrightHeader));
		mCopyrightSectionSize.select(mCopyrightSectionSize.indexOf(Integer.toString(header.getExtraInternalLines())));
		mCopyrightSectionStyle.select(findDataIndex(mCopyrightSectionStyle, header.getStyle()));
		mCopyrightSectionWidth.setSelection(header.getWidth());
		mCopyrightSectionFillChar.setText(header.getFillChar());
		mCopyrightSectionPostLines.select(header.getLinesBefore());
		
		mUseCopyrightHeader.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseCopyright));
		mRemoveExistingCopyright.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_RemoveExistingCopyrightHeaders));
		
		mUseSectionComments.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseSectionHeaders));
		mUseSectionCommentsInMXML.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_UseSectionHeadersInMXML));
		mRemoveExistingSectionComments.setSelection(store.getDefaultBoolean(PreferenceConstants.ASRearr_RemoveAllExistingHeaders));
		mHeaderSpecs=ASRearranger.readHeaderSpecs(store.getDefaultString(PreferenceConstants.ASRearr_SectionHeaders));
		updatePrintStringsInOrderTable(mElementOrderTable);
		updatePrintStringsInOrderTable(mFunctionOrderTable);
		updatePrintStringsInOrderTable(mStaticFunctionOrderTable);
		updatePrintStringsInOrderTable(mPropertyOrderTable);
		updatePrintStringsInOrderTable(mStaticPropertyOrderTable);
		/////////////////////////////////////////////////////////////////////////
		
		enableWidgets();
		reformatText();
		updateRearrangeText();
	}

	private Composite createActionscriptSettingsTab(TabFolder tabFolder, final SelectionListener textUpdater)
	{
		final Composite mainASComp=new Composite(tabFolder, SWT.NONE);
		GridLayout gl=new GridLayout(2, false);
		gl.marginHeight=0;
		mainASComp.setLayout(gl);
		mainASComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabFolder subTabFolder=new TabFolder(mainASComp, SWT.None);
		GridData gd=new GridData(GridData.FILL_VERTICAL);
		subTabFolder.setLayoutData(gd);
//		subTabFolder.addSelectionListener(new SelectionAdapter()
//		{
//			@Override
//			public void widgetSelected(SelectionEvent e)
//			{
//				if (e.item==mRearrangeTab)
//					updateRearrangeText();
//				else
//					reformatText();
//			}
//		});
		
		
		{
			Composite thisTab=new Composite(subTabFolder, SWT.None);
			thisTab.setLayout(new GridLayout());
			thisTab.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			ScrolledComposite scrollComp=new ScrolledComposite(thisTab, SWT.V_SCROLL);
			gl=new GridLayout();
			gl.marginWidth=0;
			gl.marginHeight=0;
			scrollComp.setLayout(gl);
			gd=new GridData(GridData.FILL_BOTH);
			scrollComp.setLayoutData(gd);
			scrollComp.setExpandHorizontal(true);
			scrollComp.setExpandVertical(true);

			Composite blankLinesComp=new Composite(scrollComp, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			blankLinesComp.setLayout(gl);
			
			scrollComp.setContent(blankLinesComp);
			scrollComp.setAlwaysShowScrollBars(true);

			TabItem blanksTab=new TabItem(subTabFolder, SWT.None);
			blanksTab.setText("Blank lines/spaces");
			
			blanksTab.setControl(thisTab);
			
			Composite settingsComp=new Composite(blankLinesComp, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			settingsComp.setLayout(gl);

			mSpacesGroup=new Group(settingsComp, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			mSpacesGroup.setLayout(gl);
			mSpacesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			mSpacesGroup.setText("Spaces");
			
			Composite labelComp;
			
			createAdvancedGroup(mSpacesGroup, new IAdvancedSettingsLayout()
			{
				public void addAdvancedItems(Composite parent)
				{
					Composite labelComp = createLabelComp( parent, "Spaces inside ( in parameter lists" );
					mASAdvancedSpacesInsideParensInParameterLists = new Spinner(labelComp, SWT.BORDER );
					mASAdvancedSpacesInsideParensInParameterLists.setMinimum( 0 );
					mASAdvancedSpacesInsideParensInParameterLists.setMaximum( 10 );
					mASAdvancedSpacesInsideParensInParameterLists.addSelectionListener( textUpdater );
					mASAdvancedSpacesInsideParensInParameterLists.setToolTipText( "Number of spaces inside opening and closing '(' in parameter declarations (methods, catch clauses)" );
					
					labelComp = createLabelComp( parent, "Spaces inside ( in argument lists" );
					mASAdvancedSpacesInsideParensInArgumentLists = new Spinner(labelComp, SWT.BORDER );
					mASAdvancedSpacesInsideParensInArgumentLists.setMinimum( 0 );
					mASAdvancedSpacesInsideParensInArgumentLists.setMaximum( 10 );
					mASAdvancedSpacesInsideParensInArgumentLists.addSelectionListener( textUpdater );
					mASAdvancedSpacesInsideParensInArgumentLists.setToolTipText( "Number of spaces inside opening and closing '(' in method calls and binding decl arguments" );
					
					labelComp = createLabelComp( parent, "Spaces inside ( elsewhere" );
					mASAdvancedSpacesInsideParensInOtherPlaces = new Spinner(labelComp, SWT.BORDER );
					mASAdvancedSpacesInsideParensInOtherPlaces.setMinimum( 0 );
					mASAdvancedSpacesInsideParensInOtherPlaces.setMaximum( 10 );
					mASAdvancedSpacesInsideParensInOtherPlaces.addSelectionListener( textUpdater );
					mASAdvancedSpacesInsideParensInOtherPlaces.setToolTipText( "Number of spaces inside opening and closing '(' for expressions and control statements (like 'if' and 'for')" );

					labelComp=createLabelComp(parent, "Spaces inside { in object literals");
					mASAdvancedSpacesInsideObjectLiteralBraces=new Spinner(labelComp, SWT.BORDER);
					mASAdvancedSpacesInsideObjectLiteralBraces.setMinimum(0);
					mASAdvancedSpacesInsideObjectLiteralBraces.setMaximum(10);
					mASAdvancedSpacesInsideObjectLiteralBraces.addSelectionListener(textUpdater);
					mASAdvancedSpacesInsideObjectLiteralBraces.setToolTipText("Number of spaces inside opening and closing '{' (inside object arrays}.");
					
					labelComp=createLabelComp(parent, "Spaces inside '[' in array literals");
					mASAdvancedSpacesInsideArrayDeclBrackets=new Spinner(labelComp, SWT.BORDER);
					mASAdvancedSpacesInsideArrayDeclBrackets.setMinimum(0);
					mASAdvancedSpacesInsideArrayDeclBrackets.setMaximum(10);
					mASAdvancedSpacesInsideArrayDeclBrackets.addSelectionListener(textUpdater);
					mASAdvancedSpacesInsideArrayDeclBrackets.setToolTipText("Number of spaces inside opening and closing '[' in array literals");

					labelComp=createLabelComp(parent, "Spaces inside '[' in array references");
					mASAdvancedSpacesInsideArrayRefBrackets=new Spinner(labelComp, SWT.BORDER);
					mASAdvancedSpacesInsideArrayRefBrackets.setMinimum(0);
					mASAdvancedSpacesInsideArrayRefBrackets.setMaximum(10);
					mASAdvancedSpacesInsideArrayRefBrackets.addSelectionListener(textUpdater);
					mASAdvancedSpacesInsideArrayRefBrackets.setToolTipText("Number of spaces inside opening and closing '[' in array references");
				}
				
				public void addGlobalItem(final ExpandableComposite ec)
				{
					Composite labelComp=new Composite(ec, SWT.NONE);
					GridLayout gl=new GridLayout(5, false);
					gl.marginHeight=0;
					labelComp.setLayout(gl);
					
					mASUseGlobalSpacesInsideParens=new Button(labelComp, SWT.CHECK);
					mASUseGlobalSpacesInsideParens.addSelectionListener(textUpdater);
					mASUseGlobalSpacesInsideParens.setToolTipText("If checked, use the global spaces setting.  Otherwise, specify the advanced settings available by clicking the 'twistie' button");
					//TODO: this call to setExpanded doesn't work.  Couldn't see how to get access to the private toggleState() call.
					mASUseGlobalSpacesInsideParens.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							ec.setExpanded(!mASUseGlobalSpacesInsideParens.getSelection());
							relayoutFromComponent(ec);
						}
						
					});
					
					Text spacer=new Text(labelComp, SWT.None);
					GridData gd=new GridData();
					gd.widthHint=5;
					spacer.setLayoutData(gd);
					spacer.setVisible(false);
					
					Label l=new Label(labelComp, SWT.None);
					l.setText("Spaces inside (,[,{");
					
					mASSpacesInsideParens=new Spinner(labelComp, SWT.BORDER);
					mASSpacesInsideParens.setMinimum(0);
					mASSpacesInsideParens.setMaximum(10);
					mASSpacesInsideParens.addSelectionListener(textUpdater);
					mASSpacesInsideParens.setToolTipText("Number of spaces inside opening and closing '(', '[', and '{' (inside object literals}.");
					
					spacer=new Text(labelComp, SWT.None);
					spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					spacer.setVisible(false);
					
					ec.setTextClient(labelComp);
				}
			}
			);
			
			mASCollapseAdjacentParens=new Button(mSpacesGroup, SWT.CHECK);
			mASCollapseAdjacentParens.setText("No spaces between adjacent (,[");
			mASCollapseAdjacentParens.setToolTipText("If checked, don't include spaces between adjacent parentheses/brackets (ex. '[(( x+2 )+2 )]')");
			mASCollapseAdjacentParens.addSelectionListener(textUpdater);
			
			labelComp=createLabelComp(mSpacesGroup, "Spaces before comma");
			mSpacesBeforeComma=new Spinner(labelComp, SWT.BORDER);
			mSpacesBeforeComma.setMinimum(0);
			mSpacesBeforeComma.setMaximum(10);
			mSpacesBeforeComma.addSelectionListener(textUpdater);
			mSpacesBeforeComma.setToolTipText("Number of spaces before each comma (in declarations, parameters lists, etc.)");
			
			labelComp=createLabelComp(mSpacesGroup, "Spaces after comma");
			mSpacesAfterComma=new Spinner(labelComp, SWT.BORDER);
			mSpacesAfterComma.setMinimum(0);
			mSpacesAfterComma.setMaximum(10);
			mSpacesAfterComma.addSelectionListener(textUpdater);
			mSpacesAfterComma.setToolTipText("Number of spaces after each comma (in declarations, parameters lists, etc.)");
			
			labelComp=createLabelComp(mSpacesGroup, "Spaces around assignments");
			mSpacesAroundAssignment=new Spinner(labelComp, SWT.BORDER);
			mSpacesAroundAssignment.setMinimum(0);
			mSpacesAroundAssignment.setMaximum(10);
			mSpacesAroundAssignment.addSelectionListener(textUpdater);
			mSpacesAroundAssignment.setToolTipText("Number of spaces on either side of '=' in variable assignments");
			
			labelComp=createLabelComp(mSpacesGroup, "Spaces around binary symbolic operators");
			mSpacesAroundSymbolicOperator=new Spinner(labelComp, SWT.BORDER);
			mSpacesAroundSymbolicOperator.setMinimum(0);
			mSpacesAroundSymbolicOperator.setMaximum(10);
			mSpacesAroundSymbolicOperator.addSelectionListener(textUpdater);
			mSpacesAroundSymbolicOperator.setToolTipText("Number of spaces on either side of an operator (boolean or arithmetic symbolic operators)");
			
			
			createAdvancedGroup(mSpacesGroup, new IAdvancedSettingsLayout()
			{
				public void addAdvancedItems(Composite parent)
				{
					Composite labelComp=createLabelComp(parent, "Spaces before variable declaration colons");
					mASAdvancedSpacesBeforeColonsInDeclarations = new Spinner( labelComp, SWT.BORDER );
					mASAdvancedSpacesBeforeColonsInDeclarations.setMinimum( 0 );
					mASAdvancedSpacesBeforeColonsInDeclarations.setMaximum( 10 );
					mASAdvancedSpacesBeforeColonsInDeclarations.addSelectionListener( textUpdater );
					mASAdvancedSpacesBeforeColonsInDeclarations.setToolTipText( "Number of spaces before a declaration colon (ex. var a<spaces>:Boolean)" );
					
					labelComp=createLabelComp(parent, "Spaces after variable declaration colons");
					mASAdvancedSpacesAfterColonsInDeclarations = new Spinner( labelComp, SWT.BORDER );
					mASAdvancedSpacesAfterColonsInDeclarations.setMinimum( 0 );
					mASAdvancedSpacesAfterColonsInDeclarations.setMaximum( 10 );
					mASAdvancedSpacesAfterColonsInDeclarations.addSelectionListener( textUpdater );
					mASAdvancedSpacesAfterColonsInDeclarations.setToolTipText( "Number of spaces after a declaration colon (ex. var a:<spaces>Boolean)" );

					labelComp = createLabelComp( parent, "Spaces before colons in function type declarations" );
					mASAdvancedSpacesBeforeColonsInFunctions = new Spinner(labelComp, SWT.BORDER );
					mASAdvancedSpacesBeforeColonsInFunctions.setMinimum( 0 );
					mASAdvancedSpacesBeforeColonsInFunctions.setMaximum( 10 );
					mASAdvancedSpacesBeforeColonsInFunctions.addSelectionListener( textUpdater );
					mASAdvancedSpacesBeforeColonsInFunctions.setToolTipText( "Number of spaces before a type colon in function signatures (ex. function a()<spaces>:void)" );

					labelComp = createLabelComp( parent, "Spaces after colons in function type declarations" );
					mASAdvancedSpacesAfterColonsInFunctions = new Spinner(labelComp, SWT.BORDER );
					mASAdvancedSpacesAfterColonsInFunctions.setMinimum( 0 );
					mASAdvancedSpacesAfterColonsInFunctions.setMaximum( 10 );
					mASAdvancedSpacesAfterColonsInFunctions.addSelectionListener( textUpdater );
					mASAdvancedSpacesAfterColonsInFunctions.setToolTipText( "Number of spaces after a type colon in a function signature (ex. function a():<spaces>void)" );
				}
				
				public void addGlobalItem(final ExpandableComposite ec)
				{
					Composite labelComp=new Composite(ec, SWT.NONE);
					GridLayout gl=new GridLayout(5, false);
					gl.marginHeight=0;
					labelComp.setLayout(gl);
					
					mASUseGlobalSpacesAroundColons=new Button(labelComp, SWT.CHECK);
					mASUseGlobalSpacesAroundColons.addSelectionListener(textUpdater);
					mASUseGlobalSpacesAroundColons.setToolTipText("If checked, use the global colons setting.  Otherwise, specify the advanced settings available by clicking the 'twistie' button");
					//TODO: this call to setExpanded doesn't work.  Couldn't see how to get access to the private toggleState() call.
					mASUseGlobalSpacesAroundColons.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							ec.setExpanded(!mASUseGlobalSpacesAroundColons.getSelection());
							relayoutFromComponent(ec);
						}
						
					});
					
					Text spacer=new Text(labelComp, SWT.None);
					GridData gd=new GridData();
					gd.widthHint=5;
					spacer.setLayoutData(gd);
					spacer.setVisible(false);
					
					Label l=new Label(labelComp, SWT.None);
					l.setText("Spaces around declaration colons");
					
					mASSpacesAroundColons=new Spinner(labelComp, SWT.BORDER);
					mASSpacesAroundColons.setMinimum(0);
					mASSpacesAroundColons.setMaximum(10);
					mASSpacesAroundColons.addSelectionListener(textUpdater);
					mASSpacesAroundColons.setToolTipText("Number of spaces around the ':' in type declarations");
					
					spacer=new Text(labelComp, SWT.None);
					spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					spacer.setVisible(false);
					
					ec.setTextClient(labelComp);
				}
			});
			
//			labelComp=createLabelComp(spacesGroup, "Spaces around declaration colons");
//			mASSpacesAroundColons=new Spinner(labelComp, SWT.BORDER);
//			mASSpacesAroundColons.setMinimum(0);
//			mASSpacesAroundColons.setMaximum(10);
//			mASSpacesAroundColons.addSelectionListener(textUpdater);
//			mASSpacesAroundColons.setToolTipText("Number of spaces around the ':' in type declarations");
			
			labelComp=createLabelComp(mSpacesGroup, "Spaces after label colon");
			mASSpacesAfterLabelColon=new Spinner(labelComp, SWT.BORDER);
			mASSpacesAfterLabelColon.setMinimum(0);
			mASSpacesAfterLabelColon.setMaximum(10);
			mASSpacesAfterLabelColon.addSelectionListener(textUpdater);
			mASSpacesAfterLabelColon.setToolTipText("Number of spaces after the ':' in labels and switch statements)");
			
			labelComp=createLabelComp(mSpacesGroup, "Spaces before open paren (after keyword)");
			mASSpacesBeforeOpenControlParen=new Spinner(labelComp, SWT.BORDER);
			mASSpacesBeforeOpenControlParen.setMinimum(0);
			mASSpacesBeforeOpenControlParen.setMaximum(10);
			mASSpacesBeforeOpenControlParen.addSelectionListener(textUpdater);
			mASSpacesBeforeOpenControlParen.setToolTipText("Number of spaces before the open paren in control statements (ex. after 'for' keyword)");
			
			Composite beforeParenComp=new Composite(mSpacesGroup, SWT.None);
			GridLayout parenCompLayout=new GridLayout(2, false);
			parenCompLayout.marginWidth=0;
			parenCompLayout.marginHeight=0;
			beforeParenComp.setLayout(parenCompLayout);
			labelComp=createLabelComp(beforeParenComp, "Spaces before parameters:");
			mASSpacesBeforeDeclParameters=new Spinner(labelComp, SWT.BORDER);
			mASSpacesBeforeDeclParameters.setMinimum(0);
			mASSpacesBeforeDeclParameters.setMaximum(10);
			mASSpacesBeforeDeclParameters.addSelectionListener(textUpdater);
			mASSpacesBeforeDeclParameters.setToolTipText("Number of spaces before the open paren of function parameters");
			
			labelComp=createLabelComp(beforeParenComp, "arguments:");
			mASSpacesBeforeArguments=new Spinner(labelComp, SWT.BORDER);
			mASSpacesBeforeArguments.setMinimum(0);
			mASSpacesBeforeArguments.setMaximum(10);
			mASSpacesBeforeArguments.addSelectionListener(textUpdater);
			mASSpacesBeforeArguments.setToolTipText("Number of spaces before the open paren of function arguments");
			
			Group blankLinesGroup=new Group(settingsComp, SWT.None);
			blankLinesGroup.setLayout(new GridLayout());
			blankLinesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			blankLinesGroup.setText("Blank Lines");
			
			Composite spaceSaver=new Composite(blankLinesGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			spaceSaver.setLayout(gl);
			
			mKeepBlankLines=new Button(spaceSaver, SWT.CHECK);
			mKeepBlankLines.setText("Don't delete existing blank lines");
			mKeepBlankLines.addSelectionListener(textUpdater);
			mKeepBlankLines.setToolTipText("Attempt to keep blank lines where they currently exist.  Blank lines will be added based on other settings, but not removed.\nHowever, this is overridden to some extent based on the wrapping settings for expressions etc.");
			
			labelComp=createLabelComp(spaceSaver, "...but keep this many:");
			((GridLayout)labelComp.getLayout()).marginLeft=15;
			mASBlankLinesToKeep=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesToKeep.setMinimum(0);
			mASBlankLinesToKeep.setMaximum(10);
			mASBlankLinesToKeep.addSelectionListener(textUpdater);
			mASBlankLinesToKeep.setToolTipText("Number of blank lines to keep while deleting extra blank lines");
			
			labelComp=createLabelComp(spaceSaver, "Lines before functions");
			mBlankLinesBeforeFunctions=new Spinner(labelComp, SWT.BORDER);
			mBlankLinesBeforeFunctions.setMinimum(0);
			mBlankLinesBeforeFunctions.setMaximum(10);
			mBlankLinesBeforeFunctions.addSelectionListener(textUpdater);
			mBlankLinesBeforeFunctions.setToolTipText("Number of blank lines before function declarations");
			
			labelComp=createLabelComp(spaceSaver, "Lines before classes");
			mASBlankLinesBeforeClass=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesBeforeClass.setMinimum(0);
			mASBlankLinesBeforeClass.setMaximum(10);
			mASBlankLinesBeforeClass.addSelectionListener(textUpdater);
			mASBlankLinesBeforeClass.setToolTipText("Number of blank lines before class/interface declarations");
			
			labelComp=createLabelComp(spaceSaver, "Lines before imports");
			mASBlankLinesBeforeImports=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesBeforeImports.setMinimum(0);
			mASBlankLinesBeforeImports.setMaximum(10);
			mASBlankLinesBeforeImports.addSelectionListener(textUpdater);
			mASBlankLinesBeforeImports.setToolTipText("Number of blank lines before the first import in the package block.  Does not apply otherwise.");
			
			labelComp=createLabelComp(spaceSaver, "Lines before properties");
			mASBlankLinesBeforeProperties=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesBeforeProperties.setMinimum(0);
			mASBlankLinesBeforeProperties.setMaximum(10);
			mASBlankLinesBeforeProperties.addSelectionListener(textUpdater);
			mASBlankLinesBeforeProperties.setToolTipText("Number of blank lines before each property declaration");
			
			labelComp=createLabelComp(spaceSaver, "Lines at function start");
			mASBlankLinesAtFunctionStart=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesAtFunctionStart.setMinimum(0);
			mASBlankLinesAtFunctionStart.setMaximum(10);
			mASBlankLinesAtFunctionStart.addSelectionListener(textUpdater);
			mASBlankLinesAtFunctionStart.setToolTipText("Number of blank lines at the beginning of each function");
			
			labelComp=createLabelComp(spaceSaver, "Lines at function end");
			mASBlankLinesAtFunctionEnd=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesAtFunctionEnd.setMinimum(0);
			mASBlankLinesAtFunctionEnd.setMaximum(10);
			mASBlankLinesAtFunctionEnd.addSelectionListener(textUpdater);
			mASBlankLinesAtFunctionEnd.setToolTipText("Number of blank lines at end end of each function");
			
			labelComp=createLabelComp(blankLinesGroup, "Lines before control statements");
			mASBlankLinesBeforeControlStatement=new Spinner(labelComp, SWT.BORDER);
			mASBlankLinesBeforeControlStatement.setMinimum(0);
			mASBlankLinesBeforeControlStatement.setMaximum(10);
			mASBlankLinesBeforeControlStatement.addSelectionListener(textUpdater);
			mASBlankLinesBeforeControlStatement.setToolTipText("Number of blank lines before control statements (if, for, switch, etc.)");
			
			labelComp=createLabelComp(settingsComp, "**Always generate indent");
			mASAlwaysGenerateIndent=new Button(labelComp, SWT.CHECK);
			mASAlwaysGenerateIndent.addSelectionListener(textUpdater);
			mASAlwaysGenerateIndent.setToolTipText("Generate indent whitespace even if there is no text content on the line");
			
//			mASTrimTrailingWS=new Button(settingsComp, SWT.CHECK);
//			mASTrimTrailingWS.setText("Trim all trailing whitespace");
//			mASTrimTrailingWS.addSelectionListener(textUpdater);
//			mASTrimTrailingWS.setToolTipText("Remove all trailing whitespace from lines while formatting.  Even if this option isn't checked, the only whitespace left may be a space or two after case labels and wrapped operators.\nThis option may be more fully specified in the future.");
		}
		
		{
			Composite thisTab=new Composite(subTabFolder, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			thisTab.setLayout(gl);
			thisTab.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			ScrolledComposite scrollComp=new ScrolledComposite(thisTab, SWT.V_SCROLL);
			gl=new GridLayout();
			gl.marginWidth=0;
			gl.marginHeight=0;
			scrollComp.setLayout(gl);
			gd=new GridData(GridData.FILL_BOTH);
			scrollComp.setLayoutData(gd);
			scrollComp.setExpandHorizontal(true);
			scrollComp.setExpandVertical(true);
			
			Composite newlinesComp=new Composite(scrollComp, SWT.NONE);
			gl=new GridLayout();
			gl.marginWidth=0;
			gl.marginHeight=0;
			newlinesComp.setLayout(gl);
//			newlinesComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			scrollComp.setContent(newlinesComp);
			scrollComp.setAlwaysShowScrollBars(true);
			
			TabItem newlinesTab=new TabItem(subTabFolder, SWT.None);
			newlinesTab.setText("Newlines/wrapping");
			
			newlinesTab.setControl(thisTab);
			
			Composite settingsComp=new Composite(newlinesComp, SWT.NONE);
			gl=new GridLayout();
			gl.marginWidth=0;
			gl.marginHeight=0;
			settingsComp.setLayout(gl);
//			settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Composite intGroup=new Composite(settingsComp, SWT.None);
			intGroup.setLayout(new GridLayout(2, true));
	
			
			Group wrapGroup=new Group(settingsComp, SWT.None);
			wrapGroup.setLayout(new GridLayout());
			wrapGroup.setText("Text wrapping/formatting");
			
			Composite labelComp=createLabelComp(wrapGroup, "Array declarations");
			mASArrayDeclWrapCombo=new Combo(labelComp, SWT.BORDER | SWT.READ_ONLY);
			addWrapOptions(mASArrayDeclWrapCombo, false);
			mASArrayDeclWrapCombo.addSelectionListener(textUpdater);
			
			labelComp=createLabelComp(wrapGroup, "Method arguments");
			mASMethodCallWrapCombo=new Combo(labelComp, SWT.BORDER | SWT.READ_ONLY);
			addWrapOptions(mASMethodCallWrapCombo, false);
			mASMethodCallWrapCombo.addSelectionListener(textUpdater);
			mASMethodCallWrapCombo.setToolTipText("This applies to method calls");
			
			labelComp=createLabelComp(wrapGroup, "Method parameters");
			mASMethodDeclWrapCombo=new Combo(labelComp, SWT.BORDER | SWT.READ_ONLY);
			addWrapOptions(mASMethodDeclWrapCombo, false);
			mASMethodDeclWrapCombo.addSelectionListener(textUpdater);
			mASMethodDeclWrapCombo.setToolTipText("This applies to method declarations");
			
			labelComp=createLabelComp(wrapGroup, "General expressions");
			mASExpressionWrapCombo=new Combo(labelComp, SWT.BORDER | SWT.READ_ONLY);
			addWrapOptions(mASExpressionWrapCombo, false);
			mASExpressionWrapCombo.addSelectionListener(textUpdater);
			mASExpressionWrapCombo.setToolTipText("This applies to general expressions connected by arithmetic or boolean operators");
		
			labelComp=createLabelComp(wrapGroup, "Embedded XML (e4X)");
			mASXMLWrapCombo=new Combo(labelComp, SWT.BORDER | SWT.READ_ONLY);
			addWrapOptions(mASXMLWrapCombo, true);
			mASXMLWrapCombo.addSelectionListener(textUpdater);
			mASXMLWrapCombo.setToolTipText("This applies to XML code embedded inside actionscript; it does not apply to MXML code, nor do MXML settings apply to it");
			
			labelComp=createLabelComp(wrapGroup, "Max line length (see tooltip)");
			mASMaxLineLength=new Spinner(labelComp, SWT.BORDER);
			mASMaxLineLength.setMinimum(50);
			mASMaxLineLength.setMaximum(1000);
			mASMaxLineLength.addSelectionListener(textUpdater);
			mASMaxLineLength.setToolTipText("Max line length is used by particular formatting elements as a HINT, not a firm limit.  \nIt applies to any wrapping element that is set to 'Wrap to max length' or 'Wrap without removing newlines'");

			Composite beforeAfterComp=new Composite(wrapGroup, SWT.None);
			gl=new GridLayout(4, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			beforeAfterComp.setLayout(gl);
			beforeAfterComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASBreakLinesBeforeComma=new Button(beforeAfterComp, SWT.CHECK);
			mASBreakLinesBeforeComma.setText("Break lines before separator");
			mASBreakLinesBeforeComma.addSelectionListener(textUpdater);
			mASBreakLinesBeforeComma.setToolTipText("When wrapping a line, should the line break be added before the ',' (or '=', '.', '::', or 'keyword'), or afterward?");
			
			mASBreakLinesBeforeArithmeticOperator=new Button(beforeAfterComp, SWT.CHECK);
			mASBreakLinesBeforeArithmeticOperator.setText("...math");
			mASBreakLinesBeforeArithmeticOperator.addSelectionListener(textUpdater);
			mASBreakLinesBeforeArithmeticOperator.setToolTipText("When wrapping a line, should the line break be added before the arithmetic operator, or afterward?");
			
			mASBreakLinesBeforeLogicalOperator=new Button(beforeAfterComp, SWT.CHECK);
			mASBreakLinesBeforeLogicalOperator.setText("...logical");
			mASBreakLinesBeforeLogicalOperator.addSelectionListener(textUpdater);
			mASBreakLinesBeforeLogicalOperator.setToolTipText("When wrapping a line, should the line break be added before the logical operator, or afterward?");
			
			mASBreakLinesBeforeAssignment=new Button(beforeAfterComp, SWT.CHECK);
			mASBreakLinesBeforeAssignment.setText("...assign");
			mASBreakLinesBeforeAssignment.addSelectionListener(textUpdater);
			mASBreakLinesBeforeAssignment.setToolTipText("When wrapping a line, should the line break be added before the '=', or afterward?");
			
			Composite indentComp=new Composite(wrapGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			indentComp.setLayout(gl);
			
			mASSpecialWrapCommaItems=new Button(indentComp, SWT.CHECK);
			mASSpecialWrapCommaItems.setText("Indent to first item");
			mASSpecialWrapCommaItems.addSelectionListener(textUpdater);
			mASSpecialWrapCommaItems.setToolTipText("When indenting, should regular indent (based on the hanging indent setting) be used, or should indent be to the first item (ex. wrap to column of 1st parameter in method call), where applicable");
			
			labelComp=createLabelComp(indentComp, "**Hanging indent tab stops");
			mASHangingIndentSize=new Spinner(labelComp, SWT.BORDER);
			mASHangingIndentSize.setMinimum(0);
			mASHangingIndentSize.setMaximum(10);
			mASHangingIndentSize.addSelectionListener(textUpdater);
			mASHangingIndentSize.setToolTipText("Number of tab stops to use for hanging indents caused by wrapping.  This is used when not indenting to the first item.");
			
			/////////////////////////////////////////Advanced as wrapping info /////////////////////////////////////
			Group advWrapGroup=new Group(wrapGroup, SWT.None);
			advWrapGroup.setText("Advanced wrapping");
			gl=new GridLayout();
			gl.marginHeight=0;
			gl.marginWidth=0;
			advWrapGroup.setLayout(gl);
			advWrapGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			Composite advWrapComp=new Composite(advWrapGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginHeight=0;
			gl.marginWidth=0;
			advWrapComp.setLayout(gl);
			advWrapComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASUseAdvancedWrapping=new Button(advWrapComp, SWT.CHECK);
			mASUseAdvancedWrapping.setText("Use advanced wrapping");
			mASUseAdvancedWrapping.addSelectionListener(textUpdater);
			mASUseAdvancedWrapping.setToolTipText("Use the new advanced wrapping algorithm.  The advanced algorithm will become the only algorithm at some point.");
			
			new Label(advWrapComp, SWT.None); //placeholder
			
			mASWrappingItemsTable=new Table(advWrapComp, SWT.SINGLE | SWT.BORDER | SWT.CHECK | SWT.V_SCROLL);
			gd=new GridData(GridData.FILL_BOTH);
			gd.heightHint=mASWrappingItemsTable.getItemHeight()*3;
			mASWrappingItemsTable.setLayoutData(gd);
			mASWrappingItemsTable.addSelectionListener(textUpdater);
			mASWrappingItemsTable.setToolTipText("Choose items from the table where you want line breaks to be potentially added.  If you don't check an item, then lines will not be broken there unless you choose the 'Enforce Max' option.");
			
			Composite otherWrapOptions=new Composite(advWrapComp, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			otherWrapOptions.setLayout(gl);
			
			labelComp=createLabelComp(otherWrapOptions, "Grace columns");
			mASWrappingPostMaxLeniency=new Spinner(labelComp, SWT.BORDER);
			mASWrappingPostMaxLeniency.setMinimum(0);
			mASWrappingPostMaxLeniency.addSelectionListener(textUpdater);
			mASWrappingPostMaxLeniency.setToolTipText("The number of columns beyond the 'max' column that can be used to break lines at preferred locations.");
			
			mASWrappingBreakOnPhrases=new Button(otherWrapOptions, SWT.CHECK);
			mASWrappingBreakOnPhrases.setText("Break on phrase boundaries");
			mASWrappingBreakOnPhrases.setToolTipText("If checked, search for available break points working from higher precedence to lower precedence.  If not checked, then precedence is not used.");
			mASWrappingBreakOnPhrases.addSelectionListener(textUpdater);
			
			mASWrappingEnforceMax=new Button(otherWrapOptions, SWT.CHECK);
			mASWrappingEnforceMax.setText("Enforce max length");
			mASWrappingEnforceMax.addSelectionListener(textUpdater);
			mASWrappingEnforceMax.setToolTipText("If checked, use all available break locations if searching the user-selected break points (in the table) doesn't find a break location.  Otherwise, the line will not be wrapped."); 
			
			Composite wrapAllComp=new Composite(advWrapGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginHeight=0;
			wrapAllComp.setLayout(gl);
			
			mASWrappingAllArgs=new Button(wrapAllComp, SWT.CHECK);
			mASWrappingAllArgs.setText("Wrap each method argument if any are wrapped");
			mASWrappingAllArgs.addSelectionListener(textUpdater);
			mASWrappingAllArgs.setToolTipText("If checked, wrap each of the arguments to a method call if any split point is chosen on a comma separating the args.  Only valid when wrapping to max line length.  Will not work exactly the same when indenting."); 
			
			mASWrappingFirstArg=new Button(wrapAllComp, SWT.CHECK);
			mASWrappingFirstArg.setText("Wrap first");
			mASWrappingFirstArg.addSelectionListener(textUpdater);
			mASWrappingFirstArg.setToolTipText("If checked, wrap the first argument when wrapping all the arguments.  So, put the first argument on a separate line from the open paren of the method call."); 
			
			mASWrappingAllParms=new Button(wrapAllComp, SWT.CHECK);
			mASWrappingAllParms.setText("Wrap each method parameter if any are wrapped");
			mASWrappingAllParms.addSelectionListener(textUpdater);
			mASWrappingAllParms.setToolTipText("If checked, wrap each of the parameters of a method declaration if any split point is chosen on a comma separating the parameters.  Only valid when wrapping to max line length.  Will not work exactly the same when indenting."); 
			
			mASWrappingFirstParm=new Button(wrapAllComp, SWT.CHECK);
			mASWrappingFirstParm.setText("Wrap first");
			mASWrappingFirstParm.addSelectionListener(textUpdater);
			mASWrappingFirstParm.setToolTipText("If checked, wrap the first parameter when wrapping all the parameters.  So, put the first parameter on a separate line from the open paren of the function declaration."); 
			
			Composite wrapItemsComp=new Composite(advWrapGroup, SWT.None);
			gl=new GridLayout(3, false);
			gl.marginHeight=0;
			wrapItemsComp.setLayout(gl);
			
			mASWrappingAllArrayItems=new Button(wrapItemsComp, SWT.CHECK);
			mASWrappingAllArrayItems.setText("Wrap array items all at once");
			mASWrappingAllArrayItems.addSelectionListener(textUpdater);
			mASWrappingAllArrayItems.setToolTipText("If checked, wrap all of the array items to the max length if any split point is chosen on a comma separating the args.  Only valid when wrapping to max line length.  Will not work exactly the same when indenting."); 
			
			mASWrappingFirstArrayItem=new Button(wrapItemsComp, SWT.CHECK);
			mASWrappingFirstArrayItem.setText("Wrap first");
			mASWrappingFirstArrayItem.addSelectionListener(textUpdater);
			mASWrappingFirstArrayItem.setToolTipText("If checked, wrap the first array item when wrapping all the items.  So, put the first array element on a separate line."); 
			
			mASWrappingAlignArrayItems=new Button(wrapItemsComp, SWT.CHECK);
			mASWrappingAlignArrayItems.setText("Align");
			mASWrappingAlignArrayItems.addSelectionListener(textUpdater);
			mASWrappingAlignArrayItems.setToolTipText("If checked, align wrapped elements of the array so that they start at the same column (ex. 1st item on line 1 is directly above 1st item on line 2)."); 
			
			mASWrappingAllObjectItems=new Button(wrapItemsComp, SWT.CHECK);
			mASWrappingAllObjectItems.setText("Wrap object attrs all at once");
			mASWrappingAllObjectItems.addSelectionListener(textUpdater);
			mASWrappingAllObjectItems.setToolTipText("If checked, wrap all the attributes of an object definition if any split point is chosen on a comma separating the attributes.  Only valid when wrapping to max line length.  Will not work exactly the same when indenting."); 
			
			mASWrappingFirstObjectItem=new Button(wrapItemsComp, SWT.CHECK);
			mASWrappingFirstObjectItem.setText("Wrap first");
			mASWrappingFirstObjectItem.addSelectionListener(textUpdater);
			mASWrappingFirstObjectItem.setToolTipText("If checked, wrap the first attribute when wrapping all the items.  So, put the first attribute on a separate line."); 
			
			mASWrappingAlignObjectItems=new Button(wrapItemsComp, SWT.CHECK);
			mASWrappingAlignObjectItems.setText("Align");
			mASWrappingAlignObjectItems.addSelectionListener(textUpdater);
			mASWrappingAlignObjectItems.setToolTipText("If checked, align wrapped attributes of the object so that they start at the same column (ex. 1st item on line is directly above 1st item on line 2)."); 
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			Group newlineGroup=new Group(settingsComp, SWT.None);
			newlineGroup.setLayout(new GridLayout());
			newlineGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			newlineGroup.setText("Braces/Line breaks");
			
			createAdvancedGroup(newlineGroup, new IAdvancedSettingsLayout()
			{
				public void addAdvancedItems(Composite parent)
				{
					Composite wrapperComp=new Composite(parent, SWT.None);
					GridLayout layout=new GridLayout(2, true);
					wrapperComp.setLayout(layout);
					
					mASCRBeforeElse=new Button(wrapperComp, SWT.CHECK);
					mASCRBeforeElse.setText("Else on new line");
					mASCRBeforeElse.addSelectionListener(textUpdater);
					mASCRBeforeElse.setToolTipText("If true, insert a line break before 'else' clauses.  Makes most sense if '{' are also on new line.");
					
					mASCRBeforeElseInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mKeepElseIfOnSameLine=new Button(wrapperComp, SWT.CHECK);
					mKeepElseIfOnSameLine.setText("Else-If on same line");
					mKeepElseIfOnSameLine.addSelectionListener(textUpdater);
					mKeepElseIfOnSameLine.setToolTipText("if true, don't insert a carriage return before the 'if' in an 'else if' construct");
					
					mKeepElseIfOnSameLineInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mASCRBeforeCatch=new Button(wrapperComp, SWT.CHECK);
					mASCRBeforeCatch.setText("Catch/Finally on new line");
					mASCRBeforeCatch.addSelectionListener(textUpdater);
					mASCRBeforeCatch.setToolTipText("If true, insert a line break before catch/finally clauses.  Makes most sense if '{' are also on new line.");
					
					mASCRBeforeCatchInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mASCRBeforeWhile=new Button(wrapperComp, SWT.CHECK);
					mASCRBeforeWhile.setText("While on new line after }");
					mASCRBeforeWhile.addSelectionListener(textUpdater);
					mASCRBeforeWhile.setToolTipText("If true, insert a line break before while keyword in a do..while loop.");
					
					mASCRBeforeWhileInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);

					mASNoCRBeforeBreak=new Button(wrapperComp, SWT.CHECK);
					mASNoCRBeforeBreak.setText("No new CR before break");
					mASNoCRBeforeBreak.addSelectionListener(textUpdater);
					mASNoCRBeforeBreak.setToolTipText("If true, don't insert a line break before 'break' statements that are on the same line with a conditional or loop (ex. 'if', 'for')");
					
					mASNoCRBeforeBreakInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mASNoCRBeforeContinue=new Button(wrapperComp, SWT.CHECK);
					mASNoCRBeforeContinue.setText("No new CR before Continue");
					mASNoCRBeforeContinue.addSelectionListener(textUpdater);
					mASNoCRBeforeContinue.setToolTipText("If true, don't insert a line break before 'continue' statements that are on the same line with a conditional or loop (ex. 'if', 'for')");
					
					mASNoCRBeforeContinueInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mASNoCRBeforeReturn=new Button(wrapperComp, SWT.CHECK);
					mASNoCRBeforeReturn.setText("No new CR before Return");
					mASNoCRBeforeReturn.addSelectionListener(textUpdater);
					mASNoCRBeforeReturn.setToolTipText("If true, don't insert a line break before 'return' statements that are on the same line with a conditional or loop (ex. 'if', 'for')");
					
					mASNoCRBeforeReturnInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mASNoCRBeforeThrow=new Button(wrapperComp, SWT.CHECK);
					mASNoCRBeforeThrow.setText("No new CR before Throw");
					mASNoCRBeforeThrow.addSelectionListener(textUpdater);
					mASNoCRBeforeThrow.setToolTipText("If true, don't insert a line break before 'throw' statements that are on the same line with a conditional or loop (ex. 'if', 'for')");
					
					mASNoCRBeforeThrowInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					mASNoCRBeforeExpression=new Button(wrapperComp, SWT.CHECK);
					mASNoCRBeforeExpression.setText("No new CR before expression");
					mASNoCRBeforeExpression.addSelectionListener(textUpdater);
					mASNoCRBeforeExpression.setToolTipText("If true, don't insert a line break before expression statements that are on the same line with a conditional or loop (ex. 'if', 'for').\nThis might be a function call or pretty much any other non-control-statement code.\nThis is considered experimental because there may be code situations where this option yields surprising results.");
					
					mASNoCRBeforeExpressionInheritedVal=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
					
					createAdvancedGroup(parent, new IAdvancedSettingsLayout()
					{
						public void addAdvancedItems(Composite parent)
						{
							Label l=new Label(parent, SWT.None);
							l.setText("Newline before '{' for:");
							Composite wrapperComp=new Composite(parent, SWT.None);
							GridLayout layout=new GridLayout(2, true);
							layout.marginLeft=20;
							wrapperComp.setLayout(layout);
							
							List<String> crTypes=new ArrayList<String>();
							crTypes.addAll(ASPrettyPrinter.Brace_Context_Items.keySet());
							Collections.sort(crTypes);
							for (String key: crTypes) {
								Button boolButton=new Button(wrapperComp, SWT.CHECK);
								boolButton.setText(key);
								boolButton.addSelectionListener(textUpdater);
								boolButton.setToolTipText("If checked, put the open brace associated with this item on a new line");
								mASAdvancedOpenBraceButtons.add(boolButton);
								boolButton.setData(ASPrettyPrinter.Brace_Context_Items.get(key));
								boolButton.addSelectionListener(textUpdater);
								
								Text inheritedValText=new Text(wrapperComp, SWT.READ_ONLY | SWT.SINGLE);
								mASAdvancedOpenBraceInheritedValues.add(inheritedValText);
							}
						}

						public void addGlobalItem(final ExpandableComposite ec)
						{
							Composite labelComp=new Composite(ec, SWT.NONE);
							GridLayout gl=new GridLayout(5, false);
							gl.marginHeight=0;
							labelComp.setLayout(gl);

							mASUseGlobalOpenBraceOnNewLine=new Button(labelComp, SWT.CHECK);
							mASUseGlobalOpenBraceOnNewLine.addSelectionListener(textUpdater);
							mASUseGlobalOpenBraceOnNewLine.setToolTipText("If checked, use the global newline setting.  Otherwise, specify the advanced settings available by clicking the 'twistie' button");
							mASUseGlobalOpenBraceOnNewLine.addSelectionListener(new SelectionAdapter()
							{
								@Override
								public void widgetSelected(SelectionEvent e)
								{
									ec.setExpanded(!mASUseGlobalOpenBraceOnNewLine.getSelection());
									relayoutFromComponent(ec);
								}

							});

							Text spacer=new Text(labelComp, SWT.None);
							GridData gd=new GridData();
							gd.widthHint=5;
							spacer.setLayoutData(gd);
							spacer.setVisible(false);

							mOpenBraceOnNewLine=new Button(labelComp, SWT.CHECK);
							mOpenBraceOnNewLine.setText("Open brace on new line");
							mOpenBraceOnNewLine.addSelectionListener(textUpdater);
							mOpenBraceOnNewLine.setToolTipText("If selected, the '{' will be placed on the next line after a control statement, class decl, function decl, etc.");

							mOpenBraceOnNewLineInheritedVal=new Text(labelComp, SWT.READ_ONLY | SWT.SINGLE);

							spacer=new Text(labelComp, SWT.None);
							spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
							spacer.setVisible(false);

							ec.setTextClient(labelComp);
						}
					});
				}
				
				public void addGlobalItem(final ExpandableComposite ec)
				{
					Composite labelComp=new Composite(ec, SWT.NONE);
					GridLayout gl=new GridLayout(5, false);
					gl.marginHeight=0;
					labelComp.setLayout(gl);
					labelComp.setLayoutData(new GridData(GridData.BEGINNING));
					
					mASUseBraceStyle=new Button(labelComp, SWT.CHECK);
					mASUseBraceStyle.addSelectionListener(textUpdater);
//					mASUseBraceStyle.setText("Use global setting - ");
					mASUseBraceStyle.setToolTipText("If checked, use the global brace style setting.  Otherwise, specify the advanced settings available by clicking the 'twistie' button");
					mASUseBraceStyle.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(SelectionEvent e)
						{
							ec.setExpanded(!mASUseBraceStyle.getSelection());
							relayoutFromComponent(ec);
						}
						
					});
					
					Text spacer=new Text(labelComp, SWT.None);
					GridData gd=new GridData();
					gd.widthHint=5;
					spacer.setLayoutData(gd);
					spacer.setVisible(false);
					
					Label l=new Label(labelComp, SWT.None);
					l.setText("Brace style:");
					
					mASBraceStyle=new Combo(labelComp, SWT.BORDER | SWT.READ_ONLY);
					String text="Adobe style";
					mASBraceStyle.add(text, 0);
					mASBraceStyle.setData(text, ASPrettyPrinter.BraceStyle_Adobe);
					text="Sun style";
					mASBraceStyle.add(text, 1);
					mASBraceStyle.setData(text, ASPrettyPrinter.BraceStyle_Sun);
					mASBraceStyle.addSelectionListener(textUpdater);
					mASBraceStyle.setToolTipText("Sun style is to have open braces on the same line as the statement.  Adobe is for open braces to appear on the next line.");
					
					spacer=new Text(labelComp, SWT.None);
					spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
					spacer.setVisible(false);
					
					ec.setTextClient(labelComp);
				}
			}
			);
			
//			mOpenBraceOnNewLine=new Button(newlineGroup, SWT.CHECK);
//			mOpenBraceOnNewLine.setText("Open brace on new line");
//			mOpenBraceOnNewLine.addSelectionListener(textUpdater);
//			mOpenBraceOnNewLine.setToolTipText("If selected, the '{' will be placed on the next line after a control statement, class decl, function decl, etc.");
//	
//			mASCRBeforeElse=new Button(newlineGroup, SWT.CHECK);
//			mASCRBeforeElse.setText("Else on new line");
//			mASCRBeforeElse.addSelectionListener(textUpdater);
//			mASCRBeforeElse.setToolTipText("If true, insert a line break before 'else' clauses.  Makes most sense if '{' are also on new line.");
//			
//			mKeepElseIfOnSameLine=new Button(newlineGroup, SWT.CHECK);
//			mKeepElseIfOnSameLine.setText("Else-If on same line");
//			mKeepElseIfOnSameLine.addSelectionListener(textUpdater);
//			mKeepElseIfOnSameLine.setToolTipText("if true, don't insert a carriage return before the 'if' in an 'else if' construct");
//			
//			mASCRBeforeCatch=new Button(newlineGroup, SWT.CHECK);
//			mASCRBeforeCatch.setText("Catch/Finally on new line");
//			mASCRBeforeCatch.addSelectionListener(textUpdater);
//			mASCRBeforeCatch.setToolTipText("If true, insert a line break before catch/finally clauses.  Makes most sense if '{' are also on new line.");
			
			mASNewlineBeforeBindableFunction=new Button(newlineGroup, SWT.CHECK);
			mASNewlineBeforeBindableFunction.setText("Newline between [Bindable] and functions");
			mASNewlineBeforeBindableFunction.addSelectionListener(textUpdater);
			mASNewlineBeforeBindableFunction.setToolTipText("If selected, include a line break after a [Bindable] tag.  Otherwise only use a space.\nThis only applies if the metatag is the last metatag before the associated declaration.");

			mASNewlineBeforeBindableProperty=new Button(newlineGroup, SWT.CHECK);
			mASNewlineBeforeBindableProperty.setText("Newline between [Bindable] and properties");
			mASNewlineBeforeBindableProperty.addSelectionListener(textUpdater);
			mASNewlineBeforeBindableProperty.setToolTipText("If selected, include a line break after a [Bindable] tag.  Otherwise only use a space.\nThis only applies if the metatag is the last metatag before the associated declaration.\nSee the tweaks page to add tags other than Bindable to the list.");

			mASEmptyStatementsOnNewLine=new Button(newlineGroup, SWT.CHECK);
			mASEmptyStatementsOnNewLine.setText("Put empty statements (';') on new line");
			mASEmptyStatementsOnNewLine.addSelectionListener(textUpdater);
			mASEmptyStatementsOnNewLine.setToolTipText("If selected, put empty statements on a line by themselves.  An empty statement is a semicolon with no other code.");
			
//			Text t=new Text(newlinesComp, SWT.MULTI);
//			t.setVisible(false);
//			gd=new GridData();
//			gd.heightHint=500;
//			t.setLayoutData(gd);
			newlinesComp.setSize (newlinesComp.computeSize
					(SWT.DEFAULT, SWT.DEFAULT));
			
		}
		
		{
			Composite tweaksComp=new Composite(subTabFolder, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			tweaksComp.setLayout(gl);
			
			TabItem tweaksTab=new TabItem(subTabFolder, SWT.None);
			tweaksTab.setText("Tweaks");
			
			tweaksTab.setControl(tweaksComp);
			
			Composite settingsComp=new Composite(tweaksComp, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			settingsComp.setLayout(gl);
			
			mNoFormatWarning=new Text(settingsComp, SWT.BOLD | SWT.BORDER_DOT);
			mNoFormatWarning.setText("WARNING: wrap settings may override some spacing options");

			Composite tweakComp=new Composite(settingsComp, SWT.None);
			gl=new GridLayout(3, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			tweakComp.setLayout(gl);
			
			mASUseSpacesAroundAssignmentInParameters=new Button(tweakComp, SWT.CHECK);
			mASUseSpacesAroundAssignmentInParameters.addSelectionListener(textUpdater);
			mASUseSpacesAroundAssignmentInParameters.setToolTipText("If this box is not checked, the regular setting for spaces around assignments is used");
			
			Label l=new Label(tweakComp, SWT.None);
			l.setText("Spaces around '=' of optional parameters");
			
			mASSpacesAroundAssignmentInParameters=new Spinner(tweakComp, SWT.BORDER);
			mASSpacesAroundAssignmentInParameters.setMinimum(0);
			mASSpacesAroundAssignmentInParameters.setMaximum(10);
			mASSpacesAroundAssignmentInParameters.addSelectionListener(textUpdater);
			mASSpacesAroundAssignmentInParameters.setToolTipText("Number of spaces around the '=' for optional parameters in function declarations.");
			
			mASUseSpacesAroundAssignmentInMetatags=new Button(tweakComp, SWT.CHECK);
			mASUseSpacesAroundAssignmentInMetatags.addSelectionListener(textUpdater);
			mASUseSpacesAroundAssignmentInMetatags.setToolTipText("If this box is not checked, the regular setting for spaces around assignments is used");
			
			l=new Label(tweakComp, SWT.None);
			l.setText("Spaces around '=' of meta tags");
			
			mASSpacesAroundAssignmentInMetatags=new Spinner(tweakComp, SWT.BORDER);
			mASSpacesAroundAssignmentInMetatags.setMinimum(0);
			mASSpacesAroundAssignmentInMetatags.setMaximum(10);
			mASSpacesAroundAssignmentInMetatags.addSelectionListener(textUpdater);
			mASSpacesAroundAssignmentInMetatags.setToolTipText("Number of spaces around the '=' for attributes in metatags.");
			
			mASDontIndentPackageElements=new Button(settingsComp, SWT.CHECK);
			mASDontIndentPackageElements.setText("**Don't indent package elements");
			mASDontIndentPackageElements.addSelectionListener(textUpdater);
			mASDontIndentPackageElements.setToolTipText("In Adobe sample code, they don't indent the elements inside a package statement (imports, class declaration, etc.).  This setting allows you to turn on that behavior.");
			
			mASDontIndentSwitchCases=new Button(settingsComp, SWT.CHECK);
			mASDontIndentSwitchCases.setText("**Don't indent switch cases");
			mASDontIndentSwitchCases.addSelectionListener(textUpdater);
			mASDontIndentSwitchCases.setToolTipText("Align the 'case' keyword with the 'switch' keyword.  Otherwise, indent normally.");
			
			mASNoIndentForExpressionTerminatorButton=new Button(settingsComp, SWT.CHECK);
			mASNoIndentForExpressionTerminatorButton.setText("**Don't indent expression terminators");
			mASNoIndentForExpressionTerminatorButton.addSelectionListener(textUpdater);
			mASNoIndentForExpressionTerminatorButton.setToolTipText("If an expression statement or declaration is wrapped, don't indent the last line if it only contains the terminator. (ex. the '});' that ends an inline function defined inside a method call.");

			mASKeepSingleLineFunctions=new Button(settingsComp, SWT.CHECK);
			mASKeepSingleLineFunctions.setText("Keep single-line functions");
			mASKeepSingleLineFunctions.addSelectionListener(textUpdater);
			mASKeepSingleLineFunctions.setToolTipText("If the function is already on a single line (and doesn't contain any code blocks), don't add any carriage returns on that line.");
			
			Group declEqualsGroup=new Group(settingsComp, SWT.None);
			declEqualsGroup.setText("Declaration spacing");
			declEqualsGroup.setLayout(new GridLayout());
			declEqualsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASLeaveExtraWhitespaceAroundVarDecls=new Button(declEqualsGroup, SWT.CHECK);
			mASLeaveExtraWhitespaceAroundVarDecls.setText("Leave extra whitespace around variable declarations");
			mASLeaveExtraWhitespaceAroundVarDecls.addSelectionListener(textUpdater);
			mASLeaveExtraWhitespaceAroundVarDecls.setToolTipText("If checked, don't delete extra whitespace around variable declarations.  This is useful if you like to line up the '=' in some of your declarations.  Tabs are converted to spaces.");
			
			mASAlignDeclEquals=new Button(declEqualsGroup, SWT.CHECK);
			mASAlignDeclEquals.setText("Align '=' for declarations");
			mASAlignDeclEquals.addSelectionListener(textUpdater);
			mASAlignDeclEquals.setToolTipText("If checked, align the equals sign ('=') for variable declarations.  Spaces are used.");
	
			Composite radioComp=new Composite(declEqualsGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginHeight=0;
			radioComp.setLayout(gl);
			
			mASAlignDeclEqualsConsecutive=new Button(radioComp, SWT.RADIO);
			mASAlignDeclEqualsConsecutive.setText("Consecutive");
			mASAlignDeclEqualsConsecutive.addSelectionListener(textUpdater);
			mASAlignDeclEqualsConsecutive.setToolTipText("Align the equals sign ('=') for consecutive variable declarations.  Other statements and directives break up groups of declarations; Comments do not.");

			mASAlignDeclEqualsScope=new Button(radioComp, SWT.RADIO);
			mASAlignDeclEqualsScope.setText("Same scope");
			mASAlignDeclEqualsScope.addSelectionListener(textUpdater);
			mASAlignDeclEqualsScope.setToolTipText("Align the equals sign ('=') for variable declarations in the same scope.");
			
			Group g=new Group(settingsComp, SWT.None);
			g.setText("Pre-formatting options (only applies to format of entire file)");
			g.setLayout(new GridLayout());
			g.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			l=new Label(g, SWT.None);
			l.setText("Braces for loop statements ('Remove' is EXPERIMENTAL)");
			
			Composite c1=new Composite(g, SWT.None);
			gl=new GridLayout(4, false);
			gl.marginHeight=0;
			c1.setLayout(gl);
			c1.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASNoModifyLoopBraces=new Button(c1, SWT.RADIO);
			mASNoModifyLoopBraces.setText("Leave as-is");
			mASNoModifyLoopBraces.addSelectionListener(textUpdater);
			mASNoModifyLoopBraces.setToolTipText("Do not add or remove braces from loop statements.");
			
			mASEnsureLoopBraces=new Button(c1, SWT.RADIO);
			mASEnsureLoopBraces.setText("Add always");
			mASEnsureLoopBraces.addSelectionListener(textUpdater);
			mASEnsureLoopBraces.setToolTipText("If checked, make a block statement for any loops where the current body is only a single statement.");
			
			mASSmartAddLoopBraces=new Button(c1, SWT.RADIO);
			mASSmartAddLoopBraces.setText("Add smart");
			mASSmartAddLoopBraces.addSelectionListener(textUpdater);
			mASSmartAddLoopBraces.setToolTipText("If checked, make a block statement for any loops where the current body is only a single statement, but only if the loop control piece is on a single line.");
			
			mASSmartAddRemoveLoopBraces=new Button(c1, SWT.RADIO);
			mASSmartAddRemoveLoopBraces.setText("Add/remove smart");
			mASSmartAddRemoveLoopBraces.addSelectionListener(textUpdater);
			mASSmartAddRemoveLoopBraces.setToolTipText("If checked, same as 'Add Smart', except that braces will be removed if the loop control and the statement appear simple.");
			
			l=new Label(g, SWT.None);
			l.setText("Braces for conditional statements ('Remove' is EXPERIMENTAL)");
			
			Composite c2=new Composite(g, SWT.None);
			gl=new GridLayout(4, false);
			gl.marginHeight=0;
			c2.setLayout(gl);
			c2.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASNoModifyConditionalBraces=new Button(c2, SWT.RADIO);
			mASNoModifyConditionalBraces.setText("Leave as-is");
			mASNoModifyConditionalBraces.addSelectionListener(textUpdater);
			mASNoModifyConditionalBraces.setToolTipText("Do not add or remove braces from if/else statements.");

			mASEnsureConditionalBraces=new Button(c2, SWT.RADIO);
			mASEnsureConditionalBraces.setText("Add always");
			mASEnsureConditionalBraces.addSelectionListener(textUpdater);
			mASEnsureConditionalBraces.setToolTipText("If checked, make a block statement for any if/else clauses where the current body is only a single statement.");

			mASSmartAddConditionalBraces=new Button(c2, SWT.RADIO);
			mASSmartAddConditionalBraces.setText("Add smart");
			mASSmartAddConditionalBraces.addSelectionListener(textUpdater);
			mASSmartAddConditionalBraces.setToolTipText("If checked, make a block statement for any if/else where the current body is only a single statement, but only if the conditional expression piece is on a single line.");

			mASSmartAddRemoveConditionalBraces=new Button(c2, SWT.RADIO);
			mASSmartAddRemoveConditionalBraces.setText("Add/remove smart");
			mASSmartAddRemoveConditionalBraces.addSelectionListener(textUpdater);
			mASSmartAddRemoveConditionalBraces.setToolTipText("If checked, same as 'Add Smart', except that braces will be removed if the if expression and the statement appear simple.");

			l=new Label(g, SWT.None);
			l.setText("Braces for switch statements");
			
			Composite c3=new Composite(g, SWT.None);
			gl=new GridLayout(4, false);
			gl.marginHeight=0;
			c3.setLayout(gl);
			c3.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASNoModifySwitchBraces=new Button(c3, SWT.RADIO);
			mASNoModifySwitchBraces.setText("Leave as-is");
			mASNoModifySwitchBraces.addSelectionListener(textUpdater);
			mASNoModifySwitchBraces.setToolTipText("Do not add or remove braces from case statements.");

			mASSmartAddSwitchBraces=new Button(c3, SWT.RADIO);
			mASSmartAddSwitchBraces.setText("Add smart");
			mASSmartAddSwitchBraces.addSelectionListener(textUpdater);
			mASSmartAddSwitchBraces.setToolTipText("If checked, make a block statement for most case statements.  No block will be added if the case statement is empty or only a 'break'.");

			mASRemoveSwitchBraces=new Button(c3, SWT.RADIO);
			mASRemoveSwitchBraces.setText("Remove");
			mASRemoveSwitchBraces.addSelectionListener(textUpdater);
			mASRemoveSwitchBraces.setToolTipText("If checked, remove braces around all case statements.");
			
			Group bindableGroup=new Group(settingsComp, SWT.None);
			bindableGroup.setText("Tags eligible to share line with properties");
			bindableGroup.setLayout(new GridLayout());
			bindableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			//edit for property tags
			Composite tableComp=new Composite(bindableGroup, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginHeight=0;
			gl.marginWidth=0;
			tableComp.setLayout(gl);
			tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));

			mSingleLineMetaTagsTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
			gd=new GridData(GridData.FILL_BOTH);
			gd.heightHint=30;
			gd.widthHint=50;
			mSingleLineMetaTagsTable.setLayoutData(gd);
			mSingleLineMetaTagsTable.addSelectionListener(textUpdater);
			mSingleLineMetaTagsTable.setToolTipText("Each item in the table will be placed on the same line with the subsequent property.  There will still be a line break added if the tag is not the last metatag associated with that property.");

			Composite buttonComp=new Composite(tableComp, SWT.None);
			buttonComp.setLayout(new GridLayout());
			mSingleLineMetaTagAddButton=new Button(buttonComp, SWT.PUSH);
			mSingleLineMetaTagAddButton.setText("Add...");
			mSingleLineMetaTagAddButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					InputDialog dlg=new InputDialog(getShell(), "Single-line metatags", "Enter a tag name that can appear on the same line with its associated property", null, new IInputValidator()
					{
						public String isValid(String newText)
						{
							String text=newText.trim();
							boolean inUse=false;
							TableItem[] items=mSingleLineMetaTagsTable.getItems();
							for (TableItem tableItem : items) {
								if (text.equals(tableItem.getText()))
								{
									inUse=true;
									break;
								}
							}

							if (inUse)
								return "Tag already exists in the list box";

							if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
								return "Whitespace in middle of tag name";

//							String error=validateRegex(text);
//							if (error!=null)
//								return error;

							return null;
						}

					});
					if (dlg.open()==Dialog.OK)
					{
						String newTag=dlg.getValue();
						TableItem newItem=new TableItem(mSingleLineMetaTagsTable, SWT.None);
						newItem.setText(newTag);
						reformatText();
						enableWidgets();
					}
				}
			});
			mSingleLineMetaTagRemoveButton=new Button(buttonComp, SWT.PUSH);
			mSingleLineMetaTagRemoveButton.setText("Remove");
			mSingleLineMetaTagRemoveButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					int selIndex=mSingleLineMetaTagsTable.getSelectionIndex();
					if (selIndex>=0)
					{
						mSingleLineMetaTagsTable.remove(selIndex);
						if (mSingleLineMetaTagsTable.getItemCount()>0)
							mSingleLineMetaTagsTable.setSelection(Math.min(selIndex, mSingleLineMetaTagsTable.getItemCount()-1));
						reformatText();
						enableWidgets();
					}
				}
			});
			
			mASUseGnuBraceIndent=new Button(settingsComp, SWT.CHECK);
			mASUseGnuBraceIndent.setText("Use Gnu-style brace indent");
			mASUseGnuBraceIndent.addSelectionListener(textUpdater);
			mASUseGnuBraceIndent.setToolTipText("If checked, indent braces by one tab under control statements and internal statements another tab.");
		}
		
		{
			Composite tweaksComp=new Composite(subTabFolder, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			tweaksComp.setLayout(gl);
			
			TabItem tweaksTab=new TabItem(subTabFolder, SWT.None);
			tweaksTab.setText("Comments");
			
			tweaksTab.setControl(tweaksComp);
			
			Composite settingsComp=new Composite(tweaksComp, SWT.NONE);
			gl=new GridLayout();
			gl.marginHeight=0;
			settingsComp.setLayout(gl);

			Group lineCommentGroup=new Group(settingsComp, SWT.None);
			lineCommentGroup.setText("Comments on the end of a line");
			lineCommentGroup.setLayout(new GridLayout());
			lineCommentGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASKeepSpacesBeforeLineComments=new Button(lineCommentGroup, SWT.RADIO);
			mASKeepSpacesBeforeLineComments.setText("Keep existing whitespace before comments on end of line");
			mASKeepSpacesBeforeLineComments.addSelectionListener(textUpdater);
			mASKeepSpacesBeforeLineComments.setToolTipText("If checked, don't remove the spaces between the end of the code on a line and the subsequent comment.  Applies to // or /* comments after a line of text.");

			mASAddOneSpaceBeforeLineComments=new Button(lineCommentGroup, SWT.RADIO);
			mASAddOneSpaceBeforeLineComments.setText("Add one space before comments on end of line");
			mASAddOneSpaceBeforeLineComments.addSelectionListener(textUpdater);
			mASAddOneSpaceBeforeLineComments.setToolTipText("In this mode, add one space before each comment when it occurs on the end of a line (after some code text).\nApplies to // or /* comments after a line of text.");
			
			mASAlignCommentsAtColumn=new Button(lineCommentGroup, SWT.RADIO);
			mASAlignCommentsAtColumn.setText("Align end-of-line comments on a column");
			mASAlignCommentsAtColumn.addSelectionListener(textUpdater);
			mASAlignCommentsAtColumn.setToolTipText("In this mode, add padding spaces to make the comment start on the specified column when the comment occurs on the end of the line (after code text).\nApplies to // or /* comments after a line of text.");
			
			Composite labelComp=createLabelComp(lineCommentGroup, "Alignment column");
			((GridLayout)labelComp.getLayout()).marginLeft=15;
			mASAlignCommentsColumn=new Spinner(labelComp, SWT.BORDER);
			mASAlignCommentsColumn.setMinimum(30);
			mASAlignCommentsColumn.addSelectionListener(textUpdater);
			mASAlignCommentsColumn.setToolTipText("Column index where comments should start.");
			
			mASWrapLineComments=new Button(settingsComp, SWT.CHECK);
			mASWrapLineComments.setText("Wrap // comments");
			mASWrapLineComments.addSelectionListener(textUpdater);
			mASWrapLineComments.setToolTipText("If checked, keep the relative indent of lines 2 to n of multi-line comments with respect to line 1.");

			mKeepSLCommentsOnColumn1=new Button(settingsComp, SWT.CHECK);
			mKeepSLCommentsOnColumn1.setText("**Don't indent // comments in first column");
			mKeepSLCommentsOnColumn1.addSelectionListener(textUpdater);
			mKeepSLCommentsOnColumn1.setToolTipText("If selected, don't indent line comments that start in column one; otherwise, indent them normally");

			mASKeepRelativeIndentOfMultilineComments=new Button(settingsComp, SWT.CHECK);
			mASKeepRelativeIndentOfMultilineComments.setText("Keep relative indent inside multi-line comments");
			mASKeepRelativeIndentOfMultilineComments.addSelectionListener(textUpdater);
			mASKeepRelativeIndentOfMultilineComments.setToolTipText("If checked, keep the relative indent of lines 2 to n of multi-line comments with respect to line 1.\nThis setting is not compatible with wrapping of /* or ASDoc comments.");
			
			Group docCommentGroup=new Group(settingsComp, SWT.None);
			docCommentGroup.setText("ASDoc Comments");
			docCommentGroup.setLayout(new GridLayout());
			docCommentGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASWrapASDocComments=new Button(docCommentGroup, SWT.CHECK);
			mASWrapASDocComments.setText("Wrap /** comments (Experimental)");
			mASWrapASDocComments.addSelectionListener(textUpdater);
			mASWrapASDocComments.setToolTipText("If checked, attempt to wrap ASDoc comments (/**) based on the max line length setting.");

			mASWrapDocCommentsReflow=new Button(docCommentGroup, SWT.CHECK);
			mASWrapDocCommentsReflow.setText("Reflow lines");
			mASWrapDocCommentsReflow.addSelectionListener(textUpdater);
			mASWrapDocCommentsReflow.setToolTipText("If checked, allow line breaks to be deleted if a line is shorter than necessary based on the max line length.");

			mASWrapDocCommentsKeepBlankLines=new Button(docCommentGroup, SWT.CHECK);
			mASWrapDocCommentsKeepBlankLines.setText("Keep blank lines");
			mASWrapDocCommentsKeepBlankLines.addSelectionListener(textUpdater);
			mASWrapDocCommentsKeepBlankLines.setToolTipText("If checked, don't remove empty lines in the comment body (lines are empty if they only contain an asterisk and whitespace).");

			labelComp=createLabelComp(docCommentGroup, "Hanging tabs");
			mASWrapDocCommentsHangingTabs=new Spinner(labelComp, SWT.BORDER);
			mASWrapDocCommentsHangingTabs.setMinimum(0);
			mASWrapDocCommentsHangingTabs.setMaximum(10);
			mASWrapDocCommentsHangingTabs.addSelectionListener(textUpdater);
			mASWrapDocCommentsHangingTabs.setToolTipText("If >0, indent lines 2..n of each attribute (@).  This allows the attributes to stand out more.");

			Group mlCommentGroup=new Group(settingsComp, SWT.None);
			mlCommentGroup.setText("Multi-line Comments");
			mlCommentGroup.setLayout(new GridLayout());
			mlCommentGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mASWrapMLComments=new Button(mlCommentGroup, SWT.CHECK);
			mASWrapMLComments.setText("Wrap /* comments (Experimental)");
			mASWrapMLComments.addSelectionListener(textUpdater);
			mASWrapMLComments.setToolTipText("If checked, attempt to wrap multi-line comments (/*) based on the max line length setting.");

			mASWrapMLCommentsReflow=new Button(mlCommentGroup, SWT.CHECK);
			mASWrapMLCommentsReflow.setText("Reflow lines");
			mASWrapMLCommentsReflow.addSelectionListener(textUpdater);
			mASWrapMLCommentsReflow.setToolTipText("If checked, allow line breaks to be deleted if a line is shorter than necessary based on the max line length.");

			mASWrapMLCommentsKeepBlankLines=new Button(mlCommentGroup, SWT.CHECK);
			mASWrapMLCommentsKeepBlankLines.setText("Keep blank lines");
			mASWrapMLCommentsKeepBlankLines.addSelectionListener(textUpdater);
			mASWrapMLCommentsKeepBlankLines.setToolTipText("If checked, don't remove empty lines in the comment body (lines are empty if they only contain an asterisk and whitespace).");

			mASWrapMLCommentsSeparateHeader=new Button(mlCommentGroup, SWT.CHECK);
			mASWrapMLCommentsSeparateHeader.setText("/* on separate line");
			mASWrapMLCommentsSeparateHeader.addSelectionListener(textUpdater);
			mASWrapMLCommentsSeparateHeader.setToolTipText("If checked, put the start/end tags of a multi-line comment on their own lines.  That is, if the comment is more than one line, then /* and */ should be on separate lines from the comment body.");

			labelComp=createLabelComp(mlCommentGroup, "Asterisk mode");
			mASWrapMLCommentsAsteriskMode=new Combo(labelComp, SWT.BORDER);
			String text="Keep current asterisk style";
			mASWrapMLCommentsAsteriskMode.add(text, 0);
			mASWrapMLCommentsAsteriskMode.setData(text, ASPrettyPrinter.MLAsteriskStyle_AsIs);
			text="Asterisks at line start";
			mASWrapMLCommentsAsteriskMode.add(text, 1);
			mASWrapMLCommentsAsteriskMode.setData(text, ASPrettyPrinter.MLAsteriskStyle_All);
			text="No asterisks at line start";
			mASWrapMLCommentsAsteriskMode.add(text, 2);
			mASWrapMLCommentsAsteriskMode.setData(text, ASPrettyPrinter.MLAsteriskStyle_None);
			mASWrapMLCommentsAsteriskMode.addSelectionListener(textUpdater);
			mASWrapMLCommentsAsteriskMode.setToolTipText("This controls whether /* comments have lines starting with asterisks.  Generally, you want to choose the setting for always add or always remove.  But there is a third setting for 'try to keep the style the same as current'.");
			
		}
		
//		{
//			Composite rearrangeComp=new Composite(subTabFolder, SWT.NONE);
//			rearrangeComp.setLayout(new GridLayout());
//			
//			mRearrangeTab=new TabItem(subTabFolder, SWT.None);
//			mRearrangeTab.setText("Rearranging");
//			
//			mRearrangeTab.setControl(rearrangeComp);
//			
//			Composite settingsComp=new Composite(rearrangeComp, SWT.NONE);
//			settingsComp.setLayout(new GridLayout());
//			
//			Composite comp=new Composite(settingsComp, SWT.None);
//			GridLayout gl=new GridLayout(1, false);
//			gl.marginWidth=0;
//			comp.setLayout(gl);
//			
//			mASRearrangeDuringFormatting=new Button(comp, SWT.CHECK);
//			mASRearrangeDuringFormatting.setText("Rearrange while formatting");
////			mASRearrangeDuringFormatting.addSelectionListener(textUpdater);
//			mASRearrangeDuringFormatting.setToolTipText("If this box is not checked, code rearranging will not be done as part of normal formatting.  Rearranging can still be invoked separately.");
////			mASRearrangeDuringFormatting.setEnabled(false);
//
//			try {
//				createRearrangingWidgets(comp);
//			} catch (Exception e1) {
//				Activator.logException(e1, "");
//			}
//		}
		
		Composite editorComp=new Composite(mainASComp, SWT.None);
		gl=new GridLayout();
		gl.marginHeight=0;
		editorComp.setLayout(gl);
		editorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Button allowUserASText=new Button(editorComp, SWT.CHECK);
		allowUserASText.setText("Allow custom text");
		allowUserASText.setToolTipText("If checked, you can edit the text and it will be reformatted on the next settings change.\nNOTE: you may get errors if you introduce syntax errors.");
		allowUserASText.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				mSampleASText.setEditable(allowUserASText.getSelection());
			}
		});
		
		mSampleASText=createCodeArea(editorComp);
		mASLineStyleListener=new ASLineStyleListener(mSampleASText);
		mSampleASText.addLineStyleListener(mASLineStyleListener);
		mOriginalASSampleText=loadSampleText("flexprettyprint/preferences/sample.as");
		mSampleASText.setText(mOriginalASSampleText);
		
		return mainASComp;
	}
	
	private void createMXMLRearrangingTab(TabFolder folder)
	{
		Composite settingsComp=new Composite(folder, SWT.NONE);
		settingsComp.setLayout(new GridLayout());
		settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem mxmlReTab=new TabItem(folder, SWT.None);
		mxmlReTab.setText("MXML Rearranging");
		
		mxmlReTab.setControl(settingsComp);
		
		final SelectionListener textUpdater=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableWidgets();
				updateMXMLRearrangeText();
			}
		};
		
		
		mMXMLRearrangeWhileFormatting=new Button(settingsComp, SWT.CHECK);
		mMXMLRearrangeWhileFormatting.setText("Rearrange while formatting");
		mMXMLRearrangeWhileFormatting.setToolTipText("If checked, attempt to perform MXML rearranging as part of formating.\nOtherwise, you can use the rearrange button to invoke MXML rearranging.");
		mMXMLRearrangeWhileFormatting.addSelectionListener(textUpdater);
		
		mUseMXMLTagOrdering=new Button(settingsComp, SWT.CHECK);
		mUseMXMLTagOrdering.setText("Use tag ordering");
		mUseMXMLTagOrdering.setToolTipText("If checked, use the list to order the main tags (those under the root tag).");
		mUseMXMLTagOrdering.addSelectionListener(textUpdater);
		
		Label l=new Label(settingsComp, SWT.None);
		l.setText("Order of top-level MXML tags");
		
		GridLayout gl;
		Composite mainRearrangeComp=new Composite(settingsComp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginHeight=0;
		mainRearrangeComp.setLayout(gl);
		mainRearrangeComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite tableComp=new Composite(mainRearrangeComp, SWT.None);
		tableComp.setLayout(new GridLayout(2, false));
		tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite editorComp=new Composite(mainRearrangeComp, SWT.None);
		gl=new GridLayout();
		gl.marginHeight=0;
		editorComp.setLayout(gl);
		editorComp.setLayoutData(new GridData(GridData.FILL_BOTH));		
		mSampleMXMLRearrangingText=createCodeArea(editorComp);
		mOriginalMXMLRearrangingSampleText=loadSampleText("flexprettyprint/preferences/rearrangeSample.mxml");
		mMXMLRearrangeLineStyleListener=new MXMLLineStyleListener(mSampleMXMLRearrangingText);
		mSampleMXMLRearrangingText.addLineStyleListener(mMXMLRearrangeLineStyleListener);
		mSampleMXMLRearrangingText.setText(mOriginalMXMLRearrangingSampleText);
		
		mMXMLOrderTable=new Table(tableComp, SWT.BORDER);
		GridData gd=new GridData(GridData.FILL_BOTH);
		gd.heightHint=mMXMLOrderTable.getItemHeight()*10;
		mMXMLOrderTable.setLayoutData(gd);
		mMXMLOrderTable.addSelectionListener(textUpdater);
		mMXMLOrderTable.setToolTipText("Items in this table control the order of mxml tags.");
		
		Composite buttonComp=new Composite(tableComp, SWT.None);
		buttonComp.setLayout(new GridLayout());
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		mMXMLOrderAddButton=new Button(buttonComp, SWT.PUSH);
		mMXMLOrderAddButton.setText("Add...");
		mMXMLOrderAddButton.setToolTipText("Add a new tag to the list.");
		mMXMLOrderAddButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				InputDialog dlg=new InputDialog(getShell(), "Top level tag", "Enter a tag name to use as a top-level tag.\nUse Java regex syntax to match multiple tag names.\nExamples: mx:List OR .* OR .*:List", null, new IInputValidator()
				{
					public String isValid(String newText)
					{
						String text=newText.trim();
						boolean inUse=false;
						TableItem[] items=mMXMLOrderTable.getItems();
						for (TableItem tableItem : items) {
							if (text.equals(tableItem.getText()))
							{
								inUse=true;
								break;
							}
						}

						if (inUse)
							return "Tag already exists in the list box";

						if (text.indexOf(' ')>=0 || text.indexOf('\t')>=0)
							return "Whitespace in middle of tag name";

						String error=validateRegex(text);
						if (error!=null)
							return error;

						return null;
					}

				});
				if (dlg.open()==Dialog.OK)
				{
					String newTag=dlg.getValue();
					TableItem newItem=new TableItem(mMXMLOrderTable, SWT.None);
					newItem.setText(newTag);
					newItem.setData(new ImportHolder(newTag));
					updateMXMLRearrangeText();
					enableWidgets();
				}
			}
		});
		
		mMXMLOrderDeleteButton=new Button(buttonComp, SWT.PUSH);
		mMXMLOrderDeleteButton.setText("Delete");
		mMXMLOrderDeleteButton.setToolTipText("Delete the selected tag from the list");
		mMXMLOrderDeleteButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TableItem[] selItems=mMXMLOrderTable.getSelection();
				int selIndex=mMXMLOrderTable.getSelectionIndex();
				if (selItems.length>0)
				{
					selItems[0].dispose();
					updateRearrangeText();
					if (mMXMLOrderTable.getItemCount()>0)
					{
						mMXMLOrderTable.setSelection(Math.min(mMXMLOrderTable.getItemCount()-1, selIndex));
						updateMXMLRearrangeText();
					}
				}
			}
		});
//		
//		mMXMLOrderEditButton=new Button(buttonComp, SWT.PUSH);
//		mMXMLOrderEditButton.setText("Edit...");

		mMXMLOrderUpButton=new Button(buttonComp, SWT.PUSH);
		mMXMLOrderUpButton.setText("Move up");
		mMXMLOrderUpButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveUpHelper(mMXMLOrderTable);
				updateMXMLRearrangeText();
			}
		});
		
		mMXMLOrderDownButton=new Button(buttonComp, SWT.PUSH);
		mMXMLOrderDownButton.setText("Move down");
		mMXMLOrderDownButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveDownHelper(mMXMLOrderTable);
				updateMXMLRearrangeText();
			}
		});
	
		mMXMLRestoreDefaultsButton=new Button(tableComp, SWT.PUSH);
		mMXMLRestoreDefaultsButton.setText("Restore default tag list");
		mMXMLRestoreDefaultsButton.setToolTipText("Replace the contents of the table with the default tags.");
		mMXMLRestoreDefaultsButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				IPreferenceStore store=Activator.getDefault().getPreferenceStore();
				addItemsToSimpleOrderingTable(store.getDefaultString(PreferenceConstants.MXMLRearr_RearrangeTagOrdering), mMXMLOrderTable);
				updateMXMLRearrangeText();
			}
		});
		
	}

	private Composite createRearrangingWidgetsTab(TabFolder folder)
	{
		Composite settingsComp=new Composite(folder, SWT.NONE);
		GridLayout gl=new GridLayout();
		gl.marginHeight=0;
		settingsComp.setLayout(gl);
		settingsComp.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		mASRearrangeDuringFormatting=new Button(settingsComp, SWT.CHECK);
		mASRearrangeDuringFormatting.setText("Rearrange while formatting");
		mASRearrangeDuringFormatting.setToolTipText("If this box is not checked, code rearranging will not be done as part of normal formatting.  Rearranging can still be invoked separately.");

		Composite mainRearrangeComp=new Composite(settingsComp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginHeight=0;
		mainRearrangeComp.setLayout(gl);
		mainRearrangeComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabFolder tabFolder=new TabFolder(mainRearrangeComp, SWT.None);
		GridData gd=new GridData(GridData.FILL_VERTICAL); //GridData.FILL_BOTH);
		tabFolder.setLayoutData(gd);
		
		Composite editorComp=new Composite(mainRearrangeComp, SWT.None);
		gl=new GridLayout();
		gl.marginHeight=0;
		editorComp.setLayout(gl);
		editorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Button allowUserASText=new Button(editorComp, SWT.CHECK);
		allowUserASText.setText("Allow custom text");
		allowUserASText.setToolTipText("If checked, you can edit the text and it will be reformatted on the next settings change.\nNOTE: you may get errors if you introduce syntax errors.");
		allowUserASText.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				mSampleRearrangingText.setEditable(allowUserASText.getSelection());
			}
		});
		
		mSampleRearrangingText=createCodeArea(editorComp);
		mOriginalASRearrangingSampleText=loadSampleText("flexasrearrangecodecommand/preferences/sample.as");
		mRearrangeLineStyleListener=new ASLineStyleListener(mSampleRearrangingText);
		mSampleRearrangingText.addLineStyleListener(mRearrangeLineStyleListener);
		mSampleRearrangingText.setText(mOriginalASRearrangingSampleText);
		
		TabItem modifierTab=new TabItem(tabFolder, SWT.None);
		modifierTab.setText("Modifiers");
		
		TabItem elementOrderTab=new TabItem(tabFolder, SWT.None);
		elementOrderTab.setText("Elements");
		
		TabItem headerTab=new TabItem(tabFolder, SWT.None);
		headerTab.setText("Spans/Headers");
		
		TabItem copyrightTab=new TabItem(tabFolder, SWT.None);
		copyrightTab.setText("Copyright");
		
		final SelectionListener enableUpdater=new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableWidgets();
				updateRearrangeText();
			}
		};
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////// Modifier ordering ///////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		Composite modComp=new Composite(tabFolder, SWT.None);
		gl=new GridLayout();
		gl.marginHeight=0;
		modComp.setLayout(gl);
		modComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		modifierTab.setControl(modComp);
		
		Composite typeComp=new Composite(modComp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		typeComp.setLayout(gl);
		typeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mUseGlobalOrderButton=new Button(typeComp, SWT.CHECK);
		mUseGlobalOrderButton.setText("Use same modifier order for all element types");
		mUseGlobalOrderButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				saveSettingsForCombo(mCurrentType);
				mTypeCombo.select(0);
				mCurrentType=Settings_Function;
				updateSettingsForCombo();
				enableWidgets();
				updateRearrangeText();
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
				enableWidgets();
				updateRearrangeText();
			}
			
		});
		mTypeCombo.select(0);
		
		mUseModifierOrder=new Button(modComp, SWT.CHECK);
		mUseModifierOrder.setText("Enable Modifier Reordering");
		mUseModifierOrder.addSelectionListener(enableUpdater);
		
		Composite tableComp=new Composite(modComp, SWT.None);
		gl=new GridLayout(2, false);
		gl.marginHeight=0;
		tableComp.setLayout(gl);
		tableComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		mModifierTable=new Table(tableComp, SWT.BORDER | SWT.SINGLE);
		gd=new GridData();
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
				enableWidgets();
				updateRearrangeText();
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
				enableWidgets();
				updateRearrangeText();
			}
		});
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////// Element ordering ///////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		{
			Composite elementComp=new Composite(tabFolder, SWT.None);
			elementComp.setLayout(new GridLayout());
			elementComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			elementOrderTab.setControl(elementComp);
			
			mUseElementOrder=new Button(elementComp, SWT.CHECK);
			mUseElementOrder.setText("Use element ordering");
			mUseElementOrder.addSelectionListener(enableUpdater);
			
			Label l=new Label(elementComp, SWT.None);
			l.setText("Choose element order");
			
			Composite dualComp=new Composite(elementComp, SWT.None);
			dualComp.setLayout(new GridLayout(2, false));
			dualComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mElementOrderTable=new Table(dualComp, SWT.BORDER | SWT.SINGLE);
			gd=new GridData(GridData.FILL_BOTH);
			gd.heightHint=mElementOrderTable.getItemHeight()*5;
			GC gc=new GC(mElementOrderTable);
			gd.widthHint=gc.getCharWidth('w')*18;
			mElementOrderTable.setLayoutData(gd);
			mElementOrderTable.addSelectionListener(enableUpdater);
			mElementOrderTable.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length>0)
					{
						Object item=mElementTypeTabFolder.getData(selItems[0].getText());
						if (item!=null && (item instanceof TabItem))
						{
							mElementTypeTabFolder.setSelection((TabItem)item);
						}
						
						Object o=selItems[0].getData();
						if (o instanceof MemberSelectionSpec)
							mElementBlankLinesCombo.select(((MemberSelectionSpec)o).getBlankLinesBefore());
						else
						{
							int blankLines=1;
							Integer mapValue=mBlankLinesMap.get(((ElementHolder)o).getReferenceID());
							if (mapValue!=null)
								blankLines=mapValue.intValue();
							mElementBlankLinesCombo.select(blankLines);
						}
					}
					updateSectionInfoText();
				}
			});

			Composite tableButtonComp=new Composite(dualComp, SWT.None);
			gl=new GridLayout(1, true);
			gl.marginHeight=0;
			tableButtonComp.setLayout(gl);
			tableButtonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			
			mElementNewButton=new Button(tableButtonComp, SWT.PUSH);
			mElementNewButton.setText("New Member selector...");
			mElementNewButton.setToolTipText("Create a new filter to select a slice of functions or properties");
			mElementNewButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					SelectFieldsOrFunctionsDialog dlg=new SelectFieldsOrFunctionsDialog(getShell(), null);
					if (dlg.open()==Dialog.OK)
					{
						MemberSelectionSpec selSpec=dlg.getSelectionSpec();
						TableItem item=new TableItem(mElementOrderTable, SWT.None);
						item.setData(selSpec);
						
						if (selSpec.getPreselectPriority()>0)
						{
							List<MemberSelectionSpec> specs=getPreselectPriorityItems();
							selSpec.setPreselectPriority(specs.size());
						}
						
						updateItemText(item);
						updateRearrangeText();
						mElementOrderTable.select(mElementOrderTable.getItemCount()-1);
						enableWidgets();
					}
				}
			});
			
			Composite editCombinationComp=new Composite(tableButtonComp, SWT.None);
			gl=new GridLayout(3, false);
			gl.marginWidth=0;
			editCombinationComp.setLayout(gl);
			mElementEditButton=new Button(editCombinationComp, SWT.PUSH);
			mElementEditButton.setText("Edit...");
			mElementEditButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1 && (selItems[0].getData() instanceof MemberSelectionSpec))
					{
						SelectFieldsOrFunctionsDialog dlg=new SelectFieldsOrFunctionsDialog(getShell(), (MemberSelectionSpec)selItems[0].getData());
						if (dlg.open()==Dialog.OK)
						{
							MemberSelectionSpec oldFilter=(MemberSelectionSpec)selItems[0].getData();
							MemberSelectionSpec selSpec=dlg.getSelectionSpec();
							SectionSpec oldSpec=mHeaderSpecs.remove(((MemberSelectionSpec)selItems[0].getData()).persist());
							if (oldSpec!=null)
								mHeaderSpecs.put(selSpec.persist(), oldSpec);
							selItems[0].setData(selSpec);
							
							//if the prefilter was off and turned on or the reverse, then we need to redo the numbering
							if (oldFilter.getPreselectPriority()!=selSpec.getPreselectPriority())
							{
								if (selSpec.getPreselectPriority()>0)
									selSpec.setPreselectPriority(Integer.MAX_VALUE); //make it a number beyond the end of the ones already defined. 
								List<MemberSelectionSpec> specs=getPreselectPriorityItems();
								reflowPrefilterPriorities(specs);
								updatePrintStringsInOrderTable(mElementOrderTable);
							}
							
							updateItemText(selItems[0]);
							enableWidgets();
							updateRearrangeText();
						}
					}
				}
			});

			mElementSectionSpanEditButton=new Button(editCombinationComp, SWT.PUSH);
			mElementSectionSpanEditButton.setText("Span...");
			mElementSectionSpanEditButton.setToolTipText("Configure the spanning section header starting with the selected element");
			mElementSectionSpanEditButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1)
					{
						Object data=selItems[0].getData();
						
						//get the section header id based on the object type from the table
						String sectionID=((ISectionItem)data).getReferenceID()+ASRearranger.SpanningSuffix;
						editSectionHeader(sectionID);
						updatePrintStringsInOrderTable(mElementOrderTable);
					}
				}

			});

			mElementSectionEditButton=new Button(editCombinationComp, SWT.PUSH);
			mElementSectionEditButton.setText("Header...");
			mElementSectionEditButton.setToolTipText("Configure the section header for the selected element");
			mElementSectionEditButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1)
					{
						Object data=selItems[0].getData();
						
						//get the section header id based on the object type from the table
						String sectionID=((ISectionItem)data).getReferenceID();
						editSectionHeader(sectionID);
						updatePrintStringsInOrderTable(mElementOrderTable);
					}
				}

			});
			
			Composite lbcomp=new Composite(tableButtonComp, SWT.None);
			gl=new GridLayout(2, false);
			gl.marginWidth=0;
			gl.marginHeight=0;
			lbcomp.setLayout(gl);
			
			l=new Label(lbcomp, SWT.None);
			l.setText("Lines before");
			
			mElementBlankLinesCombo=new Combo(lbcomp, SWT.READ_ONLY);
			mElementBlankLinesCombo.setToolTipText("Set the number of blank lines before the first item of this element type.\nIgnored if there is a header comment added before the first item.");
			mElementBlankLinesCombo.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1)
					{
						Object o=selItems[0].getData();
						int blankLines=((Integer)mElementBlankLinesCombo.getData(mElementBlankLinesCombo.getText())).intValue();
						if (o instanceof MemberSelectionSpec)
						{
							((MemberSelectionSpec)o).setBlankLinesBefore(blankLines);
						}
						else
						{
							mBlankLinesMap.put(((ElementHolder)o).getReferenceID(), blankLines);
						}
						updateRearrangeText();
					}					
				}
			});
			for (int i=0;i<5;i++)
			{
				String printString=Integer.toString(i);
				mElementBlankLinesCombo.add(printString);
				mElementBlankLinesCombo.setData(printString, i);
			}

			mElementDeleteButton=new Button(tableButtonComp, SWT.PUSH);
			mElementDeleteButton.setText("Delete");
			mElementDeleteButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1 && (selItems[0].getData() instanceof MemberSelectionSpec))
					{
						MemberSelectionSpec selSpec=(MemberSelectionSpec)selItems[0].getData();
						int selIndex=mElementOrderTable.getSelectionIndex();
						selItems[0].dispose();
						if (mElementOrderTable.getItemCount()>0)
						{
							mElementOrderTable.setSelection(Math.min(mElementOrderTable.getItemCount()-1, selIndex));
						}
						updateRearrangeText();
						
						if (selSpec.getPreselectPriority()>0)
						{
							List<MemberSelectionSpec> specs=getPreselectPriorityItems();
							reflowPrefilterPriorities(specs);
						}
						
						updatePrintStringsInOrderTable(mElementOrderTable);
					}					
				}
			});

			Composite updownComp=new Composite(tableButtonComp, SWT.None);
			gl=new GridLayout(4, false);
			gl.marginWidth=0;
			updownComp.setLayout(gl);
		
			mElementUpButton=new Button(updownComp, SWT.PUSH);
			mElementUpButton.setText("Up");
			mElementUpButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					moveUpHelper(mElementOrderTable);
					updatePrintStringsInOrderTable(mElementOrderTable);
				}
			});

			mElementDownButton=new Button(updownComp, SWT.PUSH);
			mElementDownButton.setText("Down");
			mElementDownButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					moveDownHelper(mElementOrderTable);
					updatePrintStringsInOrderTable(mElementOrderTable);
				}
			});
			
			mElementPriorityUpButton=new Button(updownComp, SWT.PUSH);
			mElementPriorityUpButton.setText("Up*");
			mElementPriorityUpButton.setToolTipText("Adjust the priority of member selectors that are pre-filtering the members");
			mElementPriorityUpButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1 && (selItems[0].getData() instanceof MemberSelectionSpec))
					{
						MemberSelectionSpec targetSpec=(MemberSelectionSpec)selItems[0].getData();
						int index=targetSpec.getPreselectPriority();
						List<MemberSelectionSpec> members=getPreselectPriorityItems();
						for (MemberSelectionSpec spec : members) {
							if (spec.getPreselectPriority()==index-1)
							{
								spec.setPreselectPriority(index);
								targetSpec.setPreselectPriority(index-1);
								updatePrintStringsInOrderTable(mElementOrderTable);
								enableWidgets();
								break;
							}
						}
					}
				}
			});

			mElementPriorityDownButton=new Button(updownComp, SWT.PUSH);
			mElementPriorityDownButton.setText("Down*");
			mElementPriorityDownButton.setToolTipText("Adjust the priority of member selectors that are pre-filtering the members");
			mElementPriorityDownButton.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					TableItem[] selItems=mElementOrderTable.getSelection();
					if (selItems.length==1 && (selItems[0].getData() instanceof MemberSelectionSpec))
					{
						MemberSelectionSpec targetSpec=(MemberSelectionSpec)selItems[0].getData();
						int index=targetSpec.getPreselectPriority();
						List<MemberSelectionSpec> members=getPreselectPriorityItems();
						for (MemberSelectionSpec spec : members) {
							if (spec.getPreselectPriority()==index+1)
							{
								spec.setPreselectPriority(index);
								targetSpec.setPreselectPriority(index+1);
								updatePrintStringsInOrderTable(mElementOrderTable);
								enableWidgets();
								break;
							}
						}
					}
				}
			});
			

			mSpanInfoText=new Text(elementComp, SWT.READ_ONLY);
			mSpanInfoText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			mElementTypeTabFolder=new TabFolder(elementComp, SWT.None);
			gd=new GridData(GridData.FILL_BOTH);
			mElementTypeTabFolder.setLayoutData(gd);
			
			TabItem includeTab=new TabItem(mElementTypeTabFolder, SWT.None);
			includeTab.setText("Includes");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_Include, includeTab);

			TabItem functionTab=new TabItem(mElementTypeTabFolder, SWT.None);
			functionTab.setText("Funcs");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_Function, functionTab);

			TabItem propertyTab=new TabItem(mElementTypeTabFolder, SWT.None);
			propertyTab.setText("Props");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_Property, propertyTab);

			TabItem staticFunctionTab=new TabItem(mElementTypeTabFolder, SWT.None);
			staticFunctionTab.setText("Funcs (S)");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_StaticFunction, staticFunctionTab);

			TabItem staticPropertyTab=new TabItem(mElementTypeTabFolder, SWT.None);
			staticPropertyTab.setText("Props (S)");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_StaticProperty, staticPropertyTab);

			TabItem namespaceTab=new TabItem(mElementTypeTabFolder, SWT.None);
			namespaceTab.setText("Namespaces");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_DefineNamespace, namespaceTab);
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_DefaultNamespace, namespaceTab);
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_UseNamespace, namespaceTab);

			TabItem metatagTab=new TabItem(mElementTypeTabFolder, SWT.None);
			metatagTab.setText("Metatags");

			TabItem importTab=new TabItem(mElementTypeTabFolder, SWT.None);
			importTab.setText("Imports");
			mElementTypeTabFolder.setData(PreferenceConstants.ASRearr_Element_Import, importTab);

//			TabItem defaultnsTab=new TabItem(elementTypeFolder, SWT.None);
//			defaultnsTab.setText("Default XML Namespace");

			{
				Composite aComp=new Composite(mElementTypeTabFolder, SWT.None);
				aComp.setLayout(new GridLayout());
				aComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				includeTab.setControl(aComp);
				
				mIncludeSort=new Button(aComp, SWT.CHECK);
				mIncludeSort.setText("Sort by include path");
				mIncludeSort.addSelectionListener(enableUpdater);
			}

			{
				Composite aComp=new Composite(mElementTypeTabFolder, SWT.None);
				aComp.setLayout(new GridLayout());
				aComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				namespaceTab.setControl(aComp);
				
				mNamespaceSort=new Button(aComp, SWT.CHECK);
				mNamespaceSort.setText("Sort by name");
				mNamespaceSort.addSelectionListener(enableUpdater);
			}

			{
				Composite aComp=new Composite(mElementTypeTabFolder, SWT.None);
				aComp.setLayout(new GridLayout());
				aComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				importTab.setControl(aComp);
				
				mImportMoveOut=new Button(aComp, SWT.CHECK);
				mImportMoveOut.setText("Move all imports outside class");
				mImportMoveOut.addSelectionListener(enableUpdater);
				
				mImportEnableOrdering=new Button(aComp, SWT.CHECK);
				mImportEnableOrdering.setText("Group according to table");
				mImportEnableOrdering.addSelectionListener(enableUpdater);
				
				Composite importTableComp=new Composite(aComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				importTableComp.setLayout(gl);
				importTableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				mImportTable=new Table(importTableComp, SWT.SINGLE | SWT.BORDER);
				mImportTable.setLayoutData(new GridData(GridData.FILL_BOTH));
				mImportTable.addSelectionListener(enableUpdater);
				
				buttonComp=new Composite(importTableComp, SWT.None);
				gl=new GridLayout(1, true);
				gl.marginHeight=0;
				buttonComp.setLayout(gl);
				buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				
				mImportUpButton=new Button(buttonComp, SWT.PUSH);
				mImportUpButton.setText("Up");
				mImportUpButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						moveUpHelper(mImportTable);
					}
				});
				
				mImportDownButton=new Button(buttonComp, SWT.PUSH);
				mImportDownButton.setText("Down");
				mImportDownButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						moveDownHelper(mImportTable);
					}
				});
				
				Composite newComp=new Composite(buttonComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginWidth=0;
				gl.marginHeight=0;
				newComp.setLayout(gl);
				
				mImportNewButton=new Button(newComp, SWT.PUSH);
				mImportNewButton.setText("New...");
				mImportNewButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						InputDialog dlg=new InputDialog(getShell(), "New Import", "Enter the import prefix (ex. com.adobe.)", null, new ImportValidator());
						if (dlg.open()==Dialog.OK)
						{
							TableItem item=new TableItem(mImportTable, SWT.None);
							item.setData(dlg.getValue());
							updateItemText(item);
							updateRearrangeText();
						}
					}
				});
				
				mImportNewSeparatorButton=new Button(newComp, SWT.PUSH);
				mImportNewSeparatorButton.setText("New Line");
				mImportNewSeparatorButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						TableItem item=new TableItem(mImportTable, SWT.None);
						item.setData(PreferenceConstants.ASRearr_ImportSeparator);
						updateItemText(item);
						updateRearrangeText();
					}
				});
				
				Composite editComp=new Composite(buttonComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				gl.marginWidth=0;
				editComp.setLayout(gl);
				
				mImportEditButton=new Button(editComp, SWT.PUSH);
				mImportEditButton.setText("Edit...");
				mImportEditButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						TableItem[] selItems=mImportTable.getSelection();
						if (selItems.length>0)
						{
							InputDialog dlg=new InputDialog(getShell(), "Edit Import", "Enter the import prefix (ex. com.adobe.)", selItems[0].getText(), new ImportValidator());
							if (dlg.open()==Dialog.OK)
							{
								selItems[0].setData(dlg.getValue());
								updateItemText(selItems[0]);
								updateRearrangeText();
							}
						}
					}
				});
				
				mImportDeleteButton=new Button(editComp, SWT.PUSH);
				mImportDeleteButton.setText("Delete");
				mImportDeleteButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						TableItem[] selItems=mImportTable.getSelection();
						int selIndex=mImportTable.getSelectionIndex();
						if (selItems.length>0)
						{
							selItems[0].dispose();
							updateRearrangeText();
							if (mImportTable.getItemCount()>0)
							{
								mImportTable.setSelection(Math.min(mImportTable.getItemCount()-1, selIndex));
							}
						}
					}
				});

				mImportSort=new Button(aComp, SWT.CHECK);
				mImportSort.setText("Sort by name within group");
				mImportSort.addSelectionListener(enableUpdater);
			}

			try
			{
				createMemberWidgets(mElementTypeTabFolder, functionTab, PreferenceConstants.ASRearr_Element_Function, enableUpdater, "These settings apply to the 'Function' group", "mFunctionUseSortOrder", "mFunctionOrderTable", "mFunctionUpButton", "mFunctionDownButton", "mFunctionSectionEditButton", "mFunctionSort", null, null, null);
				createMemberWidgets(mElementTypeTabFolder, staticFunctionTab, PreferenceConstants.ASRearr_Element_StaticFunction, enableUpdater, "These settings apply to the 'Static Function' group", "mStaticFunctionUseSortOrder", "mStaticFunctionOrderTable", "mStaticFunctionUpButton", "mStaticFunctionDownButton", "mStaticFunctionSectionEditButton", "mStaticFunctionSort", null, null, null);
				createMemberWidgets(mElementTypeTabFolder, propertyTab, PreferenceConstants.ASRearr_Element_Property, enableUpdater, "These settings apply to the 'Property' group", "mPropertyUseSortOrder", "mPropertyOrderTable", "mPropertyUpButton", "mPropertyDownButton", "mPropertySectionEditButton", "mPropertySort", "mPropertyGrabGettersButton", "mPropertyAssociateGettersButton", "mPropertyGettersHeaderButton");
				createMemberWidgets(mElementTypeTabFolder, staticPropertyTab, PreferenceConstants.ASRearr_Element_StaticProperty, enableUpdater, "These settings apply to the 'Static Property' group", "mStaticPropertyUseSortOrder", "mStaticPropertyOrderTable", "mStaticPropertyUpButton", "mStaticPropertyDownButton", "mStaticPropertySectionEditButton", "mStaticPropertySort", "mStaticPropertyGrabGettersButton", "mStaticPropertyAssociateGettersButton", "mStaticPropertyGettersHeaderButton");
				
				mPropertyGettersHeaderButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						PropertyHeaderDlg dlg=new PropertyHeaderDlg(getShell(), mPropertyHeadersStyle, mPropertyAssociateGettersButton.getSelection());
						if (dlg.open()==Dialog.OK)
						{
							mPropertyHeadersStyle=dlg.getStyle();
							enableUpdater.widgetSelected(null);
						}
					}
				});
					
				mStaticPropertyGettersHeaderButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						PropertyHeaderDlg dlg=new PropertyHeaderDlg(getShell(), mStaticPropertyHeadersStyle, mStaticPropertyAssociateGettersButton.getSelection());
						if (dlg.open()==Dialog.OK)
						{
							mStaticPropertyHeadersStyle=dlg.getStyle();
							enableUpdater.widgetSelected(null);
						}
					}
				});
			}
			catch (Exception e)
			{
				Activator.logException(e, "");
			}
				
			{
				Composite aComp=new Composite(mElementTypeTabFolder, SWT.None);
				gl=new GridLayout();
				gl.marginHeight=0;
				aComp.setLayout(gl);
				aComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				metatagTab.setControl(aComp);
				
				mMetatagUseSortOrder=new Button(aComp, SWT.CHECK);
				mMetatagUseSortOrder.setText("Group according to table");
				mMetatagUseSortOrder.addSelectionListener(enableUpdater);
				
				Composite orderComp=new Composite(aComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				orderComp.setLayout(gl);
				orderComp.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				mMetatagOrderTable=new Table(orderComp, SWT.SINGLE | SWT.BORDER);
				gd=new GridData(GridData.FILL_BOTH);
				gd.heightHint=mMetatagOrderTable.getItemHeight()*5;
				mMetatagOrderTable.setLayoutData(gd);
				mMetatagOrderTable.addSelectionListener(enableUpdater);
				
				buttonComp=new Composite(orderComp, SWT.None);
				gl=new GridLayout(1, true);
				gl.marginHeight=0;
				buttonComp.setLayout(gl);
				buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
				
				mMetatagUpButton=new Button(buttonComp, SWT.PUSH);
				mMetatagUpButton.setText("Up");
				mMetatagUpButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						moveUpHelper(mMetatagOrderTable);
					}
				});
				
				
				mMetatagDownButton=new Button(buttonComp, SWT.PUSH);
				mMetatagDownButton.setText("Down");
				mMetatagDownButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						moveDownHelper(mMetatagOrderTable);
					}
				});
				
				mMetatagNewButton=new Button(buttonComp, SWT.PUSH);
				mMetatagNewButton.setText("New...");
				mMetatagNewButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						InputDialog dlg=new InputDialog(getShell(), "New Metatag", "Enter the metatag name (ex. ArrayElement)", null, new MetatagValidator());
						if (dlg.open()==Dialog.OK)
						{
							TableItem item=new TableItem(mMetatagOrderTable, SWT.None);
							item.setData(dlg.getValue());
							updateItemText(item);
							updateRearrangeText();
						}
					}
				});
				
				
				Composite editComp=new Composite(buttonComp, SWT.None);
				gl=new GridLayout(2, false);
				gl.marginHeight=0;
				gl.marginWidth=0;
				editComp.setLayout(gl);

				mMetatagEditButton=new Button(editComp, SWT.PUSH);
				mMetatagEditButton.setText("Edit...");
				mMetatagEditButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						TableItem[] selItems=mMetatagOrderTable.getSelection();
						if (selItems.length>0)
						{
							InputDialog dlg=new InputDialog(getShell(), "Edit Metatag", "Enter the metatag name (ex. ArrayElement)", selItems[0].getText(), new MetatagValidator());
							if (dlg.open()==Dialog.OK)
							{
								selItems[0].setData(dlg.getValue());
								updateItemText(selItems[0]);
								updateRearrangeText();
							}
						}
					}
				});

				mMetatagDeleteButton=new Button(editComp, SWT.PUSH);
				mMetatagDeleteButton.setText("Delete");
				mMetatagDeleteButton.addSelectionListener(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						TableItem[] selItems=mMetatagOrderTable.getSelection();
						if (selItems.length>0)
						{
							int oldSelIndex=mMetatagOrderTable.getSelectionIndex();
							mMetatagOrderTable.remove(mMetatagOrderTable.getSelectionIndex());
							mMetatagOrderTable.redraw();
							updateRearrangeText();
							if (mMetatagOrderTable.getItemCount()>0)
								mMetatagOrderTable.setSelection(Math.min(mMetatagOrderTable.getItemCount()-1, oldSelIndex));
							
						}
					}
				});
				
				
				
//				mMetatagSectionEditButton=new Button(editComp, SWT.PUSH);
//				mMetatagSectionEditButton.setText("Section...");
//				mMetatagSectionEditButton.addSelectionListener(new SelectionAdapter()
//				{
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						TableItem[] selItems=mMetatagOrderTable.getSelection();
//						if (selItems.length==1)
//						{
//							String sectionID=Section_Metatags;
//							sectionID=sectionID+"#"+((ImportHolder)selItems[0].getData()).toString();
//							editSectionHeader(sectionID);
//						}
//					}
//
//				});
				
				
				mMetatagSort=new Button(aComp, SWT.CHECK);
				mMetatagSort.setText("Sort by name within group");
				mMetatagSort.addSelectionListener(enableUpdater);
			}
		}

		//adding comment headers for sections
		{
			Composite elementComp=new Composite(tabFolder, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			elementComp.setLayout(gl);
			elementComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			headerTab.setControl(elementComp);
			
			mUseSectionComments=new Button(elementComp, SWT.CHECK);
			mUseSectionComments.setText("Use code section comments");
			mUseSectionComments.setToolTipText("If checked, add section comments as configured on this tab and the 'Elements' tab");
			mUseSectionComments.addSelectionListener(enableUpdater);
			
			mUseSectionCommentsInMXML=new Button(elementComp, SWT.CHECK);
			mUseSectionCommentsInMXML.setText("Use code section comments in MXML files");
			mUseSectionCommentsInMXML.setToolTipText("If checked, add section comments to ActionScript code inside MXML files.");
			mUseSectionCommentsInMXML.addSelectionListener(enableUpdater);
			
			mRemoveExistingSectionComments=new Button(elementComp, SWT.CHECK);
			mRemoveExistingSectionComments.setText("Remove any existing section comments (WARNING: possibly destructive)");
			mRemoveExistingSectionComments.setToolTipText("If checked, remove all comments that 'look like' section headers before adding back the ones you have configured.\nIf not checked, then only section comments that match the text contents of one of the configured section headers will be removed.\nWARNING: this processing might remove something that looks like section header, but isn't.");
			mRemoveExistingSectionComments.addSelectionListener(enableUpdater);
			
			TabFolder sectionHeaderTabFolder=new TabFolder(elementComp, SWT.None);
			gd=new GridData(GridData.FILL_BOTH);
			sectionHeaderTabFolder.setLayoutData(gd);
			
			TabItem majorTab=new TabItem(sectionHeaderTabFolder, SWT.None);
			majorTab.setText("Major");

			TabItem minorTab=new TabItem(sectionHeaderTabFolder, SWT.None);
			minorTab.setText("Minor");
			
			Composite majorComp=new Composite(sectionHeaderTabFolder, SWT.None);
			majorComp.setLayout(new GridLayout());
			majorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			majorTab.setControl(majorComp);
			
			Composite minorComp=new Composite(sectionHeaderTabFolder, SWT.None);
			minorComp.setLayout(new GridLayout());
			minorComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			minorTab.setControl(minorComp);
			
			Composite labelComp=createLabelComp(majorComp, "Comment style");
			mMajorSectionStyle=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(majorComp, "Comment repeat char");
			mMajorSectionFillChar=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(majorComp, "Extra internal lines");
			mMajorSectionSize=new Combo(labelComp, SWT.READ_ONLY); 
			
			labelComp=createLabelComp(majorComp, "Blank lines before header");
			mMajorSectionPreLines=new Combo(labelComp, SWT.READ_ONLY); 
			
			labelComp=createLabelComp(majorComp, "Section width");
			mMajorSectionWidth=new Spinner(labelComp, SWT.BORDER);
			
			mSampleMajorSectionHeader=new Text(majorComp, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL);
			
			labelComp=createLabelComp(minorComp, "Comment style");
			mMinorSectionStyle=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(minorComp, "Comment repeat char");
			mMinorSectionFillChar=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(minorComp, "Extra internal lines");
			mMinorSectionSize=new Combo(labelComp, SWT.READ_ONLY); 
			
			labelComp=createLabelComp(minorComp, "Blank lines before header");
			mMinorSectionPreLines=new Combo(labelComp, SWT.READ_ONLY); 
			
			labelComp=createLabelComp(minorComp, "Section width");
			mMinorSectionWidth=new Spinner(labelComp, SWT.BORDER);
			
			mSampleMinorSectionHeader=new Text(minorComp, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL);
			
			initializeSectionHeaderWidgets(mSampleMajorSectionHeader, mMajorSectionWidth, mMajorSectionSize, mMajorSectionStyle, mMajorSectionFillChar, mMajorSectionPreLines, enableUpdater);
			initializeSectionHeaderWidgets(mSampleMinorSectionHeader, mMinorSectionWidth, mMinorSectionSize, mMinorSectionStyle, mMinorSectionFillChar, mMinorSectionPreLines, enableUpdater);
		}
		
		{
			Composite elementComp=new Composite(tabFolder, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			elementComp.setLayout(gl);
			elementComp.setLayoutData(new GridData(GridData.FILL_BOTH));
			copyrightTab.setControl(elementComp);
			
			mUseCopyrightHeader=new Button(elementComp, SWT.CHECK);
			mUseCopyrightHeader.setText("Create copyright headers");
			mUseCopyrightHeader.setToolTipText("If checked, add a copyright header at the start of each file");
			mUseCopyrightHeader.addSelectionListener(enableUpdater);
			
			mRemoveExistingCopyright=new Button(elementComp, SWT.CHECK);
			mRemoveExistingCopyright.setText("Remove any existing copyright comments (WARNING: possibly destructive)");
			mRemoveExistingCopyright.setToolTipText("If checked, remove the first comment in a file if it looks like a copyright comment.  Otherwise, leave the existing comment and do not add a new one.");
			mRemoveExistingCopyright.addSelectionListener(enableUpdater);
			
			Group g=new Group(elementComp, SWT.None);
			gl=new GridLayout();
			gl.marginHeight=0;
			g.setLayout(gl);
			g.setLayoutData(new GridData(GridData.FILL_BOTH));
			g.setText("Copyright");

			Composite labelComp=createLabelComp(g, "Comment style");
			mCopyrightSectionStyle=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(g, "Comment repeat char");
			mCopyrightSectionFillChar=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(g, "Extra internal lines");
			mCopyrightSectionSize=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(g, "Lines after copyright");
			mCopyrightSectionPostLines=new Combo(labelComp, SWT.READ_ONLY);
			
			labelComp=createLabelComp(g, "Section width");
			mCopyrightSectionWidth=new Spinner(labelComp, SWT.BORDER);
			
			mCopyrightText=new Text(g, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			gd=new GridData(GridData.FILL_HORIZONTAL);
			FontData[] fonts=Display.getDefault().getFontList("Courier", false);
			if (fonts.length>0)
			{
				Font font=new Font(Display.getCurrent(), fonts[0]);
				mCopyrightText.setFont(font);
				gd.heightHint=mCopyrightText.getLineHeight()*8;
			}
			mCopyrightText.setLayoutData(gd);
			mCopyrightText.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e) {
					updateRearrangeText();
				}
			});
			
			mSampleCopyrightHeader=new Text(g, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL);
			
			initializeSectionHeaderWidgets(mSampleCopyrightHeader, mCopyrightSectionWidth, mCopyrightSectionSize, mCopyrightSectionStyle, mCopyrightSectionFillChar, mCopyrightSectionPostLines, enableUpdater);			
			((GridData)mSampleCopyrightHeader.getLayoutData()).heightHint=mSampleCopyrightHeader.getLineHeight()*8;
		}
		
		return settingsComp;
	}

	/**
	 * Reflow the properties of prefiltered member selection specs so there are no gaps and they start with 1
	 * @param specs the member selection specs that are currently in the table
	 */
	protected void reflowPrefilterPriorities(List<MemberSelectionSpec> specs)
	{
		Collections.sort(specs, new Comparator<MemberSelectionSpec>()
		{
			public int compare(MemberSelectionSpec o1, MemberSelectionSpec o2) {
				return o1.getPreselectPriority()-o2.getPreselectPriority();
			}
		});
		
		for (int i=0;i<specs.size();i++)
		{
			specs.get(i).setPreselectPriority(i+1); //because we want to start with 1
		}
	}

	protected List<MemberSelectionSpec> getPreselectPriorityItems()
	{
		List<MemberSelectionSpec> specs=new ArrayList<MemberSelectionSpec>();
		for (TableItem item : mElementOrderTable.getItems()) {
			if (item.getData() instanceof MemberSelectionSpec)
			{
				MemberSelectionSpec spec=(MemberSelectionSpec)item.getData();
				if (spec.getPreselectPriority()>0)
					specs.add(spec);
			}
		}
		return specs;
	}

	protected void updateSectionInfoText() {
		TableItem[] selItems=mElementOrderTable.getSelection();
		if (selItems.length>0)
		{
			StringBuffer buffer=new StringBuffer();
			List<ISectionItem> allSections=getSections();
			String sectionID=((ISectionItem)selItems[0].getData()).getReferenceID();
			SectionSpec spanSpec=ASRearranger.getSectionSpec(sectionID+ASRearranger.SpanningSuffix, allSections, mHeaderSpecs);
			SectionSpec spec=ASRearranger.getSectionSpec(sectionID, allSections, mHeaderSpecs);
			
			if (spec==null || !spec.isUseHeader())
				buffer.append("No header configured, ");
			else
			{
				buffer.append("Header configured, ");
			}
			
			if (spanSpec==null || !spanSpec.isUseHeader())
				buffer.append("no spanning header");
			else
			{
				if (spanSpec.getID().startsWith(sectionID)) //if this item starts a span
				{
					//find the item it extends to, which might be a member selector
					String endPrintString=findPrintStringForSectionID(allSections, spanSpec.getEndSpanSectionID());
					buffer.append("spanning header extends to : "+endPrintString);
				}
				else //this item is part of a span
				{
					String rootSection=spanSpec.getID().substring(0, spanSpec.getID().length()-ASRearranger.SpanningSuffix.length());
					String startPrintString=findPrintStringForSectionID(allSections, rootSection);
					buffer.append("included in section span starting with: "+startPrintString);
				}
				
			}
			
			mSpanInfoText.setText(buffer.toString());
		}
		else
			mSpanInfoText.setText("");
	}

	protected String findPrintStringForSectionID(List<ISectionItem> allSections, String refID)
	{
		for (ISectionItem sectionItem : allSections) {
			if (sectionItem.getReferenceID().equals(refID))
				return sectionItem.getPrintString();
		}
		return "";
	}

	private void createMemberWidgets(TabFolder elementTypeTabFolder, TabItem tab, final String elementConstant, SelectionListener enableUpdater, String mainTitle, String sUseSortOrder, 
			String sOrderTable, String sUp, String sDown, String sSection, String sSortButton, String sGetterButton, String sGetterSortButton, String sGetterHeaderButton) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException
	{
		//getClass().getField("").set(this, obj);

		Composite aComp=new Composite(elementTypeTabFolder, SWT.None);
		GridLayout gl=new GridLayout();
		gl.marginHeight=0;
		aComp.setLayout(gl);
		aComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab.setControl(aComp);
		
		Label l=new Label(aComp, SWT.None);
		l.setText(mainTitle);
		
		Button sortOrder=new Button(aComp, SWT.CHECK);
		getClass().getDeclaredField(sUseSortOrder).set(this, sortOrder);
		sortOrder.setText("Group by visibility");
		sortOrder.setToolTipText("Use the items in the visibility table as the primary sort.");
		sortOrder.addSelectionListener(enableUpdater);
		
		Composite orderComp=new Composite(aComp, SWT.None);
		orderComp.setLayout(new GridLayout(2, false));
		orderComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		final Table orderTable=new Table(orderComp, SWT.SINGLE | SWT.BORDER | SWT.CHECK);
		getClass().getDeclaredField(sOrderTable).set(this, orderTable);
		orderTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		orderTable.addSelectionListener(enableUpdater);
		
		Composite buttonComp=new Composite(orderComp, SWT.None);
		buttonComp.setLayout(new GridLayout(1, true));
		buttonComp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		Button upButton=new Button(buttonComp, SWT.PUSH);
		getClass().getDeclaredField(sUp).set(this, upButton);
		upButton.setText("Up");
		upButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				moveUpHelper(orderTable);
			}
		});
		
		Button downButton=new Button(buttonComp, SWT.PUSH);
		getClass().getDeclaredField(sDown).set(this, downButton);
		downButton.setText("Down");
		downButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				moveDownHelper(orderTable);
			}
		});
		
		Button sectionButton=new Button(buttonComp, SWT.PUSH);
		getClass().getDeclaredField(sSection).set(this, sectionButton);
		sectionButton.setText("Header...");
		sectionButton.setToolTipText("Configure the section header for the selected table item");
		sectionButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] visSel=orderTable.getSelection();
				if (visSel.length==1)
				{
					//get the section header id based on the object type from the table
					String sectionID=elementConstant;
					sectionID=sectionID+"#"+((ElementHolder)visSel[0].getData()).toString();
					editSectionHeader(sectionID);
					updatePrintStringsInOrderTable(orderTable);
				}
			}

		});
		
		if (sGetterButton!=null)	
		{
			Button getterButton=new Button(aComp, SWT.CHECK);
			getClass().getDeclaredField(sGetterButton).set(this, getterButton);
			getterButton.setText("Include getter/setter methods in this group");
			getterButton.setToolTipText("If checked, getter/setter functions will be included with regular properties.");
			getterButton.addSelectionListener(enableUpdater);
		}

		Composite sortComp=new Composite(aComp, SWT.None);
		gl=new GridLayout(3, false);
		gl.marginWidth=0;
		gl.marginHeight=0;
		sortComp.setLayout(gl);
		sortComp.setLayoutData(new GridData());
		
		Button sortButton=new Button(sortComp, SWT.CHECK);
		getClass().getDeclaredField(sSortButton).set(this, sortButton);
		sortButton.setText("Sort by name within group");
		sortButton.setToolTipText("This sort occurs after the visibility sort, if enabled.");
		sortButton.addSelectionListener(enableUpdater);
		
		if (sGetterSortButton!=null)
		{
			Button getterButton=new Button(sortComp, SWT.CHECK);
			getClass().getDeclaredField(sGetterSortButton).set(this, getterButton);
			getterButton.setText("Associate implicit properties");
			getterButton.setToolTipText("If checked, actual properties will be sorted beside their associated getter/setter functions, if they can be associated.");
			getterButton.addSelectionListener(enableUpdater);
			
//			l=new Label(sortComp, SWT.None);
//
////			Composite headerComp=new Composite(sortComp, SWT.None);
////			gl=new GridLayout(2, false);
////			gl.marginHeight=0;
////			gl.marginLeft=30;
////			headerComp.setLayout(gl);
////			headerComp.setLayoutData(new GridData());
//			
//			l=new Label(sortComp, SWT.None);
			
			Button headerButton=new Button(sortComp, SWT.PUSH);
			getClass().getDeclaredField(sGetterHeaderButton).set(this, headerButton);
			headerButton.setText("Headers...");
			headerButton.setToolTipText("Configure whether to create a default minor property header for properties in this group.");
			headerButton.addSelectionListener(enableUpdater);

//			Button allHeadersButton=new Button(sortComp, SWT.CHECK);
//			getClass().getDeclaredField(sAllGetterHeaderButton).set(this, allHeadersButton);
//			allHeadersButton.setText("Headers always");
//			allHeadersButton.setToolTipText("If checked, create a default minor property header for every property.");
//			allHeadersButton.addSelectionListener(enableUpdater);
		}
	}
	
	
	protected List<ISectionItem> getSections()
	{
		List<ISectionItem> results=new ArrayList<ISectionItem>();
		TableItem[] allElements=mElementOrderTable.getItems();
		for (TableItem tableItem : allElements) {
			if (tableItem.getData() instanceof ISectionItem)
				results.add((ISectionItem)tableItem.getData());
		}
		return results;
	}

	protected void editSectionHeader(String sectionID) {
		if (sectionID.length()==0)
			return;
		
		//search existing header specs to see if the sectionID is included as part of another header
		List<ISectionItem> allSections=getSections();
		SectionSpec spec=ASRearranger.getSectionSpec(sectionID, allSections, mHeaderSpecs);
		
		if (spec==null)
		{
			//create a new one if we didn't have one before
			String text=sectionID;
			for (ISectionItem sectionItem : allSections) {
				//look for the section that matches this reference ID so we can get the printstring.  I made
				//a special case for the 'visibility' items like Property#private so that they will show
				//that string rather than just "Property".
				if (sectionID.startsWith(sectionItem.getReferenceID()) && (sectionID.indexOf('#')<0 || sectionID.endsWith(ASRearranger.SpanningSuffix)))
				{
					text=sectionItem.getPrintString();
					break;
				}
			}
			if (text.length()>50)
				text="Members";
			spec=new SectionSpec(sectionID, SectionSpec.MAJOR, new String[]{"   "+text}, false);
		}
		Map<Integer, SectionHeader> templates=new HashMap<Integer, SectionHeader>();
		templates.put(SectionSpec.MAJOR, getMajorSectionHeader(new String[]{}));
		templates.put(SectionSpec.MINOR, getMinorSectionHeader(new String[]{}));
		EditSectionHeaderDlg dlg=new EditSectionHeaderDlg(getShell(), spec, templates, sectionID, allSections);
		if (dlg.open()==Dialog.OK)
		{
			mHeaderSpecs.put(sectionID, dlg.getHeaderSpec());
			enableWidgets();
			updateRearrangeText();
			updateSectionInfoText();
		}
	}

	protected SectionHeader getMinorSectionHeader(String[] text)
	{
		return new SectionHeader(((Integer)mMinorSectionStyle.getData(mMinorSectionStyle.getText())).intValue(), mMinorSectionWidth.getSelection(), ((Integer)mMinorSectionSize.getData(mMinorSectionSize.getText())).intValue(), mMinorSectionFillChar.getText(), text, ((Integer)mMinorSectionPreLines.getData(mMinorSectionPreLines.getText())).intValue());
	}

	protected SectionHeader getMajorSectionHeader(String[] text)
	{
		return new SectionHeader(((Integer)mMajorSectionStyle.getData(mMajorSectionStyle.getText())).intValue(), mMajorSectionWidth.getSelection(), ((Integer)mMajorSectionSize.getData(mMajorSectionSize.getText())).intValue(), mMajorSectionFillChar.getText(), text, ((Integer)mMajorSectionPreLines.getData(mMajorSectionPreLines.getText())).intValue());
	}

	private void initializeSectionHeaderWidgets(Text sample, Spinner width, Combo size, Combo style, Combo fillChar, Combo blankLines, final SelectionListener enableUpdater)
	{
//		SelectionListener sectionExampleUpdater=new SelectionAdapter()
//		{
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				reformatText();
//				enableUpdater.widgetSelected(e);
//			}
//		};
		style.addSelectionListener(enableUpdater);
		style.setToolTipText("Set the comment style of section headers.  The Adobe style is //");
		
		String text="//";
		style.add(text);
		style.setData(text, SectionHeader.AS_Section_Style_SlashSlash);

//		text="/*";
//		style.add(text);
//		style.setData(text, SectionHeader.AS_Section_Style_SlashStarblock);

		text="/* */";
		style.add(text);
		style.setData(text, SectionHeader.AS_Section_Style_SlashStarLine);
		
		style.select(0);
		
		fillChar.addSelectionListener(enableUpdater);
		fillChar.setToolTipText("This is the 'repeat' character used to fill out the header and footer lines of a section header.  The Adobe default is a dash (-)");
		fillChar.add("-");
		fillChar.add("=");
		fillChar.add("#");
		fillChar.add("_");
		fillChar.add("+");
		fillChar.add("/");
		fillChar.add(".");
		fillChar.add("@");
		fillChar.select(0);
		
		for (int i=0;i<5;i++)
		{
			String printString=Integer.toString(i);
			blankLines.add(printString);
			blankLines.setData(printString, new Integer(i));
		}
		blankLines.select(1);
		blankLines.addSelectionListener(enableUpdater);
		
		size.setToolTipText("This setting controls the number of blank lines between the header/footer and the header content.");
		size.addSelectionListener(enableUpdater);
		for (int i=0;i<8;i+=2)
		{
			String printString=Integer.toString(i);
			size.add(printString);
			size.setData(printString, new Integer(i));
		}
		size.select(0);
		
		width.setMinimum(20);
//		mMajorSectionWidth.setMaximum(10);
		width.setToolTipText("");
		width.addSelectionListener(enableUpdater);
		width.setToolTipText("This is the default width for the header comment.  The Adobe standard is for a Major header to be 80 chars wide, and for a Minor header to be 40.");
		width.setSelection(40);
		
		GridData gd=new GridData(GridData.FILL_HORIZONTAL);
		FontData[] fonts=Display.getDefault().getFontList("Courier", false);
		if (fonts.length>0)
		{
			Font font=new Font(Display.getCurrent(), fonts[0]);
			sample.setFont(font);
			gd.heightHint=sample.getLineHeight()*12;
		}
		gd.widthHint=250;
		sample.setLayoutData(gd);
		if (sample!=mSampleCopyrightHeader)
			sample.setToolTipText("This shows what a typical major/minor header would look like.  The text is just sample.  Configure \na header on the 'Elements' page to set the specific text and see what it will look like.");
	}

	protected void generateSectionHeaderSamples(SectionHeader header, Text sampleArea)
	{
//		Integer style=(Integer)sectionStyle.getData(sectionStyle.getText());
//		int lineWidth=width.getSelection();
//		Integer lines=(Integer)size.getData(size.getText());
		
//		SectionHeader header=new SectionHeader(style, lineWidth, lines.intValue(), text);
		String[] data=header.getCommentLines();
		StringBuffer buffer=new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String line = data[i];
			buffer.append(line);
			if (i+1<data.length)
				buffer.append('\n');
		}
		
		sampleArea.setText(buffer.toString());
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
	
	private void updateMXMLRearrangeText()
	{
		if (!mVisualsInitialized)
			return;
		
		String text=mSampleMXMLRearrangingText.getEditable() ? mSampleMXMLRearrangingText.getText() : mOriginalMXMLRearrangingSampleText;
		
		//rearrange
		IPreferenceStore store=new PreferenceStore();
		Properties props=new Properties();
		setProperties(props);
		for (Map.Entry<Object, Object> entry : props.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			store.putValue(key, value);
		}
		
		MXMLRearranger rearranger=new MXMLRearranger(store);
		IDocument doc=new Document(text);
		try {
			rearranger.rearrangeCode(doc, new ArrayList<MarkerAnnotation>());
			mMXMLRearrangeLineStyleListener.reset();
			int topLine=mSampleMXMLRearrangingText.getTopIndex();
			mSampleMXMLRearrangingText.setRedraw(false);
			mSampleMXMLRearrangingText.setText(doc.get());
			mSampleMXMLRearrangingText.setTopIndex(topLine);
			mSampleMXMLRearrangingText.setRedraw(true);
		} catch (Exception e) {
			Activator.logException(e, "");
		}
	}
	
	private void updateRearrangeText()
	{
		if (!mVisualsInitialized)
			return;
		
		String text=mSampleRearrangingText.getEditable() ? mSampleRearrangingText.getText() : mOriginalASRearrangingSampleText;
		
		//rearrange
		IPreferenceStore store=new PreferenceStore();
		Properties props=new Properties();
		setProperties(props);
		for (Map.Entry<Object, Object> entry : props.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			store.putValue(key, value);
		}
		
		ASRearranger rearranger=new ASRearranger(store);
		IDocument doc=new Document(text);
		try {
			rearranger.rearrangeCode(doc, new ArrayList<MarkerAnnotation>(), false);
			mRearrangeLineStyleListener.reset();
			int topLine=mSampleRearrangingText.getTopIndex();
			mSampleRearrangingText.setRedraw(false);
			mSampleRearrangingText.setText(doc.get());
			mSampleRearrangingText.setTopIndex(topLine);
			mSampleRearrangingText.setRedraw(true);
		} catch (Exception e) {
			Activator.logException(e, "");
		}
		
		generateSectionHeaderSamples(getMajorSectionHeader(new String[]{"Major section text"}), mSampleMajorSectionHeader);
		generateSectionHeaderSamples(getMinorSectionHeader(new String[]{"Minor section"}), mSampleMinorSectionHeader);
		generateSectionHeaderSamples(getCopyrightHeader(mCopyrightText), mSampleCopyrightHeader);
	}

	private SectionHeader getCopyrightHeader(Text textWidget)
	{
		String text=textWidget.getText();
		String[] lines=text.split(textWidget.getLineDelimiter());
		return new SectionHeader(((Integer)mCopyrightSectionStyle.getData(mCopyrightSectionStyle.getText())).intValue(), mCopyrightSectionWidth.getSelection(), ((Integer)mCopyrightSectionSize.getData(mCopyrightSectionSize.getText())).intValue(), mCopyrightSectionFillChar.getText(), lines, ((Integer)mCopyrightSectionPostLines.getData(mCopyrightSectionPostLines.getText())).intValue());
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
	
	private void addItemsToOrderingTable(String orderData, Table table)
	{
		table.removeAll();
		String[] items=orderData.split(PreferenceConstants.AS_Pref_Line_Separator);
		for (String modItem : items) {
			modItem=modItem.trim();
			if (modItem.length()>0)
			{
				String[] tags=modItem.split(PreferenceConstants.AS_Pref_Tag_Separator);
				if (tags.length>=2)
				{
					TableItem item=new TableItem(table, SWT.None);
					item.setData(new VisibilityHolder(tags[0]));
					updateItemText(item);
					item.setChecked(Boolean.valueOf(tags[1]));
				}
			}
		}
	}

	private void addItemsToSimpleOrderingTable(String orderData, Table table)
	{
		table.removeAll();
		String[] items=orderData.split(PreferenceConstants.AS_Pref_Line_Separator);
		for (String modItem : items) {
			modItem=modItem.trim();
			if (modItem.length()>0)
			{
				TableItem item=new TableItem(table, SWT.None);
				item.setData(new ImportHolder(modItem));
				item.setText(modItem);
			}
		}
	}

	private void addTableItems(String[] keywords)
	{
		mModifierTable.removeAll();
		for (String key : keywords) {
			TableItem item=new TableItem(mModifierTable, SWT.None);
			item.setText(key);
		}
	}

	private void saveSimpleTableOrder(Properties store, Table table, String prefKey) {
		TableItem[] allItems=table.getItems();
		StringBuffer buffer=new StringBuffer();
		for (TableItem tableItem : allItems)
		{
			buffer.append(tableItem.getText());
			buffer.append(PreferenceConstants.AS_Pref_Line_Separator);
		}
		store.put(prefKey, buffer.toString());
		
	}

	private void saveVisibilityOrder(Properties store, Table table, String prefKey)
	{
		TableItem[] allItems=table.getItems();
		StringBuffer buffer=new StringBuffer();
		for (TableItem tableItem : allItems) {
			buffer.append(tableItem.getData().toString());
			buffer.append(PreferenceConstants.AS_Pref_Tag_Separator);
			buffer.append(Boolean.toString(tableItem.getChecked()));
			buffer.append(PreferenceConstants.AS_Pref_Line_Separator);
		}
		store.put(prefKey, buffer.toString());
	}

	private void createAdvancedGroup(final Composite parent, IAdvancedSettingsLayout advancedSettingsLayout)
	{
		final Composite ecGroup=new Canvas(parent, SWT.None);
		FillLayout fl=new FillLayout();
		fl.marginHeight=2;
		fl.marginWidth=2;
		ecGroup.setLayout(fl);
		ecGroup.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				GC gc=new GC(ecGroup);
				Rectangle rect=ecGroup.getClientArea();
				gc.setLineStyle(SWT.LINE_DOT);
				gc.drawRoundRectangle(0, 0, rect.width-1, rect.height-1, 3, 3);
			}
			
		});
		
//		Composite myComp=new Composite(ecGroup, SWT.None);
//		GridLayout gl=new GridLayout();
//		gl.marginHeight=0;
//		myComp.setLayout(gl);
//		GridData gd=new GridData(GridData.FILL_HORIZONTAL);
		
//		GridLayout gl=new GridLayout();
//		gl.marginHeight=0;
//		ecGroup.setLayout(gl);
//		GridData gd=new GridData(GridData.FILL_HORIZONTAL);
		
//		gd.horizontalAlignment=SWT.LEFT;
		final ExpandableComposite ec=new ExpandableComposite(ecGroup, ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT | ExpandableComposite.NO_TITLE);
//		ec.setLayout(new GridLayout());
//		gd=new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalIndent=SWT.BEGINNING;
//		ec.setLayoutData(gd);
		ec.addExpansionListener(new IExpansionListener()
		{
			public void expansionStateChanged(ExpansionEvent e) {
				//TODO: probably walk up more levels
				relayoutFromComponent(ec);
//				ec.layout(true);
//				ecGroup.layout(true);
//				parent.layout(true);
//				parent.getParent().layout(true);
//				parent.getParent().getParent().layout(true);
			}

			public void expansionStateChanging(ExpansionEvent e) {
				//nothing to do
			}
		});
		advancedSettingsLayout.addGlobalItem(ec);
		Composite advancedComp=new Composite(ec, SWT.None);
		GridLayout gl=new GridLayout();
		gl.marginHeight=0;
		gl.marginWidth=0;
		advancedComp.setLayout(gl);
		advancedSettingsLayout.addAdvancedItems(advancedComp);
		ec.setClient(advancedComp);
	}

	protected void relayoutFromComponent(ExpandableComposite ec)
	{
		ec.layout(true);
		Composite parent=ec.getParent();
		boolean seenScrolledComp=false;
		while (parent!=null)
		{
//			parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			parent.layout(true);
			if (!seenScrolledComp && parent instanceof ScrolledComposite)
			{
				((ScrolledComposite)parent).setMinHeight(parent.getChildren()[0].computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				seenScrolledComp=true;
			}
//			if (parent.getParent() instanceof ScrolledComposite)
//			{
//				parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//			}
//			parent.pack(true);
			parent=parent.getParent();
		}
	}

	private boolean tagExists(String tag)
	{
		TableItem[] neverItems=mNeverFormatTable.getItems();
		TableItem[] alwaysItems=mAlwaysFormatTable.getItems();
		List<TableItem> items=new ArrayList<TableItem>();
		items.addAll(Arrays.asList(neverItems));
		items.addAll(Arrays.asList(alwaysItems));
		for (TableItem tableItem : items) {
			if (tag.equals(tableItem.getText()))
				return true;
		}
		
		return false;
	}
	
	private String loadSampleText(String filePath)
	{
		//load sample text
		try
		{
			InputStream is=getClass().getClassLoader().getResourceAsStream(filePath);
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
			StringBuffer buffer=new StringBuffer();
			while (true)
			{
				String line=br.readLine();
				if (line==null)
					break;
				buffer.append(line);
				buffer.append("\n");
			}
			br.close();
			return buffer.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	
	private StyledText createCodeArea(Composite parent)
	{
		StyledText sampleText=new StyledText(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		GridData gd=new GridData(GridData.FILL_BOTH);
//		gd.grabExcessHorizontalSpace=true;
//		gd.grabExcessVerticalSpace=true;
		//TODO: figure out why text area doesn't grow when window expanded
		gd.widthHint=400;
		FontData[] fonts=Display.getDefault().getFontList("Courier", false);
		if (fonts.length>0)
		{
			Font font=new Font(Display.getCurrent(), fonts[0]);
			sampleText.setFont(font);
			gd.heightHint=sampleText.getLineHeight()*30;
		}
		else
			gd.heightHint=350;
		sampleText.setLayoutData(gd);
		return sampleText;
	}
	
	private int getIndexOfWrapOption(Combo wrapCombo, int optionCode)
	{
		for (int i=0;i<wrapCombo.getItemCount();i++)
		{
			String item=wrapCombo.getItem(i);
			Integer code=(Integer)wrapCombo.getData(item);
			if (code.intValue()==optionCode)
				return i;
		}
		
		return 0;
	}
	
	void updateWidgets(IPreferenceStore store)
	{
		boolean useTabs=store.getBoolean(Initializer.Pref_Flex_UseTabs);
		mUseTabsRadio.setSelection(useTabs);
		mUseSpacesRadio.setSelection(!useTabs);
		
		mSpacesBeforeComma.setSelection(store.getInt(Initializer.Pref_AS_SpacesBeforeComma));
		mSpacesAfterComma.setSelection(store.getInt(Initializer.Pref_AS_SpacesAfterComma));
		mSpacesAroundAssignment.setSelection(store.getInt(Initializer.Pref_AS_SpacesAroundAssignment));
		mASSpacesAroundAssignmentInParameters.setSelection(store.getInt(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInOptionalParameters));
		mASSpacesAroundAssignmentInMetatags.setSelection(store.getInt(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInMetatags));
		mASUseSpacesAroundAssignmentInParameters.setSelection(store.getBoolean(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInOptionalParameters));
		mASUseSpacesAroundAssignmentInMetatags.setSelection(store.getBoolean(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInMetatags));
		mSpacesAroundSymbolicOperator.setSelection(store.getInt(Initializer.Pref_AS_SpacesAroundSymbolicOperator));
		mASSpacesAroundColons.setSelection(store.getInt(Initializer.Pref_AS_SpacesAroundColons));
		mASAdvancedSpacesBeforeColonsInDeclarations.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInDeclarations ) );
		mASAdvancedSpacesAfterColonsInDeclarations.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesAfterColonsInDeclarations ) );
		mASAdvancedSpacesBeforeColonsInFunctions.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes ) );
		mASAdvancedSpacesAfterColonsInFunctions.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes ) );
		mASUseGlobalSpacesAroundColons.setSelection(store.getBoolean(Initializer.Pref_AS_UseGlobalSpacesAroundColons));
		mBlankLinesBeforeFunctions.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesBeforeFunctions));
		mASBlankLinesBeforeClass.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesBeforeClasses));
		mASMaxLineLength.setSelection(store.getInt(Initializer.Pref_AS_MaxLineLength));
		mASBlankLinesBeforeControlStatement.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesBeforeControlStatements));
		mASBlankLinesBeforeImports.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesBeforeImportBlock));
		mASBlankLinesBeforeProperties.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesBeforeProperties));
		mASBlankLinesAtFunctionStart.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesAtFunctionStart));
		mASBlankLinesAtFunctionEnd.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesAtFunctionEnd));
		mASSpacesInsideParens.setSelection(store.getInt(Initializer.Pref_AS_SpacesInsideParens));

		mASUseGlobalSpacesInsideParens.setSelection(store.getBoolean(Initializer.Pref_AS_UseGlobalSpacesInsideParens));
		mASAdvancedSpacesInsideArrayDeclBrackets.setSelection(store.getInt(Initializer.Pref_AS_AdvancedSpacesInsideArrayDeclBrackets));
		mASAdvancedSpacesInsideArrayRefBrackets.setSelection(store.getInt(Initializer.Pref_AS_AdvancedSpacesInsideArrayRefBrackets));
		mASAdvancedSpacesInsideObjectLiteralBraces.setSelection(store.getInt(Initializer.Pref_AS_AdvancedSpacesInsideLiteralBraces));
		mASAdvancedSpacesInsideParensInOtherPlaces.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInOtherPlaces) );
		mASAdvancedSpacesInsideParensInParameterLists.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInParameterLists) );
		mASAdvancedSpacesInsideParensInArgumentLists.setSelection( store.getInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInArgumentLists) );
		
		mASCollapseAdjacentParens.setSelection(store.getBoolean(Initializer.Pref_AS_CollapseSpacesForAdjacentParens));
		mASArrayDeclWrapCombo.select(getIndexOfWrapOption(mASArrayDeclWrapCombo, store.getInt(Initializer.Pref_AS_WrapArrayDeclMode)));
		mASMethodCallWrapCombo.select(getIndexOfWrapOption(mASMethodCallWrapCombo, store.getInt(Initializer.Pref_AS_WrapMethodCallMode)));
		mASMethodDeclWrapCombo.select(getIndexOfWrapOption(mASMethodDeclWrapCombo, store.getInt(Initializer.Pref_AS_WrapMethodDeclMode)));
		mASExpressionWrapCombo.select(getIndexOfWrapOption(mASExpressionWrapCombo, store.getInt(Initializer.Pref_AS_WrapExpressionMode)));
		mASXMLWrapCombo.select(getIndexOfWrapOption(mASXMLWrapCombo, store.getInt(Initializer.Pref_AS_WrapXMLMode)));
		mASSpecialWrapCommaItems.setSelection(store.getInt(Initializer.Pref_AS_WrapIndentStyle)==WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT);
		mASBreakLinesBeforeComma.setSelection(store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeComma));
		mASBreakLinesBeforeArithmeticOperator.setSelection(store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeArithmetic));
		mASBreakLinesBeforeLogicalOperator.setSelection(store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeLogical));
		mASBreakLinesBeforeAssignment.setSelection(store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeAssignment));
		mASHangingIndentSize.setSelection(store.getInt(Initializer.Pref_AS_TabsInHangingIndent));
//		mASTrimTrailingWS.setSelection(store.getBoolean(Initializer.Pref_AS_TrimTrailingWhitespace));
		mASSpacesAfterLabelColon.setSelection(store.getInt(Initializer.Pref_AS_SpacesAfterLabel));
		mKeepBlankLines.setSelection(store.getBoolean(Initializer.Pref_AS_KeepBlankLines));
		mASBlankLinesToKeep.setSelection(store.getInt(Initializer.Pref_AS_BlankLinesToKeep));
		mOpenBraceOnNewLine.setSelection(store.getBoolean(Initializer.Pref_AS_OpenBraceOnNewLine));
		mASCRBeforeElse.setSelection(store.getBoolean(Initializer.Pref_AS_ElseOnNewLine));
		mASCRBeforeWhile.setSelection(store.getBoolean(Initializer.Pref_AS_WhileOnNewLine));
		mASNoCRBeforeBreak.setSelection(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeBreak));
		mASNoCRBeforeContinue.setSelection(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeContinue));
		mASNoCRBeforeReturn.setSelection(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeReturn));
		mASNoCRBeforeThrow.setSelection(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeThrow));
		mASNoCRBeforeExpression.setSelection(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeExpression));
		mKeepElseIfOnSameLine.setSelection(store.getBoolean(Initializer.Pref_AS_ElseIfOnSameLine));
		mASCRBeforeCatch.setSelection(store.getBoolean(Initializer.Pref_AS_CatchOnNewLine));
		mASUseBraceStyle.setSelection(store.getBoolean(Initializer.Pref_AS_UseBraceStyle));
		mASBraceStyle.select(getComboIndexFromBraceStyle(store.getInt(Initializer.Pref_AS_BraceStyle)));
		mKeepSLCommentsOnColumn1.setSelection(store.getBoolean(Initializer.Pref_AS_KeepSLCommentsOnColumn1));
		mASAlwaysGenerateIndent.setSelection(store.getBoolean(Initializer.Pref_AS_AlwaysGenerateIndent));
		mASSpacesBeforeOpenControlParen.setSelection(store.getInt(Initializer.Pref_AS_SpacesBeforeOpenControlParen));
		mASSpacesBeforeDeclParameters.setSelection(store.getInt(Initializer.Pref_AS_SpacesBeforeFormalParameters));
		mASSpacesBeforeArguments.setSelection(store.getInt(Initializer.Pref_AS_SpacesBeforeArguments));
		mASNewlineBeforeBindableFunction.setSelection(store.getBoolean(Initializer.Pref_AS_NewlineBeforeBindableFunction));
		mASNewlineBeforeBindableProperty.setSelection(store.getBoolean(Initializer.Pref_AS_NewlineBeforeBindableProperty));
		mASEmptyStatementsOnNewLine.setSelection(store.getBoolean(Initializer.Pref_AS_PutEmptyStatementsOnNewLine));
		mASDontIndentPackageElements.setSelection(store.getBoolean(Initializer.Pref_AS_DontIndentPackageItems));
		mASDontIndentSwitchCases.setSelection(store.getBoolean(Initializer.Pref_AS_DontIndentSwitchCases));
		mASNoIndentForExpressionTerminatorButton.setSelection(store.getBoolean(Initializer.Pref_AS_UnindentExpressionTerminators));
		mASLeaveExtraWhitespaceAroundVarDecls.setSelection(store.getBoolean(Initializer.Pref_AS_LeaveExtraWhitespaceAroundVarDecls));
		mASAlignDeclEquals.setSelection(store.getBoolean(Initializer.Pref_AS_AlignDeclEquals));
		mASAlignDeclEqualsConsecutive.setSelection(store.getInt(Initializer.Pref_AS_AlignDeclMode)!=ASPrettyPrinter.Decl_Align_Scope);
		mASAlignDeclEqualsScope.setSelection(store.getInt(Initializer.Pref_AS_AlignDeclMode)==ASPrettyPrinter.Decl_Align_Scope);
		mASKeepSingleLineFunctions.setSelection(store.getBoolean(Initializer.Pref_AS_LeaveSingleLineFunctions));
		mASRearrangeDuringFormatting.setSelection(store.getBoolean(Initializer.Pref_AS_RearrangeAsPartOfFormat));
//		mASEnsureConditionalBraces.setSelection(store.getBoolean(Initializer.Pref_AS_EnsureConditionalsHaveBraces));
		mASEnsureConditionalBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_AddIfMissing)!=0);
		mASSmartAddConditionalBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_AddSmart)!=0);
		mASSmartAddRemoveConditionalBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_AddRemoveSmart)!=0);
		mASNoModifyConditionalBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToConditionals) & ASPrettyPrinter.Braces_NoModify)!=0);
//		mASEnsureLoopBraces.setSelection(store.getBoolean(Initializer.Pref_AS_EnsureLoopsHaveBraces));
		mASEnsureLoopBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_AddIfMissing)!=0);
		mASSmartAddLoopBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_AddSmart)!=0);
		mASSmartAddRemoveLoopBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_AddRemoveSmart)!=0);
		mASNoModifyLoopBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToLoops) & ASPrettyPrinter.Braces_NoModify)!=0);
//		mASEnsureSwitchBraces.setSelection(store.getBoolean(Initializer.Pref_AS_EnsureSwitchCasesHaveBraces));
		mASSmartAddSwitchBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToCases) & ASPrettyPrinter.Braces_AddSmart)!=0);
		mASRemoveSwitchBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToCases) & ASPrettyPrinter.Braces_RemoveUnnecessary)!=0);
		mASNoModifySwitchBraces.setSelection((store.getInt(Initializer.Pref_AS_AddBracesToCases) & ASPrettyPrinter.Braces_NoModify)!=0);
		mASUseGnuBraceIndent.setSelection(store.getBoolean(Initializer.Pref_AS_UseGnuBraceIndent));
		
		mASKeepRelativeIndentOfMultilineComments.setSelection(store.getBoolean(Initializer.Pref_AS_KeepRelativeIndentInDocComments));
		mASWrapLineComments.setSelection(store.getBoolean(Initializer.Pref_AS_UseLineCommentWrapping));
		mASWrapASDocComments.setSelection(store.getBoolean(Initializer.Pref_AS_UseDocCommentWrapping));
		mASWrapDocCommentsHangingTabs.setSelection((store.getInt(Initializer.Pref_AS_DocCommentHangingIndentTabs)));
		mASWrapDocCommentsKeepBlankLines.setSelection(store.getBoolean(Initializer.Pref_AS_DocCommentKeepBlankLines));
		mASWrapDocCommentsReflow.setSelection(store.getBoolean(Initializer.Pref_AS_DocCommentReflow));
		mASWrapMLComments.setSelection(store.getBoolean(Initializer.Pref_AS_UseMLCommentWrapping));
		mASWrapMLCommentsReflow.setSelection(store.getBoolean(Initializer.Pref_AS_MLCommentReflow));
		mASWrapMLCommentsKeepBlankLines.setSelection(store.getBoolean(Initializer.Pref_AS_MLCommentKeepBlankLines));
		mASWrapMLCommentsSeparateHeader.setSelection(store.getBoolean(Initializer.Pref_AS_MLCommentHeaderOnSeparateLine));
		mASWrapMLCommentsAsteriskMode.select(getComboIndexFromCommentAsteriskMode(store.getInt(Initializer.Pref_AS_MLCommentAsteriskMode)));

		mASKeepSpacesBeforeLineComments.setSelection(false);
		mASAlignCommentsAtColumn.setSelection(false);
		mASAddOneSpaceBeforeLineComments.setSelection(false);
		if (store.getBoolean(Initializer.Pref_AS_KeepSpacesBeforeLineComments))
		{
			mASKeepSpacesBeforeLineComments.setSelection(true);
		}
		else if (store.getInt(Initializer.Pref_AS_AlignLineCommentsAtColumn)>0)
		{
			mASAlignCommentsAtColumn.setSelection(true);
			mASAlignCommentsColumn.setSelection(store.getInt(Initializer.Pref_AS_AlignLineCommentsAtColumn));
		}
		else
		{
			mASAddOneSpaceBeforeLineComments.setSelection(true);
		}
			
//		mASKeepSpacesBeforeLineComments.setSelection(store.getBoolean(Initializer.Pref_AS_KeepSpacesBeforeLineComments));
		
		mASUseGlobalOpenBraceOnNewLine.setSelection(store.getBoolean(Initializer.Pref_AS_UseGlobalCRBeforeBrace));
		int braceItems=store.getInt(Initializer.Pref_AS_AdvancedCRBeforeBraceSettings);
		for (Button button : mASAdvancedOpenBraceButtons) {
			button.setSelection((braceItems & ((Integer)button.getData()).intValue())!=0);
		}
		
		mASUseAdvancedWrapping.setSelection(store.getBoolean(Initializer.Pref_AS_UseAdvancedWrapping));
		mASWrappingBreakOnPhrases.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingPreservePhrases));
		mASWrappingEnforceMax.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingEnforceMax));
		mASWrappingAllArgs.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllArgs));
		mASWrappingAllParms.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllParms));
		mASWrappingFirstArg.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstArg));
		mASWrappingFirstParm.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstParm));
		mASWrappingFirstArrayItem.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstArrayItem));
		mASWrappingFirstObjectItem.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstObjectItem));
		mASWrappingAlignArrayItems.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAlignArrayItems));
		mASWrappingAlignObjectItems.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAlignObjectItems));
		mASWrappingAllArrayItems.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllArrayItems));
		mASWrappingAllObjectItems.setSelection(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllObjectItems));
		mASWrappingPostMaxLeniency.setSelection(store.getInt(Initializer.Pref_AS_AdvancedWrappingGraceColumns));
		int selectedWrapItems=store.getInt(Initializer.Pref_AS_AdvancedWrappingElements);
		updateWrappingTable(selectedWrapItems);
		
		mMXMLRearrangeWhileFormatting.setSelection(store.getBoolean(PreferenceConstants.MXMLRearr_RearrangeWhileFormatting));
		mUseMXMLTagOrdering.setSelection(store.getBoolean(PreferenceConstants.MXMLRearr_UseRearrangeTagOrdering));
		mUseExcludeSubTags.setSelection(store.getBoolean(Initializer.Pref_MXML_UseTagsDoNotFormatInside));
		mMXMLSpacesAroundEquals.setSelection(store.getInt(Initializer.Pref_MXML_SpacesAroundEquals));
		mMXMLSpacesBeforeEmptyTagEnd.setSelection(store.getInt(Initializer.Pref_MXML_SpacesBeforeEmptyTagEnd));
		mMXMLUseSpacesInsideBraces.setSelection(store.getBoolean(Initializer.Pref_MXML_UseSpacesInsideAttributeBraces));
		mMXMLSpacesInsideBraces.setSelection(store.getInt(Initializer.Pref_MXML_SpacesInsideAttributeBraces));
		mMXMLFormatBindingExpressions.setSelection(store.getBoolean(Initializer.Pref_MXML_UseFormattingOfBoundAttributes));
		mMXMLKeepBlankLines.setSelection(store.getBoolean(Initializer.Pref_MXML_KeepBlankLines));
		mMXMLKeepRelativeIndentInsideMultilineComments.setSelection(store.getBoolean(Initializer.Pref_MXML_KeepRelativeIndentInMultilineComments));
		mMXMLBlankLinesBeforeComments.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesBeforeComments));
		mMXMLTabsBeforeCDATASpinner.setSelection(store.getInt(Initializer.Pref_MXML_ScriptCDataIndentTabs));
		mMXMLBlankLinesInsideCDATASpinner.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesAtCDataStart));
		mMXMLTabsBeforeScriptCodeSpinner.setSelection(store.getInt(Initializer.Pref_MXML_ScriptIndentTabs));
		mMXMLKeepScriptCDATAOnSameLine.setSelection(store.getBoolean(Initializer.Pref_MXML_KeepScriptCDataOnSameLine));
		mMXMLRemoveUnusedNamespacesButton.setSelection(store.getBoolean(Initializer.Pref_MXML_RemoveNamespacesAsPartOfFormat));
		mMXMLSpecialWrapTags.setSelection(store.getInt(Initializer.Pref_MXML_WrapIndentStyle)==WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT);
		mMXMLHangingIndentSize.setSelection(store.getInt(Initializer.Pref_MXML_TabsInHangingIndent));
		mMXMLRequireCDataButton.setSelection(store.getBoolean(Initializer.Pref_MXML_RequireCDATAForASFormatting));
		mSortMode=store.getInt(Initializer.Pref_MXML_SortAttrMode);
		mSortExtraAttrs=store.getBoolean(Initializer.Pref_MXML_SortExtraAttrs);
		mAddNewlineAfterLastAttr=store.getBoolean(Initializer.Pref_MXML_AddNewlineAfterLastAttr);
		mIndentTagClose=store.getBoolean(Initializer.Pref_MXML_IndentTagClose);
		String manualSortStrings=store.getString(Initializer.Pref_MXML_SortAttrData);
		mManualSortOrder=new ArrayList<String>();
		mManualSortOrder.addAll(Arrays.asList(manualSortStrings.split("\n")));
		mManualSortOrder.remove("");
		
		mMXMLAttrGroups=new ArrayList<AttrGroup>();
		String groupData=store.getString(Initializer.Pref_MXML_AttrGroups);
		String[] groups=groupData.split(LineSplitter);
		for (String g: groups) {
			AttrGroup group=AttrGroup.load(g);
			if (group!=null)
				mMXMLAttrGroups.add(group);
		}
		
		mAttrsPerLineSpinner.setSelection(store.getInt(Initializer.Pref_MXML_AttrsPerLine));
		mMXMLBlankLinesBeforeTagsSpinner.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesBeforeTags));
		mMXMLBlankLinesAfterSpecificTagsSpinner.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesAfterSpecificParentTags));
		mMXMLBlankLinesBetweenSiblingTagsSpinner.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesBetweenSiblingTags));
		mMXMLBlankLinesAfterParentTagsSpinner.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesAfterParentTags));
		mMXMLBlankLinesBeforeCloseTagsSpinner.setSelection(store.getInt(Initializer.Pref_MXML_BlankLinesBeforeClosingTags));
		mMXMLUseAttrsToKeepOnSameLineButton.setSelection(store.getBoolean(Initializer.Pref_MXML_UseAttrsToKeepOnSameLine));
		mMXMLAttrsToKeepOnSameLineSpinner.setSelection(store.getInt(Initializer.Pref_MXML_AttrsToKeepOnSameLine));
		mMXMLObeyMaxLength.setSelection(store.getBoolean(Initializer.Pref_MXML_AlwaysUseMaxLineLength));
		mMaxLineLengthSpinner.setSelection(store.getInt(Initializer.Pref_MXML_MaxLineLength));
		int wrapType=store.getInt(Initializer.Pref_MXML_AttrWrapMode);
		mWrapItemsPerLineButton.setSelection(false);
		mWrapLineLengthButton.setSelection(false);
		mWrapNoneButton.setSelection(false);
		switch (wrapType)
		{
		case MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE:
			mWrapItemsPerLineButton.setSelection(true);
			break;
		case MXMLPrettyPrinter.MXML_ATTR_WRAP_LINE_LENGTH:
			mWrapLineLengthButton.setSelection(true);
			break;
		default:
			mWrapNoneButton.setSelection(true);
			break;
		}
		
		updateTagTables(store.getString(Initializer.Pref_MXML_TagsCannotFormat), store.getString(Initializer.Pref_MXML_TagsCanFormat));
		updateTable(mTagsWithLeadingBlankLinesTable, store.getString(Initializer.Pref_MXML_TagsWithBlankLinesBefore));
		updateTable(mTagsWithTrailingBlankLinesTable, store.getString(Initializer.Pref_MXML_ParentTagsWithBlankLinesAfter));
		updateTable(mMXMLTagsContainingActionScriptTable, store.getString(Initializer.Pref_MXML_TagsWithASContent));
		updateTable(mExcludeSubTagsTable, store.getString(Initializer.Pref_MXML_TagsDoNotFormatInside));
		updateTable(mSingleLineMetaTagsTable, store.getString(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetProperty));
		addItemsToSimpleOrderingTable(store.getString(PreferenceConstants.MXMLRearr_RearrangeTagOrdering), mMXMLOrderTable);
//		updateTable(mMXMLOrderTable, store.getString(PreferenceConstants.MXMLRearr_RearrangeTagOrdering));
		
		////////////////////////////////////////////////////////////////////////////////////////
		//Rearrange widgets
		//modifier rearranging stuff
		mModifierOrders[Settings_Class]=store.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class);
		mModifierOrders[Settings_Function]=store.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		mModifierOrders[Settings_Property]=store.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property);
		
		mUseModifierOrders[Settings_Class]=store.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class);
		mUseModifierOrders[Settings_Function]=store.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		mUseModifierOrders[Settings_Property]=store.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property);
		
		mUseGlobalOrderButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements));
		
		mCurrentType=Settings_Function;
		updateSettingsForCombo();
		
		//element rearranging stuff
		
		mUseElementOrder.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseElementOrder));
		String orderData=store.getString(PreferenceConstants.ASRearr_ElementOrder);
		String[] items=orderData.split(PreferenceConstants.AS_Pref_Line_Separator);
		updateElementOrderTable(items);
		
		String blankLinesData=store.getString(PreferenceConstants.ASRearr_BlankLinesBeforeElement);
		ASRearranger.updateBlankLinesMap(mBlankLinesMap, blankLinesData);
		
		mFunctionUseSortOrder.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseFunctionVisibilityOrder));
		addItemsToOrderingTable(store.getString(PreferenceConstants.ASRearr_FunctionVisibilityOrder), mFunctionOrderTable);
		mFunctionSort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortFunctions));
		
		mPropertyUseSortOrder.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UsePropertyVisibilityOrder));
		addItemsToOrderingTable(store.getString(PreferenceConstants.ASRearr_PropertyVisibilityOrder), mPropertyOrderTable);
		mPropertySort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortProperties));
		mPropertyGrabGettersButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties));
		mPropertyAssociateGettersButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithProperties));
		mPropertyHeadersStyle=store.getInt(PreferenceConstants.ASRearr_AddDefaultHeaderForProperties);
//		mPropertyGettersHeaderButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedProperties));
//		mPropertyAlwaysHeaderButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAllProperties));

		mStaticFunctionUseSortOrder.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseStaticFunctionVisibilityOrder));
		addItemsToOrderingTable(store.getString(PreferenceConstants.ASRearr_StaticFunctionVisibilityOrder), mStaticFunctionOrderTable);
		mStaticFunctionSort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortStaticFunctions));
		
		mStaticPropertyUseSortOrder.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseStaticPropertyVisibilityOrder));
		addItemsToOrderingTable(store.getString(PreferenceConstants.ASRearr_StaticPropertyVisibilityOrder), mStaticPropertyOrderTable);
		mStaticPropertySort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortStaticProperties));
		mStaticPropertyGrabGettersButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties));
		mStaticPropertyAssociateGettersButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithStaticProperties));
		mStaticPropertyHeadersStyle=store.getInt(PreferenceConstants.ASRearr_AddDefaultHeaderForStaticProperties);
//		mStaticPropertyGettersHeaderButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedStaticProperties));
//		mStaticPropertyAlwaysHeaderButton.setSelection(store.getBoolean(PreferenceConstants.ASRearr_AddDefaultHeaderForAllStaticProperties));
		
		mImportSort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortImports));
		mImportEnableOrdering.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseImportOrder));
		mImportMoveOut.setSelection(store.getBoolean(PreferenceConstants.ASRearr_MoveImportsOutsideClass));
		addItemsToSimpleOrderingTable(store.getString(PreferenceConstants.ASRearr_ImportOrder), mImportTable);
		
		mIncludeSort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortIncludes));
		
		mMetatagUseSortOrder.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseMetatagOrder));
		mMetatagSort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortMetatags));
		mMetatagOrderTable.removeAll();
		addItemsToSimpleOrderingTable(store.getString(PreferenceConstants.ASRearr_MetatagOrder), mMetatagOrderTable);
		

		mNamespaceSort.setSelection(store.getBoolean(PreferenceConstants.ASRearr_SortNamespaces));
		
		SectionHeader header=SectionHeader.load(store.getString(PreferenceConstants.ASRearr_MajorSectionHeader));
		mMajorSectionSize.select(mMajorSectionSize.indexOf(Integer.toString(header.getExtraInternalLines())));
		mMajorSectionStyle.select(findDataIndex(mMajorSectionStyle, header.getStyle()));
		mMajorSectionWidth.setSelection(header.getWidth());
		mMajorSectionFillChar.setText(header.getFillChar());
		mMajorSectionPreLines.select(header.getLinesBefore());
		
		header=SectionHeader.load(store.getString(PreferenceConstants.ASRearr_MinorSectionHeader));
		mMinorSectionSize.select(mMinorSectionSize.indexOf(Integer.toString(header.getExtraInternalLines())));
		mMinorSectionStyle.select(findDataIndex(mMinorSectionStyle, header.getStyle()));
		mMinorSectionWidth.setSelection(header.getWidth());
		mMinorSectionFillChar.setText(header.getFillChar());
		mMinorSectionPreLines.select(header.getLinesBefore());

		header=SectionHeader.load(store.getString(PreferenceConstants.ASRearr_CopyrightHeader));
		mCopyrightSectionSize.select(mCopyrightSectionSize.indexOf(Integer.toString(header.getExtraInternalLines())));
		mCopyrightSectionStyle.select(findDataIndex(mCopyrightSectionStyle, header.getStyle()));
		mCopyrightSectionWidth.setSelection(header.getWidth());
		mCopyrightSectionFillChar.setText(header.getFillChar());
		mCopyrightSectionPostLines.select(header.getLinesBefore());
		String[] lines=header.getText();
		StringBuffer buffer=new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			buffer.append(line);
			if (i+1<lines.length)
				buffer.append('\n');
		}
		mCopyrightText.setText(buffer.toString());
		
		mUseCopyrightHeader.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseCopyright));
		mRemoveExistingCopyright.setSelection(store.getBoolean(PreferenceConstants.ASRearr_RemoveExistingCopyrightHeaders));
		
		mUseSectionComments.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseSectionHeaders));
		mUseSectionCommentsInMXML.setSelection(store.getBoolean(PreferenceConstants.ASRearr_UseSectionHeadersInMXML));
		mRemoveExistingSectionComments.setSelection(store.getBoolean(PreferenceConstants.ASRearr_RemoveAllExistingHeaders));
		mHeaderSpecs=ASRearranger.readHeaderSpecs(store.getString(PreferenceConstants.ASRearr_SectionHeaders));
		updatePrintStringsInOrderTable(mElementOrderTable);
		updatePrintStringsInOrderTable(mFunctionOrderTable);
		updatePrintStringsInOrderTable(mStaticFunctionOrderTable);
		updatePrintStringsInOrderTable(mPropertyOrderTable);
		updatePrintStringsInOrderTable(mStaticPropertyOrderTable);
		////////////////////////////////////////////////////////////////////////////////////////
		
		reformatText();
		updateRearrangeText();
		updateMXMLRearrangeText();
		enableWidgets();
	}
	
	private int findDataIndex(Combo combo, int code)
	{
		String[] items=combo.getItems();
		for (int i = 0; i < items.length; i++) {
			String text= items[i];
			Object data=combo.getData(text);
			if (data!=null && (data instanceof Integer))
			{
				if (((Integer)data).intValue()==code)
				{
					return i;
				}
			}
		}
		return 0;
	}
	
//	public static int getAdvancedBraceNewlineSettings(int advancedNewlineData)
//	{
//		targetMap.clear();
//		Properties braceProps=new Properties();
//		try {
//			braceProps.load(new StringReader(advancedNewlineData));
//			Enumeration keys=braceProps.keys();
//			while (keys.hasMoreElements())
//			{
//				String key=(String)keys.nextElement();
//				Boolean value=Boolean.parseBoolean(braceProps.getProperty(key));
//				targetMap.put(key, value);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	private int getComboIndexFromBraceStyle(int braceStyle)
	{
		String[] items=mASBraceStyle.getItems();
		for (int i = 0; i < items.length; i++) {
			String comboItem= items[i];
			Object data=mASBraceStyle.getData(comboItem);
			if (data instanceof Integer && ((Integer)data).intValue()==braceStyle)
				return i;
		}
		
		return 0;
	}

	private int getComboIndexFromCommentAsteriskMode(int asteriskStyle)
	{
		String[] items=mASWrapMLCommentsAsteriskMode.getItems();
		for (int i = 0; i < items.length; i++) {
			String comboItem= items[i];
			Object data=mASWrapMLCommentsAsteriskMode.getData(comboItem);
			if (data instanceof Integer && ((Integer)data).intValue()==asteriskStyle)
				return i;
		}
		
		return 0;
	}

	private void updateTable(Table table, String itemsString)
	{
		String[] tags=itemsString.split(",");
		table.removeAll();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				TableItem item=new TableItem(table, SWT.None);
				item.setText(tag);
			}
		}
	}
	
	private void updateTagTables(String neverFormatTags, String alwaysFormatTags)
	{
		updateTable(mNeverFormatTable, neverFormatTags);
		updateTable(mAlwaysFormatTable, alwaysFormatTags);
//		String[] tags=neverFormatTags.split(",");
//		mNeverFormatTable.removeAll();
//		for (String tag : tags) {
//			if (tag.length()>0)
//			{
//				TableItem item=new TableItem(mNeverFormatTable, SWT.None);
//				item.setText(tag);
//			}
//		}
//		
//		tags=alwaysFormatTags.split(",");
//		mAlwaysFormatTable.removeAll();
//		for (String tag : tags) {
//			if (tag.length()>0)
//			{
//				TableItem item=new TableItem(mAlwaysFormatTable, SWT.None);
//				item.setText(tag);
//			}
//		}
	}

	private void addWrapOptions(Combo wrapCombo, boolean xml)
	{
		String text="Don't format";
		wrapCombo.add(text);
		wrapCombo.setData(text, WrapOptions.WRAP_DONT_PROCESS);
		
		text="Format without changing newlines";
		wrapCombo.add(text);
		wrapCombo.setData(text, WrapOptions.WRAP_FORMAT_NO_CRs);
		
		text="Keep on single line";
		wrapCombo.add(text);
		wrapCombo.setData(text, WrapOptions.WRAP_NONE);
		
		text="Wrap to max length";
		wrapCombo.add(text);
		wrapCombo.setData(text, WrapOptions.WRAP_BY_COLUMN);

		text="Format without removing newlines";
		wrapCombo.add(text);
		wrapCombo.setData(text, WrapOptions.WRAP_BY_COLUMN_ONLY_ADD_CRS);
		
		if (xml)
		{
			text="Each tag on a new line";
			wrapCombo.add(text);
			wrapCombo.setData(text, WrapOptions.WRAP_BY_TAG);
		}
	}

	public static Composite createLabelComp(Composite parent, String label)
	{
		Composite labelComp=new Composite(parent, SWT.None);
		GridLayout gl=new GridLayout(2, false);
//		gl.marginWidth=0;
		gl.marginHeight=0;
//		gl.verticalSpacing=0;
		labelComp.setLayout(gl);
//		labelComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label l=new Label(labelComp, SWT.None);
		l.setText(label);
		return labelComp;
	}
	
	protected void reformatText()
	{
		if (!mVisualsInitialized)
			return;
		int asTop=mSampleASText.getTopIndex();
		int mxmlTop=mSampleMXMLText.getTopIndex();
		mSampleASText.setRedraw(false);
		mSampleMXMLText.setRedraw(false);
		reformatText(mSampleASText.getEditable() ? mSampleASText.getText() : mOriginalASSampleText, false, mSampleASText);
		reformatText(mSampleMXMLText.getEditable() ? mSampleMXMLText.getText() : mOriginalMXMLSampleText, true, mSampleMXMLText);
		mSampleASText.setTopIndex(asTop);
		mSampleMXMLText.setTopIndex(mxmlTop);
		mSampleASText.setRedraw(true);
		mSampleMXMLText.setRedraw(true);
	}

	protected void reformatText(String text, boolean mxml, StyledText widget)
	{
		//TODO: maybe add a processing step for removing namespaces so that the sample will be
		//responsive to that option.
		
		MXMLPrettyPrinter printer=new MXMLPrettyPrinter(text);
		printer.setDoFormat(true);
		
		int tabSize=3; //just pick a reasonable size since we don't know what size the actual editor will use

		///////set options
		PreferenceStore tempStore=new PreferenceStore();
		Properties props=new Properties();
		setProperties(props);
		for (Map.Entry<Object, Object> entry : props.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			tempStore.putValue(key, value);
		}
		FormatUtility.configureMXMLPrinter(printer, tempStore, tabSize);
		
		widget.setTabs(tabSize);
		try {
			String resultData=null;
			if (mxml)
				resultData=printer.print(0);
			else
			{
				printer.getASPrinter().setData(text);
				resultData=printer.getASPrinter().print(0);
			}
			if (resultData!=null)
			{
				if (!mxml)
					mASLineStyleListener.reset();
				else
					mMXMLLineStyleListener.reset();

				widget.setText(resultData);
			}
			else
			{
				PrefPage.showErrors(getShell(), printer.getParseErrors(), "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			List<Exception> exceptions=new ArrayList<Exception>();
			if (printer.getParseErrors()!=null)
				exceptions.addAll(printer.getParseErrors());
			exceptions.add(e);
			PrefPage.showErrors(getShell(), exceptions, "");
		}
	}

	private void updateWrappingTable(int selectedWrapItems)
	{
		mASWrappingItemsTable.removeAll();
		for (String breakItem : ASPrettyPrinter.Break_Table_Items.keySet()) {
			TableItem item=new TableItem(mASWrappingItemsTable, SWT.CHECK);
			item.setText(breakItem);
			Integer itemCode=ASPrettyPrinter.Break_Table_Items.get(breakItem);
			item.setData(itemCode);
			if ((itemCode & selectedWrapItems)!=0)
				item.setChecked(true);
			
		}
	}
	
	private void updatePrintStringsInOrderTable(Table table)
	{
		TableItem[] allItems=table.getItems();
		for (TableItem item : allItems) {
			updateItemText(item);
		}
	}

	private void updateElementOrderTable(String[] items)
	{
		mElementOrderTable.removeAll();
		for (String element : items)
		{
			TableItem item=new TableItem(mElementOrderTable, SWT.None);
			if (element.startsWith(PreferenceConstants.AS_Pref_MemberSpecPrefix))
			{
				MemberSelectionSpec spec=new MemberSelectionSpec();
				spec.initializeFromData(element.substring(1));
				item.setData(spec);
			}
			else
				item.setData(new ElementHolder(element));
			updateItemText(item);
		}
	}

	void setProperties(Properties store)
	{
		//using putValue() so that only empty strings will be ignored because they match the "default"
		store.put(Initializer.Pref_Flex_UseTabs, Boolean.toString(mUseTabsRadio.getSelection()));
//		store.put(Initializer.Pref_Flex_IndentSize, mIndentSizeSpinner.getSelection());
//		store.put(Initializer.Pref_Flex_TabSize, mTabSizeSpinner.getSelection());
		
		store.put(Initializer.Pref_AS_SpacesBeforeComma, Integer.toString(mSpacesBeforeComma.getSelection()));
		store.put(Initializer.Pref_AS_SpacesAfterComma, Integer.toString(mSpacesAfterComma.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesBeforeFunctions, Integer.toString(mBlankLinesBeforeFunctions.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesBeforeControlStatements, Integer.toString(mASBlankLinesBeforeControlStatement.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesBeforeImportBlock, Integer.toString(mASBlankLinesBeforeImports.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesBeforeProperties, Integer.toString(mASBlankLinesBeforeProperties.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesAtFunctionStart, Integer.toString(mASBlankLinesAtFunctionStart.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesAtFunctionEnd, Integer.toString(mASBlankLinesAtFunctionEnd.getSelection()));
		store.put(Initializer.Pref_AS_SpacesInsideParens, Integer.toString(mASSpacesInsideParens.getSelection()));
		store.put(Initializer.Pref_AS_UseGlobalSpacesInsideParens, Boolean.toString(mASUseGlobalSpacesInsideParens.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedSpacesInsideArrayDeclBrackets, Integer.toString(mASAdvancedSpacesInsideArrayDeclBrackets.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedSpacesInsideArrayRefBrackets, Integer.toString(mASAdvancedSpacesInsideArrayRefBrackets.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedSpacesInsideLiteralBraces, Integer.toString(mASAdvancedSpacesInsideObjectLiteralBraces.getSelection()));
		store.put( Initializer.Pref_AS_AdvancedSpacesInsideParensInOtherPlaces, Integer.toString( mASAdvancedSpacesInsideParensInOtherPlaces.getSelection() ) );
		store.put( Initializer.Pref_AS_AdvancedSpacesInsideParensInParameterLists, Integer.toString( mASAdvancedSpacesInsideParensInParameterLists.getSelection() ) );
		store.put( Initializer.Pref_AS_AdvancedSpacesInsideParensInArgumentLists, Integer.toString( mASAdvancedSpacesInsideParensInArgumentLists.getSelection() ) );
		
		store.put(Initializer.Pref_AS_CollapseSpacesForAdjacentParens, Boolean.toString(mASCollapseAdjacentParens.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesBeforeClasses, Integer.toString(mASBlankLinesBeforeClass.getSelection()));
		store.put(Initializer.Pref_AS_OpenBraceOnNewLine, Boolean.toString(mOpenBraceOnNewLine.getSelection()));
		store.put(Initializer.Pref_AS_KeepBlankLines, Boolean.toString(mKeepBlankLines.getSelection()));
		store.put(Initializer.Pref_AS_BlankLinesToKeep, Integer.toString(mASBlankLinesToKeep.getSelection()));
		store.put(Initializer.Pref_AS_CatchOnNewLine, Boolean.toString(mASCRBeforeCatch.getSelection()));
		store.put(Initializer.Pref_AS_UseBraceStyle, Boolean.toString(mASUseBraceStyle.getSelection()));
		store.put(Initializer.Pref_AS_UseGlobalCRBeforeBrace, Boolean.toString(mASUseGlobalOpenBraceOnNewLine.getSelection()));
		
		int braceSettings=0;
		for (Button button : mASAdvancedOpenBraceButtons)
		{
			Integer braceCode=(Integer)button.getData();
			if (braceCode!=null && button.getSelection())
				braceSettings|=braceCode.intValue();
		}
		store.put(Initializer.Pref_AS_AdvancedCRBeforeBraceSettings, Integer.toString(braceSettings));
		
		store.put(Initializer.Pref_AS_BraceStyle, ((Integer)mASBraceStyle.getData(mASBraceStyle.getItem(mASBraceStyle.getSelectionIndex()))).toString());
		store.put(Initializer.Pref_AS_ElseOnNewLine, Boolean.toString(mASCRBeforeElse.getSelection()));
		store.put(Initializer.Pref_AS_WhileOnNewLine, Boolean.toString(mASCRBeforeWhile.getSelection()));
		store.put(Initializer.Pref_AS_NoNewCRsBeforeBreak, Boolean.toString(mASNoCRBeforeBreak.getSelection()));
		store.put(Initializer.Pref_AS_NoNewCRsBeforeContinue, Boolean.toString(mASNoCRBeforeContinue.getSelection()));
		store.put(Initializer.Pref_AS_NoNewCRsBeforeReturn, Boolean.toString(mASNoCRBeforeReturn.getSelection()));
		store.put(Initializer.Pref_AS_NoNewCRsBeforeThrow, Boolean.toString(mASNoCRBeforeThrow.getSelection()));
		store.put(Initializer.Pref_AS_NoNewCRsBeforeExpression, Boolean.toString(mASNoCRBeforeExpression.getSelection()));
		store.put(Initializer.Pref_AS_ElseIfOnSameLine, Boolean.toString(mKeepElseIfOnSameLine.getSelection()));
		store.put(Initializer.Pref_AS_MaxLineLength, Integer.toString(mASMaxLineLength.getSelection()));
		store.put(Initializer.Pref_AS_SpacesAroundAssignment, Integer.toString(mSpacesAroundAssignment.getSelection()));
		store.put(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInOptionalParameters, Integer.toString(mASSpacesAroundAssignmentInParameters.getSelection()));
		store.put(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInMetatags, Integer.toString(mASSpacesAroundAssignmentInMetatags.getSelection()));
		store.put(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInOptionalParameters, Boolean.toString(mASUseSpacesAroundAssignmentInParameters.getSelection()));
		store.put(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInMetatags, Boolean.toString(mASUseSpacesAroundAssignmentInMetatags.getSelection()));
		store.put(Initializer.Pref_AS_SpacesAroundSymbolicOperator, Integer.toString(mSpacesAroundSymbolicOperator.getSelection()));
		store.put(Initializer.Pref_AS_SpacesAroundColons, Integer.toString(mASSpacesAroundColons.getSelection()));
		store.put(Initializer.Pref_AS_TabsInHangingIndent, Integer.toString(mASHangingIndentSize.getSelection()));
		store.put( Initializer.Pref_AS_AdvancedSpacesAfterColonsInDeclarations, Integer.toString( mASAdvancedSpacesAfterColonsInDeclarations.getSelection() ) );
		store.put( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInDeclarations, Integer.toString( mASAdvancedSpacesBeforeColonsInDeclarations.getSelection() ) );
		store.put( Initializer.Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes, Integer.toString( mASAdvancedSpacesAfterColonsInFunctions.getSelection() ) );
		store.put( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes, Integer.toString( mASAdvancedSpacesBeforeColonsInFunctions.getSelection() ) );
		store.put(Initializer.Pref_AS_UseGlobalSpacesAroundColons, Boolean.toString(mASUseGlobalSpacesAroundColons.getSelection()));
		store.put(Initializer.Pref_AS_KeepSLCommentsOnColumn1, Boolean.toString(mKeepSLCommentsOnColumn1.getSelection()));
		store.put(Initializer.Pref_AS_AlwaysGenerateIndent, Boolean.toString(mASAlwaysGenerateIndent.getSelection()));
		store.put(Initializer.Pref_AS_SpacesBeforeOpenControlParen, Integer.toString(mASSpacesBeforeOpenControlParen.getSelection()));
		store.put(Initializer.Pref_AS_SpacesBeforeFormalParameters, Integer.toString(mASSpacesBeforeDeclParameters.getSelection()));
		store.put(Initializer.Pref_AS_SpacesBeforeArguments, Integer.toString(mASSpacesBeforeArguments.getSelection()));
		store.put(Initializer.Pref_AS_NewlineBeforeBindableFunction, Boolean.toString(mASNewlineBeforeBindableFunction.getSelection()));
		store.put(Initializer.Pref_AS_NewlineBeforeBindableProperty, Boolean.toString(mASNewlineBeforeBindableProperty.getSelection()));
		store.put(Initializer.Pref_AS_UseGnuBraceIndent, Boolean.toString(mASUseGnuBraceIndent.getSelection()));
		IPreferenceStore mainAppStore=Activator.getDefault().getPreferenceStore();
		store.put(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetFunction, mainAppStore.getDefaultString(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetFunction));
		store.put(Initializer.Pref_AS_PutEmptyStatementsOnNewLine, Boolean.toString(mASEmptyStatementsOnNewLine.getSelection()));
		store.put(Initializer.Pref_AS_DontIndentPackageItems, Boolean.toString(mASDontIndentPackageElements.getSelection()));
		store.put(Initializer.Pref_AS_DontIndentSwitchCases, Boolean.toString(mASDontIndentSwitchCases.getSelection()));
		store.put(Initializer.Pref_AS_UnindentExpressionTerminators, Boolean.toString(mASNoIndentForExpressionTerminatorButton.getSelection()));
		store.put(Initializer.Pref_AS_LeaveExtraWhitespaceAroundVarDecls, Boolean.toString(mASLeaveExtraWhitespaceAroundVarDecls.getSelection()));
		store.put(Initializer.Pref_AS_LeaveSingleLineFunctions, Boolean.toString(mASKeepSingleLineFunctions.getSelection()));
		store.put(Initializer.Pref_AS_AlignDeclEquals, Boolean.toString(mASAlignDeclEquals.getSelection()));
		store.put(Initializer.Pref_AS_AlignDeclMode, Integer.toString(mASAlignDeclEqualsConsecutive.getSelection() ? ASPrettyPrinter.Decl_Align_Consecutive : ASPrettyPrinter.Decl_Align_Scope));
//		store.put(Initializer.Pref_AS_EnsureConditionalsHaveBraces, Boolean.toString(mASEnsureConditionalBraces.getSelection()));
		int conditionalCode=ASPrettyPrinter.Braces_NoModify;
		if (mASEnsureConditionalBraces.getSelection())
			conditionalCode=ASPrettyPrinter.Braces_AddIfMissing;
		else if (mASSmartAddConditionalBraces.getSelection())
			conditionalCode=ASPrettyPrinter.Braces_AddSmart;
		else if (mASSmartAddRemoveConditionalBraces.getSelection())
			conditionalCode=ASPrettyPrinter.Braces_AddRemoveSmart;
		store.put(Initializer.Pref_AS_AddBracesToConditionals, Integer.toString(conditionalCode));
//		store.put(Initializer.Pref_AS_EnsureLoopsHaveBraces, Boolean.toString(mASEnsureLoopBraces.getSelection()));
		int loopCode=ASPrettyPrinter.Braces_NoModify;
		if (mASEnsureLoopBraces.getSelection())
			loopCode=ASPrettyPrinter.Braces_AddIfMissing;
		else if (mASSmartAddLoopBraces.getSelection())
			loopCode=ASPrettyPrinter.Braces_AddSmart;
		else if (mASSmartAddRemoveLoopBraces.getSelection())
			loopCode=ASPrettyPrinter.Braces_AddRemoveSmart;
		store.put(Initializer.Pref_AS_AddBracesToLoops, Integer.toString(loopCode));
			
//		store.put(Initializer.Pref_AS_EnsureSwitchCasesHaveBraces, Boolean.toString(mASEnsureSwitchBraces.getSelection()));
		int caseCode=ASPrettyPrinter.Braces_NoModify;
		if (mASSmartAddSwitchBraces.getSelection())
			caseCode=ASPrettyPrinter.Braces_AddSmart;
		else if (mASRemoveSwitchBraces.getSelection())
			caseCode=ASPrettyPrinter.Braces_RemoveUnnecessary;
		store.put(Initializer.Pref_AS_AddBracesToCases, Integer.toString(caseCode));

		//special handling to convert the radio buttons to the separate settings that correspond to these options
		store.put(Initializer.Pref_AS_KeepSpacesBeforeLineComments, Boolean.toString(false));
		store.put(Initializer.Pref_AS_AlignLineCommentsAtColumn, Integer.toString(0));
		if (mASKeepSpacesBeforeLineComments.getSelection())
		{
			store.put(Initializer.Pref_AS_KeepSpacesBeforeLineComments, Boolean.toString(true));
		}
		else if (mASAlignCommentsAtColumn.getSelection())
		{
			store.put(Initializer.Pref_AS_AlignLineCommentsAtColumn, Integer.toString(mASAlignCommentsColumn.getSelection()));
		}
//		store.put(Initializer.Pref_AS_KeepSpacesBeforeLineComments, Boolean.toString(mASKeepSpacesBeforeLineComments.getSelection()));
		
		store.put(Initializer.Pref_AS_KeepRelativeIndentInDocComments, Boolean.toString(mASKeepRelativeIndentOfMultilineComments.getSelection()));
		store.put(Initializer.Pref_AS_RearrangeAsPartOfFormat, Boolean.toString(mASRearrangeDuringFormatting.getSelection()));
		store.put(Initializer.Pref_AS_BreakLinesBeforeComma, Boolean.toString(mASBreakLinesBeforeComma.getSelection()));
		store.put(Initializer.Pref_AS_BreakLinesBeforeArithmetic, Boolean.toString(mASBreakLinesBeforeArithmeticOperator.getSelection()));
		store.put(Initializer.Pref_AS_BreakLinesBeforeLogical, Boolean.toString(mASBreakLinesBeforeLogicalOperator.getSelection()));
		store.put(Initializer.Pref_AS_BreakLinesBeforeAssignment, Boolean.toString(mASBreakLinesBeforeAssignment.getSelection()));
		store.put(Initializer.Pref_AS_WrapArrayDeclMode, ((Integer)mASArrayDeclWrapCombo.getData(mASArrayDeclWrapCombo.getText())).toString());
		store.put(Initializer.Pref_AS_WrapExpressionMode, ((Integer)mASExpressionWrapCombo.getData(mASExpressionWrapCombo.getText())).toString());
		store.put(Initializer.Pref_AS_WrapMethodCallMode, ((Integer)mASMethodCallWrapCombo.getData(mASMethodCallWrapCombo.getText())).toString());
		store.put(Initializer.Pref_AS_WrapMethodDeclMode, ((Integer)mASMethodDeclWrapCombo.getData(mASMethodDeclWrapCombo.getText())).toString());
		store.put(Initializer.Pref_AS_WrapXMLMode, ((Integer)mASXMLWrapCombo.getData(mASXMLWrapCombo.getText())).toString());
		store.put(Initializer.Pref_AS_WrapIndentStyle, Integer.valueOf(mASSpecialWrapCommaItems.getSelection() ? WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT : WrapOptions.WRAP_STYLE_INDENT_NORMAL).toString());
//		store.put(Initializer.Pref_AS_TrimTrailingWhitespace, Boolean.toString(mASTrimTrailingWS.getSelection()));
		store.put(Initializer.Pref_AS_SpacesAfterLabel, Integer.toString(mASSpacesAfterLabelColon.getSelection()));

		store.put(Initializer.Pref_AS_DocCommentHangingIndentTabs, Integer.toString(mASWrapDocCommentsHangingTabs.getSelection()));
		store.put(Initializer.Pref_AS_UseLineCommentWrapping, Boolean.toString(mASWrapLineComments.getSelection()));
		store.put(Initializer.Pref_AS_UseDocCommentWrapping, Boolean.toString(mASWrapASDocComments.getSelection()));
		store.put(Initializer.Pref_AS_UseMLCommentWrapping, Boolean.toString(mASWrapMLComments.getSelection()));
		store.put(Initializer.Pref_AS_MLCommentReflow, Boolean.toString(mASWrapMLCommentsReflow.getSelection()));
		store.put(Initializer.Pref_AS_DocCommentReflow, Boolean.toString(mASWrapDocCommentsReflow.getSelection()));
		store.put(Initializer.Pref_AS_MLCommentKeepBlankLines, Boolean.toString(mASWrapMLCommentsKeepBlankLines.getSelection()));
		store.put(Initializer.Pref_AS_DocCommentKeepBlankLines, Boolean.toString(mASWrapDocCommentsKeepBlankLines.getSelection()));
		store.put(Initializer.Pref_AS_MLCommentHeaderOnSeparateLine, Boolean.toString(mASWrapMLCommentsSeparateHeader.getSelection()));
		store.put(Initializer.Pref_AS_MLCommentAsteriskMode, Integer.toString((Integer)mASWrapMLCommentsAsteriskMode.getData(mASWrapMLCommentsAsteriskMode.getText()))); 
		
		
		store.put(Initializer.Pref_AS_UseAdvancedWrapping, Boolean.toString(mASUseAdvancedWrapping.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingEnforceMax, Boolean.toString(mASWrappingEnforceMax.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingAllArgs, Boolean.toString(mASWrappingAllArgs.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingAllParms, Boolean.toString(mASWrappingAllParms.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingFirstArg, Boolean.toString(mASWrappingFirstArg.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingFirstParm, Boolean.toString(mASWrappingFirstParm.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingFirstArrayItem, Boolean.toString(mASWrappingFirstArrayItem.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingFirstObjectItem, Boolean.toString(mASWrappingFirstObjectItem.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingAlignArrayItems, Boolean.toString(mASWrappingAlignArrayItems.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingAlignObjectItems, Boolean.toString(mASWrappingAlignObjectItems.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingAllArrayItems, Boolean.toString(mASWrappingAllArrayItems.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingAllObjectItems, Boolean.toString(mASWrappingAllObjectItems.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingGraceColumns, Integer.toString(mASWrappingPostMaxLeniency.getSelection()));
		store.put(Initializer.Pref_AS_AdvancedWrappingPreservePhrases, Boolean.toString(mASWrappingBreakOnPhrases.getSelection()));
		TableItem[] items=mASWrappingItemsTable.getItems();
		int wrappingElementMask=0;
		for (TableItem tableItem : items) {
			if (tableItem.getChecked())
			{
				Integer bitCode=(Integer)tableItem.getData();
				wrappingElementMask|=bitCode;
			}
		}
		store.put(Initializer.Pref_AS_AdvancedWrappingElements, Integer.toString(wrappingElementMask));
		
		store.put(PreferenceConstants.MXMLRearr_RearrangeWhileFormatting, Boolean.toString(mMXMLRearrangeWhileFormatting.getSelection()));
		store.put(PreferenceConstants.MXMLRearr_UseRearrangeTagOrdering, Boolean.toString(mUseMXMLTagOrdering.getSelection()));
//		store.put(Initializer.Pref_MXML_BlockIndent, Boolean.toString(mMXMLBlockIndent.getSelection());
		store.put(Initializer.Pref_MXML_UseTagsDoNotFormatInside, Boolean.toString(mUseExcludeSubTags.getSelection()));
		store.put(Initializer.Pref_MXML_SpacesAroundEquals, Integer.toString(mMXMLSpacesAroundEquals.getSelection()));
		store.put(Initializer.Pref_MXML_SpacesBeforeEmptyTagEnd, Integer.toString(mMXMLSpacesBeforeEmptyTagEnd.getSelection()));
		store.put(Initializer.Pref_MXML_UseSpacesInsideAttributeBraces, Boolean.toString(mMXMLUseSpacesInsideBraces.getSelection()));
		store.put(Initializer.Pref_MXML_SpacesInsideAttributeBraces, Integer.toString(mMXMLSpacesInsideBraces.getSelection()));
		store.put(Initializer.Pref_MXML_UseFormattingOfBoundAttributes, Boolean.toString(mMXMLFormatBindingExpressions.getSelection()));
		store.put(Initializer.Pref_MXML_KeepBlankLines, Boolean.toString(mMXMLKeepBlankLines.getSelection()));
		store.put(Initializer.Pref_MXML_KeepRelativeIndentInMultilineComments, Boolean.toString(mMXMLKeepRelativeIndentInsideMultilineComments.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesBeforeComments, Integer.toString(mMXMLBlankLinesBeforeComments.getSelection()));
		store.put(Initializer.Pref_MXML_ScriptCDataIndentTabs, Integer.toString(mMXMLTabsBeforeCDATASpinner.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesAtCDataStart, Integer.toString(mMXMLBlankLinesInsideCDATASpinner.getSelection()));
		store.put(Initializer.Pref_MXML_ScriptIndentTabs, Integer.toString(mMXMLTabsBeforeScriptCodeSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_KeepScriptCDataOnSameLine, Boolean.toString(mMXMLKeepScriptCDATAOnSameLine.getSelection()));
		store.put(Initializer.Pref_MXML_RemoveNamespacesAsPartOfFormat, Boolean.toString(mMXMLRemoveUnusedNamespacesButton.getSelection()));
		store.put(Initializer.Pref_MXML_WrapIndentStyle, Integer.valueOf(mMXMLSpecialWrapTags.getSelection() ? WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT : WrapOptions.WRAP_STYLE_INDENT_NORMAL).toString());
		store.put(Initializer.Pref_MXML_TabsInHangingIndent, Integer.toString(mMXMLHangingIndentSize.getSelection()));
		store.put(Initializer.Pref_MXML_SortAttrMode, Integer.toString(mSortMode));
		store.put(Initializer.Pref_MXML_SortExtraAttrs, Boolean.toString(mSortExtraAttrs));
		store.put(Initializer.Pref_MXML_AddNewlineAfterLastAttr, Boolean.toString(mAddNewlineAfterLastAttr));
		store.put(Initializer.Pref_MXML_IndentTagClose, Boolean.toString(mIndentTagClose));
		store.put(Initializer.Pref_MXML_MaxLineLength, Integer.toString(mMaxLineLengthSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_AttrsPerLine, Integer.toString(mAttrsPerLineSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_UseAttrsToKeepOnSameLine, Boolean.toString(mMXMLUseAttrsToKeepOnSameLineButton.getSelection()));
		store.put(Initializer.Pref_MXML_RequireCDATAForASFormatting, Boolean.toString(mMXMLRequireCDataButton.getSelection()));
		store.put(Initializer.Pref_MXML_AttrsToKeepOnSameLine, Integer.toString(mMXMLAttrsToKeepOnSameLineSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_AlwaysUseMaxLineLength, Boolean.toString(mMXMLObeyMaxLength.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesBeforeTags, Integer.toString(mMXMLBlankLinesBeforeTagsSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesAfterSpecificParentTags, Integer.toString(mMXMLBlankLinesAfterSpecificTagsSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesBetweenSiblingTags, Integer.toString(mMXMLBlankLinesBetweenSiblingTagsSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesAfterParentTags, Integer.toString(mMXMLBlankLinesAfterParentTagsSpinner.getSelection()));
		store.put(Initializer.Pref_MXML_BlankLinesBeforeClosingTags, Integer.toString(mMXMLBlankLinesBeforeCloseTagsSpinner.getSelection()));
		int wrapType=0;
		if (mWrapItemsPerLineButton.getSelection())
			wrapType=MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE;
		else if (mWrapNoneButton.getSelection())
			wrapType=MXMLPrettyPrinter.MXML_ATTR_WRAP_NONE;
		else if (mWrapLineLengthButton.getSelection())
			wrapType=MXMLPrettyPrinter.MXML_ATTR_WRAP_LINE_LENGTH;
		store.put(Initializer.Pref_MXML_AttrWrapMode, Integer.toString(wrapType));
		StringBuffer buffer=new StringBuffer();
		for (String attr : mManualSortOrder) {
			if (attr.length()>0)
			{
				buffer.append(attr);
				buffer.append('\n');
			}
		}
		store.put(Initializer.Pref_MXML_SortAttrData, buffer.toString());
		
		buffer=new StringBuffer();
		for (AttrGroup group : mMXMLAttrGroups) {
			buffer.append(group.save());
			buffer.append(LineSplitter);
		}
		store.put(Initializer.Pref_MXML_AttrGroups, buffer.toString());
		
		items=mNeverFormatTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_MXML_TagsCannotFormat, buffer.toString());
		
		items=mAlwaysFormatTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_MXML_TagsCanFormat, buffer.toString());
		
		items=mTagsWithLeadingBlankLinesTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_MXML_TagsWithBlankLinesBefore, buffer.toString());		
		
		items=mTagsWithTrailingBlankLinesTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_MXML_ParentTagsWithBlankLinesAfter, buffer.toString());		
		
		items=mMXMLTagsContainingActionScriptTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_MXML_TagsWithASContent, buffer.toString());		
		
		items=mExcludeSubTagsTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_MXML_TagsDoNotFormatInside, buffer.toString());		
		
		items=mSingleLineMetaTagsTable.getItems();
		buffer=new StringBuffer();
		for (TableItem item : items) {
			buffer.append(item.getText());
			buffer.append(',');
		}
		store.put(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetProperty, buffer.toString());		
		
		saveSimpleTableOrder(store, mMXMLOrderTable, PreferenceConstants.MXMLRearr_RearrangeTagOrdering);
//		items=mMXMLOrderTable.getItems();
//		buffer=new StringBuffer();
//		for (TableItem item : items) {
//			buffer.append(item.getText());
//			buffer.append(PreferenceConstants.AS_Pref_Line_Separator);
//		}
//		store.put(PreferenceConstants.MXMLRearr_RearrangeTagOrdering, buffer.toString());		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		//modifier ordering stuff
		saveSettingsForCombo(mCurrentType);
		
		store.put(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class, mModifierOrders[Settings_Class]);
		store.put(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function, mModifierOrders[Settings_Function]);
		store.put(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property, mModifierOrders[Settings_Property]);
		
		store.put(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class, Boolean.toString(mUseModifierOrders[Settings_Class]));
		store.put(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function, Boolean.toString(mUseModifierOrders[Settings_Function]));
		store.put(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property, Boolean.toString(mUseModifierOrders[Settings_Property]));
		
		store.put(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements, Boolean.toString(mUseGlobalOrderButton.getSelection()));
		
		//element rearranging stuff
		saveVisibilityOrder(store, mFunctionOrderTable, PreferenceConstants.ASRearr_FunctionVisibilityOrder);
		saveVisibilityOrder(store, mStaticFunctionOrderTable, PreferenceConstants.ASRearr_StaticFunctionVisibilityOrder);
		saveVisibilityOrder(store, mPropertyOrderTable, PreferenceConstants.ASRearr_PropertyVisibilityOrder);
		saveVisibilityOrder(store, mStaticPropertyOrderTable, PreferenceConstants.ASRearr_StaticPropertyVisibilityOrder);
		saveSimpleTableOrder(store, mMetatagOrderTable, PreferenceConstants.ASRearr_MetatagOrder);
		saveSimpleTableOrder(store, mImportTable, PreferenceConstants.ASRearr_ImportOrder);
		
//		saveSimpleTableOrder(store, mElementOrderTable, PreferenceConstants.ASRearr_ElementOrder);
		TableItem[] allItems=mElementOrderTable.getItems();
		buffer=new StringBuffer();
		for (TableItem tableItem : allItems)
		{
			Object data=tableItem.getData();
			if (data instanceof MemberSelectionSpec)
				buffer.append(PreferenceConstants.AS_Pref_MemberSpecPrefix+((MemberSelectionSpec)data).persist());
			else
				buffer.append(tableItem.getData().toString());
			buffer.append(PreferenceConstants.AS_Pref_Line_Separator);
		}
		store.put(PreferenceConstants.ASRearr_ElementOrder, buffer.toString());
		
		
		store.put(PreferenceConstants.ASRearr_UseImportOrder, Boolean.toString(mImportEnableOrdering.getSelection()));
		store.put(PreferenceConstants.ASRearr_MoveImportsOutsideClass, Boolean.toString(mImportMoveOut.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortImports, Boolean.toString(mImportSort.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortIncludes, Boolean.toString(mIncludeSort.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortMetatags, Boolean.toString(mMetatagSort.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortNamespaces, Boolean.toString(mNamespaceSort.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortProperties, Boolean.toString(mPropertySort.getSelection()));
		store.put(PreferenceConstants.ASRearr_UsePropertyVisibilityOrder, Boolean.toString(mPropertyUseSortOrder.getSelection()));
		store.put(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties, Boolean.toString(mPropertyGrabGettersButton.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortGettersAndSettersWithProperties, Boolean.toString(mPropertyAssociateGettersButton.getSelection()));
		store.put(PreferenceConstants.ASRearr_AddDefaultHeaderForProperties, Integer.toString(mPropertyHeadersStyle));
//		store.put(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedProperties, Boolean.toString(mPropertyGettersHeaderButton.getSelection()));
//		store.put(PreferenceConstants.ASRearr_AddDefaultHeaderForAllProperties, Boolean.toString(mPropertyAlwaysHeaderButton.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortFunctions, Boolean.toString(mFunctionSort.getSelection()));
		store.put(PreferenceConstants.ASRearr_UseFunctionVisibilityOrder, Boolean.toString(mFunctionUseSortOrder.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortStaticFunctions, Boolean.toString(mStaticFunctionSort.getSelection()));
		store.put(PreferenceConstants.ASRearr_UseStaticFunctionVisibilityOrder, Boolean.toString(mStaticFunctionUseSortOrder.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortStaticProperties, Boolean.toString(mStaticPropertySort.getSelection()));
		store.put(PreferenceConstants.ASRearr_UseStaticPropertyVisibilityOrder, Boolean.toString(mStaticPropertyUseSortOrder.getSelection()));
		store.put(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties, Boolean.toString(mStaticPropertyGrabGettersButton.getSelection()));
		store.put(PreferenceConstants.ASRearr_SortGettersAndSettersWithStaticProperties, Boolean.toString(mStaticPropertyAssociateGettersButton.getSelection()));
		store.put(PreferenceConstants.ASRearr_AddDefaultHeaderForStaticProperties, Integer.toString(mStaticPropertyHeadersStyle));
//		store.put(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedStaticProperties, Boolean.toString(mStaticPropertyGettersHeaderButton.getSelection()));
//		store.put(PreferenceConstants.ASRearr_AddDefaultHeaderForAllStaticProperties, Boolean.toString(mStaticPropertyAlwaysHeaderButton.getSelection()));
		store.put(PreferenceConstants.ASRearr_UseElementOrder, Boolean.toString(mUseElementOrder.getSelection()));
		store.put(PreferenceConstants.ASRearr_UseMetatagOrder, Boolean.toString(mMetatagUseSortOrder.getSelection()));
		
		buffer=new StringBuffer();
		for (Map.Entry<String, Integer> entry : mBlankLinesMap.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(PreferenceConstants.AS_Pref_Equals);
			buffer.append(entry.getValue().toString());
			buffer.append(PreferenceConstants.AS_Pref_Line_Separator);
		}
		store.put(PreferenceConstants.ASRearr_BlankLinesBeforeElement, buffer.toString());
		
		store.put(PreferenceConstants.ASRearr_MajorSectionHeader, getMajorSectionHeader(new String[]{}).save());
		store.put(PreferenceConstants.ASRearr_MinorSectionHeader, getMinorSectionHeader(new String[]{}).save());
		store.put(PreferenceConstants.ASRearr_UseSectionHeaders, Boolean.toString(mUseSectionComments.getSelection()));
		store.put(PreferenceConstants.ASRearr_UseSectionHeadersInMXML, Boolean.toString(mUseSectionCommentsInMXML.getSelection()));
		store.put(PreferenceConstants.ASRearr_RemoveAllExistingHeaders, Boolean.toString(mRemoveExistingSectionComments.getSelection()));
		
		store.put(PreferenceConstants.ASRearr_CopyrightHeader, getCopyrightHeader(mCopyrightText).save());
		store.put(PreferenceConstants.ASRearr_UseCopyright, Boolean.toString(mUseCopyrightHeader.getSelection()));
		store.put(PreferenceConstants.ASRearr_RemoveExistingCopyrightHeaders, Boolean.toString(mRemoveExistingCopyright.getSelection()));
		
		try
		{
			buffer=new StringBuffer();
			Properties props=new Properties();
			for (Map.Entry<String, SectionSpec> header : mHeaderSpecs.entrySet()) {
				props.put(header.getKey(), header.getValue().save());
			}
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			props.store(os, "");
			os.close();
			store.put(PreferenceConstants.ASRearr_SectionHeaders, new String(os.toByteArray()));
		}
		catch (Exception e)
		{
			Activator.logException(e, "");
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	void enableWidgets()
	{
		if (mDisableMode)
		{
			return;
		}
		mNoFormatWarning.setVisible(false);
		mSpacesGroup.setText("Spaces");
		if (((Integer)mASArrayDeclWrapCombo.getData(mASArrayDeclWrapCombo.getText())).intValue()==WrapOptions.WRAP_DONT_PROCESS
				|| ((Integer)mASExpressionWrapCombo.getData(mASExpressionWrapCombo.getText())).intValue()==WrapOptions.WRAP_DONT_PROCESS
				|| ((Integer)mASMethodCallWrapCombo.getData(mASMethodCallWrapCombo.getText())).intValue()==WrapOptions.WRAP_DONT_PROCESS
				|| ((Integer)mASMethodDeclWrapCombo.getData(mASMethodDeclWrapCombo.getText())).intValue()==WrapOptions.WRAP_DONT_PROCESS
			)
		{
			mNoFormatWarning.setVisible(true);
			mSpacesGroup.setText("Spaces - WARNING: current wrap settings may override some options");
		}
		
		mNeverFormatRemoveButton.setEnabled(mNeverFormatTable.getSelectionCount()>0);
		mAlwaysFormatRemoveButton.setEnabled(mAlwaysFormatTable.getSelectionCount()>0);
		
		mExcludeAddButton.setEnabled(mUseExcludeSubTags.getSelection());
		mExcludeEditButton.setEnabled(mUseExcludeSubTags.getSelection() && mExcludeSubTagsTable.getSelectionCount()>0);
		mExcludeRemoveButton.setEnabled(mUseExcludeSubTags.getSelection() && mExcludeSubTagsTable.getSelectionCount()>0);
		mExcludeSubTagsTable.setEnabled(mUseExcludeSubTags.getSelection());
		
		mMXMLASTagsDeleteButton.setEnabled(mMXMLTagsContainingActionScriptTable.getSelectionCount()>0);
		mMXMLASTagsEditButton.setEnabled(mMXMLTagsContainingActionScriptTable.getSelectionCount()>0);
		
		mMaxLineLengthSpinner.setEnabled((mSortMode==MXMLPrettyPrinter.MXML_ATTR_ORDERING_USEDATA && mManualSortOrder.size()>0) || mWrapLineLengthButton.getSelection());
		mAttrsPerLineSpinner.setEnabled((mSortMode==MXMLPrettyPrinter.MXML_ATTR_ORDERING_USEDATA && mManualSortOrder.size()>0) || mWrapItemsPerLineButton.getSelection());
		
		mMXMLAttrsToKeepOnSameLineSpinner.setEnabled(mMXMLUseAttrsToKeepOnSameLineButton.getSelection());
		mMXMLObeyMaxLength.setEnabled(mMXMLUseAttrsToKeepOnSameLineButton.getSelection() && mWrapLineLengthButton.getSelection());
		
		mMXMLSpacesInsideBraces.setEnabled(mMXMLUseSpacesInsideBraces.getSelection());
		mMXMLFormatBindingExpressions.setEnabled(mMXMLUseSpacesInsideBraces.getSelection());
		
		mMXMLTabsBeforeCDATASpinner.setEnabled(!mMXMLKeepScriptCDATAOnSameLine.getSelection());
		
		mMXMLHangingIndentSize.setEnabled(!mMXMLSpecialWrapTags.getSelection());
		
		//advanced settings for spaces inside parens
		mASSpacesInsideParens.setEnabled(mASUseGlobalSpacesInsideParens.getSelection());
		mASAdvancedSpacesInsideParensInOtherPlaces.setEnabled(!mASUseGlobalSpacesInsideParens.getSelection());
		mASAdvancedSpacesInsideParensInParameterLists.setEnabled(!mASUseGlobalSpacesInsideParens.getSelection());
		mASAdvancedSpacesInsideParensInArgumentLists.setEnabled(!mASUseGlobalSpacesInsideParens.getSelection());
		mASAdvancedSpacesInsideObjectLiteralBraces.setEnabled(!mASUseGlobalSpacesInsideParens.getSelection());
		mASAdvancedSpacesInsideArrayRefBrackets.setEnabled(!mASUseGlobalSpacesInsideParens.getSelection());
		mASAdvancedSpacesInsideArrayDeclBrackets.setEnabled(!mASUseGlobalSpacesInsideParens.getSelection());
		
		mASSpacesAroundAssignmentInParameters.setEnabled(mASUseSpacesAroundAssignmentInParameters.getSelection());
		mASSpacesAroundAssignmentInMetatags.setEnabled(mASUseSpacesAroundAssignmentInMetatags.getSelection());
		
		mASBlankLinesToKeep.setEnabled(!mKeepBlankLines.getSelection());
		
		mASAdvancedSpacesAfterColonsInDeclarations.setEnabled(!mASUseGlobalSpacesAroundColons.getSelection());
		mASAdvancedSpacesBeforeColonsInDeclarations.setEnabled(!mASUseGlobalSpacesAroundColons.getSelection());
		mASAdvancedSpacesAfterColonsInFunctions.setEnabled(!mASUseGlobalSpacesAroundColons.getSelection());
		mASAdvancedSpacesBeforeColonsInFunctions.setEnabled(!mASUseGlobalSpacesAroundColons.getSelection());
		mASSpacesAroundColons.setEnabled(mASUseGlobalSpacesAroundColons.getSelection());
		
		mASAlignDeclEquals.setEnabled(!mASLeaveExtraWhitespaceAroundVarDecls.getSelection());
		mASAlignDeclEqualsConsecutive.setEnabled(mASAlignDeclEquals.getEnabled()&& mASAlignDeclEquals.getSelection());
		mASAlignDeclEqualsScope.setEnabled(mASAlignDeclEquals.getEnabled()&& mASAlignDeclEquals.getSelection());
		
		boolean advancedWrappingPossible=false;
		int methodWrap=((Integer)mASMethodDeclWrapCombo.getData(mASMethodDeclWrapCombo.getText())).intValue();
		int argWrap=((Integer)mASMethodCallWrapCombo.getData(mASMethodCallWrapCombo.getText())).intValue();
		int exprWrap=((Integer)mASExpressionWrapCombo.getData(mASExpressionWrapCombo.getText())).intValue();
		int arrayWrap=((Integer)mASArrayDeclWrapCombo.getData(mASArrayDeclWrapCombo.getText())).intValue();
		int xmlWrap=((Integer)mASXMLWrapCombo.getData(mASXMLWrapCombo.getText())).intValue();
		if (methodWrap==WrapOptions.WRAP_BY_COLUMN || methodWrap==WrapOptions.WRAP_BY_COLUMN_ONLY_ADD_CRS)
			advancedWrappingPossible=true;
		else if (argWrap==WrapOptions.WRAP_BY_COLUMN || argWrap==WrapOptions.WRAP_BY_COLUMN_ONLY_ADD_CRS)
			advancedWrappingPossible=true;
		else if (exprWrap==WrapOptions.WRAP_BY_COLUMN || exprWrap==WrapOptions.WRAP_BY_COLUMN_ONLY_ADD_CRS)
			advancedWrappingPossible=true;
		else if (arrayWrap==WrapOptions.WRAP_BY_COLUMN || arrayWrap==WrapOptions.WRAP_BY_COLUMN_ONLY_ADD_CRS)
			advancedWrappingPossible=true;
		else if (xmlWrap==WrapOptions.WRAP_BY_COLUMN || xmlWrap==WrapOptions.WRAP_BY_COLUMN_ONLY_ADD_CRS)
			advancedWrappingPossible=true;
		else if (mASWrapASDocComments.getSelection() || mASWrapMLComments.getSelection())
			advancedWrappingPossible=true;
		mASMaxLineLength.setEnabled(advancedWrappingPossible);
		mASUseAdvancedWrapping.setEnabled(advancedWrappingPossible);
		mASWrappingBreakOnPhrases.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled());
		mASWrappingEnforceMax.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled());
		
		if (((Integer)mASArrayDeclWrapCombo.getData(mASArrayDeclWrapCombo.getText())!=WrapOptions.WRAP_BY_COLUMN))
		{
			mASWrappingAllArrayItems.setSelection(false);
			mASWrappingAllObjectItems.setSelection(false);
		}	
		if (((Integer)mASMethodCallWrapCombo.getData(mASMethodCallWrapCombo.getText())!=WrapOptions.WRAP_BY_COLUMN))
		{
			mASWrappingAllArgs.setSelection(false);
		}	
		if (((Integer)mASMethodDeclWrapCombo.getData(mASMethodDeclWrapCombo.getText())!=WrapOptions.WRAP_BY_COLUMN))
		{
			mASWrappingAllParms.setSelection(false);
		}
		
		mSingleLineMetaTagsTable.setEnabled(!mASNewlineBeforeBindableProperty.getSelection());
		mSingleLineMetaTagAddButton.setEnabled(!mASNewlineBeforeBindableProperty.getSelection());
		mSingleLineMetaTagRemoveButton.setEnabled(!mASNewlineBeforeBindableProperty.getSelection());
		
		mASWrappingAllArgs.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && ((Integer)mASMethodCallWrapCombo.getData(mASMethodCallWrapCombo.getText())==WrapOptions.WRAP_BY_COLUMN));
		mASWrappingAllParms.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && ((Integer)mASMethodDeclWrapCombo.getData(mASMethodDeclWrapCombo.getText())==WrapOptions.WRAP_BY_COLUMN));
		mASWrappingFirstArg.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && mASWrappingAllArgs.getSelection());
		mASWrappingFirstParm.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && mASWrappingAllParms.getSelection());
		mASWrappingAllArrayItems.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && ((Integer)mASArrayDeclWrapCombo.getData(mASArrayDeclWrapCombo.getText())==WrapOptions.WRAP_BY_COLUMN));
		mASWrappingAllObjectItems.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && ((Integer)mASArrayDeclWrapCombo.getData(mASArrayDeclWrapCombo.getText())==WrapOptions.WRAP_BY_COLUMN));
		mASWrappingFirstArrayItem.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && mASWrappingAllArrayItems.getSelection());
		mASWrappingFirstObjectItem.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && mASWrappingAllObjectItems.getSelection());
		mASWrappingAlignArrayItems.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && mASWrappingAllArrayItems.getSelection());
		mASWrappingAlignObjectItems.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled() && mASWrappingAllObjectItems.getSelection());
		mASWrappingItemsTable.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled());
		mASWrappingPostMaxLeniency.setEnabled(mASUseAdvancedWrapping.getSelection() && mASUseAdvancedWrapping.getEnabled());

		//comment settings
		mASAlignCommentsColumn.setEnabled(mASAlignCommentsAtColumn.getSelection());
		boolean relativeIndent=mASKeepRelativeIndentOfMultilineComments.getSelection();
		mASWrapMLComments.setEnabled(!relativeIndent);
		mASWrapASDocComments.setEnabled(!relativeIndent);
		mASWrapDocCommentsHangingTabs.setEnabled(mASWrapASDocComments.getSelection() && !relativeIndent);
		mASWrapMLCommentsAsteriskMode.setEnabled(mASWrapMLComments.getSelection() && !relativeIndent);
		mASWrapMLCommentsReflow.setEnabled(mASWrapMLComments.getSelection() && !relativeIndent);
		mASWrapDocCommentsReflow.setEnabled(mASWrapASDocComments.getSelection() && !relativeIndent);
		mASWrapMLCommentsKeepBlankLines.setEnabled(mASWrapMLComments.getSelection() && mASWrapMLCommentsReflow.getSelection() && !relativeIndent);
		mASWrapDocCommentsKeepBlankLines.setEnabled(mASWrapASDocComments.getSelection() && mASWrapDocCommentsReflow.getSelection() && !relativeIndent);
		mASWrapMLCommentsSeparateHeader.setEnabled(mASWrapMLComments.getSelection() && !relativeIndent);
		
		////////////////////Brace style settings/////////////////////////
		mASBraceStyle.setEnabled(mASUseBraceStyle.getSelection());
		mASCRBeforeCatch.setEnabled(!mASUseBraceStyle.getSelection());
		mASCRBeforeElse.setEnabled(!mASUseBraceStyle.getSelection());
		mASCRBeforeWhile.setEnabled(!mASUseBraceStyle.getSelection());
		mASNoCRBeforeBreak.setEnabled(!mASUseBraceStyle.getSelection());
		mASNoCRBeforeContinue.setEnabled(!mASUseBraceStyle.getSelection());
		mASNoCRBeforeReturn.setEnabled(!mASUseBraceStyle.getSelection());
		mASNoCRBeforeThrow.setEnabled(!mASUseBraceStyle.getSelection());
		mASNoCRBeforeExpression.setEnabled(!mASUseBraceStyle.getSelection());
		mOpenBraceOnNewLine.setEnabled(!mASUseBraceStyle.getSelection() && mASUseGlobalOpenBraceOnNewLine.getSelection());
		mKeepElseIfOnSameLine.setEnabled(!mASUseBraceStyle.getSelection());
		
		mASCRBeforeCatchInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASCRBeforeElseInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASCRBeforeWhileInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mOpenBraceOnNewLineInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mKeepElseIfOnSameLineInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASNoCRBeforeBreakInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASNoCRBeforeContinueInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASNoCRBeforeReturnInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASNoCRBeforeThrowInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		mASNoCRBeforeExpressionInheritedVal.setVisible(mASUseBraceStyle.getSelection());
		
		mKeepElseIfOnSameLineInheritedVal.setText("on"); //on with either setting
		int style=((Integer)mASBraceStyle.getData(mASBraceStyle.getItem(mASBraceStyle.getSelectionIndex()))).intValue();
		boolean onSameLine=(style==ASPrettyPrinter.BraceStyle_Sun);
		mOpenBraceOnNewLineInheritedVal.setText(onSameLine ? "off" : "on" );
		mASCRBeforeCatchInheritedVal.setText(onSameLine ? "off" : "on" );
		mASCRBeforeElseInheritedVal.setText(onSameLine ? "off" : "on" );
		mASCRBeforeWhileInheritedVal.setText("off"); //off with either setting
		mASNoCRBeforeBreakInheritedVal.setText("off"); //off with either setting
		mASNoCRBeforeContinueInheritedVal.setText("off"); //off with either setting
		mASNoCRBeforeReturnInheritedVal.setText("off"); //off with either setting
		mASNoCRBeforeThrowInheritedVal.setText("off"); //off with either setting
		mASNoCRBeforeExpressionInheritedVal.setText("off"); //off with either setting
		
		mASUseGlobalOpenBraceOnNewLine.setEnabled(!mASUseBraceStyle.getSelection());
		
		for (int i = 0; i < mASAdvancedOpenBraceButtons.size(); i++) {
			Button button = mASAdvancedOpenBraceButtons.get(i);
			Text text=mASAdvancedOpenBraceInheritedValues.get(i);
			text.setVisible(mASUseGlobalOpenBraceOnNewLine.getSelection() || mASUseBraceStyle.getSelection());
			if (mASUseBraceStyle.getSelection())
			{
				text.setText(!onSameLine ? "on" : "off");
			}
			else if (mASUseGlobalOpenBraceOnNewLine.getSelection())
			{
				text.setText(mOpenBraceOnNewLine.getSelection() ? "on" : "off");
			}
				
			button.setEnabled(!mASUseGlobalOpenBraceOnNewLine.getSelection() && !mASUseBraceStyle.getSelection());
		}
		////////////////////Brace style settings/////////////////////////
		
		
		////////////////////////////////////////////////////////
		////mxml rearrange settings
		mMXMLOrderTable.setEnabled(mUseMXMLTagOrdering.getSelection());
		int selIndex=mMXMLOrderTable.getSelectionIndex();
		mMXMLOrderUpButton.setEnabled(selIndex>0 && mUseMXMLTagOrdering.getSelection());
		mMXMLOrderDownButton.setEnabled(selIndex>=0 && selIndex+1<mMXMLOrderTable.getItemCount() && mUseMXMLTagOrdering.getSelection());
		mMXMLOrderDeleteButton.setEnabled(mMXMLOrderTable.getSelectionCount()==1 && (mMXMLOrderTable.getSelection()[0].getData() instanceof ImportHolder) && !((ImportHolder)mMXMLOrderTable.getSelection()[0].getData()).toString().equals(PreferenceConstants.MXMLUnmatchedTagsConstant) && mUseMXMLTagOrdering.getSelection());
		mMXMLOrderAddButton.setEnabled(mUseMXMLTagOrdering.getSelection());
		mMXMLRestoreDefaultsButton.setEnabled(mUseMXMLTagOrdering.getSelection());
		//////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////////
		//as rearrange settings
		selIndex=mModifierTable.getSelectionIndex();
		mMoveUp.setEnabled(selIndex>0);
		mMoveDown.setEnabled(selIndex>=0 && selIndex+1<mModifierTable.getItemCount());
		mModifierTable.setEnabled(mUseModifierOrder.getSelection());
		mTypeCombo.setEnabled(!mUseGlobalOrderButton.getSelection());
		
		boolean elementOrderEnabled=mUseElementOrder.getSelection();
		updateTableEnable(elementOrderEnabled && mUseElementOrder.getSelection(), mElementOrderTable, mElementUpButton, mElementDownButton);
		updateTableEnable(elementOrderEnabled && mFunctionUseSortOrder.getSelection(), mFunctionOrderTable, mFunctionUpButton, mFunctionDownButton);
		updateTableEnable(elementOrderEnabled && mStaticFunctionUseSortOrder.getSelection(), mStaticFunctionOrderTable, mStaticFunctionUpButton, mStaticFunctionDownButton);
		updateTableEnable(elementOrderEnabled && mPropertyUseSortOrder.getSelection(), mPropertyOrderTable, mPropertyUpButton, mPropertyDownButton);
		updateTableEnable(elementOrderEnabled && mStaticPropertyUseSortOrder.getSelection(), mStaticPropertyOrderTable, mStaticPropertyUpButton, mStaticPropertyDownButton);
		mElementNewButton.setEnabled(mUseElementOrder.getSelection());
		mElementEditButton.setEnabled(mUseElementOrder.getSelection() && mElementOrderTable.getSelectionCount()==1 && (mElementOrderTable.getSelection()[0].getData() instanceof MemberSelectionSpec));
		mElementSectionEditButton.setEnabled(mUseElementOrder.getSelection() && mElementOrderTable.getSelectionCount()==1 && mUseSectionComments.getSelection());
		mElementSectionSpanEditButton.setEnabled(mUseElementOrder.getSelection() && mElementOrderTable.getSelectionCount()==1 && mUseSectionComments.getSelection());
		mElementDeleteButton.setEnabled(mUseElementOrder.getSelection() && mElementOrderTable.getSelectionCount()==1 && (mElementOrderTable.getSelection()[0].getData() instanceof MemberSelectionSpec));
		mElementBlankLinesCombo.setEnabled(mUseElementOrder.getSelection() && mElementOrderTable.getSelectionCount()==1);
		
		List<MemberSelectionSpec> preFilterItems=getPreselectPriorityItems();
		MemberSelectionSpec member=null;
		TableItem[] selItems=mElementOrderTable.getSelection();
		if (selItems.length==1 && selItems[0].getData() instanceof MemberSelectionSpec)
		{
			MemberSelectionSpec spec=(MemberSelectionSpec)selItems[0].getData();
			if (spec.getPreselectPriority()>0)
				member=spec;
		}
		mElementPriorityDownButton.setEnabled(member!=null && member.getPreselectPriority()<preFilterItems.size());
		mElementPriorityUpButton.setEnabled(member!=null && member.getPreselectPriority()>1);
		
		boolean enabled=mImportEnableOrdering.getSelection();
		updateTableEnable(enabled, mImportTable, mImportUpButton, mImportDownButton);
		selIndex=mImportTable.getSelectionIndex();
		mImportNewButton.setEnabled(enabled);
		mImportNewSeparatorButton.setEnabled(enabled);
		mImportEditButton.setEnabled(enabled && selIndex>=0);
		mImportDeleteButton.setEnabled(enabled && selIndex>=0);

		
		enabled=elementOrderEnabled && mMetatagUseSortOrder.getSelection();
		selIndex=mMetatagOrderTable.getSelectionIndex();
		updateTableEnable(enabled, mMetatagOrderTable, mMetatagUpButton, mMetatagDownButton);
		mMetatagNewButton.setEnabled(enabled);
		mMetatagDeleteButton.setEnabled(enabled && selIndex>=0);
		mMetatagEditButton.setEnabled(enabled && selIndex>=0);
//		mMetatagSectionEditButton.setEnabled(enabled && selIndex>=0);
		
		mFunctionSort.setEnabled(elementOrderEnabled);
		mFunctionUseSortOrder.setEnabled(elementOrderEnabled);
		mFunctionSectionEditButton.setEnabled(elementOrderEnabled && mFunctionOrderTable.getSelectionCount()>0);
		mIncludeSort.setEnabled(elementOrderEnabled);
		mMetatagSort.setEnabled(elementOrderEnabled);
		mMetatagUseSortOrder.setEnabled(elementOrderEnabled);
		mNamespaceSort.setEnabled(elementOrderEnabled);
		mPropertySort.setEnabled(elementOrderEnabled);
		mPropertyUseSortOrder.setEnabled(elementOrderEnabled);
		mPropertyGrabGettersButton.setEnabled(elementOrderEnabled);
		mPropertyAssociateGettersButton.setEnabled(elementOrderEnabled && /*mPropertySort.getSelection() && */mPropertyGrabGettersButton.getSelection());
//		mPropertyGettersHeaderButton.setEnabled(elementOrderEnabled && mUseSectionComments.getSelection() && mPropertySort.getSelection() && mPropertyGrabGettersButton.getSelection() && mPropertyAssociateGettersButton.getSelection());
//		mPropertyAlwaysHeaderButton.setEnabled(elementOrderEnabled && mUseSectionComments.getSelection() && mPropertySort.getSelection() && mPropertyGrabGettersButton.getSelection() && mPropertyAssociateGettersButton.getSelection() && mPropertyGettersHeaderButton.getSelection());
		mPropertySectionEditButton.setEnabled(elementOrderEnabled && mPropertyOrderTable.getSelectionCount()>0);
		mStaticFunctionSort.setEnabled(elementOrderEnabled);
		mStaticFunctionUseSortOrder.setEnabled(elementOrderEnabled);
		mStaticFunctionSectionEditButton.setEnabled(elementOrderEnabled && mStaticFunctionOrderTable.getSelectionCount()>0);
		mStaticPropertySort.setEnabled(elementOrderEnabled);
		mStaticPropertyUseSortOrder.setEnabled(elementOrderEnabled);
		mStaticPropertyGrabGettersButton.setEnabled(elementOrderEnabled);
		mStaticPropertyAssociateGettersButton.setEnabled(elementOrderEnabled && /*mStaticPropertySort.getSelection() &&*/ mStaticPropertyGrabGettersButton.getSelection());
//		mStaticPropertyGettersHeaderButton.setEnabled(elementOrderEnabled && mUseSectionComments.getSelection() && mStaticPropertySort.getSelection() && mStaticPropertyGrabGettersButton.getSelection() && mStaticPropertyAssociateGettersButton.getSelection());
//		mStaticPropertyAlwaysHeaderButton.setEnabled(elementOrderEnabled && mUseSectionComments.getSelection() && mStaticPropertySort.getSelection() && mStaticPropertyGrabGettersButton.getSelection() && mStaticPropertyAssociateGettersButton.getSelection() && mStaticPropertyGettersHeaderButton.getSelection());
		mStaticPropertySectionEditButton.setEnabled(elementOrderEnabled && mStaticPropertyOrderTable.getSelectionCount()>0);
		///////////////////////////////////////////////////////////////////////
		
		/////////////section header settings
		mUseSectionComments.setEnabled(mUseElementOrder.getSelection());
		mRemoveExistingSectionComments.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mUseSectionCommentsInMXML.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMajorSectionSize.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMajorSectionStyle.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMajorSectionWidth.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mSampleMajorSectionHeader.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMajorSectionFillChar.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMajorSectionPreLines.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMinorSectionSize.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMinorSectionStyle.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMinorSectionWidth.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mSampleMinorSectionHeader.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMinorSectionFillChar.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		mMinorSectionPreLines.setEnabled(mUseElementOrder.getSelection() && mUseSectionComments.getSelection());
		/////////////////////////////////////
		
		///////////////////////////////////////////
		//copyright header settings
		mRemoveExistingCopyright.setEnabled(mUseCopyrightHeader.getSelection());
		mCopyrightSectionSize.setEnabled(mUseCopyrightHeader.getSelection());
		mCopyrightSectionStyle.setEnabled(mUseCopyrightHeader.getSelection());
		mCopyrightSectionWidth.setEnabled(mUseCopyrightHeader.getSelection());
		mCopyrightText.setEnabled(mUseCopyrightHeader.getSelection());
		mCopyrightSectionFillChar.setEnabled(mUseCopyrightHeader.getSelection());
		mCopyrightSectionPostLines.setEnabled(mUseCopyrightHeader.getSelection());
		///////////////////////////////////////////
	}

	private void updateTableEnable(boolean enabled, Table table, Button upButton, Button downButton)
	{
		table.setEnabled(enabled);
		int selIndex=table.getSelectionIndex();
		downButton.setEnabled(enabled && selIndex>=0 && selIndex+1<table.getItemCount());
		upButton.setEnabled(enabled && selIndex>0);
	}

	static interface IAdvancedSettingsLayout
	{
		public void addAdvancedItems(Composite parent);
		public void addGlobalItem(ExpandableComposite ec);
	}
	
	//This is a hacked-together class to supply coloring for the Actionscript sample
	private static class ASLineStyleListener implements LineStyleListener
	{
		private List<List<StyleRange>> mRanges;
		private StyledText mWidget;
		private Color textColor=Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		private Color bgColor=Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		private Color keywordColor=Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		private Color commentColor=Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
		private Color stringColor=Display.getDefault().getSystemColor(SWT.COLOR_RED);
		public ASLineStyleListener(StyledText widget)
		{
			mRanges=null;
			mWidget=widget;
		}
		
		public void reset()
		{
			mRanges=null;
		}
		
		public void lineGetStyle(LineStyleEvent event) {
			if (mRanges==null)
			{
				calculateRanges();
			}
			int line=mWidget.getLineAtOffset(event.lineOffset);
			if (line<mRanges.size())
				event.styles=mRanges.get(line).toArray(new StyleRange[]{});
			else
				event.styles=new StyleRange[]{new StyleRange(event.lineOffset, event.lineText.length(), textColor, bgColor)};
			System.out.println();
		}
		
		private void calculateRanges()
		{
			//simple lexer to find strings, comments, reserved words
			mRanges=new ArrayList<List<StyleRange>>();
			Set<String> reservedWords=new HashSet<String>();
			reservedWords.add("package");
			reservedWords.add("public");
			reservedWords.add("private");
			reservedWords.add("protected");
			reservedWords.add("var");
			reservedWords.add("const");
			reservedWords.add("import");
			reservedWords.add("Bindable");
			reservedWords.add("for");
			reservedWords.add("if");
			reservedWords.add("else");
			reservedWords.add("while");
			reservedWords.add("do");
			reservedWords.add("switch");
			reservedWords.add("function");
			reservedWords.add("try");
			reservedWords.add("catch");
			reservedWords.add("finally");
			reservedWords.add("true");
			reservedWords.add("false");
			reservedWords.add("Embed");
			reservedWords.add("Transient");
			reservedWords.add("ArrayElementType");
			reservedWords.add("extends");
			reservedWords.add("Effect");
			reservedWords.add("Event");
			reservedWords.add("DefaultProperty");
			reservedWords.add("Deprecated");
			reservedWords.add("implements");
			reservedWords.add("namespace");
			reservedWords.add("new");
			reservedWords.add("each");
			reservedWords.add("get");
			reservedWords.add("set");
			reservedWords.add("include");
			reservedWords.add("dynamic");
			reservedWords.add("final");
			reservedWords.add("override");
			reservedWords.add("static");
			reservedWords.add("as");
			reservedWords.add("break");
			reservedWords.add("case");
			reservedWords.add("class");
			reservedWords.add("continue");
			reservedWords.add("default");
			reservedWords.add("delete");
			reservedWords.add("in");
			reservedWords.add("instanceof");
			reservedWords.add("interface");
			reservedWords.add("internal");
			reservedWords.add("is");
			reservedWords.add("native");
			reservedWords.add("null");
			reservedWords.add("return");
			reservedWords.add("super");
			reservedWords.add("this");
			reservedWords.add("throw");
			reservedWords.add("to");
			reservedWords.add("typeof");
			reservedWords.add("use");
			reservedWords.add("void");
			reservedWords.add("with");
			
			String text=mWidget.getText();
			int lastRangeStart=0;
			List<StyleRange> lineRanges=new ArrayList<StyleRange>();
			for (int i=0;i<text.length();)
			{
				//multi-line comment is the only thing that crosses lines
				if (i+1<text.length() && text.substring(i, i+2).equals(ASRearranger.SlashStar))
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					for (;i<text.length();i++)
					{
						if (i+2<text.length() && text.substring(i, i+2).equals(ASRearranger.StarSlash))
						{
							i+=2;
							break;
						}
						if (text.charAt(i)=='\n')
						{
							StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, commentColor, bgColor);
							lineRanges.add(newRange);
							lastRangeStart=i+1;
							mRanges.add(lineRanges);
							lineRanges=new ArrayList<StyleRange>();
						}
					}
					
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, commentColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (i+1<text.length() && text.substring(i, i+2).equals("//"))
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					for (;i<text.length();i++)
					{
						if (text.charAt(i)=='\n')
							break;
					}
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, commentColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i+1;
					i++;
					mRanges.add(lineRanges);
					lineRanges=new ArrayList<StyleRange>();
				}
				else if (text.charAt(i)=='"')
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					for (i++;i<text.length();i++)
					{
						if (text.charAt(i)=='"')
						{
							i++;
							break;
						}
					}
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, stringColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (text.charAt(i)=='\'')
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					for (i++;i<text.length();i++)
					{
						if (text.charAt(i)=='\'')
						{
							i++;
							break;
						}
					}
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, stringColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (text.charAt(i)=='\n')
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i+1;
					i++;
					mRanges.add(lineRanges);
					lineRanges=new ArrayList<StyleRange>();
				}
				else
				{
					if (Character.isJavaIdentifierStart(text.charAt(i)))
					{
						int originalI=i;
						for (;i<text.length();i++)
						{
							if (!Character.isJavaIdentifierStart(text.charAt(i)))
							{
								if (reservedWords.contains(text.substring(originalI, i)))
								{
									if (originalI>lastRangeStart)
									{
										StyleRange newRange=new StyleRange(lastRangeStart, originalI-lastRangeStart, textColor, bgColor);
										lineRanges.add(newRange);
										lastRangeStart=originalI;
									}
									
									StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, keywordColor, bgColor);
									lineRanges.add(newRange);
									lastRangeStart=i;
								}
								break;
							}
						}
					}
					else
					{
						i++;
					}
				}
			}
		}

		private void captureTextRange(String text, int lastRangeStart, List<StyleRange> lineRanges, int i)
		{
			if (i>lastRangeStart)
			{
				StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, textColor, bgColor);
				lineRanges.add(newRange);
			}
		}
	}
	
	//This is a hacked-together class to supply coloring for the MXML sample (doesn't color actionscript code)
	private static class MXMLLineStyleListener implements LineStyleListener
	{
		private List<List<StyleRange>> mRanges;
		private StyledText mWidget;
		private Color textColor=Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		private Color bgColor=Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
//		private Color attrColor=Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		private Color tagColor=Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		private Color commentColor=Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
		private Color stringColor=Display.getDefault().getSystemColor(SWT.COLOR_RED);
		public MXMLLineStyleListener(StyledText widget)
		{
			mRanges=null;
			mWidget=widget;
		}
		
		public void reset()
		{
			mRanges=null;
		}
		
		public void lineGetStyle(LineStyleEvent event) {
			if (mRanges==null)
			{
				calculateRanges();
			}
			int line=mWidget.getLineAtOffset(event.lineOffset);
			if (line<mRanges.size())
				event.styles=mRanges.get(line).toArray(new StyleRange[]{});
			else
				event.styles=new StyleRange[]{new StyleRange(event.lineOffset, event.lineText.length(), textColor, bgColor)};
			System.out.println();
		}
		
		private void calculateRanges()
		{
			//simple lexer to find strings, comments, reserved words
			mRanges=new ArrayList<List<StyleRange>>();
			String text=mWidget.getText();
			int lastRangeStart=0;
			List<StyleRange> lineRanges=new ArrayList<StyleRange>();
			boolean insideTag=false;
			boolean seenTagName=false;
			for (int i=0;i<text.length();)
			{
				//multi-line comment is the only thing that crosses lines
				if (i+3<text.length() && text.substring(i, i+3).equals("<!--"))
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					for (;i<text.length();i++)
					{
						if (text.substring(i, i+3).equals("-->"))
						{
							i+=3;
							break;
						}
						if (text.charAt(i)=='\n')
						{
							StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, commentColor, bgColor);
							lineRanges.add(newRange);
							lastRangeStart=i+1;
							i++;
							mRanges.add(lineRanges);
							lineRanges=new ArrayList<StyleRange>();
						}
					}
					
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, commentColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (text.charAt(i)=='<')
				{
					insideTag=true;
					seenTagName=false;
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					i++;
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, tagColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (text.charAt(i)=='>')
				{
					insideTag=false;
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					i++;
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, tagColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (insideTag && Character.isJavaIdentifierPart(text.charAt(i)))
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					
					if (!seenTagName)
					{
						for (;i<text.length();i++)
						{
							char c=text.charAt(i);
							if (!Character.isJavaIdentifierPart(c) && c!=':')
							{
								StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, tagColor, bgColor);
								lineRanges.add(newRange);
								lastRangeStart=i;
								break;
							}
						}
						seenTagName=true;
					}
					else
					{
						i++;
					}
//					else
//					{
//						for (;i<text.length();i++)
//						{
//							char c=text.charAt(i);
//							if (!Character.isJavaIdentifierPart(c) && c!=':')
//							{
//								StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, tagColor, bgColor, SWT.BOLD);
//								lineRanges.add(newRange);
//								lastRangeStart=i;
//								break;
//							}
//						}
//					}
					
					
				}
				else if (insideTag && text.charAt(i)=='"')
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i;
					for (i++;i<text.length();i++)
					{
						if (text.charAt(i)=='"')
						{
							i++;
							break;
						}
					}
					StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, stringColor, bgColor);
					lineRanges.add(newRange);
					lastRangeStart=i;
				}
				else if (text.charAt(i)=='\n')
				{
					captureTextRange(text, lastRangeStart, lineRanges, i);
					lastRangeStart=i+1;
					i++;
					mRanges.add(lineRanges);
					lineRanges=new ArrayList<StyleRange>();
				}
				else
				{
					i++;
				}
			}
		}

		private void captureTextRange(String text, int lastRangeStart, List<StyleRange> lineRanges, int i)
		{
			if (i>lastRangeStart)
			{
				StyleRange newRange=new StyleRange(lastRangeStart, i-lastRangeStart, textColor, bgColor);
				lineRanges.add(newRange);
			}
		}
	}

	private static class DataDialog extends Dialog
	{
		String mData;
		Text mText;
		String mTitle;
		protected DataDialog(Shell parentShell, String title, String initialData)
		{
			super(parentShell);
			mData=initialData;
			mTitle=title;
		}
		
		@Override
		protected Control createDialogArea(Composite parent)
		{
			getShell().setText(mTitle);
			Composite comp=new Composite(parent, SWT.None);
			comp.setLayout(new GridLayout());
			comp.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			mText=new Text(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			GridData gd=new GridData(GridData.FILL_BOTH);
			gd.widthHint=mText.getLineHeight()*40;
			gd.heightHint=mText.getLineHeight()*20;
			mText.setLayoutData(gd);
			if (mData!=null)
				mText.setText(mData);
			mData=null;
			return comp;
		}
		
		public String getData()
		{
			return mData;
		}

		@Override
		protected void okPressed() {
			mData=mText.getText();
			super.okPressed();
		}
	}
	
	public static class ElementHolder implements ISectionItem
	{
		private String mPrintString;
		public ElementHolder(String printString)
		{
			mPrintString=printString;
		}
		
		public String toString()
		{
			return mPrintString;
		}

		public String getPrintString() {
			return toString();
		}

		public String getReferenceID() {
			return toString();
		}
	}
	
	private static class VisibilityHolder extends ElementHolder
	{
		public VisibilityHolder(String printString)
		{
			super(printString);
		}
	}
	
//	private static class MetatagHolder extends ElementHolder
//	{
//		public MetatagHolder(String printString)
//		{
//			super(printString);
//		}
//	}
	
	private static class ImportHolder extends ElementHolder
	{
		public ImportHolder(String printString)
		{
			super(printString);
		}
	}
	
	private void moveUpHelper(Table table)
	{
		int selIndex=table.getSelectionIndex();
		if (selIndex>0)
		{
			Object saveObject=table.getItem(selIndex-1).getData();
			table.getItem(selIndex-1).setData(table.getItem(selIndex).getData());
			table.getItem(selIndex).setData(saveObject);
			table.setSelection(selIndex-1);
			updateItemText(table.getItem(selIndex-1));
			updateItemText(table.getItem(selIndex));
			enableWidgets();
			updateRearrangeText();
		}
	}
	
	private void moveDownHelper(Table table)
	{
		int selIndex=table.getSelectionIndex();
		if (selIndex+1<table.getItemCount())
		{
			Object saveObject=table.getItem(selIndex+1).getData();
			table.getItem(selIndex+1).setData(table.getItem(selIndex).getData());
			table.getItem(selIndex).setData(saveObject);
			table.setSelection(selIndex+1);
			updateItemText(table.getItem(selIndex+1));
			updateItemText(table.getItem(selIndex));
			enableWidgets();
			updateRearrangeText();
		}
	}

	private void updateItemText(TableItem item)
	{
		if (item.getData()!=null)
		{
			if (item.getParent()==mElementOrderTable)
			{
				//special handling for header sections
				List<ISectionItem> allSections=getSections();
				String sectionID=((ISectionItem)item.getData()).getReferenceID();
				SectionSpec spanSpec=ASRearranger.getSectionSpec(sectionID+ASRearranger.SpanningSuffix, allSections, mHeaderSpecs);
				SectionSpec spec=ASRearranger.getSectionSpec(sectionID, allSections, mHeaderSpecs);
				String printString=item.getData().toString();

				//TODO: special handling for member selectors with pre-filtering turned on
				if (item.getData() instanceof MemberSelectionSpec)
				{
					MemberSelectionSpec filter=(MemberSelectionSpec)item.getData();
					if (filter.getPreselectPriority()>0)
					{
						printString="("+Integer.toString(filter.getPreselectPriority())+") "+printString;
					}
				}
				
				if (spec!=null && spec.isUseHeader())
				{
					printString="*"+printString+"* "+"("+spec.getContentPrintString()+")";
				}
				
				if (spanSpec!=null && spanSpec.isUseHeader())
				{
					if (spanSpec.getID().startsWith(sectionID))
					{
						printString=printString+" Span="+"("+spanSpec.getContentPrintString()+")";
					}
					else
					{
						printString="    "+printString;
					}
				}
				
				item.setText(printString);
				return;
			}
			else if (item.getData() instanceof VisibilityHolder)
			{
				String elementConstant=null;
				if (item.getParent()==mStaticFunctionOrderTable)
					elementConstant=PreferenceConstants.ASRearr_Element_StaticFunction;
				else if (item.getParent()==mFunctionOrderTable)
					elementConstant=PreferenceConstants.ASRearr_Element_Function;
				else if (item.getParent()==mStaticPropertyOrderTable)
					elementConstant=PreferenceConstants.ASRearr_Element_StaticProperty;
				else if (item.getParent()==mPropertyOrderTable)
					elementConstant=PreferenceConstants.ASRearr_Element_Property;
				if (elementConstant!=null)
				{
					String sectionID=elementConstant;
					sectionID=sectionID+"#"+((ElementHolder)item.getData()).toString();
					SectionSpec spec=mHeaderSpecs.get(sectionID);
					if (spec!=null && spec.isUseHeader())
					{
						String printString=item.getData().toString();
						printString="*"+printString+"* "+"("+spec.getContentPrintString()+")";
						item.setText(printString);
						return;
					}
				}
			}
			item.setText(item.getData().toString());
		}
	}
	
	private static class ImportValidator implements IInputValidator
	{
		public String isValid(String newText)
		{
			if (newText.length()==0)
				return "Import is empty";
			for (int i=0;i<newText.length();i++)
			{
				char c=newText.charAt(i);
				if (Character.isJavaIdentifierStart(c) || (i>0 && c=='.'))
				{
					//char okay
				}
				else
				{
					return "Invalid character: "+c;
				}
			}
			return null;
		}
	}
	
	private static class MetatagValidator implements IInputValidator
	{
		public String isValid(String newText)
		{
			if (newText.length()==0)
				return "Metatag is empty";
			for (int i=0;i<newText.length();i++)
			{
				char c=newText.charAt(i);
				if (Character.isJavaIdentifierPart(c))
				{
					//char okay
				}
				else
				{
					return "Invalid character: "+c;
				}
			}
			return null;
		}
	}

	public static String validateRegex(String text)
	{
		try
		{
			Pattern.compile(text);
		}
		catch (Exception e)
		{
			return "Invalid regular expression: "+e.getMessage();
		}
		return null;
	}

	public void setDisableMode(boolean on)
	{
		mDisableMode=on;
	}
}
