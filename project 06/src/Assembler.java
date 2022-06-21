import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class Assembler {
    //1. Read Code and safe it in Arraylist
    //2. Initialize Symbol table
    //3. 1. Pass: Go through the entire source code and add all user-defined label symbols to the symbol table
    //              For each label (LABEL) add pair <LABEL,n> to the Hashmap
    //4. 2. Pass: Go again through the source code, and use the symbol table to translate all the commands
    //              Handle all the user defined variable symbols
    //              Check with Instruction (C or A)
    //              IF C:
    //              IF A:   1. IF @xxx is a number: use number
    //                      2. IF @xxx is a symbol: look it up: 1. IF symbol found: replace it with numeric value
    //                                                          2. IF not found: new VAR: ADD pair <xxx, n> to hashmap
    //                                                                                  n is next available RAM address
    private int ram_address = 16;
    //Array List for all code lines
    ArrayList<String> code = new ArrayList<>();
    //Hashmap for Symbol table
    HashMap<String, Integer> symbol_table = new HashMap<>();
    //Hashmap for Comp-Part of C-instruction
    HashMap<String, String> comp_instruction = new HashMap<>();
    //Init Hashmaps
    private void init_symbol() {
        for(int i=0;i<16;i++) {
            symbol_table.put("R"+ i, i);
        }
        symbol_table.put("SCREEN", 16384);
        symbol_table.put("KBD", 24576);
        symbol_table.put("SP", 0);
        symbol_table.put("LCL", 1);
        symbol_table.put("ARG", 2);
        symbol_table.put("THIS", 3);
        symbol_table.put("THAT", 4);
    }
    private void init_comp_instruction() {
        comp_instruction.put("0", "0101010");
        comp_instruction.put("1", "0111111");
        comp_instruction.put("-1", "0111010");
        comp_instruction.put("D", "0001100");
        comp_instruction.put("A", "0110000");
        comp_instruction.put("!D", "0001101");
        comp_instruction.put("!A", "0110001");
        comp_instruction.put("-D", "0001111");
        comp_instruction.put("-A", "0110011");
        comp_instruction.put("D+1", "0011111");
        comp_instruction.put("A+1", "0110111");
        comp_instruction.put("D-1", "0001110");
        comp_instruction.put("A-1", "0110010");
        comp_instruction.put("D+A", "0000010");
        comp_instruction.put("D-A", "0010011");
        comp_instruction.put("A-D", "0000111");
        comp_instruction.put("D&A", "0000000");
        comp_instruction.put("D|A", "0010101");
        comp_instruction.put("M", "1110000");
        comp_instruction.put("!M", "1110001");
        comp_instruction.put("M+1", "1110111");
        comp_instruction.put("M-1", "1110010");
        comp_instruction.put("D+M", "1000010");
        comp_instruction.put("D-M", "1010011");
        comp_instruction.put("M-D", "1000111");
        comp_instruction.put("D&M", "1000000");
        comp_instruction.put("D|M", "1010101");
    }
    //1.Pass - Read code in and safe it in List can be done simultaneously
    private void first_pass(String path){
        try {
            Scanner in = new Scanner(new File(path));
            int line=0;
            int string_length;
            while(in.hasNext()) {
                String current= in.next();
                string_length=current.length()-1;
                //Check for Label
                if((current.charAt(0)=='(') && (current.charAt(string_length)==')')) {
                    symbol_table.put(current.substring(1,string_length), line);
                }
                //Check for Comment
                else if((current.charAt(0)=='/') && (current.charAt(1)=='/')){
                    in.nextLine();
                }
                else {
                    code.add(current);
                    line++;
                }
            }
            in.close();
            //code.forEach((k) -> System.out.println(k));
        }
        catch (FileNotFoundException ex) {
            System.out.println("ERROR: File not found!");
        }
    }
    //2.Pass - Turn Code into Binary
    private void second_pass(String path) {
        //Init the Name of the output file
        String file_name = path.substring(0, path.indexOf("."))+".hack";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file_name));
            for (String line_of_code : code) {
                if (line_of_code.charAt(0) == '@') {
                    String symbol = line_of_code.substring(1);
                    // 3 possibilities:
                    // 1. Number --> Decode to Binary
                    // 2. Symbol that is in the Symbol Table --> Take value
                    // 3. Symbol that is not in the Symbol Table --> New entry with value ram_address --> ram_address++
                    if(isNumeric(symbol)) bw.write(int_in_binary(Integer.parseInt(symbol)));
                    else if(symbol_table.containsKey(symbol)) bw.write(int_in_binary(symbol_table.get(symbol)));
                    else {
                            symbol_table.put(symbol, ram_address);
                            bw.write(int_in_binary(ram_address));
                            ram_address++;
                    }
                }
                else {
                    String output = "111" + comp(line_of_code) + dest(line_of_code) + jump(line_of_code);
                    bw.write(output);
                }
                bw.write("\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.out.println("ERROR");
        }
    }
    //Check if String is Numeric
    private boolean isNumeric(String str){
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
    //Print a int-number into binary-String with leading zeros
    private String int_in_binary(int number) {
        String number_in_binary = Integer.toBinaryString(number);
        String zero = "0";
        for(int i=number_in_binary.length(); i<16; i++) {
            number_in_binary = zero+number_in_binary;
        }
        return number_in_binary;
    }
    //C-Instructions: Turn code line in binary Instruction
    private String comp(String line_of_code) {
        //Check if Code has a Equal-Sign
        if (line_of_code.contains("=")) {
            String comp_code = line_of_code.substring(line_of_code.indexOf("=")+1);
            return comp_instruction.getOrDefault(comp_code, "0000000");
        }
        //Check if Line_of_code has a Jump
        else if(line_of_code.contains(";")){
            String comp_code_jmp = line_of_code.substring(0,line_of_code.indexOf(";"));
            return comp_instruction.getOrDefault(comp_code_jmp, "0000000");
        }
        else {
            return "0000000";
        }
    }
    private String dest(String line_of_code) {
        int a=0;
        int m=0;
        int d=0;
        if(line_of_code.contains("=")) {
            for (int i = 0; i < line_of_code.length(); i++) {
                if ((line_of_code.charAt(i) == '=') || (line_of_code.charAt(i) == ';')) break;
                if (line_of_code.charAt(i) == 'A') a = 1;
                if (line_of_code.charAt(i) == 'D') d = 1;
                if (line_of_code.charAt(i) == 'M') m = 1;
            }
        }
        return Integer.toString(a)+Integer.toString(d)+Integer.toString(m);
    }
    private String jump(String line_of_code) {
        int j1=0;
        int j2=0;
        int j3=0;
        if ((line_of_code.contains("JGT")) || (line_of_code.contains("JGE")) ||
                (line_of_code.contains("JNE")) || (line_of_code.contains("JMP"))) j3=1;
        if ((line_of_code.contains("JEQ")) || (line_of_code.contains("JGE")) ||
                (line_of_code.contains("JLE")) || (line_of_code.contains("JMP"))) j2=1;
        if ((line_of_code.contains("JLT")) || (line_of_code.contains("JNE")) ||
                (line_of_code.contains("JLE")) || (line_of_code.contains("JMP"))) j1=1;
        return Integer.toString(j1)+Integer.toString(j2)+Integer.toString(j3);
    }

    public static void main(String[] args) {
        Assembler assembler = new Assembler();
        assembler.init_symbol();
        assembler.init_comp_instruction();
        assembler.first_pass(args[0]);
        assembler.second_pass(args[0]);
    }
}