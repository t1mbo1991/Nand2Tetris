//C_PUSH constant 111
@111
D=A
@SP
A=M
M=D
@SP
M=M+1
//C_PUSH constant 333
@333
D=A
@SP
A=M
M=D
@SP
M=M+1
//C_PUSH constant 888
@888
D=A
@SP
A=M
M=D
@SP
M=M+1
//C_POP static 8
@StaticTest8
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//C_POP static 3
@StaticTest3
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//C_POP static 1
@StaticTest1
D=A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
//C_PUSH static 3
@StaticTest3
D=M
@SP
A=M
M=D
@SP
M=M+1
//C_PUSH static 1
@StaticTest1
D=M
@SP
A=M
M=D
@SP
M=M+1
//sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
//C_PUSH static 8
@StaticTest8
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
AM=M-1
D=M
A=A-1
M=M+D
