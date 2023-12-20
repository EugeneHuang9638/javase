package taolu.pipeline.pipe;

import taolu.pipeline.context.PipelineContext;

/**
 * @author muyang
 * @create 2023/12/20 19:03
 */
public class Test1Pipe extends AbstractPipe {

    public Test1Pipe(String pipeLineCode) {
        super(pipeLineCode);
    }

    @Override
    protected void doInvoke(PipelineContext pipeLineContext) {
        try {
            pipeLineContext.putParameter("test1", "111111");
            System.out.println("Test1PipeLine's doInvoke method. put test1 key to context");
        } catch (Exception e) {
            System.out.println("Test1PipeLine occurred an exception");
        }
    }

    @Override
    protected boolean ignoreIt(PipelineContext pipeLineContext) {
        return false;
    }

}
