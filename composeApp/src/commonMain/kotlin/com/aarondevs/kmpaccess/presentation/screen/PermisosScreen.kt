package com.aarondevs.kmpaccess.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarondevs.kmpaccess.presentation.component.Button
import com.aarondevs.kmpaccess.shared.*
import kmpaccess.composeapp.generated.resources.Res
import kmpaccess.composeapp.generated.resources.bg_app
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
fun PermisosScreen() {

    val sistema = getPlataforma()

    val lista = remember { listaPermisos() }

    val permisosFiltrados = lista.filter {
        it.plataforma == "AMBOS" || it.plataforma == sistema
    }

    PermisosScreenBody(
        permisos = permisosFiltrados,
        listaOriginal = lista
    )
}

@Composable
fun PermisosScreenBody(
    permisos: List<PermisoUI>,
    listaOriginal: MutableList<PermisoUI>
) {
    val yaIntentoVoicemail = remember { mutableStateOf(false) }
    val permisoActual = permisos.firstOrNull { !it.respuesta.value.estado }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF171719))
        )

        Image(
            painter = painterResource(resource = Res.drawable.bg_app),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            permisoActual?.let { permiso ->

                PermisoItem(
                    permiso = permiso,
                    yaIntentoVoicemail = yaIntentoVoicemail,
                    onSolicitar = {

                        val resultado = permiso.funcion()
                        permiso.respuesta.value = resultado

                        val index = listaOriginal.indexOfFirst { it.id == permiso.id }
                        if (index != -1) {
                            listaOriginal[index] = permiso
                        }
                    },
                    onVerTerminos = { }
                )

            } ?: run {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Todos los permisos han sido concedidos",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun PermisoItem(
    permiso: PermisoUI,
    yaIntentoVoicemail: MutableState<Boolean>,
    onSolicitar: suspend () -> Unit,
    onVerTerminos: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(permiso.icono),
                    contentDescription = permiso.nombre,
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = permiso.nombre,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = permiso.descripcion,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(48.dp))


                if (!permiso.respuesta.value.estado && permiso.respuesta.value.mensaje.isNotBlank()) {

                    val terminalShape = RoundedCornerShape(14.dp)

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(terminalShape)
                            .background(Color(0xFF0B0B0B))
                            .border(
                                width = 1.dp,
                                color = Color(0x33FF4D4D),
                                shape = terminalShape
                            )
                    ) {

                        Column {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF151515))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFFFF5F56), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFFFFBD2E), CircleShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF27C93F), CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "Info",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {

                                Row {
                                    Text(
                                        text = ">",
                                        color = Color(0xFFFF4D4D),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = permiso.respuesta.value.mensaje,
                                        color = Color(0xFFFF6B6B),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "_",
                                    color = Color(0x55FF6B6B),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        Button(
            text = "PERMITIR",
            onClick = {
                scope.launch {
                    val esVoicemail = permiso.id == "voicemail"
                    if (esVoicemail && yaIntentoVoicemail.value) {
                        permiso.respuesta.value = PermisoRespuesta(
                            estado = true,
                            mensaje = ""
                        )

                        return@launch
                    }

                    if (esVoicemail && !permiso.respuesta.value.estado) {
                        yaIntentoVoicemail.value = true
                    }

                    onSolicitar()
                }
            },
            borderRadius = 16,
            borderSize = 2,
            borderColor = Brush.horizontalGradient(
                listOf(Color(0xFFFFFFFF), Color(0xFFB0B0B0))
            ),
            textSize = 16
        )

        val annotatedText = buildAnnotatedString {

            append("Acepto los ")

            pushStringAnnotation(
                tag = "TERMINOS",
                annotation = "terminos"
            )
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFFFFFFF),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("términos y condiciones")
            }
            pop()

            append(" / ")

            pushStringAnnotation(
                tag = "POLITICAS",
                annotation = "politicas"
            )
            withStyle(
                style = SpanStyle(
                    color = Color(0xFFFFFFFF),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium
                )
            ) {
                append("políticas de seguridad")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            style = TextStyle(
                fontSize = 12.sp,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center
            ),
            onClick = { offset ->

                annotatedText
                    .getStringAnnotations(start = offset, end = offset)
                    .firstOrNull()
                    ?.let { annotation ->

                        when (annotation.tag) {
                            "TERMINOS" -> {

                            }
                            "POLITICAS" -> {

                            }
                        }
                    }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun PermisosScreenPreview() {
    val sistema = getPlataforma()

    val lista = remember { listaPermisos() }

    val permisosFiltrados = lista.filter {
        it.plataforma == "AMBOS" || it.plataforma == sistema
    }

    PermisosScreenBody(
        permisos = permisosFiltrados,
        listaOriginal = lista
    )
}