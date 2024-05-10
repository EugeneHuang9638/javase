package leetcode.model;

public class ListNode {

    public int val;
    public ListNode next;

    public ListNode(int val) {
        this.val = val;
    }

//    @Override
//    public String toString() {
//        return "ListNode{" +
//                "val=" + val +
//                ", next=" + next +
//                '}';
////        StringBuilder sb = new StringBuilder();
////        sb.append(this.val);
////        ListNode currentNode = this;
////        while (currentNode.next != null) {
////            currentNode = currentNode.next;
////            sb.append("->").append(currentNode.val);
////        }
////        return sb.toString();
//    }
}
