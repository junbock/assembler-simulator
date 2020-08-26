package SP19_simulator;

import java.util.ArrayList;

public class TokenTable {
	ArrayList<Token> tokenList;
	
	TokenTable() {
		tokenList = new ArrayList<Token>();;
	}
	
	public void putToken(Token token) {
		tokenList.add(token);
	}
	
	public Token getToken(int index) {
		return tokenList.get(index);
	}
	
	public void modifyToken(Token token, int location) {
		tokenList.set(location, token);
	}
	
	public int length() {
		return tokenList.size();
	}
}

class Token {
	public String name;	//�̸�
	public int addr;	//�ּ�
	public int sub;	//��ƾ index
	public int mod;	//��������
	public int isOp;	//���� /+ : 1 /- : 2 /���� : 0
	
	public int op;
	public int format;
	public int nixbpe;
	public int target;
	
	final int mask3 = 4095, mask4 = 65535;
	Token(String str)
	{
		this.name=str;
	}
	
	void modify(int addr) {
		String str;
		int modInst = Integer.valueOf(name, 16);

		if (format == 3) {
			addr = addr & mask3;
			modInst += addr;
			str = String.format("%06X", modInst);
		}
		else {
			addr = addr & mask4;
			modInst += addr;		
			str = String.format("%08X", modInst);
		}

		this.target += addr;
		name = str;
		
	}
}