// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Pseudocode:
//-------------------------------
// while(true) {
//     set screen white;
//     while(key.ispressed()) {
//         set screen black;
//     }
// }
//-------------------------------

// Code
        //Set KBD to 24576
        @24576
        D=A
        @KBD
        M=D

        // Set Screen to 16384
        @16384
        D=A
        @SCREEN
        M=D

    (START1)
        @SCREEN
        D=A
        @addr
        M=D
        //Set Screen White

    (WHITE)
        //Check if Every Pixel is White
        @addr
        D=M
        @KBD
        D=D-M
        @START2
        D;JGT

        //Set single Pixel to White
        @addr
        A=M
        M=0

        //INC Pixel
        @32
        D=A
        @addr
        M=D+M
        @WHITE
        0;JMP

    (START2)
        //Check if Keyboard is pressed
        @KBD
        D=M
        @END2
        D;JEQ

        @SCREEN
        D=A
        @addr
        M=D
        //Set Screen Black

    (BLACK)
        //Check if Every Pixel is Black
        @addr
        D=M
        @KBD
        D=D-M
        @END2
        D;JGT

        //Set single Pixel to Black
        @addr
        A=M
        M=-1

        //INC Pixel
        @32
        D=A
        @addr
        M=D+M
        @BLACK
        0;JMP

    (END2)
        @START1
        0;JMP

    (END1)
        @END1
        0;JMP
