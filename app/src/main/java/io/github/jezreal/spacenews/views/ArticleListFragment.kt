package io.github.jezreal.spacenews.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.jezreal.spacenews.databinding.FragmentArticleListBinding
import io.github.jezreal.spacenews.models.Article
import io.github.jezreal.spacenews.recyclerview.ArticleAdapter
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.ArticleEvent.LoadUrl
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.ArticleEvent.ShowSnackBar
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.ArticleState.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding
    private val viewModel: ArticleViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleListBinding.inflate(inflater, container, false)

        viewModel.articleList.observe(viewLifecycleOwner) { articleState ->
            when (articleState) {
                is Empty -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                }
                is Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }
                is Success -> {
                    binding.loading.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    val adapter = ArticleAdapter { article ->
                        adapterOnClick(article)
                    }
                    binding.recyclerView.adapter = adapter
                    adapter.submitList(articleState.articles)
                }

                is Error -> {
                    binding.loading.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = articleState.message
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.articleEvent.collect { event ->
                when (event) {
                    is ShowSnackBar -> {
                        Snackbar.make(binding.root, event.message, Snackbar.LENGTH_LONG).show()
                        Log.d("ArticleListFragment", "vent collected")
                    }
                    is LoadUrl -> {
                        findNavController().navigate(
                            ArticleListFragmentDirections.actionArticleListFragmentToArticleFragment(
                                event.url
                            )
                        )
                    }
                }
            }
        }

        viewModel.getArticles()

        return binding.root
    }

    private fun adapterOnClick(article: Article) {
        viewModel.loadUrl(article.url)
    }

}