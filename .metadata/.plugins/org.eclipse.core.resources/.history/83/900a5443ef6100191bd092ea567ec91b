package net.jetblack.feedbus.distributor.interactors;

import java.io.Closeable;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

public class InteractorRepository implements Closeable {

	private final Map<String, Interactor> _interactors = new HashMap<String, Interactor>();

	public InteractorRepository() {
	}

	public void add(Interactor interactor) {
		_interactors.put(interactor.Id, interactor);
	}

	public Interactor remove(Interactor interactor) {
		return _interactors.remove(interactor.Id);
	}

	@Override
	public void close() throws IOException {
		for (Interactor interactor : _interactors.values()) {
			interactor.close();
		}
		_interactors.clear();
	}
}
