package hu.ait.traveldiary.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.traveldiary.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginScreenViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("peter@ait.hu") }
    var password by rememberSaveable { mutableStateOf("123456") }

    val coroutineScope = rememberCoroutineScope()

    Box() {
        Image(
            painter = painterResource(id = R.drawable.tickets),
            contentDescription = stringResource(R.string.login_background),
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Row(Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start){
            Text(
                text = stringResource(R.string.wander_snap),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 45.dp, top = 200.dp, end = 180.dp),
                lineHeight = 40.sp,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = stringResource(R.string.e_mail))
                },
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, null)
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = stringResource(R.string.password))
                },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation =
                if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Info, null)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Default.KeyboardArrowUp, null)
                        } else {
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    Color.White // Change this to your desired background color
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    coroutineScope.launch {
                        val result = loginViewModel.loginUser(email,password)
                        if (result?.user != null) {
                            onLoginSuccess()
                        }
                    }
                }) {
                    Text(text = stringResource(R.string.login))
                }
                OutlinedButton(onClick = {
                    loginViewModel.registerUser(email,password)
                }) {
                    Text(text = stringResource(R.string.register))
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loginViewModel.loginUiState) {
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.RegisterSuccess -> Text(text = stringResource(R.string.registration_ok))
                is LoginUiState.Error -> Text(text = stringResource(R.string.error) + "${
                    (loginViewModel.loginUiState as LoginUiState.Error).error
                }")
                is LoginUiState.LoginSuccess -> Text(text = stringResource(R.string.login_ok))
                LoginUiState.Init -> {}
            }

        }
    }
}
