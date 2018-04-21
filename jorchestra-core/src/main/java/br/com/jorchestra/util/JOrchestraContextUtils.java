package br.com.jorchestra.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.util.function.Consumer;

import br.com.jorchestra.annotation.JOrchestra;
import br.com.jorchestra.annotation.JOrchestraFailover;
import br.com.jorchestra.canonical.JOrchestraHandle;
import br.com.jorchestra.canonical.JOrchestraSignalType;
import br.com.jorchestra.canonical.JOrchestraStateCall;

public class JOrchestraContextUtils {

	private static String LOCAL_ADDRESS;
	private static Integer LOCAL_PORT;

	private static ApplicationContext APPLICATION_CONTEXT;
	private static HazelcastInstance HAZELCAST_INSTANCE;

	private static Map<String, Map<JOrchestraStateCall, Future<Object>>> EXECUTOR_SERVICE_MAP = Collections
			.synchronizedMap(new HashMap<>());

	public static HazelcastInstance getJOrchestraHazelcastInstance() {
		return HAZELCAST_INSTANCE;
	}

	public static void setJORchestraHazelcastInstance(final HazelcastInstance jORchestraHazelcastInstance) {
		JOrchestraContextUtils.HAZELCAST_INSTANCE = jORchestraHazelcastInstance;
	}

	public static void registerAddressAndPort(final String address, final Integer port) {
		LOCAL_ADDRESS = address;
		LOCAL_PORT = port;
	}

	public static Map.Entry<String, Integer> getAdressAndPort() {
		return new Entry<String, Integer>() {

			@Override
			public synchronized Integer setValue(final Integer port) {
				final Integer aux = LOCAL_PORT;
				LOCAL_PORT = port;
				return aux;
			}

			@Override
			public synchronized Integer getValue() {
				return LOCAL_PORT;
			}

			@Override
			public synchronized String getKey() {
				return LOCAL_ADDRESS;
			}
		};
	}

	public static Map<String, Map<JOrchestraStateCall, Future<Object>>> getExecutorServiceMap() {
		return EXECUTOR_SERVICE_MAP;
	}

	public static Object getJorchestraBean(final String jOrchestraBeanName) {
		return APPLICATION_CONTEXT.getBean(jOrchestraBeanName);
	}

	public static void setApplicationContext(final ApplicationContext applicationContext) {
		APPLICATION_CONTEXT = applicationContext;
	}

	public static List<JOrchestraHandle> jorchestraHandleConsumer(final Consumer<JOrchestraHandle> consumer) {
		return jorchestraHandleConsumer(APPLICATION_CONTEXT, consumer);
	}

	public static Map<String, Object> loadJOrchestraBeans() {
		return loadJOrchestraBeans(APPLICATION_CONTEXT);
	}

	public static Method getMethosByJOrchestraPath(final Object jOrchestraBean, final String methodName,
			final Class<?>[] parametersType) throws NoSuchMethodException, SecurityException {
		return jOrchestraBean.getClass().getMethod(methodName, parametersType);
	}

	public static List<JOrchestraHandle> jorchestraHandleConsumer(final ApplicationContext applicationContext,
			final Consumer<JOrchestraHandle> consumer) {

		final List<JOrchestraHandle> list = new ArrayList<>();

		JOrchestraContextUtils.loadJOrchestraBeans(applicationContext) //
				.entrySet() //
				.parallelStream() //
				.forEach(entry -> {
					mapToJListOrchestraHandle(entry) //
							.parallelStream() //
							.forEach(jOrchestraHandle -> { //
								consumer.accept(jOrchestraHandle);
								list.add(jOrchestraHandle);
							});
				});

		return list;
	}

	public static List<JOrchestraHandle> mapToJListOrchestraHandle(final ApplicationContext applicationContext,
			final Entry<String, Object> entry) {

		final Entry<String, Integer> entryAdressPort = JOrchestraContextUtils.getAdressAndPort();

		final List<String> failOverMethodNames = new ArrayList<>();

		final List<JOrchestraHandle> handles = Arrays.asList(entry.getValue().getClass().getDeclaredMethods()) //
				.parallelStream() //
				.filter(method -> method.getModifiers() == Modifier.PUBLIC) //
				.map(method -> {

					final String jOrchestraBeanName = entry.getKey();
					final Object jOrchestraBean = JOrchestraContextUtils.getJorchestraBean(jOrchestraBeanName);

					final JOrchestra jOrchestra = jOrchestraBean.getClass().getDeclaredAnnotation(JOrchestra.class);
					final String path = jOrchestra.path();
					final JOrchestraSignalType jOrchestraSignal = jOrchestra.jOrchestraSignalType();
					final Boolean reliable = jOrchestra.reliable();
					final String methodName = method.getName();
					final Class<?>[] jorchestraParametersType = method.getParameterTypes();
					final JOrchestraFailover jOrchestraFailover = method
							.getDeclaredAnnotation(JOrchestraFailover.class);

					if (jOrchestraFailover != null && jOrchestraFailover.failOverMethodName() != null) {
						final String failOverMethodName = jOrchestraFailover.failOverMethodName();

						failOverMethodNames.add(failOverMethodName);

						return new JOrchestraHandle(jOrchestraBeanName, methodName, jorchestraParametersType, path,
								jOrchestraSignal, reliable, failOverMethodName, entryAdressPort.getKey(),
								entryAdressPort.getValue());
					} else {
						return new JOrchestraHandle(jOrchestraBeanName, methodName, jorchestraParametersType, path,
								jOrchestraSignal, reliable, null, entryAdressPort.getKey(), entryAdressPort.getValue());
					}

				}).collect(Collectors.toList());

		return handles //
				.parallelStream() //
				.filter(jOrchestraHandle -> !failOverMethodNames //
						.contains(jOrchestraHandle.getMethodName())) //
				.collect(Collectors.toList());
	}

	public static List<JOrchestraHandle> mapToJListOrchestraHandle(final Entry<String, Object> entry) {
		return mapToJListOrchestraHandle(APPLICATION_CONTEXT, entry);
	}

	public static Map<String, Object> loadJOrchestraBeans(final ApplicationContext applicationContext) {

		final Map<String, Object> map = new HashMap<>();

		getJorchestraBeanNames(applicationContext) //
				.parallelStream() //
				.forEach(jorcherstraBeanName -> {
					final Object object = applicationContext.getBean(jorcherstraBeanName);
					map.put(jorcherstraBeanName, object);
				});

		return map;
	}

	private static List<String> getJorchestraBeanNames(final ApplicationContext applicationContext) {
		return Arrays.asList(applicationContext.getBeanNamesForAnnotation(JOrchestra.class));
	}
}
