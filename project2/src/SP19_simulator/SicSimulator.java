package SP19_simulator;

import java.io.File;

/**
 * �ùķ����ͷμ��� �۾��� ����Ѵ�. VisualSimulator���� ������� ��û�� ������ �̿� ����
 * ResourceManager�� �����Ͽ� �۾��� �����Ѵ�.  
 * 
 * �ۼ����� ���ǻ��� : <br>
 *  1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ���� ������ ��.<br>
 *  2) �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.<br>
 *  3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.<br>
 *  4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)<br>
 * 
 * <br><br>
 *  + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� �� �ֽ��ϴ�.
 */
public class SicSimulator {
	ResourceManager rMgr;
	int instCnt = 0;
	final int nmask = 32, imask = 16, xmask = 8, bmask = 4, pmask = 2, emask = 1, chmask = 255;
	final int minus = 2048;
	final int sim = 0, ind = 1, imm = 2;
	public SicSimulator(ResourceManager resourceManager) {
		// �ʿ��ϴٸ� �ʱ�ȭ ���� �߰�
		this.rMgr = resourceManager;
	}

	/**
	 * ��������, �޸� �ʱ�ȭ �� ���α׷� load�� ���õ� �۾� ����.
	 * ��, object code�� �޸� ���� �� �ؼ��� SicLoader���� �����ϵ��� �Ѵ�. 
	 */
	public void load(File program) {
		/* �޸� �ʱ�ȭ, �������� �ʱ�ȭ ��*/
		//rMgr.initializeResource();
	}

	/**
	 * 1���� instruction�� ����� ����� ���δ�. 
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
	 * ���� ��� instruction�� ����� ����� ���δ�.
	 */
	public void allStep(SicLoader loader) {
		
	}
	
	/**
	 * �� �ܰ踦 ������ �� ���� ���õ� ����� ���⵵�� �Ѵ�.
	 */
	public void addLog(String log) {
		
	}	
}
