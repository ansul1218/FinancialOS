package com.financialos.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.financialos.MainViewModel
import com.financialos.ui.dashboard.DashboardScreen
import com.financialos.ui.accounts.AccountsScreen
import com.financialos.ui.tasks.TaskBoardScreen

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val accounts by viewModel.repository.accounts.collectAsState()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentRoute == "accounts",
                    onClick = { navController.navigate("accounts") },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Accounts") },
                    label = { Text("Accounts") }
                )
                NavigationBarItem(
                    selected = currentRoute == "tasks",
                    onClick = { navController.navigate("tasks") },
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                DashboardScreen(accounts = accounts)
            }
            composable("accounts") {
                AccountsScreen(accounts = accounts)
            }
            composable("tasks") {
                TaskBoardScreen()
            }
        }
    }
}
