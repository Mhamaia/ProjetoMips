import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.math.BigInteger;

/*POR: GRUPO F
Anizio Silva
Marcelo Maia
Matheus Silva
Samara Freitas

Parte 2
*/
public class App {
    public static void main(String[] args) throws Exception {

        // assembly
        // System.out.println(hexToBin("0x00430820"));
        // A casse BinToAs recebe um número em binário e retorna seu correspondente em
        // Assembly
        int[] reg = new int[32];
        BinToAs assembly = new BinToAs(reg); 
        String nome = "entrada.txt"; //arquivo de entreda com os números em octal

        try {
            // carrega o arquivo de entrada para leitura
            FileReader entrada = new FileReader(nome);
            BufferedReader lerArq = new BufferedReader(entrada);

            // cria um arquivo de saída
            FileWriter saida = new FileWriter("saida.txt");
            PrintWriter gravarArq = new PrintWriter(saida);
            

            String linha = lerArq.readLine();
            // lê a primeira linha
            // a variável "linha" recebe o valor "null" quando o processo
            // de repetição atingir o final do arquivo texto

            while (linha != null) {
                // imprime no terminal a saída
                System.out.printf("%s\n", assembly.IdentifyOpcode(hexToBin(linha)));
                // escreve a saída no arquivo criado

                gravarArq.printf("%s\n", assembly.IdentifyOpcode(hexToBin(linha)));
                linha = lerArq.readLine(); // lê da segunda até a última linha
            }

            // fecha os dois arquivos
            entrada.close();
            saida.close(); 
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }

        System.out.println();

    }

    // Strign formatada, recebe uma string em hex, trasnforma em binário através da
    // calsse BigInteger
    // Recebe uma String em Hexadecima e retorna uma String em binário com 32bits
    // ex: hexToBin("0x3c010064") returns "00111100000000010000000001100100"
    public static String hexToBin(String hex) {

        hex = hex.replaceFirst("0x", "");
        String binario = new BigInteger(hex, 16).toString(2);
        while (binario.length() < 32) {

            binario = "0" + binario;

        }

        return binario;
    }
}
