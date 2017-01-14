import java.util.function.Function;

public class CachingFunction<T, R> implements Function<T, R> {

	Function function;
	BoundHashMap<T, R> boundHashMap = new BoundHashMap();

	/**
	 * @param func
	 */
	public CachingFunction(Function f) {
		super();
		function = f;
	}

	@Override
	public R apply(T t) {
		if (!boundHashMap.containsKey(t)) {
			boundHashMap.put(t, (R) function.apply(t));
		}
		return boundHashMap.get(t);
	}

}
