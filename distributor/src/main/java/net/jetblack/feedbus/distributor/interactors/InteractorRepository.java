package net.jetblack.feedbus.distributor.interactors;

import java.io.Closeable;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

/**
 * A repository of interactors.
 */
public class InteractorRepository implements Closeable {

	private final Map<String, Interactor> _interactors = new HashMap<String, Interactor>();

	/**
	 * Add an interactor.
	 * @param interactor The interactor to add.
	 */
	public void add(Interactor interactor) {
		_interactors.put(interactor.getId(), interactor);
	}

	/**
	 * Remove an interactor.
	 * @param interactor The interactor to remove.
	 * @return
	 */
	public Interactor remove(Interactor interactor) {
		return _interactors.remove(interactor.getId());
	}

	@Override
	public void close() throws IOException {
		for (Interactor interactor : _interactors.values()) {
			interactor.close();
		}
		_interactors.clear();
	}
}
