package soft3d.v1_0.compiler;

public class Identifier {
	int index;
	char token;
	String name;
	public boolean temporary;
	public boolean is_operator;
	public String variableType;
	public Object objectValue;
	int lineNumber;
	int column;
}