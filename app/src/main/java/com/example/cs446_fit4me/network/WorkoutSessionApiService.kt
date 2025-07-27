import com.example.cs446_fit4me.model.ExerciseSessionResponse
import com.example.cs446_fit4me.model.WorkoutSessionResponse
import com.example.cs446_fit4me.model.WorkoutSessionUpdateRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WorkoutSessionApiService {
    @GET("workout-sessions/{id}")
    suspend fun getWorkoutSession(@Path("id") id: String): WorkoutSessionResponse

    @PUT("workout-sessions/{id}")
    suspend fun updateWorkoutSession(
        @Path("id") sessionId: String,
        @Body session: WorkoutSessionUpdateRequest
    )

    @POST("workout-sessions/from-template/{templateId}")
    suspend fun startWorkoutSessionFromTemplate(
        @Path("templateId") templateId: String
    ): WorkoutSessionStartResponse

    @DELETE("workout-sessions/{id}")
    suspend fun deleteWorkoutSession(@Path("id") id: String): retrofit2.Response<Unit>

    @GET("workout-sessions/by-user")
    suspend fun getWorkoutSessionsByUser(): List<WorkoutSessionResponse>

    data class WorkoutSessionStartResponse(
        val workoutSessionId: String
    )
}
