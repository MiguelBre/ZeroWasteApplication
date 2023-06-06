package br.senai.jandira.sp.zerowastetest


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelCupons.Coupon
import com.google.gson.Gson


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TelaCupomRes(coupon: Coupon, onDismiss:()->Unit) {
    Dialog(onDismissRequest = { onDismiss }, properties = DialogProperties(
        usePlatformDefaultWidth = false
    )
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxSize()
                .background(Color(181, 116, 48)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        )
        {

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth(0.95f)
            ) {

                Card(
                    shape = RoundedCornerShape(25.dp),
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxSize()


                ) {
                    Column()
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_cupom_tela),
                                contentDescription = "logo Mercado",
                                modifier = Modifier
                                    .size(130.dp)
                                    .padding(start = 10.dp)


                            )

                            Spacer(modifier = Modifier.padding(15.dp))


                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            )
                            {
                                Text(
                                    text = coupon.nome
                                    ,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 24.sp, color = Color.Black
                                )


                                Text(
                                    text = "Extra",
                                    modifier = Modifier.padding(top = 8.dp),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }


                        }

                        Text(
                            text = coupon.descricao,
                            modifier = Modifier.padding(start = 15.dp),
                            fontSize = 16.sp, fontWeight = FontWeight.Bold

                        )

                        Spacer(modifier = Modifier.padding(10.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        )
                        {

                            Text(
                                text = coupon.criterios,
                                modifier = Modifier.padding(top = 8.dp, start = 20.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )

                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp, start = 12.dp, end = 12.dp)
                        ) {
                            Canvas(modifier = Modifier.matchParentSize()) {
                                drawLine(
                                    color = Color.Gray,
                                    start = Offset(0f, size.height / 1),
                                    end = Offset(size.width, size.height / 1),
                                    strokeWidth = 4f,
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                        floatArrayOf(40f, 20f),
                                        0f
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                ,
                            verticalArrangement = Arrangement.Center


                        ) {

                            Text(
                                text = coupon.codigo,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally),
                                color = Color(181, 116, 48),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }

                }

                Box(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .size(45.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = (-20).dp)
                        .background(Color(181, 116, 48), shape = CircleShape)
                ) {

                }
                Box(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .size(45.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (20).dp)
                        .background(Color(181, 116, 48), shape = CircleShape)
                )
            }



            Spacer(modifier = Modifier.padding(10.dp))


            // Coluna bo botão
            Column(
                modifier = Modifier
                    .width(90.dp)
                    .height(75.dp)
            ) {
                Button(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = RoundedCornerShape(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(
                            255, 255, 255
                        )
                    )
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.sair_x_marrom),
                        contentDescription = stringResource(id = R.string.sair_cupom),
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                    )
                }
            }


        }
    }



}

