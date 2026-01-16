package com.passione.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import com.passione.R
import com.passione.data.model.CartItem

class CartAdapter(
    private val onQuantityChange: (String, Int) -> Unit,
    private val onRemove: (String) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position), onQuantityChange, onRemove)
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dishImage: ImageView = view.findViewById(R.id.dishImage)
        private val dishName: TextView = view.findViewById(R.id.dishName)
        private val itemTotal: TextView = view.findViewById(R.id.itemTotal)
        private val quantityText: TextView = view.findViewById(R.id.quantityText)
        private val minusButton: MaterialButton = view.findViewById(R.id.minusButton)
        private val plusButton: MaterialButton = view.findViewById(R.id.plusButton)
        private val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)

        fun bind(
            item: CartItem,
            onQuantityChange: (String, Int) -> Unit,
            onRemove: (String) -> Unit
        ) {
            dishName.text = item.dish?.name ?: "Блюдо"
            itemTotal.text = "${item.itemTotal.toInt()} ₽"
            quantityText.text = item.quantity.toString()

            item.dish?.imageUrl?.let { url ->
                if (url.isNotEmpty()) {
                    Glide.with(dishImage.context)
                        .load(url)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(dishImage)
                }
            }

            minusButton.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChange(item.id, item.quantity - 1)
                } else {
                    onRemove(item.id)
                }
            }

            plusButton.setOnClickListener {
                onQuantityChange(item.id, item.quantity + 1)
            }

            deleteButton.setOnClickListener {
                onRemove(item.id)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
