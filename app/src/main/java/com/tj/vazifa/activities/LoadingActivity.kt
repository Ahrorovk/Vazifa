package com.tj.vazifa.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tj.vazifa.R
import com.tj.vazifa.components.Permissions
import com.tj.vazifa.data.local.DataStoreManager
import com.tj.vazifa.ui.theme.VazifaTheme
import com.tj.vazifa.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class LoadingActivity : ComponentActivity(), CoroutineScope {
    @Inject

    lateinit var dataStoreManager: DataStoreManager

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main + CoroutineName("Activity Scope") + CoroutineExceptionHandler { coroutineContext, throwable ->
            println("Exception $throwable in context:$coroutineContext")
        }

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VazifaTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Permissions()
                    val state = remember { mutableStateOf(true) }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "     Мы все загружаем,\nподождите немножко...",
                                style = TextStyle(
                                    fontFamily = FontFamily(
                                        Font(R.font.montserrat_variablefont_wght),
                                        Font(R.font.montserrat_variablefont_wght, FontWeight.W200),
                                    ),
                                    fontWeight = FontWeight.W200,
                                    fontSize = 16.sp
                                ),
                                fontWeight = FontWeight.W900,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 35.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = "C \uD83D\uDC99 Вазифа",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontFamily = FontFamily(
                                    Font(R.font.montserrat_variablefont_wght),
                                    Font(R.font.montserrat_variablefont_wght, FontWeight.W200),
                                ),
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            ),
                            fontWeight = FontWeight.W900,
                            fontSize = 16.sp
                        )
                    }
                    val coroutineScope = rememberCoroutineScope()
                    val viewModel = hiltViewModel<MainViewModel>()

                    if (isConnectedToInternet(context = applicationContext)) {
                        LaunchedEffect(key1 = true) {
                            dataStoreManager.getIsInServer.onEach { isInServer ->
                                Log.e("dataStoreManager", "True $isInServer")
                                when (isInServer) {
                                    0 -> {
                                        viewModel.getFromServer()
                                    }

                                    1 -> {
                                        val intent =
                                            Intent(applicationContext, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        applicationContext.startActivity(intent)

                                        val resultIntent = Intent()
                                        resultIntent.putExtra("resultKey", "Some result data")
                                        setResult(RESULT_OK, resultIntent)
                                        finish()
                                    }

                                    2 -> {
                                        delay(2000)
                                        val intent =
                                            Intent(applicationContext, WebActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        applicationContext.startActivity(intent)

                                        val resultIntent = Intent()
                                        resultIntent.putExtra("resultKey", "Some result data")
                                        setResult(RESULT_OK, resultIntent)
                                        finish()
                                    }

                                    3 -> {
                                        coroutineScope.launch(Dispatchers.Default) {
                                            delay(100)
                                            dataStoreManager.updateIsInServer(0)
                                        }
                                    }
                                }
                            }.launchIn(this)
                        }
                    } else {
                        LaunchedEffect(key1 = true) {
                            val intent =
                                Intent(applicationContext, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            applicationContext.startActivity(intent)

                            val resultIntent = Intent()
                            resultIntent.putExtra("resultKey", "Some result data")
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun isConnectedToInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}