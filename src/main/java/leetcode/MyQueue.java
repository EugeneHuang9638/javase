package leetcode;

import java.util.Stack;

/**
 *
 * 使用栈实现队列的下列操作：

 * push(x) -- 将一个元素放入队列的尾部。
 * pop() -- 从队列首部移除元素。
 * peek() -- 返回队列首部的元素。
 * empty() -- 返回队列是否为空。
 *
 * 示例:
 *
 * MyQueue queue = new MyQueue();
 *
 * queue.push(1);
 * queue.push(2);
 * queue.peek();  // 返回 1
 * queue.pop();   // 返回 1
 * queue.empty(); // 返回 false
 *
 * 说明:
 *
 * 你只能使用标准的栈操作 -- 也就是只有 push to top, peek/pop from top, size, 和 is empty 操作是合法的。
 * 你所使用的语言也许不支持栈。你可以使用 list 或者 deque（双端队列）来模拟一个栈，只要是标准的栈操作即可。
 * 假设所有操作都是有效的 （例如，一个空的队列不会调用 pop 或者 peek 操作）。
 *
 *
 *
 *
 * 分析:
 *   因为队列是FIFO(Fast In Fast Out)
 *   而栈是FILO(Fast In Last Out)
 *
 *   所以我们可以用两个栈来实现队列，
 *   当入队列时，把元素放置input栈中
 *   当要出队列时，若output栈中无元素，则遍历input中的元素，按input的出栈顺序压入output中
 *   若output中有元素(肯定是按照FIFO的顺序)，则直接出栈
 *
 */
public class MyQueue {

    // 队列入队时，放进input中
    private Stack<Integer> input = new Stack<>();

    // 队列出队时，从output中出队
    private Stack<Integer> output = new Stack<>();


    private int size;


    /** Initialize your data structure here. */
    public MyQueue() {

    }

    /** Push element x to the back of queue. */
    public void push(int x) {
        input.push(x);
        size++;
    }

    /** Removes the element from in front of queue and returns that element. */
    public int pop() {
        populateInput();

        size--;

        return output.pop();
    }

    /** Get the front element. */
    public int peek() {
        populateInput();
        return output.peek();
    }

    /** Returns whether the queue is empty. */
    public boolean empty() {
        return size == 0;
    }

    private void populateInput() {
        if (output.size() == 0) {
            // 将input中的元素全部出栈，并入output栈
            while (input.size() > 0) {
                Integer element = input.pop();
                output.push(element);
            }
        }
    }

    public static void main(String[] args) {
        MyQueue queue = new MyQueue();
        queue.push(1);
        queue.push(2);

        // 返回 1
        System.out.println(queue.peek());

        // 返回 1
        System.out.println(queue.pop());

        // 返回 false
        System.out.println(queue.empty());

    }
}
