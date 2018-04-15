package soft3d.v1_0.compiler;

import java.nio.CharBuffer;

public class Tokenizer {
	public final static int Num = 1, Sys = 3, Id = 4,  Else = 5, Enum = 6, If = 'f', Int = 'n',
			Return = 'r', While = 'w', Cond = '?';
	public final static char Brak = '[', Assign = '=', Lor = 'o', Lan = 'a', Or = '|', Xor = '^', And = '&', Eq = 'E',
			Ne = '!', Lt = '<', Gt = '>', Le = 'L', Ge = 'G', Shl = 'S', Shr = 'R', Add = '+', Sub = '-', Mul = '*',Cast = 'C',Call = '(',
			Div = '/', Mod = '%', Inc = 'I', Dec = 'D';
	public static byte[] priority = new byte[128];
	static{
		priority[Tokenizer.Call]=0;
		priority['[']=0;
		priority['.']=0;
		priority['-']=1;
		priority[Tokenizer.Inc]=1;
		priority[Tokenizer.Dec]=1;
		priority[Tokenizer.Cast]=1;
		priority['/']=2;
		priority['*']=2;
		priority['%']=2;
		priority['+']=3;
		priority['-']=3;
		priority[Tokenizer.Shl]=4;
		priority[Tokenizer.Shr]=4;
		priority[Tokenizer.Ge]=5;
		priority[Tokenizer.Gt]=5;
		priority[Tokenizer.Le]=5;
		priority[Tokenizer.Lt]=5;
		priority[Tokenizer.Eq]=6;
		priority[Tokenizer.Ne]=6;
		priority['&']=11;
		priority['|']=11;
		priority['^']=11;
		priority[Tokenizer.Lan]=11;
		priority[Tokenizer.Lor]=11;
		priority['?']=12;
		priority['=']=13;
		priority[' ']=100;priority[';']=100;
	}

	char token;
	String token_val;
	int line;
	Identifier current_id;
	CharBuffer old_src;

	boolean isBlank(char token) {
		return token == '\n' || token == '\r' || token == '\t' || token == ' ';

	}

	public static boolean isWord(char token) {
		return (token >= 'a' && token <= 'z') || (token >= 'A' && token <= 'Z');
	}

