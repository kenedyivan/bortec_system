package com.project.ken.botec.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ken on 4/27/18.
 */

public class SalesLog {

    public static List<SalesLog.SaleLog> ITEMS = new ArrayList<>();

    public static void addSaleLog(SaleLog item) {
        ITEMS.add(item);
    }

    public static SaleLog createLogItem(String id, String item_name,
                                        String quantity, String date, String unit_price) {
        return new SaleLog(id, item_name, quantity, date, unit_price);
    }

    public static class SaleLog {
        public final String id;
        public final String item_name;
        public final String quantity;
        public final String date;
        public final String unit_price;

        public SaleLog(String id, String item_name, String quantity, String date, String unit_price) {
            this.id = id;
            this.item_name = item_name;
            this.quantity = quantity;
            this.date = date;
            this.unit_price = unit_price;
        }

        @Override
        public String toString() {
            return "SaleLog{" +
                    "item_name='" + item_name + '\'' +
                    '}';
        }
    }
}
