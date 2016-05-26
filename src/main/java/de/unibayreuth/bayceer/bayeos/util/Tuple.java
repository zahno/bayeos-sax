package de.unibayreuth.bayceer.bayeos.util;

/**
 * Tuple. Order of X and Y matters.
 * @author christiane-goehring
 *
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x.toString() + "," + y.toString() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple<?, ?>) {
			return ((Tuple<?, ?>) obj).x == this.x && ((Tuple<?, ?>) obj).y == y;
		}
		return super.equals(obj);

	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (x.hashCode() < y.hashCode()) {
			result = prime * result + ((x == null) ? 0 : x.hashCode());
			result = prime * result + ((y == null) ? 0 : y.hashCode());
		} else {
			result = prime * result + ((y == null) ? 0 : y.hashCode());
			result = prime * result + ((x == null) ? 0 : x.hashCode());
		}
		return result;
	}

}
