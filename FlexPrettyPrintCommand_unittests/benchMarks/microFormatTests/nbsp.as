package no.papirfly.msp.application.interfaces {
	import no.papirfly.msp.application.types.Warning;
	import no.papirfly.msp.application.types.WarningLocation;

	/**
	 * Defines a class that have warning an warning instance and can be registered and parsed by WarningManager
	 * @author tcaspersen
	 *
	 */
	public interface IWarningOwner {
		/**
		 *
		 * @param obj
		 * @return
		 *
		 */
		function getWarningLocation(obj:* = null):WarningLocation;
	}
}