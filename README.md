# B-Tree
The project is an implementation of B+-Tree, and consists mainly of the UI and
BTree two parts.

## UI Part
### btree data.txt:
* scanner in the file
* build in the initial B+-tree: based on the insert function
### insert 10 20 2
* Randomly choose 2 num in range of [10,20]
* Based on the search function to find the existing value/insert pos
### delete 15 16
* Based on the delete function delete all the key exist in the givenrange
* The delete rule is same as B+-tree delete showing above
### print
* Print out the B+-Tree and the picture show in below is the showingformat
* ![image](https://github.com/Charlotte-Song/B-Tree/blob/main/WechatIMG136.png)

## B+Tree Part
### Btree
* Compose: order(the max key count), root, first child
* Data Structure: B+-Tree (Data structure of BTree)
* Function:
  * BTree (String Path): Constructor, initial the tree and set the order.
  * insert(int key, int value)/.../insert(int low, int high,int num): through the Stack structure to help to insert. and through the recursion to complete.
  * delete (long key)/delete (int low, int high): and after the delete, entering the check.
  * check(Stack<BPlusTreeNode> nodeStack,Stack<Integer> indexStack): check after the delete through the stack to help to check.whether the tree needs to merge or not
  * merge: merge the unqualified space node together t
  * search: though the loop to traverse all the number in the range
  * print: depends on the toString to complete.
  * getNodeTotalCount, getKeyTotalCount, getNodeCount, getAveFillFactor, getHeight

### Node
* Compose: index, order, number of keys, the corresponding value of the key, children node, sibling node
* Data structure: array (Data structure of Node)
* Function:
  * BPlusTreeNode(int m): Constructor, construct the node and set the order.
  * show (int depth): Print the information tree under this node.  
  * toString(), toString(int level): return the information tree under this node in string type  
  
## Data Structure
### Array
* Data structure of Node: key, value, children node Through the fixed length array to store these variables at the same time control the length(volume) of these variables.
### Stack
* Data Structure of BTree insert: Through the stack we can easily pop or push the node and find the first node.
* Data Structure of BTree check:  Through the stack we can easily pop or push the node and check whether the space is enough.
### Tree
* Data Structure of BTree: BTree The related rules and details is show in the description of the B+-tree, which is the first part of the report

## Authors

An Java project completed duirng the end of Nov.2021 to Begining of Dec.2021 by Charlotte
Also part of the coding completed by my groupmate(WK & XYZ),thanks for their contibutions.
