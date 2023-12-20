package taolu.pipeline.pipe;

import taolu.pipeline.context.PipelineContext;

/**
 * @author muyang
 * @create 2023/12/20 19:03
 */
public class Test2Pipe extends AbstractPipe {

    public Test2Pipe(String pipeLineCode) {
        super(pipeLineCode);
    }

    @Override
    protected void doInvoke(PipelineContext pipeLineContext) {
        try {
            pipeLineContext.putParameter("test2", "222222");
            System.out.println("Test2PipeLine's doInvoke method. put test2 key to context");
        } catch (Exception e) {
            System.out.println("Test2PipeLine occurred an exception");
        }
    }

    @Override
    protected boolean ignoreIt(PipelineContext pipeLineContext) {
        return false;
    }
}
