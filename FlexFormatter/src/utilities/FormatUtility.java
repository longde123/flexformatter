package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import flexprettyprint.handlers.ASPrettyPrinter;
import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprint.handlers.WrapOptions;
import flexprettyprint.preferences.AttrGroup;
import flexprettyprint.preferences.CommonPrefComposite;
import flexprettyprint.preferences.Initializer;

public class FormatUtility
{
	
	public static void configureMXMLPrinter(MXMLPrettyPrinter printer, IPreferenceStore store, int tabSize)
	{
		printer.setStore(store);
		printer.setAttrSortMode(store.getInt(Initializer.Pref_MXML_SortAttrMode));
//		printer.setIndentAmount(store.getInt(Initializer.Pref_Flex_IndentSize));
		printer.setUseTabs(store.getBoolean(Initializer.Pref_Flex_UseTabs));
//		printer.setTabSize(store.getInt(Initializer.Pref_Flex_TabSize));
		printer.setTabSize(tabSize);
		printer.setIndentAmount(tabSize);
		printer.setManualAttrSortData(Arrays.asList(store.getString(Initializer.Pref_MXML_SortAttrData).split("\n")));
		printer.setSortOtherAttrs(store.getBoolean(Initializer.Pref_MXML_SortExtraAttrs));
		printer.setSpacesAroundEquals(store.getInt(Initializer.Pref_MXML_SpacesAroundEquals));
		printer.setSpacesBeforeEmptyTagEnd(store.getInt(Initializer.Pref_MXML_SpacesBeforeEmptyTagEnd));
		printer.setKeepBlankLines(store.getBoolean(Initializer.Pref_MXML_KeepBlankLines));
		printer.setKeepRelativeCommentIndent(store.getBoolean(Initializer.Pref_MXML_KeepRelativeIndentInMultilineComments));
		printer.setBlankLinesBeforeComments(store.getInt(Initializer.Pref_MXML_BlankLinesBeforeComments));
		printer.setBlankLinesBeforeTags(store.getInt(Initializer.Pref_MXML_BlankLinesBeforeTags));
		printer.setBlankLinesAfterSpecificParentTags(store.getInt(Initializer.Pref_MXML_BlankLinesAfterSpecificParentTags));
		printer.setSpacesBetweenSiblingTags(store.getInt(Initializer.Pref_MXML_BlankLinesBetweenSiblingTags));
		printer.setSpacesAfterParentTags(store.getInt(Initializer.Pref_MXML_BlankLinesAfterParentTags));
		printer.setBlankLinesBeforeCloseTags(store.getInt(Initializer.Pref_MXML_BlankLinesBeforeClosingTags));
		printer.setWrapStyle(store.getInt(Initializer.Pref_MXML_WrapIndentStyle));
		printer.setHangingIndentTabs(store.getInt(Initializer.Pref_MXML_TabsInHangingIndent));
		printer.setUseSpacesInsideAttrBraces(store.getBoolean(Initializer.Pref_MXML_UseSpacesInsideAttributeBraces));
		printer.setFormatBoundAttributes(store.getBoolean(Initializer.Pref_MXML_UseFormattingOfBoundAttributes));
		printer.setSpacesInsideAttrBraces(store.getInt(Initializer.Pref_MXML_SpacesInsideAttributeBraces));
		printer.setCDATAIndentTabs(store.getInt(Initializer.Pref_MXML_ScriptCDataIndentTabs));
		printer.setScriptIndentTabs(store.getInt(Initializer.Pref_MXML_ScriptIndentTabs));
		printer.setBlankLinesAtCDataStart(store.getInt(Initializer.Pref_MXML_BlankLinesAtCDataStart));
		printer.setBlankLinesAtCDataEnd(store.getInt(Initializer.Pref_MXML_BlankLinesAtCDataStart)); //TODO: change pref constant if I split them on the pref page
		printer.setKeepCDataOnSameLine(store.getBoolean(Initializer.Pref_MXML_KeepScriptCDataOnSameLine));
		printer.setMaxLineLength(store.getInt(Initializer.Pref_MXML_MaxLineLength));
		printer.setWrapMode(store.getInt(Initializer.Pref_MXML_AttrWrapMode));
		printer.setAttrsPerLine(store.getInt(Initializer.Pref_MXML_AttrsPerLine));
		printer.setAddNewlineAfterLastAttr(store.getBoolean(Initializer.Pref_MXML_AddNewlineAfterLastAttr));
		printer.setIndentCloseTag(store.getBoolean(Initializer.Pref_MXML_IndentTagClose));
		printer.setUseAttrsToKeepOnSameLine(store.getBoolean(Initializer.Pref_MXML_UseAttrsToKeepOnSameLine));
		printer.setRequireCDATAForASContent(store.getBoolean(Initializer.Pref_MXML_RequireCDATAForASFormatting));
		printer.setAttrsToKeepOnSameLine(store.getInt(Initializer.Pref_MXML_AttrsToKeepOnSameLine));
		printer.setObeyMaxLineLength(store.getBoolean(Initializer.Pref_MXML_AlwaysUseMaxLineLength));
		
		String[] tags=store.getString(Initializer.Pref_MXML_TagsCannotFormat).split(",");
		Set<String> tagSet=new HashSet<String>();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				tagSet.add(tag);
			}
		}
		printer.setTagsThatCannotBeFormatted(tagSet);
		
		tags=store.getString(Initializer.Pref_MXML_TagsCanFormat).split(",");
		tagSet=new HashSet<String>();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				tagSet.add(tag);
			}
		}
		printer.setTagsThatCanBeFormatted(tagSet);
		
		tags=store.getString(Initializer.Pref_MXML_TagsWithBlankLinesBefore).split(",");
		tagSet=new HashSet<String>();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				tagSet.add(tag);
			}
		}
		printer.setTagsWithBlankLinesBeforeThem(tagSet);
		
		tags=store.getString(Initializer.Pref_MXML_ParentTagsWithBlankLinesAfter).split(",");
		tagSet=new HashSet<String>();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				tagSet.add(tag);
			}
		}
		printer.setParentTagsWithBlankLinesAfterThem(tagSet);
		
		tags=store.getString(Initializer.Pref_MXML_TagsWithASContent).split(",");
		tagSet=new HashSet<String>();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				tagSet.add(tag);
			}
		}
		printer.setASScriptTags(tagSet);
		
		List<AttrGroup> attrGroups=new ArrayList<AttrGroup>();
		String groupData=store.getString(Initializer.Pref_MXML_AttrGroups);
		String[] groups=groupData.split(CommonPrefComposite.LineSplitter);
		for (String g: groups) {
			AttrGroup group=AttrGroup.load(g);
			if (group!=null)
				attrGroups.add(group);
		}
		printer.setAttrGroups(attrGroups);
		
		printer.setUsePrivateTags(store.getBoolean(Initializer.Pref_MXML_UseTagsDoNotFormatInside));
		tags=store.getString(Initializer.Pref_MXML_TagsDoNotFormatInside).split(",");
		List<String> tagList=new ArrayList<String>();
		for (String tag : tags) {
			if (tag.length()>0)
			{
				tagList.add(tag);
			}
		}
		printer.setPrivateTags(tagList);

		configureASPrinter(printer.getASPrinter(), store, tabSize);
