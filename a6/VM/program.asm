PUSHMT
SETD 0
PUSH -32768
PUSH -32768
ADDR 0 0             
PUSH 0
STORE              
ADDR 0 1
PUSH 1             
STORE
ADDR 0 1           
LOAD
PUSH 4
LT
PUSH 52            
BF
ADDR 0 0           
DUP
LOAD               
ADDR 0 1
LOAD               
ADD
STORE              
ADDR 0 1
DUP                
LOAD
PUSH 1             
ADD
STORE              
PUSH 19
BR                 
ADDR 0 0
LOAD               
PRINTI
PUSH 10            
PRINTC
HALT
