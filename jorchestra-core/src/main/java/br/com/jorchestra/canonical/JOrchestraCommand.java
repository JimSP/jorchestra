package br.com.jorchestra.canonical;

import java.util.Map.Entry;
import java.util.concurrent.Future;

public enum JOrchestraCommand {

	CANCEL_TASK_RUNNING {
		@Override
		public Boolean execute(final Entry<JOrchestraStateCall, Future<Object>> action) {
			return action.getValue().cancel(Boolean.TRUE);
		}
	},
	CANCEL_TASK_NOT_RUNNING {
		@Override
		public Boolean execute(final Entry<JOrchestraStateCall, Future<Object>> action) {
			return action.getValue().cancel(Boolean.FALSE);
		}
	};

	public abstract Boolean execute(final Entry<JOrchestraStateCall, Future<Object>> action);

}
