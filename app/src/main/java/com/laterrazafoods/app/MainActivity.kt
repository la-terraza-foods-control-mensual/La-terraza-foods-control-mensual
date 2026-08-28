package com.laterrazafoods.app

import android.app.*
import android.os.Bundle
import android.content.*
import android.graphics.Color
import android.view.*
import android.widget.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class Gasto(
    val id: Long,
    val fecha: String,
    val proveedor: String,
    val monto: Long,
    val conIva: Boolean,
    val categoria: String
)

class MainActivity : Activity() {
    private val gastos = mutableListOf<Gasto>()
    private lateinit var prefs: android.content.SharedPreferences
    private val fmt = NumberFormat.getCurrencyInstance(Locale("es","CL"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("gastos", MODE_PRIVATE)
        cargar()
        mostrarInicio()
    }

    private fun dinero(n: Long): String {
        fmt.maximumFractionDigits = 0
        return fmt.format(n)
    }

    private fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun cargar() {
        val raw = prefs.getString("items", "") ?: ""
        if (raw.isBlank()) return
        raw.split("\n").forEach {
            val p = it.split("|")
            if (p.size == 6) gastos.add(Gasto(p[0].toLong(), p[1], p[2], p[3].toLong(), p[4]=="1", p[5]))
        }
    }

    private fun guardar() {
        val raw = gastos.joinToString("\n") { "${it.id}|${it.fecha}|${it.proveedor.replace("|"," ")}|${it.monto}|${if(it.conIva) "1" else "0"}|${it.categoria}" }
        prefs.edit().putString("items", raw).apply()
    }

    private fun mostrarInicio() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 24, 28, 24)
        }
        val title = TextView(this).apply {
            text = "La Terraza Foods, Control Mensual"
            textSize = 27f
            setTextColor(Color.rgb(25,25,25))
        }
        val subtitle = TextView(this).apply {
            text = "Control de gastos · IVA 19%"
            textSize = 15f
            setPadding(0,4,0,22)
        }
        root.addView(title)
        root.addView(subtitle)

        val day = TextView(this).apply { textSize = 18f; setPadding(0,8,0,18) }
        root.addView(day)

        val btnAdd = Button(this).apply { text = "＋ Agregar gasto" }
        val btnHistory = Button(this).apply { text = "Historial" }
        root.addView(btnAdd)
        root.addView(btnHistory)

        val datePicker = EditText(this).apply {
            hint = "Fecha del resumen (AAAA-MM-DD)"
            setText(hoy())
            inputType = android.text.InputType.TYPE_CLASS_DATETIME
            setPadding(12,12,12,12)
        }
        root.addView(datePicker)

        fun refresh() {
            val d = datePicker.text.toString()
            val list = gastos.filter { it.fecha == d }
            val total = list.sumOf { it.monto }
            val con = list.filter { it.conIva }.sumOf { it.monto }
            val sin = list.filter { !it.conIva }.sumOf { it.monto }
            val iva = list.filter { it.conIva }.sumOf { (it.monto * 19) / 119 }
            day.text = "RESUMEN DIARIO\n\nTotal: ${dinero(total)}\nCon IVA: ${dinero(con)}\nSin IVA: ${dinero(sin)}\nIVA crédito fiscal: ${dinero(iva)}\nCompras: ${list.size}"
        }
        datePicker.setOnFocusChangeListener { _, _ -> refresh() }
        datePicker.setOnEditorActionListener { _,_,_ -> refresh(); false }
        refresh()

        btnAdd.setOnClickListener { mostrarAgregar() }
        btnHistory.setOnClickListener { mostrarHistorial() }

        setContentView(root)
    }

    private fun mostrarAgregar() {
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(28,24,28,24) }
        val title=TextView(this).apply { text="Agregar gasto"; textSize=25f }
        root.addView(title)
        fun campo(h:String): EditText = EditText(this).apply { hint=h; setPadding(10,8,10,8) }
        val fecha=campo("Fecha (AAAA-MM-DD)"); fecha.setText(hoy())
        val proveedor=campo("Proveedor")
        val monto=campo("Monto total pagado"); monto.inputType=2
        val cat=campo("Categoría (Alimentos, Insumos, etc.)")
        val tipo=Spinner(this)
        tipo.adapter=ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayOf("Compra con IVA","Compra sin IVA"))
        root.addView(fecha);root.addView(proveedor);root.addView(monto);root.addView(cat);root.addView(tipo)
        val info=TextView(this);root.addView(info)
        monto.setOnKeyListener { _,_,_ ->
            val n=monto.text.toString().toLongOrNull()?:0
            info.text=if(tipo.selectedItemPosition==0 && n>0) "Neto: ${dinero(n-(n*19/119))} · IVA: ${dinero(n*19/119)}" else ""
            false
        }
        val save=Button(this).apply{text="Guardar gasto"}
        val back=Button(this).apply{text="Volver"}
        root.addView(save);root.addView(back)
        save.setOnClickListener {
            val n=monto.text.toString().toLongOrNull()?:0
            if(fecha.text.isBlank()||proveedor.text.isBlank()||n<=0){Toast.makeText(this,"Completa fecha, proveedor y monto",Toast.LENGTH_SHORT).show();return@setOnClickListener}
            gastos.add(Gasto(System.currentTimeMillis(),fecha.text.toString(),proveedor.text.toString(),n,tipo.selectedItemPosition==0,cat.text.toString().ifBlank{"Otros"}))
            guardar();Toast.makeText(this,"Gasto guardado",Toast.LENGTH_SHORT).show();mostrarInicio()
        }
        back.setOnClickListener{mostrarInicio()}
        setContentView(root)
    }

    private fun mostrarHistorial() {
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,24,28,24)}
        root.addView(TextView(this).apply{text="Historial";textSize=25f})
        val scroll=ScrollView(this)
        val list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        if(gastos.isEmpty()) list.addView(TextView(this).apply{text="No hay gastos registrados.";setPadding(0,20,0,20)})
        gastos.sortedByDescending{it.id}.forEach { g ->
            val row=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(0,12,0,12)}
            row.addView(TextView(this).apply{text="${g.fecha} · ${g.proveedor}\n${g.categoria} · ${if(g.conIva)"Con IVA" else "Sin IVA"}\n${dinero(g.monto)}${if(g.conIva)" · IVA ${dinero((g.monto*19)/119)}" else ""}";textSize=16f})
            val del=Button(this).apply{text="Eliminar"}
            del.setOnClickListener{gastos.removeIf{x->x.id==g.id};guardar();mostrarHistorial()}
            row.addView(del);list.addView(row)
        }
        scroll.addView(list);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f))
        val back=Button(this).apply{text="Volver"};back.setOnClickListener{mostrarInicio()};root.addView(back)
        setContentView(root)
    }
}