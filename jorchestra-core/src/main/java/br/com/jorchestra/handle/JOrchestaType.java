package br.com.jorchestra.handle;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

public enum JOrchestaType {

	BOOLEAN(Boolean.class, true), //
	BYTE(Byte.class, 0xFF), //
	CHARACTER(Character.class, '@'), //
	SHORT(Short.class, 32767), //
	INTEGER(Integer.class, 2147483647), //
	LONG(Long.class, 9223372036854775807L), //
	FLOAT(Float.class, 3.4028235E38F), //
	DOUBLE(Double.class, 1.7976931348623157E308), //
	BIG_INTEGER(BigInteger.class, "99999999999999999999999999999999999999999999999999999999"), //
	BIG_DECIMAL(BigDecimal.class, "99999999999999999999999999999999999999999999999999999999.99"), //
	TIME(Time.class, "23:59:59"), //
	DATE(Date.class, new Date(System.currentTimeMillis())), //
	STRING(String.class, "JOrquestra :-)"), //

	@SuppressWarnings("rawtypes")
	ARRAY(List.class, new ArrayList()), //

	ENUM(Enum.class, new Object()), //
	OBJECT(Object.class, new Object());

	private static final Logger LOGGER = LoggerFactory.getLogger(JOrchestaType.class);

	private final Class<?> javaType;
	private final Object defaultValue;

	private JOrchestaType(final Class<?> javaType, final Object defaultValue) {
		this.javaType = javaType;
		this.defaultValue = defaultValue;
	}

	public static Object getDefaltValueFromClazz(final Class<?> clazz) {
		return Arrays.asList(JOrchestaType.values()) //
				.parallelStream() //
				.filter(predicate -> predicate.javaType.isAssignableFrom(ClassUtils.resolvePrimitiveIfNecessary(clazz))) //
				.map(jOrchestaType -> clazz.isEnum() ? getEnumDefaultValue(clazz) : jOrchestaType.defaultValue)
				.findFirst() //
				.orElse("");
	}

	private static Object getEnumDefaultValue(final Class<?> clazz) {
		try {
			final Object array = clazz.getDeclaredMethod("values", new Class[] {}).invoke(null, new Object[] {});
			int len = Array.getLength(array);
			return len > 0 ? Array.get(array, len - 1) : null;
		} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOGGER.debug("m=getEnumDefaultValue, clazz=" + clazz, e);
			return null;
		}
	}
}
