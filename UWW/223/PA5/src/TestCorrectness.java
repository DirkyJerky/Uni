import java.util.Arrays;

public class TestCorrectness {

	private static void testRecursion() {
		System.out.println("*** Test Recursion ***\n");
		int A[] = { 123076, 689201, 6592073, 12461, 1355171 };
		for (int a = 0; a < A.length; a++)
			System.out.printf("Sum of even digits in %d is %d\n", A[a], Recursion.sumEvenDigits(A[a]));

		int B[] = { 1, 2, 3, 4, 5 };
		for (int b = 0; b < B.length; b++) {
			System.out.printf("\nAll binary strings of length %d that have more ones than zeroes\n", B[b]);
			Recursion.binaryStringsWithMoreOnes(B[b]);
		}
	}

	private static void testRotatedHelper(int array[], int arrayLen, int keys[], int numKeys) throws Exception {
		System.out.println("Array is: " + Arrays.toString(array));
		for (int a = 0; a < numKeys; a++) {
			int index = RotatedBinarySearch.search(array, arrayLen, keys[a]);
			boolean actuallyExists = false;
			for (int i = 0; i < arrayLen; i++)
				if (array[i] == keys[a]) {
					actuallyExists = true;
					break;
				}
			if ((actuallyExists && index < 0) || (!actuallyExists && index >= 0)
					|| (index >= 0 && array[index] != keys[a])) {
				throw new Exception("Something is wrong!!!");
			}
			if (index >= 0)
				System.out.printf("Key %d found at index %d\n", keys[a], index);
			else
				System.out.printf("Key %d not found\n", keys[a]);
		}
	}

	private static void testRotatedArray() throws Exception {
		System.out.println("\n*** Test Rotated Array ***\n");
		int A[] = { 8, 10, 14, 17, 19, 21, 1, 3, 5, 6 };
		int keys_A[] = { 8, 9, 13, 14, 15, 17, 18, 19, 20, 21, 24, 1, 2, 3, 4, 5, 6, 7, 9, 10, 12 };
		testRotatedHelper(A, A.length, keys_A, keys_A.length);

		System.out.println();
		int B[] = { 10, 1, 5, 7 };
		int keys_B[] = { 8, 10, 12, 0, 1, 3, 5, 6, 7, 9 };
		testRotatedHelper(B, B.length, keys_B, keys_B.length);

		System.out.println();
		int C[] = { 12, 1, 5, 7, 10, 11 };
		int keys_C[] = { 8, 10, 11, 12, 0, 1, 3, 5, 6, 7, 9 };
		testRotatedHelper(C, C.length, keys_C, keys_C.length);

		System.out.println();
		int D[] = { 12, 10, 11 };
		int keys_D[] = { 8, 10, 11, 12, 15 };
		testRotatedHelper(D, D.length, keys_D, keys_D.length);

		System.out.println();
		int E[] = { 12, 1 };
		int keys_E[] = { 0, 1, 4, 12, 14 };
		testRotatedHelper(E, E.length, keys_E, keys_E.length);
	}

	private static void testMergesort() {
		System.out.println("\n*** Test Mergesort ***\n");
		int A[] = { 19, 1, 12, 100, 7, 8, 4, -10, 88, 100, 349, 0, 7, -10 };
		System.out.println("Original Array: " + Arrays.toString(A));
		MergeSortAndFriends.mergesort(A, 0, A.length - 1);
		System.out.println("Sorted Array: " + Arrays.toString(A));
	}

