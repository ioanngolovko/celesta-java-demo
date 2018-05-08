package ru.curs.celesta.java.demo;

import ru.curs.celesta.Celesta;
import ru.curs.celesta.SessionContext;

public class App {

    public static void main(String args[]) {
        Celesta celesta = Celesta.createInstance();

        celesta.login(SessionContext.SYSTEM_SESSION_ID, SessionContext.SYSTEM_USER_ID);

        if (args.length > 1)
            celesta.runProc(SessionContext.SYSTEM_SESSION_ID, args[0], args[1]);
    }
}
