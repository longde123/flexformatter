package flexasrearrangecodecommand.preferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import flexprettyprint.preferences.AttrGroup;
import flexprettyprint.preferences.CommonPrefComposite;
import flexprettyprint.preferences.SectionHeader;
import flexprettyprintcommand.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
		String order=PreferenceConstants.NamespaceGeneric+",override,native,public,private,protected,internal,static,dynamic,final";
		store.setDefault(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class, order);
		store.setDefault(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function, order);
		store.setDefault(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property, order);

		store.setDefault(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class, true);
		store.setDefault(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function, true);
		store.setDefault(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property, true);
		
		store.setDefault(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements, true);
		
		
		StringBuffer buffer=new StringBuffer();
		String[] modOrder=new String[]{
				PreferenceConstants.AS_Mod_Public,
				PreferenceConstants.AS_Mod_Protected,
				PreferenceConstants.AS_Mod_Internal,
				PreferenceConstants.AS_Mod_Private,
				};
		boolean[] useOrder=new boolean[]{true, true, true, true};
		for (int i=0;i<modOrder.length;i++)
		{
			buffer.append(modOrder[i]+PreferenceConstants.AS_Pref_Tag_Separator+useOrder[i]+PreferenceConstants.AS_Pref_Line_Separator);
		}
		store.setDefault(PreferenceConstants.ASRearr_FunctionVisibilityOrder, buffer.toString());
		store.setDefault(PreferenceConstants.ASRearr_PropertyVisibilityOrder, buffer.toString());
		store.setDefault(PreferenceConstants.ASRearr_UseFunctionVisibilityOrder, true);
		store.setDefault(PreferenceConstants.ASRearr_UsePropertyVisibilityOrder, true);
		store.setDefault(PreferenceConstants.ASRearr_StaticFunctionVisibilityOrder, buffer.toString());
		store.setDefault(PreferenceConstants.ASRearr_StaticPropertyVisibilityOrder, buffer.toString());
		store.setDefault(PreferenceConstants.ASRearr_UseStaticFunctionVisibilityOrder, true);
		store.setDefault(PreferenceConstants.ASRearr_UseStaticPropertyVisibilityOrder, true);
		store.setDefault(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties, false);
		store.setDefault(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties, false);
		store.setDefault(PreferenceConstants.ASRearr_SortGettersAndSettersWithProperties, true);
		store.setDefault(PreferenceConstants.ASRearr_SortGettersAndSettersWithStaticProperties, true);
		store.setDefault(PreferenceConstants.ASRearr_AddDefaultHeaderForProperties, PreferenceConstants.PropertyHeaders_None);
		store.setDefault(PreferenceConstants.ASRearr_AddDefaultHeaderForStaticProperties, PreferenceConstants.PropertyHeaders_None);
//		store.setDefault(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedProperties, false);
//		store.setDefault(PreferenceConstants.ASRearr_AddDefaultHeaderForAssociatedStaticProperties, false);
//		store.setDefault(PreferenceConstants.ASRearr_AddDefaultHeaderForAllProperties, false);
//		store.setDefault(PreferenceConstants.ASRearr_AddDefaultHeaderForAllStaticProperties, false);
		
		String elementOrder=PreferenceConstants.ASRearr_Element_Import+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_Include+PreferenceConstants.AS_Pref_Line_Separator+
