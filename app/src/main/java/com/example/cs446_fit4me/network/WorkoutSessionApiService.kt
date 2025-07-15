import com.example.cs446_fit4me.model.WorkoutSessionResponse
import com.example.cs446_fit4me.model.WorkoutSessionUI
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkoutSessionApiService {
    @GET("workout-sessions/{id}")
    suspend fun getWorkoutSession(@Path("id") id: String): WorkoutSessionResponse

    @POST("workout-sessions/{id}/save")
    suspend fun saveWorkoutSession(
        @Path("id") id: String,
        @Body session: WorkoutSessionUI
    )

    @POST("workout-sessions/from-template/{templateId}")
    suspend fun startWorkoutSessionFromTemplate(
        @Path("templateId") templateId: String
    ): WorkoutSessionStartResponse

    data class WorkoutSessionStartResponse(
        val workoutSessionId: String
    )
}
