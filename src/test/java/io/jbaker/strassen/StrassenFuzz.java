package io.jbaker.strassen;

import static org.junit.Assert.*;
import io.jbaker.strassen.Strassen;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class StrassenFuzz {
	private static final int SIZE = 16;
	
	private static long[][] mult(long[][] a, long[][] b) {
		long[][] r = new long[a.length][a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				for (int k = 0; k < a.length; k++) {
					r[i][j] += (a[i][k] * b[k][j]);
				}
			}
		}
		return r;
	}

	private long[][] makeMatrix(long seed) {
		Random r = new Random(seed);
		long[][] result = new long[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				result[i][j] = r.nextLong();
			}
		}
		return result;
	}
	
	@Test
	public void test() {
		for (int i = 0; i < 1000; i++) {
			long[][] a = makeMatrix(2*i);
			long[][] b = makeMatrix(2*i + 1);
			long[][] expected = mult(a, b);
			long[][] result = Strassen.multiply(a, b);
			assertTrue(Arrays.deepEquals(expected, result));
		}
	}

}
