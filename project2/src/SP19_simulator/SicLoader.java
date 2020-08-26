package SP19_simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SicLoader는 프로그램을 해석해서 메모리에 올리는 역할을 수행한다. 이 과정에서 linker의 역할 또한 수행한다. 
 * <br><br>
 * SicLoader가 수행하는 일을 예를 들면 다음과 같다.<br>
 * - program code를 메모리에 적재시키기<br>
 * - 주어진 공간만큼 메모리에 빈 공간 할당하기<br>
 * - 과정에서 발생하는 symbol, 프로그램 시작주소, control section 등 실행을 위한 정보 생성 및 관리
 */
public class SicLoader {
	ResourceManager rMgr;
	InstTable instTable;
	BufferedReader read = null;
	TokenTable extref = new TokenTable();
	TokenTable extdef = new TokenTable();
	TokenTable mod = new TokenTable();
	TokenTable inst = new TokenTable();
	
	int sub = 0, programLength = 0;
	
	static String programName, programStart;	//mod레코드에서 상대주소를 저장하는 리스트
	
	static ArrayList<String> start = new ArrayList<String>();	//루틴의 시작주소
	static ArrayList<String> rStart = new ArrayList<String>();	//루틴의 실제시작주소
	static ArrayList<String> name = new ArrayList<String>();	//루틴의 이름
	static ArrayList<String> length = new ArrayList<String>();	//루틴의 길이
	static int loc=0;	//loc값
	
	public SicLoader(ResourceManager resourceManager) {
		// 필요하다면 초기화
		setResourceManager(resourceManager);
		instTable = new InstTable("inst.data");
	}

	/**
	 * Loader와 프로그램을 적재할 메모리를 연결시킨다.
	 * @param rMgr
	 */
	public void setResourceManager(ResourceManager resourceManager) {
		this.rMgr=resourceManager;
	}
	
