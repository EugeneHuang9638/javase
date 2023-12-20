package taolu.pipeline.factory;

import taolu.pipeline.pipe.Pipeline;
import taolu.pipeline.pipe.Test1Pipe;
import taolu.pipeline.pipe.Test2Pipe;

/**
 * 创建pipeline的工厂类
 * @author muyang
 * @create 2023/12/20 19:18
 */
public final class PipelineFactory {


    public static Pipeline createPipeLine(String pipeLineCode) {
        if (pipeLineCode.equals("test1")) {
            return new Test1Pipe(pipeLineCode);
        } else if (pipeLineCode.equals("test2")) {
            return new Test2Pipe(pipeLineCode);
        } else {
            return null;
        }
    }

}
