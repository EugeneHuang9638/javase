package pipeline.pipe;

import pipeline.context.PipeLineContext;

/**
 * @author muyang
 * @create 2023/12/20 19:03
 */
public class Test1Pipe extends AbstractPipe {

    public Test1Pipe(String pipeLineCode) {
        super(pipeLineCode);
    }

    @Override
    protected void doInvoke(PipeLineContext pipeLineContext) {
        pipeLineContext.putParameter("test1", "111111");
        System.out.println("Test1PipeLine's doInvoke method. put test1 key to context");
    }

    @Override
    protected boolean ignoreIt(PipeLineContext pipeLineContext) {
        return false;
    }

}
