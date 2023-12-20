package taolu.pipeline.pipe;

import taolu.pipeline.context.PipelineContext;

/**
 * 存储在管道中的pipe
 * @author muyang
 * @create 2023/12/20 18:57
 */
public interface Pipeline {

    void invoke(PipelineContext pipeLineContext);

}
