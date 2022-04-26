package org.tty.dailyset.dailyset_unic.bean.enums

/**
 * the period code for
 */
enum class PeriodCode(val code: Int) {
    /**
     * unspecified period code.
     */
    UnSpecified(0),

    /**
     * 上学期 the first term of the year.
     */
    FirstTerm(1),

    /**
     * 上学期考试周
     */
    FirstTermEnd(2),

    /**
     * 寒假 winter vacation.
     */
    WinterVacation(4),

    /**
     * 下学期 the second term of the year.
     */
    SecondTerm(7),

    /**
     * 下学期考试周
     */
    SecondTermEnd(8),

    /**
     * 短学期 short term
     */
    ShortTerm(13),

    /**
     * 暑假 summer vacation
     */
    SummerVacation(14);

    fun toTerm(): Int {
        return when(this) {
            FirstTerm -> 1
            SecondTerm -> 2
            else -> 0
        }
    }

    companion object {
        fun from(code: Int): PeriodCode {
            return values().firstOrNull { it.code == code } ?: UnSpecified
        }
    }
}