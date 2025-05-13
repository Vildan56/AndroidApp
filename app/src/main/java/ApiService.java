import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    @GET("products") // Впишите ваш эндпоинт здесь
    Call<List<ProductBD>> getProducts(); // Product — это ваша модель данных
}
