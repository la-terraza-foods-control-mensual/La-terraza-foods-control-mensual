package com.laterrazafoods.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class Venta(
    val fecha: String,
    val efectivoIva: Long,
    val efectivoSinIva: Long,
    val tarjeta: Long,
    val transferenciaIva: Long,
    val transferenciaSinIva: Long
)

data class Gasto(
    val fecha: String,
    val proveedor: String,
    val monto: Long,
    val categoria: String,
    val conIva: Boolean
)

class MainActivity : Activity() {

    private val ventas = mutableListOf<Venta>()
    private val gastos = mutableListOf<Gasto>()

    private lateinit var contenido: LinearLayout

    private val oro = Color.rgb(245, 174, 20)
    private val negro = Color.rgb(15, 15, 15)
    private val blanco = Color.WHITE
    private val gris = Color.rgb(245, 242, 238)

    private val formatoMoneda =
        NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mostrarInicio()
    }

    private fun mostrarInicio() {
        contenido = LinearLayout(this)
        contenido.orientation = LinearLayout.VERTICAL
        contenido.setPadding(24, 20, 24, 30)
        contenido.setBackgroundColor(negro)

        val scroll = ScrollView(this)
        val logo = ImageView(this)
logo.setImageResource(R.drawable.logo)
logo.scaleType = ImageView.ScaleType.FIT_CENTER
logo.adjustViewBounds = true

val logoParams = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.MATCH_PARENT,
    260
)
logo.layoutParams = logoParams

