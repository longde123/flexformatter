package flexasrearrangecodecommand.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final int PropertyHeaders_None=1;
	public static final int PropertyHeaders_All=2;
	public static final int PropertyHeaders_AssociatedOnly=3; //only properties with multiple items corresponding to the same item
	public static final int PropertyHeaders_AssociatedConditional=4; //all properties in header clusters that contain any associated items
	
	public static final String ASRearr_Class = "Class";
	public static final String ASRearr_Function = "Function";
	public static final String ASRearr_Property = "Property";
	
	public static final String ASRearr_Element_Property = "Property";
	public static final String ASRearr_Element_StaticProperty = "Static Property";
	public static final String ASRearr_Element_Function= "Function";
	public static final String ASRearr_Element_StaticFunction= "Static Function";
	public static final String ASRearr_Element_Constructor= "Constructor";
	public static final String ASRearr_Element_Import= "Import";
	public static final String ASRearr_Element_Include= "Include";
//	public static final String ASRearr_Element_Metatag = "Meta Tag";
	public static final String ASRearr_Element_DefineNamespace= "Namespace Definition";
	public static final String ASRearr_Element_UseNamespace= "Namespace Use";
	public static final String ASRearr_Element_DefaultNamespace= "Default Namespace";
	public static final String ASRearr_Element_StaticInitializer= "Static Initializer";
	
	public static final String ASRearr_Metatag_ArrayElementType= "ArrayElementType";
	public static final String ASRearr_Metatag_Bindable= "Bindable";
	public static final String ASRearr_Metatag_DefaultProperty= "DefaultProperty";
	public static final String ASRearr_Metatag_Deprecated= "Deprecated";
	public static final String ASRearr_Metatag_Effect= "Effect";
	public static final String ASRearr_Metatag_Embed= "Embed";
	public static final String ASRearr_Metatag_Event= "Event";
	public static final String ASRearr_Metatag_Exclude= "Exclude";
	public static final String ASRearr_Metatag_ExcludeClass= "ExcludeClass";
	public static final String ASRearr_Metatag_IconFile= "IconFile";
	public static final String ASRearr_Metatag_Inspectable= "Inspectable";
	public static final String ASRearr_Metatag_InstanceType= "InstanceType";
	public static final String ASRearr_Metatag_NonCommittingChangeEvent= "NonCommittingChangeEvent";
	public static final String ASRearr_Metatag_RemoteClass= "RemoteClass";
	public static final String ASRearr_Metatag_Style= "Style";
	public static final String ASRearr_Metatag_SWF= "SWF";
	public static final String ASRearr_Metatag_Transient= "Transient";
	
	
	
	public static final String AS_Pref_Tag_Separator=":";
	public static final String AS_Pref_Line_Separator=",";
	public static final String AS_Pref_MemberSpecPrefix="!";
	public static final String AS_Pref_Equals="=";
	
	public static final String AS_Mod_Public="public";
	public static final String AS_Mod_Protected="protected";
	public static final String AS_Mod_Internal="internal";
	public static final String AS_Mod_Private="private";
	public static final String AS_Mod_Override="override";
	public static final String AS_Mod_Static="static";
	public static final String AS_Mod_Final="final";
	public static final String AS_Mod_Dynamic="dynamic";
	public static final String AS_Mod_Namespace="<namespace>";
	
	
