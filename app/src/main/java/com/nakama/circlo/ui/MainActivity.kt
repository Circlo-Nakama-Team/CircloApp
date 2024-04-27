package com.nakama.circlo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nakama.circlo.R
import com.nakama.circlo.databinding.ActivityMainBinding
import com.nakama.circlo.util.confirmDialog
import com.nakama.circlo.util.toast
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val viewModel by viewModels<SharedViewModel>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentLayout.id) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        validateUserToken()
    }

    private fun validateUserToken() {
        viewModel.getUser().observe(this) { token ->
            setupAction(token)
        }
    }

    private fun setupAction(token : String) {
        binding.btnScan.setOnClickListener {
            if (token != "") {
                PickImageDialog.build(PickSetup())
                    .setOnPickResult {
                        if (it.error == null) {
                            val bundle = Bundle()
                            bundle.putString("imageUri", it.uri.toString())
                            // Move from main activity to scan fragment
                            navController.navigate(R.id.action_homeFragment_to_resultFragment, bundle)
                        } else {
                            toast(it.error.message.toString())
                        }
                    }

                    .setOnPickCancel {
                        toast("Canceled")
                    }.show(supportFragmentManager)
            } else {
                confirmDialog(
                    this,
                    "Anda belum login",
                    "Login sekarang?",
                    "Ya",
                    "Tidak"
                ) {
                    navController.navigate(R.id.action_homeFragment_to_loginFragment)
                }
            }
        }
    }

}