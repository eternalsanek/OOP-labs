package functions;

class Node {
    public Node next;
    public Node prev;
    public double xValue;
    public double yValue;
    public Node(double xValue, double yValue){
        this.xValue = xValue;
        this.yValue = yValue;
        this.prev = null;
        this.next = null;
    }
}
