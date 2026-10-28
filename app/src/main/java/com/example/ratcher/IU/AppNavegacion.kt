package com.example.ratcher.IU

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ratcher.util.Sesion
import com.example.ratcher.viewmodel.DetalleViewModel
import com.example.ratcher.viewmodel.HomeViewModel
import com.example.ratcher.viewmodel.LoginViewModel
import com.example.ratcher.viewmodel.NuevoLevantamientoViewModel
import com.example.ratcher.viewmodel.RegistroViewModel

// Rutas (nombres) de cada pantalla
object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home"
    const val NUEVO = "nuevo"
    const val DETALLE = "detalle/{id}"

    fun detalle(id: Long): String = "detalle/" + id
}

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        // Animaciones al cambiar de pantalla
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(300)) + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) + fadeOut(tween(300))
        }
    ) {
        composable(Rutas.LOGIN) {
            val vm: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = vm,
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) },
                onLoginExitoso = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.REGISTRO) {
            val vm: RegistroViewModel = viewModel()
            RegistroScreen(
                viewModel = vm,
                onAtras = { navController.popBackStack() },
                onRegistroExitoso = { navController.popBackStack() }
            )
        }

        composable(Rutas.HOME) {
            val vm: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = vm,
                onNuevo = { navController.navigate(Rutas.NUEVO) },
                onAbrir = { id -> navController.navigate(Rutas.detalle(id)) },
                onSalir = {
                    Sesion.cerrar()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.NUEVO) {
            val vm: NuevoLevantamientoViewModel = viewModel()
            NuevoLevantamientoScreen(
                viewModel = vm,
                onAtras = { navController.popBackStack() },
                onCreado = { id ->
                    navController.navigate(Rutas.detalle(id)) {
                        popUpTo(Rutas.NUEVO) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entrada ->
            val vm: DetalleViewModel = viewModel()
            val id = entrada.arguments?.getLong("id") ?: 0L
            DetalleScreen(
                viewModel = vm,
                idLevantamiento = id,
                onAtras = { navController.popBackStack() }
            )
        }
    }
}
