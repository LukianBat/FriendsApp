package ru.lukianbat.friendsapp.friends

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.friends.FriendsService
import com.vk.sdk.api.friends.dto.FriendsGetFieldsResponse
import com.vk.sdk.api.users.dto.UsersFields
import ru.lukianbat.friendsapp.R
import ru.lukianbat.friendsapp.map.YandexMapActivity
import ru.lukianbat.friendsapp.map.YandexMapActivity.Companion.CITY_PARAM

class FriendsActivity : Activity() {

    private var recyclerView: RecyclerView? = null
    private val friendsAdapter by lazy {
        FriendsAdapter {
            startActivity(
                Intent(this, YandexMapActivity::class.java).apply {
                    putExtra(CITY_PARAM, it.cityName)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        setupViews()
        requestFriends()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.friendsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = friendsAdapter
    }

    private fun requestFriends() {
        VK.execute(
            FriendsService().friendsGet(fields = listOf(UsersFields.PHOTO_200, UsersFields.CITY)),
            object : VKApiCallback<FriendsGetFieldsResponse> {
                override fun success(result: FriendsGetFieldsResponse) {
                    val friendModels = result.items.map { friend ->
                        FriendModel(
                            firstName = friend.firstName ?: "",
                            lastName = friend.lastName ?: "",
                            photo = friend.photo200 ?: "",
                            cityName = friend.city?.title ?: "",
                        )
                    }
                    friendsAdapter.setData(friendModels)
                }

                override fun fail(error: Exception) {
                    Toast.makeText(
                        this@FriendsActivity,
                        R.string.message_unknown_error,
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        )
    }
}
