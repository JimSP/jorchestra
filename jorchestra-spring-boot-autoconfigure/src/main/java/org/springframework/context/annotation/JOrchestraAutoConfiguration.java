package org.springframework.context.annotation;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraSignal;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraAdminWebSocket;
import br.com.jorchestra.controller.JOrchestraConversationWebSocketController;
import br.com.jorchestra.controller.JOrchestraMonitorWebSocket;
import br.com.jorchestra.service.JOrchestraBeans;
import br.com.jorchestra.util.JOrchestraContextUtils;
import br.com.jorchestra.util.JOrchestraDetectUseLocalPort;

@Configuration(value = "jOrchestraAutoConfiguration")
@EnableWebMvc
@EnableWebSocket
@EnableConfigurationProperties(JOrchestraConfigurationProperties.class)
public class JOrchestraAutoConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	private static final int JORCHESTRA_TIME_TO_LIVE = 255;
	private static final int JORCHESTRA_PORT = 6660;

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraAutoConfiguration.class);

	private static HazelcastInstance HAZELCAST_INSTANCE;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	@PreDestroy
	public void shutdown() {
		LOGGER.info("m=shutdown, msg=\"bye bye!\"");
		HAZELCAST_INSTANCE.shutdown();
	}

	@Value("${server.port:8080}")
	private Integer serverPort;

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return (container -> {
			final int targetPort = JOrchestraDetectUseLocalPort.incrementPortIfIsInUser(serverPort);
			container.setPort(targetPort);
		});
	}

	@Bean
	public JOrchestraBeans JOrchestraBeans() {
		return new JOrchestraBeans();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry webSocketHandlerRegistry) {
		LOGGER.info("m=registerWebSocketHandlers");

		JOrchestraContextUtils.setApplicationContext(applicationContext);

		final JOrchestraMonitorWebSocket jOrchestraMonitorWebSocket = new JOrchestraMonitorWebSocket();
		final JOrchestraAdminWebSocket jOrchestraAdminWebSocket = new JOrchestraAdminWebSocket(
				jOrchestraConfigurationProperties, JOrchestraContextUtils.getExecutorServiceMap());

		final Config config = hazelCastConfig(jOrchestraConfigurationProperties);

		final List<JOrchestraHandle> list = JOrchestraContextUtils.jorchestraHandleConsumer( //
				(jOrchestraHandle) -> {
					final JOrchestraSignal jOrchestraSignal = jOrchestraHandle.getjOrchestraSignal();
					registerJOrchestraPath(jOrchestraHandle, config, jOrchestraSignal);
				});

		HAZELCAST_INSTANCE = hazelcastInstance(config);
		final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic = HAZELCAST_INSTANCE
				.getReliableTopic("jOrchestraStateCallTopic");
		jOrchestraStateCallTopic.addMessageListener(jOrchestraMonitorWebSocket);

		config.addTopicConfig(createConversationTopic());

		final ITopic<String[]> conversationTopic = HAZELCAST_INSTANCE.getTopic("jOrchestra-conversation");
		final JOrchestraConversationWebSocketController joOrchestraConversationWebSocketController = new JOrchestraConversationWebSocketController(
				null, jOrchestraStateCallTopic, jOrchestraConfigurationProperties, conversationTopic);

		conversationTopic.addMessageListener(joOrchestraConversationWebSocketController);

		list.forEach(jOrchestraHandle -> {
			final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
			final JOrchestraSignal jOrchestraSignal = jOrchestraHandle.getjOrchestraSignal();
			final Boolean reliable = jOrchestraHandle.isReliable();
			final Class<?> messageType = jOrchestraSignal.getMessageType();
			final Class classType = jOrchestraSignal.getClassType();

			final Object iService = jOrchestraSignal.createService(jorchestraPath, reliable, HAZELCAST_INSTANCE,
					messageType, classType);

			jOrchestraSignal.register(jOrchestraStateCallTopic, jorchestraPath, jOrchestraHandle,
					webSocketHandlerRegistry, jOrchestraConfigurationProperties, iService);
		});

		webSocketHandlerRegistry //
				.addHandler(jOrchestraMonitorWebSocket, "jOrchestra-monitor") //
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());

		webSocketHandlerRegistry //
				.addHandler(jOrchestraAdminWebSocket, "jOrchestra-admin")
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());

		webSocketHandlerRegistry //
				.addHandler(joOrchestraConversationWebSocketController, "jOrchestra-conversation") //
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());
	}

	@Override
	public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
		LOGGER.info("m=configureDefaultServletHandling");
		configurer.enable();
	}

	private TopicConfig createConversationTopic() {
		final TopicConfig topicConfig = new TopicConfig("jOrchestra-conversation");
		topicConfig.setGlobalOrderingEnabled(true);
		return topicConfig;
	}

	private void registerJOrchestraPath(final JOrchestraHandle jOrchestraHandle, final Config config,
			final JOrchestraSignal jOrchestraSignal) {
		final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
		LOGGER.info("m=registerJOrchestraPath, jorchestraPath=" + jorchestraPath);
		jOrchestraSignal.addConfig(jorchestraPath, config);
	}

	private static Config hazelCastConfig(final JOrchestraConfigurationProperties jOrchestraConfigurationProperties) {
		final Config config = new Config(jOrchestraConfigurationProperties.getName());
		//config.setGroupConfig(hazelCastGroupConfig(jOrchestraConfigurationProperties.getUsername(),
		//		jOrchestraConfigurationProperties.getPassword()));
		//config.setNetworkConfig(hazelCastNetworkConfig(jOrchestraConfigurationProperties.getEnableTcpLink(),
		//		jOrchestraConfigurationProperties.getTcpClusterMembers()));
		config.addTopicConfig(new TopicConfig("jOrchestraStateCallTopic"));
		// config.addWanReplicationConfig(wanReplicationConfig());
		//config.setManagementCenterConfig(
		//		managementCenterConfig(jOrchestraConfigurationProperties.getManagementCenterConfigEnable(),
		//				jOrchestraConfigurationProperties.getManagementCenterConfigUpdateInterval(),
		//				jOrchestraConfigurationProperties.getManagementCenterConfigUrl()));
		
		return config;
	}

	@SuppressWarnings("unused")
	private static WanReplicationConfig wanReplicationConfig() {
		final WanReplicationConfig wanReplicationConfig = new WanReplicationConfig();
		wanReplicationConfig.setName("JOrchestraWanReplication");
		wanReplicationConfig.setWanConsumerConfig(wanConsumerConfig());
		wanReplicationConfig.setWanPublisherConfigs(wanPublisherConfigs());
		return wanReplicationConfig;
	}

	private static WanConsumerConfig wanConsumerConfig() {
		final WanConsumerConfig wanConsumerConfig = new WanConsumerConfig();
		return wanConsumerConfig;
	}

	private static List<WanPublisherConfig> wanPublisherConfigs() {
		final WanPublisherConfig wanPublisherConfig = new WanPublisherConfig();
		return Arrays.asList(wanPublisherConfig);
	}

	private static HazelcastInstance hazelcastInstance(final Config config) {
		return Hazelcast.getOrCreateHazelcastInstance(config);
	}

	@SuppressWarnings("unused")
	private static GroupConfig hazelCastGroupConfig(final String username, final String password) {
		final GroupConfig groupConfig = new GroupConfig(username, password);
		return groupConfig;
	}

	@SuppressWarnings("unused")
	private static NetworkConfig hazelCastNetworkConfig(final Boolean enabledTcpLink, final List<String> members) {
		final NetworkConfig networkConfig = new NetworkConfig();
		int targetPort = JOrchestraDetectUseLocalPort.incrementPortIfIsInUser(JORCHESTRA_PORT);
		networkConfig.setPort(targetPort);
		networkConfig.setJoin(hazelCastJoin(enabledTcpLink, members));
		return networkConfig;
	}

	private static JoinConfig hazelCastJoin(final Boolean enabledTcpLink, final List<String> members) {
		final JoinConfig joinConfig = new JoinConfig();
		joinConfig.setMulticastConfig(hazelCastMulticastConfig());
		joinConfig.setTcpIpConfig(hazelCastTcpIpConfig(enabledTcpLink, members));
		return joinConfig;
	}

	private static MulticastConfig hazelCastMulticastConfig() {
		final MulticastConfig multicastConfig = new MulticastConfig();
		multicastConfig.setEnabled(Boolean.TRUE);
		multicastConfig.setLoopbackModeEnabled(Boolean.TRUE);
		multicastConfig.setMulticastGroup(MulticastConfig.DEFAULT_MULTICAST_GROUP);
		multicastConfig.setMulticastPort(MulticastConfig.DEFAULT_MULTICAST_PORT);
		multicastConfig.setMulticastTimeToLive(JORCHESTRA_TIME_TO_LIVE);
		return multicastConfig;
	}

	private static TcpIpConfig hazelCastTcpIpConfig(final Boolean enabled, final List<String> members) {
		final TcpIpConfig tcpIpConfig = new TcpIpConfig();
		tcpIpConfig.setEnabled(enabled);
		tcpIpConfig.setMembers(members);
		return tcpIpConfig;
	}

	@SuppressWarnings("unused")
	private static ManagementCenterConfig managementCenterConfig(final Boolean managementCenterConfigEnable,
			final Integer managementCenterConfigUpdateInterval, final String managementCenterConfigUrl) {
		final ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
		managementCenterConfig.setEnabled(managementCenterConfigEnable);
		managementCenterConfig.setUpdateInterval(managementCenterConfigUpdateInterval);
		managementCenterConfig.setUrl(managementCenterConfigUrl);
		return managementCenterConfig;
	}
}
