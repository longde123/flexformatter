package flexprettyprint.preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import flexprettyprint.handlers.ASPrettyPrinter;
import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprint.handlers.WrapOptions;
import flexprettyprintcommand.Activator;

public class Initializer extends AbstractPreferenceInitializer {

	public static final String Attr_Group_Other="Special_Group--Other Attributes";
	
	public static final String Pref_Flex_CheckForUpdates = "Flex.checkForFormatterUpdates"; //$NON-NLS-1$
	public static final String Pref_Flex_LastCheckForUpdatesVersion = "Flex.lastVersionFoundCheckingForFormatterUpdates"; //$NON-NLS-1$
	public static final String Pref_Flex_UseAutoSyncFile = "Flex.useAutoSyncFromFile"; //$NON-NLS-1$
	public static final String Pref_Flex_WriteToAutoSyncFile="Flex.useAutoSyncToFile"; //$NON-NLS-1$
	public static final String Pref_Flex_AutoSyncFile= "Flex.autoSyncFilePath"; //$NON-NLS-1$
	public static final String Pref_Flex_AutoSyncLocalChangesDirty = "Flex.autoSyncLocalChangesFlag";
	public static final String Pref_Flex_ShowBatchResultsInDialog = "Flex.showBatchResultsInDialog";
	
	public static final String Pref_Flex_UseTabs = "Flex.useTabs"; //$NON-NLS-1$
//	public static final String Pref_Flex_IndentSize = "Flex.indentSize";
//	public static final String Pref_Flex_TabSize = "Flex.tabSize";
	
//	public static final String Pref_FlexCommand_UseMicroEdits= "FlexPrettyPrintCommand.useMicroEdits";
	
//	public static final String Pref_AS_BlockIndent="Actionscript.blockIndent";
	public static final String Pref_AS_SpacesBeforeComma="Actionscript.spacesBeforeComma"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesAfterComma="Actionscript.spacesAfterComma"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesAroundAssignment="Actionscript.spacesAroundAssignment"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesAroundColons="Actionscript.spacesAroundColons"; //$NON-NLS-1$
	public static final String Pref_AS_UseGlobalSpacesAroundColons="Actionscript.useGlobalSpacesAroundColons"; //$NON-NLS-1$
	
	
	/**
	 * @deprecated
 	 */
	public static final String Pref_AS_AdvancedSpacesBeforeColons="Actionscript.spacesBeforeColons"; //$NON-NLS-1$
	/**
	 * @deprecated
 	 */
	public static final String Pref_AS_AdvancedSpacesAfterColons="Actionscript.spacesAfterColons"; //$NON-NLS-1$
	public static final String	Pref_AS_AdvancedSpacesBeforeColonsInDeclarations		= "Actionscript.spacesBeforeColonsInDeclarations";					//$NON-NLS-1$
	public static final String	Pref_AS_AdvancedSpacesAfterColonsInDeclarations			= "Actionscript.spacesAfterColonsInDeclarations";					//$NON-NLS-1$
	public static final String	Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes			= "Actionscript.spacesBeforeColonsInFunctions";					//$NON-NLS-1$
	public static final String	Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes			= "Actionscript.spacesAfterColonsInFunctions";						//$NON-NLS-1$
	public static final String Pref_AS_SpacesAfterLabel="Actionscript.spacesAfterLabel"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesAroundSymbolicOperator="Actionscript.spacesAroundBinarySymbolicOperator"; //$NON-NLS-1$
	public static final String Pref_AS_NoNewCRsBeforeBreak="Actionscript.noNewCRsBeforeBreak"; //$NON-NLS-1$
	public static final String Pref_AS_NoNewCRsBeforeContinue="Actionscript.noNewCRsBeforeContinue"; //$NON-NLS-1$
	public static final String Pref_AS_NoNewCRsBeforeReturn="Actionscript.noNewCRsBeforeReturn"; //$NON-NLS-1$
	public static final String Pref_AS_NoNewCRsBeforeThrow="Actionscript.noNewCRsBeforeThrow"; //$NON-NLS-1$
	public static final String Pref_AS_NoNewCRsBeforeExpression="Actionscript.noNewCRsBeforeExpression"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesBeforeFunctions="Actionscript.blankLinesBeforeFunctions"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesBeforeProperties="Actionscript.blankLinesBeforeProperties"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesAtFunctionStart="Actionscript.blankLinesAtFunctionStart"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesAtFunctionEnd="Actionscript.blankLinesAtFunctionEnd"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesBeforeClasses="Actionscript.blankLinesBeforeClasses"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesBeforeControlStatements="Actionscript.blankLinesBeforeControlStatements"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesBeforeImportBlock="Actionscript.blankLinesBeforeImportBlock"; //$NON-NLS-1$
	public static final String Pref_AS_KeepBlankLines="Actionscript.keepBlankLines"; //$NON-NLS-1$
	public static final String Pref_AS_KeepSpacesBeforeLineComments="Actionscript.keepSpacesBeforeLineComments"; //$NON-NLS-1$
	public static final String Pref_AS_AlignLineCommentsAtColumn="Actionscript.alignLineCommentsAtColumn"; //$NON-NLS-1$
	public static final String Pref_AS_BlankLinesToKeep="Actionscript.blankLinesToKeep"; //if not keeping blank lines in general, still keep this many //$NON-NLS-1$
	public static final String Pref_AS_BreakLinesBeforeComma="Actionscript.breakLinesBeforeComma"; //$NON-NLS-1$
	public static final String Pref_AS_BreakLinesBeforeAssignment="Actionscript.breakLinesBeforeAssignment"; //$NON-NLS-1$
	public static final String Pref_AS_BreakLinesBeforeArithmetic="Actionscript.breakLinesBeforeArithmeticOperator"; //$NON-NLS-1$
	public static final String Pref_AS_BreakLinesBeforeLogical="Actionscript.breakLinesBeforeLogicalOperator"; //$NON-NLS-1$
	public static final String Pref_AS_OpenBraceOnNewLine="Actionscript.putOpenBraceOnNewLine"; //$NON-NLS-1$
	public static final String Pref_AS_ElseOnNewLine = "Actionscript.putElseOnNewLine"; //$NON-NLS-1$
	public static final String Pref_AS_WhileOnNewLine = "Actionscript.putWhileOnNewLine"; //$NON-NLS-1$
	public static final String Pref_AS_CatchOnNewLine = "Actionscript.putCatchOnNewLine"; //$NON-NLS-1$
	public static final String Pref_AS_ElseIfOnSameLine = "Actionscript.keepElseIfOnSameLine"; //$NON-NLS-1$
	public static final String Pref_AS_MaxLineLength = "Actionscript.maxLineLength"; //$NON-NLS-1$
	public static final String Pref_AS_KeepSLCommentsOnColumn1="Actionscript.keepSLCommentsOnColumn1"; //$NON-NLS-1$
	public static final String Pref_AS_WrapExpressionMode="Actionscript.wrapExpressionMode"; //$NON-NLS-1$
	public static final String Pref_AS_WrapMethodDeclMode="Actionscript.wrapMethodDeclMode"; //$NON-NLS-1$
	public static final String Pref_AS_WrapMethodCallMode="Actionscript.wrapMethodCallMode"; //$NON-NLS-1$
	public static final String Pref_AS_WrapArrayDeclMode="Actionscript.wrapArrayDeclMode"; //$NON-NLS-1$
	public static final String Pref_AS_WrapXMLMode="Actionscript.wrapEmbeddedXMLMode"; //$NON-NLS-1$
	public static final String Pref_AS_WrapIndentStyle="Actionscript.wrapIndentStyle"; //$NON-NLS-1$
	public static final String Pref_AS_CollapseSpacesForAdjacentParens ="Actionscript.collapseSpacesForAdjacentParens"; //$NON-NLS-1$
	public static final String Pref_AS_UnindentExpressionTerminators="Actionscript.unindentExpressionTerminators"; //if not keeping blank lines in general, still keep this many //$NON-NLS-1$
	public static final String Pref_AS_LeaveSingleLineFunctions="Actionscript.leaveSingleLineFunctions"; //if not keeping blank lines in general, still keep this many //$NON-NLS-1$
	
	/**
	 * @deprecated
	 */
	public static final String Pref_AS_EnsureLoopsHaveBraces="Actionscript.ensureLoopsHaveBraces"; //$NON-NLS-1$
	public static final String Pref_AS_AddBracesToLoops="Actionscript.addBracesToLoops"; //$NON-NLS-1$
	/**
	 * @deprecated
	 */
	public static final String Pref_AS_EnsureConditionalsHaveBraces ="Actionscript.ensureConditionalsHaveBraces"; //$NON-NLS-1$
	public static final String Pref_AS_AddBracesToConditionals="Actionscript.addBracesToConditionals"; //$NON-NLS-1$
	/**
	 * @deprecated
	 */
	public static final String Pref_AS_EnsureSwitchCasesHaveBraces ="Actionscript.ensureSwitchCasesHaveBraces"; //$NON-NLS-1$
	public static final String Pref_AS_AddBracesToCases="Actionscript.addBracesToCases"; //$NON-NLS-1$
	
