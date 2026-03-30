package com.example.outdoortrack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.outdoortrack.R
import com.example.outdoortrack.core.ServiceLocator
import com.example.outdoortrack.data.repository.AuthRepository

/**
 * 单 Activity 容器，内部通过 Navigation Component 管理页面路由。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // 根据本地会话是否有效，动态设置起始页：登录页 / 首页
        val authRepository: AuthRepository = ServiceLocator.authRepository
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(
            if (authRepository.hasValidSession()) R.id.homeFragment else R.id.loginFragment
        )
        navController.graph = navGraph
    }
}
