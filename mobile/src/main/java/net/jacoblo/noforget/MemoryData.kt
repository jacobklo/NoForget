package net.jacoblo.noforget

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.ZoneId

// TODO : linking problem!! has to copy to this diretory for now

// LINK Gson tutorial : http://www.studytrails.com/java/json/java-google-json-introduction/
fun main( args: Array<String>) {
    val datasets = ArrayList<MemoryEntry>()
    datasets.add(MemoryEntry(10, LocalDateTime.now(),"ReminderName1", ArrayList<LocalDateTime>(), "Remind1"))
    val memoryData: MemoryData = MemoryData(1, datasets)


    val result = memoryDataToJson(memoryData)
    println(result)

    val fromJson = """{"memory_data_id":1,"memory_entries":[{"memory_entry_id":10,"date_created":{"date":{"year":2018,"month":9,"day":15},"time":{"hour":20,"minute":23,"second":18,"nano":544000000}},"reminder_dates":[],"entry_data":"Remind1"}]}"""
    val mdResult = readJsonToMemoryData(fromJson)
    println(mdResult.toString())
}


fun memoryDataToJson(memoryData: MemoryData): String {
  val gsonBuilder: GsonBuilder = GsonBuilder()
  gsonBuilder.setPrettyPrinting().serializeNulls()
  val gson: Gson = gsonBuilder.create();
  return gson.toJson(memoryData)
}

fun readJsonToMemoryData(memoryDataJson: String?): MemoryData {
  val gsonBuilder: GsonBuilder = GsonBuilder()
  val gson: Gson = gsonBuilder.create();
  val md: MemoryData = gson.fromJson(memoryDataJson, MemoryData::class.java)
  return md
}

// TODO refactor copy methods
fun memoryEntryToJson(memoryEntry: MemoryEntry): String {
  val gsonBuilder: GsonBuilder = GsonBuilder()
  gsonBuilder.setPrettyPrinting().serializeNulls()
  val gson: Gson = gsonBuilder.create();
  return gson.toJson(memoryEntry)
}

fun readJsonToMemoryEntry(memoryEntryJson: String?): MemoryEntry {
  val gsonBuilder: GsonBuilder = GsonBuilder()
  val gson: Gson = gsonBuilder.create();
  val md: MemoryEntry = gson.fromJson(memoryEntryJson, MemoryEntry::class.java)
  return md
}

fun calcUpcomingReminders( memory_data: MemoryData ): List<MemoryEntry> {
  return memory_data.memory_entries.filter {
    me: MemoryEntry ->
    var hasFutureReminderDate = false
    for ( ldt: LocalDateTime in me.reminder_dates) {
      if ( ldt > LocalDateTime.now() ) {
        hasFutureReminderDate = true
      }
    }
    hasFutureReminderDate
  }.sortedBy {
    me: MemoryEntry ->
    var earliestReminderForThis: LocalDateTime? = null
    for ( ldt: LocalDateTime in me.reminder_dates) {
      if ( ldt > LocalDateTime.now() ) {
        earliestReminderForThis = ldt
        break
      }
    }
    val zoneId = ZoneId.systemDefault()
    earliestReminderForThis!!.atZone(zoneId).toEpochSecond()
  }
}

data class MemoryData(val memory_data_id: Int
                 , val memory_entries: ArrayList<MemoryEntry>)

data class MemoryEntry(val memory_entry_id: Int
                       , val date_created: LocalDateTime
                       , val entry_name: String
                       , val reminder_dates: ArrayList<LocalDateTime>
                       , val entry_data: String)
