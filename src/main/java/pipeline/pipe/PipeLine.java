package pipeline.pipe;

import pipeline.context.PipeLineContext;

/**
 * 存储在管道中的pipe
 * @author muyang
 * @create 2023/12/20 18:57
 */
public interface PipeLine {

    void invoke(PipeLineContext pipeLineContext);

}
