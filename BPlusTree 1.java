
import java.util.Stack;

public class BPlusTree {
    int maxKeyCount;	//最大关键字个数，即：B+树的路数
    //最小关键字个数=(maxKeyCount+1)/2;
    BPlusTreeNode root;	//根
    BPlusTreeNode first;//第一个子结点
    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {

        int list[]={52,52,187,98,180,148,88,122,8,58,129,184,87,74,71,111,198,74,181,185,86};
        //int list[] = { 52, 187, 98, 180, 148, 88, 122, 8, 58, 129, 184 };
        int n = list.length;

        BPlusTree b = new BPlusTree(4);
        System.out.println("Height:"+b.getHeight());
        System.out.println("NodeTotalCount:"+b.getNodeTotalCount());
        System.out.println("LeafNodeCount:"+b.getLeafNodeCount());
        System.out.println("KeyCount:"+b.getKeyTotalCount());

        //往B+树中插入关键字
        for (int i = 0; i < n; i++) {
            b.insert(list[i], i);
        }

        System.out.println("Height:"+b.getHeight());
        System.out.println("NodeTotalCount:"+b.getNodeTotalCount());
        System.out.println("LeafNodeCount:"+b.getLeafNodeCount());
        System.out.println("KeyCount:"+b.getKeyTotalCount());

        //输出树结构
        b.print_tree_2();
        System.out.println("------------------------------------");
        b.print_leaf_layer();
        //System.out.println("------------------------------------");
        System.out.println("一共" + n + "个关键字\n");

        long addr = b.search(74);
        System.out.println("address="+addr);
        addr = b.search(47);
        System.out.println("address="+addr);
        addr = b.search(129);
        System.out.println("address="+addr);

        //从B+树中移除关键字
        boolean flag;
        flag = b.delete(74);
        System.out.println("删除"+(flag?"成功":"失败"));
        flag = b.delete(47);
        System.out.println("删除"+(flag?"成功":"失败"));
        flag = b.delete(129);
        System.out.println("删除"+(flag?"成功":"失败"));

        //输出树结构
        b.print_tree_2();
        System.out.println("------------------------------------");
        //b.print_leaf_layer();
        //System.out.println("------------------------------------");

        addr = b.search(74);
        System.out.println("address="+addr);
        addr = b.search(47);
        System.out.println("address="+addr);
        addr = b.search(129);
        System.out.println("address="+addr);

        System.out.println("Height:"+b.getHeight());
        System.out.println("NodeTotalCount:"+b.getNodeTotalCount());
        System.out.println("LeafNodeCount:"+b.getLeafNodeCount());
        System.out.println("KeyCount:"+b.getKeyTotalCount());

        b.print_tree();

    }

    /**
     * BPlusTree构造函数
     */
    public BPlusTree(int maxKeyCount){
        if(maxKeyCount < 2) maxKeyCount=2;
        this.maxKeyCount = maxKeyCount;
        this.root = null;
        this.first = null;
    }

