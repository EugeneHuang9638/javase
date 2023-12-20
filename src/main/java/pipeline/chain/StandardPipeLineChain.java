package pipeline.chain;

import pipeline.context.PipeLineContext;
import pipeline.pipe.PipeLine;

import java.util.ArrayList;
import java.util.List;

/**
 * 标准的管道链
 * @author muyang
 * @create 2023/12/20 19:07
 */
public class StandardPipeLineChain implements PipeLineChain {

    private volatile List<PipeLine> lines = new ArrayList<>();

    @Override
    public void addPipeLine(PipeLine pipeLine) {
        if (lines.contains(pipeLine)) {
            return;
        }

        lines.add(pipeLine);
    }

    @Override
    public void removePipeLine(PipeLine pipeLine) {
        if (lines.contains(pipeLine)) {
            lines.remove(pipeLine);
        }
    }

    @Override
    public void execute(PipeLineContext pipeLineContext) {
        for (PipeLine line : lines) {
            line.invoke(pipeLineContext);
        }
    }
}
