package SP19_simulator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * ResourceManager는 컴퓨터의 가상 리소스들을 선언하고 관리하는 클래스이다.
 * 크게 네가지의 가상 자원 공간을 선언하고, 이를 관리할 수 있는 함수들을 제공한다.<br><br>
 * 
 * 1) 입출력을 위한 외부 장치 또는 device<br>
 * 2) 프로그램 로드 및 실행을 위한 메모리 공간. 여기서는 64KB를 최대값으로 잡는다.<br>
 * 3) 연산을 수행하는데 사용하는 레지스터 공간.<br>
 * 4) SYMTAB 등 simulator의 실행 과정에서 사용되는 데이터들을 위한 변수들. 
 * <br><br>
 * 2번은 simulator위에서 실행되는 프로그램을 위한 메모리공간인 반면,
 * 4번은 simulator의 실행을 위한 메모리 공간이라는 점에서 차이가 있다.
 */
public class ResourceManager{
	/**
	 * 디바이스는 원래 입출력 장치들을 의미 하지만 여기서는 파일로 디바이스를 대체한다.<br>
	 * 즉, 'F1'이라는 디바이스는 'F1'이라는 이름의 파일을 의미한다. <br>
	 * deviceManager는 디바이스의 이름을 입력받았을 때 해당 이름의 파일 입출력 관리 클래스를 리턴하는 역할을 한다.
	 * 예를 들어, 'A1'이라는 디바이스에서 파일을 read모드로 열었을 경우, hashMap에 <"A1", scanner(A1)> 등을 넣음으로서 이를 관리할 수 있다.
	 * <br><br>
	 * 변형된 형태로 사용하는 것 역시 허용한다.<br>
	 * 예를 들면 key값으로 String대신 Integer를 사용할 수 있다.
	 * 파일 입출력을 위해 사용하는 stream 역시 자유로이 선택, 구현한다.
	 * <br><br>
	 * 이것도 복잡하면 알아서 구현해서 사용해도 괜찮습니다.
	 */
	HashMap<Integer,Object> deviceManager = new HashMap<Integer,Object>();
	char[] memory = new char[65536]; // String으로 수정해서 사용하여도 무방함.
	int[] register = new int[10];
	Integer curdev = null;
	SymbolTable symtabList;
	
	public static final int aReg=0;
	public static final int xReg=1;
	public static final int lReg=2;
	public static final int bReg=3;
	public static final int sReg=4;
	public static final int tReg=5;
	public static final int fReg=6;
	public static final int pcReg=7;
	public static final int swReg=8;
	// 이외에도 필요한 변수 선언해서 사용할 것.

	/**
	 * 메모리, 레지스터등 가상 리소스들을 초기화한다.
	 */
	public void initializeResource(){
		for(int i = 0; i < memory.length; i++) {
			memory[i] = 0;
		}
		
		for (int i = 0; i < register.length; i++) {
			register[i] = 0;
		}
	}
	
	/**
	 * deviceManager가 관리하고 있는 파일 입출력 stream들을 전부 종료시키는 역할.
	 * 프로그램을 종료하거나 연결을 끊을 때 호출한다.
	 */
	public void closeDevice() {
		deviceManager.clear();
	}
	
	/**
	 * 디바이스를 사용할 수 있는 상황인지 체크. TD명령어를 사용했을 때 호출되는 함수.
	 * 입출력 stream을 열고 deviceManager를 통해 관리시킨다.
	 * @param devName 확인하고자 하는 디바이스의 번호,또는 이름
	 */
	public void testDevice(Integer devName) {
		if (deviceManager.get(devName) == null)  {
			deviceManager.put(devName, new Device());
			register[swReg] = 0;
		}
		else {
			register[swReg] = 1;
			curdev = devName;
		}
	}

	/**
	 * 디바이스로부터 원하는 개수만큼의 글자를 읽어들인다. RD명령어를 사용했을 때 호출되는 함수.
	 * @param devName 디바이스의 이름
	 * @param num 가져오는 글자의 개수
	 * @return 가져온 데이터
	 */
	public char[] readDevice(Integer devName, int num){
		Device device = (Device) deviceManager.get(devName);
		return device.read(num);
	}

