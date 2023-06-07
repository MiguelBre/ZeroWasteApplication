package br.senai.jandira.sp.zerowastetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import br.senai.jandira.sp.zerowastetest.ui.theme.ZeroWasteTestTheme
import br.senai.jandira.sp.zerowastetest.view.ViewPagerSlider
import com.google.accompanist.pager.ExperimentalPagerApi

class PageSliderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeroWasteTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    TelaCuponsContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TelaCuponsContent() {
    ViewPagerSlider(LocalContext.current)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview5() {
    ZeroWasteTestTheme {
        TelaCuponsContent()
    }
}