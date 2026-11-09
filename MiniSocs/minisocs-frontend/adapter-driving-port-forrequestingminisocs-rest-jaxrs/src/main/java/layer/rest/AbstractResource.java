package layer.rest;

import minisocs.MiniSocsAPI;

public abstract class AbstractResource {
	private static MiniSocsAPI applicationFacade;
	protected AbstractResource() {
		super();
	}
	public static void setApplicationFacade(final MiniSocsAPI facade) {
		applicationFacade = facade;
	}
	protected static MiniSocsAPI accessApplicationFacade() {
		if (applicationFacade == null) {
			throw new IllegalStateException("the reference to the facade has not been set");
		}
		return applicationFacade;
	}
	
}
