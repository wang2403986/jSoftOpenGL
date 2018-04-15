package loader.xfile;

import java.util.Stack;

public class TokenStack {
	static final String symbol="{";
	Stack<Object> stack=new Stack<Object>();
	
	public void clear() {
		stack.clear();
	}
	public void beginWith(Object node) {
		stack.clear();
		stack.push(node);
	}
	public void begin() {
		stack.clear();
		stack.push(symbol);
	}
	public boolean end(String line){
		checkPush(line);
		return checkPop(line);
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