	/**
	 * @deprecated
	 */
	public static final String Pref_AS_NewlineAfterBindable="Actionscript.newLineAfterBindable"; //$NON-NLS-1$
	public static final String Pref_AS_NewlineBeforeBindableFunction="Actionscript.newLineBeforeBindableFunction"; //$NON-NLS-1$
	public static final String Pref_AS_NewlineBeforeBindableProperty="Actionscript.newLineBeforeBindableProperty"; //$NON-NLS-1$
	public static final String Pref_AS_TrimTrailingWhitespace="Actionscript.trimTrailingWhitespace"; //$NON-NLS-1$
	public static final String Pref_AS_PutEmptyStatementsOnNewLine="Actionscript.putEmptyStatementsOnNewLine"; //$NON-NLS-1$
	public static final String Pref_AS_Tweak_UseSpacesAroundEqualsInOptionalParameters="Actionscript.advancedUseSpacesAroundEqualsInOptionalParameters"; //$NON-NLS-1$
	public static final String Pref_AS_Tweak_UseSpacesAroundEqualsInMetatags="Actionscript.advancedUseSpacesAroundEqualsInMetatags"; //$NON-NLS-1$
	public static final String Pref_AS_Tweak_SpacesAroundEqualsInOptionalParameters="Actionscript.advancedSpacesAroundEqualsInOptionalParameters"; //$NON-NLS-1$
	public static final String Pref_AS_Tweak_SpacesAroundEqualsInMetatags="Actionscript.advancedSpacesAroundEqualsInMetatags"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesBeforeOpenControlParen="Actionscript.spacesBeforeControlOpenParen"; //$NON-NLS-1$
	public static final String Pref_AS_AlwaysGenerateIndent="Actionscript.alwaysGenerateIndent"; //$NON-NLS-1$
	public static final String Pref_AS_DontIndentPackageItems="Actionscript.dontIndentPackageItems"; //$NON-NLS-1$
	public static final String Pref_AS_DontIndentSwitchCases="Actionscript.dontIndentSwitchCases"; //$NON-NLS-1$
	public static final String Pref_AS_LeaveExtraWhitespaceAroundVarDecls="Actionscript.leaveExtraWhitespaceAroundVarDecls"; //$NON-NLS-1$
	public static final String Pref_AS_AlignDeclEquals="Actionscript.alignDeclEquals"; //$NON-NLS-1$
	public static final String Pref_AS_AlignDeclMode="Actionscript.alignDeclEqualsMode"; //$NON-NLS-1$
	public static final String Pref_AS_TabsInHangingIndent="Actionscript.tabCountForHangingIndent"; //$NON-NLS-1$
	public static final String Pref_AS_RearrangeAsPartOfFormat="Actionscript.doRearrangeWhileFormatting"; //$NON-NLS-1$
	public static final String Pref_AS_UseGlobalCRBeforeBrace="Actionscript.useGlobalCRBeforeBrace"; //$NON-NLS-1$
	public static final String Pref_AS_AdvancedCRBeforeBraceSettings="Actionscript.advancedCRBeforeBraceSettings"; //$NON-NLS-1$
	public static final String Pref_AS_PerformRearrangeWhileFormatting= "ActionScript.rearrangeWhileFormatting"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesBeforeFormalParameters= "ActionScript.spacesBeforeFormalParameters"; //$NON-NLS-1$
	public static final String Pref_AS_SpacesBeforeArguments= "ActionScript.spacesBeforeArguments"; //$NON-NLS-1$
	public static final String Pref_AS_MetaTagsOnSameLineAsTargetFunction= "ActionScript.metatagsOnSameLineAsTargetFunction"; //$NON-NLS-1$
	public static final String Pref_AS_MetaTagsOnSameLineAsTargetProperty= "ActionScript.metatagsOnSameLineAsTargetProperty"; //$NON-NLS-1$
	public static final String Pref_AS_KeepRelativeIndentInDocComments= "ActionScript.keepRelativeIndentInDocComments"; //$NON-NLS-1$
	public static final String Pref_AS_RemoveTrailingWhitespace= "ActionScript.removeTrailingWhitespace"; //$NON-NLS-1$
	
	public static final String Pref_AS_BraceStyle="Actionscript.braceStyle"; //$NON-NLS-1$
	public static final String Pref_AS_UseBraceStyle="Actionscript.useBraceStyle"; //$NON-NLS-1$
	public static final String Pref_AS_UseGnuBraceIndent="Actionscript.useGnuBraceIndent"; //$NON-NLS-1$

	public static final String Pref_AS_UseLineCommentWrapping="Actionscript.useLineCommentWrapping"; //$NON-NLS-1$
	public static final String Pref_AS_UseMLCommentWrapping="Actionscript.useMLCommentWrapping"; //$NON-NLS-1$
	public static final String Pref_AS_MLCommentReflow="Actionscript.MLCommentReflowLines"; //$NON-NLS-1$
	public static final String Pref_AS_MLCommentAsteriskMode="Actionscript.MLCommentAsteriskMode"; //$NON-NLS-1$
	public static final String Pref_AS_MLCommentKeepBlankLines="Actionscript.MLCommentKeepBlankLines"; //$NON-NLS-1$
	public static final String Pref_AS_MLCommentHeaderOnSeparateLine="Actionscript.MLCommentHeaderOnSeparateLine"; //$NON-NLS-1$
	public static final String Pref_AS_UseDocCommentWrapping="Actionscript.useDocCommentWrapping"; //$NON-NLS-1$
	public static final String Pref_AS_DocCommentHangingIndentTabs="Actionscript.docCommentHangingIndentTabs"; //$NON-NLS-1$
	public static final String Pref_AS_DocCommentReflow="Actionscript.docCommentReflow"; //$NON-NLS-1$
	public static final String Pref_AS_DocCommentKeepBlankLines="Actionscript.DocCommentKeepBlankLines"; //$NON-NLS-1$
	
	
	//advanced wrapping settings
	public static final String Pref_AS_UseAdvancedWrapping="Actionscript.useAdvancedWrapping";
	public static final String Pref_AS_AdvancedWrappingElements="Actionscript.advancedWrappingElements";
	public static final String Pref_AS_AdvancedWrappingPreservePhrases="Actionscript.advancedWrappingPreservePhrases";
	public static final String Pref_AS_AdvancedWrappingGraceColumns="Actionscript.advancedWrappingGraceColumns";
	public static final String Pref_AS_AdvancedWrappingEnforceMax="Actionscript.advancedWrappingEnforceMax";
	public static final String Pref_AS_AdvancedWrappingAllArgs="Actionscript.advancedWrappingAllArgs";
	public static final String Pref_AS_AdvancedWrappingAllParms="Actionscript.advancedWrappingAllParms";
	public static final String Pref_AS_AdvancedWrappingFirstArg="Actionscript.advancedWrappingFirstArg";
	public static final String Pref_AS_AdvancedWrappingFirstParm="Actionscript.advancedWrappingFirstParm";
	public static final String Pref_AS_AdvancedWrappingAllArrayItems="Actionscript.advancedWrappingAllArrayItems";
	public static final String Pref_AS_AdvancedWrappingAllObjectItems="Actionscript.advancedWrappingAllObjectItems";
	public static final String Pref_AS_AdvancedWrappingFirstObjectItem="Actionscript.advancedWrappingFirstObjectItem";
	public static final String Pref_AS_AdvancedWrappingFirstArrayItem="Actionscript.advancedWrappingFirstArrayItem";
	public static final String Pref_AS_AdvancedWrappingAlignObjectItems="Actionscript.advancedWrappingAlignObjectItems";
	public static final String Pref_AS_AdvancedWrappingAlignArrayItems="Actionscript.advancedWrappingAlignArrayItems";
	

	//parens advanced settings
	public static final String Pref_AS_SpacesInsideParens="Actionscript.spacesInsideParens"; //$NON-NLS-1$
	public static final String Pref_AS_UseGlobalSpacesInsideParens="Actionscript.useGlobalSpacesInsideParens"; //$NON-NLS-1$
	
