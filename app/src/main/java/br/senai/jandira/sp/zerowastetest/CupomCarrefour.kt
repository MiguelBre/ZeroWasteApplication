package br.senai.jandira.sp.zerowastetest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme


class CupomCarrefour : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeroWasteTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(8, 113, 19)
                ) {
                    TelaCupomCarrefour()
                }
            }
        }
    }
}

@Composable
fun TelaCupomCarrefour() {

    val context = LocalContext.current



    Column(
        modifier = Modifier.padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {

        Box(
            modifier = Modifier
                .height(540.dp)
                .width(350.dp)
        ) {


        Card(
            shape = RoundedCornerShape(25.dp),
            elevation = 4.dp,
            modifier = Modifier
                .width(350.dp)
                .height(540.dp)
                .padding(16.dp)



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
                        painter = painterResource(id = R.drawable.logo_carrefour),
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
                            text = "25% OFF",
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp, color = Color.Black
                        )


                        Text(
                            text = "Carrefour",
                            modifier = Modifier.padding(top = 8.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }


                }

                Text(
                    text = "Ganhe 20% na sua próxima compra Carrefour",
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 15.sp, fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.padding(10.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {

                    Text(
                        text = "Válido em todos os mercados Carrefour no Brasil.",
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )


                    Text(
                        text = "Não é acumulável com outros descontos.",
                        modifier = Modifier.padding(top = 8.dp, start = 20.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = "Nenhum valor em dinheiro.",
                        modifier = Modifier.padding(top = 8.dp, start = 28.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
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
                        .fillMaxHeight()


                ) {
                    Text(
                        text = "AJ9YFW6X",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(top = 60.dp, start = 90.dp),
                        color = Color(8, 113, 19),
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
                        .background(Color(8, 113, 19), shape = CircleShape)
                ) {

                }
                Box(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .size(45.dp)
                        .align(Alignment.CenterEnd)
                        .offset(x = (20).dp)
                        .background(Color(8, 113, 19), shape = CircleShape)
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
                    val navigateToCupons = Intent(context, MainActivity::class.java)
                    context.startActivity(navigateToCupons)
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
                    painter = painterResource(id = R.drawable.sair_x),
                    contentDescription = stringResource(id = R.string.sair_cupom),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(150.dp)
                        .height(150.dp)
                )
            }
        }

    }

}
