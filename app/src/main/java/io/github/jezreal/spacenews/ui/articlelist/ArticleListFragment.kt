package io.github.jezreal.spacenews.ui.articlelist

import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import io.github.jezreal.spacenews.databinding.FragmentArticleListBinding
import io.github.jezreal.spacenews.network.NetworkArticle
import io.github.jezreal.spacenews.ui.articlelist.ArticleViewModel.ArticleEvent.ShowSnackBar
import io.github.jezreal.spacenews.ui.articlelist.ArticleViewModel.ArticleState.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding
    private val viewModel: ArticleViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleListBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                //listen for state changes
                launch {
                    viewModel.articleList.collect { state ->
                        when (state) {
                            is Empty -> {
                                binding.recyclerView.visibility = View.GONE
                                binding.errorMessage.visibility = View.GONE
                                binding.loading.visibility = View.GONE
                            }

                            is Loading -> {
                                binding.loading.visibility = View.VISIBLE
                            }

                            is Refresh -> {
                                binding.recyclerView.visibility = View.VISIBLE
                                binding.loading.visibility = View.GONE
                                binding.errorMessage.visibility = View.GONE
                            }

                            is Success -> {
                                binding.loading.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                adapter = ArticleAdapter { article ->
                                    adapterOnClick(article)
                                }
                                binding.recyclerView.adapter = adapter
                                adapter.submitList(state.articles)

                                binding.swipeToRefresh.isRefreshing = false
                            }

                            is Error -> {
                                binding.loading.visibility = View.GONE
                                viewModel.showSnackBar(state.message)

                                binding.swipeToRefresh.isRefreshing = false
                            }
                        }
                    }
                }

                //listen for events
                launch {
                    viewModel.articleEvent.collect { event ->
                        when (event) {
                            is ShowSnackBar -> {
                                Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT)
                                    .show()
                                Log.d("ArticleListFragment", "vent collected")
                            }
                        }
                    }
                }

            }
        }

        viewModel.getArticles()

        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.showSnackBar("Refreshing articles...")
            viewModel.refreshArticles()
        }

        return binding.root
    }

    private fun adapterOnClick(article: Article) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(article.url))
    }

}