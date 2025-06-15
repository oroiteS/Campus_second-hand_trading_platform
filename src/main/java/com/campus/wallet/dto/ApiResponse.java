@Data
@AllArgsConstructor
public class ApiResponse {
    private int code;
    private String message;
    private Object data;
}