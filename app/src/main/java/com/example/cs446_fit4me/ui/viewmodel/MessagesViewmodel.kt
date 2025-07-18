import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cs446_fit4me.model.UserResponse
import com.example.cs446_fit4me.network.ChatApiService
import com.example.cs446_fit4me.network.SimpleUser
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val chatApi: ChatApiService
) : ViewModel() {
    var users by mutableStateOf<List<SimpleUser>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun loadConversations() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = chatApi.getConversations()
                users = response.users
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}