	/**
	 * object code를 읽어서 load과정을 수행한다. load한 데이터는 resourceManager가 관리하는 메모리에 올라가도록 한다.
	 * load과정에서 만들어진 symbol table 등 자료구조 역시 resourceManager에 전달한다.
	 * @param objectCode 읽어들인 파일
	 */
	public void load(File objectCode) {
		String line;
		int cnt = 0;
		int op, nixbpe, rstart=0;
		Instruction instBuf;
		final int opMask = 252 , nixbpeMask = 63, niMask = 48, bpMask = 6;
		char[] buf = null;
		
		try {
			read = new BufferedReader(new FileReader(objectCode));
			
			while ((line = read.readLine()) != null) {
				if (line.charAt(0) == 'H') {
					sub++;
					name.add(line.substring(1, 7));
					length.add(line.substring(14, 19));
					if (sub == 1) { 
						programName = line.substring(1, 7);
						programStart = line.substring(7, 13);
						rstart = Integer.valueOf(line.substring(7, 13), 16);
					}
					if (sub > 1) {
						int n = 0, m = 0;
						
						for (int i = 0; i < sub - 1; i++) {
							n += Integer.parseInt(length.get(i), 16);
						}
						
						m = Integer.parseInt(line.substring(7, 13), 16);
						String s = String.format("%06X", n + m);
						System.out.println("start "+s);
						rStart.add(s);
						loc = n + m;
						rstart = loc;
						cnt = 0;
						cnt += Integer.parseInt(s, 16);
						
						Token tokenbuf =new Token(line.substring(1, 7));
						tokenbuf.addr = loc;
						tokenbuf.sub = sub;
						extdef.putToken(tokenbuf);
					}
				}
				else if (line.charAt(0) == 'D') {
					Token tokenbuf = new Token(line.substring(1, 7));
					tokenbuf.sub = sub;
					
					if (sub > 1) {
						tokenbuf.addr = Integer.parseInt(line.substring(7, 13), 16)
								+ Integer.parseInt(rStart.get(sub - 2), 16);
					}
					else {
						tokenbuf.addr = Integer.parseInt(line.substring(7, 13), 16);
					}
					
					extdef.putToken(tokenbuf);
					
					if (line.length() > 14) {
						tokenbuf = new Token(line.substring(13, 19));
						tokenbuf.sub = sub;
						
						if (sub > 1) {
							tokenbuf.addr = Integer.parseInt(line.substring(19, 25), 16)
									+ Integer.parseInt(rStart.get(sub - 2), 16);
						}
						else {
							tokenbuf.addr = Integer.parseInt(line.substring(19, 25), 16);
						}
						
						extdef.putToken(tokenbuf);
					}
					if (line.length() > 26) {
						tokenbuf = new Token(line.substring(25, 31));
						tokenbuf.sub = sub;
						
						if (sub > 1) {
							tokenbuf.addr = Integer.parseInt(line.substring(31, line.length()), 16)
									+Integer.parseInt(rStart.get(sub - 2), 16);
						}
						else {
							tokenbuf.addr = Integer.parseInt(line.substring(31, line.length()), 16);
						}
						
						extdef.putToken(tokenbuf);	
					}					
				}
				else if (line.charAt(0) == 'R')
				{
					Token tokenbuf = new Token(line.substring(1, 7));
					tokenbuf.sub = sub;					
					extref.putToken(tokenbuf);
					
					if (line.length() > 8) {
						tokenbuf = new Token(line.substring(7,13));
						tokenbuf.sub = sub;
						extref.putToken(tokenbuf);
					}
					if(line.length()>14) {
						tokenbuf = new Token(line.substring(13, line.length()));
						tokenbuf.sub = sub;
						extref.putToken(tokenbuf);
					}
				}
				else if (line.charAt(0) == 'T') {
					int tCnt, tLen = Integer.parseInt(line.substring(7, 9), 16);
					tCnt = 9;
					System.out.println(line.substring(9,line.length()));
					for (int i = 11; i < line.length()+1; i+=2) {
						buf = rMgr.intToChar(Integer.valueOf(line.substring(i-2,i),16));
						rMgr.setMemory(rstart+Integer.valueOf(line.substring(1,7), 16) + (i - 11)/2, buf, 1);
					}
					while (true) {
						if ((tCnt/2) > tLen + 3)
							break;
						
						Token tokenbuf;
						
						if (line.length() < tCnt + 3) {
							tCnt += 2;
							break;
						}
						
						op = Integer.valueOf(line.substring(tCnt, tCnt + 2), 16) & opMask;
						instBuf = instTable.getInstruction(op);
						
						if (instBuf != null) {
							System.out.println(instBuf.getInstruction());
							int[] n = new int[3];
							if (instBuf.getFormat() != 2) {
								nixbpe = Integer.parseInt(line.substring(tCnt + 1, tCnt + 3), 16) & nixbpeMask;								
							}
							else 
								nixbpe = 0;
							
							//데이터
							if((((nixbpe & niMask) == 0 && (((nixbpe & bpMask) == 0) && ((nixbpe & 1) == 0))) 
									&& (instBuf.getFormat()!=2)))
							{
								n[0]=Integer.parseInt(line.substring(tCnt,tCnt+2),16);
								if (line.length() > (tCnt-9)*2+5) 
								{
									n[1] = Integer.parseInt(line.substring(tCnt + 2, tCnt + 4), 16);
								}
								if (line.length() > (tCnt-9)*2 +9 ) 
								{
									n[2] = Integer.parseInt(line.substring(tCnt + 4, tCnt + 6), 16);
								}
								if(n[2]<=65 && n[1]<=65)
								{
									tCnt+=2;
								}
								else
								{
									cnt+=3;
									tCnt+=6;
								}
							}
							else {
								if (instBuf.getFormat() == 2) {
									tokenbuf = new Token(line.substring(tCnt, tCnt + 4));
									tokenbuf.sub = sub;
									tokenbuf.addr = loc;
									tokenbuf.op = instBuf.getOpcode();
									tokenbuf.format = 2;
									inst.putToken(tokenbuf);
									cnt += 2;
									tCnt += 4;
									loc += 2;
								}
								else if (((nixbpe & 1) == 0) && (instBuf.getFormat() == 3)) {
									tokenbuf = new Token(line.substring(tCnt, tCnt + 6));
									tokenbuf.sub = sub;
									tokenbuf.addr = loc;
									tokenbuf.op = instBuf.getOpcode();
									tokenbuf.format = 3;
									tokenbuf.nixbpe = nixbpe;
									tokenbuf.target = Integer.valueOf(line.substring(tCnt+3, tCnt+6), 16);
									inst.putToken(tokenbuf);
									cnt += 3;
									tCnt += 6;
									loc += 3;
								}
								else if (nixbpe > 0) {
									tokenbuf = new Token(line.substring(tCnt, tCnt + 8));
									tokenbuf.sub = sub;
									tokenbuf.addr = loc;
									tokenbuf.op = instBuf.getOpcode();									
									tokenbuf.format = 4;
									tokenbuf.nixbpe = nixbpe;
									tokenbuf.target = Integer.valueOf(line.substring(tCnt+3, tCnt+8), 16);
									inst.putToken(tokenbuf);
									cnt +=4;
									tCnt += 8;
									loc += 4;
								}
								
								
							}						
							
						}
						else {
							System.out.println(buf.length);
							cnt += 1;
							tCnt += 2;
							loc += 1;
						}
						
					}
				}
				else if (line.charAt(0)=='M') {
					Token tokenbuf = new Token(line.substring(10, line.length()));;
					if (sub > 1) {
						tokenbuf.addr = Integer.parseInt(line.substring(1, 7), 16)
								+Integer.parseInt(rStart.get(sub - 2), 16);
					}
					else {
						tokenbuf.addr = Integer.parseInt(line.substring(1, 7), 16);
					}
					
					tokenbuf.mod = Integer.parseInt(line.substring(7, 9), 16);
					
					if (line.substring(9, 10).equals("+")) {
						tokenbuf.isOp = 1;
					}
					else
						tokenbuf.isOp = 2;
					
					mod.putToken(tokenbuf);
				}
				
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < length.size(); i++) {
			programLength += Integer.valueOf(length.get(i), 16);
		}
		
		Token modbuf=null, defbuf=null, instbuf=null;
		for (int i = 0; i < mod.length(); i++) {
			char[] c;
			modbuf = mod.getToken(i);
			String str = null;
			defbuf = null;
			instbuf = null;
			
			for (int j = 0; j < extdef.length(); j++) {
				defbuf = extdef.getToken(j);
				if (defbuf.name.contains(modbuf.name)) {
					break;							
				}
			}
			
			if (defbuf != null) {
				for (int j = 0; j < inst.length(); j++) {
					instbuf = inst.getToken(j);
					if (instbuf.addr <= mod.getToken(i).addr 
							&& instbuf.addr + instbuf.format > mod.getToken(i).addr) {					
						instbuf.modify(defbuf.addr);
						break;
					}
					if (j == inst.length() - 1)
						instbuf = null;
				}
			}
			
			if (instbuf != null) {
				buf = rMgr.intToChar(Integer.valueOf(instbuf.name, 16));
				rMgr.setMemory(mod.getToken(i).addr - 1, buf, 4);
			}
			else {
				System.out.println(modbuf.name);
				if (modbuf.mod == 5) {
					c = rMgr.getMemory(modbuf.addr + 1, 3);
					str = String.format("%06X", defbuf.addr);
					buf = rMgr.intToChar(Integer.valueOf(str, 16) + rMgr.charToInt(c));
					rMgr.setMemory(modbuf.addr + 1, buf, 3);
				}
				else {
					c = rMgr.getMemory(modbuf.addr, 3);
					str = String.format("%06X", defbuf.addr);
					if (modbuf.isOp == 1)
						buf = rMgr.intToChar(Integer.valueOf(str, 16) + rMgr.charToInt(c));
					else
						buf = rMgr.intToChar(-Integer.valueOf(str, 16) + rMgr.charToInt(c));
					rMgr.setMemory(modbuf.addr, buf, 3);
				}
				System.out.println(String.format("%06X", rMgr.charToInt(rMgr.getMemory(modbuf.addr, 3))));
			}
			
		}
		
	}

}
