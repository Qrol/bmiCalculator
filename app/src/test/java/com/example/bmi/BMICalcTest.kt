package com.example.bmi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BMICalcTest {
    @Test
    fun testCalculateValidInputKgAnCm() {
        val bmiCalc = BMICalc(BMICalc.UnitType.KgAndCm)
        assertEquals(10000.0, bmiCalc.calc(1.0, 1.0)!!, 0.01)
        assertEquals(21.12, bmiCalc.calc(33.0, 125.0)!!, 0.01)
        assertEquals(23.90, bmiCalc.calc(37.34, 125.0)!!, 0.01)
        assertEquals(23.90, bmiCalc.calc(37.34, 125.0)!!, 0.01)
    }


    @Test
    fun testCalculateValidInputLbAndIn() {
        val bmiCalc = BMICalc(BMICalc.UnitType.LbAndIn)
        assertEquals(703.0, bmiCalc.calc(1.0, 1.0)!!, 0.01)
        assertEquals(1.48, bmiCalc.calc(33.0, 125.0)!!, 0.01)
        assertEquals(1.68, bmiCalc.calc(37.34, 125.0)!!, 0.01)
        assertEquals(23.11, bmiCalc.calc(200.0, 78.0)!!, 0.01)
    }

    @Test
    fun testCalculateInvalidInput() {
        var bmiCalc = BMICalc(BMICalc.UnitType.KgAndCm)
        assertNull(bmiCalc.calc(-1.0, 1.0))
        assertNull(bmiCalc.calc(0.0, 18.1))
        assertNull(bmiCalc.calc(33.0, 0.0))
        assertNull(bmiCalc.calc(27.0, -9.0))

        bmiCalc = BMICalc(BMICalc.UnitType.LbAndIn)
        assertNull(bmiCalc.calc(-1000.0, 15.1))
        assertNull(bmiCalc.calc(0.0, 10.666))
        assertNull(bmiCalc.calc(2.9, 0.0))
        assertNull(bmiCalc.calc(332.9, -60.0))
    }
}