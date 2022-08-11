package bioroute_analysis;


import java.io.Serializable;


/**
 * A Quadruple stores four values (a "quadruple") and respects their order.
 * This generic class implements a commonly used data structure which is not present in
 * the current collection framework. Although it could be simulated with a List containing
 * two Objects, this implementation offers type safety and maximizes convenience for programmers.
 *
 * @author mfelder
 *
 * @param <A>
 * @param <B>
 * @param <C>
 */
public final class Quadruple<A, B, C, D> implements Serializable {
	private static final long serialVersionUID = 1L;

	public static <A, B, C, D> Quadruple<A, B, C, D> of(final A first, final B second, final C third, final D fourth) {
		return new Quadruple<>(first, second, third, fourth);
	}

	/**
	 * First entry of the tuple
	 */
	private final A first;
	/**
	 * Second entry of the tuple
	 */
	private final B second;
	/**
	 * Third entry of the tuple
	 */
	private final C third;
	/**
	 * Fourth entry of the tuple
	 */
	private final D fourth;
	/**
	 * Creates a new tuple with the two entries.
	 * @param first
	 * @param second
	 */
	public Quadruple(final A first, final B second, final C third, final D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}

	public A getFirst() {
		return this.first;
	}

	public B getSecond() {
		return this.second;
	}
	
	public C getThird() {
		return this.third;
	}
	public D getFourth() {
		return this.fourth;
	}
}

