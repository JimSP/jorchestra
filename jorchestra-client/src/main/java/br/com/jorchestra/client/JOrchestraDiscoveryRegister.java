package br.com.jorchestra.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.jorchestra.canonical.JOrchestraHandle;

public final class JOrchestraDiscoveryRegister {

	public static class Singleton {
		private static final JOrchestraDiscoveryRegister jOrchestraRegisterInstance = new JOrchestraDiscoveryRegister();

		private static final Map<String, List<JOrchestraHandle>> jOrchestraHandles = Collections
				.synchronizedMap(new HashMap<>());

		private static final Map<String, AtomicInteger[]> balaceOrder = Collections.synchronizedMap(new HashMap<>());

		protected static JOrchestraDiscoveryRegister getJOrchestraRegisterInstance() {
			return Singleton.jOrchestraRegisterInstance;
		}
	}

	private JOrchestraDiscoveryRegister() {

	}

	protected void add(final JOrchestraHandle jOrchestraHandle) {
		final List<JOrchestraHandle> jOrchestraHandleList = Singleton.jOrchestraHandles
				.get(jOrchestraHandle.getJOrchestraPath());
		if (jOrchestraHandleList == null) {
			final List<JOrchestraHandle> newJOrchestraHandleList = Collections.synchronizedList(new ArrayList<>());
			newJOrchestraHandleList.add(jOrchestraHandle);
			Singleton.jOrchestraHandles.put(jOrchestraHandle.getJOrchestraPath(), newJOrchestraHandleList);
		} else {
			jOrchestraHandleList.add(jOrchestraHandle);
		}
	}

	protected void remove(final JOrchestraHandle jOrchestraHandle) {
		final List<JOrchestraHandle> jOrchestraHandleList = Singleton.jOrchestraHandles
				.get(jOrchestraHandle.getJOrchestraPath());

		if (jOrchestraHandleList != null) {
			jOrchestraHandleList.remove(jOrchestraHandle);
		}
	}

	protected String getPath(final BalanceStrategy balanceStrategy, final String jOrchestraPath) {
		final List<JOrchestraHandle> jOrchestraHandleList = Singleton.jOrchestraHandles.get(jOrchestraPath);
		return balanceJOrchestraPath(balanceStrategy, jOrchestraPath, jOrchestraHandleList);
	}

	private String balanceJOrchestraPath(final BalanceStrategy balanceStrategy, final String jOrchestraPath,
			final List<JOrchestraHandle> jOrchestraMachinePaths) {

		final AtomicInteger[] atomicIntegers = Singleton.balaceOrder.get(jOrchestraPath);

		if (atomicIntegers == null) {
			final AtomicInteger weight = new AtomicInteger(2);
			final AtomicInteger trys = new AtomicInteger(0);
			Singleton.balaceOrder.put(jOrchestraPath, new AtomicInteger[] { weight, trys });
		}

		return jOrchestraMachinePaths //
				.get(balanceStrategy.next(jOrchestraMachinePaths.size(), atomicIntegers)) //
				.getJOrchestraMachineAddress();
	}

}
