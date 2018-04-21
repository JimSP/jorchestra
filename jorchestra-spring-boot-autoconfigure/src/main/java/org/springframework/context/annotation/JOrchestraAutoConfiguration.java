package org.springframework.context.annotation;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.ItemListenerConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.config.WanConsumerConfig;
import com.hazelcast.config.WanPublisherConfig;
import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.ItemListener;

import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraSignalType;
import br.com.jorchestra.canonical.JOrchestraStateCall;
import br.com.jorchestra.configuration.JOrchestraConfigurationProperties;
import br.com.jorchestra.controller.JOrchestraAdminWebSocket;
import br.com.jorchestra.controller.JOrchestraConversationWebSocketController;
import br.com.jorchestra.controller.JOrchestraDiscoveryWebSocketController;
import br.com.jorchestra.controller.JOrchestraMonitorWebSocket;
import br.com.jorchestra.service.JOrchestraBeans;
import br.com.jorchestra.util.JOrchestraContextUtils;
import br.com.jorchestra.util.JOrchestraDetectUseLocalPort;

@Configuration(value = "jOrchestraAutoConfiguration")
@EnableWebMvc
@EnableWebSocket
public class JOrchestraAutoConfiguration extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	private static final int JORCHESTRA_TIME_TO_LIVE = 255;
	private static final int JORCHESTRA_PORT = 6660;

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestraAutoConfiguration.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JOrchestraConfigurationProperties jOrchestraConfigurationProperties;

	@Value("${server.port:8080}")
	private Integer serverPort;

	@PreDestroy
	public void shutdown() {
		LOGGER.info("m=shutdown, msg=\"bye bye!\"");
		JOrchestraContextUtils.getJOrchestraHazelcastInstance().shutdown();
	}

	@Bean
	public JOrchestraContainerConfiguration JOrchestraContainerConfiguration() {
		return new JOrchestraContainerConfiguration();
	}

	@Bean
	public JOrchestraBeans JOrchestraBeans() {
		return new JOrchestraBeans();
	}

	@Override
	public void registerWebSocketHandlers(final WebSocketHandlerRegistry webSocketHandlerRegistry) {
		LOGGER.info("m=registerWebSocketHandlers");

		JOrchestraContextUtils.setApplicationContext(applicationContext);

		final JOrchestraDiscoveryWebSocketController jOrchestraDiscoveryWebSocketController = new JOrchestraDiscoveryWebSocketController(
				jOrchestraConfigurationProperties);

		webSocketHandlerRegistry.addHandler(jOrchestraDiscoveryWebSocketController, "jOrchestra-discovery") //
				.setAllowedOrigins(jOrchestraConfigurationProperties.getAllowedOrigins());

		final JOrchestraMonitorWebSocket jOrchestraMonitorWebSocket = new JOrchestraMonitorWebSocket(
				jOrchestraConfigurationProperties);

		final JOrchestraAdminWebSocket jOrchestraAdminWebSocket = new JOrchestraAdminWebSocket(
				jOrchestraConfigurationProperties, JOrchestraContextUtils.getExecutorServiceMap());

		final Config config = hazelCastConfig(jOrchestraConfigurationProperties,
				jOrchestraDiscoveryWebSocketController);

		final List<JOrchestraHandle> list = JOrchestraContextUtils.jorchestraHandleConsumer( //
				(jOrchestraHandle) -> {
					final JOrchestraSignalType jOrchestraSignal = jOrchestraHandle.getjOrchestraSignalType();
					registerJOrchestraPath(jOrchestraHandle, config, jOrchestraSignal);
				});

		JOrchestraContextUtils.setJORchestraHazelcastInstance(hazelcastInstance(config));
		final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic = JOrchestraContextUtils
				.getJOrchestraHazelcastInstance().getReliableTopic("jOrchestraStateCallTopic");
		jOrchestraStateCallTopic.addMessageListener(jOrchestraMonitorWebSocket);

		config.addTopicConfig(createConversationTopic());

		final ITopic<String[]> conversationTopic = JOrchestraContextUtils.getJOrchestraHazelcastInstance()
				.getTopic("jOrchestra-conversation");
		final JOrchestraConversationWebSocketController joOrchestraConversationWebSocketController = new JOrchestraConversationWebSocketController(
				null, jOrchestraStateCallTopic, jOrchestraConfigurationProperties, conversationTopic);

		conversationTopic.addMessageListener(joOrchestraConversationWebSocketController);

		final ISet<JOrchestraHandle> jOrchestraPathRegisterSet = JOrchestraContextUtils.getJOrchestraHazelcastInstance()
				.getSet("jOrchestraPathRegisterSet");

		list.
		parallelStream()
		.forEach(jOrchestraHandle -> {
			try {
				createEndPoints(webSocketHandlerRegistry, jOrchestraStateCallTopic, jOrchestraHandle);
				jOrchestraPathRegisterSet.add(jOrchestraHandle);
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createEndPoints(final WebSocketHandlerRegistry webSocketHandlerRegistry,
			final ITopic<JOrchestraStateCall> jOrchestraStateCallTopic, final JOrchestraHandle jOrchestraHandle)
			throws UnknownHostException {

		final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
		final JOrchestraSignalType jOrchestraSignal = jOrchestraHandle.getjOrchestraSignalType();
		final Boolean reliable = jOrchestraHandle.isReliable();
		final Class<?> messageType = jOrchestraSignal.getMessageType();
		final Class classType = jOrchestraSignal.getClassType();

		final Object iService = jOrchestraSignal.createService(jorchestraPath, reliable,
				JOrchestraContextUtils.getJOrchestraHazelcastInstance(), messageType, classType);

		jOrchestraSignal.register(jOrchestraStateCallTopic, jorchestraPath, jOrchestraHandle, webSocketHandlerRegistry,
				jOrchestraConfigurationProperties, iService);
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
			final JOrchestraSignalType jOrchestraSignalType) {
		final String jorchestraPath = jOrchestraHandle.getJOrchestraPath();
		LOGGER.info("m=registerJOrchestraPath, jorchestraPath=" + jorchestraPath);
		jOrchestraSignalType.addConfig(jorchestraPath, config);
	}

	private static Config hazelCastConfig(final JOrchestraConfigurationProperties jOrchestraConfigurationProperties,
			final ItemListener<JOrchestraHandle> itemListener) {
		final Config config = new Config(jOrchestraConfigurationProperties.getName());
		config.setGroupConfig(hazelCastGroupConfig(jOrchestraConfigurationProperties.getUsername(),
				jOrchestraConfigurationProperties.getPassword()));
		config.setNetworkConfig(hazelCastNetworkConfig(jOrchestraConfigurationProperties.getEnableTcpLink(),
				jOrchestraConfigurationProperties.getTcpClusterMembers()));
		config.addTopicConfig(new TopicConfig("jOrchestraStateCallTopic"));
		config.addWanReplicationConfig(wanReplicationConfig());
		config.setManagementCenterConfig(
				managementCenterConfig(jOrchestraConfigurationProperties.getManagementCenterConfigEnable(),
						jOrchestraConfigurationProperties.getManagementCenterConfigUpdateInterval(),
						jOrchestraConfigurationProperties.getManagementCenterConfigUrl()));
		config.addSetConfig(setConfig(itemListener));

		return config;
	}

	private static SetConfig setConfig(final ItemListener<JOrchestraHandle> itemListener) {
		final SetConfig setConfig = new SetConfig("jOrchestraPathRegisterSet");
		setConfig.addItemListenerConfig(itemListenerConfig(itemListener));
		return setConfig;
	}

	private static ItemListenerConfig itemListenerConfig(final ItemListener<JOrchestraHandle> itemListener) {
		return new ItemListenerConfig(itemListener, Boolean.TRUE);
	}

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

	private static GroupConfig hazelCastGroupConfig(final String username, final String password) {
		final GroupConfig groupConfig = new GroupConfig(username, password);
		return groupConfig;
	}

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

	private static ManagementCenterConfig managementCenterConfig(final Boolean managementCenterConfigEnable,
			final Integer managementCenterConfigUpdateInterval, final String managementCenterConfigUrl) {
		final ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
		managementCenterConfig.setEnabled(managementCenterConfigEnable);
		managementCenterConfig.setUpdateInterval(managementCenterConfigUpdateInterval);
		managementCenterConfig.setUrl(managementCenterConfigUrl);
		return managementCenterConfig;
	}
}
