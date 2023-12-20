package taolu.pipeline.registry;

import taolu.pipeline.chain.PipelineChain;
import taolu.pipeline.factory.PipelineFactory;
import taolu.pipeline.pipe.Pipeline;

/**
 * 根据管道code创建管道，并将管道注册到链中
 * @author muyang
 * @create 2023/12/20 19:04
 */
public final class PipelineChainRegistry {

    public static void register(PipelineChain pipeLineChain, String... pipeLineCode) {
        for (String code : pipeLineCode) {
            Pipeline pipeLine = PipelineFactory.createPipeLine(code);
            if (pipeLine != null) {
                pipeLineChain.addPipeLine(pipeLine);
            }
        }
    }

}
