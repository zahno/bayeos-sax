package de.unibayreuth.bayceer.bayeos.util;

/**
 * Tuple where the order of X and Y doesn't matter
 * @author christiane-goehring
 *
 * @param <X>
 * @param <Y>
 */
public class MirroredTuple<X, Y> {
	public final X x;
	public final Y y;

	public MirroredTuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof MirroredTuple<?, ?>) {
			return (((MirroredTuple<?, ?>) obj).x == this.x && ((MirroredTuple<?, ?>) obj).y == y)
					|| (((MirroredTuple<?, ?>) obj).x == this.y && ((MirroredTuple<?, ?>) obj).y == x);
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
	
	@Override
	public String toString() {
		return "("+x.toString()+","+y.toString()+")";
	}

}
