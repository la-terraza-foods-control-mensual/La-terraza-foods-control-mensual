package com.laterrazafoods.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
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
    val glosa: String
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
    private val oroClaro = Color.rgb(255, 204, 76)
    private val negro = Color.rgb(15, 15, 15)
    private val blanco = Color.WHITE
    private val grisOscuro = Color.rgb(34, 34, 34)

    private val formatoMoneda =
        NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mostrarInicio()
    }

    private fun mostrarInicio() {
        contenido = LinearLayout(this)
        contenido.orientation = LinearLayout.VERTICAL
        contenido.setPadding(18, 18, 18, 30)
        contenido.setBackgroundColor(negro)

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(negro)
        scroll.addView(contenido)
        setContentView(scroll)

        val panel = LinearLayout(this)
        panel.orientation = LinearLayout.VERTICAL
        panel.gravity = Gravity.CENTER
        panel.setPadding(22, 20, 22, 22)
        panel.background = fondoRedondeado(oro, 28f)

        val panelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        panelParams.setMargins(0, 0, 0, 22)
        contenido.addView(panel, panelParams)

        val logo = ImageView(this)
        logo.setImageResource(R.drawable.logo)
        logo.scaleType = ImageView.ScaleType.FIT_CENTER
        logo.adjustViewBounds = true
        panel.addView(
            logo,
            LinearLayout.LayoutParams.MATCH_PARENT,
            145
        )

        val titulo = TextView(this)
        titulo.text = "LA TERRAZA FOODS"
        titulo.textSize = 28f
        titulo.setTextColor(negro)
        titulo.gravity = Gravity.CENTER
        titulo.setTypeface(null, Typeface.BOLD)
        panel.addView(titulo)

        val subtitulo = TextView(this)
        subtitulo.text = "CONTROL MENSUAL"
        subtitulo.textSize = 15f
        subtitulo.setTextColor(negro)
        subtitulo.gravity = Gravity.CENTER
        subtitulo.setTypeface(null, Typeface.BOLD)
        subtitulo.setPadding(0, 4, 0, 14)
        panel.addView(subtitulo)

        val resumenPanel = LinearLayout(this)
        resumenPanel.orientation = LinearLayout.VERTICAL
        resumenPanel.gravity = Gravity.CENTER
        resumenPanel.setPadding(18, 12, 18, 12)
        resumenPanel.background = fondoRedondeado(negro, 20f)
        panel.addView(
            resumenPanel,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val ventasTotales = totalVentas()
        val gastosTotales = totalGastos()
        val resultado = ventasTotales - gastosTotales

        val etiqueta = TextView(this)
        etiqueta.text = "PANEL FINANCIERO"
        etiqueta.textSize = 13f
        etiqueta.setTextColor(oroClaro)
        etiqueta.gravity = Gravity.CENTER
        etiqueta.setTypeface(null, Typeface.BOLD)
        resumenPanel.addView(etiqueta)

        val saldo = TextView(this)
        saldo.text = dinero(resultado)
        saldo.textSize = 27f
        saldo.setTextColor(blanco)
        saldo.gravity = Gravity.CENTER
        saldo.setTypeface(null, Typeface.BOLD)
        saldo.setPadding(0, 4, 0, 4)
        resumenPanel.addView(saldo)

        val detalle = TextView(this)
        detalle.text =
            "Ventas ${dinero(ventasTotales)}   鈥�   Gastos ${dinero(gastosTotales)}"
        detalle.textSize = 12f
        detalle.setTextColor(Color.LTGRAY)
        detalle.gravity = Gravity.CENTER
        resumenPanel.addView(detalle)

        // MEN脷: mantiene los botones y sus funciones.
                agregarBoton("GASTOS") { mostrarGastos() }
        agregarBoton("VENTAS") { mostrarVentas() }
        agregarBoton("INFORMES") { mostrarReportes() }
        agregarBoton("RESUMEN") { mostrarResumen() }
        agregarBoton("HISTORIAL") { mostrarMisGastos() }
        agregarBoton("IVA Y PPM 1%") { mostrarReportes() }
        agregarBoton("CALENDARIO") { mostrarResumen() }
        agregarBoton("CONFIGURACIÓN") { mostrarConfiguracion() }
    }

    private fun agregarBoton(texto: String, accion: () -> Unit) {
        val boton = Button(this)
        boton.text = texto
        boton.textSize = 16f
        boton.setTextColor(negro)
        boton.setTypeface(null, Typeface.BOLD)
        boton.background = fondoRedondeado(oro, 18f)
        boton.setPadding(10, 16, 10, 16)

        val parametros = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parametros.setMargins(0, 6, 0, 6)

        contenido.addView(boton, parametros)
        boton.setOnClickListener { accion() }
    }

    private fun fondoRedondeado(color: Int, radio: Float): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radio
        }
    }

    private fun tituloPantalla(texto: String) {
        contenido.removeAllViews()
        contenido.setBackgroundColor(negro)

        val titulo = TextView(this)
        titulo.text = texto
        titulo.textSize = 25f
        titulo.setTextColor(oro)
        titulo.setTypeface(null, Typeface.BOLD)
        titulo.gravity = Gravity.CENTER
        titulo.setPadding(0, 18, 0, 24)

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
        tituloPantalla("VENTAS")

        agregarBoton("VENTAS CON TARJETAS") {
            mostrarFormularioVenta("VENTAS CON TARJETAS", "TARJETA")
        }

        agregarBoton("VENTAS EFECTIVO CON IVA") {
            mostrarFormularioVenta("VENTAS EFECTIVO CON IVA", "EFECTIVO_IVA")
        }

        agregarBoton("VENTAS EFECTIVO SIN IVA") {
            mostrarFormularioVenta("VENTAS EFECTIVO SIN IVA", "EFECTIVO_SIN_IVA")
        }

        agregarBoton("VENTAS TRANSFERENCIA CON IVA") {
            mostrarFormularioVenta("VENTAS TRANSFERENCIA CON IVA", "TRANSFERENCIA_IVA")
        }

        agregarBoton("VENTAS TRANSFERENCIA SIN IVA") {
            mostrarFormularioVenta("VENTAS TRANSFERENCIA SIN IVA", "TRANSFERENCIA_SIN_IVA")
        }

        agregarVolver()
    }

    private fun mostrarFormularioVenta(tipo: String, forma: String) {
        tituloPantalla(tipo)

        val fecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        agregarTexto("Fecha: $fecha")

        val monto = campo("Monto")
        val glosa = campo("Glosa")

        val guardar = botonAccion("GUARDAR VENTA")

        guardar.setOnClickListener {
            if (numero(monto) <= 0) {
                monto.error = "Ingrese un monto"
                return@setOnClickListener
            }

            if (glosa.text.toString().trim().isEmpty()) {
                glosa.error = "Ingrese una glosa"
                return@setOnClickListener
            }

            var efectivoIva = 0L
            var efectivoSinIva = 0L
            var tarjeta = 0L
            var transferenciaIva = 0L
            var transferenciaSinIva = 0L

            when (forma) {
                "EFECTIVO_IVA" -> efectivoIva = numero(monto)
                "EFECTIVO_SIN_IVA" -> efectivoSinIva = numero(monto)
                "TARJETA" -> tarjeta = numero(monto)
                "TRANSFERENCIA_IVA" -> transferenciaIva = numero(monto)
                "TRANSFERENCIA_SIN_IVA" -> transferenciaSinIva = numero(monto)
            }

            ventas.add(
                Venta(
                    fecha,
                    efectivoIva,
                    efectivoSinIva,
                    tarjeta,
                    transferenciaIva,
                    transferenciaSinIva,
                    glosa.text.toString().trim()
                )
            )

            Toast.makeText(
                this,
                "Venta guardada correctamente",
                Toast.LENGTH_SHORT
            ).show()

            mostrarInicio()
        }

        contenido.addView(guardar)
        agregarVolver()
    } 
    private fun mostrarGastos() {
        tituloPantalla("GASTOS")

        agregarBoton("COMPRAS CON FACTURA") {
            mostrarFormularioGasto("COMPRAS CON FACTURA", true)
        }

        agregarBoton("COMPRAS SIN FACTURA") {
            mostrarFormularioGasto("COMPRAS SIN FACTURA", false)
        }

        agregarBoton("GASTOS BÁSICOS CON FACTURA") {
            mostrarFormularioGasto("GASTOS BÁSICOS CON FACTURA", true)
        }

        agregarBoton("GASTOS BÁSICOS SIN FACTURA") {
            mostrarFormularioGasto("GASTOS BÁSICOS SIN FACTURA", false)
        }

        agregarBoton("SUELDOS") {
            mostrarFormularioGasto("SUELDOS", false)
        }

        agregarVolver()
    }

    private fun mostrarFormularioGasto(tipo: String, tieneIva: Boolean) {
        tituloPantalla(tipo)

        val fecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        agregarTexto("Fecha: $fecha")

        val monto = campo("Monto")
        val glosa = campo("Glosa")

        val guardar = botonAccion("GUARDAR GASTO")

        guardar.setOnClickListener {
            if (numero(monto) <= 0) {
                monto.error = "Ingrese un monto"
                return@setOnClickListener
            }

            if (glosa.text.toString().trim().isEmpty()) {
                glosa.error = "Ingrese una glosa"
                return@setOnClickListener
            }

            gastos.add(
                Gasto(
                    fecha,
                    glosa.text.toString().trim(),
                    numero(monto),
                    tipo,
                    tieneIva
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
        agregarVolver()
    }
    private fun mostrarGastosBasicos() {
        tituloPantalla("GASTOS B脕SICOS")

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
            "Tel茅fono",
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

        val guardar = botonAccion("GUARDAR GASTO B脕SICO")

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
                "Gasto b谩sico guardado",
                Toast.LENGTH_SHORT
            ).show()

            mostrarInicio()
        }

        contenido.addView(guardar)
        agregarVolver()
    }

    private fun mostrarResumen() {
        tituloPantalla("RESUMEN")

        agregarTextoGrande(
            "VENTAS TOTALES\n${dinero(totalVentas())}"
        )

        agregarTextoGrande(
            "GASTOS TOTALES\n${dinero(totalGastos())}"
        )

        agregarTextoGrande(
            "RESULTADO\n${dinero(totalVentas() - totalGastos())}"
        )

        agregarVolver()
    }

    private fun mostrarMisGastos() {
        tituloPantalla("MIS GASTOS")

        if (gastos.isEmpty()) {
            agregarTexto("Todav铆a no existen gastos registrados.")
        } else {
            gastos.forEach {
                agregarTexto(
                    "${it.fecha}\n" +
                    "Proveedor: ${it.proveedor}\n" +
                    "Categor铆a: ${it.categoria}\n" +
                    "Monto: ${dinero(it.monto)}\n" +
                    if (it.conIva) "Con IVA" else "Sin IVA"
                )
            }
        }

        agregarVolver()
    }

    private fun mostrarGastosConIva() {
        tituloPantalla("GASTOS CON IVA")

        val gastosIva = gastos.filter { it.conIva }

        if (gastosIva.isEmpty()) {
            agregarTexto("No existen gastos con IVA registrados.")
        } else {
            var total = 0L

            gastosIva.forEach {
                total += it.monto

                agregarTexto(
                    "${it.fecha}\n" +
                    "Proveedor: ${it.proveedor}\n" +
                    "Categor铆a: ${it.categoria}\n" +
                    "Monto: ${dinero(it.monto)}"
                )
            }

            agregarTextoGrande(
                "TOTAL CON IVA\n${dinero(total)}"
            )
        }

        agregarVolver()
    }

    private fun mostrarReportes() {
        tituloPantalla("REPORTES")

        val ventasTotales = totalVentas()
        val gastosTotales = totalGastos()

        agregarTextoGrande(
            "TOTAL VENTAS\n${dinero(ventasTotales)}"
        )

        agregarTextoGrande(
            "TOTAL GASTOS\n${dinero(gastosTotales)}"
        )

        agregarTextoGrande(
            "RESULTADO\n${dinero(ventasTotales - gastosTotales)}"
        )

        agregarTexto(
            "Registros de ventas: ${ventas.size}\n" +
            "Registros de gastos: ${gastos.size}"
        )

        agregarVolver()
    }

    private fun mostrarConfiguracion() {
        tituloPantalla("CONFIGURACI脫N")

        agregarTexto("LA TERRAZA FOODS")
        agregarTexto("Control Mensual")
        agregarTexto("Moneda: Peso chileno (CLP)")

        agregarVolver()
    }

    private fun agregarVolver() {
        val volver = botonSecundario("VOLVER")
        volver.setOnClickListener { mostrarInicio() }
        contenido.addView(volver)
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
        boton.background = fondoRedondeado(oro, 18f)
        boton.setPadding(10, 15, 10, 15)
        return boton
    }

    private fun botonSecundario(texto: String): Button {
        val boton = Button(this)
        boton.text = texto
        boton.textSize = 16f
        boton.setTypeface(null, Typeface.BOLD)
        boton.setTextColor(blanco)
        boton.background = fondoRedondeado(grisOscuro, 18f)
        boton.setPadding(10, 14, 10, 14)
        return boton
    }

    private fun totalVentas(): Long {
        return ventas.sumOf {
            it.efectivoIva +
            it.efectivoSinIva +
            it.tarjeta +
            it.transferenciaIva +
            it.transferenciaSinIva
        }
    }

    private fun totalGastos(): Long {
        return gastos.sumOf { it.monto }
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
