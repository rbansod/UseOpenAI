import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redis.RedisClient;
import com.redis.search.*;

public class RedisOperations {
    private static final Logger logger = Logger.getLogger(RedisOperations.class.getName());

    public static DataFrame findMatchingVectorsInRedis(float[] np_vector, List<String> return_fields, String search_type, int number_of_results, String vector_field_name) {
        String base_query = String.format("*=>[%s %d @%s $vec_param as vector_score]", search_type, number_of_results, vector_field_name);

        Query query = new Query(base_query).sortBy("vector_score").paging(0, number_of_results).returnFields(return_fields.toArray(new String[0])).dialect(2);

        Map<String, byte[]> param_dict = new HashMap<>();
        param_dict.put("vec_param", Utils.toByteArray(np_vector));

        Result res = RedisClient.getClient().search(query, param_dict);
        List<Map<String, Object>> results = res.docs();
        List<Map<String, Object>> result_list = new ArrayList<>();
        for (Map<String, Object> r : results) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", r.get("id"));
            result.put("text", r.get("text"));
            result.put("filename", r.get("filename"));
            result.put("vector_score", r.get("vector_score"));
            result_list.add(result);
        }
        return new DataFrame(result_list);
    }

    public static DataFrame searchSemanticRedis(float[] query_embedding) {
        return findMatchingVectorsInRedis(query_embedding, new ArrayList<>(), "KNN", 3, "embeddings");
    }

    public static void populateRedisWithDocuments(List<McCrawledDocumentsChunked> documents) {
        for (int i = 0; i < documents.size(); i++) {
            McCrawledDocumentsChunked document = documents.get(i);
            Map<String, Object> redis_doc = new HashMap<>();
            redis_doc.put("url", document.url);
            redis_doc.put("chunk_text", document.text);
            redis_doc.put("chunk_embedding", document.chunk_embedding);

            String redis_doc_json = new Gson().toJson(redis_doc, new TypeToken<Map<String, Object>>() {}.getType());
            RedisClient.getClient().json().set(String.format("doc:%s-%s-%d", crawl_job, crawl_job_version, i), redis_doc_json);
        }
    }

    public static void deleteRedisDocuments(String crawl_job, String crawl_job_version) {
        RedisClient.getClient().del(Arrays.stream(RedisClient.getClient().keys(String.format("doc:%s-%s-*", crawl_job, crawl_job_version))).toArray(String[]::new));
    }
}
