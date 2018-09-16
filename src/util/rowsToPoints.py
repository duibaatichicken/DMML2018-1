def readData(line):
    row = line.split(",")
    for elt in row[:-1]:
        if elt == "b":
            elt = 0
        elif elt == "o":
            elt = -1
        elif elt == "x":
            elt = 1
        else:
            raise ValueError('Invalid data')
    if row[-1] == "draw":
        row[-1] = 0
    elif row[-1] == "win":
        row[-1] = 1
    elif row[-1] == "loss":
        row[-1] = -1
    else:
        raise ValueError('Invalid data')
    return row

"""
testing
"""
print readData("b,o,x")
print readData("b,o,x,Dazzle")
        
            
    
