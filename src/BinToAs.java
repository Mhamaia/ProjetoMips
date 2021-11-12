
import java.util.HashMap;
import java.util.Map;

//Essa classe vai converter de binário para Assembly
public class BinToAs {
	public String bin;
	public String op;
	public String rs;
	public String rt;
	public String rd;
	public String sh;
	public String fn;
	public String operand;
	public String jta;

	//variaveis hi e lo, usadas na divisão e multiplicação 
	protected int hi = 0;
	protected int lo = 0;

	// array de reg, onde Regitradores[0] = $0 ...
	public int[] reg = new int[32];
	

	public BinToAs(int[] reg) {
		this.reg = reg;

		//REgistradores de acordo com a entrada da professora no exemplo de saída
		/*
		reg[0] = 0;
		reg[1] = 0;
		reg[2] = 6;
		reg[3] = 5;
		reg[4] = 8;
		reg[5] = 10;
		reg[6] = 12;
		reg[7] = 15;
		reg[8] = 2;
		reg[9] = 8;
		reg[10] = 11;
		reg[11] = 4;
		reg[12] = 5;
		reg[13] = 0;
		reg[14] = 0;
		reg[15] = 0;
		reg[16] = 0;
		reg[17] = 0;
		reg[18] = 0;
		reg[19] = 0;
		reg[20] = 0;
		reg[21] = 0;
		reg[22] = 0;
		reg[23] = 0;
		reg[24] = 0;
		reg[25] = 0;
		reg[26] = 0;
		reg[27] = 0;
		reg[28] = 0;
		reg[29] = 0;
		reg[30] = 0;
		reg[31] = 0;
		*/

		//inicializa os registradores com 0
		for (int i = 0; i < reg.length; i++){
			reg[i] = 0;
		}
	}


	// Separa a parte do OPcode da String
	public String separaOP(String bin) {
		String bin_op = bin.substring(0, 6);
		return bin_op;

	}

	// R e I - separa rs da String
	public String separaRS(String bin) {
		String bin_rs;
		bin_rs = bin.substring(6, 11);
		return bin_rs;
	}

	// R e I - separa rt da String
	public String separaRT(String bin) {
		String bin_rt = bin.substring(11, 16);
		return bin_rt;
	}

	// R - separa rd da String
	public String separaRD(String bin) {
		String bin_rd = bin.substring(16, 21);
		return bin_rd;
	}

	// R - separa sh da String
	public String separaSH(String bin) {
		String bin_sh = bin.substring(21, 26);
		return bin_sh;
	}

	// Separa a parte fn da String
	public String separaFN(String bin) {
		String bin_fn = bin.substring(26, 32);
		return bin_fn;
	}

	// Separa a parte do operando da String
	public String separaOperand(String bin) {
		String bin_operand = bin.substring(16, 32);
		return bin_operand;
	}

	// Tipo J - separa o jump target adress
	public String separaJTA(String bin) {
		String bin_JTA = bin.substring(6, 32);
		return bin_JTA;
	}

