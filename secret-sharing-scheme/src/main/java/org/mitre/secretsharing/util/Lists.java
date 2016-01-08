package org.mitre.secretsharing.util;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

public abstract class Lists {

	private static class ViewAppendedList<T> extends AbstractList<T> {
		private final List<? extends T> lhs;
		private final List<? extends T> rhs;

		public ViewAppendedList(List<? extends T> lhs, List<? extends T> rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}

		@Override
		public T get(int index) {
			if(index < 0 || index >= size())
				throw new IndexOutOfBoundsException();
			return (index < lhs.size() ? lhs.get(index) : rhs.get(index - lhs.size()));
		}

		@Override
		public int size() {
			return lhs.size() + rhs.size();
		}
	}
	
	private static class RandomAccessViewAppendedList<T> extends ViewAppendedList<T> implements RandomAccess {
		public RandomAccessViewAppendedList(List<? extends T> lhs, List<? extends T> rhs) {
			super(lhs, rhs);
		}
	}

	private Lists() {}

	public static <T> List<T> viewAppended(List<? extends T> lhs, List<? extends T> rhs) {
		if((lhs instanceof RandomAccess) && (rhs instanceof RandomAccess))
			return new RandomAccessViewAppendedList<T>(lhs, rhs);
		return new ViewAppendedList<T>(lhs, rhs);
	}
	
}
