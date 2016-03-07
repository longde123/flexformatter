package flexasrearrangecodecommand.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import flexasrearrangecodecommand.Activator;

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
		
		String order=PreferenceConstants.NamespaceGeneric+",override,public,private,protected,internal,static,dynamic,final";
		store.setDefault(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Class, order);
		store.setDefault(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function, order);
		store.setDefault(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Property, order);

		store.setDefault(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Class, true);
		store.setDefault(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function, true);
		store.setDefault(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Property, true);
		
		store.setDefault(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements, true);
	}

}