//		printer.getASPrinter().setBlankLinesBeforeImports(0); //special case: we only want blank lines before imports in .as files
	}

	public static void configureASPrinter(ASPrettyPrinter printer, IPreferenceStore store, int tabSize)
	{
		printer.setBlankLinesBeforeFunction(store.getInt(Initializer.Pref_AS_BlankLinesBeforeFunctions));
		printer.setBlankLinesBeforeClass(store.getInt(Initializer.Pref_AS_BlankLinesBeforeClasses));
		printer.setBlankLinesBeforeControlStatement(store.getInt(Initializer.Pref_AS_BlankLinesBeforeControlStatements));
		printer.setBlankLinesBeforeImports(store.getInt(Initializer.Pref_AS_BlankLinesBeforeImportBlock));
		printer.setBlankLinesBeforeProperties(store.getInt(Initializer.Pref_AS_BlankLinesBeforeProperties));
		printer.setBlankLinesToStartFunctions(store.getInt(Initializer.Pref_AS_BlankLinesAtFunctionStart));
		printer.setBlankLinesToEndFunctions(store.getInt(Initializer.Pref_AS_BlankLinesAtFunctionEnd));
		printer.setBlockIndent(tabSize);
		printer.setUseTabs(store.getBoolean(Initializer.Pref_Flex_UseTabs));
		printer.setTabSize(tabSize);
		printer.setSpacesAfterComma(store.getInt(Initializer.Pref_AS_SpacesAfterComma));
		printer.setSpacesBeforeComma(store.getInt(Initializer.Pref_AS_SpacesBeforeComma));
		printer.setCRBeforeOpenBrace(store.getBoolean(Initializer.Pref_AS_OpenBraceOnNewLine));
		printer.setCRBeforeCatch(store.getBoolean(Initializer.Pref_AS_CatchOnNewLine));
		printer.setCRBeforeElse(store.getBoolean(Initializer.Pref_AS_ElseOnNewLine));
		printer.setCRBeforeWhile(store.getBoolean(Initializer.Pref_AS_WhileOnNewLine));
		printer.setUseBraceStyleSetting(store.getBoolean(Initializer.Pref_AS_UseBraceStyle));
		printer.setBraceStyleSetting(store.getInt(Initializer.Pref_AS_BraceStyle));
		printer.setKeepBlankLines(store.getBoolean(Initializer.Pref_AS_KeepBlankLines));
		printer.setBlankLinesToKeep(store.getInt(Initializer.Pref_AS_BlankLinesToKeep));
		printer.setSpacesAroundAssignment(store.getInt(Initializer.Pref_AS_SpacesAroundAssignment));
		printer.setAdvancedSpacesAroundAssignmentInOptionalParameters(store.getInt(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInOptionalParameters));
		printer.setUseAdvancedSpacesAroundAssignmentInOptionalParameters(store.getBoolean(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInOptionalParameters));
		printer.setAdvancedSpacesAroundAssignmentInMetatags(store.getInt(Initializer.Pref_AS_Tweak_SpacesAroundEqualsInMetatags));
		printer.setUseAdvancedSpacesAroundAssignmentInMetatags(store.getBoolean(Initializer.Pref_AS_Tweak_UseSpacesAroundEqualsInMetatags));
		printer.setSpacesAroundColons(store.getInt(Initializer.Pref_AS_SpacesAroundColons));
		printer.setAdvancedSpacesAfterColonsInDeclarations( store.getInt( Initializer.Pref_AS_AdvancedSpacesAfterColonsInDeclarations ) );
		printer.setAdvancedSpacesBeforeColonsInDeclarations( store.getInt( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInDeclarations ) );
		printer.setAdvancedSpacesAfterColonsInFunctionTypes( store.getInt( Initializer.Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes ) );
		printer.setAdvancedSpacesBeforeColonsInFunctionTypes( store.getInt( Initializer.Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes ) );
		printer.setUseGlobalSpacesAroundColons(store.getBoolean(Initializer.Pref_AS_UseGlobalSpacesAroundColons));
		printer.setMaxLineLength(store.getInt(Initializer.Pref_AS_MaxLineLength));
		printer.setExpressionSpacesAroundSymbolicOperators(store.getInt(Initializer.Pref_AS_SpacesAroundSymbolicOperator));
		printer.setKeepElseIfOnSameLine(store.getBoolean(Initializer.Pref_AS_ElseIfOnSameLine));
		printer.setKeepSingleLineCommentsAtColumn1(store.getBoolean(Initializer.Pref_AS_KeepSLCommentsOnColumn1));
		printer.setUseLineCommentWrapping(store.getBoolean(Initializer.Pref_AS_UseLineCommentWrapping));
		printer.setUseMLCommentWrapping(store.getBoolean(Initializer.Pref_AS_UseMLCommentWrapping));
		printer.setMLCommentCollapseLines(store.getBoolean(Initializer.Pref_AS_MLCommentReflow));
		printer.setDocCommentCollapseLines(store.getBoolean(Initializer.Pref_AS_DocCommentReflow));
		printer.setMLTextOnNewLines(store.getBoolean(Initializer.Pref_AS_MLCommentHeaderOnSeparateLine));
		printer.setMLAsteriskMode(store.getInt(Initializer.Pref_AS_MLCommentAsteriskMode));
		printer.setUseDocCommentWrapping(store.getBoolean(Initializer.Pref_AS_UseDocCommentWrapping));
		printer.setDocCommentHangingIndentTabs(store.getInt( Initializer.Pref_AS_DocCommentHangingIndentTabs) );
		printer.setDocCommentKeepBlankLines(store.getBoolean(Initializer.Pref_AS_DocCommentKeepBlankLines));
		printer.setMLCommentKeepBlankLines(store.getBoolean(Initializer.Pref_AS_MLCommentKeepBlankLines));
		printer.setKeepSingleLineFunctions(store.getBoolean(Initializer.Pref_AS_LeaveSingleLineFunctions));
		
		printer.setNoIndentForTerminators(store.getBoolean(Initializer.Pref_AS_UnindentExpressionTerminators));
		printer.setNoCRBeforeBreak(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeBreak));
		printer.setNoCRBeforeContinue(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeContinue));
		printer.setNoCRBeforeReturn(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeReturn));
		printer.setNoCRBeforeThrow(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeThrow));
		printer.setNoCRBeforeExpressions(store.getBoolean(Initializer.Pref_AS_NoNewCRsBeforeExpression));
		printer.setKeepRelativeCommentIndent(store.getBoolean(Initializer.Pref_AS_KeepRelativeIndentInDocComments));
		printer.setSpacesInsideParensEtc(store.getInt(Initializer.Pref_AS_SpacesInsideParens));
		printer.setUseSpacesInsideParensEtc(store.getBoolean(Initializer.Pref_AS_UseGlobalSpacesInsideParens));
		printer.setHangingIndentTabs(store.getInt(Initializer.Pref_AS_TabsInHangingIndent));
		printer.setAdvancedSpacesInsideArrayDeclBrackets(store.getInt(Initializer.Pref_AS_AdvancedSpacesInsideArrayDeclBrackets));
		printer.setAdvancedSpacesInsideArrayReferenceBrackets(store.getInt(Initializer.Pref_AS_AdvancedSpacesInsideArrayRefBrackets));
		printer.setAdvancedSpacesInsideObjectBraces(store.getInt(Initializer.Pref_AS_AdvancedSpacesInsideLiteralBraces));
		printer.setAdvancedSpacesInsideParensInOtherPlaces( store.getInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInOtherPlaces) );
		printer.setAdvancedSpacesInsideParensInParameterLists(store.getInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInParameterLists) );
		printer.setAdvancedSpacesInsideParensInArgumentLists(store.getInt( Initializer.Pref_AS_AdvancedSpacesInsideParensInArgumentLists) );
		printer.setSpacesBetweenControlKeywordsAndParens(store.getInt(Initializer.Pref_AS_SpacesBeforeOpenControlParen));
		printer.setSpacesBeforeFormalParameters(store.getInt(Initializer.Pref_AS_SpacesBeforeFormalParameters));
		printer.setSpacesBeforeArguments(store.getInt(Initializer.Pref_AS_SpacesBeforeArguments));
		printer.setAlwaysGenerateIndent(store.getBoolean(Initializer.Pref_AS_AlwaysGenerateIndent));
		printer.setUseGNUBraceIndent(store.getBoolean(Initializer.Pref_AS_UseGnuBraceIndent));
