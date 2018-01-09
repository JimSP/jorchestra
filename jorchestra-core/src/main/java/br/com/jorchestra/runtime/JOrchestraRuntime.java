package br.com.jorchestra.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

public class JOrchestraRuntime {

	public void execute(final Map<String, String> data, final RuntimeCallback runtimeCallback,
			final WebSocketSession webSocketSession) {
		final Runtime runtime = Runtime.getRuntime();
		try {
			final Process process = runtime.exec(data.get("shelCommand"));
			final InputStream inputStream = process.getInputStream();
			final InputStream errorStream = process.getErrorStream();

			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			final BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(errorStream));

			collectAndSend(runtimeCallback, bufferedReader, "success", webSocketSession);
			collectAndSend(runtimeCallback, bufferedReaderError, "error", webSocketSession);

		} catch (IOException e) {
			runtimeCallback.sendMessage(webSocketSession, "execution error.", e);
		}
	}

	private static void collectAndSend(final RuntimeCallback runtimeCallback, final BufferedReader bufferedReader,
			final String tag, final WebSocketSession webSocketSession) throws IOException {
		final List<String> lines = new ArrayList<>();
		while (bufferedReader.ready()) {
			lines.add(bufferedReader.readLine());
		}

		if (!lines.isEmpty()) {
			runtimeCallback.sendMessage(webSocketSession, tag, lines);
		}
	}

}