//			PreferenceConstants.ASRearr_Element_Metatag+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_DefineNamespace+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_DefaultNamespace+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_UseNamespace+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_StaticProperty+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_StaticFunction+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_StaticInitializer+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_Constructor+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_Property+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Element_Function+PreferenceConstants.AS_Pref_Line_Separator;
		store.setDefault(PreferenceConstants.ASRearr_BlankLinesBeforeElement, ""); //name=value,name=value format
		store.setDefault(PreferenceConstants.ASRearr_ElementOrder, elementOrder);
		store.setDefault(PreferenceConstants.ASRearr_UseElementOrder, true);
		store.setDefault(PreferenceConstants.ASRearr_SortFunctions, true);
		store.setDefault(PreferenceConstants.ASRearr_SortProperties, true);
		store.setDefault(PreferenceConstants.ASRearr_SortStaticFunctions, true);
		store.setDefault(PreferenceConstants.ASRearr_SortStaticProperties, true);
		store.setDefault(PreferenceConstants.ASRearr_SortImports, true);
		store.setDefault(PreferenceConstants.ASRearr_SortIncludes, true);
		store.setDefault(PreferenceConstants.ASRearr_SortNamespaces, true);
		store.setDefault(PreferenceConstants.ASRearr_SortMetatags, false);
		store.setDefault(PreferenceConstants.ASRearr_UseImportOrder, true);
		store.setDefault(PreferenceConstants.ASRearr_UseMetatagOrder, false);
		store.setDefault(PreferenceConstants.ASRearr_MoveImportsOutsideClass, true);
		
		store.setDefault(PreferenceConstants.ASRearr_ImportOrder, "adobe,com,flash,mx");
		
		String tagOrder=PreferenceConstants.ASRearr_Metatag_ArrayElementType+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Bindable+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_DefaultProperty+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Deprecated+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Effect+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Embed+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Event+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Exclude+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_ExcludeClass+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_IconFile+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Inspectable+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_InstanceType+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_NonCommittingChangeEvent+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_RemoteClass+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Style+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_SWF+PreferenceConstants.AS_Pref_Line_Separator+
			PreferenceConstants.ASRearr_Metatag_Transient+PreferenceConstants.AS_Pref_Line_Separator;
		store.setDefault(PreferenceConstants.ASRearr_MetatagOrder, tagOrder);
		
		if (!store.getString(PreferenceConstants.ASRearr_ElementOrder).contains(PreferenceConstants.ASRearr_Element_StaticInitializer))
		{
			store.setValue(PreferenceConstants.ASRearr_ElementOrder, store.getString(PreferenceConstants.ASRearr_ElementOrder)+PreferenceConstants.ASRearr_Element_StaticInitializer+PreferenceConstants.AS_Pref_Line_Separator);
		}
		
		store.setDefault(PreferenceConstants.ASRearr_MajorSectionHeader, new SectionHeader(SectionHeader.AS_Section_Style_SlashSlash, 60, 4, "-", new String[]{"   Major section header"}, 1).save());
		store.setDefault(PreferenceConstants.ASRearr_MinorSectionHeader, new SectionHeader(SectionHeader.AS_Section_Style_SlashSlash, 40, 0, "-", new String[]{"   Minor section"}, 1).save());
		store.setDefault(PreferenceConstants.ASRearr_SectionHeaders, ""); //no default section header data
		store.setDefault(PreferenceConstants.ASRearr_UseSectionHeaders, false);
		store.setDefault(PreferenceConstants.ASRearr_RemoveAllExistingHeaders, false);
		store.setDefault(PreferenceConstants.ASRearr_UseSectionHeadersInMXML, false);
		
		store.setDefault(PreferenceConstants.ASRearr_UseCopyright, false);
		store.setDefault(PreferenceConstants.ASRearr_RemoveExistingCopyrightHeaders, false);
		String[] copyrightLines=new String[2];
		copyrightLines[0]="   Copyright "+Calendar.getInstance().get(Calendar.YEAR);
		copyrightLines[1]="   All rights reserved.";
		store.setDefault(PreferenceConstants.ASRearr_CopyrightHeader, new SectionHeader(SectionHeader.AS_Section_Style_SlashSlash, 80, 2, "-", copyrightLines, 1).save());
		
		
		store.setDefault(PreferenceConstants.MXMLRearr_RearrangeWhileFormatting, false);
		store.setDefault(PreferenceConstants.MXMLRearr_UseRearrangeTagOrdering, false);
		
		StringBuffer mxmlTagList=new StringBuffer();
		List<String> tagList=getMXMLTagList();
		for (String tag : tagList) {
			mxmlTagList.append(tag);
			mxmlTagList.append(",");
		}
		store.setDefault(PreferenceConstants.MXMLRearr_RearrangeTagOrdering, mxmlTagList.toString());
	}

	private List<String> getMXMLTagList() 
	{
		List<String> result=new ArrayList<String>();
		result.add(".*:Binding");
		result.add(".*:Component");
		result.add(".*:Declarations");
		result.add(".*:Definition");
		result.add(".*:DesignLayer");
		result.add(".*:Library");
		result.add(".*:Metadata");
		result.add(".*:Model");
		result.add(".*:Private");
		result.add(".*:Reparent");
		result.add(".*:Script");
		result.add(".*:Style");
		result.add(".*:XML");
		result.add(".*:XMLList");
		result.add(".*:operation");
		result.add(".*:request");
		result.add(".*:method");
		result.add(".*:arguments");
		result.add(".*:states");
		result.add(".*:layout");
		result.add(PreferenceConstants.MXMLUnmatchedTagsConstant);
		return result;
	}

}