//		printer.setLoopBraceMode(store.getBoolean(Initializer.Pref_AS_EnsureLoopsHaveBraces) ? ASPrettyPrinter.Braces_AddIfMissing : ASPrettyPrinter.Braces_NoModify);
		printer.setLoopBraceMode(store.getInt(Initializer.Pref_AS_AddBracesToLoops));
//		printer.setSwitchBraceMode(store.getBoolean(Initializer.Pref_AS_EnsureSwitchCasesHaveBraces) ? ASPrettyPrinter.Braces_AddIfMissing : ASPrettyPrinter.Braces_NoModify);
		printer.setSwitchBraceMode(store.getInt(Initializer.Pref_AS_AddBracesToCases));
//		printer.setConditionalBraceMode(store.getBoolean(Initializer.Pref_AS_EnsureConditionalsHaveBraces) ? ASPrettyPrinter.Braces_AddIfMissing : ASPrettyPrinter.Braces_NoModify);
		printer.setConditionalBraceMode(store.getInt(Initializer.Pref_AS_AddBracesToConditionals));
		printer.setIndentAtPackageLevel(!store.getBoolean(Initializer.Pref_AS_DontIndentPackageItems));
		printer.setIndentSwitchCases(!store.getBoolean(Initializer.Pref_AS_DontIndentSwitchCases));
		printer.setKeepingExcessDeclWhitespace(store.getBoolean(Initializer.Pref_AS_LeaveExtraWhitespaceAroundVarDecls));
		printer.setAlignDeclEquals(store.getBoolean(Initializer.Pref_AS_AlignDeclEquals));
		printer.setAlignDeclMode(store.getInt(Initializer.Pref_AS_AlignDeclMode));
		printer.setKeepSpacesBeforeLineComments(store.getBoolean(Initializer.Pref_AS_KeepSpacesBeforeLineComments));
		printer.setLineCommentColumn(store.getInt(Initializer.Pref_AS_AlignLineCommentsAtColumn));
		printer.setUseGlobalNewlineBeforeBraceSetting(store.getBoolean(Initializer.Pref_AS_UseGlobalCRBeforeBrace));
		printer.setAdvancedNewlineBeforeBraceSettings(store.getInt(Initializer.Pref_AS_AdvancedCRBeforeBraceSettings));
		printer.setCollapseSpaceForAdjacentParens(store.getBoolean(Initializer.Pref_AS_CollapseSpacesForAdjacentParens));
