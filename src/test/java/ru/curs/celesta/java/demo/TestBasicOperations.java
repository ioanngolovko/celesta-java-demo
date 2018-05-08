package ru.curs.celesta.java.demo;

import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.curs.celesta.CallContext;
import ru.curs.celesta.Celesta;
import ru.curs.celesta.SessionContext;
import secontest.OrderHeaderCursor;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class TestBasicOperations {
    private static JSONObject request1;
    private static  JSONObject request2;

    private static Celesta celesta;

    @BeforeAll
    static void beforeAll() throws Exception {
        String request1Str = new String(Files.readAllBytes(Paths.get(
                TestBasicOperations.class.getResource("request1.json").toURI()
        )));
        String request2Str = new String(Files.readAllBytes(Paths.get(
                TestBasicOperations.class.getResource("request2.json").toURI()
        )));
        request1 = new JSONObject(request1Str);
        request2 = new JSONObject(request2Str);

        celesta = Celesta.createInstance();
        celesta.login(SessionContext.SYSTEM_SESSION_ID, SessionContext.SYSTEM_USER_ID);
    }

    @AfterAll
    static void afterAll() {
        celesta.close();
    }

    @Test
    void testDocumentIsPutToDb() {
        //Вызываем тестируемую процедуру
        celesta.runProc(
                SessionContext.SYSTEM_SESSION_ID,
                "ru.curs.celesta.java.demo.secon.BasicOperations#postOrder",
                request1
        );
        //Проверяем, что данные попали в базу
        try (CallContext context = celesta.callContext()) {
            OrderHeaderCursor header = new OrderHeaderCursor(context);
            header.tryFirst();
            assertEquals("no1", header.getId());
        }
    }

    @Test
    void testReportReturnsAggregatedQtys() {
        celesta.runProc(
                SessionContext.SYSTEM_SESSION_ID,
                "ru.curs.celesta.java.demo.secon.BasicOperations#postOrder",
                request1
        );
        celesta.runProc(
                SessionContext.SYSTEM_SESSION_ID,
                "ru.curs.celesta.java.demo.secon.BasicOperations#postOrder",
                request2
        );

        Map<String, Integer> result = (Map<String, Integer>) celesta.runProc(
                SessionContext.SYSTEM_SESSION_ID,
                "ru.curs.celesta.java.demo.secon.BasicOperations#getAggregateReport"
        );

        assertAll(
                () -> assertEquals(Integer.valueOf(8), result.get("A")),
                () -> assertEquals(Integer.valueOf(4), result.get("B"))

        );
    }
}
