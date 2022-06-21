package de.bolte;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class CodeWriter {
    private BufferedWriter bw;
    private String fileName;
    private int labelNumber = 0;
    private int returnNumber = 0;

    public CodeWriter(Path path) {
        fileName =path.getFileName().toString().replace("vm","");
        String newPath;
        if(path.toString().contains(".vm")) {
            newPath = path.toString().replace(".vm", ".asm");
        }
        else {
            newPath = path + ".asm";
        }
        try {
            bw = new BufferedWriter(new FileWriter(newPath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    public void writeArithmetic(String command) {
        try {
            bw.write("//" + command + "\n");
            switch (command) {
                case "add":
                    bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=M+D\n");
                    break;
                case "sub":
                    bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n");
                    break;
                case "neg":
                    bw.write("@SP\nA=M-1\nM=-M\n");
                    break;
                case "eq":
//                    Nimmt obersten beiden Wörter vom Stack und vergleicht diese
//                    Wenn Wörter gleich sind, wird 0 auf den Stack gelegt
//                    Wenn Wörter nicht gleich sind, wird -1 auf den Stack gelegt
                    bw.write(jump("JEQ"));
                    break;
                case "gt":
//                    Siehe eq:
                    bw.write(jump("JGT"));
                    break;
                case "lt":
//                    Siehe eq:
                    bw.write(jump("JLT"));
                    break;
                case "and":
                    bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=M&D\n");
                    break;
                case "or":
                    bw.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=M|D\n");
                    break;
                case "not":
                    bw.write("@SP\nA=M-1\nM=!M\n");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    private String jump(String jumpCommand) {
        labelNumber++;
        return "@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=M-D\n@"+jumpCommand+"LABEL"+ labelNumber +"\nD;"
                +jumpCommand+"\n" + "@SP\nA=M\nM=0\n@JMPLABEL"+ labelNumber +"\n0;JMP\n"
                + "("+jumpCommand+"LABEL"+ labelNumber +")\n@SP\nA=M\nM=-1\n"
                + "(JMPLABEL"+ labelNumber +")\n@SP\nM=M+1\n";
    }
    public void writePush(String commandType, String arg1, int arg2) {
        try {
            bw.write("//" + commandType + " " + arg1 + " " + arg2 + "\n");
            String index = String.valueOf(arg2);
            switch (arg1) {
                case "local":
                    bw.write("@LCL\nD=M\n@" + index + "\nA=D+A\nD=M\n" + pushRest());
                    break;
                case "argument":
                    bw.write("@ARG\nD=M\n@" + index + "\nA=D+A\nD=M\n" + pushRest());
                    break;
                case "this":
                    bw.write("@THIS\nD=M\n@" + index + "\nA=D+A\nD=M\n" + pushRest());
                    break;
                case "that":
                    bw.write("@THAT\nD=M\n@" + index + "\nA=D+A\nD=M\n" + pushRest());
                    break;
                case "constant":
                    bw.write("@" + index + "\nD=A\n" + pushRest());
                    break;
                case "static":
                    bw.write("@" + fileName + index + "\nD=M\n" + pushRest());
                    break;
                case "pointer":
                    String atPointer = "THIS";
                    if (arg2 == 1) atPointer = "THAT";
                    bw.write("@" + atPointer + "\nD=M\n" + pushRest());
                    break;
                case "temp":
                    index = String.valueOf(arg2+5);
                    bw.write("@R5\nD=M\n@" + index + "\nA=D+A\nD=M\n" + pushRest());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    public void writePop(String commandType, String arg1, int arg2) {
        try {
            bw.write("//" + commandType + " " + arg1 + " " + arg2 + "\n");
            String index = String.valueOf(arg2);
            switch (arg1) {
                case "local":
                    bw.write("@LCL\nD=M\n@" + index + "\nD=D+A\n" + popRest());
                    break;
                case "argument":
                    bw.write("@ARG\nD=M\n@" + index + "\nD=D+A\n" + popRest());
                    break;
                case "this":
                    bw.write("@THIS\nD=M\n@" + index + "\nD=D+A\n" + popRest());
                    break;
                case "that":
                    bw.write("@THAT\nD=M\n@" + index + "\nD=D+A\n" + popRest());
                    break;
                case "static":

                    bw.write("@" + fileName + index + "\nD=A\n" + popRest());
                    break;
                case "pointer":
                    String atPointer = "THIS";
                    if (arg2 == 1) atPointer = "THAT";
                    bw.write("@"+atPointer+"\nD=A\n" + popRest());
                    break;
                case "temp":
                    index = String.valueOf(arg2+5);
                    bw.write("@R5\nD=M\n@" + index + "\nD=D+A\n" + popRest());
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    private String pushRest() {
        return "@SP\nA=M\nM=D\n@SP\nM=M+1\n";
    }
    private String popRest() {
        return "@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not close BufferedWriter");
        }
    }
    // Methoden für Projekt 8:
    public void setFilename(String newFileName){
        fileName = newFileName;
    }
    public void writeInit() {
        // Write Bootstrap code (in assembly) at start of .asm File
        // SP=256
        // call Sys.init
        try {
            bw.write("//Init\n");
            bw.write("@256\nD=A\n@SP\nM=D\n");
            writeCall("Sys.init", 0);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }

    public void writeLabel(String label) {
        try {
            bw.write("//Label: "+ label + "\n");
            bw.write("("+label+")\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    public void writeGoto(String label) {
        try {
            bw.write("//goto "+label+"\n");
            bw.write("@"+label+"\n0;JMP\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    public void writeIf(String label) {
        try {
            bw.write("//if-goto "+label+"\n" );
            bw.write("@SP\nAM=M-1\nD=M\n");
            bw.write("@"+label+ "\nD;JNE\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    public void writeFunction(String functionName, int numArgs) {
        try {
            bw.write("//function "+functionName+ " "+ numArgs + "\n");
            bw.write("("+functionName+")\n");
            //repeat numArgs times:
            //Push 0 - Initializes the local variables to 0
            for(int zaehler = 0; zaehler < numArgs; zaehler++) {
                writePush("C_PUSH","constant",0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }
    public void writeCall(String functionName, int numArgs) {
        try {
            bw.write("//call "+ functionName + " "+ numArgs + "\n");
            //push retAddrLabel
            returnNumber++;
            bw.write("@"+fileName+"$ret."+returnNumber+"\nD=A\n"+pushRest());
            //push LCL,ARG,THIS,THAT
            bw.write("@LCL\nD=M\n"+pushRest());
            bw.write("@ARG\nD=M\n"+pushRest());
            bw.write("@THIS\nD=M\n"+pushRest());
            bw.write("@THAT\nD=M\n"+pushRest());
            //ARG = SP-5-numArgs
            int num = numArgs +5;
            bw.write("@SP\nD=M\n@"+ num + "\nD=D-A\n@ARG\nM=D\n");
            //LCL = SP
            bw.write("@SP\nD=M\n@LCL\nM=D\n");
            //goto functionName
            writeGoto(functionName);
            // (retAddrLabel)
            bw.write("("+fileName+"$ret."+returnNumber+")\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }

    public void writeReturn() {
        try {
            bw.write("//return\n");
            //endFrame=LCL
            bw.write("@LCL\nD=M\n@R11\nM=D\n");
            //retAddr=*(endFrame-5)
            bw.write("@5\nA=D-A\nD=M\n@R12\nM=D\n");
            //*ARG = pop()
            bw.write("@ARG\nD=M\n@0\nD=D+A\n"+popRest());
            //SP=ARG+1
            bw.write("@ARG\nD=M\n@SP\nM=D+1\n");
            //THAT=*(endFrame-1)
            bw.write("@R11\nD=M-1\nAM=D\nD=M\n@THAT\nM=D\n");
            //THIS=*(endFrame-2)
            bw.write("@R11\nD=M-1\nAM=D\nD=M\n@THIS\nM=D\n");
            //ARG=*(endFrame-3)
            bw.write("@R11\nD=M-1\nAM=D\nD=M\n@ARG\nM=D\n");
            //LCL=*(endFrame-4)
            bw.write("@R11\nD=M-1\nAM=D\nD=M\n@LCL\nM=D\n");
            //goto retAddr
            bw.write("@R12\nA=M\n0;JMP\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not write File");
        }
    }

}
