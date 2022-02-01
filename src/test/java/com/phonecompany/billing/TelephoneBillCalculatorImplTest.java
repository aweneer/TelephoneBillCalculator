package com.phonecompany.billing;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelephoneBillCalculatorImplTest {

    TelephoneBillCalculatorImpl calculator;

    @BeforeEach
    public void init() {
        calculator = new TelephoneBillCalculatorImpl("07:59:59", "16:00:00");
        MathContext mc = new MathContext(2);
    }

    @Test
    public void CallWithinInterval() {
        String call = "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";

        assertEquals(6.20, calculator.calculate(call).doubleValue());
    }

    @Test
    public void CallOutsideInterval() {
        String call = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57";

        assertEquals(1.5, calculator.calculate(call).doubleValue());
    }

    @Test
    public void TwoCalls_ButOnePhoneNumber_IsArithmeticallyLarger() {
        String calls = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00";

        assertEquals(1.5, calculator.calculate(calls).doubleValue());
    }

    @Test
    public void TwoCalls_ButOnePhoneNumber_IsFrequent() {
        String calls ="420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00\n420774577453,13-01-2020 19:10:15,13-01-2020 19:15:14";

        assertEquals(6.2, calculator.calculate(calls).doubleValue());
    }
}
