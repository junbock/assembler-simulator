package SP19_simulator;

import java.io.File;

/**
 * 시뮬레이터로서의 작업을 담당한다. VisualSimulator에서 사용자의 요청을 받으면 이에 따라
 * ResourceManager에 접근하여 작업을 수행한다.  
 * 
 * 작성중의 유의사항 : <br>
 *  1) 새로운 클래스, 새로운 변수, 새로운 함수 선언은 얼마든지 허용됨. 단, 기존의 변수와 함수들을 삭제하거나 완전히 대체하는 것은 지양할 것.<br>
 *  2) 필요에 따라 예외처리, 인터페이스 또는 상속 사용 또한 허용됨.<br>
 *  3) 모든 void 타입의 리턴값은 유저의 필요에 따라 다른 리턴 타입으로 변경 가능.<br>
 *  4) 파일, 또는 콘솔창에 한글을 출력시키지 말 것. (채점상의 이유. 주석에 포함된 한글은 상관 없음)<br>
 * 
 * <br><br>
 *  + 제공하는 프로그램 구조의 개선방법을 제안하고 싶은 분들은 보고서의 결론 뒷부분에 첨부 바랍니다. 내용에 따라 가산점이 있을 수 있습니다.
 */
public class SicSimulator {
	ResourceManager rMgr;
	int instCnt = 0;
	final int nmask = 32, imask = 16, xmask = 8, bmask = 4, pmask = 2, emask = 1, chmask = 255;
	final int minus = 2048;
	final int sim = 0, ind = 1, imm = 2;
	public SicSimulator(ResourceManager resourceManager) {
		// 필요하다면 초기화 과정 추가
		this.rMgr = resourceManager;
	}

	/**
	 * 레지스터, 메모리 초기화 등 프로그램 load와 관련된 작업 수행.
	 * 단, object code의 메모리 적재 및 해석은 SicLoader에서 수행하도록 한다. 
	 */
	public void load(File program) {
		/* 메모리 초기화, 레지스터 초기화 등*/
		//rMgr.initializeResource();
	}

