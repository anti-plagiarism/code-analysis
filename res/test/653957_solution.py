import math
t = int(input())
for i in range(t):
    n = int(input())
    l = int(math.log2(n))
    #print(l)
    if n == 1:
        print(0)
    else:
        if l % 2 == 0:
            print(int(math.sqrt(n)) * 2 - 1) 
        else:        
            print(int(math.sqrt(n)) * 2)