	private static void testCommonElements() {
		System.out.println("\n*** Test Common Elements in Sorted Arrays ***\n");
		int A[] = { 1, 1, 3, 7, 9, 14, 14, 14, 14, 14, 14, 18, 23, 39, 39, 39 };
		int B[] = { 1, 1, 5, 7, 9, 14, 23, 27, 39, 39 };
		System.out.println("Array 1: " + Arrays.toString(A));
		System.out.println("Array 2: " + Arrays.toString(B));
		System.out.println("Common elements are " + MergeSortAndFriends.commonElements(A, B, A.length, B.length));

		int C[] = { 1, 1, 3, 7, 9, 14, 14, 14, 14, 14, 14, 18, 23, 39, 39, 39 };
		int D[] = { 8, 17, 17, 26, 38, 43, 43 };
		System.out.println("\nArray 1: " + Arrays.toString(C));
		System.out.println("Array 2: " + Arrays.toString(D));
		System.out.println("Common elements are " + MergeSortAndFriends.commonElements(C, D, C.length, D.length));

		int E[] = { 1, 3, 7, 9, 14, 18, 23, 39 };
		int F[] = { 1, 5, 7, 9, 14, 39 };
		System.out.println("\nArray 1: " + Arrays.toString(E));
		System.out.println("Array 2: " + Arrays.toString(F));
		System.out.println("Common elements are " + MergeSortAndFriends.commonElements(E, F, E.length, F.length));
	}

	private static void testKSortedMerge() {
		System.out.println("\n*** Test Merging k Sorted Arrays ***\n");
		int list0[] = { 1, 5, 9, 18 };
		int list1[] = { -10, 5, 18, 67, 100 };
		int list2[] = { -12, -9, -6, 0, 1, };
		int list3[] = { -65, -32, 10, };
		int list4[] = { 1, 19, 45, 67 };
		int lists[][] = { list0, list1, list2, list3, list4 };
		int k = lists.length;
		int eachListLength[] = new int[k];
		for (int i = 0; i < k; i++)
			eachListLength[i] = lists[i].length;
		int mergedList[] = MergeSortAndFriends.kWayMerge(lists, eachListLength, k);
		System.out.println("Original sorted arrays");
		for (int i = 0; i < k; i++)
			System.out.println(Arrays.toString(lists[i]));
		System.out.println("\nFinal merged array:" + Arrays.toString(mergedList) + "\n");

		k = 4;
		eachListLength = new int[k];
		for (int i = 0; i < k; i++)
			eachListLength[i] = lists[i].length;
		mergedList = MergeSortAndFriends.kWayMerge(lists, eachListLength, k);
		System.out.println("Original sorted arrays");
		for (int i = 0; i < k; i++)
			System.out.println(Arrays.toString(lists[i]));
		System.out.println("\nFinal merged array:" + Arrays.toString(mergedList) + "\n");

		k = 2;
		eachListLength = new int[k];
		for (int i = 0; i < k; i++)
			eachListLength[i] = lists[i].length;
		mergedList = MergeSortAndFriends.kWayMerge(lists, eachListLength, k);
		System.out.println("Original sorted arrays");
		for (int i = 0; i < k; i++)
			System.out.println(Arrays.toString(lists[i]));
		System.out.println("\nFinal merged array:" + Arrays.toString(mergedList) + "\n");

		k = 1;
		eachListLength = new int[k];
		for (int i = 0; i < k; i++)
			eachListLength[i] = lists[i].length;
		mergedList = MergeSortAndFriends.kWayMerge(lists, eachListLength, k);
		System.out.println("Original sorted arrays");
		for (int i = 0; i < k; i++)
			System.out.println(Arrays.toString(lists[i]));
		System.out.println("\nFinal merged array:" + Arrays.toString(mergedList) + "\n");
	}

	private static void testMergesortLinkedList() {
		System.out.println("*** Mergesort Linked List ***\n");

		LinkedList list = new LinkedList();

		list.insertAtFront(5);
		list.insertAtEnd(32);
		list.insertAtFront(16);
		list.insertAtFront(5);
		list.insertAtEnd(32);
		list.insertAtFront(16);
		list.insertAtFront(8);
		list.insertAtEnd(21);
		list.insertAtEnd(50);
		list.insertAtEnd(32);
		list.insertAtFront(66);
		list.insertAtFront(66);
		list.insertAtFront(-12);
		System.out.print("Original List: ");
		list.printList();
		list.mergesort();
		System.out.print("Sorted List: ");
		list.printList();
	}

	public static void main(String[] args) throws Exception {
		testRecursion();
		testRotatedArray();
		testMergesort();
		testCommonElements();
		testKSortedMerge();
		testMergesortLinkedList();
	}
}
