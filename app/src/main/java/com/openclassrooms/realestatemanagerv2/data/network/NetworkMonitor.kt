import com.openclassrooms.realestatemanagerv2.domain.model.NetworkStatus // <--- NOUVEL IMPORT
import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val networkStatus: Flow<NetworkStatus>
}
        