	// Identifica que tipo de operação é pelo OPcode
	public String IdentifyOpcode(String bin) {
		String bin_op = separaOP(bin); // chama o método separaOP para separar o OP code do resto da String
		if (bin_op.equals("000000")) // Se o Opcode "000000 então ou é syscall, ou uma instrução do tipo R"
		{
			// verifica se é syscall
			if (bin.substring(6, 32).equals("00000000000000000000001100")) {
				return DictSyscall("00000000000000000000001100") + ""; // DictSyscall retornará o código em Assembly
			} else
				// se não é syscall, então é R
				// separa todas as partes da instrução R
				op = bin_op;
			rs = separaRS(bin);
			rt = separaRT(bin);
			rd = separaRD(bin);
			sh = separaSH(bin);
			fn = separaFN(bin);
			return toAssemblyR(rs, rt, rd, sh, fn); // Envia cada parte para o método toAssemblyR, que irá retorna o
													// código em Assembly
		} else if (bin_op.equals("000010") | bin_op.equals("000011")) {
			// verifica se é uma instrução do tipo J
			// separa o opcode do jump target adress
			op = bin_op;
			jta = separaJTA(bin);
			return toAssemblyJ(op, jta); // Envia cada parte para o método toAssemblyJ, que irá retorna o código em
											// Assembly
		} else {
			// Se não é syscall, R ou J, é instrução do tipo I.
			// separa as partes de acordo com a instrução tipo I.
			op = bin_op;
			rs = separaRS(bin);
			rt = separaRT(bin);
			operand = separaOperand(bin);
			return toAssemblyI(op, rs, rt, operand); // Envia as partes para o método toAssemblyI, que irá retorna o
														// código em Assembly
		}
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos R e
	// retorna os devidos comandos
	public String toAssemblyR(String rs, String rt, String rd, String sh, String fn) {
		String assembly = "";
		// se o fn da instrução é igual a x
		// então a instrução terá esse modelo
		if (fn.equals("001000")) {
			assembly = DictR(fn) + " " + DictR(rs) + "";
		} else if (fn.equals("010000") || fn.equals("010010") || fn.equals("001000")) {
			assembly = DictR(fn) + " " + DictR(rd) + "";
		} else if (fn.equals("011000") || fn.equals("011010") || fn.equals("011011") || fn.equals("011001")) {
			assembly = DictR(fn) + " " + DictR(rs) + ", " + DictR(rt) + "";
		} else if (fn.equals("000010") || fn.equals("000011") || fn.equals("000000")) {
			assembly = DictR(fn) + " " + DictR(rd) + ", " + DictR(rt) + ", " + DictR(sh) + "";
		} else
			assembly = DictR(fn) + " " + DictR(rd) + ", " + DictR(rs) + ", " + DictR(rt) + DictR(sh) + "";

		return assembly + exeIntrucaoR(this.fn, this.rs, this.rd, this.rt, this.sh); // retorna o comando em Assembly
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos I e
	// retorna os devidos comandos
	public String toAssemblyI(String op, String rs, String rt, String operand) {
		int numero = Integer.parseInt(operand, 2); // Converte o número do operando de binário para decimal
		String assembly = "";
		
		// Se o opcode foi igual a x
		// Então a instrução terá esse modelo
		if (op.equals("001111")) {
			assembly = DictI(op) + " " + DictI(rt) + ", " + numero + "";
		} else if (op.equals("000001")) {
			assembly = DictI(op) + " " + DictI(rs) + ", " + "start";
		} else if (Integer.parseInt(op, 2) >= 32 && Integer.parseInt(op, 2) <= 43) {
			assembly = DictI(op) + " " + DictI(rt) + ", " + numero + "(" + DictI(rs) + ")";
		} else if (op.equals("000101") || op.equals("000100")) {
			assembly = DictI(op) + " " + DictI(rs) + ", " + DictI(rt) + ", start";
		} else
			assembly = DictI(op) + " " + DictR(rt) + ", " + DictR(rs) + ", " + numero + "";

		return assembly + exeIntrucaoI(this.op, this.rs, this.rt, this.operand); // Retora a instrução em Assembly
	}

	// recebe os valores separados em binário, consulta o dicionario de comandos J e
	// retorna os devidos comandos
	public String toAssemblyJ(String op, String jta) {
		// As instruções do tipo J seguem o seguinte modelo:
		String assembly = DictJ(op) + " " + "start";

		return assembly;
	}

	// dicionário de comandos R a key é o codigo em binário, o value é o comando em
	// Assembly
	public String DictR(String bin) {
		// Dicionário do tipo R
		// Recebe uma String em binário e retorna seu correspondente em Assembly, de
		// acordo com o tipo R
		Map<String, String> dicr = new HashMap<String, String>();
		dicr.put("00001", "$1");
		dicr.put("00011", "$3");
		dicr.put("00010", "$2");
		dicr.put("00000", "");
		dicr.put("100000", "add");
		dicr.put("100001", "addu");
		dicr.put("100100", "and");
		dicr.put("011010", "div");
		dicr.put("011011", "divu");
		dicr.put("001000", "jr");
		dicr.put("010000", "mfhi");
		dicr.put("010010", "mflo");
		dicr.put("011000", "mult");
		dicr.put("011001", "multu");
		dicr.put("100111", "nor");
		dicr.put("100101", "or");
		dicr.put("000000", "sll");
		dicr.put("000100", "sllv");
		dicr.put("101010", "slt");
		dicr.put("000011", "sra");
		dicr.put("000111", "srav");
		dicr.put("000010", "srl");
		dicr.put("000110", "srlv");
		dicr.put("100010", "sub");
		dicr.put("100011", "subu");
		dicr.put("100110", "xor");
		dicr.put("01010", "10");

		return dicr.get(bin); // Retorna o comando em Assembly
	}

	// dicionário de comandos I a key é o codigo em binário, o value é o comando em
	// Assembly
	public String DictI(String bin) {
		// Dicionário do tipo I
		// Recebe uma String em binário e retorna seu correspondente em Assembly, de
		// acordo com o tipo I
		Map<String, String> dici = new HashMap<String, String>();
		/*
		 * Falta entender o start, já que varia de entrada para entrada dici.put("",
		 * "start");
		 */
		dici.put("00001", "$1");
		dici.put("00010", "$2");
		dici.put("001000", "addi");
		dici.put("001001", "addiu");
		dici.put("001100", "andi");
		dici.put("000111", "bgtz");
		dici.put("000100", "beq");
		dici.put("000001", "bltz");
		dici.put("000110", "blez");
		dici.put("000101", "bne");
		dici.put("100000", "lb");
		dici.put("100100", "lbu");
		dici.put("001111", "lui");
		dici.put("100011", "lw");
		dici.put("001101", "ori");
		dici.put("101000", "sb");
		dici.put("001010", "slti");
		dici.put("101011", "sw");
		dici.put("001110", "xori");

		return dici.get(bin);
	}

	// dicionário de comandos J a key é o codigo em binário, o value é o comando em
	// Assembly
	public String DictJ(String bin) {
		// Dicionário do tipo J
		// Recebe uma String em binário e retorna seu correspondente em Assembly, de
		// acordo com o tipo J
		Map<String, String> dicj = new HashMap<String, String>();
		/*
		 * Falta entender o start, já que varia de entrada para entrada dicj.put("",
		 * "start");
		 */
		dicj.put("000010", "j");
		dicj.put("000011", "jal");

		return dicj.get(bin);
	}

	// dicionario com comando Syscall
	public String DictSyscall(String bin) {
		// Dicionário do tipo syscall
		// Recebe uma String em binário e retorna seu correspondente em Assembly
		Map<String, String> dicsyscall = new HashMap<String, String>();
		dicsyscall.put("00000", "");
		dicsyscall.put("00000000000000000000001100", "syscall");

		return dicsyscall.get(bin);
	}


	//identificador de instruções tipo R
	public String exeIntrucaoR(String avalia, String rs, String rd, String rt, String sh){
		int reg1 = Integer.parseInt(rs, 2);
		int regD = Integer.parseInt(rd, 2);
		int reg2 = Integer.parseInt(rt, 2);
		int shift = Integer.parseInt(sh, 2);
		
		String retorno = "";

		switch (avalia){
			case "100000": //add
				this.add(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;

			case "100010" : //sub
				this.sub(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;

			case "101010": //slt
				this.slt(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;

			case "100100": //and
				this.add(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;

			case "010000": //mfhi
				this.mfhi(regD);
				retorno = this.imprimeRegs();
				break;

			case "010010": //mflo
				this.mflo(regD);
				retorno = this.imprimeRegs();
				break;

			case "011010": //div
				this.div(reg1, reg2);
				retorno = this.imprimeRegs();
				break;

			case "000011": //sra
				this.sra(regD, reg2, shift);
				retorno = this.imprimeRegs();
			
			case "000111": //srav
				this.srav(regD, reg2, reg1);
				retorno = this.imprimeRegs();
				break;

			case "100001": //addu
				this.addu(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "100101": //or
				this.or(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "100110" : //xor
				this.xor(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "100111" : //nor
				this.nor(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "100011": //subu
				this.subu(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "011000": //mult
				this.mult(reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "011001": //multu
				this.mult(reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "011011": // divu
				this.divu(reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "000000": //sll
				this.sll(regD, reg1, shift);
				retorno = this.imprimeRegs();
				break;
			
			case "000010": //srl
				this.srl(regD, reg1, shift);
				retorno = this.imprimeRegs();
				break;
			
			case "000100": //sllv
				this.sllv(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
			
			case "000110": //srlv
				this.srlv(regD, reg1, reg2);
				retorno = this.imprimeRegs();
				break;
				
		}

	 return retorno;
	}

	//identificador de instruções tipo I
    public String exeIntrucaoI(String op, String rs, String rt, String operand){
		int reg1 = Integer.parseInt(rs, 2);
		int regD = Integer.parseInt(rt, 2);
		int imediato = Integer.parseInt(operand, 2);

		String retorno = "";

		switch (op) {
			case "001000": //addi
				this.addi(regD, reg1, imediato);
				retorno = this.imprimeRegs();
				break;

			case "001010": // slti
				this.slti(regD, reg1, imediato);
				retorno = this.imprimeRegs();
				break;

			case "001100": // andi
				this.andi(regD, reg1, imediato);
				retorno = this.imprimeRegs();
				break;

			case "001101": //ori
				this.ori(regD, reg1, imediato);
				retorno = this.imprimeRegs();
				break;
			
			case "001110": //xori
				this.xori(regD, reg1, imediato);
				retorno = this.imprimeRegs();
				break;
			
			case "001001": //addiu
				this.addiu(regD, reg1, imediato);
				retorno = this.imprimeRegs();
				break;
		}
	    return retorno;
    }


	//Intruções Lógicas e Aritméticas 

		//soma com overflow
	public void add(int destino, int fonte1, int fonte2){
		if ((reg[fonte1] + reg[fonte2] > Integer.MAX_VALUE) || (reg[fonte1] + reg[fonte2] < Integer.MIN_VALUE)){
			reg[destino] = reg[destino] + 0; // se a soma causa overflow, a intrução não faz nada 
		} else 
			reg[destino] = reg[fonte1] + reg[fonte2]; // senão soma normalmete 
	}

		//subtração com overflow
	public void sub(int destino, int fonte1, int fonte2){

		if (((reg[fonte1] - reg[fonte2]) > Integer.MAX_VALUE) || ((reg[fonte1] - reg[fonte2]) < Integer.MIN_VALUE)){

			reg[destino] = reg[destino] - 0; // se a subtração causa overflow, a intrução não faz nada 
		} else 
			reg[destino] = reg[fonte1] - reg[fonte2]; // senão subtrai normalmete 
	}

		// menor que 
	public void slt(int destino, int fonte1, int fonte2){
		if (reg[fonte1] < reg[fonte2]){
			reg[destino] = 1;
		} else 
			reg[destino] = 0;
	}


		// AND lógico
			//tabela verdade
			// 0 and 0 = 0
			// 0 and 1 = 0
			// 1 and 0 = 0
			// 1 and 1 = 1
	public void and(int destino, int fonte1, int fonte2){
		String fonte1String = setTamanho(Integer.toBinaryString(reg[fonte1]), 5); //transforma o valor que está no registrador fonte em binário
		String fonte2String = setTamanho(Integer.toBinaryString(reg[fonte2]), 5);
		String andResult = ""; //essa string vai receber o resultado do and em binário
		for(int i = 0; i < 5; i++) //esse for vai comparar bit por bit
		{
			if(fonte1String.charAt(i) == '1' && fonte2String.charAt(i) == '1')
			{
				//se os bits forem 1 e 1, soma 1
				andResult = andResult + "1"; 

			}else{
				//se não for o soma 0
				andResult = andResult + "0";
			}
		}
		reg[destino] = Integer.parseInt(andResult, 2); //o registrador de destino recebe o valor do and em decimal
	}

	// Tabela verdade:
	// 0 or 0 = 0
	// 0 or 1 = 1
	// 1 or 0 = 1
	// 1 or 1 = 1
	public void or(int destino, int fonte1, int fonte2) {
		String fonte1String = setTamanho(Integer.toBinaryString(reg[fonte1]), 5); // transforma o valor que está no
																				// registrador fonte em binário
		String fonte2String = setTamanho(Integer.toBinaryString(reg[fonte2]), 5);
		String orResult = ""; // essa string vai receber o resultado do or em binário
		for (int i = 0; i < 5; i++) // esse for vai comparar bit por bit
		{
			if (fonte1String.charAt(i) == '0' && fonte2String.charAt(i) == '0') {
				// se os bits equivalentes o resultado é 0
				orResult = orResult + "0";

			} else {
				// se não for o resultador é 1
				orResult = orResult + "1";
			}
		}
		reg[destino] = Integer.parseInt(orResult, 2); // o registrador de destino recebe o valor do or em binário
	}

	// XOR é um or exclusivo
	// Tabela verdade:
	// 0 xor 0 = 0
	// 0 xor 1 = 1
	// 1 xor 0 = 1
	// 1 xor 1 = 0
	public void xor(int destino, int fonte1, int fonte2) {
		String fonte1String = Integer.toBinaryString(reg[fonte1]); // transforma o valor que está no registrador fonte
																	// em binário
		String fonte2String = Integer.toBinaryString(reg[fonte2]);
		fonte1String = setTamanho(fonte1String, 5); // pega esse valor em binário e transforma em 5 bits
		fonte2String = setTamanho(fonte2String, 5);
		String xorResult = ""; // essa string vai receber o resultado do or em binário
		for (int i = 0; i < 5; i++) // esse for vai comparar bit por bit
		{
			if ((fonte1String.charAt(i) == '0' && fonte2String.charAt(i) == '0')
					| (fonte1String.charAt(i) == '1' && fonte2String.charAt(i) == '1')) {
				// se os bits equivalentes o resultado é 0
				xorResult = xorResult + "0";

			} else {
				// se não for o resultador é 1
				xorResult = xorResult + "1";
			}
		}

		reg[destino] = Integer.parseInt(xorResult, 2); // o registrador de destino recebe o valor do xor em binário

	}


    //NOT( a OR b)
	public void nor(int destino, int fonte1, int fonte2)
	{
		or(destino, fonte1, fonte2);
		reg[destino] = (reg[destino] * (-1)) - 1;
		
	}


	public void mfhi(int destino){
		reg[destino] = hi; // coloca o registrador como = hi
	}

	public void mflo(int destino){
		reg[destino] = lo; // coloca o registrador como = lo
	}
		//adição sem overflow
	public void addu(int destino, int fonte1, int fonte2){
		reg[destino] = reg[fonte1] + reg[fonte2]; // realiza a soma sem qualquer tipo de cirtério
	}

		//subtração sem overflow
	public void subu(int destino, int fonte1, int fonte2){
		reg[destino] = reg[fonte1] - reg[fonte2]; // realiza a subtração sem qualeur tipo de critério
	}

		//multiplicação com overflow
	public void mult(int fonte1, int fonte2){ 
		// hi recebe a palavra de mais significado do produto 
		 hi = 0; 
		//lo recebe resultado ou a palavra menos significativa do produto 
		if ((reg[fonte1] * reg[fonte2] > Integer.MAX_VALUE) || (reg[fonte1] * reg[fonte2] < Integer.MIN_VALUE)){
			lo = lo * 1;// se a multiplicação causa overflow, a intrução não faz nada 
		} else 
		    lo = reg[fonte1] * reg[fonte2]; 
			  
	}

		//multiplicação sem overflow
	public void multu(int destino, int fonte1, int fonte2){
		hi = 0;
		lo = reg[fonte1] * reg[fonte2];
	}

		//divisão com overflow
	public void div(int fonte1, int fonte2){
		try {
			if ((reg[fonte1] / reg[fonte2] > Integer.MAX_VALUE) || (reg[fonte1] / reg[fonte2] < Integer.MIN_VALUE)){
		           hi = reg[fonte1] % reg[fonte2]; // hi recebe o resto 
		           lo = (reg[fonte1] / reg[fonte2]);//lo recebe o quociente
			} else 
				lo = 0; 		
		} catch (ArithmeticException e) { // se ocorrer a divisão por zero 
				lo = 0;
				hi = 0;
		}
	}

		//divisão sem overflow
	public void divu(int fonte1, int fonte2){
		try {
			lo = (reg[fonte1] / reg[fonte2]);
			hi = reg[fonte1] % reg[fonte2];
		} catch (ArithmeticException e) { // caso da divisão por zero
			lo = 0;
			hi = 0;
		}
	}

	// shift left logical
	public void sll(int destino, int fonte1, int shift)
	{
		String fonte1String = Integer.toBinaryString(reg[fonte1]); //transforma o valor do registrador 1 em binário
		for(int i = 0; i < shift; i++) //enquanto i for menor que o shift,
		{
			fonte1String = fonte1String + "0"; // adicionar 0 nos finais
		}
		fonte1 = Integer.parseInt(fonte1String, 2); //fonte1 recebe fonte1String(que está em binário) na forma decimal
		reg[destino] = fonte1; //registrador de destino recebe o valor após o shift
	}

	// shift right logical
	public void srl(int destino, int fonte1, int shift)
	{
		String fonte1String = Integer.toBinaryString(reg[fonte1]); //transforma o valor do registrador 1 em binário
		for(int i = 0; i < shift; i++) //enquanto i for menor que o shift,
		{
			fonte1String = "0" + fonte1String; // adicionar 0 no inicio
		}
		fonte1 = Integer.parseInt(fonte1String, 2); //fonte1 recebe fonte1String(que está em binário) na forma decimal
		reg[destino] = fonte1; //o registrador de destino recebe o valor após o shift

	}

		//shift right aritmethic 
	public void sra(int destino, int fonte2, int shiftzada){
			int y = reg[fonte2] >> shiftzada; 
			reg[destino] = y;  
	}

	// shift left logical variable
	public void sllv(int destino, int fonte1, int fonte2)
	{
	  String fonte1String = Integer.toBinaryString(reg[fonte1]); //transforma o valor do registrador 1 em binário
	  for(int i = 0; i < reg[fonte2]; i++) //enquanto i for menor que o valor no registrador 2
	  {
		fonte1String = fonte1String + "0"; // 0 são adicionados no final
	  }
	  fonte1 = Integer.parseInt(fonte1String, 2); //fonte1 recebe o valor de fonte1String(que está em binário) em decimal
	  reg[destino] = fonte1; //registrador destino recebe o valor após o shift
	}
  
  
	// shift right logical variable
	public void srlv(int destino, int fonte1, int fonte2)
	{
	  String fonte1String = Integer.toBinaryString(reg[fonte1]); //transforma o valor do registrador 1 em binário
  
  
	  for(int i = 0; i < reg[fonte2]; i++) //enquanto i for menor que o valor no registrador 2
	  {
		fonte1String = "0" + fonte1String; // 0 são adicionados no inicio
	  }
	  fonte1 = Integer.parseInt(fonte1String, 2); //fonte1 recebe o valor de fonte1String(que está em binário) em decimal
	  reg[destino] = fonte1; //registrador destino recebe o valor após o shift
  
	}
	   //shift right aritmethic variable 
	public void srav(int destino, int fonte2, int fonte1){ 
		int y = reg[fonte2] >> reg[fonte1];  
		reg[destino] = y;   
	}

	   //adição imediata
	public void addi(int destino, int fonte1, int imediato){
		if ((reg[fonte1] + imediato > Integer.MAX_VALUE) || (reg[fonte1] + imediato < Integer.MIN_VALUE)){
			reg[destino] = reg[destino] + 0; // se a soma causa overflow, a intrução não faz nada 
		} else 
			reg[destino] = reg[fonte1] + imediato; // senão soma normalmete 
	}

	   //menor que imediato
	public void slti(int destino, int fonte1, int imediato){
		if (reg[fonte1] < imediato){
			reg[destino] = 1;
		}else
		 	reg[destino] = 0;
	}

	//AND imediato 
	public void andi(int destino, int fonte1, int imediato) {
		
		String fonte1String = Integer.toBinaryString(reg[fonte1]); // transforma o valor que está no registrador 1 em binário
		String imediatoString = Integer.toBinaryString(imediato); // transforma o valor que está no imediato em binário
		if(fonte1String.length() > imediatoString.length()) // se o número de bits no registrador 1 for maior que o imediato
		{
			imediatoString = setTamanho(imediatoString, fonte1String.length()); //são adicionados 0 ao imediato para que ele tenha tamanho igual ao registrador 1
		}else if(fonte1String.length() < imediatoString.length()) //se o registrador 1 é menor do que o imediato
		{
			fonte1String = setTamanho(fonte1String, imediatoString.length()); //são adicionados 0s no registrador 1 para que ele tenha tamanho igual ao do imediato
		}
			String andiResult = ""; //essa string vai receber o resultado do andi em binário 
			for(int i = 0; i < imediatoString.length(); i++) //esse for vai comparar bit por bit 
			{ 
			 if(fonte1String.charAt(i) == '1' && imediatoString.charAt(i) == '1') 
			 { 
			  //se os bits equivalentes adiciona 1
			  andiResult = andiResult + "1";  
		   
			 }else{ 
			  //se não for adiciona 0
			  andiResult = andiResult + "0"; 
			 } 
			} 
			reg[destino] = Integer.parseInt(andiResult, 2); //o registrador de destino recebe o valor do andi em binário 
		   
	}

	// XOR (OR exclusivo) imediato
	public void xori(int destino, int fonte1, int imediato) {
		String fonte1String = Integer.toBinaryString(reg[fonte1]); // transforma o valor que está no registrador 1 em binário
		String imediatoString = Integer.toBinaryString(imediato); //transforma o imediato em binário
		if(fonte1String.length() > imediatoString.length()) //se fonte1 em binário tem mais bit do que o imediato
		{
			imediatoString = setTamanho(imediatoString, fonte1String.length()); // são adicionados 0s ao imediato para igualar os tamanhos
		}else if(fonte1String.length() < imediatoString.length()) // se fonte1 em binário tem menos bits do que o imediato
		{
			fonte1String = setTamanho(fonte1String, imediatoString.length()); //são adicionados 0s a fonte 1 para igualar os tamanhos
		}
		String xorResult = ""; // essa string vai receber o resultado do xor em binário
		for (int i = 0; i < fonte1String.length(); i++) // esse for vai comparar bit por bit
		{
			if ((fonte1String.charAt(i) == '0' && imediatoString.charAt(i) == '0')
					|| (fonte1String.charAt(i) == '1' && imediatoString.charAt(i) == '1')) {
				// se os bits equivalentes o resultado é 0
				xorResult = xorResult + "0";

			} else {
				// se não for o resultador é 1
				xorResult = xorResult + "1";
			}
		}

		reg[destino] = Integer.parseInt(xorResult, 2); // o registrador de destino recebe o valor do xor em binário

	}

	// OR imediato
	public void ori(int destino, int fonte1, int imediato) {
		String fonte1String = Integer.toBinaryString(reg[fonte1]); // transforma o valor que está no registrador 1 em binário
		String imediatoString = Integer.toBinaryString(imediato); // transforma o valor que está no imediato em binário
		if(fonte1String.length() > imediatoString.length()) //se fonte1 em binário tiver mais bits que o imediato em bin
		{
			imediatoString = setTamanho(imediatoString, fonte1String.length()); //iguala os tamanhos de acordo com fonte1
		}else if(fonte1String.length() < imediatoString.length()) //caso contrário
		{
			fonte1String = setTamanho(fonte1String, imediatoString.length()); // iguala os tamanhos de acordo com o imediato
		}
		String orResult = ""; // essa string vai receber o resultado do ori em binário
		for (int i = 0; i < fonte1String.length(); i++) // esse for vai comparar bit por bit
		{
			if (fonte1String.charAt(i) == '0' && imediatoString.charAt(i) == '0') {
				// se os bits forem 0 e 0, o resultado é 0
				orResult = orResult + "0";

			} else {
				// se não for o resultador é 1
				orResult = orResult + "1";
			}
		}
		reg[destino] = Integer.parseInt(orResult, 2); // o registrador de destino recebe o valor do ori em decimal
	}

	  //adição imediata sem overflow
	public void addiu(int destino, int fonte1, int imediato){
		reg[destino] = reg[fonte1] + imediato; // realiza a soma do imediato sem verificar o overflow
	}

	// pega uma String de números binários e transforma em 5 bits
	// ex: Se fonte = 1, retorna 00001
	private String setTamanho(String fonte, int tamanho) {
		String zero = "0";
		
			for (int i = fonte.length(); i < tamanho; i++) {
				fonte = zero + fonte;
			}
		
		return fonte;
		

	}


	public String imprimeRegs(){
		String regsFormat = "\nREGS[";
		for (int i = 0; i < (reg.length -1); i++){ //avalia os n-1 elementos do array reg
			regsFormat = regsFormat + "$" + i + " = " + reg[i] +", "; // adiciona a string o simbolo dos registradores 
		}
		regsFormat = regsFormat + "$" + (reg.length - 1) + " = " + reg[reg.length - 1] + "]"; // quando sair do loop, o ultimo elemento tem um formatação doferente 
	return regsFormat;
	}

}
