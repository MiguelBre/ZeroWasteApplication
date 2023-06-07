package br.senai.jandira.sp.zerowastetest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelCupons.Coupon

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Dialogs(
    onDismiss:()->Unit,
    onConfirm: (id: Int) -> Unit,
    coupon: Coupon,
) {


    Dialog(
        onDismissRequest = {onDismiss()},
                properties = DialogProperties(
                usePlatformDefaultWidth = false
                )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.4f)
                .border(
                    BorderStroke(2.dp, Color(107, 177, 115)),
                    shape = RoundedCornerShape(15.dp)
                ),
            shape = RoundedCornerShape(15.dp), elevation = 20.dp
        ) {
            Column(modifier = Modifier.padding(top = 7.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {

                Text(
                    text = "Deseja Prosseguir?", fontSize = 17.sp, modifier = Modifier.padding(top = 8.dp)

                )

                Text(
                    text = "Ao clicar na opção SIM, o cupom será resgatado",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 5.dp, start = 20.dp, end = 20.dp)
                )

                Text(
                    text = coupon.descricao,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = Color(8, 113, 19),
                    modifier = Modifier.padding(top = 5.dp, start = 35.dp, end = 35.dp)
                )

                Text(
                    text = coupon.criterios,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = Color(8, 113, 19),
                    modifier = Modifier.padding(top = 5.dp, start = 15.dp, end = 15.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                    )
                {

                    Button(
                        onClick =  {
                            onConfirm(coupon.id)
                        }, modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(8, 113, 19))
                    )
                    {
                        Text(
                            text = "Sim",color = Color.White
                        )


                    }

                    Button(
                        onClick =  {
                            onDismiss()
                        }, modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(210, 49, 28))
                    )
                    {
                        Text(
                            text = "Não", color = Color.White,
                        )


                    }
                }

            }

        }

    }
}
