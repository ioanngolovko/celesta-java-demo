package ru.curs.celesta.java.demo.secon;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.curs.celesta.CallContext;
import ru.curs.celesta.annotation.CelestaProc;
import secontest.OrderHeaderCursor;
import secontest.OrderLineCursor;
import secontest.OrderedQtyCursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class BasicOperations {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("YYYY-MM-DD");

    @CelestaProc
    public void postOrder(CallContext context, JSONObject doc) throws ParseException {
        OrderHeaderCursor header = new OrderHeaderCursor(context);
        OrderLineCursor line = new OrderLineCursor(context);

        header.setId(doc.getString("id"));
        header.setDate(DATE_FORMATTER.parse(doc.getString("date")));
        header.setCustomer_id(doc.getString("customer_id"));
        header.setCustomer_name(doc.getString("customer_name"));
        header.insert();

        JSONArray lines = doc.getJSONArray("lines");

        for (int i = 0; i < lines.length(); ++i ) {
            JSONObject json = lines.getJSONObject(i);

            line.setLine_no(i + 1);
            line.setOrder_id(doc.getString("id"));
            line.setItem_id(json.getString("item_id"));
            line.setQty(json.getInt("qty"));

            line.insert();
        }

    }

    @CelestaProc
    public static Map<String, Integer> getAggregateReport(CallContext context) {
        Map<String, Integer> result = new HashMap<>();
        OrderedQtyCursor orderedQty = new OrderedQtyCursor(context);

        orderedQty.forEach(
                qty -> result.put(qty.getItem_id(), qty.getQty())
        );

        return result;
    }
}
