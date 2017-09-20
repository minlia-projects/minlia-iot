package com.minlia.iot.signature;

import java.util.Comparator;
import lombok.Getter;
import lombok.Setter;

/**
 * ASCII字典排序
 * 
 * @param <T>
 */
public class AsciiSortedComparator<T> implements Comparator<T> {

	@Setter
	@Getter
	private StringAssign<T> stringAssign;

	public AsciiSortedComparator(StringAssign<T> stringAssign) {
		this.stringAssign = stringAssign;
	}

	@Override
	public int compare(T t1, T t2) {
		char[] string1 = stringAssign.assign(t1).toCharArray();
		char[] string2 = stringAssign.assign(t2).toCharArray();

		for (int i = 0; i < string1.length; i++) {

			if (i < string2.length) {
				if (string1[i] < string2[i]) {
					return -1;
				} else if (string1[i] > string2[i]) {
					return 1;
				}
			} else {
				return 1;
			}
		}

		return 0;
	}

	/**
	 * 指定String接口
	 * 
	 * @param <T>
	 */
	@FunctionalInterface
	public interface StringAssign<T> {

		public String assign(T t);
	}

}
