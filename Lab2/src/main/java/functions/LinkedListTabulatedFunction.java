package functions;

import exceptions.InterpolationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable, Serializable {
    @Serial
    private static final long serialVersionUID = -2027113432870045149L;
    private static class Node implements Serializable{
        @Serial
        private static final long serialVersionUID = -4575913742387339332L;
        public Node next;
        public Node prev;
        public double xValue;
        public double yValue;
        public Node(double xValue, double yValue){
            this.xValue = xValue;
            this.yValue = yValue;
        }
    }
    private Node head;
    private void addNode(double xValue, double yValue){
        Node newNode = new Node(xValue, yValue);
        if (head == null){
            head = newNode;
            head.next = head;
            head.prev = head;
        }
        else{
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
        count++;
    }
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues){
        if (xValues.length < 2){
            throw new IllegalArgumentException("At least 2 points required");
        }

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        for (int i = 0; i<xValues.length; ++i){
            addNode(xValues[i], yValues[i]);
        }
    }
    public LinkedListTabulatedFunction(MathFunction source, double xForm, double xTo, int count){
        if (count < 2){
            throw new IllegalArgumentException("At least 2 points required");
        }
        double start, end;
        if (xForm > xTo){
            start = xTo;
            end = xForm;
        }
        else{
            start = xForm;
            end = xTo;
        }
        double step = (end - start) / (count - 1);
        for(int i = 0; i < count; i++){
            double x = start + i * step;
            double y = source.apply(x);
            addNode(x, y);
        }
    }
    private Node getNode(int index){
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }
    @Override
    public double getX(int index){
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        return getNode(index).xValue;
    }
    @Override
    public double getY(int index){
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        return getNode(index).yValue;
    }
    @Override
    public void setY(int index, double value){
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        getNode(index).yValue = value;
    }
    @Override
    public int indexOfX(double x){
        Node current = head;
        for(int i = 0; i < count; i++){
            if (Math.abs(current.xValue - x) < 1e-12)
                return i;
            current = current.next;
        }
        return -1;
    }
    @Override
    public int indexOfY(double y){
        Node current = head;
        for(int i = 0; i < count; i++){
            if (Math.abs(current.yValue - y) < 1e-12)
                return i;
            current = current.next;
        }
        return -1;
    }
    @Override
    public double leftBound(){
        return head.xValue;
    }
    @Override
    public double rightBound(){
        return head.prev.xValue;
    }
    @Override
    protected int floorIndexOfX(double x){
        if (x < leftBound()){
            throw new IllegalArgumentException("The value is less than the left bound");
        }
        if (x > head.prev.xValue){
            return count;
        }
        Node current = head;
        for (int i = 0; i < count - 1; i++){
            if (current.xValue <= x && current.next.xValue > x){
                return i;
            }
            current = current.next;
        }
        if (Math.abs(current.next.xValue - x) > 1e-12){
            return count - 1;
        }
        return -1;
    }
    @Override
    protected double extrapolateLeft(double x){
        return interpolate(x, head.xValue, head.next.xValue, head.yValue, head.next.yValue);
    }
    @Override
    protected double extrapolateRight(double x){
        Node last = head.prev;
        Node secondLast = last.prev;
        return interpolate(x, secondLast.xValue, last.xValue, secondLast.yValue, last.yValue);
    }
    @Override
    protected double interpolate(double x, int floorIndex){
        Node leftNode = getNode(floorIndex);
        Node rightNode = leftNode.next;

        if (x <  leftNode.xValue || x > rightNode.xValue) {
            throw new InterpolationException("X is outside the interpolation interval");
        }

        return interpolate(x, leftNode.xValue, rightNode.xValue, leftNode.yValue, rightNode.yValue);
    }
    @Override
    public int getCount(){
        return count;
    }
    @Override
    public void remove(int index){
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        Node nodeToRemove = getNode(index);
        if (count == 1){
            head = null;
        }
        else{
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;
            if (nodeToRemove == head){
                head = head.next;
            }
        }
        count--;
    }

    @Override
    public void insert(double x, double y) {
        if (head == null) addNode(x, y);

        int index = indexOfX(x);

        if (index != -1) {
            setY(index, y);
            return;
        }

        if (x < head.xValue) {
            Node newNode = new Node(x, y);
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
            head = newNode; // переносим голову
            count++;
            return;
        }

        if (x > head.prev.xValue) {
            Node newNode = new Node(x,y);
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
            count++;
            return;
        }

        int i = floorIndexOfX(x);
        Node newNode = new Node(x, y);
        Node mergeNode = getNode(i);
        newNode.next = mergeNode.next;
        newNode.prev = mergeNode;
        mergeNode.next.prev = newNode;
        mergeNode.next = newNode;
        ++count;
    }
    @Override
    public Iterator<Point> iterator(){
        return new Iterator<Point>() {
            private Node currentNode = head;
            private int countPassed = 0;
            @Override
            public boolean hasNext(){
                return countPassed < getCount();
            }
            @Override
            public Point next(){
                if(!hasNext()){
                    throw new NoSuchElementException();
                }
                Point point = new Point(currentNode.xValue, currentNode.yValue);
                currentNode = currentNode.next;
                countPassed++;
                return point;
            }
        };
    }
}
