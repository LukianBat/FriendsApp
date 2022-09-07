package ru.lukianbat.friendsapp.welcome

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKAuthException
import ru.lukianbat.friendsapp.friends.FriendsActivity
import ru.lukianbat.friendsapp.R

class WelcomeActivity : AppCompatActivity() {

    // ActivityResultLauncher для запуска интента авторизации вк
    private var authVkLauncher: ActivityResultLauncher<Collection<VKScope>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (VK.isLoggedIn()) {
            onUserAuthorized()
        }

        // Проверка авторизации пользователя, если пользователь аторизован, открываем экран пользователя
        openUserScreenIfNeeded()

        setContentView(R.layout.activity_welcome)

        // Настраиваем слушатель событий авторизации VK
        setupAuthVkLauncher()

        // Настраиваем вьюшки
        setupViews()
    }

    private fun setupAuthVkLauncher() {
        // ActivityResultLauncher для запуска интента авторизации вк

        authVkLauncher = VK.login(this) { result: VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> onUserAuthorized()
                is VKAuthenticationResult.Failed -> onLoginFailed(result.exception)
            }
        }
    }

    private fun setupViews() {
        // setup screen views

        val vkAuthButton = findViewById<Button>(R.id.vkAuthButton)

        vkAuthButton.setOnClickListener {
            startVkAuth()
        }

    }

    private fun startVkAuth() {
        authVkLauncher?.launch(arrayListOf(VKScope.FRIENDS))
    }

    private fun openUserScreenIfNeeded() {
        // Проверка авторизации пользователя, если пользователь аторизован, открываем экран пользователя
        if (VK.isLoggedIn()) {
            onUserAuthorized()
        }
    }

    private fun onUserAuthorized() {
        // Открываем экран пользователя
        startActivity(Intent(this, FriendsActivity::class.java))
        finish()
    }

    private fun onLoginFailed(exception: VKAuthException) {
        // Если авторизация произошла с ошибкой, предлагаем пользователю повторить авторизацию
        val descriptionResource =
            if (exception.webViewError == WebViewClient.ERROR_HOST_LOOKUP) R.string.message_connection_error
            else R.string.message_unknown_error

        AlertDialog.Builder(this)
            .setMessage(descriptionResource)
            .setPositiveButton(R.string.vk_retry) { _, _ -> startVkAuth() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
