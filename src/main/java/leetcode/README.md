# Leetcode之旅

## 一、突发事件

* 因在**2020-05-04**日的leetcode 的每日一题[跳跃游戏 II](https://leetcode-cn.com/problems/jump-game-ii) 中, 备受打击，虽然知道它也是一道动态规划类型的题，但是还是找不出他们的递推关系，所以决定温故下数据结构的相关知识点，包括时间、空间复杂度的运算，所以leetcode的每日一题的打卡暂停一段时间。

## 二、数据结构算法的基础知识

* **注意事项：Github不支持数学表达式以及对章节的链接不太友好，建议使用Typeora打开此README.md文件查看内容**

### 2.1 时间复杂度的表现形式

* 表格

  | 表达式 | 类别                                               | 对应代码案例                                     |
  | :----: | :------------------------------------------------: | ------ |
  |  O(1)  | `常数阶`：不管是O(1), O(2), O(3)都会统一用O(1)来表示 | [传送门](####2.2.1 常数阶O(1)) |
  |  O(n)  | `线性阶`：一般遍历一些线程结构时，会使用到线性阶 | [传送门](####2.2.2 线性阶O(n)) |
  | O(n!) | `阶乘阶`：代码块要重复执行n的阶乘次 | [传送门](####2.2.3 阶乘阶O(n!)) |
  | O($$n^2$$) | `平方阶`：一般是两个线性的数据结构进行嵌套循环 | [传送门](####2.2.4 平方阶O($$n^2$$)) |
  | O($$n^3$$) | `立方阶`：一般是三个线程的数据结构进行嵌套循环 | [传送门](####2.2.5 立方阶O( $$n^3$$)) |
  | O($2^n$) | `指数阶`：还不清楚属性哪些数据结构 |   [传送门](####2.2.6 指数阶O($2^n$))    |
  | O($$log_2N$$) | `对数阶`：一般是以2为底 | [传送门](####2.2.7 对数阶O($$log_2N$$)) |
  

### 2.2 对应复杂度及其案例

#### 2.2.1 常数阶O(1)

* 代码示例

  ```java
  int n = 100;
  System.out.println(n);
  ```

#### 2.2.2 线性阶O(n)

* 代码示例

  ```javascript
  // 代码块要执行n次
  for (int i = 0; i < n; i++) {
      System.out.println(i);
  }
  ```

#### 2.2.3 阶乘阶O(n!)

* 代码示例

  ```java
  // 代码块要执行n!次
  for (int i = 0; i < factorial(n); i++) {
      System.out.println(i);
  }
  ```

#### 2.2.4 平方阶O($$n^2$$)

* 代码示例

  ```java
  // 要对数组操作n次，
  for (int i = 0; i < n; i++) {
      // 针对数组中的i还要计算一次数组的长度
      for (int j = 0; j < n; j++) {
          System.out.println("i: " + i + ", j = " + j);
      }
  }
  ```

#### 2.2.5 立方阶O( $$n^3$$)

* 代码示例

  ```java
  
  for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
          for (int k = 0; k < n; k++) {
              System.out.println("i: " + i + ", j = " + j, + ", k = " + k);            
          }
      }
  }
  ```

#### 2.2.6 指数阶O($2^n$)

* 代码示例

  ```java
  // Math.pow(2, n)的表达式就是求2的n次方的值，所以循环内的代码块执行了2的n次方
  for (int i = 0; i < Math.pow(2, n); i++) {
      System.out.println(i);
  }
  ```

#### 2.2.7 对数阶O($$log_2N$$)

* 代码示例

  ```java
  // 根据循环条件可知：代码块执行时的i能取的值为:
  // 1, 2, 4, 8, 16, 32.....
  // 对应的值为
  // ==> 2的0次方， 2的一次方， 2的平方，2的三次方。。。。。
  // ==> log以2为底1的对数等于为0， log以2为底2的对数的值为1
  // 具体见下表
  for (int i = 1; i < n; i = i * 2) {
      System.out.println(i); 
  }
  ```

  |  i   | 指数表达式 |         对数表达式          |             表达式汇总             |
  | :--: | :--------: | :-------------------------: | :--------------------------------: |
  |  1   |  $$2^0$$   | $$log_2i$$ = $$log_21$$ = 0 | $$2^{log_21}$$  ===> 其实就是i的值 |
  |  2   |  $$2^1$$   | $$log_2i$$ = $$log_22$$ = 1 | $$2^{log_22}$$  ===> 其实就是i的值 |
  |  4   |  $$2^2$$   | $$log_2i$$ = $$log_24$$ = 2 | $$2^{log_24}$$  ===> 其实就是i的值 |

### 2.3 时间复杂度实战与优化

#### 2.3.1 等差数列

* 示例

  ```java
  // 题目: 求1 + 2 + 3 + ……………… + n
  // 时间复杂度为： O(n)的解法
  int count = 0;
  for (int i = 1; i <= n; i++) {
      count += i;
  }
  System.out.println(count);
  
  // ========================优化后=====================
  // 我们都知道等差数列的求和是有一个公司的
  // n(n + 1) / 2
  // 所以我们可以用公司来计算上述的答案， 
  // 代码如下:   ===> 时间复杂度瞬间降为了O(1)
  int count = n(n + 1) / 2;
  System.out.println(count);
  
  // 我们知道程序运算会占用机器的内存的，若我们可以减少一些算法的时间
  // 复杂度和空间复杂度，这样的话就可以减少机器的配置
  // 这不就是间接的省了一大笔钱吗？
  ```

#### 2.3.2 斐波拉契数列

* 示例

  ```java
  // 给定一个数组：1, 1, 2, 3, 5, 8, 13, 21
  // 求第n个数
  // 通常的解法为使用递归，具体代码如下:
  public int fib(int n) {
      if (n == 0 || n == 1) {
          return 1;
      }
      
      return f(n - 1) + f(n - 2);
  }
  // 代码虽然只有简单的几行，但是它消耗的时间复杂度却非常的多，几乎达到了指数阶的程度
  // 假设我们要求f(5), 具体需要调用的结果如下图所示:
  ```

  ![fib求fib(5)流程.png](./fib求fib(5)流程.png)

### 2.4 常见算法的时间复杂度

* 表格

  |  查找算法  |  时间复杂度   |
  | :--------: | :-----------: |
  |  二分查找  | $$O(log_2n)$$ |
  | 二叉树遍历 |     O(n)      |


### 2.5 常见数据结构之Hash表

* 基础知识：

  ```txt
  所谓hash表，即是用key算出它的hash值，然后对表中的长度取模，最终得到一个值，此值就是该key对应的下标。所以使用此算法来存储值的话，那么必然会存在如下的几个问题：
  1. Hash碰撞: 所谓hash碰撞即不同的key最终算出来存储在表中的index是一样的，而上述所谓的表其实就是一个数组，数组中的每个下标只能存储一个值。所以要处理hash碰撞的情况的话，那么就必须在数组中的每个元素中以链表的形式来存值，
  即所谓的拉链法(数组 + 链表)
  2. 若产生了Hash碰撞，并且这个key之前已经设置过了，如果要采取覆盖的策略。此时我们要重写equals方法来判断key是否相等。
  
  综上所述，我们似乎已经能明白为什么要重写hash和equals方法了吧？
  就拿Hash表来说，我们需要hash来计算它要防止那一块内存中，同时还要使用equals来判断链表中是否有相同的key
  ```

* hash表原理图

  ![hash表原理图](./hash表原理.png)

### 2.6 二叉查找树的由来

* 原理图：

  ![二叉查找树的由来.png](./二叉查找树的由来.png)

## 三、每日打卡leetcode
* 规则: 根据leetcode的每日一题进行打卡

* 导航栏

    | 类名                          |                         对应算法题目                         | 数据结构类型   | 难度 |  完成时间  |                             总结                             |
    | :---------------------------- | :----------------------------------------------------------: | -------------- | :--: | :--------: | :----------------------------------------------------------: |
    | MergeTwoSortedLists.java      | [合并两个有序链表](https://leetcode-cn.com/problems/merge-two-sorted-lists) | 链表           | 简单 | 2020/05.01 | 1. 一开始的思想居然是把两个链表合并成一个链表，然后对他迭代排序，到最后因为对象引用的处理不当，一直卡壳。最后直接看了官方的答案。<br>2.看了官方的答案后知道了有递归和迭代的两种方式实现。针对递归，一开始看的是云里雾里的，最后自己在笔记本上用笔画出了每个调用过程以及结合了递归的基本思想(`递规的基本思想是把一个大型复杂的问题层层转化为一个与原问题相似的规模较小的问题来求解`)进行了理解，也算是基本理解了。<br>3.通过迭代的方式，这和自己一开始的思想是一样的。只不过自己画蛇添足了。看了官方的迭代方式的答案后，一直有一个问题困扰着自己，就是preNode节点一开始的引用是指向哑结点preHead的，但是后续它明明修改了引用，那为什么还能操作到preHead呢？后来才发现是自己基础不足，把链表当成一个list对象了，以为整个链表都是一个对象。但是实际上每个链表都是由一个个节点组成的，每个节点就只有那么几个属性: **val、next**，其中next存储的下一个节点的引用。preNode一开始操作的是preHead节点后续都是操作preHead的next节点以及preHead的next节点的next节点。 |
    | ReverseList.java              |                           链表反转                           | 链表           | 简单 | 2020/05/04 | 非leetcode 2020/05/04的每日一题，是自己在温故算法知识时，遇到了这么一道题，所以就写一下。具体可参考`ReverseList.java类` |
    | SwapPairs.java                | [两两交换链表中的节点](https://leetcode-cn.com/problems/swap-nodes-in-pairs/) | 链表           | 中等 | 2020/05/05 | 非leetcode 2020/05/05的每日一题，是自己在温故算法知识时，遇到了这么一道题。具体参考`SwapPairs.java类` |
    | HasCycle.java                 | [环形链表](https://leetcode-cn.com/problems/linked-list-cycle/) | 链表           | 简单 | 2020/05/07 | 继续做链表相关的题，从leetcode中找了一个判断链表是否有环的简单题目。具体参考`HasCycle.java`类 |
    | DeleteDuplicates.java         | [删除排序链表中的重复元素 II](https://leetcode-cn.com/problems/remove-duplicates-from-sorted-list-ii/) | 链表           | 中等 | 2020/06/15 | 给定一个排序链表，删除所有含有重复数字的节点，只保留原始链表中 *没有重复出现* 的数字。(`leetcode 82`题)，详见`DeleteDuplicates.java`类 |
    | DeleteNNumberNode             |                      删除倒数第n个元素                       | 链表           | 中等 | 2024/05/11 | 快慢指针：快指针先走n+1步。然后再遍历快指针，当快指针遍历完链表后，慢指针就是倒数第n+1个节点，直接用链表删除元素的方式把下一个节点删掉即可。 |
    | ReturnMiddleNode.java         |                  给定一个链表，返回中间节点                  | 链表           | 中等 | 2024/05/11 | 快慢指针：快指针走两步，慢指针走一步。当快指针遍历完链表后。慢指针就是中间的元素了。 |
    | LengthOfLongestSubstring.java | [无重复字符的最长子串](https://leetcode-cn.com/problems/longest-substring-without-repeating-characters) | 字符串         | 中等 | 2020/05/02 | 1. 自己的思路：将字符串转成数组，针对每个字符查找出它的`无重复字符的子串`，并使用一个变量用来记录`无重复字符子串的最大长度`。此方法应该是比较常规的思路了。缺点是效率比较低。基本上时间复杂度为O(m * (m -1))，m为字符串的长度<br>2. 官网推荐的`滑动窗口`解法，可以说是一个模板吧，以后这种解决**无重复字符的子串**都可以使用此种方法进行解题。大致思路为：遍历字符串的每个字符，获取每个字符对应的`无重复字符的子串`作为一个`滑动窗口`，但是这里和上面的解法有个区别。因为滑动窗口中的字符串已经是一个**无重复字符的子串**了，所以后续的操作都是对`滑动窗口`而言，因为`滑动窗口`的第一个元素就是遍历字符串数组的当前字符，当进行下一轮遍历时，要把滑动窗口的第一个字符给去掉，以及获取`滑动窗口`的最后一个字符的index，然后再从字符串的index + 1的位置上开始处理字符，若字符不在`滑动窗口`中,那么此字符应该加入到`滑动窗口`中, 作为当前处理字符的**无重复字符子串**的一个元素。同时也要定义一个变量存储**无重复字符的子串**的最大长度，每次产生新的**无重复字符的子串**时，再去长度最大的那个。 |
    | MaxSubArray.java              | [最大子序和](https://leetcode-cn.com/problems/maximum-subarray) | 动态规划       | 简单 | 2020/05/03 | `官网虽然给的评级是简单，但是我没有做出来！！！`，后来我在官网的评论中找到了一个大神的提示: [传送门](https://leetcode-cn.com/problems/maximum-subarray/solution/zhen-zheng-li-jie-on-jie-fa-ben-zhi-shi-dong-tai-g/)，然后结合动态规划的基本思想**(动态规划算法通常用于求解具有某种最优性质的问题。在这类问题中，可能会有许多可行解。每一个解都对应于一个值，我们希望找到具有最优值的解)**得到了一个动态规划的解题流程(具体详见`MaxSubArray.java`类) |
    | JumpGame2.java                | [跳跃游戏 II](https://leetcode-cn.com/problems/jump-game-ii) | 动态规划       | 困难 | 2020/05/04 | 哇，做这道算法题时简直心态爆炸了。赶紧去恶补一下数据结构再！ |
    |                               |                                                              |                |      |            |                                                              |
    |                               |                                                              |                |      |            |                                                              |
    |                               |                                                              |                |      |            |                                                              |
    | ValidString.java              | [有效的括号](https://leetcode-cn.com/problems/valid-parentheses/) | 栈             | 简单 | 2020/05/08 | 做关于栈的题目，经典题目: 判断一个字符串是否有合法的括号。具体参考`ValidString.java` |
    | MyQueue.java                  | [用栈实现队列](https://leetcode-cn.com/problems/implement-queue-using-stacks/) | 栈             | 简单 | 2020/05/08 |   用栈实现一个队列，leetcode232题。具体参考`MyQueue.java`    |
    | MyStack.java                  | [用队列实现栈](https://leetcode-cn.com/problems/implement-stack-using-queues/) | 队列           | 简单 | 2020/05/08 | 用队列实现一个栈，leetcode的225题。具体参考`MyStack.java`类  |
    | KthLargest.java               | [数据流中的第K大元素](https://leetcode-cn.com/problems/kth-largest-element-in-a-stream/) | 队列(优先队列) | 简单 | 2020/05/09 | 设计一个类，找出数据流中的第k大的元素。具体参考`KthLargest.java`类 |
    | MaxSlidingWindow.java         | [滑动窗口最大值](https://leetcode-cn.com/problems/sliding-window-maximum/) | 滑动窗口       | 困难 | 2020/05/10 |       leetcode239题。具体参考`MaxSlidingWindow.java`类       |
    | MaxSlidingWindow.java         | [滑动窗口最大值](https://leetcode-cn.com/problems/sliding-window-maximum/) | 滑动窗口       | 困难 | 2020/05/11 | 继续昨天的**滑动窗口最大值**题目，目前时间复杂度比较差，在leetcode中执行的时间比较长，但结果是正确的。`优化方面日后再做吧！` |
    | ValidAnagram.java             | [有效的字母异位词](https://leetcode-cn.com/problems/valid-anagram/) | 哈希表         | 简单 | 2020/05/12 |        leetcode`242`题。具体参考`ValidAnagram.java`类        |
    | TwoSum.java                   |    [两数之和](https://leetcode-cn.com/problems/two-sum/)     | 哈希表         | 简单 | 2020/05/12 |           leetcode第一题。具体参考`TwoSum.java`类            |
    | ThreeSum.java                 |      [三数之和](https://leetcode-cn.com/problems/3sum/)      | 哈希表         | 中等 | 2020/05/13 | leetcode第15题。具体参考`ThreeSum.java`类。在leetcode的运行结果中，有一种case为输出一个空数组，但是leetcode 的期望结果是一个空数组，但是方法的返回值是LIst<List<Integer>>,所以没法输出它期待的打印结果。不过没关系，解题思路有了就行了！此提的解题思路和上述的`两数之和`类似，先变量表达式的前面部分，最后一部分从map(key为数组的value，value为数组的index)中去查找，同时要注意map中value的重复使用，同时还要注意拆箱过程中的空指针异常。 |
    | TraverseTree.java             |                    自己写的遍历二叉查找树                    | 树             | 简单 | 2020/05/13 | 自己使用递归实现了二叉查找树的先序遍历、中序遍历、后序遍历的算法。集体参考`TraverseTree.java`类 |
    | ValidBST.java                 | [验证二叉搜索树](https://leetcode-cn.com/problems/validate-binary-search-tree/) | 树             | 中等 | 2020/05/14 | 验证一颗树是不是二叉查找树。leetcode 98题。具体参考`ValidBST.java`类 |
    | BSTLowestCommonAncestor.java  | [二叉搜索树的最近公共祖先](https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | 树             | 简单 | 2020/06/11 | 给定一个二叉搜索树, 找到该树中两个指定节点的最近公共祖先。leetcode 235题。具体参考**BSTLowestCommonAncestor.java**类 |
    | BTLowestCommonAncestor.java   | [二叉树的最近公共祖先](https://leetcode-cn.com/problems/lowest-common-ancestor-of-a-binary-tree/) | 树             | 中等 | 2020/06/11 | 给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。leetcode 236题。具体参考`BTLowestCommonAncestor.java`类 |
    | PowerOfNumber.java            |                         求2的幂次方                          | 分治法         | 简单 | 2020/06/11 |               给定一个数m和n，返回的m的n次方。               |
    | MaxProfit.java                | [买卖股票的最佳时机 II](https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-ii/) | 贪心算法       | 简单 | 2020/06/11 | 买卖股票的最佳时机 II。leetcode的 122题。具体参考 `MaxProfit.java`类 |
    | BreadthFirstSearch.java       |                         广度优先搜索                         | 树             | 简单 | 2020/06/12 | 实现一颗二叉树的广度优先搜索算法。以及实现一种特殊顺序的广度优先算法(`leetcode 103`)。 |
    | DeepFirstSearch.java          |                         深度优先搜索                         | 树             | 简单 | 2020/06/12 |              实现一颗二叉树的深度优先搜索算法。              |
    | BinaryTreeLevelOrder.java     | [二叉树的层序遍历](https://leetcode-cn.com/problems/binary-tree-level-order-traversal/) | 树             | 中等 | 2020/06/14 | 对二叉树实现广度搜索算法。详见BinaryTreeLevelOrder.java`类(`leetcode 102题`) |
    |                               |                                                              |                |      |            |                                                              |

