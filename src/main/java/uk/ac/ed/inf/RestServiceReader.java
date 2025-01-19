package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.web.reactive.function.client.WebClient;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RestServiceReader {

    //Access Data in Rest
    private static String getData (String path){

        WebClient.Builder builder = WebClient.builder();

        return builder
                .codecs(codecs -> codecs
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))
                .build()
                .get()
                .uri(path)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public static Restaurant[] getRestaurant(String baseURL) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(getData(baseURL + "/restaurants"), Restaurant[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Order[] getOrder(String baseURL, LocalDate orderDate) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return Arrays.stream(mapper.readValue(getData(baseURL + "/orders"), Order[].class)).filter(o -> o.getOrderDate().isEqual(orderDate)).toArray(Order[]::new);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static NamedRegion getCentralArea(String baseURL) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(getData(baseURL + "/centralArea"), NamedRegion.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static NamedRegion[] getNoFlyZones(String baseURL) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(getData(baseURL + "/noFlyZones"), NamedRegion[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
