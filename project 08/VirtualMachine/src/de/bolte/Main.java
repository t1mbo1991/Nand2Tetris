package de.bolte;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1){
            System.out.println("Error: Wrong Arguments");
            return;
        }
        Path path = Paths.get(args[0]);
        CodeWriter print = new CodeWriter(path);
        //Check if Input is a Single File or a Directory
        // If Single File: Construct Parser that marches through file (Like Project 7)
        // If Directory: Do that for every file in the Directory - Output in single directoryName.asm File
        if(path.toString().contains(".vm")) {
            parseFile(path, print);
        }
        else {
            File folder = path.toFile();
            File[] listOfPaths = folder.listFiles();
            if (listOfPaths != null) {
                int numberOfFiles = 0;
                for (File file : listOfPaths) {
                    if (file.toString().contains(".vm")) numberOfFiles++;
                }
               if (numberOfFiles>1) print.writeInit();
                for (File file : listOfPaths) {
                    Path filePath = file.toPath();
                    if(filePath.toString().contains(".vm")) {
                        print.setFilename(filePath.getFileName().toString());
                        parseFile(filePath,print);
                    }
                }
            }
        }
        print.close();
        System.out.println("File created:" + path.toString().replace(".vm",".asm"));
    }

    private static void parseFile(Path path, CodeWriter print) {
        Parser parser = new Parser(path);
        while (parser.hasMoreCommands()) {
            parser.advance();
            String commandType = parser.commandType();
            switch (commandType) {
                case "C_ARITHMETIC" -> print.writeArithmetic(parser.arg1());
                case "C_PUSH" -> print.writePush(commandType, parser.arg1(), parser.arg2());
                case "C_POP" -> print.writePop(commandType, parser.arg1(), parser.arg2());
                case "C_LABEL" -> print.writeLabel(parser.arg1());
                case "C_GOTO" -> print.writeGoto(parser.arg1());
                case "C_IF" -> print.writeIf(parser.arg1());
                case "C_FUNCTION" -> print.writeFunction(parser.arg1(), parser.arg2());
                case "C_CALL" -> print.writeCall(parser.arg1(), parser.arg2());
                case "C_RETURN" -> print.writeReturn();
            }
        }
    }
}