	/**
	 * @deprecated
 	 */
	public static final String Pref_AS_AdvancedSpacesInsideParens="Actionscript.advancedSpacesInsideParens"; //$NON-NLS-1$
	public static final String Pref_AS_AdvancedSpacesInsideParensInParameterLists= "Actionscript.advancedSpacesInsideParensInParameterLists";			//$NON-NLS-1$
	public static final String Pref_AS_AdvancedSpacesInsideParensInArgumentLists= "Actionscript.advancedSpacesInsideParensInArgumentLists";			//$NON-NLS-1$
	public static final String Pref_AS_AdvancedSpacesInsideParensInOtherPlaces= "Actionscript.advancedSpacesInsideParensInOtherPlaces";			//$NON-NLS-1$
	public static final String Pref_AS_AdvancedSpacesInsideLiteralBraces="Actionscript.advancedSpacesInsideLiteralBraces"; //$NON-NLS-1$
	public static final String Pref_AS_AdvancedSpacesInsideArrayRefBrackets="Actionscript.advancedSpacesInsideArrayRefBrackets"; //$NON-NLS-1$
	public static final String Pref_AS_AdvancedSpacesInsideArrayDeclBrackets="Actionscript.advancedSpacesInsideArrayDeclBrackets"; //$NON-NLS-1$
	
//	public static final String Pref_MXML_BlockIndent="MXML.blockIndent";
	public static final String Pref_MXML_SpacesAroundEquals="MXML.spacesAroundEquals"; //$NON-NLS-1$
	public static final String Pref_MXML_SortExtraAttrs="MXML.sortExtraAttrs"; //$NON-NLS-1$
	public static final String Pref_MXML_AddNewlineAfterLastAttr="MXML.addNewlineAfterLastAttr"; //$NON-NLS-1$
	public static final String Pref_MXML_IndentTagClose="MXML.indentTagClose"; //$NON-NLS-1$
	public static final String Pref_MXML_SortAttrData="MXML.sortAttrData"; //$NON-NLS-1$
	public static final String Pref_MXML_SortAttrMode="MXML.sortAttrMode"; //$NON-NLS-1$
	public static final String Pref_MXML_MaxLineLength = "MXML.maxLineLength"; //$NON-NLS-1$
	public static final String Pref_MXML_AttrWrapMode = "MXML.attrWrapMode"; //$NON-NLS-1$
	public static final String Pref_MXML_AttrsPerLine = "MXML.attrsPerLine"; //$NON-NLS-1$
	public static final String Pref_MXML_KeepBlankLines="MXML.keepBlankLines"; //$NON-NLS-1$
	public static final String Pref_MXML_WrapIndentStyle="MXML.wrapIndentStyle"; //$NON-NLS-1$
	public static final String Pref_MXML_TabsInHangingIndent="MXML.tabCountForHangingIndent"; //$NON-NLS-1$
	public static final String Pref_MXML_TagsCanFormat="MXML.tagsCanFormat"; //$NON-NLS-1$
	public static final String Pref_MXML_TagsCannotFormat="MXML.tagsCannotFormat"; //$NON-NLS-1$
	public static final String Pref_MXML_TagsDoNotFormatInside="MXML.tagsDoNotFormatInside"; //$NON-NLS-1$
	public static final String Pref_MXML_UseTagsDoNotFormatInside="MXML.useTagsDoNotFormatInside"; //$NON-NLS-1$
	public static final String Pref_MXML_TagsWithBlankLinesBefore="MXML.tagsToHaveBlankLinesAddedBeforeThem"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesBeforeTags="MXML.blankLinesBeforeTags"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesBeforeComments="MXML.blankLinesBeforeComments"; //$NON-NLS-1$
	public static final String Pref_MXML_ParentTagsWithBlankLinesAfter="MXML.parentTagsToHaveBlankLinesAddedAfterThem"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesAfterSpecificParentTags="MXML.blankLinesAfterSpecificParentTags"; //$NON-NLS-1$
	public static final String Pref_MXML_AttrGroups="MXML.attrGroups"; //$NON-NLS-1$
	public static final String Pref_MXML_UseAttrsToKeepOnSameLine="MXML.useAttrsToKeepOnSameLine"; //$NON-NLS-1$
	public static final String Pref_MXML_AttrsToKeepOnSameLine="MXML.attrsToKeepOnSameLine"; //$NON-NLS-1$
	public static final String Pref_MXML_AlwaysUseMaxLineLength="MXML.alwaysUseMaxLineLength"; //$NON-NLS-1$
	public static final String Pref_MXML_SpacesBeforeEmptyTagEnd="MXML.spacesBeforeEmptyTagEnd"; //$NON-NLS-1$
	public static final String Pref_MXML_RequireCDATAForASFormatting="MXML.onlyFormatASIfCDATABlock"; //$NON-NLS-1$
	public static final String Pref_MXML_TagsWithASContent="MXML.tagsWithASContent"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesBetweenSiblingTags="MXML.blankLinesBetweenSiblingTags"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesAfterParentTags="MXML.blankLinesAfterParentTags"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesBeforeClosingTags="MXML.blankLinesBeforeClosingTags"; //$NON-NLS-1$
	public static final String Pref_MXML_SpacesInsideAttributeBraces="MXML.spacesInsideAttributeBraces"; //$NON-NLS-1$
	public static final String Pref_MXML_UseSpacesInsideAttributeBraces="MXML.useSpacesInsideAttributeBraces"; //$NON-NLS-1$
	public static final String Pref_MXML_UseFormattingOfBoundAttributes="MXML.useFormattingOfBoundAttributes"; //$NON-NLS-1$
	public static final String Pref_MXML_KeepRelativeIndentInMultilineComments= "MXML.keepRelativeIndentInMultilineComments"; //$NON-NLS-1$
	public static final String Pref_MXML_ScriptCDataIndentTabs= "MXML.ScriptCDataIndentTabs"; //$NON-NLS-1$
	public static final String Pref_MXML_ScriptIndentTabs= "MXML.ScriptIndentTabs"; //$NON-NLS-1$
	public static final String Pref_MXML_KeepScriptCDataOnSameLine= "MXML.KeepScriptCDataOnSameLine"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesAtCDataStart= "MXML.blankLinesAtCDataStart"; //$NON-NLS-1$
	public static final String Pref_MXML_BlankLinesAtCDataEnd= "MXML.blankLinesAtCDataEnd"; //$NON-NLS-1$
	public static final String Pref_MXML_RemoveNamespacesAsPartOfFormat="MXML.doRemoveNamespacesWhileFormatting"; //$NON-NLS-1$
	
	
//	public static final String Pref_AS_UseTabs="Actionscript.useTabs";
//	public static final String Pref_AS_TabSize="Actionscript.tabSize";
	
	public static final String Pref_MXML_AutoFormatStyle="MXML.AutoFormatStyle"; //format=true or indent //$NON-NLS-1$
	public static final String Pref_AS_AutoFormatStyle="AS.AutoFormatStyle"; //format=true or indent //$NON-NLS-1$
	public static final String Pref_MXML_DoAutoFormat="MXML.DoAutoFormat"; //on or off //$NON-NLS-1$
	public static final String Pref_AS_DoAutoFormat="AS.DoAutoFormat"; //on or off //$NON-NLS-1$
	public static final String Pref_Flex_AutoFormat_ExcludePaths="Flex.AutoFormat_PathExcludes"; //comma separated list //$NON-NLS-1$
	public static final String Pref_MXML_AdditionalExtensions="MXML.additionalXMLExtensions"; //comma separated list, no '.' //$NON-NLS-1$
	public static final String Pref_MXML_AutoFormat_AdditionalExtensions="MXML.autoFormatAdditionalXMLExtensions";  //$NON-NLS-1$

	public Initializer() {
		// TODO Auto-generated constructor stub
	}
	
	public Initializer(IPreferenceStore prefStore)
	{
		mPrefStore=prefStore;
	}

	private IPreferenceStore mPrefStore=null;
	
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store=mPrefStore;
		if (store==null)
			store=Activator.getDefault().getPreferenceStore();

//		store.setDefault(Pref_FlexCommand_UseMicroEdits, true);
		
		store.setDefault(Pref_Flex_CheckForUpdates, false);
		store.setDefault(Pref_Flex_UseAutoSyncFile, false);
		store.setDefault(Pref_Flex_WriteToAutoSyncFile, false);
		store.setDefault(Pref_Flex_AutoSyncFile, ""); //$NON-NLS-1$
		store.setDefault(Pref_Flex_AutoSyncLocalChangesDirty, false);
		store.setDefault(Pref_Flex_ShowBatchResultsInDialog, true);
		
		store.setDefault(Pref_Flex_UseTabs, true);
//		store.setDefault(Pref_Flex_IndentSize, 4);
//		store.setDefault(Pref_Flex_TabSize, 4);
		
