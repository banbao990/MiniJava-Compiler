PIGLET SPECIFICATION

procedures:
    If we have a procedure
        p [5] s
    then it takes five arguments, called (TEMP 0), (TEMP 1), ..., (TEMP 4).
    Other temporaries (TEMP 5 and higher) can be used without declaration and
    are treated as local variables within the procedure.

NOOP:
    Does nothing.

ERROR:
    Terminates the program execution with an error message.

CJUMP Exp Label:
    If Exp evaluates to 1, then continue with the next statement,
    otherwise jump to Label.

LT:
    This is the "<" operator.  It returns 0 for false and 1 for true.
    Also use this operator to test whether a memory address is null
    (the value 0), by asking whether the address is less than 1.

HALLOCATE Exp:
    Exp evaluates to an integer, that corresponding number of bytes
    of heapspace is allocated, and the address of the newly allocated
    memory block is returned as the result.  Both integers and memory
    addresses (i.e., pointers) have a size of 4 bytes, so in general you
    will allocate memory in multiples of 4.

HSTORE Exp_1 IntegerLiteral Exp_2:
    Exp_1 evaluates to an address
    IntegerLiteral is an offset
    Exp_2 evaluates to the value that should be stored.

HLOAD Temp Exp IntegerLiteral
    Temp is the temporary into which a value should be loaded
    Exp evaluates to an address
    IntegerLiteral is an offset.
