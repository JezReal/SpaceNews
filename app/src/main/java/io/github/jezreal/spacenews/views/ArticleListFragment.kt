package io.github.jezreal.spacenews.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.jezreal.spacenews.databinding.FragmentArticleListBinding

class ArticleListFragment : Fragment() {

    private lateinit var binding: FragmentArticleListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleListBinding.inflate(inflater, container, false)

        return binding.root
    }
}