//		printer.setNewlineAfterBindable(store.getBoolean(Initializer.Pref_AS_NewlineAfterBindable));
		printer.setNewlineBeforeBindableFunction(store.getBoolean(Initializer.Pref_AS_NewlineBeforeBindableFunction));
		printer.setNewlineBeforeBindableProperty(store.getBoolean(Initializer.Pref_AS_NewlineBeforeBindableProperty));
		Set<String> tags=new HashSet<String>();
		tags.addAll(Arrays.asList(store.getString(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetFunction).split(",")));
		printer.setMetaTagsToKeepOnSameLineAsFunction(tags);
		tags.clear();
		tags.addAll(Arrays.asList(store.getString(Initializer.Pref_AS_MetaTagsOnSameLineAsTargetProperty).split(",")));
		printer.setMetaTagsToKeepOnSameLineAsProperty(tags);
//		printer.setTrimTrailingWS(store.getBoolean(Initializer.Pref_AS_TrimTrailingWhitespace));
		printer.setSpacesAfterLabel(store.getInt(Initializer.Pref_AS_SpacesAfterLabel));
		printer.setEmptyStatementsOnNewLine(store.getBoolean(Initializer.Pref_AS_PutEmptyStatementsOnNewLine));
		printer.setUseAdvancedWrapping(store.getBoolean(Initializer.Pref_AS_UseAdvancedWrapping));
		printer.setAdvancedWrappingElements(store.getInt(Initializer.Pref_AS_AdvancedWrappingElements));
		printer.setAdvancedWrappingEnforceMax(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingEnforceMax));
		printer.setWrapAllArgumentsIfAny(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllArgs));
		printer.setWrapAllParametersIfAny(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllParms));
		printer.setWrapFirstArgument(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstArg));
		printer.setWrapFirstParameter(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstParm));
		printer.setWrapFirstArrayItem(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstArrayItem));
		printer.setWrapFirstObjectItem(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingFirstObjectItem));
		printer.setWrapAllArrayItemsIfAny(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllArrayItems));
		printer.setWrapAllObjectItemsIfAny(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAllObjectItems));
		printer.setWrapArrayItemsAlignStart(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAlignArrayItems));
		printer.setWrapObjectItemsAlignStart(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingAlignObjectItems));
		printer.setAdvancedWrappingGraceColumns(store.getInt(Initializer.Pref_AS_AdvancedWrappingGraceColumns));
		printer.setAdvancedWrappingPreservePhrases(store.getBoolean(Initializer.Pref_AS_AdvancedWrappingPreservePhrases));
		
		boolean breakBeforeComma=store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeComma);
		boolean breakBeforeArithmetic=store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeArithmetic);
		boolean breakBeforeLogical=store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeLogical);
		boolean breakBeforeAssign=store.getBoolean(Initializer.Pref_AS_BreakLinesBeforeAssignment);
		int wrapIndentStyle=store.getInt(Initializer.Pref_AS_WrapIndentStyle);
		WrapOptions options=new WrapOptions(store.getInt(Initializer.Pref_AS_WrapArrayDeclMode));
		options.setBeforeSeparator(breakBeforeComma);
		options.setBeforeArithmeticOperator(breakBeforeArithmetic);
		options.setBeforeLogicalOperator(breakBeforeLogical);
		options.setBeforeAssignmentOperator(breakBeforeAssign);
		options.setIndentStyle(wrapIndentStyle);
		printer.setArrayInitWrapOptions(options);

		options=new WrapOptions(store.getInt(Initializer.Pref_AS_WrapMethodCallMode));
		options.setBeforeSeparator(breakBeforeComma);
		options.setBeforeArithmeticOperator(breakBeforeArithmetic);
		options.setBeforeLogicalOperator(breakBeforeLogical);
		options.setBeforeAssignmentOperator(breakBeforeAssign);
		options.setIndentStyle(wrapIndentStyle);
		printer.setMethodCallWrapOptions(options);

		options=new WrapOptions(store.getInt(Initializer.Pref_AS_WrapMethodDeclMode));
		options.setBeforeSeparator(breakBeforeComma);
		options.setIndentStyle(wrapIndentStyle);
		printer.setMethodDeclWrapOptions(options);

		options=new WrapOptions(store.getInt(Initializer.Pref_AS_WrapExpressionMode));
		options.setBeforeSeparator(breakBeforeComma);
		options.setBeforeArithmeticOperator(breakBeforeArithmetic);
		options.setBeforeLogicalOperator(breakBeforeLogical);
		options.setBeforeAssignmentOperator(breakBeforeAssign);
		options.setIndentStyle(wrapIndentStyle);
		printer.setExpressionWrapOptions(options);

		options=new WrapOptions(store.getInt(Initializer.Pref_AS_WrapXMLMode));
		options.setBeforeSeparator(breakBeforeComma);
		options.setBeforeArithmeticOperator(breakBeforeArithmetic);
		options.setBeforeLogicalOperator(breakBeforeLogical);
		options.setBeforeAssignmentOperator(breakBeforeAssign);
		options.setIndentStyle(wrapIndentStyle);
		printer.setXMLWrapOptions(options);
	}

}
