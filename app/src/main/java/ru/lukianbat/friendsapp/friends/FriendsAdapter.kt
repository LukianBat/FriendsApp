package ru.lukianbat.friendsapp.friends

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.lukianbat.friendsapp.R

class FriendsAdapter(
    private val onItemClick: (user: FriendModel) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val friends: MutableList<FriendModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserHolder(
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false),
            onItemClick = onItemClick,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserHolder).bind(friends[position])
    }

    fun setData(friends: List<FriendModel>) {
        this.friends.clear()
        this.friends.addAll(friends)
        notifyDataSetChanged()
    }

    override fun getItemCount() = friends.size

    class UserHolder(
        view: View,
        private val onItemClick: (user: FriendModel) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val friendAvatarImageView: ImageView = itemView.findViewById(R.id.friendAvatarImageView)
        private val friendNameTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
        private val friendCityTextView: TextView = itemView.findViewById(R.id.friendCityTextView)


        fun bind(friendModel: FriendModel) {
            friendNameTextView.text = "${friendModel.firstName} ${friendModel.lastName}"
            friendCityTextView.text = friendModel.cityName
            if (!TextUtils.isEmpty(friendModel.photo)) {
                Picasso.get().load(friendModel.photo).error(R.drawable.user_placeholder).into(friendAvatarImageView)
            } else {
                friendAvatarImageView.setImageResource(R.drawable.user_placeholder)
            }

            itemView.setOnClickListener { onItemClick(friendModel) }
        }
    }
}