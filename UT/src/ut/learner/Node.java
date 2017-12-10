package ut.learner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Node<T> {
	private T data = null;
	private List<Node<T>> children = new ArrayList<Node<T>>();
	
	private Node<T> parent = null;
	
	public Node(T data){
		this.data = data;
	}
	
	public Node<T> addChild(Node<T> child){
		child.setParent(this);
		this.children.add(child);
		return child;
	}
	
	public void addChildren(List<Node<T>> children){
		children.forEach(each->each.setParent(this));
		this.children.addAll(children);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<Node<T>> getChildren() {
		return children;
	}

	public void setChildren(List<Node<T>> children) {
		this.children = children;
	}

	public Node<T> getParent() {
		return parent;
	}

	public void setParent(Node<T> parent) {
		this.parent = parent;
	}
		
	
}