		///////////////ActionScript/////////////////////////////////////
		
//		store.setDefault(Pref_AS_BlockIndent, 3);
		store.setDefault(Pref_AS_SpacesBeforeComma, 0);
		store.setDefault(Pref_AS_SpacesAfterComma, 1);
		store.setDefault(Pref_AS_SpacesAroundColons, 0);
		store.setDefault(Pref_AS_UseGlobalSpacesAroundColons, true);
		store.setDefault( Pref_AS_AdvancedSpacesBeforeColonsInDeclarations, 0 );
		store.setDefault( Pref_AS_AdvancedSpacesAfterColonsInDeclarations, 0 );
		store.setDefault( Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes, 0 );
		store.setDefault( Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes, 0 );
//		store.setDefault( Pref_AS_AdvancedSpacesAfterColons, -1 );
//		store.setDefault( Pref_AS_AdvancedSpacesBeforeColons, -1 );
		store.setDefault(Pref_AS_BlankLinesBeforeFunctions, 1);
		store.setDefault(Pref_AS_NoNewCRsBeforeBreak, false);
		store.setDefault(Pref_AS_NoNewCRsBeforeContinue, false);
		store.setDefault(Pref_AS_NoNewCRsBeforeReturn, false);
		store.setDefault(Pref_AS_NoNewCRsBeforeThrow, false);
		store.setDefault(Pref_AS_NoNewCRsBeforeExpression, false);
		store.setDefault(Pref_AS_BlankLinesBeforeClasses, 1);
		store.setDefault(Pref_AS_BlankLinesBeforeProperties, 0);
		store.setDefault(Pref_AS_BlankLinesAtFunctionStart, 0);
		store.setDefault(Pref_AS_BlankLinesAtFunctionEnd, 0);
		store.setDefault(Pref_AS_BlankLinesBeforeControlStatements, 0);
		store.setDefault(Pref_AS_BlankLinesBeforeImportBlock, 0);
		store.setDefault(Pref_AS_KeepBlankLines, true);
		store.setDefault(Pref_AS_BlankLinesToKeep, 0);
		store.setDefault(Pref_AS_OpenBraceOnNewLine, true);
		store.setDefault(Pref_AS_ElseOnNewLine, true);
		store.setDefault(Pref_AS_WhileOnNewLine, false);
		store.setDefault(Pref_AS_CatchOnNewLine, true);
		store.setDefault(Pref_AS_ElseIfOnSameLine, true);
		store.setDefault(Pref_AS_MaxLineLength, 200);
		store.setDefault(Pref_AS_SpacesAroundAssignment, 0);
		store.setDefault(Pref_AS_SpacesAroundSymbolicOperator, 1);
		store.setDefault(Pref_AS_KeepSLCommentsOnColumn1, true);
		store.setDefault(Pref_AS_BreakLinesBeforeComma, false);
		store.setDefault(Pref_AS_BreakLinesBeforeAssignment, false);
		store.setDefault(Pref_AS_BreakLinesBeforeArithmetic, false);
		store.setDefault(Pref_AS_BreakLinesBeforeLogical, false);
		store.setDefault(Pref_AS_WrapExpressionMode, WrapOptions.WRAP_NONE);
		store.setDefault(Pref_AS_KeepSpacesBeforeLineComments, false);
		store.setDefault(Pref_AS_AlignLineCommentsAtColumn, 0);
		store.setDefault(Pref_AS_WrapMethodDeclMode, WrapOptions.WRAP_NONE);
		store.setDefault(Pref_AS_WrapMethodCallMode, WrapOptions.WRAP_NONE);
		store.setDefault(Pref_AS_WrapArrayDeclMode, WrapOptions.WRAP_NONE);
		store.setDefault(Pref_AS_WrapXMLMode, WrapOptions.WRAP_DONT_PROCESS);
		store.setDefault(Pref_AS_WrapIndentStyle, WrapOptions.WRAP_STYLE_INDENT_NORMAL);
		store.setDefault(Pref_AS_CollapseSpacesForAdjacentParens, true);
		store.setDefault(Pref_AS_UnindentExpressionTerminators, false);
		store.setDefault(Pref_AS_LeaveSingleLineFunctions, false);
//		store.setDefault(Pref_AS_NewlineAfterBindable, true);
		store.setDefault(Pref_AS_NewlineBeforeBindableFunction, true);
		store.setDefault(Pref_AS_NewlineBeforeBindableProperty, true);
		store.setDefault(Pref_AS_MetaTagsOnSameLineAsTargetFunction, "Bindable");
		store.setDefault(Pref_AS_MetaTagsOnSameLineAsTargetProperty, "Bindable");
		store.setDefault(Pref_AS_SpacesAfterLabel, 1);
		store.setDefault(Pref_AS_TrimTrailingWhitespace, true);
		store.setDefault(Pref_AS_PutEmptyStatementsOnNewLine, true);
		store.setDefault(Pref_AS_SpacesBeforeOpenControlParen, 1);
		store.setDefault(Pref_AS_AlwaysGenerateIndent, false);
		store.setDefault(Pref_AS_DontIndentPackageItems, false);
		store.setDefault(Pref_AS_DontIndentSwitchCases, false);
		store.setDefault(Pref_AS_LeaveExtraWhitespaceAroundVarDecls, false);
		store.setDefault(Pref_AS_AlignDeclEquals, false);
		store.setDefault(Pref_AS_AlignDeclMode, ASPrettyPrinter.Decl_Align_Consecutive);
		store.setDefault(Pref_AS_TabsInHangingIndent, 1);
		store.setDefault(Pref_AS_RearrangeAsPartOfFormat, false);
		store.setDefault(Pref_AS_UseGlobalCRBeforeBrace, true);
		store.setDefault(Pref_AS_SpacesBeforeFormalParameters, 0);
		store.setDefault(Pref_AS_SpacesBeforeArguments, 0);
//		store.setDefault(Pref_AS_EnsureConditionalsHaveBraces, false);
//		store.setDefault(Pref_AS_EnsureLoopsHaveBraces, false);
		store.setDefault(Pref_AS_AddBracesToLoops, ASPrettyPrinter.Braces_NoModify);
		store.setDefault(Pref_AS_AddBracesToCases, ASPrettyPrinter.Braces_NoModify);
		store.setDefault(Pref_AS_AddBracesToConditionals, ASPrettyPrinter.Braces_NoModify);
//		store.setDefault(Pref_AS_EnsureSwitchCasesHaveBraces, false);
		store.setDefault(Pref_AS_KeepRelativeIndentInDocComments, true);
		store.setDefault(Pref_AS_PerformRearrangeWhileFormatting, false);
		store.setDefault(Pref_AS_RemoveTrailingWhitespace, false);
		
		store.setDefault(Pref_AS_UseLineCommentWrapping, false);		
		store.setDefault(Pref_AS_UseDocCommentWrapping, false);		
		store.setDefault(Pref_AS_DocCommentHangingIndentTabs, 0);	
		store.setDefault(Pref_AS_DocCommentReflow, false);		
		store.setDefault(Pref_AS_DocCommentKeepBlankLines, true);
		store.setDefault(Pref_AS_UseMLCommentWrapping, false);		
		store.setDefault(Pref_AS_MLCommentReflow, false);		
		store.setDefault(Pref_AS_MLCommentHeaderOnSeparateLine, false);		
		store.setDefault(Pref_AS_MLCommentKeepBlankLines, true);
		store.setDefault(Pref_AS_MLCommentAsteriskMode, 0);		
		{
//			StringBuffer buffer=new StringBuffer();
//			Properties defaultAdvancedSettings=new Properties();
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_catch, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_class, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_Conditional, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_controlStatement, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_finally, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_functionDecl, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_functionExpression, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_interface, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_package, Boolean.TRUE.toString());
//			defaultAdvancedSettings.setProperty(ASPrettyPrinter.BraceContext_try, Boolean.TRUE.toString());
//			StringWriter writer=new StringWriter();
//			try {
//				defaultAdvancedSettings.store(writer, "");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			for (Map.Entry<String, Boolean> entry : defaultAdvancedSettings.entrySet()) {
//				buffer.append(entry.getKey()+" "+entry.getValue());
//				buffer.append(',');
//			}
			store.setDefault(Pref_AS_AdvancedCRBeforeBraceSettings, 0xFFF);
		}
		
		store.setDefault(Pref_AS_UseAdvancedWrapping, false);
		store.setDefault(Pref_AS_AdvancedWrappingElements, ASPrettyPrinter.Break_Logical_Ops_code | ASPrettyPrinter.Break_Commas_code | ASPrettyPrinter.Break_Assignment_code | ASPrettyPrinter.Break_Keyword_code);
		store.setDefault(Pref_AS_AdvancedWrappingEnforceMax, false);
		store.setDefault(Pref_AS_AdvancedWrappingAllArgs, false);
		store.setDefault(Pref_AS_AdvancedWrappingAllParms, false);
		store.setDefault(Pref_AS_AdvancedWrappingFirstArg, false);
		store.setDefault(Pref_AS_AdvancedWrappingFirstParm, false);
		store.setDefault(Pref_AS_AdvancedWrappingAlignArrayItems, false);
		store.setDefault(Pref_AS_AdvancedWrappingAlignObjectItems, false);
		store.setDefault(Pref_AS_AdvancedWrappingAllArrayItems, false);
		store.setDefault(Pref_AS_AdvancedWrappingAllObjectItems, false);
		store.setDefault(Pref_AS_AdvancedWrappingFirstArrayItem, false);
		store.setDefault(Pref_AS_AdvancedWrappingFirstObjectItem, false);
		store.setDefault(Pref_AS_AdvancedWrappingGraceColumns, 5);
		store.setDefault(Pref_AS_AdvancedWrappingPreservePhrases, true);
		
		store.setDefault(Pref_AS_BraceStyle, ASPrettyPrinter.BraceStyle_Adobe);
