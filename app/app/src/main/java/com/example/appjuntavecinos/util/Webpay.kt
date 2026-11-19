package com.example.appjuntavecinos.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.appjuntavecinos.WebpayActivity
import com.example.appjuntavecinos.data.remote.dto.WebpayIniciarResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Flujo Webpay (Etapa 4):
 *  1. La app pide al backend POST pagos/webpay/iniciar -> {url, token}
 *  2. Se abre WebpayActivity (WebView con FLAG_SECURE) en  url?token_ws=token
 *  3. Transbank vuelve al backend, que confirma el pago y redirige a zaror://pago?estado=ok|error
 *  4. WebpayActivity intercepta ese enlace, avisa a EventosPago y se cierra; las pantallas
 *     recargan reservas/mias y ven "pagada: true".
 */
object Webpay {

    fun urlFormulario(r: WebpayIniciarResponse): String =
        Uri.parse(r.url).buildUpon().appendQueryParameter("token_ws", r.token).build().toString()

    /** Abre el formulario de pago en WebpayActivity (WebView protegido con FLAG_SECURE). */
    fun abrir(context: Context, url: String) {
        context.startActivity(
            Intent(context, WebpayActivity::class.java).putExtra(WebpayActivity.EXTRA_URL, url)
        )
    }
}

/**
 * Resultado que llega por el deep link zaror://pago?estado=ok|error.
 * WebpayActivity lo publica aquí; el Scaffold del vecino lo muestra en un Snackbar.
 */
object EventosPago {
    private val _resultado = MutableStateFlow<Boolean?>(null)   // true = aprobado, false = rechazado
    val resultado: StateFlow<Boolean?> = _resultado.asStateFlow()

    fun recibir(uri: Uri?) {
        if (uri?.scheme == "zaror" && uri.host == "pago") {
            _resultado.value = uri.getQueryParameter("estado") == "ok"
        }
    }

    fun consumir() { _resultado.value = null }
}