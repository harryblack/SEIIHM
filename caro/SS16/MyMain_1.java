import java.util.function.Function;

public class MyMain_1 {
	public static void main(String... args) {
		BoundHashMap boundHashMap = new BoundHashMap();
		boundHashMap.put(1, "Erster");
		boundHashMap.put(2, "Zweiter");
		boundHashMap.put(3, "Dritter");
		boundHashMap.put(4, "Vierter");
		boundHashMap.put(5, "Fünfter");
		boundHashMap.put(6, "Sechser");
		boundHashMap.put(7, "Sieben");
		boundHashMap.put(8, "Acht");
		boundHashMap.put(9, "Neun");
		boundHashMap.put(10, "Zehn");
		boundHashMap.put(11, "Elf");
		boundHashMap.put(12, "Zwoelf");

		Function<Double, Double> f = new Function<Double, Double>() {
			@Override
			public Double apply(Double t) {
				return Math.sin(t);
			}
		};

		CachingFunction<Double, Double> cachingFunction = new CachingFunction<>(f);
		cachingFunction.apply(1.0);

	}
}