    /**
     * 搜索关键字key所指向的数据位置
     * @param key 关键字，作为搜索的依据
     * @return int 关键字key所指向的数据位置,-1则表示未找到
     */
    public long search(int key){
        if (this.root == null || key > this.root.key[this.root.num - 1]) {
            // root为null 或 key大于树中最大key
            return -1;
        }
        BPlusTreeNode node = this.root;
        int i;

        while (node!=null) {
            for (i = 0; node.key[i] < key; i++);
            if (node.isLeaf == false) {// 中间结点
                node = node.children[i];
            } else {
                if (node.key[i] == key) {// 找到
                    //System.out.println("找到关键字"+key);
                    return node.addr[i];
                } else {// 未找到
                    //System.out.println("未找到" + key);
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * 向BPlusTree中insert关键字为key的记录项，并设定数据指针为addr
     * @param key 关键字
     * @param addr 数据指针
     */
    public void insert(int key, int addr){
        //System.out.println("进入insert(" + key + "," + addr + ")方法");
        BPlusTreeNode node = this.root;
        int i;
        Stack<BPlusTreeNode> nodeStack = new Stack<BPlusTreeNode>();
        Stack<Integer> indexStack = new Stack<Integer>();

        nodeStack.push(null);
        indexStack.push(0);
        //System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
        if (node != null) {
            //System.out.println("root!=null");
            i = node.num;
            if (key < node.key[i - 1]) {
                for (;;) {
                    for (i = 0; node.key[i] < key; i++)
                        ;
                    nodeStack.push(node);
                    indexStack.push(i);
                    //System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
                    if (node.isLeaf == false) {// 非终端结点
                        node = node.children[i];
                    } else {
                        break;
                    }
                }
            } else {
                for (;;) {
                    nodeStack.push(node);
                    if (node.isLeaf == false) {
                        node.key[i - 1] = key;// 修改上级结点的最大索引值
                        indexStack.push(i-1);

                        node = node.children[i - 1];
                        i = node.num;
                    } else {
                        indexStack.push(i);
                        break;
                    }
                }
                //System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));
            }
        } else {
            //System.out.println("root==null");
            node = new BPlusTreeNode(this.maxKeyCount);
            this.first = this.root = node;
            indexStack.push(0);
            nodeStack.push(node);
            node.num = 0;
            node.isLeaf = true;
            node.next = null;
        }
        //递归调用
        this.insert(key, addr, null, nodeStack, indexStack);
        //System.out.println("完成插入");
    }

    /**
     * 递归插入(key,addr)
     * @param key 关键字
     * @param addr 数据指针
     * @param child 指示子节点
     * @param nodeStack 结点堆栈
     * @param indexStack 索引堆栈
     */
    private void insert(int key, int addr, BPlusTreeNode child,
                        Stack<BPlusTreeNode> nodeStack, Stack<Integer> indexStack)
    {
        //System.out.println("进入insert递归方法");
        BPlusTreeNode node = nodeStack.peek(), parent, sibling;
        int j = indexStack.peek();
        //System.out.println("index[top]即j="+j);
        int i, m, k;
        if (node.num < this.maxKeyCount) {// 有剩余空间
            //System.out.println("有剩余空间");
            for (i = node.num; i > j; i--) {
                node.children[i] = node.children[i - 1];
                node.key[i] = node.key[i - 1];
                node.addr[i] = node.addr[i - 1];
            }
            node.children[j] = child;
            node.key[j] = key;
            node.addr[j] = addr;
            node.num++;
            return;
        }

        // 分裂结点
        //System.out.println("分裂结点");
        sibling = new BPlusTreeNode(this.maxKeyCount);
        m = (this.maxKeyCount + 1) >> 1; // M=(KEY_COUNT+1)/2;
        //System.out.println("m=" + m);
        sibling.next = node.next;
        sibling.isLeaf = node.isLeaf;
        sibling.num = this.maxKeyCount + 1 - m;
        node.next = sibling;
        node.num = m;
        if (j < m) {
            //System.out.println("j(" + j + ")<m(" + m + ")");
            for (i = m - 1, k = 0; i < this.maxKeyCount; i++, k++) {
                sibling.key[k] = node.key[i];
                sibling.addr[k] = node.addr[i];
                sibling.children[k] = node.children[i];
            }
            for (i = m - 2; i >= j; i--) {
                node.key[i + 1] = node.key[i];
                node.addr[i + 1] = node.addr[i];
                node.children[i + 1] = node.children[i];
            }
            node.key[j] = key;
            node.addr[j] = addr;
            node.children[j] = child;
        } else {
            //System.out.println("j(" + j + ")>=m(" + m + ")");
            for (i = m, k = 0; i < j; i++, k++) {
                sibling.key[k] = node.key[i];
                sibling.addr[k] = node.addr[i];
                sibling.children[k] = node.children[i];
            }
            sibling.key[k] = key;
            sibling.addr[k] = addr;
            sibling.children[k] = child;
            k++;
            for (; i < this.maxKeyCount; i++, k++) {
                sibling.key[k] = node.key[i];
                sibling.addr[k] = node.addr[i];
                sibling.children[k] = node.children[i];
            }
        }

        // 修改上级索引
        //System.out.println("修改上级索引");
        nodeStack.pop();
        indexStack.pop();
        parent = nodeStack.peek();
        j = indexStack.peek();
        if (parent != null) {
            //System.out.println("修改上级索引parent!=null");
            parent.children[j] = sibling;
            key = node.key[m - 1];
            this.insert(key, addr, node, nodeStack, indexStack);
        } else {
            //System.out.println("新的根结点root");
            this.root = parent = new BPlusTreeNode(this.maxKeyCount);
            parent.num = 2;
            parent.next = null;
            parent.isLeaf = false;
            //nodeStack.setElementAt(parent, nodeStack.size()-1);

            parent.key[0] = node.key[m - 1];
            parent.children[0] = node;
            parent.key[1] = sibling.key[this.maxKeyCount - m];
            parent.children[1] = sibling;
        }
    }

    /**
     * 从BPlusTree中移除关键字key
     * @param key 待删除的关键字
     * @return 删除成功返回true，否则返回false
     */
    public boolean delete(long key) {
        //System.out.println("进入方法delete(" + key + ")");
        if (this.root == null || key > this.root.key[this.root.num - 1]) {
            // root为null 或 key大于树中最大key
            return false;
        }
        BPlusTreeNode node = this.root;
        int i, new_key;
        boolean key_found = false;
        Stack<BPlusTreeNode> nodeStack = new Stack<BPlusTreeNode>();
        Stack<Integer> indexStack = new Stack<Integer>();

        nodeStack.push(null);
        indexStack.push(0);
        //System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));

        for (;;) {
            for (i = 0; node.key[i] < key; i++)
                ;
            //top++;
            //System.out.println("top="+top);
            nodeStack.push(node);
            indexStack.push(i);
            //System.out.println("nodeStack.top:"+(nodeStack.size()-1)+",indexStack.top:"+(indexStack.size()-1));

            if (node.isLeaf == false) {// 中间结点
                node = node.children[i];
            } else {
                if (node.key[i] == key) {// 找到
                    //System.out.println("找到关键字");
                    key_found = true;
                    node.num--;
                    for (; i < node.num; i++) {// 删除k[i]
                        node.key[i] = node.key[i + 1];
                        node.addr[i] = node.addr[i + 1];
                        node.children[i] = node.children[i + 1];
                    }
                    new_key = node.key[node.num - 1];

                    // 修改上级结点的key
                    //System.out.println("修改上级结点的key");
                    //i = top - 1;
                    i = nodeStack.size()-2;//top == nodeStack.size()-1;
                    BPlusTreeNode node_i= nodeStack.elementAt(i);
                    BPlusTreeNode node_i1= nodeStack.peek();//恰好i+1==nodeStack.size()-1;
                    int index_i = indexStack.elementAt(i);
                    int index_i1 = indexStack.peek();
                    if (i > 0 && node_i.key[index_i] == key
                            && node_i1.num == index_i1) {

                        node_i.key[index_i] = new_key;
                        //i = top - 2;
                        for (i--; i > 0; i--) {
                            node_i = nodeStack.elementAt(i);
                            node_i1 = nodeStack.elementAt(i + 1);
                            index_i = indexStack.elementAt(i);
                            index_i1 = indexStack.elementAt(i + 1);
                            if (node_i.key[index_i] == key
                                    && node_i1.num - 1 == index_i1) {
                                node_i.key[index_i] = new_key;
                            } else {
                                break;
                            }
                        }
                    }
                } else {// 未找到
                    //System.out.println("未找到" + key);
                    // BPlusTreeNode.print(stack[top-3],0);
                }
                break;
            }
        }
        if (key_found) {
			/*if (!this.check_node(this.root)) {
				System.out.println("ERROR_1");
			}*/
            this.check(nodeStack, indexStack);
			/*if (!this.check_node(this.root)) {
				System.out.println("ERROR_2");
			}*/
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检测结点是否需要 合并
     * @param nodeStack
     * @param indexStack
     */
    private void check(Stack<BPlusTreeNode> nodeStack, Stack<Integer> indexStack){
        BPlusTreeNode node = nodeStack.peek();
        BPlusTreeNode parent, lchild, rchild;
        int pos;

        while (node.num < (this.maxKeyCount + 1) / 2) {// 结点中元素太少
            nodeStack.pop();
            indexStack.pop();
            pos = indexStack.peek();
            parent = nodeStack.peek();
            if (parent == null) {// 根结点
                if (node.num <= 1) {
                    // 修改根结点
                    if (node.children[0] != null) {
                        this.root = node.children[0];
                        // 回收根结点
                        node = null;
                    } else {
                        if (node.num == 0) {
                            this.root = null;
                            // 回收结点
                            node = null;
                        }
                    }
                }
                break;
            }

            if (pos == 0) {// 最左
                lchild = node;
                rchild = parent.children[pos + 1];
            } else {
                pos--;
                rchild = node;
                lchild = parent.children[pos];
            }
            if (this.merge(lchild, pos, rchild, parent)) {
                //System.out.println("合并结点成功");
            } else {
                //System.out.println("平分结点中的元素");
            }
            node = parent;
        }
    }

    /**
     * 合并结点
     * @param lchild
     * @param index index为 lchild 在parent中的索引位置
     * @param rchild
     * @param parent
     */
    private boolean merge(BPlusTreeNode lchild, int index,
                          BPlusTreeNode rchild, BPlusTreeNode parent) {
        int i, j, m;
        int k = lchild.num + rchild.num;
        if (k <= this.maxKeyCount) {
            // 合并为一个结点
            System.out.println("合并为一个结点");
            // rchild中的元素合并到lchild中
            for (i = lchild.num, j = 0; j < rchild.num; j++, i++) {
                lchild.key[i] = rchild.key[j];
                lchild.addr[i] = rchild.addr[j];
                lchild.children[i] = rchild.children[j];
            }
            lchild.next = rchild.next;
            lchild.num = k;

            // 调整上级索引
            parent.num--;
            parent.key[index] = lchild.key[k - 1];
            for (i = index + 1; i < parent.num; i++) {
                parent.children[i] = parent.children[i + 1];
                parent.key[i] = parent.key[i + 1];
            }

            // 回收结点
            rchild = null;
            return true;
        } else {
            // 平分结点中的元素
            System.out.println("平分结点中的元素");
            BPlusTreeNode[] children = new BPlusTreeNode[this.maxKeyCount * 2];
            int[] key = new int[this.maxKeyCount * 2];
            int[] addr = new int[this.maxKeyCount * 2];

            // 收集
            for (i = 0; i < lchild.num; i++) {
                children[i] = lchild.children[i];
                key[i] = lchild.key[i];
                addr[i] = lchild.addr[i];
            }
            for (j = 0; j < rchild.num; i++, j++) {
                children[i] = rchild.children[j];
                key[i] = rchild.key[j];
                addr[i] = rchild.addr[j];
            }

            // 平分
            m = k >> 1;// m = (lchild->n + rchild->n) / 2
            for (i = 0; i < m; i++) {
                lchild.children[i] = children[i];
                lchild.key[i] = key[i];
                lchild.addr[i] = addr[i];
            }
            lchild.num = m;

            for (j = 0; i < k; i++, j++) {
                rchild.children[j] = children[i];
                rchild.key[j] = key[i];
                rchild.addr[j] = addr[i];
            }
            rchild.num = k - m;

            // 调整上级索引
            parent.key[index] = key[m - 1];
            return false;
        }
    }

    /**
     * 得到数的高度
     * @return
     */
    public int getHeight(){
        int h=0;
        BPlusTreeNode node = this.root;
        while(node!=null){
            node = node.children[0];
            h++;
        }
        return h;
    }

    /**
     * 得到BPlusTreeNode结点个数
     * @return
     */
    public int getNodeTotalCount(){
        return getNodeCount(this.root);
    }

    /**
     * 得到以node为跟的子树的节点数
     * @param node
     * @return
     */
    private int getNodeCount(BPlusTreeNode node){
        int count = 0;
        if(node==null){
            return 0;
        }else if(node.isLeaf){
            return 1;
        }else{
            for(int i=0;i<node.num;i++){
                count += getNodeCount(node.children[i]);
            }
            count++;
        }
        return count;
    }

    /**
     * 得到叶子结点个数
     * @return
     */
    public int getLeafNodeCount(){
        int leafCount = 0;
        BPlusTreeNode node = this.first;

        while (node!=null) {
            leafCount++;
            node = node.next;
        }
        return leafCount;
    }

    /**
     * 得到关键字个数，即叶子结点上的所有关键字个数总和
     * @return
     */
    public int getKeyTotalCount(){
        int keyCount = 0;
        BPlusTreeNode node = this.first;

        while (node!=null) {
            keyCount+=node.num;
            node = node.next;
        }
        return keyCount;
    }

    /**
     * 展示这棵B+树
     */
    @Deprecated
    public void showTree(){
        if(root!=null)
            BPlusTreeNode.show(root, 0);
    }

    /**
     * 纵向输出树结构
     */
    @Deprecated
    public void print_tree(){
        // System.out.println();
        if(this.root!=null)
            BPlusTreeNode.print(this.root,0);
        // System.out.println();
    }

    /**
     * 横向输出树结构
     */
    public void print_tree_2(){
        // System.out.println();
        if(this.root!=null)
            BPlusTreeNode.print_2(this.root,0);
        //System.out.println();
    }

    /**
     * 类似print_2，只是转化为字符串
     */
    public String toString(){
        if(this.root!=null)
            return this.root.toString();
        else
            return "null";
    }

    /**
     * 输出叶子结点层
     */
    public void print_leaf_layer() {
        int i;
        BPlusTreeNode node = this.first;

        while (node!=null && node.next!=null) {
            for (i = 0; i < node.num; i++) {
                System.out.print("("+node.key[i]+","+node.addr[i]+"),");
            }
            node = node.next;
        }
        if (node != null) {
            for (i = 0; i < node.num - 1; i++) {
                System.out.print("(" + node.key[i] + "," + node.addr[i] + "),");
            }
            System.out.println("(" + node.key[i] + "," + node.addr[i] + ")");
        }
    }
}


class BPlusTreeNode {
    private static final long serialVersionUID = 1L;

    static int index = 0;
    int position; // 记录结点所处的索引的序号

    int max; //B+树的阶数,即结点中(子结点,关键字)的最大数目
    /* (子结点,关键字数目相同) */
    boolean isLeaf;	// 是否是叶结点
    int num;	// 关键字个数,最大为max个
    int[] key;	// num个关键值
    int[] addr;	// 关键值对应的数据指针()
    /* 结点指针 */
    BPlusTreeNode[] children;	// fork下级结点,子结点。若子结点已在内存时，方便引用
    BPlusTreeNode next;			// 同层的下一结点,便于遍历

    /**
     * BTNode构造函数
     * @param m BTNode的阶数
     */
    public BPlusTreeNode(int m) {
        position = index++;
        max = m;

        isLeaf = true;
        num = 0;

        key = new int[max];
        addr = new int[max];
        children = new BPlusTreeNode[max];
        for (int i = 0; i < max; i++) {
            key[i] = -1;
            addr[i] = -1;
            children[i] = null;
        }
        next = null;
    }

    /**
     * 打印出this结点信息树
     *
     * @param depth
     */
    @Deprecated
    public void show(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("    ");
        }
        System.out.print("node[" + this.position + "]:{");
        for (int i = 0; i < this.num; i++) {
            System.out.print("(" + this.key[i] + "," + this.addr[i] + ")");
        }
        System.out.println("}");
        for (int i = 0; i < this.num; i++) {
            if (this.children[i] != null)
                this.children[i].show(depth + 1);
        }
    }

    /**
     * 打印出结点信息树:与node.show(depth)功能相差无异
     *
     * @param node
     * @param depth
     */
    @Deprecated
    public static void show(BPlusTreeNode node, int depth) {
        if (node == null)
            return;
        for (int i = 0; i < depth; i++) {
            System.out.print("    ");
        }
        System.out.print("node[" + node.position + "]:{");
        for (int i = 0; i < node.num; i++) {
            System.out.print("(" + node.key[i] + "," + node.addr[i] + ")");
        }
        System.out.println("}");
        for (int i = 0; i < node.num; i++) {
            show(node.children[i], depth + 1);
        }
    }

    /**
     * 纵向输出结点
     * @param node
     * @param level
     */
    @Deprecated
    public static void print(BPlusTreeNode node, int level) {
        int i, j;
        if (node.isLeaf) {
            for (i = 0; i < node.num; i++) {
                for (j = 0; j < level; j++) {
                    System.out.print(" ");
                }
                System.out.println("("+node.key[i]+","+node.addr[i]+")");
            }
        } else {
            for (i = 0; i < node.num/*-1*/; i++) {
                print(node.children[i], level + 4);
                for (j = 0; j < level; j++) {
                    System.out.print(" ");
                }
                System.out.println(node.key[i]);
            }
        }
    }

    /**
     * 类似print_2，只是转化为字符串
     */
    public String toString(){
        return this.toString(0);
    }
    /*递归方法:供toString调用*/
    private String toString(int level){
        StringBuffer str = new StringBuffer(1024);
        int i, j;
        if (this.isLeaf) {
            for (j = 0; j < level; j++) {
                str.append(" ");
            }
            str.append("{");
            for (i = 0; i < this.num - 1; i++) {
                str.append("("+this.key[i]+","+this.addr[i]+"),");
            }
            str.append("("+this.key[i]+","+this.addr[i]+")}\n");
        } else {
            for (j = 0; j < level; j++) {
                str.append(" ");
            }
            str.append("{");
            for (i = 0; i < this.num - 1; i++) {
                str.append(this.key[i]+",");
            }
            str.append(this.key[i] + "}\n");
            for (i = 0; i < this.num/*-1*/; i++) {
                str.append(this.children[i].toString(level + 4));
            }
        }
        return str.toString();
    }

    /**
     * 横向输出结点
     * @param node
     * @param level
     */
    public static void print_2(BPlusTreeNode node, int level) {
        int i, j;
        if (node.isLeaf) {
            for (j = 0; j < level; j++) {
                System.out.print(" ");
            }
            System.out.print("{");
            for (i = 0; i < node.num - 1; i++) {
                System.out.print("("+node.key[i]+","+node.addr[i]+"),");
            }
            System.out.println("("+node.key[i]+","+node.addr[i]+")}");
        } else {
            for (j = 0; j < level; j++) {
                System.out.print(" ");
            }
            System.out.print("{");
            for (i = 0; i < node.num - 1; i++) {
                System.out.print(node.key[i]+",");
            }
            System.out.println(node.key[i] + "}");
            for (i = 0; i < node.num; i++) {
                print_2(node.children[i], level + 4);
            }
        }
    }
}
