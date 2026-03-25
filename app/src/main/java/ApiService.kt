import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val mensaje: String
)

interface ApiService {

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

}