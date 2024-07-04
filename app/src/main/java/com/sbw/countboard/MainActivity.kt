package com.sbw.countboard

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import com.sbw.countboard.databinding.ActivityMainBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), EventAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: EventAdapter
    private val events = mutableListOf<Event>()
    private var isAddButtonClicked = true
    private var isMangerButtonClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.eventsRv.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(events, this)
        binding.eventsRv.adapter = adapter
        // 从SharedPreferences加载事件
        loadEvents()

        //折叠
        binding.addBtn.setOnClickListener {
            if (isAddButtonClicked) {
                binding.addCard.visibility = View.GONE
                isAddButtonClicked = false
            } else {
                binding.addCard.visibility = View.VISIBLE
                isAddButtonClicked = true
            }
        }
        binding.mangerBtn.setOnClickListener {
            if (isMangerButtonClicked) {
                binding.mangerCard.visibility = View.GONE
                isMangerButtonClicked = false
            } else {
                binding.mangerCard.visibility = View.VISIBLE
                isMangerButtonClicked = true
            }
        }
        binding.selectTimeTv.setOnClickListener {
            CardDatePickerDialog.builder(this)
                .setDisplayType(mutableListOf(DateTimeConfig.YEAR, DateTimeConfig.MONTH, DateTimeConfig.DAY))
                .setBackGroundModel(CardDatePickerDialog.STACK)
                .showBackNow(true)
                .setTitle("请选择结束日期")
                .setOnChoose("确定") {millisecond->
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val date = Date(millisecond)
                    val dayTimeStr = simpleDateFormat.format(date)
                    binding.selectTimeTv.text = dayTimeStr
                }.build().show()
        }
        //添加
        binding.enterBtn.setOnClickListener {
            if (binding.titleEt.text.toString().isEmpty()) {
                Toast.makeText(this, "请输入日程名称", Toast.LENGTH_SHORT).show()
            } else if (!isValidDateFormat(binding.selectTimeTv.text.toString())) {
                Toast.makeText(this, "请选择日期", Toast.LENGTH_SHORT).show()
            } else {
                val event = Event(binding.titleEt.text.toString(), binding.selectTimeTv.text.toString())
                events.add(event)
                // 保存到SharedPreferences或数据库
                saveEvents()
                adapter.notifyDataSetChanged()
            }
            // 更新Widget
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val widgetComponent = ComponentName(this, EventWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            sendBroadcast(intent)
            binding.titleEt.text.clear()
            binding.selectTimeTv.text = "选择日程结束时间"
        }
        // 安排每日更新任务
        scheduleDailyUpdate()
    }

    private fun loadEvents() {
        val prefs = getSharedPreferences("events", Context.MODE_PRIVATE)
        val eventSet = prefs.getStringSet("event_set", setOf()) ?: setOf()
        events.clear()
        events.addAll(eventSet.map {
            val parts = it.split(",")
            Event(parts[0], parts[1])
        })
    }

    private fun saveEvents() {
        val prefs = getSharedPreferences("events", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val eventSet = events.map { "${it.title},${it.doneTime},${it.leftTime}" }.toSet()
        editor.putStringSet("event_set", eventSet)
        editor.apply()
    }

    private fun scheduleDailyUpdate() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val updateWorkRequest = PeriodicWorkRequest.Builder(UpdateEventsWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(updateWorkRequest)
    }

    fun isValidDateFormat(date: String): Boolean {
        // 定义一个正则表达式来匹配 yyyy-MM-dd 格式
        val regex = Regex("""\d{4}-\d{2}-\d{2}""")

        // 检查日期字符串是否匹配正则表达式
        if (!regex.matches(date)) {
            return false
        }

        // 使用 SimpleDateFormat 来进一步验证日期是否合法
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: ParseException) {
            false
        }
    }

    override fun onItemClick(event: Event) {
        AlertDialog.Builder(this).apply {
            setTitle("提示")
            setMessage("确认删除这个日程吗?")
            setPositiveButton("确认") {
                    dialog, which ->
                events.remove(event)
                saveEvents()
                adapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "${event.title}删除成功", Toast.LENGTH_SHORT).show()

                // 更新Widget
                val appWidgetManager = AppWidgetManager.getInstance(this@MainActivity)
                val widgetComponent = ComponentName(this@MainActivity, EventWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                sendBroadcast(intent)
            }
            setNegativeButton("取消") {
                    dialog, which ->
                dialog.dismiss()
            }
            show()
        }
    }

}