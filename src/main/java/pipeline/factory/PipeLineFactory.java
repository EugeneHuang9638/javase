package pipeline.factory;

import pipeline.pipe.PipeLine;
import pipeline.pipe.Test1Pipe;
import pipeline.pipe.Test2Pipe;

/**
 * 创建pipeline的工厂类
 * @author muyang
 * @create 2023/12/20 19:18
 */
public final class PipeLineFactory {


    public static PipeLine createPipeLine(String pipeLineCode) {
        if (pipeLineCode.equals("test1")) {
            return new Test1Pipe(pipeLineCode);
        } else if (pipeLineCode.equals("test2")) {
            return new Test2Pipe(pipeLineCode);
        } else {
            return null;
        }
    }

}
