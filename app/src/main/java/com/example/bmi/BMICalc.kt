package com.example.bmi

class BMICalc(var inputType: BMICalc.UnitType) {
    enum class UnitType {
        KgAndCm, LbAndIn;
        companion object{
            fun fromOrdinal(ord: Int): UnitType?{
                for (value in UnitType.values()){
                    if (value.ordinal == ord){
                        return value
                    }
                }
                return null
            }
        }
    }

    fun calc(mass: Double, height: Double): Double? {
        if (mass <= 0) return null
        if (height <= 0) return null

        return if (inputType == UnitType.KgAndCm) {
            mass * 10000 / (height * height)
        } else {
            mass * 703 / (height * height)
        }
    }
}