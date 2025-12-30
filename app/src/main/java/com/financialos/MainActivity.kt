package com.financialos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.financialos.ui.theme.FinancialOSTheme

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.financialos.ui.login.LoginScreen
import com.financialos.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinancialOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val app = application as FinancialApplication
                    val viewModel: MainViewModel = viewModel(
                        factory = MainViewModelFactory(app)
                    )
                    
                    val user by viewModel.user.collectAsState()
                    
                    if (user == null) {
                        LoginScreen(viewModel = viewModel) {
                            // On Success, the StateFlow updates and triggers recomposition
                        }
                    } else {
                        MainScreen(viewModel = viewModel) {
                            viewModel.signOut()
                        }
                    }
                }
            }
        }
    }
}