	public static boolean isNumber(char token) {
		return token >= '0' && token <= '9';
	}
	int lineNumber=0,columnNumber=0;
	void lineIncrement(){ lineNumber++;columnNumber=0;}
	public Identifier next(CharBuffer src) {
		Identifier identifier = new Identifier();
		identifier.is_operator = true;
		while (src.hasRemaining()) {
			token = src.get();
			columnNumber++;
			if (isBlank(token)) {
				if(token=='\n') lineIncrement();
				else if(token=='\r') columnNumber=0;
				else if(token=='\t') columnNumber+=3;
			} else if (token == '#') {
				// // skip macro, because we will not support it
				while (src.hasRemaining() && (src.get() != '\n')) {
				}
				lineIncrement();
			} else if (isWord(token) || (token == '_')) {
				identifier.is_operator = false;
				token_val = String.valueOf(token);
				char next_token = src.get(src.position());
				while (isWord(next_token) || isNumber(next_token) || (next_token == '_')) {
					token_val = token_val + next_token;
					token = src.get();
					next_token = src.get(src.position());
				}
				// look for existing identifier, linear search
				current_id = null;
				identifier = new Identifier();
				// store new ID
				identifier.token = (char)Id;
				identifier.name = token_val;
				int next_pos=src.position();
				while (next_pos< src.length() && isBlank(next_token)) {
					next_token = src.get(++next_pos);
				}
				if (next_token == '(') {
					identifier.token = Call;
					identifier.is_operator=true;
					src.position(next_pos+1);
				}
				if("int".equals(identifier.name)){
					System.out.println("");
				}
				return identifier;
			} else if (isNumber(token)) {
				boolean is_float=false;
				identifier.is_operator = false;
				identifier.variableType="int";
				// parse number, three kinds: dec(123) hex(0x123) oct(017)
				char next_token = src.get(src.position());
				token_val = String.valueOf(token);
				if (token == '0'&&(next_token == 'x' || next_token == 'X')) {
					// hex
					// token = *++src;
					while ((token >= '0' && token <= '9') || (token >= 'a' && token <= 'f')
							|| (token >= 'A' && token <= 'F')) {
						// token_val = token_val * 16 + (token & 15) +
						// (token >= 'A' ? 9 : 0);
						// token = *++src;
						token_val = token_val + next_token;
						token = src.get();
						next_token = src.get(src.position());
					}
					
				} else {
					// dec, starts with [0-9\.]
					next_token = src.get(src.position());
					while (isNumber(next_token) || next_token == '.') {
						if(next_token=='.')
							is_float=true;
						token_val = token_val + next_token;
						token = src.get();
						next_token = src.get(src.position());
					}
				}
				next_token = src.get(src.position());
				if(next_token=='f'||next_token=='F'||next_token=='D'||next_token=='d'){
					is_float=true;
					src.get();
				}
				if (is_float) {
					token_val=token_val+"f";
					identifier.variableType="float";
				}
				identifier.token = Num;
				identifier.name = token_val;
				return identifier;
			} else if (token == '/') {
				char next_token = src.get(src.position());
				if (next_token == '/') {
					identifier.is_operator = false;
					// skip comments
					while (src.hasRemaining() && src.get() != '\n') {
						// ++src;
					}
					lineIncrement();
				} else {
					identifier.is_operator = true;
					// divide operator
					identifier = new Identifier();
					identifier.name = "/";
					identifier.is_operator = true;
					identifier.token = Div;
					return identifier;
				}
				
			} else if (token == '"' || token == '\'') {
				identifier.is_operator = false;
				// parse string literal, currently, the only supported escape
				// character is '\n', store the string literal into data.
				// last_pos = data;
				// while (*src != 0 && *src != token) {
				// token_val = *src++;
				// if (token_val == '\\') {
				// // escape character
				// token_val = *src++;
				// if (token_val == 'n') {
				// token_val = '\n';
				// }
				// }
				//
				// if (token == '"') {
				// *data++ = token_val;
				// }
				// }
				//
				// src++;
				// // if it is a single character, return Num token
				// if (token == '"') {
				// token_val = (int)last_pos;
				// } else {
				// token = Num;
				// }
				//
				// return;
			} else if (token == '=') {
				identifier = new Identifier();
				identifier.is_operator = true;
				// parse '==' and '='
				char next_token = src.get(src.position());
				if (next_token == '=') {
					src.get();
					identifier.token = Eq;
					identifier.name = "==";
				} else {
					identifier.name = "=";
					identifier.token = Assign;
				}
				return identifier;
			} else if (token == '+') {
				identifier.is_operator = true;
				identifier.name = "+";
				// parse '+' and '++'
				char next_token = src.get(src.position());
				if (next_token == '+') {
					src.get();
					identifier.token = Inc;
					identifier.name="++";
				} else {
					identifier.token = Add;
				}
				return identifier;
			} else if (token == '-') {
				identifier.name = "-";
				identifier.is_operator = true;
				// parse '-' and '--'
				char next_token = src.get(src.position());
				if (next_token == '-') {
					src.get();
					identifier.token = Dec;
					identifier.name = "--";
				} else {
					identifier.token = Sub;
				}
				return identifier;
			} else if (token == '!') {
				identifier.name = "!";
				identifier.is_operator = true;
				// parse '!='
				char next_token = src.get(src.position());
				if (next_token == '=') {
					src.get();
					identifier.token = Ne;
					identifier.name = "!=";
				}
				return identifier;
			} else if (token == '<') {
				identifier.name = "<";
				identifier.is_operator = true;
				// parse '<=', '<<' or '<'
				char next_token = src.get(src.position());
				if (next_token == '=') {
					src.get();
					identifier.token = Le;
					identifier.name = "<=";
				} else if (next_token == '<') {
					src.get();
					identifier.token = Shl;
					identifier.name = "<<";
				} else {
					identifier.token = Lt;
				}
				return identifier;
			} else if (token == '>') {
				identifier.name = ">";
				identifier.is_operator = true;
				// parse '>=', '>>' or '>'
				char next_token = src.get(src.position());
				if (next_token == '=') {
					src.get();
					identifier.token = Ge;
					identifier.name = ">=";
				} else if (next_token == '>') {
					src.get();
					identifier.token = Shr;
					identifier.name = ">>";
				} else {
					identifier.token = Gt;
				}
				return identifier;
			} else if (token == '|') {
				identifier.name = "|";
				identifier.is_operator = true;
				// parse '|' or '||'
				char next_token = src.get(src.position());
				if (next_token == '|') {
					src.get();
					identifier.token = Lor;
					identifier.name = "||";
				} else {
					identifier.token = Or;
				}
				return identifier;
			} else if (token == '&') {
				identifier.name = "&";
				identifier.is_operator = true;
				// parse '&' and '&&'
				char next_token = src.get(src.position());
				if (next_token == '&') {
					src.get();
					identifier.token = Lan;
					identifier.name = "&&";
				} else {
					identifier.token = And;
				}
				return identifier;
			} else if (token == '^') {
				identifier.name = "^";
				identifier.token = Xor;
				return identifier;
			} else if (token == '%') {
				identifier.name = "%";
				identifier.token = Mod;
				return identifier;
			} else if (token == '*') {
				identifier.name = "*";
				identifier.token = Mul;
				return identifier;
			} else if (token == '.') {
				identifier.name = ".";
				identifier.is_operator = true;
				identifier.token = '.';
				return identifier;
			} else if (token == '[') {
				identifier.name = "[";
				identifier.is_operator = true;
				identifier.token = Brak;
				return identifier;
			} else if (token == '?') {
				identifier.is_operator = true;
				identifier.token = Cond;
				identifier.name = "?";
				return identifier;
			} else if (token == ';' || token == '{' || token == '}' || token == ','|| token == ']') {
				identifier.is_operator = false;
				// directly return the character as token;
				identifier.token = token;
				identifier.name = String.valueOf(token);
				return identifier;
			} else if (token == '~' || token == ';' || token == '{' || token == '}' || token == '(' || token == ')'
					|| token == ']' || token == ',' || token == ':') {
				identifier.is_operator = false;
				// directly return the character as token;
				identifier.token = token;
				if(identifier.name==null)
				identifier.name = String.valueOf(token);
				return identifier;
			}
		}
		return null;
	}
}