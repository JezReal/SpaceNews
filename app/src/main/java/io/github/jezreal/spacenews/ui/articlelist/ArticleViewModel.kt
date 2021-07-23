package io.github.jezreal.spacenews.ui.articlelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jezreal.spacenews.network.NetworkArticle
import io.github.jezreal.spacenews.repository.ArticleRepository
import io.github.jezreal.spacenews.ui.articlelist.ArticleViewModel.ArticleEvent.ShowSnackBar
import io.github.jezreal.spacenews.wrappers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _articleList = MutableStateFlow<ArticleState>(ArticleState.Empty)
    val articleList: Flow<ArticleState> = _articleList

    private val _articleEvent = Channel<ArticleEvent>()
    val articleEvent = _articleEvent.receiveAsFlow()

    val articles = repository.articles

    fun getArticles() {
        getArticlesFromNetwork()
    }

    fun refreshArticles() {
        _articleList.value = ArticleState.Refresh

        viewModelScope.launch(Dispatchers.Default) {
            when (val articleResponse = repository.getArticleList()) {
                is Resource.Success -> {
                    _articleList.value = ArticleState.Success
                }
                is Resource.Error -> {
                    _articleList.value = ArticleState.Error(articleResponse.message!!)
                }
            }
        }
    }

    private fun getArticlesFromNetwork() {
        _articleList.value = ArticleState.Loading

        viewModelScope.launch(Dispatchers.Default) {
            when (val articleResponse = repository.getArticleList()) {
                is Resource.Success -> {
                    insertArticlesToDatabase(articleResponse.data!!)
                }
                is Resource.Error -> {
                    _articleList.value = ArticleState.Error(articleResponse.message!!)
                }
            }
        }
    }

    private fun insertArticlesToDatabase(articles: List<NetworkArticle>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertArticlesToDatabase(articles)
        }
        _articleList.value = ArticleState.Success
    }

    fun showSnackBar(message: String, length: Int) {
        viewModelScope.launch {
            _articleEvent.send(ShowSnackBar(message, length))
        }
    }


    sealed class ArticleState {
        object Empty : ArticleState()
        object Loading : ArticleState()
        object Refresh : ArticleState()
        object Success : ArticleState()
        class Error(val message: String) : ArticleState()
    }

    sealed class ArticleEvent {
        class ShowSnackBar(val message: String, val length: Int) : ArticleEvent()
    }

}