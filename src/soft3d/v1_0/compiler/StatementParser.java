package soft3d.v1_0.compiler;

import java.util.LinkedList;

public class StatementParser {
	StringBuilder out=new StringBuilder();
	public void println(CharSequence str){ out.append(str);  out.append("\n"); }
	public void print(CharSequence str){ out.append(str); }
	public Identifier expression2(LinkedList<Identifier> expressionStrs, Context context) {
		StringBuilder newExpressionStrs= new StringBuilder();
		Identifier current_id = null ;
		while (!expressionStrs.isEmpty()) {
			current_id = expressionStrs.removeFirst();
			if ("(".equals(current_id.name)) { // 如果是左括号,则入栈
				Identifier sub = expression(expressionStrs, null);
				if(expressionStrs.removeFirst().token==')'){
					sub.name = "("+sub.name +")";
					newExpressionStrs.append(sub.name);
				}
			} else if (is_operator(current_id)) {
				if (Tokenizer.Call == current_id.token) {// a0=a+(b*a+(c+f)*(a+b));
					LinkedList<Identifier> params = new LinkedList<>();//t0 b*a; t1,(c+f); t2,a+b; t3 ,*; b+0,t4;
					Identifier func = current_id;
					while (!")".equals(current_id.name)) {
						Context ctxt = new Context();
						Identifier param0 = expression(expressionStrs, ctxt);
						params.add(param0);
						current_id = expressionStrs.removeFirst();
					}
					Identifier pa = new Identifier();
					pa.name= "(";
					if (params.size()>0) {
						pa.name= pa.name+params.removeFirst().name;
					}
					for (Identifier i : params) {
						pa.name= pa.name+","+i.name;
					}
					pa.name=pa.name+ ")";
					newExpressionStrs.append(func.name+pa.name);
				}else if ('[' == current_id.token ) {
					if(!expressionStrs.isEmpty()&&expressionStrs.getFirst().token==']'){
						newExpressionStrs.append("[]");expressionStrs.removeFirst();
					} else {
						Identifier param0 = expression(expressionStrs, null);
						expressionStrs.removeFirst();
						newExpressionStrs.append("["+param0.name+"]");
					}
				}else if ('?' == current_id.token ) {//   a + ( a+b==b*c? a+b*c: a+b );
					Identifier param0 = expression(expressionStrs, null);
					current_id = expressionStrs.removeFirst();//:
					Identifier param1 = expression(expressionStrs, null);
					param0.name = param0.name+":"+param1.name;
					newExpressionStrs.append("? "+param0.name +" ");
				}else {
					if (Tokenizer.Inc == current_id.token ||Tokenizer.Dec == current_id.token &&expressionStrs.getFirst().token==(char)Tokenizer.Id){
						newExpressionStrs.append(current_id.name+expressionStrs.removeFirst().name+" ");
					} else
					newExpressionStrs.append(current_id.name +" ");
				}
			}  else if (',' == current_id.token || ';' == current_id.token || ')'==current_id.token|| ']'==current_id.token|| '}'==current_id.token|| '{'==current_id.token|| ':'==current_id.token) {
				expressionStrs.addFirst(current_id);
				break;/////////
			}else {
				Identifier next_id;
				if (!expressionStrs.isEmpty() && ((next_id = expressionStrs.getFirst()).token==Tokenizer.Inc || Tokenizer.Dec == next_id.token) ) {
					newExpressionStrs.append(current_id.name+expressionStrs.removeFirst().name+" ");
				} else  newExpressionStrs.append(current_id.name +" ");
			}
		}
		Identifier r =new Identifier(); r.name=newExpressionStrs.toString();
		return r;
	}
	public Identifier expression(LinkedList<Identifier> expressionStrs, Context context) {
		LinkedList<Identifier> newExpressionStrs = new LinkedList<Identifier>();
		Identifier current_id = null ;
		while (!expressionStrs.isEmpty()) {
			current_id = expressionStrs.removeFirst();
			if ("(".equals(current_id.name)) { // 如果是左括号,则入栈
				Identifier sub = expression(expressionStrs, null);
				if(expressionStrs.removeFirst().token==')'){
					if (isTypeCast(sub, expressionStrs)) {
						sub.name = "("+sub.name +")";
						sub.token = Tokenizer.Cast;
						sub.is_operator=true;
						newExpressionStrs.add(sub);
					}else {
						sub.name = "("+sub.name +")";
						newExpressionStrs.add(sub);
					}
				}
			} else if (is_operator(current_id)) {
				if (Tokenizer.Call == current_id.token) {// a0=a+(b*a+(c+f)*(a+b));
					LinkedList<Identifier> params = new LinkedList<>();//t0 b*a; t1,(c+f); t2,a+b; t3 ,*; b+0,t4;
					Identifier func = current_id;
					while (!")".equals(current_id.name)) {
						Context ctxt = new Context();
						Identifier param0 = expression(expressionStrs, ctxt);
						params.add(param0);
						current_id = expressionStrs.removeFirst();
					}
					Identifier pa = new Identifier();
					pa.name= "(";
					if (params.size()>0) {
						pa.name= pa.name+params.removeFirst().name;
					}
					for (Identifier i : params) {
						pa.name= pa.name+","+i.name;
					}
					pa.name=pa.name+ ")";
					func.is_operator=false;
					func.token=0;
					CALL.is_operator=true;
					CALL.token=Tokenizer.Call;
					newExpressionStrs.add(func);
					calc_one(newExpressionStrs, CALL);
					newExpressionStrs.add(pa);
				}else if ('[' == current_id.token ) { // int[] a = {}; int a = array[1];
					if(!expressionStrs.isEmpty()&&expressionStrs.getFirst().token==']'){
						newExpressionStrs.getLast().name+="[]"; expressionStrs.removeFirst();
					} else {
					Identifier param0 = expression(expressionStrs, null);
					expressionStrs.removeFirst();
					calc_one(newExpressionStrs, current_id);
					newExpressionStrs.add(param0); }
				}else if ('?' == current_id.token ) {//   a+(    a+b==b*c? a+b*c: a+b );
					calc_one(newExpressionStrs, current_id);
					Identifier param0 = expression(expressionStrs, null);
					current_id = expressionStrs.removeFirst();
					Identifier param1 = expression(expressionStrs, null);
					param0.name = param0.name+":"+param1.name;
					newExpressionStrs.add(param0);
				}else {
					calc_one(newExpressionStrs, current_id);
				}
			} else if ('{'==current_id.token){ // int[] array = {1, 1, 1};
				String s=""; Identifier arrayItem = new Identifier();
				while ('}'!=current_id.token) {
					arrayItem = expression(expressionStrs, null);
					current_id = expressionStrs.removeFirst();
					s+=   (current_id.token==','?arrayItem.name+",": arrayItem.name);
				}  
				arrayItem.name="{"+s+"}";
				newExpressionStrs.add(arrayItem);
			} else if (',' == current_id.token || ';' == current_id.token || ')'==current_id.token|| ']'==current_id.token|| '}'==current_id.token|| '{'==current_id.token|| ':'==current_id.token) {
				expressionStrs.addFirst(current_id);
				break;/////////
			}else {
				newExpressionStrs.add(current_id);
			}
		}
		Identifier r = resolve(newExpressionStrs);
		return r;
	}
	private boolean isTypeCast(Identifier sub, LinkedList<Identifier> expressionStrs) {
		int a=0,aa=1;  int aaa= (Integer) a +1;aaa= (Integer) (a+1);
		if(expressionStrs.isEmpty())
			return false;
		Identifier current_id=expressionStrs.getFirst();
		if (!is_operator(current_id)&&
		!(',' == current_id.token || ';' == current_id.token || ')'==current_id.token|| ']'==current_id.token|| '}'==current_id.token|| '{'==current_id.token|| ':'==current_id.token))
			{ return true; }
		return false;
	}
	public void calc_one(LinkedList<Identifier> newExpressionStrs, Identifier new_op) {
		int pre_size = newExpressionStrs.size()+1;
		Identifier b = null,a = null,op = null;
		int size = newExpressionStrs.size();
		if(size>0)b = newExpressionStrs.removeLast();
		if(size>1)op = newExpressionStrs.removeLast();//
		if(size>2)a = newExpressionStrs.removeLast();
		if(size>2)newExpressionStrs.add(a);
		if(size>1)newExpressionStrs.add(op);
		if(size>0)newExpressionStrs.add(b);
		//  a+b+c*d+func(a)+ a+b+-c*-(int)a.d.aa[a+b];
		if (b != null&&is_operator(b)) {
			// a++;
			if (op!=null&&!is_operator(op)&&
					(b.token==Tokenizer.Inc||b.token==Tokenizer.Dec)) {
				op.name = op.name +b.name;
				newExpressionStrs.removeLast();
				System.out.println("find:  a++;");
			}
		} else if (op!=null&&is_operator(op)) {
			int a1=0;
			a1=a1+ (int)-(int)-  --a1 +1;
			if ((op.token==Tokenizer.Cast||op.token=='-'||op.token==Tokenizer.Inc||op.token==Tokenizer.Dec)
					&& (a==null|| is_operator(a))  ) {//  
				if (op.token==Tokenizer.Call||op.token=='.'||op.token=='['||op.token=='(') {
				}else {
					b=newExpressionStrs.removeLast();
					newExpressionStrs.removeLast();
					b.name = op.name+b.name;
					newExpressionStrs.add(b);
				}
			} else if (a!=null&&!is_operator(a)) {
				if (operator_compare(new_op, op)  ) {
				} else {
					Identifier r= binaryOperation(a, b, op);
					b = newExpressionStrs.removeLast();
					op = newExpressionStrs.removeLast();
					a = newExpressionStrs.removeLast();
					newExpressionStrs.add(r);
				}
			}else if (a!=null&&is_operator(a)) {
				System.err.println(a.name+"!=null&& not operator");
			}else if (a==null    ) {
				System.err.println("a==null&&   op !='-'  ");
			}
		} else if (a!=null  &&is_operator(a)) {
			System.err.println(a.name+"!=null&& is_operator");
		}
		newExpressionStrs.add(new_op);
		if (pre_size>newExpressionStrs.size()) {
			Identifier  nested=newExpressionStrs.removeLast();
			calc_one(newExpressionStrs,nested);
		}
	}
	public Identifier binaryOperation(Identifier a, Identifier b, Identifier operator) {
		System.out.println(a.name + operator.name + b.name);
		Identifier ret =new Identifier();
		if (operator.token == '.') {
			b.name = a.name + "." + b.name;
			return b;
		} else if (operator.token == '[') {
			Identifier r = a;
			r.name=a.name+"["+b.name+"]";
			return r;
		}  else if (operator == CALL) {
			Identifier r = new Identifier();
			r.name = a.name+b.name   +"";
			return r;
		} else if (operator.token == '+'||operator.token == '-'||operator.token == '*'||operator.token == '/') {
			String funcName="";
			switch (operator.token) {
			case '+':
				funcName = "add";
				break;
			case '-':
				funcName = "sub";
				break;
			case '*':
				funcName = "mul";
				break;
			case '/':
				funcName = "div";
				break;
			default:
				break;
			}
			ret.name= funcName+"("+a.name+"," +b.name+")";
			return ret;
		} else   {
			ret.name= ""+a.name+"" +operator.name+"" +b.name+"";
			return ret;
		}
	}
	void match(int t) {
		if (statement.isEmpty()) {
			System.err.println("DO NOT match:" + t);
			return;
		} else if (statement.removeFirst().token != (char)t) {
			System.err.println("DO NOT match:" + t);
		}
	}

