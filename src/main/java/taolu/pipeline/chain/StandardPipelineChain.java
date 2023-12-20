package taolu.pipeline.chain;

import taolu.pipeline.context.PipelineContext;
import taolu.pipeline.pipe.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准的管道链
 * @author muyang
 * @create 2023/12/20 19:07
 */
public class StandardPipelineChain implements PipelineChain {

    private volatile List<Pipeline> lines = new ArrayList<>();

    @Override
    public void addPipeLine(Pipeline pipeLine) {
        if (lines.contains(pipeLine)) {
            return;
        }

        lines.add(pipeLine);
    }

    @Override
    public void removePipeLine(Pipeline pipeLine) {
        if (lines.contains(pipeLine)) {
            lines.remove(pipeLine);
        }
    }

    @Override
    public void execute(PipelineContext pipeLineContext) {
        for (Pipeline line : lines) {
            line.invoke(pipeLineContext);
        }
    }
}
