package br.com.jorchestra.client;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public enum BalanceStrategy {
	RANDON {
		@Override
		public Integer next(final Integer size, final AtomicInteger[]... current) {
			final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
			threadLocalRandom.setSeed(System.currentTimeMillis());
			return threadLocalRandom.nextInt(size);
		}
	},
	ROUND_ROBIN {
		@Override
		public Integer next(final Integer size, final AtomicInteger[]... current) {
			final Integer nextValue = current[0][0].incrementAndGet();

			if (nextValue > size) {
				current[0][0].set(0);
				return 0;
			} else {
				return nextValue;
			}
		}
	},
	WEIGHTS {
		@Override
		public Integer next(final Integer size, final AtomicInteger[]... current) {
			for (AtomicInteger[] atomicIntegers : current) {
				final AtomicInteger weight = atomicIntegers[0];
				final AtomicInteger trys = atomicIntegers[1];

				if (weight.get() < trys.get()) {
					return trys.incrementAndGet();
				} else if (weight.get() == trys.get()) {
					trys.set(0);
				}
			}
			return current[0][0].incrementAndGet();
		}
	};

	private BalanceStrategy() {

	}

	public abstract Integer next(final Integer size, final AtomicInteger[]... current);
}
