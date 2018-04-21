package br.com.jorchestra.canonical;

import java.io.Serializable;
import java.util.UUID;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.dto.JOrchestraAdminRequest;

public class JOrchestraStateCall implements Serializable {

	private static final long serialVersionUID = -1154684408146759207L;

	public static JOrchestraStateCall createJOrchestraStateCall_WAITING(final String clusterName,
			final String jOcrhestrName, final String jOrchestraPah, final String sessionId, final String payload) {

		final UUID requestId = UUID.randomUUID();
		final Long beginTimestamp = null;
		final JOrchestraState jOrchestraState = JOrchestraState.DATA_WAITING;
		final Long endTimestamp = null;

		return new JOrchestraStateCall( //
				clusterName, //
				jOcrhestrName, //
				jOrchestraPah, //
				sessionId, //
				requestId, //
				beginTimestamp, //
				endTimestamp, //
				jOrchestraState, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_PROCESSING(
			final JOrchestraStateCall jOrchestraStateCall_Waiting, final String payload) {
		final Long beginTimestamp = System.currentTimeMillis();

		return new JOrchestraStateCall( //
				jOrchestraStateCall_Waiting.getClusterName(), //
				jOrchestraStateCall_Waiting.getjOcrhestrName(), //
				jOrchestraStateCall_Waiting.getjOrchestraPath(), //
				jOrchestraStateCall_Waiting.getSessionId(), //
				jOrchestraStateCall_Waiting.getRequestId(), //
				beginTimestamp, //
				null, //
				JOrchestraState.DATA_PROCESSING, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_CANCELED(
			final JOrchestraStateCall jOrchestraStateCall_Processing, final String payload) {

		return new JOrchestraStateCall( //
				jOrchestraStateCall_Processing.getClusterName(), //
				jOrchestraStateCall_Processing.getjOcrhestrName(), //
				jOrchestraStateCall_Processing.getjOrchestraPath(), //
				jOrchestraStateCall_Processing.getSessionId(), //
				jOrchestraStateCall_Processing.getRequestId(), //
				jOrchestraStateCall_Processing.getBeginTimestamp(), System.currentTimeMillis(), //
				JOrchestraState.DATA_CANCELED, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_SUCCESS(
			final JOrchestraStateCall jOrchestraStateCall_Processing, final String payload) {

		return new JOrchestraStateCall( //
				jOrchestraStateCall_Processing.getClusterName(), //
				jOrchestraStateCall_Processing.getjOcrhestrName(), //
				jOrchestraStateCall_Processing.getjOrchestraPath(), //
				jOrchestraStateCall_Processing.getSessionId(), //
				jOrchestraStateCall_Processing.getRequestId(), //
				jOrchestraStateCall_Processing.getBeginTimestamp(), //
				System.currentTimeMillis(), //
				JOrchestraState.DATA_SUCCESS, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_ERROR(final String clusterName,
			final String jOcrhestrName, final String jOrchestraPath, final String sessionId) {
		final UUID requestId = UUID.randomUUID();
		final Long beginTimestamp = null;
		final JOrchestraState jOrchestraState = JOrchestraState.DATA_ERROR;
		final Long endTimestamp = System.currentTimeMillis();
		final String payload = null;

		return new JOrchestraStateCall( //
				clusterName, //
				jOcrhestrName, //
				jOrchestraPath, //
				sessionId, //
				requestId, //
				beginTimestamp, //
				endTimestamp, //
				jOrchestraState, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_ERROR(final String clusterName,
			final String jOcrhestrName, final String jOrchestraPath, final String sessionId, final Long beginTimestamp,
			final Long endTimestamp, final String payload) {

		return new JOrchestraStateCall( //
				clusterName, //
				jOcrhestrName, //
				jOrchestraPath, //
				sessionId, //
				UUID.randomUUID(), //
				beginTimestamp, //
				endTimestamp, //
				JOrchestraState.DATA_ERROR, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_ERROR(
			final JOrchestraStateCall jOrchestraStateCall_Processing, final String payload) {

		return new JOrchestraStateCall( //
				jOrchestraStateCall_Processing.getClusterName(), //
				jOrchestraStateCall_Processing.getjOcrhestrName(), //
				jOrchestraStateCall_Processing.getjOrchestraPath(), //
				jOrchestraStateCall_Processing.getSessionId(), //
				jOrchestraStateCall_Processing.getRequestId(), //
				jOrchestraStateCall_Processing.getBeginTimestamp(), //
				System.currentTimeMillis(), //
				JOrchestraState.DATA_ERROR, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_OPEN(final String clusterName,
			final String jOcrhestrName, final String jOrchestraPath, final String sessionId) {
		final UUID requestId = UUID.randomUUID();
		final Long beginTimestamp = System.currentTimeMillis();
		final JOrchestraState jOrchestraState = JOrchestraState.SESSION_OPEN;
		final Long endTimestamp = null;
		final String payload = null;
		return new JOrchestraStateCall( //
				clusterName, //
				jOcrhestrName, //
				jOrchestraPath, //
				sessionId, //
				requestId, //
				beginTimestamp, //
				endTimestamp, //
				jOrchestraState, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_CLOSE(final String clusterName,
			final String jOcrhestrName, final String jOrchestraPath, final String sessionId) {
		final UUID requestId = UUID.randomUUID();
		final Long beginTimestamp = null;
		final JOrchestraState jOrchestraState = JOrchestraState.SESSION_CLOSE;
		final Long endTimestamp = System.currentTimeMillis();
		final String payload = null;

		return new JOrchestraStateCall( //
				clusterName, //
				jOcrhestrName, //
				jOrchestraPath, //
				sessionId, //
				requestId, //
				beginTimestamp, //
				endTimestamp, //
				jOrchestraState, //
				payload);
	}

	public static JOrchestraStateCall createJOrchestraStateCall_PROCESSING_FAILOVER(
			final JOrchestraStateCall jOrchestraStateCall_Processing, final String payload) {

		return new JOrchestraStateCall( //
				jOrchestraStateCall_Processing.getClusterName(), //
				jOrchestraStateCall_Processing.getjOcrhestrName(), //
				jOrchestraStateCall_Processing.getjOrchestraPath(), //
				jOrchestraStateCall_Processing.getSessionId(), //
				jOrchestraStateCall_Processing.getRequestId(), //
				System.currentTimeMillis(), //
				null, //
				JOrchestraState.PROCESSING_FAILOVER, //
				payload);
	}

	public static JOrchestraStateCall createSearchTemplate(
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final JOrchestraAdminRequest jOrchestraAdminRequest, final String jOrchestraPah) {

		return new JOrchestraStateCall( //
				jOrchestraConfigurationProperties.getClusterName(), //
				jOrchestraConfigurationProperties.getName(), //
				jOrchestraPah, //
				jOrchestraAdminRequest.getSessionId(), //
				UUID.fromString(jOrchestraAdminRequest.getRequestId()), //
				null, //
				null, //
				null, //
				null);
	}

	private final String id;
	private final String clusterName;
	private final String jOcrhestrName;
	private final String jOrchestraPath;
	private final String sessionId;
	private final UUID requestId;
	private final Long beginTimestamp;
	private final Long endTimestamp;
	private final JOrchestraState jOrchestraState;
	private final String payload;

	public JOrchestraStateCall(final String clusterName, final String jOcrhestrName, final String jOrchestraPath,
			final String sessionId, final UUID requestId, final Long beginTimestamp, final Long endTimestamp,
			final JOrchestraState jOrchestraState, final String payload) {
		super();
		this.id = clusterName + "#" + jOcrhestrName + "#" + sessionId + "#" + jOrchestraPath + "#" + requestId;
		this.clusterName = clusterName;
		this.jOcrhestrName = jOcrhestrName;
		this.jOrchestraPath = jOrchestraPath;
		this.sessionId = sessionId;
		this.requestId = requestId;
		this.beginTimestamp = beginTimestamp;
		this.endTimestamp = endTimestamp;
		this.jOrchestraState = jOrchestraState;
		this.payload = payload;
	}

	public String getId() {
		return id;
	}

	public String getClusterName() {
		return clusterName;
	}

	public String getjOcrhestrName() {
		return jOcrhestrName;
	}

	public String getjOrchestraPath() {
		return jOrchestraPath;
	}

	public String getSessionId() {
		return sessionId;
	}

	public UUID getRequestId() {
		return requestId;
	}

	public Long getBeginTimestamp() {
		return beginTimestamp;
	}

	public Long getEndTimestamp() {
		return endTimestamp;
	}

	public JOrchestraState getjOrchestraState() {
		return jOrchestraState;
	}

	public String getPayload() {
		return payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JOrchestraStateCall other = (JOrchestraStateCall) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JOrchestraStateCall [id=" + id + ", clusterName=" + clusterName + ", jOcrhestrName=" + jOcrhestrName
				+ ", sessionId=" + sessionId + ", requestId=" + requestId + ", beginTimestamp=" + beginTimestamp
				+ ", endTimestamp=" + endTimestamp + ", jOrchestraState=" + jOrchestraState + ", payload=" + payload
				+ "]";
	}
}
