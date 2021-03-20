package io.github.jezreal.spacenews.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jezreal.spacenews.models.Article
import io.github.jezreal.spacenews.repository.ArticleRepository
import io.github.jezreal.spacenews.viewmodels.ArticleViewModel.ArticleEvent.LoadUrl
import io.github.jezreal.spacenews.wrappers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _articleList = MutableStateFlow<ArticleState>(ArticleState.Empty)
    val articleList: LiveData<ArticleState> = _articleList.asLiveData()

    private val _articleEvent = Channel<ArticleEvent>()
    val articleEvent = _articleEvent.receiveAsFlow()

    fun getArticles() {
        _articleList.value = ArticleState.Loading

        viewModelScope.launch(Dispatchers.Default) {
            when (val articleResponse = repository.getArticleList()) {
                is Resource.Success -> {
                    _articleList.value = ArticleState.Success(articleResponse.data!!)
                }
                is Resource.Error -> {
                    _articleList.value = ArticleState.Error(articleResponse.message!!)
                }
            }
        }
    }

    fun loadUrl(url: String) {
        viewModelScope.launch {
            _articleEvent.send(LoadUrl(url))
        }
    }

    sealed class ArticleState {
        object Empty : ArticleState()
        object Loading : ArticleState()
        class Success(val articles: List<Article>) : ArticleState()
        class Error(val message: String) : ArticleState()
    }

    sealed class ArticleEvent {
        class ShowSnackBar(val message: String) : ArticleEvent()
        class LoadUrl(val url: String) : ArticleEvent()
    }

}