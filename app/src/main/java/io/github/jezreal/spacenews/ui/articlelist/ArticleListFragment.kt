package io.github.jezreal.spacenews.ui.articlelist

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.jezreal.spacenews.database.toDomainModel
import io.github.jezreal.spacenews.databinding.FragmentArticleListBinding
import io.github.jezreal.spacenews.domain.Article
import io.github.jezreal.spacenews.ui.articlelist.ArticleViewModel.ArticleEvent.ShowSnackBar
import io.github.jezreal.spacenews.ui.articlelist.ArticleViewModel.ArticleState.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding
    private val viewModel: ArticleViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter
    private var cachedArticles: List<Article> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleListBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    collectArticles()
                }
                launch {
                    collectState()
                }
                launch {
                    collectEvents()
                }
            }
        }

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.showSnackBar("Refreshing articles", Snackbar.LENGTH_SHORT)
            viewModel.refreshArticles()
        }

        return binding.root
    }

    private fun adapterOnClick(article: Article) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(article.url))
    }

    private suspend fun collectState() {
        viewModel.articleList.collect { state ->
            when (state) {
                is Empty -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                    binding.loading.visibility = View.GONE

                    viewModel.getArticles()
                }

                is Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

                is Refresh -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                }

                is Error -> {
                    binding.swipeToRefresh.isRefreshing = false
                    showArticles()
                }

                is Success -> {
                    binding.swipeToRefresh.isRefreshing = false
                    showArticles()
                }
            }
        }
    }

    private suspend fun collectEvents() {
        viewModel.articleEvent.collect { event ->
            when (event) {
                is ShowSnackBar -> {
                    Snackbar.make(binding.root, event.message, event.length).show()
                }
            }
        }
    }

    private suspend fun collectArticles() {
        viewModel.articles.collect { articles ->
            cachedArticles = articles.toDomainModel()
            adapter = ArticleAdapter { article ->
                adapterOnClick(article)
            }

            binding.recyclerView.adapter = adapter
            adapter.submitList(cachedArticles)
        }
    }

    private fun showArticles() {
        binding.loading.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE

        if (cachedArticles.isEmpty()) {
            viewModel.getArticles()
        }

        binding.swipeToRefresh.isRefreshing = false
    }
}