//		boolean globalSettingExists=(store.contains(Pref_AS_UseBraceStyle));
//		boolean advancedSettingExists=(store.contains(Pref_AS_OpenBraceOnNewLine));
//		boolean reallyAdvancedSettingExists=(store.contains(Pref_AS_AdvancedCRBeforeBraceSettings));
		store.setDefault(Pref_AS_UseBraceStyle, true); //now this is true  
		store.setDefault(Pref_AS_UseGnuBraceIndent, false);
		
		
		store.setDefault(Pref_AS_SpacesInsideParens, 0);
		store.setDefault(Pref_AS_UseGlobalSpacesInsideParens, true);
		store.setDefault(Pref_AS_AdvancedSpacesInsideArrayDeclBrackets, 1);
		store.setDefault(Pref_AS_AdvancedSpacesInsideArrayRefBrackets, 0);
		store.setDefault(Pref_AS_AdvancedSpacesInsideLiteralBraces, 1);
		store.setDefault( Pref_AS_AdvancedSpacesInsideParensInArgumentLists, 0 );
		store.setDefault( Pref_AS_AdvancedSpacesInsideParensInParameterLists, 0 );
		store.setDefault( Pref_AS_AdvancedSpacesInsideParensInOtherPlaces, 0 );
		
		//set default value for deprecated item so I can identify it?
//		store.setDefault(Pref_AS_AdvancedSpacesInsideParens, -1); //set to invalid value

		store.setDefault(Pref_AS_Tweak_UseSpacesAroundEqualsInOptionalParameters, false);
		store.setDefault(Pref_AS_Tweak_UseSpacesAroundEqualsInMetatags, false);
		store.setDefault(Pref_AS_Tweak_SpacesAroundEqualsInOptionalParameters, 0);
		store.setDefault(Pref_AS_Tweak_SpacesAroundEqualsInMetatags, 0);
		
		//////////////////MXML///////////////////////////////////////
		
