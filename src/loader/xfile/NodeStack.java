package loader.xfile;

import java.util.Stack;

public class NodeStack {
	static final String symbol="{";
	Stack<Object> stack=new Stack<Object>();
	
	public boolean isBegin(String line) {
		if (line.endsWith("{")) {
			return true;
		}
		return false;
		
	}
	public void push(Object node) {
		stack.push(node);
	}
	public boolean isEmpty() {
		
		return stack.isEmpty();
		
	}
	public void clear() {
		stack.clear();
	}
	public void beginWith(Object node) {
		stack.clear();
		stack.push(node);
	}
	public Object pop() {
		return stack.pop();
	}
	public Object peek() {
		if(!stack.isEmpty())
			return stack.peek();
		return null;
	}
	public boolean isEnd(String line) {
		if (line.charAt(0)=='}') {
			return true;
		}
		return false;
		
	}
	public boolean  checkPush(String line) {
		if (line.endsWith("{")) {
			stack.push(symbol);
			return true;
		}
		return false;
		
	}
	public boolean checkPop(String line) {
		if (line.length()==0) {
			return false;
		}
		if (line.charAt(0)=='}') {
			stack.pop();
			if(stack.isEmpty())
				return true;
		}
		return false;
	}
}
