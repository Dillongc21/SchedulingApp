package com.scheduler.common.model;

public class MonthTypeReportItem {
    private final String month;
    private final String type;
    private final Integer total;

    public MonthTypeReportItem(String month, String type, Integer total) {
        this.month = month;
        this.type = type;
        this.total = total;
    }

    public String getMonth() { return month; }
    public String getType() { return type; }
    public Integer getTotal() { return total; }
}
