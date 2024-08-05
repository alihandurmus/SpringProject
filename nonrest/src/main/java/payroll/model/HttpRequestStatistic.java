package payroll.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "httpRequestStatistic")
@Data
@NoArgsConstructor
public class HttpRequestStatistic {
    @Id
    private String id;
    private String request_type;
    private String request_status;
    private long timestamp;
    /*public HttpRequestStatistic(String message) {
        String[] parts = message.split(":");
        this.request_type = parts[0];
        this.request_status = parts[1];
        this.timestamp = Long.parseLong(parts[3]);
    }*/

    public HttpRequestStatistic (String message) {
        String[] parts = message.split(",");
        for (String part : parts) {
            String key = part.split(":")[0];
            String value = part.split(":")[1];
            switch (key) {
                case "method":
                    request_type = value;
                    break;
                case "status":
                    request_status = value;
                    break;
                case "timestamp":
                    timestamp = Long.parseLong(value);
                    break;
                default:
                    break;
            }
        }
        System.out.println(this.toString());

    }



}
