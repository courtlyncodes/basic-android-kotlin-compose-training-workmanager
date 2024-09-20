import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.bluromatic.workers.BlurWorker
import com.example.bluromatic.workers.CleanupWorker
import com.example.bluromatic.workers.SaveImageToFileWorker
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WorkerInstrumentationTest {
    private lateinit var context: Context
    private val KEY_IMAGE_URI = "KEY_IMAGE_URI"

    // Define the mock URI input as a pair
    private val mockUriInput = Data.Builder()
        .putString(KEY_IMAGE_URI, "android.resource://com.example.bluromatic/drawable/android_cupcake")
        .build()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun cleanupWorker_doWork_resultSuccess() {
        val cleanupWorker = TestListenableWorkerBuilder<CleanupWorker>(context).build()
        runBlocking {
            val result = cleanupWorker.doWork()
            assertTrue(result is ListenableWorker.Result.Success)
        }
    }

    @Test
    fun blurWorker_doWork_resultSuccessReturnsUri() {
        val worker = TestListenableWorkerBuilder<BlurWorker>(context)
            .setInputData(mockUriInput)
            .build()
        runBlocking {
            val result = worker.doWork()
            val image = result.outputData.getString(KEY_IMAGE_URI)
            assertTrue(result is ListenableWorker.Result.Success)
            assertTrue(image?.startsWith("file:///data/user/0/com.example.bluromatic/files/blur_filter_outputs/blur-filter-output-") ?: false)
        }
    }

    @Test
    fun saveImageToFileWorker_doWork_resultSuccessReturnsUri() {
        val worker = TestListenableWorkerBuilder<SaveImageToFileWorker>(context)
            .setInputData(mockUriInput)
            .build()
        runBlocking {
            val result = worker.doWork()
            val image = result.outputData.getString(KEY_IMAGE_URI)
            assertTrue(result is ListenableWorker.Result.Success)
            assertTrue(image?.startsWith("content://media/external/images/media/") ?: false)
        }
    }
}