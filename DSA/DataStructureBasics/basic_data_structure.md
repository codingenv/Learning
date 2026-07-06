# Data Structures -- 150 Standard Interview Questions with Optimized Java Solutions

> **150 Questions** | 6 Data Structures | 25 Questions Each
> Covers: Singly Linked List | Doubly Linked List | Stack | Queue | Tree | Graph
> Difficulty progression: Basic -> Medium -> Hard
> Each question includes: Examples, Optimized Java Code, Approach Rationale, Complexity Analysis

---

## Master Index

### Difficulty Breakdown

| Difficulty | Count | Percentage |
|---|---|---|
| **Basic** | ~30 | 20% |
| **Medium** | ~78 | 52% |
| **Hard** | ~42 | 28% |

---

### Part 1: Singly Linked List (Q1 - Q25)

| # | Question | Difficulty | Key Technique |
|---|---|---|---|
| Q1 | Reverse a Singly Linked List | Basic | Three-pointer iterative |
| Q2 | Find the Middle of a Linked List | Basic | Slow/fast pointer |
| Q3 | Delete a Node (given only node reference) | Basic | Copy next node's value |
| Q4 | Remove Duplicates from Sorted List | Basic | Single-pass pointer |
| Q5 | Merge Two Sorted Linked Lists | Basic | Dummy head merge |
| Q6 | Detect a Cycle in a Linked List | Medium | Floyd's cycle detection |
| Q7 | Find the Starting Node of a Cycle | Medium | Floyd's + second pass |
| Q8 | Intersection Point of Two Linked Lists | Medium | Length difference technique |
| Q9 | Remove N-th Node from End of List | Medium | Two-pointer gap |
| Q10 | Check if Linked List is a Palindrome | Medium | Reverse second half |
| Q11 | Add Two Numbers as Linked Lists | Medium | Digit-by-digit with carry |
| Q12 | Odd-Even Linked List | Medium | Two-pointer partition |
| Q13 | Partition List Around a Value | Medium | Two-list merge |
| Q14 | Rotate Linked List by K Places | Medium | Circular + break |
| Q15 | Swap Nodes in Pairs | Medium | Iterative pair swap |
| Q16 | Sort a Linked List (Merge Sort) | Medium | Top-down merge sort |
| Q17 | Remove Duplicates from Unsorted List | Medium | HashSet tracking |
| Q18 | Reorder List: L0->Ln->L1->Ln-1 | Medium | Split + reverse + merge |
| Q19 | Copy Linked List with Random Pointer | Hard | Interleaving technique |
| Q20 | Flatten a Multilevel Sorted Linked List | Hard | Merge with priority queue |
| Q21 | Reverse Nodes in k-Group | Hard | Iterative group reversal |
| Q22 | Merge K Sorted Linked Lists | Hard | Min-heap |
| Q23 | Reverse Linked List II (between m and n) | Hard | In-place partial reversal |
| Q24 | Cycle Length + Entry Point Combined | Hard | Floyd's extended |
| Q25 | Design a Linked List (full implementation) | Hard | From-scratch design |

### Part 2: Doubly Linked List (Q26 - Q50)

| # | Question | Difficulty | Key Technique |
|---|---|---|---|
| Q26 | Insert at Beginning of DLL | Basic | Head pointer update |
| Q27 | Insert at End of DLL | Basic | Tail traversal |
| Q28 | Delete a Given Node from DLL | Basic | Prev/next bypass |
| Q29 | Reverse a Doubly Linked List | Basic | Swap prev/next |
| Q30 | Insert at a Given Position in DLL | Basic | Traverse + splice |
| Q31 | Remove Duplicates from Sorted DLL | Medium | Single-pass skip |
| Q32 | Find Pairs with Given Sum in Sorted DLL | Medium | Two-pointer inward |
| Q33 | Merge Two Sorted Doubly Linked Lists | Medium | Dummy head merge |
| Q34 | Count Triplets with Given Sum in Sorted DLL | Medium | Fix one + two-pointer |
| Q35 | Rotate DLL by N Nodes | Medium | Circular + break |
| Q36 | Delete All Occurrences of a Key in DLL | Medium | Multi-delete traversal |
| Q37 | Sort a Doubly Linked List (Merge Sort) | Medium | Merge sort for DLL |
| Q38 | Group Odd and Even Indexed Nodes | Medium | Two-list partition |
| Q39 | Insert in Sorted Position in Sorted DLL | Medium | Find position + splice |
| Q40 | Swap Kth from Beginning with Kth from End | Medium | Two-pointer + swap |
| Q41 | Convert Binary Tree to DLL (inorder) | Medium | Inorder DFS |
| Q42 | Reverse DLL in Groups of K | Medium | Group reversal |
| Q43 | Flatten Multilevel Doubly Linked List | Medium | DFS / stack flatten |
| Q44 | LRU Cache using DLL + HashMap | Hard | Sentinel DLL + HashMap |
| Q45 | Design Browser History | Hard | DLL navigation |
| Q46 | Max Stack using DLL + TreeMap | Hard | DLL + sorted map |
| Q47 | All O(1) Data Structure (Inc/Dec/GetMax/GetMin) | Hard | Bucket DLL + HashMap |
| Q48 | Design Text Editor with Cursor | Hard | DLL with cursor pointer |
| Q49 | LFU Cache (Least Frequently Used) | Hard | Frequency buckets + DLL |
| Q50 | Memory-Efficient DLL (XOR Linked List concept) | Hard | ID-based simulation |

### Part 3: Stack (Q51 - Q75)

| # | Question | Difficulty | Key Technique |
|---|---|---|---|
| Q51 | Implement Stack using Array | Basic | Dynamic array |
| Q52 | Valid Parentheses | Basic | Stack matching |
| Q53 | Reverse a String using Stack | Basic | LIFO reversal |
| Q54 | Implement Two Stacks in One Array | Basic | Grow from both ends |
| Q55 | Sort a Stack using Recursion | Basic | Recursive insert |
| Q56 | Min Stack (getMin in O(1)) | Medium | Parallel min stack |
| Q57 | Evaluate Reverse Polish Notation | Medium | Operand stack |
| Q58 | Daily Temperatures | Medium | Monotonic stack (decreasing) |
| Q59 | Next Greater Element I | Medium | Monotonic stack + HashMap |
| Q60 | Next Greater Element II (Circular) | Medium | Circular traversal + stack |
| Q61 | Implement Stack using Two Queues | Medium | Push-heavy or pop-heavy |
| Q62 | Decode String ("3[a2[c]]") | Medium | Nested stack |
| Q63 | Simplify Unix File Path | Medium | Token stack |
| Q64 | Asteroid Collision | Medium | Simulation stack |
| Q65 | Remove Adjacent Duplicates II (k dupes) | Medium | Count stack |
| Q66 | Remove K Digits for Smallest Number | Medium | Monotonic stack (increasing) |
| Q67 | Stock Span Problem | Medium | Monotonic stack |
| Q68 | Validate Stack Sequences | Medium | Simulation |
| Q69 | Basic Calculator (+ - parentheses) | Hard | Recursive / sign stack |
| Q70 | Basic Calculator II (+ - * /) | Hard | Operator precedence stack |
| Q71 | Largest Rectangle in Histogram | Hard | Monotonic stack |
| Q72 | Trapping Rain Water (stack approach) | Hard | Monotonic stack |
| Q73 | Maximum Frequency Stack | Hard | Freq map + stack map |
| Q74 | Longest Valid Parentheses | Hard | Stack with index tracking |
| Q75 | Number of Visible People in a Queue | Hard | Monotonic stack (right to left) |

### Part 4: Queue (Q76 - Q100)

| # | Question | Difficulty | Key Technique |
|---|---|---|---|
| Q76 | Implement Queue using Array (Circular) | Basic | Modular arithmetic |
| Q77 | Implement Queue using Two Stacks | Basic | Lazy transfer |
| Q78 | Implement Queue using Linked List | Basic | Head/tail pointers |
| Q79 | Generate Binary Numbers 1 to N | Basic | BFS pattern |
| Q80 | Number of Recent Calls | Basic | Time-window queue |
| Q81 | Design Circular Queue | Medium | Array + front/rear/size |
| Q82 | Implement Deque from Scratch | Medium | Circular array |
| Q83 | First Non-Repeating Character in Stream | Medium | Queue + frequency map |
| Q84 | Implement Stack using Two Queues | Medium | Push-heavy or pop-heavy |
| Q85 | Reverse First K Elements of Queue | Medium | Stack-assisted reversal |
| Q86 | Interleave First Half with Second Half | Medium | Queue splitting |
| Q87 | Rotting Oranges (multi-source BFS) | Medium | BFS level-by-level |
| Q88 | Walls and Gates (BFS from gates) | Medium | Multi-source BFS |
| Q89 | Moving Average from Data Stream | Medium | Sliding window queue |
| Q90 | Task Scheduler | Medium | Greedy + cooldown |
| Q91 | Design Hit Counter | Medium | Queue / circular buffer |
| Q92 | Zigzag Iterator | Medium | Queue of iterators |
| Q93 | Design Circular Deque | Medium | Circular array |
| Q94 | Time Needed to Buy Tickets | Medium | Simulation |
| Q95 | Sliding Window Maximum | Hard | Monotonic deque |
| Q96 | Shortest Subarray with Sum >= K | Hard | Prefix sum + deque |
| Q97 | Jump Game VI (DP + Deque) | Hard | DP + monotonic deque |
| Q98 | Design Snake Game | Hard | Deque simulation |
| Q99 | Maximum of All Subarrays of Size K | Hard | Monotonic deque |
| Q100 | Process Tasks Using Servers | Hard | Priority queue + queue |

### Part 5: Tree (Q101 - Q125)

| # | Question | Difficulty | Key Technique |
|---|---|---|---|
| Q101 | Binary Tree Inorder Traversal (Iterative) | Basic | Explicit stack |
| Q102 | Binary Tree Preorder Traversal (Iterative) | Basic | Stack push right-then-left |
| Q103 | Binary Tree Postorder Traversal (Iterative) | Basic | Two-stack / reverse |
| Q104 | Maximum Depth of Binary Tree | Basic | Recursive DFS |
| Q105 | Check if Two Binary Trees are Identical | Basic | Parallel DFS |
| Q106 | Invert / Mirror a Binary Tree | Basic | Recursive swap |
| Q107 | Symmetric Tree (Mirror Check) | Medium | Dual-pointer mirror |
| Q108 | Level Order Traversal (BFS) | Medium | Queue-based BFS |
| Q109 | Zigzag Level Order Traversal | Medium | BFS + direction flag |
| Q110 | Validate Binary Search Tree | Medium | Range-based recursion |
| Q111 | Lowest Common Ancestor | Medium | Post-order DFS |
| Q112 | Construct Tree from Preorder + Inorder | Medium | Recursive + HashMap |
| Q113 | Kth Smallest Element in BST | Medium | Iterative inorder |
| Q114 | Binary Tree Right Side View | Medium | BFS last-per-level |
| Q115 | Flatten Binary Tree to Linked List | Medium | Reverse postorder |
| Q116 | Diameter of Binary Tree | Medium | DFS with global max |
| Q117 | Path Sum II (all paths with target) | Medium | Backtracking DFS |
| Q118 | Count Complete Tree Nodes in O(log^2 n) | Medium | Height comparison trick |
| Q119 | Binary Tree Maximum Path Sum | Hard | DP on tree |
| Q120 | Serialize and Deserialize Binary Tree | Hard | Preorder + null markers |
| Q121 | Vertical Order Traversal | Hard | BFS + TreeMap |
| Q122 | Recover BST (two swapped nodes) | Hard | Inorder anomaly detection |
| Q123 | Morris Inorder Traversal (O(1) space) | Hard | Threaded binary tree |
| Q124 | Binary Tree Cameras | Hard | 3-state greedy DP |
| Q125 | Convert Sorted Array/List to Balanced BST | Hard | Recursive midpoint |

### Part 6: Graph (Q126 - Q150)

| # | Question | Difficulty | Key Technique |
|---|---|---|---|
| Q126 | BFS Traversal of a Graph | Basic | Queue + visited set |
| Q127 | DFS Traversal of a Graph (Iterative) | Basic | Stack + visited set |
| Q128 | Count Connected Components | Basic | DFS/BFS per component |
| Q129 | Find if Path Exists Between Two Nodes | Basic | BFS / Union-Find |
| Q130 | Detect Cycle in Undirected Graph | Medium | Union-Find |
| Q131 | Detect Cycle in Directed Graph | Medium | DFS 3-color (W/G/B) |
| Q132 | Number of Islands | Medium | DFS flood fill |
| Q133 | Clone a Graph (Deep Copy) | Medium | BFS + HashMap |
| Q134 | Is Graph Bipartite? | Medium | 2-color BFS |
| Q135 | Topological Sort (Kahn's Algorithm) | Medium | BFS + in-degree |
| Q136 | Course Schedule (can finish?) | Medium | Cycle detection in DAG |
| Q137 | Course Schedule II (return order) | Medium | Topological sort |
| Q138 | Word Ladder (shortest transformation) | Medium | BFS word graph |
| Q139 | Pacific Atlantic Water Flow | Medium | Multi-source DFS/BFS |
| Q140 | Surrounded Regions | Medium | Border-connected DFS |
| Q141 | Graph Valid Tree | Medium | n-1 edges + connected |
| Q142 | Minimum Height Trees (centroids) | Medium | Leaf pruning |
| Q143 | Minimum Spanning Tree (Kruskal's) | Hard | Sort edges + Union-Find |
| Q144 | Dijkstra's Shortest Path | Hard | Min-heap greedy |
| Q145 | Bellman-Ford Shortest Path | Hard | V-1 relaxation rounds |
| Q146 | Floyd-Warshall All-Pairs Shortest Path | Hard | 3-nested-loop DP |
| Q147 | Network Delay Time | Hard | Dijkstra application |
| Q148 | Cheapest Flights Within K Stops | Hard | Modified Bellman-Ford |
| Q149 | Critical Connections (Tarjan's Bridges) | Hard | Low-link DFS |
| Q150 | Alien Dictionary | Hard | Topological sort from constraints |

---

## Common Node Definitions (used across all solutions)

```java
// Singly Linked List Node
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; this.next = null; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

// Doubly Linked List Node
class DoublyListNode {
    int val;
    DoublyListNode prev, next;
    DoublyListNode(int val) { this.val = val; }
}

// Binary Tree Node
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
```

---
---

# SINGLY LINKED LIST

> **Node Definition:** All solutions in this section use the shared `ListNode` class defined in the common definitions section. Refer to it there.

---

## Mini-Index

| # | Title | Difficulty | Key Technique |
|---|---|---|---|
| 1 | [Reverse a Singly Linked List](#q1-reverse-a-singly-linked-list) | Basic | Iterative 3-pointer |
| 2 | [Find the Middle of a Linked List](#q2-find-the-middle-of-a-linked-list) | Basic | Slow/Fast pointer |
| 3 | [Delete a Node Given Only That Node's Reference](#q3-delete-a-node-given-only-that-nodes-reference) | Basic | Copy next node's value |
| 4 | [Remove Duplicates from Sorted Linked List](#q4-remove-duplicates-from-sorted-linked-list) | Basic | Sequential scan |
| 5 | [Merge Two Sorted Linked Lists](#q5-merge-two-sorted-linked-lists) | Basic | Dummy head technique |
| 6 | [Detect a Cycle in a Linked List](#q6-detect-a-cycle-in-a-linked-list) | Medium | Floyd's algorithm |
| 7 | [Find the Starting Node of a Cycle](#q7-find-the-starting-node-of-a-cycle) | Medium | Floyd's + phase 2 |
| 8 | [Intersection Point of Two Linked Lists](#q8-intersection-point-of-two-linked-lists) | Medium | Two-pointer length equalization |
| 9 | [Remove N-th Node from End of List](#q9-remove-n-th-node-from-end-of-list) | Medium | Two-pointer gap |
| 10 | [Check if Linked List is a Palindrome](#q10-check-if-linked-list-is-a-palindrome) | Medium | Reverse second half |
| 11 | [Add Two Numbers Represented as Linked Lists](#q11-add-two-numbers-represented-as-linked-lists) | Medium | Digit-by-digit with carry |
| 12 | [Odd-Even Linked List](#q12-odd-even-linked-list) | Medium | Odd/Even index grouping |
| 13 | [Partition List Around a Value](#q13-partition-list-around-a-value) | Medium | Two dummy lists |
| 14 | [Rotate Linked List by K Places](#q14-rotate-linked-list-by-k-places) | Medium | Circular connection |
| 15 | [Swap Nodes in Pairs](#q15-swap-nodes-in-pairs) | Medium | Iterative pair swap |
| 16 | [Sort a Linked List](#q16-sort-a-linked-list) | Medium | Merge Sort |
| 17 | [Remove Duplicates from Unsorted Linked List](#q17-remove-duplicates-from-unsorted-linked-list) | Medium | HashSet tracking |
| 18 | [Reorder List: L0->Ln->L1->Ln-1->...](#q18-reorder-list-l0lnl1ln-1) | Medium | Split + Reverse + Merge |
| 19 | [Copy Linked List with Random Pointer](#q19-copy-linked-list-with-random-pointer) | Hard | Interleaving clone |
| 20 | [Flatten a Multilevel Sorted Linked List](#q20-flatten-a-multilevel-sorted-linked-list) | Hard | Recursive merge |
| 21 | [Reverse Nodes in k-Group](#q21-reverse-nodes-in-k-group) | Hard | Group-based reversal |
| 22 | [Merge K Sorted Linked Lists](#q22-merge-k-sorted-linked-lists) | Hard | Min-Heap |
| 23 | [Reverse Linked List II (Between Positions m and n)](#q23-reverse-linked-list-ii-between-positions-m-and-n) | Hard | Sublist reversal |
| 24 | [Find the Length of a Cycle + Entry Point Combined](#q24-find-the-length-of-a-cycle--entry-point-combined) | Hard | Floyd's extended |
| 25 | [Design a Linked List](#q25-design-a-linked-list) | Hard | Full implementation |

---
---

## Q1. Reverse a Singly Linked List

**Difficulty:** `Basic` | **Data Structure:** Singly Linked List

### Problem

Given the head of a singly linked list, reverse the list and return the new head. The reversal must be done **in-place** using iterative pointer manipulation.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5 -> null
Output: 5 -> 4 -> 3 -> 2 -> 1 -> null

Input:  1 -> 2 -> null
Output: 2 -> 1 -> null

Input:  null
Output: null
```

### Approach: Iterative 3-Pointer Reversal

**Why this approach?**

We maintain three pointers -- `prev`, `curr`, and `next` -- to reverse each link one at a time as we traverse the list. At each step we:
1. Save `curr.next` into `next` (so we don't lose the rest of the list).
2. Point `curr.next` back to `prev` (reverse the link).
3. Advance `prev` and `curr` forward.

This is preferred over recursion because it uses **O(1) space** (no call stack), runs in a single pass, and is the most intuitive mental model: "walk forward, flip each arrow behind you." A recursive approach would add O(n) stack space for no algorithmic benefit.

**Walk-through with `1 -> 2 -> 3`:**
| Step | prev | curr | next | Action |
|------|------|------|------|--------|
| 0 | null | 1 | -- | Start |
| 1 | null | 1 | 2 | 1.next = null |
| 2 | 1 | 2 | 3 | 2.next = 1 |
| 3 | 2 | 3 | null | 3.next = 2 |
| End | 3 | null | -- | Return prev (3) |

### Optimized Java Solution

```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;

    while (curr != null) {
        ListNode next = curr.next;   // save next
        curr.next = prev;            // reverse the link
        prev = curr;                 // advance prev
        curr = next;                 // advance curr
    }
    return prev; // prev is the new head
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We visit each of the n nodes exactly once. |
| **Space** | **O(1)** | Only three pointer variables are used regardless of list size. |

---

## Q2. Find the Middle of a Linked List

**Difficulty:** `Basic` | **Data Structure:** Singly Linked List

### Problem

Given the head of a singly linked list, return the **middle** node. If there are two middle nodes (even-length list), return the **second** middle node.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5 -> null
Output: Node with value 3
Explanation: The middle of a 5-node list is the 3rd node.

Input:  1 -> 2 -> 3 -> 4 -> 5 -> 6 -> null
Output: Node with value 4
Explanation: Two middles (3 and 4); we return the second one.

Input:  1 -> null
Output: Node with value 1
```

### Approach: Slow/Fast Pointer (Tortoise and Hare)

**Why this approach?**

Use two pointers: `slow` moves one step at a time, `fast` moves two steps. When `fast` reaches the end, `slow` is at the middle. This is a **single-pass O(n)** solution with **O(1) space**.

Alternative: Count the length first, then traverse to `length/2`. This works but requires **two passes**. The slow/fast technique is a fundamental linked-list pattern that also underpins cycle detection and many other problems, making it essential to master.

**Walk-through with `1 -> 2 -> 3 -> 4 -> 5`:**
| Step | slow | fast |
|------|------|------|
| 0 | 1 | 1 |
| 1 | 2 | 3 |
| 2 | 3 | 5 |
| -- | stop | fast.next == null |

Result: `slow` = node 3.

### Optimized Java Solution

```java
public ListNode middleNode(ListNode head) {
    var slow = head;
    var fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Fast pointer traverses the list once; slow pointer traverses half. Total work is O(n). |
| **Space** | **O(1)** | Only two pointer variables used. |

---

## Q3. Delete a Node Given Only That Node's Reference

**Difficulty:** `Basic` | **Data Structure:** Singly Linked List

### Problem

You are given **only** a reference to a node to be "deleted" from a singly linked list. You do **not** have access to the `head` of the list. The given node is guaranteed **not** to be the tail node. Delete the node from the linked list.

### Examples

```
Input:  List = 4 -> 5 -> 1 -> 9, node = 5
Output: 4 -> 1 -> 9
Explanation: Node with value 5 is removed.

Input:  List = 4 -> 5 -> 1 -> 9, node = 1
Output: 4 -> 5 -> 9

Input:  List = 1 -> 2 -> 3 -> 4, node = 1
Output: 2 -> 3 -> 4
```

### Approach: Copy Next Node's Value

**Why this approach?**

Since we have no access to the previous node, we cannot do a traditional "unlink" operation. Instead, we **copy** the next node's value into the current node, then skip the next node. Effectively, we delete the *next* node but make the *current* node impersonate it first.

This is a classic trick question. The key insight is that "deletion" doesn't mean freeing this specific memory location -- it means the list no longer contains this value in this position.

**Limitation:** This cannot delete the **tail** node (no next node to copy from). The problem guarantees the node is not the tail.

### Optimized Java Solution

```java
public void deleteNode(ListNode node) {
    // Copy next node's value into this node
    node.val = node.next.val;
    // Skip (delete) the next node
    node.next = node.next.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** | Only two operations regardless of list size. |
| **Space** | **O(1)** | No extra memory used. |

---

## Q4. Remove Duplicates from Sorted Linked List

**Difficulty:** `Basic` | **Data Structure:** Singly Linked List

### Problem

Given the head of a **sorted** linked list, delete all duplicate values so that each element appears only once. Return the modified list (still sorted).

### Examples

```
Input:  1 -> 1 -> 2 -> null
Output: 1 -> 2 -> null

Input:  1 -> 1 -> 2 -> 3 -> 3 -> null
Output: 1 -> 2 -> 3 -> null

Input:  1 -> 1 -> 1 -> null
Output: 1 -> null
```

### Approach: Sequential Scan

**Why this approach?**

Because the list is **sorted**, all duplicates are adjacent. We simply walk the list: if `curr.val == curr.next.val`, we skip `curr.next` by relinking. Otherwise, we advance `curr`.

No hash set is needed (unlike the unsorted variant in Q17) because sorting guarantees duplicates cluster together. This yields O(n) time with O(1) space -- optimal for the sorted case.

### Optimized Java Solution

```java
public ListNode deleteDuplicates(ListNode head) {
    var curr = head;

    while (curr != null && curr.next != null) {
        if (curr.val == curr.next.val) {
            curr.next = curr.next.next; // skip the duplicate
        } else {
            curr = curr.next;           // advance only when no duplicate
        }
    }
    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through all n nodes. Each node is visited at most once. |
| **Space** | **O(1)** | Only one pointer variable; modifications are in-place. |

---

## Q5. Merge Two Sorted Linked Lists

**Difficulty:** `Basic` | **Data Structure:** Singly Linked List

### Problem

Given the heads of two sorted linked lists, merge them into a single sorted linked list by splicing together the nodes of the two input lists. Return the head of the merged list.

### Examples

```
Input:  l1 = 1 -> 2 -> 4, l2 = 1 -> 3 -> 4
Output: 1 -> 1 -> 2 -> 3 -> 4 -> 4

Input:  l1 = null, l2 = 0 -> 3 -> 5
Output: 0 -> 3 -> 5

Input:  l1 = 2 -> 6, l2 = 1 -> 4 -> 7
Output: 1 -> 2 -> 4 -> 6 -> 7
```

### Approach: Dummy Head Technique

**Why this approach?**

We create a **dummy node** that acts as a placeholder head. A `tail` pointer builds the merged list by always appending the smaller of the two current nodes. This avoids special-casing "which list provides the first node" -- the dummy head absorbs that complexity.

At the end, we simply return `dummy.next` as the real head. Any remaining nodes from the non-exhausted list are appended directly.

This is cleaner than recursive merge (which uses O(n+m) stack space) and simpler than trying to merge without a dummy head (which requires extra `if` conditions for the head node).

### Optimized Java Solution

```java
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    var dummy = new ListNode(0);
    var tail = dummy;

    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            tail.next = l1;
            l1 = l1.next;
        } else {
            tail.next = l2;
            l2 = l2.next;
        }
        tail = tail.next;
    }

    // Attach the remaining non-null list
    tail.next = (l1 != null) ? l1 : l2;

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n + m)** | Each node from both lists is visited exactly once, where n and m are the lengths of the two lists. |
| **Space** | **O(1)** | We reuse existing nodes; only the dummy node and tail pointer are extra. |

---
---

## Q6. Detect a Cycle in a Linked List

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list, determine if the list contains a **cycle**. A cycle exists if some node's `next` pointer points back to a previously visited node. Return `true` if there is a cycle, `false` otherwise.

### Examples

```
Input:  3 -> 2 -> 0 -> -4 -> (back to node 2)
Output: true
Explanation: The tail connects back to the second node, forming a cycle.

Input:  1 -> 2 -> (back to node 1)
Output: true

Input:  1 -> null
Output: false
```

### Approach: Floyd's Cycle Detection (Tortoise and Hare)

**Why this approach?**

Two pointers move at different speeds: `slow` (1 step) and `fast` (2 steps). If there's a cycle, `fast` will eventually "lap" `slow` and they will meet. If there's no cycle, `fast` reaches `null`.

**Why not use a HashSet?** A HashSet of visited nodes works but costs O(n) space. Floyd's algorithm achieves the same result in O(1) space. In interviews, Floyd's is the expected answer because it demonstrates knowledge of this fundamental technique.

**Proof of correctness:** Once both pointers enter the cycle, the gap between them shrinks by 1 each step (fast gains 1 on slow). They must meet within one full rotation of the cycle.

### Optimized Java Solution

```java
public boolean hasCycle(ListNode head) {
    var slow = head;
    var fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            return true; // cycle detected
        }
    }
    return false; // fast reached end -- no cycle
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | In the worst case, slow traverses the entire list once before meeting fast inside the cycle. |
| **Space** | **O(1)** | Only two pointer variables used. |

---

## Q7. Find the Starting Node of a Cycle

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list that may contain a cycle, return the **node where the cycle begins**. If there is no cycle, return `null`.

### Examples

```
Input:  3 -> 2 -> 0 -> -4 -> (back to node 2)
Output: Node with value 2
Explanation: The cycle starts at node 2.

Input:  1 -> 2 -> (back to node 1)
Output: Node with value 1

Input:  1 -> 2 -> 3 -> null
Output: null
```

### Approach: Floyd's Algorithm Phase 2

**Why this approach?**

**Phase 1:** Use Floyd's slow/fast to detect the cycle (same as Q6). Let them meet at some node inside the cycle.

**Phase 2:** Move one pointer back to `head`. Now advance **both** pointers one step at a time. The node where they meet again is the **cycle start**.

**Mathematical proof:**
Let `F` = distance from head to cycle start, `C` = cycle length, and `a` = distance from cycle start to the meeting point. At the meeting point:
- `slow` has traveled `F + a` steps.
- `fast` has traveled `F + a + kC` steps (for some integer k).
- Since `fast` moves at 2x speed: `2(F + a) = F + a + kC`, so `F + a = kC`, thus `F = kC - a`.

This means if we start one pointer at `head` and another at the meeting point, both moving 1 step, they will meet at the cycle start after `F` steps.

### Optimized Java Solution

```java
public ListNode detectCycle(ListNode head) {
    var slow = head;
    var fast = head;

    // Phase 1: Detect cycle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            // Phase 2: Find cycle start
            var entry = head;
            while (entry != slow) {
                entry = entry.next;
                slow = slow.next;
            }
            return entry;
        }
    }
    return null; // no cycle
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Phase 1 takes O(n) steps. Phase 2 takes at most O(n) steps (the entry pointer walks at most the full list length). |
| **Space** | **O(1)** | Only pointer variables are used. |

---

## Q8. Intersection Point of Two Linked Lists

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the heads of two singly linked lists, find the **node** at which the two lists intersect. If they do not intersect, return `null`. The intersection is defined by reference (same node object), not by value.

### Examples

```
Input:  listA = 4 -> 1 -> 8 -> 4 -> 5
        listB = 5 -> 6 -> 1 -> 8 -> 4 -> 5
        (lists intersect at node with value 8)
Output: Node with value 8

Input:  listA = 2 -> 6 -> 4
        listB = 1 -> 5
Output: null (no intersection)

Input:  listA = 3 -> 3
        listB = 3 -> 3
        (intersect at the first node of both)
Output: Node with value 3
```

### Approach: Two-Pointer Length Equalization

**Why this approach?**

Two pointers start at `headA` and `headB`. When one pointer reaches the end, it redirects to the **other** list's head. After at most two passes, both pointers have traveled `lenA + lenB` steps, so they are "aligned" and will meet at the intersection node (or both reach `null` simultaneously if there is no intersection).

**Why not calculate lengths first?** That also works (compute lengths, advance the longer list's pointer by the difference, then walk together). The two-pointer redirect technique is more elegant: same O(n+m) time but avoids an explicit length-counting pass. Both are O(1) space.

### Optimized Java Solution

```java
public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    if (headA == null || headB == null) return null;

    var ptrA = headA;
    var ptrB = headB;

    // When ptrA reaches end, redirect to headB (and vice versa).
    // They will meet at the intersection or both become null.
    while (ptrA != ptrB) {
        ptrA = (ptrA != null) ? ptrA.next : headB;
        ptrB = (ptrB != null) ? ptrB.next : headA;
    }
    return ptrA; // intersection node or null
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n + m)** | Each pointer traverses at most both lists once (lenA + lenB steps). |
| **Space** | **O(1)** | Only two pointer variables. |

---

## Q9. Remove N-th Node from End of List

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list, remove the **n-th node from the end** of the list and return the head. Assume `n` is always valid (1 <= n <= list length).

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5, n = 2
Output: 1 -> 2 -> 3 -> 5
Explanation: The 2nd node from end is 4; remove it.

Input:  1 -> null, n = 1
Output: null

Input:  1 -> 2, n = 1
Output: 1
```

### Approach: Two-Pointer Gap

**Why this approach?**

We use a **dummy head** and two pointers spaced `n + 1` nodes apart. When the `fast` pointer reaches `null`, the `slow` pointer is exactly **one node before** the target. We then skip the target node.

The dummy head handles the edge case where the **first** node must be removed (e.g., single-element list with n=1).

**Why not two passes (count then remove)?** Two passes is simple, but the two-pointer technique achieves removal in a **single pass**, which is the expected interview optimization.

### Optimized Java Solution

```java
public ListNode removeNthFromEnd(ListNode head, int n) {
    var dummy = new ListNode(0);
    dummy.next = head;

    var fast = dummy;
    var slow = dummy;

    // Advance fast by (n + 1) steps so the gap is n nodes
    for (int i = 0; i <= n; i++) {
        fast = fast.next;
    }

    // Move both until fast reaches null
    while (fast != null) {
        fast = fast.next;
        slow = slow.next;
    }

    // slow is now one node before the target; skip the target
    slow.next = slow.next.next;

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through the list (fast pointer traverses all n nodes). |
| **Space** | **O(1)** | Only the dummy node and two pointers. |

---

## Q10. Check if Linked List is a Palindrome

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a singly linked list, determine if it is a **palindrome** (reads the same forwards and backwards).

### Examples

```
Input:  1 -> 2 -> 2 -> 1 -> null
Output: true

Input:  1 -> 2 -> 3 -> 2 -> 1 -> null
Output: true

Input:  1 -> 2 -> null
Output: false
```

### Approach: Reverse Second Half

**Why this approach?**

1. **Find the middle** using slow/fast pointers (Q2).
2. **Reverse** the second half in-place (Q1).
3. **Compare** the first half and reversed second half node by node.
4. (Optional) Restore the list by reversing the second half again.

**Why not use a stack or array?** Copying values into an array and checking palindrome is O(n) space. This approach achieves **O(1) space** by modifying the list in-place. In interviews, the O(1) space solution is the expected answer.

### Optimized Java Solution

```java
public boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;

    // Step 1: Find the middle
    var slow = head;
    var fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // Step 2: Reverse the second half (starting from slow.next)
    ListNode secondHalf = reverse(slow.next);

    // Step 3: Compare both halves
    var p1 = head;
    var p2 = secondHalf;
    boolean result = true;
    while (p2 != null) {
        if (p1.val != p2.val) {
            result = false;
            break;
        }
        p1 = p1.next;
        p2 = p2.next;
    }

    // Step 4 (optional): Restore the list
    slow.next = reverse(secondHalf);

    return result;
}

private ListNode reverse(ListNode head) {
    ListNode prev = null;
    var curr = head;
    while (curr != null) {
        var next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Finding middle O(n/2) + reversing O(n/2) + comparing O(n/2) = O(n). |
| **Space** | **O(1)** | All operations are in-place; only pointer variables used. |

---

## Q11. Add Two Numbers Represented as Linked Lists

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Two non-negative integers are represented as linked lists where each node contains a single digit. The digits are stored in **reverse order** (the 1s digit is at the head). Add the two numbers and return the sum as a linked list in the same reverse-order format.

### Examples

```
Input:  l1 = 2 -> 4 -> 3  (represents 342)
        l2 = 5 -> 6 -> 4  (represents 465)
Output: 7 -> 0 -> 8  (represents 807)
Explanation: 342 + 465 = 807

Input:  l1 = 0, l2 = 0
Output: 0

Input:  l1 = 9 -> 9 -> 9 -> 9  (9999)
        l2 = 1                   (1)
Output: 0 -> 0 -> 0 -> 0 -> 1  (10000)
```

### Approach: Digit-by-Digit Addition with Carry

**Why this approach?**

Since digits are in reverse order, the heads are the least-significant digits -- exactly the order we perform addition. We walk both lists simultaneously, summing corresponding digits plus any carry. A new node is created for each digit of the result.

We use a **dummy head** to simplify building the result list. The loop continues while either list has remaining nodes **or** there is a carry to propagate.

### Optimized Java Solution

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    var dummy = new ListNode(0);
    var curr = dummy;
    int carry = 0;

    while (l1 != null || l2 != null || carry != 0) {
        int sum = carry;
        if (l1 != null) {
            sum += l1.val;
            l1 = l1.next;
        }
        if (l2 != null) {
            sum += l2.val;
            l2 = l2.next;
        }

        carry = sum / 10;
        curr.next = new ListNode(sum % 10);
        curr = curr.next;
    }

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(max(n, m))** | We process each digit of both numbers once; the loop runs for the length of the longer list (plus at most one extra step for a final carry). |
| **Space** | **O(max(n, m))** | The result list has at most max(n, m) + 1 nodes. |

---

## Q12. Odd-Even Linked List

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a singly linked list, group all nodes at **odd indices** together followed by the nodes at **even indices**, and return the reordered list. The first node is index 1 (odd), the second is index 2 (even), etc. The relative order within each group must be preserved.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5 -> null
Output: 1 -> 3 -> 5 -> 2 -> 4 -> null
Explanation: Odd-indexed (1,3,5) then even-indexed (2,4).

Input:  2 -> 1 -> 3 -> 5 -> 6 -> 4 -> 7 -> null
Output: 2 -> 3 -> 6 -> 7 -> 1 -> 5 -> 4 -> null

Input:  1 -> 2 -> null
Output: 1 -> 2 -> null
```

### Approach: Odd/Even Index Grouping

**Why this approach?**

We maintain two separate chains: one for odd-indexed nodes and one for even-indexed nodes. We save the head of the even chain (`evenHead`). As we traverse, odd nodes link to the next odd, even nodes link to the next even. At the end, we connect the odd chain's tail to `evenHead`.

This is an **in-place** rearrangement with **O(1) space** -- no auxiliary data structures needed.

### Optimized Java Solution

```java
public ListNode oddEvenList(ListNode head) {
    if (head == null || head.next == null) return head;

    var odd = head;
    var even = head.next;
    var evenHead = even; // save start of even chain

    while (even != null && even.next != null) {
        odd.next = even.next;    // odd points to next odd
        odd = odd.next;
        even.next = odd.next;    // even points to next even
        even = even.next;
    }

    odd.next = evenHead; // connect odd tail to even head
    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through all n nodes. |
| **Space** | **O(1)** | Only pointer variables; rearrangement is in-place. |

---

## Q13. Partition List Around a Value

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list and a value `x`, partition the list such that all nodes with values **less than** `x` come before nodes with values **greater than or equal to** `x`. Preserve the original relative order of the nodes in each partition.

### Examples

```
Input:  1 -> 4 -> 3 -> 2 -> 5 -> 2, x = 3
Output: 1 -> 2 -> 2 -> 4 -> 3 -> 5
Explanation: Nodes < 3: [1,2,2]. Nodes >= 3: [4,3,5]. Concatenated.

Input:  2 -> 1, x = 2
Output: 1 -> 2

Input:  1 -> 1, x = 0
Output: 1 -> 1  (all nodes >= 0, order preserved)
```

### Approach: Two Dummy Lists

**Why this approach?**

Create two separate lists using dummy heads: `less` (for nodes < x) and `greater` (for nodes >= x). Traverse the original list, appending each node to the appropriate partition. Finally, connect the tail of `less` to the head of `greater`, and terminate `greater` with `null`.

This is cleaner and less error-prone than swapping nodes in-place. The dummy heads eliminate edge cases for empty partitions.

### Optimized Java Solution

```java
public ListNode partition(ListNode head, int x) {
    var lessHead = new ListNode(0);
    var greaterHead = new ListNode(0);
    var less = lessHead;
    var greater = greaterHead;

    while (head != null) {
        if (head.val < x) {
            less.next = head;
            less = less.next;
        } else {
            greater.next = head;
            greater = greater.next;
        }
        head = head.next;
    }

    greater.next = null;            // terminate the greater list
    less.next = greaterHead.next;   // connect less tail to greater head

    return lessHead.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through all n nodes. |
| **Space** | **O(1)** | Only dummy nodes and pointers; we reuse existing nodes. |

---

## Q14. Rotate Linked List by K Places

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list and an integer `k`, rotate the list to the **right** by `k` places.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5, k = 2
Output: 4 -> 5 -> 1 -> 2 -> 3
Explanation: Rotate right by 2: last 2 nodes move to front.

Input:  0 -> 1 -> 2, k = 4
Output: 2 -> 0 -> 1
Explanation: k = 4 is equivalent to k = 1 (4 % 3 = 1).

Input:  1 -> 2, k = 0
Output: 1 -> 2  (no rotation)
```

### Approach: Circular Connection

**Why this approach?**

1. **Compute length** `n` and find the tail.
2. `k = k % n` to handle k >= n.
3. If `k == 0`, return as-is.
4. **Form a circle** by connecting tail to head.
5. The new tail is at position `n - k` from the current head. Traverse there.
6. **Break the circle** at the new tail, and the next node becomes the new head.

This converts rotation into a simple "find the break point" problem. The circular list technique avoids complex pointer juggling.

### Optimized Java Solution

```java
public ListNode rotateRight(ListNode head, int k) {
    if (head == null || head.next == null || k == 0) return head;

    // Step 1: Compute length and find tail
    int length = 1;
    var tail = head;
    while (tail.next != null) {
        tail = tail.next;
        length++;
    }

    // Step 2: Normalize k
    k = k % length;
    if (k == 0) return head;

    // Step 3: Form circular list
    tail.next = head;

    // Step 4: Find new tail at position (length - k) from head
    var newTail = head;
    for (int i = 1; i < length - k; i++) {
        newTail = newTail.next;
    }

    // Step 5: Break the circle
    var newHead = newTail.next;
    newTail.next = null;

    return newHead;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | One pass to compute length, one partial pass to find the break point. Total <= 2n. |
| **Space** | **O(1)** | Only pointer variables. |

---

## Q15. Swap Nodes in Pairs

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given a linked list, swap every two adjacent nodes and return the modified list's head. You must swap the **nodes themselves**, not just their values.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> null
Output: 2 -> 1 -> 4 -> 3 -> null

Input:  1 -> 2 -> 3 -> null
Output: 2 -> 1 -> 3 -> null  (last node stays in place)

Input:  1 -> null
Output: 1 -> null  (single node, no swap)
```

### Approach: Iterative Pair Swap with Dummy Head

**Why this approach?**

A dummy head simplifies the first pair's swap. For each pair, we have a `prev` node pointing to the node before the pair. We swap by adjusting three links:
1. `prev.next` = second node
2. first.next = second.next (first now points past the pair)
3. second.next = first (second now points to first)

Then advance `prev` to `first` (which is now the second node in the swapped pair) for the next iteration.

**Walk-through with `1 -> 2 -> 3 -> 4`:**
```
dummy -> 1 -> 2 -> 3 -> 4
Step 1:  prev=dummy, first=1, second=2
         dummy -> 2 -> 1 -> 3 -> 4
Step 2:  prev=1, first=3, second=4
         dummy -> 2 -> 1 -> 4 -> 3
```

### Optimized Java Solution

```java
public ListNode swapPairs(ListNode head) {
    var dummy = new ListNode(0);
    dummy.next = head;
    var prev = dummy;

    while (prev.next != null && prev.next.next != null) {
        var first = prev.next;
        var second = prev.next.next;

        // Perform the swap
        first.next = second.next;
        second.next = first;
        prev.next = second;

        // Advance prev to the end of the swapped pair
        prev = first;
    }

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We process each pair once, visiting all n nodes. |
| **Space** | **O(1)** | Only pointer variables; swaps are in-place. |

---

## Q16. Sort a Linked List

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list, sort it in **ascending order** using O(n log n) time and O(1) space (ignoring recursion stack).

### Examples

```
Input:  4 -> 2 -> 1 -> 3 -> null
Output: 1 -> 2 -> 3 -> 4 -> null

Input:  -1 -> 5 -> 3 -> 4 -> 0 -> null
Output: -1 -> 0 -> 3 -> 4 -> 5 -> null

Input:  null
Output: null
```

### Approach: Merge Sort (Top-Down)

**Why this approach?**

Merge Sort is ideal for linked lists because:
1. **O(n log n)** time -- optimal comparison-based sort.
2. The merge step for linked lists is **O(1) space** (we re-link nodes rather than copying into arrays).
3. Unlike arrays, linked lists have no random access, so quicksort's partition step is awkward. Merge sort's split (via slow/fast) is natural.

**Steps:**
1. **Base case:** 0 or 1 nodes -- already sorted.
2. **Split** the list into two halves using slow/fast pointer.
3. **Recursively sort** each half.
4. **Merge** the two sorted halves using the dummy-head technique (Q5).

### Optimized Java Solution

```java
public ListNode sortList(ListNode head) {
    // Base case: 0 or 1 node
    if (head == null || head.next == null) return head;

    // Step 1: Split into two halves
    var mid = getMid(head);
    var rightHead = mid.next;
    mid.next = null; // cut the list

    // Step 2: Recursively sort each half
    var left = sortList(head);
    var right = sortList(rightHead);

    // Step 3: Merge sorted halves
    return merge(left, right);
}

// Returns the node BEFORE the midpoint (so we can cut)
private ListNode getMid(ListNode head) {
    var slow = head;
    var fast = head.next; // start fast one ahead to get left-middle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}

private ListNode merge(ListNode l1, ListNode l2) {
    var dummy = new ListNode(0);
    var tail = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            tail.next = l1;
            l1 = l1.next;
        } else {
            tail.next = l2;
            l2 = l2.next;
        }
        tail = tail.next;
    }
    tail.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n log n)** | The list is split in half log n times; each level of recursion does O(n) work for merging. |
| **Space** | **O(log n)** | Recursion stack depth is log n. The merge step itself is O(1) since we re-link existing nodes. |

---

## Q17. Remove Duplicates from Unsorted Linked List

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given the head of an **unsorted** linked list, remove all duplicate values so that each value appears only once. Preserve the **first occurrence** of each value.

### Examples

```
Input:  3 -> 2 -> 3 -> 1 -> 2 -> null
Output: 3 -> 2 -> 1 -> null

Input:  1 -> 1 -> 1 -> null
Output: 1 -> null

Input:  5 -> 3 -> 1 -> null
Output: 5 -> 3 -> 1 -> null  (no duplicates)
```

### Approach: HashSet Tracking

**Why this approach?**

Unlike Q4 (sorted list), duplicates are **not adjacent**, so we cannot just compare consecutive nodes. A `HashSet` tracks all values seen so far. For each node, if its value is in the set, we remove it; otherwise, we add the value to the set.

**Why not sort first?** Sorting takes O(n log n) and would change the relative order of first occurrences. The HashSet approach is O(n) time and preserves order.

**Follow-up (no extra space):** Without extra space, use two nested loops: for each node, scan ahead to remove all its duplicates. This is O(n^2) time, O(1) space.

### Optimized Java Solution

```java
public ListNode removeDuplicates(ListNode head) {
    var seen = new java.util.HashSet<Integer>();
    var dummy = new ListNode(0);
    dummy.next = head;
    var prev = dummy;
    var curr = head;

    while (curr != null) {
        if (seen.contains(curr.val)) {
            prev.next = curr.next; // skip duplicate
        } else {
            seen.add(curr.val);
            prev = curr;
        }
        curr = curr.next;
    }

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through n nodes; HashSet add/contains are O(1) amortized. |
| **Space** | **O(n)** | HashSet stores up to n distinct values. |

---

## Q18. Reorder List: L0->Ln->L1->Ln-1->...

**Difficulty:** `Medium` | **Data Structure:** Singly Linked List

### Problem

Given a singly linked list `L0 -> L1 -> ... -> Ln-1 -> Ln`, reorder it to `L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 -> ...`. You must modify the list **in-place** without altering node values.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> null
Output: 1 -> 4 -> 2 -> 3 -> null

Input:  1 -> 2 -> 3 -> 4 -> 5 -> null
Output: 1 -> 5 -> 2 -> 4 -> 3 -> null

Input:  1 -> 2 -> null
Output: 1 -> 2 -> null  (already reordered)
```

### Approach: Split + Reverse + Merge

**Why this approach?**

This problem combines three fundamental linked-list operations:

1. **Find the middle** (slow/fast pointer -- Q2) to split the list into two halves.
2. **Reverse** the second half (Q1) so the last node becomes the first.
3. **Interleave/merge** the two halves by alternating nodes.

This is O(1) space and O(n) time -- the most efficient approach possible. Using a stack or array to store the second half would cost O(n) extra space.

**Walk-through with `1 -> 2 -> 3 -> 4 -> 5`:**
```
Step 1 - Split:    first = [1, 2, 3], second = [4, 5]
Step 2 - Reverse:  second = [5, 4]
Step 3 - Merge:    1 -> 5 -> 2 -> 4 -> 3
```

### Optimized Java Solution

```java
public void reorderList(ListNode head) {
    if (head == null || head.next == null) return;

    // Step 1: Find middle
    var slow = head;
    var fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // Step 2: Reverse second half
    var secondHalf = reverse(slow.next);
    slow.next = null; // cut the first half

    // Step 3: Interleave merge
    var first = head;
    var second = secondHalf;
    while (second != null) {
        var tmp1 = first.next;
        var tmp2 = second.next;
        first.next = second;
        second.next = tmp1;
        first = tmp1;
        second = tmp2;
    }
}

private ListNode reverse(ListNode head) {
    ListNode prev = null;
    var curr = head;
    while (curr != null) {
        var next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Finding middle O(n/2) + reversing O(n/2) + merging O(n/2) = O(n). |
| **Space** | **O(1)** | All operations are in-place with only pointer variables. |

---
---

## Q19. Copy Linked List with Random Pointer

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

A linked list is given where each node has a `next` pointer and an additional **`random`** pointer that can point to **any** node in the list or `null`. Construct a **deep copy** of the list.

```java
class Node {
    int val;
    Node next;
    Node random;
    Node(int val) { this.val = val; this.next = null; this.random = null; }
}
```

### Examples

```
Input:  [[7,null],[13,0],[11,4],[10,2],[1,0]]
        (each pair is [val, index of random target])
Output: A deep copy with the same structure.

Input:  [[1,1],[2,1]]
        Node 0 random -> Node 1, Node 1 random -> Node 1
Output: Deep copy preserving the same random relationships.

Input:  [[3,null],[3,0],[3,null]]
Output: Deep copy with identical structure.
```

### Approach: Interleaving Clone (O(1) Space)

**Why this approach?**

**Phase 1 -- Interleave:** For each original node `A`, create a clone `A'` and insert it right after `A`:
`A -> A' -> B -> B' -> C -> C' -> ...`

**Phase 2 -- Set random pointers:** For each original node `A`, its clone `A'` is `A.next`. So `A'.random = A.random.next` (the clone of whatever A's random points to).

**Phase 3 -- Separate lists:** Restore the original list and extract the cloned list.

**Why not HashMap?** A `HashMap<Node, Node>` mapping originals to clones also works in O(n) time but costs **O(n) space**. The interleaving technique achieves the same result with **O(1) extra space** by cleverly embedding clones within the original list structure.

### Optimized Java Solution

```java
public Node copyRandomList(Node head) {
    if (head == null) return null;

    // Phase 1: Create interleaved clones
    var curr = head;
    while (curr != null) {
        var clone = new Node(curr.val);
        clone.next = curr.next;
        curr.next = clone;
        curr = clone.next;
    }

    // Phase 2: Assign random pointers for clones
    curr = head;
    while (curr != null) {
        if (curr.random != null) {
            curr.next.random = curr.random.next;
        }
        curr = curr.next.next;
    }

    // Phase 3: Separate the two lists
    var cloneHead = head.next;
    curr = head;
    var cloneCurr = cloneHead;
    while (curr != null) {
        curr.next = curr.next.next;
        cloneCurr.next = (cloneCurr.next != null) ? cloneCurr.next.next : null;
        curr = curr.next;
        cloneCurr = cloneCurr.next;
    }

    return cloneHead;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Three passes through the list, each O(n). |
| **Space** | **O(1)** | No extra data structures (the cloned nodes are the output, not extra space). |

---

## Q20. Flatten a Multilevel Sorted Linked List

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

Given multiple sorted linked lists where each node has a `next` pointer (horizontal) and a `down` pointer (vertical), flatten all lists into a single sorted linked list using the `down` pointer. Each node belongs to one horizontal list, and each vertical chain is sorted.

```java
class MultiNode {
    int val;
    MultiNode next;   // horizontal
    MultiNode down;   // vertical
    MultiNode(int val) { this.val = val; }
}
```

### Examples

```
Input:
  5 -> 10 -> 19 -> 28
  |     |     |     |
  7    20    22    35
  |           |     |
  8          50    40
  |                 |
  30               45

Output (down pointers): 5 -> 7 -> 8 -> 10 -> 19 -> 20 -> 22 -> 28 -> 30 -> 35 -> 40 -> 45 -> 50

Input:
  1 -> 5
  |    |
  2    6
  |
  3

Output: 1 -> 2 -> 3 -> 5 -> 6

Input:
  3
  |
  7
  |
  12

Output: 3 -> 7 -> 12  (single list, already flat)
```

### Approach: Recursive Merge from Right

**Why this approach?**

We use a right-to-left recursive strategy:
1. Start from the **rightmost** pair of lists.
2. **Merge** them into one sorted list (using down pointers).
3. Move left and merge the result with the next list.
4. Repeat until all lists are merged.

This is essentially a linked-list version of merging multiple sorted arrays. Processing right-to-left avoids creating intermediate lists that grow progressively larger from the left.

### Optimized Java Solution

```java
public MultiNode flatten(MultiNode head) {
    if (head == null || head.next == null) return head;

    // Recursively flatten from the right
    head.next = flatten(head.next);

    // Merge current list with the flattened right portion
    head = mergeSorted(head, head.next);

    return head;
}

private MultiNode mergeSorted(MultiNode a, MultiNode b) {
    if (a == null) return b;
    if (b == null) return a;

    MultiNode result;
    if (a.val <= b.val) {
        result = a;
        result.down = mergeSorted(a.down, b);
    } else {
        result = b;
        result.down = mergeSorted(a, b.down);
    }
    result.next = null; // flattened list only uses down pointers
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is visited exactly once across all merges, where n is the total number of nodes. |
| **Space** | **O(n)** | Recursion stack depth in the merge function can be O(n) in the worst case. An iterative merge reduces this to O(k) where k is the number of top-level lists. |

---

## Q21. Reverse Nodes in k-Group

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list, reverse the nodes of the list **k at a time** and return the modified list. If the number of remaining nodes is less than k, leave them in their original order. Only node links may be changed (not values).

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5, k = 2
Output: 2 -> 1 -> 4 -> 3 -> 5
Explanation: Reverse pairs [1,2] and [3,4]; 5 is leftover.

Input:  1 -> 2 -> 3 -> 4 -> 5, k = 3
Output: 3 -> 2 -> 1 -> 4 -> 5
Explanation: Reverse [1,2,3]; [4,5] has only 2 nodes, so they stay.

Input:  1 -> 2 -> 3 -> 4, k = 4
Output: 4 -> 3 -> 2 -> 1
```

### Approach: Group-Based Iterative Reversal

**Why this approach?**

For each group of k nodes:
1. **Check** if k nodes remain; if not, stop.
2. **Reverse** those k nodes in-place (using the 3-pointer technique from Q1).
3. **Connect** the reversed group to the previous group's tail and the next group's head.
4. Advance and repeat.

A dummy head simplifies connecting the first reversed group. We keep track of `groupPrev` (the node before the current group) to stitch groups together.

**Walk-through with `1 -> 2 -> 3 -> 4 -> 5`, k = 2:**
```
dummy -> 1 -> 2 -> 3 -> 4 -> 5
Group 1: reverse [1,2] -> dummy -> 2 -> 1 -> 3 -> 4 -> 5
Group 2: reverse [3,4] -> dummy -> 2 -> 1 -> 4 -> 3 -> 5
Group 3: only [5] remains, stop.
```

### Optimized Java Solution

```java
public ListNode reverseKGroup(ListNode head, int k) {
    var dummy = new ListNode(0);
    dummy.next = head;
    var groupPrev = dummy;

    while (true) {
        // Check if k nodes remain
        var kthNode = getKth(groupPrev, k);
        if (kthNode == null) break;

        var groupNext = kthNode.next; // save node after this group

        // Reverse the group
        var prev = groupNext; // reversed group's last node points to groupNext
        var curr = groupPrev.next;
        for (int i = 0; i < k; i++) {
            var next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        // Connect: groupPrev -> reversed group head
        var groupFirst = groupPrev.next; // this was the first node, now the last
        groupPrev.next = kthNode;        // kthNode is now the first after reversal

        // Advance groupPrev to the end of the reversed group
        groupPrev = groupFirst;
    }

    return dummy.next;
}

// Returns the k-th node from the given start node, or null if fewer than k nodes remain
private ListNode getKth(ListNode start, int k) {
    var curr = start;
    for (int i = 0; i < k; i++) {
        if (curr.next == null) return null;
        curr = curr.next;
    }
    return curr;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited at most twice: once to check if k nodes exist, once to reverse. |
| **Space** | **O(1)** | Only pointer variables; all reversals are in-place. |

---

## Q22. Merge K Sorted Linked Lists

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

Given an array of `k` sorted linked lists, merge all lists into **one sorted** linked list and return its head.

### Examples

```
Input:  lists = [
          1 -> 4 -> 5,
          1 -> 3 -> 4,
          2 -> 6
        ]
Output: 1 -> 1 -> 2 -> 3 -> 4 -> 4 -> 5 -> 6

Input:  lists = []
Output: null

Input:  lists = [null, 1 -> 2, null]
Output: 1 -> 2
```

### Approach: Min-Heap (Priority Queue)

**Why this approach?**

We use a min-heap (priority queue) of size k:
1. Insert the head of each non-null list into the heap.
2. Extract the minimum node, append it to the result.
3. If the extracted node has a next, push that next into the heap.
4. Repeat until the heap is empty.

**Why not merge lists pairwise?** Pairwise merge (divide-and-conquer) is also O(n log k) and uses O(1) space (ignoring recursion). The heap approach is more intuitive, equally efficient, and directly demonstrates priority queue mastery -- a commonly tested skill.

**Why not merge one-by-one?** Merging lists sequentially (merge list 1 with 2, then result with 3, etc.) is O(nk) because early nodes are compared repeatedly. The heap ensures each comparison involves at most k elements.

### Optimized Java Solution

```java
public ListNode mergeKLists(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;

    var minHeap = new java.util.PriorityQueue<ListNode>(
        java.util.Comparator.comparingInt(node -> node.val)
    );

    // Seed the heap with the head of each list
    for (var list : lists) {
        if (list != null) {
            minHeap.offer(list);
        }
    }

    var dummy = new ListNode(0);
    var tail = dummy;

    while (!minHeap.isEmpty()) {
        var smallest = minHeap.poll();
        tail.next = smallest;
        tail = tail.next;

        if (smallest.next != null) {
            minHeap.offer(smallest.next);
        }
    }

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n log k)** | Each of the n total nodes is inserted into and extracted from the heap once. Each heap operation is O(log k). |
| **Space** | **O(k)** | The heap holds at most k nodes at any time (one from each list). |

---

## Q23. Reverse Linked List II (Between Positions m and n)

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

Given the head of a singly linked list and two integers `left` and `right` (1-indexed, `left <= right`), reverse the nodes from position `left` to position `right` (inclusive) and return the modified list. Do it in **one pass**.

### Examples

```
Input:  1 -> 2 -> 3 -> 4 -> 5, left = 2, right = 4
Output: 1 -> 4 -> 3 -> 2 -> 5
Explanation: Sublist [2,3,4] is reversed to [4,3,2].

Input:  5, left = 1, right = 1
Output: 5  (single element, nothing to reverse)

Input:  1 -> 2 -> 3 -> 4 -> 5, left = 1, right = 5
Output: 5 -> 4 -> 3 -> 2 -> 1  (full reversal)
```

### Approach: Sublist Reversal in One Pass

**Why this approach?**

We use a dummy head and navigate to the node just **before** position `left` (call it `prev`). Then we repeatedly take the node after `curr` and move it to the front of the reversed section. This "pull-to-front" technique reverses the sublist in-place without needing to identify the end first.

**Walk-through with `1 -> 2 -> 3 -> 4 -> 5`, left=2, right=4:**
```
dummy -> 1 -> 2 -> 3 -> 4 -> 5     prev=1, curr=2
Step 1: Move 3 to front of sublist:
dummy -> 1 -> 3 -> 2 -> 4 -> 5
Step 2: Move 4 to front of sublist:
dummy -> 1 -> 4 -> 3 -> 2 -> 5
Done (right - left = 2 steps).
```

### Optimized Java Solution

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    var dummy = new ListNode(0);
    dummy.next = head;

    // Step 1: Navigate to the node before position 'left'
    var prev = dummy;
    for (int i = 1; i < left; i++) {
        prev = prev.next;
    }

    // Step 2: Reverse the sublist from 'left' to 'right'
    var curr = prev.next; // the node at position 'left'
    for (int i = 0; i < right - left; i++) {
        var nodeToMove = curr.next;     // the node to pull to the front
        curr.next = nodeToMove.next;    // skip nodeToMove
        nodeToMove.next = prev.next;    // nodeToMove points to current front
        prev.next = nodeToMove;         // prev now points to nodeToMove
    }

    return dummy.next;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | At most one full pass through the list. Navigating to `left` takes O(left) and reversing takes O(right - left). Total is O(right) <= O(n). |
| **Space** | **O(1)** | Only pointer variables; reversal is in-place. |

---

## Q24. Find the Length of a Cycle in Linked List + Entry Point Combined

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

Given the head of a linked list that may contain a cycle, determine:
1. Whether a cycle exists.
2. The **length** of the cycle (number of nodes in the loop).
3. The **entry node** where the cycle begins.

Return all three pieces of information. If there is no cycle, return `(false, 0, null)`.

### Examples

```
Input:  3 -> 2 -> 0 -> -4 -> (back to node 2)
Output: hasCycle = true, cycleLength = 3, entryNode = node(2)
Explanation: Cycle is 2 -> 0 -> -4 -> 2 (length 3), starting at node 2.

Input:  1 -> 2 -> 3 -> 4 -> (back to node 2)
Output: hasCycle = true, cycleLength = 3, entryNode = node(2)
Explanation: Cycle is 2 -> 3 -> 4 -> 2.

Input:  1 -> 2 -> 3 -> null
Output: hasCycle = false, cycleLength = 0, entryNode = null
```

### Approach: Floyd's Extended (Detect + Length + Entry)

**Why this approach?**

This combines the techniques from Q6 and Q7 with an additional step:

1. **Phase 1 (Detect):** Floyd's slow/fast cycle detection.
2. **Phase 2 (Length):** Once slow and fast meet inside the cycle, keep one pointer stationary and advance the other until they meet again. Count the steps -- that's the cycle length.
3. **Phase 3 (Entry):** Reset one pointer to head. Advance both one step at a time until they meet. That meeting point is the cycle entry (same mathematical proof as Q7).

All three phases are O(n) with O(1) space.

### Optimized Java Solution

```java
public class CycleInfo {
    boolean hasCycle;
    int cycleLength;
    ListNode entryNode;

    CycleInfo(boolean hasCycle, int cycleLength, ListNode entryNode) {
        this.hasCycle = hasCycle;
        this.cycleLength = cycleLength;
        this.entryNode = entryNode;
    }
}

public CycleInfo analyzeCycle(ListNode head) {
    var slow = head;
    var fast = head;

    // Phase 1: Detect cycle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            // Phase 2: Compute cycle length
            int length = 1;
            var runner = slow.next;
            while (runner != slow) {
                runner = runner.next;
                length++;
            }

            // Phase 3: Find entry point
            var entry = head;
            var pointer = slow;
            while (entry != pointer) {
                entry = entry.next;
                pointer = pointer.next;
            }

            return new CycleInfo(true, length, entry);
        }
    }

    return new CycleInfo(false, 0, null);
}
```

> **Cleaner version** (separating concerns for production code):

```java
public CycleInfo analyzeCycle(ListNode head) {
    // Phase 1: Detect meeting point
    ListNode meetingPoint = findMeetingPoint(head);
    if (meetingPoint == null) {
        return new CycleInfo(false, 0, null);
    }

    // Phase 2: Compute cycle length
    int length = computeCycleLength(meetingPoint);

    // Phase 3: Find entry node
    ListNode entry = findCycleEntry(head, meetingPoint);

    return new CycleInfo(true, length, entry);
}

private ListNode findMeetingPoint(ListNode head) {
    var slow = head;
    var fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return slow;
    }
    return null;
}

private int computeCycleLength(ListNode meetingPoint) {
    int length = 1;
    var curr = meetingPoint.next;
    while (curr != meetingPoint) {
        curr = curr.next;
        length++;
    }
    return length;
}

private ListNode findCycleEntry(ListNode head, ListNode meetingPoint) {
    var entry = head;
    var pointer = meetingPoint;
    while (entry != pointer) {
        entry = entry.next;
        pointer = pointer.next;
    }
    return entry;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Phase 1: O(n). Phase 2: O(cycle length) <= O(n). Phase 3: O(n). Total O(n). |
| **Space** | **O(1)** | Only pointer variables and a counter. |

---

## Q25. Design a Linked List

**Difficulty:** `Hard` | **Data Structure:** Singly Linked List

### Problem

Design and implement a singly linked list class from scratch. Support the following operations:

- `get(index)` -- Get the value of the index-th node (0-indexed). Return -1 if the index is invalid.
- `addAtHead(val)` -- Add a node with value `val` before the first element.
- `addAtTail(val)` -- Append a node with value `val` at the end.
- `addAtIndex(index, val)` -- Add a node with value `val` before the index-th node. If `index` equals the list length, append to end. If `index` > length, do nothing.
- `deleteAtIndex(index)` -- Delete the index-th node if the index is valid.

### Examples

```
Input:
  MyLinkedList list = new MyLinkedList();
  list.addAtHead(1);          // [1]
  list.addAtTail(3);          // [1, 3]
  list.addAtIndex(1, 2);      // [1, 2, 3]
  list.get(1);                // returns 2
  list.deleteAtIndex(1);      // [1, 3]
  list.get(1);                // returns 3

Input:
  MyLinkedList list = new MyLinkedList();
  list.addAtHead(7);          // [7]
  list.addAtHead(2);          // [2, 7]
  list.addAtHead(1);          // [1, 2, 7]
  list.addAtIndex(3, 0);      // [1, 2, 7, 0]
  list.deleteAtIndex(2);      // [1, 2, 0]
  list.addAtHead(6);          // [6, 1, 2, 0]
  list.addAtTail(4);          // [6, 1, 2, 0, 4]
  list.get(4);                // returns 4

Input:
  MyLinkedList list = new MyLinkedList();
  list.get(0);                // returns -1 (empty list)
  list.addAtIndex(1, 10);     // does nothing (index > length)
  list.addAtIndex(0, 10);     // [10]
  list.deleteAtIndex(0);      // []
  list.get(0);                // returns -1
```

### Approach: Sentinel (Dummy) Head Design

**Why this approach?**

Using a **sentinel/dummy head node** eliminates special-case handling for operations at the head of the list. Every real node is "after" the sentinel, so `addAtHead`, `deleteAtIndex(0)`, and other head operations use the same logic as any other position.

We also maintain a `size` counter to validate indices in O(1) instead of traversing to check bounds.

**Design decisions:**
- **Sentinel head:** Simplifies all insert/delete operations.
- **Size tracking:** Enables O(1) index validation.
- **0-indexed:** Matches the problem specification and typical array conventions.

### Optimized Java Solution

```java
class MyLinkedList {
    private final ListNode sentinel; // dummy head
    private int size;

    public MyLinkedList() {
        this.sentinel = new ListNode(0);
        this.size = 0;
    }

    /**
     * Get the value at the given index. Returns -1 if index is invalid.
     * Time: O(index)
     */
    public int get(int index) {
        if (index < 0 || index >= size) return -1;

        var curr = sentinel.next;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.val;
    }

    /**
     * Add a node with the given value at the head of the list.
     * Time: O(1)
     */
    public void addAtHead(int val) {
        addAtIndex(0, val);
    }

    /**
     * Append a node with the given value at the tail of the list.
     * Time: O(n)
     */
    public void addAtTail(int val) {
        addAtIndex(size, val);
    }

    /**
     * Add a node with the given value before the index-th node.
     * If index == size, appends to end.
     * If index > size, does nothing.
     * Time: O(index)
     */
    public void addAtIndex(int index, int val) {
        if (index < 0 || index > size) return;

        // Navigate to the node BEFORE the insertion point
        var prev = sentinel;
        for (int i = 0; i < index; i++) {
            prev = prev.next;
        }

        var newNode = new ListNode(val);
        newNode.next = prev.next;
        prev.next = newNode;
        size++;
    }

    /**
     * Delete the index-th node if the index is valid.
     * Time: O(index)
     */
    public void deleteAtIndex(int index) {
        if (index < 0 || index >= size) return;

        // Navigate to the node BEFORE the deletion point
        var prev = sentinel;
        for (int i = 0; i < index; i++) {
            prev = prev.next;
        }

        prev.next = prev.next.next;
        size--;
    }

    /**
     * Utility: Returns the current size of the list.
     */
    public int getSize() {
        return size;
    }

    /**
     * Utility: Returns a string representation for debugging.
     */
    @Override
    public String toString() {
        var sb = new StringBuilder("[");
        var curr = sentinel.next;
        while (curr != null) {
            sb.append(curr.val);
            if (curr.next != null) sb.append(", ");
            curr = curr.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **get(index)** | **O(index)** | Traverse from sentinel to the index-th node. |
| **addAtHead(val)** | **O(1)** | Insert directly after the sentinel. |
| **addAtTail(val)** | **O(n)** | Must traverse to the end to append. |
| **addAtIndex(index, val)** | **O(index)** | Traverse to the node before the insertion point. |
| **deleteAtIndex(index)** | **O(index)** | Traverse to the node before the deletion point. |
| **Space (overall)** | **O(n)** | One node per element, plus the sentinel and size counter. |

> **Optimization note:** To achieve O(1) `addAtTail`, maintain a `tail` pointer. This adds minimal complexity but doubles the bookkeeping for insert/delete operations that might affect the tail.
# DOUBLY LINKED LIST

> **Node Definition:** All solutions below use the `DoublyListNode` class defined in the common definitions section. Refer to it there.

---

## Mini-Index

| # | Title | Difficulty | Key Technique |
|---|---|---|---|
| Q26 | Insert a Node at the Beginning of DLL | Basic | Pointer rewiring |
| Q27 | Insert a Node at the End of DLL | Basic | Tail traversal |
| Q28 | Delete a Given Node from DLL | Basic | Neighbor linking |
| Q29 | Reverse a Doubly Linked List | Basic | Swap prev/next |
| Q30 | Insert at a Given Position in DLL | Basic | Indexed traversal |
| Q31 | Remove Duplicates from Sorted DLL | Medium | Sequential scan |
| Q32 | Find Pairs with Given Sum in Sorted DLL | Medium | Two pointers |
| Q33 | Merge Two Sorted Doubly Linked Lists | Medium | Merge technique |
| Q34 | Count Triplets with Given Sum in Sorted DLL | Medium | Fix one + two pointers |
| Q35 | Rotate DLL by N Nodes | Medium | Pointer rearrangement |
| Q36 | Delete All Occurrences of a Key in DLL | Medium | Traversal + deletion |
| Q37 | Sort a Doubly Linked List (Merge Sort) | Medium | Divide and conquer |
| Q38 | Group Odd and Even Indexed Nodes in DLL | Medium | Index-based partitioning |
| Q39 | Insert in Sorted Position in Sorted DLL | Medium | Sorted insertion |
| Q40 | Swap Kth Node from Beginning with Kth from End | Medium | Two-pass swap |
| Q41 | Convert a Binary Tree to DLL (Inorder) | Medium | Inorder traversal |
| Q42 | Reverse DLL in Groups of K | Medium | Group reversal |
| Q43 | Flatten a Multilevel Doubly Linked List | Medium | DFS / Stack |
| Q44 | LRU Cache using DLL + HashMap | Hard | DLL + HashMap |
| Q45 | Design Browser History using DLL | Hard | DLL navigation |
| Q46 | Max Stack using DLL + TreeMap | Hard | DLL + TreeMap |
| Q47 | All O(1) Data Structure | Hard | Bucket DLL + HashMap |
| Q48 | Design a Text Editor with Cursor | Hard | DLL cursor model |
| Q49 | LFU Cache | Hard | Frequency buckets + DLL |
| Q50 | Memory-Efficient DLL (XOR Linked List Concept) | Hard | XOR / simulation |

---

## Q26. Insert a Node at the Beginning of DLL

**Difficulty:** `Basic` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list and an integer value, insert a new node with the given value at the very beginning of the list. Return the new head.

### Examples

```
Input:  DLL: 10 <-> 20 <-> 30, val = 5
Output: 5 <-> 10 <-> 20 <-> 30
Explanation: Node with value 5 is inserted before the current head (10).

Input:  DLL: (empty), val = 1
Output: 1
Explanation: Inserting into an empty list makes the new node the head.

Input:  DLL: 7, val = 3
Output: 3 <-> 7
Explanation: New node 3 becomes the head; its next points to 7, and 7's prev points to 3.
```

### Approach: Direct Pointer Rewiring

**Why this approach?**

Inserting at the beginning of a DLL is an O(1) operation because we have direct access to the head. We simply create a new node, point its `next` to the current head, update the old head's `prev` to the new node, and return the new node as the head. No traversal is needed. This is the most efficient possible approach for head insertion.

### Optimized Java Solution

```java
public DoublyListNode insertAtBeginning(DoublyListNode head, int val) {
    DoublyListNode newNode = new DoublyListNode(val);
    newNode.next = head;
    if (head != null) {
        head.prev = newNode;
    }
    return newNode; // newNode is the new head
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** | Only a constant number of pointer operations regardless of list size. |
| **Space** | **O(1)** | Only one new node is allocated; no auxiliary data structures. |

---

## Q27. Insert a Node at the End of DLL

**Difficulty:** `Basic` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list and an integer value, insert a new node with the given value at the end of the list. Return the head of the list.

### Examples

```
Input:  DLL: 10 <-> 20 <-> 30, val = 40
Output: 10 <-> 20 <-> 30 <-> 40
Explanation: Node 40 is appended after the current tail (30).

Input:  DLL: (empty), val = 5
Output: 5
Explanation: The new node becomes both head and tail.

Input:  DLL: 1, val = 2
Output: 1 <-> 2
```

### Approach: Traverse to Tail, Then Link

**Why this approach?**

Without a tail pointer, we must traverse to the last node to insert at the end. This takes O(n) but is the standard approach for a singly-referenced head. Once at the tail, we perform O(1) pointer rewiring. An alternative is maintaining a separate tail reference, which makes this O(1) but requires extra bookkeeping on every operation. For a simple function, traversal is the cleanest solution.

### Optimized Java Solution

```java
public DoublyListNode insertAtEnd(DoublyListNode head, int val) {
    DoublyListNode newNode = new DoublyListNode(val);
    if (head == null) {
        return newNode;
    }
    DoublyListNode tail = head;
    while (tail.next != null) {
        tail = tail.next;
    }
    tail.next = newNode;
    newNode.prev = tail;
    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We traverse all n nodes to reach the tail. |
| **Space** | **O(1)** | Only one new node; no auxiliary storage. |

---

## Q28. Delete a Given Node from DLL (Given Node Reference)

**Difficulty:** `Basic` | **Data Structure:** Doubly Linked List

### Problem

Given a reference to a node in a doubly linked list, delete that node from the list. You are also given the head of the list. Return the (possibly new) head.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 4, delete node with val = 3
Output: 1 <-> 2 <-> 4
Explanation: Node 3's prev (2) now links to node 3's next (4), and vice versa.

Input:  DLL: 1 <-> 2 <-> 3, delete node with val = 1
Output: 2 <-> 3
Explanation: Deleting the head; new head is node 2, and its prev becomes null.

Input:  DLL: 5, delete node with val = 5
Output: (empty)
Explanation: Single-node list becomes empty after deletion.
```

### Approach: Bypass via Neighbor Linking

**Why this approach?**

The key advantage of a DLL over a singly linked list is that deletion given a node reference is O(1) - we can access both the predecessor and successor directly via `prev` and `next`. We simply connect the predecessor's `next` to the successor and the successor's `prev` to the predecessor. Edge cases: the node is the head (no predecessor) or the tail (no successor).

### Optimized Java Solution

```java
public DoublyListNode deleteNode(DoublyListNode head, DoublyListNode node) {
    if (head == null || node == null) return head;

    // If node is the head
    if (node == head) {
        head = node.next;
    }
    // Link previous node to next node
    if (node.prev != null) {
        node.prev.next = node.next;
    }
    // Link next node to previous node
    if (node.next != null) {
        node.next.prev = node.prev;
    }
    // Clean up deleted node
    node.prev = null;
    node.next = null;
    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** | Direct access to neighbors via prev/next pointers; no traversal needed. |
| **Space** | **O(1)** | No extra space; only pointer reassignments. |

---

## Q29. Reverse a Doubly Linked List

**Difficulty:** `Basic` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list, reverse it in-place and return the new head (which was formerly the tail).

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 4
Output: 4 <-> 3 <-> 2 <-> 1

Input:  DLL: 10 <-> 20
Output: 20 <-> 10

Input:  DLL: 5
Output: 5
Explanation: A single-node list is already reversed.
```

### Approach: Swap prev and next for Every Node

**Why this approach?**

In a DLL, every node has both `prev` and `next`. To reverse the list, we swap these two pointers for every node. After processing all nodes, the last node we visited becomes the new head. This is the most direct approach: a single pass, constant extra space, and it leverages the DLL's bidirectional nature. An alternative (building a new list) would waste O(n) space unnecessarily.

**Walk-through with DLL: 1 <-> 2 <-> 3**
- At node 1: swap prev/next -> prev=2, next=null. Move to prev (which was old next=2).
- At node 2: swap prev/next -> prev=3, next=1. Move to prev (which was old next=3).
- At node 3: swap prev/next -> prev=null, next=2. Move to prev (which was old next=null) -> null, so we stop.
- New head = node 3. Result: 3 <-> 2 <-> 1.

### Optimized Java Solution

```java
public DoublyListNode reverse(DoublyListNode head) {
    if (head == null) return null;

    DoublyListNode current = head;
    DoublyListNode temp = null;

    while (current != null) {
        // Swap prev and next
        temp = current.prev;
        current.prev = current.next;
        current.next = temp;
        // Move to next node (which is now current.prev after swap)
        current = current.prev;
    }
    // After the loop, temp.prev is null but temp points to old prev of last visited node.
    // The new head is the last node we processed.
    // temp holds the old prev of the last node, which after swap is the node before last.
    // Actually, when current becomes null, the last valid node was current's predecessor.
    // At that point, temp = last visited node's old prev (before swap), 
    // and the new head = temp (the previous pointer before we exited).
    // More precisely: when current == null, the new head is temp.prev if temp != null.
    // Let's simplify:
    if (temp != null) {
        head = temp.prev;
    }
    return head;
}
```

> **Cleaner alternative (tracks new head explicitly):**

```java
public DoublyListNode reverse(DoublyListNode head) {
    if (head == null) return null;

    DoublyListNode current = head;
    DoublyListNode newHead = null;

    while (current != null) {
        DoublyListNode prevNode = current.prev;
        current.prev = current.next;
        current.next = prevNode;
        newHead = current;                // This node might be the new head
        current = current.prev;           // Advance (prev is the original next)
    }
    return newHead;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through all n nodes. |
| **Space** | **O(1)** | Only a few temporary pointers; in-place reversal. |

---

## Q30. Insert at a Given Position in DLL

**Difficulty:** `Basic` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list, an integer value, and a 1-based position `pos`, insert a new node with the given value at that position. If `pos` is 1, insert at the beginning. If `pos` exceeds the list length, insert at the end. Return the head of the list.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 4, val = 3, pos = 3
Output: 1 <-> 2 <-> 3 <-> 4
Explanation: Node 3 is inserted at position 3 (between 2 and 4).

Input:  DLL: 10 <-> 20, val = 5, pos = 1
Output: 5 <-> 10 <-> 20
Explanation: Insert at the beginning.

Input:  DLL: 1 <-> 2, val = 3, pos = 10
Output: 1 <-> 2 <-> 3
Explanation: Position exceeds length; insert at the end.
```

### Approach: Traverse to Position, Then Insert

**Why this approach?**

We traverse to the node currently at position `pos - 1` (the node after which we insert), then rewire pointers. This straightforward traversal approach is optimal since we need to reach the correct position, which in the worst case is O(n). Handling edge cases (insert at head, insert at tail, insert in middle) ensures robustness.

### Optimized Java Solution

```java
public DoublyListNode insertAtPosition(DoublyListNode head, int val, int pos) {
    DoublyListNode newNode = new DoublyListNode(val);

    // Insert at beginning
    if (pos == 1 || head == null) {
        newNode.next = head;
        if (head != null) head.prev = newNode;
        return newNode;
    }

    // Traverse to the node at position (pos - 1)
    DoublyListNode current = head;
    for (int i = 1; i < pos - 1 && current.next != null; i++) {
        current = current.next;
    }

    // Insert newNode after current
    newNode.next = current.next;
    newNode.prev = current;
    if (current.next != null) {
        current.next.prev = newNode;
    }
    current.next = newNode;

    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | In the worst case we traverse to the end of the list (position = n). |
| **Space** | **O(1)** | Only one new node and a pointer for traversal. |

---

## Q31. Remove Duplicates from Sorted Doubly Linked List

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a **sorted** doubly linked list, remove all duplicate nodes so that each value appears only once. Return the head of the modified list.

### Examples

```
Input:  DLL: 1 <-> 1 <-> 2 <-> 3 <-> 3 <-> 3 <-> 4
Output: 1 <-> 2 <-> 3 <-> 4

Input:  DLL: 5 <-> 5 <-> 5
Output: 5

Input:  DLL: 1 <-> 2 <-> 3
Output: 1 <-> 2 <-> 3
Explanation: No duplicates exist; list is unchanged.
```

### Approach: Sequential Scan and Skip

**Why this approach?**

Since the list is sorted, all duplicates are adjacent. We traverse with a single pointer: if the current node's value equals its next node's value, we skip (delete) the next node. Otherwise, we advance. This gives us O(n) time with O(1) space. An alternative using a HashSet would also work but wastes O(n) space and doesn't exploit the sorted property.

### Optimized Java Solution

```java
public DoublyListNode removeDuplicates(DoublyListNode head) {
    if (head == null) return null;

    DoublyListNode current = head;
    while (current.next != null) {
        if (current.val == current.next.val) {
            // Skip the duplicate node
            DoublyListNode duplicate = current.next;
            current.next = duplicate.next;
            if (duplicate.next != null) {
                duplicate.next.prev = current;
            }
            duplicate.prev = null;
            duplicate.next = null;
        } else {
            current = current.next;
        }
    }
    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single traversal through the list; each node is visited at most once. |
| **Space** | **O(1)** | In-place removal; no additional data structures. |

---

## Q32. Find Pairs with Given Sum in Sorted DLL (Two-Pointer)

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a **sorted** doubly linked list and a target sum, find all pairs of nodes whose values add up to the target. Return the pairs as a list of integer arrays.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 4 <-> 5 <-> 6 <-> 8 <-> 9, target = 7
Output: [[1, 6], [2, 5]]

Input:  DLL: 1 <-> 2 <-> 3 <-> 4, target = 5
Output: [[1, 4], [2, 3]]

Input:  DLL: 1 <-> 5 <-> 6, target = 20
Output: []
Explanation: No pair adds up to 20.
```

### Approach: Two-Pointer (Left from Head, Right from Tail)

**Why this approach?**

The DLL is sorted and doubly linked, so we can use a two-pointer technique just like we would on a sorted array. Place one pointer at the head (smallest) and another at the tail (largest). If the sum is too small, advance the left pointer; if too large, retreat the right pointer. This runs in O(n) time. An alternative brute-force (checking all pairs) takes O(n^2). A HashSet approach takes O(n) time but O(n) space and doesn't leverage the sorted + DLL structure.

### Optimized Java Solution

```java
public List<int[]> findPairsWithSum(DoublyListNode head, int target) {
    List<int[]> result = new ArrayList<>();
    if (head == null) return result;

    // Find the tail
    DoublyListNode left = head;
    DoublyListNode right = head;
    while (right.next != null) {
        right = right.next;
    }

    // Two-pointer traversal
    while (left != right && left.prev != right) {
        int sum = left.val + right.val;
        if (sum == target) {
            result.add(new int[]{left.val, right.val});
            left = left.next;
            right = right.prev;
        } else if (sum < target) {
            left = left.next;
        } else {
            right = right.prev;
        }
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Finding the tail takes O(n), and the two-pointer scan also takes O(n). Total: O(n). |
| **Space** | **O(1)** | Excluding the output list, only two pointers are used. |

---

## Q33. Merge Two Sorted Doubly Linked Lists

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the heads of two sorted doubly linked lists, merge them into a single sorted doubly linked list. Return the head of the merged list. Do not create new nodes; rearrange existing ones.

### Examples

```
Input:  DLL1: 1 <-> 3 <-> 5, DLL2: 2 <-> 4 <-> 6
Output: 1 <-> 2 <-> 3 <-> 4 <-> 5 <-> 6

Input:  DLL1: 1 <-> 1 <-> 2, DLL2: 1 <-> 3
Output: 1 <-> 1 <-> 1 <-> 2 <-> 3

Input:  DLL1: (empty), DLL2: 10 <-> 20
Output: 10 <-> 20
```

### Approach: Iterative Merge (Analogous to Merge Sort's Merge Step)

**Why this approach?**

This is the classic merge of two sorted sequences. We use a dummy node to simplify head management, then compare elements from both lists one by one, appending the smaller one to the result. This is O(n + m) time and O(1) extra space (we reuse existing nodes). It mirrors the merge step in merge sort and is well-understood, efficient, and easy to reason about correctness.

### Optimized Java Solution

```java
public DoublyListNode mergeSorted(DoublyListNode head1, DoublyListNode head2) {
    if (head1 == null) return head2;
    if (head2 == null) return head1;

    DoublyListNode dummy = new DoublyListNode(0);
    DoublyListNode tail = dummy;

    while (head1 != null && head2 != null) {
        if (head1.val <= head2.val) {
            tail.next = head1;
            head1.prev = tail;
            head1 = head1.next;
        } else {
            tail.next = head2;
            head2.prev = tail;
            head2 = head2.next;
        }
        tail = tail.next;
    }

    // Append remaining nodes
    DoublyListNode remaining = (head1 != null) ? head1 : head2;
    tail.next = remaining;
    if (remaining != null) {
        remaining.prev = tail;
    }

    // Fix the head's prev pointer (it should be null, not dummy)
    DoublyListNode mergedHead = dummy.next;
    mergedHead.prev = null;
    return mergedHead;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n + m)** | Each node from both lists is visited exactly once. |
| **Space** | **O(1)** | Only pointer rearrangement; the dummy node is the only extra allocation. |

---

## Q34. Count Triplets with Given Sum in Sorted DLL

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a **sorted** doubly linked list and a target sum, count the number of triplets (three distinct nodes) whose values sum to the target.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 4 <-> 5 <-> 6 <-> 8 <-> 9, target = 17
Output: 2
Explanation: Triplets are (2, 6, 9) and (4, 5, 8).

Input:  DLL: 1 <-> 2 <-> 3 <-> 4, target = 6
Output: 1
Explanation: Only triplet is (1, 2, 3).

Input:  DLL: 1 <-> 2 <-> 3, target = 100
Output: 0
```

### Approach: Fix One Node + Two-Pointer for Remaining Pair

**Why this approach?**

This is the DLL equivalent of the classic 3Sum problem. For each node, we fix it and then use the two-pointer technique (from Q32) on the remaining list to find pairs that complete the target sum. Fixing one node costs O(n) iterations, and each two-pointer scan costs O(n), giving O(n^2) total. The brute-force approach of checking all triplets is O(n^3). Using a hash set could achieve O(n^2) but would use O(n) extra space. The two-pointer approach is optimal for a sorted DLL.

### Optimized Java Solution

```java
public int countTriplets(DoublyListNode head, int target) {
    if (head == null) return 0;

    // Find the tail
    DoublyListNode tail = head;
    while (tail.next != null) {
        tail = tail.next;
    }

    int count = 0;
    DoublyListNode current = head;

    // Fix each node and find pairs in the remaining list
    while (current != null) {
        int remaining = target - current.val;
        DoublyListNode left = current.next;
        DoublyListNode right = tail;

        while (left != null && right != null && left != right && right.next != left) {
            int pairSum = left.val + right.val;
            if (pairSum == remaining) {
                count++;
                left = left.next;
                right = right.prev;
            } else if (pairSum < remaining) {
                left = left.next;
            } else {
                right = right.prev;
            }
        }
        current = current.next;
    }
    return count;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n^2)** | For each of n nodes, a two-pointer scan takes O(n). |
| **Space** | **O(1)** | Only pointer variables; no extra data structures. |

---

## Q35. Rotate DLL by N Nodes

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list and an integer `k`, rotate the list to the left by `k` positions. The first `k` nodes move to the end of the list.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 4 <-> 5, k = 2
Output: 3 <-> 4 <-> 5 <-> 1 <-> 2
Explanation: First 2 nodes (1, 2) are moved to the end.

Input:  DLL: 10 <-> 20 <-> 30, k = 3
Output: 10 <-> 20 <-> 30
Explanation: Rotating by the list length results in the same list.

Input:  DLL: 1 <-> 2 <-> 3 <-> 4, k = 6
Output: 3 <-> 4 <-> 1 <-> 2
Explanation: k = 6, length = 4, effective rotation = 6 % 4 = 2.
```

### Approach: Find Split Point, Rearrange Pointers

**Why this approach?**

We first compute the list length to handle `k >= length` via modular arithmetic. Then we traverse to the kth node, break the list there, and reconnect: the (k+1)th node becomes the new head, and the old tail connects to the old head. This is O(n) time and O(1) space. The alternative of rotating one position at a time k times would be O(n*k), which is far worse.

### Optimized Java Solution

```java
public DoublyListNode rotateDLL(DoublyListNode head, int k) {
    if (head == null || head.next == null || k == 0) return head;

    // Find length and tail
    int length = 1;
    DoublyListNode tail = head;
    while (tail.next != null) {
        tail = tail.next;
        length++;
    }

    k = k % length;
    if (k == 0) return head;

    // Traverse to the kth node (the new tail of rotated list)
    DoublyListNode current = head;
    for (int i = 1; i < k; i++) {
        current = current.next;
    }

    // current is the kth node; current.next is the new head
    DoublyListNode newHead = current.next;
    newHead.prev = null;

    current.next = null;

    // Connect old tail to old head
    tail.next = head;
    head.prev = tail;

    return newHead;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Two passes at most: one to find length/tail, one to find split point. |
| **Space** | **O(1)** | Only a few pointers; purely in-place rearrangement. |

---

## Q36. Delete All Occurrences of a Key in DLL

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list and an integer `key`, delete **all** nodes whose value equals `key`. Return the head of the modified list.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 2 <-> 4 <-> 2, key = 2
Output: 1 <-> 3 <-> 4

Input:  DLL: 5 <-> 5 <-> 5, key = 5
Output: (empty)

Input:  DLL: 1 <-> 2 <-> 3, key = 4
Output: 1 <-> 2 <-> 3
Explanation: Key not found; list is unchanged.
```

### Approach: Single-Pass Traversal with Deletion

**Why this approach?**

We traverse the list once. For each node matching the key, we use the DLL's O(1) deletion capability (rewire prev and next neighbors). We must carefully handle head deletion (update head pointer) and consecutive deletions. This is optimal: O(n) time, O(1) space, and handles all edge cases in a single clean pass.

### Optimized Java Solution

```java
public DoublyListNode deleteAllOccurrences(DoublyListNode head, int key) {
    DoublyListNode current = head;

    while (current != null) {
        if (current.val == key) {
            DoublyListNode nextNode = current.next;

            // Update head if we're deleting it
            if (current == head) {
                head = nextNode;
            }
            // Bypass current node
            if (current.prev != null) {
                current.prev.next = current.next;
            }
            if (current.next != null) {
                current.next.prev = current.prev;
            }
            current = nextNode;
        } else {
            current = current.next;
        }
    }
    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is visited exactly once. |
| **Space** | **O(1)** | In-place deletions; no extra data structures. |

---

## Q37. Sort a Doubly Linked List (Merge Sort)

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of an unsorted doubly linked list, sort it in ascending order using an efficient algorithm. Return the head of the sorted list.

### Examples

```
Input:  DLL: 4 <-> 2 <-> 8 <-> 1 <-> 5
Output: 1 <-> 2 <-> 4 <-> 5 <-> 8

Input:  DLL: 3 <-> 1
Output: 1 <-> 3

Input:  DLL: 1
Output: 1
```

### Approach: Merge Sort (Top-Down)

**Why this approach?**

Merge sort is the preferred sorting algorithm for linked lists because:
1. It achieves O(n log n) worst-case time complexity.
2. It works naturally with linked lists - the merge step only requires pointer manipulation, not random access.
3. Unlike quicksort (which degrades to O(n^2) on linked lists with bad pivots), merge sort guarantees O(n log n).
4. It can be implemented with O(log n) stack space (recursion depth).

The algorithm: find the middle via slow/fast pointers, split into two halves, recursively sort each, and merge.

### Optimized Java Solution

```java
public DoublyListNode sortDLL(DoublyListNode head) {
    if (head == null || head.next == null) return head;

    // Split the list into two halves
    DoublyListNode mid = getMiddle(head);
    DoublyListNode secondHalf = mid.next;
    mid.next = null;
    if (secondHalf != null) {
        secondHalf.prev = null;
    }

    // Recursively sort both halves
    DoublyListNode left = sortDLL(head);
    DoublyListNode right = sortDLL(secondHalf);

    // Merge sorted halves
    return mergeSorted(left, right);
}

private DoublyListNode getMiddle(DoublyListNode head) {
    DoublyListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}

private DoublyListNode mergeSorted(DoublyListNode h1, DoublyListNode h2) {
    if (h1 == null) return h2;
    if (h2 == null) return h1;

    DoublyListNode dummy = new DoublyListNode(0);
    DoublyListNode tail = dummy;

    while (h1 != null && h2 != null) {
        if (h1.val <= h2.val) {
            tail.next = h1;
            h1.prev = tail;
            h1 = h1.next;
        } else {
            tail.next = h2;
            h2.prev = tail;
            h2 = h2.next;
        }
        tail = tail.next;
    }

    tail.next = (h1 != null) ? h1 : h2;
    if (tail.next != null) {
        tail.next.prev = tail;
    }

    DoublyListNode result = dummy.next;
    result.prev = null;
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n log n)** | log n levels of recursion; each level does O(n) work for merging. |
| **Space** | **O(log n)** | Recursion stack depth is O(log n); merge is done in-place. |

---

## Q38. Group Odd and Even Indexed Nodes in DLL

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list, rearrange it so that all **odd-indexed** nodes come first (1st, 3rd, 5th, ...) followed by all **even-indexed** nodes (2nd, 4th, 6th, ...). Indices are 1-based. Maintain the relative order within each group. Return the head.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 4 <-> 5
Output: 1 <-> 3 <-> 5 <-> 2 <-> 4
Explanation: Odd-indexed (1,3,5) then even-indexed (2,4).

Input:  DLL: 10 <-> 20 <-> 30 <-> 40
Output: 10 <-> 30 <-> 20 <-> 40

Input:  DLL: 7
Output: 7
```

### Approach: Separate into Two Lists, Then Concatenate

**Why this approach?**

We traverse the list once, placing alternating nodes into an "odd" list and an "even" list. After traversal, we connect the tail of the odd list to the head of the even list and fix all prev pointers. This is clean, O(n) time, O(1) space, and maintains relative order. Attempting to do in-place swaps would be more complex and error-prone.

### Optimized Java Solution

```java
public DoublyListNode oddEvenDLL(DoublyListNode head) {
    if (head == null || head.next == null) return head;

    DoublyListNode odd = head;
    DoublyListNode even = head.next;
    DoublyListNode evenHead = even;

    while (even != null && even.next != null) {
        odd.next = even.next;
        even.next.prev = odd;
        odd = odd.next;

        even.next = odd.next;
        if (odd.next != null) {
            odd.next.prev = even;
        }
        even = even.next;
    }

    // Connect odd list's tail to even list's head
    odd.next = evenHead;
    evenHead.prev = odd;

    // Even list's tail should point to null
    if (even != null) {
        even.next = null;
    }

    // Head's prev should be null
    head.prev = null;

    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single traversal through the list. |
| **Space** | **O(1)** | Only a few pointers used; rearrangement is in-place. |

---

## Q39. Insert in Sorted Position in Sorted DLL

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a **sorted** (ascending) doubly linked list and an integer value, insert a new node with the given value at the correct position to maintain sorted order. Return the head.

### Examples

```
Input:  DLL: 1 <-> 3 <-> 5 <-> 7, val = 4
Output: 1 <-> 3 <-> 4 <-> 5 <-> 7

Input:  DLL: 10 <-> 20 <-> 30, val = 5
Output: 5 <-> 10 <-> 20 <-> 30
Explanation: 5 is smaller than all elements; insert at head.

Input:  DLL: 1 <-> 2, val = 10
Output: 1 <-> 2 <-> 10
Explanation: 10 is larger than all elements; insert at end.
```

### Approach: Traverse to Correct Position, Then Insert

**Why this approach?**

Since the list is sorted, we traverse from the head until we find the first node with a value greater than or equal to the new value. We insert before that node. Edge cases include inserting at the head (new value is smallest) and at the tail (new value is largest). This is the natural O(n) approach and cannot be improved without additional data structures (e.g., skip list).

### Optimized Java Solution

```java
public DoublyListNode insertSorted(DoublyListNode head, int val) {
    DoublyListNode newNode = new DoublyListNode(val);

    // Empty list
    if (head == null) return newNode;

    // Insert before head
    if (val <= head.val) {
        newNode.next = head;
        head.prev = newNode;
        return newNode;
    }

    // Find the node just before the insertion point
    DoublyListNode current = head;
    while (current.next != null && current.next.val < val) {
        current = current.next;
    }

    // Insert after current
    newNode.next = current.next;
    newNode.prev = current;
    if (current.next != null) {
        current.next.prev = newNode;
    }
    current.next = newNode;

    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Worst case: insert at end requires full traversal. |
| **Space** | **O(1)** | Only one new node and a traversal pointer. |

---

## Q40. Swap Kth Node from Beginning with Kth from End

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list and an integer `k`, swap the **values** of the kth node from the beginning and the kth node from the end (1-indexed). Return the head.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 4 <-> 5, k = 2
Output: 1 <-> 4 <-> 3 <-> 2 <-> 5
Explanation: 2nd from beginning (2) and 2nd from end (4) are swapped.

Input:  DLL: 10 <-> 20 <-> 30 <-> 40, k = 1
Output: 40 <-> 20 <-> 30 <-> 10
Explanation: 1st from beginning (10) and 1st from end (40) are swapped.

Input:  DLL: 1 <-> 2 <-> 3, k = 2
Output: 1 <-> 2 <-> 3
Explanation: The 2nd from beginning and 2nd from end are the same node (middle); no swap needed.
```

### Approach: Two-Pass — Find Both Nodes, Swap Values

**Why this approach?**

First pass: find the list length. Then, the kth node from end is the (length - k + 1)th from the beginning. Second pass: traverse to both positions, swap values. Swapping values (not nodes) is much simpler and avoids complex pointer rewiring. Since we only swap `val`, the DLL structure remains intact. Time is O(n) for two passes; this is optimal.

### Optimized Java Solution

```java
public DoublyListNode swapKthNodes(DoublyListNode head, int k) {
    if (head == null) return null;

    // Find length
    int length = 0;
    DoublyListNode current = head;
    while (current != null) {
        length++;
        current = current.next;
    }

    if (k > length) return head;

    // kth from beginning = position k
    // kth from end = position (length - k + 1)
    int posFromEnd = length - k + 1;

    if (k == posFromEnd) return head; // Same node

    // Find kth node from beginning
    DoublyListNode nodeFromBegin = head;
    for (int i = 1; i < k; i++) {
        nodeFromBegin = nodeFromBegin.next;
    }

    // Find kth node from end
    DoublyListNode nodeFromEnd = head;
    for (int i = 1; i < posFromEnd; i++) {
        nodeFromEnd = nodeFromEnd.next;
    }

    // Swap values
    int temp = nodeFromBegin.val;
    nodeFromBegin.val = nodeFromEnd.val;
    nodeFromEnd.val = temp;

    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Two traversals of the list (one for length, one for each target node). |
| **Space** | **O(1)** | Only a few variables; value swap is in-place. |

---

## Q41. Convert a Binary Tree to Doubly Linked List (Inorder)

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List / Binary Tree

### Problem

Given the root of a binary tree, convert it to a doubly linked list **in-place** using inorder traversal. The `left` pointer of each tree node should act as `prev`, and the `right` pointer should act as `next` in the DLL. Return the head of the DLL (the leftmost node of the tree).

> **Note:** Since this problem involves tree nodes (with `left` and `right`), we define a compatible node:

```java
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
```
After conversion, `left` acts as `prev` and `right` acts as `next`.

### Examples

```
Input:      4
           / \
          2   5
         / \
        1   3
Output: 1 <-> 2 <-> 3 <-> 4 <-> 5
Explanation: Inorder traversal is [1, 2, 3, 4, 5].

Input:      1
             \
              2
               \
                3
Output: 1 <-> 2 <-> 3

Input:  (single node) 5
Output: 5
```

### Approach: Inorder Traversal with a Global Previous Pointer

**Why this approach?**

Inorder traversal naturally visits nodes in sorted order for a BST (and in inorder for any binary tree). We maintain a `prev` pointer that tracks the last node visited. For each node, we set `current.left = prev` and `prev.right = current`, effectively building the DLL link by link. This is O(n) time with O(h) stack space (recursion depth). An iterative approach using an explicit stack would also work but is more complex.

### Optimized Java Solution

```java
public class TreeToDLL {
    private TreeNode prev = null;
    private TreeNode head = null;

    public TreeNode convertToDLL(TreeNode root) {
        prev = null;
        head = null;
        inorder(root);
        return head;
    }

    private void inorder(TreeNode node) {
        if (node == null) return;

        // Recurse left subtree
        inorder(node.left);

        // Process current node
        if (prev == null) {
            // This is the leftmost (smallest) node -> head of DLL
            head = node;
        } else {
            // Link previous node and current node
            prev.right = node;
            node.left = prev;
        }
        prev = node;

        // Recurse right subtree
        inorder(node.right);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is visited exactly once during inorder traversal. |
| **Space** | **O(h)** | Recursion stack uses O(h) space where h is the tree height. O(log n) for balanced, O(n) for skewed. |

---

## Q42. Reverse DLL in Groups of K

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

Given the head of a doubly linked list and an integer `k`, reverse the nodes in groups of `k`. If the last group has fewer than `k` nodes, reverse them as well. Return the new head.

### Examples

```
Input:  DLL: 1 <-> 2 <-> 3 <-> 4 <-> 5 <-> 6, k = 3
Output: 3 <-> 2 <-> 1 <-> 6 <-> 5 <-> 4
Explanation: Group 1 [1,2,3] reversed to [3,2,1]. Group 2 [4,5,6] reversed to [6,5,4].

Input:  DLL: 1 <-> 2 <-> 3 <-> 4 <-> 5, k = 2
Output: 2 <-> 1 <-> 4 <-> 3 <-> 5
Explanation: Groups: [1,2]->[2,1], [3,4]->[4,3], [5]->[5].

Input:  DLL: 1 <-> 2, k = 3
Output: 2 <-> 1
Explanation: Only one group with fewer than k nodes; still reversed.
```

### Approach: Iterative Group Reversal

**Why this approach?**

We process the list in chunks of k nodes. For each chunk, we reverse the internal pointers (swap prev/next for each node in the group), then connect the reversed group to the previous group's tail and the next group's head. Using an iterative approach avoids recursion overhead and gives clear control over group boundaries. This is O(n) time and O(1) space.

### Optimized Java Solution

```java
public DoublyListNode reverseInGroups(DoublyListNode head, int k) {
    if (head == null || k <= 1) return head;

    DoublyListNode current = head;
    DoublyListNode newHead = null;
    DoublyListNode prevGroupTail = null;

    while (current != null) {
        DoublyListNode groupStart = current;
        DoublyListNode prev = null;
        int count = 0;

        // Reverse k nodes (or fewer if list ends)
        while (current != null && count < k) {
            DoublyListNode nextNode = current.next;
            current.next = prev;
            current.prev = nextNode;
            prev = current;
            current = nextNode;
            count++;
        }

        // prev is now the head of the reversed group
        // groupStart is now the tail of the reversed group
        if (newHead == null) {
            newHead = prev;
            newHead.prev = null;
        }

        // Connect previous group's tail to this group's head
        if (prevGroupTail != null) {
            prevGroupTail.next = prev;
            prev.prev = prevGroupTail;
        }

        // groupStart is the tail of this reversed group
        prevGroupTail = groupStart;
    }

    // Ensure the last node's next is null
    if (prevGroupTail != null) {
        prevGroupTail.next = null;
    }

    return newHead;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once across all groups. |
| **Space** | **O(1)** | Only pointer variables; no recursion or auxiliary storage. |

---

## Q43. Flatten a Multilevel Doubly Linked List

**Difficulty:** `Medium` | **Data Structure:** Doubly Linked List

### Problem

You are given a doubly linked list where each node may have a `child` pointer in addition to `prev` and `next`. This child pointer may point to a separate doubly linked list (which may itself have children). Flatten the list so that all nodes appear in a single-level doubly linked list. The child lists should be inserted between the node and its `next` node (depth-first order).

```java
class MultiLevelNode {
    int val;
    MultiLevelNode prev, next, child;
    MultiLevelNode(int val) { this.val = val; }
}
```

### Examples

```
Input:  1 <-> 2 <-> 3 <-> 4 <-> 5
             |
             6 <-> 7 <-> 8
                   |
                   9 <-> 10
Output: 1 <-> 2 <-> 6 <-> 7 <-> 9 <-> 10 <-> 8 <-> 3 <-> 4 <-> 5
Explanation: Node 2's child list [6,7,8] is inserted after 2. Node 7's child [9,10] is inserted after 7.

Input:  1 <-> 2
        |
        3
Output: 1 <-> 3 <-> 2

Input:  1
Output: 1
```

### Approach: Iterative with Stack (DFS Simulation)

**Why this approach?**

We simulate depth-first traversal using a stack. When we encounter a node with a child, we push the current node's `next` onto the stack (to return to later), and set the child as the next node. When we reach a dead end (no next and no child), we pop from the stack to resume. This is equivalent to recursive DFS but uses O(d) explicit stack space (where d is the maximum depth), avoiding potential stack overflow for very deep structures.

Alternatively, a purely iterative approach without a stack traverses the list and, for each child, finds the child-chain's tail, then splices the child chain in. Both are O(n). The stack approach is more intuitive for depth-first flattening.

### Optimized Java Solution

```java
public MultiLevelNode flatten(MultiLevelNode head) {
    if (head == null) return null;

    Deque<MultiLevelNode> stack = new ArrayDeque<>();
    MultiLevelNode current = head;

    while (current != null) {
        if (current.child != null) {
            // Save next for later
            if (current.next != null) {
                stack.push(current.next);
            }
            // Link child as next
            current.next = current.child;
            current.child.prev = current;
            current.child = null;
        }

        if (current.next == null && !stack.isEmpty()) {
            // Resume from the saved next
            MultiLevelNode saved = stack.pop();
            current.next = saved;
            saved.prev = current;
        }

        current = current.next;
    }

    return head;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once. |
| **Space** | **O(d)** | Stack holds at most d entries where d is the maximum nesting depth. |

---

## Q44. LRU Cache using DLL + HashMap

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List + HashMap

### Problem

Design a data structure that implements a **Least Recently Used (LRU)** cache with the following operations:

- `LRUCache(int capacity)` — Initialize the cache with positive size capacity.
- `int get(int key)` — Return the value of the key if it exists, otherwise return -1. Marks the key as recently used.
- `void put(int key, int value)` — Update the value of the key if it exists, or add the key-value pair. If the number of keys exceeds the capacity, evict the least recently used key.

Both operations must run in **O(1)** time.

### Examples

```
Input:
  LRUCache cache = new LRUCache(2);
  cache.put(1, 1);    // cache: {1=1}
  cache.put(2, 2);    // cache: {1=1, 2=2}
  cache.get(1);       // returns 1, cache: {2=2, 1=1} (1 is now most recent)
  cache.put(3, 3);    // evicts key 2, cache: {1=1, 3=3}
  cache.get(2);       // returns -1 (not found)
  cache.put(4, 4);    // evicts key 1, cache: {3=3, 4=4}
  cache.get(1);       // returns -1
  cache.get(3);       // returns 3
  cache.get(4);       // returns 4
```

### Approach: Doubly Linked List + HashMap

**Why this approach?**

We need O(1) for both `get` and `put`. A HashMap provides O(1) key lookup, but doesn't track ordering. A DLL provides O(1) insertion and deletion (given a node reference) and maintains insertion/access order. By combining them:

- **HashMap<Integer, DLLNode>** maps key -> node in the DLL.
- **DLL** maintains access order: most recently used at head, least recently used at tail.
- On `get`: look up in map, move node to head.
- On `put`: if key exists, update and move to head. If new, insert at head. If over capacity, remove from tail (evict LRU) and remove from map.

Using sentinel head/tail nodes eliminates null checks and simplifies edge cases.

**Walk-through with capacity=2:**
```
put(1,1): DLL: [1], Map: {1->node1}
put(2,2): DLL: [2, 1], Map: {1->node1, 2->node2}
get(1):   Move node1 to head. DLL: [1, 2]. Return 1.
put(3,3): Capacity full. Evict tail (node2, key=2). Add node3 at head.
          DLL: [3, 1], Map: {1->node1, 3->node3}
get(2):   Not in map. Return -1.
```

### Optimized Java Solution

```java
public class LRUCache {

    private static class DLLNode {
        int key, value;
        DLLNode prev, next;

        DLLNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, DLLNode> map;
    private final DLLNode head; // Sentinel head (most recent side)
    private final DLLNode tail; // Sentinel tail (least recent side)

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        // Initialize sentinel nodes
        this.head = new DLLNode(-1, -1);
        this.tail = new DLLNode(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        DLLNode node = map.get(key);
        if (node == null) return -1;
        // Move to front (most recently used)
        removeNode(node);
        addToFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        DLLNode existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            removeNode(existing);
            addToFront(existing);
        } else {
            if (map.size() == capacity) {
                // Evict LRU (node just before tail sentinel)
                DLLNode lru = tail.prev;
                removeNode(lru);
                map.remove(lru.key);
            }
            DLLNode newNode = new DLLNode(key, value);
            addToFront(newNode);
            map.put(key, newNode);
        }
    }

    private void addToFront(DLLNode node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(DLLNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1) per operation** | HashMap lookup, DLL insertion, and DLL removal are all O(1). |
| **Space** | **O(capacity)** | At most `capacity` entries in the map and DLL. |

---

## Q45. Design Browser History using DLL

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List

### Problem

Design a browser history manager with the following operations:

- `BrowserHistory(String homepage)` — Initialize with the homepage.
- `void visit(String url)` — Visit a URL from the current page. Clears all forward history.
- `String back(int steps)` — Move back at most `steps` pages. Return the current URL.
- `String forward(int steps)` — Move forward at most `steps` pages. Return the current URL.

### Examples

```
Input:
  BrowserHistory bh = new BrowserHistory("google.com");
  bh.visit("facebook.com");    // google -> facebook
  bh.visit("youtube.com");     // google -> facebook -> youtube
  bh.back(1);                  // returns "facebook.com"
  bh.back(1);                  // returns "google.com"
  bh.forward(1);               // returns "facebook.com"
  bh.visit("linkedin.com");    // google -> facebook -> linkedin (youtube is cleared)
  bh.forward(2);               // returns "linkedin.com" (can't go forward)
  bh.back(2);                  // returns "google.com"
  bh.back(7);                  // returns "google.com" (can't go further back)
```

### Approach: DLL with Current Pointer

**Why this approach?**

A DLL naturally models browser history: `prev` is the "back" direction and `next` is "forward". The current pointer tracks where the user is. On `visit`, we create a new node after current and discard all forward nodes. On `back`/`forward`, we traverse the prev/next pointers. This provides O(1) for `visit` and O(steps) for `back`/`forward`, which is optimal since we must traverse steps links.

An alternative is using a list with an index pointer, but that requires O(n) for clearing forward history (shifting elements). The DLL approach clears forward history in O(1) by simply severing the `next` link.

### Optimized Java Solution

```java
public class BrowserHistory {

    private static class PageNode {
        String url;
        PageNode prev, next;

        PageNode(String url) {
            this.url = url;
        }
    }

    private PageNode current;

    public BrowserHistory(String homepage) {
        current = new PageNode(homepage);
    }

    public void visit(String url) {
        PageNode newPage = new PageNode(url);
        current.next = newPage;
        newPage.prev = current;
        // Forward history is automatically cleared (newPage.next is null)
        current = newPage;
    }

    public String back(int steps) {
        while (steps > 0 && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current.url;
    }

    public String forward(int steps) {
        while (steps > 0 && current.next != null) {
            current = current.next;
            steps--;
        }
        return current.url;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1) for visit, O(min(steps, n)) for back/forward** | Visit is constant. Back/forward traverse at most `steps` nodes. |
| **Space** | **O(n)** | One node per page in history. Forward history is garbage collected when discarded. |

---

## Q46. Max Stack using DLL + TreeMap

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List + TreeMap

### Problem

Design a max stack that supports the following operations:

- `void push(int x)` — Push element x onto stack.
- `int pop()` — Remove the element on top of the stack and return it.
- `int top()` — Get the element on the top of the stack without removing it.
- `int peekMax()` — Retrieve the maximum element in the stack without removing it.
- `int popMax()` — Retrieve the maximum element in the stack and remove it. If there are multiple maximum elements, remove the one closest to the top.

All operations should be efficient.

### Examples

```
Input:
  MaxStack stack = new MaxStack();
  stack.push(5);     // stack: [5]
  stack.push(1);     // stack: [5, 1]
  stack.push(5);     // stack: [5, 1, 5]
  stack.top();       // returns 5
  stack.popMax();    // returns 5 (top 5 is removed), stack: [5, 1]
  stack.top();       // returns 1
  stack.peekMax();   // returns 5
  stack.pop();       // returns 1, stack: [5]
  stack.top();       // returns 5
```

### Approach: DLL + TreeMap<Integer, List<DLLNode>>

**Why this approach?**

- **DLL** maintains the stack order. The tail of the DLL is the "top" of the stack.
- **TreeMap<Integer, List<DLLNode>>** maps values to a list of DLL nodes with that value. TreeMap keeps keys sorted, so `lastKey()` gives the max in O(log n).
- `push(x)`: Add node at DLL tail; add node reference to TreeMap. O(log n).
- `pop()`: Remove DLL tail; remove from TreeMap. O(log n).
- `top()`: Return DLL tail's value. O(1).
- `peekMax()`: Return TreeMap's `lastKey()`. O(log n).
- `popMax()`: Get the last node from `TreeMap.lastKey()`'s list (closest to top), remove from DLL in O(1), remove from TreeMap in O(log n).

This beats the naive O(n) `popMax` approach (scanning the entire stack). Using a heap would complicate removal of arbitrary elements.

### Optimized Java Solution

```java
public class MaxStack {

    private static class DLLNode {
        int val;
        DLLNode prev, next;

        DLLNode(int val) {
            this.val = val;
        }
    }

    private final DLLNode head; // Sentinel
    private final DLLNode tail; // Sentinel
    private final TreeMap<Integer, List<DLLNode>> map;

    public MaxStack() {
        head = new DLLNode(0);
        tail = new DLLNode(0);
        head.next = tail;
        tail.prev = head;
        map = new TreeMap<>();
    }

    public void push(int x) {
        DLLNode node = new DLLNode(x);
        // Add to DLL tail (top of stack)
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
        // Add to TreeMap
        map.computeIfAbsent(x, k -> new ArrayList<>()).add(node);
    }

    public int pop() {
        DLLNode topNode = tail.prev;
        removeFromDLL(topNode);
        removeFromMap(topNode);
        return topNode.val;
    }

    public int top() {
        return tail.prev.val;
    }

    public int peekMax() {
        return map.lastKey();
    }

    public int popMax() {
        int maxVal = map.lastKey();
        List<DLLNode> nodes = map.get(maxVal);
        // Remove the most recently added (closest to top) max node
        DLLNode maxNode = nodes.removeLast();
        if (nodes.isEmpty()) {
            map.remove(maxVal);
        }
        removeFromDLL(maxNode);
        return maxVal;
    }

    private void removeFromDLL(DLLNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void removeFromMap(DLLNode node) {
        List<DLLNode> nodes = map.get(node.val);
        nodes.removeLast(); // Remove the last occurrence (most recent push)
        if (nodes.isEmpty()) {
            map.remove(node.val);
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(log n) per operation** | TreeMap operations (insert, delete, lastKey) are O(log n). DLL operations are O(1). |
| **Space** | **O(n)** | Each element is stored once in the DLL and referenced once in the TreeMap. |

---

## Q47. All O(1) Data Structure (Inc/Dec Key, GetMaxKey, GetMinKey)

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List + HashMap

### Problem

Design a data structure that supports the following operations, all in **O(1)** time:

- `inc(String key)` — Increment the count of key by 1. If key doesn't exist, insert it with count 1.
- `dec(String key)` — Decrement the count of key by 1. If the count reaches 0, remove the key.
- `getMaxKey()` — Return any key with the maximum count. Return `""` if empty.
- `getMinKey()` — Return any key with the minimum count. Return `""` if empty.

### Examples

```
Input:
  AllOne ds = new AllOne();
  ds.inc("hello");         // {"hello": 1}
  ds.inc("hello");         // {"hello": 2}
  ds.getMaxKey();          // returns "hello"
  ds.getMinKey();          // returns "hello"
  ds.inc("leet");          // {"hello": 2, "leet": 1}
  ds.getMaxKey();          // returns "hello"
  ds.getMinKey();          // returns "leet"
  ds.dec("hello");         // {"hello": 1, "leet": 1}
  ds.getMaxKey();          // returns "hello" or "leet"
  ds.getMinKey();          // returns "hello" or "leet"
```

### Approach: Bucket DLL + Two HashMaps

**Why this approach?**

The key insight is maintaining a **doubly linked list of "buckets"**, where each bucket holds all keys with the same count. The buckets are ordered by count. This way:
- **getMaxKey()** returns any key from the tail bucket (highest count). O(1).
- **getMinKey()** returns any key from the head bucket (lowest count). O(1).
- **inc(key)** and **dec(key)** move the key between adjacent buckets. Since we know the key's current bucket (via a HashMap), and adjacent buckets differ by 1 in count, we either use the existing adjacent bucket or create a new one. All operations are O(1).

Two maps are needed:
1. `keyToBucket: HashMap<String, Bucket>` — maps each key to its bucket.
2. Each bucket contains a `Set<String>` of keys and an `int count`.

This is the canonical solution for this problem and achieves true O(1) for all operations.

### Optimized Java Solution

```java
public class AllOne {

    private static class Bucket {
        int count;
        Set<String> keys;
        Bucket prev, next;

        Bucket(int count) {
            this.count = count;
            this.keys = new LinkedHashSet<>();
        }
    }

    private final Map<String, Bucket> keyToBucket;
    private final Bucket head; // Sentinel (min side)
    private final Bucket tail; // Sentinel (max side)

    public AllOne() {
        keyToBucket = new HashMap<>();
        head = new Bucket(Integer.MIN_VALUE);
        tail = new Bucket(Integer.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
    }

    public void inc(String key) {
        if (keyToBucket.containsKey(key)) {
            Bucket currentBucket = keyToBucket.get(key);
            int newCount = currentBucket.count + 1;
            Bucket nextBucket = currentBucket.next;

            // If the next bucket doesn't have the right count, create one
            if (nextBucket.count != newCount) {
                nextBucket = insertBucketAfter(currentBucket, newCount);
            }
            nextBucket.keys.add(key);
            keyToBucket.put(key, nextBucket);

            // Remove from old bucket
            currentBucket.keys.remove(key);
            if (currentBucket.keys.isEmpty()) {
                removeBucket(currentBucket);
            }
        } else {
            // New key with count 1
            Bucket firstBucket = head.next;
            if (firstBucket.count != 1) {
                firstBucket = insertBucketAfter(head, 1);
            }
            firstBucket.keys.add(key);
            keyToBucket.put(key, firstBucket);
        }
    }

    public void dec(String key) {
        if (!keyToBucket.containsKey(key)) return;

        Bucket currentBucket = keyToBucket.get(key);
        int newCount = currentBucket.count - 1;

        if (newCount == 0) {
            keyToBucket.remove(key);
        } else {
            Bucket prevBucket = currentBucket.prev;
            if (prevBucket.count != newCount) {
                prevBucket = insertBucketAfter(currentBucket.prev, newCount);
            }
            prevBucket.keys.add(key);
            keyToBucket.put(key, prevBucket);
        }

        currentBucket.keys.remove(key);
        if (currentBucket.keys.isEmpty()) {
            removeBucket(currentBucket);
        }
    }

    public String getMaxKey() {
        if (tail.prev == head) return "";
        return tail.prev.keys.iterator().next();
    }

    public String getMinKey() {
        if (head.next == tail) return "";
        return head.next.keys.iterator().next();
    }

    private Bucket insertBucketAfter(Bucket node, int count) {
        Bucket newBucket = new Bucket(count);
        newBucket.prev = node;
        newBucket.next = node.next;
        node.next.prev = newBucket;
        node.next = newBucket;
        return newBucket;
    }

    private void removeBucket(Bucket bucket) {
        bucket.prev.next = bucket.next;
        bucket.next.prev = bucket.prev;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1) per operation** | All operations involve HashMap lookups and DLL insertions/removals between known neighbors — all O(1). |
| **Space** | **O(n)** | Where n is the number of distinct keys. Each key is in exactly one bucket. |

---

## Q48. Design a Text Editor with Cursor (Insert, Delete, Move Left/Right)

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List

### Problem

Design a text editor with a cursor that supports the following operations:

- `TextEditor()` — Initialize with empty text and cursor at position 0.
- `void addText(String text)` — Insert `text` at the cursor position. Cursor moves to the end of the inserted text.
- `int deleteText(int k)` — Delete up to `k` characters to the left of the cursor. Return the number of characters actually deleted. Cursor moves left accordingly.
- `String cursorLeft(int k)` — Move the cursor left by up to `k` positions. Return the last min(10, len) characters to the left of the cursor, where `len` is the number of characters to the left.
- `String cursorRight(int k)` — Move the cursor right by up to `k` positions. Return the last min(10, len) characters to the left of the cursor.

### Examples

```
Input:
  TextEditor editor = new TextEditor();
  editor.addText("leetcode");     // text: "leetcode|"
  editor.deleteText(4);           // text: "leet|", returns 4
  editor.addText("practice");     // text: "leetpractice|"
  editor.cursorLeft(8);           // text: "leet|practice", returns "leet"
  editor.deleteText(10);          // text: "|practice", returns 4
  editor.cursorLeft(2);           // text: "|practice", returns ""
  editor.cursorRight(6);          // text: "practi|ce", returns "practi"
```

### Approach: DLL of Characters with Cursor Pointer

**Why this approach?**

A DLL allows O(1) insertion and deletion at the cursor position. The cursor is represented as a pointer between two DLL nodes. We use a sentinel node so the cursor always has a valid "left" reference. Moving left/right is O(k). Insertion of text is O(|text|). This is more efficient than using a `StringBuilder` where insertions in the middle would be O(n).

An alternative is using two stacks (left of cursor and right of cursor), which also works well. The DLL approach is more natural for this section and demonstrates DLL's strength for in-place editing.

### Optimized Java Solution

```java
public class TextEditor {

    private static class CharNode {
        char ch;
        CharNode prev, next;

        CharNode(char ch) {
            this.ch = ch;
        }
    }

    private final CharNode sentinel; // Sentinel before all characters
    private CharNode cursor;         // Points to the node just LEFT of the cursor position

    public TextEditor() {
        sentinel = new CharNode('\0');
        cursor = sentinel; // Cursor is at position 0 (nothing to the left)
    }

    public void addText(String text) {
        for (char ch : text.toCharArray()) {
            CharNode newNode = new CharNode(ch);
            // Insert newNode after cursor
            newNode.next = cursor.next;
            newNode.prev = cursor;
            if (cursor.next != null) {
                cursor.next.prev = newNode;
            }
            cursor.next = newNode;
            cursor = newNode; // Move cursor to the right of inserted character
        }
    }

    public int deleteText(int k) {
        int deleted = 0;
        while (k > 0 && cursor != sentinel) {
            CharNode toDelete = cursor;
            cursor = cursor.prev;
            // Remove toDelete
            cursor.next = toDelete.next;
            if (toDelete.next != null) {
                toDelete.next.prev = cursor;
            }
            deleted++;
            k--;
        }
        return deleted;
    }

    public String cursorLeft(int k) {
        while (k > 0 && cursor != sentinel) {
            cursor = cursor.prev;
            k--;
        }
        return getLeftText();
    }

    public String cursorRight(int k) {
        while (k > 0 && cursor.next != null) {
            cursor = cursor.next;
            k--;
        }
        return getLeftText();
    }

    private String getLeftText() {
        StringBuilder sb = new StringBuilder();
        CharNode node = cursor;
        int count = 0;
        while (node != sentinel && count < 10) {
            sb.append(node.ch);
            node = node.prev;
            count++;
        }
        return sb.reverse().toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **addText: O(|text|), deleteText: O(k), cursorLeft/Right: O(k)** | Each operation traverses at most k or |text| nodes. getLeftText is O(10) = O(1). |
| **Space** | **O(n)** | Where n is the total number of characters currently in the editor. |

---

## Q49. LFU Cache (Least Frequently Used)

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List + HashMap

### Problem

Design a **Least Frequently Used (LFU)** cache with the following operations:

- `LFUCache(int capacity)` — Initialize with positive capacity.
- `int get(int key)` — Return the value of the key if it exists, otherwise -1. Increments the frequency of the key.
- `void put(int key, int value)` — Insert or update the key-value pair. If the cache is full, evict the least frequently used key. If there is a tie in frequency, evict the **least recently used** among the tied keys.

Both operations must run in **O(1)** time.

### Examples

```
Input:
  LFUCache cache = new LFUCache(2);
  cache.put(1, 1);     // freq(1)=1, cache: {1=1}
  cache.put(2, 2);     // freq(2)=1, cache: {1=1, 2=2}
  cache.get(1);        // returns 1, freq(1)=2
  cache.put(3, 3);     // evicts key 2 (freq=1, LRU among freq-1 keys)
                        // cache: {1=1, 3=3}
  cache.get(2);        // returns -1
  cache.get(3);        // returns 3, freq(3)=2
  cache.put(4, 4);     // evicts key 1 (freq=2, but 1 was accessed before 3, so 1 is LRU among freq-2)
                        // Wait: freq(1)=2, freq(3)=2. Key 1 was last used before key 3.
                        // Actually: after get(3), freq(1)=2, freq(3)=2. Key 1 was used at time t=3, key 3 at t=5.
                        // So key 1 is LRU among freq-2 keys. Evict key 1.
                        // cache: {3=3, 4=4}
  cache.get(1);        // returns -1
  cache.get(3);        // returns 3
  cache.get(4);        // returns 4
```

### Approach: HashMap + Frequency -> DLL Mapping

**Why this approach?**

LFU requires tracking both **frequency** and **recency** (for tie-breaking). The canonical O(1) approach uses:

1. **`keyToNode`: HashMap<Integer, Node>** — Maps key to its node (stores key, value, frequency).
2. **`freqToDLL`: HashMap<Integer, DoublyLinkedList>** — Maps each frequency to a DLL of nodes with that frequency. Within each DLL, the most recently used node is at the head, and the least recently used is at the tail.
3. **`minFreq`: int** — Tracks the current minimum frequency (for O(1) eviction).

**Operations:**
- `get(key)`: Look up node, increment its frequency, move from old frequency's DLL to new frequency's DLL. Update `minFreq` if necessary.
- `put(key, value)`: If exists, update like get. If new and at capacity, evict the tail node from `freqToDLL.get(minFreq)` (the LRU node among minimum frequency nodes). Insert with frequency 1, set `minFreq = 1`.

This is the classic O(1) LFU solution and is significantly more complex than LRU.

### Optimized Java Solution

```java
public class LFUCache {

    private static class Node {
        int key, value, freq;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    private static class DoublyLinkedList {
        final Node head, tail; // Sentinels
        int size;

        DoublyLinkedList() {
            head = new Node(-1, -1);
            tail = new Node(-1, -1);
            head.next = tail;
            tail.prev = head;
            size = 0;
        }

        void addFirst(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        Node removeLast() {
            if (size == 0) return null;
            Node last = tail.prev;
            remove(last);
            return last;
        }

        boolean isEmpty() {
            return size == 0;
        }
    }

    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> keyToNode;
    private final Map<Integer, DoublyLinkedList> freqToDLL;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToNode = new HashMap<>();
        this.freqToDLL = new HashMap<>();
    }

    public int get(int key) {
        Node node = keyToNode.get(key);
        if (node == null) return -1;
        incrementFrequency(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        Node node = keyToNode.get(key);
        if (node != null) {
            node.value = value;
            incrementFrequency(node);
        } else {
            if (keyToNode.size() == capacity) {
                // Evict LFU (and LRU among those)
                DoublyLinkedList minFreqList = freqToDLL.get(minFreq);
                Node evicted = minFreqList.removeLast();
                keyToNode.remove(evicted.key);
            }
            Node newNode = new Node(key, value);
            keyToNode.put(key, newNode);
            freqToDLL.computeIfAbsent(1, k -> new DoublyLinkedList()).addFirst(newNode);
            minFreq = 1;
        }
    }

    private void incrementFrequency(Node node) {
        int oldFreq = node.freq;
        int newFreq = oldFreq + 1;
        node.freq = newFreq;

        // Remove from old frequency list
        DoublyLinkedList oldList = freqToDLL.get(oldFreq);
        oldList.remove(node);

        // Update minFreq if necessary
        if (oldFreq == minFreq && oldList.isEmpty()) {
            minFreq++;
        }

        // Add to new frequency list
        freqToDLL.computeIfAbsent(newFreq, k -> new DoublyLinkedList()).addFirst(node);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1) per operation** | All operations use HashMap lookups and DLL manipulations between known positions — all O(1). |
| **Space** | **O(capacity)** | At most `capacity` nodes across all frequency lists. |

---

## Q50. Implement a Memory-Efficient DLL (XOR Linked List Concept in Java)

**Difficulty:** `Hard` | **Data Structure:** Doubly Linked List (Simulated XOR)

### Problem

An XOR Linked List is a memory-efficient doubly linked list where each node stores a single pointer: `XOR(prev_address, next_address)` instead of two separate pointers. This halves the pointer storage per node.

Since Java doesn't support raw pointer arithmetic, **simulate** the XOR linked list concept by storing integer-based node IDs and using XOR operations to derive prev/next. Implement the following operations:

- `insert(int val)` — Insert a value at the beginning.
- `List<Integer> traverseForward()` — Traverse and return all values from head to tail.
- `List<Integer> traverseBackward()` — Traverse and return all values from tail to head.
- `delete(int val)` — Delete the first occurrence of a value.

### Examples

```
Input:
  XORLinkedList xll = new XORLinkedList();
  xll.insert(10);
  xll.insert(20);
  xll.insert(30);
  xll.traverseForward();     // returns [30, 20, 10]
  xll.traverseBackward();    // returns [10, 20, 30]
  xll.delete(20);
  xll.traverseForward();     // returns [30, 10]
```

### Approach: Simulate with Integer IDs and a Node Map

**Why this approach?**

In C/C++, XOR linked lists use actual memory addresses. Java prohibits pointer arithmetic, so we simulate the concept using integer node IDs:

1. Each node stores `int xorPtr = prevId XOR nextId`.
2. A `HashMap<Integer, XORNode>` maps node IDs to nodes (simulating memory).
3. To traverse forward: given prevId and current node, `nextId = current.xorPtr XOR prevId`.
4. To traverse backward: given nextId and current node, `prevId = current.xorPtr XOR nextId`.

This demonstrates the XOR linked list's core principle — each node stores a single "pointer" value from which both neighbors can be derived, given one of them — while working within Java's constraints.

**Walk-through:**
```
Insert 10: node0 (id=0, xorPtr = 0 XOR 0 = 0). head=0, tail=0.
Insert 20: node1 (id=1). 
  - node1.xorPtr = 0 XOR 0 = 0 (prev=none, next=node0).
  Wait, let's use -1 for null.
  - node1.xorPtr = (-1) XOR 0 = -1 XOR 0 (prev=null=-1, next=0).
  - Update node0.xorPtr: old = (-1) XOR (-1); now prev=1, next=-1. xorPtr = 1 XOR (-1).
  head=1, tail=0.

Traverse forward from head(id=1):
  prevId = -1 (null)
  current = node1, xorPtr = (-1) XOR 0 => nextId = xorPtr XOR prevId = ((-1)^0) ^ (-1) = 0.
  Move: prevId=1, current = node0, nextId = node0.xorPtr XOR 1 => should be -1 (null). Stop.
```

### Optimized Java Solution

```java
public class XORLinkedList {

    private static final int NULL_ID = -1;

    private static class XORNode {
        int val;
        int xorPtr; // prevId XOR nextId

        XORNode(int val) {
            this.val = val;
            this.xorPtr = NULL_ID ^ NULL_ID; // both neighbors are null initially
        }
    }

    private final Map<Integer, XORNode> memory; // Simulates memory (id -> node)
    private int nextId;
    private int headId;
    private int tailId;

    public XORLinkedList() {
        memory = new HashMap<>();
        nextId = 0;
        headId = NULL_ID;
        tailId = NULL_ID;
    }

    public void insert(int val) {
        int newId = nextId++;
        XORNode newNode = new XORNode(val);
        memory.put(newId, newNode);

        if (headId == NULL_ID) {
            // First node
            newNode.xorPtr = NULL_ID ^ NULL_ID;
            headId = newId;
            tailId = newId;
        } else {
            // Insert at beginning
            // newNode: prev = NULL, next = headId
            newNode.xorPtr = NULL_ID ^ headId;

            // Update old head: its prev changes from NULL to newId
            XORNode oldHead = memory.get(headId);
            // oldHead.xorPtr was (NULL ^ oldNextId) = (NULL_ID ^ oldNextId)
            // New xorPtr = newId ^ oldNextId
            // oldNextId = oldHead.xorPtr ^ NULL_ID
            int oldNextId = oldHead.xorPtr ^ NULL_ID;
            oldHead.xorPtr = newId ^ oldNextId;

            headId = newId;
        }
    }

    public List<Integer> traverseForward() {
        List<Integer> result = new ArrayList<>();
        int prevId = NULL_ID;
        int currId = headId;

        while (currId != NULL_ID) {
            XORNode curr = memory.get(currId);
            result.add(curr.val);
            int nextIdVal = curr.xorPtr ^ prevId;
            prevId = currId;
            currId = nextIdVal;
        }
        return result;
    }

    public List<Integer> traverseBackward() {
        List<Integer> result = new ArrayList<>();
        int nextIdVal = NULL_ID;
        int currId = tailId;

        while (currId != NULL_ID) {
            XORNode curr = memory.get(currId);
            result.add(curr.val);
            int prevId = curr.xorPtr ^ nextIdVal;
            nextIdVal = currId;
            currId = prevId;
        }
        return result;
    }

    public boolean delete(int val) {
        int prevId = NULL_ID;
        int currId = headId;

        while (currId != NULL_ID) {
            XORNode curr = memory.get(currId);
            int nextIdVal = curr.xorPtr ^ prevId;

            if (curr.val == val) {
                // Update previous node's xorPtr
                if (prevId != NULL_ID) {
                    XORNode prevNode = memory.get(prevId);
                    // prevNode.xorPtr was (prevPrevId ^ currId)
                    // Change to (prevPrevId ^ nextIdVal)
                    // prevPrevId = prevNode.xorPtr ^ currId
                    int prevPrevId = prevNode.xorPtr ^ currId;
                    prevNode.xorPtr = prevPrevId ^ nextIdVal;
                } else {
                    headId = nextIdVal;
                }

                // Update next node's xorPtr
                if (nextIdVal != NULL_ID) {
                    XORNode nextNode = memory.get(nextIdVal);
                    // nextNode.xorPtr was (currId ^ nextNextId)
                    // Change to (prevId ^ nextNextId)
                    int nextNextId = nextNode.xorPtr ^ currId;
                    nextNode.xorPtr = prevId ^ nextNextId;
                } else {
                    tailId = prevId;
                }

                memory.remove(currId);
                return true;
            }

            prevId = currId;
            currId = nextIdVal;
        }
        return false; // Value not found
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **insert: O(1), traverseForward/Backward: O(n), delete: O(n)** | Insert at head is O(1). Traversal and search-based delete require visiting nodes sequentially. |
| **Space** | **O(n)** | Each node uses a single `xorPtr` integer instead of two pointer references. The HashMap adds overhead in Java, but the concept demonstrates the 50% pointer storage savings achieved in C/C++. |

> **Note:** In a real C/C++ implementation, XOR linked lists save memory by replacing two 8-byte pointers with a single 8-byte XOR value per node. In Java, the HashMap simulation adds overhead, but the algorithm demonstrates the core principle: `next = xorPtr XOR prev` and `prev = xorPtr XOR next`.
# STACK

> **25 Questions** | Q51–Q75 | Basic → Medium → Hard

## Mini-Index

| # | Title | Difficulty | Key Concept |
|---|---|---|---|
| Q51 | Implement Stack using Array | Basic | Dynamic resizing, amortized O(1) |
| Q52 | Valid Parentheses | Basic | Bracket matching |
| Q53 | Reverse a String using Stack | Basic | LIFO property |
| Q54 | Implement Two Stacks in One Array | Basic | Space-efficient dual stacks |
| Q55 | Sort a Stack using Recursion | Basic | Recursive insertion sort |
| Q56 | Min Stack — getMin in O(1) | Medium | Auxiliary stack / encoding |
| Q57 | Evaluate Reverse Polish Notation | Medium | Postfix evaluation |
| Q58 | Daily Temperatures | Medium | Monotonic stack (decreasing) |
| Q59 | Next Greater Element I | Medium | Monotonic stack + hash map |
| Q60 | Next Greater Element II (Circular) | Medium | Circular traversal trick |
| Q61 | Implement Stack using Two Queues | Medium | Queue-based LIFO simulation |
| Q62 | Decode String | Medium | Nested encoding, recursive stack |
| Q63 | Simplify Unix File Path | Medium | Path canonicalization |
| Q64 | Asteroid Collision | Medium | Simulation with stack |
| Q65 | Remove All Adjacent Duplicates II | Medium | Stack with counts |
| Q66 | Remove K Digits | Medium | Monotonic stack (greedy) |
| Q67 | Stock Span Problem | Medium | Monotonic stack (decreasing) |
| Q68 | Validate Stack Sequences | Medium | Simulation |
| Q69 | Basic Calculator | Hard | Recursive descent / sign stack |
| Q70 | Basic Calculator II | Hard | Operator precedence |
| Q71 | Largest Rectangle in Histogram | Hard | Monotonic stack (increasing) |
| Q72 | Trapping Rain Water (Stack) | Hard | Monotonic stack (decreasing) |
| Q73 | Maximum Frequency Stack | Hard | Frequency map + stack-of-stacks |
| Q74 | Longest Valid Parentheses | Hard | Stack-based interval tracking |
| Q75 | Number of Visible People in Queue | Hard | Monotonic stack (decreasing) |

---
---

## Q51. Implement Stack using Array

**Difficulty:** `Basic` | **Data Structure:** Stack

### Problem

Design a stack that uses a raw array internally and supports **dynamic resizing**. Implement the following operations:

- `push(x)` — Push element x onto the stack. If the underlying array is full, double its capacity.
- `pop()` — Remove and return the top element. Throw an exception if the stack is empty. Optionally shrink the array when utilization falls below 25%.
- `peek()` — Return the top element without removing it.
- `isEmpty()` — Return true if the stack has no elements.
- `size()` — Return the number of elements currently in the stack.

### Examples

```
Input:  push(10), push(20), push(30), pop(), peek(), size()
Output: pop() -> 30, peek() -> 20, size() -> 2
```

```
Input:  push(5), isEmpty(), pop(), isEmpty()
Output: isEmpty() -> false, pop() -> 5, isEmpty() -> true
```

```
Input:  push(1), push(2), push(3), push(4), push(5)  [initial capacity = 4]
Output: Internal array resizes from capacity 4 → 8 on the 5th push
```

### Approach: Amortized Doubling Array

**Why this approach?**

A fixed-size array wastes space if too large or fails if too small. Dynamic resizing solves both problems. We **double** when full and **halve** when the array is only 25% utilized. Doubling gives **amortized O(1)** push — while a single resize costs O(n), it happens so infrequently that averaged over n pushes the cost per push is O(1). We shrink at 25% (not 50%) to avoid *thrashing* — repeatedly resizing when push/pop alternate around the boundary.

**Alternative rejected:** Linked-list stack has O(1) worst-case push/pop but suffers from poor cache locality and higher per-element memory overhead (each node stores a pointer). The array-based stack is almost always preferred in practice.

### Optimized Java Solution

```java
import java.util.NoSuchElementException;

public class DynamicArrayStack<T> {
    private static final int DEFAULT_CAPACITY = 8;
    private Object[] data;
    private int top; // index of next insertion point (also equals size)

    public DynamicArrayStack() {
        data = new Object[DEFAULT_CAPACITY];
        top = 0;
    }

    public void push(T item) {
        if (top == data.length) {
            resize(data.length * 2);
        }
        data[top++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack is empty");
        T item = (T) data[--top];
        data[top] = null; // prevent memory leak
        // Shrink when 25% full (but never below default capacity)
        if (top > 0 && top == data.length / 4) {
            resize(data.length / 2);
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Stack is empty");
        return (T) data[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public int size() {
        return top;
    }

    private void resize(int newCapacity) {
        Object[] newData = new Object[Math.max(newCapacity, DEFAULT_CAPACITY)];
        System.arraycopy(data, 0, newData, 0, top);
        data = newData;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **Amortized O(1)** per push/pop | Each resize doubles/halves the array. Over n pushes, total copy work is n + n/2 + n/4 + … ≈ 2n, so amortized cost is O(1). peek, isEmpty, size are worst-case O(1). |
| **Space** | **O(n)** | The array holds at most 4n elements (between 25%–100% utilization), so space is Θ(n). |

---

## Q52. Valid Parentheses

**Difficulty:** `Basic` | **Data Structure:** Stack

### Problem

Given a string `s` containing only the characters `'('`, `')'`, `'{'`, `'}'`, `'['`, `']'`, determine if the input string is **valid**.

A string is valid if:
1. Every open bracket has a corresponding close bracket of the same type.
2. Brackets are closed in the correct (nested) order.
3. Every close bracket has a matching open bracket.

### Examples

```
Input:  s = "()"
Output: true
```

```
Input:  s = "()[]{}"
Output: true
```

```
Input:  s = "(]"
Output: false
Explanation: '(' is opened but closed with ']' — type mismatch.
```

### Approach: Stack-Based Matching

**Why this approach?**

A stack naturally handles the nesting requirement: the **most recently opened** bracket must be the **first one closed** (LIFO). When we see an opening bracket we push it; when we see a closing bracket we check that the top of the stack holds the matching opener.

**Alternative rejected:** Counting open vs. closed brackets only works for a single bracket type and fails for interleaved types like `"([)]"`. A stack is necessary to enforce correct nesting.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            // Push the expected closing bracket
            switch (c) {
                case '(' -> stack.push(')');
                case '{' -> stack.push('}');
                case '[' -> stack.push(']');
                default -> {
                    // It's a closing bracket — check match
                    if (stack.isEmpty() || stack.pop() != c) {
                        return false;
                    }
                }
            }
        }
        return stack.isEmpty(); // no unmatched openers
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through the string; each character is pushed/popped at most once. |
| **Space** | **O(n)** | In the worst case (all openers), the stack holds n/2 elements → O(n). |

---

## Q53. Reverse a String using Stack

**Difficulty:** `Basic` | **Data Structure:** Stack

### Problem

Given a string, reverse it using a stack. Do not use `StringBuilder.reverse()` or any built-in reverse utility.

### Examples

```
Input:  "hello"
Output: "olleh"
```

```
Input:  "abcde"
Output: "edcba"
```

```
Input:  "A"
Output: "A"
```

### Approach: Push-All-Then-Pop-All

**Why this approach?**

A stack reverses insertion order by its LIFO nature. Push every character left-to-right, then pop them all — the result is the reversed string. This directly demonstrates the fundamental reversal property of stacks.

**Alternative rejected:** Using a two-pointer swap is more space-efficient (O(1) extra space), but the purpose here is to demonstrate stack mechanics. In practice, prefer the two-pointer approach or `StringBuilder.reverse()`.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public String reverseString(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            stack.push(c);
        }

        StringBuilder result = new StringBuilder(s.length());
        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }

        return result.toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Two passes: one to push all characters, one to pop them all. |
| **Space** | **O(n)** | The stack holds all n characters simultaneously. |

---

## Q54. Implement Two Stacks in One Array

**Difficulty:** `Basic` | **Data Structure:** Stack

### Problem

Implement two stacks using a **single array**. Both stacks should support `push`, `pop`, `peek`, and `isEmpty`. The implementation should be **space-efficient** — the total number of elements across both stacks can be up to the array capacity.

### Examples

```
Input:  stack1.push(1), stack1.push(2), stack2.push(10), stack2.push(20)
        stack1.pop(), stack2.pop()
Output: stack1.pop() -> 2, stack2.pop() -> 20
```

```
Input:  Capacity = 5
        stack1.push(1), stack1.push(2), stack1.push(3), stack2.push(4), stack2.push(5)
Output: Both stacks together use all 5 slots — no overflow.
```

```
Input:  stack1.push(1), stack2.push(2), stack1.isEmpty(), stack2.isEmpty()
Output: false, false
```

### Approach: Grow from Opposite Ends

**Why this approach?**

Stack 1 grows from index 0 → right, Stack 2 grows from index (capacity - 1) → left. They share the same array and can each use as much space as needed until they meet. This is optimal because no space is wasted — if one stack is empty, the other can use the entire array.

**Alternative rejected:** Splitting the array in half (each stack gets half) wastes space if one stack is much larger than the other.

### Optimized Java Solution

```java
import java.util.NoSuchElementException;

public class TwoStacks {
    private final int[] data;
    private int top1; // next insertion for stack 1 (grows right)
    private int top2; // next insertion for stack 2 (grows left)

    public TwoStacks(int capacity) {
        data = new int[capacity];
        top1 = -1;
        top2 = capacity;
    }

    // ---- Stack 1 operations ----
    public void push1(int val) {
        if (top1 + 1 == top2) throw new IllegalStateException("Array is full");
        data[++top1] = val;
    }

    public int pop1() {
        if (top1 == -1) throw new NoSuchElementException("Stack 1 is empty");
        return data[top1--];
    }

    public int peek1() {
        if (top1 == -1) throw new NoSuchElementException("Stack 1 is empty");
        return data[top1];
    }

    public boolean isEmpty1() {
        return top1 == -1;
    }

    // ---- Stack 2 operations ----
    public void push2(int val) {
        if (top1 + 1 == top2) throw new IllegalStateException("Array is full");
        data[--top2] = val;
    }

    public int pop2() {
        if (top2 == data.length) throw new NoSuchElementException("Stack 2 is empty");
        return data[top2++];
    }

    public int peek2() {
        if (top2 == data.length) throw new NoSuchElementException("Stack 2 is empty");
        return data[top2];
    }

    public boolean isEmpty2() {
        return top2 == data.length;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per operation | push, pop, peek, isEmpty are all constant-time index operations. |
| **Space** | **O(n)** | Single array of size n shared between both stacks. No wasted space. |

---

## Q55. Sort a Stack using Recursion

**Difficulty:** `Basic` | **Data Structure:** Stack

### Problem

Sort a stack in **ascending order** (smallest on top) using only recursion. You may use the following stack operations: `push`, `pop`, `peek`, `isEmpty`. You may **not** use any other data structure (no extra array, no second stack variable).

### Examples

```
Input:  Stack (top → bottom): [3, 1, 4, 2]
Output: Stack (top → bottom): [1, 2, 3, 4]
```

```
Input:  Stack: [5, 5, 1]
Output: Stack: [1, 5, 5]
```

```
Input:  Stack: [42]
Output: Stack: [42]
```

### Approach: Recursive Insertion Sort

**Why this approach?**

The constraint forbids auxiliary data structures. We use the **call stack** as implicit storage. The idea:
1. **`sortStack()`**: Pop the top element, recursively sort the remaining stack, then insert the popped element back in its correct sorted position.
2. **`insertSorted(val)`**: If the stack is empty or `val` ≤ top, push `val`. Otherwise, pop the top, recursively insert `val`, then push the popped element back.

This is essentially **insertion sort** implemented via recursion.

**Alternative rejected:** Using a temporary stack (iterative approach) is O(n²) time and O(n) space with an explicit extra stack — equally valid but doesn't satisfy the "no other data structure" constraint.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public void sortStack(Deque<Integer> stack) {
        if (stack.isEmpty()) return;

        int top = stack.pop();
        sortStack(stack);       // recursively sort the rest
        insertSorted(stack, top); // insert top in correct position
    }

    private void insertSorted(Deque<Integer> stack, int val) {
        // Base case: stack is empty or val is ≤ current top → push here
        if (stack.isEmpty() || val <= stack.peek()) {
            stack.push(val);
            return;
        }

        // val is larger than top → pop top, recurse, then push top back
        int top = stack.pop();
        insertSorted(stack, val);
        stack.push(top);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n²)** | For each of n elements, `insertSorted` may traverse the entire stack (up to n comparisons). Total: n + (n-1) + … + 1 = O(n²). |
| **Space** | **O(n²)** | Each `sortStack` call uses O(1) space but `insertSorted` can recurse up to n deep inside each `sortStack` call. Maximum call stack depth is O(n) per level × O(n) levels → O(n²) in the worst case. |

---

## Q56. Min Stack — getMin in O(1)

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

Design a stack that supports the following operations, all in **O(1)** time:
- `push(val)` — Push val onto the stack.
- `pop()` — Remove the top element.
- `top()` — Return the top element.
- `getMin()` — Retrieve the minimum element currently in the stack.

### Examples

```
Input:  push(-2), push(0), push(-3), getMin(), pop(), top(), getMin()
Output: getMin() -> -3, pop() removes -3, top() -> 0, getMin() -> -2
```

```
Input:  push(1), push(1), getMin(), pop(), getMin()
Output: getMin() -> 1, getMin() -> 1
```

```
Input:  push(5), push(3), push(7), getMin(), pop(), getMin()
Output: getMin() -> 3, getMin() -> 3
```

### Approach: Synchronized Min Stack

**Why this approach?**

We maintain **two stacks**: the main stack and a `minStack` that tracks the running minimum. Every time we push, we also push the current minimum onto `minStack`. When we pop, we pop from both. This way, `minStack.peek()` always gives the current minimum in O(1).

**Alternative considered:** Storing `(value, currentMin)` pairs in a single stack works identically but uses slightly more memory per entry. Another approach encodes the delta between value and current min into the stack to use only one stack, but it's error-prone with integer overflow.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class MinStack {
    private final Deque<Integer> stack;
    private final Deque<Integer> minStack;

    public MinStack() {
        stack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    public void push(int val) {
        stack.push(val);
        int currentMin = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
        minStack.push(currentMin);
    }

    public void pop() {
        stack.pop();
        minStack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per operation | push, pop, top, getMin all do a constant number of stack operations. |
| **Space** | **O(n)** | Two stacks of size n. The minStack can be optimized to only push when a new minimum is found, but worst case remains O(n). |

---

## Q57. Evaluate Reverse Polish Notation (Postfix)

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

Evaluate the value of an arithmetic expression in **Reverse Polish Notation** (postfix notation). Valid operators are `+`, `-`, `*`, `/`. Each operand may be an integer or another expression. Division truncates toward zero.

### Examples

```
Input:  tokens = ["2","1","+","3","*"]
Output: 9
Explanation: ((2 + 1) * 3) = 9
```

```
Input:  tokens = ["4","13","5","/","+"]
Output: 6
Explanation: (4 + (13 / 5)) = 4 + 2 = 6
```

```
Input:  tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
Output: 22
```

### Approach: Operand Stack

**Why this approach?**

RPN is designed for stack-based evaluation. When we see a **number**, push it. When we see an **operator**, pop two operands, apply the operator, and push the result. After processing all tokens, the stack holds exactly one value — the answer.

**Why RPN / postfix?** It eliminates the need for parentheses and operator precedence rules. Every expression has exactly one unambiguous evaluation order.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : tokens) {
            switch (token) {
                case "+", "-", "*", "/" -> {
                    int b = stack.pop(); // second operand (popped first)
                    int a = stack.pop(); // first operand
                    int result = switch (token) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        case "*" -> a * b;
                        case "/" -> a / b; // truncates toward zero in Java
                        default -> throw new IllegalArgumentException();
                    };
                    stack.push(result);
                }
                default -> stack.push(Integer.parseInt(token));
            }
        }

        return stack.pop();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We process each token exactly once. |
| **Space** | **O(n)** | The stack can hold up to (n+1)/2 operands (when all operands are pushed before any operator). |

---

## Q58. Daily Temperatures

**Difficulty:** `Medium` | **Data Structure:** Stack (Monotonic)

### Problem

Given an array of integers `temperatures` representing daily temperatures, return an array `answer` such that `answer[i]` is the number of days you have to wait after day `i` to get a warmer temperature. If there is no future day with a warmer temperature, `answer[i] = 0`.

### Examples

```
Input:  temperatures = [73,74,75,71,69,72,76,73]
Output:               [ 1, 1, 4, 2, 1, 1, 0, 0]
```

```
Input:  temperatures = [30,40,50,60]
Output:               [ 1, 1, 1, 0]
```

```
Input:  temperatures = [30,20,10]
Output:               [ 0, 0, 0]
```

### Approach: Monotonic Decreasing Stack

**Why this approach?**

We need the **next greater element** for each position. A **monotonic decreasing stack** (stores indices of temperatures in decreasing order) lets us efficiently resolve each day: when we encounter a warmer day, we pop all cooler days from the stack and compute their wait times.

**Visual walk-through** for `[73, 74, 75, 71, 69, 72, 76, 73]`:

```
Step | Temp | Stack (indices→temps)       | Action                    | answer updates
-----|------|-----------------------------|---------------------------|---------------
  0  |  73  | [0→73]                      | push 0                    |
  1  |  74  | [1→74]                      | 74>73: pop 0, push 1      | ans[0]=1-0=1
  2  |  75  | [2→75]                      | 75>74: pop 1, push 2      | ans[1]=2-1=1
  3  |  71  | [2→75, 3→71]               | 71<75: push 3             |
  4  |  69  | [2→75, 3→71, 4→69]         | 69<71: push 4             |
  5  |  72  | [2→75, 5→72]               | 72>69: pop 4, 72>71: pop 3| ans[4]=1, ans[3]=2
  6  |  76  | [6→76]                      | 76>72: pop 5, 76>75: pop 2| ans[5]=1, ans[2]=4
  7  |  73  | [6→76, 7→73]               | 73<76: push 7             |
Remaining in stack: ans[6]=0, ans[7]=0
```

**Alternative rejected:** Brute force checks every future day for each position → O(n²). The monotonic stack ensures each index is pushed and popped at most once → O(n).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevDay = stack.pop();
                answer[prevDay] = i - prevDay;
            }
            stack.push(i);
        }
        // Indices remaining in the stack have answer = 0 (default)
        return answer;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each index is pushed and popped at most once. Total operations ≤ 2n. |
| **Space** | **O(n)** | The stack can hold up to n indices (monotonically decreasing temperatures). |

---

## Q59. Next Greater Element I

**Difficulty:** `Medium` | **Data Structure:** Stack (Monotonic)

### Problem

You are given two **distinct** integer arrays `nums1` and `nums2` where `nums1` is a subset of `nums2`. For each element in `nums1`, find the **next greater element** in `nums2`. The next greater element of `x` in `nums2` is the first element to the **right** of `x` in `nums2` that is greater than `x`. If no such element exists, return `-1`.

### Examples

```
Input:  nums1 = [4,1,2], nums2 = [1,3,4,2]
Output: [-1,3,-1]
Explanation: 4 → no greater to its right → -1
             1 → next greater is 3
             2 → no greater to its right → -1
```

```
Input:  nums1 = [2,4], nums2 = [1,2,3,4]
Output: [3,-1]
```

```
Input:  nums1 = [1], nums2 = [1]
Output: [-1]
```

### Approach: Monotonic Stack + HashMap

**Why this approach?**

First, compute the next greater element for **every** element in `nums2` using a monotonic decreasing stack. Store results in a HashMap. Then, for each element in `nums1`, simply look up the answer.

This decouples the two arrays: the stack pass is O(m) on `nums2`, and lookups for `nums1` are O(n).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> nextGreater = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();

        // Build next-greater map for all elements in nums2
        for (int num : nums2) {
            while (!stack.isEmpty() && stack.peek() < num) {
                nextGreater.put(stack.pop(), num);
            }
            stack.push(num);
        }

        // Answer queries for nums1
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            result[i] = nextGreater.getOrDefault(nums1[i], -1);
        }
        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n + m)** | One pass through nums2 (length m) for the stack, one pass through nums1 (length n) for lookups. |
| **Space** | **O(m)** | HashMap stores up to m entries; stack holds up to m elements. |

---

## Q60. Next Greater Element II (Circular Array)

**Difficulty:** `Medium` | **Data Structure:** Stack (Monotonic)

### Problem

Given a **circular** integer array `nums`, return the next greater element for every element. The next greater number of `nums[i]` is the first number greater than `nums[i]` traversing circularly. If no such number exists, return `-1`.

### Examples

```
Input:  nums = [1,2,1]
Output:       [2,-1,2]
Explanation: 1 → 2, 2 → no greater → -1, 1 → wraps around to 2
```

```
Input:  nums = [1,2,3,4,3]
Output:       [2,3,4,-1,4]
```

```
Input:  nums = [5,4,3,2,1]
Output:       [-1,5,5,5,5]
Explanation: Circular — every element except 5 wraps around to find 5.
```

### Approach: Monotonic Stack with Double Traversal

**Why this approach?**

To handle circularity, we iterate through the array **twice** (indices 0 to 2n−1) using `i % n` to simulate wrapping. We only assign results during the first pass (i < n) to avoid overwriting. The monotonic decreasing stack remains the same as the standard next-greater-element pattern.

**Why traverse twice?** An element near the end of the array may have its next greater element near the beginning. Two passes guarantee every element has a chance to "see" elements that come before it circularly.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class Solution {
    public int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices

        for (int i = 0; i < 2 * n; i++) {
            int num = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] < num) {
                result[stack.pop()] = num;
            }
            if (i < n) {
                stack.push(i); // only push indices from the first pass
            }
        }

        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We iterate 2n times, but each index is pushed and popped at most once → O(n) total stack operations. |
| **Space** | **O(n)** | The stack holds at most n indices. |

---

## Q61. Implement Stack using Two Queues

**Difficulty:** `Medium` | **Data Structure:** Stack, Queue

### Problem

Implement a **LIFO** stack using only **two queues**. The stack should support: `push(x)`, `pop()`, `top()`, `isEmpty()`.

### Examples

```
Input:  push(1), push(2), top(), pop(), isEmpty()
Output: top() -> 2, pop() -> 2, isEmpty() -> false (1 is still there)
```

```
Input:  push(10), push(20), push(30), pop(), pop()
Output: pop() -> 30, pop() -> 20
```

```
Input:  push(5), pop(), isEmpty()
Output: pop() -> 5, isEmpty() -> true
```

### Approach: Costly Push (Maintain Reversed Order)

**Why this approach?**

We keep a single main queue where the **front** always holds the most recently pushed element (stack top). On `push(x)`:
1. Add `x` to the empty helper queue.
2. Move all elements from the main queue to the helper queue.
3. Swap the names of the two queues.

This makes `pop()` and `top()` O(1) at the cost of O(n) `push()`.

**Alternative:** Making `pop()` costly (O(n) pop, O(1) push) is equally valid. The push-costly version is often preferred because it provides O(1) `top()`, which is called more frequently in many applications.

### Optimized Java Solution

```java
import java.util.LinkedList;
import java.util.Queue;

class MyStack {
    private Queue<Integer> main;
    private Queue<Integer> helper;

    public MyStack() {
        main = new LinkedList<>();
        helper = new LinkedList<>();
    }

    public void push(int x) {
        helper.offer(x);
        while (!main.isEmpty()) {
            helper.offer(main.poll());
        }
        // Swap queues
        Queue<Integer> temp = main;
        main = helper;
        helper = temp;
    }

    public int pop() {
        return main.poll();
    }

    public int top() {
        return main.peek();
    }

    public boolean empty() {
        return main.isEmpty();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **push O(n), pop/top/empty O(1)** | Push transfers all n elements between queues. Pop, top, and empty are direct queue operations. |
| **Space** | **O(n)** | Both queues together always hold exactly n elements. |

---

## Q62. Decode String

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

Given an encoded string, return its decoded string. The encoding rule is: `k[encoded_string]`, where the `encoded_string` inside the brackets is repeated exactly `k` times. You may assume the input is always valid.

### Examples

```
Input:  s = "3[a]2[bc]"
Output: "aaabcbc"
```

```
Input:  s = "3[a2[c]]"
Output: "accaccacc"
Explanation: Inner first: 2[c] = "cc", then 3[acc] = "accaccacc"
```

```
Input:  s = "2[abc]3[cd]ef"
Output: "abcabccdcdcdef"
```

### Approach: Two-Stack (Count Stack + String Stack)

**Why this approach?**

Nesting means we must remember the **context** (current string and repeat count) before entering a `[`. A stack is perfect for saving/restoring nested contexts.

- **countStack**: saves the repeat count before entering a nested `[`.
- **stringStack**: saves the string built so far before entering a nested `[`.
- When we hit `]`: pop the count and the previous string, repeat the current segment, and append it to the previous string.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public String decodeString(String s) {
        Deque<Integer> countStack = new ArrayDeque<>();
        Deque<StringBuilder> stringStack = new ArrayDeque<>();
        StringBuilder current = new StringBuilder();
        int k = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0'); // handle multi-digit numbers
            } else if (c == '[') {
                countStack.push(k);
                stringStack.push(current);
                current = new StringBuilder();
                k = 0;
            } else if (c == ']') {
                int repeatCount = countStack.pop();
                StringBuilder decoded = stringStack.pop();
                decoded.append(String.valueOf(current).repeat(repeatCount));
                current = decoded;
            } else {
                current.append(c);
            }
        }

        return current.toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n · maxK)** | Where n is the length of the decoded output. Each character of the final output is generated once. Building via `repeat()` takes time proportional to the repeated length. |
| **Space** | **O(n)** | Stack depth is bounded by nesting depth; the dominant space is the output string. |

---

## Q63. Simplify Unix File Path

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

Given an absolute Unix-style file path, simplify it by converting it to the **canonical path**:
- Starts with `/`.
- Directories are separated by a single `/`.
- No trailing `/` (unless it's the root).
- `"."` refers to the current directory (skip it).
- `".."` refers to the parent directory (go up one level).
- Multiple consecutive slashes are treated as a single slash.

### Examples

```
Input:  path = "/home/"
Output: "/home"
```

```
Input:  path = "/home//foo/"
Output: "/home/foo"
```

```
Input:  path = "/a/./b/../../c/"
Output: "/c"
Explanation: /a → /a/b → /a → / → /c
```

### Approach: Split and Stack Filter

**Why this approach?**

Split the path by `/`, then iterate over the components:
- Skip empty strings (from consecutive slashes) and `"."`.
- For `".."`, pop from the stack (go up one level) if the stack isn't empty.
- Otherwise, push the directory name.

Finally, join the stack contents with `/` and prepend `/`.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();

        for (String component : path.split("/")) {
            switch (component) {
                case "", "." -> { /* skip */ }
                case ".." -> { if (!stack.isEmpty()) stack.pollLast(); }
                default -> stack.offerLast(component);
            }
        }

        return "/" + String.join("/", stack);
    }
}
```

> **Note:** We use `offerLast`/`pollLast` to treat the `ArrayDeque` as a stack while preserving iteration order (front to back) for the final join.

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Splitting is O(n), iterating over components is O(n), joining is O(n). |
| **Space** | **O(n)** | The stack and the split array each hold at most O(n) elements. |

---

## Q64. Asteroid Collision

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

We are given an array `asteroids` of integers representing asteroids in a row. For each asteroid, the absolute value represents its size, and the sign represents its direction (positive = right, negative = left). All asteroids move at the same speed.

When two asteroids meet, the smaller one explodes. If they are the same size, both explode. Two asteroids moving in the same direction never meet.

Return the state of the asteroids after all collisions.

### Examples

```
Input:  asteroids = [5,10,-5]
Output: [5,10]
Explanation: 10 and -5 collide → 10 survives. 5 and 10 never collide (same direction).
```

```
Input:  asteroids = [8,-8]
Output: []
Explanation: Equal size, both explode.
```

```
Input:  asteroids = [10,2,-5]
Output: [10]
Explanation: 2 and -5 collide → -5 survives. 10 and -5 collide → 10 survives.
```

### Approach: Stack Simulation

**Why this approach?**

A collision only happens when a **right-moving** asteroid (positive, already on the stack) meets a **left-moving** asteroid (negative, incoming). We push right-moving asteroids onto the stack. When a left-moving asteroid arrives, we simulate collisions with the stack top until the incoming asteroid is destroyed, survives, or the stack has no more right-moving asteroids.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (int ast : asteroids) {
            boolean survived = true;

            // Collision: stack top is moving right (+) and current is moving left (-)
            while (!stack.isEmpty() && stack.peek() > 0 && ast < 0) {
                int top = stack.peek();
                if (top < -ast) {
                    stack.pop();  // top is smaller, it explodes; continue checking
                } else if (top == -ast) {
                    stack.pop();  // equal size: both explode
                    survived = false;
                    break;
                } else {
                    survived = false; // top is bigger: current asteroid explodes
                    break;
                }
            }

            if (survived) {
                stack.push(ast);
            }
        }

        // Convert stack to array (bottom to top order)
        int[] result = new int[stack.size()];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = stack.pop();
        }
        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each asteroid is pushed and popped at most once → total operations ≤ 2n. |
| **Space** | **O(n)** | In the worst case (no collisions), all asteroids remain in the stack. |

---

## Q65. Remove All Adjacent Duplicates in String II

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

Given a string `s` and an integer `k`, repeatedly remove groups of `k` **adjacent identical** characters until no more such groups exist. Return the final string.

### Examples

```
Input:  s = "abcd", k = 2
Output: "abcd"
Explanation: No adjacent duplicates of length 2.
```

```
Input:  s = "deeedbbcccbdaa", k = 3
Output: "aa"
Explanation: "deeedbbcccbdaa" → "deedbbcccbdaa" (eee→e? No: remove eee) → "dbbcccbdaa"
             → "dbbcccbdaa" → "dbbbdaa" (ccc removed) → "dddaa" (bbb removed) → "aa" (ddd removed)
```

```
Input:  s = "pbbcggttciiippooaais", k = 2
Output: "ps"
```

### Approach: Stack of (Character, Count) Pairs

**Why this approach?**

Instead of repeatedly scanning the string (O(n²)), we maintain a stack of `(char, count)` pairs. For each new character:
- If it matches the top of the stack, increment the count.
- If the count reaches `k`, pop the pair (the group is removed).
- Otherwise, push a new `(char, 1)` pair.

This handles cascading removals automatically — when a group is removed, the characters beneath it are now adjacent and can form new groups.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public String removeDuplicates(String s, int k) {
        // Stack stores [character, count] pairs
        Deque<int[]> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek()[0] == c) {
                stack.peek()[1]++;
                if (stack.peek()[1] == k) {
                    stack.pop(); // remove the group of k
                }
            } else {
                stack.push(new int[]{c, 1});
            }
        }

        // Build result from stack (bottom to top)
        StringBuilder sb = new StringBuilder();
        for (int[] pair : stack) {
            sb.append(String.valueOf((char) pair[0]).repeat(pair[1]));
        }
        return sb.reverse().toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each character is pushed and popped at most once. Building the result is O(n). |
| **Space** | **O(n)** | The stack holds at most n entries (when no adjacent characters are the same). |

---

## Q66. Remove K Digits to Make Smallest Number

**Difficulty:** `Medium` | **Data Structure:** Stack (Monotonic)

### Problem

Given a non-negative integer represented as a string `num` and an integer `k`, remove `k` digits from `num` so that the resulting number is the **smallest possible**. Return the result as a string (without leading zeros).

### Examples

```
Input:  num = "1432219", k = 3
Output: "1219"
Explanation: Remove 4, 3, 2 → "1219"
```

```
Input:  num = "10200", k = 1
Output: "200"
Explanation: Remove 1 → "0200" → "200" (strip leading zeros)
```

```
Input:  num = "10", k = 2
Output: "0"
Explanation: Remove all digits → "0"
```

### Approach: Greedy Monotonic Stack

**Why this approach?**

To minimize the number, we want **smaller digits in higher (leftward) positions**. Scan left-to-right: whenever the current digit is smaller than the top of the stack, pop the stack (remove that larger digit). Each pop uses one of the `k` removals. This greedy strategy works because a locally suboptimal digit in a higher position always makes the overall number larger.

The stack effectively builds a **monotonically non-decreasing** sequence of digits (when possible).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public String removeKdigits(String num, int k) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char digit : num.toCharArray()) {
            // Remove larger digits from the top when a smaller digit arrives
            while (k > 0 && !stack.isEmpty() && stack.peek() > digit) {
                stack.pop();
                k--;
            }
            stack.push(digit);
        }

        // If we still need to remove digits, remove from the top (largest at the end)
        while (k > 0) {
            stack.pop();
            k--;
        }

        // Build result (stack is in reverse order)
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.pollLast());
        }

        // Strip leading zeros
        int start = 0;
        while (start < sb.length() - 1 && sb.charAt(start) == '0') {
            start++;
        }

        return sb.substring(start);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each digit is pushed and popped at most once. Leading-zero strip is O(n). |
| **Space** | **O(n)** | The stack and StringBuilder each hold up to n characters. |

---

## Q67. Stock Span Problem

**Difficulty:** `Medium` | **Data Structure:** Stack (Monotonic)

### Problem

Design a class `StockSpanner` that collects daily stock price quotes and returns the **span** of that day's price. The span is defined as the maximum number of consecutive days (starting from today and going backwards) for which the stock price was **less than or equal to** today's price.

### Examples

```
Input:  prices in order: [100, 80, 60, 70, 60, 75, 85]
Output: spans:           [  1,  1,  1,  2,  1,  4,  6]
Explanation for 85: 85 ≥ 75, 75 ≥ 60, 75 ≥ 70, 70 ≥ 60, but we count
                    85 ≥ 85(itself), 85 ≥ 75, 85 ≥ 60, 85 ≥ 70, 85 ≥ 60, 85 ≥ 80 → span = 6
```

```
Input:  [10, 20, 30]
Output: [ 1,  2,  3]
```

```
Input:  [30, 20, 10]
Output: [ 1,  1,  1]
```

### Approach: Monotonic Decreasing Stack with Span Accumulation

**Why this approach?**

We maintain a stack of `(price, span)` pairs in decreasing order. When a new price arrives, we pop all entries with price ≤ new price and **accumulate their spans**. The new entry's span is 1 (itself) plus all accumulated spans. This avoids re-scanning past days.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class StockSpanner {
    private final Deque<int[]> stack; // [price, span]

    public StockSpanner() {
        stack = new ArrayDeque<>();
    }

    public int next(int price) {
        int span = 1;

        while (!stack.isEmpty() && stack.peek()[0] <= price) {
            span += stack.pop()[1]; // absorb the span of the smaller/equal price
        }

        stack.push(new int[]{price, span});
        return span;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **Amortized O(1)** per `next()` call | Each price is pushed and popped at most once across all calls. Over n calls, total work is O(n). |
| **Space** | **O(n)** | The stack can hold up to n entries (if prices are strictly decreasing). |

---

## Q68. Validate Stack Sequences

**Difficulty:** `Medium` | **Data Structure:** Stack

### Problem

Given two integer arrays `pushed` and `popped`, each with distinct values, return `true` if this could have been the result of a sequence of push and pop operations on an initially empty stack.

### Examples

```
Input:  pushed = [1,2,3,4,5], popped = [4,5,3,2,1]
Output: true
Explanation: push 1,2,3,4 → pop 4 → push 5 → pop 5,3,2,1
```

```
Input:  pushed = [1,2,3,4,5], popped = [4,3,5,1,2]
Output: false
Explanation: After pushing 1,2,3,4 and popping 4,3 → stack is [1,2].
             Push 5, pop 5 → stack is [1,2]. Next pop should be 2, but popped wants 1.
```

```
Input:  pushed = [1], popped = [1]
Output: true
```

### Approach: Simulation

**Why this approach?**

Simulate the push/pop process greedily: push elements from `pushed` onto a stack. After each push, check if the top of the stack matches the next expected pop — if so, pop it and advance the pop pointer. If after processing all pushes the stack is empty, the sequence is valid.

This is the most direct and efficient approach. No alternative is simpler.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Deque<Integer> stack = new ArrayDeque<>();
        int popIdx = 0;

        for (int val : pushed) {
            stack.push(val);
            while (!stack.isEmpty() && stack.peek() == popped[popIdx]) {
                stack.pop();
                popIdx++;
            }
        }

        return stack.isEmpty();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each element is pushed once and popped at most once. The inner while loop across all iterations of the outer loop does at most n pops total. |
| **Space** | **O(n)** | The stack holds at most n elements. |

---

## Q69. Basic Calculator

**Difficulty:** `Hard` | **Data Structure:** Stack

### Problem

Implement a basic calculator to evaluate a simple expression string. The string may contain:
- Digits `0-9`
- `+` and `-` operators
- Parentheses `(` and `)`
- Spaces ` `

The expression is always valid. **No multiplication or division.**

### Examples

```
Input:  s = "1 + 1"
Output: 2
```

```
Input:  s = " 2-1 + 2 "
Output: 3
```

```
Input:  s = "(1+(4+5+2)-3)+(6+8)"
Output: 23
Explanation: 1 + (11) - 3 + 14 = 23
```

### Approach: Sign Stack

**Why this approach?**

The key insight: parentheses don't change operator precedence (only `+` and `-` exist), but a **minus before a parenthesis flips all signs inside**. We track the effective sign using a stack:

1. `sign` = current sign (+1 or -1).
2. When we encounter `(`, push the current cumulative sign onto the stack.
3. When we encounter `)`, pop from the stack.
4. The effective sign for any number = `stack.peek() * currentSign`.

**Step-by-step trace** for `"(1+(4+5+2)-3)+(6+8)"`:

```
Char  | Action                          | result | sign | stack
------|---------------------------------|--------|------|------
(     | push(+1)                        |   0    |  +1  | [+1, +1]
1     | result += +1 * +1 * 1 = 1       |   1    |  +1  | [+1, +1]
+     | sign = +1                       |   1    |  +1  | [+1, +1]
(     | push(+1*+1 = +1)               |   1    |  +1  | [+1, +1, +1]
4     | result += +1 * 4 = 4            |   5    |  +1  |
+     | sign = +1                       |   5    |  +1  |
5     | result += +1 * 5 = 5            |  10    |  +1  |
+     | sign = +1                       |  10    |  +1  |
2     | result += +1 * 2 = 2            |  12    |  +1  |
)     | pop                             |  12    |  +1  | [+1, +1]
-     | sign = -1                       |  12    |  -1  | [+1, +1]
3     | result += +1 * -1 * 3 = -3      |   9    |  -1  | [+1, +1]
)     | pop                             |   9    |  +1  | [+1]
+     | sign = +1                       |   9    |  +1  | [+1]
(     | push(+1*+1=+1)                 |   9    |  +1  | [+1, +1]
6     | result += +1 * 6 = 6            |  15    |  +1  |
+     | sign = +1                       |  15    |  +1  |
8     | result += +1 * 8 = 8            |  23    |  +1  |
)     | pop                             |  23    |      | [+1]
                                        Result = 23 ✓
```

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int calculate(String s) {
        Deque<Integer> signStack = new ArrayDeque<>();
        signStack.push(1); // base sign: positive

        int result = 0;
        int sign = 1;
        int i = 0;

        while (i < s.length()) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                int num = 0;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + (s.charAt(i) - '0');
                    i++;
                }
                result += signStack.peek() * sign * num;
                continue; // i already advanced past the number
            }

            switch (c) {
                case '+' -> sign = 1;
                case '-' -> sign = -1;
                case '(' -> {
                    signStack.push(signStack.peek() * sign);
                    sign = 1; // reset sign inside parentheses
                }
                case ')' -> signStack.pop();
                // space: skip
            }
            i++;
        }

        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through the string. Each character processed once. |
| **Space** | **O(n)** | Stack depth equals the maximum nesting depth of parentheses (at most n/2). |

---

## Q70. Basic Calculator II

**Difficulty:** `Hard` | **Data Structure:** Stack

### Problem

Implement a calculator to evaluate a string expression containing:
- Digits `0-9`
- `+`, `-`, `*`, `/` operators
- Spaces ` `
- **No parentheses**

Division truncates toward zero. Operator precedence: `*` and `/` before `+` and `-`.

### Examples

```
Input:  s = "3+2*2"
Output: 7
Explanation: 2*2 = 4, then 3+4 = 7
```

```
Input:  s = " 3/2 "
Output: 1
```

```
Input:  s = " 3+5 / 2 "
Output: 5
Explanation: 5/2 = 2, then 3+2 = 5
```

### Approach: Deferred Evaluation with Stack

**Why this approach?**

Since `*` and `/` have higher precedence, we must evaluate them immediately, but defer `+` and `-`. We track the **previous operator**:
- `+` → push `+num` onto the stack
- `-` → push `-num` onto the stack
- `*` → pop the top, multiply with `num`, push the result
- `/` → pop the top, divide by `num`, push the result

At the end, sum all values on the stack.

**Step-by-step trace** for `"3+2*2"`:

```
Token | prevOp | Stack      | Action
------|--------|------------|----------------------------------
  3   |  '+'   | [3]        | prevOp is +, push +3
  +   |  '+'   |            | set prevOp = '+'
  2   |  '+'   | [3, 2]     | prevOp is +, push +2
  *   |  '*'   |            | set prevOp = '*'
  2   |  '*'   | [3, 4]     | prevOp is *, pop 2, push 2*2=4
                              Sum stack: 3 + 4 = 7 ✓
```

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int num = 0;
        char prevOp = '+';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }

            // Process when we hit an operator or the end of the string
            if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
                switch (prevOp) {
                    case '+' -> stack.push(num);
                    case '-' -> stack.push(-num);
                    case '*' -> stack.push(stack.pop() * num);
                    case '/' -> stack.push(stack.pop() / num);
                }
                prevOp = c;
                num = 0;
            }
        }

        int result = 0;
        for (int val : stack) {
            result += val;
        }
        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass to parse and evaluate; one pass to sum the stack. |
| **Space** | **O(n)** | Stack can hold up to n/2 values (all additions, no multiplications). Can be optimized to O(1) by tracking running sum. |

---

## Q71. Largest Rectangle in Histogram

**Difficulty:** `Hard` | **Data Structure:** Stack (Monotonic)

### Problem

Given an array of integers `heights` representing the histogram's bar heights (each bar has width 1), find the area of the **largest rectangle** that can be formed within the histogram.

### Examples

```
Input:  heights = [2,1,5,6,2,3]
Output: 10
Explanation: The largest rectangle spans bars of height 5 and 6 → width=2, height=5, area=10.
```

```
Input:  heights = [2,4]
Output: 4
```

```
Input:  heights = [1,1,1,1,1]
Output: 5
Explanation: Rectangle of height 1 spanning all 5 bars.
```

### Approach: Monotonic Increasing Stack

**Why this approach?**

For each bar, we need to know how far **left** and **right** it can extend as the minimum height. A **monotonic increasing stack** (bars in non-decreasing height order) computes this in one pass:

- We push bar indices onto the stack, maintaining increasing heights.
- When a bar is **shorter** than the stack top, the stack-top bar has found its right boundary (current index). Its left boundary is the bar below it in the stack (or -1 if the stack is empty).
- We pop and compute the area for each such bar.

**Visual walk-through** for `[2, 1, 5, 6, 2, 3]`:

```
i | h[i] | Stack (idx) | Action                                           | Area computed
--|------|-------------|--------------------------------------------------|-------------
0 |  2   | [0]         | push 0                                           |
1 |  1   | []→[1]      | h[1]=1 < h[0]=2: pop 0                           | h=2, w=1-(-1)-1=1, area=2
  |      |             |   (stack empty → left boundary = -1)              |
  |      |             | push 1                                           |
2 |  5   | [1,2]       | push 2                                           |
3 |  6   | [1,2,3]     | push 3                                           |
4 |  2   | [1,2]→[1,4] | h[4]=2 < h[3]=6: pop 3                           | h=6, w=4-2-1=1, area=6
  |      |             | h[4]=2 < h[2]=5: pop 2                           | h=5, w=4-1-1=2, area=10 ★
  |      |             | h[4]=2 ≥ h[1]=1: stop. push 4                    |
5 |  3   | [1,4,5]     | push 5                                           |
-- end of array: pop remaining --
  | pop 5| [1,4]       | h=3, w=6-4-1=1, area=3                           |
  | pop 4| [1]         | h=2, w=6-1-1=4, area=8                           |
  | pop 1| []          | h=1, w=6-(-1)-1=6, area=6                        |

Maximum area = 10 ✓
```

**Alternative rejected:** Brute force considers every pair of bars → O(n²). Divide-and-conquer (segment tree) achieves O(n log n) but is more complex. The monotonic stack achieves optimal O(n).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int maxArea = 0;
        Deque<Integer> stack = new ArrayDeque<>(); // stores indices

        for (int i = 0; i <= n; i++) {
            // Use height 0 as a sentinel at the end to flush the stack
            int currentHeight = (i == n) ? 0 : heights[i];

            while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : (i - stack.peek() - 1);
                maxArea = Math.max(maxArea, height * width);
            }

            stack.push(i);
        }

        return maxArea;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each bar index is pushed once and popped once. The sentinel bar at the end ensures all bars are processed. |
| **Space** | **O(n)** | The stack holds at most n+1 indices. |

---

## Q72. Trapping Rain Water (Stack Approach)

**Difficulty:** `Hard` | **Data Structure:** Stack (Monotonic)

### Problem

Given `n` non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.

### Examples

```
Input:  height = [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6
```

```
Input:  height = [4,2,0,3,2,5]
Output: 9
```

```
Input:  height = [1,0,1]
Output: 1
```

### Approach: Monotonic Decreasing Stack (Layer by Layer)

**Why this approach?**

While the two-pointer approach calculates water **column by column**, the stack approach calculates water **layer by layer** (horizontally). We maintain a decreasing stack of indices:

- When the current bar is **taller** than the stack top, we've found a "valley" — the stack top is the bottom of a pool.
- Pop the bottom, and the new stack top is the left wall. The current bar is the right wall.
- Water trapped = `(min(leftWall, rightWall) - bottom) × width`.

**Visual walk-through** for `[0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]`:

```
i | h[i] | Stack (idx→h) | Action                                       | Water
--|------|---------------|----------------------------------------------|------
0 |  0   | [0→0]         | push                                         |
1 |  1   | [1→1]         | pop 0(h=0): stack empty, no left wall. push 1|  0
2 |  0   | [1,2]         | push (0<1)                                   |
3 |  2   | ...           | pop 2(h=0): bottom=0, left=h[1]=1, right=h[3]=2
  |      |               |   water = (min(1,2)-0)*(3-1-1) = 1*1 = 1     |  1
  |      |               | pop 1(h=1): bottom=1, stack empty→no left wall|
  |      | [3→2]         | push 3                                       |
4 |  1   | [3,4]         | push (1<2)                                   |
5 |  0   | [3,4,5]       | push (0<1)                                   |
6 |  1   | [3,4,6]       | pop 5(h=0): bottom=0, left=h[4]=1, right=h[6]=1
  |      |               |   water = (min(1,1)-0)*(6-4-1) = 1*1 = 1     |  2
  |      |               | h[6]=1 = h[4]=1, stop. push 6                |
7 |  3   | ...           | pop 6(h=1): bottom=1, left=h[4]=1, right=h[7]=3
  |      |               |   water = (min(1,3)-1)*(7-4-1) = 0*2 = 0     |  2
  |      |               | pop 4(h=1): bottom=1, left=h[3]=2, right=h[7]=3
  |      |               |   water = (min(2,3)-1)*(7-3-1) = 1*3 = 3     |  5
  |      |               | pop 3(h=2): bottom=2, stack empty→no left wall|
  |      | [7→3]         | push 7                                       |
8 |  2   | [7,8]         | push                                         |
9 |  1   | [7,8,9]       | push                                         |
10|  2   | [7,8,10]      | pop 9(h=1): bottom=1, left=h[8]=2, right=h[10]=2
  |      |               |   water = (min(2,2)-1)*(10-8-1) = 1*1 = 1    |  6
  |      |               | h[10]=2 = h[8]=2, stop. push 10              |
11|  1   | [7,8,10,11]   | push                                         |

Total water = 6 ✓
```

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int trap(int[] height) {
        Deque<Integer> stack = new ArrayDeque<>(); // decreasing stack of indices
        int water = 0;

        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = height[stack.pop()];

                if (stack.isEmpty()) break; // no left wall

                int leftIdx = stack.peek();
                int width = i - leftIdx - 1;
                int boundedHeight = Math.min(height[leftIdx], height[i]) - bottom;

                water += width * boundedHeight;
            }
            stack.push(i);
        }

        return water;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each index is pushed and popped at most once → total operations ≤ 2n. |
| **Space** | **O(n)** | The stack holds at most n indices (strictly decreasing sequence). |

---

## Q73. Maximum Frequency Stack

**Difficulty:** `Hard` | **Data Structure:** Stack, HashMap

### Problem

Design a stack-like data structure that pushes elements and pops the **most frequent** element. If there is a tie in frequency, pop the element **closest to the stack's top** (most recently pushed).

Implement `FreqStack`:
- `push(val)` — Push val onto the stack.
- `pop()` — Remove and return the most frequent element. If tie, return the most recently pushed among the tied elements.

### Examples

```
Input:  push(5), push(7), push(5), push(7), push(4), push(5)
        pop(), pop(), pop(), pop()
Output: 5, 7, 5, 4

Explanation after all pushes:
  Freq 1: [5, 7, 4]
  Freq 2: [5, 7]
  Freq 3: [5]
  maxFreq = 3

  pop() → 5 (freq 3), maxFreq drops to 2
  pop() → 7 (freq 2, most recent among freq-2 elements)
  pop() → 5 (freq 2, next in freq-2 stack)
  pop() → 4 (freq 1, most recent among freq-1 elements)
```

```
Input:  push(1), push(1), push(1), pop(), push(1), pop()
Output: pop() -> 1, pop() -> 1
```

```
Input:  push(4), push(0), push(9), push(3), push(4), push(2)
        pop(), pop()
Output: 4, 2
```

### Approach: Frequency Map + Stack of Stacks

**Why this approach?**

We maintain:
1. `freqMap`: maps each `val` → current frequency.
2. `groupMap`: maps each frequency → a stack of values that achieved that frequency.
3. `maxFreq`: the current maximum frequency.

**Push:** Increment val's frequency. Push val onto `groupMap[newFreq]`. Update `maxFreq`.

**Pop:** Pop from `groupMap[maxFreq]`. Decrement the popped val's frequency. If `groupMap[maxFreq]` is now empty, decrement `maxFreq`.

The key insight: an element with frequency `f` appears in groups `1, 2, ..., f`. When we pop it from group `f`, it naturally "falls back" to group `f-1` without any explicit bookkeeping.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class FreqStack {
    private final Map<Integer, Integer> freqMap;                // val -> frequency
    private final Map<Integer, Deque<Integer>> groupMap;        // freq -> stack of vals
    private int maxFreq;

    public FreqStack() {
        freqMap = new HashMap<>();
        groupMap = new HashMap<>();
        maxFreq = 0;
    }

    public void push(int val) {
        int freq = freqMap.merge(val, 1, Integer::sum);
        groupMap.computeIfAbsent(freq, k -> new ArrayDeque<>()).push(val);
        maxFreq = Math.max(maxFreq, freq);
    }

    public int pop() {
        Deque<Integer> topGroup = groupMap.get(maxFreq);
        int val = topGroup.pop();

        freqMap.merge(val, -1, Integer::sum);

        if (topGroup.isEmpty()) {
            groupMap.remove(maxFreq);
            maxFreq--;
        }

        return val;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per push/pop | All operations use HashMap lookups and stack push/pop — all O(1) amortized. |
| **Space** | **O(n)** | Each push adds one entry to a group stack and one entry to the frequency map. Over n pushes, total storage is O(n). |

---

## Q74. Longest Valid Parentheses

**Difficulty:** `Hard` | **Data Structure:** Stack

### Problem

Given a string containing only `'('` and `')'`, find the length of the **longest valid (well-formed) parentheses substring**.

### Examples

```
Input:  s = "(()"
Output: 2
Explanation: The longest valid parentheses substring is "()".
```

```
Input:  s = ")()())"
Output: 4
Explanation: The longest valid parentheses substring is "()()".
```

```
Input:  s = ""
Output: 0
```

### Approach: Stack with Index Tracking

**Why this approach?**

The stack stores **indices** of unmatched parentheses. The key idea:
1. Initialize the stack with `-1` as a "base" marker (the index before the start of a valid substring).
2. For `'('`: push its index.
3. For `')'`: pop the top.
   - If the stack becomes **empty**, this `)` is unmatched — push its index as the new base marker.
   - Otherwise, the valid substring length = `current index - stack.peek()`.

**Why this works:** At any point, `stack.peek()` gives the index of the last unmatched character (or the base marker). The distance from the current index to that marker gives the length of the valid substring ending at the current index.

**Step-by-step trace** for `")()())"`:

```
i | char | Stack     | Action                           | maxLen
--|------|-----------|----------------------------------|-------
  |      | [-1]      | initial base                     | 0
0 | )    | [0]       | pop -1, stack empty → push 0     | 0
1 | (    | [0, 1]    | push 1                           | 0
2 | )    | [0]       | pop 1 → len = 2-0 = 2            | 2
3 | (    | [0, 3]    | push 3                           | 2
4 | )    | [0]       | pop 3 → len = 4-0 = 4            | 4
5 | )    | [5]       | pop 0, stack empty → push 5      | 4

Result = 4 ✓
```

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int longestValidParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(-1); // base marker

        int maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                stack.pop();
                if (stack.isEmpty()) {
                    stack.push(i); // new base marker
                } else {
                    maxLen = Math.max(maxLen, i - stack.peek());
                }
            }
        }

        return maxLen;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through the string. Each index is pushed and popped at most once. |
| **Space** | **O(n)** | Stack can hold up to n+1 elements (all opening parentheses plus the base marker). |

---

## Q75. Number of Visible People in a Queue

**Difficulty:** `Hard` | **Data Structure:** Stack (Monotonic)

### Problem

There are `n` people standing in a queue, represented by an array `heights`. A person `i` can **see** person `j` (where `j > i`) if everyone between them is shorter than **both** `heights[i]` and `heights[j]`. Return an array `answer` where `answer[i]` is the number of people that person `i` can see to their right.

More precisely, person `i` can see person `j` if `min(heights[i], heights[j]) > max(heights[k])` for all `k` between `i` and `j`.

### Examples

```
Input:  heights = [10,6,8,5,11,9]
Output:          [ 3,1,2,1, 1,0]

Explanation:
  Person 0 (h=10): sees 6 (shorter, visible), 8 (shorter, visible), 
                    5 (blocked by 8? No — 5<8<10 means 8 blocks 5... 
                    wait: 10 sees past 6 to 8, past 8 to 11 but not 5 because 8>5)
                    Actually: 0 sees 6, 8, 11 → 3 people
  Person 1 (h=6):  sees 8 (8>6, stops) → 1
  Person 2 (h=8):  sees 5, 11 (5 is shorter; 11>8 stops) → 2  
  Person 3 (h=5):  sees 11 (11>5, stops) → 1
  Person 4 (h=11): sees 9 → 1
  Person 5 (h=9):  nobody → 0
```

```
Input:  heights = [5,1,2,3,10]
Output:          [4,1,1,1, 0]
```

```
Input:  heights = [3,1,5,2,4]
Output:          [2,1,2,1,0]
```

### Approach: Monotonic Decreasing Stack (Right to Left)

**Why this approach?**

We traverse **right to left**, maintaining a **monotonic decreasing stack** of heights. For each person `i`:
1. Count how many people in the stack are **shorter** than `heights[i]` — these are visible (pop them; they are "consumed" because no one further left can see past person `i` to reach them).
2. If the stack is non-empty after popping (there's a taller or equal person remaining), that person is also visible (+1).
3. Push `heights[i]` onto the stack.

**Why pop shorter people?** Once person `i` has "seen" them, no person further to the left (with index < i) can see them — person `i` blocks the view.

**Walk-through** for `[10, 6, 8, 5, 11, 9]` (right to left):

```
i | h[i] | Stack (top→)    | Popped (shorter) | +1 for taller? | answer[i]
--|------|-----------------|------------------|----------------|----------
5 |  9   | [9]             | none             | no             | 0
4 | 11   | [11]            | 9 (1 popped)     | stack empty: no| 1
3 |  5   | [11, 5]         | none             | yes (11)       | 0+1=1
2 |  8   | [11, 8]         | 5 (1 popped)     | yes (11)       | 1+1=2
1 |  6   | [11, 8, 6]      | none             | yes (8)        | 0+1=1
0 | 10   | [11, 10]        | 6,8 (2 popped)   | yes (11)       | 2+1=3

Result: [3, 1, 2, 1, 1, 0] ✓
```

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] canSeePersonsCount(int[] heights) {
        int n = heights.length;
        int[] answer = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // monotonic decreasing stack of heights

        for (int i = n - 1; i >= 0; i--) {
            int count = 0;

            // Pop all shorter people — person i can see each of them
            while (!stack.isEmpty() && stack.peek() < heights[i]) {
                stack.pop();
                count++;
            }

            // If stack is non-empty, person i also sees the first taller/equal person
            if (!stack.isEmpty()) {
                count++;
            }

            answer[i] = count;
            stack.push(heights[i]);
        }

        return answer;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each height is pushed once and popped at most once. Total stack operations ≤ 2n. |
| **Space** | **O(n)** | The stack can hold up to n heights (strictly decreasing sequence). |

---
---

## Summary: Stack Patterns Cheat Sheet

| Pattern | Problems | Key Idea |
|---|---|---|
| **Basic LIFO** | Q51, Q53, Q54 | Push/pop mechanics, LIFO order |
| **Bracket Matching** | Q52, Q74 | Push openers, match on closers |
| **Monotonic Decreasing Stack** | Q58, Q59, Q60, Q67, Q72, Q75 | Find next greater / previous greater element |
| **Monotonic Increasing Stack** | Q66, Q71 | Find next smaller / previous smaller element |
| **Expression Evaluation** | Q57, Q69, Q70 | Operand stack, operator precedence |
| **Context Save/Restore** | Q62, Q63, Q69 | Push context on `[` or `(`, restore on `]` or `)` |
| **Simulation** | Q64, Q68 | Model real-world processes with stack |
| **Stack + HashMap** | Q56, Q65, Q73 | Augmented stack for O(1) queries |
| **Recursive Stack** | Q55 | Use call stack as implicit storage |
# QUEUE

> **Questions Q76 - Q100** | 25 Problems covering Queue, Circular Queue, Deque, BFS, and Monotonic Deque patterns.

## Mini-Index

| # | Title | Difficulty | Key Pattern |
|---|---|---|---|
| Q76 | Implement Queue using Array (Circular) | Basic | Circular buffer |
| Q77 | Implement Queue using Two Stacks | Basic | Amortized transfer |
| Q78 | Implement Queue using Linked List | Basic | Singly linked list |
| Q79 | Generate Binary Numbers from 1 to N | Basic | BFS generation |
| Q80 | Number of Recent Calls | Basic | Sliding window queue |
| Q81 | Implement Circular Queue (Design) | Medium | Circular buffer design |
| Q82 | Implement Deque (Double-Ended Queue) | Medium | Circular array deque |
| Q83 | First Non-Repeating Character in Stream | Medium | Queue + frequency map |
| Q84 | Implement Stack using Two Queues | Medium | Queue reversal trick |
| Q85 | Reverse First K Elements of Queue | Medium | Stack-queue interplay |
| Q86 | Interleave First Half with Second Half | Medium | Two-half merge |
| Q87 | Rotting Oranges | Medium | Multi-source BFS |
| Q88 | Walls and Gates | Medium | Multi-source BFS |
| Q89 | Moving Average from Data Stream | Medium | Sliding window |
| Q90 | Task Scheduler | Medium | Greedy + cooldown |
| Q91 | Design Hit Counter | Medium | Queue-based counting |
| Q92 | Zigzag Iterator | Medium | Round-robin queuing |
| Q93 | Design Circular Deque | Medium | Circular array deque |
| Q94 | Time Needed to Buy Tickets | Medium | Queue simulation |
| Q95 | Sliding Window Maximum | Hard | Monotonic deque |
| Q96 | Shortest Subarray with Sum at Least K | Hard | Prefix sum + deque |
| Q97 | Jump Game VI | Hard | DP + monotonic deque |
| Q98 | Design Snake Game | Hard | Queue simulation |
| Q99 | Maximum of All Subarrays of Size K | Hard | Monotonic deque |
| Q100 | Process Tasks Using Servers | Hard | Priority queue + queue |

---
---

## Q76. Implement Queue using Array (Circular)

**Difficulty:** `Basic` | **Data Structure:** Queue / Circular Buffer

### Problem

Design a queue that uses a fixed-size array as its underlying storage. The queue must support:

- `enqueue(value)` - Add an element to the rear. Throw an exception if the queue is full.
- `dequeue()` - Remove and return the element from the front. Throw an exception if the queue is empty.
- `peek()` - Return the front element without removing it.
- `isEmpty()` / `isFull()` / `size()` - Utility methods.

The key challenge is avoiding wasted space: after several enqueue/dequeue cycles, a naive array implementation wastes the front portion. A **circular** (ring buffer) approach wraps indices around using modulo arithmetic.

### Examples

```
Example 1:
  Queue capacity = 5
  enqueue(10) -> [10, _, _, _, _]   front=0, rear=0
  enqueue(20) -> [10, 20, _, _, _]  front=0, rear=1
  enqueue(30) -> [10, 20, 30, _, _] front=0, rear=2
  dequeue()   -> returns 10, [_, 20, 30, _, _] front=1, rear=2
  dequeue()   -> returns 20, [_, _, 30, _, _]  front=2, rear=2
  enqueue(40) -> [_, _, 30, 40, _]  front=2, rear=3
  enqueue(50) -> [_, _, 30, 40, 50] front=2, rear=4
  enqueue(60) -> [60, _, 30, 40, 50] front=2, rear=0  <-- wraps around!

Example 2:
  Queue capacity = 3
  enqueue(1), enqueue(2), enqueue(3) -> full
  enqueue(4) -> throws "Queue is full"
  dequeue() -> 1
  enqueue(4) -> succeeds, array is [4, 2, 3] with front=1, rear=0
```

### Approach: Circular Array with Modulo Arithmetic

**Why this approach?**

A naive array queue shifts all elements on dequeue (O(n)) or wastes space at the front. The circular buffer solves both:
- We maintain `front` and `rear` pointers and a `count`.
- On enqueue: place at `rear`, then `rear = (rear + 1) % capacity`.
- On dequeue: read from `front`, then `front = (front + 1) % capacity`.
- Full/empty are distinguished using the `count` variable (alternative: waste one slot, or use a boolean flag).

This gives O(1) for all operations with zero wasted space (beyond the fixed array).

### Optimized Java Solution

```java
public class CircularArrayQueue {
    private final int[] data;
    private final int capacity;
    private int front;  // index of the front element
    private int rear;   // index of the NEXT insertion point
    private int count;

    public CircularArrayQueue(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.front = 0;
        this.rear = 0;
        this.count = 0;
    }

    public void enqueue(int value) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }
        data[rear] = value;
        rear = (rear + 1) % capacity;
        count++;
    }

    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        int value = data[front];
        front = (front + 1) % capacity;
        count--;
        return value;
    }

    public int peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return data[front];
    }

    public boolean isEmpty() { return count == 0; }
    public boolean isFull()  { return count == capacity; }
    public int size()        { return count; }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per operation | enqueue, dequeue, peek, isEmpty, isFull, size are all constant time. No shifting or copying needed. |
| **Space** | **O(n)** | Where n = capacity. We allocate a fixed-size array once. |

---

## Q77. Implement Queue using Two Stacks

**Difficulty:** `Basic` | **Data Structure:** Queue / Stack

### Problem

Implement a FIFO queue using only two LIFO stacks. Support the standard queue operations:
- `push(x)` - Push element to the back of the queue.
- `pop()` - Remove the element from the front and return it.
- `peek()` - Get the front element.
- `empty()` - Return whether the queue is empty.

You must use only standard stack operations: `push`, `pop`, `peek/top`, `size`, `isEmpty`.

*(LeetCode 232)*

### Examples

```
Example 1:
  push(1), push(2)
  peek()  -> 1
  pop()   -> 1
  empty() -> false
  pop()   -> 2
  empty() -> true

Example 2:
  push(10), push(20), push(30)
  pop()  -> 10
  push(40)
  pop()  -> 20
  pop()  -> 30
  pop()  -> 40
```

### Approach: Amortized Transfer (Lazy Transfer)

**Why this approach?**

We use two stacks: `inStack` (for pushes) and `outStack` (for pops/peeks).

- **push**: Always push onto `inStack`. O(1).
- **pop/peek**: If `outStack` is empty, transfer ALL elements from `inStack` to `outStack` (reversing order to restore FIFO). Then pop/peek from `outStack`.

**Why not eager transfer?** Eager transfer (moving elements on every push) costs O(n) per push. The lazy approach amortizes the transfer cost: each element is moved at most once from `inStack` to `outStack`, giving **amortized O(1)** per operation.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class QueueUsingTwoStacks {
    private final Deque<Integer> inStack  = new ArrayDeque<>();
    private final Deque<Integer> outStack = new ArrayDeque<>();

    public void push(int x) {
        inStack.push(x);
    }

    public int pop() {
        shiftIfNeeded();
        return outStack.pop();
    }

    public int peek() {
        shiftIfNeeded();
        return outStack.peek();
    }

    public boolean empty() {
        return inStack.isEmpty() && outStack.isEmpty();
    }

    private void shiftIfNeeded() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **Amortized O(1)** per operation | Each element is pushed/popped from each stack at most once. Over n operations, total work is O(n), so amortized O(1) per operation. Worst-case single pop is O(n) when a bulk transfer is needed. |
| **Space** | **O(n)** | Where n is the number of elements in the queue. All elements reside across the two stacks. |

---

## Q78. Implement Queue using Linked List

**Difficulty:** `Basic` | **Data Structure:** Queue / Linked List

### Problem

Implement a queue using a singly linked list. Support:
- `enqueue(value)` - Add to the rear.
- `dequeue()` - Remove from the front and return the value.
- `peek()` - View the front element.
- `isEmpty()` / `size()`

Unlike the array-based queue, this has no fixed capacity (dynamic sizing).

### Examples

```
Example 1:
  enqueue(1)  -> head->[1]->null, tail->[1]
  enqueue(2)  -> head->[1]->[2]->null, tail->[2]
  enqueue(3)  -> head->[1]->[2]->[3]->null, tail->[3]
  dequeue()   -> returns 1, head->[2]->[3]->null
  peek()      -> 2
  size()      -> 2

Example 2:
  enqueue(5), enqueue(10)
  dequeue() -> 5
  dequeue() -> 10
  isEmpty() -> true
  dequeue() -> throws "Queue is empty"
```

### Approach: Head/Tail Pointer Linked List

**Why this approach?**

With a singly linked list, `dequeue` from the head is O(1) and `enqueue` at the tail is O(1) if we maintain a tail pointer. Without a tail pointer, enqueue would be O(n) since we'd have to traverse to the end.

This is simpler than a circular array (no capacity limits, no modulo math) and is the canonical dynamic queue implementation.

### Optimized Java Solution

```java
public class LinkedListQueue<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head; // front of queue (dequeue from here)
    private Node<T> tail; // rear of queue (enqueue here)
    private int size;

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        if (tail != null) {
            tail.next = newNode;
        }
        tail = newNode;
        if (head == null) {
            head = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        T value = head.data;
        head = head.next;
        if (head == null) {
            tail = null; // queue is now empty
        }
        size--;
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return head.data;
    }

    public boolean isEmpty() { return size == 0; }
    public int size()        { return size; }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per operation | enqueue appends at tail, dequeue removes from head. Both are pointer updates. |
| **Space** | **O(n)** | Each element requires a Node object (data + next pointer), so overhead is proportional to queue size. |

---

## Q79. Generate Binary Numbers from 1 to N using Queue

**Difficulty:** `Basic` | **Data Structure:** Queue

### Problem

Given a number `n`, generate and return a list of binary representations of all numbers from 1 to n.

### Examples

```
Example 1:
  Input:  n = 5
  Output: ["1", "10", "11", "100", "101"]

Example 2:
  Input:  n = 10
  Output: ["1", "10", "11", "100", "101", "110", "111", "1000", "1001", "1010"]

Example 3:
  Input:  n = 3
  Output: ["1", "10", "11"]
```

### Approach: BFS-style Generation with Queue

**Why this approach?**

Observe the pattern: starting from "1", each binary number generates two children by appending "0" and "1":
```
        "1"
       /    \
     "10"   "11"
     / \    / \
  "100" "101" "110" "111"
```

This is a level-order (BFS) traversal of a binary tree! We seed the queue with "1", then for each dequeued string, enqueue `s + "0"` and `s + "1"`. We collect the first `n` dequeued values.

**Why not convert integers?** `Integer.toBinaryString(i)` works but this approach demonstrates the power of BFS-based generation and avoids repeated integer-to-binary conversion.

### Optimized Java Solution

```java
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BinaryNumberGenerator {
    public List<String> generateBinaryNumbers(int n) {
        List<String> result = new ArrayList<>(n);
        Queue<String> queue = new LinkedList<>();
        queue.offer("1");

        for (int i = 0; i < n; i++) {
            String current = queue.poll();
            result.add(current);
            queue.offer(current + "0");
            queue.offer(current + "1");
        }
        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We perform exactly n dequeue operations and 2n enqueue operations. String concatenation per step is O(log n) in theory (binary length grows logarithmically), so precise bound is O(n log n). |
| **Space** | **O(n)** | The queue holds at most ~n elements at any time, and each binary string is O(log n) length. Output itself is O(n log n). |

---

## Q80. Number of Recent Calls (Request Counter with Time Window)

**Difficulty:** `Basic` | **Data Structure:** Queue

### Problem

Design a class `RecentCounter` that counts the number of recent requests within a certain time frame.

- `RecentCounter()` - Initializes the counter with zero requests.
- `int ping(int t)` - Adds a new request at time `t` (milliseconds) and returns the number of requests that have happened in the past 3000 milliseconds (inclusive), i.e., in the range `[t - 3000, t]`.

It is guaranteed that each call to `ping` uses a strictly larger value of `t` than the previous call.

*(LeetCode 933)*

### Examples

```
Example 1:
  Input:  ping(1)    -> Output: 1   (range [−2999, 1], requests: {1})
  Input:  ping(100)  -> Output: 2   (range [−2900, 100], requests: {1, 100})
  Input:  ping(3001) -> Output: 3   (range [1, 3001], requests: {1, 100, 3001})
  Input:  ping(3002) -> Output: 3   (range [2, 3002], requests: {100, 3001, 3002} — 1 is expired)

Example 2:
  Input:  ping(1000)  -> 1
  Input:  ping(2000)  -> 2
  Input:  ping(4500)  -> 2  (range [1500, 4500]: 1000 is outside, only 2000 and 4500)
```

### Approach: Queue with Sliding Window Cleanup

**Why this approach?**

Since timestamps arrive in increasing order, expired timestamps are always at the front of the queue. We maintain a queue of timestamps:
1. Add the new timestamp `t` to the rear.
2. Remove all timestamps from the front that are older than `t - 3000`.
3. Return the queue size.

**Why not a list/array with binary search?** A queue is simpler and more memory-efficient because we eagerly discard expired entries. Binary search would keep all entries and require O(log n) per query but waste memory.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class RecentCounter {
    private final Deque<Integer> queue;

    public RecentCounter() {
        queue = new ArrayDeque<>();
    }

    public int ping(int t) {
        queue.addLast(t);
        int cutoff = t - 3000;
        while (queue.peekFirst() < cutoff) {
            queue.pollFirst();
        }
        return queue.size();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **Amortized O(1)** per ping | Each timestamp is added once and removed at most once. Over n calls, total removals <= n, so amortized O(1). |
| **Space** | **O(W)** | Where W is the max number of requests in any 3000ms window. In the worst case, O(n) if all pings fall within 3000ms. |

---
---

## Q81. Implement Circular Queue (Design)

**Difficulty:** `Medium` | **Data Structure:** Queue / Circular Buffer

### Problem

Design a circular queue (ring buffer) with the following operations:

- `MyCircularQueue(k)` - Constructor, set the size of the queue to k.
- `boolean enQueue(int value)` - Insert an element. Return true if successful.
- `boolean deQueue()` - Delete an element. Return true if successful.
- `int Front()` - Get the front item. Return -1 if empty.
- `int Rear()` - Get the last item. Return -1 if empty.
- `boolean isEmpty()` / `boolean isFull()`

*(LeetCode 622)*

### Examples

```
Example 1:
  MyCircularQueue cq = new MyCircularQueue(3);
  cq.enQueue(1) -> true    // queue: [1]
  cq.enQueue(2) -> true    // queue: [1, 2]
  cq.enQueue(3) -> true    // queue: [1, 2, 3]
  cq.enQueue(4) -> false   // full
  cq.Rear()     -> 3
  cq.isFull()   -> true
  cq.deQueue()  -> true    // queue: [2, 3]
  cq.enQueue(4) -> true    // queue: [2, 3, 4]
  cq.Rear()     -> 4

Example 2:
  MyCircularQueue cq = new MyCircularQueue(2);
  cq.enQueue(1) -> true
  cq.enQueue(2) -> true
  cq.Front()    -> 1
  cq.Rear()     -> 2
  cq.deQueue()  -> true
  cq.Front()    -> 2
  cq.Rear()     -> 2
```

### Approach: Array with Front/Count Tracking

**Why this approach?**

Same principle as Q76, but this is the LeetCode design version. We use `front` index and `count` to distinguish full from empty (avoids the "waste one slot" trick). The rear index is computed as `(front + count - 1) % capacity`.

Using `count` instead of maintaining both `front` and `rear` independently simplifies the logic and eliminates ambiguity.

### Optimized Java Solution

```java
public class MyCircularQueue {
    private final int[] data;
    private final int capacity;
    private int front;
    private int count;

    public MyCircularQueue(int k) {
        this.capacity = k;
        this.data = new int[k];
        this.front = 0;
        this.count = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) return false;
        int rear = (front + count) % capacity;
        data[rear] = value;
        count++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) return false;
        front = (front + 1) % capacity;
        count--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : data[front];
    }

    public int Rear() {
        if (isEmpty()) return -1;
        int rear = (front + count - 1) % capacity;
        return data[rear];
    }

    public boolean isEmpty() { return count == 0; }
    public boolean isFull()  { return count == capacity; }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per operation | All operations are index computations and array accesses. |
| **Space** | **O(k)** | Fixed array of size k allocated at construction. |

---

## Q82. Implement Deque (Double-Ended Queue from Scratch)

**Difficulty:** `Medium` | **Data Structure:** Deque / Circular Array

### Problem

Design a double-ended queue (deque) from scratch supporting:
- `addFirst(val)` / `addLast(val)` - Insert at front/rear.
- `removeFirst()` / `removeLast()` - Remove and return from front/rear.
- `peekFirst()` / `peekLast()` - View front/rear without removing.
- `isEmpty()` / `size()`

Implement this with a circular array that dynamically resizes when full.

### Examples

```
Example 1:
  addLast(1)   -> [1]
  addLast(2)   -> [1, 2]
  addFirst(0)  -> [0, 1, 2]
  peekFirst()  -> 0
  peekLast()   -> 2
  removeFirst()-> 0, deque is [1, 2]
  removeLast() -> 2, deque is [1]

Example 2:
  addFirst(3)  -> [3]
  addFirst(2)  -> [2, 3]
  addFirst(1)  -> [1, 2, 3]
  addLast(4)   -> [1, 2, 3, 4]
  size()       -> 4
```

### Approach: Circular Resizable Array

**Why this approach?**

Java's `ArrayDeque` uses this exact technique internally. We maintain a circular array with `head` and `tail` pointers:
- `addFirst`: decrement `head` (wrapping), place element.
- `addLast`: place element at `tail`, increment `tail` (wrapping).
- When the array is full, double its capacity and copy elements in logical order.

**Why not linked list?** A doubly-linked list deque is simpler to code but has worse cache performance and higher memory overhead (two pointers per node). The circular array approach is what production libraries use.

### Optimized Java Solution

```java
import java.util.NoSuchElementException;

public class ResizableDeque<T> {
    private Object[] data;
    private int head; // index of the first element
    private int tail; // index AFTER the last element
    private int size;

    public ResizableDeque() {
        data = new Object[8]; // initial capacity
        head = 0;
        tail = 0;
        size = 0;
    }

    public void addFirst(T val) {
        ensureCapacity();
        head = (head - 1 + data.length) % data.length;
        data[head] = val;
        size++;
    }

    public void addLast(T val) {
        ensureCapacity();
        data[tail] = val;
        tail = (tail + 1) % data.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        T val = (T) data[head];
        data[head] = null; // help GC
        head = (head + 1) % data.length;
        size--;
        return val;
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        tail = (tail - 1 + data.length) % data.length;
        T val = (T) data[tail];
        data[tail] = null;
        size--;
        return val;
    }

    @SuppressWarnings("unchecked")
    public T peekFirst() {
        if (isEmpty()) throw new NoSuchElementException();
        return (T) data[head];
    }

    @SuppressWarnings("unchecked")
    public T peekLast() {
        if (isEmpty()) throw new NoSuchElementException();
        return (T) data[(tail - 1 + data.length) % data.length];
    }

    public boolean isEmpty() { return size == 0; }
    public int size()        { return size; }

    private void ensureCapacity() {
        if (size < data.length) return;
        Object[] newData = new Object[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(head + i) % data.length];
        }
        data = newData;
        head = 0;
        tail = size;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **Amortized O(1)** per operation | All operations are O(1) except when resizing, which copies n elements but happens infrequently. Doubling ensures amortized O(1). |
| **Space** | **O(n)** | The backing array is at most 2x the number of stored elements due to the doubling strategy. |

---

## Q83. First Non-Repeating Character in a Stream

**Difficulty:** `Medium` | **Data Structure:** Queue / HashMap

### Problem

Given a stream of characters, find the first non-repeating character at each point in the stream. If no non-repeating character exists, return `'#'`.

### Examples

```
Example 1:
  Stream: "aabcbd"
  After 'a': first non-repeating = 'a'  -> "a"
  After 'a': 'a' repeats, no other     -> "#"
  After 'b': first non-repeating = 'b'  -> "#b"
  After 'c': first non-repeating = 'b'  -> "#bb"
  After 'b': 'b' repeats, first = 'c'   -> "#bbc"
  After 'd': first non-repeating = 'c'  -> "#bbcc"

  Output at each step: ['a', '#', 'b', 'b', 'c', 'c']

Example 2:
  Stream: "abcabc"
  After 'a': 'a'
  After 'b': 'a'
  After 'c': 'a'
  After 'a': 'b' (a repeated)
  After 'b': 'c' (b repeated)
  After 'c': '#' (all repeated)

  Output: ['a', 'a', 'a', 'b', 'c', '#']
```

### Approach: Queue + Frequency Map

**Why this approach?**

We maintain:
1. A **frequency map** tracking how many times each character has appeared.
2. A **queue** holding characters in arrival order.

On each new character:
1. Increment its frequency.
2. Add it to the queue.
3. Drain the front of the queue while the front character has frequency > 1.
4. The front of the queue (if any) is the answer; otherwise `'#'`.

**Why not scan the entire map each time?** That would be O(alphabet) per character. The queue approach is amortized O(1) because each character is enqueued and dequeued at most once.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class FirstNonRepeatingStream {
    private final Map<Character, Integer> freq = new HashMap<>();
    private final Deque<Character> queue = new ArrayDeque<>();

    public char processChar(char ch) {
        freq.merge(ch, 1, Integer::sum);
        queue.addLast(ch);

        // Drain characters that have become repeating
        while (!queue.isEmpty() && freq.get(queue.peekFirst()) > 1) {
            queue.pollFirst();
        }

        return queue.isEmpty() ? '#' : queue.peekFirst();
    }

    /**
     * Process an entire stream and return the result after each character.
     */
    public String processStream(String stream) {
        StringBuilder result = new StringBuilder();
        for (char ch : stream.toCharArray()) {
            result.append(processChar(ch));
        }
        return result.toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **Amortized O(1)** per character | Each character is enqueued once and dequeued at most once. HashMap operations are O(1). |
| **Space** | **O(k)** | Where k is the number of distinct characters (at most 26 for lowercase English). Queue and map together hold at most k entries. |

---

## Q84. Implement Stack using Two Queues

**Difficulty:** `Medium` | **Data Structure:** Stack / Queue

### Problem

Implement a LIFO stack using only two FIFO queues. Support:
- `push(x)` - Push element onto the stack.
- `pop()` - Remove and return the top element.
- `top()` - Get the top element.
- `empty()` - Check if empty.

*(LeetCode 225)*

### Examples

```
Example 1:
  push(1), push(2)
  top()   -> 2
  pop()   -> 2
  empty() -> false
  pop()   -> 1
  empty() -> true

Example 2:
  push(10), push(20), push(30)
  pop()  -> 30
  pop()  -> 20
  push(40)
  top()  -> 40
```

### Approach: Push-Costly with Single Queue (Optimized)

**Why this approach?**

The classic two-queue solution moves n-1 elements on pop. A more elegant approach uses a **single queue**: on `push(x)`, enqueue x, then rotate the queue by dequeuing and re-enqueuing the first `size - 1` elements. This puts x at the front. Now `pop` and `top` are O(1).

**Why push-costly over pop-costly?** In many real scenarios, pops/tops are more frequent than pushes (e.g., processing stacks). Making pop O(1) is generally more desirable.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Queue;

public class StackUsingQueues {
    private final Queue<Integer> queue = new ArrayDeque<>();

    public void push(int x) {
        queue.offer(x);
        // Rotate: move all elements before x to the back
        int rotations = queue.size() - 1;
        for (int i = 0; i < rotations; i++) {
            queue.offer(queue.poll());
        }
    }

    public int pop() {
        return queue.poll();
    }

    public int top() {
        return queue.peek();
    }

    public boolean empty() {
        return queue.isEmpty();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **push: O(n), pop/top: O(1)** | Push rotates all existing elements. Pop and top are direct queue operations. |
| **Space** | **O(n)** | Single queue holding all n elements. |

---

## Q85. Reverse First K Elements of Queue

**Difficulty:** `Medium` | **Data Structure:** Queue / Stack

### Problem

Given a queue of integers and a number `k`, reverse the order of the first `k` elements of the queue, leaving the remaining elements in the same relative order.

### Examples

```
Example 1:
  Input:  queue = [1, 2, 3, 4, 5], k = 3
  Output: [3, 2, 1, 4, 5]
  Explanation: First 3 elements [1,2,3] reversed to [3,2,1], rest [4,5] stays.

Example 2:
  Input:  queue = [10, 20, 30, 40, 50, 60], k = 4
  Output: [40, 30, 20, 10, 50, 60]

Example 3:
  Input:  queue = [1, 2, 3], k = 3
  Output: [3, 2, 1]
```

### Approach: Stack for Reversal + Queue Rotation

**Why this approach?**

1. **Dequeue** the first k elements and push them onto a **stack** (reverses order).
2. **Pop** all k elements from the stack back into the queue.
3. **Rotate** the remaining `n - k` elements from the front to the back (dequeue and re-enqueue).

This preserves the relative order of the remaining elements while reversing exactly the first k.

**Why use a stack?** A stack naturally reverses order. Alternatives (like using an array and swapping) are more complex and offer no advantage.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class ReverseFirstKElements {
    public static void reverseFirstK(Queue<Integer> queue, int k) {
        if (queue == null || k <= 0 || k > queue.size()) return;

        Deque<Integer> stack = new ArrayDeque<>();

        // Step 1: Dequeue first k elements into stack
        for (int i = 0; i < k; i++) {
            stack.push(queue.poll());
        }

        // Step 2: Push stack elements back to queue (reversed order)
        while (!stack.isEmpty()) {
            queue.offer(stack.pop());
        }

        // Step 3: Rotate the remaining (n - k) elements to the back
        int remaining = queue.size() - k;
        for (int i = 0; i < remaining; i++) {
            queue.offer(queue.poll());
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We touch each element a constant number of times: k elements go to stack and back, (n-k) elements are rotated. |
| **Space** | **O(k)** | Stack holds at most k elements. The operation is done in-place on the queue otherwise. |

---

## Q86. Interleave First Half with Second Half of Queue

**Difficulty:** `Medium` | **Data Structure:** Queue

### Problem

Given a queue of even length, interleave the first half with the second half. The first element of the first half should come first, then the first element of the second half, then the second element of the first half, and so on.

### Examples

```
Example 1:
  Input:  [1, 2, 3, 4, 5, 6, 7, 8]
  First half:  [1, 2, 3, 4]
  Second half: [5, 6, 7, 8]
  Output: [1, 5, 2, 6, 3, 7, 4, 8]

Example 2:
  Input:  [10, 20, 30, 40]
  First half:  [10, 20]
  Second half: [30, 40]
  Output: [10, 30, 20, 40]

Example 3:
  Input:  [1, 2]
  Output: [1, 2]  (already interleaved)
```

### Approach: Split into Two Halves, Merge

**Why this approach?**

1. Dequeue the first half into a temporary queue (or use the same queue with rotation).
2. Interleave: alternately enqueue from the first-half storage and from the remaining queue.

This is clean and intuitive. The approach using only one extra queue is preferred over in-place tricks that are error-prone with index math.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Queue;

public class InterleaveQueue {
    public static void interleave(Queue<Integer> queue) {
        int n = queue.size();
        if (n <= 2) return;

        int half = n / 2;
        Queue<Integer> firstHalf = new ArrayDeque<>();

        // Step 1: Move first half to temporary queue
        for (int i = 0; i < half; i++) {
            firstHalf.offer(queue.poll());
        }

        // Step 2: Interleave - alternate from firstHalf and queue (which has second half)
        while (!firstHalf.isEmpty()) {
            queue.offer(firstHalf.poll());  // from first half
            queue.offer(queue.poll());       // from second half
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | We move n/2 elements to a temp queue, then interleave n elements total. Each element is touched O(1) times. |
| **Space** | **O(n)** | Temporary queue holds n/2 elements. |

---

## Q87. Rotting Oranges (BFS Multi-Source)

**Difficulty:** `Medium` | **Data Structure:** Queue / BFS

### Problem

You are given an `m x n` grid where each cell can have one of three values:
- `0` - Empty cell
- `1` - Fresh orange
- `2` - Rotten orange

Every minute, any fresh orange adjacent (4-directionally) to a rotten orange becomes rotten. Return the minimum number of minutes until no cell has a fresh orange. If impossible, return `-1`.

*(LeetCode 994)*

### Examples

```
Example 1:
  Input:  grid = [[2,1,1],
                   [1,1,0],
                   [0,1,1]]
  Output: 4

  Trace (level-by-level BFS):
  Minute 0: Rotten at (0,0)
            Grid: [2,1,1]    Queue: {(0,0)}
                  [1,1,0]
                  [0,1,1]

  Minute 1: (0,0) rots (0,1) and (1,0)
            Grid: [2,2,1]    Queue: {(0,1),(1,0)}
                  [2,1,0]
                  [0,1,1]

  Minute 2: (0,1) rots (0,2), (1,0) rots (1,1)  [(1,1) also adjacent to (0,1)]
            Grid: [2,2,2]    Queue: {(0,2),(1,1)}
                  [2,2,0]
                  [0,1,1]

  Minute 3: (1,1) rots (2,1)
            Grid: [2,2,2]    Queue: {(2,1)}
                  [2,2,0]
                  [0,2,1]

  Minute 4: (2,1) rots (2,2)
            Grid: [2,2,2]    Queue: {(2,2)}
                  [2,2,0]
                  [0,2,2]
  All fresh oranges are rotten. Answer: 4.

Example 2:
  Input:  grid = [[2,1,1],
                   [0,1,1],
                   [1,0,1]]
  Output: -1
  Explanation: The orange at (2,0) can never be reached.

Example 3:
  Input:  grid = [[0,2]]
  Output: 0
  Explanation: No fresh oranges exist.
```

### Approach: Multi-Source BFS

**Why this approach?**

This is a classic **multi-source BFS** problem. All initially rotten oranges are sources. We add them all to the queue at time 0, then perform BFS level-by-level. Each level corresponds to one minute. After BFS completes, if any fresh orange remains, return -1.

**Why BFS over DFS?** BFS naturally gives shortest distances from sources (minutes in this case). DFS would require complex tracking to ensure minimum times are recorded, and multi-source BFS elegantly handles simultaneous spreading from multiple rotten oranges.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Queue;

public class RottingOranges {
    public int orangesRotting(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Queue<int[]> queue = new ArrayDeque<>();
        int freshCount = 0;

        // Step 1: Enqueue all initially rotten oranges, count fresh ones
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    freshCount++;
                }
            }
        }

        if (freshCount == 0) return 0; // no fresh oranges

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int minutes = 0;

        // Step 2: BFS level-by-level
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean rotted = false;

            for (int i = 0; i < size; i++) {
                int[] cell = queue.poll();
                for (int[] d : dirs) {
                    int nr = cell[0] + d[0];
                    int nc = cell[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                            && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        freshCount--;
                        queue.offer(new int[]{nr, nc});
                        rotted = true;
                    }
                }
            }

            if (rotted) minutes++;
        }

        return freshCount == 0 ? minutes : -1;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(m * n)** | Each cell is visited at most once. BFS processes every cell in the grid. |
| **Space** | **O(m * n)** | In the worst case, all cells are rotten and enqueued simultaneously. |

---

## Q88. Walls and Gates (BFS from Gates)

**Difficulty:** `Medium` | **Data Structure:** Queue / BFS

### Problem

You are given an `m x n` grid `rooms` initialized with:
- `-1` - A wall or obstacle.
- `0` - A gate.
- `INF` (2147483647) - An empty room.

Fill each empty room with the distance to its nearest gate. If it is impossible to reach a gate, leave it as `INF`.

*(LeetCode 286)*

### Examples

```
Example 1:
  Input:  rooms = [[INF, -1,  0, INF],
                    [INF, INF, INF, -1],
                    [INF, -1, INF, -1],
                    [0,   -1, INF, INF]]

  Output:          [[3, -1, 0,  1],
                    [2,  2, 1, -1],
                    [1, -1, 2, -1],
                    [0, -1, 3,  4]]

  BFS Trace (level-by-level from gates at (0,2) and (3,0)):
  Level 0 (distance 0): Queue = {(0,2), (3,0)}  — these are gates
  Level 1 (distance 1): (0,2) reaches (0,3)=1, (1,2)=1
                         (3,0) reaches (2,0)=1
                         Queue = {(0,3), (1,2), (2,0)}
  Level 2 (distance 2): (0,3) has no INF neighbors
                         (1,2) reaches (1,1)=2
                         (2,0) has no INF neighbors (wall at (2,1))
                          but wait — also check (1,0): (2,0) reaches (1,0)=2
                         Queue = {(1,1), (1,0)}
  Level 3 (distance 3): (1,1) reaches nothing new (already filled)
                         (1,0) reaches (0,0)=3
                         Queue = {(0,0)}
                         Also (1,2) at level 1 reached (2,2)=2 at level 2,
                         then (2,2) reaches (3,2)=3 at level 3
  Level 4 (distance 4): (3,2) reaches (3,3)=4

Example 2:
  Input:  rooms = [[0, -1],
                    [INF, INF]]
  Output:          [[0, -1],
                    [1,  2]]
```

### Approach: Multi-Source BFS from All Gates

**Why this approach?**

Instead of BFS from each gate separately (O(gates * m * n)), we do a **single multi-source BFS** starting from all gates simultaneously. This computes the shortest distance from any gate for every cell in O(m * n) time.

Each cell is visited at most once because we only process cells that still have value `INF` (unvisited). When a cell is reached, it's assigned the current distance and enqueued.

**Why not DFS?** DFS doesn't guarantee shortest paths naturally. We'd need to revisit cells to update shorter distances, leading to worse time complexity.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Queue;

public class WallsAndGates {
    private static final int INF = Integer.MAX_VALUE;

    public void wallsAndGates(int[][] rooms) {
        int rows = rooms.length, cols = rooms[0].length;
        Queue<int[]> queue = new ArrayDeque<>();

        // Step 1: Enqueue all gates
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (rooms[r][c] == 0) {
                    queue.offer(new int[]{r, c});
                }
            }
        }

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Step 2: BFS from all gates simultaneously
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int r = cell[0], c = cell[1];

            for (int[] d : dirs) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && rooms[nr][nc] == INF) {
                    rooms[nr][nc] = rooms[r][c] + 1;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(m * n)** | Each cell is enqueued and processed at most once. |
| **Space** | **O(m * n)** | Queue can hold up to all cells in the worst case. |

---

## Q89. Moving Average from Data Stream

**Difficulty:** `Medium` | **Data Structure:** Queue / Sliding Window

### Problem

Given a stream of integers and a window size, calculate the moving average of all integers in the sliding window.

- `MovingAverage(int size)` - Initialize with window size.
- `double next(int val)` - Return the moving average after adding `val`.

*(LeetCode 346)*

### Examples

```
Example 1:
  MovingAverage ma = new MovingAverage(3);
  ma.next(1) -> 1.0         // window: [1], sum = 1, avg = 1/1
  ma.next(10) -> 5.5        // window: [1, 10], sum = 11, avg = 11/2
  ma.next(3) -> 4.666...    // window: [1, 10, 3], sum = 14, avg = 14/3
  ma.next(5) -> 6.0         // window: [10, 3, 5], sum = 18, avg = 18/3 (1 evicted)

Example 2:
  MovingAverage ma = new MovingAverage(1);
  ma.next(4)  -> 4.0
  ma.next(7)  -> 7.0
  ma.next(2)  -> 2.0
```

### Approach: Queue with Running Sum

**Why this approach?**

Maintain a queue of at most `size` elements and a running sum. On each `next(val)`:
1. Add `val` to the queue and the sum.
2. If queue exceeds window size, remove the oldest element and subtract from sum.
3. Return `sum / queue.size()`.

**Why not recompute sum each time?** Recomputing sum from the queue would be O(size) per call. Maintaining a running sum gives O(1).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class MovingAverage {
    private final int maxSize;
    private final Deque<Integer> window;
    private double sum;

    public MovingAverage(int size) {
        this.maxSize = size;
        this.window = new ArrayDeque<>();
        this.sum = 0.0;
    }

    public double next(int val) {
        window.addLast(val);
        sum += val;

        if (window.size() > maxSize) {
            sum -= window.pollFirst();
        }

        return sum / window.size();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per next() call | Constant-time queue operations and arithmetic. |
| **Space** | **O(size)** | Queue stores at most `size` elements. |

---

## Q90. Task Scheduler (CPU Task Scheduling)

**Difficulty:** `Medium` | **Data Structure:** Queue / Greedy

### Problem

Given a list of CPU tasks represented by characters (A-Z) and a non-negative cooldown interval `n`, find the least number of intervals the CPU will take to finish all tasks. The same task must have at least `n` intervals between two executions. The CPU can be idle during cooldown.

*(LeetCode 621)*

### Examples

```
Example 1:
  Input:  tasks = ["A","A","A","B","B","B"], n = 2
  Output: 8
  Explanation: A -> B -> idle -> A -> B -> idle -> A -> B
               One possible schedule with 8 intervals.

Example 2:
  Input:  tasks = ["A","A","A","B","B","B"], n = 0
  Output: 6
  Explanation: No cooldown needed. Execute all 6 tasks back-to-back.

Example 3:
  Input:  tasks = ["A","A","A","A","A","A","B","C","D","E","F","G"], n = 2
  Output: 16
  Explanation: A -> B -> C -> A -> D -> E -> A -> F -> G -> A -> idle -> idle -> A -> idle -> idle -> A
```

### Approach: Greedy with Math Formula

**Why this approach?**

The key insight is: the most frequent task dictates the schedule. If the max frequency is `maxFreq` and there are `maxCount` tasks with that frequency:

```
intervals = (maxFreq - 1) * (n + 1) + maxCount
```

- `(maxFreq - 1)` groups of `(n + 1)` slots each, plus a final group of `maxCount` tasks.
- If total tasks exceed this formula result, the answer is simply `tasks.length` (no idle time needed because there are enough different tasks to fill cooldowns).

**Why not simulation with priority queue?** A PQ-based simulation (greedily pick highest-freq task each round) also works in O(n * totalTasks) but is more complex. The math formula gives O(n) and is cleaner.

### Optimized Java Solution

```java
public class TaskScheduler {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char task : tasks) {
            freq[task - 'A']++;
        }

        int maxFreq = 0;
        int maxCount = 0;
        for (int f : freq) {
            if (f > maxFreq) {
                maxFreq = f;
                maxCount = 1;
            } else if (f == maxFreq) {
                maxCount++;
            }
        }

        // Formula: (maxFreq - 1) chunks of size (n + 1), plus final chunk of maxCount
        int intervals = (maxFreq - 1) * (n + 1) + maxCount;

        // If we have enough tasks to fill all idle slots, no idle time is needed
        return Math.max(intervals, tasks.length);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(T)** | Where T = tasks.length. One pass to count frequencies, one pass over 26 letters. |
| **Space** | **O(1)** | Fixed array of size 26 for the English uppercase letters. |

---

## Q91. Design Hit Counter

**Difficulty:** `Medium` | **Data Structure:** Queue

### Problem

Design a hit counter that counts the number of hits received in the past 5 minutes (300 seconds).

- `void hit(int timestamp)` - Record a hit at the given timestamp (in seconds).
- `int getHits(int timestamp)` - Return the number of hits in the past 300 seconds (inclusive of current timestamp).

Timestamps are given in increasing order (may have duplicates). Multiple hits may arrive at the same timestamp.

*(LeetCode 362)*

### Examples

```
Example 1:
  hit(1)
  hit(2)
  hit(3)
  getHits(4)   -> 3 (hits at 1, 2, 3 are all within [4-299, 4])
  hit(300)
  getHits(300) -> 4 (hits at 1, 2, 3, 300 are all within [1, 300])
  getHits(301) -> 3 (hit at 1 is expired, hits at 2, 3, 300 remain)

Example 2:
  hit(1), hit(1), hit(1)
  getHits(1)   -> 3
  getHits(301) -> 0 (all three hits at t=1 expired)
```

### Approach: Queue-Based Sliding Window

**Why this approach?**

Maintain a queue of timestamps. On `hit(t)`, enqueue `t`. On `getHits(t)`, remove all entries from the front that are older than `t - 299` (expired), then return queue size.

**Why not a fixed-size circular buffer?** A circular buffer with 300 slots works but can't handle multiple hits at the same timestamp without additional arrays. The queue approach is simpler and handles variable hit rates.

**Scalability note:** For extremely high throughput, we could use a fixed 300-slot array where `times[i % 300]` stores the timestamp and `hits[i % 300]` stores the count for that timestamp. This gives O(1) `hit` and O(300) = O(1) `getHits`.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class HitCounter {
    private final Deque<Integer> hits;

    public HitCounter() {
        hits = new ArrayDeque<>();
    }

    public void hit(int timestamp) {
        hits.addLast(timestamp);
    }

    public int getHits(int timestamp) {
        // Remove expired hits
        while (!hits.isEmpty() && hits.peekFirst() <= timestamp - 300) {
            hits.pollFirst();
        }
        return hits.size();
    }
}

/**
 * Scalable O(1) version using fixed-size circular buffer.
 * Better for high-throughput scenarios.
 */
class HitCounterScalable {
    private final int[] times = new int[300];
    private final int[] counts = new int[300];

    public void hit(int timestamp) {
        int idx = timestamp % 300;
        if (times[idx] != timestamp) {
            times[idx] = timestamp;
            counts[idx] = 1;
        } else {
            counts[idx]++;
        }
    }

    public int getHits(int timestamp) {
        int total = 0;
        for (int i = 0; i < 300; i++) {
            if (timestamp - times[i] < 300) {
                total += counts[i];
            }
        }
        return total;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time (Queue)** | **hit: O(1), getHits: amortized O(1)** | Each timestamp is enqueued once and dequeued at most once across all getHits calls. |
| **Time (Scalable)** | **hit: O(1), getHits: O(300) = O(1)** | Fixed scan of 300 slots. |
| **Space** | **O(n) / O(300)** | Queue version: proportional to hits in window. Scalable version: fixed 600 integers. |

---

## Q92. Zigzag Iterator (Alternating Between Two Lists)

**Difficulty:** `Medium` | **Data Structure:** Queue / Iterator

### Problem

Given two 1D vectors, implement an iterator that alternates elements from each vector.

- `ZigzagIterator(List<Integer> v1, List<Integer> v2)` - Constructor.
- `int next()` - Return the next element in zigzag order.
- `boolean hasNext()` - Return true if there are more elements.

*(LeetCode 281)*

### Examples

```
Example 1:
  Input:  v1 = [1, 2], v2 = [3, 4, 5, 6]
  Output sequence: [1, 3, 2, 4, 5, 6]
  Explanation: alternate 1,3, then 2,4, then v1 exhausted, continue with 5,6.

Example 2:
  Input:  v1 = [1, 2, 3], v2 = [4, 5, 6]
  Output sequence: [1, 4, 2, 5, 3, 6]

Example 3:
  Input:  v1 = [], v2 = [1, 2, 3]
  Output sequence: [1, 2, 3]
```

### Approach: Queue of Iterators (Generalizable to K Lists)

**Why this approach?**

Store Java `Iterator` objects in a queue. On `next()`:
1. Poll the front iterator.
2. Get its next element.
3. If the iterator still has elements, enqueue it back.

This generalizes beautifully to k lists: just initialize the queue with all k iterators. The round-robin rotation naturally produces the zigzag pattern.

**Why not track indices manually?** Index tracking works for 2 lists but becomes unwieldy for k lists. The queue-of-iterators pattern is elegant and extensible.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class ZigzagIterator {
    private final Queue<Iterator<Integer>> queue;

    public ZigzagIterator(List<Integer> v1, List<Integer> v2) {
        queue = new ArrayDeque<>();
        if (v1 != null && !v1.isEmpty()) queue.offer(v1.iterator());
        if (v2 != null && !v2.isEmpty()) queue.offer(v2.iterator());
    }

    /** Generalized constructor for k lists. */
    public ZigzagIterator(List<List<Integer>> lists) {
        queue = new ArrayDeque<>();
        for (List<Integer> list : lists) {
            if (list != null && !list.isEmpty()) {
                queue.offer(list.iterator());
            }
        }
    }

    public int next() {
        Iterator<Integer> it = queue.poll();
        int val = it.next();
        if (it.hasNext()) {
            queue.offer(it); // put back for future rounds
        }
        return val;
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per next()/hasNext() | Queue poll/offer are O(1). Iterator.next() is O(1). |
| **Space** | **O(k)** | Where k is the number of non-empty lists. The queue holds at most k iterators. The data itself is not copied. |

---

## Q93. Design Circular Deque

**Difficulty:** `Medium` | **Data Structure:** Deque / Circular Array

### Problem

Design a circular double-ended queue (deque) with fixed capacity:

- `MyCircularDeque(int k)` - Constructor with max size k.
- `boolean insertFront(int value)` / `boolean insertLast(int value)` - Insert at front/rear.
- `boolean deleteFront()` / `boolean deleteLast()` - Delete from front/rear.
- `int getFront()` / `int getRear()` - Get front/rear element, return -1 if empty.
- `boolean isEmpty()` / `boolean isFull()`

*(LeetCode 641)*

### Examples

```
Example 1:
  MyCircularDeque cd = new MyCircularDeque(3);
  cd.insertLast(1)  -> true  // [1]
  cd.insertLast(2)  -> true  // [1, 2]
  cd.insertFront(4) -> true  // [4, 1, 2]
  cd.insertFront(9) -> false // full
  cd.getRear()      -> 2
  cd.isFull()       -> true
  cd.deleteLast()   -> true  // [4, 1]
  cd.insertFront(9) -> true  // [9, 4, 1]
  cd.getFront()     -> 9

Example 2:
  MyCircularDeque cd = new MyCircularDeque(2);
  cd.insertFront(7) -> true  // [7]
  cd.insertLast(8)  -> true  // [7, 8]
  cd.getFront()     -> 7
  cd.getRear()      -> 8
  cd.deleteFront()  -> true  // [8]
  cd.getFront()     -> 8
  cd.getRear()      -> 8
```

### Approach: Circular Array with Front/Count

**Why this approach?**

Similar to Q81 (Circular Queue), but with both front insertion and rear deletion. We track `front` index and `count`:

- **insertFront**: Decrement `front` (mod capacity), place value.
- **insertLast**: Compute rear as `(front + count) % capacity`, place value.
- **deleteFront**: Increment `front` (mod capacity).
- **deleteLast**: Just decrement `count`.
- **getRear**: Compute `(front + count - 1) % capacity`.

### Optimized Java Solution

```java
public class MyCircularDeque {
    private final int[] data;
    private final int capacity;
    private int front;
    private int count;

    public MyCircularDeque(int k) {
        this.capacity = k;
        this.data = new int[k];
        this.front = 0;
        this.count = 0;
    }

    public boolean insertFront(int value) {
        if (isFull()) return false;
        front = (front - 1 + capacity) % capacity;
        data[front] = value;
        count++;
        return true;
    }

    public boolean insertLast(int value) {
        if (isFull()) return false;
        int rear = (front + count) % capacity;
        data[rear] = value;
        count++;
        return true;
    }

    public boolean deleteFront() {
        if (isEmpty()) return false;
        front = (front + 1) % capacity;
        count--;
        return true;
    }

    public boolean deleteLast() {
        if (isEmpty()) return false;
        count--;
        return true;
    }

    public int getFront() {
        return isEmpty() ? -1 : data[front];
    }

    public int getRear() {
        if (isEmpty()) return -1;
        int rear = (front + count - 1) % capacity;
        return data[rear];
    }

    public boolean isEmpty() { return count == 0; }
    public boolean isFull()  { return count == capacity; }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per operation | All operations are simple index arithmetic and array access. |
| **Space** | **O(k)** | Fixed-size array of capacity k. |

---

## Q94. Time Needed to Buy Tickets (Simulation with Queue)

**Difficulty:** `Medium` | **Data Structure:** Queue / Simulation

### Problem

There are `n` people standing in a line to buy tickets. The `i`-th person wants to buy `tickets[i]` tickets. Each person takes exactly 1 second to buy 1 ticket, then goes to the back of the line if they need more. Return the time taken for the person at position `k` to finish buying all their tickets.

*(LeetCode 2073)*

### Examples

```
Example 1:
  Input:  tickets = [2, 3, 2], k = 2
  Output: 6
  Explanation:
  Round 1: [2,3,2] -> person 0 buys -> [1,3,2], time=1
           [1,3,2] -> person 1 buys -> [1,2,2], time=2
           [1,2,2] -> person 2 buys -> [1,2,1], time=3
  Round 2: [1,2,1] -> person 0 buys -> [0,2,1], time=4
           [0,2,1] -> person 1 buys -> [0,1,1], time=5
           [0,1,1] -> person 2 buys -> [0,1,0], time=6  <-- person 2 done!

Example 2:
  Input:  tickets = [5, 1, 1, 1], k = 0
  Output: 8
  Explanation: Person 0 needs 5 tickets. Others need 1 each.
  After round 1: [4,0,0,0], time=4 (persons 1,2,3 done, each took 1s)
  Remaining: person 0 alone, takes 4 more seconds. Total = 4 + 4 = 8.
```

### Approach: O(n) Mathematical Calculation

**Why this approach?**

Instead of simulating each second (O(sum of tickets)), we calculate each person's contribution:

- For person `i` **before or at** position `k`: they buy `min(tickets[i], tickets[k])` tickets before person k finishes.
- For person `i` **after** position `k`: they buy `min(tickets[i], tickets[k] - 1)` tickets (they get one fewer chance per full round since they come after k).

This gives an O(n) solution.

**Why not full simulation?** Simulation would be O(sum(tickets)) which can be very large. The math approach is always O(n).

### Optimized Java Solution

```java
public class TimeToBuyTickets {
    public int timeRequiredToBuy(int[] tickets, int k) {
        int time = 0;
        int target = tickets[k];

        for (int i = 0; i < tickets.length; i++) {
            if (i <= k) {
                // Persons at or before k get min(tickets[i], target) turns
                time += Math.min(tickets[i], target);
            } else {
                // Persons after k get min(tickets[i], target - 1) turns
                time += Math.min(tickets[i], target - 1);
            }
        }

        return time;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Single pass through the tickets array. |
| **Space** | **O(1)** | Only a few integer variables used. |

---
---

## Q95. Sliding Window Maximum (Monotonic Deque)

**Difficulty:** `Hard` | **Data Structure:** Deque (Monotonic)

### Problem

Given an array of integers `nums` and a sliding window of size `k`, return the maximum value in each window as the window slides from left to right.

*(LeetCode 239)*

### Examples

```
Example 1:
  Input:  nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3
  Output: [3, 3, 5, 5, 6, 7]

  Window position                Max
  ---------------               -----
  [1  3  -1] -3  5  3  6  7      3
   1 [3  -1  -3] 5  3  6  7      3
   1  3 [-1  -3  5] 3  6  7      5
   1  3  -1 [-3  5  3] 6  7      5
   1  3  -1  -3 [5  3  6] 7      6
   1  3  -1  -3  5 [3  6  7]     7

  Monotonic Deque State Trace (stores indices):
  i=0: nums[0]=1,  deque=[0]                    (not yet k elements)
  i=1: nums[1]=3,  3>1 → pop 0, deque=[1]       (not yet k elements)
  i=2: nums[2]=-1, -1<3 → keep, deque=[1,2]     window [0..2], max=nums[1]=3
  i=3: nums[3]=-3, -3<-1 → keep, deque=[1,2,3]  window [1..3], max=nums[1]=3
  i=4: nums[4]=5,  5>-3 → pop 3, 5>-1 → pop 2, 5>3 → pop 1, deque=[4]
       window [2..4], max=nums[4]=5
  i=5: nums[5]=3,  3<5 → keep, deque=[4,5]      window [3..5], max=nums[4]=5
  i=6: nums[6]=6,  6>3 → pop 5, 6>5 → pop 4, deque=[6]
       window [4..6], max=nums[6]=6
  i=7: nums[7]=7,  7>6 → pop 6, deque=[7]       window [5..7], max=nums[7]=7

Example 2:
  Input:  nums = [1], k = 1
  Output: [1]

Example 3:
  Input:  nums = [9, 11], k = 2
  Output: [11]
```

### Approach: Monotonic Decreasing Deque

**Why this approach?**

We maintain a deque of **indices** such that `nums[deque[i]]` is in **decreasing** order. This means the front of the deque is always the index of the maximum in the current window.

For each new element `nums[i]`:
1. **Remove from back**: While the back of the deque has indices pointing to values ≤ `nums[i]`, pop them. They can never be a window maximum because `nums[i]` is larger and will stay in the window longer.
2. **Add `i`** to the back.
3. **Remove from front**: If the front index is outside the window (`< i - k + 1`), remove it.
4. **Record answer**: `nums[deque.peekFirst()]` is the window maximum.

**Why not a heap (priority queue)?** A max-heap gives O(n log n) because removing expired elements is O(log n). The monotonic deque achieves O(n) total because each element is added and removed at most once.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowMaximum {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // stores indices

        for (int i = 0; i < n; i++) {
            // Remove indices of elements smaller than nums[i] from the back
            while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
                deque.pollLast();
            }

            deque.addLast(i);

            // Remove the front if it's outside the window
            if (deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Record result once we've filled the first window
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each index is pushed to and popped from the deque at most once. Total operations: 2n. |
| **Space** | **O(k)** | The deque holds at most k indices (elements within the current window). |

---

## Q96. Shortest Subarray with Sum at Least K (Deque)

**Difficulty:** `Hard` | **Data Structure:** Deque / Prefix Sum

### Problem

Given an integer array `nums` (may contain **negative** numbers) and an integer `k`, return the length of the shortest non-empty subarray whose sum is at least `k`. Return `-1` if no such subarray exists.

*(LeetCode 862)*

### Examples

```
Example 1:
  Input:  nums = [2, -1, 2], k = 3
  Output: 3
  Explanation: [2, -1, 2] has sum 3, which is the shortest subarray with sum >= 3.

Example 2:
  Input:  nums = [1], k = 1
  Output: 1

Example 3:
  Input:  nums = [1, 2], k = 4
  Output: -1

Detailed Trace for nums = [2, -1, 2], k = 3:
  prefix = [0, 2, 1, 3]

  i=0: prefix[0]=0, deque=[]
       Push 0, deque=[0]

  i=1: prefix[1]=2
       Check front: prefix[1]-prefix[0] = 2-0 = 2 < 3, no answer yet
       Clean back: prefix[1]=2 >= prefix[0]=0? No cleanup needed (monotonic)
       Push 1, deque=[0,1]

  i=2: prefix[2]=1
       Check front: prefix[2]-prefix[0] = 1-0 = 1 < 3, no answer
       Clean back: prefix[2]=1 < prefix[1]=2? Yes, pop 1. prefix[2]=1 > prefix[0]=0? Keep 0.
       Push 2, deque=[0,2]

  i=3: prefix[3]=3
       Check front: prefix[3]-prefix[0] = 3-0 = 3 >= 3! answer=min(INF, 3-0)=3, pop front 0
       Check front: prefix[3]-prefix[2] = 3-1 = 2 < 3, stop
       Clean back: prefix[3]=3 >= prefix[2]=1? Pop 2. Deque empty.
       Push 3, deque=[3]

  Result: 3
```

### Approach: Prefix Sum + Monotonic Deque

**Why this approach?**

For subarrays with **negative numbers**, the sliding window approach (used when all positive) fails because shrinking the window doesn't guarantee sum decreases.

Instead:
1. Compute prefix sums: `prefix[i] = nums[0] + ... + nums[i-1]`. Subarray sum `[l..r)` = `prefix[r] - prefix[l]`.
2. We want to find, for each `r`, the **largest** `l < r` such that `prefix[r] - prefix[l] >= k`, i.e., `prefix[l] <= prefix[r] - k`, while minimizing `r - l`.

Maintain a **monotonically increasing** deque of indices into `prefix`:
- **Pop from front** while `prefix[r] - prefix[deque.front] >= k` (record answer, pop since no future `r` will give a shorter subarray with this `l`).
- **Pop from back** while `prefix[deque.back] >= prefix[r]` (a later index with a smaller prefix sum dominates).
- Push `r`.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class ShortestSubarraySumK {
    public int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }

        Deque<Integer> deque = new ArrayDeque<>(); // indices into prefix[]
        int minLen = Integer.MAX_VALUE;

        for (int i = 0; i <= n; i++) {
            // Pop from front: if prefix[i] - prefix[front] >= k, record answer
            while (!deque.isEmpty() && prefix[i] - prefix[deque.peekFirst()] >= k) {
                minLen = Math.min(minLen, i - deque.pollFirst());
            }

            // Pop from back: maintain monotonically increasing prefix values
            while (!deque.isEmpty() && prefix[deque.peekLast()] >= prefix[i]) {
                deque.pollLast();
            }

            deque.addLast(i);
        }

        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each index is added to and removed from the deque at most once. Prefix sum computation is O(n). |
| **Space** | **O(n)** | Prefix array of size n+1 and deque holding at most n+1 indices. |

---

## Q97. Jump Game VI (DP + Deque Optimization)

**Difficulty:** `Hard` | **Data Structure:** DP / Monotonic Deque

### Problem

You are given a 0-indexed integer array `nums` and an integer `k`. Starting at index 0, in each step you can jump from index `i` to any index in the range `[i+1, min(n-1, i+k)]`. You want to reach index `n-1` with the **maximum score** (sum of values at visited indices).

Return the maximum score.

*(LeetCode 1696)*

### Examples

```
Example 1:
  Input:  nums = [1, -1, -2, 4, -7, 3], k = 2
  Output: 7
  Explanation: Path: 0 -> 3 -> 5 with scores 1 + 4 + 3 = 8?
               Actually: indices [0, 3, 5] -> 1 + 4 + 3 = 8, but jump from 0 to 3 is distance 3 > k=2.
               Correct path: 0 -> 1 -> 3 -> 5: 1 + (-1) + 4 + 3 = 7

Example 2:
  Input:  nums = [10, -5, -2, 4, 0, 3], k = 3
  Output: 17
  Explanation: Path: 0 -> 3 -> 5: 10 + 4 + 3 = 17

Example 3:
  Input:  nums = [1, -5, -20, 4, -1, 3, -6, -3], k = 2
  Output: 0
  Explanation: Path: 0 -> 1 -> 3 -> 5: 1 + (-5) + 4 + (-1) + 3 = 2
               Or: 0 -> 2 -> 4 -> 6 -> 7: many negatives. Best is 0 -> 1 -> 3 -> 5 = 2?
               Let me recalculate: dp[0]=1, dp[1]=max(dp[0])+(-5)=-4,
               dp[2]=max(dp[0],dp[1])+(-20)=1-20=-19,
               dp[3]=max(dp[1],dp[2])+4=-4+4=0,
               dp[4]=max(dp[2],dp[3])+(-1)=0-1=-1,
               dp[5]=max(dp[3],dp[4])+3=0+3=3,
               dp[6]=max(dp[4],dp[5])+(-6)=3-6=-3,
               dp[7]=max(dp[5],dp[6])+(-3)=3-3=0. Answer: 0.

Deque State Trace for Example 1 (nums = [1,-1,-2,4,-7,3], k=2):
  dp[0] = 1
  deque = [0]  (stores indices, monotonically decreasing by dp value)

  i=1: dp[1] = max(dp[j] for j in [max(0,0)..0]) + (-1)
       deque front = 0, dp[0]=1 (in range), dp[1]=1+(-1)=0
       Clean back: dp[1]=0 < dp[0]=1? Keep. deque=[0,1]

  i=2: dp[2] = max(dp[j] for j in [0..1]) + (-2)
       deque front = 0 (in range), dp[0]=1, dp[2]=1+(-2)=-1
       Clean back: dp[2]=-1 < dp[1]=0? Keep. deque=[0,1,2]

  i=3: deque front = 0, but 3-0=3 > k=2, pop 0. New front = 1, in range.
       dp[3] = dp[1] + 4 = 0+4=4?  Wait, dp[1]=0 and dp[2]=-1, max is dp[1]=0.
       Actually front of deque is max, so dp[3] = dp[deque.front] + nums[3] = dp[1]+4=4
       Clean back: dp[3]=4 > dp[2]=-1, pop. dp[3]=4 > dp[1]=0, pop. deque=[3]

  i=4: deque front = 3 (in range), dp[4] = dp[3]+(-7) = 4-7=-3
       Clean back: dp[4]=-3 < dp[3]=4? Keep. deque=[3,4]

  i=5: deque front = 3 (5-3=2 <= k=2, in range), dp[5] = dp[3]+3 = 4+3=7
       Clean back: dp[5]=7 > dp[4]=-3, pop. dp[5]=7 > dp[3]=4, pop. deque=[5]

  Answer: dp[5] = 7 ✓
```

### Approach: DP with Monotonic Deque for Sliding Window Maximum

**Why this approach?**

The DP recurrence is: `dp[i] = nums[i] + max(dp[j]) for j in [max(0, i-k), i-1]`.

Naively, computing `max(dp[j])` in the range is O(k) per index, giving O(nk) total. By maintaining a **monotonic decreasing deque** (storing indices with decreasing dp values), we get the maximum in O(1) amortized — the same technique as Sliding Window Maximum (Q95).

**Why not a heap?** A max-heap would give O(n log n) due to lazy deletion. The monotonic deque gives O(n).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class JumpGameVI {
    public int maxResult(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[0] = nums[0];

        // Monotonic decreasing deque of indices by dp value
        Deque<Integer> deque = new ArrayDeque<>();
        deque.addLast(0);

        for (int i = 1; i < n; i++) {
            // Remove indices outside the window [i-k, i-1]
            while (!deque.isEmpty() && deque.peekFirst() < i - k) {
                deque.pollFirst();
            }

            // dp[i] = best reachable dp value + nums[i]
            dp[i] = dp[deque.peekFirst()] + nums[i];

            // Maintain decreasing order: remove indices with dp <= dp[i]
            while (!deque.isEmpty() && dp[deque.peekLast()] <= dp[i]) {
                deque.pollLast();
            }

            deque.addLast(i);
        }

        return dp[n - 1];
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each index is enqueued and dequeued at most once. DP computation is O(1) per index with the deque providing the max. |
| **Space** | **O(n)** | DP array of size n. Deque holds at most k indices but allocated up to n. Can optimize dp to O(k) if needed. |

---

## Q98. Design Snake Game (Queue-Based Simulation)

**Difficulty:** `Hard` | **Data Structure:** Queue / Deque / HashSet

### Problem

Design a Snake game played on a `width x height` board. The snake starts at position `(0, 0)` with length 1. Food appears one at a time at given positions.

- `SnakeGame(int width, int height, int[][] food)` - Constructor.
- `int move(String direction)` - Move the snake one step in the given direction ("U", "D", "L", "R"). Return the score (food eaten) after the move, or -1 if the game is over (wall collision or self-collision).

The snake grows by 1 when it eats food (tail doesn't retract that turn). The snake can move into the cell just vacated by its tail.

*(LeetCode 353)*

### Examples

```
Example 1:
  width = 3, height = 2
  food = [[1,2], [0,1]]

  Initial state (S = snake at (0,0)):
  S . .
  . . .

  move("R") -> 0   Snake: (0,0)->(0,1).  Score: 0
  . S .
  is wrong... let me reconsider:
  Board (row, col):
  (0,0) (0,1) (0,2)
  (1,0) (1,1) (1,2)

  Start: snake = [(0,0)]
  move("R") -> snake moves to (0,1). No food at (0,1). Tail retracts.
               snake = [(0,1)]. Score = 0.

  move("D") -> snake moves to (1,1). No food at (1,1).
               snake = [(1,1)]. Score = 0.

  move("R") -> snake moves to (1,2). Food at (1,2)! Eat it. Tail stays.
               snake = [(1,1),(1,2)]. Score = 1.

  move("U") -> snake moves to (0,2). No food. Tail retracts.
               snake = [(1,2),(0,2)]. Score = 1.

  move("L") -> snake moves to (0,1). Food at (0,1)! Eat it.
               snake = [(1,2),(0,2),(0,1)]. Score = 2.

  move("U") -> snake moves to (-1,1). Out of bounds! Game over. Return -1.

Example 2:
  width = 2, height = 2
  food = [[0,1],[1,1],[1,0]]

  move("R") -> eat food at (0,1). snake=[(0,0),(0,1)]. Score=1.
  move("D") -> eat food at (1,1). snake=[(0,0),(0,1),(1,1)]. Score=2.
  move("L") -> eat food at (1,0). snake=[(0,0),(0,1),(1,1),(1,0)]. Score=3.
  move("U") -> move to (0,0). Tail vacates (0,0) first, so this is valid!
               snake=[(0,1),(1,1),(1,0),(0,0)]. Score=3.
```

### Approach: Deque for Snake Body + HashSet for Collision Detection

**Why this approach?**

The snake body is naturally a **deque** (double-ended queue):
- **Head** is the front — new head position is added to the front on each move.
- **Tail** is the back — removed from the back unless food is eaten.

A **HashSet** of positions allows O(1) self-collision checks. Key detail: the tail cell is vacated *before* checking collision with the new head, so we remove the tail from the set first (unless eating food).

**Why not a list?** Checking if the new head collides with the body would be O(n) with a list. The HashSet gives O(1).

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class SnakeGame {
    private final int width, height;
    private final int[][] food;
    private int foodIndex;
    private int score;
    private final Deque<int[]> snake;  // front = head, back = tail
    private final Set<String> occupied; // "row,col" strings for O(1) lookup

    public SnakeGame(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.food = food;
        this.foodIndex = 0;
        this.score = 0;
        this.snake = new ArrayDeque<>();
        this.occupied = new HashSet<>();

        snake.addFirst(new int[]{0, 0});
        occupied.add("0,0");
    }

    public int move(String direction) {
        int[] head = snake.peekFirst();
        int newRow = head[0], newCol = head[1];

        switch (direction) {
            case "U" -> newRow--;
            case "D" -> newRow++;
            case "L" -> newCol--;
            case "R" -> newCol++;
        }

        // Check wall collision
        if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) {
            return -1;
        }

        // Check if eating food
        boolean eating = foodIndex < food.length
                && food[foodIndex][0] == newRow
                && food[foodIndex][1] == newCol;

        if (!eating) {
            // Remove tail (it vacates the cell BEFORE we check self-collision)
            int[] tail = snake.pollLast();
            occupied.remove(tail[0] + "," + tail[1]);
        } else {
            foodIndex++;
            score++;
        }

        // Check self-collision (after tail removal if not eating)
        String newPos = newRow + "," + newCol;
        if (occupied.contains(newPos)) {
            return -1;
        }

        // Add new head
        snake.addFirst(new int[]{newRow, newCol});
        occupied.add(newPos);

        return score;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(1)** per move | HashSet lookup/insert/remove are O(1). Deque addFirst/pollLast are O(1). |
| **Space** | **O(n)** | Where n is the snake's length. Deque and HashSet both store n positions. |

---

## Q99. Maximum of All Subarrays of Size K (Deque Approach)

**Difficulty:** `Hard` | **Data Structure:** Deque (Monotonic)

### Problem

Given an array `arr[]` of size `n` and an integer `k`, find the maximum element for each contiguous subarray of size `k`. This is the same core problem as Q95 (Sliding Window Maximum) but presented with different examples and a deeper trace.

### Examples

```
Example 1:
  Input:  arr = [8, 5, 10, 7, 9, 4, 15, 12, 90, 13], k = 4
  Output: [10, 10, 10, 15, 15, 90, 90]

  Subarrays:
  [8,5,10,7]      -> max = 10
  [5,10,7,9]      -> max = 10
  [10,7,9,4]      -> max = 10
  [7,9,4,15]      -> max = 15
  [9,4,15,12]     -> max = 15
  [4,15,12,90]    -> max = 90
  [15,12,90,13]   -> max = 90

  Monotonic Deque Trace (stores indices, decreasing by value):
  i=0: val=8.  deque=[0]
  i=1: val=5.  5<8, push. deque=[0,1]
  i=2: val=10. 10>5→pop 1. 10>8→pop 0. deque=[2].
       i>=k-1=3? No.
  i=3: val=7.  7<10, push. deque=[2,3].
       i>=3? Yes. result[0]=arr[2]=10.
  i=4: val=9.  9>7→pop 3. 9<10→keep. deque=[2,4].
       Front index 2, 4-2=2<k=4? In range. result[1]=arr[2]=10.
  i=5: val=4.  4<9, push. deque=[2,4,5].
       Front index 2, 5-2=3<4. result[2]=arr[2]=10.
  i=6: val=15. 15>4→pop5. 15>9→pop4. 15>10→pop2. deque=[6].
       result[3]=arr[6]=15.
  i=7: val=12. 12<15, push. deque=[6,7].
       result[4]=arr[6]=15.
  i=8: val=90. 90>12→pop7. 90>15→pop6. deque=[8].
       result[5]=arr[8]=90.
  i=9: val=13. 13<90, push. deque=[8,9].
       Front index 8, 9-8=1<4. result[6]=arr[8]=90.

  Final Output: [10, 10, 10, 15, 15, 90, 90] ✓

Example 2:
  Input:  arr = [1, 2, 3, 1, 4, 5, 2, 3, 6], k = 3
  Output: [3, 3, 4, 5, 5, 5, 6]

Example 3:
  Input:  arr = [4, 3, 2, 1], k = 2
  Output: [4, 3, 2]
```

### Approach: Monotonic Decreasing Deque

**Why this approach?**

Identical to Q95 — maintain a deque of indices where values are in decreasing order. The front always holds the index of the current window's maximum.

**Key invariant:** For any two indices `i < j` in the deque, `arr[i] > arr[j]`. If a new element `arr[j] >= arr[i]`, we pop `i` because `i` will leave the window before `j`, and `j` is already larger — so `i` can never be the answer for any future window.

**Comparison with other approaches:**
- Brute force: O(nk) — scan k elements per window.
- Max-Heap: O(n log n) — log n per insertion, lazy deletion needed.
- Monotonic Deque: O(n) — each element pushed and popped at most once.

### Optimized Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class MaxOfAllSubarrays {
    public int[] maxOfSubarrays(int[] arr, int k) {
        int n = arr.length;
        if (n == 0 || k == 0) return new int[0];

        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // stores indices

        for (int i = 0; i < n; i++) {
            // Remove elements from back that are smaller than current
            while (!deque.isEmpty() && arr[deque.peekLast()] <= arr[i]) {
                deque.pollLast();
            }

            deque.addLast(i);

            // Remove front if outside window
            if (deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }

            // Record result for complete windows
            if (i >= k - 1) {
                result[i - k + 1] = arr[deque.peekFirst()];
            }
        }

        return result;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each element enters and exits the deque exactly once. Total pushes + pops = 2n. |
| **Space** | **O(k)** | Deque holds at most k indices at any time (one window's worth). Output array is O(n-k+1). |

---

## Q100. Process Tasks Using Servers (Priority Queue + Queue)

**Difficulty:** `Hard` | **Data Structure:** Priority Queue / Queue

### Problem

You are given two 0-indexed integer arrays `servers` and `tasks`:
- `servers[i]` is the weight of the `i`-th server.
- `tasks[j]` is the time needed to process the `j`-th task.

Tasks arrive one per second (task `j` arrives at second `j`). A free server with the **smallest weight** is assigned the task (ties broken by smallest index). If no server is free, the task waits until one becomes available.

Return an array `ans` where `ans[j]` is the index of the server assigned to task `j`.

*(LeetCode 1882)*

### Examples

```
Example 1:
  Input:  servers = [3, 3, 2], tasks = [1, 2, 3, 2, 1, 2]
  Output: [2, 2, 0, 2, 1, 2]

  Trace:
  t=0: Task 0 arrives (duration 1). Free servers: {0(w=3), 1(w=3), 2(w=2)}.
       Smallest weight = server 2. Assign task 0 → server 2. Server 2 busy until t=1.
       ans[0] = 2

  t=1: Server 2 finishes (was busy 0→1). Task 1 arrives (duration 2).
       Free servers: {0(w=3), 1(w=3), 2(w=2)}.
       Smallest weight = server 2. Assign task 1 → server 2. Busy until t=3.
       ans[1] = 2

  t=2: Task 2 arrives (duration 3). Free servers: {0(w=3), 1(w=3)}.
       Smallest weight = server 0 (tie, lower index). Assign task 2 → server 0. Busy until t=5.
       ans[2] = 0

  t=3: Server 2 finishes. Task 3 arrives (duration 2).
       Free servers: {1(w=3), 2(w=2)}.
       Smallest weight = server 2. Assign task 3 → server 2. Busy until t=5.
       ans[3] = 2

  t=4: Task 4 arrives (duration 1). Free servers: {1(w=3)}.
       Assign task 4 → server 1. Busy until t=5.
       ans[4] = 1

  t=5: Servers 0, 2, 1 all finish. Task 5 arrives (duration 2).
       Free servers: {0(w=3), 1(w=3), 2(w=2)}.
       Smallest weight = server 2. ans[5] = 2.

  Output: [2, 2, 0, 2, 1, 2] ✓

Example 2:
  Input:  servers = [5, 1, 4, 3, 2], tasks = [2, 1, 2, 4, 5, 2, 1]
  Output: [1, 4, 1, 4, 1, 3, 2]

Example 3:
  Input:  servers = [10, 63, 95, 16], tasks = [70, 10, 3]
  Output: [3, 0, 3]
```

### Approach: Two Priority Queues (Free + Busy)

**Why this approach?**

We need to efficiently:
1. Find the free server with smallest weight (then smallest index) → **min-heap by (weight, index)**.
2. Track when busy servers become free → **min-heap by (endTime, weight, index)**.

Algorithm:
1. Initialize a `freeServers` min-heap with all servers keyed by `(weight, index)`.
2. Initialize an empty `busyServers` min-heap keyed by `(endTime, weight, index)`.
3. For each task `j`:
   - Set current time = `max(j, earliest server end time if no servers free)`.
   - Move all servers from `busyServers` whose end time <= current time to `freeServers`.
   - Assign the task to the top of `freeServers`. Add to `busyServers` with end time `current time + tasks[j]`.

**Why two heaps?** This cleanly separates the "which server is free?" query from the "when does the next server become free?" query. Each runs in O(log m) where m = number of servers.

### Optimized Java Solution

```java
import java.util.PriorityQueue;

public class ProcessTasksUsingServers {
    public int[] assignTasks(int[] servers, int[] tasks) {
        int m = servers.length;
        int n = tasks.length;
        int[] ans = new int[n];

        // Free servers: min-heap by (weight, index)
        PriorityQueue<int[]> free = new PriorityQueue<>((a, b) ->
                a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);

        // Busy servers: min-heap by (endTime, weight, index)
        PriorityQueue<int[]> busy = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0]) return a[0] - b[0];
            if (a[1] != b[1]) return a[1] - b[1];
            return a[2] - b[2];
        });

        // Initialize: all servers are free
        for (int i = 0; i < m; i++) {
            free.offer(new int[]{servers[i], i}); // {weight, index}
        }

        for (int j = 0; j < n; j++) {
            int time = j; // task j arrives at time j

            // If no free server, fast-forward to when the earliest busy one finishes
            if (free.isEmpty()) {
                time = Math.max(time, busy.peek()[0]);
            }

            // Release all servers that have finished by 'time'
            while (!busy.isEmpty() && busy.peek()[0] <= time) {
                int[] server = busy.poll();
                free.offer(new int[]{server[1], server[2]}); // {weight, index}
            }

            // Assign task to the best available server
            int[] server = free.poll();
            ans[j] = server[1]; // server index
            busy.offer(new int[]{time + tasks[j], server[0], server[1]});
            // {endTime, weight, index}
        }

        return ans;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O((m + n) log m)** | Each of the m servers may be inserted/removed from heaps multiple times, but each task causes at most O(log m) heap operations. Total heap operations across all tasks is O((m + n) log m). |
| **Space** | **O(m)** | Both heaps together hold at most m server entries. The answer array is O(n). |

---
---

## Summary: Queue Pattern Cheat Sheet

| Pattern | Problems | Key Idea |
|---|---|---|
| **Basic Queue Implementation** | Q76, Q78, Q81, Q82, Q93 | Circular array or linked list with front/rear pointers |
| **Two-Stack Queue** | Q77 | Amortized O(1) via lazy transfer between stacks |
| **Queue ↔ Stack Conversion** | Q77, Q84 | Use one data structure to simulate the other |
| **BFS with Queue** | Q79, Q87, Q88 | Level-order traversal; multi-source BFS for simultaneous spread |
| **Sliding Window Queue** | Q80, Q89, Q91 | Maintain a window of recent events, expire old entries |
| **Monotonic Deque** | Q95, Q96, Q97, Q99 | Maintain decreasing/increasing order for O(1) min/max queries in sliding windows |
| **Queue Manipulation** | Q85, Q86 | Reverse, interleave using stack as auxiliary |
| **Simulation** | Q94, Q98 | Model real-world processes (ticket lines, snake game) |
| **Greedy + Queue/PQ** | Q90, Q100 | Task scheduling with cooldowns or server assignment |
| **Stream Processing** | Q83, Q92 | Process incoming data with queue-based state tracking |
# TREE

> All solutions assume the shared `TreeNode` class defined in the Common Definitions section.

## Mini-Index

| # | Title | Difficulty | Key Technique |
|---|---|---|---|
| Q101 | Binary Tree Inorder Traversal | Basic | Iterative Stack |
| Q102 | Binary Tree Preorder Traversal | Basic | Iterative Stack |
| Q103 | Binary Tree Postorder Traversal | Basic | Iterative Two-Stack / Reverse |
| Q104 | Maximum Depth of Binary Tree | Basic | DFS Recursion |
| Q105 | Check if Two Binary Trees are Identical | Basic | Parallel DFS |
| Q106 | Invert / Mirror a Binary Tree | Basic | Recursive Swap |
| Q107 | Symmetric Tree (Mirror Check) | Medium | Dual-Pointer Recursion |
| Q108 | Level Order Traversal (BFS) | Medium | Queue-Based BFS |
| Q109 | Binary Tree Zigzag Level Order Traversal | Medium | BFS + Direction Flag |
| Q110 | Validate Binary Search Tree (BST) | Medium | Inorder with Range Check |
| Q111 | Lowest Common Ancestor of a Binary Tree | Medium | Post-Order DFS |
| Q112 | Construct Binary Tree from Preorder and Inorder | Medium | Recursive Partitioning |
| Q113 | Kth Smallest Element in a BST | Medium | Inorder Traversal (Iterative) |
| Q114 | Binary Tree Right Side View | Medium | BFS (Last per Level) |
| Q115 | Flatten Binary Tree to Linked List | Medium | Reverse Postorder In-Place |
| Q116 | Diameter of Binary Tree | Medium | DFS with Global Max |
| Q117 | Path Sum II | Medium | Backtracking DFS |
| Q118 | Count Complete Tree Nodes in O(log^2 n) | Medium | Binary Search on Last Level |
| Q119 | Binary Tree Maximum Path Sum | Hard | DP on Tree (Post-Order) |
| Q120 | Serialize and Deserialize Binary Tree | Hard | BFS / Preorder with Markers |
| Q121 | Vertical Order Traversal of Binary Tree | Hard | BFS + Column Mapping |
| Q122 | Recover BST (Two Nodes Swapped) | Hard | Inorder Anomaly Detection |
| Q123 | Morris Inorder Traversal | Hard | Threaded Binary Tree (O(1) Space) |
| Q124 | Binary Tree Cameras | Hard | Greedy DP on Tree |
| Q125 | Convert Sorted Array/List to Balanced BST | Hard | Divide and Conquer |

---
---

## Q101. Binary Tree Inorder Traversal

**Difficulty:** `Basic` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **inorder** traversal (Left -> Root -> Right) as a list of values. Implement the solution **iteratively using a stack** (not recursion).

### Examples

**Example 1:**
```
Input:
    1
     \
      2
     /
    3

Output: [1, 3, 2]
Explanation: Inorder visits left subtree, root, right subtree.
             Visit 1 (no left), then go right to 2, visit its left child 3, then 2.
```

**Example 2:**
```
Input:
        4
       / \
      2    6
     / \  / \
    1   3 5   7

Output: [1, 2, 3, 4, 5, 6, 7]
Explanation: Classic BST inorder gives sorted output.
```

**Example 3:**
```
Input: (empty tree, root = null)
Output: []
```

### Approach: Iterative Stack Simulation

**Why this approach?**

The recursive solution is simple but uses O(n) call-stack space and can overflow on deep trees. The iterative approach explicitly manages the stack, giving us full control. The idea: keep pushing left children onto the stack. When we can't go left anymore, pop a node, record its value (visit it), then move to its right child and repeat.

**Algorithm:**
1. Initialize an empty stack and set `current = root`.
2. While `current != null` OR the stack is not empty:
   - Push `current` and all its left descendants onto the stack.
   - Pop the top node, add its value to the result.
   - Move `current` to the popped node's right child.
3. Return the result list.

### Optimized Java Solution

```java
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode current = root;

    while (current != null || !stack.isEmpty()) {
        // Go as far left as possible
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        // Visit the node
        current = stack.pop();
        result.add(current.val);
        // Move to right subtree
        current = current.right;
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is pushed and popped exactly once. |
| **Space** | **O(h)** | Stack holds at most h nodes (tree height). h = O(log n) balanced, O(n) skewed. |

---

## Q102. Binary Tree Preorder Traversal

**Difficulty:** `Basic` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **preorder** traversal (Root -> Left -> Right) as a list of values. Implement **iteratively using a stack**.

### Examples

**Example 1:**
```
Input:
    1
     \
      2
     /
    3

Output: [1, 2, 3]
Explanation: Visit root 1, go right to 2, visit 2, go left to 3, visit 3.
```

**Example 2:**
```
Input:
        1
       / \
      2    3
     / \    \
    4   5    6

Output: [1, 2, 4, 5, 3, 6]
Explanation: Visit 1, then entire left subtree [2,4,5], then right subtree [3,6].
```

**Example 3:**
```
Input: (empty tree, root = null)
Output: []
```

### Approach: Iterative Stack (Push Right First)

**Why this approach?**

Preorder visits the root first, so we can process each node immediately when we encounter it. The trick: push the **right child first**, then the left child onto the stack. Since a stack is LIFO, the left child is processed before the right --- exactly preorder order.

**Algorithm:**
1. Push root onto the stack.
2. While the stack is not empty:
   - Pop a node, add its value to result.
   - Push its right child (if exists), then its left child (if exists).
3. Return result.

### Optimized Java Solution

```java
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);
        // Push right first so left is processed first (LIFO)
        if (node.right != null) stack.push(node.right);
        if (node.left != null)  stack.push(node.left);
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once. |
| **Space** | **O(h)** | Stack holds at most O(h) nodes. Worst case O(n) for a skewed tree, O(log n) for balanced. |

---

## Q103. Binary Tree Postorder Traversal

**Difficulty:** `Basic` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **postorder** traversal (Left -> Right -> Root) as a list of values. Implement **iteratively**.

### Examples

**Example 1:**
```
Input:
    1
     \
      2
     /
    3

Output: [3, 2, 1]
Explanation: Visit left subtrees first, then right, then root.
```

**Example 2:**
```
Input:
        1
       / \
      2    3
     / \    \
    4   5    6

Output: [4, 5, 2, 6, 3, 1]
```

**Example 3:**
```
Input: root = null
Output: []
```

### Approach: Reverse-Modified-Preorder (Two-Pass Trick)

**Why this approach?**

Postorder (L -> R -> Root) is the reverse of a modified preorder (Root -> R -> L). So we can do a preorder traversal but visit right before left, then reverse the result. This avoids the complexity of tracking whether both children have been processed.

**Alternative:** A single-stack approach with a `prev` pointer works but is more complex. The reverse trick is elegant and easy to reason about.

**Algorithm:**
1. Perform modified preorder: Root -> Right -> Left (push left first, then right).
2. Collect results into a list.
3. Reverse the list to get Left -> Right -> Root.

### Optimized Java Solution

```java
public List<Integer> postorderTraversal(TreeNode root) {
    LinkedList<Integer> result = new LinkedList<>();
    if (root == null) return result;

    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        // Add to front instead of reversing at end
        result.addFirst(node.val);
        // Push left first, then right (right gets processed first -> added to front first)
        if (node.left != null)  stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once. `addFirst` on LinkedList is O(1). |
| **Space** | **O(h)** | Stack holds at most O(h) nodes at a time. |

---

## Q104. Maximum Depth of Binary Tree

**Difficulty:** `Basic` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **maximum depth** --- the number of nodes along the longest path from the root node down to the farthest leaf node.

### Examples

**Example 1:**
```
Input:
       3
      / \
     9  20
       /  \
      15   7

Output: 3
Explanation: Longest path is 3 -> 20 -> 15 (or 3 -> 20 -> 7), which has 3 nodes.
```

**Example 2:**
```
Input:
    1
     \
      2

Output: 2
```

**Example 3:**
```
Input: root = null
Output: 0
```

### Approach: Recursive DFS (Bottom-Up)

**Why this approach?**

The depth of a tree is 1 + max(depth of left subtree, depth of right subtree). This naturally maps to recursion. The base case is a null node having depth 0. This is the simplest and most intuitive solution.

An iterative BFS approach also works (count levels), but recursion is cleaner here and the call stack depth equals tree height --- acceptable for most inputs.

### Optimized Java Solution

```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Visit every node exactly once. |
| **Space** | **O(h)** | Recursion stack depth = height of tree. O(log n) balanced, O(n) skewed. |

---

## Q105. Check if Two Binary Trees are Identical

**Difficulty:** `Basic` | **Data Structure:** Tree

### Problem

Given the roots of two binary trees `p` and `q`, check whether they are **structurally identical** and have the **same node values**.

### Examples

**Example 1:**
```
Input:
  Tree p:       Tree q:
     1             1
    / \           / \
   2   3         2   3

Output: true
Explanation: Same structure and same values at every position.
```

**Example 2:**
```
Input:
  Tree p:       Tree q:
     1             1
    /               \
   2                 2

Output: false
Explanation: Same values but different structure (left child vs right child).
```

**Example 3:**
```
Input:
  Tree p:       Tree q:
     1             1
    / \           / \
   2   1         1   2

Output: false
Explanation: Same structure but different values at corresponding positions.
```

### Approach: Parallel Recursive DFS

**Why this approach?**

We traverse both trees simultaneously. At each step, if both nodes are null, they match. If one is null and the other isn't, they don't match. If both exist but have different values, they don't match. Otherwise, recursively check left and right subtrees.

This is the most natural approach. An iterative BFS with two queues is possible but adds unnecessary complexity.

### Optimized Java Solution

```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    return p.val == q.val
        && isSameTree(p.left, q.left)
        && isSameTree(p.right, q.right);
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(min(n, m))** | We visit at most min(n, m) nodes, where n and m are the sizes of the two trees. Mismatch causes early termination. |
| **Space** | **O(min(h1, h2))** | Recursion depth is bounded by the shorter tree's height. |

---

## Q106. Invert / Mirror a Binary Tree

**Difficulty:** `Basic` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, **invert** it (mirror it) so that left and right children are swapped at every level. Return the root of the inverted tree.

### Examples

**Example 1:**
```
Input:                Output:
       4                   4
      / \                 / \
     2    7              7    2
    / \  / \            / \  / \
   1   3 6   9         9   6 3   1
```

**Example 2:**
```
Input:                Output:
     2                   2
    / \                 / \
   1   3               3   1
```

**Example 3:**
```
Input: root = null
Output: null
```

### Approach: Recursive Swap

**Why this approach?**

At each node, swap its left and right children, then recursively invert the subtrees. This is the most elegant solution. The base case is a null node (nothing to swap). Every node is visited once.

### Optimized Java Solution

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    // Swap left and right children
    TreeNode temp = root.left;
    root.left = root.right;
    root.right = temp;
    // Recursively invert subtrees
    invertTree(root.left);
    invertTree(root.right);
    return root;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is visited exactly once. |
| **Space** | **O(h)** | Recursion stack depth equals tree height. |

---

## Q107. Symmetric Tree (Mirror Check)

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, check whether it is a **mirror of itself** (i.e., symmetric around its center).

### Examples

**Example 1:**
```
Input:
         1
        / \
       2    2
      / \  / \
     3   4 4   3

Output: true
Explanation: Left subtree mirrors the right subtree perfectly.
```

**Example 2:**
```
Input:
         1
        / \
       2    2
        \    \
         3    3

Output: false
Explanation: Right child of left == 3, but it should mirror to left child of right.
             Left child of right is null, not 3.
```

**Example 3:**
```
Input:
    1

Output: true
Explanation: A single node is symmetric.
```

### Approach: Dual-Pointer Recursive Mirror Check

**Why this approach?**

A tree is symmetric if its left subtree is a mirror of its right subtree. Two trees are mirrors if:
1. Their roots have the same value.
2. The left subtree of one is a mirror of the right subtree of the other (and vice versa).

We define a helper `isMirror(t1, t2)` that checks this recursively. This is cleaner than serializing and comparing, and avoids the overhead of iterative BFS.

### Optimized Java Solution

```java
public boolean isSymmetric(TreeNode root) {
    if (root == null) return true;
    return isMirror(root.left, root.right);
}

private boolean isMirror(TreeNode t1, TreeNode t2) {
    if (t1 == null && t2 == null) return true;
    if (t1 == null || t2 == null) return false;
    return t1.val == t2.val
        && isMirror(t1.left, t2.right)
        && isMirror(t1.right, t2.left);
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited once in the comparison. |
| **Space** | **O(h)** | Recursion depth equals tree height. |

---

## Q108. Level Order Traversal (BFS)

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **level order traversal** --- node values grouped by level, from left to right, top to bottom.

### Examples

**Example 1:**
```
Input:
       3
      / \
     9  20
       /  \
      15   7

Output: [[3], [9, 20], [15, 7]]
```

**Example 2:**
```
Input:
    1

Output: [[1]]
```

**Example 3:**
```
Input: root = null
Output: []
```

### Approach: Queue-Based BFS

**Why this approach?**

BFS naturally explores level by level. We use a queue and process all nodes at the current level before moving to the next. The key insight: at the start of each iteration, `queue.size()` gives us exactly how many nodes are at the current level.

### Optimized Java Solution

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>(levelSize);

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            currentLevel.add(node.val);
            if (node.left != null)  queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(currentLevel);
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is enqueued and dequeued exactly once. |
| **Space** | **O(w)** | Queue holds at most one level at a time. Maximum width w can be up to n/2 for a complete binary tree. |

---

## Q109. Binary Tree Zigzag Level Order Traversal

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **zigzag level order traversal** --- node values grouped by level, alternating between left-to-right and right-to-left at each successive level.

### Examples

**Example 1:**
```
Input:
       3
      / \
     9  20
       /  \
      15   7

Output: [[3], [20, 9], [15, 7]]
Explanation:
  Level 0 (L->R): [3]
  Level 1 (R->L): [20, 9]
  Level 2 (L->R): [15, 7]
```

**Example 2:**
```
Input:
         1
        / \
       2    3
      / \  / \
     4   5 6   7

Output: [[1], [3, 2], [4, 5, 6, 7]]
```

**Example 3:**
```
Input: root = null
Output: []
```

### Approach: BFS with Direction Flag

**Why this approach?**

We perform standard BFS level-by-level. For each level, we collect values into a `LinkedList`. On even levels (0, 2, 4...) we add to the end (left-to-right). On odd levels, we add to the front (right-to-left). This avoids reversing arrays; `addFirst` on a LinkedList is O(1).

### Optimized Java Solution

```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        LinkedList<Integer> currentLevel = new LinkedList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            if (leftToRight) {
                currentLevel.addLast(node.val);
            } else {
                currentLevel.addFirst(node.val);
            }
            if (node.left != null)  queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(currentLevel);
        leftToRight = !leftToRight;
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited once. `addFirst`/`addLast` on LinkedList are O(1). |
| **Space** | **O(w)** | Queue holds up to one level of nodes. |

---

## Q110. Validate Binary Search Tree (BST)

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, determine if it is a valid **Binary Search Tree (BST)**. A valid BST satisfies:
- The left subtree of a node contains only nodes with keys **strictly less than** the node's key.
- The right subtree contains only nodes with keys **strictly greater than** the node's key.
- Both left and right subtrees must also be valid BSTs.

### Examples

**Example 1:**
```
Input:
    2
   / \
  1   3

Output: true
```

**Example 2:**
```
Input:
       5
      / \
     1   4
        / \
       3   6

Output: false
Explanation: Node 3 is in the right subtree of 5 but 3 < 5. Violates BST property.
```

**Example 3:**
```
Input:
       5
      / \
     4   6
        / \
       3   7

Output: false
Explanation: Node 3 is in the right subtree of 5 (since it's under 6, which is right of 5).
             3 < 5 violates the BST property.
```

### Approach: Range-Based Recursive Validation

**Why this approach?**

A common mistake is to only compare a node with its direct children. But BST property requires ALL nodes in the left subtree to be less than the root. We pass down a valid range `(min, max)` for each node. The root can be anything (`-inf` to `+inf`). When we go left, the max becomes the parent's value. When we go right, the min becomes the parent's value.

**Alternative:** Inorder traversal should produce a strictly increasing sequence. But the range approach is more intuitive and equally efficient.

### Optimized Java Solution

```java
public boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

private boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val)
        && validate(node.right, node.val, max);
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited once. Short-circuits on first violation. |
| **Space** | **O(h)** | Recursion depth equals tree height. |

---

## Q111. Lowest Common Ancestor of a Binary Tree

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given a binary tree and two nodes `p` and `q`, find their **Lowest Common Ancestor (LCA)** --- the deepest node that is an ancestor of both `p` and `q`. A node can be an ancestor of itself.

### Examples

**Example 1:**
```
Input:
            3
           / \
          5    1
         / \  / \
        6   2 0   8
           / \
          7   4

p = 5, q = 1
Output: 3
Explanation: The LCA of nodes 5 and 1 is the root node 3.
```

**Example 2:**
```
Input: (same tree as above)
p = 5, q = 4
Output: 5
Explanation: Node 5 is an ancestor of 4, and a node can be its own ancestor.
```

**Example 3:**
```
Input:
    1
   /
  2

p = 1, q = 2
Output: 1
```

### Approach: Post-Order DFS (Bottom-Up)

**Why this approach?**

We search for `p` and `q` recursively. For each node:
- If it's null, return null.
- If it matches `p` or `q`, return it.
- Recursively search left and right subtrees.
- If both subtrees return non-null, this node is the LCA (p and q are in different subtrees).
- If only one subtree returns non-null, propagate that result up.

This works because the first node where both `p` and `q` are found in different subtrees (or the node itself is one of them) is the LCA.

### Optimized Java Solution

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;

    TreeNode left  = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);

    if (left != null && right != null) return root;  // LCA found
    return left != null ? left : right;               // propagate non-null result
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Worst case visits every node (both targets in deepest leaves). |
| **Space** | **O(h)** | Recursion stack depth equals tree height. |

---

## Q112. Construct Binary Tree from Preorder and Inorder Traversal

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given two integer arrays `preorder` (preorder traversal) and `inorder` (inorder traversal) of the same binary tree, construct and return the binary tree. Assume no duplicate values.

### Examples

**Example 1:**
```
Input:
  preorder = [3, 9, 20, 15, 7]
  inorder  = [9, 3, 15, 20, 7]

Output:
       3
      / \
     9  20
       /  \
      15   7

Explanation:
  preorder[0] = 3 is the root.
  In inorder, 3 is at index 1 -> left subtree has [9], right subtree has [15, 20, 7].
```

**Example 2:**
```
Input:
  preorder = [1, 2, 4, 5, 3, 6]
  inorder  = [4, 2, 5, 1, 3, 6]

Output:
         1
        / \
       2    3
      / \    \
     4   5    6
```

**Example 3:**
```
Input:
  preorder = [1]
  inorder  = [1]

Output:
    1
```

### Approach: Recursive Partitioning with HashMap

**Why this approach?**

Key insight:
1. The first element in `preorder` is always the root.
2. Find that root in `inorder` --- everything to its left is the left subtree, everything to the right is the right subtree.
3. Use the sizes of these partitions to split the `preorder` array accordingly.
4. Recurse on left and right partitions.

We use a HashMap to look up indices in `inorder` in O(1) instead of O(n) linear search, bringing total time from O(n^2) to O(n).

### Optimized Java Solution

```java
public TreeNode buildTree(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inorderIndex = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inorderIndex.put(inorder[i], i);
    }
    return build(preorder, 0, preorder.length - 1,
                 0, inorder.length - 1, inorderIndex);
}

private TreeNode build(int[] preorder, int preStart, int preEnd,
                       int inStart, int inEnd,
                       Map<Integer, Integer> inorderIndex) {
    if (preStart > preEnd || inStart > inEnd) return null;

    int rootVal = preorder[preStart];
    TreeNode root = new TreeNode(rootVal);

    int rootIndexInorder = inorderIndex.get(rootVal);
    int leftSubtreeSize = rootIndexInorder - inStart;

    root.left = build(preorder, preStart + 1, preStart + leftSubtreeSize,
                      inStart, rootIndexInorder - 1, inorderIndex);
    root.right = build(preorder, preStart + leftSubtreeSize + 1, preEnd,
                       rootIndexInorder + 1, inEnd, inorderIndex);
    return root;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is constructed once. HashMap lookups are O(1). |
| **Space** | **O(n)** | HashMap stores n entries. Recursion stack is O(h). |

---

## Q113. Kth Smallest Element in a BST

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a BST and an integer `k`, return the **kth smallest** element (1-indexed) in the BST.

### Examples

**Example 1:**
```
Input:
       3
      / \
     1   4
      \
       2
k = 1

Output: 1
Explanation: Inorder = [1, 2, 3, 4]. The 1st smallest is 1.
```

**Example 2:**
```
Input:
           5
          / \
         3   6
        / \
       2   4
      /
     1
k = 3

Output: 3
Explanation: Inorder = [1, 2, 3, 4, 5, 6]. The 3rd smallest is 3.
```

**Example 3:**
```
Input:
    2
   / \
  1   3
k = 2

Output: 2
```

### Approach: Iterative Inorder Traversal (Early Termination)

**Why this approach?**

Inorder traversal of a BST yields values in sorted order. We perform an iterative inorder traversal and stop as soon as we've visited k nodes. This is better than collecting all elements and indexing, because we may terminate early without visiting the entire tree.

### Optimized Java Solution

```java
public int kthSmallest(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode current = root;

    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        current = stack.pop();
        k--;
        if (k == 0) return current.val;
        current = current.right;
    }
    return -1; // k is larger than tree size (shouldn't happen per problem constraints)
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(h + k)** | We go down to the leftmost node (h steps), then visit k nodes. |
| **Space** | **O(h)** | Stack holds at most h nodes. |

---

## Q114. Binary Tree Right Side View

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, imagine standing on the **right side** of it. Return the values of the nodes you can see from top to bottom.

### Examples

**Example 1:**
```
Input:
       1
      / \
     2    3
      \    \
       5    4

Output: [1, 3, 4]
Explanation:
  Level 0: see 1
  Level 1: see 3 (rightmost)
  Level 2: see 4 (rightmost)
```

**Example 2:**
```
Input:
       1
      /
     2
    /
   3

Output: [1, 2, 3]
Explanation: Only left children, but from the right side you see each one
             since there's nothing blocking them.
```

**Example 3:**
```
Input:
    1
     \
      3

Output: [1, 3]
```

### Approach: BFS --- Take Last Node per Level

**Why this approach?**

Using level-order BFS, the rightmost node at each level is the last node processed in that level. We simply record the last node of each level.

**Alternative:** DFS with right-first traversal (visit right subtree before left). Maintain a depth counter; if `depth == result.size()`, this is the first node seen at this depth from the right.

### Optimized Java Solution

```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            if (i == levelSize - 1) {
                result.add(node.val);  // last node in this level = rightmost
            }
            if (node.left != null)  queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Every node is visited exactly once. |
| **Space** | **O(w)** | Queue holds at most one level. Max width w can be n/2. |

---

## Q115. Flatten Binary Tree to Linked List

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, flatten it to a **linked list in-place**. The linked list should use the same `TreeNode` class where the `right` pointer points to the next node and the `left` pointer is always `null`. The list should be in **preorder** traversal order.

### Examples

**Example 1:**
```
Input:
         1
        / \
       2    5
      / \    \
     3   4    6

Output (as linked list via right pointers):
  1 -> 2 -> 3 -> 4 -> 5 -> 6

  Tree structure:
  1
   \
    2
     \
      3
       \
        4
         \
          5
           \
            6
```

**Example 2:**
```
Input:
    1
   /
  2

Output:
  1
   \
    2
```

**Example 3:**
```
Input: root = null
Output: null
```

### Approach: Reverse Postorder (Right -> Left -> Root)

**Why this approach?**

If we process the tree in reverse preorder (right, left, root), we visit nodes in the reverse order of the desired linked list. We maintain a `prev` pointer that tracks the previously processed node. For each current node, set `node.right = prev` and `node.left = null`, then update `prev = node`.

This is elegant, O(1) extra space (beyond recursion stack), and modifies the tree in-place.

### Optimized Java Solution

```java
private TreeNode prev = null;

public void flatten(TreeNode root) {
    if (root == null) return;
    // Process right first, then left, then current (reverse preorder)
    flatten(root.right);
    flatten(root.left);
    // Link current node to the previously processed node
    root.right = prev;
    root.left = null;
    prev = root;
}
```

> **Note:** If you need to avoid instance variables, use an iterative approach with a stack or the O(1)-space iterative approach below:

```java
public void flattenIterative(TreeNode root) {
    TreeNode current = root;
    while (current != null) {
        if (current.left != null) {
            // Find the rightmost node of the left subtree
            TreeNode rightmost = current.left;
            while (rightmost.right != null) {
                rightmost = rightmost.right;
            }
            // Connect it to current's right subtree
            rightmost.right = current.right;
            // Move left subtree to right
            current.right = current.left;
            current.left = null;
        }
        current = current.right;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited once. |
| **Space** | **O(h)** recursive / **O(1)** iterative | Recursive uses call stack. Iterative version uses only pointers. |

---

## Q116. Diameter of Binary Tree

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return the **diameter** --- the length of the **longest path between any two nodes**. The path may or may not pass through the root. The length is measured by the number of **edges** between nodes.

### Examples

**Example 1:**
```
Input:
       1
      / \
     2    3
    / \
   4   5

Output: 3
Explanation: Longest path is 4 -> 2 -> 1 -> 3 (or 5 -> 2 -> 1 -> 3), which has 3 edges.
```

**Example 2:**
```
Input:
       1
      /
     2
    / \
   4   5
  /     \
 6       7

Output: 4
Explanation: Path 6 -> 4 -> 2 -> 5 -> 7 has 4 edges. Note it doesn't pass through root 1.
```

**Example 3:**
```
Input:
    1
     \
      2

Output: 1
```

### Approach: DFS with Global Maximum

**Why this approach?**

For each node, the longest path *through* that node equals `leftHeight + rightHeight`. We compute the height of each subtree recursively, and at each node we update a global maximum diameter. The function returns the height (for the parent), while the side-effect updates the diameter.

### Optimized Java Solution

```java
private int maxDiameter = 0;

public int diameterOfBinaryTree(TreeNode root) {
    maxDiameter = 0;
    height(root);
    return maxDiameter;
}

private int height(TreeNode node) {
    if (node == null) return 0;
    int leftH  = height(node.left);
    int rightH = height(node.right);
    // The diameter through this node
    maxDiameter = Math.max(maxDiameter, leftH + rightH);
    // Return height of this subtree
    return 1 + Math.max(leftH, rightH);
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once. |
| **Space** | **O(h)** | Recursion stack depth equals tree height. |

---

## Q117. Path Sum II (All Root-to-Leaf Paths with Target Sum)

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a binary tree and an integer `targetSum`, return **all root-to-leaf paths** where the sum of node values equals `targetSum`. Each path should be a list of node values from root to leaf.

### Examples

**Example 1:**
```
Input:
           5
          / \
         4    8
        /    / \
       11   13   4
      / \       / \
     7   2     5   1

targetSum = 22

Output: [[5, 4, 11, 2], [5, 8, 4, 5]]
Explanation:
  Path 5 -> 4 -> 11 -> 2 = 22  (leaf node 2)
  Path 5 -> 8 -> 4 -> 5 = 22   (leaf node 5)
```

**Example 2:**
```
Input:
    1
   / \
  2   3

targetSum = 4

Output: []
Explanation: Path 1->2 = 3, Path 1->3 = 4. But node 3 is a leaf and 1+3=4? Yes!
             Wait: Output: [[1, 3]]
```

**Example 3:**
```
Input:
    1
   /
  2

targetSum = 1

Output: []
Explanation: 1 is not a leaf (it has a left child). Path 1->2 sums to 3, not 1.
```

### Approach: Backtracking DFS

**Why this approach?**

We use DFS to explore every root-to-leaf path. We maintain a running list of nodes in the current path and subtract each node's value from the remaining target sum. When we reach a leaf and the remaining sum equals the leaf's value, we've found a valid path. After exploring a subtree, we backtrack by removing the last element from the path.

### Optimized Java Solution

```java
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), result);
    return result;
}

private void dfs(TreeNode node, int remaining,
                 List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;

    path.add(node.val);

    // Check if it's a leaf with the correct sum
    if (node.left == null && node.right == null && remaining == node.val) {
        result.add(new ArrayList<>(path));  // copy the path
    } else {
        dfs(node.left,  remaining - node.val, path, result);
        dfs(node.right, remaining - node.val, path, result);
    }

    path.remove(path.size() - 1);  // backtrack
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n^2)** | We visit all n nodes. In the worst case (balanced tree), there are O(n/2) leaves each with path length O(log n), but copying paths takes O(n) each. Worst case: O(n) paths of O(n) length = O(n^2). |
| **Space** | **O(n)** | Path list can hold up to O(n) elements. Recursion stack is O(h). |

---

## Q118. Count Complete Tree Nodes in O(log^2 n)

**Difficulty:** `Medium` | **Data Structure:** Tree

### Problem

Given the root of a **complete binary tree**, return the number of nodes. A complete binary tree has every level fully filled except possibly the last, which is filled from left to right.

**Constraint:** Design an algorithm that runs in less than O(n) time.

### Examples

**Example 1:**
```
Input:
         1
        / \
       2    3
      / \  /
     4   5 6

Output: 6
Explanation: All 6 nodes counted. This is a complete tree (last level filled from left).
```

**Example 2:**
```
Input:
         1
        / \
       2    3
      / \  / \
     4   5 6   7

Output: 7
Explanation: A perfect binary tree is also complete.
```

**Example 3:**
```
Input:
    1

Output: 1
```

### Approach: Binary Search on Last Level + Height Comparison

**Why this approach?**

A naive DFS counts every node in O(n). But for a complete binary tree, we can exploit its structure:

1. Compute the left height (always go left) and right height (always go right) of the tree.
2. If they are equal, the tree is **perfect** --- return `2^h - 1`.
3. If not, recurse on left and right subtrees: `1 + count(left) + count(right)`.

The key insight: at each level of recursion, one of the two subtrees is always a perfect tree (because the tree is complete). So one branch terminates in O(log n) and the other recurses. Total: O(log^2 n).

### Optimized Java Solution

```java
public int countNodes(TreeNode root) {
    if (root == null) return 0;

    int leftHeight  = getLeftHeight(root);
    int rightHeight = getRightHeight(root);

    if (leftHeight == rightHeight) {
        // Perfect binary tree: 2^h - 1 nodes
        return (1 << leftHeight) - 1;
    }
    // Not perfect: recurse on both subtrees
    return 1 + countNodes(root.left) + countNodes(root.right);
}

private int getLeftHeight(TreeNode node) {
    int height = 0;
    while (node != null) {
        height++;
        node = node.left;
    }
    return height;
}

private int getRightHeight(TreeNode node) {
    int height = 0;
    while (node != null) {
        height++;
        node = node.right;
    }
    return height;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(log^2 n)** | At each recursion level, we compute heights in O(log n). There are O(log n) recursion levels (one subtree is always perfect, so recursion only continues on one side). Total: O(log n) levels * O(log n) height computation = O(log^2 n). |
| **Space** | **O(log n)** | Recursion depth is O(log n) since the tree is complete. |

---

## Q119. Binary Tree Maximum Path Sum

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return the **maximum path sum**. A path is any sequence of nodes where each pair of adjacent nodes has a parent-child connection. The path does **not** need to pass through the root and must contain at least one node. Node values can be **negative**.

### Examples

**Example 1:**
```
Input:
    1
   / \
  2   3

Output: 6
Explanation: Path 2 -> 1 -> 3 has sum 2 + 1 + 3 = 6.
```

**Example 2:**
```
Input:
       -10
       /  \
      9    20
          /  \
         15   7

Output: 42
Explanation: Path 15 -> 20 -> 7 has sum 15 + 20 + 7 = 42.
             Note: Including -10 would decrease the sum.
```

**Example 3:**
```
Input:
       -3

Output: -3
Explanation: Single node. Even though it's negative, the path must contain at least one node.
```

### Approach: DP on Tree (Post-Order with Global Max)

**Why this approach?**

This is a classic "DP on tree" problem. For each node, we compute two things:

1. **Max path sum through this node (can bend here):** `node.val + max(0, leftGain) + max(0, rightGain)`. This path uses both subtrees and this node as the "turning point." We update a global maximum with this value.

2. **Max gain this subtree can contribute to its parent:** `node.val + max(0, max(leftGain, rightGain))`. A path passed to the parent can only go through ONE child (it can't bend). If both children give negative gain, we take 0 (don't extend into them).

The key insight is separating "the path that *ends* here" (what we return) from "the path that *peaks* here" (what updates the global max).

### Optimized Java Solution

```java
private int maxSum = Integer.MIN_VALUE;

public int maxPathSum(TreeNode root) {
    maxSum = Integer.MIN_VALUE;
    maxGain(root);
    return maxSum;
}

/**
 * Returns the maximum "single-arm" gain from this node
 * (the max sum of a path starting at this node and going down).
 * Side effect: updates maxSum with the best path that peaks at this node.
 */
private int maxGain(TreeNode node) {
    if (node == null) return 0;

    // Max gain from left and right children (ignore negative gains)
    int leftGain  = Math.max(0, maxGain(node.left));
    int rightGain = Math.max(0, maxGain(node.right));

    // Path that peaks at this node (uses both arms)
    int pathThroughNode = node.val + leftGain + rightGain;
    maxSum = Math.max(maxSum, pathThroughNode);

    // Return the max single-arm gain to the parent
    return node.val + Math.max(leftGain, rightGain);
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once. |
| **Space** | **O(h)** | Recursion stack depth. O(log n) balanced, O(n) skewed. |

---

## Q120. Serialize and Deserialize Binary Tree

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

Design an algorithm to **serialize** a binary tree to a string and **deserialize** the string back to the original tree.

### Examples

**Example 1:**
```
Input:
       1
      / \
     2    3
         / \
        4   5

Serialized: "1,2,null,null,3,4,null,null,5,null,null"
Deserialized: (reconstructs the original tree above)
```

**Example 2:**
```
Input:
    1
   /
  2
 /
3

Serialized: "1,2,3,null,null,null,null"
Deserialized: (reconstructs the original tree)
```

**Example 3:**
```
Input: root = null
Serialized: "null"
Deserialized: null
```

### Approach: Preorder DFS with Null Markers

**Why this approach?**

We perform a preorder traversal and write each node's value (or "null" for null nodes) separated by commas. During deserialization, we read tokens in the same preorder sequence. When we encounter "null", we return null. Otherwise, we create a node and recursively build its left and right children.

**Why preorder?** The root comes first, making deserialization straightforward --- we know the first token is the root, then the next tokens build the left subtree, then the right.

**Alternative:** BFS serialization works but requires more bookkeeping for null children tracking.

### Optimized Java Solution

```java
public class Codec {

    // Serialize: Preorder DFS with "null" markers
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeDFS(root, sb);
        return sb.toString();
    }

    private void serializeDFS(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }
        sb.append(node.val).append(",");
        serializeDFS(node.left, sb);
        serializeDFS(node.right, sb);
    }

    // Deserialize: Read tokens in preorder sequence
    public TreeNode deserialize(String data) {
        Queue<String> tokens = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeDFS(tokens);
    }

    private TreeNode deserializeDFS(Queue<String> tokens) {
        String val = tokens.poll();
        if ("null".equals(val) || val == null) return null;

        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left  = deserializeDFS(tokens);
        node.right = deserializeDFS(tokens);
        return node;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Serialize and deserialize both visit each node once. |
| **Space** | **O(n)** | The serialized string has O(n) tokens. Recursion stack is O(h). |

---

## Q121. Vertical Order Traversal of Binary Tree

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

Given the root of a binary tree, return its **vertical order traversal**. For each column index (root is column 0, left child is column - 1, right child is column + 1), list the node values from top to bottom. If two nodes share the same row and column, sort them by value.

### Examples

**Example 1:**
```
Input:
       3
      / \
     9   20
        /  \
       15   7

Column -1: [9]
Column  0: [3, 15]
Column  1: [20]
Column  2: [7]

Output: [[9], [3, 15], [20], [7]]
```

**Example 2:**
```
Input:
         1
        / \
       2    3
      / \  / \
     4   5 6   7

Column -2: [4]
Column -1: [2]
Column  0: [1, 5, 6]   (5 and 6 are same row & col -> sorted by value)
Column  1: [3]
Column  2: [7]

Output: [[4], [2], [1, 5, 6], [3], [7]]
```

**Example 3:**
```
Input:
         1
        / \
       2    3
      / \  / \
     4   6 5   7

Column  0: [1, 5, 6]   (5 and 6 same row/col -> sorted: 5, 6)

Output: [[4], [2], [1, 5, 6], [3], [7]]
```

### Approach: BFS with Column Mapping + TreeMap

**Why this approach?**

We use BFS (to guarantee top-to-bottom ordering) and track each node's column index. A `TreeMap<Integer, List>` maps columns to lists of (row, value) pairs. After BFS completes, we iterate columns in sorted order. Within each column, we sort by row first, then by value (for same-row ties).

**Why TreeMap?** It keeps columns sorted automatically. We could use a HashMap and sort keys later, but TreeMap is cleaner.

### Optimized Java Solution

```java
public List<List<Integer>> verticalTraversal(TreeNode root) {
    // column -> list of (row, value) pairs
    TreeMap<Integer, List<int[]>> columnMap = new TreeMap<>();
    // BFS: queue stores (node, row, col)
    Queue<Object[]> queue = new LinkedList<>();
    queue.offer(new Object[]{root, 0, 0});

    while (!queue.isEmpty()) {
        Object[] entry = queue.poll();
        TreeNode node = (TreeNode) entry[0];
        int row = (int) entry[1];
        int col = (int) entry[2];

        columnMap.computeIfAbsent(col, k -> new ArrayList<>())
                 .add(new int[]{row, node.val});

        if (node.left != null)  queue.offer(new Object[]{node.left,  row + 1, col - 1});
        if (node.right != null) queue.offer(new Object[]{node.right, row + 1, col + 1});
    }

    List<List<Integer>> result = new ArrayList<>();
    for (List<int[]> column : columnMap.values()) {
        // Sort by row first, then by value for same-row ties
        column.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
        List<Integer> sortedValues = new ArrayList<>();
        for (int[] pair : column) {
            sortedValues.add(pair[1]);
        }
        result.add(sortedValues);
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n log n)** | BFS is O(n). Sorting within columns is O(n log n) in aggregate. |
| **Space** | **O(n)** | TreeMap and queue each store O(n) entries. |

---

## Q122. Recover BST (Two Nodes Swapped by Mistake)

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

In a BST, exactly **two nodes have been swapped** by mistake. Recover the tree by swapping them back **without changing its structure**.

### Examples

**Example 1:**
```
Input:
    1       (This should be a valid BST, but 1 and 3 are swapped)
   / \
  3   2

  Inorder: [3, 1, 2]  (should be [1, 2, 3])

Output:
    3
   / \
  1   2

  Inorder: [1, 2, 3]  ✓ Valid BST
```

**Example 2:**
```
Input:
       3
      / \
     1   4
        /
       2

  Inorder: [1, 3, 2, 4]  (3 and 2 are swapped — adjacent in inorder)

Output:
       2
      / \
     1   4
        /
       3

  Inorder: [1, 2, 3, 4]  ✓ Valid BST
```

**Example 3:**
```
Input:
       6
      / \
     3   4
    / \
   2   5

  Inorder: [2, 3, 5, 6, 4]  (5 and 4 are swapped — non-adjacent)
  Anomalies: 5 > 6 is wrong, 6 > 4 is wrong.
  First anomaly: node with value 5 (first "big" node)
  Second anomaly: node with value 4 (second "small" node)
  Swap 5 and 4 to fix.

Output:
       6
      / \
     3   5
    / \
   2   4

  Inorder: [2, 3, 4, 5, 6]  ✓ Valid BST
```

### Approach: Inorder Traversal Anomaly Detection

**Why this approach?**

Inorder traversal of a valid BST gives strictly increasing values. When two nodes are swapped, there will be either **one or two** places where a value is greater than its successor (inversions).

- **Two inversions (non-adjacent swap):** In `[1, 5, 3, 4, 2, 6]`, inversions at (5,3) and (4,2). Swap the first element of the first inversion (5) with the second element of the second inversion (2).
- **One inversion (adjacent swap):** In `[1, 3, 2, 4]`, inversion at (3,2). Swap these two directly.

We track `first`, `second`, and `prev` pointers during inorder traversal.

### Optimized Java Solution

```java
private TreeNode first  = null;  // first misplaced node
private TreeNode second = null;  // second misplaced node
private TreeNode prev   = null;  // previously visited node in inorder

public void recoverTree(TreeNode root) {
    first = second = prev = null;
    inorder(root);
    // Swap the values of the two misplaced nodes
    int temp = first.val;
    first.val = second.val;
    second.val = temp;
}

private void inorder(TreeNode node) {
    if (node == null) return;

    inorder(node.left);

    if (prev != null && prev.val > node.val) {
        if (first == null) {
            first = prev;      // first inversion: the "big" node
        }
        second = node;         // always update second to the "small" node
    }
    prev = node;

    inorder(node.right);
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Full inorder traversal visits each node once. |
| **Space** | **O(h)** | Recursion stack. Can be O(1) extra space with Morris traversal. |

---

## Q123. Morris Inorder Traversal (O(1) Space, No Stack)

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

Perform an **inorder traversal** of a binary tree using **O(1) extra space** --- no stack, no recursion. This is known as **Morris Traversal**. The tree structure must be restored to its original form after traversal.

### Examples

**Example 1:**
```
Input:
        4
       / \
      2    6
     / \  / \
    1   3 5   7

Output: [1, 2, 3, 4, 5, 6, 7]
```

**Example 2:**
```
Input:
    1
     \
      2
     /
    3

Output: [1, 3, 2]
```

**Example 3:**
```
Input:
         1
        / \
       2    3

Output: [2, 1, 3]
```

### Approach: Threaded Binary Tree (Morris Traversal)

**Why this approach?**

Normal inorder requires O(h) space for a stack or recursion. Morris traversal achieves O(1) space by temporarily creating **threaded links** --- pointers from a node's inorder predecessor back to it. This lets us "return" to a node after processing its left subtree without a stack.

#### Detailed Walk-Through of How Threaded Pointers Work

Consider this tree:
```
        4
       / \
      2    6
     / \
    1   3
```

**Step 1:** `current = 4`. It has a left child (2).
- Find the inorder predecessor of 4 in its left subtree. Start at 2, go right as far as possible: 2 -> 3. Node 3 is the predecessor.
- 3's right child is null, so **create a thread**: `3.right = 4`.
- Move left: `current = 2`.

```
        4
       / \
      2    6
     / \
    1   3 ---> 4  (thread created: 3.right points back to 4)
```

**Step 2:** `current = 2`. It has a left child (1).
- Find predecessor of 2: start at 1, go right. 1's right is null. Predecessor is 1.
- 1's right is null, so **create a thread**: `1.right = 2`.
- Move left: `current = 1`.

```
        4
       / \
      2    6
     / \
    1 -> 2    3 ---> 4
    (thread)  (thread)
```

**Step 3:** `current = 1`. No left child.
- **Visit 1** (add to result). Output so far: `[1]`.
- Move right: `current = 1.right = 2` (following the thread back).

**Step 4:** `current = 2`. It has a left child (1).
- Find predecessor: start at 1, go right: `1.right = 2` (it's current!). We've come back via a thread.
- This means the left subtree is already processed. **Remove the thread**: `1.right = null`.
- **Visit 2** (add to result). Output: `[1, 2]`.
- Move right: `current = 3`.

**Step 5:** `current = 3`. No left child.
- **Visit 3**. Output: `[1, 2, 3]`.
- Move right: `current = 3.right = 4` (following the thread back).

**Step 6:** `current = 4`. It has a left child (2).
- Find predecessor: start at 2, go right: 2 -> 3, and `3.right = 4` (it's current!).
- Left subtree already processed. **Remove the thread**: `3.right = null`.
- **Visit 4**. Output: `[1, 2, 3, 4]`.
- Move right: `current = 6`.

**Step 7:** `current = 6`. No left child.
- **Visit 6**. Output: `[1, 2, 3, 4, 6]`.
- Move right: `current = null`. Done.

Wait, we missed 5 and 7. Let me redo with the full tree from Example 1:
```
        4
       / \
      2    6
     / \  / \
    1   3 5   7
```

After visiting 4, we move to 6. Node 6 has a left child (5). Find predecessor of 6: go to 5, its right is null. Create thread: `5.right = 6`. Move left to 5. Visit 5 (no left child). Follow thread to 6. Detect thread (predecessor's right == current), remove it. Visit 6. Move right to 7. Visit 7. Done.

Output: `[1, 2, 3, 4, 5, 6, 7]` ✓

#### Summary of the Algorithm:
```
while current != null:
    if current has no left child:
        VISIT current
        current = current.right
    else:
        find inorder predecessor of current in left subtree
        if predecessor.right == null:       // first visit
            predecessor.right = current     // create thread
            current = current.left
        else:                               // predecessor.right == current (thread exists)
            predecessor.right = null        // remove thread (restore tree)
            VISIT current
            current = current.right
```

### Optimized Java Solution

```java
public List<Integer> morrisInorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    TreeNode current = root;

    while (current != null) {
        if (current.left == null) {
            // No left subtree: visit this node, move right
            result.add(current.val);
            current = current.right;
        } else {
            // Find the inorder predecessor (rightmost node in left subtree)
            TreeNode predecessor = current.left;
            while (predecessor.right != null && predecessor.right != current) {
                predecessor = predecessor.right;
            }

            if (predecessor.right == null) {
                // First time here: create thread and move left
                predecessor.right = current;
                current = current.left;
            } else {
                // Second time here: left subtree done. Remove thread, visit, go right
                predecessor.right = null;
                result.add(current.val);
                current = current.right;
            }
        }
    }
    return result;
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each edge is traversed at most 3 times (once normally, once to find predecessor, once to remove thread). Total work is proportional to n. |
| **Space** | **O(1)** | No stack, no recursion. Only a few pointers used. The result list is O(n) but that's output space, not auxiliary. |

---

## Q124. Binary Tree Cameras

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

Given a binary tree, we install cameras on the tree nodes where each camera at a node can **monitor** its parent, itself, and its immediate children. Return the **minimum number of cameras** needed to monitor all nodes.

### Examples

**Example 1:**
```
Input:
       0
      /
     0
    / \
   0   0

Output: 1
Explanation: One camera at the second node monitors all four nodes.

       0
      /
    [C]       <-- camera here
    / \
   0   0
```

**Example 2:**
```
Input:
         0
        /
       0
      /
     0
    /
   0

Output: 2
Explanation:
         0
        /
      [C]
      /
     0
    /
  [C]
```

**Example 3:**
```
Input:
       0
      / \
     0    0

Output: 1
Explanation: Camera at root monitors all three nodes.
```

### Approach: Greedy DP on Tree (Post-Order with 3 States)

**Why this approach?**

This is a classic tree DP / greedy problem. We do a post-order traversal (children before parent) and assign each node one of three states:

- **State 0 (NEEDS_CAMERA):** This node is NOT monitored and needs a camera placed on its parent.
- **State 1 (HAS_CAMERA):** This node has a camera installed.
- **State 2 (MONITORED):** This node is monitored (by a child's camera) but has no camera itself.

**Greedy insight:** Place cameras as **far from leaves as possible**. Leaves should NOT get cameras (wasteful --- they can only monitor 2 nodes). Instead, let leaves be "needing coverage" and place cameras on their parents.

**Transition rules for a node based on its children's states:**
1. If any child `NEEDS_CAMERA` (state 0) --> place a camera here (return state 1).
2. If any child `HAS_CAMERA` (state 1) --> this node is monitored (return state 2).
3. If all children are `MONITORED` (state 2) or null --> this node needs coverage from parent (return state 0).

**Null nodes** return state 2 (MONITORED) --- they don't need coverage, and this prevents placing cameras on leaves.

After the DFS, if the root still needs a camera (state 0), increment the count.

### Optimized Java Solution

```java
private int cameras = 0;

// States
private static final int NEEDS_CAMERA = 0;
private static final int HAS_CAMERA   = 1;
private static final int MONITORED    = 2;

public int minCameraCover(TreeNode root) {
    cameras = 0;
    int rootState = dfs(root);
    // If root itself is not covered, it needs a camera
    if (rootState == NEEDS_CAMERA) cameras++;
    return cameras;
}

private int dfs(TreeNode node) {
    if (node == null) return MONITORED;  // null nodes are "covered"

    int left  = dfs(node.left);
    int right = dfs(node.right);

    // If any child needs a camera, we MUST place one here
    if (left == NEEDS_CAMERA || right == NEEDS_CAMERA) {
        cameras++;
        return HAS_CAMERA;
    }

    // If any child has a camera, this node is monitored
    if (left == HAS_CAMERA || right == HAS_CAMERA) {
        return MONITORED;
    }

    // Both children are MONITORED (no camera nearby) -> this node needs coverage
    return NEEDS_CAMERA;
}
```

**Trace through Example 2:**
```
         0 (d)
        /
       0 (c)
      /
     0 (b)
    /
   0 (a)
```
- Node (a) is a leaf. Left = null -> MONITORED, Right = null -> MONITORED. Both monitored -> return NEEDS_CAMERA.
- Node (b): left = NEEDS_CAMERA -> place camera. cameras = 1. Return HAS_CAMERA.
- Node (c): left = HAS_CAMERA -> return MONITORED.
- Node (d): left = MONITORED, right = null (MONITORED) -> return NEEDS_CAMERA.
- Root needs camera -> cameras++ = 2. **Answer: 2** ✓

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each node is visited exactly once in post-order DFS. |
| **Space** | **O(h)** | Recursion stack depth. O(log n) for balanced trees, O(n) for skewed. |

---

## Q125. Convert Sorted Array/List to Balanced BST

**Difficulty:** `Hard` | **Data Structure:** Tree

### Problem

Given a sorted (ascending) integer array, convert it to a **height-balanced BST** --- a binary tree where the depth of the two subtrees of every node never differs by more than one.

**Follow-up:** Also handle a sorted singly-linked list as input efficiently.

### Examples

**Example 1:**
```
Input: nums = [-10, -3, 0, 5, 9]

Output (one valid answer):
         0
        / \
      -3    9
      /    /
   -10   5

Explanation: The middle element (0) becomes root.
             Left half [-10, -3] builds the left subtree.
             Right half [5, 9] builds the right subtree.
```

**Example 2:**
```
Input: nums = [1, 2, 3, 4, 5, 6, 7]

Output:
           4
          / \
         2    6
        / \  / \
       1   3 5   7
```

**Example 3:**
```
Input: nums = [1, 3]

Output (one valid answer):
    1           or          3
     \                     /
      3                   1
```

### Approach: Divide and Conquer (Binary Search-Style Partitioning)

**Why this approach?**

A balanced BST has roughly equal nodes in left and right subtrees. The sorted array gives us a natural strategy: **the middle element becomes the root**, the left half forms the left subtree, and the right half forms the right subtree. Recursing on each half guarantees balance because we always split evenly.

This is exactly how binary search partitions an array, giving us O(log n) height.

**For sorted linked list (follow-up):**
We can't random-access the middle. Two approaches:
1. Convert list to array first, then use the array approach. O(n) extra space.
2. Use an inorder simulation: advance a global pointer through the list as we build nodes left-to-right. This avoids converting to an array.

### Optimized Java Solution

**Part A: Sorted Array to BST**
```java
public TreeNode sortedArrayToBST(int[] nums) {
    return buildBST(nums, 0, nums.length - 1);
}

private TreeNode buildBST(int[] nums, int left, int right) {
    if (left > right) return null;

    int mid = left + (right - left) / 2;
    TreeNode node = new TreeNode(nums[mid]);
    node.left  = buildBST(nums, left, mid - 1);
    node.right = buildBST(nums, mid + 1, right);
    return node;
}
```

**Part B: Sorted Linked List to BST**
```java
// Definition for singly-linked list:
// class ListNode { int val; ListNode next; ListNode(int val) { this.val = val; } }

private ListNode current;  // global pointer advancing through the linked list

public TreeNode sortedListToBST(ListNode head) {
    // Count the length of the list
    int size = 0;
    ListNode temp = head;
    while (temp != null) {
        size++;
        temp = temp.next;
    }

    current = head;
    return buildFromList(0, size - 1);
}

/**
 * Inorder simulation: builds nodes in sorted order,
 * which matches the order of the linked list.
 */
private TreeNode buildFromList(int left, int right) {
    if (left > right) return null;

    int mid = left + (right - left) / 2;

    // Build left subtree first (inorder: left, root, right)
    TreeNode leftChild = buildFromList(left, mid - 1);

    // Create current node using the list's current pointer
    TreeNode node = new TreeNode(current.val);
    current = current.next;  // advance the list pointer

    // Build right subtree
    node.left  = leftChild;
    node.right = buildFromList(mid + 1, right);

    return node;
}
```

**Why the linked list approach works:**

The key insight is that an inorder traversal of a BST visits nodes in sorted order --- the same order as the linked list. By building the tree inorder (left subtree first, then root, then right subtree), we can consume linked list nodes sequentially without random access. The `mid` calculation determines the structure (where to split), while the actual values come from advancing the list pointer.

### Complexity Analysis

**Sorted Array to BST:**

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | Each element is visited once to create a node. |
| **Space** | **O(log n)** | Recursion depth is log n (balanced partitioning). No extra data structures. |

**Sorted List to BST:**

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(n)** | List is traversed once. Each node created in O(1). |
| **Space** | **O(log n)** | Recursion depth is log n. No conversion to array needed. |
# GRAPH

> **Questions Q126 -- Q150** | 4 Basic, 13 Medium, 8 Hard

## Mini-Index

| # | Title | Difficulty | Core Technique |
|---|---|---|---|
| Q126 | BFS Traversal of a Graph | Basic | BFS + visited set |
| Q127 | DFS Traversal of a Graph | Basic | Iterative DFS with stack |
| Q128 | Count Connected Components in Undirected Graph | Basic | DFS / Union-Find |
| Q129 | Find if Path Exists Between Two Nodes | Basic | BFS / DFS |
| Q130 | Detect Cycle in an Undirected Graph | Medium | Union-Find |
| Q131 | Detect Cycle in a Directed Graph | Medium | DFS coloring (white/gray/black) |
| Q132 | Number of Islands | Medium | Grid DFS/BFS |
| Q133 | Clone a Graph | Medium | BFS + HashMap deep copy |
| Q134 | Is Graph Bipartite? | Medium | 2-coloring BFS |
| Q135 | Topological Sort (Kahn's Algorithm) | Medium | BFS in-degree |
| Q136 | Course Schedule | Medium | Topological sort cycle detection |
| Q137 | Course Schedule II | Medium | Kahn's BFS order |
| Q138 | Word Ladder | Medium | BFS shortest transformation |
| Q139 | Pacific Atlantic Water Flow | Medium | Multi-source DFS from borders |
| Q140 | Surrounded Regions | Medium | Border-connected DFS |
| Q141 | Graph Valid Tree | Medium | n-1 edges + connected check |
| Q142 | Minimum Height Trees | Medium | Leaf-pruning to find centroids |
| Q143 | Minimum Spanning Tree (Kruskal's) | Hard | Sort edges + Union-Find |
| Q144 | Dijkstra's Shortest Path | Hard | Priority queue greedy |
| Q145 | Bellman-Ford Shortest Path | Hard | Edge relaxation, negative weights |
| Q146 | Floyd-Warshall All-Pairs Shortest Path | Hard | DP on intermediate vertices |
| Q147 | Network Delay Time | Hard | Dijkstra weighted BFS |
| Q148 | Cheapest Flights Within K Stops | Hard | Modified Bellman-Ford |
| Q149 | Critical Connections in Network | Hard | Tarjan's bridges (low-link) |
| Q150 | Alien Dictionary | Hard | Topological sort from constraints |

---
---

## Q126. BFS Traversal of a Graph

**Difficulty:** `Basic` | **Data Structure:** Graph | **Pattern:** Breadth-First Search

### Problem

Given an undirected graph represented as an adjacency list and a source node, return the BFS traversal order starting from the source. Visit neighbors in the order they appear in the adjacency list. Use a visited set to avoid revisiting nodes.

### Examples

**Example 1:**

```
Graph (adjacency list):
  0 -- 1
  |    |
  3 -- 2

  0: [1, 3]
  1: [0, 2]
  2: [1, 3]
  3: [0, 2]

Input:  n = 4, adj = [[1,3],[0,2],[1,3],[0,2]], source = 0
Output: [0, 1, 3, 2]
Explanation: Start at 0 -> enqueue neighbors 1,3 -> visit 1, enqueue 2 -> visit 3 (2 already queued) -> visit 2.
```

**Example 2:**

```
Graph:
  0 -- 1    3 -- 4
       |
       2

  0: [1]
  1: [0, 2]
  2: [1]
  3: [4]
  4: [3]

Input:  n = 5, adj = [[1],[0,2],[1],[4],[3]], source = 0
Output: [0, 1, 2]
Explanation: Only nodes reachable from source 0 are visited. Nodes 3 and 4 are in a different component.
```

**Example 3:**

```
Input:  n = 1, adj = [[]], source = 0
Output: [0]
Explanation: Single node, just return itself.
```

### Approach: Iterative BFS with Queue and Visited Set

**Why this approach?**

BFS explores all neighbors at the current depth before moving deeper, making it the natural choice for level-order traversal and shortest-path problems in unweighted graphs. We use a `boolean[] visited` array (O(1) lookup) instead of a `HashSet` since nodes are numbered 0..n-1. We mark a node as visited **when we enqueue it** (not when we dequeue it) to prevent the same node from being added to the queue multiple times -- this is a critical optimization that keeps the queue size bounded by O(V).

An `ArrayDeque` is used instead of `LinkedList` because `ArrayDeque` has better cache locality, no node-allocation overhead, and is recommended by the Java documentation for both queue and stack use cases.

### Optimized Java Solution

```java
import java.util.*;

public class BFSTraversal {

    /**
     * Returns BFS traversal order starting from the given source node.
     *
     * @param n      number of nodes (0-indexed)
     * @param adj    adjacency list
     * @param source starting node
     * @return list of nodes in BFS order
     */
    public List<Integer> bfs(int n, List<List<Integer>> adj, int source) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[n];

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);
        visited[source] = true;          // mark visited at enqueue time

        while (!queue.isEmpty()) {
            int node = queue.poll();
            order.add(node);

            for (int neighbor : adj.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;  // mark BEFORE enqueue
                    queue.offer(neighbor);
                }
            }
        }
        return order;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Every vertex is enqueued/dequeued once (O(V)). Every edge is examined once from each endpoint (O(E) total for undirected, O(E) for directed). |
| **Space** | **O(V)** | The `visited` array is O(V). The queue holds at most O(V) nodes. The output list is O(V). |

---
---

## Q127. DFS Traversal of a Graph

**Difficulty:** `Basic` | **Data Structure:** Graph | **Pattern:** Depth-First Search (Iterative)

### Problem

Given an undirected graph represented as an adjacency list and a source node, return the DFS traversal order starting from the source. Implement iteratively using an explicit stack. Visit neighbors in the order they appear in the adjacency list.

### Examples

**Example 1:**

```
Graph:
  0 -- 1
  |    |
  3 -- 2

  0: [1, 3]
  1: [0, 2]
  2: [1, 3]
  3: [0, 2]

Input:  n = 4, adj = [[1,3],[0,2],[1,3],[0,2]], source = 0
Output: [0, 3, 2, 1]
Explanation: Stack-based DFS -- push 0; pop 0, push 1,3; pop 3, push 2; pop 2; pop 1 (already visited skip neighbors).
Note: iterative DFS processes the LAST pushed neighbor first, so the traversal order may differ from recursive DFS.
```

**Example 2:**

```
Graph:
  0 -> 1 -> 2 -> 3

Input:  n = 4, adj = [[1],[2],[3],[]], source = 0
Output: [0, 1, 2, 3]
```

**Example 3:**

```
Graph:
  0 -- 1
  |  / |
  2    3

  0: [1, 2]
  1: [0, 2, 3]
  2: [0, 1]
  3: [1]

Input:  n = 4, adj = [[1,2],[0,2,3],[0,1],[1]], source = 0
Output: [0, 2, 1, 3]
Explanation: Push 0; pop 0, push 1,2; pop 2 (push 0-visited,1-visited); pop 1 (push 0-visited,2-visited,3); pop 3.
```

### Approach: Iterative DFS with Explicit Stack

**Why this approach?**

Iterative DFS avoids stack overflow on deep graphs (e.g., a linked-list-shaped graph with 100K nodes). The key detail is that we check `visited` **when popping** from the stack rather than when pushing. This is because a node may be pushed multiple times from different neighbors before it is first popped. Checking at pop time is simpler and correct; the duplicate pushes are harmless and simply discarded when popped.

Alternative: checking at push time (like BFS) requires more bookkeeping but reduces stack size. Both are valid.

### Optimized Java Solution

```java
import java.util.*;

public class DFSTraversal {

    /**
     * Returns iterative DFS traversal order from the given source.
     */
    public List<Integer> dfs(int n, List<List<Integer>> adj, int source) {
        List<Integer> order = new ArrayList<>();
        boolean[] visited = new boolean[n];

        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(source);

        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (visited[node]) continue;   // skip if already processed
            visited[node] = true;
            order.add(node);

            // Push neighbors in reverse order so that the first neighbor
            // in the adjacency list is processed first (sits on top of stack).
            List<Integer> neighbors = adj.get(node);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                int nb = neighbors.get(i);
                if (!visited[nb]) {
                    stack.push(nb);
                }
            }
        }
        return order;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Each vertex is popped and processed at most once. Each edge contributes at most one push per endpoint. |
| **Space** | **O(V + E)** | In the worst case the stack can hold O(E) entries (e.g., a star graph pushes all edges of the center). The visited array is O(V). |

---
---

## Q128. Count Connected Components in Undirected Graph

**Difficulty:** `Basic` | **Data Structure:** Graph | **Pattern:** DFS / Union-Find

### Problem

Given `n` nodes labeled `0` to `n-1` and a list of undirected edges, return the number of connected components in the graph.

### Examples

**Example 1:**

```
  0 -- 1     3
       |     |
       2     4

Input:  n = 5, edges = [[0,1],[1,2],[3,4]]
Output: 2
Explanation: Component 1: {0,1,2}, Component 2: {3,4}.
```

**Example 2:**

```
  0    1    2

Input:  n = 3, edges = []
Output: 3
Explanation: No edges, each node is its own component.
```

**Example 3:**

```
  0 -- 1 -- 2 -- 3

Input:  n = 4, edges = [[0,1],[1,2],[2,3]]
Output: 1
Explanation: All nodes are connected in a single chain.
```

### Approach: DFS from Every Unvisited Node

**Why this approach?**

Each DFS call from an unvisited node discovers one entire connected component. We simply iterate through all nodes, and each time we find an unvisited node, we increment the component count and run DFS to mark everything reachable. This is O(V+E) -- optimal since we must examine every edge at least once. Union-Find is an alternative (also shown below), useful in streaming/dynamic settings but slightly more complex.

### Optimized Java Solution

```java
import java.util.*;

public class ConnectedComponents {

    /**
     * Counts connected components using DFS.
     */
    public int countComponents(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        boolean[] visited = new boolean[n];
        int components = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                components++;
                dfs(i, adj, visited);
            }
        }
        return components;
    }

    private void dfs(int node, List<List<Integer>> adj, boolean[] visited) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(node);
        visited[node] = true;

        while (!stack.isEmpty()) {
            int curr = stack.pop();
            for (int nb : adj.get(curr)) {
                if (!visited[nb]) {
                    visited[nb] = true;
                    stack.push(nb);
                }
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Building adjacency list: O(E). DFS across all components visits each vertex once and each edge twice (undirected). |
| **Space** | **O(V + E)** | Adjacency list: O(V + E). Visited array: O(V). DFS stack: O(V) worst case. |

---
---

## Q129. Find if Path Exists Between Two Nodes

**Difficulty:** `Basic` | **Data Structure:** Graph | **Pattern:** BFS / DFS

### Problem

Given an undirected graph with `n` nodes (labeled `0` to `n-1`) and an array of edges, determine if there is a valid path from node `source` to node `destination`.

*(LeetCode 1971)*

### Examples

**Example 1:**

```
  0 -- 1 -- 2

Input:  n = 3, edges = [[0,1],[1,2]], source = 0, destination = 2
Output: true
Explanation: Path exists: 0 -> 1 -> 2
```

**Example 2:**

```
  0 -- 1    2 -- 3

Input:  n = 4, edges = [[0,1],[2,3]], source = 0, destination = 3
Output: false
Explanation: No path connects node 0 to node 3.
```

**Example 3:**

```
Input:  n = 1, edges = [], source = 0, destination = 0
Output: true
Explanation: Source == destination; trivially reachable.
```

### Approach: BFS from Source

**Why this approach?**

We need simple reachability, so BFS from the source is straightforward. If we reach the destination, return true. If BFS finishes without reaching it, return false. BFS is preferred over DFS here because it also finds the shortest path if needed later, and avoids deep recursion issues. An alternative Union-Find approach works too and is better for repeated queries on the same graph.

### Optimized Java Solution

```java
import java.util.*;

public class PathExists {

    public boolean validPath(int n, int[][] edges, int source, int destination) {
        if (source == destination) return true;

        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }

        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int nb : adj.get(node)) {
                if (nb == destination) return true;  // early exit
                if (!visited[nb]) {
                    visited[nb] = true;
                    queue.offer(nb);
                }
            }
        }
        return false;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | In the worst case we visit every node and examine every edge before concluding no path exists. |
| **Space** | **O(V + E)** | Adjacency list: O(V + E). Visited array + queue: O(V). |

---
---

## Q130. Detect Cycle in an Undirected Graph (Union-Find)

**Difficulty:** `Medium` | **Data Structure:** Graph | **Pattern:** Union-Find (Disjoint Set Union)

### Problem

Given an undirected graph with `n` nodes and a list of edges, determine if the graph contains a cycle. Use the Union-Find approach.

### Examples

**Example 1:**

```
  0 -- 1
  |    |
  3 -- 2

Input:  n = 4, edges = [[0,1],[1,2],[2,3],[3,0]]
Output: true
Explanation: Edge (3,0) connects two nodes already in the same component -> cycle detected.
```

**Example 2:**

```
  0 -- 1 -- 2

Input:  n = 3, edges = [[0,1],[1,2]]
Output: false
Explanation: This is a tree (n-1 edges, no cycle).
```

**Example 3:**

```
  0 -- 1
       |
  2 -- 3

Input:  n = 4, edges = [[0,1],[1,3],[2,3]]
Output: false
Explanation: All nodes form a single tree with 3 edges and 4 nodes.
```

### Approach: Union-Find with Path Compression and Union by Rank

**Why this approach?**

For each edge (u, v), we check if u and v are already in the same connected component (same root). If they are, adding this edge creates a cycle. Union-Find is especially elegant here because it processes edges one-by-one without building a full adjacency list. With path compression + union by rank, each operation is nearly O(1) amortized -- specifically O(alpha(n)) where alpha is the inverse Ackermann function.

Alternative: DFS can detect cycles by tracking parent pointers, but Union-Find is cleaner, faster in practice, and extends naturally to dynamic/streaming graphs.

### Optimized Java Solution

```java
import java.util.*;

public class CycleUndirected {

    private int[] parent;
    private int[] rank;

    public boolean hasCycle(int n, int[][] edges) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;  // each node is its own root

        for (int[] edge : edges) {
            int rootA = find(edge[0]);
            int rootB = find(edge[1]);

            if (rootA == rootB) return true;  // cycle: both endpoints share a root

            union(rootA, rootB);
        }
        return false;
    }

    /** Find with path compression. */
    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);  // path compression
        }
        return parent[x];
    }

    /** Union by rank. */
    private void union(int a, int b) {
        if (rank[a] < rank[b]) {
            parent[a] = b;
        } else if (rank[a] > rank[b]) {
            parent[b] = a;
        } else {
            parent[b] = a;
            rank[a]++;
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(E * alpha(V))** | Each union/find is O(alpha(V)) amortized, effectively O(1). We process E edges. |
| **Space** | **O(V)** | Parent and rank arrays, each of size V. |

---
---

## Q131. Detect Cycle in a Directed Graph (DFS Coloring)

**Difficulty:** `Medium` | **Data Structure:** Graph | **Pattern:** DFS with 3-state coloring

### Problem

Given a directed graph with `n` nodes and a list of directed edges, determine if the graph contains a cycle. Use the DFS coloring approach (white/gray/black).

### Examples

**Example 1:**

```
  0 --> 1 --> 2
  ^           |
  +-----------+

Input:  n = 3, edges = [[0,1],[1,2],[2,0]]
Output: true
Explanation: Cycle: 0 -> 1 -> 2 -> 0. During DFS from 0, we reach 0 again while it's still GRAY.
```

**Example 2:**

```
  0 --> 1 --> 2
        |
        v
        3

Input:  n = 4, edges = [[0,1],[1,2],[1,3]]
Output: false
Explanation: DAG; no back edges.
```

**Example 3:**

```
  0 --> 1
  |     |
  v     v
  2 --> 3

Input:  n = 4, edges = [[0,1],[0,2],[1,3],[2,3]]
Output: false
Explanation: Node 3 is reachable from two paths, but both complete before revisiting. No cycle.
```

### Approach: DFS with White-Gray-Black Coloring

**Why this approach?**

In a directed graph, a simple visited boolean is insufficient because a node can be revisited via a different path without a cycle (cross edge vs. back edge). The 3-color scheme distinguishes:
- **WHITE (0):** Unvisited.
- **GRAY (1):** Currently in the recursion stack (being explored).
- **BLACK (2):** Fully processed (all descendants explored).

A cycle exists if and only if we encounter a **GRAY** node during DFS -- this is a **back edge** in the DFS tree. Cross edges lead to BLACK nodes and are safe.

### Optimized Java Solution

```java
import java.util.*;

public class CycleDirected {

    private static final int WHITE = 0, GRAY = 1, BLACK = 2;

    public boolean hasCycle(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);

        int[] color = new int[n];  // all WHITE initially

        for (int i = 0; i < n; i++) {
            if (color[i] == WHITE) {
                if (dfsHasCycle(i, adj, color)) return true;
            }
        }
        return false;
    }

    private boolean dfsHasCycle(int node, List<List<Integer>> adj, int[] color) {
        color[node] = GRAY;  // enter recursion stack

        for (int neighbor : adj.get(node)) {
            if (color[neighbor] == GRAY) return true;   // back edge -> cycle!
            if (color[neighbor] == WHITE) {
                if (dfsHasCycle(neighbor, adj, color)) return true;
            }
            // BLACK neighbors are already fully explored -- skip
        }

        color[node] = BLACK;  // all descendants processed
        return false;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Each vertex changes color WHITE->GRAY->BLACK exactly once. Each edge is examined once. |
| **Space** | **O(V)** | Color array: O(V). Recursion stack depth: O(V) worst case (long chain). |

---
---

## Q132. Number of Islands

**Difficulty:** `Medium` | **Data Structure:** Graph (Grid) | **Pattern:** Grid DFS/BFS

### Problem

Given an `m x n` 2D grid of `'1'`s (land) and `'0'`s (water), count the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are surrounded by water.

*(LeetCode 200)*

### Examples

**Example 1:**

```
Grid:
  1 1 0 0 0
  1 1 0 0 0
  0 0 1 0 0
  0 0 0 1 1

Input:  grid = [['1','1','0','0','0'],
                ['1','1','0','0','0'],
                ['0','0','1','0','0'],
                ['0','0','0','1','1']]
Output: 3
Explanation: Three islands (top-left 2x2 block, center '1', bottom-right pair).
```

**Example 2:**

```
Grid:
  1 1 1
  0 1 0
  1 1 1

Input:  grid = [['1','1','1'],['0','1','0'],['1','1','1']]
Output: 1
Explanation: All '1's are connected, forming one island.
```

**Example 3:**

```
Grid:
  1 0 1
  0 1 0
  1 0 1

Input:  grid = [['1','0','1'],['0','1','0'],['1','0','1']]
Output: 5
Explanation: No horizontal/vertical connections between any two '1's (diagonal doesn't count).
```

### Approach: DFS Flood-Fill (In-place Marking)

**Why this approach?**

Each unvisited land cell is the start of a new island. We DFS/flood-fill from it, marking all connected land cells as visited (by changing '1' to '0'). This avoids a separate visited array by modifying the grid in-place.

DFS is simpler than BFS here (no queue needed), and iterative DFS avoids stack overflow on large grids. We use the 4-directional offset array `{0,1,0,-1,0}` trick to keep code clean.

```
Grid state changes (Example 1):

Island 1 found at (0,0) -- flood fill marks all '1's in top-left:
  0 0 0 0 0        Island 2 found at (2,2):    Island 3 at (3,3):
  0 0 0 0 0        0 0 0 0 0                   0 0 0 0 0
  0 0 1 0 0   ->   0 0 0 0 0              ->   0 0 0 0 0
  0 0 0 1 1        0 0 0 1 1                   0 0 0 0 0
                                            count = 3
```

### Optimized Java Solution

```java
public class NumberOfIslands {

    private static final int[] DIR = {0, 1, 0, -1, 0}; // right, down, left, up

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;

        int rows = grid.length, cols = grid[0].length;
        int count = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c, rows, cols);
                }
            }
        }
        return count;
    }

    /** Flood-fill: sink the island by marking '1' -> '0'. */
    private void dfs(char[][] grid, int r, int c, int rows, int cols) {
        grid[r][c] = '0';  // mark visited (sink)

        for (int d = 0; d < 4; d++) {
            int nr = r + DIR[d], nc = c + DIR[d + 1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == '1') {
                dfs(grid, nr, nc, rows, cols);
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(M * N)** | Each cell is visited at most once. |
| **Space** | **O(M * N)** | Recursion stack in worst case (e.g., entire grid is '1', DFS depth = M*N). Iterative DFS brings this down but same worst case for stack storage. |

---
---

## Q133. Clone a Graph

**Difficulty:** `Medium` | **Data Structure:** Graph | **Pattern:** BFS + HashMap

### Problem

Given a reference of a node in a connected undirected graph, return a **deep copy** (clone) of the graph. Each node in the graph contains a value (`int`) and a list (`List<Node>`) of its neighbors.

*(LeetCode 133)*

### Examples

**Example 1:**

```
Graph:
  1 -- 2
  |    |
  4 -- 3

Input:  node = reference to node 1
Output: Clone of entire graph (new node objects with same structure)
```

**Example 2:**

```
Input:  node = single node with val=1, no neighbors
Output: Clone of single node with val=1, no neighbors
```

**Example 3:**

```
Input:  node = null
Output: null
```

### Approach: BFS with Original-to-Clone HashMap

**Why this approach?**

We need to create new Node objects while preserving the neighbor relationships. A `HashMap<Node, Node>` maps each original node to its clone. BFS ensures we visit every node exactly once. When we process a node's neighbors, if the neighbor hasn't been cloned yet, we create its clone and enqueue it. Then we wire the clone's neighbor list.

DFS works too, but BFS is easier to reason about iteratively and avoids deep recursion.

### Optimized Java Solution

```java
import java.util.*;

public class CloneGraph {

    // Definition for a graph node
    static class Node {
        public int val;
        public List<Node> neighbors;

        public Node(int val) {
            this.val = val;
            this.neighbors = new ArrayList<>();
        }
    }

    public Node cloneGraph(Node node) {
        if (node == null) return null;

        Map<Node, Node> cloneMap = new HashMap<>();
        Queue<Node> queue = new ArrayDeque<>();

        // Clone the source node
        cloneMap.put(node, new Node(node.val));
        queue.offer(node);

        while (!queue.isEmpty()) {
            Node original = queue.poll();
            Node clone = cloneMap.get(original);

            for (Node neighbor : original.neighbors) {
                if (!cloneMap.containsKey(neighbor)) {
                    cloneMap.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }
                clone.neighbors.add(cloneMap.get(neighbor));
            }
        }

        return cloneMap.get(node);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Every node is enqueued and processed once. Every edge is traversed once for neighbor wiring. |
| **Space** | **O(V)** | HashMap stores V entries. Queue holds at most O(V) nodes. The cloned graph itself is O(V + E) but that's the output, not auxiliary space. |

---
---

## Q134. Is Graph Bipartite?

**Difficulty:** `Medium` | **Data Structure:** Graph | **Pattern:** 2-coloring BFS

### Problem

Given an undirected graph represented as an adjacency list, determine if the graph is bipartite. A graph is bipartite if we can split its set of nodes into two independent subsets A and B such that every edge connects a node in A to a node in B.

*(LeetCode 785)*

### Examples

**Example 1:**

```
Graph:
  0 -- 1
  |    |
  3 -- 2

  graph = [[1,3],[0,2],[1,3],[0,2]]

Input:  graph = [[1,3],[0,2],[1,3],[0,2]]
Output: true
Explanation: A = {0, 2}, B = {1, 3}. Every edge crosses between A and B.
```

**Example 2:**

```
Graph:
  0 -- 1
  |  / |
  3    2

  graph = [[1,3],[0,2,3],[1],[0,1]]

Input:  graph = [[1,3],[0,2,3],[1],[0,1]]
Output: false
Explanation: Node 1 connects to both 0 and 3, but 0 and 3 also connect. Triangle {0,1,3} is odd-length cycle.
```

**Example 3:**

```
  0    1

  graph = [[],[]]

Input:  graph = [[],[]]
Output: true
Explanation: Disconnected nodes -- trivially bipartite.
```

### Approach: BFS 2-Coloring

**Why this approach?**

A graph is bipartite if and only if it contains no odd-length cycles. We can verify this by attempting to 2-color the graph: assign color 0 to a starting node, then color all neighbors with color 1, their neighbors with 0, etc. If we ever find a neighbor that already has the **same** color as the current node, the graph is not bipartite.

We must handle disconnected components by iterating through all uncolored nodes.

### Optimized Java Solution

```java
import java.util.*;

public class BipartiteCheck {

    public boolean isBipartite(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n];
        Arrays.fill(color, -1);  // -1 = uncolored

        for (int i = 0; i < n; i++) {
            if (color[i] == -1) {
                if (!bfsColor(i, graph, color)) return false;
            }
        }
        return true;
    }

    private boolean bfsColor(int start, int[][] graph, int[] color) {
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(start);
        color[start] = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : graph[node]) {
                if (color[neighbor] == -1) {
                    color[neighbor] = 1 - color[node];  // alternate: 0 <-> 1
                    queue.offer(neighbor);
                } else if (color[neighbor] == color[node]) {
                    return false;  // same color on adjacent nodes -> not bipartite
                }
            }
        }
        return true;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Standard BFS touching each node and edge once. |
| **Space** | **O(V)** | Color array and queue. |

---
---

## Q135. Topological Sort (Kahn's Algorithm BFS)

**Difficulty:** `Medium` | **Data Structure:** Graph (DAG) | **Pattern:** BFS with In-degree

### Problem

Given a Directed Acyclic Graph (DAG) with `n` nodes, return a valid topological ordering. In a topological ordering, for every directed edge `u -> v`, node `u` appears before node `v`.

### Examples

**Example 1:**

```
  5 --> 0 <-- 4
  |           |
  v           v
  2 --> 3 --> 1

Input:  n = 6, edges = [[5,0],[5,2],[4,0],[4,1],[2,3],[3,1]]
Output: [4, 5, 2, 0, 3, 1] (one valid order)
Explanation: All prerequisites appear before dependents.
```

**Example 2:**

```
  0 --> 1 --> 2

Input:  n = 3, edges = [[0,1],[1,2]]
Output: [0, 1, 2]
```

**Example 3:**

```
  0    1

Input:  n = 2, edges = []
Output: [0, 1] or [1, 0]
Explanation: No dependencies, any order is valid.
```

### Approach: Kahn's Algorithm (BFS with In-degree)

**Why this approach?**

Kahn's algorithm is intuitive: repeatedly remove nodes with in-degree 0 (no prerequisites). It's BFS-based, iterative, and naturally detects cycles (if the output size < n, a cycle exists). This makes it safer and easier to debug than the DFS-based reverse-postorder approach.

**Algorithm:**
1. Compute in-degree for every node.
2. Enqueue all nodes with in-degree 0.
3. While queue is not empty: dequeue node, add to result, reduce in-degree of all neighbors. If a neighbor's in-degree drops to 0, enqueue it.
4. If result size < n, graph has a cycle.

### Optimized Java Solution

```java
import java.util.*;

public class TopologicalSort {

    public List<Integer> topoSort(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[n];
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());

        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            inDegree[e[1]]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            order.add(node);

            for (int neighbor : adj.get(node)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        if (order.size() != n) {
            throw new IllegalArgumentException("Graph contains a cycle -- no topological order exists");
        }
        return order;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Each node enqueued/dequeued once. Each edge processed once when decrementing in-degrees. |
| **Space** | **O(V + E)** | Adjacency list: O(V + E). In-degree array, queue, and result: O(V). |

---
---

## Q136. Course Schedule

**Difficulty:** `Medium` | **Data Structure:** Graph (DAG) | **Pattern:** Topological Sort / Cycle Detection

### Problem

There are `numCourses` courses labeled `0` to `numCourses - 1`. You are given an array `prerequisites` where `prerequisites[i] = [a, b]` means you must take course `b` before course `a`. Return `true` if you can finish all courses, or `false` if there is a circular dependency.

*(LeetCode 207)*

### Examples

**Example 1:**

```
  0 <-- 1  (must take 0 before 1)

Input:  numCourses = 2, prerequisites = [[1,0]]
Output: true
Explanation: Take course 0 first, then course 1.
```

**Example 2:**

```
  0 --> 1
  ^     |
  +-----+

Input:  numCourses = 2, prerequisites = [[1,0],[0,1]]
Output: false
Explanation: Circular dependency: 0 requires 1, 1 requires 0.
```

**Example 3:**

```
Input:  numCourses = 3, prerequisites = [[1,0],[2,1]]
Output: true
Explanation: Take 0, then 1, then 2.
```

### Approach: Kahn's Algorithm -- Check if Topo Sort Includes All Nodes

**Why this approach?**

This is exactly "detect cycle in a directed graph" rephrased. If a valid topological ordering exists that includes all courses, we can finish them all. If not (cycle exists), we can't. Kahn's algorithm is ideal: if the BFS processes fewer than `numCourses` nodes, a cycle exists.

### Optimized Java Solution

```java
import java.util.*;

public class CourseSchedule {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);   // p[1] -> p[0]
            inDegree[p[0]]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;

            for (int next : adj.get(course)) {
                if (--inDegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        return processed == numCourses;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | V = numCourses, E = number of prerequisites. Standard Kahn's BFS. |
| **Space** | **O(V + E)** | Adjacency list and in-degree array. |

---
---

## Q137. Course Schedule II

**Difficulty:** `Medium` | **Data Structure:** Graph (DAG) | **Pattern:** Kahn's BFS Ordering

### Problem

Given `numCourses` and prerequisites, return a valid order in which to take all courses. If there are multiple valid orders, return any. If it is impossible (cycle), return an empty array.

*(LeetCode 210)*

### Examples

**Example 1:**

```
Input:  numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]

  0 --> 1 --> 3
  |           ^
  +--> 2 -----+

Output: [0, 1, 2, 3] or [0, 2, 1, 3]
Explanation: Course 0 has no prerequisites. 1 and 2 both need 0. 3 needs both 1 and 2.
```

**Example 2:**

```
Input:  numCourses = 2, prerequisites = [[1,0]]
Output: [0, 1]
```

**Example 3:**

```
Input:  numCourses = 2, prerequisites = [[0,1],[1,0]]
Output: []
Explanation: Cycle detected -- impossible.
```

### Approach: Kahn's Algorithm Returning the Order

**Why this approach?**

Same as Course Schedule I, but instead of just counting, we collect the order. Kahn's algorithm naturally produces a topological ordering as a side effect of the BFS.

### Optimized Java Solution

```java
import java.util.*;

public class CourseScheduleII {

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            inDegree[p[0]]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int[] order = new int[numCourses];
        int idx = 0;

        while (!queue.isEmpty()) {
            int course = queue.poll();
            order[idx++] = course;

            for (int next : adj.get(course)) {
                if (--inDegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        return idx == numCourses ? order : new int[0];
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Standard Kahn's. |
| **Space** | **O(V + E)** | Adjacency list, in-degree array, output array. |

---
---

## Q138. Word Ladder

**Difficulty:** `Medium` | **Data Structure:** Graph | **Pattern:** BFS Shortest Transformation

### Problem

Given two words `beginWord` and `endWord`, and a dictionary `wordList`, find the length of the **shortest transformation sequence** from `beginWord` to `endWord`, such that:
- Only one letter can be changed at a time.
- Each transformed word must exist in the word list.

Return 0 if no such transformation sequence exists.

*(LeetCode 127)*

### Examples

**Example 1:**

```
Input:  beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
Output: 5
Explanation: hit -> hot -> dot -> dog -> cog  (5 words in the sequence)
```

**Example 2:**

```
Input:  beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log"]
Output: 0
Explanation: endWord "cog" is not in wordList.
```

**Example 3:**

```
Input:  beginWord = "a", endWord = "c", wordList = ["a","b","c"]
Output: 2
Explanation: a -> c
```

### Approach: BFS on Implicit Graph with Wildcard Pattern Bucketing

**Why this approach?**

Each word is a node; edges connect words that differ by exactly one character. BFS finds the shortest path in this unweighted graph. The key optimization is **wildcard bucketing**: for each word, generate patterns like `h*t`, `*ot`, `ho*` and group words by pattern. This avoids O(N^2 * L) pairwise comparisons and reduces edge generation to O(N * L).

### Optimized Java Solution

```java
import java.util.*;

public class WordLadder {

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        // BFS
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(beginWord);
        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        int level = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                char[] word = queue.poll().toCharArray();

                for (int j = 0; j < word.length; j++) {
                    char original = word[j];

                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        word[j] = c;
                        String next = new String(word);

                        if (next.equals(endWord)) return level + 1;

                        if (wordSet.contains(next) && !visited.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                    word[j] = original;  // restore
                }
            }
            level++;
        }
        return 0;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(N * L * 26)** | N = number of words, L = word length. For each word in the queue, we try 26 letters at each of L positions. Simplifies to O(N * L). |
| **Space** | **O(N * L)** | Visited set and queue store up to N words of length L. |

---
---

## Q139. Pacific Atlantic Water Flow

**Difficulty:** `Medium` | **Data Structure:** Graph (Grid) | **Pattern:** Multi-source DFS from Borders

### Problem

Given an `m x n` grid of non-negative integers representing heights, water can flow from a cell to its 4-directional neighbors if the neighbor's height is **less than or equal to** the current cell's height. The Pacific ocean touches the left and top edges. The Atlantic ocean touches the right and bottom edges. Return a list of cells from which water can flow to **both** oceans.

*(LeetCode 417)*

### Examples

**Example 1:**

```
Heights:
  P  P  P  P  P
P [1, 2, 2, 3, 5] 
P [3, 2, 3, 4, 4] 
P [2, 4, 5, 3, 1] A
P [6, 7, 1, 4, 5] A
P [5, 1, 1, 2, 4] A
     A  A  A  A  A

Input:  heights = [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]]
Output: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]
```

**Example 2:**

```
Input:  heights = [[1]]
Output: [[0,0]]
Explanation: Single cell touches both oceans.
```

### Approach: Reverse DFS from Ocean Borders

**Why this approach?**

Instead of DFS from every cell (which would be O((MN)^2)), we reverse the problem: start DFS from each ocean's border cells and flow **uphill** (to cells with height >= current). A cell from which water can reach the Pacific is marked in `pacific[][]`, and similarly for `atlantic[][]`. The answer is the intersection.

```
Grid state after DFS from Pacific border (P = reachable):
  P  P  P  P  P
  P  P  P  P  P
  .  P  P  .  .
  P  P  .  .  .
  P  .  .  .  .

Grid state after DFS from Atlantic border (A = reachable):
  .  .  .  .  A
  .  .  .  A  A
  .  .  A  .  A
  A  A  .  A  A
  A  A  A  A  A

Intersection (both P and A):
  .  .  .  .  *    -> (0,4)
  .  .  .  *  *    -> (1,3),(1,4)
  .  .  *  .  .    -> (2,2)
  *  *  .  .  .    -> (3,0),(3,1)
  *  .  .  .  .    -> (4,0)
```

### Optimized Java Solution

```java
import java.util.*;

public class PacificAtlantic {

    private static final int[] DIR = {0, 1, 0, -1, 0};

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];

        // DFS from Pacific borders (top row + left column)
        for (int c = 0; c < n; c++) dfs(heights, 0, c, pacific, m, n);
        for (int r = 0; r < m; r++) dfs(heights, r, 0, pacific, m, n);

        // DFS from Atlantic borders (bottom row + right column)
        for (int c = 0; c < n; c++) dfs(heights, m - 1, c, atlantic, m, n);
        for (int r = 0; r < m; r++) dfs(heights, r, n - 1, atlantic, m, n);

        // Intersection
        List<List<Integer>> result = new ArrayList<>();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (pacific[r][c] && atlantic[r][c]) {
                    result.add(List.of(r, c));
                }
            }
        }
        return result;
    }

    private void dfs(int[][] heights, int r, int c, boolean[][] reachable, int m, int n) {
        reachable[r][c] = true;
        for (int d = 0; d < 4; d++) {
            int nr = r + DIR[d], nc = c + DIR[d + 1];
            if (nr >= 0 && nr < m && nc >= 0 && nc < n
                    && !reachable[nr][nc]
                    && heights[nr][nc] >= heights[r][c]) {  // flow uphill
                dfs(heights, nr, nc, reachable, m, n);
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(M * N)** | Each cell is visited at most twice (once per ocean DFS). |
| **Space** | **O(M * N)** | Two boolean grids + recursion stack up to O(M*N). |

---
---

## Q140. Surrounded Regions

**Difficulty:** `Medium` | **Data Structure:** Graph (Grid) | **Pattern:** Border-connected DFS

### Problem

Given an `m x n` board containing `'X'` and `'O'`, capture all regions that are completely surrounded by `'X'`. A region is captured by flipping all `'O'`s into `'X'`s. An `'O'` on the border (or connected to a border `'O'`) cannot be captured.

*(LeetCode 130)*

### Examples

**Example 1:**

```
Input:              Output:
  X X X X             X X X X
  X O O X     ->      X X X X
  X X O X             X X X X
  X O X X             X O X X

Explanation: The 'O' at (3,1) is connected to the left border -- not captured.
The interior 'O's at (1,1),(1,2),(2,2) are fully surrounded -- captured.
```

**Example 2:**

```
Input:              Output:
  X X X               X X X
  X O X       ->      X X X
  X X X               X X X
```

**Example 3:**

```
Input:              Output:
  O X O               O X O
  X O X       ->      X X X
  O X O               O X O

Explanation: Corner 'O's touch borders. Center 'O' is surrounded.
```

### Approach: DFS from Border 'O' Cells (Mark Safe, Capture Rest)

**Why this approach?**

Instead of checking each 'O' to see if it can reach a border (expensive), we flip the logic: start from border 'O' cells, DFS to mark all connected 'O's as safe (temporarily marked as 'S'). Then sweep the board: remaining 'O's are captured (change to 'X'), and 'S's are restored to 'O'.

```
Step 1 - Mark border-connected 'O's as 'S':
  X X X X        X X X X
  X O O X   ->   X O O X    (no border 'O's connect to interior here except...)
  X X O X        X X O X
  X O X X        X S X X    (border 'O' at (3,1) -> mark 'S')

Step 2 - Capture remaining 'O's, restore 'S' -> 'O':
  X X X X
  X X X X
  X X X X
  X O X X
```

### Optimized Java Solution

```java
public class SurroundedRegions {

    private static final int[] DIR = {0, 1, 0, -1, 0};

    public void solve(char[][] board) {
        int m = board.length, n = board[0].length;

        // Step 1: DFS from border 'O' cells, mark as 'S' (safe)
        for (int r = 0; r < m; r++) {
            if (board[r][0] == 'O') markSafe(board, r, 0, m, n);
            if (board[r][n - 1] == 'O') markSafe(board, r, n - 1, m, n);
        }
        for (int c = 0; c < n; c++) {
            if (board[0][c] == 'O') markSafe(board, 0, c, m, n);
            if (board[m - 1][c] == 'O') markSafe(board, m - 1, c, m, n);
        }

        // Step 2: Capture surrounded 'O's and restore safe cells
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] == 'O') board[r][c] = 'X';       // captured
                else if (board[r][c] == 'S') board[r][c] = 'O';  // restored
            }
        }
    }

    private void markSafe(char[][] board, int r, int c, int m, int n) {
        board[r][c] = 'S';
        for (int d = 0; d < 4; d++) {
            int nr = r + DIR[d], nc = c + DIR[d + 1];
            if (nr >= 0 && nr < m && nc >= 0 && nc < n && board[nr][nc] == 'O') {
                markSafe(board, nr, nc, m, n);
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(M * N)** | Each cell visited at most once during DFS + one pass for the final sweep. |
| **Space** | **O(M * N)** | Recursion stack in worst case. Can be reduced to O(min(M,N)) with iterative BFS. |

---
---

## Q141. Graph Valid Tree

**Difficulty:** `Medium` | **Data Structure:** Graph | **Pattern:** n-1 edges + connected check

### Problem

Given `n` nodes labeled from `0` to `n-1` and a list of undirected edges, determine if these edges form a valid tree. A valid tree has exactly `n-1` edges and is connected (no cycles).

*(LeetCode 261)*

### Examples

**Example 1:**

```
  0 -- 1
  |
  2 -- 3
       |
       4

Input:  n = 5, edges = [[0,1],[0,2],[2,3],[3,4]]
Output: true
Explanation: 4 edges for 5 nodes, and the graph is connected. Valid tree.
```

**Example 2:**

```
  0 -- 1    3
       |    |
       2    4

Input:  n = 5, edges = [[0,1],[1,2],[3,4]]
Output: false
Explanation: 3 edges for 5 nodes (need 4). Graph is disconnected.
```

**Example 3:**

```
  0 -- 1
  |    |
  3 -- 2

Input:  n = 4, edges = [[0,1],[1,2],[2,3],[3,0]]
Output: false
Explanation: 4 edges for 4 nodes (too many). Contains a cycle.
```

### Approach: Edge Count + Union-Find Connectivity

**Why this approach?**

A graph is a valid tree if and only if:
1. It has exactly `n - 1` edges, **AND**
2. It is connected (equivalently, no cycle -- given condition 1, being connected and having n-1 edges are equivalent).

We first check the edge count (O(1)). Then we use Union-Find to verify that all nodes end up in one component. If any edge connects two nodes already in the same component, there's a cycle. This combined check is elegant and efficient.

### Optimized Java Solution

```java
public class GraphValidTree {

    private int[] parent, rank;

    public boolean validTree(int n, int[][] edges) {
        // A tree with n nodes has exactly n-1 edges
        if (edges.length != n - 1) return false;

        // Union-Find to check connectivity (and implicitly, no cycle)
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int components = n;
        for (int[] edge : edges) {
            int rootA = find(edge[0]);
            int rootB = find(edge[1]);

            if (rootA == rootB) return false;  // cycle (redundant with edge count check, but safe)

            union(rootA, rootB);
            components--;
        }

        return components == 1;
    }

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    private void union(int a, int b) {
        if (rank[a] < rank[b]) { parent[a] = b; }
        else if (rank[a] > rank[b]) { parent[b] = a; }
        else { parent[b] = a; rank[a]++; }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(E * alpha(V))** | E = n-1 union-find operations, each nearly O(1). |
| **Space** | **O(V)** | Parent and rank arrays. |

---
---

## Q142. Minimum Height Trees

**Difficulty:** `Medium` | **Data Structure:** Graph (Tree) | **Pattern:** Leaf-Pruning to Find Centroids

### Problem

A tree is an undirected graph in which any two vertices are connected by exactly one path. Given a tree of `n` nodes labeled from `0` to `n-1`, and `n-1` edges, find all roots that minimize the tree height. These are called **centroids**. Return a list of their labels.

*(LeetCode 310)*

### Examples

**Example 1:**

```
       0
       |
       1
      / \
     2   3

If rooted at 0: height = 2. If rooted at 1: height = 1.

Input:  n = 4, edges = [[1,0],[1,2],[1,3]]
Output: [1]
Explanation: Rooting at node 1 gives height 1 (minimum).
```

**Example 2:**

```
  0 -- 1 -- 2 -- 3

Rooted at 1: height = 2. Rooted at 2: height = 2. Both are minimum.

Input:  n = 4, edges = [[0,1],[1,2],[2,3]]
Output: [1, 2]
```

**Example 3:**

```
Input:  n = 1, edges = []
Output: [0]
```

### Approach: Iterative Leaf Pruning (Topological Peeling)

**Why this approach?**

The centroids of a tree are found by repeatedly removing leaf nodes (degree 1). Like peeling an onion, we strip layers of leaves until 1 or 2 nodes remain -- these are the centroids. This works because leaves are always the farthest from the center.

The result always has at most 2 centroids (a tree has either 1 center or 2 adjacent centers).

This is analogous to topological sort: leaves have degree 1, and we iteratively remove them.

### Optimized Java Solution

```java
import java.util.*;

public class MinHeightTrees {

    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if (n == 1) return List.of(0);
        if (n == 2) return List.of(0, 1);

        // Build adjacency list and track degree
        Set<Integer>[] adj = new HashSet[n];
        for (int i = 0; i < n; i++) adj[i] = new HashSet<>();
        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }

        // Collect initial leaves (degree == 1)
        Queue<Integer> leaves = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (adj[i].size() == 1) leaves.offer(i);
        }

        int remaining = n;
        while (remaining > 2) {
            int leafCount = leaves.size();
            remaining -= leafCount;

            Queue<Integer> newLeaves = new ArrayDeque<>();
            for (int i = 0; i < leafCount; i++) {
                int leaf = leaves.poll();
                // leaf has exactly one neighbor remaining
                int neighbor = adj[leaf].iterator().next();
                adj[neighbor].remove(leaf);

                if (adj[neighbor].size() == 1) {
                    newLeaves.offer(neighbor);
                }
            }
            leaves = newLeaves;
        }

        return new ArrayList<>(leaves);
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V)** | Each node is removed exactly once. Total edge removals = V - 1 (it's a tree). |
| **Space** | **O(V)** | Adjacency sets and leaf queue. |

---
---

## Q143. Minimum Spanning Tree (Kruskal's with Union-Find)

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Sort Edges + Union-Find

### Problem

Given a connected, undirected, weighted graph with `n` nodes and a list of edges `[u, v, weight]`, find the Minimum Spanning Tree (MST) -- a subset of edges that connects all nodes with the minimum total weight and no cycles. Return the total weight of the MST.

### Examples

**Example 1:**

```
Graph:
  0 --1-- 1
  |       /|
  4     3  2
  |   /    |
  3 --5-- 2

Edges: [0,1,1], [0,3,4], [1,2,2], [1,3,3], [2,3,5]

Sorted by weight: (0,1,1), (1,2,2), (1,3,3), (0,3,4), (2,3,5)

Step-by-step:
  Edge (0,1,1): 0 and 1 in different sets -> ACCEPT. Total = 1.
  Edge (1,2,2): 1 and 2 in different sets -> ACCEPT. Total = 3.
  Edge (1,3,3): 1 and 3 in different sets -> ACCEPT. Total = 6. (3 edges for 4 nodes -> MST complete)
  Edge (0,3,4): 0 and 3 already connected -> SKIP.
  Edge (2,3,5): 2 and 3 already connected -> SKIP.

Input:  n = 4, edges = [[0,1,1],[0,3,4],[1,2,2],[1,3,3],[2,3,5]]
Output: 6
MST edges: (0,1), (1,2), (1,3)
```

**Example 2:**

```
  0 --10-- 1 --1-- 2

Input:  n = 3, edges = [[0,1,10],[1,2,1]]
Output: 11
Explanation: Must include both edges (only n-1 = 2 edges total).
```

**Example 3:**

```
  0 --1-- 1
  |  \    |
  3   2   4
  |    \  |
  3 --5-- 2

Input:  n = 4, edges = [[0,1,1],[0,2,2],[0,3,3],[1,2,4],[2,3,5]]
Output: 6
Explanation: MST: (0,1,1) + (0,2,2) + (0,3,3) = 6.
```

### Approach: Kruskal's Algorithm (Greedy Sort + Union-Find)

**Why this approach?**

Kruskal's algorithm is greedy: sort all edges by weight, then pick edges in order, skipping any that would create a cycle. Union-Find efficiently checks for cycles (if both endpoints share the same root, adding the edge creates a cycle). Stop once we've accepted `n - 1` edges.

**Why Kruskal over Prim?** Kruskal is simpler to implement, works well with edge lists, and is optimal for sparse graphs. Prim (with priority queue) is better for dense graphs. For competitive programming, Kruskal + Union-Find is the go-to.

### Optimized Java Solution

```java
import java.util.*;

public class KruskalMST {

    private int[] parent, rank;

    public int minimumSpanningTree(int n, int[][] edges) {
        // Sort edges by weight
        Arrays.sort(edges, (a, b) -> Integer.compare(a[2], b[2]));

        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        int totalWeight = 0;
        int edgesUsed = 0;

        for (int[] edge : edges) {
            if (edgesUsed == n - 1) break;  // MST complete

            int rootU = find(edge[0]);
            int rootV = find(edge[1]);

            if (rootU != rootV) {
                union(rootU, rootV);
                totalWeight += edge[2];
                edgesUsed++;
            }
        }

        // Optional: check if MST spans all nodes
        if (edgesUsed != n - 1) {
            throw new IllegalArgumentException("Graph is not connected");
        }

        return totalWeight;
    }

    private int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    private void union(int a, int b) {
        if (rank[a] < rank[b]) { parent[a] = b; }
        else if (rank[a] > rank[b]) { parent[b] = a; }
        else { parent[b] = a; rank[a]++; }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(E log E)** | Sorting edges dominates. Union-Find operations are O(E * alpha(V)), which is much less than the sort. |
| **Space** | **O(V)** | Parent and rank arrays. Sorting may use O(log E) stack space. |

---
---

## Q144. Dijkstra's Shortest Path

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Priority Queue Greedy

### Problem

Given a weighted directed graph with non-negative edge weights, find the shortest path from a source node to all other nodes. Return an array `dist[]` where `dist[i]` is the shortest distance from `source` to node `i`. If a node is unreachable, its distance is `Integer.MAX_VALUE`.

### Examples

**Example 1:**

```
Graph:
  0 --(4)--> 1 --(1)--> 3
  |                      ^
  (1)                   (2)
  |                      |
  v                      |
  2 ---------(5)-------> 3
  
  Edges: [0,1,4], [0,2,1], [1,3,1], [2,3,5]

Step-by-step distance table (source = 0):

  Step | Node Processed | dist[0] | dist[1] | dist[2] | dist[3]
  -----|----------------|---------|---------|---------|--------
  Init |      -         |    0    |   INF   |   INF   |  INF
    1  |      0         |    0    |    4    |    1    |  INF
    2  |      2         |    0    |    4    |    1    |   6
    3  |      1         |    0    |    4    |    1    |   5
    4  |      3         |    0    |    4    |    1    |   5

Input:  n = 4, edges = [[0,1,4],[0,2,1],[1,3,1],[2,3,5]], source = 0
Output: [0, 4, 1, 5]
```

**Example 2:**

```
  0 --(1)--> 1
              |
             (2)
              v
  3          2

Input:  n = 4, edges = [[0,1,1],[1,2,2]], source = 0
Output: [0, 1, 3, 2147483647]
Explanation: Node 3 is unreachable.
```

**Example 3:**

```
  0 --(7)--> 1
  |           ^
  (2)       (3)
  v           |
  2 ----------+

Input:  n = 3, edges = [[0,1,7],[0,2,2],[2,1,3]], source = 0
Output: [0, 5, 2]
Explanation: Shortest path to 1 is 0->2->1 with cost 2+3=5, not direct edge 0->1 with cost 7.
```

### Approach: Dijkstra's Algorithm with Min-Heap (Priority Queue)

**Why this approach?**

Dijkstra's is the optimal single-source shortest path algorithm for graphs with **non-negative weights**. It greedily processes the closest unvisited node, relaxing its neighbors. Using a min-heap ensures we always pick the minimum distance node in O(log V) time.

**Key details:**
- We use a **lazy deletion** approach: instead of decreasing keys in the priority queue (complex with Java's PriorityQueue), we simply add new entries and skip stale ones when dequeued.
- An entry is stale if `polled distance > dist[node]`.

**Why not Bellman-Ford?** Bellman-Ford handles negative weights but is O(VE) -- much slower than Dijkstra's O((V+E) log V) for non-negative graphs.

### Optimized Java Solution

```java
import java.util.*;

public class DijkstraShortestPath {

    public int[] dijkstra(int n, int[][] edges, int source) {
        // Build adjacency list: node -> [(neighbor, weight), ...]
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(new int[]{e[1], e[2]});
        }

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // Min-heap: (distance, node)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], u = curr[1];

            // Skip stale entries (lazy deletion)
            if (d > dist[u]) continue;

            for (int[] edge : adj.get(u)) {
                int v = edge[0], w = edge[1];
                int newDist = dist[u] + w;

                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }

        return dist;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O((V + E) log V)** | Each vertex is extracted from the PQ once: O(V log V). Each edge may trigger an insertion: O(E log V). Total: O((V + E) log V). |
| **Space** | **O(V + E)** | Adjacency list: O(V + E). Priority queue: O(V) in ideal case, O(E) with lazy deletion. dist array: O(V). |

---
---

## Q145. Bellman-Ford Shortest Path

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Edge Relaxation (handles negative weights)

### Problem

Given a weighted directed graph that may contain **negative weight** edges (but no negative weight cycles reachable from the source), find the shortest path from a source node to all other nodes. Also detect if a negative weight cycle is reachable from the source.

### Examples

**Example 1:**

```
Graph:
  0 --(6)--> 1 --(-2)--> 3
  |                       ^
  (7)                    (-1)
  v                       |
  2 ---------(8)-------> 3
  \
   (5)
    v
    3

  Edges: [0,1,6], [0,2,7], [1,3,-2], [2,3,8], [2,3,5]
  
  Note: two edges from 2 to 3 (weights 8 and 5). Only keep the better one effectively.

Simplify: Edges: [0,1,6], [0,2,7], [1,3,-2]

Step-by-step (source = 0, V = 4, V-1 = 3 iterations):

  Iteration | dist[0] | dist[1] | dist[2] | dist[3]
  ----------|---------|---------|---------|--------
  Init      |    0    |   INF   |   INF   |  INF
  1         |    0    |    6    |    7    |  INF     (relax 0->1, 0->2)
  2         |    0    |    6    |    7    |   4      (relax 1->3: 6+(-2)=4)
  3         |    0    |    6    |    7    |   4      (no change)

Input:  n = 4, edges = [[0,1,6],[0,2,7],[1,3,-2]], source = 0
Output: [0, 6, 7, 4]
```

**Example 2 (Negative cycle detection):**

```
  0 --(1)--> 1 --(-3)--> 2 --(1)--> 0

Input:  n = 3, edges = [[0,1,1],[1,2,-3],[2,0,1]], source = 0
Output: NEGATIVE CYCLE DETECTED
Explanation: Cycle 0->1->2->0 has total weight 1+(-3)+1 = -1. Can reduce distance infinitely.
```

### Approach: Bellman-Ford (V-1 Relaxation Passes + Negative Cycle Check)

**Why this approach?**

Bellman-Ford can handle negative edge weights, unlike Dijkstra. It works by relaxing all edges V-1 times. After V-1 iterations, if any edge can still be relaxed, a negative weight cycle exists.

**Why V-1 iterations?** The shortest path between any two nodes in a graph with V nodes has at most V-1 edges. Each iteration guarantees at least one more edge of the shortest path is correctly computed.

### Optimized Java Solution

```java
import java.util.*;

public class BellmanFord {

    /**
     * @return dist array, or null if a negative cycle is reachable from source
     */
    public int[] bellmanFord(int n, int[][] edges, int source) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // Relax all edges V-1 times
        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;
            for (int[] edge : edges) {
                int u = edge[0], v = edge[1], w = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    updated = true;
                }
            }
            if (!updated) break;  // early termination: no changes means we're done
        }

        // Check for negative weight cycles (V-th relaxation pass)
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                return null;  // negative cycle detected
            }
        }

        return dist;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V * E)** | V-1 iterations, each examining all E edges. Early termination helps in practice but worst case remains O(VE). |
| **Space** | **O(V)** | Distance array. Edge list is input. |

---
---

## Q146. Floyd-Warshall All-Pairs Shortest Path

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Dynamic Programming on Intermediate Vertices

### Problem

Given a weighted directed graph (which may include negative weights but no negative cycles), compute the shortest distance between **every pair** of nodes. Return a 2D matrix `dist[i][j]` representing the shortest path from node `i` to node `j`.

### Examples

**Example 1:**

```
Graph:
  0 --(3)--> 1
  ^           |
  |          (1)
 (7)         v
  |           2 --(2)--> 3
  +---(-5)--------+
       from 3 to 0

Edges: [0,1,3], [1,2,1], [2,3,2], [3,0,-5]
  (No negative cycle: 3 + 1 + 2 + (-5) = 1 > 0)

Step-by-step DP (k = intermediate vertex allowed):

Initial dist matrix (direct edges only):
       0     1     2     3
  0 [  0,    3,  INF,  INF ]
  1 [INF,    0,    1,  INF ]
  2 [INF,  INF,    0,    2 ]
  3 [ -5,  INF,  INF,    0 ]

After k=0 (paths through vertex 0):
       0     1     2     3
  0 [  0,    3,  INF,  INF ]
  1 [INF,    0,    1,  INF ]
  2 [INF,  INF,    0,    2 ]
  3 [ -5,   -2,  INF,    0 ]   <- dist[3][1] = dist[3][0]+dist[0][1] = -5+3 = -2

After k=1 (paths through vertices 0,1):
       0     1     2     3
  0 [  0,    3,    4,  INF ]   <- dist[0][2] = dist[0][1]+dist[1][2] = 3+1 = 4
  1 [INF,    0,    1,  INF ]
  2 [INF,  INF,    0,    2 ]
  3 [ -5,   -2,   -1,    0 ]   <- dist[3][2] = dist[3][1]+dist[1][2] = -2+1 = -1

After k=2 (paths through vertices 0,1,2):
       0     1     2     3
  0 [  0,    3,    4,    6 ]   <- dist[0][3] = dist[0][2]+dist[2][3] = 4+2 = 6
  1 [INF,    0,    1,    3 ]   <- dist[1][3] = dist[1][2]+dist[2][3] = 1+2 = 3
  2 [INF,  INF,    0,    2 ]
  3 [ -5,   -2,   -1,    1 ]   <- dist[3][3] via 2 = -1+2 = 1, but 0 is shorter, keep 0

After k=3 (paths through all vertices):
       0     1     2     3
  0 [  0,    3,    4,    6 ]
  1 [ -2,    0,    1,    3 ]   <- dist[1][0] = dist[1][3]+dist[3][0] = 3+(-5) = -2
  2 [ -3,   -1,    0,    2 ]   <- dist[2][0] = dist[2][3]+dist[3][0] = 2+(-5) = -3
  3 [ -5,   -2,   -1,    0 ]

Input:  n = 4, edges = [[0,1,3],[1,2,1],[2,3,2],[3,0,-5]]
Output: [[0,3,4,6],[-2,0,1,3],[-3,-1,0,2],[-5,-2,-1,0]]
```

**Example 2:**

```
  0 --(1)--> 1
  0 --(4)--> 2
  1 --(2)--> 2

Input:  n = 3, edges = [[0,1,1],[0,2,4],[1,2,2]]
Output: [[0,1,3],[INF,0,2],[INF,INF,0]]
Explanation: dist[0][2] via 1 = 1+2 = 3 < direct 4.
```

### Approach: Floyd-Warshall DP

**Why this approach?**

Floyd-Warshall computes all-pairs shortest paths in O(V^3) using a clean DP formulation:
`dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])` for each intermediate vertex k.

**Why not run Dijkstra V times?** That would be O(V * (V + E) log V), which is faster for sparse graphs. Floyd-Warshall wins for dense graphs (E ~ V^2) and is much simpler to implement. It also handles negative weights (unlike Dijkstra).

### Optimized Java Solution

```java
import java.util.*;

public class FloydWarshall {

    private static final int INF = 1_000_000_000; // Use large int, not MAX_VALUE (avoid overflow)

    public int[][] allPairsShortestPath(int n, int[][] edges) {
        // Initialize distance matrix
        int[][] dist = new int[n][n];
        for (int[] row : dist) Arrays.fill(row, INF);
        for (int i = 0; i < n; i++) dist[i][i] = 0;

        for (int[] e : edges) {
            dist[e[0]][e[1]] = Math.min(dist[e[0]][e[1]], e[2]); // handle parallel edges
        }

        // Floyd-Warshall DP: try each vertex k as intermediate
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == INF) continue;  // optimization: skip unreachable
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == INF) continue;
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        // Optional: detect negative cycles (dist[i][i] < 0 for some i)
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                throw new IllegalArgumentException("Negative cycle detected involving node " + i);
            }
        }

        return dist;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V^3)** | Three nested loops, each iterating V times. |
| **Space** | **O(V^2)** | The distance matrix. Can be done in-place (no extra matrix needed since we overwrite). |

---
---

## Q147. Network Delay Time

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Dijkstra's Algorithm

### Problem

You are given a network of `n` nodes, labeled from `1` to `n`, and a list of travel times as directed edges `times[i] = (u, v, w)` where `u` is the source, `v` is the target, and `w` is the travel time. A signal is sent from node `k`. Return the minimum time it takes for all nodes to receive the signal. If it is impossible for all nodes to receive the signal, return `-1`.

*(LeetCode 743)*

### Examples

**Example 1:**

```
  1 --(2)--> 2
  |           |
 (1)        (1)
  v           v
  3 <--(1)-- 4

  times = [[1,2,2],[1,3,1],[2,4,1],[4,3,1]]

Distance table (source = 1):
  Step | Processed | dist[1] | dist[2] | dist[3] | dist[4]
  -----|-----------|---------|---------|---------|--------
  Init |    -      |    0    |   INF   |   INF   |  INF
    1  |    1      |    0    |    2    |    1    |  INF
    2  |    3      |    0    |    2    |    1    |  INF   (3 has no outgoing edges to update)
    3  |    2      |    0    |    2    |    1    |   3
    4  |    4      |    0    |    2    |    1    |   3    (4->3: 3+1=4 > 1, no update)

  Max distance = 3

Input:  times = [[1,2,2],[1,3,1],[2,4,1],[4,3,1]], n = 4, k = 1
Output: 3
Explanation: Signal reaches all nodes by time 3. Node 4 is the last to receive at time 3.
```

**Example 2:**

```
Input:  times = [[1,2,1]], n = 2, k = 2
Output: -1
Explanation: Node 2 cannot reach node 1.
```

**Example 3:**

```
Input:  times = [[1,2,1]], n = 2, k = 1
Output: 1
```

### Approach: Dijkstra's Algorithm -- Maximum of Shortest Distances

**Why this approach?**

The signal propagates through the network via shortest paths. We need the maximum shortest-path distance from source `k` to any node -- that's when the last node receives the signal. Dijkstra's is perfect for single-source shortest path with non-negative weights (travel times are always positive).

### Optimized Java Solution

```java
import java.util.*;

public class NetworkDelayTime {

    public int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list (1-indexed nodes)
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] t : times) {
            adj.get(t[0]).add(new int[]{t[1], t[2]});
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        // Min-heap: (distance, node)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, k});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int d = curr[0], u = curr[1];
            if (d > dist[u]) continue;  // stale entry

            for (int[] edge : adj.get(u)) {
                int v = edge[0], w = edge[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }

        int maxDist = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxDist = Math.max(maxDist, dist[i]);
        }
        return maxDist;
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O((V + E) log V)** | Dijkstra's with binary heap. V = n, E = number of time entries. |
| **Space** | **O(V + E)** | Adjacency list + priority queue + distance array. |

---
---

## Q148. Cheapest Flights Within K Stops

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Modified Bellman-Ford

### Problem

There are `n` cities connected by `flights`. Each flight `[from, to, price]` has a cost. Given `src`, `dst`, and `k`, find the cheapest price from `src` to `dst` with **at most `k` stops** (i.e., at most `k + 1` edges). If there is no such route, return `-1`.

*(LeetCode 787)*

### Examples

**Example 1:**

```
      0
     / \
   (100) (500)
   /       \
  v         v
  1 --(200)--> 2

Input:  n = 3, flights = [[0,1,100],[1,2,200],[0,2,500]], src = 0, dst = 2, k = 1
Output: 300
Explanation: 0 -> 1 -> 2 costs 300 (1 stop). Direct 0 -> 2 costs 500.
```

**Example 2:**

```
Input:  n = 3, flights = [[0,1,100],[1,2,200],[0,2,500]], src = 0, dst = 2, k = 0
Output: 500
Explanation: With 0 stops, only direct flight 0 -> 2 is available.
```

**Example 3:**

```
  0 --(1)--> 1 --(1)--> 2 --(1)--> 3

Input:  n = 4, flights = [[0,1,1],[1,2,1],[2,3,1]], src = 0, dst = 3, k = 1
Output: -1
Explanation: Need 3 edges (2 stops), but only 1 stop allowed.
```

### Approach: Modified Bellman-Ford with K+1 Iterations

**Why this approach?**

Standard Bellman-Ford runs V-1 iterations; here we limit to K+1 iterations (K+1 edges = K stops). In each iteration, we relax all edges, but use the **previous iteration's distances** (not the current one) to prevent using more edges than allowed. This is the key distinction from standard Bellman-Ford.

**Why not Dijkstra?** Dijkstra doesn't naturally handle the "at most K stops" constraint. While a modified Dijkstra with state `(cost, node, stops)` works, it can be slower due to exponential state space in worst case.

### Optimized Java Solution

```java
import java.util.*;

public class CheapestFlightsKStops {

    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // K stops = K+1 edges, so run K+1 relaxation rounds
        for (int i = 0; i <= k; i++) {
            // IMPORTANT: copy dist from previous round to avoid cascading updates
            int[] prev = dist.clone();
            boolean updated = false;

            for (int[] flight : flights) {
                int from = flight[0], to = flight[1], price = flight[2];
                if (prev[from] != Integer.MAX_VALUE && prev[from] + price < dist[to]) {
                    dist[to] = prev[from] + price;
                    updated = true;
                }
            }

            if (!updated) break;  // early termination
        }

        return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(K * E)** | K+1 iterations, each examining all E flights. K <= V-1, so worst case O(V * E). |
| **Space** | **O(V)** | Two distance arrays (current + previous). |

---
---

## Q149. Critical Connections in Network (Tarjan's Bridges)

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Tarjan's Algorithm (Low-Link Values)

### Problem

Given `n` servers and connections between them, a **critical connection** (bridge) is an edge whose removal disconnects the graph. Find all critical connections.

*(LeetCode 1192)*

### Examples

**Example 1:**

```
Graph:
  0 -- 1
  |    |
  3    2

  Note: 0-1-2 form a cycle with edge 0-3 hanging off.
  Wait -- let me draw correctly:
  
  0 -- 1 -- 2
  |
  3

Input:  n = 4, connections = [[0,1],[1,2],[2,0],[1,3]]
Output: [[1,3]]
Explanation: 
  0-1-2-0 is a cycle, so none of those edges are bridges.
  Removing edge (1,3) disconnects node 3 from the rest.
```

**Example 2:**

```
  0 -- 1 -- 2 -- 3 -- 4

Input:  n = 5, connections = [[0,1],[1,2],[2,3],[3,4]]
Output: [[0,1],[1,2],[2,3],[3,4]]
Explanation: Every edge is a bridge (it's a simple path / tree).
```

**Example 3:**

```
  0 -- 1
  |  / |
  3    2
  |   /
  +--+

  0-1, 1-2, 2-3, 3-0, 1-3

Input:  n = 4, connections = [[0,1],[1,2],[2,3],[3,0],[1,3]]
Output: [[1,2]]
Explanation: Removing (1,2) isolates node 2 from {0,1,3} cycle.
  Actually with edge 2-3 and 3-0, 2 connects back. Let me reconsider.
  0-1, 1-2, 2-3, 3-0, 1-3: every node can reach every other even if one edge removed.
  
  Corrected: n = 5, connections = [[0,1],[1,2],[2,0],[3,4],[2,3]]
  Output: [[2,3],[3,4]]
```

### Approach: Tarjan's Bridge-Finding Algorithm

**Why this approach?**

Tarjan's algorithm finds all bridges in O(V + E) using DFS with two key arrays:
- **disc[u]:** Discovery time of node u in DFS.
- **low[u]:** Lowest discovery time reachable from the subtree rooted at u.

**Core insight:** An edge (u, v) where v is a child of u in the DFS tree is a **bridge** if and only if `low[v] > disc[u]`. This means there's no back edge from the subtree of v that goes above u -- so removing (u, v) disconnects v's subtree.

**Low-link value explanation:**
- `low[u]` is initialized to `disc[u]`.
- For each neighbor v:
  - If v is unvisited: recurse, then `low[u] = min(low[u], low[v])`.
  - If v is visited and v != parent: `low[u] = min(low[u], disc[v])` (back edge).
- After processing all neighbors, if `low[v] > disc[u]` for some child v, edge (u, v) is a bridge.

### Optimized Java Solution

```java
import java.util.*;

public class CriticalConnections {

    private int timer = 0;

    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (List<Integer> conn : connections) {
            adj.get(conn.get(0)).add(conn.get(1));
            adj.get(conn.get(1)).add(conn.get(0));
        }

        int[] disc = new int[n];   // discovery time
        int[] low = new int[n];    // lowest reachable discovery time
        Arrays.fill(disc, -1);     // -1 means unvisited

        List<List<Integer>> bridges = new ArrayList<>();

        // DFS from node 0 (graph is connected per problem)
        dfs(0, -1, adj, disc, low, bridges);

        return bridges;
    }

    private void dfs(int u, int parent, List<List<Integer>> adj,
                     int[] disc, int[] low, List<List<Integer>> bridges) {
        disc[u] = low[u] = timer++;

        for (int v : adj.get(u)) {
            if (v == parent) continue;         // skip the edge we came from

            if (disc[v] == -1) {
                // Tree edge: v is unvisited
                dfs(v, u, adj, disc, low, bridges);
                low[u] = Math.min(low[u], low[v]);

                // Bridge condition: no back edge from v's subtree reaches u or above
                if (low[v] > disc[u]) {
                    bridges.add(List.of(u, v));
                }
            } else {
                // Back edge: v is already visited
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(V + E)** | Single DFS pass. Each node and edge is processed once. |
| **Space** | **O(V + E)** | Adjacency list: O(V + E). disc, low arrays: O(V). Recursion stack: O(V). |

---
---

## Q150. Alien Dictionary

**Difficulty:** `Hard` | **Data Structure:** Graph | **Pattern:** Topological Sort from Constraints

### Problem

There is a new alien language that uses the English alphabet. However, the order among letters is unknown to you. You are given a list of strings `words` from the alien language's dictionary, where the strings are **sorted lexicographically** by the rules of this new language. Derive the order of letters in this language. If the order is invalid (cycle), return `""`. If there are multiple valid orderings, return any of them.

*(LeetCode 269)*

### Examples

**Example 1:**

```
Input:  words = ["wrt", "wrf", "er", "ett", "rftt"]
Output: "wertf"

Derivation of constraints:
  "wrt" vs "wrf"  -> t < f  (first difference at index 2)
  "wrf" vs "er"   -> w < e  (first difference at index 0)
  "er"  vs "ett"  -> r < t  (first difference at index 1)
  "ett" vs "rftt" -> e < r  (first difference at index 0)

Graph: w -> e -> r -> t -> f
Topological order: w, e, r, t, f -> "wertf"
```

**Example 2:**

```
Input:  words = ["z", "x"]
Output: "zx"
Explanation: z comes before x in alien alphabet.
```

**Example 3:**

```
Input:  words = ["z", "x", "z"]
Output: ""
Explanation: z < x and x < z => cycle => invalid.
```

### Approach: Build DAG from Adjacent Word Pairs, Then Topological Sort

**Why this approach?**

The sorted word list gives us ordering constraints. By comparing adjacent words character by character, the **first difference** tells us a precedence rule `char1 < char2`. We build a directed graph from these constraints and run topological sort (Kahn's BFS). If the sort includes all characters, we have a valid order. If not (cycle), return "".

**Edge case:** If a longer word appears before its prefix (e.g., ["abc", "ab"]), the input is invalid.

### Optimized Java Solution

```java
import java.util.*;

public class AlienDictionary {

    public String alienOrder(String[] words) {
        // Step 1: Initialize in-degree for all characters that appear
        Map<Character, Integer> inDegree = new HashMap<>();
        Map<Character, Set<Character>> adj = new HashMap<>();

        for (String word : words) {
            for (char c : word.toCharArray()) {
                inDegree.putIfAbsent(c, 0);
                adj.putIfAbsent(c, new HashSet<>());
            }
        }

        // Step 2: Build graph from adjacent word pairs
        for (int i = 0; i < words.length - 1; i++) {
            String w1 = words[i], w2 = words[i + 1];
            int minLen = Math.min(w1.length(), w2.length());

            // Edge case: "abc" before "ab" is invalid
            if (w1.length() > w2.length() && w1.startsWith(w2)) {
                return "";
            }

            for (int j = 0; j < minLen; j++) {
                char c1 = w1.charAt(j), c2 = w2.charAt(j);
                if (c1 != c2) {
                    // c1 comes before c2 in alien alphabet
                    if (!adj.get(c1).contains(c2)) {
                        adj.get(c1).add(c2);
                        inDegree.merge(c2, 1, Integer::sum);
                    }
                    break;  // only the first difference matters
                }
            }
        }

        // Step 3: Kahn's topological sort
        Queue<Character> queue = new ArrayDeque<>();
        for (Map.Entry<Character, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        StringBuilder order = new StringBuilder();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            order.append(c);

            for (char neighbor : adj.get(c)) {
                int newDeg = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newDeg);
                if (newDeg == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        // If not all characters are in the order, there's a cycle
        if (order.length() != inDegree.size()) return "";

        return order.toString();
    }
}
```

### Complexity Analysis

| Metric | Value | Explanation |
|---|---|---|
| **Time** | **O(C)** | C = total number of characters across all words. Building the graph scans all characters. Topological sort processes at most 26 nodes and E edges (E <= C). |
| **Space** | **O(1) or O(U + E)** | U = unique characters (at most 26). Adjacency map and in-degree map bounded by 26 characters. E = number of edges (at most 26^2 = 676). Effectively O(1) for fixed alphabet. |

---
---

*End of GRAPH section (Q126 -- Q150)*


---
---

# COMPLETE COMPLEXITY REFERENCE TABLE (All 150 Questions)

## Singly Linked List (Q1-Q25)

| # | Question | Time | Space | Difficulty |
|---|---|---|---|---|
| Q1 | Reverse Linked List | O(n) | O(1) | Basic |
| Q2 | Find Middle | O(n) | O(1) | Basic |
| Q3 | Delete Node (ref only) | O(1) | O(1) | Basic |
| Q4 | Remove Sorted Duplicates | O(n) | O(1) | Basic |
| Q5 | Merge Two Sorted Lists | O(n+m) | O(1) | Basic |
| Q6 | Detect Cycle | O(n) | O(1) | Medium |
| Q7 | Find Cycle Start | O(n) | O(1) | Medium |
| Q8 | Intersection Point | O(n+m) | O(1) | Medium |
| Q9 | Remove Nth from End | O(n) | O(1) | Medium |
| Q10 | Palindrome Check | O(n) | O(1) | Medium |
| Q11 | Add Two Numbers | O(max(n,m)) | O(max(n,m)) | Medium |
| Q12 | Odd-Even Linked List | O(n) | O(1) | Medium |
| Q13 | Partition List | O(n) | O(1) | Medium |
| Q14 | Rotate List | O(n) | O(1) | Medium |
| Q15 | Swap Pairs | O(n) | O(1) | Medium |
| Q16 | Sort List (Merge Sort) | O(n log n) | O(log n) | Medium |
| Q17 | Remove Unsorted Dupes | O(n) | O(n) | Medium |
| Q18 | Reorder List | O(n) | O(1) | Medium |
| Q19 | Copy with Random Pointer | O(n) | O(1) | Hard |
| Q20 | Flatten Multilevel Sorted | O(n log k) | O(k) | Hard |
| Q21 | Reverse k-Group | O(n) | O(1) | Hard |
| Q22 | Merge K Sorted Lists | O(N log k) | O(k) | Hard |
| Q23 | Reverse Between m and n | O(n) | O(1) | Hard |
| Q24 | Cycle Length + Entry | O(n) | O(1) | Hard |
| Q25 | Design Linked List | O(n) per op | O(n) | Hard |

## Doubly Linked List (Q26-Q50)

| # | Question | Time | Space | Difficulty |
|---|---|---|---|---|
| Q26 | Insert at Beginning | O(1) | O(1) | Basic |
| Q27 | Insert at End | O(n) or O(1) | O(1) | Basic |
| Q28 | Delete Given Node | O(1) | O(1) | Basic |
| Q29 | Reverse DLL | O(n) | O(1) | Basic |
| Q30 | Insert at Position | O(n) | O(1) | Basic |
| Q31 | Remove Sorted Duplicates | O(n) | O(1) | Medium |
| Q32 | Pairs with Sum (Sorted) | O(n) | O(1) | Medium |
| Q33 | Merge Two Sorted DLLs | O(n+m) | O(1) | Medium |
| Q34 | Count Triplets with Sum | O(n^2) | O(1) | Medium |
| Q35 | Rotate DLL by N | O(n) | O(1) | Medium |
| Q36 | Delete All Occurrences | O(n) | O(1) | Medium |
| Q37 | Sort DLL (Merge Sort) | O(n log n) | O(log n) | Medium |
| Q38 | Group Odd/Even Nodes | O(n) | O(1) | Medium |
| Q39 | Insert in Sorted Position | O(n) | O(1) | Medium |
| Q40 | Swap Kth from Both Ends | O(n) | O(1) | Medium |
| Q41 | Binary Tree to DLL | O(n) | O(h) | Medium |
| Q42 | Reverse in Groups of K | O(n) | O(1) | Medium |
| Q43 | Flatten Multilevel DLL | O(n) | O(n) | Medium |
| Q44 | LRU Cache | O(1) per op | O(capacity) | Hard |
| Q45 | Browser History | O(1) per op | O(n) | Hard |
| Q46 | Max Stack | O(log n) per op | O(n) | Hard |
| Q47 | All O(1) Data Structure | O(1) per op | O(n) | Hard |
| Q48 | Text Editor with Cursor | O(1) per op | O(n) | Hard |
| Q49 | LFU Cache | O(1) per op | O(capacity) | Hard |
| Q50 | XOR Linked List (simulated) | O(1) per op | O(n) | Hard |

## Stack (Q51-Q75)

| # | Question | Time | Space | Difficulty |
|---|---|---|---|---|
| Q51 | Implement Stack (Array) | O(1) amortized | O(n) | Basic |
| Q52 | Valid Parentheses | O(n) | O(n) | Basic |
| Q53 | Reverse String | O(n) | O(n) | Basic |
| Q54 | Two Stacks in One Array | O(1) per op | O(n) | Basic |
| Q55 | Sort Stack (Recursion) | O(n^2) | O(n) | Basic |
| Q56 | Min Stack | O(1) per op | O(n) | Medium |
| Q57 | Evaluate RPN | O(n) | O(n) | Medium |
| Q58 | Daily Temperatures | O(n) | O(n) | Medium |
| Q59 | Next Greater Element I | O(n+m) | O(n) | Medium |
| Q60 | Next Greater Element II | O(n) | O(n) | Medium |
| Q61 | Stack using Queues | O(n) push or O(n) pop | O(n) | Medium |
| Q62 | Decode String | O(n * maxK) | O(n) | Medium |
| Q63 | Simplify Path | O(n) | O(n) | Medium |
| Q64 | Asteroid Collision | O(n) | O(n) | Medium |
| Q65 | Remove k Adjacent Dupes | O(n) | O(n) | Medium |
| Q66 | Remove K Digits | O(n) | O(n) | Medium |
| Q67 | Stock Span | O(n) | O(n) | Medium |
| Q68 | Validate Stack Sequences | O(n) | O(n) | Medium |
| Q69 | Basic Calculator | O(n) | O(n) | Hard |
| Q70 | Basic Calculator II | O(n) | O(n) | Hard |
| Q71 | Largest Rectangle Histogram | O(n) | O(n) | Hard |
| Q72 | Trapping Rain Water | O(n) | O(n) | Hard |
| Q73 | Max Frequency Stack | O(1) per op | O(n) | Hard |
| Q74 | Longest Valid Parentheses | O(n) | O(n) | Hard |
| Q75 | Visible People in Queue | O(n) | O(n) | Hard |

## Queue (Q76-Q100)

| # | Question | Time | Space | Difficulty |
|---|---|---|---|---|
| Q76 | Queue using Array (Circular) | O(1) per op | O(n) | Basic |
| Q77 | Queue using Two Stacks | Amortized O(1) | O(n) | Basic |
| Q78 | Queue using Linked List | O(1) per op | O(n) | Basic |
| Q79 | Binary Numbers 1 to N | O(n) | O(n) | Basic |
| Q80 | Recent Calls Counter | O(1) amortized | O(W) | Basic |
| Q81 | Circular Queue Design | O(1) per op | O(k) | Medium |
| Q82 | Deque from Scratch | O(1) per op | O(n) | Medium |
| Q83 | First Non-Repeating Char | O(1) per char | O(26) | Medium |
| Q84 | Stack using Queues | O(n) per push/pop | O(n) | Medium |
| Q85 | Reverse First K Elements | O(n) | O(k) | Medium |
| Q86 | Interleave Halves | O(n) | O(n) | Medium |
| Q87 | Rotting Oranges | O(M*N) | O(M*N) | Medium |
| Q88 | Walls and Gates | O(M*N) | O(M*N) | Medium |
| Q89 | Moving Average | O(1) per call | O(k) | Medium |
| Q90 | Task Scheduler | O(n) | O(1) | Medium |
| Q91 | Hit Counter | O(1) amortized | O(W) | Medium |
| Q92 | Zigzag Iterator | O(1) per next | O(k) | Medium |
| Q93 | Circular Deque | O(1) per op | O(k) | Medium |
| Q94 | Time to Buy Tickets | O(n) | O(1) | Medium |
| Q95 | Sliding Window Maximum | O(n) | O(k) | Hard |
| Q96 | Shortest Subarray Sum>=K | O(n) | O(n) | Hard |
| Q97 | Jump Game VI | O(n) | O(n) | Hard |
| Q98 | Snake Game | O(1) per move | O(n) | Hard |
| Q99 | Max of Subarrays Size K | O(n) | O(k) | Hard |
| Q100 | Process Tasks Using Servers | O(n log n) | O(n) | Hard |

## Tree (Q101-Q125)

| # | Question | Time | Space | Difficulty |
|---|---|---|---|---|
| Q101 | Inorder Traversal (Iterative) | O(n) | O(h) | Basic |
| Q102 | Preorder Traversal (Iterative) | O(n) | O(h) | Basic |
| Q103 | Postorder Traversal (Iterative) | O(n) | O(h) | Basic |
| Q104 | Maximum Depth | O(n) | O(h) | Basic |
| Q105 | Identical Trees | O(n) | O(h) | Basic |
| Q106 | Invert Binary Tree | O(n) | O(h) | Basic |
| Q107 | Symmetric Tree | O(n) | O(h) | Medium |
| Q108 | Level Order Traversal | O(n) | O(w) | Medium |
| Q109 | Zigzag Level Order | O(n) | O(w) | Medium |
| Q110 | Validate BST | O(n) | O(h) | Medium |
| Q111 | Lowest Common Ancestor | O(n) | O(h) | Medium |
| Q112 | Construct from Pre+In | O(n) | O(n) | Medium |
| Q113 | Kth Smallest in BST | O(h+k) | O(h) | Medium |
| Q114 | Right Side View | O(n) | O(w) | Medium |
| Q115 | Flatten to Linked List | O(n) | O(1) | Medium |
| Q116 | Diameter of Tree | O(n) | O(h) | Medium |
| Q117 | Path Sum II | O(n) | O(h) | Medium |
| Q118 | Count Complete Tree Nodes | O(log^2 n) | O(log n) | Medium |
| Q119 | Maximum Path Sum | O(n) | O(h) | Hard |
| Q120 | Serialize/Deserialize | O(n) | O(n) | Hard |
| Q121 | Vertical Order Traversal | O(n log n) | O(n) | Hard |
| Q122 | Recover BST | O(n) | O(h) | Hard |
| Q123 | Morris Traversal | O(n) | O(1) | Hard |
| Q124 | Binary Tree Cameras | O(n) | O(h) | Hard |
| Q125 | Sorted Array to BST | O(n) | O(log n) | Hard |

## Graph (Q126-Q150)

| # | Question | Time | Space | Difficulty |
|---|---|---|---|---|
| Q126 | BFS Traversal | O(V+E) | O(V) | Basic |
| Q127 | DFS Traversal (Iterative) | O(V+E) | O(V) | Basic |
| Q128 | Connected Components | O(V+E) | O(V) | Basic |
| Q129 | Path Exists | O(V+E) | O(V) | Basic |
| Q130 | Cycle in Undirected (UF) | O(E*a(V)) | O(V) | Medium |
| Q131 | Cycle in Directed (DFS) | O(V+E) | O(V) | Medium |
| Q132 | Number of Islands | O(M*N) | O(M*N) | Medium |
| Q133 | Clone Graph | O(V+E) | O(V) | Medium |
| Q134 | Is Bipartite? | O(V+E) | O(V) | Medium |
| Q135 | Topological Sort | O(V+E) | O(V+E) | Medium |
| Q136 | Course Schedule | O(V+E) | O(V+E) | Medium |
| Q137 | Course Schedule II | O(V+E) | O(V+E) | Medium |
| Q138 | Word Ladder | O(M^2 * N) | O(M^2 * N) | Medium |
| Q139 | Pacific Atlantic | O(M*N) | O(M*N) | Medium |
| Q140 | Surrounded Regions | O(M*N) | O(M*N) | Medium |
| Q141 | Graph Valid Tree | O(V+E) | O(V) | Medium |
| Q142 | Minimum Height Trees | O(V+E) | O(V+E) | Medium |
| Q143 | MST (Kruskal's) | O(E log E) | O(V) | Hard |
| Q144 | Dijkstra's | O((V+E) log V) | O(V+E) | Hard |
| Q145 | Bellman-Ford | O(V*E) | O(V) | Hard |
| Q146 | Floyd-Warshall | O(V^3) | O(V^2) | Hard |
| Q147 | Network Delay Time | O((V+E) log V) | O(V+E) | Hard |
| Q148 | Cheapest Flights K Stops | O(K*E) | O(V) | Hard |
| Q149 | Critical Connections | O(V+E) | O(V+E) | Hard |
| Q150 | Alien Dictionary | O(C) | O(1) | Hard |

> **h** = tree height, **w** = max tree width, **V** = vertices, **E** = edges, **M*N** = grid dimensions, **a** = inverse Ackermann, **C** = total characters, **W** = time window

---

> **Study Strategy:**
> 1. Master all **Basic** questions first (Q1-5, Q26-30, Q51-55, Q76-80, Q101-106, Q126-129)
> 2. Then tackle **Medium** questions grouped by data structure
> 3. Save **Hard** questions for last -- they all build on patterns from earlier questions
> 4. For interviews: focus on Q6, Q8, Q10, Q18, Q22, Q44, Q52, Q58, Q66, Q71, Q87, Q95, Q110, Q111, Q119, Q132, Q135, Q144

---
