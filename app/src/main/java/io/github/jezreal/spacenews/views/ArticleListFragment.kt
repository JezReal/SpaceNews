package io.github.jezreal.spacenews.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.jezreal.spacenews.recyclerview.ArticleAdapter
import io.github.jezreal.spacenews.databinding.FragmentArticleListBinding
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.*

@AndroidEntryPoint
class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding
    private val viewModel: ArticleViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleListBinding.inflate(inflater, container, false)

        viewModel.articleList.observe(viewLifecycleOwner) { articleState ->
            when (articleState) {
                is ArticleState.Empty -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                }
                is ArticleState.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }
                is ArticleState.Sucess -> {
                    binding.loading.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    val adapter = ArticleAdapter()
                    binding.recyclerView.adapter = adapter
                    adapter.submitList(articleState.articles)
                }

                is ArticleState.Error -> {
                    binding.loading.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = articleState.message
                }
            }
        }

        viewModel.getArticles()

        return binding.root
    }


}