package com.passione.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import com.passione.R
import com.passione.data.model.Category
import com.passione.data.model.Dish

sealed class MenuItem {
    data class CategoryHeader(val category: Category) : MenuItem()
    data class DishItem(val dish: Dish) : MenuItem()
}

class MenuAdapter(
    private val onAddToCart: (Dish) -> Unit
) : ListAdapter<MenuItem, RecyclerView.ViewHolder>(MenuDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_DISH = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MenuItem.CategoryHeader -> TYPE_HEADER
            is MenuItem.DishItem -> TYPE_DISH
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> CategoryViewHolder(
                inflater.inflate(R.layout.item_category_header, parent, false)
            )
            else -> DishViewHolder(
                inflater.inflate(R.layout.item_dish, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MenuItem.CategoryHeader -> (holder as CategoryViewHolder).bind(item.category)
            is MenuItem.DishItem -> (holder as DishViewHolder).bind(item.dish, onAddToCart)
        }
    }

    fun submitCategories(categories: List<Category>) {
        val items = mutableListOf<MenuItem>()
        categories.forEach { category ->
            items.add(MenuItem.CategoryHeader(category))
            category.dishes.forEach { dish ->
                items.add(MenuItem.DishItem(dish))
            }
        }
        submitList(items)
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoryName: TextView = view.findViewById(R.id.categoryName)

        fun bind(category: Category) {
            categoryName.text = category.name
        }
    }

    class DishViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dishImage: ImageView = view.findViewById(R.id.dishImage)
        private val dishName: TextView = view.findViewById(R.id.dishName)
        private val dishDescription: TextView = view.findViewById(R.id.dishDescription)
        private val dishPrice: TextView = view.findViewById(R.id.dishPrice)
        private val addButton: MaterialButton = view.findViewById(R.id.addButton)

        fun bind(dish: Dish, onAddToCart: (Dish) -> Unit) {
            dishName.text = dish.name
            dishDescription.text = dish.description ?: ""
            dishPrice.text = "${dish.price.toInt()} ₽"

            if (!dish.imageUrl.isNullOrEmpty()) {
                Glide.with(dishImage.context)
                    .load(dish.imageUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(dishImage)
            }

            addButton.setOnClickListener {
                onAddToCart(dish)
            }

            // Dim if unavailable
            itemView.alpha = if (dish.isAvailable) 1f else 0.5f
            addButton.isEnabled = dish.isAvailable
        }
    }

    class MenuDiffCallback : DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return when {
                oldItem is MenuItem.CategoryHeader && newItem is MenuItem.CategoryHeader ->
                    oldItem.category.id == newItem.category.id
                oldItem is MenuItem.DishItem && newItem is MenuItem.DishItem ->
                    oldItem.dish.id == newItem.dish.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem == newItem
        }
    }
}
