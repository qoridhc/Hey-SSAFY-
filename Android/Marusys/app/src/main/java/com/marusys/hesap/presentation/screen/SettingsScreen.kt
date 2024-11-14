import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marusys.hesap.R
import com.marusys.hesap.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.delay


@Composable
fun SettingScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var cpuUsage by remember { mutableStateOf("Loading...") }
    var appMemoryUsage by remember { mutableStateOf("Loading...") }

    // 메모리 사용량, CPU 사용량 업데이트
    LaunchedEffect(Unit) {
        viewModel.updateMemoryUsage(context)

        var previousCpuTime = Debug.threadCpuTimeNanos()
        while (true) {
            delay(1000)  // 1초마다 업데이트

            // CPU 사용량 계산
            val currentCpuTime = Debug.threadCpuTimeNanos()
            val cpuUsageMs = (currentCpuTime - previousCpuTime) / 1_000_000  // 나노초 → 밀리초 변환
            cpuUsage = "$cpuUsageMs ms"
            previousCpuTime = currentCpuTime  // 이전 시간 업데이트

            // 앱 메모리 사용량 계산
            appMemoryUsage = getAppMemoryUsage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // 시스템 성능 모니터링 글자 위 여백 추가

        Text(
            text = "시스템 성능 모니터링",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        PerformanceCard(
            title = "CPU 사용량",
            description = cpuUsage,  // 실시간 CPU 사용량 표시
            imageRes = R.drawable.ic_cpu // CPU 관련 이미지
        )

        Spacer(modifier = Modifier.height(16.dp))

        PerformanceCard(
            title = "메모리 사용량",
            description = appMemoryUsage,  // 앱 메모리 사용량 표시
            imageRes = R.drawable.ic_memory // 메모리 관련 이미지
        )

        Spacer(modifier = Modifier.height(16.dp))

        PerformanceCard(
            title = "배터리 사용량",
            description = "추가 예정",  // 배터리 사용량 데이터 추가 예정
            imageRes = R.drawable.ic_battery // 배터리 관련 이미지
        )
    }
}

@Composable
fun PerformanceCard(
    title: String,
    description: String,
    imageRes: Int // 이미지 리소스 ID를 추가
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지 추가
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp) // 이미지와 텍스트 간 간격
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.DarkGray,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// 현재 앱 메모리 사용량 계산 함수
fun getAppMemoryUsage(): String {
    val memoryInfo = Debug.MemoryInfo()
    Debug.getMemoryInfo(memoryInfo)

    // 현재 앱이 사용하는 메모리 사용량 계산 (MB 단위)
    val totalPss = memoryInfo.totalPss / 1024  // KB에서 MB로 변환

    return "$totalPss MB"
}

