package io.jbaker.strassen;

import java.util.Arrays;

import com.google.common.base.Preconditions;

public class Strassen {
	
	private static boolean isPow2(long n) {
		// max value of long 2^63-1, so max power of 2 2^62.
		for (int i = 0; i < 63; i++) {
			if (n == (1 << i)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isSquare(long[][] m) {
		for (long[] row : m) {
			Preconditions.checkNotNull(row);
			if (row.length != m.length) {
				return false;
			}
		}
		return true;
	}
	
	private static long[][] add(long[][] m1, long[][] m2) {
		final int n = m1.length;
		long[][] result = new long[n][n];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				result[i][j] = m1[i][j] + m2[i][j];
			}
		}
		
		return result;
	}
	
	private static long[][] sub(long[][] m1, long[][] m2) {
		final int n = m1.length;
		long[][] result = new long[n][n];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				result[i][j] = m1[i][j] - m2[i][j];
			}
		}
		
		return result;
	}
	
	private static class Tuple4 {
		final long[][] m11;
		final long[][] m12;
		final long[][] m21;
		final long[][] m22;
		
		Tuple4(long[][] a, long[][] b, long[][] c, long[][] d) {
			this.m11 = a;
			this.m12 = b;
			this.m21 = c;
			this.m22 = d;
		}
	}
	
	private static Tuple4 partition(long[][] m) {
		final int n = m.length;
	
		long[][] m11 = new long[n/2][n/2];
		long[][] m12 = new long[n/2][n/2];
		long[][] m21 = new long[n/2][n/2];
		long[][] m22 = new long[n/2][n/2];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				boolean c1 = i < (n/2);
				boolean c2 = j < (n/2);
				if (c1 && c2) {
					m11[i][j] = m[i][j];
				} else if (c1 && !c2) {
					m12[i][j - n/2] = m[i][j];
				} else if (!c1 && c2) {
					m21[i - n/2][j] = m[i][j];
				} else {
					m22[i - n/2][j - n/2] = m[i][j];
				}
			}
		}
		
		return new Tuple4(m11, m12, m21, m22);
	}
	
	private static long[][] merge(long[][] m11, long[][] m12, long[][] m21, long[][] m22) {		
		final int n = m11.length;
		long[][] result = new long[2*n][];
		for (int i = 0; i < n; i++) {
			long[] row1 = Arrays.copyOf(m11[i], 2*n);
			System.arraycopy(m12[i], 0, row1, n, n);
			result[i] = row1;
			
			long[] row2 = Arrays.copyOf(m21[i], 2*n);
			System.arraycopy(m22[i], 0, row2, n, n);
			result[n+i] = row2;
		}
		return result;
	}
	
	public static long[][] multiply(long[][] m1, long[][] m2) {
		Preconditions.checkNotNull(m1);
		Preconditions.checkNotNull(m2);
		Preconditions.checkArgument(m1.length == m2.length);
		Preconditions.checkArgument(isPow2(m1.length));
		Preconditions.checkArgument(isSquare(m1));
		Preconditions.checkArgument(isSquare(m2));
		
		return mul(m1, m2);
	}
	
	private static long[][] mul(long[][] m1, long[][] m2) {	
		final int n = m1.length;
		if (n == 1) {
			return new long[][]{{m1[0][0]*m2[0][0]}};
		}
		Tuple4 t1 = partition(m1);
		Tuple4 t2 = partition(m2);
		
		long[][] s1 = sub(t2.m12, t2.m22);
		long[][] s2 = add(t1.m11, t1.m12);
		long[][] s3 = add(t1.m21, t1.m22);
		long[][] s4 = sub(t2.m21, t2.m11);
		long[][] s5 = add(t1.m11, t1.m22);
		long[][] s6 = add(t2.m11, t2.m22);
		long[][] s7 = sub(t1.m12, t1.m22);
		long[][] s8 = add(t2.m21, t2.m22);
		long[][] s9 = sub(t1.m11, t1.m21);
		long[][] s10 = add(t2.m11, t2.m12);
		
		long[][] p1 = multiply(t1.m11, s1);
		long[][] p2 = multiply(s2, t2.m22);
		long[][] p3 = multiply(s3, t2.m11);
		long[][] p4 = multiply(t1.m22, s4);
		long[][] p5 = multiply(s5, s6);
		long[][] p6 = multiply(s7, s8);
		long[][] p7 = multiply(s9, s10);
		
		long[][] r11 = add(sub(add(p5, p4), p2), p6);
		long[][] r12 = add(p1, p2);
		long[][] r21 = add(p3, p4);
		long[][] r22 = sub(sub(add(p5, p1), p3), p7);
		
		return merge(r11, r12, r21, r22);
	}

}