//	public static final String ASDoc_VisibilityFilter_Root = "ASDoc_visibilityFilter_";
//	public static final String ASDoc_ModifierFilter_Root = "ASDoc_modifierFilter_";
	public static final String ASRearr_ModifierOrder_Root = "ASRearr_ModifierOrder_";
	public static final String ASRearr_UseModifierOrder_Root = "ASRearr_UseModifierOrder_";
	public static final String ASRearr_UseSameModifierOrderForAllElements = "ASRearr_UseGlobalModifierOrder";
	
	public static final String NamespaceGeneric="<Namespace>";
	public static final String ASRearr_StatsURL = "ASRearr_StatsURL";
	public static final String ASRearr_CaptureStatsPrompted = "ASRearr_AlreadyPromptedCaptureStats";
	public static final String ASRearr_CaptureStats = "ASRearr_CaptureStats";
	

	public static final String ASRearr_ElementOrder="ASRearr_ElementOrder";
	public static final String ASRearr_BlankLinesBeforeElement="ASRearr_BlankLinesBeforeElement";
	public static final String ASRearr_UseElementOrder="ASRearr_UseElementOrder";
	public static final String ASRearr_UseFunctionVisibilityOrder="ASRearr_UseElementFunctionVisibilityOrder";
	public static final String ASRearr_UsePropertyVisibilityOrder="ASRearr_UseElementPropertyVisibilityOrder";
	public static final String ASRearr_FunctionVisibilityOrder="ASRearr_ElementFunctionVisibilityOrder";
	public static final String ASRearr_PropertyVisibilityOrder="ASRearr_ElementPropertyVisibilityOrder";
	public static final String ASRearr_UseStaticFunctionVisibilityOrder="ASRearr_UseElementStaticFunctionVisibilityOrder";
	public static final String ASRearr_UseStaticPropertyVisibilityOrder="ASRearr_UseElementStaticPropertyVisibilityOrder";
	public static final String ASRearr_StaticFunctionVisibilityOrder="ASRearr_ElementStaticFunctionVisibilityOrder";
	public static final String ASRearr_StaticPropertyVisibilityOrder="ASRearr_ElementStaticPropertyVisibilityOrder";
	public static final String ASRearr_SortFunctions="ASRearr_ElementSortFunctions";
	public static final String ASRearr_SortProperties="ASRearr_ElementSortProperties";
	public static final String ASRearr_SortIncludes="ASRearr_ElementSortIncludes";
	public static final String ASRearr_SortImports="ASRearr_ElementSortImports";
	public static final String ASRearr_SortMetatags="ASRearr_ElementSortMetatags";
	public static final String ASRearr_SortNamespaces="ASRearr_ElementSortNamespaces";
	public static final String ASRearr_SortStaticFunctions="ASRearr_ElementSortStaticFunctions";
	public static final String ASRearr_SortStaticProperties="ASRearr_ElementSortStaticProperties";
	public static final String ASRearr_TreatGettersAndSettersAsProperties="ASRearr_GroupGettersAndSettersWithProperties";
	public static final String ASRearr_TreatGettersAndSettersAsStaticProperties="ASRearr_GroupGettersAndSettersWithStaticProperties";
	public static final String ASRearr_SortGettersAndSettersWithProperties="ASRearr_SortGettersAndSettersWithAssociatedProperties";
	public static final String ASRearr_AddDefaultHeaderForProperties="ASRearr_AddDefaultHeaderForProperties";
//	public static final String ASRearr_AddDefaultHeaderForAssociatedProperties="ASRearr_AddDefaultHeaderForAssociatedProperties";
//	public static final String ASRearr_AddDefaultHeaderForAllProperties="ASRearr_AddDefaultHeaderForAllProperties";
	public static final String ASRearr_SortGettersAndSettersWithStaticProperties="ASRearr_SortGettersAndSettersWithAssociatedStaticProperties";
//	public static final String ASRearr_AddDefaultHeaderForAssociatedStaticProperties="ASRearr_AddDefaultHeaderForAssociatedStaticProperties";
//	public static final String ASRearr_AddDefaultHeaderForAllStaticProperties="ASRearr_AddDefaultHeaderForAllStaticProperties";
	public static final String ASRearr_AddDefaultHeaderForStaticProperties="ASRearr_AddDefaultHeaderForStaticProperties";

	public static final String ASRearr_UseMetatagOrder="ASRearr_UseMetatagOrder";
	public static final String ASRearr_MetatagOrder="ASRearr_MetatagOrder";
	public static final String ASRearr_UseImportOrder="ASRearr_UseImportOrder";
	public static final String ASRearr_ImportOrder="ASRearr_ImportOrder";
	public static final String ASRearr_MoveImportsOutsideClass="ASRearr_MoveImportsOutsideClass";
	public static final String ASRearr_ImportSeparator = "\\n";
	
	public static final String ASRearr_UseSectionHeaders = "ASRearr_UseSectionHeaders";
	public static final String ASRearr_MajorSectionHeader = "ASRearr_MajorSectionHeader";
	public static final String ASRearr_MinorSectionHeader = "ASRearr_MinorSectionHeader";
	public static final String ASRearr_SectionHeaders="ASRearr_SectionHeaderMap";
	public static final String ASRearr_RemoveAllExistingHeaders = "ASRearr_RemoveExistingSectionHeaders";
	public static final String ASRearr_UseSectionHeadersInMXML = "ASRearr_UseSectionHeadersInMXML";
	
	public static final String ASRearr_UseCopyright = "ASRearr_UseCopyrightGeneration";
	public static final String ASRearr_RemoveExistingCopyrightHeaders = "ASRearr_RemoveExistingCopyrightHeaders";
	public static final String ASRearr_CopyrightHeader = "ASRearr_CopyrightHeader";
	public static final String ASRearr_BlanksAfterCopyrightHeader = "ASRearr_BlanksAfterCopyrightHeader";
	
	
	public static final String MXMLRearr_RearrangeTagOrdering = "MXMLRearr_RearrangeTagOrdering";
	public static final String MXMLRearr_UseRearrangeTagOrdering = "MXMLRearr_UseRearrangeTagOrdering";
	public static final String MXMLRearr_RearrangeWhileFormatting = "MXMLRearr_RearrangeWhileFormatting";
	public static final String MXMLUnmatchedTagsConstant="###UnmatchedTags###";
}