contenido.addView(logo)
        scroll.addView(contenido)

        setContentView(scroll)

        // Encabezado
        val titulo = TextView(this)
        titulo.text = "LA TERRAZA\nFOODS"
        titulo.textSize = 30f
        titulo.setTextColor(oro)
        titulo.gravity = Gravity.CENTER
        titulo.setTypeface(null, Typeface.BOLD)
        titulo.setPadding(0, 20, 0, 10)
        contenido.addView(titulo)

        val subtitulo = TextView(this)
        subtitulo.text = "CONTROL MENSUAL"
        subtitulo.textSize = 15f
        subtitulo.setTextColor(blanco)
        subtitulo.gravity = Gravity.CENTER
        subtitulo.setPadding(0, 0, 0, 25)
        contenido.addView(subtitulo)

        agregarBoton("💰  VENTAS DIARIAS") {
            mostrarVentas()
        }

        agregarBoton("🧾  GASTOS") {
            mostrarGastos()
        }

        agregarBoton("🏪  GASTOS BÁSICOS") {
            mostrarGastosBasicos()
        }

        agregarBoton("📊  RESUMEN") {
            mostrarResumen()
        }

        agregarBoton("📅  HISTORIAL") {
            mostrarHistorial()
        }
    }

    private fun agregarBoton(texto: String, accion: () -> Unit) {
        val boton = Button(this)
        boton.text = texto
        boton.textSize = 17f
        boton.setTextColor(negro)
        boton.setTypeface(null, Typeface.BOLD)
        boton.setBackgroundColor(oro)
        boton.setPadding(10, 18, 10, 18)

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parametros.setMargins(0, 8, 0, 8)

        contenido.addView(boton, parametros)
        boton.setOnClickListener { accion() }
    }

    private fun tituloPantalla(texto: String) {
        contenido.removeAllViews()

        val titulo = TextView(this)
        titulo.text = texto
        titulo.textSize = 25f
        titulo.setTextColor(oro)
        titulo.setTypeface(null, Typeface.BOLD)
        titulo.gravity = Gravity.CENTER
        titulo.setPadding(0, 15, 0, 25)

        contenido.addView(titulo)
    }

    private fun campo(texto: String): EditText {
        val campo = EditText(this)
        campo.hint = texto
        campo.textSize = 16f
        campo.setTextColor(blanco)
        campo.setHintTextColor(Color.LTGRAY)
        campo.setPadding(12, 12, 12, 12)

        contenido.addView(campo)

        return campo
    }

    private fun mostrarVentas() {
        tituloPantalla("VENTAS DIARIAS")

        val fecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        val fechaTexto = TextView(this)
        fechaTexto.text = "Fecha: $fecha"
        fechaTexto.textSize = 17f
        fechaTexto.setTextColor(blanco)
        contenido.addView(fechaTexto)

        val efectivoIva = campo("Efectivo CON IVA")
        val efectivoSinIva = campo("Efectivo SIN IVA")
        val tarjeta = campo("Tarjeta de crédito")
        val transferenciaIva = campo("Transferencia CON IVA")
        val transferenciaSinIva = campo("Transferencia SIN IVA")

        val guardar = botonAccion("GUARDAR VENTAS")

        guardar.setOnClickListener {
            val venta = Venta(
                fecha,
                numero(efectivoIva),
                numero(efectivoSinIva),
                numero(tarjeta),
                numero(transferenciaIva),
                numero(transferenciaSinIva)
            )

            ventas.add(venta)

            Toast.makeText(
                this,
                "Ventas guardadas correctamente",
                Toast.LENGTH_SHORT
            ).show()

            mostrarInicio()
        }

        contenido.addView(guardar)

        val volver = botonSecundario("VOLVER")
        volver.setOnClickListener { mostrarInicio() }
        contenido.addView(volver)
    }

    private fun mostrarGastos() {
        tituloPantalla("GASTOS")

        val fecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        val proveedor = campo("Proveedor")
        val monto = campo("Monto total pagado")
        val categoria = campo("Categoría")

        val iva = CheckBox(this)
        iva.text = "Compra con IVA"
        iva.textSize = 17f
        iva.setTextColor(blanco)
        iva.isChecked = true
        contenido.addView(iva)

        val guardar = botonAccion("GUARDAR GASTO")

        guardar.setOnClickListener {
            if (proveedor.text.toString().trim().isEmpty()) {
                proveedor.error = "Ingrese el proveedor"
                return@setOnClickListener
            }

            if (numero(monto) <= 0) {
                monto.error = "Ingrese un monto"
                return@setOnClickListener
            }

            gastos.add(
                Gasto(
                    fecha,
                    proveedor.text.toString(),
                    numero(monto),
                    categoria.text.toString(),
                    iva.isChecked
                )
            )

            Toast.makeText(
                this,
                "Gasto guardado correctamente",
                Toast.LENGTH_SHORT
            ).show()

            mostrarInicio()
        }

        contenido.addView(guardar)

        val volver = botonSecundario("VOLVER")
        volver.setOnClickListener { mostrarInicio() }
        contenido.addView(volver)
    }

    private fun mostrarGastosBasicos() {
        tituloPantalla("GASTOS BÁSICOS")

        val fecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        val proveedor = campo("Proveedor / empresa")
        val monto = campo("Monto pagado")

        val categoria = Spinner(this)

        val opciones = arrayOf(
            "Luz",
            "Agua",
            "Gas",
            "Internet",
            "Teléfono",
            "Arriendo",
            "Patente",
            "Otros"
        )

        categoria.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            opciones
        )

        contenido.addView(categoria)

        val iva = CheckBox(this)
        iva.text = "Gasto con IVA"
        iva.setTextColor(blanco)
        iva.textSize = 17f
        contenido.addView(iva)

        val guardar = botonAccion("GUARDAR GASTO BÁSICO")

        guardar.setOnClickListener {

            if (proveedor.text.toString().trim().isEmpty()) {
                proveedor.error = "Ingrese el proveedor"
                return@setOnClickListener
            }

            if (numero(monto) <= 0) {
                monto.error = "Ingrese el monto"
                return@setOnClickListener
            }

            gastos.add(
                Gasto(
                    fecha,
                    proveedor.text.toString(),
                    numero(monto),
                    categoria.selectedItem.toString(),
                    iva.isChecked
                )
            )

            Toast.makeText(
                this,
                "Gasto básico guardado",
                Toast.LENGTH_SHORT
            ).show()

            mostrarInicio()
        }

        contenido.addView(guardar)

        val volver = botonSecundario("VOLVER")
        volver.setOnClickListener { mostrarInicio() }
        contenido.addView(volver)
    }

    private fun mostrarResumen() {
        tituloPantalla("RESUMEN")

        var totalVentas = 0L

        ventas.forEach {
            totalVentas += it.efectivoIva
            totalVentas += it.efectivoSinIva
            totalVentas += it.tarjeta
            totalVentas += it.transferenciaIva
            totalVentas += it.transferenciaSinIva
        }

        var totalGastos = 0L

        gastos.forEach {
            totalGastos += it.monto
        }

        val resultado = totalVentas - totalGastos

        agregarTextoGrande(
            "VENTAS TOTALES\n${dinero(totalVentas)}"
        )

        agregarTextoGrande(
            "GASTOS TOTALES\n${dinero(totalGastos)}"
        )

        agregarTextoGrande(
            "RESULTADO\n${dinero(resultado)}"
        )

        val volver = botonSecundario("VOLVER")
        volver.setOnClickListener { mostrarInicio() }
        contenido.addView(volver)
    }

    private fun mostrarHistorial() {
        tituloPantalla("HISTORIAL")

        if (ventas.isEmpty() && gastos.isEmpty()) {
            agregarTexto("Todavía no existen registros.")
        }

        if (ventas.isNotEmpty()) {
            agregarTexto("VENTAS")

            ventas.forEach {
                val total =
                    it.efectivoIva +
                    it.efectivoSinIva +
                    it.tarjeta +
                    it.transferenciaIva +
                    it.transferenciaSinIva

                agregarTexto(
                    "${it.fecha}\nTotal: ${dinero(total)}\n" +
                    "Efectivo IVA: ${dinero(it.efectivoIva)}\n" +
                    "Efectivo sin IVA: ${dinero(it.efectivoSinIva)}\n" +
                    "Tarjeta: ${dinero(it.tarjeta)}\n" +
                    "Transferencia IVA: ${dinero(it.transferenciaIva)}\n" +
                    "Transferencia sin IVA: ${dinero(it.transferenciaSinIva)}"
                )
            }
        }

        if (gastos.isNotEmpty()) {
            agregarTexto("GASTOS")

            gastos.forEach {
                agregarTexto(
                    "${it.fecha}\n" +
                    "${it.proveedor}\n" +
                    "${it.categoria}\n" +
                    "${dinero(it.monto)}\n" +
                    if (it.conIva) "Con IVA" else "Sin IVA"
                )
            }
        }

        val volver = botonSecundario("VOLVER")
        volver.setOnClickListener { mostrarInicio() }
        
    }

    private fun agregarTexto(texto: String) {
        val t = TextView(this)
        t.text = texto
        t.textSize = 16f
        t.setTextColor(blanco)
        t.setPadding(10, 15, 10, 15)

        contenido.addView(t)
    }

    private fun agregarTextoGrande(texto: String) {
        val t = TextView(this)
        t.text = texto
        t.textSize = 21f
        t.setTypeface(null, Typeface.BOLD)
        t.setTextColor(oro)
        t.setPadding(10, 20, 10, 20)

        contenido.addView(t)
    }

    private fun botonAccion(texto: String): Button {
        val boton = Button(this)
        boton.text = texto
        boton.textSize = 17f
        boton.setTypeface(null, Typeface.BOLD)
        boton.setTextColor(negro)
        boton.setBackgroundColor(oro)
        boton.setPadding(10, 15, 10, 15)

        val p = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        p.setMargins(0, 15, 0, 10)

        

        return boton
    }

    private fun botonSecundario(texto: String): Button {
        val boton = Button(this)
        boton.text = texto
        boton.textSize = 16f
        boton.setTextColor(blanco)
        boton.setBackgroundColor(Color.DKGRAY)

                val p = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        p.setMargins(0, 10, 0, 10)

        
        return boton
    }

    private fun numero(campo: EditText): Long {
        return campo.text.toString()
            .replace(".", "")
            .replace(",", "")
            .trim()
            .toLongOrNull() ?: 0L
    }

    private fun dinero(valor: Long): String {
        return formatoMoneda.format(valor)
    }
}
