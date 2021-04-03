package io.github.jezreal.spacenews.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.jezreal.spacenews.databinding.FragmentArticleListBinding
import io.github.jezreal.spacenews.models.Article
import io.github.jezreal.spacenews.recyclerview.ArticleAdapter
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.ArticleEvent.ShowSnackBar
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.ArticleState.*
import kotlinx.coroutines.flow.collect

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
                    adapter = ArticleAdapter { article ->
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
                }
            }
        }

        viewModel.getArticles()

        return binding.root
    }

    private fun adapterOnClick(article: Article) {
        val uri = Uri.parse(article.url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        requireActivity().startActivity(intent)
    }

}