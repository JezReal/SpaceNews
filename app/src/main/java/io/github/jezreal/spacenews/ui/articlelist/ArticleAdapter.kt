package io.github.jezreal.spacenews.ui.articlelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.github.jezreal.spacenews.databinding.ArticleItemCardBinding
import io.github.jezreal.spacenews.domain.Article

class ArticleAdapter(private val onclick: (Article) -> Unit) :
    ListAdapter<Article, ArticleAdapter.ViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onclick)
    }

    class ViewHolder private constructor(
        private val binding: ArticleItemCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Article, onclick: (Article) -> Unit) {
            binding.articleTitle.text = item.title
            binding.articleSummary.text = item.summary
            Glide.with(binding.root)
                .load(item.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.articleImage)

            binding.root.setOnClickListener {
                onclick(item)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ArticleItemCardBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }

        }
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
}