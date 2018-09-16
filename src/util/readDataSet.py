import numpy as np
import rowstoPoints as toPts

def readDataSet(filename, length = 60802):
    attrArray = np.zeros(length,42)
    classArray = np.zeros(length)
    dataset = open(filename)
    lineNumber = 0
    for line in dataset:
        line = toPts.readData(line)
        attrArray[lineNumber: ] = line[:-1]
        classArray[lineNumber] = line[-1]
        lineNumber = lineNumber + 1
    return (attrArray, classArray)
