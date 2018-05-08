package ru.curs.celesta.java.demo.secon;

import ru.curs.celesta.CallContext;
import ru.curs.celesta.annotation.CelestaProc;

public class Hello {

    @CelestaProc
    public void run(CallContext context, String name) {
        System.out.printf("Hello, %s!%n", name);
    }
}
