package de.bolte;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

public class Parser {
    private Scanner scanner;
    private String commandType ="";
    private String argument1 ="";
    private int argument2 = -1;

    public Parser(Path path) {
        try {
            scanner = new Scanner(new File(String.valueOf(path)));
        }
        catch (FileNotFoundException ex) {
            System.out.println("Error: File not found!");
        }
    }

    public boolean hasMoreCommands() {
        return scanner.hasNext();
    }

    public void advance() {
        String command = scanner.next();
        while ((command.charAt(0)=='/') && (command.charAt(1)=='/')) {
            scanner.nextLine();
            command = scanner.next();
        }

        if ((command.equals("pop")) || (command.equals("push"))) {
            commandType = "C_"+command.toUpperCase();
            argument1 = scanner.next();
            argument2 = scanner.nextInt();
            scanner.nextLine();
        }
        else if ((command.equals("add")) || (command.equals("sub")) || (command.equals("neg")) ||
                (command.equals("eq")) || (command.equals("gt")) || (command.equals("lt")) ||
                (command.equals("and")) || (command.equals("or")) || (command.equals("not"))) {
            commandType = "C_ARITHMETIC";
            argument1 = command;
            argument2 = -1;
            scanner.nextLine();
        }
        else if ((command.equals("function")) || (command.equals("call"))) {
            commandType = "C_"+command.toUpperCase();
            argument1 = scanner.next();
            argument2 = scanner.nextInt();
            scanner.nextLine();
        }

        else if ((command.equals("goto")) || (command.equals("label"))) {
            commandType = "C_"+command.toUpperCase();
            argument1 = scanner.next();
            argument2 = -1;
            scanner.nextLine();
        }
        else if (command.equals("if-goto")) {
            commandType= "C_IF";
            argument1 = scanner.next();
            argument2 = -1;
            scanner.nextLine();
        }
        else if (command.equals("return")) {
            commandType = "C_"+command.toUpperCase();
            argument1 = "";
            argument2 = -1;
            scanner.nextLine();
        }
    }

    public String commandType() {
        return commandType;
    }

    public String arg1() {
        return argument1;
    }

    public int arg2() {
        return argument2;
    }
}