	/**
	 * 디바이스로 원하는 개수 만큼의 글자를 출력한다. WD명령어를 사용했을 때 호출되는 함수.
	 * @param devName 디바이스의 이름
	 * @param data 보내는 데이터
	 * @param num 보내는 글자의 개수
	 */
	public void writeDevice(Integer devName, char[] data, int num){
		Device device = (Device) deviceManager.get(devName);
		device.write(data, num);
	}
	
	/**
	 * 메모리의 특정 위치에서 원하는 개수만큼의 글자를 가져온다.
	 * @param location 메모리 접근 위치 인덱스
	 * @param num 데이터 개수
	 * @return 가져오는 데이터
	 */
	public char[] getMemory(int location, int num){
		char[] buf = new char[num];
		for(int i = 0; i < num; i++) {
			buf[i] = memory[location + i];
			System.out.println("getMemory at " + String.format("%06X", location+i));
		}
		System.out.println(String.format("%06X", this.charToInt(buf)));
		return buf;
	}

	/**
	 * 메모리의 특정 위치에 원하는 개수만큼의 데이터를 저장한다. 
	 * @param locate 접근 위치 인덱스
	 * @param data 저장하려는 데이터
	 * @param num 저장하는 데이터의 개수
	 */
	public void setMemory(int locate, char[] data, int num){
		for(int i = 0; i < num && i < data.length; i++) {
			memory[locate + (num - data.length) + i] = data[i];
			System.out.print("setMemory " +String.format("%06X", locate + (num - data.length) + i) + " "  +  " " + String.format("%02X", (int)memory[locate + (num - data.length) + i]) + " ");
		}
		System.out.println();
	}

	/**
	 * 번호에 해당하는 레지스터가 현재 들고 있는 값을 리턴한다. 레지스터가 들고 있는 값은 문자열이 아님에 주의한다.
	 * @param regNum 레지스터 분류번호
	 * @return 레지스터가 소지한 값
	 */
	public int getRegister(int regNum){
		
		return register[regNum];
	}

	/**
	 * 번호에 해당하는 레지스터에 새로운 값을 입력한다. 레지스터가 들고 있는 값은 문자열이 아님에 주의한다.
	 * @param regNum 레지스터의 분류번호
	 * @param value 레지스터에 집어넣는 값
	 */
	public void setRegister(int regNum, int value){
		register[regNum] = value;
	}

	/**
	 * 주로 레지스터와 메모리간의 데이터 교환에서 사용된다. int값을 char[]형태로 변경한다.
	 * @param data
	 * @return
	 */
	public char[] intToChar(int data){
		char buf[];
		int i = 0, tmp = data;
		for (; tmp / 256 > 0; tmp /= 256, i++) {}
		i++;
		buf = new char[i];
		for (int j = 0; j < i; j++, data /= 256) {
			 buf[i-j-1] = (char) (data & 255);
		}
		return buf;
	}

	/**
	 * 주로 레지스터와 메모리간의 데이터 교환에서 사용된다. char[]값을 int형태로 변경한다.
	 * @param data
	 * @return
	 */
	public int charToInt(char[] data){
		int n = 0;
		for (int i = 0; i < data.length; i++) {
			n += data[i] << 8 * (data.length - (i+1));
		}
		return n;
	}
}

class Device {
	char[] buf;
	int index;
	public Device() {
		buf = new char[100];
		for (int i = 0; i < 3; i++)
			buf[i] = (char)i;
		index = 0;
	}
	
	char[] read(int n) {
		char[] buffer = new char[n];
		for (int i = 0; i < n; i++) {
			buffer[i] = buf[index++];
		}
		return buffer;
	}
	
	void write(char[] buffer, int n) {
		for (int i = 0; i < n && i < buffer.length; i++) {
			buf[index++] = buffer[i];
		}
	}
}