	/**
	 * 1개의 instruction이 수행된 모습을 보인다. 
	 */
	public String oneStep(SicLoader loader) {
		Token inst;
		
		if (instCnt < loader.inst.length()) {
			inst = loader.inst.getToken(instCnt++);			
		}
		else {
			return "END";
		}
		
		String name = loader.instTable.getInstruction(inst.op).getInstruction();
		int data = -1, x = 0, loc = 0;
		int mode = -1;
		int reg1, reg2;
		int target;
		char[] buf;
		System.out.println(name);
		if (instCnt < loader.inst.length()) {
			rMgr.setRegister(rMgr.pcReg, loader.inst.getToken(instCnt).addr);
		}
		if (inst.format == 3 || inst.format == 4) {
			if ((inst.nixbpe & nmask) == nmask && (inst.nixbpe & imask) == imask) 
				mode = sim;
			else if ((inst.nixbpe & nmask) == nmask)
				mode = ind;
			else if ((inst.nixbpe & imask) == imask)
				mode = imm;
			
			if ((inst.nixbpe & xmask) == xmask)
				x = rMgr.getRegister(rMgr.xReg);
			else 
				x = 0;
			
			if ((inst.nixbpe & pmask) == pmask)
				loc = rMgr.getRegister(rMgr.pcReg);
			else if ((inst.nixbpe & bmask) == bmask)
				loc = rMgr.getRegister(rMgr.bReg);
			else
				loc = 0;
			
			if ((inst.target & minus) == minus) {
				inst.target = (-4096) | inst.target;
			}
				
		}		
		
		if (name.equals("ADD")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.getRegister(rMgr.aReg) + data);			
		}
		else if (name.equals("ADDF")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 5));
			}
			rMgr.setRegister(rMgr.fReg, rMgr.getRegister(rMgr.fReg) + data);	
		}
		else if (name.equals("ADDR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			data = rMgr.getRegister(reg1) + rMgr.getRegister(reg2);
			rMgr.setRegister(reg2, data);
		}
		else if (name.equals("AND")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.getRegister(rMgr.aReg) & data);
		}
		else if (name.equals("CLEAR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			rMgr.setRegister(reg1, 0);
		}
		else if (name.equals("COMP")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			
			rMgr.setRegister(rMgr.swReg, rMgr.getRegister(rMgr.aReg) - data);
				
		}
		else if (name.equals("COMPF")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 5));
			}
			rMgr.setRegister(rMgr.swReg, rMgr.getRegister(rMgr.fReg) - data);
		}
		else if (name.equals("COMPR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			rMgr.setRegister(rMgr.swReg, rMgr.getRegister(reg1) - rMgr.getRegister(reg2));
		}
		else if (name.equals("DIV")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.getRegister(rMgr.aReg) / data);
		}
		else if (name.equals("ADDF")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 5));
			}
			rMgr.setRegister(rMgr.fReg, rMgr.getRegister(rMgr.fReg) / data);	
		}
		else if (name.equals("ADDR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			data = rMgr.getRegister(reg2) / rMgr.getRegister(reg1);
			rMgr.setRegister(reg2, data);
		}
		else if (name.equals("J")) {
			if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else 
				data = inst.target + x + loc;
			rMgr.setRegister(rMgr.pcReg, data);
			
			for (int i = 0; i < loader.inst.length(); i++) {
				if (loader.inst.getToken(i).addr == data) {
					instCnt = i;
					break;
				}
			}
			
			
		}
		else if (name.equals("JEQ")) {
			if (rMgr.getRegister(rMgr.swReg) == 0) {
				if (mode == ind) {
					data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				}
				else 
					data = inst.target + x + loc;
				rMgr.setRegister(rMgr.pcReg, data);
				
				for (int i = 0; i < loader.inst.length(); i++) {
					if (loader.inst.getToken(i).addr == data) {
						instCnt = i;
						break;
					}
				}
			}
		}
		else if (name.equals("JGT")) {
			if (rMgr.getRegister(rMgr.swReg) > 0) {
				if (mode == ind) {
					data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				}
				else 
					data = inst.target + x + loc;
				rMgr.setRegister(rMgr.pcReg, data);
				
				for (int i = 0; i < loader.inst.length(); i++) {
					if (loader.inst.getToken(i).addr == data) {
						instCnt = i;
						break;
					}
				}
			}
		}
		else if (name.equals("JLT")) {
			if (rMgr.getRegister(rMgr.swReg) < 0) {
				if (mode == ind) {
					data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				}
				else 
					data = inst.target + x + loc;
				rMgr.setRegister(rMgr.pcReg, data);
				
				for (int i = 0; i < loader.inst.length(); i++) {
					if (loader.inst.getToken(i).addr == data) {
						instCnt = i;
						break;
					}
				}
			}
		}
		else if (name.equals("JSUB")) {
			rMgr.setRegister(rMgr.lReg, rMgr.getRegister(rMgr.pcReg));
			if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else 
				data = inst.target + x + loc;
			rMgr.setRegister(rMgr.pcReg, data);
			
			for (int i = 0; i < loader.inst.length(); i++) {
				if (loader.inst.getToken(i).addr == data) {
					instCnt = i;
					break;
				}
			}
		}
		else if (name.equals("LDA")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, data);
		}
		else if (name.equals("LDB")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.bReg, data);
		}
		else if (name.equals("LDCH")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 1));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 1));
			}
			data &= chmask;
			rMgr.setRegister(rMgr.aReg, data);
		}
		else if (name.equals("LDF")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 5));
			}
			rMgr.setRegister(rMgr.fReg, data);
		}
		else if (name.equals("LDL")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.lReg, data);
		}
		else if (name.equals("LDS")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.sReg, data);
		}
		else if (name.equals("LDT")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.tReg, data);
		}
		else if (name.equals("LDX")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.xReg, data);
		}
		if (name.equals("MUL")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.getRegister(rMgr.aReg) * data);			
		}
		else if (name.equals("MULF")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 5));
			}
			rMgr.setRegister(rMgr.fReg, rMgr.getRegister(rMgr.fReg) * data);	
		}
		else if (name.equals("MULR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			data = rMgr.getRegister(reg1) * rMgr.getRegister(reg2);
			rMgr.setRegister(reg2, data);
		}
		else if (name.equals("OR")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.getRegister(rMgr.aReg) | data);
		}
		else if (name.equals("RD")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 1));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 1));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.charToInt(rMgr.readDevice(data, 3)));
		}
		else if (name.equals("RMO")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			rMgr.setRegister(reg2, rMgr.getRegister(reg1));
		}
		else if (name.equals("RSUB")) {
			rMgr.setRegister(rMgr.pcReg, rMgr.getRegister(rMgr.lReg));
			
			data = rMgr.getRegister(rMgr.lReg);
			
			for (int i = 0; i < loader.inst.length(); i++) {
				if (loader.inst.getToken(i).addr == data) {
					instCnt = i;
					break;
				}
			}
		}
		else if (name.equals("RMO")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			rMgr.setRegister(reg2, rMgr.getRegister(reg1));
		}
		else if (name.equals("SHIFTL")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			rMgr.setRegister(rMgr.swReg, rMgr.getRegister(reg1) << reg2);
		}
		else if (name.equals("SHIFTL")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			rMgr.setRegister(rMgr.swReg, rMgr.getRegister(reg1) >> reg2);
		}
		else if (name.equals("STA")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.aReg)), 3);
		}
		else if (name.equals("STB")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.bReg)), 3);
		}
		else if (name.equals("STCH")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.aReg)&chmask), 3);
		}
		else if (name.equals("STF")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.fReg)&chmask), 5);
		}
		else if (name.equals("STL")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.lReg)), 3);
		}
		else if (name.equals("STS")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.sReg)), 3);
		}
		else if (name.equals("STSW")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.swReg)), 3);
		}
		else if (name.equals("STT")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.tReg)), 3);
		}
		else if (name.equals("STX")){
			if (mode == sim) {
				data = inst.target + x + loc;
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			rMgr.setMemory(data, rMgr.intToChar(rMgr.getRegister(rMgr.xReg)), 3);
		}
		if (name.equals("SUB")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			rMgr.setRegister(rMgr.aReg, rMgr.getRegister(rMgr.aReg) - data);			
		}
		else if (name.equals("SUBF")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 5));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 5));
			}
			rMgr.setRegister(rMgr.fReg, rMgr.getRegister(rMgr.fReg) - data);	
		}
		else if (name.equals("SUBR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			reg2 = Integer.valueOf(inst.name.substring(3,4), 16);
			data = rMgr.getRegister(reg2) - rMgr.getRegister(reg1);
			rMgr.setRegister(reg2, data);
		}
		else if (name.equals("TD")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 1));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 1));
			}
			rMgr.testDevice(data);
		}
		else if (name.equals("TIX")) {
			rMgr.setRegister(rMgr.xReg, rMgr.getRegister(rMgr.xReg)+1);
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 3));
			}
			
			rMgr.setRegister(rMgr.swReg, data-rMgr.getRegister(rMgr.xReg));
		}
		else if (name.equals("TIXR")) {
			reg1 = Integer.valueOf(inst.name.substring(2,3), 16);
			rMgr.setRegister(rMgr.xReg, rMgr.getRegister(rMgr.xReg)+1);
			rMgr.setRegister(rMgr.swReg, rMgr.getRegister(rMgr.xReg)-rMgr.getRegister(reg1));
		}
		else if (name.equals("WD")) {
			if (mode == sim) {
				data = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 1));
			}
			else if (mode == imm) {
				data = inst.target;
			}
			else if (mode == ind) {
				target = rMgr.charToInt(rMgr.getMemory(inst.target + x + loc, 3));
				data = rMgr.charToInt(rMgr.getMemory(target, 1));
			}
			rMgr.writeDevice(data, rMgr.intToChar(rMgr.getRegister(rMgr.aReg)), 3);
		}
		
		return name;
	}
	
	/**
	 * 남은 모든 instruction이 수행된 모습을 보인다.
	 */
	public void allStep(SicLoader loader) {
		
	}
	
	/**
	 * 각 단계를 수행할 때 마다 관련된 기록을 남기도록 한다.
	 */
	public void addLog(String log) {
		
	}	
}
