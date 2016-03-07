package flexasdocgencommand.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import actionscriptinfocollector.TopLevelItemRecord;
import flexasdocgen.Activator;

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
		
		store.setDefault(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Class, TopLevelItemRecord.ASDoc_Public | TopLevelItemRecord.ASDoc_Protected);
		store.setDefault(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Function, TopLevelItemRecord.ASDoc_Public | TopLevelItemRecord.ASDoc_Protected);
		store.setDefault(PreferenceConstants.ASDoc_VisibilityFilter_Root+PreferenceConstants.ASDoc_Property, TopLevelItemRecord.ASDoc_Public | TopLevelItemRecord.ASDoc_Protected);
		
		int mods=TopLevelItemRecord.ASDoc_Dynamic | TopLevelItemRecord.ASDoc_Final | TopLevelItemRecord.ASDoc_Native | TopLevelItemRecord.ASDoc_Static;
		store.setDefault(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Class, mods);
		store.setDefault(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Function, mods);
		store.setDefault(PreferenceConstants.ASDoc_ModifierFilter_Root+PreferenceConstants.ASDoc_Property, mods);
		
		StringBuffer template=new StringBuffer();
		template.append("/**\n");
		template.append(" * \n");
		template.append(" * %param%\n");
		template.append(" * %return%\n");
		template.append(" * %throws%\n");
		template.append(" */\n");
		store.setDefault(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Function, template.toString());
		
		template=new StringBuffer();
		template.append("/**\n");
		template.append(" * \n");
		template.append(" * %author%\n");
		template.append(" */\n");
		store.setDefault(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Class, template.toString());		

		template=new StringBuffer();
		template.append("/**\n");
		template.append(" * \n");
		template.append(" * %default%\n");
		template.append(" */\n");
		store.setDefault(PreferenceConstants.ASDoc_Template_Root+PreferenceConstants.ASDoc_Property, template.toString());
	}

}
