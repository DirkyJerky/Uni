public class LinkedList {

	private ListNode head, tail;
	private int size;
	private int mergedArray[];

	public LinkedList() {
		head = tail = null;
		size = 0;
	}

	public void mergesort() {
		mergedArray = new int[size()];
		mergesort(0, size() - 1, head, tail);
	}

	private void mergesort(int left, int right, ListNode leftNode, ListNode rightNode) { // complete this function
		if (left >= right) {
			return;
		}
		
		int mid = (left + right) / 2;
		
		ListNode midNode = leftNode;
		for (int i = left; i < mid; i += 1) {
			midNode = midNode.next;
		}
		
		mergesort(left, mid, leftNode, midNode);
		mergesort(mid + 1, right, midNode.next, rightNode);
		
		int cursorMerged = left;
		ListNode cursorLeft = leftNode;
		ListNode cursorRight = midNode.next;
		
		while (cursorLeft != midNode.next && cursorRight != rightNode.next) {
			if (cursorLeft.value < cursorRight.value) {
				mergedArray[cursorMerged] = cursorLeft.value;
				cursorMerged += 1;
				cursorLeft = cursorLeft.next;
			} else {
				mergedArray[cursorMerged] = cursorRight.value;
				cursorMerged += 1;
				cursorRight = cursorRight.next;
			}
		}
		
		while (cursorLeft != midNode.next) {
			mergedArray[cursorMerged] = cursorLeft.value;
			cursorMerged += 1;
			cursorLeft = cursorLeft.next;
		} 
		
		while (cursorRight != rightNode.next) {
			mergedArray[cursorMerged] = cursorRight.value;
			cursorMerged += 1;
			cursorRight = cursorRight.next;
		}
		
		cursorMerged = left;
		ListNode cursorLinked = leftNode;
		
		while (cursorLinked != rightNode.next) {
			cursorLinked.value = mergedArray[cursorMerged];
			cursorMerged += 1;
			cursorLinked = cursorLinked.next;
		} 
	}

	public ListNode insertAtFront(int value) {
		ListNode newNode = new ListNode(value);
		if (size == 0) {
			head = tail = newNode;
		} else {
			newNode.next = head;
			head = newNode;
		}
		size++;
		return newNode;
	}

	public ListNode insertAtEnd(int value) {
		ListNode newNode = new ListNode(value);
		if (size == 0) {
			head = tail = newNode;
		} else {
			tail.next = newNode;
			tail = newNode;
		}
		size++;
		return newNode;
	}

	public void printList() {
		if (size == 0)
			System.out.println("[]");
		else {
			ListNode tmp = head;
			String output = "[";
			for (int i = 0; i < size - 1; i++) {
				output += tmp.value + " -> ";
				tmp = tmp.next;
			}
			output += tail.value + "]";
			System.out.println(output);
		}
	}

	public int size() {
		return size;
	}
}
