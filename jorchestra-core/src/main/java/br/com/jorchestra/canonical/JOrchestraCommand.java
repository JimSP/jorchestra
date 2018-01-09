package br.com.jorchestra.canonical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.web.socket.WebSocketSession;

import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraAdminWebSocket;
import br.com.jorchestra.dto.JOrchestraAdminRequest;
import br.com.jorchestra.runtime.JOrchestraRuntime;

public enum JOrchestraCommand {

	CANCEL_TASK_RUNNING {
		@Override
		public void execute(final JOrchestraAdminWebSocket jOrchestraAdminWebSocket,
				final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
				final JOrchestraAdminRequest jOrchestraAdminRequest, final WebSocketSession webSocketSession,
				final JOrchestraRuntime jOrchestraRuntime) {

			final JOrchestraStateCall jOrchestraStateCallSearchTemplate = JOrchestraStateCall
					.createSearchTemplate(jOrchestraConfigurationProperties, jOrchestraAdminRequest);

			JOrchestraCommand.execute(jOrchestraAdminWebSocket, executorServiceMap, jOrchestraAdminRequest,
					webSocketSession, jOrchestraStateCallSearchTemplate, Boolean.TRUE);
		}
	},
	CANCEL_TASK_NOT_RUNNING {
		@Override
		public void execute(final JOrchestraAdminWebSocket jOrchestraAdminWebSocket,
				final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
				final JOrchestraAdminRequest jOrchestraAdminRequest, final WebSocketSession webSocketSession,
				final JOrchestraRuntime jOrchestraRuntime) {

			final JOrchestraStateCall jOrchestraStateCallSearchTemplate = JOrchestraStateCall
					.createSearchTemplate(jOrchestraConfigurationProperties, jOrchestraAdminRequest);

			JOrchestraCommand.execute(jOrchestraAdminWebSocket, executorServiceMap, jOrchestraAdminRequest,
					webSocketSession, jOrchestraStateCallSearchTemplate, Boolean.FALSE);
		}
	},
	SHEL {
		@Override
		public void execute(final JOrchestraAdminWebSocket jOrchestraAdminWebSocket,
				final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap,
				final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
				final JOrchestraAdminRequest jOrchestraAdminRequest, final WebSocketSession webSocketSession,
				final JOrchestraRuntime jOrchestraRuntime) {
			jOrchestraRuntime.execute(jOrchestraAdminRequest.getExtraData(), jOrchestraAdminWebSocket,
					webSocketSession);

			final Runtime runtime = Runtime.getRuntime();
			try {
				final Process process = runtime.exec(jOrchestraAdminRequest.getExtraData().get(SHEL_COMMAND));
				final InputStream inputStream = process.getInputStream();
				final InputStream errorStream = process.getErrorStream();

				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				final BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(errorStream));

				if (process.waitFor() == 0) {
					final List<String> lines = new ArrayList<>();
					while (bufferedReader.ready()) {
						lines.add(bufferedReader.readLine());
					}

					if (!lines.isEmpty()) {
						jOrchestraAdminWebSocket.sendMessage(webSocketSession, COMMAND_EXECUTED_SUCCESSFULLY, lines);
					} else {
						jOrchestraAdminWebSocket.sendMessage(webSocketSession, COMMAND_EXECUTED_SUCCESSFULLY);
					}
				} else {
					final List<String> linesError = new ArrayList<>();
					while (bufferedReaderError.ready()) {
						linesError.add(bufferedReaderError.readLine());
					}

					if (!linesError.isEmpty()) {
						jOrchestraAdminWebSocket.sendMessage(webSocketSession, COMMAND_EXECUTED_SUCCESSFULLY,
								linesError);
					} else {
						jOrchestraAdminWebSocket.sendMessage(webSocketSession, COMMAND_EXECUTED_SUCCESSFULLY);
					}
				}
			} catch (IOException | InterruptedException e) {
				jOrchestraAdminWebSocket.sendMessage(webSocketSession, EXECUTION_ERROR, e);
			}
		}
	};

	private static final String EXECUTION_ERROR = "execution error.";
	private static final String SHEL_COMMAND = "shelCommand";
	private static final String COMMAND_EXECUTED_SUCCESSFULLY = "command executed successfully.";

	public abstract void execute(final JOrchestraAdminWebSocket jOrchestraAdminWebSocket,
			final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap,
			final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final JOrchestraAdminRequest jOrchestraAdminRequest, final WebSocketSession webSocketSession,
			final JOrchestraRuntime jOrchestraRuntime);

	private static void execute(final JOrchestraAdminWebSocket jOrchestraAdminWebSocket,
			final Map<String, Map<JOrchestraStateCall, Future<Object>>> executorServiceMap,
			final JOrchestraAdminRequest jOrchestraAdminRequest, final WebSocketSession webSocketSession,
			final JOrchestraStateCall jOrchestraStateCallSearchTemplate, final Boolean mayInterruptIfRunning) {
		final Map<JOrchestraStateCall, Future<Object>> map = executorServiceMap
				.get(jOrchestraAdminRequest.getSessionId());

		map.entrySet().parallelStream().filter(predicate -> {
			return predicate.getKey().equals(jOrchestraStateCallSearchTemplate);
		}).forEach(action -> {
			jOrchestraAdminWebSocket.sendMessage(webSocketSession, COMMAND_EXECUTED_SUCCESSFULLY,
					action.getValue().cancel(mayInterruptIfRunning));
		});
	}
}
