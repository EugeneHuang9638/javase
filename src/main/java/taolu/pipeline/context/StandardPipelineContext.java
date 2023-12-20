package taolu.pipeline.context;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 标准的管到上下文实现
 * @author muyang
 * @create 2023/12/20 18:52
 */
public class StandardPipelineContext implements PipelineContext {

    private static Map<String, Object> parameters = new ConcurrentHashMap<>();

    @Override
    public boolean putParameter(String key, Object value) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key is null");
        }

        if (parameters.containsKey(key)) {
            return true;
        }

        parameters.put(key, value);
        return true;
    }

    @Override
    public boolean removeParameter(String key, Object value) {
        if (parameters.containsKey(key)) {
            parameters.remove(key);
            return true;
        } else {
            return false;
        }
    }
}
