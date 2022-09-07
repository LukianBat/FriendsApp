package ru.lukianbat.friendsapp

import android.app.Application
import android.content.Intent
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.yandex.mapkit.MapKitFactory
import ru.lukianbat.friendsapp.welcome.WelcomeActivity

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("Ваш ключ апи")
        VK.addTokenExpiredHandler(
            object : VKTokenExpiredHandler {
                override fun onTokenExpired() {
                    startActivity(Intent(baseContext, WelcomeActivity::class.java))
                }
            }
        )
    }
}
