package pipeline.context;

/**
 * 在管到链的上下文参数。在chain中的所有pipe都能共享这份context
 * @author muyang
 * @create 2023/12/20 18:50
 */
public interface PipeLineContext {

    /**
     * 往context中添加key和value
     * @param key
     * @param value
     * @return
     */
    boolean putParameter(String key, Object value);

    /**
     * 往context中移除key和value
     * @param key
     * @param value
     * @return
     */
    boolean removeParameter(String key, Object value);

}
