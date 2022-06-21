// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

// How to:
// Calc. R2+=R1 for every R0.
// set R2=0;
// for(int i = r0; i>0; i--) {
//      R2+=R1;
//  }

//Code:
    //Set R2=0
        @0
        D=A
        @R2
        M=D

    (LOOP)
    //Check, if R0 = 0
        @R0
        D=M
        @END
        D;JEQ

    //R2=R2+R1
        @R1
        D=M
        @R2
        M=M+D

    //R0=R0-1
        @R0
        M=M-1

        @LOOP
        0;JMP

    (END)
        @END
        0;JMP