	void statement(LinkedList<Identifier> statement) {
		if (statement.isEmpty()) return;
		this.statement = statement;
		Identifier current_id = statement.removeFirst();
		int token = current_id.token;
		if ("if".equals(current_id.name)) {
			// if (...) <statement> [else <statement>]
			// if (...) <cond>
			// JZ a
			// <statement> <statement>
			// else: JMP b
			// a:
			// <statement> <statement>
			// b: b:
			out.append(current_id.name);
			out.append('(');
			Identifier exp = expression(statement,null); // parse condition Assign
			out.append(exp.name);  out.append(')');
			match(')'); 
			// emit code for if
			// *++text = JZ;
			// b = ++text;
			statement(statement); // parse statement
			if ("else".equals(current_id.name)) { // parse else
				// emit code for JMP B
				// *b = (int)(text + 3);
				// *++text = JMP;
				// b = ++text;
				out.append(current_id.name +" ");
				statement(statement);
			}
			// *b = (int)(text + 1);
		} else if ("while".equals(current_id.name)) {
			// a: a:
			// while (<cond>) <cond>
			// JZ b
			// <statement> <statement>
			// JMP a
			// b: b:
			// a = text + 1;
//			match('(');
			out.append(current_id.name);
			out.append('(');
			Identifier exp = expression(statement,null);
			match(')');
			out.append(exp.name);  out.append(')');
			// *++text = JZ;
			// b = ++text;
			statement(statement);
			// *++text = JMP;
			// *++text = (int)a;
			// *b = (int)(text + 1);
		} else if (token == '{') {
			// { <statement> ... }
			// match('{');
			out.append("{");
			while (token != '}') {
				statement(statement);
				if (statement.isEmpty()){
					System.out.println();
				}
				token = statement.getFirst().token;
			}
			match('}');
			println("}");
		} else if ("return".equals(current_id.name)) {
			// return [expression];
			print("return ");
			if(current_id.token=='(') print("("); // Fix a bug for '('
			if (token != ';') {
				// expression(Assign);
				Identifier exp = expression(statement, null);
				if (exp!=null) {
					System.err.println(exp.name+";");
					print(exp.name);
				}
			}
			match(';');
			println(";");
			// emit code for return
			// *++text = LEV;
		} else if (token == ';') {
			System.err.println("empty statement");
		} else {
			// a = b; or function_call();
			// expression(Assign);
			statement.addFirst(current_id);
			Identifier exp = expression(statement,null);
			if (exp!=null) {
				System.err.println(exp.name+";");
				println(exp.name+";");
				match(';');
			}
		}
	}

	/**
	 * priority compare
	 * 
	 * @return if less equal then return false, else return true
	 */
	public static boolean operator_compare(Identifier operator1, Identifier operator2) {
		return Tokenizer.priority[operator1.token] < Tokenizer.priority[operator2.token];
	}

	public Identifier resolve(LinkedList<Identifier> newExpressionStrs) {
		char END = ' ';
		if (newExpressionStrs.size() > 2) {
			Identifier i =new Identifier();
			i.is_operator=true;
			i.token=END;
			calc_one(newExpressionStrs, i);
		}
		if (newExpressionStrs.size() >= 2) {
			Identifier a= newExpressionStrs.getFirst();
			Identifier b= newExpressionStrs.get(1);
			if ( b.token!=END) {
				a.name=a.name+" "+b.name;
			}
		}
		return newExpressionStrs.isEmpty()?null:newExpressionStrs.getFirst();
	}

	public static boolean is_operator(Identifier s) {
		return s.is_operator;
	}

	public static class Context {
	}

	private LinkedList<Identifier> statement;
	public final static Identifier CALL = new Identifier(), CAST = new Identifier();

	static {
		CALL.token = Tokenizer.Call;
		CAST.token = Tokenizer.Cast;
	}
}