//		store.setDefault(Pref_MXML_BlockIndent, 4);
		store.setDefault(Pref_MXML_SpacesAroundEquals, 0);
		store.setDefault(Pref_MXML_SortExtraAttrs, false);
		store.setDefault(Pref_MXML_AddNewlineAfterLastAttr, false);
		store.setDefault(Pref_MXML_IndentTagClose, true);
		store.setDefault(Pref_MXML_SortAttrData, ""); //$NON-NLS-1$
		store.setDefault(Pref_MXML_SortAttrMode, MXMLPrettyPrinter.MXML_ATTR_ORDERING_NONE);
		store.setDefault(Pref_MXML_MaxLineLength, 200);
		store.setDefault(Pref_MXML_AttrsPerLine, 1);
		store.setDefault(Pref_MXML_AttrWrapMode, MXMLPrettyPrinter.MXML_ATTR_WRAP_COUNT_PER_LINE);
		store.setDefault(Pref_MXML_KeepBlankLines, true);
		store.setDefault(Pref_MXML_BlankLinesBeforeTags, 0);
		store.setDefault(Pref_MXML_BlankLinesBeforeComments, 0);
		store.setDefault(Pref_MXML_BlankLinesAfterSpecificParentTags, 0);
		store.setDefault(Pref_MXML_BlankLinesBetweenSiblingTags, 0);
		store.setDefault(Pref_MXML_BlankLinesAfterParentTags, 0);
		store.setDefault(Pref_MXML_BlankLinesBeforeClosingTags, 0);
		store.setDefault(Pref_MXML_SpacesInsideAttributeBraces, 0);
		store.setDefault(Pref_MXML_UseSpacesInsideAttributeBraces, false);
		store.setDefault(Pref_MXML_UseFormattingOfBoundAttributes, false);
		store.setDefault(Pref_MXML_WrapIndentStyle, WrapOptions.WRAP_STYLE_INDENT_TO_WRAP_ELEMENT);
		store.setDefault(Pref_MXML_TabsInHangingIndent, 1);
		store.setDefault(Pref_MXML_TagsCanFormat, "mx:List,fx:List"); //$NON-NLS-1$
		store.setDefault(Pref_MXML_TagsCannotFormat, "mx:String,fx:String"); //$NON-NLS-1$
		store.setDefault(Pref_MXML_TagsDoNotFormatInside, ".*:Model,.*:XML"); //$NON-NLS-1$
		store.setDefault(Pref_MXML_UseTagsDoNotFormatInside, false);
		store.setDefault(Pref_MXML_UseAttrsToKeepOnSameLine, false);
		store.setDefault(Pref_MXML_AlwaysUseMaxLineLength, false);
		store.setDefault(Pref_MXML_KeepRelativeIndentInMultilineComments, true);
		store.setDefault(Pref_MXML_ScriptCDataIndentTabs, 1);
		store.setDefault(Pref_MXML_ScriptIndentTabs, 1);
		store.setDefault(Pref_MXML_KeepScriptCDataOnSameLine, false);
		store.setDefault(Pref_MXML_AttrsToKeepOnSameLine, 4);
		store.setDefault(Pref_MXML_BlankLinesAtCDataStart, 0);
		store.setDefault(Pref_MXML_BlankLinesAtCDataEnd, 0);
		store.setDefault(Pref_MXML_RemoveNamespacesAsPartOfFormat, false);
		store.setDefault(Pref_MXML_SpacesBeforeEmptyTagEnd, 0);
		store.setDefault(Pref_MXML_RequireCDATAForASFormatting, false);
		List<String> eventAttrs=getEvents();
		StringBuffer asTags=new StringBuffer();
		for (String tag : eventAttrs) {
			asTags.append(".*:");
			asTags.append(tag);
			asTags.append(',');
		}
		asTags.append(".*:Script");
		store.setDefault(Pref_MXML_TagsWithASContent, asTags.toString());
		
		List<AttrGroup> defaultGroups=createDefaultGroups();
		StringBuffer buffer=new StringBuffer();
		for (AttrGroup attrGroup : defaultGroups) {
			buffer.append(attrGroup.save());
			buffer.append(CommonPrefComposite.LineSplitter);
		}
		store.setDefault(Pref_MXML_AttrGroups, buffer.toString());
		
		//auto format settings
		store.setDefault(Pref_MXML_DoAutoFormat, false);
		store.setDefault(Pref_AS_DoAutoFormat, false);
		store.setDefault(Pref_MXML_AutoFormatStyle, false);
		store.setDefault(Pref_AS_AutoFormatStyle, false);
		store.setDefault(Pref_Flex_AutoFormat_ExcludePaths, ""); //$NON-NLS-1$
		
		store.setDefault(Pref_MXML_AdditionalExtensions, "xml"); //$NON-NLS-1$
		store.setDefault(Pref_MXML_AutoFormat_AdditionalExtensions, false);
	}
	
	private List<AttrGroup> createDefaultGroups()
	{
		List<AttrGroup> groups=new ArrayList<AttrGroup>();
		
		//property group
		List<String> attrs=new ArrayList<String>();
		attrs.add("allowDisjointSelection"); //$NON-NLS-1$
		attrs.add("allowMultipleSelection"); //$NON-NLS-1$
		attrs.add("allowThumbOverlap"); //$NON-NLS-1$
		attrs.add("allowTrackClick"); //$NON-NLS-1$
		attrs.add("autoLayout"); //$NON-NLS-1$
		attrs.add("autoRepeat"); //$NON-NLS-1$
		attrs.add("automationName"); //$NON-NLS-1$
		attrs.add("cachePolicy"); //$NON-NLS-1$
		attrs.add("class"); //$NON-NLS-1$
		attrs.add("clipContent"); //$NON-NLS-1$
		attrs.add("condenseWhite"); //$NON-NLS-1$
		attrs.add("conversion"); //$NON-NLS-1$
		attrs.add("creationIndex"); //$NON-NLS-1$
		attrs.add("creationPolicy"); //$NON-NLS-1$
		attrs.add("currentState"); //$NON-NLS-1$
		attrs.add("data"); //$NON-NLS-1$
		attrs.add("dataDescriptor"); //$NON-NLS-1$
		attrs.add("dataProvider"); //$NON-NLS-1$
		attrs.add("dataTipFormatFunction"); //$NON-NLS-1$
		attrs.add("dayNames"); //$NON-NLS-1$
		attrs.add("defaultButton"); //$NON-NLS-1$
		attrs.add("direction"); //$NON-NLS-1$
		attrs.add("disabledDays"); //$NON-NLS-1$
		attrs.add("disabledRanges"); //$NON-NLS-1$
		attrs.add("displayedMonth"); //$NON-NLS-1$
		attrs.add("displayedYear"); //$NON-NLS-1$
		attrs.add("doubleClickEnabled"); //$NON-NLS-1$
		attrs.add("emphasized"); //$NON-NLS-1$
		attrs.add("enabled"); //$NON-NLS-1$
		attrs.add("explicitHeight"); //$NON-NLS-1$
		attrs.add("explicitMaxHeight"); //$NON-NLS-1$
		attrs.add("explicitMaxWidth"); //$NON-NLS-1$
		attrs.add("explicitMinHeight"); //$NON-NLS-1$
		attrs.add("explicitMinWidth"); //$NON-NLS-1$
		attrs.add("explicitWidth"); //$NON-NLS-1$
		attrs.add("firstDayOfWeek"); //$NON-NLS-1$
		attrs.add("focusEnabled"); //$NON-NLS-1$
		attrs.add("fontContext"); //$NON-NLS-1$
		attrs.add("height"); //$NON-NLS-1$
		attrs.add("horizontalLineScrollSize"); //$NON-NLS-1$
		attrs.add("horizontalPageScrollSize"); //$NON-NLS-1$
		attrs.add("horizontalScrollBar"); //$NON-NLS-1$
		attrs.add("horizontalScrollPolicy"); //$NON-NLS-1$
		attrs.add("horizontalScrollPosition"); //$NON-NLS-1$
		attrs.add("htmlText"); //$NON-NLS-1$
		attrs.add("icon"); //$NON-NLS-1$
		attrs.add("iconField"); //$NON-NLS-1$
		attrs.add("id"); //$NON-NLS-1$
		attrs.add("imeMode"); //$NON-NLS-1$
		attrs.add("includeInLayout"); //$NON-NLS-1$
		attrs.add("indeterminate"); //$NON-NLS-1$
		attrs.add("label"); //$NON-NLS-1$
		attrs.add("labelField"); //$NON-NLS-1$
		attrs.add("labelFunction"); //$NON-NLS-1$
		attrs.add("labelPlacement"); //$NON-NLS-1$
		attrs.add("labels"); //$NON-NLS-1$
		attrs.add("layout"); //$NON-NLS-1$
		attrs.add("lineScrollSize"); //$NON-NLS-1$
		attrs.add("listData"); //$NON-NLS-1$
		attrs.add("liveDragging"); //$NON-NLS-1$
		attrs.add("maxChars"); //$NON-NLS-1$
		attrs.add("maxHeight"); //$NON-NLS-1$
		attrs.add("maxScrollPosition"); //$NON-NLS-1$
		attrs.add("maxWidth"); //$NON-NLS-1$
		attrs.add("maxYear"); //$NON-NLS-1$
		attrs.add("maximum"); //$NON-NLS-1$
		attrs.add("measuredHeight"); //$NON-NLS-1$
		attrs.add("measuredMinHeight"); //$NON-NLS-1$
		attrs.add("measuredMinWidth"); //$NON-NLS-1$
		attrs.add("measuredWidth"); //$NON-NLS-1$
		attrs.add("menuBarItemRenderer"); //$NON-NLS-1$
		attrs.add("menuBarItems"); //$NON-NLS-1$
		attrs.add("menus"); //$NON-NLS-1$
		attrs.add("minHeight"); //$NON-NLS-1$
		attrs.add("minScrollPosition"); //$NON-NLS-1$
		attrs.add("minWidth"); //$NON-NLS-1$
		attrs.add("minYear"); //$NON-NLS-1$
		attrs.add("minimum"); //$NON-NLS-1$
		attrs.add("mode"); //$NON-NLS-1$
		attrs.add("monthNames"); //$NON-NLS-1$
		attrs.add("monthSymbol"); //$NON-NLS-1$
		attrs.add("mouseFocusEnabled"); //$NON-NLS-1$
		attrs.add("pageScrollSize"); //$NON-NLS-1$
		attrs.add("pageSize"); //$NON-NLS-1$
		attrs.add("percentHeight"); //$NON-NLS-1$
		attrs.add("percentWidth"); //$NON-NLS-1$
		attrs.add("scaleX"); //$NON-NLS-1$
		attrs.add("scaleY"); //$NON-NLS-1$
		attrs.add("scrollPosition"); //$NON-NLS-1$
		attrs.add("selectable"); //$NON-NLS-1$
		attrs.add("selectableRange"); //$NON-NLS-1$
		attrs.add("selected"); //$NON-NLS-1$
		attrs.add("selectedDate"); //$NON-NLS-1$
		attrs.add("selectedField"); //$NON-NLS-1$
		attrs.add("selectedIndex"); //$NON-NLS-1$
		attrs.add("selectedRanges"); //$NON-NLS-1$
		attrs.add("showDataTip"); //$NON-NLS-1$
		attrs.add("showRoot"); //$NON-NLS-1$
		attrs.add("showToday"); //$NON-NLS-1$
		attrs.add("sliderDataTipClass"); //$NON-NLS-1$
		attrs.add("sliderThumbClass"); //$NON-NLS-1$
		attrs.add("snapInterval"); //$NON-NLS-1$
		attrs.add("source"); //$NON-NLS-1$
		attrs.add("states"); //$NON-NLS-1$
		attrs.add("stepSize"); //$NON-NLS-1$
		attrs.add("stickyHighlighting"); //$NON-NLS-1$
		attrs.add("styleName"); //$NON-NLS-1$
		attrs.add("text"); //$NON-NLS-1$
		attrs.add("thumbCount"); //$NON-NLS-1$
		attrs.add("tickInterval"); //$NON-NLS-1$
		attrs.add("tickValues"); //$NON-NLS-1$
		attrs.add("toggle"); //$NON-NLS-1$
		attrs.add("toolTip"); //$NON-NLS-1$
		attrs.add("transitions"); //$NON-NLS-1$
		attrs.add("truncateToFit"); //$NON-NLS-1$
		attrs.add("validationSubField"); //$NON-NLS-1$
		attrs.add("value"); //$NON-NLS-1$
		attrs.add("verticalLineScrollSize"); //$NON-NLS-1$
		attrs.add("verticalPageScrollSize"); //$NON-NLS-1$
		attrs.add("verticalScrollBar"); //$NON-NLS-1$
		attrs.add("verticalScrollPolicy"); //$NON-NLS-1$
		attrs.add("verticalScrollPosition"); //$NON-NLS-1$
		attrs.add("width"); //$NON-NLS-1$
		attrs.add("x"); //$NON-NLS-1$
		attrs.add("y"); //$NON-NLS-1$
		attrs.add("yearNavigationEnabled"); //$NON-NLS-1$
		attrs.add("yearSymbol"); //$NON-NLS-1$
		     
		groups.add(new AttrGroup("properties", attrs, MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true)); //$NON-NLS-1$
		
		attrs=new ArrayList<String>();
		attrs.add("xmlns");
		attrs.add("xmlns:.*");
		groups.add(new AttrGroup("xml_namespaces", attrs, MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true)); //$NON-NLS-1$

		attrs=getEvents();
		groups.add(new AttrGroup("events", attrs, MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true)); //$NON-NLS-1$
		
		attrs=new ArrayList<String>();
		attrs.add("backgroundAlpha"); //$NON-NLS-1$
		attrs.add("backgroundAttachment"); //$NON-NLS-1$
		attrs.add("backgroundColor"); //$NON-NLS-1$
		attrs.add("backgroundDisabledColor"); //$NON-NLS-1$
		attrs.add("backgroundImage"); //$NON-NLS-1$
		attrs.add("backgroundSize");  //$NON-NLS-1$
		attrs.add("backgroundSkin"); //$NON-NLS-1$
		attrs.add("barColor"); //$NON-NLS-1$
		attrs.add("barSkin"); //$NON-NLS-1$
		attrs.add("borderColor");  //$NON-NLS-1$
		attrs.add("borderSides"); //$NON-NLS-1$
		attrs.add("borderSkin"); //$NON-NLS-1$
		attrs.add("borderStyle"); //$NON-NLS-1$
		attrs.add("borderThickness"); //$NON-NLS-1$
		attrs.add("bottom"); //$NON-NLS-1$
		attrs.add("color"); //$NON-NLS-1$
		attrs.add("cornerRadius");  //$NON-NLS-1$
		attrs.add("dataTipOffset"); //$NON-NLS-1$
		attrs.add("dataTipPrecision"); //$NON-NLS-1$
		attrs.add("dataTipStyleName"); //$NON-NLS-1$
		attrs.add("disabledColor"); //$NON-NLS-1$
		attrs.add("disabledIcon"); //$NON-NLS-1$
		attrs.add("disabledIconColor"); //$NON-NLS-1$
		attrs.add("disabledSkin"); //$NON-NLS-1$
		attrs.add("disbledOverlayAlpha"); //$NON-NLS-1$
		attrs.add("downArrowDisabledSkin"); //$NON-NLS-1$
		attrs.add("downArrowDownSkin"); //$NON-NLS-1$
		attrs.add("downArrowOverSkin"); //$NON-NLS-1$
		attrs.add("downArrowUpSkin"); //$NON-NLS-1$
		attrs.add("downIcon"); //$NON-NLS-1$
		attrs.add("downSkin"); //$NON-NLS-1$
		attrs.add("dropShadowColor"); //$NON-NLS-1$
		attrs.add("dropShadowEnabled"); //$NON-NLS-1$
		attrs.add("errorColor"); //$NON-NLS-1$
		attrs.add("fillAlphas");  //$NON-NLS-1$
		attrs.add("fillColors");  //$NON-NLS-1$
		attrs.add("focusAlpha"); //$NON-NLS-1$
		attrs.add("focusBlendMode"); //$NON-NLS-1$
		attrs.add("focusRoundedCorners"); //$NON-NLS-1$
		attrs.add("focusSkin"); //$NON-NLS-1$
		attrs.add("focusThickness"); //$NON-NLS-1$
		attrs.add("fontAntiAliasType"); //$NON-NLS-1$
		attrs.add("fontFamily"); //$NON-NLS-1$
		attrs.add("fontGridFitType"); //$NON-NLS-1$
		attrs.add("fontSharpness"); //$NON-NLS-1$
		attrs.add("fontSize"); //$NON-NLS-1$
		attrs.add("fontStyle"); //$NON-NLS-1$
		attrs.add("fontThickness"); //$NON-NLS-1$
		attrs.add("fontWeight"); //$NON-NLS-1$
		attrs.add("fontfamily"); //$NON-NLS-1$
		attrs.add("headerColors"); //$NON-NLS-1$
		attrs.add("headerStyleName"); //$NON-NLS-1$
		attrs.add("highlightAlphas");  //$NON-NLS-1$
		attrs.add("horizontalAlign"); //$NON-NLS-1$
		attrs.add("horizontalCenter"); //$NON-NLS-1$
		attrs.add("horizontalGap"); //$NON-NLS-1$
		attrs.add("horizontalScrollBarStyleName"); //$NON-NLS-1$
		attrs.add("icon"); //$NON-NLS-1$
		attrs.add("iconColor"); //$NON-NLS-1$
		attrs.add("indeterminateMoveInterval"); //$NON-NLS-1$
		attrs.add("indeterminateSkin"); //$NON-NLS-1$
		attrs.add("itemDownSkin"); //$NON-NLS-1$
		attrs.add("itemOverSkin"); //$NON-NLS-1$
		attrs.add("itemUpSkin"); //$NON-NLS-1$
		attrs.add("kerning"); //$NON-NLS-1$
		attrs.add("labelOffset"); //$NON-NLS-1$
		attrs.add("labelStyleName"); //$NON-NLS-1$
		attrs.add("labelWidth"); //$NON-NLS-1$
		attrs.add("leading"); //$NON-NLS-1$
		attrs.add("left"); //$NON-NLS-1$
		attrs.add("letterSpacing"); //$NON-NLS-1$
		attrs.add("maskSkin"); //$NON-NLS-1$
		attrs.add("menuStyleName"); //$NON-NLS-1$
		attrs.add("nextMonthDisabledSkin"); //$NON-NLS-1$
		attrs.add("nextMonthDownSkin"); //$NON-NLS-1$
		attrs.add("nextMonthOverSkin"); //$NON-NLS-1$
		attrs.add("nextMonthSkin");  //$NON-NLS-1$
		attrs.add("nextMonthUpSkin"); //$NON-NLS-1$
		attrs.add("nextYearDisabledSkin"); //$NON-NLS-1$
		attrs.add("nextYearDownSkin"); //$NON-NLS-1$
		attrs.add("nextYearOverSkin"); //$NON-NLS-1$
		attrs.add("nextYearSkin"); //$NON-NLS-1$
		attrs.add("nextYearUpSkin"); //$NON-NLS-1$
		attrs.add("overIcon"); //$NON-NLS-1$
		attrs.add("overSkin"); //$NON-NLS-1$
		attrs.add("paddingBottom"); //$NON-NLS-1$
		attrs.add("paddingLeft"); //$NON-NLS-1$
		attrs.add("paddingRight"); //$NON-NLS-1$
		attrs.add("paddingTop"); //$NON-NLS-1$
		attrs.add("prevMonthDisabledSkin"); //$NON-NLS-1$
		attrs.add("prevMonthDownSkin"); //$NON-NLS-1$
		attrs.add("prevMonthOverSkin"); //$NON-NLS-1$
		attrs.add("prevMonthSkin "); //$NON-NLS-1$
		attrs.add("prevMonthUpSkin"); //$NON-NLS-1$
		attrs.add("prevYearDisabledSkin"); //$NON-NLS-1$
		attrs.add("prevYearDownSkin"); //$NON-NLS-1$
		attrs.add("prevYearOverSkin"); //$NON-NLS-1$
		attrs.add("prevYearSkin "); //$NON-NLS-1$
		attrs.add("prevYearUpSkin"); //$NON-NLS-1$
		attrs.add("repeatDelay"); //$NON-NLS-1$
		attrs.add("repeatInterval"); //$NON-NLS-1$
		attrs.add("right"); //$NON-NLS-1$
		attrs.add("rollOverColor"); //$NON-NLS-1$
		attrs.add("rollOverIndicatorSkin"); //$NON-NLS-1$
		attrs.add("selectedDisabledIcon"); //$NON-NLS-1$
		attrs.add("selectedDisabledSkin"); //$NON-NLS-1$
		attrs.add("selectedDownIcon"); //$NON-NLS-1$
		attrs.add("selectedDownSkin"); //$NON-NLS-1$
		attrs.add("selectedOverIcon"); //$NON-NLS-1$
		attrs.add("selectedOverSkin"); //$NON-NLS-1$
		attrs.add("selectedUpIcon"); //$NON-NLS-1$
		attrs.add("selectedUpSkin"); //$NON-NLS-1$
		attrs.add("selectionColor"); //$NON-NLS-1$
		attrs.add("selectionIndicatorSkin"); //$NON-NLS-1$
		attrs.add("shadowColor"); //$NON-NLS-1$
		attrs.add("shadowDirection"); //$NON-NLS-1$
		attrs.add("shadowDistance"); //$NON-NLS-1$
		attrs.add("showTrackHighlight"); //$NON-NLS-1$
		attrs.add("skin"); //$NON-NLS-1$
		attrs.add("slideDuration"); //$NON-NLS-1$
		attrs.add("slideEasingFunction"); //$NON-NLS-1$
		attrs.add("strokeColor"); //$NON-NLS-1$
		attrs.add("strokeWidth");		 //$NON-NLS-1$
		attrs.add("textAlign"); //$NON-NLS-1$
		attrs.add("textDecoration"); //$NON-NLS-1$
		attrs.add("textIndent"); //$NON-NLS-1$
		attrs.add("textRollOverColor"); //$NON-NLS-1$
		attrs.add("textSelectedColor"); //$NON-NLS-1$
		attrs.add("themeColor"); //$NON-NLS-1$
		attrs.add("thumbDisabledSkin"); //$NON-NLS-1$
		attrs.add("thumbDownSkin"); //$NON-NLS-1$
		attrs.add("thumbIcon"); //$NON-NLS-1$
		attrs.add("thumbOffset"); //$NON-NLS-1$
		attrs.add("thumbOverSkin"); //$NON-NLS-1$
		attrs.add("thumbUpSkin"); //$NON-NLS-1$
		attrs.add("tickColor"); //$NON-NLS-1$
		attrs.add("tickLength"); //$NON-NLS-1$
		attrs.add("tickOffset"); //$NON-NLS-1$
		attrs.add("tickThickness"); //$NON-NLS-1$
		attrs.add("todayColor"); //$NON-NLS-1$
		attrs.add("todayIndicatorSkin"); //$NON-NLS-1$
		attrs.add("todayStyleName"); //$NON-NLS-1$
		attrs.add("top"); //$NON-NLS-1$
		attrs.add("tracHighlightSkin"); //$NON-NLS-1$
		attrs.add("trackColors"); //$NON-NLS-1$
		attrs.add("trackHeight"); //$NON-NLS-1$
		attrs.add("trackMargin"); //$NON-NLS-1$
		attrs.add("trackSkin"); //$NON-NLS-1$
		attrs.add("upArrowDisabledSkin"); //$NON-NLS-1$
		attrs.add("upArrowDownSkin"); //$NON-NLS-1$
		attrs.add("upArrowOverSkin"); //$NON-NLS-1$
		attrs.add("upArrowUpSkin"); //$NON-NLS-1$
		attrs.add("upIcon"); //$NON-NLS-1$
		attrs.add("upSkin"); //$NON-NLS-1$
		attrs.add("verticalAlign"); //$NON-NLS-1$
		attrs.add("verticalCenter"); //$NON-NLS-1$
		attrs.add("verticalGap"); //$NON-NLS-1$
		attrs.add("verticalScrollBarStyleName"); //$NON-NLS-1$
		attrs.add("weekDayStyleName"); //$NON-NLS-1$
		
		groups.add(new AttrGroup("styles", attrs, MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true)); //$NON-NLS-1$

		
		attrs=new ArrayList<String>();
		attrs.add("addedEffect"); //$NON-NLS-1$
		attrs.add("completeEffect"); //$NON-NLS-1$
		attrs.add("creationCompleteEffect"); //$NON-NLS-1$
		attrs.add("focusInEffect"); //$NON-NLS-1$
		attrs.add("focusOutEffect"); //$NON-NLS-1$
		attrs.add("hideEffect"); //$NON-NLS-1$
		attrs.add("mouseDownEffect"); //$NON-NLS-1$
		attrs.add("mouseUpEffect"); //$NON-NLS-1$
		attrs.add("moveEffect"); //$NON-NLS-1$
		attrs.add("removedEffect"); //$NON-NLS-1$
		attrs.add("resizeEffect"); //$NON-NLS-1$
		attrs.add("rollOutEffect"); //$NON-NLS-1$
		attrs.add("rollOverEffect");     //$NON-NLS-1$
		attrs.add("showEffect"); //$NON-NLS-1$
		
		groups.add(new AttrGroup("effects", attrs, MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true)); //$NON-NLS-1$
	
		groups.add(new AttrGroup(Attr_Group_Other, new ArrayList<String>(), MXMLPrettyPrinter.MXML_Sort_AscByCase, MXMLPrettyPrinter.MXML_ATTR_WRAP_DEFAULT, true)); //$NON-NLS-1$
		return groups;
	}

	public static List<String> getEvents()
	{
		List<String> attrs=new ArrayList<String>();
		attrs.add("add"); //$NON-NLS-1$
		attrs.add("added"); //$NON-NLS-1$
		attrs.add("activate"); //$NON-NLS-1$
		attrs.add("addedToStage"); //$NON-NLS-1$
		attrs.add("buttonDown"); //$NON-NLS-1$
		attrs.add("change"); //$NON-NLS-1$
		attrs.add("childAdd"); //$NON-NLS-1$
		attrs.add("childIndexChange"); //$NON-NLS-1$
		attrs.add("childRemove"); //$NON-NLS-1$
		attrs.add("clickHandler"); //$NON-NLS-1$
		attrs.add("clear"); //$NON-NLS-1$
		attrs.add("click"); //$NON-NLS-1$
		attrs.add("complete"); //$NON-NLS-1$
		attrs.add("contextMenu"); //$NON-NLS-1$
		attrs.add("copy"); //$NON-NLS-1$
		attrs.add("creationComplete"); //$NON-NLS-1$
		attrs.add("currentStateChange"); //$NON-NLS-1$
		attrs.add("currentStateChanging"); //$NON-NLS-1$
		attrs.add("cut"); //$NON-NLS-1$
		attrs.add("dataChange"); //$NON-NLS-1$
		attrs.add("deactivate"); //$NON-NLS-1$
		attrs.add("doubleClick"); //$NON-NLS-1$
		attrs.add("dragComplete"); //$NON-NLS-1$
		attrs.add("dragDrop"); //$NON-NLS-1$
		attrs.add("dragEnter"); //$NON-NLS-1$
		attrs.add("dragExit"); //$NON-NLS-1$
		attrs.add("dragOver"); //$NON-NLS-1$
		attrs.add("dragStart"); //$NON-NLS-1$
		attrs.add("effectEnd"); //$NON-NLS-1$
		attrs.add("effectStart"); //$NON-NLS-1$
		attrs.add("enterFrame"); //$NON-NLS-1$
		attrs.add("enterState"); //$NON-NLS-1$
		attrs.add("exitFrame"); //$NON-NLS-1$
		attrs.add("exitState"); //$NON-NLS-1$
		attrs.add("focusIn"); //$NON-NLS-1$
		attrs.add("focusOut"); //$NON-NLS-1$
		attrs.add("frameConstructed"); //$NON-NLS-1$
		attrs.add("hide"); //$NON-NLS-1$
		attrs.add("httpStatus"); //$NON-NLS-1$
		attrs.add("init"); //$NON-NLS-1$
		attrs.add("initialize"); //$NON-NLS-1$
		attrs.add("invalid"); //$NON-NLS-1$
		attrs.add("ioError"); //$NON-NLS-1$
		attrs.add("itemClick"); //$NON-NLS-1$
		attrs.add("itemRollOut"); //$NON-NLS-1$
		attrs.add("itemRollOver"); //$NON-NLS-1$
		attrs.add("keyDown"); //$NON-NLS-1$
		attrs.add("keyFocusChange"); //$NON-NLS-1$
		attrs.add("keyUp"); //$NON-NLS-1$
		attrs.add("menuHide"); //$NON-NLS-1$
		attrs.add("menuShow");		 //$NON-NLS-1$
		attrs.add("middleClick"); //$NON-NLS-1$
		attrs.add("middleMouseDown"); //$NON-NLS-1$
		attrs.add("middleMouseUp"); //$NON-NLS-1$
		attrs.add("mouseDown"); //$NON-NLS-1$
		attrs.add("mouseUp"); //$NON-NLS-1$
		attrs.add("mouseOver"); //$NON-NLS-1$
		attrs.add("mouseMove"); //$NON-NLS-1$
		attrs.add("mouseOut"); //$NON-NLS-1$
		attrs.add("mouseFocusChange"); //$NON-NLS-1$
		attrs.add("mouseWheel"); //$NON-NLS-1$
		attrs.add("mouseDownOutside"); //$NON-NLS-1$
		attrs.add("mouseWheelOutside"); //$NON-NLS-1$
		attrs.add("move"); //$NON-NLS-1$
		attrs.add("nativeDragComplete"); //$NON-NLS-1$
		attrs.add("nativeDragDrop"); //$NON-NLS-1$
		attrs.add("nativeDragEnter"); //$NON-NLS-1$
		attrs.add("nativeDragExit"); //$NON-NLS-1$
		attrs.add("nativeDragOver"); //$NON-NLS-1$
		attrs.add("nativeDragStart"); //$NON-NLS-1$
		attrs.add("nativeDragUpdate"); //$NON-NLS-1$
		attrs.add("open"); //$NON-NLS-1$
		attrs.add("paste"); //$NON-NLS-1$
		attrs.add("preinitialize"); //$NON-NLS-1$
		attrs.add("progress"); //$NON-NLS-1$
		attrs.add("record"); //$NON-NLS-1$
		attrs.add("remove"); //$NON-NLS-1$
		attrs.add("removed"); //$NON-NLS-1$
		attrs.add("removedFromStage"); //$NON-NLS-1$
		attrs.add("render"); //$NON-NLS-1$
		attrs.add("resize"); //$NON-NLS-1$
		attrs.add("rightClick"); //$NON-NLS-1$
		attrs.add("rightMouseDown"); //$NON-NLS-1$
		attrs.add("rightMouseUp"); //$NON-NLS-1$
		attrs.add("rollOut"); //$NON-NLS-1$
		attrs.add("rollOver"); //$NON-NLS-1$
		attrs.add("scroll"); //$NON-NLS-1$
		attrs.add("securityError"); //$NON-NLS-1$
		attrs.add("selectAll"); //$NON-NLS-1$
		attrs.add("show"); //$NON-NLS-1$
		attrs.add("tabChildrenChange"); //$NON-NLS-1$
		attrs.add("tabEnabledChange"); //$NON-NLS-1$
		attrs.add("tabIndexChange"); //$NON-NLS-1$
		attrs.add("thumbDrag"); //$NON-NLS-1$
		attrs.add("thumbPress"); //$NON-NLS-1$
		attrs.add("thumbRelease"); //$NON-NLS-1$
		attrs.add("toolTipCreate"); //$NON-NLS-1$
		attrs.add("toolTipEnd"); //$NON-NLS-1$
		attrs.add("toolTipHide"); //$NON-NLS-1$
		attrs.add("toolTipShow"); //$NON-NLS-1$
		attrs.add("toolTipShown"); //$NON-NLS-1$
		attrs.add("toolTipStart"); //$NON-NLS-1$
		attrs.add("updateComplete"); //$NON-NLS-1$
		attrs.add("unload"); //$NON-NLS-1$
		attrs.add("valid"); //$NON-NLS-1$
		attrs.add("valueCommit"); //$NON-NLS-1$
		return attrs;
	}

	public static Set<String> getSet(String data, boolean lowerCase)
	{
		Set<String> result=new HashSet<String>();
		String[] items=data.split(AttrOrderConfigDialog.Attr_Grouping_Splitter);
		for (String item : items)
		{
			item=item.trim();
			if (item.length()>0)
			{
				if (lowerCase)
					item=item.toLowerCase();
				result.add(item);
			}
		}
		
		return result;
	}
}
