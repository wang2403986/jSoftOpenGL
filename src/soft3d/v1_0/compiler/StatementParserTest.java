package soft3d.v1_0.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

public class StatementParserTest {
	public static void main(String[] a) throws Exception{
		FileReader fileReader= new FileReader("kernels/fragShader2.txt");
		BufferedReader br = new BufferedReader(fileReader);
		String line="";
		StringBuilder code = new StringBuilder();
		while ((line=br.readLine())!=null) {
			code.append(line);
		}
		br.close();
		CharBuffer buffer =CharBuffer.wrap( code.toString().toCharArray());
		Tokenizer tokenizer=new Tokenizer();
		LinkedList<Identifier> list = new LinkedList<>();
		while (buffer.hasRemaining()) {
			Identifier i= tokenizer.next(buffer);
			int a1=0;
			{
				float a2 =0;
			}
			int a2=3;
			list.add(i);
		}
		ArrayList<Identifier> array =new ArrayList<>(list);
		StatementParser parser =new StatementParser();
		parser.statement(list);
		parser.statement(list);
		parser.statement(list);
		parser.statement(list);
}
	
}
