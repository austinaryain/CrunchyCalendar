package ru.cleverpumpkin.calendar

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * This is an immutable class that represents a date as year-month-day.
 *
 * This class overrides [equals] and [hashCode] methods so instances of this class can be used
 * as a key in [HashMap] or [HashSet].
 *
 * This class implements [Parcelable] interface so instances of this class
 * can be stored in [Parcel] object.
 */
class CalendarDate(date: Date) : Parcelable, Comparable<CalendarDate> {

    constructor(dateInMillis: Long) : this(Date(dateInMillis))

    constructor(parcel: Parcel) : this(parcel.readLong())

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<CalendarDate> {
            override fun createFromParcel(parcel: Parcel) = CalendarDate(parcel)

            override fun newArray(size: Int) = arrayOfNulls<CalendarDate>(size)
        }

        val today: CalendarDate get() = CalendarDate(Date())
    }

    private val _calendar = Calendar.getInstance().apply {
        this.time = date
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)
    }

    val year: Int get() = _calendar.get(Calendar.YEAR)

    val month: Int get() = _calendar.get(Calendar.MONTH)

    val dayOfMonth: Int get() = _calendar.get(Calendar.DAY_OF_MONTH)

    val dayOfWeek: Int get() = _calendar.get(Calendar.DAY_OF_WEEK)

    val date: Date get() = _calendar.time

    val timeInMillis: Long get() = _calendar.timeInMillis

    val calendar: Calendar
        get() {
            val newCalendar = Calendar.getInstance()
            newCalendar.timeInMillis = _calendar.timeInMillis
            return newCalendar
        }

    override fun compareTo(other: CalendarDate): Int {
        var result = year - other.year
        if (result == 0) {
            result = month - other.month
            if (result == 0) {
                result = dayOfMonth - other.dayOfMonth
            }
        }

        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalendarDate

        if (year != other.year) return false
        if (month != other.month) return false
        if (dayOfMonth != other.dayOfMonth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + dayOfMonth
        return result
    }

    override fun toString(): String {
        return "$dayOfMonth/${month.inc()}/$year"
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(_calendar.timeInMillis)
    }

    override fun describeContents() = 0

    fun minusMonths(monthsCount: Int): CalendarDate {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.time = _calendar.time
        tmpCalendar.add(Calendar.MONTH, monthsCount.unaryMinus())

        return CalendarDate(tmpCalendar.time)
    }

    fun plusMonths(monthsCount: Int): CalendarDate {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.time = _calendar.time
        tmpCalendar.add(Calendar.MONTH, monthsCount)

        return CalendarDate(tmpCalendar.time)
    }

    fun monthsBetween(other: CalendarDate): Int {
        if (other < this) {
            return 0
        }
        val diffYear = other.year - this.year
        return diffYear * 12 + other.month - this.month
    }

    fun monthBeginning(): CalendarDate {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.set(year, month, 1)

        return CalendarDate(tmpCalendar.time)
    }

    fun monthEnd(): CalendarDate {
        val tmpCalendar = Calendar.getInstance()
        tmpCalendar.set(year, month, _calendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        return CalendarDate(tmpCalendar.time)
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    fun isBetween(dateFrom: CalendarDate, dateTo: CalendarDate): Boolean {
        return this >= dateFrom && this <= dateTo
    }
}