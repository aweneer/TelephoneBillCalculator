package com.phonecompany.billing;

import java.math.MathContext;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    private final HashMap<String, Long> callsPerPhoneNumber;
    private final LocalTime intervalStart;
    private final LocalTime intervalEnd;
    private final MathContext mc;

    TelephoneBillCalculatorImpl(String intervalStart, String intervalEnd) {
        callsPerPhoneNumber = new HashMap<>();
        this.intervalStart = LocalTime.parse(intervalStart);
        this.intervalEnd = LocalTime.parse(intervalEnd);
        mc = new MathContext(2);
    }

    @Override
    public BigDecimal calculate(String phoneLog) {
        BigDecimal cost = new BigDecimal(0, mc);
        String[] calls = phoneLog.split("\n");
        for (String call : calls) {
            String[] data = splitData(call);
            addNumberToCallsList(data[0]);
        }

        for (String call : calls) {
            String[] data = splitData(call);
            LocalTime callStart = LocalTime.parse(splitDateTime(data[1])[1]);
            LocalTime callEnd = LocalTime.parse(splitDateTime(data[2])[1]);

            long totalMinutes = callStart.until(callEnd, ChronoUnit.MINUTES);
            long totalSeconds = callStart.until(callEnd, ChronoUnit.SECONDS);
            if (totalSeconds % 60 != 0) totalMinutes++;

            // If phone number is the most frequently called, its price is not added to the total bill at all - the calculation is left out.
            if (!isMostFrequentlyCalled(data[0])) {
                if (totalMinutes > 5) {
                    long additionalMinutes = totalMinutes - 5;
                    cost = cost.add(new BigDecimal(additionalMinutes * 0.20, mc));
                    totalMinutes -= additionalMinutes;
                }
                if (callStart.isAfter(intervalStart) && callStart.isBefore(intervalEnd)) {
                    cost = cost.add(new BigDecimal(totalMinutes, mc));
                } else {
                    cost = cost.add(new BigDecimal(totalMinutes * 0.50, mc));
                }
            }
        }
        return cost;
    }

    public boolean isMostFrequentlyCalled(String phoneNumber) {
        Map.Entry<String, Long> frequentNumber = null;
        if (callsPerPhoneNumber.size() > 1) {
            for (Map.Entry<String, Long> number : callsPerPhoneNumber.entrySet()) {
                if (frequentNumber == null || number.getValue() > frequentNumber.getValue()) {
                    frequentNumber = number;
                } else if (number.getValue().equals(frequentNumber.getValue())) {
                    frequentNumber = Long.parseLong(frequentNumber.getKey()) > Long.parseLong(number.getKey()) ? frequentNumber : number;
                }
            }
            return phoneNumber.equals(Objects.requireNonNull(frequentNumber).getKey());
        }
        return false;
    }

    private void addNumberToCallsList(String phoneNumber) {
        if (callsPerPhoneNumber.containsKey(phoneNumber)) {
            long count = callsPerPhoneNumber.get(phoneNumber);
            callsPerPhoneNumber.put(phoneNumber, count + 1);
        } else {
            callsPerPhoneNumber.put(phoneNumber, 1L);
        }
    }

    public String[] splitData(String data) {
        return Arrays.stream(data.split(",")).toArray(String[]::new);
    }

    public String[] splitDateTime(String dateTime) {
        return Arrays.stream(dateTime.split(" ")).toArray(String[]::new);
    }
}
