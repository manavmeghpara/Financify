package com.example.financify.ui.budget.DBs

import android.icu.util.Calendar
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(
    tableName = "purchases",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["name"],
            childColumns = ["categoryName"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class Purchase(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    // Foreign key referencing category table
    var categoryName: String,

    var name: String,

    var amount: Int,

    @TypeConverters(CalendarTypeConverter::class)
    var dateTime: Calendar = Calendar.getInstance(),
)

class CalendarTypeConverter {
    @TypeConverter
    fun calendarToLong(calendar: Calendar): Long {
        return calendar.timeInMillis
    }

    @TypeConverter
    fun longToCalendar(value: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = value
        return calendar
    }
}
