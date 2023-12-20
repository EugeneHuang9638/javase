package pipeline.registry;

import pipeline.chain.PipeLineChain;
import pipeline.factory.PipeLineFactory;
import pipeline.pipe.PipeLine;

/**
 * 根据管道code创建管道，并将管道注册到链中
 * @author muyang
 * @create 2023/12/20 19:04
 */
public final class PipeLineChainRegistry {

    public static void register(PipeLineChain pipeLineChain, String... pipeLineCode) {
        for (String code : pipeLineCode) {
            PipeLine pipeLine = PipeLineFactory.createPipeLine(code);
            if (pipeLine != null) {
                pipeLineChain.addPipeLine(pipeLine);
            }
        }
    }

}
