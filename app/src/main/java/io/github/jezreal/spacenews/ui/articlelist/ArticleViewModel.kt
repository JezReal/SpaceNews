package io.github.jezreal.spacenews.ui.articlelist

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jezreal.spacenews.network.NetworkArticle
import io.github.jezreal.spacenews.repository.ArticleRepository
import io.github.jezreal.spacenews.ui.articlelist.ArticleViewModel.ArticleEvent.ShowSnackBar
import io.github.jezreal.spacenews.wrappers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {
    private val _articleList = MutableLiveData<ArticleState>(ArticleState.Empty)
    val articleList: LiveData<ArticleState> = _articleList

    private val _articleEvent = MutableSharedFlow<ArticleEvent>()
    val articleEvent = _articleEvent.asSharedFlow()

    val articles = repository.articles.asLiveData()

    fun getArticles() {
        getArticlesFromNetwork()
    }

    fun refreshArticles() {
        _articleList.value = ArticleState.Refresh

        viewModelScope.launch(Dispatchers.Default) {
            when (val articleResponse = repository.getArticleList()) {
                is Resource.Success -> {
                    insertArticlesToDatabase(articleResponse.data!!)
                }
                is Resource.Error -> {
                    _articleList.value = ArticleState.Error
                    _articleEvent.emit(
                        ShowSnackBar(
                            articleResponse.message!!,
                            Snackbar.LENGTH_SHORT
                        )
                    )
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
                    _articleList.postValue(ArticleState.Error)
                    _articleEvent.emit(
                        ShowSnackBar(
                            articleResponse.message!!,
                            Snackbar.LENGTH_SHORT
                        )
                    )
                }
            }
        }
    }

    private fun insertArticlesToDatabase(articles: List<NetworkArticle>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCachedArticles()
            repository.insertArticlesToDatabase(articles)
        }
        _articleList.postValue(ArticleState.Success)
    }

    fun showSnackBar(message: String, length: Int) {
        viewModelScope.launch {
            _articleEvent.emit(ShowSnackBar(message, length))
        }
    }


    sealed class ArticleState {
        object Empty : ArticleState()
        object Loading : ArticleState()
        object Refresh : ArticleState()
        object Success : ArticleState()
        object Error : ArticleState()
    }

    sealed class ArticleEvent {
        class ShowSnackBar(val message: String, val length: Int) : ArticleEvent()
    }

}