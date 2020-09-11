import java.util.ArrayList;
import java.util.Arrays;

public class MergeSortAndFriends {

	private static int[] binaryMerge(int A[], int B[], int lenA, int lenB) { // complete this function
		int[] merged = new int[lenA + lenB];
		
		int cursorA = 0;
		int cursorB = 0;
		int cursorMerged = 0;
		
		while (cursorA < lenA && cursorB < lenB) {
			if (A[cursorA] < B[cursorB]) {
				merged[cursorMerged] = A[cursorA];
				cursorA += 1;
			} else {
				merged[cursorMerged] = B[cursorB];
				cursorB += 1;
			}
			cursorMerged += 1;
		}
		
		// At this point, either A or B is fully in `merged`, so add the other
		
		while (cursorA < lenA) {
			merged[cursorMerged] = A[cursorA];
			cursorA += 1;
			cursorMerged += 1;
		}
		
		while (cursorB < lenB) {
			merged[cursorMerged] = B[cursorB];
			cursorB += 1;
			cursorMerged += 1;
		}
		
		return merged;
	}

	public static ArrayList<Integer> commonElements(int A[], int B[], int lenA, int lenB) { // complete this function
		ArrayList<Integer> commons = new ArrayList<>();
		
		int cursorA = 0;
		int cursorB = 0;
		
		while (cursorA < lenA && cursorB < lenB) {
			if (A[cursorA] < B[cursorB]) {
				cursorA += 1;
			} else if (A[cursorA] > B[cursorB]){
				cursorB += 1;
			} else {
				commons.add(A[cursorA]);
				cursorA += 1;
				
				while (cursorA < lenA && A[cursorA] == B[cursorB]) {
					cursorA += 1;
				}
			}
		}
		
		return commons;
	}

	public static int[] kWayMerge(int lists[][], int listLengths[], int k) { // complete this function
		if (k == 1) {
			return lists[0];
		} else if (k == 2) {
			return binaryMerge(lists[0], lists[1], listLengths[0], listLengths[1]);
		} else {
			int newK = (k + 1) / 2;
			int[][] mergedLists = new int[newK][];
			int[] mergedListLengths = new int[newK];
			
			for (int i = 0; i < (k/2); i += 1) {
				mergedListLengths[i] = listLengths[2*i] + listLengths[2*i + 1];
				mergedLists[i] = binaryMerge(lists[2*i], lists[2*i + 1], listLengths[2*i], listLengths[2*i + 1]);
			}
			
			if (k % 2 == 1) {
				mergedLists[newK-1] = lists[k-1];
				mergedListLengths[newK-1] = listLengths[k-1];
			}
			
			return kWayMerge(mergedLists, mergedListLengths, newK);
		}
	}

	public static void mergesort(int[] array, int left, int right) {
		if (left < right) {
			int mid = (left + right) / 2;
			mergesort(array, left, mid);
			mergesort(array, mid + 1, right);
			int A[] = Arrays.copyOfRange(array, left, mid + 1);
			int B[] = Arrays.copyOfRange(array, mid + 1, right + 1);
			int mergedArray[] = binaryMerge(A, B, A.length, B.length);
			int i = left;
			int j = 0;
			while (j <= right - left)
				array[i++] = mergedArray[j++];
		}
	}
}