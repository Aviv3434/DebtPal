package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.DebtItem
import com.example.myapplication.databinding.FragmentStatisticsBinding
import com.example.myapplication.repository.DebtRepository
import com.example.myapplication.viewmodel.DebtViewModel
import com.example.myapplication.viewmodel.DebtViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DebtViewModel
    private val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dao = AppDatabase.getDatabase(requireContext()).debtDao()
        val repository = DebtRepository(dao)
        val factory = DebtViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DebtViewModel::class.java]

        val spinner = binding.spinnerStatisticType
        val options = listOf(
            getString(R.string.status_breakdown),
            getString(R.string.debts_over_time),
            getString(R.string.debt_amounts_histogram),
            getString(R.string.summary_report)
        )

        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, v: View?, position: Int, id: Long) {
                viewModel.allDebts.observe(viewLifecycleOwner) { debts ->
                    binding.statisticContainer.removeAllViews()
                    when (options[position]) {
                        getString(R.string.status_breakdown) -> showStatusBreakdown(debts)
                        getString(R.string.debts_over_time) -> showDebtsOverTime(debts)
                        getString(R.string.debt_amounts_histogram) -> showHistogram(debts)
                        getString(R.string.summary_report) -> showTextSummary(debts)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showStatusBreakdown(debts: List<DebtItem>) {
        val settled = debts.count { it.isSettled }
        val unsettled = debts.count { !it.isSettled }
        val addedThisMonth = debts.count {
            val cal = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
            it.date >= cal.timeInMillis
        }

        val labelSettled = getString(R.string.settled)
        val labelUnsettled = getString(R.string.unsettled)
        val labelNew = getString(R.string.new_debt)

        val data = mapOf(
            labelSettled to settled.toFloat(),
            labelUnsettled to unsettled.toFloat(),
            labelNew to addedThisMonth.toFloat()
        )


        val colors = listOf(
            Color.parseColor("#4CAF50"),
            Color.parseColor("#F44336"),
            Color.parseColor("#2196F3")
        )

        val pieChart = PieChartView(requireContext(), data, colors).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            ).apply { setMargins(0, 16, 0, 16) }
        }

        binding.statisticContainer.addView(pieChart)
        addLegend(labelSettled, colors[0], "✅")
        addLegend(labelUnsettled, colors[1], "❌")
        addLegend(labelNew, colors[2], "🆕")
    }

    private fun showDebtsOverTime(debts: List<DebtItem>) {
        val grouped = debts.groupBy { dateFormat.format(Date(it.date)) }.toSortedMap()
        val chart = BarChartView(requireContext(), grouped.mapValues { it.value.size.toFloat() })
        chart.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            500
        ).apply { setMargins(0, 16, 0, 16) }
        binding.statisticContainer.addView(chart)
    }

    private fun showHistogram(debts: List<DebtItem>) {
        val bins = mutableMapOf("₪0–50" to 0f, "₪51–100" to 0f, "₪101–200" to 0f, "₪201+" to 0f)
        for (debt in debts) {
            when (debt.amount) {
                in 0.0..50.0 -> bins["₪0–50"] = bins["₪0–50"]!! + 1
                in 51.0..100.0 -> bins["₪51–100"] = bins["₪51–100"]!! + 1
                in 101.0..200.0 -> bins["₪101–200"] = bins["₪101–200"]!! + 1
                else -> bins["₪201+"] = bins["₪201+"]!! + 1
            }
        }
        val chart = BarChartView(requireContext(), bins)
        chart.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            500
        ).apply { setMargins(0, 16, 0, 16) }
        binding.statisticContainer.addView(chart)
    }

    private fun showTextSummary(debts: List<DebtItem>) {
        val total = debts.sumOf { it.amount }
        val avg = if (debts.isNotEmpty()) total / debts.size else 0.0
        val max = debts.maxOfOrNull { it.amount } ?: 0.0
        val totalCount = debts.size
        val settledCount = debts.count { it.isSettled }
        val unsettledCount = debts.count { !it.isSettled }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            setBackgroundResource(R.drawable.panel_background)
        }

        val title = TextView(requireContext()).apply {
            text = "\uD83D\uDCCB  ${getString(R.string.stat_summary_report)}"
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#3F51B5"))
            setPadding(0, 0, 0, 24)
        }

        container.addView(title)

        fun addStyledLine(text: String, colorBg: String? = null, emoji: String = "") {
            val row = TextView(requireContext()).apply {
                this.text = "$emoji  $text"
                textSize = 16f
                setPadding(16, 12, 16, 12)
                setTextColor(Color.parseColor("#212121"))
                if (colorBg != null) {
                    setBackgroundColor(Color.parseColor(colorBg))
                    setTextColor(Color.WHITE)
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
            container.addView(row, params)
        }

        addStyledLine(getString(R.string.summary_total, total), "#607D8B", "\uD83D\uDCB0")
        addStyledLine(getString(R.string.summary_average, avg), null, "➗")
        addStyledLine(getString(R.string.summary_max, max), "#FF9800", "\uD83D\uDD3C")
        addStyledLine(getString(R.string.summary_entries, totalCount), null, "\uD83D\uDCC5")
        addStyledLine(getString(R.string.summary_settled, settledCount), "#4CAF50", "✅")
        addStyledLine(getString(R.string.summary_unsettled, unsettledCount), "#F44336", "❌")



        // PDF Download Button
        val button = Button(requireContext()).apply {
            text = getString(R.string.download_pdf)
            setPadding(16, 12, 16, 12)
            setBackgroundColor(Color.parseColor("#3F51B5"))
            setTextColor(Color.WHITE)
            textSize = 16f
            setOnClickListener {
                generateSummaryPdf(total, avg, max, totalCount, settledCount, unsettledCount)
            }
        }

        container.addView(button)

        binding.statisticContainer.addView(container)
    }

    private fun generateSummaryPdf(
        total: Double,
        avg: Double,
        max: Double,
        count: Int,
        settled: Int,
        unsettled: Int
    ) {
        val pdfDocument = android.graphics.pdf.PdfDocument()
        val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val lines = listOf(
            getString(R.string.summary_report),
            "------------------------",
            "${getString(R.string.total_debts)}: ₪${"%.2f".format(total)}",
            "${getString(R.string.avg_debt)}: ₪${"%.2f".format(avg)}",
            "${getString(R.string.max_debt)}: ₪${"%.2f".format(max)}",
            "${getString(R.string.total_entries)}: $count",
            getString(R.string.summary_settled, settled),
            getString(R.string.summary_unsettled, unsettled)
        )


        var y = 40f
        for (line in lines) {
            canvas.drawText(line, 20f, y, paint)
            y += 24f
        }

        pdfDocument.finishPage(page)

        val file = File(requireContext().getExternalFilesDir(null), "summary_report.pdf")
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        Toast.makeText(requireContext(), "PDF saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }

    private fun addLegend(label: String, color: Int, emoji: String) {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 4, 8, 4)
        }

        val icon = TextView(requireContext()).apply {
            text = emoji
            textSize = 16f
            setPadding(4, 0, 12, 0)
        }

        val colorBox = View(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(20, 20).apply { marginEnd = 8 }
            setBackgroundColor(color)
        }

        val text = TextView(requireContext()).apply {
            this.text = label
            textSize = 16f
        }

        row.addView(icon)
        row.addView(text)
        row.addView(colorBox)
        binding.statisticContainer.addView(row)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PieChartView(context: Context, private val data: Map<String, Float>, private val colors: List<Int>) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas) {
        val total = data.values.sum()
        if (total == 0f) return

        val size = min(width, height) * 0.9f
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = size / 2f
        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        var startAngle = 0f
        var index = 0

        for ((_, value) in data) {
            val angle = (value / total) * 360f
            paint.color = colors[index % colors.size]
            canvas.drawArc(rectF, startAngle, angle, true, paint)

            val midAngle = Math.toRadians((startAngle + angle / 2).toDouble())
            val labelX = centerX + (radius / 1.8f * cos(midAngle)).toFloat()
            val labelY = centerY + (radius / 1.8f * sin(midAngle)).toFloat()
            val percent = (value / total * 100).toInt()
            canvas.drawText("$percent%", labelX, labelY, textPaint)

            startAngle += angle
            index++
        }
    }
}

class BarChartView(context: Context, private val data: Map<String, Float>) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3F51B5") }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        val barWidth = width / (data.size * 2f)
        val max = (data.values.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val chartHeight = height * 0.75f
        var x = barWidth

        for ((label, value) in data) {
            val barHeight = (value / max) * chartHeight
            val top = height - barHeight
            canvas.drawRect(x, top, x + barWidth, height.toFloat(), paint)
            canvas.drawText(label, x + barWidth / 2, height - barHeight - 10, textPaint)
            x += barWidth * 2
        }
    }
}