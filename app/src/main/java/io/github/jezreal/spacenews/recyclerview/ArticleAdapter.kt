package io.github.jezreal.spacenews.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.jezreal.spacenews.databinding.ArticleItemBinding
import io.github.jezreal.spacenews.models.Article

class ArticleAdapter : ListAdapter<Article, ArticleAdapter.ViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder private constructor(private val binding: ArticleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Article) {
            binding.articleTitle.text = item.title
            binding.articleSummary.text = item.summary
            Glide.with(binding.root)
                .load(item.imageUrl)
                .into(binding.articleImage)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ArticleItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }

        }
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
}
