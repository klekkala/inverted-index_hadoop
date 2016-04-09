package spouts;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.google.common.base.Preconditions;

public class TailSpout extends BaseRichSpout
{
	private static final long serialVersionUID = 1L;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
    public static final int DEFAULT_DELAY = 1000;

    private File file;
    private long interval;
    private volatile boolean stop = false;
    private SpoutOutputCollector collector;
    private SynchronousQueue<String> queue; 	
    private Tailer tailer;
    private TailerListener listener;


    /**
     * Creates a TailFileSpout for the given file, starting from the beginning, with the default interval of 1.0s.
     *
     * @param filename the name of the file to follow.
     */
    public TailSpout(String filename) {
        this(filename, DEFAULT_DELAY);
    }

    /**
     * Creates a TailFileSpout for the given file, starting from the beginning.
     *
     * @param filename the name of the file to follow.
     * @param interval    the interval between checks of the file for new content in milliseconds.
     */
    public TailSpout(String filename, long interval) {
        this(new File(filename), interval);
    }


    /**
     * Creates a TailFileSpout for the given file, starting from the beginning, with the default interval of 1.0s.
     *
     * @param file the file to follow.
     */
    public TailSpout(File file) {
        this(file, DEFAULT_DELAY);
    }
    
    /**
     * Creates a TailFileSpout for the given file, starting from the beginning.
     *
     * @param file  the file to follow.
     * @param interval the interval between checks of the file for new content in milliseconds.
     */
    public TailSpout(File file, long interval) {
        Preconditions.checkArgument(file.isFile(), "[TAILSPOUT - TailSpout]TailFileSpout expects a file but '" + file + "' is not.");
        this.file = file;
        this.interval = interval;
    }

    @Override
    public void open(@SuppressWarnings("rawtypes") Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        this.collector = collector;
        logger.info("[TAILSPOUT - open] Open method of TailSpout");
        queue = new SynchronousQueue<String>();
        listener = new QueueSender(); // This listener send each file line in the queue
        tailer = new Tailer(file, listener, interval); // Start a tailer thread
        tailer.run();
		
        logger.info("[TAILSPOUT - open]Opening TailFileSpout on file " + file.getAbsolutePath() + " with an interval of " + interval + " ms.");
    }

    @Override
    public void nextTuple() {
        try {
            while (!stop) {
                //String line = queue.poll(100, TimeUnit.MILLISECONDS); // Wait for a file line from the queue
            	if(queue.size()==0){
            		//logger.info("[TAILSPOUT - nextTuple] Queue size:" + queue.size());
            		continue;
            	}
            	String line = queue.take();
                logger.debug("[TAILSPOUT - nextTuple]Poll a new line from the queue : " + line);
                if (line != null) 
                {
                	logger.debug("[TAILSPOUT - nextTuple]Emitting a new word: " + line);
                    collector.emit(new Values(line));
                    logger.debug("[TAILSPOUT - nextTuple]Returning: " + line);
                    return;
                }
            }
        } catch (InterruptedException e) {
            logger.error("[TAILSPOUT - nextTuple]Tailing on file " + file.getAbsolutePath() + " was interrupted: ");
        	e.printStackTrace();
        }
    }

    @Override
    public void close() {
        stop = true;
        tailer.stop();
        logger.info("[TAILSPOUT - close]Closing TailFileSpout on file " + file.getAbsolutePath());
    }

    /**
     * Emits tuples containing only one field, named "line".
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("line"));
    }


    /**
     * A listener for the tailer sending current file line in a blocking queue.
     */
    private class QueueSender extends TailerListenerAdapter {
        
        public void handle(String line) {
            logger.info("[TAILSPOUT - QueueSender]Put a new line in the queue : " + line);
			//queue.put(line);
			queue.add(line);
        }

        @Override
		public void init(Tailer tailer) {
			logger.error("[TAILSPOUT QueueSender-init] Launching tailer");
			super.init(tailer);
		}

		@Override
		public void handle(Exception ex) {
			logger.error("[TAILSPOUT QueueSender-handleEx] Exception in handler");
			ex.printStackTrace();
			super.handle(ex);
		}

		@Override
        public void fileRotated() {
            logger.info("[TAILSPOUT - QueueSender]File was rotated or rename");
        }
    }

}
