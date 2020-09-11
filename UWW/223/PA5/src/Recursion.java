public class Recursion {

	// Simple 
//	public static int sumEvenDigits(int n) { // complete this function
//		int digit = n % 10;
//		int rest = n / 10;
//		
//		int total = 0;
//		
//		if (digit % 2 == 0) {
//			total += digit;
//		}
//		
//		if (rest != 0) {
//			total += sumEvenDigits(rest);
//		}
//		
//		return total;
//	}
	
	// Tail-call optimized
	public static int sumEvenDigits(int n) { // complete this function
		return sumEvenDigits(n,0);
	}
	private static int sumEvenDigits(int n, int accum) {
		if (n == 0) {
			return accum;
		}

		int digit = n % 10;
		if (digit % 2 == 0) {
			accum += digit;
		}
		
		return sumEvenDigits(n / 10, accum);
	}

	public static void binaryStringsWithMoreOnes(int n) {
		binaryStringsWithMoreOnes("", 0, 0, n);
	}

	private static void binaryStringsWithMoreOnes(String str, int numZeroes, int numOnes, int n) { // complete this function
		if (numZeroes > (n / 2)) {
			return;
		}
		
		if (numZeroes + numOnes == n) {
			if (numOnes > numZeroes) {
				System.out.println(str);
			}
		} else {
			binaryStringsWithMoreOnes(str + "0", numZeroes + 1, numOnes, n);
			binaryStringsWithMoreOnes(str + "1", numZeroes, numOnes + 1, n);
		}
	}
}